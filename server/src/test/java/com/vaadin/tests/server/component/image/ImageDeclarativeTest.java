package com.vaadin.tests.server.component.image;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Image;

/**
 * Tests declarative support for implementations of {@link Image}.
 *
 * @author Vaadin Ltd
 */
public class ImageDeclarativeTest extends DeclarativeTestBase<Image> {

    protected String getDesign() {
        return "<vaadin-image source='http://foo.bar/img.png' alt='Some random image from the theme'></vaadin-image>";
    }

    protected Image getExpectedResult() {
        Image i = new Image();
        i.setSource(new ExternalResource("http://foo.bar/img.png"));
        i.setAlternateText("Some random image from the theme");
        return i;
    };

    @Test
    public void read() {
        testRead(getDesign(), getExpectedResult());
    }

    @Test
    public void write() {
        testWrite(getDesign(), getExpectedResult());
    }

    @Test
    public void testEmpty() {
        testRead("<vaadin-image />", new Image());
    }

}
