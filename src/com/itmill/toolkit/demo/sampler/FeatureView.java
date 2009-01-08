package com.itmill.toolkit.demo.sampler;

import java.util.HashMap;

import com.itmill.toolkit.demo.sampler.ActiveLink.LinkActivatedEvent;
import com.itmill.toolkit.demo.sampler.ActiveLink.LinkActivatedListener;
import com.itmill.toolkit.demo.sampler.SamplerApplication.SamplerWindow;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class FeatureView extends HorizontalLayout {

    private static final String MSG_SHOW_SRC = "⊞ Show Java™ source";
    private static final String MSG_HIDE_SRC = "⊟ Hide Java™ source";

    private Panel right;
    private Panel left;

    private VerticalLayout controls;

    private Panel sourcePanel;
    private Label sourceCode;
    private Button showCode;

    private HashMap<Feature, Component> exampleCache = new HashMap<Feature, Component>();

    private Feature currentFeature;

    public FeatureView() {

        setSizeFull();

        left = new Panel();
        left.setStyleName(Panel.STYLE_LIGHT);
        left.addStyleName("feature-main");
        left.setSizeFull();
        ((VerticalLayout) left.getLayout()).setSpacing(true);
        addComponent(left);
        setExpandRatio(left, 1);

        right = new Panel();
        right.setStyleName(Panel.STYLE_LIGHT);
        right.addStyleName("feature-info");
        right.setWidth("350px");
        right.setHeight("100%");
        addComponent(right);

        controls = new VerticalLayout();
        controls.setStyleName("feature-controls");
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
            right.removeAllComponents();
            left.removeAllComponents();
            showSource(false);

            left.addComponent(controls);

            left.addComponent(getExampleFor(feature));

            right.setCaption(feature.getName());

            Label l = new Label(feature.getDescription());
            l.setContentMode(Label.CONTENT_XHTML);
            right.addComponent(l);

            sourceCode.setValue(feature.getSource());

            NamedExternalResource[] resources = feature.getRelatedResources();
            if (resources != null) {
                VerticalLayout res = new VerticalLayout();
                res.setCaption("Additional resources");
                for (NamedExternalResource r : resources) {
                    res.addComponent(new Link(r.getName(), r));
                }
                right.addComponent(res);
            }

            APIResource[] apis = feature.getRelatedAPI();
            if (apis != null) {
                VerticalLayout api = new VerticalLayout();
                api.setCaption("API documentation");
                for (APIResource r : apis) {
                    api.addComponent(new Link(r.getName(), r));
                }
                right.addComponent(api);
            }

            Class[] features = feature.getRelatedFeatures();
            if (features != null) {
                VerticalLayout rel = new VerticalLayout();
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
                right.addComponent(rel);
            }
        }

    }

    private Component getExampleFor(Feature f) {
        Component ex = exampleCache.get(f);
        if (ex == null) {
            ex = f.getExample();
            exampleCache.put(f, ex);
        }
        return ex;
    }

}
