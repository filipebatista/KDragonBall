import Foundation

func localized(_ key: String) -> String {
    return NSLocalizedString(key, bundle: Bundle.main, comment: "")
}
