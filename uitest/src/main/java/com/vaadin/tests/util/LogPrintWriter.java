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
package com.vaadin.tests.util;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Use for collecting HTTP response.
 *
 */
public class LogPrintWriter extends PrintWriter {

    private final StringBuffer result = new StringBuffer(256);

    public LogPrintWriter(Writer out) {
        super(out);
    }

    @Override
    public void print(String s) {
        result.append(s);
        super.print(s);
    }

    @Override
    public void write(String s) {
        result.append(s);
        super.write(s);
    }

    public String getResult() {
        return result.toString();
    }

}
