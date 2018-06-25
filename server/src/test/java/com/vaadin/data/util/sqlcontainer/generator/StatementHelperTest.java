package com.vaadin.data.util.sqlcontainer.generator;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

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
            Assert.fail("Expected SQLExecption for unsupported type");
        } catch (SQLException e) {
            // Exception should contain info about which parameter and the type
            // which was unsupported
            Assert.assertTrue(e.getMessage().contains("parameter 0"));
            Assert.assertTrue(
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
