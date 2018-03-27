package com.vaadin.data.fieldgroup;

import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.PopupDateField;

public class FieldGroupExceptionTest {

    @Test(expected = CommitException.class)
    public void testUnboundCommitException() throws CommitException {
        FieldGroup fieldGroup = new FieldGroup();
        PopupDateField dateField = new PopupDateField();
        fieldGroup.bind(dateField, "date");
        fieldGroup.commit();
    }

}
