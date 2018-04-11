/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.data.util.sqlcontainer.query.generator.filter;

import java.util.Locale;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;

/**
 * @deprecated As of 8.0, no replacement available.
 */
@Deprecated
public class LikeTranslator implements FilterTranslator {

    @Override
    public boolean translatesFilter(Filter filter) {
        return filter instanceof Like;
    }

    @Override
    public String getWhereStringForFilter(Filter filter, StatementHelper sh) {
        Like like = (Like) filter;
        if (like.isCaseSensitive()) {
            sh.addParameterValue(like.getValue());
            return QueryBuilder.quote(like.getPropertyId()) + " LIKE ?";
        } else {
            sh.addParameterValue(like.getValue().toUpperCase(Locale.ROOT));
            return "UPPER(" + QueryBuilder.quote(like.getPropertyId())
                    + ") LIKE ?";
        }
    }

}
