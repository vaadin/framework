package com.vaadin.tests.fields;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TabIndexesTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testTabIndexesSetToZero() {
        // clicked by default
        assertLogText("1. Setting tab indexes to 0");
        for (WebElement element : getFieldElements()) {
            assertTabIndex("0", element);
        }
    }

    @Test
    public void testTabIndexesSetToOne() {
        setTabIndexesTo("1");
        for (WebElement element : getFieldElements()) {
            assertTabIndex("1", element);
        }
    }

    @Test
    public void testTabIndexesSetToOneThroughN() {
        setTabIndexesTo("1..N");
        int counter = 0;
        for (WebElement element : getFieldElements()) {
            ++counter;
            assertTabIndex(String.valueOf(counter), element);
        }
    }

    @Test
    public void testTabIndexesSetToNThroughOne() {
        setTabIndexesTo("N..1");
        List<WebElement> fieldElements = getFieldElements();
        int counter = fieldElements.size();
        for (WebElement element : fieldElements) {
            assertTabIndex(String.valueOf(counter), element);
            --counter;
        }
    }

    private void setTabIndexesTo(String expected) {
        String caption = String.format("Set %stab indexes to %s",
                (expected.contains("N") ? "" : "all "), expected);
        $(ButtonElement.class).caption(caption).first().click();
        assertLogText("2. Setting tab indexes to " + expected);
    }

    private void assertLogText(String expected) {
        Assert.assertEquals("Unexpected log contents,", expected, getLogRow(0));
    }

    private void assertTabIndex(String expected, WebElement element) {
        Assert.assertEquals("Unexpected tab index,", expected,
                element.getAttribute("tabIndex"));
    }

    private List<WebElement> getFieldElements() {
        List<WebElement> fieldElements = new ArrayList<WebElement>();
        fieldElements.add(getElement1());
        fieldElements.add(getElement2());
        fieldElements.add(getElement3());
        fieldElements.add(getElement4());
        fieldElements.add(getElement5());
        fieldElements.add(getElement6());
        fieldElements.add(getElement7());
        fieldElements.add(getElement8());
        fieldElements.add(getElement9());
        fieldElements.add(getElement10());
        fieldElements.add(getElement11());
        fieldElements.add(getElement12());
        fieldElements.add(getElement13());
        fieldElements.add(getElement14());
        fieldElements.add(getElement15());
        fieldElements.add(getElement16());
        fieldElements.add(getElement17());
        return fieldElements;
    }

    private WebElement getElement1() {
        return vaadinElement("PID_Sfield-1/domChild[1]/domChild[1]");
    }

    private WebElement getElement2() {
        return vaadinElement("PID_Sfield-2/domChild[0]");
    }

    private WebElement getElement3() {
        return vaadinElement("PID_Sfield-3/domChild[0]");
    }

    private WebElement getElement4() {
        return vaadinElement("PID_Sfield-4/domChild[0]");
    }

    private WebElement getElement5() {
        return vaadinElement("PID_Sfield-5");
    }

    private WebElement getElement6() {
        return vaadinElement("PID_Sfield-6/domChild[0]");
    }

    private WebElement getElement7() {
        return vaadinElement("PID_Sfield-7/domChild[0]");
    }

    private WebElement getElement8() {
        return vaadinElement("PID_Sfield-8/domChild[0]/domChild[0]");
    }

    private WebElement getElement9() {
        return vaadinElement("PID_Sfield-9/domChild[1]/domChild[1]");
    }

    private WebElement getElement10() {
        return vaadinElement("PID_Sfield-10/domChild[1]");
    }

    private WebElement getElement11() {
        return vaadinElement("PID_Sfield-11/domChild[1]");
    }

    private WebElement getElement12() {
        return vaadinElement("PID_Sfield-12");
    }

    private WebElement getElement13() {
        return vaadinElement("PID_Sfield-13");
    }

    private WebElement getElement14() {
        return vaadinElement("PID_Sfield-14");
    }

    private WebElement getElement15() {
        return vaadinElement("PID_Sfield-15/domChild[1]");
    }

    private WebElement getElement16() {
        return vaadinElement("PID_Sfield-16/domChild[0]");
    }

    private WebElement getElement17() {
        return vaadinElement("PID_Sfield-17");
    }
}
