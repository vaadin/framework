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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

@Push
public class PushFromInit extends AbstractTestUIWithLog {

    public static final String LOG_DURING_INIT = "Logged from access run before init ends";
    public static final String LOG_AFTER_INIT = "Logged from background thread run after init has finished";

    @Override
    protected void setup(VaadinRequest request) {
        log("Logged in init");
        Thread t = new Thread(new RunBeforeInitEnds());
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        new Thread(new RunAfterInit()).start();
        addComponent(new Button("Sync"));
    }

    class RunBeforeInitEnds implements Runnable {
        @Override
        public void run() {
            access(new Runnable() {
                @Override
                public void run() {
                    log(LOG_DURING_INIT);
                }
            });
        }
    }

    class RunAfterInit implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            access(new Runnable() {
                @Override
                public void run() {
                    log(LOG_AFTER_INIT);
                }
            });
        }
    }

    @Override
    protected String getTestDescription() {
        return "Pusing something to a newly created UI should not cause race conditions";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11529);
    }

}
