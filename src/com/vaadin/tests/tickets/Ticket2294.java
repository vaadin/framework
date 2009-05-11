package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.AlignmentHandler;

public class Ticket2294 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        Label label1 = new Label();
        Label label2 = null;
        Label label3 = new Label();
        String result1 = "";
        String result2 = "";
        String result3 = "";

        layout.addComponent(label1);
        try {
            layout.setComponentAlignment(label1,
                    AlignmentHandler.ALIGNMENT_LEFT,
                    AlignmentHandler.ALIGNMENT_BOTTOM);
            result1 = "OK";
        } catch (Exception e) {
            result1 = "FAILED: " + e.getMessage();
        }

        try {
            layout.setComponentAlignment(label2,
                    AlignmentHandler.ALIGNMENT_LEFT,
                    AlignmentHandler.ALIGNMENT_BOTTOM);
            result2 = "FAILED, no exception";
        } catch (IllegalArgumentException e) {
            result2 = "OK";
        } catch (Exception e) {
            result2 = "FAILED: " + e.getMessage();
        }

        try {
            layout.setComponentAlignment(label3,
                    AlignmentHandler.ALIGNMENT_LEFT,
                    AlignmentHandler.ALIGNMENT_BOTTOM);
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
