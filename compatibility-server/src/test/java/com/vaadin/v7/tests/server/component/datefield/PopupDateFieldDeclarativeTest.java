package com.vaadin.v7.tests.server.component.datefield;

import java.util.Date;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.PopupDateField;

/**
 * Tests the declarative support for implementations of {@link PopupDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class PopupDateFieldDeclarativeTest
        extends DeclarativeTestBase<PopupDateField> {

    private String getBasicDesign() {
        return "<vaadin7-popup-date-field assistive-text='at' text-field-enabled='false' show-iso-week-numbers resolution=\"MINUTE\" range-end=\"2019-01-15\" input-prompt=\"Pick a day\" value=\"2003-02-27 07:15\"></vaadin7-popup-date-field>";
    }

    private PopupDateField getBasicExpected() {
        PopupDateField pdf = new PopupDateField();
        pdf.setShowISOWeekNumbers(true);
        pdf.setResolution(Resolution.MINUTE);
        pdf.setRangeEnd(new Date(2019 - 1900, 1 - 1, 15));
        pdf.setInputPrompt("Pick a day");
        pdf.setValue(new Date(2003 - 1900, 2 - 1, 27, 7, 15));
        pdf.setTextFieldEnabled(false);
        pdf.setAssistiveText("at");
        return pdf;
    }

    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

}
