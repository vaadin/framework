package com.vaadin.tests.design;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test reading a design with all components using the legacy prefix.
 */
public class ParseLegacyPrefixTest {

    @Test
    public void allComponentsAreParsed() throws FileNotFoundException {
        DesignContext ctx = Design.read(
                getClass().getResourceAsStream("all-components-legacy.html"),
                null);

        assertThat(ctx, is(not(nullValue())));
        assertThat(ctx.getRootComponent(), is(not(nullValue())));
    }
}
