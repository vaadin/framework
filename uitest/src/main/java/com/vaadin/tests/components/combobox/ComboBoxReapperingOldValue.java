package com.vaadin.tests.components.combobox;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ComboBoxReapperingOldValue extends LegacyApplication implements
        ValueChangeListener {

    ComboBox cbox1 = new ComboBox();
    ComboBox cbox2 = new ComboBox();

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow("ComboBoxCacheTest");
        setMainWindow(mainWindow);

        VerticalLayout layout = new VerticalLayout();

        Label lbl = new Label(
                "try selecting value 1 from the first combo box, so that the second combo box will be populated. select a value in second combo box."
                        + "then select a new value from combo box one, after that click on the second combo box. The old selected value appears.");
        layout.addComponent(lbl);

        cbox1.setCaption("Com Box 1");
        cbox1.setFilteringMode(FilteringMode.CONTAINS);
        cbox1.setContainerDataSource(getContainer());
        cbox1.setImmediate(true);
        cbox1.setNullSelectionAllowed(false);
        cbox1.addListener(this);

        layout.addComponent(cbox1);
        layout.addComponent(cbox2);

        cbox2.setCaption("Com Box 2");
        cbox2.setEnabled(false);
        cbox2.setNullSelectionAllowed(false);

        mainWindow.setContent(layout);

    }

    private Container getContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("na", String.class, null);

        for (int i = 0; i < 10; i++) {
            container.addItem(i);
        }
        return container;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        cbox2.removeAllItems();
        if ("1".equals(event.getProperty().getValue().toString())) {
            cbox2.setEnabled(true);
            cbox2.setContainerDataSource(getContainer());
        }
    }

}
