package com.vaadin.tests.components.abstractsingleselect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.elements.AbstractSingleSelectElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import com.vaadin.tests.tb3.SingleBrowserTest;

@RunWith(ParameterizedTB3Runner.class)
public class AbstractSingleSelectionTest extends SingleBrowserTest {

    private static final Map<String, Class<? extends AbstractSingleSelectElement>> elementClasses = new LinkedHashMap<>();

    @Parameters
    public static Collection<String> getElementClassNames() {
        if (elementClasses.isEmpty()) {
            elementClasses.put("RadioButtonGroup",
                    RadioButtonGroupElement.class);
            elementClasses.put("NativeSelect", NativeSelectElement.class);
            elementClasses.put("ComboBox", ComboBoxElement.class);
        }

        return elementClasses.keySet();
    }

    private String elementClassName;

    public void setElementClassName(String elementClassName) {
        this.elementClassName = elementClassName;
    }

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testSelectNull() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);

        assertInitial();

        $(ButtonElement.class).caption("Deselect").first().click();

        AbstractSingleSelectElement selectElement = getSelectElement();
        // TODO: TB API behavior should be unified.
        if (selectElement instanceof RadioButtonGroupElement) {
            assertNull("No value should be selected", selectElement.getValue());
        } else if (selectElement instanceof ComboBoxElement) {
            assertTrue("No value should be selected",
                    selectElement.getValue().isEmpty());
        } else {
            // NativeSelectElement throws if no value is selected.
            try {
                selectElement.getValue();
                fail("No value should be selected");
            } catch (NoSuchElementException e) {
                // All is fine.
            }
        }
    }

    @Test
    public void testSelectOnClientAndRefresh() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);

        assertInitial();

        AbstractSingleSelectElement select = getSelectElement();
        select.selectByText("Baz");
        assertEquals("Value should change", "Baz", select.getValue());

        $(ButtonElement.class).caption("Refresh").first().click();
        assertEquals("Value should stay the same through refreshAll", "Baz",
                select.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServer() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);

        assertInitial();

        AbstractSingleSelectElement select = getSelectElement();
        select.selectByText("Baz");
        assertEquals("Value should change", "Baz", select.getValue());

        $(ButtonElement.class).caption("Select Bar").first().click();
        assertEquals("Original value should be selected again", "Bar",
                select.getValue());
    }

    @Test
    public void testSelectOnClientAndResetValueOnServerInListener() {
        $(NativeSelectElement.class).first().selectByText(elementClassName);

        assertInitial();

        AbstractSingleSelectElement rbg = getSelectElement();
        rbg.selectByText("Reset");
        // Selecting "Reset" selects "Bar" on server. Value was initially "Bar"
        assertEquals("Original value should be selected again", "Bar",
                rbg.getValue());
    }

    private void assertInitial() {
        assertEquals("Initial state unexpected", "Bar",
                getSelectElement().getValue());
    }

    private AbstractSingleSelectElement getSelectElement() {
        return $(elementClasses.get(elementClassName)).id("testComponent");
    }
}
