package com.vaadin.tests.components.link;

import java.util.LinkedHashMap;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Link;

public class LinkTest extends AbstractComponentTest<Link> {

    private Command<Link, Resource> linkTargetCommand = new Command<Link, Resource>() {

        @Override
        public void execute(Link c, Resource value, Object data) {
            c.setResource(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createTargetSelect(CATEGORY_FEATURES);
    }

    private void createTargetSelect(String category) {
        LinkedHashMap<String, Resource> options = new LinkedHashMap<>();
        options.put("-", null);
        options.put("https://vaadin.com",
                new ExternalResource("https://vaadin.com"));
        options.put("32x32 theme icon", ICON_32_ATTENTION_PNG_CACHEABLE);
        options.put("linktest-target.html",
                new ClassResource("linktest-target.html"));

        createSelectAction("Link target", category, options,
                "https://vaadin.com", linkTargetCommand, null);
    }

    @Override
    protected Class<Link> getTestClass() {
        return Link.class;
    }
}
