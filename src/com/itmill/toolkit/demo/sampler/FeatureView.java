package com.itmill.toolkit.demo.sampler;

import java.util.HashMap;

import com.itmill.toolkit.demo.sampler.ActiveLink.LinkActivatedEvent;
import com.itmill.toolkit.demo.sampler.ActiveLink.LinkActivatedListener;
import com.itmill.toolkit.demo.sampler.SamplerApplication.SamplerWindow;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FeatureView extends CustomLayout {

    private static final String MSG_SHOW_SRC = "⊞ Show Java™ source";
    private static final String MSG_HIDE_SRC = "⊟ Hide Java™ source";

    private OrderedLayout controls;

    private Panel sourcePanel;
    private Label sourceCode;
    private Button showCode;

    private HashMap exampleCache = new HashMap();

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
        showCode.addStyleName("showcode");
        showCode.setWidth("100%");
        controls.addComponent(showCode);

        sourceCode = new CodeLabel();
        sourceCode.setContentMode(Label.CONTENT_PREFORMATTED);

        sourcePanel = new Panel();
        sourcePanel.getLayout().setSizeUndefined();
        sourcePanel.addStyleName(Panel.STYLE_LIGHT);
        sourcePanel.addStyleName("source");
        sourcePanel.addComponent(sourceCode);
        sourcePanel.setVisible(false);
        sourcePanel.setWidth("100%");
        sourcePanel.setHeight("250px");

        controls.addComponent(sourcePanel);
    }

    private void toggleSource() {
        showSource(!sourcePanel.isVisible());
    }

    private void showSource(boolean show) {
        showCode.setCaption((show ? MSG_HIDE_SRC : MSG_SHOW_SRC));
        sourcePanel.setVisible(show);
    }

    public void setFeature(Feature feature) {
        if (feature != currentFeature) {
            removeAllComponents();
            showSource(false);

            addComponent(controls, "feature-controls");

            addComponent(getExampleFor(feature), "feature-example");

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
                    final Feature f = SamplerApplication.getFeatureFor(c);
                    if (f != null) {
                        String path = SamplerApplication.getPathFor(f);
                        ActiveLink al = new ActiveLink(f.getName(),
                                new ExternalResource(getApplication().getURL()
                                        + "#" + path));
                        al.addListener(new LinkActivatedListener() {
                            public void linkActivated(LinkActivatedEvent event) {
                                if (event.isLinkOpened()) {
                                    getWindow()
                                            .showNotification(
                                                    f.getName()
                                                            + " opened if new window/tab");
                                } else {
                                    SamplerWindow w = (SamplerWindow) getWindow();
                                    w.setFeature(f);
                                }
                            }
                        });
                        rel.addComponent(al);
                    }
                }
                addComponent(rel, "feature-rel");
            }
        }

    }

    private Component getExampleFor(Feature f) {

        Component ex = (Component) exampleCache.get(f);
        if (ex == null) {
            ex = f.getExample();
            exampleCache.put(f, ex);
        }
        return ex;
    }

}
