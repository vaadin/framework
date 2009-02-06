package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.data.Validatable;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.FeatureSet;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.form.FormPojoExample;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Form;

public class Validation extends Feature {

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
