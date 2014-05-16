package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class Ticket1435 extends LegacyApplication {

    private static final boolean useWorkaround = true;

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(
                "ButtonPanel containing a table test");
        setMainWindow(mainWin);
        ((AbstractOrderedLayout) mainWin.getContent()).setSpacing(true);

        ButtonPanel dataCardView1 = buildButtonPanel("My Tickets");
        ButtonPanel dataCardView2 = buildButtonPanel("My Tickets 2");

        mainWin.addComponent(dataCardView1);
        mainWin.addComponent(dataCardView2);

    }

    /**
     * A ButtonPanel is a Panel, which has context specific Buttons in its
     * header.
     * 
     * ButtonPanel also provides buttons for controlling its visibility
     * (collapse/expand).
     */
    public class ButtonPanel extends CustomComponent {

        VerticalLayout root = new VerticalLayout();

        // In header are the panel's title and the control buttons.
        // Panel title is expanded by default.
        HorizontalLayout header = new HorizontalLayout();

        // This is where the actual data is put.
        VerticalLayout containerLayout = new VerticalLayout();
        Panel container = new Panel(containerLayout);

        // Last known height before the panel was collapsed
        private float lastHeight = -1;
        private Unit lastHeightUnit = null;

        public ButtonPanel(String labelString) {
            setCompositionRoot(root);
            root.setSizeFull();

            root.setStyleName("toolbarpanel");
            header.setStyleName("toolbar");

            initHeader(labelString);

            containerLayout.setMargin(true);

            initContainer();
        }

        private void initHeader(String labelString) {
            root.addComponent(header);
            header.setWidth("100%");
            header.setHeight("26px");
            Label label = new Label(labelString);
            label.setStyleName("caption");
            header.addComponent(label);

            final Layout buttonContainer;
            if (useWorkaround) {
                buttonContainer = header;

            } else {
                buttonContainer = new HorizontalLayout();
                header.addComponent(buttonContainer);

            }

            Button edit = new Button("Edit");
            edit.setStyleName("link");
            buttonContainer.addComponent(edit);

            Button copy = new Button("Copy");
            copy.setStyleName("link");
            buttonContainer.addComponent(copy);

            Button move = new Button("Move");
            move.setStyleName("link");
            buttonContainer.addComponent(move);

            Button delete = new Button("Delete");
            delete.setStyleName("link");
            buttonContainer.addComponent(delete);

            Button bind = new Button("Bind");
            bind.setStyleName("link");
            buttonContainer.addComponent(bind);

            Button options = new Button("Options...");
            options.setStyleName("link");
            buttonContainer.addComponent(options);

            final Button expand = new Button("Expand");

            final Button collapse = new Button("Collapse");
            buttonContainer.addComponent(collapse);

            collapse.setStyleName("collapse");
            collapse.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (useWorkaround) {
                        container.setVisible(false);
                        lastHeight = root.getHeight();
                        lastHeightUnit = root.getHeightUnits();
                        root.setHeight("26px");
                        buttonContainer.replaceComponent(collapse, expand);
                    } else {
                        boolean visible = container.isVisible();
                        container.setVisible(!visible);
                        if (visible) {
                            lastHeight = root.getHeight();
                            lastHeightUnit = root.getHeightUnits();
                            root.setHeight("26px");
                        } else {
                            root.setHeight(lastHeight, lastHeightUnit);
                        }
                        event.getButton().setCaption(
                                visible ? "Expand" : "Collapse");
                    }
                }
            });

            if (useWorkaround) {
                expand.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        container.setVisible(true);
                        root.setHeight(lastHeight, lastHeightUnit);
                        buttonContainer.replaceComponent(expand, collapse);
                    }
                });
            }

        }

        private void initContainer() {
            container.setStyleName("custompanel");
            container.setSizeFull();
            containerLayout.setMargin(false);
            containerLayout.setSizeFull();
            root.addComponent(container);
            root.setExpandRatio(container, 1);
        }

        public void setHeight(int height, Unit unit) {
            root.setHeight(height, unit);
            lastHeight = height;
            lastHeightUnit = unit;
            container.setHeight("100%");
        }

        @Override
        public void setHeight(String height) {
            root.setHeight(height);
            lastHeight = root.getHeight();
            lastHeightUnit = root.getHeightUnits();
            container.setHeight("100%");
        }

        @Override
        public void setWidth(String width) {
            root.setWidth(width);
        }

        public void setWidth(int width, Unit unit) {
            root.setWidth(width, unit);
        }

        @Override
        public void setSizeFull() {
            setWidth("100%");
            setHeight("100%");
        }

        public void setPanelComponent(Component component) {
            containerLayout.removeAllComponents();
            containerLayout.addComponent(component);
        }
    }

    public ButtonPanel buildButtonPanel(String caption) {
        ButtonPanel panel = new ButtonPanel(caption);

        panel.setHeight("250px");
        panel.setWidth("500px");

        Table table = new Table();

        table.setSizeFull();

        table.addContainerProperty("checkbox", CheckBox.class, new CheckBox());
        table.setColumnWidth("checkbox", 30);
        table.setColumnHeader("checkbox", "");

        table.addContainerProperty("Tickets", String.class, null);
        table.setColumnWidth("Tickets", 150);

        table.addContainerProperty("Deadline", String.class, null);

        for (int i = 0; i < 10; i++) {
            String name = "Name " + i;
            table.addItem(new Object[] { new CheckBox(), name,
                    "02-22-2007 13:37" }, new Integer(i));
        }

        panel.setPanelComponent(table);

        return panel;
    }
}
