package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.ListSelect;

public class ListSelectSingle extends Feature {
    @Override
    public String getName() {
        return "Listselect - Single";
    }

    @Override
    public String getDescription() {
        return "A simple list select component with single item selection.<br/>"
                + " You can allow or disallow <i>null selection</i> - i.e the"
                + " possibility to make an empty selection. Null selection is"
                + " not allowed in this example.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ListSelect.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NativeSelection.class, ListSelectMultiple.class,
                TwinColumnSelect.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
