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
import org.junit.Assert;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;

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

    protected String write(T object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Design.write(object, outputStream);
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

        if (o1 instanceof Collection && o2 instanceof Collection) {

        } else {
            Assert.assertEquals(o1.getClass(), o2.getClass());
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
        return c.getPackage().getName().startsWith("com.vaadin");
    }

    public void testRead(String design, T expected) {
        assertEquals(expected, read(design));
    }

    public void testWrite(String design, T expected) {
        String written = write(expected);

        Element producedElem = Jsoup.parse(written).body().child(0);
        Element comparableElem = Jsoup.parse(design).body().child(0);

        String produced = elementToHtml(producedElem);
        String comparable = elementToHtml(comparableElem);

        Assert.assertEquals(comparable, produced);
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
        for (Element child : producedElem.children()) {
            elementToHtml(child, sb);
        }
        sb.append("</").append(producedElem.tagName()).append(">");
        return sb.toString();
    }

}
