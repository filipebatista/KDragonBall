import shared
import UIKit

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Initialize Koin on app launch
        KoinInitializer.shared.initialize()

        return true
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Clean up Koin when app terminates
        KoinInitializer.shared.stop()
    }
}
