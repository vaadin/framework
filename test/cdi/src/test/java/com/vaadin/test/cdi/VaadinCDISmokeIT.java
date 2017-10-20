package com.vaadin.test.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.test.cdi.ui.RootPathUI;
import com.vaadin.test.cdi.views.GreetingService;
import com.vaadin.test.cdi.views.GreetingView;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.ParallelRunner;
import com.vaadin.testbench.parallel.ParallelTest;

@RunWith(ParallelRunner.class)
@RunLocally(Browser.PHANTOMJS)
public class VaadinCDISmokeIT extends ParallelTest {

    private static String BASE_URL = "http://localhost:8080/";

    @Rule
    public ScreenshotOnFailureRule rule = new ScreenshotOnFailureRule(this,
            true);

    @Override
    public void setup() throws Exception {
        super.setup();

        testBench().resizeViewPortTo(1024, 600);
    }

    @Test
    public void testPageLoadsAndCanBeInterractedWith() {
        getDriver().navigate().to(BASE_URL);

        $(ButtonElement.class).caption("Click Me").first().click();

        assertTrue($(NotificationElement.class).exists());
        assertEquals(ThankYouServiceImpl.THANK_YOU_TEXT,
                $(NotificationElement.class).first().getText());
    }

    @Test
    public void testAlwaysNewViewIsRecrated() {
        getDriver().navigate().to(BASE_URL);
        navigateAndGetLogContent("new");
        assertNavigationNotification("new");

        // A new navigation event should happen when navigating to view again
        navigateAndGetLogContent("new");
        assertNavigationNotification("new");

        assertEquals("GreetingService should've been only called once.",
                String.format(GreetingView.CALL_COUNT_FORMAT, 1),
                $(LabelElement.class).id("callCount").getText());
    }

    @Test
    public void testParameterChangeRecreatedView() {
        GreetingService service = new GreetingService();

        getDriver().navigate().to(BASE_URL);
        navigateAndGetLogContent("param");
        assertNavigationNotification("param");

        assertEquals("Greeting service was not called with empty parameter.",
                service.getGreeting(""), $(CssLayoutElement.class).id("log")
                        .$(LabelElement.class).first().getText());

        // Navigation event is fired with same view and different parameters
        navigateAndGetLogContent("param/foo");
        assertNavigationNotification("param");

        assertEquals("Greeting service was not called with correct parameters.",
                service.getGreeting("foo"), $(CssLayoutElement.class).id("log")
                        .$(LabelElement.class).first().getText());

        assertEquals("GreetingService should've been only called once.",
                String.format(GreetingView.CALL_COUNT_FORMAT, 1),
                $(LabelElement.class).id("callCount").getText());
    }

    @Test
    public void testParameterChangeUsesSameView() {
        GreetingService service = new GreetingService();

        getDriver().navigate().to(BASE_URL);
        navigateAndGetLogContent("name/foo");
        assertNavigationNotification("name");

        assertEquals("Greeting service was not called with 'foo' parameter.",
                service.getGreeting("foo"), $(CssLayoutElement.class).id("log")
                        .$(LabelElement.class).first().getText());

        // Navigation event fired with same view and different parameters
        navigateAndGetLogContent("name/bar");
        assertNavigationNotification("name");

        assertEquals("GreetingService should've been only called twice.",
                String.format(GreetingView.CALL_COUNT_FORMAT, 2),
                $(LabelElement.class).id("callCount").getText());

        assertEquals("Greeting service was not called with 'bar' parameter.",
                service.getGreeting("bar"), $(CssLayoutElement.class).id("log")
                        .$(LabelElement.class).get(1).getText());
    }

    @Test
    public void testUIScopedView() {
        GreetingService service = new GreetingService();
        List<String> expectedLogContents = new ArrayList<>();

        getDriver().navigate().to(BASE_URL);
        expectedLogContents
                .add(navigateAndGetLogContent("persisting", "foo", service));

        // Navigation event should with same view and different parameters
        expectedLogContents
                .add(navigateAndGetLogContent("persisting", "", service));

        // Navigating to another view should not lose the UI Scoped view.
        navigateAndGetLogContent("new");
        expectedLogContents
                .add(navigateAndGetLogContent("persisting", "", service));

        assertEquals("GreetingService unexpected call count",
                String.format(GreetingView.CALL_COUNT_FORMAT,
                        service.getCallCount()),
                $(LabelElement.class).id("callCount").getText());

        assertEquals("Unexpected contents in the log.", expectedLogContents,
                $(CssLayoutElement.class).id("log").$(LabelElement.class).all()
                        .stream().map(LabelElement::getText)
                        .collect(Collectors.toList()));
    }

    @Test
    public void testPreserveOnRefreshWithViewName() {
        GreetingService service = new GreetingService();
        List<String> expectedLogContents = new ArrayList<>();

        getDriver().navigate().to(BASE_URL);
        expectedLogContents
                .add(navigateAndGetLogContent("name", "foo", service));

        // Navigation event should with same view and different parameters
        expectedLogContents
                .add(navigateAndGetLogContent("name", "bar", service));

        // Reload the page.
        getDriver().navigate().refresh();

        assertEquals("GreetingService should've been only called twice.",
                String.format(GreetingView.CALL_COUNT_FORMAT,
                        service.getCallCount()),
                $(LabelElement.class).id("callCount").getText());

        assertEquals("Unexpected contents in the log.", expectedLogContents,
                $(CssLayoutElement.class).id("log").$(LabelElement.class).all()
                        .stream().map(LabelElement::getText)
                        .collect(Collectors.toList()));
    }

    private void assertNavigationNotification(String string) {
        // Wait animation
        sleep();

        assertTrue(isElementPresent(NotificationElement.class));
        NotificationElement notification = $(NotificationElement.class).first();
        assertEquals(String.format(RootPathUI.NAVIGATION_TEXT, string),
                notification.getText());

        // Close all notifications.
        $(NotificationElement.class).all().forEach(NotificationElement::close);
    }

    /**
     * Sleep method for waiting animations.
     */
    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    private String navigateAndGetLogContent(String viewName) {
        return navigateAndGetLogContent(viewName, "", new GreetingService());
    }

    private String navigateAndGetLogContent(String viewName, String parameter,
            GreetingService service) {
        String fullViewName = viewName
                + (parameter.isEmpty() ? "" : "/" + parameter);
        $(ButtonElement.class).caption(fullViewName).first().click();
        assertNavigationNotification(viewName);
        return service.getGreeting(parameter);
    }
}
