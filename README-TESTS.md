# TestBench tests in Vaadin Framework
## Project setup
The project currently supports running TestBench 3+ (java) tests. Each test consists of a Vaadin UI class and a TestBench script/java test.

All test UI classes go into uitest/src. These files are automatically packaged into a war file which is deployed to a Jetty server during the build process so that tests can open and interact with the UI:s. For development purposes, the Jetty server can be started in Eclipse, see running tests in Eclipse.

The project is setup so that /run is mapped to a specialized servlet which allows you to add the UI you want to test to the URL, e.g. http://localhost:8888/run/com.vaadin.tests.component.label.LabelModes or just http://localhost:8888/run/LabelModes if there are no multiple classes named LabelModes. Because of caching, the ?restartApplication parameter is needed after the first run if you want to run multiple test classes.

## Creating a new test
Creating a new test involves two steps: Creating a test UI (typically this should be provided in the ticket) and creating a TestBench test for the UI.

## Creating a new test UI
Test UIs are created inside the uitest/src folder in the com.vaadin.tests package. For tests for a given component, use the com.vaadin.tests.components.<component> package. For other features, use a suitable existing com.vaadin.tests.<something> package or create a new one if no suitable exists.

The test should be named according to what it tests, e.g. EnsureFormTooltipWorks. Names should not refer to a ticket, e.g. Ticket1123 will automatically be rejected during code review as it is non-descriptive.

There are a couple of helper UI super classes which you can use for the test. You should never extend UI directly:
* AbstractTestUI
  * Automatically sets up a VerticalLayout with a description label on the top. Use addComponent() to add components to the layout
  * Supports ?transport=websocket/streaming/xhr parameter for setting push mode. This is for testing core push functionality, typically you should just add @Push to your test UI
* AbstractTestUIWithLog
  * AbstractTestUI but adds a log at the top to which you can add rows using log(String). Handy for printing state information which the TB test can assert reliably.
* ~~AbstractTestCase, TestBase~~
  * Old base classes for tests. Don’t use for any new tests. Extends LegacyApplication.
