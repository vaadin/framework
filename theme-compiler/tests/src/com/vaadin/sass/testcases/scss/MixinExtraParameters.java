package com.vaadin.sass.testcases.scss;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.sass.AbstractTestBase;
import com.vaadin.sass.internal.ScssStylesheet;

public class MixinExtraParameters extends AbstractTestBase {
    String scss = "/scss/mixin-extra-params.scss";

    @Test
    public void testCompiler() {
        ScssStylesheet sheet;
        try {
            sheet = getStyleSheet(scss);
            sheet.compile();
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(),
                    "More parameters than expected, in Mixin test");
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
