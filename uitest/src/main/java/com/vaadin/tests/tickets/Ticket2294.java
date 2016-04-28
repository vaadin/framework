package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2294 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        Label label1 = new Label();
        Label label2 = null;
        Label label3 = new Label();
        String result1 = "";
        String result2 = "";
        String result3 = "";

        layout.addComponent(label1);
        try {
            layout.setComponentAlignment(label1, Alignment.BOTTOM_LEFT);
            result1 = "OK";
        } catch (Exception e) {
            result1 = "FAILED: " + e.getMessage();
        }

        try {
            layout.setComponentAlignment(label2, Alignment.BOTTOM_LEFT);
            result2 = "FAILED, no exception";
        } catch (IllegalArgumentException e) {
            result2 = "OK";
        } catch (Exception e) {
            result2 = "FAILED: " + e.getMessage();
        }

        try {
            layout.setComponentAlignment(label3, Alignment.BOTTOM_LEFT);
            result3 = "FAILED, no exception";
        } catch (IllegalArgumentException e) {
            result3 = "OK";
        } catch (Exception e) {
            result3 = "FAILED: " + e.getMessage();
        }

        label1.setValue("Result 1: " + result1 + ", result 2: " + result2
                + ", result 3: " + result3);
    }
}
