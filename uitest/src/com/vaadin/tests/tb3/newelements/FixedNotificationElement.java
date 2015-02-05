package com.vaadin.tests.tb3.newelements;

import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.ServerClass;

@ServerClass("com.vaadin.ui.Notification")
public class FixedNotificationElement extends NotificationElement {
    public String getCaption() {
        WebElement popup = findElement(By.className("popupContent"));
        WebElement caption = popup.findElement(By.tagName("h1"));
        return caption.getText();
    }
}
