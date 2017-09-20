package com.vaadin.v7.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.shared.ui.combobox.FilteringMode;

public class ComboBoxTest {

    private ComboBox comboBox;

    @Before
    public void setup() {
        comboBox = new ComboBox();
        comboBox.setLocale(Locale.ENGLISH);
    }

    @Test
    public void options_noFilter() {
        ComboBox comboBox = new ComboBox();
        for (int i = 0; i < 10; i++) {
            comboBox.addItem("" + i);
        }

        List<?> options = comboBox.getFilteredOptions();
        Assert.assertEquals(10, options.size());
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals("" + i, options.get(i));
        }

    }

    @Test
    public void options_inMemoryFilteringStartsWith() {
        for (int i = 0; i < 21; i++) {
            comboBox.addItem("" + i);
        }

        setFilterAndCurrentPage(comboBox, "1", 0);

        List<?> options = comboBox.getFilteredOptions();
        Assert.assertEquals(11, options.size());

    }

    @Test
    public void options_inMemoryFilteringContains() {
        comboBox.setFilteringMode(FilteringMode.CONTAINS);
        for (int i = 0; i < 21; i++) {
            comboBox.addItem("" + i);
        }

        setFilterAndCurrentPage(comboBox, "2", 0);
        List<?> options = comboBox.getFilteredOptions();
        Assert.assertEquals(3, options.size());

    }

    private static void setFilterAndCurrentPage(ComboBox comboBox,
            String filterString, int currentPage) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("filter", filterString);
        variables.put("page", currentPage);
        comboBox.changeVariables(null, variables);

    }

    @Test
    public void getOptions_moreThanOnePage_noNullItem() {
        int nrOptions = comboBox.getPageLength() * 2;
        for (int i = 0; i < nrOptions; i++) {
            comboBox.addItem("" + i);
        }
        setFilterAndCurrentPage(comboBox, "", 0);

        List<?> goingToClient = comboBox
                .sanitizeList(comboBox.getFilteredOptions(), false);
        Assert.assertEquals(comboBox.getPageLength(), goingToClient.size());

    }

    @Test
    public void getOptions_moreThanOnePage_nullItem() {
        int nrOptions = comboBox.getPageLength() * 2;
        for (int i = 0; i < nrOptions; i++) {
            comboBox.addItem("" + i);
        }

        setFilterAndCurrentPage(comboBox, "", 0);
        List<?> goingToClient = comboBox
                .sanitizeList(comboBox.getFilteredOptions(), true);
        // Null item is shown on first page
        Assert.assertEquals(comboBox.getPageLength() - 1, goingToClient.size());

        setFilterAndCurrentPage(comboBox, "", 1);
        goingToClient = comboBox.sanitizeList(comboBox.getFilteredOptions(),
                true);
        // Null item is not shown on the second page
        Assert.assertEquals(comboBox.getPageLength(), goingToClient.size());

    }

    @Test
    public void getOptions_lessThanOnePage_noNullItem() {
        int nrOptions = comboBox.getPageLength() / 2;
        for (int i = 0; i < nrOptions; i++) {
            comboBox.addItem("" + i);
        }
        setFilterAndCurrentPage(comboBox, "", 0);

        List<?> goingToClient = comboBox
                .sanitizeList(comboBox.getFilteredOptions(), false);
        Assert.assertEquals(nrOptions, goingToClient.size());

    }

    @Test
    public void getOptions_lessThanOnePage_withNullItem() {
        int nrOptions = comboBox.getPageLength() / 2;
        for (int i = 0; i < nrOptions; i++) {
            comboBox.addItem("" + i);
        }
        setFilterAndCurrentPage(comboBox, "", 0);

        List<?> goingToClient = comboBox
                .sanitizeList(comboBox.getFilteredOptions(), true);
        // All items + null still fit on one page
        Assert.assertEquals(nrOptions, goingToClient.size());

    }

    @Test
    public void getOptions_exactlyOnePage_withNullItem() {
        int nrOptions = comboBox.getPageLength();
        for (int i = 0; i < nrOptions; i++) {
            comboBox.addItem("" + i);
        }
        setFilterAndCurrentPage(comboBox, "", 0);

        List<?> goingToClient = comboBox
                .sanitizeList(comboBox.getFilteredOptions(), true);
        // Null item on first page
        Assert.assertEquals(nrOptions - 1, goingToClient.size());

        setFilterAndCurrentPage(comboBox, "", 1);
        goingToClient = comboBox.sanitizeList(comboBox.getFilteredOptions(),
                true);
        // All but one was on the first page
        Assert.assertEquals(1, goingToClient.size());

    }
}
