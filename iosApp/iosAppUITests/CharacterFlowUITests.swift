import XCTest

final class CharacterFlowUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    private func navigateToCharacterList() {
        app.staticTexts["Characters"].tap()
        _ = app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5)
    }

    // MARK: - Character List Tests

    func testCharacterList_displaysNavigationTitle() throws {
        navigateToCharacterList()

        XCTAssertTrue(app.navigationBars["Dragon Ball Characters"].exists)
    }

    func testCharacterList_displaysSearchBar() throws {
        navigateToCharacterList()

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))
    }

    func testCharacterList_hasBackButton() throws {
        navigateToCharacterList()

        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        XCTAssertTrue(backButton.exists)
    }

    func testCharacterList_backButtonReturnsToDashboard() throws {
        navigateToCharacterList()

        // Tap back button
        let backButton = app.navigationBars.buttons.element(boundBy: 0)
        backButton.tap()

        // Verify we're back on dashboard
        let welcomeText = app.staticTexts["Welcome to the Dragon Ball Universe"]
        XCTAssertTrue(welcomeText.waitForExistence(timeout: 5))
    }

    // MARK: - Search Tests

    func testCharacterList_searchFieldAcceptsInput() throws {
        navigateToCharacterList()

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        searchField.tap()
        searchField.typeText("Goku")

        // Verify text was entered
        XCTAssertEqual(searchField.value as? String, "Goku")
    }

    func testCharacterList_searchFieldCanBeCleared() throws {
        navigateToCharacterList()

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        searchField.tap()
        searchField.typeText("Test")

        // Clear the field
        let clearButton = app.buttons["Clear text"]
        if clearButton.exists {
            clearButton.tap()
        } else {
            // Try alternative clear method
            searchField.buttons.element(boundBy: 0).tap()
        }

        // Wait a moment for the field to clear
        sleep(1)

        // Check if field is empty or placeholder is shown
        let fieldValue = searchField.value as? String ?? ""
        let isEmpty = fieldValue.isEmpty || fieldValue == "Search characters..."
        XCTAssertTrue(isEmpty)
    }

    // MARK: - Navigation Flow Tests

    func testCharacterFlow_fullNavigation() throws {
        // Start from dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].exists)

        // Navigate to character list
        app.staticTexts["Characters"].tap()
        XCTAssertTrue(app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5))

        // Go back
        app.navigationBars.buttons.element(boundBy: 0).tap()

        // Verify we're back on dashboard
        XCTAssertTrue(app.staticTexts["Welcome to the Dragon Ball Universe"].waitForExistence(timeout: 5))
    }
}
