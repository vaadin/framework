/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.server.widgetsetutils.metadata;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JEnumConstant;
import com.google.gwt.core.ext.typeinfo.JEnumType;
import com.google.gwt.user.rebind.SourceWriter;

import elemental.json.Json;

public class EnumSerializer extends JsonSerializer {

    private final JEnumType enumType;

    public EnumSerializer(JEnumType type) {
        super(type);
        enumType = type;
    }

    @Override
    protected void printDeserializerBody(TreeLogger logger, SourceWriter w,
            String type, String jsonValue, String connection) {
        w.println("String enumIdentifier = " + jsonValue + ".asString();");
        for (JEnumConstant e : enumType.getEnumConstants()) {
            w.println("if (\"" + e.getName() + "\".equals(enumIdentifier)) {");
            w.indent();
            w.println("return " + enumType.getQualifiedSourceName() + "."
                    + e.getName() + ";");
            w.outdent();
            w.println("}");
        }
        w.println("return null;");
    }

    @Override
    protected void printSerializerBody(TreeLogger logger, SourceWriter w,
            String value, String applicationConnection) {
        // return Json.create(castedValue.name());
        w.println("return " + Json.class.getName() + ".create(" + value
                + ".name());");
    }

}
