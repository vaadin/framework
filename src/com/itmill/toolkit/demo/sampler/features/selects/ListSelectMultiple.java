package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.ListSelect;

public class ListSelectMultiple extends Feature {
    @Override
    public String getName() {
        return "Listselect - Multi";
    }

    @Override
    public String getDescription() {
        return "A simple list select component with multiple item selection."
                + " A null selection is also allowed in this example."
                + "<br>You can select multiple items from the list by holding"
                + " the CTRL of SHIFT key while clicking the items.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ListSelect.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { NativeSelection.class, ListSelectSingle.class,
                TwinColumnSelect.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
