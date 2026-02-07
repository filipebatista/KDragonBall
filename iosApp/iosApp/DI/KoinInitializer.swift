import Foundation
import shared

class KoinInitializer {
    static let shared = KoinInitializer()

    func initialize() {
        KoinHelper.shared.doInitKoin()
    }

    func stop() {
        KoinHelper.shared.doStopKoin()
    }
}
