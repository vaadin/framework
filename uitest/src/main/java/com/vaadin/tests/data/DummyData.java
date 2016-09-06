package com.vaadin.tests.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.DataCommunicator;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.server.data.Query;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.data.selection.SelectionModel;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

@Widgetset(TestingWidgetSet.NAME)
public class DummyData extends AbstractTestUIWithLog {

    /**
     * DataSource that keeps track on how often the data is requested.
     */
    private class LoggingDataSource extends ListDataSource<String> {
        private int count = 0;

        private LoggingDataSource(Collection<String> collection) {
            super(collection);
        }

        @Override
        public Stream<String> apply(Query query) {
            log("Backend request #" + (count++));
            return super.apply(query);
        }
    }

    /**
     * Simplified server only selection model. Selection state passed in data,
     * shown as bold text.
     */
    private static class DummySelectionModel implements SelectionModel<String> {
        private String selected;
        private DataCommunicator<String> communicator;

        @Override
        public Set<String> getSelectedItems() {
            if (selected != null) {
                return Collections.singleton(selected);
            }
            return Collections.emptySet();
        }

        @Override
        public void select(String item) {
            if (selected != null) {
                communicator.refresh(selected);
            }
            selected = item;
            if (selected != null) {
                communicator.refresh(selected);
            }
        }

        @Override
        public void deselect(String item) {
            if (item == selected) {
                select(null);
            }
        }

        private void setCommunicator(DataCommunicator<String> dataComm) {
            communicator = dataComm;
        }
    }

    public static class DummyComponent
            extends AbstractListing<String, DummySelectionModel> {

        private DummyComponent() {
            setSelectionModel(new DummySelectionModel());
            addDataGenerator((str, json) -> {
                json.put(DataCommunicatorConstants.DATA, str);
                if (isSelected(str)) {
                    json.put(DataCommunicatorConstants.SELECTED, true);
                }
            });
            getSelectionModel().setCommunicator(getDataCommunicator());
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        DummyComponent dummy = new DummyComponent();
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 300; ++i) {
            items.add("Foo " + i);
        }
        dummy.setDataSource(new LoggingDataSource(items));
        dummy.select("Foo 200");

        HorizontalLayout controls = new HorizontalLayout();
        addComponent(controls);
        controls.addComponent(new Button("Select Foo 20", e -> {
            dummy.select("Foo " + 20);
        }));
        controls.addComponent(new Button("Reset data source", e -> {
            dummy.setDataSource(new LoggingDataSource(items));
        }));
        controls.addComponent(new Button("Remove all data", e -> {
            dummy.setDataSource(new LoggingDataSource(Collections.emptyList()));
        }));
        addComponent(dummy);
    }
}
