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
package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.junit.Assert;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.ShouldWriteDataDelegate;

public abstract class DeclarativeTestBaseBase<T extends Component> {
    public interface EqualsAsserter<TT> {
        public void assertObjectEquals(TT o1, TT o2);
    }

    protected T read(String design) {
        try {
            return (T) Design.read(new ByteArrayInputStream(design
                    .getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String write(T object, boolean writeData) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            DesignContext dc = new DesignContext();
            if (writeData) {
                dc.setShouldWriteDataDelegate(new ShouldWriteDataDelegate() {
                    @Override
                    public boolean shouldWriteData(Component component) {
                        return true;
                    }
                });
            }
            dc.setRootComponent(object);
            Design.write(dc, outputStream);
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
            Assert.assertEquals(message, null, o2);
            return;
        }
        if (o2 == null) {
            Assert.assertEquals(message, null, o1);
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
        List<EqualsAsserter<Object>> result = new ArrayList<EqualsAsserter<Object>>();
        getComparators(o1.getClass(), result);
        return result;
    }

    private void getComparators(Class<?> c, List<EqualsAsserter<Object>> result) {
        if (c == null || !isVaadin(c)) {
            return;
        }
        EqualsAsserter<Object> comparator = (EqualsAsserter<Object>) getComparator(c);
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

    public T testRead(String design, T expected) {
        T read = read(design);
        assertEquals(expected, read);
        return read;
    }

    public void testWrite(String design, T expected) {
        testWrite(design, expected, false);
    }

    public void testWrite(String design, T expected, boolean writeData) {
        String written = write(expected, writeData);

        Element producedElem = Jsoup.parse(written).body().child(0);
        Element comparableElem = Jsoup.parse(design).body().child(0);

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
        ArrayList<String> names = new ArrayList<String>();
        for (Attribute a : producedElem.attributes().asList()) {
            names.add(a.getKey());
        }
        Collections.sort(names);

        sb.append("<" + producedElem.tagName() + "");
        for (String attrName : names) {
            sb.append(" ").append(attrName).append("=").append("\'")
                    .append(producedElem.attr(attrName)).append("\'");
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
