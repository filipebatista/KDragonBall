import shared
import SwiftUI

struct CharacterDetailView: View {
    let characterId: Int
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @State private var showFullscreenImage = false
    @State private var fullscreenImageUrl: String?

    var body: some View {
        let viewModel: CharacterDetailViewModel = viewModelStoreOwner.viewModel(
            factory: ViewModelFactories.shared.characterDetailViewModelFactory(characterId: Int32(characterId))
        )

        ZStack {
            Color.appBackground
                .ignoresSafeArea()

            Observing(viewModel.uiState) { uiState in
                ScrollView {
                    if uiState.isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .dragonBallOrange))
                            .padding(.top, 100)
                    } else if let character = uiState.character {
                        CharacterDetailContent(
                            character: character,
                            onImageTap: { imageUrl in
                                fullscreenImageUrl = imageUrl
                                showFullscreenImage = true
                            }
                        )
                    } else if let error = uiState.error {
                        DetailErrorView(
                            message: error,
                            onRetry: { viewModel.refresh() }
                        )
                    }
                }
                .refreshable {
                    viewModel.refresh()
                }
                .navigationTitle(uiState.character?.name ?? String(localized: "detail_character"))
                .navigationBarTitleDisplayMode(.large)
            }

            // Fullscreen image overlay
            if showFullscreenImage, let imageUrl = fullscreenImageUrl {
                FullscreenImageView(
                    imageUrl: imageUrl,
                    isPresented: $showFullscreenImage
                )
            }
        }
    }
}

// MARK: - Character Detail Content

struct CharacterDetailContent: View {
    let character: Character
    let onImageTap: (String) -> Void

    var body: some View {
        VStack(spacing: 20) {
            // Character image
            AsyncImage(url: URL(string: character.image)) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .frame(width: 200, height: 200)
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(width: 200)
                        .frame(width: 200, height: 200, alignment: .top)
                        .clipShape(Circle())
                        .shadow(color: Color.dragonBallOrange.opacity(0.3), radius: 10, x: 0, y: 5)
                        .onTapGesture {
                            onImageTap(character.image)
                        }
                case .failure:
                    Image(systemName: "person.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.gray)
                        .frame(width: 200, height: 200)
                        .background(Color.gray.opacity(0.2))
                        .clipShape(Circle())
                @unknown default:
                    EmptyView()
                }
            }
            .padding(.top, 20)

            // Basic information card
            InfoCard(title: String(localized: "detail_basic_info")) {
                InfoRow(label: String(localized: "label_name"), value: character.name)
                InfoRow(label: String(localized: "label_race"), value: character.race)
                InfoRow(label: String(localized: "label_gender"), value: character.gender)
                if !character.affiliation.isEmpty {
                    InfoRow(label: String(localized: "label_affiliation"), value: character.affiliation)
                }
                if let planet = character.originPlanet {
                    InfoRow(label: String(localized: "label_origin_planet"), value: planet.name)
                }
            }

            // Power stats card
            InfoCard(title: String(localized: "detail_power_stats")) {
                PowerStatRow(label: String(localized: "label_ki"), value: character.ki)
                PowerStatRow(label: String(localized: "label_max_ki"), value: character.maxKi)
            }

            // Description
            if !character.description_.isEmpty {
                InfoCard(title: String(localized: "detail_description")) {
                    Text(character.description_)
                        .font(.body)
                        .foregroundColor(.primary)
                        .multilineTextAlignment(.leading)
                }
            }

            // Transformations
            if !character.transformations.isEmpty {
                VStack(alignment: .leading, spacing: 12) {
                    Text(String(localized: "detail_transformations"))
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.dragonBallOrange)
                        .padding(.horizontal)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 16) {
                            ForEach(character.transformations, id: \.id) { transformation in
                                TransformationCard(
                                    transformation: transformation,
                                    onImageTap: onImageTap
                                )
                            }
                        }
                        .padding(.horizontal)
                    }
                }
            }

            Spacer(minLength: 20)
        }
    }
}

// MARK: - Info Card

struct InfoCard<Content: View>: View {
    let title: String
    let content: Content

    init(title: String, @ViewBuilder content: () -> Content) {
        self.title = title
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.dragonBallOrange)

            content
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(Color.appSurface)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
        .padding(.horizontal)
    }
}

// MARK: - Info Row

struct InfoRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.body)
                .fontWeight(.medium)
                .foregroundColor(.secondary)

            Spacer()

            Text(value)
                .font(.body)
                .foregroundColor(.primary)
        }
    }
}

// MARK: - Power Stat Row

