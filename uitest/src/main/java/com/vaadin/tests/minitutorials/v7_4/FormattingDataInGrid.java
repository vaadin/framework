package com.vaadin.tests.minitutorials.v7_4;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.CellReference;
import com.vaadin.v7.ui.Grid.CellStyleGenerator;
import com.vaadin.v7.ui.renderers.HtmlRenderer;
import com.vaadin.v7.ui.renderers.NumberRenderer;

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
                    if (value.doubleValue() == Math
                            .round(value.doubleValue())) {
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
                String stringRepresentation = super.convertToPresentation(value,
                        targetType, locale);
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
