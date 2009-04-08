package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxInputPrompt;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxNewItems;
import com.itmill.toolkit.ui.TextField;

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

    @Override
    public Class[] getRelatedFeatures() {
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
