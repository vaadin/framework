/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.tests.push;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;

public class TogglePush extends AbstractTestUI {
    public static class TogglePushInInitTB3 extends MultiBrowserTest {
        @Override
        protected boolean isPushEnabled() {
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.vaadin.tests.tb3.AbstractTB3Test#getTestUrl()
         */
        @Override
        protected String getTestUrl() {
            return null;
        }

        @Test
        public void togglePushInInit() {
            String baseUrl = getBaseURL();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }

            String url = baseUrl + getDeploymentPath();

            // Open with push disabled
            driver.get(addParameter(url, "push=disabled"));

            Assert.assertFalse(getPushToggle().isSelected());

            getDelayedCounterUpdateButton().click();
            sleep(2000);
            Assert.assertEquals("Counter has been updated 0 times",
                    getCounterText());

            // Open with push enabled
            driver.get(addParameter(url, "push=enabled"));
            Assert.assertTrue(getPushToggle().isSelected());

            getDelayedCounterUpdateButton().click();
            sleep(2000);
            Assert.assertEquals("Counter has been updated 1 times",
                    getCounterText());

        }

        /**
         * @since
         * @param url
         * @param string
         * @return
         */
        private String addParameter(String url, String queryParameter) {
            if (url.contains("?")) {
                return url + "&" + queryParameter;
            } else {
                return url + "?" + queryParameter;
            }
        }

        private String getCounterText() {
            return vaadinElement(
                    "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                    .getText();
        }

        private WebElement getPushToggle() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VCheckBox[0]/domChild[0]");
        }

        private WebElement getDelayedCounterUpdateButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[3]/VButton[0]/domChild[0]/domChild[0]");
        }

    }

    public static class TogglePushTB3 extends MultiBrowserTest {

        @Override
        protected boolean isPushEnabled() {
            return true;
        }

        @Test
        public void togglePush() {
            getDelayedCounterUpdateButton().click();
            sleep(2000);

            // Push is enabled, so text gets updated
            Assert.assertEquals("Counter has been updated 1 times",
                    getCounterText());

            // Disable push
            getPushToggle().click();
            getDelayedCounterUpdateButton().click();
            sleep(2000);
            // Push is disabled, so text is not updated
            Assert.assertEquals("Counter has been updated 1 times",
                    getCounterText());

            getDirectCounterUpdateButton().click();
            // Direct update is visible, and includes previous update
            Assert.assertEquals("Counter has been updated 3 times",
                    getCounterText());

            // Re-enable push
            getPushToggle().click();
            getDelayedCounterUpdateButton().click();
            sleep(2000);

            // Push is enabled again, so text gets updated
            Assert.assertEquals("Counter has been updated 4 times",
                    getCounterText());
        }

        private WebElement getDirectCounterUpdateButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VButton[0]/domChild[0]/domChild[0]");
        }

        private WebElement getPushToggle() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VCheckBox[0]/domChild[0]");
        }

        private String getCounterText() {
            return vaadinElement(
                    "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VLabel[0]")
                    .getText();
        }

        private WebElement getDelayedCounterUpdateButton() {
            return vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[3]/VButton[0]/domChild[0]/domChild[0]");
        }
    }

    private final Label counterLabel = new Label();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        updateCounter();
        addComponent(counterLabel);

        getPushConfiguration()
                .setPushMode(
                        "disabled".equals(request.getParameter("push")) ? PushMode.DISABLED
                                : PushMode.AUTOMATIC);

        CheckBox pushSetting = new CheckBox("Push enabled");
        pushSetting.setValue(Boolean.valueOf(getPushConfiguration()
                .getPushMode().isEnabled()));
        pushSetting.setImmediate(true);
        pushSetting.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (event.getProperty().getValue() == Boolean.TRUE) {
                    getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                } else {
                    getPushConfiguration().setPushMode(PushMode.DISABLED);
                }
            }
        });
        addComponent(pushSetting);

        addComponent(new Button("Update counter now",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        updateCounter();
                    }
                }));

        addComponent(new Button("Update counter in 1 sec",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                access(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateCounter();
                                    }
                                });
                            }
                        }, 1000);
                    }
                }));
    }

    public void updateCounter() {
        counterLabel.setValue("Counter has been updated " + counter++
                + " times");
    }

    @Override
    protected String getTestDescription() {
        return "Basic test for enabling and disabling push on the fly.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11506);
    }

}
