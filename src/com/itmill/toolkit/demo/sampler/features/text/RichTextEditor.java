package com.itmill.toolkit.demo.sampler.features.text;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.RichTextArea;

public class RichTextEditor extends Feature {
    @Override
    public String getName() {
        return "Rich text area";
    }

    @Override
    public String getDescription() {
        return "The RichTextArea allows 'rich' formatting of the input.<br/>"
                + "Click the <i>Edit</i> button to edit the label content"
                + " with the RichTextArea.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(RichTextArea.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { TextArea.class, LabelRich.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Component getExample() {
        return new LabelRichExample();
    }
}
