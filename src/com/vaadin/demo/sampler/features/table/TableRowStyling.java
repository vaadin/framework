package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableRowStyling extends Feature {

    @Override
    public String getName() {
        return "Table, row styling";
    }

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "Rows can be styled in a Table by using a CellStyleGenerator."
                + " Regular CSS is used to create the actual style.<br/>Use the"
                + " context menu (right-/ctrl-click) to apply a row style in"
                + " the example.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Table.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { FeatureSet.Tables.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
