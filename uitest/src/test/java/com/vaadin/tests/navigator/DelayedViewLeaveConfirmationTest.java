package com.vaadin.tests.navigator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DelayedViewLeaveConfirmationTest extends SingleBrowserTest {

    @Test
    public void navigateAwayWithoutChanges() {
        openMainView();
        navigateToOtherView();
        assertOnOtherView();
    }

    @Test
    public void cancelNavigateAwayWithChanges() {
        openMainView();
        updateValue();
        navigateToOtherView();
        assertOnMainView();
        chooseToStay();
        assertOnMainView();
    }

    @Test
    public void confirmNavigateAwayWithChanges() {
        openMainView();
        updateValue();
        navigateToOtherView();
        assertOnMainView();
        chooseToLeave();
        assertOnOtherView();
    }

    @Test
    public void confirmLogoutWithChanges() {
        openMainView();
        updateValue();
        logout();
        assertOnMainView();
        chooseToLeave();
        assertLoggedOut();
    }

    @Test
    public void cancelLogoutWithChanges() {
        openMainView();
        updateValue();
        logout();
        assertOnMainView();
        chooseToStay();
        assertOnMainView();
    }

    @Test
    public void logoutWithoutChanges() {
        openMainView();
        getLogout().click();
        assertLoggedOut();

    }

    private void openMainView() {
        String url = getTestURL(DelayedViewLeaveConfirmation.class);
        url += "#!main";

        driver.get(url);
    }

    private void navigateToOtherView() {
        getNavigateAway().click();
    }

    private void logout() {
        getLogout().click();
    }

    private void assertOnOtherView() {
        assertEquals("Just another view",
                $(LabelElement.class).first().getText());
    }

    private void assertOnMainView() {
        assertEquals("Saved value", $(LabelElement.class).first().getCaption());
    }

    private void assertLoggedOut() {
        assertEquals("You have been logged out",
                $(LabelElement.class).first().getText());
    }

    private void chooseToStay() {
        $(WindowElement.class).first().$(ButtonElement.class).id("stay")
                .click();
    }

    private void chooseToLeave() {
        $(WindowElement.class).first().$(ButtonElement.class).id("leave")
                .click();
    }

    private void updateValue() {
        TextFieldElement input = $(TextFieldElement.class).id("input");
        input.setValue(input.getValue() + "-upd");
    }

    private ButtonElement getNavigateAway() {
        return $(ButtonElement.class).id("navigateAway");
    }

    private ButtonElement getLogout() {
        return $(ButtonElement.class).id("logout");
    }
}
