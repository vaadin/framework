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
package com.vaadin.server.widgetsetutils;

import java.io.PrintWriter;
import java.util.Date;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.vaadin.client.ui.dd.VAcceptCriterion;
import com.vaadin.client.ui.dd.VAcceptCriterionFactory;
import com.vaadin.shared.ui.dd.AcceptCriterion;

/**
 * GWT generator to build {@link VAcceptCriterionFactory} implementation
 * dynamically based on {@link AcceptCriterion} annotations available in
 * classpath.
 * 
 */
public class AcceptCriteriaFactoryGenerator extends Generator {

    private String packageName;
    private String className;

    @Override
    public String generate(TreeLogger logger, GeneratorContext context,
            String typeName) throws UnableToCompleteException {

        try {
            TypeOracle typeOracle = context.getTypeOracle();

            // get classType and save instance variables
            JClassType classType = typeOracle.getType(typeName);
            packageName = classType.getPackage().getName();
            className = classType.getSimpleSourceName() + "Impl";
            // Generate class source code
            generateClass(logger, context);
        } catch (Exception e) {
            logger.log(TreeLogger.ERROR,
                    "Accept criterion factory creation failed", e);
        }
        // return the fully qualifed name of the class generated
        return packageName + "." + className;
    }

    /**
     * Generate source code for WidgetMapImpl
     * 
     * @param logger
     *            Logger object
     * @param context
     *            Generator context
     */
    private void generateClass(TreeLogger logger, GeneratorContext context) {
        // get print writer that receives the source code
        PrintWriter printWriter = null;
        printWriter = context.tryCreate(logger, packageName, className);
        // print writer if null, source code has ALREADY been generated,
        // return (WidgetMap is equal to all permutations atm)
        if (printWriter == null) {
            return;
        }
        logger.log(Type.INFO, "Detecting available criteria ...");
        Date date = new Date();

        // init composer, set class properties, create source writer
        ClassSourceFileComposerFactory composer = null;
        composer = new ClassSourceFileComposerFactory(packageName, className);
        composer.addImport("com.google.gwt.core.client.GWT");
        composer.setSuperclass("com.vaadin.client.ui.dd.VAcceptCriterionFactory");
        SourceWriter sourceWriter = composer.createSourceWriter(context,
                printWriter);

        // generator constructor source code
        generateInstantiatorMethod(sourceWriter, context, logger);
        // close generated class
        sourceWriter.outdent();
        sourceWriter.println("}");
        // commit generated class
        context.commit(logger, printWriter);
        logger.log(Type.INFO,
                "Done. (" + (new Date().getTime() - date.getTime()) / 1000
                        + "seconds)");

    }

    private void generateInstantiatorMethod(SourceWriter sourceWriter,
            GeneratorContext context, TreeLogger logger) {

        sourceWriter.println("public VAcceptCriterion get(String name) {");
        sourceWriter.indent();

        sourceWriter.println("name = name.intern();");

        JClassType criteriaType = context.getTypeOracle().findType(
                VAcceptCriterion.class.getName());
        for (JClassType clientClass : criteriaType.getSubtypes()) {
            AcceptCriterion annotation = clientClass
                    .getAnnotation(AcceptCriterion.class);
            if (annotation != null) {
                String clientClassName = clientClass.getQualifiedSourceName();
                Class<?> serverClass = clientClass.getAnnotation(
                        AcceptCriterion.class).value();
                String serverClassName = serverClass.getCanonicalName();
                logger.log(Type.INFO, "creating mapping for " + serverClassName);
                sourceWriter.print("if (\"");
                sourceWriter.print(serverClassName);
                sourceWriter.print("\" == name) return GWT.create(");
                sourceWriter.print(clientClassName);
                sourceWriter.println(".class );");
                sourceWriter.print("else ");
            }
        }

        sourceWriter.println("return null;");
        sourceWriter.outdent();
        sourceWriter.println("}");
    }
}
