package com.vaadin.tests.components.embedded;

import com.vaadin.server.ClassResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Embedded;

/**
 * Tests the legacy flash support in Embedded
 */
@Deprecated
public class FlashIsVisible extends TestBase {

    @Override
    protected void setup() {
        Embedded player = new Embedded();
        player.setType(Embedded.TYPE_OBJECT);
        player.setMimeType("application/x-shockwave-flash");
        player.setWidth("400px");
        player.setHeight("300px");
        player.setSource(new ClassResource(
                com.vaadin.tests.components.flash.FlashIsVisible.class,
                "simple.swf"));
        addComponent(player);
    }

    @Override
    protected String getDescription() {
        return "Flash plugin should load and be visible on all browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6501;
    }

}
