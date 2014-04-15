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

package com.vaadin.server.widgetsetutils.metadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;

public class CustomSerializer implements GeneratedSerializer {

    private final JClassType serializerType;

    public CustomSerializer(JClassType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    public void writeSerializerInstantiator(TreeLogger logger, SourceWriter w)
            throws UnableToCompleteException {
        w.print("return ");
        w.print(GWT.class.getCanonicalName());
        w.print(".create(");
        ConnectorBundleLoaderFactory.writeClassLiteral(w, serializerType);
        w.println(");");
    }
}
