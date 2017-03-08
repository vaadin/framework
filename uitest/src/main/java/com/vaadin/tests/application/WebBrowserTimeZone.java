/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.application;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

public class WebBrowserTimeZone extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        final Label offsetLabel = addLabel("Browser offset");
        final Label rawOffsetLabel = addLabel("Browser raw offset");
        final Label dstDiffLabel = addLabel(
                "Difference between raw offset and DST");
        final Label dstInEffectLabel = addLabel("Is DST currently active?");
        final Label curDateLabel = addLabel("Current date in the browser");
        final Label diffLabel = addLabel(
                "Browser to Europe/Helsinki offset difference");
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
                dstDiffLabel
                        .setValue(String.valueOf(getBrowser().getDSTSavings()));
                dstInEffectLabel
                        .setValue(getBrowser().isDSTInEffect() ? "Yes" : "No");
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
