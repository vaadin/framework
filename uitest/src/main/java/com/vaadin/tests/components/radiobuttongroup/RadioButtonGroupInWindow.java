package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Widgetset(TestingWidgetSet.NAME)
public class RadioButtonGroupInWindow extends AbstractTestUI {
    public static final String DEFAULT_DAY_TEXT_FIELD_ID = "defaultDay";
    public static final String OPEN_WINDOW_BUTTON_ID = "openWindowButton";
    public static final String WEEK_DAYS_RADIO_BUTTON_GROUP_ID = "weekDaysRadioButtonGroup";

    @Override
    protected void setup(VaadinRequest request) {
        String[] weekdays = { "Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday" };
        final VerticalLayout layout = new VerticalLayout();
        final RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setId(WEEK_DAYS_RADIO_BUTTON_GROUP_ID);
        radioButtonGroup.setItems(weekdays);

        Window selectDayWindow = new Window("Select day window");
        selectDayWindow.setWidth("500px");
        selectDayWindow.setHeight("500px");
        VerticalLayout verticalLayout = new VerticalLayout(radioButtonGroup);
        selectDayWindow.setContent(verticalLayout);
        selectDayWindow.setModal(true);

        final TextField defaultDay = new TextField("Default day");
        defaultDay.setId(DEFAULT_DAY_TEXT_FIELD_ID);
        defaultDay.setValue(weekdays[3]);
        layout.addComponent(defaultDay);

        final UI ui = getUI();
        Button openWindowButton = new Button("Select a day", event -> {
            radioButtonGroup.setValue(defaultDay.getValue());
            ui.addWindow(selectDayWindow);
        });
        openWindowButton.setId(OPEN_WINDOW_BUTTON_ID);
        layout.addComponents(openWindowButton);

        addComponent(layout);
    }
}
