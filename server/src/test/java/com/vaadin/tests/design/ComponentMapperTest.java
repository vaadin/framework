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
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.Design.ComponentFactory;
import com.vaadin.ui.declarative.Design.ComponentMapper;
import com.vaadin.ui.declarative.DesignContext;

public class ComponentMapperTest {
    private static final ComponentMapper defaultMapper = Design
            .getComponentMapper();

    private static final ThreadLocal<ComponentMapper> currentMapper = new ThreadLocal<ComponentMapper>();

    static {
        Design.setComponentMapper(new ComponentMapper() {
            @Override
            public Component tagToComponent(String tag,
                    ComponentFactory componentFactory, DesignContext context) {
                return getActualMapper().tagToComponent(tag, componentFactory,
                        context);
            }

            @Override
            public String componentToTag(Component component,
                    DesignContext context) {
                return getActualMapper().componentToTag(component, context);
            }

            private ComponentMapper getActualMapper() {
                ComponentMapper mapper = currentMapper.get();
                if (mapper == null) {
                    mapper = defaultMapper;
                }
                return mapper;
            }
        });
    }

    private final class CustomComponentMapper extends
            Design.DefaultComponentMapper {
        @Override
        public Component tagToComponent(String tag,
                ComponentFactory componentFactory, DesignContext context) {
            if (tag.startsWith("custom-")) {
                ComponentWithCustomTagName component = (ComponentWithCustomTagName) componentFactory
                        .createComponent(
                                ComponentWithCustomTagName.class.getName(),
                                context);
                component.tagName = tag;
                return component;
            } else {
                return super.tagToComponent(tag, componentFactory, context);
            }
        }

        @Override
        public String componentToTag(Component component, DesignContext context) {
            if (component instanceof ComponentWithCustomTagName) {
                ComponentWithCustomTagName withCustomTagName = (ComponentWithCustomTagName) component;
                return withCustomTagName.tagName;
            } else {
                return super.componentToTag(component, context);
            }
        }
    }

    public static class ComponentWithCustomTagName extends Label {
        private String tagName;
    }

    @Test
    public void testCustomComponentMapperRead() {
        currentMapper.set(new CustomComponentMapper());

        Component component = Design.read(new ByteArrayInputStream(
                "<custom-foobar />".getBytes()));

        Assert.assertTrue("<custom-foobar> should resolve "
                + ComponentWithCustomTagName.class.getSimpleName(),
                component instanceof ComponentWithCustomTagName);
        Assert.assertEquals("custom-foobar",
                ((ComponentWithCustomTagName) component).tagName);
    }

    @Test
    public void testCustomComponentMapperWrite() throws IOException {
        currentMapper.set(new CustomComponentMapper());

        ComponentWithCustomTagName component = new ComponentWithCustomTagName();
        component.tagName = "custom-special";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(component, bos);
        String writtenDesign = new String(bos.toByteArray());

        Assert.assertTrue(
                "Written design should contain \"<custom-special\", but instead got "
                        + writtenDesign,
                writtenDesign.contains("<custom-special"));
    }

    public void cleanup() {
        currentMapper.remove();
    }
}
