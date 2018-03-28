package com.vaadin.tests.components.window;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class BackspaceKeyWithModalOpened extends AbstractTestUI {

    private static final String DEFAULT_VIEW_ID = "";
    private static final String SECOND_VIEW_ID = "second";

    public static final String BTN_NEXT_ID = "btn_next";
    public static final String BTN_OPEN_MODAL_ID = "btn_open_modal";
    public static final String TEXT_FIELD_IN_MODAL = "txt_in_modal";
    public static final String MODAL_ID = "modal_window";

    private Navigator navigator;

    class DefaultView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            Button btnNext = new Button("Next",
                    clickEvent -> navigator.navigateTo(SECOND_VIEW_ID));

            btnNext.setId(BTN_NEXT_ID);
            addComponent(btnNext);
        }
    }

    class SecondView extends Label implements View {

        @Override
        public void enter(ViewChangeEvent event) {
            Button btnOpenModal = new Button("Open modal",
                    clickEvent -> {
                        Window window = new Window("Caption");
                        window.setId(MODAL_ID);

                        VerticalLayout layout = new VerticalLayout();
                        layout.setWidth("300px");
                        layout.setHeight("300px");

                        TextField textField = new TextField();
                        textField.setId(TEXT_FIELD_IN_MODAL);

                        layout.addComponent(textField);
                        window.setContent(layout);

                        addWindow(window);

                        window.setModal(true);

                        setFocusedComponent(window);
                    });

            btnOpenModal.setId(BTN_OPEN_MODAL_ID);
            addComponent(btnOpenModal);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Layout navigatorLayout = new VerticalLayout();
        navigator = new Navigator(this, navigatorLayout);

        addComponent(navigatorLayout);

        navigator.addView(DEFAULT_VIEW_ID, new DefaultView());

        navigator.addView(SECOND_VIEW_ID, new SecondView());
    }

    @Override
    protected String getTestDescription() {
        return "Navigator should not go back with modal opened.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13180;
    }
}
