package com.vaadin.tests.tb3;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.integration.IntegrationTestUI;

public class IntegrationTestXHR extends PrivateTB3Configuration {

    @Test
    public void runTest() throws IOException, AssertionError {
        compareScreen("initial");

        WebElement cell = vaadinElement(getTableCell(getTable(), 0, 1));
        testBenchElement(cell).click(51, 13);

        compareScreen("finland");
    }

    private String getTableCell(String tableLocator, int row, int col) {
        return tableLocator
                + "/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild["
                + row + "]/domChild[" + col + "]/domChild[0]";
    }

    protected String getTable() {
        return "/VVerticalLayout[0]/ChildComponentContainer[0]/VScrollTable[0]";
    }

    @Override
    protected Class<?> getUIClass() {
        return IntegrationTestUI.class;
    }

    @Override
    protected String getPath() {
        return "/demo" + super.getPath();
    }

    @Override
    protected String getBaseURL() {
        return System.getProperty("deployment.url");
    }

    @Override
    protected String getTestName() {
        return super.getTestName() + "-" + System.getProperty("server-name");
    }
}
