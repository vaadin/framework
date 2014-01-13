/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.sass.internal.parser;

import org.w3c.css.sac.InputSource;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface Parser extends org.w3c.css.sac.Parser {

    InputSource getInputSource();

    class ParserAccessor {

        public static Parser getParser() {
            try {
                String implClassName = Parser.class.getPackage().getName()
                        + ".ParserImpl";
                Class<?> clazz = Class.forName(implClassName);
                return (Parser) clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to load parser implementation."
                                + "Check whether you have generated parser "
                                + "class using build procedure", e);
            }
        }

    }
}
