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
package com.vaadin.sass;

import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CustomConsoleHandler extends ConsoleHandler {

    private ConsoleHandler stdoutHandler;

    public CustomConsoleHandler() {
        PrintStream err = System.err;
        /*
         * ConsoleHandler uses System.err to output all messages. Replace
         * System.err temporary to construct ConsoleHandler and set it back
         * after construction.
         */
        System.setErr(System.out);
        stdoutHandler = new ConsoleHandler();
        System.setErr(err);
    }

    @Override
    public void publish(LogRecord record) {
        if (!Level.SEVERE.equals(record.getLevel())) {
            stdoutHandler.publish(record);
        } else {
            super.publish(record);
        }
    }
}
