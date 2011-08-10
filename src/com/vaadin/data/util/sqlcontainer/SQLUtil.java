package com.vaadin.data.util.sqlcontainer;

public class SQLUtil {
	/**
     * Escapes different special characters in strings that are passed to SQL.
     * Replaces the following:
     * 
     * <list> <li>' is replaced with ''</li> <li>\x00 is removed</li> <li>\ is
     * replaced with \\</li> <li>" is replaced with \"</li> <li>
     * \x1a is removed</li> </list>
     * 
     * Also note! The escaping done here may or may not be enough to prevent any
     * and all SQL injections so it is recommended to check user input before
     * giving it to the SQLContainer/TableQuery.
     * 
     * @param constant
     * @return \\\'\'
     */
    public static String escapeSQL(String constant) {
        if (constant == null) {
            return null;
        }
        String fixedConstant = constant;
        fixedConstant = fixedConstant.replaceAll("\\\\x00", "");
        fixedConstant = fixedConstant.replaceAll("\\\\x1a", "");
        fixedConstant = fixedConstant.replaceAll("'", "''");
        fixedConstant = fixedConstant.replaceAll("\\\\", "\\\\\\\\");
        fixedConstant = fixedConstant.replaceAll("\\\"", "\\\\\"");
        return fixedConstant;
    }
}
