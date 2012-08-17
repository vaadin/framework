/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Timer;

/**
 * Executes the given command {@code delayMs} milliseconds after a call to
 * {@link #trigger()}. Calling {@link #trigger()} again before the command has
 * been executed causes the execution to be rescheduled to {@code delayMs} after
 * the second call.
 * 
 */
public class VLazyExecutor {

    private Timer timer;
    private int delayMs;
    private ScheduledCommand cmd;

    /**
     * @param delayMs
     *            Delay in milliseconds to wait before executing the command
     * @param cmd
     *            The command to execute
     */
    public VLazyExecutor(int delayMs, ScheduledCommand cmd) {
        this.delayMs = delayMs;
        this.cmd = cmd;
    }

    /**
     * Triggers execution of the command. Each call reschedules any existing
     * execution to {@link #delayMs} milliseconds from that point in time.
     */
    public void trigger() {
        if (timer == null) {
            timer = new Timer() {
                @Override
                public void run() {
                    timer = null;
                    cmd.execute();
                }
            };
        }
        // Schedule automatically cancels any old schedule
        timer.schedule(delayMs);

    }

}
