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
package com.vaadin.tests.minitutorials.v7_4;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

@Theme("valo")
public class FormattingDataInGrid extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid(GridExampleHelper.createContainer());

        setContent(grid);

        grid.setCellStyleGenerator(new CellStyleGenerator() {
            @Override
            public String getStyle(CellReference cellReference) {
                if ("amount".equals(cellReference.getPropertyId())) {
                    Double value = (Double) cellReference.getValue();
                    if (value.doubleValue() == Math.round(value.doubleValue())) {
                        return "integer";
                    }
                }
                return null;
            }
        });

        getPage().getStyles().add(".integer { color: blue; }");

        NumberFormat poundformat = NumberFormat.getCurrencyInstance(Locale.UK);
        NumberRenderer poundRenderer = new NumberRenderer(poundformat);
        grid.getColumn("amount").setRenderer(poundRenderer);

        grid.getColumn("count").setConverter(new StringToIntegerConverter() {
            @Override
            public String convertToPresentation(Integer value,
                    Class<? extends String> targetType, Locale locale)
                    throws Converter.ConversionException {
                String stringRepresentation = super.convertToPresentation(
                        value, targetType, locale);
                if (value.intValue() % 2 == 0) {
                    return "<strong>" + stringRepresentation + "</strong>";
                } else {
                    return "<em>" + stringRepresentation + "</em>";
                }
            }
        });

        grid.getColumn("count").setRenderer(new HtmlRenderer());
    }
}
