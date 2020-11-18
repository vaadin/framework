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
package com.vaadin.data.util.sqlcontainer.query.generator.filter;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

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
            sh.addParameterValue(like.getValue().toUpperCase());
            return "UPPER(" + QueryBuilder.quote(like.getPropertyId())
                    + ") LIKE ?";
        }
    }

}
