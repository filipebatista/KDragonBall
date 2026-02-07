import SwiftUI

// MARK: - Dragon Ball Theme Colors (matching Android exactly)

extension Color {
    // Primary Colors - matching Android hex values
    // DragonBallOrange = 0xFFFF6B35
    static let dragonBallOrange = Color(red: 255/255, green: 107/255, blue: 53/255)
    // DragonBallBlue = 0xFF2A9DF4
    static let dragonBallBlue = Color(red: 42/255, green: 157/255, blue: 244/255)
    // DragonBallYellow = 0xFFFFD23F
    static let dragonBallYellow = Color(red: 255/255, green: 210/255, blue: 63/255)

    // Additional colors from Android
    // DragonBallDarkOrange = 0xFFD94E2A
    static let dragonBallDarkOrange = Color(red: 217/255, green: 78/255, blue: 42/255)
    // DragonBallLightOrange = 0xFFFFB084
    static let dragonBallLightOrange = Color(red: 255/255, green: 176/255, blue: 132/255)
    // DragonBallPurple = 0xFF6A4C93
    static let dragonBallPurple = Color(red: 106/255, green: 76/255, blue: 147/255)
    // DragonBallDarkBlue = 0xFF1E3A8A
    static let dragonBallDarkBlue = Color(red: 30/255, green: 58/255, blue: 138/255)
    // DragonBallGold = 0xFFFFA500
    static let dragonBallGold = Color(red: 255/255, green: 165/255, blue: 0/255)

    // Background & Surface - matching Android light theme
    // background = 0xFFFFF8F0
    static let appBackground = Color(red: 255/255, green: 248/255, blue: 240/255)
    // surface = White
    static let appSurface = Color.white
    // surfaceVariant = 0xFFFFF4E8
    static let appSurfaceVariant = Color(red: 255/255, green: 244/255, blue: 232/255)
}

// MARK: - Navigation Bar Appearance

extension View {
    func setupNavigationBarAppearance() -> some View {
        self.onAppear {
            let appearance = UINavigationBarAppearance()
            appearance.configureWithOpaqueBackground()
            appearance.backgroundColor = UIColor(red: 255/255, green: 248/255, blue: 240/255, alpha: 1)
            appearance.titleTextAttributes = [.foregroundColor: UIColor(red: 255/255, green: 107/255, blue: 53/255, alpha: 1)]
            appearance.largeTitleTextAttributes = [.foregroundColor: UIColor(red: 255/255, green: 107/255, blue: 53/255, alpha: 1)]

            // Back button color
            let buttonAppearance = UIBarButtonItemAppearance()
            buttonAppearance.normal.titleTextAttributes = [.foregroundColor: UIColor(red: 255/255, green: 107/255, blue: 53/255, alpha: 1)]
            appearance.buttonAppearance = buttonAppearance
            appearance.backButtonAppearance = buttonAppearance

            UINavigationBar.appearance().standardAppearance = appearance
            UINavigationBar.appearance().scrollEdgeAppearance = appearance
            UINavigationBar.appearance().compactAppearance = appearance
            UINavigationBar.appearance().tintColor = UIColor(red: 255/255, green: 107/255, blue: 53/255, alpha: 1)
        }
    }
}
