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

import com.vaadin.sass.internal.parser.LexicalUnitImpl;
import java.lang.Exception;

/**
 * @version $Revision: 1.0 $
 * @author James Lefeu @ Liferay, Inc.
 */
public class ReturnNodeException extends Exception{
    private LexicalUnitImpl returnValue;

    public ReturnNodeException(LexicalUnitImpl returnValue) {
        this.returnValue = returnValue;
    }

    public LexicalUnitImpl getReturnValue() {
        return returnValue;
    }

    @Override
    public String getMessage() {
        return "@return statement should only be within an @function";
    }
}
