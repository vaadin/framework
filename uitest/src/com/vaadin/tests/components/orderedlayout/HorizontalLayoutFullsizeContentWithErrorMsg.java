package com.vaadin.tests.components.orderedlayout;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class HorizontalLayoutFullsizeContentWithErrorMsg extends AbstractTestUI {

    private static final String FIELD_ID = "f";
    private static final String BUTTON_ID = "b";
    private TextField tf;

    @Override
    protected Integer getTicketNumber() {
        return 12564;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("500px");

        tf = new TextField();
        tf.setId(FIELD_ID);
        tf.setWidth("100%");
        hl.addComponent(tf);
        hl.setExpandRatio(tf, 1);
        hl.setComponentAlignment(tf, Alignment.MIDDLE_CENTER);

        Button toggleError = new Button("Toggle error");
        toggleError.setId(BUTTON_ID);
        toggleError.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                tf.setComponentError(tf.getComponentError() == null ? new UserError(
                        "foo") : null);
            }
        });
        hl.addComponent(toggleError);

        addComponent(hl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "TextField should remain at same level vertically, horizontally width should adjust to fit error indicator.";
    }

    public static class TbTest extends MultiBrowserTest {

        @Test
        public void test() {
            openTestURL();
            WebElement element = getDriver().findElement(By.id(FIELD_ID));
            Point location = element.getLocation();

            WebElement errorToggleButton = getDriver().findElement(
                    By.id(BUTTON_ID));

            errorToggleButton.click();

            Assert.assertEquals(location, element.getLocation());

            errorToggleButton.click();

            Assert.assertEquals(location, element.getLocation());

        }

    }

}
