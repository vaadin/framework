package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.NativeSelect;

@SuppressWarnings("serial")
public class NativeSelection extends Feature {
    @Override
    public String getName() {
        return "Native select";
    }

    @Override
    public String getDescription() {
        return "A NativeSelect is a a simple drop-down list"
                + " for selecting one item. It is called <i>native</i>"
                + " because it uses the look and feel from the browser in use.<br/>"
                + " The ComboBox component is a much more versatile variant,"
                + " but without the native look and feel.<br/>"
                + " From a usability standpoint, you might also want to"
                + " consider using a ListSelect in single-select-mode, so that"
                + " the user can see all options right away.";

    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(NativeSelect.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ComboBoxPlain.class, ListSelectSingle.class,
                FeatureSet.Selects.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
