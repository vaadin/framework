package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.ListSelect;

@SuppressWarnings("serial")
public class ListSelectMultiple extends Feature {
    @Override
    public String getName() {
        return "List select, multiple selections";
    }

    @Override
    public String getDescription() {
        return "A simple list select component with multiple item selection."
                + " You can allow or disallow <i>null selection</i> - i.e the"
                + " possibility to make an empty selection. Null selection is"
                + " allowed in this example.<br/>"
                + "You can select multiple items from the list by holding"
                + " the CTRL of SHIFT key while clicking the items.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ListSelect.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { NativeSelection.class, ListSelectSingle.class,
                TwinColumnSelect.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
