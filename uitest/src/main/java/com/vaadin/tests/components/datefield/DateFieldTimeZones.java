package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.InlineDateTimeField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldTimeZones extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        InlineDateField d1 = new InlineDateField();
        InlineDateField d2 = new InlineDateField();
        InlineDateTimeField d3 = new InlineDateTimeField();
        InlineDateTimeField d4 = new InlineDateTimeField();
        InlineDateTimeField d5 = new InlineDateTimeField();

        d1.setValue(LocalDate.of(2018, 1, 1));
        d2.setValue(LocalDate.of(2019, 12, 1));
        d3.setValue(LocalDateTime.of(2019, 12, 1,0,0,0));
        d4.setValue(LocalDateTime.of(2019, 12, 1,0,0,0));
        d4.setValue(LocalDateTime.of(2019, 12, 1,0,0,0));

        d1.setResolution(DateResolution.DAY);
        d2.setResolution(DateResolution.DAY);

        d2.setRangeStart(LocalDate.of(2018, 1, 1));
        d2.setRangeEnd(LocalDate.of(2019, 12, 1));

        d3.setRangeStart(LocalDateTime.of(2018, 1, 1,0,0,0));
        d3.setRangeEnd(LocalDateTime.of(2019, 12, 1,0,0,0));

        d5.setRangeStart(LocalDateTime.of(2018, 1, 1,0,0,0));
        d5.setRangeEnd(LocalDateTime.of(2019, 12, 1,0,0,0));

        d5.setZoneId(ZoneId.of("-10"));
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponents(d1, d2,d3,d4,d5);

        addComponent(layout);
    }

}
