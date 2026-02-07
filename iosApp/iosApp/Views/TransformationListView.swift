import shared
import SwiftUI

struct TransformationListView: View {
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()
    @State private var searchText = ""

    var body: some View {
        let viewModel: TransformationListViewModel = viewModelStoreOwner.viewModel(
            factory: ViewModelFactories.shared.transformationListViewModelFactory
        )

        TransformationListContent(
            viewModel: viewModel,
            searchText: $searchText
        )
        .navigationTitle(String(localized: "transformations_title"))
        .navigationBarTitleDisplayMode(.large)
    }
}

// MARK: - Content View

private struct TransformationListContent: View {
    let viewModel: TransformationListViewModel
    @Binding var searchText: String

    var body: some View {
        ZStack {
            Color.appBackground
                .ignoresSafeArea()

            Observing(viewModel.uiState) { uiState in
                TransformationStateView(
                    uiState: uiState,
                    searchText: $searchText,
                    viewModel: viewModel
                )
            }
        }
    }
}

// MARK: - State View

private struct TransformationStateView: View {
    let uiState: TransformationListUiState
    @Binding var searchText: String
    let viewModel: TransformationListViewModel

    var body: some View {
        VStack(spacing: 0) {
            TransformationSearchBar(text: $searchText, onSearch: { query in
                viewModel.searchTransformations(query: query)
            })
            .padding()

            transformationContent
        }
    }

    @ViewBuilder
    private var transformationContent: some View {
        if let error = uiState.error, uiState.transformations.isEmpty {
            TransformationErrorStateView(
                message: error,
                onRetry: { viewModel.refresh() }
            )
        } else if uiState.isEmpty && !uiState.isLoading {
            TransformationEmptyStateView()
        } else {
            TransformationScrollView(uiState: uiState, viewModel: viewModel)
        }
    }
}

// MARK: - Scroll View

private struct TransformationScrollView: View {
    let uiState: TransformationListUiState
    let viewModel: TransformationListViewModel

    var body: some View {
        ScrollView {
            LazyVStack(spacing: 12) {
                ForEach(uiState.filteredTransformations, id: \.id) { transformation in
                    NavigationLink(destination: TransformationDetailView(transformationId: Int(transformation.id))) {
                        TransformationRow(transformation: transformation)
                    }
                    .buttonStyle(PlainButtonStyle())
                }

                if uiState.isLoading {
                    loadingIndicator
                }
            }
            .padding(.horizontal)
        }
        .refreshable {
            viewModel.refresh()
        }
    }

    private var loadingIndicator: some View {
        HStack {
            Spacer()
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: .dragonBallPurple))
            Spacer()
        }
        .padding()
    }
}

// MARK: - Search Bar

struct TransformationSearchBar: View {
    @Binding var text: String
    var onSearch: (String) -> Void

    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.gray)

            TextField(String(localized: "transformations_search_placeholder"), text: $text)
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

// MARK: - Transformation Row

struct TransformationRow: View {
    let transformation: TransformationDetail

    var body: some View {
        HStack(spacing: 16) {
            // Transformation image
            if !transformation.image.isEmpty {
                AsyncImage(url: URL(string: transformation.image)) { phase in
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
                        transformationPlaceholder
                    @unknown default:
                        EmptyView()
                    }
                }
                .frame(width: 80, height: 80)
            } else {
                transformationPlaceholder
            }

            VStack(alignment: .leading, spacing: 6) {
                Text(transformation.name)
                    .font(.headline)
                    .foregroundColor(.primary)
                    .lineLimit(2)

                HStack {
                    Label(String(format: String(localized: "format_ki"), transformation.ki), systemImage: "bolt.fill")
                        .font(.caption)
                        .foregroundColor(.dragonBallOrange)
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

    private var transformationPlaceholder: some View {
        ZStack {
            Circle()
                .fill(Color.dragonBallPurple.opacity(0.2))
                .frame(width: 80, height: 80)
            Image(systemName: "bolt.fill")
                .font(.system(size: 32))
                .foregroundColor(.dragonBallPurple)
        }
    }
}

// MARK: - Empty State

struct TransformationEmptyStateView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "bolt.slash")
                .font(.system(size: 60))
                .foregroundColor(.gray)

            Text(String(localized: "transformations_empty"))
                .font(.title3)
                .fontWeight(.medium)
                .foregroundColor(.primary)

            Text(String(localized: "transformations_empty_subtitle"))
                .font(.body)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Error State

struct TransformationErrorStateView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: "exclamationmark.triangle.fill")
                .font(.system(size: 60))
                .foregroundColor(.dragonBallPurple)

            Text(String(localized: "error_title"))
                .font(.title)
                .fontWeight(.bold)
                .foregroundColor(.dragonBallPurple)

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
                    .background(Color.dragonBallPurple)
                    .cornerRadius(24)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

// MARK: - Preview

#Preview {
    NavigationView {
        TransformationListView()
    }
}

#Preview("Empty State") {
    TransformationEmptyStateView()
}

#Preview("Error State") {
    TransformationErrorStateView(
        message: "Unable to load transformations.",
        onRetry: {}
    )
}
