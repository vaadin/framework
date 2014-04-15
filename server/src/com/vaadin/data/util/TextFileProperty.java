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

package com.vaadin.data.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * Property implementation for wrapping a text file.
 * 
 * Supports reading and writing of a File from/to String.
 * 
 * {@link ValueChangeListener}s are supported, but only fire when
 * setValue(Object) is explicitly called. {@link ReadOnlyStatusChangeListener}s
 * are supported but only fire when setReadOnly(boolean) is explicitly called.
 * 
 */
@SuppressWarnings("serial")
public class TextFileProperty extends AbstractProperty<String> {

    private File file;
    private Charset charset = null;

    /**
     * Wrap given file with property interface.
     * 
     * Setting the file to null works, but getValue() will return null.
     * 
     * @param file
     *            File to be wrapped.
     */
    public TextFileProperty(File file) {
        this.file = file;
    }

    /**
     * Wrap the given file with the property interface and specify character
     * set.
     * 
     * Setting the file to null works, but getValue() will return null.
     * 
     * @param file
     *            File to be wrapped.
     * @param charset
     *            Charset to be used for reading and writing the file.
     */
    public TextFileProperty(File file, Charset charset) {
        this.file = file;
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public String getValue() {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = charset == null ? new InputStreamReader(fis)
                    : new InputStreamReader(fis, charset);
            BufferedReader r = new BufferedReader(isr);
            StringBuilder b = new StringBuilder();
            char buf[] = new char[8 * 1024];
            int len;
            while ((len = r.read(buf)) != -1) {
                b.append(buf, 0, len);
            }
            r.close();
            isr.close();
            fis.close();
            return b.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return file == null || super.isReadOnly() || !file.canWrite();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String newValue) throws ReadOnlyException {
        if (isReadOnly()) {
            throw new ReadOnlyException();
        }
        if (file == null) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = charset == null ? new OutputStreamWriter(
                    fos) : new OutputStreamWriter(fos, charset);
            BufferedWriter w = new BufferedWriter(osw);
            w.append(newValue.toString());
            w.flush();
            w.close();
            osw.close();
            fos.close();
            fireValueChange();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
