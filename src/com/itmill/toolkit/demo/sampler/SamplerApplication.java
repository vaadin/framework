package com.itmill.toolkit.demo.sampler;

import java.util.Collections;
import java.util.LinkedList;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.demo.sampler.features.DummyFeature;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class SamplerApplication extends Application {

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

    SplitPanel split = null;

    FeatureList currentList = null;
    FeatureView featureView = null;

    Container.Ordered allFeatures = null;
    Property currentFeature = new ObjectProperty(null, Feature.class);

    public void init() {
        setTheme("example");
        setMainWindow(new MainWindow());
    }

    private class MainWindow extends Window {

        MainWindow() {
            allFeatures = (Container.Ordered) features.getContainer(true);

            ExpandLayout main = new ExpandLayout();
            setLayout(main);
            main.setSizeFull();

            ExpandLayout nav = new ExpandLayout(
                    ExpandLayout.ORIENTATION_HORIZONTAL);
            main.addComponent(nav);
            nav.setHeight("40px");
            nav.setWidth("100%");
            nav.setStyleName("topbar");

            split = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
            split.setSizeFull();
            split.setSplitPosition(0, SplitPanel.UNITS_PIXELS);
            main.addComponent(split);
            main.expand(split);

            Button logo = new Button("", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    currentFeature.setValue(null);
                }
            });
            logo.setDescription("Home");
            logo.setStyleName(Button.STYLE_LINK);
            logo.setIcon(new ThemeResource("sampler/logo.png"));
            logo.setWidth("160px");
            nav.addComponent(logo);
            nav.setComponentAlignment(logo, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            Button b = new Button("< Previous", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object prev = allFeatures.prevItemId(curr);
                    while (prev != null && prev instanceof FeatureSet) {
                        prev = allFeatures.prevItemId(prev);
                    }
                    currentFeature.setValue(prev);

                }
            });
            nav.addComponent(b);
            nav.setComponentAlignment(b, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            b = new Button("Next >", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object next = allFeatures.nextItemId(curr);
                    while (next != null && next instanceof FeatureSet) {
                        next = allFeatures.nextItemId(next);
                    }
                    currentFeature.setValue(next);

                }
            });
            nav.addComponent(b);
            nav.setComponentAlignment(b, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            b = new Button(":: | \\⊡/ | ≣");
            nav.addComponent(b);
            nav.expand(b);
            nav.setComponentAlignment(b, ExpandLayout.ALIGNMENT_RIGHT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            Tree tree = new Tree();
            tree.setImmediate(true);
            split.addComponent(tree);
            tree.setContainerDataSource(allFeatures);
            tree.setPropertyDataSource(currentFeature);
            for (int i = 0; i < features.getFeatures().length; i++) {
                tree.expandItemsRecursively(features.getFeatures()[i]);
            }
            tree.expandItemsRecursively(features);
            tree.addListener(new Table.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Feature val = (Feature) event.getProperty().getValue();
                    if (val == null) {
                        currentList.setFeatureContainer(features
                                .getContainer(true));
                        if (currentList.getParent() != split) {
                            split.replaceComponent(featureView, currentList);
                        }

                    } else if (val instanceof FeatureSet) {
                        currentList.setFeatureContainer(((FeatureSet) val)
                                .getContainer(false));
                        if (currentList.getParent() != split) {
                            split.replaceComponent(featureView, currentList);
                        }
                    } else {
                        if (featureView.getParent() != split) {
                            split.replaceComponent(currentList, featureView);
                        }
                        featureView.setFeature(val);
                    }
                }
            });

            FeatureTable tbl = new FeatureTable();
            tbl.setFeatureContainer(allFeatures);
            currentList = tbl;

            split.addComponent(tbl);

            featureView = new FeatureView();

            Feature f = features.getFeatureByPath("Components/c/DummyFeature");
            tree.setValue(f);
        }
    }

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
                    Button b = new Button(
                            itemId instanceof FeatureSet ? "See samples ‣"
                                    : "See sample ‣");
                    b.setData(itemId);
                    b.addListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            currentFeature
                                    .setValue(event.getButton().getData());
                        }
                    });
                    b.setStyleName(Button.STYLE_LINK);
                    return b;
                }

            });
        }

        public void setFeatureContainer(Container c) {
            setContainerDataSource(c);
            setVisibleColumns(new Object[] { Feature.PROPERTY_ICON,
                    Feature.PROPERTY_NAME, Feature.PROPERTY_DESCRIPTION, "" });
            setColumnWidth(Feature.PROPERTY_ICON, 60);
            setColumnWidth(Feature.PROPERTY_NAME, 150);

        }

    }

    static class FeatureSet extends Feature {

        String name;

        Feature[] content;

        HierarchicalContainer container = null;

        FeatureSet(String name, Feature[] content) {
            this.name = name;
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
                    if (fs[i].getName().equals(part)) {
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

        Container.Hierarchical getContainer(boolean recurse) {
            if (container == null) {
                container = new HierarchicalContainer();
                container.addContainerProperty(PROPERTY_NAME, String.class, "");
                container.addContainerProperty(PROPERTY_DESCRIPTION,
                        String.class, "");
                // fill
                addFeatures(this, container, recurse);
            }
            return container;
        }

        private void addFeatures(FeatureSet f, Container.Hierarchical c,
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIconName() {
            return "FeatureSet.png";
        }

    }

    interface FeatureList extends Component {
        public void setFeatureContainer(Container c);
    }

}
