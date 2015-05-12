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
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.communication.JSONSerializer;
import elemental.json.JsonValue;

public abstract class JsonSerializer implements GeneratedSerializer {

    private final JType type;

    public JsonSerializer(JType type) {
        this.type = type;
    }

    @Override
    public void writeSerializerInstantiator(TreeLogger logger, SourceWriter w)
            throws UnableToCompleteException {

        w.print("return new ");
        w.print(JSONSerializer.class.getCanonicalName());
        w.print("<");
        w.print(type.getQualifiedSourceName());
        w.println(">() {");
        w.indent();

        writeSerializerBody(logger, w);

        w.outdent();
        w.println("};");
    }

    protected void writeSerializerBody(TreeLogger logger, SourceWriter w) {
        String qualifiedSourceName = type.getQualifiedSourceName();
        w.println("public " + JsonValue.class.getName() + " serialize("
                + qualifiedSourceName + " value, "
                + ApplicationConnection.class.getName() + " connection) {");
        w.indent();
        // MouseEventDetails castedValue = (MouseEventDetails) value;
        w.println(qualifiedSourceName + " castedValue = ("
                + qualifiedSourceName + ") value;");

        printSerializerBody(logger, w, "castedValue", "connection");

        // End of serializer method
        w.outdent();
        w.println("}");

        // Deserializer
        // T deserialize(Type type, JSONValue jsonValue, ApplicationConnection
        // connection);
        w.println("public " + qualifiedSourceName + " deserialize(Type type, "
                + JsonValue.class.getName() + " jsonValue, "
                + ApplicationConnection.class.getName() + " connection) {");
        w.indent();

        printDeserializerBody(logger, w, "type", "jsonValue", "connection");

        w.outdent();
        w.println("}");
    }

    protected abstract void printDeserializerBody(TreeLogger logger,
            SourceWriter w, String type, String jsonValue, String connection);

    protected abstract void printSerializerBody(TreeLogger logger,
            SourceWriter w, String value, String applicationConnection);

}
