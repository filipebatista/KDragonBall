import shared
import SwiftUI

@main
struct iOSApp: App {
    // Use UIApplicationDelegateAdaptor to connect AppDelegate
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
