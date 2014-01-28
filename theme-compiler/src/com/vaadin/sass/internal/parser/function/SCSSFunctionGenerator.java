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
package com.vaadin.sass.internal.parser.function;

import com.vaadin.sass.internal.parser.LexicalUnitImpl;

/**
 * Generator class is used to handle SCSS functions. Generator is applied to the
 * function lexical unit if its method {@link #getFunctionName()} returns name
 * of the function.
 * 
 * If there are no dedicated generator for the function then default generator
 * is used.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface SCSSFunctionGenerator {

    /**
     * Returns function name handled by this generator. Default generator
     * returns <code>null</code> and is used if there is no dedicated generator
     * for given function.
     * 
     * @since 7.2
     * @return
     */
    String getFunctionName();

    /**
     * Prints out the current state of the function. State is SCSS content of
     * the function before compilation and compiled CSS content after
     * compilation.
     * 
     * @since 7.2
     * @param function
     *            Function lexical unit to print its state
     * @return String state representation of the function
     */
    String printState(LexicalUnitImpl function);
}
