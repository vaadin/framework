package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.terminal.gwt.client.ui.Table;
import com.itmill.toolkit.ui.Component;

public class TableRowStyling extends Feature {

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "Also known as a (Data)Grid, Table can be used to show data in"
                + " a tabular fashion. It's well suited for showing large datasets.<br>"
                + "Rows can be styled in a Table by using a CellStyleGenerator."
                + " Regular CSS is used to create the actual style.<br/>Use the"
                + " context menu (right-/ctrl-click) to apply a row style in"
                + " the example.";

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
