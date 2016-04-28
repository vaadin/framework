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
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

public class CompareTranslator implements FilterTranslator {

    @Override
    public boolean translatesFilter(Filter filter) {
        return filter instanceof Compare;
    }

    @Override
    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        Compare compare = (Compare) filter;
        sh.addParameterValue(compare.getValue());
        String prop = QueryBuilder.quote(compare.getPropertyId());
        switch (compare.getOperation()) {
        case EQUAL:
            return prop + " = ?";
        case GREATER:
            return prop + " > ?";
        case GREATER_OR_EQUAL:
            return prop + " >= ?";
        case LESS:
            return prop + " < ?";
        case LESS_OR_EQUAL:
            return prop + " <= ?";
        default:
            return "";
        }
    }

}
