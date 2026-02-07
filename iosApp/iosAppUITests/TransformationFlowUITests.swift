import XCTest

final class TransformationFlowUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    private func navigateToTransformationList() {
        app.staticTexts["Transformations"].tap()
        // Wait for navigation - the nav bar might have "Transformations" title
        sleep(2)
    }

    // MARK: - Transformation List Tests

    func testTransformationList_displaysAfterNavigation() throws {
        navigateToTransformationList()

        // Verify we navigated away from dashboard
        let welcomeText = app.staticTexts["Welcome to the Dragon Ball Universe"]
        XCTAssertFalse(welcomeText.exists)
    }

    func testTransformationList_displaysSearchBar() throws {
        navigateToTransformationList()

        let searchField = app.textFields["Search transformations..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))
    }

    func testTransformationList_hasBackButton() throws {
        navigateToTransformationList()

        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        XCTAssertTrue(backButton.exists)
    }

    func testTransformationList_backButtonReturnsToDashboard() throws {
        navigateToTransformationList()

        // Tap back button
        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        backButton.tap()

        // Verify we're back on dashboard
        let welcomeText = app.staticTexts["Welcome to the Dragon Ball Universe"]
        XCTAssertTrue(welcomeText.waitForExistence(timeout: 5))
    }

    // MARK: - Search Tests

    func testTransformationList_searchFieldAcceptsInput() throws {
        navigateToTransformationList()

        let searchField = app.textFields["Search transformations..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        searchField.tap()
        searchField.typeText("Super")

        // Verify text was entered
        XCTAssertEqual(searchField.value as? String, "Super")
    }

    // MARK: - Navigation Flow Tests

    func testTransformationFlow_fullNavigation() throws {
        // Start from dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].exists)

        // Navigate to transformation list
        app.staticTexts["Transformations"].tap()
        sleep(2)

        // Verify we navigated away from dashboard
        XCTAssertFalse(app.staticTexts["Welcome to the Dragon Ball Universe"].exists)

        // Go back
        app.navigationBars.buttons.element(boundBy: 0).tap()

        // Verify we're back on dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].waitForExistence(timeout: 5))
    }
}
