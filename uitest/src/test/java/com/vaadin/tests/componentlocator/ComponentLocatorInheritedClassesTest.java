package com.vaadin.tests.componentlocator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ComponentLocatorInheritedClassesTest extends SingleBrowserTest {

    @ServerClass("com.vaadin.tests.componentlocator.ComponentLocatorInheritedClasses.DefaultLabel")
    public static class DefaultLabelElement extends LabelElement {
    }

    @ServerClass("com.vaadin.tests.componentlocator.ComponentLocatorInheritedClasses.MyCustomLabel")
    public static class MyCustomLabelElement extends DefaultLabelElement {
    }

    @Test
    public void label_finds_all_three() {
        openTestURL();
        assertEquals(3, $(LabelElement.class).all().size());
    }

    @Test
    public void defaultlabel_finds_two() {
        openTestURL();
        assertEquals(2, $(DefaultLabelElement.class).all().size());
    }

    @Test
    public void mycustomlabel_finds_one() {
        openTestURL();
        assertEquals(1, $(MyCustomLabelElement.class).all().size());
    }
}
