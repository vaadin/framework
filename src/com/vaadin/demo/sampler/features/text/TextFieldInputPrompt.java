package com.vaadin.demo.sampler.features.text;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.selects.ComboBoxInputPrompt;
import com.vaadin.demo.sampler.features.selects.ComboBoxNewItems;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class TextFieldInputPrompt extends Feature {
    @Override
    public String getName() {
        return "Text field with input prompt";
    }

    @Override
    public String getDescription() {
        return " The TextField can have an <i>input prompt</i> - a textual hint that is shown within"
                + " the field when the field is otherwise empty.<br/>"
                + " You can use an input prompt instead of a caption to save"
                + " space, but only do so if the function of the TextField is"
                + " still clear when a value has been entered and the prompt is no"
                + " longer visible.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TextField.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        // TODO update CB -ref to 'suggest' pattern, when available
        return new Class[] { TextFieldSingle.class, TextFieldSecret.class,
                ComboBoxInputPrompt.class, ComboBoxNewItems.class,
                FeatureSet.Texts.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] { new NamedExternalResource(
                "UI Patterns, Input Prompt",
                "http://ui-patterns.com/pattern/InputPrompt") };
    }

}
