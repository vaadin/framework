/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.WrapsElement;

import com.vaadin.testbench.By.ByVaadin;
import com.vaadin.testbench.commands.CanCompareScreenshots;
import com.vaadin.testbench.commands.CanWaitForVaadin;
import com.vaadin.testbench.commands.ScreenshotComparator;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * TestBenchElement is a WebElement wrapper. It provides Vaadin specific helper
 * functionality. TestBenchElements are created when you search for elements
 * from TestBenchTestCase or a context relative search from TestBenchElement.
 */
public class TestBenchElement extends AbstractHasTestBenchCommandExecutor
        implements WrapsElement, WebElement, TestBenchElementCommands,
        CanWaitForVaadin, HasDriver, CanCompareScreenshots {

    private WebElement actualElement = null;
    private TestBenchCommandExecutor tbCommandExecutor = null;

    protected TestBenchElement() {

    }

    protected TestBenchElement(WebElement webElement,
            TestBenchCommandExecutor tbCommandExecutor) {
        init(webElement, tbCommandExecutor);
    }

    /**
     * TestBenchElement initialization function. If a subclass of
     * TestBenchElement needs to run some initialization code, it should
     * override init(), not this function.
     *
     * @param element
     *            WebElement to wrap
     * @param tbCommandExecutor
     *            TestBenchCommandExecutor instance
     */
    protected void init(WebElement element,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (null == this.tbCommandExecutor) {
            this.tbCommandExecutor = tbCommandExecutor;
            actualElement = element;
            init();
        }
    }

    /**
     * Checks if the current test is running on PhantomJS.
     *
     * @return <code>true</code> if the test is running on PhantomJS,
     *         <code>false</code> otherwise
     */
    protected boolean isPhantomJS() {
        return BrowserUtil.isPhantomJS(getCapabilities());
    }

    /**
     * Checks if the current test is running on Chrome.
     *
     * @return <code>true</code> if the test is running on Chrome,
     *         <code>false</code> otherwise
     */
    protected boolean isChrome() {
        return BrowserUtil.isChrome(getCapabilities());
    }

    /**
     * Checks if the current test is running on Firefox.
     *
     * @return <code>true</code> if the test is running on Firefox,
     *         <code>false</code> otherwise
     */
    protected boolean isFirefox() {
        return BrowserUtil.isFirefox(getCapabilities());
    }

    /**
     * Returns information about current browser used
     *
     * @see org.openqa.selenium.Capabilities
     * @return information about current browser used
     */
    protected Capabilities getCapabilities() {
        WebDriver driver;
        if (getDriver() instanceof TestBenchDriverProxy) {
            driver = ((TestBenchDriverProxy) getDriver()).getActualDriver();
        } else {
            driver = getDriver();
        }

        if (driver instanceof HasCapabilities) {
            return ((HasCapabilities) driver).getCapabilities();
        } else {
            return null;
        }
    }

    /**
     * This is run after initializing a TestBenchElement. This can be overridden
     * in subclasses of TestBenchElement to run some initialization code.
     */
    protected void init() {

    }

    @Override
    public WebElement getWrappedElement() {
        return actualElement;
    }

    @Override
    public void waitForVaadin() {
        if (getCommandExecutor() != null) {
            getCommandExecutor().waitForVaadin();
        }
    }

    @Override
    public void showTooltip() {
        waitForVaadin();
        new Actions(getCommandExecutor().getWrappedDriver())
                .moveToElement(actualElement).perform();
        // Wait for a small moment for the tooltip to appear
        try {
            Thread.sleep(1000); // VTooltip.OPEN_DELAY = 750;
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Sets the number of pixels that an element's content is scrolled from the
     * top.
     *
     * @param scrollTop
     *            value set to Element.scroll property
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#scroll(int)
     */
    @Override
    public void scroll(int scrollTop) {
        JavascriptExecutor js = getCommandExecutor();
        js.executeScript("arguments[0].scrollTop = " + scrollTop,
                actualElement);
    }

    /**
     * Sets the number of pixels that an element's content is scrolled to the
     * left.
     *
     * @param scrollLeft
     *            value set to Element.scrollLeft property
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#scrollLeft(int)
     */
    @Override
    public void scrollLeft(int scrollLeft) {
        JavascriptExecutor js = getCommandExecutor();
        js.executeScript("arguments[0].scrollLeft = " + scrollLeft,
                actualElement);
    }

    @Override
    public void click() {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.click();
    }

    @Override
    public void submit() {
        click();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        autoScrollIntoView();
        waitForVaadin();
        actualElement.clear();
    }

    @Override
    public String getTagName() {
        waitForVaadin();
        return actualElement.getTagName();
    }

    @Override
    public String getAttribute(String name) {
        waitForVaadin();
        return actualElement.getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        autoScrollIntoView();
        waitForVaadin();
        return actualElement.isSelected();
    }

    /**
     * Returns whether the Vaadin component, that this element represents, is
     * enabled or not.
     *
     * @return true if the component is enabled.
     */
    @Override
    public boolean isEnabled() {
        waitForVaadin();
        return !hasClassName("v-disabled") && actualElement.isEnabled();
    }

    @Override
    public String getText() {
        autoScrollIntoView();
        waitForVaadin();
        return actualElement.getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> elements = new ArrayList<>();
        if (by instanceof ByVaadin) {
            elements.addAll(
                    wrapElements(by.findElements(this), getCommandExecutor()));
        } else {
            elements.addAll(wrapElements(actualElement.findElements(by),
                    getCommandExecutor()));
        }
        return elements;
    }

    @Override
    public WebElement findElement(By by) {
        waitForVaadin();
        if (by instanceof ByVaadin) {
            return wrapElement(by.findElement(this), getCommandExecutor());
        }
        return wrapElement(actualElement.findElement(by), getCommandExecutor());
    }

    /**
     * Calls the Javascript click method on the element.
     *
     * Useful for elements that are hidden or covered by a pseudo-element on
     * some browser-theme combinations (for instance Firefox-Valo)
     */
    public void clickHiddenElement() {
        getCommandExecutor().executeScript("arguments[0].click()",
                actualElement);
    }

    @Override
    public boolean isDisplayed() {
        waitForVaadin();
        return actualElement.isDisplayed();
    }

    @Override
    public Point getLocation() {
        waitForVaadin();
        return actualElement.getLocation();
    }

    @Override
    public Dimension getSize() {
        waitForVaadin();
        return actualElement.getSize();
    }

    @Override
    public String getCssValue(String propertyName) {
        waitForVaadin();
        return actualElement.getCssValue(propertyName);
    }

    @Override
    public void click(int x, int y, Keys... modifiers) {
        autoScrollIntoView();
        waitForVaadin();
        Actions actions = new Actions(getCommandExecutor().getWrappedDriver());
        actions.moveToElement(actualElement, x, y);
        // Press any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyDown(modifier);
        }
        actions.click();
        // Release any modifier keys
        for (Keys modifier : modifiers) {
            actions.keyUp(modifier);
        }
        actions.build().perform();
    }

    public void doubleClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).doubleClick(actualElement).build().perform();
        // Wait till vaadin component will process the event. Without it may
        // cause problems with phantomjs
    }

    public void contextClick() {
        autoScrollIntoView();
        waitForVaadin();
        new Actions(getDriver()).contextClick(actualElement).build().perform();
        // Wait till vaadin component will process the event. Without it may
        // cause problems with phantomjs
    }

    @Override
    public <T extends AbstractElement> T wrap(Class<T> elementType) {
        return TestBench.createElement(elementType, getWrappedElement(),
                getCommandExecutor());
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return tbCommandExecutor;
    }

    @Override
    public WebDriver getDriver() {
        return getCommandExecutor().getWrappedDriver();
    }

    /**
     * Returns this TestBenchElement cast to a SearchContext. Method provided
     * for compatibility and consistency.
     */
    @Override
    public SearchContext getContext() {
        return this;
    }

    /**
     * Move browser focus to this Element
     */
    @Override
    public void focus() {
        waitForVaadin();
        getCommandExecutor().focusElement(this);
    }

    protected static List<TestBenchElement> wrapElements(
            List<WebElement> elements,
            TestBenchCommandExecutor tbCommandExecutor) {
        List<TestBenchElement> wrappedList = new ArrayList<>();

        for (WebElement e : elements) {
            wrappedList.add(wrapElement(e, tbCommandExecutor));
        }

        return wrappedList;
    }

    protected static TestBenchElement wrapElement(WebElement element,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (element instanceof TestBenchElement) {
            return (TestBenchElement) element;
        } else {
            return TestBench.createElement(element, tbCommandExecutor);
        }
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        waitForVaadin();
        return actualElement.getScreenshotAs(target);
    }

    @Override
    public Rectangle getRect() {
        waitForVaadin();
        return actualElement.getRect();
    }

    /**
     * Gets all the class names set for this element.
     *
     * @return a set of class names
     */
    public Set<String> getClassNames() {
        String classAttribute = getAttribute("class");
        Set<String> classes = new HashSet<>();
        if (classAttribute == null) {
            return classes;
        }
        classAttribute = classAttribute.trim();
        if (classAttribute.isEmpty()) {
            return classes;
        }

        Collections.addAll(classes, classAttribute.split("[ ]+"));
        return classes;
    }

    /**
     * Checks if this element has the given class name.
     * <p>
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar bafoo"
     *
     * @param className
     *            the class name to check for
     * @return <code>true</code> if the element has the given class name,
     *         <code>false</code> otherwise
     */
    public boolean hasClassName(String className) {
        if (className == null || className.isEmpty()) {
            return false;
        }

        return getClassNames().contains(className);
    }

    @Override
    public boolean equals(Object obj) {
        if (actualElement == null) {
            return false;
        }
        return actualElement.equals(obj);
    }

    @Override
    public int hashCode() {
        if (actualElement == null) {
            return 32;
        }

        return actualElement.hashCode();
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        return ScreenshotComparator.compareScreen(referenceId,
                getCommandExecutor().getReferenceNameGenerator(),
                getCommandExecutor().getImageComparison(), this,
                (HasCapabilities) getDriver());
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        return ScreenshotComparator.compareScreen(reference,
                getCommandExecutor().getImageComparison(),
                (TakesScreenshot) this, (HasCapabilities) getDriver());

    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        return ScreenshotComparator.compareScreen(reference, referenceName,
                getCommandExecutor().getImageComparison(),
                (TakesScreenshot) this, (HasCapabilities) getDriver());
    }

    /***
     * Scrolls the element into the visible area of the browser window
     */
    public void scrollIntoView() {
        getCommandExecutor().executeScript("arguments[0].scrollIntoView()",
                actualElement);
    }

    /**
     * Scrolls the element into the visible area of the browser window if
     * {@link TestBenchCommands#isAutoScrollIntoView()} is enabled and
     * the element is not displayed
     */
    private void autoScrollIntoView() {
        if (getCommandExecutor().isAutoScrollIntoView()) {
            if(!actualElement.isDisplayed()){
                scrollIntoView();
            }
        }
    }

}
