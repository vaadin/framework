package com.itmill.toolkit.demo.sampler;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.demo.sampler.ModeSwitch.ModeSwitchEvent;
import com.itmill.toolkit.demo.sampler.features.DummyFeature;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class SamplerApplication extends Application {

    // Main structure, root is always a FeatureSet that is not shown
    private static final FeatureSet features = new FeatureSet("All",
            new Feature[] {
            // Main sets
                    new FeatureSet("Patterns", new Feature[] {
                    // Patterns
                            new DummyFeature(), //
                            new DummyFeature(), //

                            new FeatureSet("c", new Feature[] {
                            // some group of patterns
                                    new DummyFeature(), //
                                    new DummyFeature(), //
                            }),

                            new FeatureSet("d", new Feature[] {
                            // another group of patterns
                                    new DummyFeature(), //
                                    new DummyFeature(), //
                            }),

                    }),

                    new FeatureSet("Components", new Feature[] {
                    // Patterns
                            new FeatureSet("öö", new Feature[] {
                            // some group of patterns
                                    new DummyFeature(), //
                                    new DummyFeature(), //
                            }), new DummyFeature(), //
                            new DummyFeature(), //

                            new FeatureSet("c", new Feature[] {
                            // some group of patterns
                                    new DummyFeature(), //
                                    new DummyFeature(), //
                            }),

                            new FeatureSet("d", new Feature[] {
                            // another group of patterns
                                    new DummyFeature(), //
                                    new DummyFeature(), //
                            }),

                    }),

            });

    // All features in one container
    private static final HierarchicalContainer allFeatures = features
            .getContainer(true);

    public void init() {
        setTheme("example");
        setMainWindow(new SamplerWindow());
    }

    // Supports multiple browser windows
    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
            w = new SamplerWindow();
            w.setName(name);
            addWindow(w);
            // secondary windows will support normal reload if this is
            // enabled, but the url gets ugly:
            // w.open(new ExternalResource(w.getURL()));

        }
        return w;
    }

    /**
     * Gets absolute path for given Feature
     * 
     * @param f
     *            the Feature whose path to get, of null if not found
     * @return the path of the Feature
     */
    String getPathFor(Feature f) {
        if (allFeatures.containsId(f)) {
            String path = f.getPathName();
            f = (Feature) allFeatures.getParent(f);
            while (f != null) {
                path = f.getPathName() + "/" + path;
            }
            return path;
        }
        return null;
    }

    /**
     * The main window for Sampler, contains the full application UI.
     * 
     */
    private class SamplerWindow extends Window {
        private FeatureList currentList = new FeatureGrid();
        private FeatureView featureView = new FeatureView();
        private Property currentFeature = new ObjectProperty(null,
                Feature.class);

        private MainArea mainArea = new MainArea();

        SamplerWindow() {
            // Main top/expanded-bottom layout
            ExpandLayout mainExpand = new ExpandLayout();
            setLayout(mainExpand);
            mainExpand.setSizeFull();

            // topbar (navigation)
            ExpandLayout nav = new ExpandLayout(
                    ExpandLayout.ORIENTATION_HORIZONTAL);
            mainExpand.addComponent(nav);
            nav.setHeight("40px");
            nav.setWidth("100%");
            nav.setStyleName("topbar");

            // Upper left logo
            Component logo = createLogo();
            nav.addComponent(logo);
            nav.setComponentAlignment(logo, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);
            nav.expand(logo);

            // Previous sample
            Button b = createPrevButton();
            nav.addComponent(b);
            nav.setComponentAlignment(b, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);
            // Next sample
            b = createNextButton();
            nav.addComponent(b);
            nav.setComponentAlignment(b, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            // Main left/right split; hidden menu tree
            SplitPanel split = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
            split.setSizeFull();
            split.setSplitPosition(0, SplitPanel.UNITS_PIXELS);
            mainExpand.addComponent(split);
            mainExpand.expand(split);

            // Menu tree, initially hidden
            Tree tree = createMenuTree();
            split.addComponent(tree);

            // Main Area
            split.addComponent(mainArea);

            // List/grid/coverflow
            Component mode = createModeSwitch();
            nav.addComponent(mode);
            nav.setComponentAlignment(mode, ExpandLayout.ALIGNMENT_RIGHT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        }

        /**
         * Displays a Feature(Set)
         * 
         * @param f
         *            the Feature(Set) to show
         */
        public void setFeature(Feature f) {
            currentFeature.setValue(f);
        }

        /**
         * Displays a Feature(Set) matching the given path, or the main view if
         * no matching Feature(Set) is found.
         * 
         * @param path
         *            the path of the Feature(Set) to show
         */
        public void setFeature(String path) {
            Feature f = features.getFeatureByPath(path);
            setFeature(f);
        }

        // Handle REST -style urls
        public DownloadStream handleURI(URL context, String relativeUri) {
            Feature f = features.getFeatureByPath(relativeUri);
            if (f != null) {
                setFeature(f);
                open(new ExternalResource(context));
            }
            return super.handleURI(context, relativeUri);
        }

        /*
         * SamplerWindow helpers
         */

        private Component createLogo() {
            Button logo = new Button("", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    currentFeature.setValue(null);
                }
            });
            logo.setDescription("↶ Home");
            logo.setStyleName(Button.STYLE_LINK);
            logo.setIcon(new ThemeResource("sampler/logo.png"));
            logo.setWidth("160px");
            return logo;
        }

        private Button createNextButton() {
            Button b = new Button("Next sample →", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object next = allFeatures.nextItemId(curr);
                    while (next != null && next instanceof FeatureSet) {
                        next = allFeatures.nextItemId(next);
                    }
                    currentFeature.setValue(next);
                }
            });
            b.setStyleName(Button.STYLE_LINK);
            return b;
        }

        private Button createPrevButton() {
            Button b = new Button("← Previous sample", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object prev = allFeatures.prevItemId(curr);
                    while (prev != null && prev instanceof FeatureSet) {
                        prev = allFeatures.prevItemId(prev);
                    }
                    currentFeature.setValue(prev);
                }
            });
            b.setStyleName(Button.STYLE_LINK);
            return b;
        }

        private Component createModeSwitch() {
            ModeSwitch m = new ModeSwitch();
            m.addMode(currentList, "", "View as Icons", new ThemeResource(
                    "sampler/grid.gif"));
            m.addMode(new FeatureGrid(), "", "View as Icons",
                    new ThemeResource("sampler/flow.gif"));
            m.addMode(new FeatureTable(), "", "View as List",
                    new ThemeResource("sampler/list.gif"));
            m.addListener(new ModeSwitch.ModeSwitchListener() {
                public void componentEvent(Event event) {
                    if (event instanceof ModeSwitchEvent) {
                        updateFeatureList((FeatureList) ((ModeSwitchEvent) event)
                                .getMode());
                    }
                }
            });
            m.setMode(currentList);
            return m;
        }

        private Tree createMenuTree() {
            Tree tree = new Tree();
            tree.setImmediate(true);
            tree.setContainerDataSource(allFeatures);
            tree.setPropertyDataSource(currentFeature);
            for (int i = 0; i < features.getFeatures().length; i++) {
                tree.expandItemsRecursively(features.getFeatures()[i]);
            }
            tree.expandItemsRecursively(features);
            tree.addListener(new Table.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    updateFeatureList(currentList);
                }
            });
            return tree;
        }

        private void updateFeatureList(FeatureList list) {
            currentList = list;
            Feature val = (Feature) currentFeature.getValue();
            if (val == null) {
                currentList.setFeatureContainer(allFeatures);
                mainArea.show(currentList);
            } else if (val instanceof FeatureSet) {
                currentList.setFeatureContainer(((FeatureSet) val)
                        .getContainer(true));
                mainArea.show(currentList);
            } else {
                mainArea.show(featureView);
                featureView.setFeature(val);
            }

        }

    }

    /**
     * Main area used to show Feature of FeatureList. In effect a one-component
     * container, to minimize repaints.
     */
    private class MainArea extends CustomComponent {
        MainArea() {
            setSizeFull();
            setCompositionRoot(new Label());
        }

        public void show(Component c) {
            if (getCompositionRoot() != c) {
                setCompositionRoot(c);
            }
        }
    }

    /**
     * Components capable of listing Features should implement this.
     * 
     */
    interface FeatureList extends Component {
        /**
         * Shows the given Features
         * 
         * @param c
         *            Container with Features to show.
         */
        public void setFeatureContainer(HierarchicalContainer c);
    }

    /**
     * Table -mode FeatureList. Displays the features in a Table.
     */
    private class FeatureTable extends Table implements FeatureList {
        FeatureTable() {
            alwaysRecalculateColumnWidths = true;
            setSelectable(false);
            setSizeFull();
            setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
            addGeneratedColumn(Feature.PROPERTY_ICON,
                    new Table.ColumnGenerator() {
                        public Component generateCell(Table source,
                                Object itemId, Object columnId) {
                            Feature f = (Feature) itemId;
                            Resource res = new ClassResource(f.getClass(), f
                                    .getIconName(), SamplerApplication.this);
                            Embedded emb = new Embedded("", res);
                            emb.setWidth("48px");
                            emb.setHeight("48px");
                            emb.setType(Embedded.TYPE_IMAGE);
                            return emb;
                        }

                    });
            addGeneratedColumn("", new Table.ColumnGenerator() {
                public Component generateCell(Table source, Object itemId,
                        Object columnId) {
                    final Feature feature = (Feature) itemId;
                    Button b = new Button(
                            feature instanceof FeatureSet ? "See samples ‣"
                                    : "See sample ‣");
                    b.addListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            ((SamplerWindow) getWindow()).setFeature(feature);

                        }
                    });
                    b.setStyleName(Button.STYLE_LINK);
                    return b;
                }

            });
        }

        public void setFeatureContainer(HierarchicalContainer c) {
            setContainerDataSource(c);
            setVisibleColumns(new Object[] { Feature.PROPERTY_ICON,
                    Feature.PROPERTY_NAME, Feature.PROPERTY_DESCRIPTION, "" });
            setColumnWidth(Feature.PROPERTY_ICON, 60);
            setColumnWidth(Feature.PROPERTY_NAME, 150);

        }

    }

    private class FeatureGrid extends GridLayout implements FeatureList {

        FeatureGrid() {
            super(5, 1);
            setWidth("100%");
        }

        private void newRow() {
            while (getCursorX() > 0) {
                space();
            }
        }

        public void setFeatureContainer(HierarchicalContainer c) {
            removeAllComponents();
            Collection features = c.getItemIds();
            for (Iterator it = features.iterator(); it.hasNext();) {
                final Feature f = (Feature) it.next();
                if (f instanceof FeatureSet) {
                    newRow();
                    addComponent(new Label(f.getName()));
                    if (c.isRoot(f)) {
                        newRow();
                    }
                } else {
                    Button b = new Button();
                    b.setWidth("130px");
                    b.setHeight("130px");
                    b.setSizeFull();
                    b.setStyleName(Button.STYLE_LINK);
                    b.setIcon(new ClassResource(f.getClass(), f.getIconName(),
                            SamplerApplication.this));
                    b.setDescription("<h3>" + f.getName() + "</h3>"
                            + f.getDescription());
                    b.addListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            ((SamplerWindow) getWindow()).setFeature(f);
                        }
                    });
                    addComponent(b);
                }
            }
        }
    }

    /**
     * A set of features.
     */
    static class FeatureSet extends Feature {

        private String pathname;

        private String name;

        private String desc;

        private String icon = "FeatureSet.png";

        private Feature[] content;

        private HierarchicalContainer container = null;

        private boolean containerRecursive = false;

        FeatureSet(String pathname, Feature[] content) {
            this(pathname, pathname, "", content);
        }

        FeatureSet(String pathname, String name, Feature[] content) {
            this(pathname, name, "", content);
        }

        FeatureSet(String pathname, String name, String desc, Feature[] content) {
            this.pathname = pathname;
            this.name = name;
            this.desc = desc;
            this.content = content;
        }

        Feature[] getFeatures() {
            return content;
        }

        Feature getFeatureByPath(String path) {
            LinkedList<String> parts = new LinkedList<String>();
            Collections.addAll(parts, path.split("/"));
            FeatureSet f = this;
            while (f != null) {
                Feature[] fs = f.getFeatures();
                f = null; // break while if no new found
                String part = parts.remove(0);
                for (int i = 0; i < fs.length; i++) {
                    if (fs[i].getPathName().equals(part)) {
                        if (parts.isEmpty()) {
                            return fs[i];
                        } else if (fs[i] instanceof FeatureSet) {
                            f = (FeatureSet) fs[i];
                            break;
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;
        }

        HierarchicalContainer getContainer(boolean recurse) {
            if (container == null || containerRecursive != recurse) {
                container = new HierarchicalContainer();
                container.addContainerProperty(PROPERTY_NAME, String.class, "");
                container.addContainerProperty(PROPERTY_DESCRIPTION,
                        String.class, "");
                // fill
                addFeatures(this, container, recurse);
            }
            return container;
        }

        private void addFeatures(FeatureSet f, HierarchicalContainer c,
                boolean recurse) {
            Feature[] features = f.getFeatures();
            for (int i = 0; i < features.length; i++) {
                Item item = c.addItem(features[i]);
                Property property = item.getItemProperty(PROPERTY_NAME);
                property.setValue(features[i].getName());
                property = item.getItemProperty(PROPERTY_DESCRIPTION);
                property.setValue(features[i].getDescription());
                if (recurse) {
                    c.setParent(features[i], f);
                    if (features[i] instanceof FeatureSet) {
                        addFeatures((FeatureSet) features[i], c, recurse);
                    }
                }
                if (!(features[i] instanceof FeatureSet)) {
                    c.setChildrenAllowed(features[i], false);
                }
            }
        }

        public String getDescription() {
            return desc;
        }

        public String getPathName() {
            return pathname;
        }

        public String getName() {
            return name;
        }

        public String getIconName() {
            return icon;
        }

    }

}
