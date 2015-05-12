package com.vaadin.tests.server.component.gridlayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class DefaultAlignmentTest {

    private GridLayout gridLayout;

    @Before
    public void setup() {
        gridLayout = new GridLayout(2, 2);
    }

    @Test
    public void testDefaultAlignment() {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        gridLayout.addComponent(label);
        gridLayout.addComponent(tf);
        Assert.assertEquals(Alignment.TOP_LEFT,
                gridLayout.getComponentAlignment(label));
        Assert.assertEquals(Alignment.TOP_LEFT,
                gridLayout.getComponentAlignment(tf));
    }

    @Test
    public void testAlteredDefaultAlignment() {
        Label label = new Label("A label");
        TextField tf = new TextField("A TextField");
        gridLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        gridLayout.addComponent(label);
        gridLayout.addComponent(tf);
        Assert.assertEquals(Alignment.MIDDLE_CENTER,
                gridLayout.getComponentAlignment(label));
        Assert.assertEquals(Alignment.MIDDLE_CENTER,
                gridLayout.getComponentAlignment(tf));
    }
}
