package com.vaadin.v7.tests.server.renderer;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.ImageRenderer;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

public class ImageRendererTest {

    private ImageRenderer renderer;

    @Before
    public void setUp() {
        UI mockUI = EasyMock.createNiceMock(UI.class);
        EasyMock.replay(mockUI);

        Grid grid = new Grid();
        grid.setParent(mockUI);

        renderer = new ImageRenderer();
        renderer.setParent(grid);
    }

    @Test
    public void testThemeResource() {
        JsonValue v = renderer.encode(new ThemeResource("foo.png"));
        assertEquals("theme://foo.png", getUrl(v));
    }

    @Test
    public void testExternalResource() {
        JsonValue v = renderer
                .encode(new ExternalResource("http://example.com/foo.png"));
        assertEquals("http://example.com/foo.png", getUrl(v));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileResource() {
        renderer.encode(new FileResource(new File("/tmp/foo.png")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassResource() {
        renderer.encode(new ClassResource("img/foo.png"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFontIcon() {
        renderer.encode(FontAwesome.AMBULANCE);
    }

    private String getUrl(JsonValue v) {
        return ((JsonObject) v).get("uRL").asString();
    }
}
