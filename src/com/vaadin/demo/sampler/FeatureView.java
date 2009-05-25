package com.vaadin.demo.sampler;

import java.util.HashMap;

import com.vaadin.demo.sampler.ActiveLink.LinkActivatedEvent;
import com.vaadin.demo.sampler.ActiveLink.LinkActivatedListener;
import com.vaadin.demo.sampler.SamplerApplication.SamplerWindow;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class FeatureView extends HorizontalLayout {

    private static final String MSG_SHOW_SRC = "Show Java™ source »";

    private Panel right;
    private VerticalLayout left;

    private VerticalLayout controls;

    private ActiveLink showSrc;

    private HashMap<Feature, Component> exampleCache = new HashMap<Feature, Component>();

    private Feature currentFeature;

    private Window srcWindow;

    public FeatureView() {

        setWidth("100%");
        setMargin(true);
        setSpacing(true);
        setStyleName("sample-view");

        left = new VerticalLayout();
        left.setWidth("100%");
        left.setSpacing(true);
        left.setMargin(false);
        addComponent(left);
        setExpandRatio(left, 1);

        right = new Panel();
        right.getLayout().setMargin(true, false, false, false);
        right.setStyleName(Panel.STYLE_LIGHT);
        right.addStyleName("feature-info");
        right.setWidth("369px");
        addComponent(right);

        controls = new VerticalLayout();
        controls.setWidth("100%");
        controls.setStyleName("feature-controls");

        HorizontalLayout controlButtons = new HorizontalLayout();
        controls.addComponent(controlButtons);

        Button resetExample = new Button("Reset example",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        resetExample();
                    }
                });
        resetExample.setStyleName(Button.STYLE_LINK);
        resetExample.addStyleName("showcode");
        controlButtons.addComponent(resetExample);

        controlButtons.addComponent(new Label("|"));

        showSrc = new ActiveLink();
        showSrc
                .setDescription("Right / middle / ctrl / shift -click for browser window/tab");
        showSrc.addListener(new LinkActivatedListener() {

            public void linkActivated(LinkActivatedEvent event) {
                if (!event.isLinkOpened()) {
                    showSource(currentFeature.getSource());
                }

            }

        });
        showSrc.setCaption(MSG_SHOW_SRC);
        showSrc.addStyleName("showcode");
        showSrc.setTargetBorder(Link.TARGET_BORDER_NONE);
        controlButtons.addComponent(showSrc);

    }

    public void showSource(String source) {
        if (srcWindow == null) {
            srcWindow = new Window("Java™ source");
            srcWindow.getContent().setSizeUndefined();
            srcWindow.setWidth("70%");
            srcWindow.setHeight("60%");
            srcWindow.setPositionX(100);
            srcWindow.setPositionY(100);
        }
        srcWindow.removeAllComponents();
        srcWindow.addComponent(new CodeLabel(source));

        if (srcWindow.getParent() == null) {
            getWindow().addWindow(srcWindow);
        }
    }

    private void resetExample() {
        if (currentFeature != null) {
            Feature f = currentFeature;
            currentFeature = null;
            exampleCache.remove(f);
            setFeature(f);
        }
    }

    public void setFeature(Feature feature) {
        if (feature != currentFeature) {
            currentFeature = feature;
            right.removeAllComponents();
            left.removeAllComponents();

            left.addComponent(controls);
            controls.setCaption(feature.getName());

            left.addComponent(getExampleFor(feature));

            right.setCaption("Description and Resources");

            final Feature parent = (Feature) SamplerApplication
                    .getAllFeatures().getParent(feature);
            String desc = parent.getDescription();
            boolean hasParentDesc = false;

            final Label parentLabel = new Label(parent.getDescription());
            if (desc != null && desc != "") {
                parentLabel.setContentMode(Label.CONTENT_XHTML);
                right.addComponent(parentLabel);
                hasParentDesc = true;
            }

            desc = feature.getDescription();
            if (desc != null && desc != "") {
                // Sample description uses additional decorations if a parent
                // description is found
                final Label l = new Label(
                        "<div class=\"outer-deco\"><div class=\"deco\"><span class=\"deco\"></span>"
                                + desc + "</div></div>", Label.CONTENT_XHTML);
                right.addComponent(l);
                if (hasParentDesc) {
                    l.setStyleName("sample-description");
                } else {
                    l.setStyleName("description");
                }
            }

            { // open src in new window -link
                String path = SamplerApplication.getPathFor(currentFeature);
                showSrc.setTargetName(path);
                showSrc.setResource(new ExternalResource(getApplication()
                        .getURL()
                        + "src/" + path));
            }

            NamedExternalResource[] resources = feature.getRelatedResources();
            if (resources != null) {
                VerticalLayout res = new VerticalLayout();
                Label caption = new Label("<span>Additional Resources</span>",
                        Label.CONTENT_XHTML);
                caption.setStyleName("section");
                caption.setWidth("100%");
                res.addComponent(caption);
                res.setMargin(false, false, true, false);
                for (NamedExternalResource r : resources) {
                    final Link l = new Link(r.getName(), r);
                    l.setIcon(new ThemeResource("../runo/icons/16/note.png"));
                    res.addComponent(l);
                }
                right.addComponent(res);
            }

            APIResource[] apis = feature.getRelatedAPI();
            if (apis != null) {
                VerticalLayout api = new VerticalLayout();
                Label caption = new Label("<span>API Documentation</span>",
                        Label.CONTENT_XHTML);
                caption.setStyleName("section");
                caption.setWidth("100%");
                api.addComponent(caption);
                api.setMargin(false, false, true, false);
                for (APIResource r : apis) {
                    final Link l = new Link(r.getName(), r);
                    l.setIcon(new ThemeResource(
                            "../runo/icons/16/document-txt.png"));
                    api.addComponent(l);
                }
                right.addComponent(api);
            }

            Class[] features = feature.getRelatedFeatures();
            if (features != null) {
                VerticalLayout rel = new VerticalLayout();
                Label caption = new Label("<span>Related Samples</span>",
                        Label.CONTENT_XHTML);
                caption.setStyleName("section");
                caption.setWidth("100%");
                rel.addComponent(caption);
                rel.setMargin(false, false, true, false);
                for (Class c : features) {
                    final Feature f = SamplerApplication.getFeatureFor(c);
                    if (f != null) {
                        String path = SamplerApplication.getPathFor(f);
                        ActiveLink al = new ActiveLink(f.getName(),
                                new ExternalResource(getApplication().getURL()
                                        + "#" + path));
                        al.setIcon(new ThemeResource(
                                (f instanceof FeatureSet ? "icons/category.gif"
                                        : "icons/sample.png")));
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
