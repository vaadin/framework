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

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

@Push
@PreserveOnRefresh
public class RefreshCloseConnection extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        log("Init");
    }

    @Override
    protected void refresh(VaadinRequest request) {
        if (getPushConnection().isConnected()) {
            log("Still connected");
        }
        log("Refresh");
        new Thread() {
            @Override
            public void run() {
                accessSynchronously(new Runnable() {
                    @Override
                    public void run() {
                        log("Push");
                    }
                });
            }
        }.start();
    }

    @Override
    protected String getTestDescription() {
        return "A log row should get pushed after reloading the page";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(14251);
    }

}
