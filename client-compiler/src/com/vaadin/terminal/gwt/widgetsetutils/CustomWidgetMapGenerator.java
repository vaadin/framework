/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.terminal.gwt.widgetsetutils;

import java.util.Collection;
import java.util.HashSet;

import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

/**
 * An abstract helper class that can be used to easily build a widgetset with
 * customized load styles for each components. In three abstract methods one can
 * override the default values given in {@link Connect} annotations.
 * 
 * @see WidgetMapGenerator
 * 
 */
public abstract class CustomWidgetMapGenerator extends WidgetMapGenerator {

    private Collection<Class<? extends ComponentConnector>> eagerPaintables = new HashSet<Class<? extends ComponentConnector>>();
    private Collection<Class<? extends ComponentConnector>> lazyPaintables = new HashSet<Class<? extends ComponentConnector>>();
    private Collection<Class<? extends ComponentConnector>> deferredPaintables = new HashSet<Class<? extends ComponentConnector>>();

    @Override
    protected LoadStyle getLoadStyle(Class<? extends ServerConnector> connector) {
        if (eagerPaintables == null) {
            init();
        }
        if (eagerPaintables.contains(connector)) {
            return LoadStyle.EAGER;
        }
        if (lazyPaintables.contains(connector)) {
            return LoadStyle.LAZY;
        }
        if (deferredPaintables.contains(connector)) {
            return LoadStyle.DEFERRED;
        }
        return super.getLoadStyle(connector);
    }

    private void init() {
        Class<? extends ComponentConnector>[] eagerComponents = getEagerComponents();
        if (eagerComponents != null) {
            for (Class<? extends ComponentConnector> class1 : eagerComponents) {
                eagerPaintables.add(class1);
            }
        }
        Class<? extends ComponentConnector>[] lazyComponents = getEagerComponents();
        if (lazyComponents != null) {
            for (Class<? extends ComponentConnector> class1 : lazyComponents) {
                lazyPaintables.add(class1);
            }
        }
        Class<? extends ComponentConnector>[] deferredComponents = getEagerComponents();
        if (deferredComponents != null) {
            for (Class<? extends ComponentConnector> class1 : deferredComponents) {
                deferredPaintables.add(class1);
            }
        }
    }

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#EAGER}
     */
    protected abstract Class<? extends ComponentConnector>[] getEagerComponents();

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#LAZY}
     */
    protected abstract Class<? extends ComponentConnector>[] getLazyComponents();

    /**
     * @return an array of components whose load style should be overridden to
     *         {@link LoadStyle#DEFERRED}
     */
    protected abstract Class<? extends ComponentConnector>[] getDeferredComponents();

}
