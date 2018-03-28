package com.vaadin.v7.tests.server.component.abstractselect;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupDeclarativeTest
        extends DeclarativeTestBase<OptionGroup> {

    private OptionGroup og;

    @Before
    public void init() {
        og = new OptionGroup();
    }

    @Test
    public void testBasicSyntax() {

        String expected = "<vaadin7-option-group />";
        testReadWrite(expected);

    }

    @Test
    public void testOptionSyntax() {

        og.addItems("foo", "bar", "baz", "bang");

        //@formatter:off
        String expected =
                "<vaadin7-option-group>"
                + "<option>foo</option>"
                + "<option>bar</option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</vaadin7-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testDisabledOptionSyntax() {

        og.addItems("foo", "bar", "baz", "bang");
        og.setItemEnabled("baz", false);

        //@formatter:off
        String expected =
                "<vaadin7-option-group>"
                + "<option>foo</option>"
                + "<option>bar</option>"
                + "<option disabled=''>baz</option>"
                + "<option>bang</option>"
                + "</vaadin7-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testIconSyntax() {

        og.addItems("foo", "bar", "baz", "bang");
        og.setItemIcon("bar", new ThemeResource("foobar.png"));

        //@formatter:off
        String expected =
                "<vaadin7-option-group>"
                + "<option>foo</option>"
                + "<option icon='theme://foobar.png'>bar</option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</vaadin7-option-group>";
        //@formatter:on

        testReadWrite(expected);

    }

    @Test
    public void testHTMLCaption() {

        og.addItems("foo", "bar", "baz", "bang");

        og.setHtmlContentAllowed(true);

        og.setItemCaption("foo", "<b>True</b>");
        og.setItemCaption("bar", "<font color='red'>False</font>");

        //@formatter:off
        String expected =
                "<vaadin7-option-group html-content-allowed>"
                + "<option item-id=\"foo\"><b>True</b></option>"
                + "<option item-id=\"bar\"><font color='red'>False</font></option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</vaadin7-option-group>";
        //@formatter:on

        testReadWrite(expected);
    }

    @Test
    public void testPlaintextCaption() {

        og.addItems("foo", "bar", "baz", "bang");

        og.setItemCaption("foo", "<b>True</b>");
        og.setItemCaption("bar", "<font color=\"red\">False</font>");

        //@formatter:off
        String expected =
                "<vaadin7-option-group>"
                + "<option item-id=\"foo\">&lt;b&gt;True&lt;/b&gt;</option>"
                + "<option item-id=\"bar\">&lt;font color=\"red\"&gt;False&lt;/font&gt;</option>"
                + "<option>baz</option>"
                + "<option>bang</option>"
                + "</vaadin7-option-group>";
        //@formatter:on

        testReadWrite(expected);
    }

    private void testReadWrite(String design) {
        testWrite(design, og, true);
        testRead(design, og);
    }

    @Override
    public OptionGroup testRead(String design, OptionGroup expected) {

        OptionGroup read = super.testRead(design, expected);
        testWrite(design, read, true);

        return read;
    }

}
