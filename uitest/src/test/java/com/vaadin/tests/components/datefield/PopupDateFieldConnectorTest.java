package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import com.vaadin.tests.legacyelements.LegacyDateFieldElement;
import com.vaadin.tests.legacyelements.LegacyPopupDateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class PopupDateFieldConnectorTest extends SingleBrowserTest {

    @Test
    public void popupDateFieldElementIsLocated() {
        openTestURL();

        assertThat($(LegacyPopupDateFieldElement.class).all().size(), is(1));
        assertThat($(LegacyDateFieldElement.class).all().size(), is(2));
    }
}