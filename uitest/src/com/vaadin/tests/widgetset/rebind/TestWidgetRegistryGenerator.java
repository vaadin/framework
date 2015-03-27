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
package com.vaadin.tests.widgetset.rebind;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.client.metadata.Invoker;
import com.vaadin.tests.widgetset.client.TestWidgetConnector;
import com.vaadin.tests.widgetset.client.TestWidgetConnector.TestWidgetRegistry;

public class TestWidgetRegistryGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        try {
            TypeOracle typeOracle = context.getTypeOracle();

            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            String packageName = classType.getPackage().getName();
            String className = classType.getSimpleSourceName() + "Impl";

            // Generate class source code
            generateClass(packageName, className, logger, context);
            return packageName + "." + className;
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR,
                    "Accept criterion factory creation failed", e);
            throw new UnableToCompleteException();
        }
        // return the fully qualifed name of the class generated
    }

    private void generateClass(String packageName, String className,
            TreeLogger logger, GeneratorContext context) {
        PrintWriter printWriter = context.tryCreate(logger, packageName,
                className);
        // print writer if null, source code has ALREADY been generated
        if (printWriter == null) {
            return;
        }

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);

        composer.setSuperclass(TestWidgetRegistry.class.getCanonicalName());

        List<JClassType> testWidgets = findTestWidgets(logger,
                context.getTypeOracle());

        SourceWriter w = composer.createSourceWriter(context, printWriter);

        w.println("public %s() {", className);
        w.indent();

        w.println("super();");
        w.println();

        for (JClassType testWidgetType : testWidgets) {
            w.println("register(\"%s\", new %s() {",
                    escape(testWidgetType.getQualifiedSourceName()),
                    Invoker.class.getCanonicalName());
            w.indent();

            w.println("public Object invoke(Object target, Object... params) {");
            w.indent();

            w.println("return new %s();",
                    testWidgetType.getQualifiedSourceName());

            w.outdent();
            w.println("}");

            w.outdent();
            w.println("});");
            w.println();
        }

        // Close constructor
        w.outdent();
        w.println("}");

        // Close class body
        w.outdent();
        w.println("}");

        // commit generated class
        context.commit(logger, printWriter);
    }

    private List<JClassType> findTestWidgets(TreeLogger logger,
            TypeOracle typeOracle) {
        List<JClassType> testWidgetTypes = new ArrayList<JClassType>();

        JClassType[] widgetTypes = typeOracle.findType(Widget.class.getName())
                .getSubtypes();
        for (JClassType widgetType : widgetTypes) {
            if (isTestWidget(widgetType)) {
                testWidgetTypes.add(widgetType);
            }
        }

        return testWidgetTypes;
    }

    private boolean isTestWidget(JClassType widgetType) {
        if (widgetType.isAbstract()) {
            return false;
        } else if (!widgetType.getPackage().getName()
                .startsWith(TestWidgetConnector.class.getPackage().getName())) {
            return false;
        } else if (widgetType.getEnclosingType() != null
                && !widgetType.isStatic()) {
            return false;
        }

        return true;
    }

}
