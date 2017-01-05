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
package com.vaadin.tests.server.component.abstractcomponent;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractComponent.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractComponentDeclarativeTestBase<T extends AbstractComponent>
        extends DeclarativeTestBase<T> {

    /**
     * Returns expected element tag for the tested component.
     *
     * @return expected element tag
     */
    protected abstract String getComponentTag();

    /**
     * Returns component class which is a subject to test
     *
     * @return the component class
     */
    protected abstract Class<? extends T> getComponentClass();

    @Test
    public void emptyAbstractComponentDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s/> ", getComponentTag());
        T component = getComponentClass().newInstance();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void abstractComponentAttributesDeserialization()
            throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String id = "testId";
        String caption = "testCaption";
        boolean captionAsHtml = true;
        String description = "testDescription";
        boolean enabled = false;
        String error = "<div>testError</div>";
        String height = "47%";
        String width = "83px";
        String icon = "img/example.gif";
        Locale locale = new Locale("fi", "FI");
        String primaryStyle = "testPrimaryStyle";
        boolean readOnly = true;
        boolean responsive = true;
        String styleName = "testStyleName";
        boolean visible = false;
        boolean requiredIndicator = true;

        T component = getComponentClass().newInstance();

        boolean hasReadOnly = callBooleanSetter(readOnly, "setReadOnly",
                component);
        boolean hasRequiredIndicator = callBooleanSetter(requiredIndicator,
                "setRequiredIndicatorVisible", component);

        String design = String.format(
                "<%s id='%s' caption='%s' caption-as-html description='%s' "
                        + "error='%s' enabled='false' width='%s' height='%s' "
                        + "icon='%s' locale='%s' primary-style-name='%s' "
                        + "%s responsive style-name='%s' visible='false' "
                        + "%s/>",
                getComponentTag(), id, caption, description, error, width,
                height, icon, locale.toString(), primaryStyle,
                hasReadOnly ? "readonly" : "", styleName,
                hasRequiredIndicator ? "required-indicator-visible" : "");

        component.setId(id);
        component.setCaption(caption);
        component.setCaptionAsHtml(captionAsHtml);
        component.setDescription(description);
        component.setEnabled(enabled);
        component.setComponentError(new UserError(error,
                com.vaadin.server.AbstractErrorMessage.ContentMode.HTML,
                ErrorLevel.ERROR));
        component.setHeight(height);
        component.setWidth(width);
        component.setIcon(new FileResource(new File(icon)));
        component.setLocale(locale);
        component.setPrimaryStyleName(primaryStyle);
        component.setResponsive(responsive);
        component.setStyleName(styleName);
        component.setVisible(visible);

        testRead(design, component);
        testWrite(design, component);
    }

    private boolean callBooleanSetter(boolean value, String setterName,
            T component)
            throws IllegalAccessException, InvocationTargetException {
        try {
            Method method = component.getClass().getMethod(setterName,
                    new Class[] { boolean.class });
            method.invoke(component, value);
            return true;
        } catch (NoSuchMethodException ignore) {
            // ignore if there is no such method
            return false;
        }
    }

    @Test
    public void externalIcon()
            throws InstantiationException, IllegalAccessException {
        String url = "http://example.com/example.gif";

        String design = String.format("<%s icon='%s'/>", getComponentTag(),
                url);

        T component = getComponentClass().newInstance();

        component.setIcon(new ExternalResource(url));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void themeIcon()
            throws InstantiationException, IllegalAccessException {
        String path = "example.gif";

        String design = String.format("<%s icon='theme://%s'/>",
                getComponentTag(), path);

        T component = getComponentClass().newInstance();

        component.setIcon(new ThemeResource(path));
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void sizeFullDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s size-full/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setSizeFull();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void widthFullDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s width-full/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setWidth("100%");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void heightFullDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s height-full/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setHeight("100%");
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void sizeUnderfinedDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setSizeUndefined();
        testRead(design, component);
        testWrite(design, component);

    }

    @Test
    public void heightUnderfinedDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setHeightUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void widthUnderfinedDeserialization()
            throws InstantiationException, IllegalAccessException {
        String design = String.format("<%s/>", getComponentTag());

        T component = getComponentClass().newInstance();

        component.setWidthUndefined();
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void testUnknownAttribute() {
        String value = "bar";
        String design = String.format("<%s foo='%s'/>", getComponentTag(),
                value);

        DesignContext context = readAndReturnContext(design);
        T label = getComponentClass().cast(context.getRootComponent());
        assertTrue("Custom attribute was preserved in custom attributes",
                context.getCustomAttributes(label).containsKey("foo"));

        testWrite(label, design, context);
    }

}
