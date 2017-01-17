package com.vaadin.tests.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractSingleSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

@Widgetset(TestingWidgetSet.NAME)
public class DummyData extends AbstractTestUIWithLog {

    /**
     * DataProvider that keeps track on how often the data is requested.
     */
    private class LoggingDataProvider extends ListDataProvider<String> {
        private int count = 0;

        private LoggingDataProvider(Collection<String> collection) {
            super(collection);
        }

        @Override
        public Stream<String> fetch(Query query) {
            log("Backend request #" + count++);
            return super.fetch(query);
        }
    }

    /**
     * Simplified server only selection model. Selection state passed in data,
     * shown as bold text.
     */
    public static class DummyComponent extends AbstractSingleSelect<String>
            implements HasDataProvider<String> {

        private String selected;

        private DummyComponent() {
            addDataGenerator((str, json) -> {
                json.put(DataCommunicatorConstants.DATA, str);
                if (isSelected(str)) {
                    json.put(DataCommunicatorConstants.SELECTED, true);
                }
            });
        }

        @Override
        public Optional<String> getSelectedItem() {
            return Optional.ofNullable(selected);
        }

        @Override
        public void setValue(String item) {
            if (selected != null) {
                getDataCommunicator().refresh(selected);
            }
            selected = item;
            if (selected != null) {
                getDataCommunicator().refresh(selected);
            }
        }

        @Override
        public DataProvider<String, ?> getDataProvider() {
            return internalGetDataProvider();
        }

        @Override
        public void setDataProvider(DataProvider<String, ?> dataProvider) {
            internalSetDataProvider(dataProvider);
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        DummyComponent dummy = new DummyComponent();
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 300; ++i) {
            items.add("Foo " + i);
        }
        dummy.setDataProvider(new LoggingDataProvider(items));
        dummy.setValue("Foo 200");

        HorizontalLayout controls = new HorizontalLayout();
        addComponent(controls);
        controls.addComponent(new Button("Select Foo 20", e -> {
            dummy.setValue("Foo " + 20);
        }));
        controls.addComponent(new Button("Reset data provider", e -> {
            dummy.setDataProvider(new LoggingDataProvider(items));
        }));
        controls.addComponent(new Button("Remove all data", e -> {
            dummy.setDataProvider(
                    new LoggingDataProvider(Collections.emptyList()));
        }));
        addComponent(dummy);
    }
}
