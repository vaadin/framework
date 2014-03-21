/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.navigator;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class NavigatorTest extends UI {

    private Log log = new Log(5);
    private Layout naviLayout = new VerticalLayout();
    private TextField params = new TextField("Parameters");

    private Navigator navi;

    class ListView extends Table implements View {

        public ListView() {
            addContainerProperty("name", String.class, "");
            addContainerProperty("value", String.class, "");
        }

        @Override
        public void enter(ViewChangeEvent event) {
            String params = event.getParameters();
            log.log("Navigated to ListView "
                    + (params.isEmpty() ? "without params" : "with params "
                            + params));
            removeAllItems();
            for (String arg : params.split(",")) {
                addItem(arg.split("=|$", 2), arg);
            }
        }
    }

    class EditView extends RichTextArea implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            String params = event.getParameters();
            log.log("Navigated to EditView "
                    + (params.isEmpty() ? "without params" : "with params "
                            + params));
            setValue("Displaying edit view with parameters " + params);
        }
    }

    class SpecialCharsView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            log.log("Navigated to SpecialCharsView: " + event.getViewName()
                    + "; fragment: " + getPage().getUriFragment());
            setValue(event.getViewName());
        }

    }

    class DefaultView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            String params = event.getParameters();
            log.log("Navigated to DefaultView "
                    + (params.isEmpty() ? "without params" : "with params "
                            + params));
            setValue("Default view: " + event.getParameters());
        }
    }

    class ForbiddenView implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            log.log("Navigated to ForbiddenView - this should not happen");
        }
    }

    class ErrorView extends Label implements View {
        @Override
        public void enter(ViewChangeEvent event) {
            log.log("View '" + event.getViewName() + "' not found!");
            setValue("Tried to navigate to " + event.getViewName()
                    + " but such a view could not be found :(");
        }
    }

    class NaviListener implements ViewChangeListener {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            if (event.getNewView() instanceof ForbiddenView) {
                log.log("Prevent navigation to ForbiddenView");
                return false;
            }
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
        }
    }

    class NaviButton extends Button {
        public NaviButton(final String path) {
            super("Navigate to " + path, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    navi.navigateTo(path + "/" + params.getValue());
                }
            });
        }
    }

    @Override
    protected void init(VaadinRequest req) {
        try {
            VerticalLayout layout = new VerticalLayout();
            layout.setMargin(true);
            setContent(layout);

            navi = new Navigator(this, naviLayout);

            navi.addView("", new DefaultView());

            navi.addView("list", new ListView());
            navi.addView("edit", new EditView());
            navi.addView("öääö !%&/()=", new SpecialCharsView());
            navi.addView("forbidden", new ForbiddenView());

            navi.addViewChangeListener(new NaviListener());

            navi.setErrorView(new ErrorView());

            layout.addComponent(new NaviButton("list"));
            layout.addComponent(new NaviButton("edit"));
            layout.addComponent(new NaviButton("forbidden"));
            layout.addComponent(new NaviButton("öääö !%&/()="));

            layout.addComponent(params);
            layout.addComponent(log);
            layout.addComponent(naviLayout);
        } catch (Exception e) {
            e.printStackTrace();
            log.log("Exception: " + e);
        }
    }
}
