package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

public class TableCellStyling extends Feature {

    @Override
    public String getName() {
        return "Table, styling cells";
    }

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "Individual cells can be styled in a Table by using a"
                + " CellStyleGenerator. Regular CSS is used to create the"
                + " actual style.<br/>Double click a first or last name to"
                + " mark/unmark that cell.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Table.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { FeatureSet.Tables.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
