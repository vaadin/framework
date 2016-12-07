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
package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.ShouldWriteDataDelegate;

public abstract class DeclarativeTestBaseBase<T extends Component> {
    private static final class AlwaysWriteDelegate
            implements ShouldWriteDataDelegate {
        private static final long serialVersionUID = -6345914431997793599L;

        @Override
        public boolean shouldWriteData(Component component) {
            return true;
        }
    }

    public static final ShouldWriteDataDelegate ALWAYS_WRITE_DATA = new AlwaysWriteDelegate();

    public interface EqualsAsserter<TT> {
        public void assertObjectEquals(TT o1, TT o2);
    }

    protected T read(String design) {
        try {
            return (T) Design
                    .read(new ByteArrayInputStream(design.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected DesignContext readAndReturnContext(String design) {
        try {
            return Design.read(
                    new ByteArrayInputStream(design.getBytes("UTF-8")), null);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String write(T object, boolean writeData) {
        DesignContext dc = new DesignContext();
        if (writeData) {
            dc.setShouldWriteDataDelegate(
                    DeclarativeTestBaseBase.ALWAYS_WRITE_DATA);
        }
        return write(object, dc);
    }

    protected String write(T object, DesignContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            context.setRootComponent(object);
            Design.write(context, outputStream);
            return outputStream.toString("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void assertEquals(Object o1, Object o2) {
        assertEquals("", o1, o2);
    }

    protected void assertEquals(String message, Object o1, Object o2) {
        if (o1 == null) {
            Assert.assertNull(message, o2);
            return;
        }
        if (o2 == null) {
            Assert.assertNull(message, o1);
            return;
        }

        if (!(o1 instanceof Collection && o2 instanceof Collection)) {
            Assert.assertEquals(o1.getClass(), o2.getClass());
        }

        if (o1 instanceof Object[]) {
            Object[] a1 = ((Object[]) o1);
            Object[] a2 = ((Object[]) o2);
            Assert.assertEquals(message + ": array length", a1.length,
                    a2.length);
            for (int i = 0; i < a1.length; i++) {
                assertEquals(message + ": element " + i, a1[i], a2[i]);
            }
            return;
        }

        List<EqualsAsserter<Object>> comparators = getComparators(o1);
        if (!comparators.isEmpty()) {
            for (EqualsAsserter<Object> ec : comparators) {
                ec.assertObjectEquals(o1, o2);
            }
        } else {
            Assert.assertEquals(message, o1, o2);
        }
    }

    private List<EqualsAsserter<Object>> getComparators(Object o1) {
        List<EqualsAsserter<Object>> result = new ArrayList<>();
        getComparators(o1.getClass(), result);
        return result;
    }

    private void getComparators(Class<?> c,
            List<EqualsAsserter<Object>> result) {
        if (c == null || !isVaadin(c)) {
            return;
        }
        EqualsAsserter<Object> comparator = (EqualsAsserter<Object>) getComparator(
                c);
        if (c.getSuperclass() != Object.class) {
            getComparators(c.getSuperclass(), result);
        }
        for (Class<?> i : c.getInterfaces()) {
            getComparators(i, result);
        }

        if (!result.contains(comparator)) {
            result.add(comparator);
        }
    }

    protected abstract <TT> EqualsAsserter<TT> getComparator(Class<TT> c);

    private boolean isVaadin(Class<?> c) {
        return c.getPackage() != null
                && c.getPackage().getName().startsWith("com.vaadin");

    }

    public static class TestLogHandler {
        final List<String> messages = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                messages.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {

            }
        };

        public TestLogHandler() {
            Logger.getLogger(AbstractComponent.class.getName()).getParent()
                    .addHandler(handler);
        }

        public String getMessages() {
            if (messages.isEmpty()) {
                return "";
            }

            String r = "";
            for (String message : messages) {
                r += message + "\n";
            }
            return r;
        }

    }

    public T testRead(String design, T expected) {
        TestLogHandler l = new TestLogHandler();
        T read = read(design);
        assertEquals(expected, read);
        Assert.assertEquals("", l.getMessages());
        return read;
    }

    public DesignContext readComponentAndCompare(String design, T expected) {
        TestLogHandler l = new TestLogHandler();
        DesignContext context = readAndReturnContext(design);
        assertEquals(expected, context.getRootComponent());
        Assert.assertEquals("", l.getMessages());
        return context;
    }

    public void testWrite(String expected, T component) {
        TestLogHandler l = new TestLogHandler();
        testWrite(expected, component, false);
        Assert.assertEquals("", l.getMessages());
    }

    public void testWrite(String expectedDesign, T component,
            boolean writeData) {
        String written = write(component, writeData);

        Element producedElem = Jsoup.parse(written).body().child(0);
        Element comparableElem = Jsoup.parse(expectedDesign).body().child(0);

        String produced = elementToHtml(producedElem);
        String comparable = elementToHtml(comparableElem);

        Assert.assertEquals(comparable, produced);
    }

    public void testWrite(T component, String expected, DesignContext context) {
        String written = write(component, context);

        Element producedElem = Jsoup.parse(written).body().child(0);
        Element comparableElem = Jsoup.parse(expected).body().child(0);

        String produced = elementToHtml(producedElem);
        String comparable = elementToHtml(comparableElem);

        Assert.assertEquals(comparable, produced);
    }

    protected Element createElement(Component c) {
        return new DesignContext().createElement(c);
    }

    private String elementToHtml(Element producedElem) {
        StringBuilder stringBuilder = new StringBuilder();
        elementToHtml(producedElem, stringBuilder);
        return stringBuilder.toString();
    }

    /**
     * Produce predictable html (attributes in alphabetical order), always
     * include close tags
     */
    private String elementToHtml(Element producedElem, StringBuilder sb) {
        HashSet<String> booleanAttributes = new HashSet<>();
        ArrayList<String> names = new ArrayList<>();
        for (Attribute a : producedElem.attributes().asList()) {
            names.add(a.getKey());
            if (a instanceof BooleanAttribute) {
                booleanAttributes.add(a.getKey());
            }
        }
        Collections.sort(names);

        sb.append("<").append(producedElem.tagName()).append("");
        for (String attrName : names) {
            sb.append(" ").append(attrName);
            if (!booleanAttributes.contains(attrName)) {
                sb.append("=").append("\'").append(producedElem.attr(attrName))
                        .append("\'");
            }
        }
        sb.append(">");
        for (Node child : producedElem.childNodes()) {
            if (child instanceof Element) {
                elementToHtml((Element) child, sb);
            } else if (child instanceof TextNode) {
                String text = ((TextNode) child).text();
                sb.append(text.trim());
            }
        }
        sb.append("</").append(producedElem.tagName()).append(">");
        return sb.toString();
    }

    protected String stripOptionTags(String design) {
        return design.replaceAll("[ \n]*<option(.*)</option>[ \n]*", "");

    }

}
