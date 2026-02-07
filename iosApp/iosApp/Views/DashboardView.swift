import SwiftUI

struct DashboardView: View {
    var body: some View {
        NavigationView {
            ZStack {
                Color.appBackground
                    .ignoresSafeArea()

                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        // Header
                        VStack(alignment: .leading, spacing: 8) {
                            Text(String(localized: "dashboard_welcome"))
                                .font(.title2)
                                .fontWeight(.bold)
                                .foregroundColor(.primary)

                            Text(String(localized: "dashboard_subtitle"))
                                .font(.body)
                                .foregroundColor(.secondary)
                        }
                        .padding(.horizontal)
                        .padding(.top, 16)

                        // Dashboard Items
                        VStack(spacing: 16) {
                            NavigationLink(destination: CharacterListView()) {
                                DashboardCard(
                                    title: String(localized: "dashboard_characters"),
                                    description: String(localized: "dashboard_characters_desc"),
                                    icon: "person.fill",
                                    gradientColors: [.dragonBallOrange, .dragonBallOrange.opacity(0.7)]
                                )
                            }
                            .buttonStyle(PlainButtonStyle())

                            NavigationLink(destination: PlanetListView()) {
                                DashboardCard(
                                    title: String(localized: "dashboard_planets"),
                                    description: String(localized: "dashboard_planets_desc"),
                                    icon: "globe",
                                    gradientColors: [.dragonBallBlue, .dragonBallBlue.opacity(0.7)]
                                )
                            }
                            .buttonStyle(PlainButtonStyle())

                            NavigationLink(destination: TransformationListView()) {
                                DashboardCard(
                                    title: String(localized: "dashboard_transformations"),
                                    description: String(localized: "dashboard_transformations_desc"),
                                    icon: "bolt.fill",
                                    gradientColors: [.dragonBallPurple, .dragonBallPurple.opacity(0.7)]
                                )
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                        .padding(.horizontal)

                        Spacer(minLength: 20)
                    }
                }
            }
            .navigationTitle(String(localized: "dashboard_title"))
            .navigationBarTitleDisplayMode(.large)
        }
        .accentColor(.dragonBallOrange)
        .tint(.dragonBallOrange)
        .setupNavigationBarAppearance()
    }
}

// MARK: - Dashboard Card

struct DashboardCard: View {
    let title: String
    let description: String
    let icon: String
    let gradientColors: [Color]

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 8) {
                Text(title)
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)

                Text(description)
                    .font(.body)
                    .foregroundColor(.white.opacity(0.9))
            }

            Spacer()

            // Icon container
            ZStack {
                RoundedRectangle(cornerRadius: 16)
                    .fill(Color.white.opacity(0.2))
                    .frame(width: 60, height: 60)

                Image(systemName: icon)
                    .font(.system(size: 28))
                    .foregroundColor(.white)
            }
        }
        .padding(20)
        .frame(height: 140)
        .background(
            LinearGradient(
                gradient: Gradient(colors: gradientColors),
                startPoint: .leading,
                endPoint: .trailing
            )
        )
        .cornerRadius(20)
        .shadow(color: gradientColors.first?.opacity(0.4) ?? .clear, radius: 8, x: 0, y: 4)
    }
}

// MARK: - Preview

#Preview {
    DashboardView()
}

#Preview("Dashboard Card") {
    DashboardCard(
        title: "Characters",
        description: "Browse all Dragon Ball characters",
        icon: "person.fill",
        gradientColors: [.dragonBallOrange, .dragonBallOrange.opacity(0.7)]
    )
    .padding()
}
