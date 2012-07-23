package com.vaadin.tests.application;

import java.util.Arrays;
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

        final Label dstDiffLabel = new Label("n/a");
        dstDiffLabel.setCaption("Difference between raw offset and DST");

        final Label dstInEffectLabel = new Label("n/a");
        dstInEffectLabel.setCaption("Is DST currently active?");

        final Label curDateLabel = new Label("n/a");
        curDateLabel.setCaption("Current date in the browser");

        final Label diffLabel = new Label("n/a");
        diffLabel.setCaption("Browser to Europe/Helsinki offset difference");

        final Label containsLabel = new Label("n/a");
        containsLabel.setCaption("Browser could be in Helsinki");

        final Button update = new Button("Get TimeZone from browser",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        TimeZone hkiTZ = TimeZone
                                .getTimeZone("Europe/Helsinki");
                        int hkiOffset = hkiTZ.getOffset(new Date().getTime());

                        int browserOffset = getBrowser().getTimezoneOffset();
                        int browserRawOffset = getBrowser()
                                .getRawTimezoneOffset();
                        String[] tzs = TimeZone
                                .getAvailableIDs(browserRawOffset);

                        boolean contains = Arrays.asList(tzs).contains(
                                hkiTZ.getID());

                        offsetLabel.setValue(String.valueOf(browserOffset));

                        rawOffsetLabel.setValue(String
                                .valueOf(browserRawOffset));

                        diffLabel.setValue(String.valueOf(browserOffset
                                - hkiOffset));

                        containsLabel.setValue(contains ? "Yes" : "No");

                        dstDiffLabel.setValue(String.valueOf(getBrowser()
                                .getDSTSavings()));

                        dstInEffectLabel
                                .setValue(getBrowser().isDSTInEffect() ? "Yes"
                                        : "No");

                        curDateLabel.setValue(getBrowser().getCurrentDate()
                                .toString());

                    }
                });

        addComponent(update);
        addComponent(offsetLabel);
        addComponent(rawOffsetLabel);
        addComponent(dstDiffLabel);
        addComponent(dstInEffectLabel);
        addComponent(curDateLabel);
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
