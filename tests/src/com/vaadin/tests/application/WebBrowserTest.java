package com.vaadin.tests.application;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class WebBrowserTest extends TestBase {

    @Override
    protected void setup() {

        final Label offsetLabel = new Label("n/a");
        offsetLabel.setCaption("Browser offset");

        final Label rawOffsetLabel = new Label("n/a");
        rawOffsetLabel.setCaption("Browser raw offset");

        final Label diffLabel = new Label("n/a");
        diffLabel.setCaption("Browser/server offset difference");

        final Label containsLabel = new Label("n/a");
        containsLabel.setCaption("Browser TimeZones include server TimeZone");

        final Button update = new Button("Get TimeZone from browser",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        TimeZone serverTZ = Calendar.getInstance()
                                .getTimeZone();
                        int serverOffset = serverTZ.getOffset(new Date()
                                .getTime());

                        int browserOffset = getBrowser().getTimezoneOffset();
                        int browserRawOffset = getBrowser()
                                .getRawTimezoneOffset();
                        String[] tzs = TimeZone
                                .getAvailableIDs(browserRawOffset);

                        boolean contains = Arrays.asList(tzs).contains(
                                serverTZ.getID());

                        offsetLabel.setValue(String.valueOf(browserOffset));

                        rawOffsetLabel.setValue(String
                                .valueOf(browserRawOffset));

                        diffLabel.setValue(String.valueOf(browserOffset
                                - serverOffset));

                        containsLabel.setValue(contains ? "Yes" : "No");
                    }
                });

        addComponent(update);
        addComponent(offsetLabel);
        addComponent(rawOffsetLabel);
        addComponent(diffLabel);
        addComponent(containsLabel);

    }

    @Override
    protected String getDescription() {
        return "Verifies that browser TimeZone offset works - should be same as server in our case (NOTE assumes server+browser in same TZ)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6691;
    }

}
