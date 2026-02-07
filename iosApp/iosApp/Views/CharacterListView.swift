import shared
import SwiftUI

struct CharacterListView: View {
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @State private var searchText = ""

    var body: some View {
        let viewModel: CharacterListViewModel = viewModelStoreOwner.viewModel(
            factory: ViewModelFactories.shared.characterListViewModelFactory
        )

        ZStack {
            Color.appBackground
                .ignoresSafeArea()

            Observing(viewModel.uiState) { uiState in
                    VStack(spacing: 0) {
                        // Search bar
                        SearchBar(text: $searchText, onSearch: { query in
                            viewModel.searchCharacters(query: query)
                        })
                        .padding()

                        // Content
                        if let error = uiState.error, uiState.characters.isEmpty {
                            ErrorStateView(
                                message: error,
                                onRetry: { viewModel.refresh() }
                            )
                        } else if uiState.isEmpty && !uiState.isLoading {
                            EmptyStateView()
                        } else {
                            ScrollView {
                                LazyVStack(spacing: 12) {
                                    ForEach(uiState.filteredCharacters, id: \.id) { character in
                                        NavigationLink(destination: CharacterDetailView(characterId: Int(character.id))) {
                                            CharacterRow(character: character)
                                        }
                                        .buttonStyle(PlainButtonStyle())
                                        .onAppear {
                                            if character.id == uiState.filteredCharacters.last?.id {
                                                viewModel.loadNextPage()
                                            }
                                        }
                                    }

                                    if uiState.isLoading {
                                        HStack {
                                            Spacer()
                                            ProgressView()
                                                .progressViewStyle(CircularProgressViewStyle(tint: .dragonBallOrange))
                                            Spacer()
                                        }
                                        .padding()
                                    }

                                    if uiState.hasMorePages && !uiState.isLoading && !uiState.characters.isEmpty {
                                        Color.clear
                                            .frame(height: 1)
                                            .onAppear {
                                                viewModel.loadNextPage()
                                            }
                                    }
                                }
                                .padding(.horizontal)
                            }
                            .refreshable {
                                viewModel.refresh()
                            }
                        }
                }
            }
        }
        .navigationTitle(String(localized: "characters_title"))
        .navigationBarTitleDisplayMode(.large)
    }
}

// MARK: - Search Bar

struct SearchBar: View {
    @Binding var text: String
    var onSearch: (String) -> Void

    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)

            TextField(String(localized: "characters_search_placeholder"), text: $text)
                .textFieldStyle(PlainTextFieldStyle())
                .onChange(of: text) { newValue in
                    onSearch(newValue)
                }

            if !text.isEmpty {
                Button(
                    action: {
                        text = ""
                        onSearch("")
                    },
                    label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                )
            }
        }
        .padding(12)
        .background(Color.appSurface)
        .cornerRadius(24)
        .shadow(color: Color.black.opacity(0.1), radius: 4, x: 0, y: 2)
    }
}

// MARK: - Character Row

struct CharacterRow: View {
    let character: Character

    var body: some View {
        HStack(spacing: 16) {
            AsyncImage(url: URL(string: character.image)) { phase in
                switch phase {
                case .empty:
                    ProgressView()
                        .frame(width: 80, height: 80)
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                        .frame(width: 80)
                        .frame(width: 80, height: 80, alignment: .top)
                        .clipShape(Circle())
                case .failure:
                    Image(systemName: "person.fill")
                        .font(.system(size: 40))
                        .foregroundColor(.gray)
                        .frame(width: 80, height: 80)
                        .background(Color.gray.opacity(0.2))
                        .clipShape(Circle())
                @unknown default:
                    EmptyView()
                }
            }
            .frame(width: 80, height: 80)

            VStack(alignment: .leading, spacing: 6) {
                Text(character.name)
                    .font(.headline)
                    .foregroundColor(.primary)

                Text(character.race)
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                HStack {
                    Label(String(format: String(localized: "format_ki"), character.ki), systemImage: "bolt.fill")
                        .font(.caption)
                        .foregroundColor(.dragonBallOrange)
                }

                Text(character.affiliation)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(character.isHero ? Color.dragonBallBlue.opacity(0.2) : Color.red.opacity(0.2))
                    .foregroundColor(character.isHero ? .dragonBallBlue : .red)
                    .cornerRadius(8)
            }

            Spacer()

            Image(systemName: "chevron.right")
                .foregroundColor(.gray)
        }
        .padding()
        .background(Color.appSurface)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
    }
}

// MARK: - Empty State

struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "person.2.slash")
                .font(.system(size: 60))
                .foregroundColor(.gray)

            Text(String(localized: "characters_empty"))
                .font(.title3)
                .fontWeight(.medium)
                .foregroundColor(.primary)

            Text(String(localized: "characters_empty_subtitle"))
                .font(.body)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Error State

struct ErrorStateView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
                .foregroundColor(.dragonBallOrange)

            Text(String(localized: "error_title"))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.dragonBallOrange)

            Text(message)
                .font(.body)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)

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
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Preview

#Preview {
    CharacterListView()
}

#Preview("Empty State") {
    EmptyStateView()
}

#Preview("Error State") {
    ErrorStateView(
        message: "Unable to load data. Please check your connection.",
        onRetry: {}
    )
}
