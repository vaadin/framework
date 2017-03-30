package com.vaadin.tests.components.grid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

public class LocalDateRenderers extends AbstractTestUI {

    private static class TimeBean {
        private LocalDate localDate;
        private LocalDateTime localDateTime;

        public TimeBean() {
            localDate = LocalDate.ofEpochDay(0);
            localDateTime = localDate.atTime(0, 0);
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
        DateTimeFormatter finnishDateFormatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("fi"));
        DateTimeFormatter finnishDateTimeFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.LONG)
                .withLocale(new Locale("fi"));

        Grid<TimeBean> grid = new Grid<>();
        grid.setLocale(new Locale("en"));
        grid.addColumn(TimeBean::getLocalDate, new LocalDateRenderer())
                .setCaption("LocalDate");
        grid.addColumn(TimeBean::getLocalDate,
                new LocalDateRenderer(finnishDateFormatter, ""))
                .setCaption("LocalDate, Finnish formatter");
        grid.addColumn(TimeBean::getLocalDateTime, new LocalDateTimeRenderer())
                .setCaption("LocalDateTime");
        grid.addColumn(TimeBean::getLocalDateTime,
                new LocalDateTimeRenderer(finnishDateTimeFormatter, ""))
                .setCaption("LocalDateTime, Finnish formatter");
        grid.setItems(new TimeBean());
        addComponent(grid);
    }

}
