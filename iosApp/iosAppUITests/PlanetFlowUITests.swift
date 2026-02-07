import XCTest

final class PlanetFlowUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    private func navigateToPlanetList() {
        app.staticTexts["Planets"].tap()
        _ = app.navigationBars["Dragon Ball Planets"].waitForExistence(timeout: 5)
    }

    // MARK: - Planet List Tests

    func testPlanetList_displaysNavigationTitle() throws {
        navigateToPlanetList()

        XCTAssertTrue(app.navigationBars["Dragon Ball Planets"].exists)
    }

    func testPlanetList_displaysSearchBar() throws {
        navigateToPlanetList()

        let searchField = app.textFields["Search planets..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))
    }

    func testPlanetList_hasBackButton() throws {
        navigateToPlanetList()

        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        XCTAssertTrue(backButton.exists)
    }

    func testPlanetList_backButtonReturnsToDashboard() throws {
        navigateToPlanetList()

        // Tap back button
        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        backButton.tap()

        // Verify we're back on dashboard
        let welcomeText = app.staticTexts["Welcome to the Dragon Ball Universe"]
        XCTAssertTrue(welcomeText.waitForExistence(timeout: 5))
    }

    // MARK: - Search Tests

    func testPlanetList_searchFieldAcceptsInput() throws {
        navigateToPlanetList()

        let searchField = app.textFields["Search planets..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        searchField.tap()
        searchField.typeText("Earth")

        // Verify text was entered
        XCTAssertEqual(searchField.value as? String, "Earth")
    }

    // MARK: - Navigation Flow Tests

    func testPlanetFlow_fullNavigation() throws {
        // Start from dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].exists)

        // Navigate to planet list
        app.staticTexts["Planets"].tap()
        XCTAssertTrue(app.navigationBars["Dragon Ball Planets"].waitForExistence(timeout: 5))

        // Go back
        app.navigationBars.buttons.element(boundBy: 0).tap()

        // Verify we're back on dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].waitForExistence(timeout: 5))
    }
}
