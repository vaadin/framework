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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.RpcDataProviderExtension;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderer.TextRenderer;

import elemental.json.JsonValue;

public class RendererTest {

    private static class TestBean {
        int i = 42;
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

    private Column foo;
    private Column bar;
    private Column baz;
    private Column bah;

    @Before
    public void setUp() {
        VaadinSession.setCurrent(new AlwaysLockedVaadinSession(null));

        IndexedContainer c = new IndexedContainer();

        c.addContainerProperty("foo", Integer.class, 0);
        c.addContainerProperty("bar", String.class, "");
        c.addContainerProperty("baz", TestBean.class, null);
        c.addContainerProperty("bah", ExtendedBean.class, null);

        Object id = c.addItem();
        Item item = c.getItem(id);
        item.getItemProperty("foo").setValue(123);
        item.getItemProperty("bar").setValue("321");
        item.getItemProperty("baz").setValue(new TestBean());
        item.getItemProperty("bah").setValue(new ExtendedBean());

        UI ui = EasyMock.createNiceMock(UI.class);
        ConnectorTracker ct = EasyMock.createNiceMock(ConnectorTracker.class);
        EasyMock.expect(ui.getConnectorTracker()).andReturn(ct).anyTimes();
        EasyMock.replay(ui, ct);

        grid = new Grid(c);
        grid.setParent(ui);

        foo = grid.getColumn("foo");
        bar = grid.getColumn("bar");
        baz = grid.getColumn("baz");
        bah = grid.getColumn("bah");
    }

    @Test
    public void testDefaultRendererAndConverter() throws Exception {
        assertSame(TextRenderer.class, foo.getRenderer().getClass());
        assertSame(StringToIntegerConverter.class, foo.getConverter()
                .getClass());

        assertSame(TextRenderer.class, bar.getRenderer().getClass());
        // String->String; converter not needed
        assertNull(bar.getConverter());

        assertSame(TextRenderer.class, baz.getRenderer().getClass());
        // MyBean->String; converter not found
        assertNull(baz.getConverter());
    }

    @Test
    public void testFindCompatibleConverter() throws Exception {
        foo.setRenderer(renderer());
        assertSame(StringToIntegerConverter.class, foo.getConverter()
                .getClass());

        bar.setRenderer(renderer());
        assertNull(bar.getConverter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotFindConverter() {
        baz.setRenderer(renderer());
    }

    @Test
    public void testExplicitConverter() throws Exception {
        baz.setRenderer(renderer(), converter());
        bah.setRenderer(renderer(), converter());
    }

    @Test
    public void testEncoding() throws Exception {
        assertEquals("42", render(foo, 42).asString());
        foo.setRenderer(renderer());
        assertEquals("renderer(42)", render(foo, 42).asString());

        assertEquals("2.72", render(bar, "2.72").asString());
        bar.setRenderer(new TestRenderer());
        assertEquals("renderer(2.72)", render(bar, "2.72").asString());
    }

    @Test
    public void testEncodingWithoutConverter() throws Exception {
        assertEquals("", render(baz, new TestBean()).asString());
    }

    @Test
    public void testBeanEncoding() throws Exception {
        baz.setRenderer(renderer(), converter());
        bah.setRenderer(renderer(), converter());

        assertEquals("renderer(TestBean(42))", render(baz, new TestBean())
                .asString());
        assertEquals("renderer(ExtendedBean(42, 3.14))",
                render(baz, new ExtendedBean()).asString());

        assertEquals("renderer(ExtendedBean(42, 3.14))",
                render(bah, new ExtendedBean()).asString());
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
