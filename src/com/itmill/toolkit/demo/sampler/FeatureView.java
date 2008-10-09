package com.itmill.toolkit.demo.sampler;

import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;

public class FeatureView extends CustomLayout {

    public FeatureView() {
        super("sampler/featureview");
    }

    public void setFeature(Feature feature) {
        removeAllComponents();

        Label l = new Label(feature.getName());
        addComponent(l, "feature-name");

        l = new Label(feature.getDescription());
        l.setContentMode(Label.CONTENT_XHTML);
        addComponent(l, "feature-desc");

    }
}
