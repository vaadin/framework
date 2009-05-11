package com.vaadin.demo.sampler.features.commons;

import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.form.FormPojoExample;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;

public class Validation extends Feature {

    @Override
    public String getName() {
        return "Validation";
    }

    private static final String desc = "Fields can have Validators that check"
            + " entered values. This is most useful when used within a Form, but"
            + " but can be used to validate single, stand-alone Fields as well.";

    @Override
    public Component getExample() {
        return new FormPojoExample();
    }

    public String getDescription() {
        return desc;
    }

    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Validatable.class),
                new APIResource(Validator.class), new APIResource(Form.class) };
    }

    public Class[] getRelatedFeatures() {
        return new Class[] { Errors.class, FeatureSet.Forms.class };
    }

    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
