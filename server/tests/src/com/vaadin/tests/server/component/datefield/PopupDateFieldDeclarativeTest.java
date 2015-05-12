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
package com.vaadin.tests.server.component.datefield;

import java.util.Date;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.PopupDateField;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class PopupDateFieldDeclarativeTest extends
        DeclarativeTestBase<PopupDateField> {

    private String getBasicDesign() {
        return "<v-popup-date-field assistive-text='at' text-field-enabled='false' show-iso-week-numbers resolution=\"MINUTE\" range-end=\"2019-01-15\" input-prompt=\"Pick a day\" value=\"2003-02-27 07:15\"></v-popup-date-field>";
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
