package com.vaadin.tests.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class CustomComponentwithUndefinedSize extends TestBase {

    @Override
    protected String getDescription() {
        return "A custom component with no size definition should not prevent scrollbars from being shown when its contents is larger than its parent";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2459;
    }

    @Override
    protected void setup() {

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        MyCustomComponent mcc = new MyCustomComponent();
        mcc.setSizeUndefined();

        // Doesn't work
        tabs.addTab(mcc, "Doesn't work (CustomComponent)", null);

        // Works:
        tabs.addTab(mcc.buildLayout(),
                "Works (no CustomComponent, same layout)", null);

        addComponent(tabs);
        getLayout().setSizeFull();
    }

    private int step = 0;

    public class MyCustomComponent extends CustomComponent {
        public MyCustomComponent() {
            setCompositionRoot(buildLayout());
        }

        public Layout buildLayout() {
            VerticalLayout layout = new VerticalLayout();
            VerticalLayout panelLayout = new VerticalLayout();
            panelLayout.setMargin(true);
            final Panel widePanel = new Panel("too big", panelLayout);
            widePanel.setSizeUndefined();
            widePanel.setWidth("2000px");
            widePanel.setHeight("200px");
            layout.addComponent(widePanel);
            Button button = new Button("Change panel size",
                    new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            switch (step++ % 4) {
                            case 0:
                                widePanel.setWidth("200px");
                                break;
                            case 1:
                                widePanel.setHeight("2000px");
                                break;
                            case 2:
                                widePanel.setWidth("2000px");
                                break;
                            case 3:
                                widePanel.setHeight("200px");
                                break;
                            }

                        }
                    });
            panelLayout.addComponent(button);
            layout.setSizeUndefined();
            return layout;
        }
    }
}
