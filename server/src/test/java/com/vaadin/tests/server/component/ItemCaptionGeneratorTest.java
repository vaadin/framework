package com.vaadin.tests.server.component;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.provider.DataGenerator;
import com.vaadin.shared.data.DataCommunicatorConstants;
import com.vaadin.shared.ui.ListingJsonConstants;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TwinColSelect;

import elemental.json.JsonObject;

public class ItemCaptionGeneratorTest {

    private static class TestDataGenerator implements DataGenerator<Object> {
        JsonObject generated = null;

        @Override
        public void generateData(Object item, JsonObject jsonObject) {
            generated = jsonObject;
        }

    }

    @Test
    public void testItemCaptionGenerator_nullCaptionGiven_convertedToEmptyString() {
        Collection<AbstractListing<Object>> listings = new ArrayList<>();

        ComboBox<Object> comboBox = new ComboBox<>();
        comboBox.setData(DataCommunicatorConstants.NAME);
        comboBox.setItemCaptionGenerator(item -> null);
        listings.add(comboBox);

        CheckBoxGroup<Object> cbg = new CheckBoxGroup<>();
        cbg.setData(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        cbg.setItemCaptionGenerator(item -> null);
        listings.add(cbg);

        ListSelect<Object> listSelect = new ListSelect<>();
        listSelect.setData(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        listSelect.setItemCaptionGenerator(item -> null);
        listings.add(listSelect);

        NativeSelect<Object> nativeSelect = new NativeSelect<>();
        nativeSelect.setData(DataCommunicatorConstants.DATA);
        nativeSelect.setItemCaptionGenerator(item -> null);
        listings.add(nativeSelect);

        RadioButtonGroup<Object> rbg = new RadioButtonGroup<>();
        rbg.setData(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        rbg.setItemCaptionGenerator(item -> null);
        listings.add(rbg);

        TwinColSelect<Object> tc = new TwinColSelect<>();
        tc.setData(ListingJsonConstants.JSONKEY_ITEM_VALUE);
        tc.setItemCaptionGenerator(item -> null);
        listings.add(tc);

        for (AbstractListing<Object> listing : listings) {
            listing.setItems("Uno");
            TestDataGenerator dataGenerator = new TestDataGenerator();
            listing.getDataCommunicator().addDataGenerator(dataGenerator);
            listing.getDataCommunicator().beforeClientResponse(true);

            Assert.assertEquals(
                    listing.getClass().getName()
                            + " does not convert null caption from generator to empty string",
                    "",
                    dataGenerator.generated.hasKey((String) listing.getData())
                            ? dataGenerator.generated.getString(
                                    (String) listing.getData())
                            : null);
        }
    }

}
