package com.vaadin.tests.components.uitest.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

public class TabSheetsCssTest {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public TabSheetsCssTest(TestSampler parent) {
        this.parent = parent;

        TabSheet basic = createTabSheetWith("Basic TabSheet", null);
        parent.addComponent(basic);

        TabSheet bordeless = createTabSheetWith("Borderelss TabSheet",
                Reindeer.TABSHEET_BORDERLESS);
        parent.addComponent(bordeless);

        TabSheet bar = createTabSheetWith("A small/bar TabSheet",
                Reindeer.TABSHEET_SMALL);
        parent.addComponent(bar);

        TabSheet minimal = createTabSheetWith("A minimal tabsheet",
                Reindeer.TABSHEET_MINIMAL);
        parent.addComponent(minimal);

        TabSheet hoverClosable = createTabSheetWith(
                "A hover-closable TabSheet", Reindeer.TABSHEET_HOVER_CLOSABLE);
        parent.addComponent(hoverClosable);

        TabSheet selectedClosable = createTabSheetWith(
                "A selected-closable TabSheet",
                Reindeer.TABSHEET_SELECTED_CLOSABLE);
        parent.addComponent(selectedClosable);

        TabSheet light = createTabSheetWith("A light TabSheet",
                Runo.TABSHEET_SMALL);
        parent.addComponent(light);

    }

    private TabSheet createTabSheetWith(String caption, String styleName) {
        TabSheet ts = new TabSheet();
        ts.setId("tabsheet" + debugIdCounter++);
        ts.setCaption(caption);
        ts.setComponentError(new UserError("A error message"));

        Label content = new Label("First Component");
        ts.addTab(content, "First");
        Label content2 = new Label("Second Component");
        ts.addTab(content2, "Second");
        ts.getTab(content2).setClosable(true);

        Label content3 = new Label("Third Component");
        ts.addTab(content3, "Third", new ThemeResource(parent.ICON_URL));
        ts.getTab(content3).setEnabled(false);

        if (styleName != null) {
            ts.addStyleName(styleName);
        }

        return ts;

    }
}
