package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class Ticket5952 extends Application {

    @Override
    public void init() {
        final Window mainWindow = new Window("Forumtests Application");
        setMainWindow(mainWindow);
        
        String mathml =
            "<math mode='display' xmlns='http://www.w3.org/1998/Math/MathML'>"+
            "<mrow>"+
            "    <msup>"+
            "      <mi>x</mi>"+
            "      <mn>2</mn>"+
            "    </msup>"+
            "    <msup>"+
            "      <mi>c</mi>"+
            "      <mn>2</mn>"+
            "    </msup>"+
            "  </mrow>"+
            "</math>";
        Label mathLabel = new Label(mathml, Label.CONTENT_XML);
        mainWindow.addComponent(mathLabel);
    }
}
