package com.itmill.toolkit.demo.sampler.features.table;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;

public class TableRowStyling extends Feature {

    @Override
    public Component getExample() {
        return new TableStylingExample();
    }

    @Override
    public String getDescription() {
        return "Rows can be styled in a Table by using a CellStyleGenerator. Regular CSS is used to create the actual style.<br/>Use the context menu (right-/ctrl-click) to apply a row style in the example.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class[] getRelatedFeatures() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
