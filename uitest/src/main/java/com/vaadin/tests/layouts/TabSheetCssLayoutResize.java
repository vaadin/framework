package com.vaadin.tests.layouts;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TabSheetCssLayoutResize extends AbstractTestUI {
    private CssLayout mainLayout;

    @Override
    protected void setup(VaadinRequest request) {
        getPage().getStyles().add(".v-csslayout {width: 100%;}");
        getLayout().setSpacing(true);
        setSizeFull();

        addComponent(new Button("Add TabSheet", clickEvent -> {
            mainLayout = new CssLayout();
            addComponent(mainLayout);
            mainLayout.addComponent(getTabSheet());
        }));
    }

    public TabSheet getTabSheet() {
        TabSheet tabSheet = new TabSheet();
        for (int i = 0; i < 20; ++i) {
            tabSheet.addTab(createTabContent(), "Tab " + i);
        }
        return tabSheet;
    }

    private HorizontalLayout createTabContent() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.addComponent(new Button("Click Me", clickEvent -> {
            Notification.show("You clicked me!");
        }));
        Button button2 = new Button("Button 2");
        layout.addComponent(button2);
        layout.setComponentAlignment(button2, Alignment.MIDDLE_RIGHT);
        return layout;
    }

    @Override
    protected String getTestDescription() {
        return "1. Start with browser window half width. 2. Add Tabsheet. "
                + "3. Resize window to be wider. 4. TabSheet contents should resize without clicking 'Click me'";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11124;
    }
}
