import shared
import SwiftUI

struct TransformationDetailView: View {
    let transformationId: Int
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @State private var showFullscreenImage = false
    @State private var fullscreenImageUrl: String?

    var body: some View {
        let viewModel: TransformationDetailViewModel = viewModelStoreOwner.viewModel(
            factory: ViewModelFactories.shared.transformationDetailViewModelFactory(transformationId: Int32(transformationId))
        )

        ZStack {
            Color.appBackground
                .ignoresSafeArea()

            Observing(viewModel.uiState) { uiState in
                ScrollView {
                    if uiState.isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .dragonBallPurple))
                            .padding(.top, 100)
                    } else if let transformation = uiState.transformation {
                        TransformationDetailContent(
                            transformation: transformation,
                            onImageTap: { imageUrl in
                                fullscreenImageUrl = imageUrl
                                showFullscreenImage = true
                            }
                        )
                    } else if let error = uiState.error {
                        TransformationDetailErrorView(
                            message: error,
                            onRetry: { viewModel.refresh() }
                        )
                    }
                }
                .refreshable {
                    viewModel.refresh()
                }
                .navigationTitle(uiState.transformation?.name ?? String(localized: "detail_transformation"))
                .navigationBarTitleDisplayMode(.large)
            }

            // Fullscreen image overlay
            if showFullscreenImage, let imageUrl = fullscreenImageUrl {
                TransformationFullscreenImageView(
                    imageUrl: imageUrl,
                    isPresented: $showFullscreenImage
                )
            }
        }
    }
}

// MARK: - Transformation Detail Content

struct TransformationDetailContent: View {
    let transformation: TransformationDetail
    let onImageTap: (String) -> Void

    var body: some View {
        VStack(spacing: 20) {
            // Transformation image
            if !transformation.image.isEmpty {
                AsyncImage(url: URL(string: transformation.image)) { phase in
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
                            .shadow(color: Color.dragonBallPurple.opacity(0.3), radius: 10, x: 0, y: 5)
                            .onTapGesture {
                                onImageTap(transformation.image)
                            }
                    case .failure:
                        transformationPlaceholder
                    @unknown default:
                        EmptyView()
                    }
                }
                .padding(.top, 20)
            } else {
                transformationPlaceholder
                    .padding(.top, 20)
            }

            // Basic information card
            TransformationInfoCard(title: String(localized: "detail_basic_info")) {
                TransformationInfoRow(label: String(localized: "label_name"), value: transformation.name)
            }

            // Power stats card
            TransformationInfoCard(title: String(localized: "detail_power_stats")) {
                TransformationPowerStatRow(label: String(localized: "label_ki"), value: transformation.ki)
            }

            Spacer(minLength: 20)
        }
    }

    private var transformationPlaceholder: some View {
        ZStack {
            Circle()
                .fill(Color.dragonBallPurple.opacity(0.2))
                .frame(width: 200, height: 200)
            Text(String(transformation.name.prefix(2)).uppercased())
                .font(.system(size: 48, weight: .bold))
                .foregroundColor(.dragonBallPurple)
        }
    }
}

// MARK: - Transformation Info Card

struct TransformationInfoCard<Content: View>: View {
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
                .foregroundColor(.dragonBallPurple)

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

// MARK: - Transformation Info Row

struct TransformationInfoRow: View {
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

// MARK: - Transformation Power Stat Row

struct TransformationPowerStatRow: View {
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

// MARK: - Detail Error View

struct TransformationDetailErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
                .foregroundColor(.dragonBallPurple)

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
                    .background(Color.dragonBallPurple)
                    .cornerRadius(24)
            }
        }
        .padding(.top, 100)
    }
}

// MARK: - Fullscreen Image View

struct TransformationFullscreenImageView: View {
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
        TransformationDetailView(transformationId: 1)
    }
}

#Preview("Transformation Info Card") {
    TransformationInfoCard(title: "Basic Information") {
        TransformationInfoRow(label: "Name", value: "Super Saiyan")
    }
    .padding()
}

#Preview("Power Stat Row") {
    TransformationPowerStatRow(label: "Ki", value: "3 Billion")
        .padding()
}

#Preview("Detail Error") {
    TransformationDetailErrorView(
        message: "Unable to load transformation details.",
        onRetry: {}
    )
}