* AbstractComponentTest, AbstractFieldTest, ...
  * Base classes for generic component tests. Generates test which have a menu on the top containing options for configuring the component. Classes follow the same component hierarchy as Vaadin component classes and this way automatically gets menu items for setting features the parent class supports.
    * Gotcha: If you add a new feature to a menu you need to run and possibly (probably) fix all TB tests which use the class as they will click on the wrong item (fixable by implementing [http://dev.vaadin.com/ticket/11307](http://dev.vaadin.com/ticket/11307))

## Creating a TestBench test for a UI
All new test for the projects must be created as TestBench3+ tests
### Test class naming
TestBench 3+ tests usually follow a naming convention which automatically maps the test to the UI class. By the convention, the TB3 test class should be named **UIClassTest**, e.g. if UI is **LabelModes** then the TB3+ test is **LabelModesTest**. Inside the LabelModesTest class there are 1..N methods which test various aspects of the LabelModes UI. This is the preferred way, but not strictly necessary; sometimes it's more conventient to use a different name for the UI and test class. In that case, remember to override the `AbstractTB3Test::getUIClass()` method in the test.  

### Super class
There are a couple of super classes you can use for a TB3+ test:
* MultiBrowserTest
  * Ensures the test is run on the browsers we support automatically
  * **This is what you typically should use**
* WebsocketTest
  * Run only on browsers which supports websockets

### Creating the test
The actual test is one method in the test class created for the given UI. The method declaration is something like:

    @Test
    public void testLabelModes() throws Exception {


“@Test” is needed for it to be run at all.

“throws Exception” should usually be added to avoid catching exceptions inside the test, as most often an exception means the test has failed. Unhandled exceptions will always fail the test; if you use checked exceptions, you'll need to handle them somehow or decide they're automatically failures on exception.

The beginning of the test should request the UI using openTestURL();

    public void testLabelModes() throws Exception {
    // Causes the test to be opened with the debug console open. Typically not needed
    // setDebug(true);
    
    // Causes the test to be opened with push enabled even though the UI does not have @Push.
    //Typically not needed
    // setPush(true);
    openTestURL();
After this it is up to you what the test should do. The browser will automatically be started before the test method and shutdown after it. If the test fails, a failure screenshot will automatically be created in addition to an exception being thrown.

When done, run the test locally, as described below, and ensure it passes. Then run it remotely, as described below, a couple of times to ensure it passes reliably.

### Best practices for creating a TB3 test

#### Communicate intent through your test class
JUnit allows you (as opposed to TB2 HTML tests) to describe what the test should do using method names. The test method should be a chain of calls which enables any reader to quickly understand what the test does, all details should be abstracted into methods.

Over the time we should collect useful helper methods into the parent classes (AbstractTB3Test already contain some, e.g. assertGreater/assertLessThan/vaadinElementById/...).

#### Do a sensible amount of things in one test method
There is an overhead before and after each test method when the browser instance is started. The tests are therefore not restricted to testing one single thing. Instead a test method should test one logical group of things (TB3 tests are not pure unit tests).

####Use ids in your test class

Use ids in your UI class. Define the IDs as constants in the UI class. Use the constants in your Test class. Use static imports to avoid massive prefixes for the constants.

    public class NativeButtonIconAndText extends AbstractTestUI implements
        ClickListener {

        static final String BUTTON_TEXT = "buttonText";
     [...]
        buttonText.setId(BUTTON_TEXT);

     

    public class NativeButtonIconAndTextTest extends MultiBrowserTest {

        @Test
        public void testNativeButtonIconAltText() {
            openTestURL();
            assertAltText(NativeButtonIconAndText.BUTTON_TEXT, "");


## Running the DevelopmentServerLauncher

The Jetty included in the project can be started in Eclipse by running the “Development Server (Vaadin)” launch configuration in /eclipse (right click -> Debug as  -> Development Server (Vaadin) ). This deploys all the tests to localhost:8888 (runs on port 8888, don’t change that unless you want problems). Use /run/<uiclass> to open a test.

The DevelopmentServerLauncher has a built-in feature which ensures only one instance can be running at a time. There is therefore no need to first stop the old and and then start a new one. Start the server and the old one will be killed automatically.
## Setup before running any tests in Eclipse

Before running any tests in Eclipse you need to
1. copy uitest/eclipse-run-selected-test.properties to work/eclipse-run-selected-test.properties
2. edit work/eclipse-run-selected-test.properties
    1. Define com.vaadin.testbench.screenshot.directory as the directory where you checked out the screenshots repository (this directory contains the “references” subdirectory)

## Running TB3+ tests

TB3+ tests are standard JUnit tests which can be run using the “Run as -> JUnit test” (or Debug as) in Eclipse. Right click on a single test instance in the JUnit result view and select to debug to re-run it on a single browser.

### Debugging TB3+ tests

#### Debugging remotely
Running remotely on a single browser (as described above) can be used to debug issues with a given browser but with the downside that you cannot see what is happening with the browser. In theory this is possible if you figure out on what machine the test is run (no good way for this at the moment) and use VNC to connect to that.
#### Debugging locally
A better option is to run the test on a local browser instance. To do this you need to add a `@RunLocally` annotation to the test class. @RunLocally uses Firefox by default but also supports other browsers using `@RunLocally(CHROME)`, `@RunLocally(SAFARI)` and `@RunLocally(PHANTOMJS)`.

Some local configuration is needed for certain browsers, especially if you did not install them in the “standard” locations.

**PhantomJS**: PhantomJS is a headless browser, good especially for fast validation. Download it from the [PhantomJS site](http://phantomjs.org/download.html) and add the binary to your PATH. 

**Firefox**: If you have Firefox in your PATH, this is everything you need. If Firefox cannot be started, add a **firefox.path** property to `/work/eclipse-run-selected-test.properties`, pointing to your Firefox binary (e.g.`firefox.path=/Applications/Firefox 17 ESR.app/Contents/MacOS/firefox`)

**Chrome**: You need to [download ChromeDriver](http://chromedriver.storage.googleapis.com/index.html) and add a chrome.driver.path property to `/work/eclipse-run-selected-test.properties`, pointing to the ChromeDriver binary (NOT the Chrome binary)

**Safari**: At least on Mac, no configuration should be needed.

Remember to remove `@RunLocally()` before committing the test or it will fail during the build.
