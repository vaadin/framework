/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@Push(transport = Transport.WEBSOCKET)
public class PushPath extends AbstractTestUI {

    public static final String PUSH_PATH_LABEL_ID = "push-path-label-id";
    public static final String PUSH_PATH_LABEL_TEXT = "Label by push";

    @Override
    protected void setup(VaadinRequest request) {
        // use only websockets
        getPushConfiguration().setFallbackTransport(Transport.WEBSOCKET);

        String pushPath = request.getService().getDeploymentConfiguration()
                .getPushPath();
        String transport = getPushConfiguration().getTransport().name();
        Label pushPathLabel = new Label(String.format(
                "Waiting for push from path '%s' using %s in 3 seconds.",
                pushPath, transport));
        addComponent(pushPathLabel);

        new PushThread().start();
    }

    public class PushThread extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            access(new Runnable() {

                @Override
                public void run() {
                    Label pushLabel = new Label(PUSH_PATH_LABEL_TEXT);
                    pushLabel.setId(PUSH_PATH_LABEL_ID);
                    addComponent(pushLabel);
                }
            });

        }
    }

    @Override
    public Integer getTicketNumber() {
        return 14432;
    }

    @Override
    public String getDescription() {
        return "Push path should be configurable since some servers can't serve both websockets and long polling from same URL.";
    }

}
