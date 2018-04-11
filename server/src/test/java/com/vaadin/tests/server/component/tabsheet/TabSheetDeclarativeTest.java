package com.vaadin.tests.server.component.tabsheet;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for TabSheet.
 *
 * @author Vaadin Ltd
 */
public class TabSheetDeclarativeTest extends DeclarativeTestBase<TabSheet> {

    @Test
    public void testFeatures() {
        String design = "<vaadin-tab-sheet tabindex=5><tab caption=test-caption "
                + "visible=false closable enabled=false icon=http://www.vaadin.com/test.png"
                + " icon-alt=OK description=test-desc style-name=test-style "
                + "id=test-id><vaadin-text-field/></tab></vaadin-tab-sheet>";
        TabSheet ts = new TabSheet();
        ts.setTabIndex(5);
        TextField tf = new TextField();
        Tab tab = ts.addTab(tf);
        tab.setCaption("test-caption");
        tab.setVisible(false);
        tab.setClosable(true);
        tab.setEnabled(false);
        tab.setIcon(new ExternalResource("http://www.vaadin.com/test.png"));
        tab.setIconAlternateText("OK");
        tab.setDescription("test-desc");
        tab.setStyleName("test-style");
        tab.setId("test-id");
        ts.setSelectedTab(tf);
        testRead(design, ts);
        testWrite(design, ts);
    }

    @Test
    public void testSelected() {
        String design = "<vaadin-tab-sheet><tab selected><vaadin-text-field/></tab></vaadin-tab-sheet>";
        TabSheet ts = new TabSheet();
        TextField tf = new TextField();
        ts.addTab(tf);
        ts.setSelectedTab(tf);
        testRead(design, ts);
        testWrite(design, ts);
    }

    @Test
    public void tabsNotShown() {
        String design = "<vaadin-tab-sheet tabs-visible=\"false\">\n"
                + "  <tab caption=\"My Tab\" selected>\n"
                + "    <vaadin-label>My Content</vaadin-label>\n" + "  </tab>\n"
                + "</vaadin-tab-sheet>\n";
        TabSheet ts = new TabSheet();
        ts.setTabsVisible(false);
        Label l = new Label("My Content", ContentMode.HTML);
        Tab tab = ts.addTab(l);
        tab.setCaption("My Tab");
        ts.setSelectedTab(tab);
        testRead(design, ts);
        testWrite(design, ts);

    }
}
