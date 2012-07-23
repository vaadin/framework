package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;

public class PopupViewNullValues extends TestBase {

    private PopupView pv[] = new PopupView[4];
    private Button b[] = new Button[4];

    @Override
    protected void setup() {
        try {
            pv[0] = new PopupView("Popupview 1 - no component", null);
            addComponent(pv[0]);
            b[0] = new Button("Open popupview 1", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    pv[0].setPopupVisible(true);
                }

            });
        } catch (Exception e) {
            getMainWindow()
                    .showNotification(
                            "Error, 'null content' should not throw an exception at this point",
                            Notification.TYPE_ERROR_MESSAGE);
        }

        try {
            pv[1] = new PopupView(null, new TextField(
                    "Empty html, contains component"));
            addComponent(pv[1]);
            b[1] = new Button("Open popupview 2", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    pv[1].setPopupVisible(true);
                }

            });
        } catch (Exception e) {
            getMainWindow()
                    .showNotification(
                            "Error, 'null html', should not throw an exception at this point",
                            Notification.TYPE_ERROR_MESSAGE);
        }

        try {
            pv[2] = new PopupView(null, null);
            addComponent(pv[2]);
            b[2] = new Button("Open popupview 3", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    pv[2].setPopupVisible(true);
                }

            });
        } catch (Exception e) {
            getMainWindow()
                    .showNotification(
                            "Error, 'null html, null content', should not throw an exception at this point",
                            Notification.TYPE_ERROR_MESSAGE);
        }
        try {
            pv[3] = new PopupView("Popupview 4 - has component", new TextField(
                    "This is the content of popupview 4"));
            addComponent(pv[3]);
            b[3] = new Button("Open popupview 4", new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    pv[3].setPopupVisible(true);
                }

            });
        } catch (Exception e) {
            getMainWindow()
                    .showNotification(
                            "Error, 'null html, null content', should not throw an exception at this point",
                            Notification.TYPE_ERROR_MESSAGE);
        }

        addComponent(b[0]);
        addComponent(b[1]);
        addComponent(b[2]);
        addComponent(b[3]);
    }

    @Override
    protected String getDescription() {
        return "This test case contains 3 popupviews. Only the second and the forth popup views have non-null components and can be opened. 1 and 3 will produce an exception if you try to open them.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3248;
    }

}
