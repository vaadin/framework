package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxPlain extends Feature {
    @Override
    public String getName() {
        return "Combobox";
    }

    @Override
    public String getDescription() {
        return "A drop-down selection component with single item selection."
                + " Shown here is the most basic variant, which basically"
                + " provides the same functionality as a NativeSelect with"
                + " added lazy-loading if there are many options.<br/>"
                + " See related examples for more advanced features.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ComboBoxStartsWith.class, ComboBoxContains.class,
                ComboBoxNewItems.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
