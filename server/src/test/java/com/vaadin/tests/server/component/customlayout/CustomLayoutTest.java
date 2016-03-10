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
package com.vaadin.tests.server.component.customlayout;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.CustomLayout;

/**
 * 
 * Tests for {@link CustomLayout}
 * 
 * @author Vaadin Ltd
 */
public class CustomLayoutTest {

    @Test
    public void ctor_inputStreamProvided_inputStreamIsRead()
            throws IOException, IllegalArgumentException,
            IllegalAccessException {
        Integer buffer = getBufferSize();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < buffer; i++) {
            builder.append('a');
        }
        byte[] bytes = builder.toString().getBytes(Charset.forName("UTF-8"));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        InputStreamImpl stream = new InputStreamImpl(inputStream, buffer / 2);
        new CustomLayout(stream);

        Assert.assertTrue("Stream is not closed in CustomLayout CTOR ",
                stream.isClosed());
        Assert.assertEquals("Number of read bytes is incorrect", bytes.length,
                stream.getCount());
    }

    private Integer getBufferSize() throws IllegalAccessException {
        Field[] fields = CustomLayout.class.getDeclaredFields();
        List<Field> list = new ArrayList<Field>(fields.length);
        for (Field field : fields) {
            if ((field.getModifiers() & Modifier.STATIC) > 0) {
                list.add(field);
            }
        }
        Field field = null;
        if (list.size() == 1) {
            field = list.get(0);
        } else {
            for (Field fld : list) {
                if (fld.getName().toLowerCase(Locale.ENGLISH)
                        .startsWith("buffer")) {
                    field = fld;
                    break;
                }
            }
        }
        Assert.assertNotNull(
                "Unable to find default buffer size in CustomLayout class",
                field);
        field.setAccessible(true);
        Integer buffer = (Integer) field.get(null);
        return buffer;
    }

    private static class InputStreamImpl extends FilterInputStream {

        InputStreamImpl(InputStream inputStream, int maxArrayLength) {
            super(inputStream);
            this.maxArrayLength = maxArrayLength;
        }

        @Override
        public int read() throws IOException {
            int read = super.read();
            if (read != -1) {
                readCount++;
            }
            return read;
        }

        @Override
        public int read(byte[] b) throws IOException {
            if (b.length > maxArrayLength) {
                return read(b, 0, maxArrayLength);
            }
            int count = super.read(b);
            if (count != -1) {
                readCount += count;
            }
            return count;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len > maxArrayLength) {
                return read(b, off, maxArrayLength);
            }
            int count = super.read(b, off, len);
            if (count != -1) {
                readCount += count;
            }
            return count;
        }

        @Override
        public void close() throws IOException {
            isClosed = true;
            super.close();
        }

        int getCount() {
            return readCount;
        }

        boolean isClosed() {
            return isClosed;
        }

        private int readCount;
        private boolean isClosed;
        private int maxArrayLength;
    }
}
