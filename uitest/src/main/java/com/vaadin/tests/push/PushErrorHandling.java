package com.vaadin.tests.push;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.AbstractInMemoryContainer;
import com.vaadin.v7.data.util.BeanContainer;
import com.vaadin.v7.ui.Table;

public class PushErrorHandling extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getPushConfiguration().setPushMode(PushMode.AUTOMATIC);

        VaadinSession.getCurrent().setErrorHandler(event -> {
            addComponent(new Label(
                    "An error! " + event.getThrowable().getMessage()));
            System.err
                    .println("An error! " + event.getThrowable().getMessage());
        });

        final Button button = new Button("Click for NPE!", event -> {
            ((String) null).length(); // Null-pointer exception
        });
        button.setId("npeButton");
        addComponent(button);

        final Table view = new Table("testtable");
        view.setId("testtable");
        view.setSelectable(true);
        view.setMultiSelect(false);
        view.setImmediate(true);
        view.setSizeFull();

        view.addItemClickListener(event -> {
            BeanContainer<String, AbstractInMemoryContainer<?, ?, ?>> metaContainer = new BeanContainer<String, AbstractInMemoryContainer<?, ?, ?>>(
                    AbstractInMemoryContainer.class) {
                @Override
                public Collection<String> getContainerPropertyIds() {
                    List<String> cpropIds = new ArrayList<>(
                            super.getContainerPropertyIds());
                    cpropIds.add("testid");
                    return cpropIds;
                }

                @Override
                public Class<?> getType(Object propertyId) {
                    ((Object) null).hashCode();
                    return super.getType(propertyId);
                }
            };
            view.setContainerDataSource(metaContainer);
        });
        view.addContainerProperty("Column", String.class, "Click for NPE");
        view.addItem(new Object());

        addComponent(view);
    }

    @Override
    protected String getTestDescription() {
        return "Error handling should still work w/ push enabled. (Button can be handled properly, table causes internal error)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11882;
    }
}
