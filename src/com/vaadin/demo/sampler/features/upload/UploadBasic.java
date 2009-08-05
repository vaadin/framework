package com.vaadin.demo.sampler.features.upload;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.Upload;

@SuppressWarnings("serial")
public class UploadBasic extends Feature {

    @Override
    public String getDescription() {
        return "Upload component provides a method to handle "
                + "files uploaded from clients. "
                + "In this example we simply be "
                + "count line breaks of the uploaded file."
                + "The data could just as well be saved on "
                + "the server as file or inserted into a database.";
    }

    @Override
    public String getName() {
        return "Basic upload";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Upload.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { UploadWithProgressMonitoring.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
