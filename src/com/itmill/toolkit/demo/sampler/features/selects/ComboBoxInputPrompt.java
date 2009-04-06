package com.itmill.toolkit.demo.sampler.features.selects;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.text.TextFieldInputPrompt;
import com.itmill.toolkit.ui.ComboBox;

public class ComboBoxInputPrompt extends Feature {
    @Override
    public String getName() {
        return "Combobox with input prompt";
    }

    @Override
    public String getDescription() {
        return "ComboBox is a drop-down selection component with single item selection."
                + " It can have an <i>input prompt</i> - a textual hint that is shown within"
                + " the select when no value is selected.<br/>"
                + " You can use an input prompt instead of a caption to save"
                + " space, but only do so if the function of the ComboBox is"
                + " still clear when a value is selected and the prompt is no"
                + " longer visible.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(ComboBox.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { ComboBoxStartsWith.class, ComboBoxContains.class,
                ComboBoxNewItems.class, TextFieldInputPrompt.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "UI Patterns, Input Prompt",
                "http://ui-patterns.com/pattern/InputPrompt") };
    }

}
