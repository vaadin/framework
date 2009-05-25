package com.vaadin.demo.sampler.features.selects;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.text.TextFieldInputPrompt;
import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
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