struct PowerStatRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Image(systemName: "bolt.fill")
                .foregroundColor(.dragonBallYellow)

            Text(label)
                .font(.body)
                .fontWeight(.medium)
                .foregroundColor(.secondary)

            Spacer()

            Text(value)
                .font(.body)
                .fontWeight(.semibold)
                .foregroundColor(.dragonBallOrange)
        }
    }
}

// MARK: - Transformation Card

struct TransformationCard: View {
    let transformation: Transformation
    let onImageTap: (String) -> Void

    var body: some View {
        VStack(spacing: 8) {
            AsyncImage(url: URL(string: transformation.image)) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .frame(width: 100, height: 100)
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(width: 100)
                        .frame(width: 100, height: 100, alignment: .top)
                        .clipShape(Circle())
                        .onTapGesture {
                            onImageTap(transformation.image)
                        }
                case .failure:
                    Image(systemName: "star.fill")
                        .font(.system(size: 40))
                        .foregroundColor(.dragonBallYellow)
                        .frame(width: 100, height: 100)
                        .background(Color.gray.opacity(0.2))
                        .clipShape(Circle())
                @unknown default:
                    EmptyView()
                }
            }

            Text(transformation.name)
                .font(.caption)
                .fontWeight(.medium)
                .foregroundColor(.primary)
                .multilineTextAlignment(.center)
                .lineLimit(2)

            HStack(spacing: 4) {
                Image(systemName: "bolt.fill")
                    .font(.caption2)
                    .foregroundColor(.dragonBallYellow)
                Text(transformation.ki)
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
        }
        .frame(width: 120)
        .padding()
        .background(Color.dragonBallBlue.opacity(0.1))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
}

// MARK: - Detail Error View

struct DetailErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
                .foregroundColor(.dragonBallOrange)

            Text(String(localized: "error_title"))
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(.primary)

            Text(message)
                .font(.body)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal)

            Button(action: onRetry) {
                Text(String(localized: "action_try_again"))
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.horizontal, 32)
                    .padding(.vertical, 12)
                    .background(Color.dragonBallOrange)
                    .cornerRadius(24)
            }
        }
        .padding(.top, 100)
    }
}

// MARK: - Fullscreen Image View

struct FullscreenImageView: View {
    let imageUrl: String
    @Binding var isPresented: Bool
    @State private var scale: CGFloat = 1.0
    @State private var lastScale: CGFloat = 1.0
    @State private var offset: CGSize = .zero
    @State private var lastOffset: CGSize = .zero

    var body: some View {
        ZStack {
            Color.black.opacity(0.9)
                .ignoresSafeArea()
                .onTapGesture {
                    isPresented = false
                }

            AsyncImage(url: URL(string: imageUrl)) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .scaleEffect(scale)
                        .offset(offset)
                        .gesture(
                            MagnificationGesture()
                                .onChanged { value in
                                    let delta = value / lastScale
                                    lastScale = value
                                    scale *= delta
                                }
                                .onEnded { _ in
                                    lastScale = 1.0
                                    if scale < 1.0 {
                                        withAnimation {
                                            scale = 1.0
                                        }
                                    }
                                }
                        )
                        .simultaneousGesture(
                            DragGesture()
                                .onChanged { value in
                                    offset = CGSize(
                                        width: lastOffset.width + value.translation.width,
                                        height: lastOffset.height + value.translation.height
                                    )
                                }
                                .onEnded { _ in
                                    lastOffset = offset
                                }
                        )
                        .onTapGesture(count: 2) {
                            withAnimation {
                                if scale > 1.0 {
                                    scale = 1.0
                                    offset = .zero
                                    lastOffset = .zero
                                } else {
                                    scale = 2.0
                                }
                            }
                        }
                case .failure:
                    Image(systemName: "photo")
                        .font(.system(size: 60))
                        .foregroundColor(.gray)
                @unknown default:
                    EmptyView()
                }
            }

            // Close button
            VStack {
                HStack {
                    Spacer()
                    Button(
                        action: {
                            isPresented = false
                        },
                        label: {
                            Image(systemName: "xmark.circle.fill")
                                .font(.system(size: 32))
                                .foregroundColor(.white)
                        }
                    )
                    .padding()
                }
                Spacer()
            }
        }
    }
}

// MARK: - Preview

#Preview {
    NavigationView {
        CharacterDetailView(characterId: 1)
    }
}

#Preview("Info Card") {
    InfoCard(title: "Basic Information") {
        InfoRow(label: "Name", value: "Goku")
        InfoRow(label: "Race", value: "Saiyan")
    }
    .padding()
}

#Preview("Power Stat Row") {
    PowerStatRow(label: "Ki", value: "60,000,000")
        .padding()
}

#Preview("Detail Error") {
    DetailErrorView(
        message: "Unable to load character details.",
        onRetry: {}
    )
}
