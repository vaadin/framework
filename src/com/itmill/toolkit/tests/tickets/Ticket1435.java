package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Window;

public class Ticket1435 extends Application {

    public void init() {

        final Window mainWin = new Window("ButtonPanel containing a table test");
        setMainWindow(mainWin);
        ((OrderedLayout) mainWin.getLayout()).setSpacing(true);

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

        ExpandLayout root = new ExpandLayout();

        // In header are the panel's title and the control buttons.
        // Panel title is expanded by default.
        ExpandLayout header = new ExpandLayout(
                ExpandLayout.ORIENTATION_HORIZONTAL);

        // This is where the actual data is put.
        Panel container = new Panel();

        // Last known height before the panel was collapsed
        private int lastHeight = -1;
        private int lastHeightUnit = -1;

        public ButtonPanel(String labelString) {
            setCompositionRoot(root);
            root.setSizeFull();

            root.setStyleName("toolbarpanel");
            header.setStyleName("toolbar");

            initHeader(labelString);

            initContainer();
        }

        private void initHeader(String labelString) {
            root.addComponent(header);
            header.setWidth("100%");
            header.setHeight("26px");
            Label label = new Label(labelString);
            label.setStyleName("caption");
            header.addComponent(label);

            OrderedLayout buttonContainer = new OrderedLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            header.addComponent(buttonContainer);

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

            Button collapse = new Button("Collapse");
            collapse.setStyleName("collapse");
            collapse.addListener(new Button.ClickListener() {
                public void buttonClick(Button.ClickEvent event) {
                    boolean visible = container.isVisible();
                    container.setVisible(!visible);
                    header.setHeight(!visible ? "26px" : "25px");
                    if (visible) {
                        lastHeight = root.getHeight();
                        lastHeightUnit = root.getHeightUnits();
                        root.setHeight("25px");
                    } else {
                        root.setHeight(lastHeight, lastHeightUnit);
                    }
                    event.getButton().setCaption(
                            visible ? "Expand" : "Collapse");
                }
            });

            buttonContainer.addComponent(collapse);
        }

        private void initContainer() {
            container.setStyleName("custompanel");
            container.setSizeFull();
            container.getLayout().setMargin(false);
            container.getLayout().setSizeFull();
            root.addComponent(container);
            root.expand(container);
        }

        public void setHeight(int height, int unit) {
            root.setHeight(height, unit);
            lastHeight = height;
            lastHeightUnit = unit;
            container.setHeight("100%");
        }

        public void setHeight(String height) {
            root.setHeight(height);
            lastHeight = root.getHeight();
            lastHeightUnit = root.getHeightUnits();
            container.setHeight("100%");
        }

        public void setWidth(String width) {
            root.setWidth(width);
        }

        public void setWidth(int width, int unit) {
            root.setWidth(width, unit);
        }

        public void setSizeFull() {
            setWidth("100%");
            setHeight("100%");
        }

        public void setPanelComponent(Component component) {
            container.removeAllComponents();
            container.addComponent(component);
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