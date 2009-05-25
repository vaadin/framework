package com.vaadin.demo.sampler.features.form;

import com.vaadin.data.Validatable;
import com.vaadin.data.Validator;
import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.FeatureSet;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.demo.sampler.features.commons.Errors;
import com.vaadin.demo.sampler.features.commons.Validation;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;

@SuppressWarnings("serial")
public class FormBasic extends Feature {

    @Override
    public String getName() {
        return "Form";
    }

    @Override
    public Component getExample() {
        return new FormPojoExample();
    }

    @Override
    public String getDescription() {
        return "A Form is most useful when connected to a data source, and"
                + " provides buffering and customization features to support"
                + " that scenario. A Form can easily be used as a POJO"
                + " or Bean editor by wrapping the bean using BeanItem. <br/>"
                + "The basic functionality only requires a couple of lines of"
                + " code, then Validators and other customizations can be "
                + "applied to taste. <br/>Enter something and try discarding or "
                + "applying.";
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
