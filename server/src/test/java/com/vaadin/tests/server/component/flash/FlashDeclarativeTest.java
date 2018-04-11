package com.vaadin.tests.server.component.flash;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Flash;

/**
 * Tests declarative support for implementations of {@link AbstractEmbedded} and
 * {@link Embedded}.
 *
 * @author Vaadin Ltd
 */
public class FlashDeclarativeTest extends DeclarativeTestBase<Flash> {

    protected Flash getExpectedResult() {
        Flash f = new Flash();
        f.setArchive("arch");
        f.setCodebase("foo");
        f.setCodetype("bar");
        f.setStandby("Please wait");
        f.setParameter("foo", "bar");
        f.setParameter("baz", "foo");
        return f;
    };

    protected String getDesign() {
        return "<vaadin-flash standby='Please wait' archive='arch' codebase='foo' codetype='bar' >"
                + "  <parameter name='baz' value='foo' />\n" //
                + "  <parameter name='foo' value='bar' />\n" //
                + "</vaadin-flash>"; //
    }

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
        testRead("<vaadin-flash />", new Flash());
    }

}
