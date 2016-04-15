package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class VerticalLayoutWithEmptyLabel extends AbstractTestUI {

    private static final float UPPER_BAR_HEIGHT = 42;

    private final VerticalLayout rootLayout = new VerticalLayout();
    private final Label subtitleLabel = new Label();
    private final String rootTitle;

    private Component lowerPanel;

    public VerticalLayoutWithEmptyLabel() {
        rootTitle = "Vaadin Layout Bug";
        getPage().setTitle(rootTitle);
    }

    @Override
    public void setup(VaadinRequest request) {
        buildRootLayout();
        setContent(rootLayout);
    }

    @Override
    public void attach() {
        super.attach();
        rebuildLowerPanel();
    }

    protected void rebuildLowerPanel() {
        updateLowerPanel(null);
    }

    private void updateLowerPanel(Object user) {

        // Remove previous content
        if (lowerPanel != null) {
            rootLayout.removeComponent(lowerPanel);
        }

        // If not logged in, present login form, otherwise check user's rights
        // and build lower panel
        lowerPanel = new MyPanel();

        // Update layout
        rootLayout.addComponent(lowerPanel);
        rootLayout.setExpandRatio(lowerPanel, 1.0f);
        rootLayout.setComponentAlignment(lowerPanel, Alignment.MIDDLE_CENTER);
    }

    protected Component getRootLowerPanel() {
        return lowerPanel;
    }

    protected void buildRootLayout() {
        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();
        rootLayout.setMargin(new MarginInfo(false, true, true, true));
        rootLayout.addComponent(buildRootUpperBar());
        rootLayout.addComponent(buildRootSeparator());
        rebuildLowerPanel();
    }

    protected Component buildRootUpperBar() {

        // Title
        Label titleLabel = new Label(rootTitle);
        titleLabel.addStyleName("pexp-application-title");
        titleLabel.setSizeUndefined();
        titleLabel.setHeight(18, Sizeable.Unit.PIXELS);
        subtitleLabel.setSizeUndefined();
        VerticalLayout titleLayout = new VerticalLayout();
        titleLayout.setSizeUndefined();
        titleLayout.addComponent(titleLabel);
        titleLayout.setComponentAlignment(titleLabel, Alignment.BOTTOM_CENTER);
        titleLayout.addComponent(subtitleLabel);
        titleLayout.setComponentAlignment(subtitleLabel,
                Alignment.BOTTOM_CENTER);

        // Sequence parts
        HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("pexp-main-upper-bar");
        layout.setSpacing(true);
        layout.setWidth("100%");
        layout.setHeight(UPPER_BAR_HEIGHT, Sizeable.Unit.PIXELS);
        layout.addComponent(titleLayout);
        layout.setExpandRatio(titleLayout, 1.0f);
        layout.setComponentAlignment(titleLayout, Alignment.BOTTOM_CENTER);
        return layout;
    }

    protected Component buildRootSeparator() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Panel panel = new Panel(layout);
        panel.addStyleName("pexp-separator");
        panel.setWidth("100%");
        panel.setHeight(3.0f, Sizeable.Unit.PIXELS);
        return panel;
    }

    class MyPanel extends VerticalLayout {

        private final Table table;
        private final Label myLabel = new Label("");
        private final TextField filterPhoneField = new TextField(
                "Foobar Number");
        private final TextField filterFoobarField = new TextField("Foobar ID");
        private final CheckBox incomingOnlyField = new CheckBox(
                "Incoming foobar only");

        public MyPanel() {

            // Setup layout
            this.setMargin(true);
            setSpacing(true);
            this.setHeight("100%");

            // Setup top layout with controls and fields
            HorizontalLayout topLayout = new HorizontalLayout();
            topLayout.setSpacing(true);

            // Foobar Content
            final TextArea smsContent = new TextArea("Foobar Content");
            topLayout.addComponent(smsContent);
            topLayout.setExpandRatio(smsContent, 1);
            smsContent.setRows(3);
            smsContent.setColumns(40);
            // topLayout.setWidth("100%");
            this.addComponent(topLayout);

            // Foobar phone #
            final TextField smsNumber = new TextField("Foobar Phone #");
            smsNumber.setSizeUndefined();
            smsNumber.setColumns(12);
            smsNumber.setMaxLength(16);
            // smsNumber.setStyleName("pexp-fixed-width");

            // Phone number and button layout
            VerticalLayout buttonNumberLayout = new VerticalLayout();
            buttonNumberLayout.setSizeUndefined();
            buttonNumberLayout.setHeight("100%");
            buttonNumberLayout.addComponent(smsNumber);
            buttonNumberLayout.addComponent(myLabel);
            Button button = new Button("Receive Foobar");
            buttonNumberLayout.addComponent(button);
            buttonNumberLayout.setExpandRatio(button, 1);
            buttonNumberLayout.setComponentAlignment(button,
                    Alignment.BOTTOM_LEFT);
            topLayout.addComponent(buttonNumberLayout);

            // Add message table
            table = new Table();
            table.setWidth("100%");
            table.setHeight("100%");
            this.addComponent(table);
            setExpandRatio(table, 1);

            // Message table controls
            VerticalLayout tableControlsLayout = new VerticalLayout();
            tableControlsLayout.setSizeUndefined();
            tableControlsLayout.setSpacing(true);

            // Configure filter for phone #
            filterPhoneField.setSizeUndefined();
            filterPhoneField.setImmediate(true);
            filterPhoneField.setColumns(12);
            filterPhoneField.setMaxLength(16);
            // this.filterPhoneField.setStyleName("pexp-fixed-width");

            // Configure filter for foobar ID
            filterFoobarField.setSizeUndefined();
            filterFoobarField.setImmediate(true);
            filterFoobarField.setColumns(16);
            filterFoobarField.setMaxLength(16);
            // this.filterFoobarField.setStyleName("pexp-fixed-width");

            // Configure incoming checkbox
            incomingOnlyField.setImmediate(true);

            // Add filter inputs for phone # and foobar ID
            tableControlsLayout.addComponent(filterPhoneField);
            tableControlsLayout.addComponent(filterFoobarField);
            topLayout.addComponent(tableControlsLayout);
            topLayout.addComponent(incomingOnlyField);
            topLayout.setComponentAlignment(incomingOnlyField,
                    Alignment.BOTTOM_LEFT);
        }
    }

    @Override
    protected String getTestDescription() {
        return "foobar";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10098;
    }
}
