package com.itmill.toolkit.demo.sampler.features.commons;

import com.itmill.toolkit.data.Validatable;
import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;

public class Validation extends Feature {

    private static final String desc = "";

    public String getDescription() {
        return desc;
    }

    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Validatable.class),
                new APIResource(Validator.class) };
    }

    public Class[] getRelatedFeatures() {
        // TODO link form sample
        return new Class[] { Errors.class };
    }

    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
