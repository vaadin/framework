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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.vaadin.sass.internal.ScssStylesheet;

public class SassCompiler {

    public static void main(String[] args) throws Exception {
        String input = null;
        String output = null;
        if (args.length < 1 || args.length > 2) {
            System.out
                    .println("usage: SassCompile <scss file to compile> <css file to write>");
            return;
        }

        File in = new File(args[0]);
        if (!in.canRead()) {
            System.err.println(in.getCanonicalPath() + " could not be read!");
            return;
        }
        input = in.getCanonicalPath();

        if (args.length == 2) {
            output = args[1];
        }

        // You can set the resolver; if none is set, VaadinResolver will be used
        // ScssStylesheet.setStylesheetResolvers(new VaadinResolver());

        ScssStylesheet scss = ScssStylesheet.get(input);
        if (scss == null) {
            System.err.println("The scss file " + input
                    + " could not be found.");
            return;
        }

        scss.compile();
        if (output == null) {
            System.out.println(scss.toString());
        } else {
            writeFile(output, scss.toString());
        }
    }

    public static void writeFile(String filename, String output)
            throws IOException {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        writer.write(output);
        writer.close();
    }
}
