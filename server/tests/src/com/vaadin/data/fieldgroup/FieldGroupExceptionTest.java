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
