package com.vaadin.tests.components.uitest.components;

import java.util.HashSet;

import com.vaadin.event.Action;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.GridLayout;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.themes.ChameleonTheme;
import com.vaadin.v7.ui.themes.Reindeer;

public class TablesCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    private final Action ACTION_MARK = new Action("Mark");
    private final Action ACTION_UNMARK = new Action("Unmark");
    private final Action ACTION_LOG = new Action("Save");
    private final Action[] ACTIONS_UNMARKED = { ACTION_MARK, ACTION_LOG };
    private final Action[] ACTIONS_MARKED = { ACTION_UNMARK, ACTION_LOG };

    public TablesCssTest(TestSampler parent) {
        super();
        setSpacing(true);
        setColumns(2);
        setWidth("100%");

        this.parent = parent;

        createTableWith("CC & flags, default table", null);
        createTableWith("Borderless", ChameleonTheme.TABLE_BORDERLESS);
        createTableWith("Big", ChameleonTheme.TABLE_BIG);
        createTableWith("Small", ChameleonTheme.TABLE_SMALL);
        createTableWith("Striped", ChameleonTheme.TABLE_STRIPED);
        createTableWith("Strong", Reindeer.TABLE_STRONG);

    }

    private void createTableWith(String caption, String primaryStyleName) {
        final HashSet<Object> markedRows = new HashSet<>();

        final Table t;
        if (caption != null) {
            t = new Table(caption);
        } else {
            t = new Table();
        }

        t.setId("table" + debugIdCounter++);

        if (primaryStyleName != null) {
            t.addStyleName(primaryStyleName);
        }

        t.setWidth("100%");
        t.setHeight("100px");

        t.setSelectable(true);
        t.setMultiSelect(true);
        t.setImmediate(true);
        t.setContainerDataSource(TestUtils.getISO3166Container());
        t.setColumnReorderingAllowed(true);
        t.setColumnCollapsingAllowed(true);
        // t.setColumnHeaders(new String[] { "Country", "Code", "Icon file" });
        t.setColumnIcon(TestUtils.iso3166_PROPERTY_NAME,
                new ThemeResource(TestSampler.ICON_URL));

        // Actions (a.k.a context menu)
        t.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(Object target, Object sender) {
                if (markedRows.contains(target)) {
                    return ACTIONS_MARKED;
                } else {
                    return ACTIONS_UNMARKED;
                }
            }

            @Override
            public void handleAction(Action action, Object sender,
                    Object target) {
                // We just want the actions UI.. don't care about the logic...
                if (ACTION_MARK == action) {
                    markedRows.add(target);
                    t.refreshRowCache();
                } else if (ACTION_UNMARK == action) {
                    markedRows.remove(target);
                    t.refreshRowCache();
                }
            }
        });

        addComponent(t);
        parent.registerComponent(t);
    }

}
