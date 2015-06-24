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
package com.vaadin.tests.server.renderer;

import com.vaadin.data.Item;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.server.component.grid.TestGrid;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import elemental.json.JsonValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class RendererTest {

    private static class TestBean {
        int i = 42;

        @Override
        public String toString() {
            return "TestBean [" + i + "]";
        }
    }

    private static class ExtendedBean extends TestBean {
        float f = 3.14f;
    }

    private static class TestRenderer extends TextRenderer {
        @Override
        public JsonValue encode(String value) {
            return super.encode("renderer(" + value + ")");
        }
    }

    private static class TestConverter implements Converter<String, TestBean> {

        @Override
        public TestBean convertToModel(String value,
                Class<? extends TestBean> targetType, Locale locale)
                throws ConversionException {
            return null;
        }

        @Override
        public String convertToPresentation(TestBean value,
                Class<? extends String> targetType, Locale locale)
                throws ConversionException {
            if (value instanceof ExtendedBean) {
                return "ExtendedBean(" + value.i + ", "
                        + ((ExtendedBean) value).f + ")";
            } else {
                return "TestBean(" + value.i + ")";
            }
        }

        @Override
        public Class<TestBean> getModelType() {
            return TestBean.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }

    private Grid grid;

    private Column intColumn;
    private Column textColumn;
    private Column beanColumn;
    private Column htmlColumn;
    private Column numberColumn;
    private Column dateColumn;
    private Column extendedBeanColumn;
    private Column buttonColumn;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));

        IndexedContainer c = new IndexedContainer();

        c.addContainerProperty("int", Integer.class, 0);
        c.addContainerProperty("text", String.class, "");
        c.addContainerProperty("html", String.class, "");
        c.addContainerProperty("number", Number.class, null);
        c.addContainerProperty("date", Date.class, null);
        c.addContainerProperty("bean", TestBean.class, null);
        c.addContainerProperty("button", String.class, null);
        c.addContainerProperty("extendedBean", ExtendedBean.class, null);

        Object id = c.addItem();
        Item item = c.getItem(id);
        item.getItemProperty("int").setValue(123);
        item.getItemProperty("text").setValue("321");
        item.getItemProperty("html").setValue("<b>html</b>");
        item.getItemProperty("number").setValue(3.14);
        item.getItemProperty("date").setValue(new Date(123456789));
        item.getItemProperty("bean").setValue(new TestBean());
        item.getItemProperty("extendedBean").setValue(new ExtendedBean());

        grid = new TestGrid(c);

        intColumn = grid.getColumn("int");
        textColumn = grid.getColumn("text");
        htmlColumn = grid.getColumn("html");
        numberColumn = grid.getColumn("number");
        dateColumn = grid.getColumn("date");
        beanColumn = grid.getColumn("bean");
        extendedBeanColumn = grid.getColumn("extendedBean");
        buttonColumn = grid.getColumn("button");

    }

    @Test
    public void testDefaultRendererAndConverter() throws Exception {
        assertSame(TextRenderer.class, intColumn.getRenderer().getClass());
        assertSame(StringToIntegerConverter.class, intColumn.getConverter()
                .getClass());

        assertSame(TextRenderer.class, textColumn.getRenderer().getClass());
        // String->String; converter not needed
        assertNull(textColumn.getConverter());

        assertSame(TextRenderer.class, beanColumn.getRenderer().getClass());
        // MyBean->String; converter not found
        assertNull(beanColumn.getConverter());
    }

    @Test
    public void testFindCompatibleConverter() throws Exception {
        intColumn.setRenderer(renderer());
        assertSame(StringToIntegerConverter.class, intColumn.getConverter()
                .getClass());

        textColumn.setRenderer(renderer());
        assertNull(textColumn.getConverter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotFindConverter() {
        beanColumn.setRenderer(renderer());
    }

    @Test
    public void testExplicitConverter() throws Exception {
        beanColumn.setRenderer(renderer(), converter());
        extendedBeanColumn.setRenderer(renderer(), converter());
    }

    @Test
    public void testEncoding() throws Exception {
        assertEquals("42", render(intColumn, 42).asString());
        intColumn.setRenderer(renderer());
        assertEquals("renderer(42)", render(intColumn, 42).asString());

        assertEquals("2.72", render(textColumn, "2.72").asString());
        textColumn.setRenderer(new TestRenderer());
        assertEquals("renderer(2.72)", render(textColumn, "2.72").asString());
    }

    @Test
    public void testEncodingWithoutConverter() throws Exception {
        assertEquals("TestBean [42]", render(beanColumn, new TestBean())
                .asString());
    }

    @Test
    public void testBeanEncoding() throws Exception {
        beanColumn.setRenderer(renderer(), converter());
        extendedBeanColumn.setRenderer(renderer(), converter());

        assertEquals("renderer(TestBean(42))",
                render(beanColumn, new TestBean()).asString());
        assertEquals("renderer(ExtendedBean(42, 3.14))",
                render(beanColumn, new ExtendedBean()).asString());

        assertEquals("renderer(ExtendedBean(42, 3.14))",
                render(extendedBeanColumn, new ExtendedBean()).asString());
    }

    @Test
    public void testNullEncoding() {

        textColumn.setRenderer(new TextRenderer());
        htmlColumn.setRenderer(new HtmlRenderer());
        numberColumn.setRenderer(new NumberRenderer());
        dateColumn.setRenderer(new DateRenderer());
        buttonColumn.setRenderer(new ButtonRenderer());

        assertEquals("", textColumn.getRenderer().encode(null).asString());
        assertEquals("", htmlColumn.getRenderer().encode(null).asString());
        assertEquals("", numberColumn.getRenderer().encode(null).asString());
        assertEquals("", dateColumn.getRenderer().encode(null).asString());
        assertEquals("", buttonColumn.getRenderer().encode(null).asString());
    }

    @Test
    public void testNullEncodingWithDefault() {

        textColumn.setRenderer(new TextRenderer("default value"));
        htmlColumn.setRenderer(new HtmlRenderer("default value"));
        numberColumn.setRenderer(new NumberRenderer("%s", Locale.getDefault(),
                "default value"));
        dateColumn.setRenderer(new DateRenderer("%s", "default value"));
        buttonColumn.setRenderer(new ButtonRenderer("default value"));

        assertEquals("default value", textColumn.getRenderer().encode(null)
                .asString());
        assertEquals("default value", htmlColumn.getRenderer().encode(null)
                .asString());
        assertEquals("default value", numberColumn.getRenderer().encode(null)
                .asString());
        assertEquals("default value", dateColumn.getRenderer().encode(null)
                .asString());
        assertEquals("default value", buttonColumn.getRenderer().encode(null)
                .asString());
    }

    private TestConverter converter() {
        return new TestConverter();
    }

    private TestRenderer renderer() {
        return new TestRenderer();
    }

    private JsonValue render(Column column, Object value) {
        return RpcDataProviderExtension.encodeValue(value,
                column.getRenderer(), column.getConverter(), grid.getLocale());
    }
}
