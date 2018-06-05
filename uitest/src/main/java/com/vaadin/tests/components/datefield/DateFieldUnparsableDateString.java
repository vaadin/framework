package com.vaadin.tests.components.datefield;

import com.vaadin.data.Result;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFieldUnparsableDateString extends AbstractTestUI {

    @Override
    protected Integer getTicketNumber() {
        return 10681;
    }

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField1 = new ParsableDateField();
        dateField1.setDateFormat("dd.MM.yyyy");
        addComponent(dateField1);
    }

    public class ParsableDateField extends DateField {

        @Override
        protected Result<LocalDate> handleUnparsableDateString(
                String dateString) {
            try {
                String parseableString = StringUtils.remove(dateString, ' ');
                if (parseableString.length() % 2 == 1) {
                    parseableString = "0" + parseableString;
                }
                int cutYear = Year.now().getValue() - 80;
                LocalDate today = LocalDate.now();

                switch (parseableString.length()) {
                case 2:
                    // Only day !!! dd
                    return Result.ok(LocalDate.parse(parseableString,
                            new DateTimeFormatterBuilder().appendPattern("dd")
                                    .parseDefaulting(ChronoField.MONTH_OF_YEAR,
                                            today.getMonthValue())
                                    .parseDefaulting(ChronoField.YEAR,
                                            today.getYear())
                                    .toFormatter()));
                case 4:
                    // Only day + month ddMM
                    return Result
                            .ok(LocalDate.parse(parseableString,
                                    new DateTimeFormatterBuilder()
                                            .appendPattern("ddMM")
                                            .parseDefaulting(ChronoField.YEAR,
                                                    today.getYear())
                                            .toFormatter()));
                case 6:
                    // Short year ddMMyy
                    return Result.ok(LocalDate.parse(parseableString,
                            new DateTimeFormatterBuilder().appendPattern("ddMM")
                                    .appendValueReduced(ChronoField.YEAR, 2, 2,
                                            cutYear)
                                    .toFormatter()));
                case 8:
                    // Long year ddMMyyyy
                    parseableString = StringUtils.leftPad(dateString, 8, "0");
                    return Result.ok(LocalDate.parse(parseableString,
                            new DateTimeFormatterBuilder()
                                    .appendPattern("ddMMyyyy").toFormatter()));
                default:
                    break;
                }
            } catch (Exception e) {
            }
            return super.handleUnparsableDateString(dateString);
        }
    }
}
