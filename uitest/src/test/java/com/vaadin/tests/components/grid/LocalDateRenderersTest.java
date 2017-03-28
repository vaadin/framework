package com.vaadin.tests.components.grid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class LocalDateRenderersTest extends SingleBrowserTest {

    @Test
    public void localDate_and_LocalDateTime_rendered_correctly() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        LocalDate epochDate = LocalDate.ofEpochDay(0);
        Assert.assertEquals(
                epochDate.format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
                                .withLocale(new Locale("en"))),
                grid.getCell(0, 0).getText());
        Assert.assertEquals("1. tammikuuta 1970", grid.getCell(0, 1).getText());
        Assert.assertEquals(
                epochDate.atTime(0, 0)
                        .format(DateTimeFormatter.ofLocalizedDateTime(
                                FormatStyle.LONG, FormatStyle.SHORT)
                                .withLocale(new Locale("en"))),
                grid.getCell(0, 2).getText());
        Assert.assertEquals("1. tammikuuta 1970 klo 0.00.00",
                grid.getCell(0, 3).getText());
    }
}
