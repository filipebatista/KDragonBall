import shared
import SwiftUI

struct PlanetListView: View {
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @State private var searchText = ""

    var body: some View {
        let viewModel: PlanetListViewModel = viewModelStoreOwner.viewModel(
            factory: ViewModelFactories.shared.planetListViewModelFactory
        )

        ZStack {
            Color.appBackground
                .ignoresSafeArea()

            Observing(viewModel.uiState) { uiState in
                VStack(spacing: 0) {
                    // Search bar
                    PlanetSearchBar(text: $searchText, onSearch: { query in
                        viewModel.searchPlanets(query: query)
                    })
                    .padding()

                    // Content
                    if let error = uiState.error, uiState.planets.isEmpty {
                        PlanetErrorStateView(
                            message: error,
                            onRetry: { viewModel.refresh() }
                        )
                    } else if uiState.isEmpty && !uiState.isLoading {
                        PlanetEmptyStateView()
                    } else {
                        ScrollView {
                            LazyVStack(spacing: 12) {
                                ForEach(uiState.filteredPlanets, id: \.id) { planet in
                                    NavigationLink(destination: PlanetDetailView(planetId: Int(planet.id))) {
                                        PlanetRow(planet: planet)
                                    }
                                    .buttonStyle(PlainButtonStyle())
                                    .onAppear {
                                        if planet.id == uiState.filteredPlanets.last?.id {
                                            viewModel.loadNextPage()
                                        }
                                    }
                                }

                                if uiState.isLoading {
                                    HStack {
                                        Spacer()
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .dragonBallBlue))
                                        Spacer()
                                    }
                                    .padding()
                                }

                                if uiState.hasMorePages && !uiState.isLoading && !uiState.planets.isEmpty {
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
        .navigationTitle(String(localized: "planets_title"))
        .navigationBarTitleDisplayMode(.large)
    }
}

// MARK: - Search Bar

struct PlanetSearchBar: View {
    @Binding var text: String
    var onSearch: (String) -> Void

    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)

            TextField(String(localized: "planets_search_placeholder"), text: $text)
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

// MARK: - Planet Row

struct PlanetRow: View {
    let planet: Planet

    var body: some View {
        HStack(spacing: 16) {
            // Planet image
            if !planet.image.isEmpty {
                AsyncImage(url: URL(string: planet.image)) { phase in
                    switch phase {
                    case .empty:
                        ProgressView()
                            .frame(width: 80, height: 80)
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: 80, height: 80)
                            .clipShape(Circle())
                    case .failure:
                        planetPlaceholder
                    @unknown default:
                        EmptyView()
                    }
                }
                .frame(width: 80, height: 80)
            } else {
                planetPlaceholder
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(planet.name)
                    .font(.headline)
                    .foregroundColor(.primary)

                // Status badge
                Text(planet.isDestroyed
                    ? String(localized: "label_status_destroyed")
                    : String(localized: "label_status_active"))
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(planet.isDestroyed ? Color.red.opacity(0.2) : Color.green.opacity(0.2))
                    .foregroundColor(planet.isDestroyed ? .red : Color(red: 46/255, green: 125/255, blue: 50/255))
                    .cornerRadius(8)

                if !planet.description_.isEmpty {
                    Text(planet.description_)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                }
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

    private var planetPlaceholder: some View {
        ZStack {
            Circle()
                .fill(Color.dragonBallBlue.opacity(0.2))
                .frame(width: 80, height: 80)
            Image(systemName: "globe")
                .font(.system(size: 32))
                .foregroundColor(.dragonBallBlue)
        }
    }
}

// MARK: - Empty State

struct PlanetEmptyStateView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "globe.americas")
                .font(.system(size: 60))
                .foregroundColor(.gray)

            Text(String(localized: "planets_empty"))
                .font(.title3)
                .fontWeight(.medium)
                .foregroundColor(.primary)

            Text(String(localized: "planets_empty_subtitle"))
                .font(.body)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Error State

struct PlanetErrorStateView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
                .foregroundColor(.dragonBallBlue)

            Text(String(localized: "error_title"))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.dragonBallBlue)

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
                    .background(Color.dragonBallBlue)
                    .cornerRadius(24)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Preview

#Preview {
    NavigationView {
        PlanetListView()
    }
}

#Preview("Empty State") {
    PlanetEmptyStateView()
}

#Preview("Error State") {
    PlanetErrorStateView(
        message: "Unable to load planets.",
        onRetry: {}
    )
}
