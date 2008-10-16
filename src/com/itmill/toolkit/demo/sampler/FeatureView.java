package com.itmill.toolkit.demo.sampler;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FeatureView extends CustomLayout {

    private static final String MSG_SHOW_SRC = "Show java source";
    private static final String MSG_HIDE_SRC = "Hide java source";

    private OrderedLayout controls;

    private Label sourceCode;
    private Button showCode;

    private Feature currentFeature;

    public FeatureView() {
        super("featureview");

        controls = new OrderedLayout();
        controls.setCaption("Live example");
        showCode = new Button(MSG_SHOW_SRC, new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                toggleSource();
            }
        });
        showCode.setStyleName(Button.STYLE_LINK);
        controls.addComponent(showCode);

        sourceCode = new Label();
        sourceCode.setVisible(false);
        sourceCode.setContentMode(Label.CONTENT_PREFORMATTED);
        controls.addComponent(sourceCode);
    }

    private void toggleSource() {
        showSource(!sourceCode.isVisible());
    }

    private void showSource(boolean show) {
        showCode.setCaption((show ? MSG_HIDE_SRC : MSG_SHOW_SRC));
        sourceCode.setVisible(show);
    }

    public void setFeature(Feature feature) {
        if (feature != currentFeature) {
            removeAllComponents();
            showSource(false);

            addComponent(controls, "feature-controls");

            addComponent(feature.getExample(), "feature-example");

            Label l = new Label(feature.getName());
            addComponent(l, "feature-name");

            l = new Label(feature.getDescription());
            l.setContentMode(Label.CONTENT_XHTML);
            addComponent(l, "feature-desc");

            sourceCode.setValue(feature.getSource());

            NamedExternalResource[] resources = feature.getRelatedResources();
            if (resources != null) {
                OrderedLayout res = new OrderedLayout();
                res.setCaption("Additional resources");
                for (NamedExternalResource r : resources) {
                    res.addComponent(new Link(r.getName(), r));
                }
                addComponent(res, "feature-res");
            }

            APIResource[] apis = feature.getRelatedAPI();
            if (apis != null) {
                OrderedLayout api = new OrderedLayout();
                api.setCaption("API documentation");
                addComponent(api, "feature-api");
                for (APIResource r : apis) {
                    api.addComponent(new Link(r.getName(), r));
                }
            }

            Class[] features = feature.getRelatedFeatures();
            if (features != null) {
                OrderedLayout rel = new OrderedLayout();
                rel.setCaption("Related Samples");
                for (Class c : features) {
                    Feature f = SamplerApplication.getFeatureFor(c);
                    if (f != null) {
                        String path = SamplerApplication.getPathFor(f);
                        rel.addComponent(new Link(f.getName(),
                                new ExternalResource(getApplication().getURL()
                                        + path)));
                    }
                }
                addComponent(rel, "feature-rel");
            }
        }

    }

}
