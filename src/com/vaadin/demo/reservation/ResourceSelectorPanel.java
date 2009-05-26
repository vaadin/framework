/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.demo.reservation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ResourceSelectorPanel extends Panel implements
        Button.ClickListener {
    private final HashMap<String, Layout> categoryLayouts = new HashMap<String, Layout>();
    private final HashMap<String, LinkedList<Item>> categoryResources = new HashMap<String, LinkedList<Item>>();

    // private Container allResources;
    private LinkedList<Item> selectedResources = null;

    public ResourceSelectorPanel(String caption) {
        super(caption, new HorizontalLayout());
        addStyleName(Panel.STYLE_LIGHT);
        setSizeUndefined();
        setWidth("100%");
    }

    public void setResourceContainer(Container resources) {
        removeAllComponents();
        categoryLayouts.clear();
        categoryResources.clear();
        if (resources != null && resources.size() > 0) {
            for (final Iterator<?> it = resources.getItemIds().iterator(); it
                    .hasNext();) {
                final Item resource = resources.getItem(it.next());
                // final Integer id = (Integer) resource.getItemProperty(
                // SampleDB.Resource.PROPERTY_ID_ID).getValue();
                final String category = (String) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_CATEGORY).getValue();
                final String name = (String) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_NAME).getValue();
                final String description = (String) resource.getItemProperty(
                        SampleDB.Resource.PROPERTY_ID_DESCRIPTION).getValue();
                final Button rButton = new Button(name, this);
                rButton.setStyleName("link");
                rButton.setDescription(description);
                rButton.setData(resource);
                Layout resourceLayout = categoryLayouts.get(category);
                LinkedList<Item> resourceList = categoryResources.get(category);
                if (resourceLayout == null) {
                    resourceLayout = new VerticalLayout();
                    resourceLayout.setSizeUndefined();
                    resourceLayout.setMargin(true);
                    addComponent(resourceLayout);
                    categoryLayouts.put(category, resourceLayout);
                    resourceList = new LinkedList<Item>();
                    categoryResources.put(category, resourceList);
                    final Button cButton = new Button(category + " (any)", this);
                    cButton.setStyleName("important-link");
                    cButton.setData(category);
                    resourceLayout.addComponent(cButton);
                }
                resourceLayout.addComponent(rButton);
                resourceList.add(resource);
            }
        }
    }

    // Selects one initial category, in practice randomly
    public void selectFirstCategory() {
        try {
            final String catId = categoryResources.keySet().iterator().next();
            final LinkedList<Item> res = categoryResources.get(catId);
            final Layout l = categoryLayouts.get(catId);
            final Button catB = (Button) l.getComponentIterator().next();
            setSelectedResources(res);
            catB.setStyleName("selected-link");
        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void setSelectedResources(LinkedList<Item> resources) {
        selectedResources = resources;
        fireEvent(new SelectedResourcesChangedEvent());
    }

    public LinkedList<Item> getSelectedResources() {
        return selectedResources;
    }

    public void buttonClick(ClickEvent event) {
        final Object source = event.getSource();
        if (source instanceof Button) {
            final Object data = ((Button) source).getData();
            resetStyles();
            if (data instanceof Item) {
                final LinkedList<Item> rlist = new LinkedList<Item>();
                rlist.add((Item) data);
                setSelectedResources(rlist);
            } else {
                final String category = (String) data;
                final LinkedList<Item> resources = categoryResources
                        .get(category);
                setSelectedResources(resources);
            }
            ((Button) source).setStyleName("selected-link");
        }

    }

    private void resetStyles() {
        for (final Iterator<Layout> it = categoryLayouts.values().iterator(); it
                .hasNext();) {
            final Layout lo = it.next();
            for (final Iterator<?> bit = lo.getComponentIterator(); bit
                    .hasNext();) {
                final Button b = (Button) bit.next();
                if (b.getData() instanceof Item) {
                    b.setStyleName("link");
                } else {
                    b.setStyleName("important-link");
                }
            }
        }

    }

    public class SelectedResourcesChangedEvent extends Event {
        public SelectedResourcesChangedEvent() {
            super(ResourceSelectorPanel.this);
        }
    }
}
