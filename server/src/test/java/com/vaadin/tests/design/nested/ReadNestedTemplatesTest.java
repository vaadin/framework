package com.vaadin.tests.design.nested;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for reading nested templates
 *
 * @author Vaadin Ltd
 */
public class ReadNestedTemplatesTest {

    private MyDesignRoot root;

    @Before
    public void setUp() {
        root = new MyDesignRoot();
    }

    @Test
    public void rootContainsOneChild() {
        assertThat(root.getComponentCount(), is(1));
        assertThat(root.iterator().next(),
                instanceOf(MyExtendedChildDesign.class));
    }

    @Test
    public void rootContainsTwoGrandChildren() {
        assertThat(root.childDesign.getComponentCount(), is(2));
    }

    @Test
    public void childComponentIsNotNull() {
        assertThat(root.childDesign, is(not(nullValue())));
    }

    @Test
    public void childLabelIsNotNull() {
        assertThat(root.childDesign.childLabel, is(not(nullValue())));
        assertThat(root.childDesign.childLabel.getValue(), is("test content"));
    }

    @Test
    public void childCustomComponentsIsNotNull() {
        assertThat(root.childDesign.childCustomComponent, is(not(nullValue())));
        assertThat(root.childDesign.childCustomComponent.getCaption(),
                is("custom content"));
    }
}
