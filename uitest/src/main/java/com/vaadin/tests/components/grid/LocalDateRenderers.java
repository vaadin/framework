package com.vaadin.tests.components.grid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.LocalDateRenderer;

public class LocalDateRenderers extends AbstractTestUI {

    private static class TimeBean {
        private LocalDate localDate;
        private LocalDateTime localDateTime;

        public TimeBean() {
            localDate = LocalDate.ofEpochDay(0);
            localDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
        }

        public LocalDate getLocalDate() {
            return localDate;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        DateTimeFormatter finnishFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("fi"));

        Grid<TimeBean> grid = new Grid<>();
        grid.addColumn(TimeBean::getLocalDate, new LocalDateRenderer())
                .setCaption("LocalDate");
        grid.addColumn(TimeBean::getLocalDate,
                new LocalDateRenderer(finnishFormatter, ""))
                .setCaption("LocalDate, Finnish formatter");
        grid.addColumn(TimeBean::getLocalDateTime).setCaption("LocalDateTime");
        grid.setItems(new TimeBean());
        addComponent(grid);
    }

}
