package com.itmill.toolkit.demo.sampler;

import java.io.BufferedReader;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FeatureView extends CustomLayout {

    OrderedLayout controls;

    Label sourceCode;

    public FeatureView() {
        super("sampler/featureview");

        controls = new OrderedLayout();

        controls.addComponent(new Label("Live example"));
        Button b = new Button("Show java source", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                // toggle source code
                sourceCode.setVisible(!sourceCode.isVisible());
                event.getButton().setCaption(
                        (sourceCode.isVisible() ? "Hide java source"
                                : "Show java source"));

            }
        });
        b.setStyleName(Button.STYLE_LINK);
        controls.addComponent(b);

        sourceCode = new Label();
        sourceCode.setVisible(false);
        sourceCode.setContentMode(Label.CONTENT_PREFORMATTED);
        controls.addComponent(sourceCode);
    }

    public void setFeature(Feature feature) {
        removeAllComponents();

        addComponent(controls, "feature-controls");

        addComponent(feature.getExample(), "feature-example");

        Label l = new Label(feature.getName());
        addComponent(l, "feature-name");

        l = new Label(feature.getDescription());
        l.setContentMode(Label.CONTENT_XHTML);
        addComponent(l, "feature-desc");

        StringBuffer src = new StringBuffer();
        BufferedReader srcbr = feature.getSource();
        try {
            for (String line = srcbr.readLine(); null != line; line = srcbr
                    .readLine()) {
                src.append(line);
                src.append("\n");
            }
        } catch (Exception e) {
            src = new StringBuffer("Sorry, no source available right now.");
        }
        sourceCode.setValue(src.toString());

        NamedExternalResource[] resources = feature.getRelatedResources();
        if (resources != null) {
            OrderedLayout res = new OrderedLayout();
            res.setCaption("Resources");
            for (NamedExternalResource r : resources) {
                res.addComponent(new Link(r.getName(), r));
            }
            addComponent(res, "feature-res");
        }

        APIResource[] apis = feature.getRelatedAPI();
        if (apis != null) {
            OrderedLayout api = new OrderedLayout();
            api.setCaption("Related Samples");
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
