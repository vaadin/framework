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
    public String getName() {
        return "Bean-bound form";
    }

    @Override
    public Component getExample() {
        return new FormPojoExample();
    }

    @Override
    public String getDescription() {
        return "It can easily be used as a POJO or Bean editor by wrapping the"
                + " bean using BeanItem. The basic functionality only requires"
                + " a couple of lines of code, then Validators and other"
                + " customizations can be applied to taste.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Validatable.class),
                new APIResource(Validator.class), new APIResource(Form.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] { Validation.class, Errors.class,
                FeatureSet.Forms.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
