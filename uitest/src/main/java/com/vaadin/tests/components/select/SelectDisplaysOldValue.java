package com.vaadin.tests.components.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;

public class SelectDisplaysOldValue extends TestBase {

    @Override
    protected void setup() {
        addComponent(new DynamicSelectTestCase());
    }

    public class DynamicSelectTestCase extends HorizontalLayout {

        private static final String CONTROLLER_COMBO_BOX_CAPTION = "Master : ";
        private static final String SLAVE_COMBO_BOX_CAPTION = "Slave :";

        private Select controllerComboBox;
        private Select slaveComboBox;

        private Map<Integer, String> controllerOptionMap = new HashMap<Integer, String>();

        private Map<String, List<String>> slaveOptionMapping = new HashMap<String, List<String>>();
        private final Object NAME_PROPERTY_ID = "name";

        public DynamicSelectTestCase() {
            populateControllerOptionMapping();
            populateSlaveOptionMappings();
            buildDynamicSelectTestCase();
        }

        private void populateControllerOptionMapping() {
            for (int i = 1; i <= 3; i++) {
                controllerOptionMap.put(i, "Master " + i);
            }
        }

        private void populateSlaveOptionMappings() {
            for (String controllerOption : controllerOptionMap.values()) {
                List<String> slaveOptions = new ArrayList<String>();

                for (int i = 1; i <= 3; i++) {
                    slaveOptions.add(controllerOption + " - Slave " + i);
                }

                slaveOptionMapping.put(controllerOption, slaveOptions);
            }
        }

        private void buildDynamicSelectTestCase() {
            setSpacing(true);
            setMargin(true);
            buildAndConfigureComboBoxes();
            addComponent(controllerComboBox);
            addComponent(slaveComboBox);
        }

        private void buildAndConfigureComboBoxes() {
            IndexedContainer masterOptionContainer = initMasterOptionContainer();
            controllerComboBox = new Select(CONTROLLER_COMBO_BOX_CAPTION,
                    masterOptionContainer);
            configureMasterOptionDropdown();
            controllerComboBox.addListener(new ControllerUpdatedListener());

            buildSlaveDropdown(1);
        }

        private void buildSlaveDropdown(Integer masterId) {
            IndexedContainer slaveOptionContainer = initSlaveOptionContainer(masterId);
            slaveComboBox = new Select(SLAVE_COMBO_BOX_CAPTION,
                    slaveOptionContainer);
            configureSlaveOptionDropdown();
        }

        private IndexedContainer initMasterOptionContainer() {
            IndexedContainer containerToReturn = new IndexedContainer();
            Object defaultValue = null;
            Item itemAdded;

            containerToReturn.addContainerProperty(NAME_PROPERTY_ID,
                    String.class, defaultValue);

            for (Integer optionId : controllerOptionMap.keySet()) {
                itemAdded = containerToReturn.addItem(optionId);
                itemAdded.getItemProperty(NAME_PROPERTY_ID).setValue(
                        controllerOptionMap.get(optionId));
            }

            return containerToReturn;
        }

        private IndexedContainer initSlaveOptionContainer(Integer masterId) {
            IndexedContainer containerToReturn = new IndexedContainer();
            Object defaultValue = null;
            Item itemAdded;
            List<String> options;

            options = slaveOptionMapping.get(controllerOptionMap.get(masterId));
            containerToReturn.addContainerProperty(NAME_PROPERTY_ID,
                    String.class, defaultValue);

            for (String option : options) {
                itemAdded = containerToReturn.addItem(option);
                itemAdded.getItemProperty(NAME_PROPERTY_ID).setValue(option);
            }

            return containerToReturn;
        }

        private void configureMasterOptionDropdown() {
            controllerComboBox.setItemCaptionPropertyId(NAME_PROPERTY_ID);
            controllerComboBox.setNullSelectionAllowed(false);
            controllerComboBox.setNewItemsAllowed(false);
            controllerComboBox.setImmediate(true);
            controllerComboBox.setBuffered(true);

        }

        private void configureSlaveOptionDropdown() {
            slaveComboBox.setItemCaptionPropertyId(NAME_PROPERTY_ID);
            slaveComboBox.setNullSelectionAllowed(false);
            slaveComboBox.setNewItemsAllowed(false);
            slaveComboBox.setImmediate(true);
            slaveComboBox.setBuffered(true);
        }

        private void refreshSlaveDropdown(Integer masterId) {
            slaveComboBox
                    .setContainerDataSource(initSlaveOptionContainer(masterId));
            System.out.println("Slave value: " + slaveComboBox.getValue());
        }

        private class ControllerUpdatedListener implements
                Property.ValueChangeListener {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                refreshSlaveDropdown((Integer) valueChangeEvent.getProperty()
                        .getValue());
            }
        }
    }

    @Override
    protected String getDescription() {
        return "Select an 'master' item from the first combo box, a slave from the second, select another master and focus+blur the slave combo box and the slave combobox will show a value not in the combo box";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5556;
    }

}
