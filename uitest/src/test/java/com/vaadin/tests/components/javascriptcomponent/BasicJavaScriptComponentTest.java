package com.vaadin.tests.components.javascriptcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.AbstractComponentElement;
import com.vaadin.testbench.elements.AbstractJavaScriptComponentElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class BasicJavaScriptComponentTest extends MultiBrowserTest {
    @Test
    public void javascriptCommunication() throws Exception {
        openTestURL();
        int idx = 0;
        assertEquals("12. Got callback message: Callback message processed",
                getLogRow(idx++));
        assertEquals("11. Got RPC message: RPC message processed",
                getLogRow(idx++));
        assertEquals("10. Parent ids checked", getLogRow(idx++));

        // Data types in JS functions
        String expected = "1970-01-01T00:00:00.111Z";
        assertEquals(
                "9. Argument[4][aDate] type: elemental.json.impl.JreJsonString, value: "
                        + expected,
                getLogRow(idx++));
        assertEquals(
                "8. Argument[4][aBoolean] type: elemental.json.impl.JreJsonBoolean, value: false",
                getLogRow(idx++));
        assertEquals(
                "7. Argument[4][anInt] type: elemental.json.impl.JreJsonNumber, value: 556",
                getLogRow(idx++));
        assertEquals(
                "6. Argument[4][aString] type: elemental.json.impl.JreJsonString, value: value1",
                getLogRow(idx++));
        assertEquals("5. Argument[4] type: elemental.json.impl.JreJsonObject",
                getLogRow(idx++));
        assertEquals(
                "4. Argument[3] type: elemental.json.impl.JreJsonBoolean, value: true",
                getLogRow(idx++));
        assertEquals(
                "3. Argument[2] type: elemental.json.impl.JreJsonNumber, value: 556",
                getLogRow(idx++));
        assertEquals(
                "2. Argument[1] type: elemental.json.impl.JreJsonString, value: a string",
                getLogRow(idx++));

        expected = "1970-01-01T00:00:00.123Z";

        assertEquals(
                "1. Argument[0] type: elemental.json.impl.JreJsonString, value: "
                        + expected,
                getLogRow(idx++));

        // Component attributes
        AbstractJavaScriptComponentElement jsComponent = $(
                AbstractJavaScriptComponentElement.class).first();
        assertEquals("Component caption", getCaption(jsComponent));

        // app://APP/connector/[uiid]/[cid]/[key]/[filename]
        assertTrue(getChildText(jsComponent, 0)
                .matches("4. Url: .*/run/APP/connector/0/\\d+/test"));
        assertEquals("3. State message: Second state message",
                getChildText(jsComponent, 1));
        assertEquals("2. State message: First state message",
                getChildText(jsComponent, 2));

        // Can't/shouldn't check parent class name as we used to because it
        // relies on parent state change events being fired before child state
        // change events and this is not guaranteed

        // Modifications
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        assertEquals("Remove component\nDon't mess with me", button.getText());
    }

    private String getCaption(AbstractComponentElement c) {
        return c.findElement(By.xpath("../div[@class='v-caption']")).getText();
    }

    private String getChildText(AbstractComponentElement e, int index) {
        return e.findElement(By.xpath("(./div)[" + (index + 1) + "]"))
                .getText();
    }

}
