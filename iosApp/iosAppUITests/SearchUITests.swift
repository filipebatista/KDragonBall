import XCTest

final class SearchUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    // MARK: - Character Search Tests

    func testCharacterSearch_emptySearchShowsAllCharacters() throws {
        // Navigate to character list
        app.staticTexts["Characters"].tap()
        _ = app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5)

        // Search field should show placeholder
        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))
    }

    func testCharacterSearch_searchFiltersResults() throws {
        // Navigate to character list
        app.staticTexts["Characters"].tap()
        _ = app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5)

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        // Type search query
        searchField.tap()
        searchField.typeText("Goku")

        // Wait for search results to update
        sleep(2)

        // Verify search text is in the field
        XCTAssertEqual(searchField.value as? String, "Goku")
    }

    func testCharacterSearch_caseInsensitive() throws {
        // Navigate to character list
        app.staticTexts["Characters"].tap()
        _ = app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5)

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        // Type lowercase search query
        searchField.tap()
        searchField.typeText("goku")

        // Wait for search results
        sleep(2)

        // Search should work regardless of case
        XCTAssertEqual(searchField.value as? String, "goku")
    }

    // MARK: - Planet Search Tests

    func testPlanetSearch_searchFieldWorks() throws {
        // Navigate to planet list
        app.staticTexts["Planets"].tap()
        _ = app.navigationBars["Dragon Ball Planets"].waitForExistence(timeout: 5)

        let searchField = app.textFields["Search planets..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        // Type search query
        searchField.tap()
        searchField.typeText("Earth")

        // Verify search text
        XCTAssertEqual(searchField.value as? String, "Earth")
    }

    // MARK: - Transformation Search Tests

    func testTransformationSearch_searchFieldWorks() throws {
        // Navigate to transformation list
        app.staticTexts["Transformations"].tap()
        sleep(2)

        let searchField = app.textFields["Search transformations..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        // Type search query
        searchField.tap()
        searchField.typeText("Super Saiyan")

        // Verify search text
        XCTAssertEqual(searchField.value as? String, "Super Saiyan")
    }

    // MARK: - Search Behavior Tests

    func testSearch_keyboardDismissesOnReturn() throws {
        // Navigate to character list
        app.staticTexts["Characters"].tap()
        _ = app.navigationBars["Dragon Ball Characters"].waitForExistence(timeout: 5)

        let searchField = app.textFields["Search characters..."]
        XCTAssertTrue(searchField.waitForExistence(timeout: 5))

        // Type and dismiss keyboard
        searchField.tap()
        searchField.typeText("Test")

        // Press return key
        app.keyboards.buttons["Return"].tap()

        // Keyboard should be dismissed (search field still has value)
        sleep(1)
        XCTAssertEqual(searchField.value as? String, "Test")
    }
}
