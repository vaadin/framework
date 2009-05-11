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
import com.vaadin.ui.Layout;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button.ClickEvent;

public class ResourceSelectorPanel extends Panel implements
        Button.ClickListener {
    private final HashMap categoryLayouts = new HashMap();
    private final HashMap categoryResources = new HashMap();

    // private Container allResources;
    private LinkedList selectedResources = null;

    public ResourceSelectorPanel(String caption) {
        super(caption, new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
        addStyleName(Panel.STYLE_LIGHT);
        setSizeUndefined();
        setWidth("100%");
    }

    public void setResourceContainer(Container resources) {
        removeAllComponents();
        categoryLayouts.clear();
        categoryResources.clear();
        if (resources != null && resources.size() > 0) {
            for (final Iterator it = resources.getItemIds().iterator(); it
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
                Layout resourceLayout = (Layout) categoryLayouts.get(category);
                LinkedList resourceList = (LinkedList) categoryResources
                        .get(category);
                if (resourceLayout == null) {
                    resourceLayout = new OrderedLayout();
                    resourceLayout.setSizeUndefined();
                    resourceLayout.setMargin(true);
                    addComponent(resourceLayout);
                    categoryLayouts.put(category, resourceLayout);
                    resourceList = new LinkedList();
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

    // Selects one initial categore, inpractice randomly
    public void selectFirstCategory() {
        try {
            final Object catId = categoryResources.keySet().iterator().next();
            final LinkedList res = (LinkedList) categoryResources.get(catId);
            final Layout l = (Layout) categoryLayouts.get(catId);
            final Button catB = (Button) l.getComponentIterator().next();
            setSelectedResources(res);
            catB.setStyleName("selected-link");
        } catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void setSelectedResources(LinkedList resources) {
        selectedResources = resources;
        fireEvent(new SelectedResourcesChangedEvent());
    }

    public LinkedList getSelectedResources() {
        return selectedResources;
    }

    public void buttonClick(ClickEvent event) {
        final Object source = event.getSource();
        if (source instanceof Button) {
            final Object data = ((Button) source).getData();
            resetStyles();
            if (data instanceof Item) {
                final LinkedList rlist = new LinkedList();
                rlist.add(data);
                setSelectedResources(rlist);
            } else {
                final String category = (String) data;
                final LinkedList resources = (LinkedList) categoryResources
                        .get(category);
                setSelectedResources(resources);
            }
            ((Button) source).setStyleName("selected-link");
        }

    }

    private void resetStyles() {
        for (final Iterator it = categoryLayouts.values().iterator(); it
                .hasNext();) {
            final Layout lo = (Layout) it.next();
            for (final Iterator bit = lo.getComponentIterator(); bit.hasNext();) {
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
