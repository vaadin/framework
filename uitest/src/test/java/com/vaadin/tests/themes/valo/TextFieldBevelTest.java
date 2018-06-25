package com.vaadin.tests.themes.valo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for $v-textfield-bevel value when $v-bevel is unset.
 *
 * @author Vaadin Ltd
 */
public class TextFieldBevelTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE8();
    }

    @Test
    public void bevelChangesBoxShadow() {
        openTestURL();
        String boxShadowWithBevel = getBoxShadow();

        openTestUrlWithoutBevel();
        String boxShadowWithoutBevel = getBoxShadow();

        assertThat(boxShadowWithBevel, is(not(boxShadowWithoutBevel)));
    }

    private void openTestUrlWithoutBevel() {
        getDriver().get(getTestUrl() + "$"
                + TextFieldBevel.ValoDefaultTextFieldBevel.class.getSimpleName()
                + "?restartApplication");
    }

    private String getBoxShadow() {
        return $(TextFieldElement.class).first().getCssValue("box-shadow");
    }
}
