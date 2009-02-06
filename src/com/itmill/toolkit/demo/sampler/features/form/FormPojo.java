package com.itmill.toolkit.demo.sampler.features.form;

import com.itmill.toolkit.data.Validatable;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.commons.Errors;
import com.itmill.toolkit.demo.sampler.features.commons.Validation;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;

public class FormPojo extends Feature {

    @Override
    public Component getExample() {
        return new FormPojoExample();
    }

    @Override
    public String getDescription() {
        return "The Form -component provides a convenient way to organize"
                + " related fields visually.<br/> It is most useful when connected"
                + " to a data source, and provides buffering and customization"
                + " features to support that scenario.<br> It easily be used as"
                + " a POJO or Bean editor by wrapping the bean using BeanItem."
                + " The basic functionality only requires a couple of lines of"
                + " code, then Validators and other customizations can be"
                + " applied to taste.";
    }

    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Validatable.class),
                new APIResource(Validator.class), new APIResource(Form.class) };
    }

    public Class[] getRelatedFeatures() {
        return new Class[] { Validation.class, Errors.class,
                FeatureSet.Forms.class };
    }

    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
