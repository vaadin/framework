package com.vaadin.tests.components.treetable;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TreeTable;

public class ProgrammaticSelect extends TestBase {

    @Override
    protected void setup() {
        final TreeTable tt = new TreeTable();
        tt.setContainerDataSource(buildDataSource(10, 100, 50));
        tt.setSelectable(true);
        addComponent(tt);

        Button selectItem = new Button("Select first row",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Object id = tt.getItemIds().iterator().next();
                        tt.select(id);
                    }
                });

        addComponent(selectItem);
    }

    private Container buildDataSource(int properties, int items, int roots) {
        Container.Hierarchical c = new HierarchicalContainer();

        populateContainer(c, properties, items);

        if (items <= roots) {
            return c;
        }

        // "roots" roots, each with
        // "firstLevel" children, two with no children (one with childAllowed,
        // one without)
        // ("firstLevel"-2)*"secondLevel" children ("secondLevel"/2 with
        // childAllowed, "secondLevel"/2 without)

        // N*M+N*(M-2)*C = items
        // items=N(M+MC-2C)

        // Using secondLevel=firstLevel/2 =>
        // items = roots*(firstLevel+firstLevel*firstLevel/2-2*firstLevel/2)
        // =roots*(firstLevel+firstLevel^2/2-firstLevel)
        // = roots*firstLevel^2/2
        // => firstLevel = sqrt(items/roots*2)

        int firstLevel = (int) Math.ceil(Math.sqrt(items / roots * 2.0));
        int secondLevel = firstLevel / 2;

        while (roots * (1 + 2 + (firstLevel - 2) * secondLevel) < items) {
            // Increase something so we get enough items
            secondLevel++;
        }

        List<Object> itemIds = new ArrayList<Object>(c.getItemIds());

        int nextItemId = roots;
        for (int rootIndex = 0; rootIndex < roots; rootIndex++) {
            // roots use items 0..roots-1
            Object rootItemId = itemIds.get(rootIndex);

            // force roots to be roots even though they automatically should be
            c.setParent(rootItemId, null);

            for (int firstLevelIndex = 0; firstLevelIndex < firstLevel; firstLevelIndex++) {
                if (nextItemId >= items) {
                    break;
                }
                Object firstLevelItemId = itemIds.get(nextItemId++);
                c.setParent(firstLevelItemId, rootItemId);

                if (firstLevelIndex < 2) {
                    continue;
                }

                // firstLevelChildren 2.. have child nodes
                for (int secondLevelIndex = 0; secondLevelIndex < secondLevel; secondLevelIndex++) {
                    if (nextItemId >= items) {
                        break;
                    }

                    Object secondLevelItemId = itemIds.get(nextItemId++);
                    c.setParent(secondLevelItemId, firstLevelItemId);
                }
            }
        }

        return c;
    }

    private void populateContainer(Container c, int properties, int items) {
        c.removeAllItems();
        for (int i = 1; i <= properties; i++) {
            c.addContainerProperty("Property " + i, String.class, "");
        }
        for (int i = 1; i <= items; i++) {
            Item item = c.addItem("Item " + i);
            for (int j = 1; j <= properties; j++) {
                item.getItemProperty("Property " + j).setValue(
                        "Item " + i + "," + j);
            }
        }

    }

    @Override
    protected String getDescription() {
        return "Programmatically selecting an item should not cause a complete repaint";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6766;
    }

}
