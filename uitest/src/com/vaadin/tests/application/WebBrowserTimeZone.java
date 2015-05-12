package com.vaadin.tests.application;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import com.vaadin.server.VaadinRequest;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@RunLocally
public class WebBrowserTimeZone extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Label offsetLabel = addLabel("Browser offset");
        final Label rawOffsetLabel = addLabel("Browser raw offset");
        final Label dstDiffLabel = addLabel("Difference between raw offset and DST");
        final Label dstInEffectLabel = addLabel("Is DST currently active?");
        final Label curDateLabel = addLabel("Current date in the browser");
        final Label diffLabel = addLabel("Browser to Europe/Helsinki offset difference");
        final Label containsLabel = addLabel("Browser could be in Helsinki");

        addButton("Get TimeZone from browser", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                TimeZone hkiTZ = TimeZone.getTimeZone("Europe/Helsinki");
                int hkiOffset = hkiTZ.getOffset(new Date().getTime());

                int browserOffset = getBrowser().getTimezoneOffset();
                int browserRawOffset = getBrowser().getRawTimezoneOffset();
                String[] tzs = TimeZone.getAvailableIDs(browserRawOffset);

                boolean contains = Arrays.asList(tzs).contains(hkiTZ.getID());

                offsetLabel.setValue(String.valueOf(browserOffset));
                rawOffsetLabel.setValue(String.valueOf(browserRawOffset));
                diffLabel.setValue(String.valueOf(browserOffset - hkiOffset));
                containsLabel.setValue(contains ? "Yes" : "No");
                dstDiffLabel.setValue(String.valueOf(getBrowser()
                        .getDSTSavings()));
                dstInEffectLabel.setValue(getBrowser().isDSTInEffect() ? "Yes"
                        : "No");
                curDateLabel.setValue(getBrowser().getCurrentDate().toString());
            }
        });
    }

    private Label addLabel(String caption) {
        final Label label = new Label("n/a");
        label.setCaption(caption);
        addComponent(label);
        return label;
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that browser TimeZone offset works - should be same as server in our case (NOTE assumes server+browser in same TZ)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6691;
    }

}
