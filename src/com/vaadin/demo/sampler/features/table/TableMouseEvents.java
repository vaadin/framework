package com.vaadin.demo.sampler.features.table;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class TableMouseEvents extends Feature {

    @Override
    public String getName() {
        return "Table, mouse events";
    }

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "An ItemClickListener can be used to react to mouse click"
                + " events. Different buttons, double click, and modifier keys"
                + " can be detected.<br/>Double-click a first or last name to"
                + " toggle it's marked state.";
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
