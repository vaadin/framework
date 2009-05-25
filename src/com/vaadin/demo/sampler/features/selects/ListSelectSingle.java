package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.ListSelect;

@SuppressWarnings("serial")
public class ListSelectSingle extends Feature {
    @Override
    public String getName() {
        return "List select, single selection";
    }

    @Override
    public String getDescription() {
        return "A simple list select component with single item selection.<br/>"
                + "You can allow or disallow <i>null selection</i> - i.e the"
                + " possibility to make an empty selection. Null selection is"
                + " not allowed in this example.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ListSelect.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { NativeSelection.class, ListSelectMultiple.class,
                TwinColumnSelect.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
