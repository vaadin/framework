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
