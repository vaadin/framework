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
        return "A RichTextArea component allows editing XHTML"
                + " content. <br/>Click the edit button to open the"
                + " editor and the show button to show the edited"
                + " result as an XHTML label.";
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
