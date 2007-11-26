package com.itmill.toolkit.tests;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class TestSetVisibleAndCaching extends com.itmill.toolkit.Application {

    Panel panelA = new Panel("Panel A");
    Panel panelB = new Panel("Panel B");
    Panel panelC = new Panel("Panel C");

    Button buttonNextPanel = new Button("Show next panel");

    int selectedPanel = 0;

    public void init() {
        Window mainWindow = new Window("TestSetVisibleAndCaching");
        setMainWindow(mainWindow);

        panelA.addComponent(new Label(
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        panelB.addComponent(new Label(
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"));
        panelC.addComponent(new Label(
                "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"));

        mainWindow
                .addComponent(new Label(
                        "Inspect transfered data from server to "
                                + "client using firebug (http request / response cycles)."
                                + " See how widgets are re-used,"
                                + " after each panel is once shown in GUI then"
                                + " their contents are not resend."));
        mainWindow.addComponent(buttonNextPanel);
        mainWindow.addComponent(panelA);
        mainWindow.addComponent(panelB);
        mainWindow.addComponent(panelC);

        selectPanel(selectedPanel);

        buttonNextPanel.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                selectedPanel++;
                if (selectedPanel > 2) {
                    selectedPanel = 0;
                }
                selectPanel(selectedPanel);
            }
        });

    }

    private void selectPanel(int selectedPanel) {
        System.err.println("Selecting panel " + selectedPanel);
        switch (selectedPanel) {
        case 0:
            panelA.setVisible(true);
            panelB.setVisible(false);
            panelC.setVisible(false);
            break;
        case 1:
            panelA.setVisible(false);
            panelB.setVisible(true);
            panelC.setVisible(false);
            break;
        case 2:
            panelA.setVisible(false);
            panelB.setVisible(false);
            panelC.setVisible(true);
            break;
        }
    }
}
