package com.vaadin.tests.declarative;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

public class DeclarativeGrid extends AbstractTestUI {

    private String design = "" + //
            "   <vaadin-grid>\n" + //
            "    <table>\n" + //
            "     <colgroup>\n" + //
            "      <col column-id=\"project\" sortable=\"false\">\n" + //
            "      <col column-id=\"status\" sortable=\"false\">\n" + //
            "      <col column-id=\"date\" sortable=\"false\">\n" + //
            "     </colgroup>\n" + //
            "     <thead>\n" + //
            "      <tr default>\n" + //
            "       <th plain-text column-ids=\"project,status\" " + //
            "          colspan=\"2\">Project and Status</th>\n" + //
            "       <th plain-text column-ids=\"date\">Date</th>\n" + //
            "      </tr>\n" + //
            "     </thead>\n" + //
            "     <tbody>\n" + //
            "      <tr item=\"project1\">\n" + //
            "       <td>Customer Project 1</td>\n" + //
            "       <td>OK</td>\n" + //
            "       <td>2020-12-31</td>\n" + //
            "      </tr>\n" + //
            "      <tr item=\"project2\">\n" + //
            "       <td>Customer Project 2</td>\n" + //
            "       <td>OK</td>\n" + //
            "       <td>2020-07-02</td>\n" + //
            "      </tr>\n" + //
            "      <tr item=\"project3\">\n" + //
            "       <td>Customer Project 3</td>\n" + //
            "       <td>OK</td>\n" + //
            "       <td>2019-10-01</td>\n" + //
            "      </tr>\n" + //
            "     </tbody>\n" + //
            "    </table>\n" + //
            "   </vaadin-grid>";

    @Override
    protected void setup(VaadinRequest request) {
        DesignContext dc = Design
                .read(new ByteArrayInputStream(design.getBytes(UTF_8)), null);
        addComponent(dc.getRootComponent());
    }

    @Override
    protected Integer getTicketNumber() {
        return 10464;
    }

    @Override
    protected String getTestDescription() {
        return "Merged column header should not cause an exception.";
    }
}
