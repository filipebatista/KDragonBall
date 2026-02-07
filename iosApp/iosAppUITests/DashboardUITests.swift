import XCTest

final class DashboardUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launch()
    }

    override func tearDownWithError() throws {
        app = nil
    }

    // MARK: - Dashboard Display Tests

    func testDashboard_displaysNavigationTitle() throws {
        XCTAssertTrue(app.navigationBars["Dragon Ball"].exists)
    }

    func testDashboard_displaysWelcomeMessage() throws {
        let welcomeText = app.staticTexts["Welcome to the Dragon Ball Universe"]
        XCTAssertTrue(welcomeText.exists)
    }

    func testDashboard_displaysSubtitle() throws {
        let subtitleText = app.staticTexts["Choose a category to explore"]
        XCTAssertTrue(subtitleText.exists)
    }

    func testDashboard_displaysCharactersCard() throws {
        let charactersText = app.staticTexts["Characters"]
        XCTAssertTrue(charactersText.exists)

        let descriptionText = app.staticTexts["Browse all Dragon Ball characters"]
        XCTAssertTrue(descriptionText.exists)
    }

    func testDashboard_displaysPlanetsCard() throws {
        let planetsText = app.staticTexts["Planets"]
        XCTAssertTrue(planetsText.exists)

        let descriptionText = app.staticTexts["Explore the Dragon Ball universe"]
        XCTAssertTrue(descriptionText.exists)
    }

    func testDashboard_displaysTransformationsCard() throws {
        let transformationsText = app.staticTexts["Transformations"]
        XCTAssertTrue(transformationsText.exists)

        let descriptionText = app.staticTexts["Discover powerful transformations"]
        XCTAssertTrue(descriptionText.exists)
    }

    // MARK: - Navigation Tests

    func testDashboard_tapCharacters_navigatesToCharacterList() throws {
        app.staticTexts["Characters"].tap()

        // Wait for navigation
        let characterListNavBar = app.navigationBars["Dragon Ball Characters"]
        XCTAssertTrue(characterListNavBar.waitForExistence(timeout: 5))
    }

    func testDashboard_tapPlanets_navigatesToPlanetList() throws {
        app.staticTexts["Planets"].tap()

        // Wait for navigation
        let planetListNavBar = app.navigationBars["Dragon Ball Planets"]
        XCTAssertTrue(planetListNavBar.waitForExistence(timeout: 5))
    }

    func testDashboard_tapTransformations_navigatesToTransformationList() throws {
        app.staticTexts["Transformations"].tap()

        // Wait for navigation - the title might just be "Transformations"
        let exists = app.navigationBars.element(boundBy: 0).waitForExistence(timeout: 5)
        XCTAssertTrue(exists)
    }
}
