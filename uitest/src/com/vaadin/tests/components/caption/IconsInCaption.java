package com.vaadin.tests.components.caption;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class IconsInCaption extends TestBase {

    private static final String TYPE_EMBEDDED = "Embedded";
    private static final String TYPE_CAPTION = "In caption";

    private static final String[] icons = new String[] { "arrow-down.png",
            "arrow-left.png", "arrow-right.png", "arrow-up.png",
            "attention.png", "calendar.png", "cancel.png", "document.png",
            "document-add.png", "document-delete.png", "document-doc.png",
            "document-image.png", "document-pdf.png", "document-ppt.png",
            "document-txt.png", "document-web.png", "document-xsl.png",
            "email.png", "email-reply.png", "email-send.png", "folder.png",
            "folder-add.png", "folder-delete.png", "globe.png", "help.png",
            "lock.png", "note.png", "ok.png", "reload.png", "settings.png",
            "trash.png", "trash-full.png", "user.png", "users.png" };

    private static final String[] sizes = new String[] { "16", "32", "64" };

    private ComponentContainer container = new VerticalLayout();

    private Log log = new Log(5);

    private ComboBox containerSelect;

    private ComboBox iconTypeSelect;

    @Override
    protected void setup() {
        iconTypeSelect = new ComboBox("Icon container");
        iconTypeSelect.addItem(TYPE_EMBEDDED);
        iconTypeSelect.addItem(TYPE_CAPTION);
        iconTypeSelect.setImmediate(true);
        iconTypeSelect.setNullSelectionAllowed(false);
        iconTypeSelect.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                updateContainer();
            }
        });

        containerSelect = new ComboBox("Container");
        for (Class<? extends ComponentContainer> cc : VaadinClasses
                .getComponentContainersSupportingUnlimitedNumberOfComponents()) {
            containerSelect.addItem(cc);
        }
        containerSelect.setImmediate(true);
        containerSelect.addListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                updateContainer();

            }
        });

        addComponent(log);
        addComponent(iconTypeSelect);
        addComponent(containerSelect);
        addComponent(container);

        iconTypeSelect.setValue(TYPE_CAPTION);
        containerSelect.setValue(VerticalLayout.class);
    }

    protected void updateContainer() {
        Class<? extends ComponentContainer> containerClass = (Class<? extends ComponentContainer>) containerSelect
                .getValue();
        if (containerClass == null) {
            return;
        }

        Object iconType = iconTypeSelect.getValue();
        try {
            ComponentContainer newContainer = createContainer(containerClass,
                    iconType);
            replaceComponent(container, newContainer);
            container = newContainer;
            log.log("Container changed to " + containerClass.getName() + "/"
                    + iconType);
        } catch (Exception e) {
            log.log("Create container failed for " + containerClass.getName()
                    + ": " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static ComponentContainer createContainer(
            Class<? extends ComponentContainer> containerClass, Object iconType)
            throws InstantiationException, IllegalAccessException {
        ComponentContainer container = containerClass.newInstance();
        for (String size : sizes) {
            Label title = new Label("<h3>" + size + "x" + size + "</h3>",
                    ContentMode.HTML);
            container.addComponent(title);
            for (String icon : icons) {
                ThemeResource res = new ThemeResource("../runo/icons/" + size
                        + "/" + icon);
                if (TYPE_CAPTION.equals(iconType)) {
                    Label name = new Label();
                    name.setCaption(icon);
                    name.setIcon(res);
                    container.addComponent(name);
                } else if (TYPE_EMBEDDED.equals(iconType)) {
                    Embedded e = new Embedded(icon, res);
                    container.addComponent(e);
                }
            }
        }

        return container;
    }

    @Override
    protected String getDescription() {
        return "Test for comparing rendering speed of icons in a caption and in an Embedded component in different component containers.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6578;
    }

}
