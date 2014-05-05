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

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.client.communication.JsonDecoder;
import com.vaadin.client.communication.JsonEncoder;
import com.vaadin.server.widgetsetutils.ConnectorBundleLoaderFactory;

public class ArraySerializer extends JsonSerializer {

    private final JArrayType arrayType;

    public ArraySerializer(JArrayType arrayType) {
        super(arrayType);
        this.arrayType = arrayType;
    }

    @Override
    protected void printDeserializerBody(TreeLogger logger, SourceWriter w,
            String type, String jsonValue, String connection) {
        JType leafType = arrayType.getLeafType();
        int rank = arrayType.getRank();

        w.println(JSONArray.class.getName() + " jsonArray = " + jsonValue
                + ".isArray();");

        // Type value = new Type[jsonArray.size()][][];
        w.print(arrayType.getQualifiedSourceName() + " value = new "
                + leafType.getQualifiedSourceName() + "[jsonArray.size()]");
        for (int i = 1; i < rank; i++) {
            w.print("[]");
        }
        w.println(";");

        w.println("for(int i = 0 ; i < value.length; i++) {");
        w.indent();

        JType componentType = arrayType.getComponentType();

        w.print("value[i] = ("
                + ConnectorBundleLoaderFactory.getBoxedTypeName(componentType)
                + ") " + JsonDecoder.class.getName() + ".decodeValue(");
        ConnectorBundleLoaderFactory.writeTypeCreator(w, componentType);
        w.print(", jsonArray.get(i), null, " + connection + ")");

        w.println(";");

        w.outdent();
        w.println("}");

        w.println("return value;");
    }

    @Override
    protected void printSerializerBody(TreeLogger logger, SourceWriter w,
            String value, String applicationConnection) {
        JType componentType = arrayType.getComponentType();

        w.println(JSONArray.class.getName() + " values = new "
                + JSONArray.class.getName() + "();");
        // JPrimitiveType primitive = componentType.isPrimitive();
        w.println("for (int i = 0; i < " + value + ".length; i++) {");
        w.indent();
        w.print("values.set(i, ");
        w.print(JsonEncoder.class.getName() + ".encode(" + value + "[i],");
        ConnectorBundleLoaderFactory.writeTypeCreator(w, componentType);
        w.print(", " + applicationConnection + ")");
        w.println(");");
        w.outdent();
        w.println("}");
        w.println("return values;");
    }

}
