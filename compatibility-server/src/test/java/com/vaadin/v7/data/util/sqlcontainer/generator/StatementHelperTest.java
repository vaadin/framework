/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.data.util.sqlcontainer.generator;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;

/**
 *
 * @author Vaadin Ltd
 */
public class StatementHelperTest {

    @Test
    public void testSetValueNullParameter() throws SQLException {
        StatementHelper helper = new StatementHelper();
        helper.addParameterValue(null, StatementHelper.class);
        PreparedStatement statement = EasyMock
                .createMock(PreparedStatement.class);
        // should throw SQLException, not NPE
        try {
            helper.setParameterValuesToStatement(statement);
            fail("Expected SQLExecption for unsupported type");
        } catch (SQLException e) {
            // Exception should contain info about which parameter and the type
            // which was unsupported
            assertTrue(e.getMessage().contains("parameter 0"));
            assertTrue(
                    e.getMessage().contains(StatementHelper.class.getName()));
        }
    }

    @Test
    public void testSetByteArrayValue() throws SQLException {
        StatementHelper helper = new StatementHelper();
        helper.addParameterValue(null, byte[].class);
        PreparedStatement statement = EasyMock
                .createMock(PreparedStatement.class);
        // should not throw SQLException
        helper.setParameterValuesToStatement(statement);

        EasyMock.replay(statement);
        statement.setBytes(1, null);
    }
}
