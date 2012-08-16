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
package com.vaadin.data.util.sqlcontainer;

import java.io.Serializable;

public class SQLUtil implements Serializable {
    /**
     * Escapes different special characters in strings that are passed to SQL.
     * Replaces the following:
     * 
     * <list> <li>' is replaced with ''</li> <li>\x00 is removed</li> <li>\ is
     * replaced with \\</li> <li>" is replaced with \"</li> <li>
     * \x1a is removed</li> </list>
     * 
     * Also note! The escaping done here may or may not be enough to prevent any
     * and all SQL injections so it is recommended to check user input before
     * giving it to the SQLContainer/TableQuery.
     * 
     * @param constant
     * @return \\\'\'
     */
    public static String escapeSQL(String constant) {
        if (constant == null) {
            return null;
        }
        String fixedConstant = constant;
        fixedConstant = fixedConstant.replaceAll("\\\\x00", "");
        fixedConstant = fixedConstant.replaceAll("\\\\x1a", "");
        fixedConstant = fixedConstant.replaceAll("'", "''");
        fixedConstant = fixedConstant.replaceAll("\\\\", "\\\\\\\\");
        fixedConstant = fixedConstant.replaceAll("\\\"", "\\\\\"");
        return fixedConstant;
    }
}
