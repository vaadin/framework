package com.vaadin.tests.components.listselect;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ListSelectStyleNamesTest extends SingleBrowserTest {

    private NativeSelectElement nativeSelect;
    private TestBenchElement nativeSelectSelect;
    private ListSelectElement listSelect;
    private TestBenchElement listSelectSelect;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        nativeSelect = $(NativeSelectElement.class).first();
        nativeSelectSelect = (TestBenchElement) nativeSelect
                .findElement(By.xpath("select"));

        listSelect = $(ListSelectElement.class).first();
        listSelectSelect = (TestBenchElement) listSelect
                .findElement(By.xpath("select"));

    }

    @Test
    public void correctInitialStyleNames() {
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void addStyleName() {
        $(ButtonElement.class).id("add").click();
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial",
                "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void changePrimaryStyleName() {
        $(ButtonElement.class).id("add").click();
        $(ButtonElement.class).id("changeprimary").click();
        assertStyleNames(nativeSelect, "newprimary", "v-widget",
                "custominitial", "newprimary-custominitial", "new",
                "newprimary-new");
        assertStyleNames(nativeSelectSelect, "newprimary-select");
        assertStyleNames(listSelect, "newprimary", "v-widget", "custominitial",
                "newprimary-custominitial", "new", "newprimary-new");
        assertStyleNames(listSelectSelect, "newprimary-select");

    }

    private void assertStyleNames(TestBenchElement element,
            String... styleNames) {
        assertEquals(new HashSet<>(Arrays.asList(styleNames)),
                element.getClassNames());
    }
}
