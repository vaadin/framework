package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Table;

public class TableColumnCollapsing extends Feature {

    @Override
    public String getName() {
        return "Table, column collapsing";
    }

    @Override
    public Component getExample() {
        return new TableMainFeaturesExample();
    }

    @Override
    public String getDescription() {
        return "Also known as a (Data)Grid, Table can be used to show data in"
                + " a tabular fashion. It's well suited for showing large datasets.<br>"
                + "Columns can be 'collapsed', which means that it's not shown,"
                + " but the user can make the column re-appear by using the"
                + " menu in the upper right of the table.<br/>"
                + " Columns can also be made invisible, in which case they can"
                + " not be brought back by the user.";
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
