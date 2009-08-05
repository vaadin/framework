package com.vaadin.demo.sampler.features.upload;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;

@SuppressWarnings("serial")
public class UploadWithProgressMonitoring extends Feature {

    @Override
    public String getDescription() {
        return "Upload does not block the entire UI. While uploading a large"
                + "file users can navigate to other views in the application."
                + " Other advanced upload features used in this demo:<ul>"
                + "<li> start upload once file is selected aka \"one-click-upload\"</li>"
                + "<li> process the file during the upload</li>"
                + "<li> trac events that occure during the update</li>"
                + "<li> visualize upload progress with ProgressIndicator</li>"
                + "<li> ability to cancel the upload</li>" + "</ul>";
    }

    @Override
    public String getName() {
        return "Upload with advanced features";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Upload.class),
                new APIResource(ProgressIndicator.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { UploadBasic.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
