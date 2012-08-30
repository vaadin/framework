package com.vaadin.tests.components.embedded;

import java.applet.Applet;
import java.awt.Graphics;

public class TestApplet extends Applet {
    @Override
    public void paint(Graphics g) {
        g.drawString("Hello, I am an applet! Look at me!", 10, 20);
    }
}
