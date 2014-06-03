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

package com.vaadin.tests.components.progressindicator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.ProgressIndicator;

public class ProgressBarTest extends AbstractTestUI {

    private Label updatedFromBackround;
    private Thread updateThread = new Thread() {
        @Override
        public void run() {
            Runnable updateTask = new Runnable() {
                @Override
                public void run() {
                    counter++;
                    updateLabel();
                }
            };

            while (true) {
                access(updateTask);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };
    private ProgressBar progressBar;
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        updatedFromBackround = new Label();
        updatedFromBackround.setCaption("Updated from background thread");
        updateLabel();
        addComponent(updatedFromBackround);

        addComponent(new Button("Use ProgressBar", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                useComponent(new ProgressBar());
            }
        }));

        addComponent(new Button("Use ProgressIndicator",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        useComponent(new ProgressIndicator());
                    }
                }));

        addComponent(new Button("Stop background thread",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        stopUpdateThread();
                    }
                }));
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void useComponent(ProgressBar progressBar) {
        if (this.progressBar != null) {
            removeComponent(this.progressBar);
        }
        this.progressBar = progressBar;
        addComponent(progressBar);

        counter = 0;
        updateLabel();
    }

    @Override
    public void detach() {
        super.detach();
        stopUpdateThread();
    }

    private void stopUpdateThread() {
        if (updateThread != null) {
            updateThread.interrupt();
            updateThread = null;
        }
    }

    private void updateLabel() {
        updatedFromBackround.setValue(String.valueOf(counter));
    }

    @Override
    protected String getTestDescription() {
        return "ProgressBar should work just as ProgressIndicator, just without the polling";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11925);
    }

}
