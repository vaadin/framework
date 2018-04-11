package com.vaadin.v7.data.fieldgroup;

import org.junit.Test;

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.ui.PopupDateField;

public class FieldGroupExceptionTest {

    @Test(expected = CommitException.class)
    public void testUnboundCommitException() throws CommitException {
        FieldGroup fieldGroup = new FieldGroup();
        PopupDateField dateField = new PopupDateField();
        fieldGroup.bind(dateField, "date");
        fieldGroup.commit();
    }

}
