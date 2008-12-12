package com.itmill.toolkit.demo.sampler;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.demo.sampler.ActiveLink.LinkActivatedEvent;
import com.itmill.toolkit.demo.sampler.ModeSwitch.ModeSwitchEvent;
import com.itmill.toolkit.event.ItemClickEvent;
import com.itmill.toolkit.event.ItemClickEvent.ItemClickListener;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.UriFragmentUtility;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;
import com.itmill.toolkit.ui.UriFragmentUtility.FragmentChangedEvent;
import com.itmill.toolkit.ui.UriFragmentUtility.FragmentChangedListener;

public class SamplerApplication extends Application {

    // All features in one container
    private static final HierarchicalContainer allFeatures = FeatureSet.FEATURES
            .getContainer(true);

    // init() inits
    private static final String THEME_NAME = "sampler";

    // used when trying to guess theme location
    private static String APP_URL = null;

    public void init() {
        setTheme("sampler");
        setMainWindow(new SamplerWindow());
        if (APP_URL == null) {
            APP_URL = getURL().toString();
        }
    }

    /**
     * Tries to guess theme location.
     * 
     * @return
     */
    public static String getThemeBase() {
        try {
            URI uri = new URI(APP_URL + "../ITMILL/themes/" + THEME_NAME + "/");
            return uri.normalize().toString();
        } catch (Exception e) {
            System.err.println("Theme location could not be resolved:" + e);
        }
        return "/ITMILL/themes/" + THEME_NAME + "/";
    }

    // Supports multiple browser windows
    public Window getWindow(String name) {
        /*- REST code, using fragments
        if (features.getFeatureByPath(name) != null) {
            return null;
        }
        -*/
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
    public static String getPathFor(Feature f) {
        if (f == null) {
            return "";
        }
        if (allFeatures.containsId(f)) {
            String path = f.getPathName();
            f = (Feature) allFeatures.getParent(f);
            while (f != null) {
                path = f.getPathName() + "/" + path;
                f = (Feature) allFeatures.getParent(f);
            }
            return path;
        }
        return "";
    }

    /**
     * Gets the instance for the given Feature class, e.g DummyFeature.class.
     * 
     * @param clazz
     * @return
     */
    public static Feature getFeatureFor(Class clazz) {
        for (Iterator it = allFeatures.getItemIds().iterator(); it.hasNext();) {
            Feature f = (Feature) it.next();
            if (f.getClass() == clazz) {
                return f;
            }
        }
        return null;
    }

    /**
     * The main window for Sampler, contains the full application UI.
     * 
     */
    class SamplerWindow extends Window {
        private FeatureList currentList = new FeatureGrid();
        private FeatureView featureView = new FeatureView();
        private ObjectProperty currentFeature = new ObjectProperty(null,
                Feature.class);

        private OrderedLayout toggleBar = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);

        private MainArea mainArea = new MainArea();

        private SplitPanel mainSplit;
        private Tree navigationTree;
        // itmill: UA-658457-6
        private GoogleAnalytics webAnalytics = new GoogleAnalytics(
                "UA-658457-6", "none");
        // "backbutton"
        UriFragmentUtility uriFragmentUtility = new UriFragmentUtility();
        // breadcrumbs
        BreadCrumbs breadcrumbs = new BreadCrumbs();

        SamplerWindow() {
            // Main top/expanded-bottom layout
            OrderedLayout mainExpand = new OrderedLayout();
            setLayout(mainExpand);
            setSizeFull();
            mainExpand.setSizeFull();

            // topbar (navigation)
            OrderedLayout nav = new OrderedLayout(
                    OrderedLayout.ORIENTATION_HORIZONTAL);
            mainExpand.addComponent(nav);
            nav.setHeight("50px");
            nav.setWidth("100%");
            nav.setStyleName("topbar");
            nav.setSpacing(true);
            nav.setMargin(false, true, false, true);

            // Upper left logo
            Component logo = createLogo();
            nav.addComponent(logo);
            nav.setComponentAlignment(logo, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            // Breadcrumbs
            nav.addComponent(breadcrumbs);
            nav.setExpandRatio(breadcrumbs, 1);
            nav.setComponentAlignment(breadcrumbs, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            // invisible analytics -component
            nav.addComponent(webAnalytics);

            // "backbutton"
            nav.addComponent(uriFragmentUtility);
            uriFragmentUtility.addListener(new FragmentChangedListener() {
                public void fragmentChanged(FragmentChangedEvent source) {
                    String frag = source.getUriFragmentUtility().getFragment();
                    setFeature(frag);
                }
            });

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

            // "Search" combobox
            // TODO add input prompt
            Component search = createSearch();
            nav.addComponent(search);
            nav.setComponentAlignment(search, ExpandLayout.ALIGNMENT_LEFT,
                    ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

            // togglebar
            mainExpand.addComponent(toggleBar);
            toggleBar.setHeight("40px");
            toggleBar.setWidth("100%");
            toggleBar.setStyleName("togglebar");
            toggleBar.setSpacing(true);
            toggleBar.setMargin(false, true, false, true);

            // Main left/right split; hidden menu tree
            mainSplit = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
            mainSplit.setSizeFull();
            mainExpand.addComponent(mainSplit);
            mainExpand.setExpandRatio(mainSplit, 1);

            // Menu tree, initially hidden
            navigationTree = createMenuTree();
            mainSplit.addComponent(navigationTree);

            // Main Area
            mainSplit.addComponent(mainArea);

            // Show / hide tree
            Component treeSwitch = createTreeSwitch();
            toggleBar.addComponent(treeSwitch);
            toggleBar.setExpandRatio(treeSwitch, 1);
            toggleBar.setComponentAlignment(treeSwitch,
                    OrderedLayout.ALIGNMENT_RIGHT,
                    OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

            // List/grid/coverflow
            Component mode = createModeSwitch();
            toggleBar.addComponent(mode);
            toggleBar.setComponentAlignment(mode,
                    OrderedLayout.ALIGNMENT_RIGHT,
                    OrderedLayout.ALIGNMENT_VERTICAL_CENTER);

        }

        /**
         * Displays a Feature(Set)
         * 
         * @param f
         *            the Feature(Set) to show
         */
        public void setFeature(Feature f) {
            currentFeature.setValue(f);
            String path = getPathFor(f);
            webAnalytics.trackPageview(path);
            uriFragmentUtility.setFragment(path, false);
            breadcrumbs.setPath(path);
            updateFeatureList(currentList);
        }

        /**
         * Displays a Feature(Set) matching the given path, or the main view if
         * no matching Feature(Set) is found.
         * 
         * @param path
         *            the path of the Feature(Set) to show
         */
        public void setFeature(String path) {
            Feature f = FeatureSet.FEATURES.getFeatureByPath(path);
            setFeature(f);
        }

        // Handle REST -style urls
        /*- USING FRAGMENTS
        public DownloadStream handleURI(URL context, String relativeUri) {

            Feature f = features.getFeatureByPath(relativeUri);
            if (f != null) {
                setFeature(f);
                open(new ExternalResource(context));
            }
            return super.handleURI(context, relativeUri);
        }
        -*/

        /*
         * SamplerWindow helpers
         */

        private Component createSearch() {
            ComboBox search = new ComboBox();
            search.setWidth("160px");
            search.setNewItemsAllowed(false);
            search.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
            search.setNullSelectionAllowed(true);
            search.setImmediate(true);
            search.setContainerDataSource(allFeatures);
            search.addListener(new ComboBox.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Feature f = (Feature) event.getProperty().getValue();
                    if (f != null) {
                        SamplerWindow.this.setFeature(f);
                        event.getProperty().setValue(null);
                    }

                }
            });
            return search;
        }

        private Component createLogo() {
            Button logo = new Button("", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    setFeature((Feature) null);
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

        private Component createTreeSwitch() {
            ModeSwitch m = new ModeSwitch();
            m.addMode(1, "", "Hide navigation", new ThemeResource(
                    "sampler/hidetree.gif"));
            m.addMode(2, "", "Show navigation", new ThemeResource(
                    "sampler/showtree.gif"));
            m.addListener(new ModeSwitch.ModeSwitchListener() {
                public void componentEvent(Event event) {
                    if (event instanceof ModeSwitchEvent) {
                        if (((ModeSwitchEvent) event).getMode().equals(1)) {
                            mainSplit.setSplitPosition(0);
                            navigationTree.setVisible(false);
                            mainSplit.setLocked(true);
                        } else {
                            mainSplit.setSplitPosition(20);
                            mainSplit.setLocked(false);
                            navigationTree.setVisible(true);
                        }
                    }
                }
            });
            m.setMode(1);
            return m;
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
            final Tree tree = new Tree();
            tree.setImmediate(true);
            tree.setContainerDataSource(allFeatures);
            currentFeature.addListener(new Property.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Feature f = (Feature) event.getProperty().getValue();
                    Feature v = (Feature) tree.getValue();
                    if ((f != null && !f.equals(v)) || (f == null && v != null)) {
                        tree.setValue(f);
                    }
                }
            });
            for (int i = 0; i < FeatureSet.FEATURES.getFeatures().length; i++) {
                tree
                        .expandItemsRecursively(FeatureSet.FEATURES
                                .getFeatures()[i]);
            }
            tree.expandItemsRecursively(FeatureSet.FEATURES);
            tree.addListener(new Tree.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Feature f = (Feature) event.getProperty().getValue();
                    setFeature(f);
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
                toggleBar.setVisible(true);
            } else if (val instanceof FeatureSet) {
                currentList.setFeatureContainer(((FeatureSet) val)
                        .getContainer(true));
                mainArea.show(currentList);
                toggleBar.setVisible(true);
            } else {
                mainArea.show(featureView);
                featureView.setFeature(val);
                toggleBar.setVisible(false);
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
                c.setSizeFull();
                setCompositionRoot(c);
            }
        }
    }

    private class BreadCrumbs extends CustomComponent implements
            ActiveLink.LinkActivatedListener {
        HorizontalLayout layout;

        BreadCrumbs() {
            layout = new HorizontalLayout();
            layout.setSpacing(true);
            setCompositionRoot(layout);
            setStyleName("breadcrumbs");
        }

        public void setPath(String path) {
            // could be optimized: always builds path from scratch
            layout.removeAllComponents();

            { // home
                ActiveLink link = new ActiveLink("Home", new ExternalResource(
                        "#"));
                link.addListener(this);
                layout.addComponent(link);
            }

            if (path != null && !"".equals(path)) {
                String parts[] = path.split("/");
                String current = "";
                ActiveLink link = null;
                for (int i = 0; i < parts.length; i++) {
                    layout.addComponent(new Label("‣"));
                    current += (i > 0 ? "/" : "") + parts[i];
                    Feature f = FeatureSet.FEATURES.getFeatureByPath(current);
                    link = new ActiveLink(f.getName(), new ExternalResource("#"
                            + f.getPathName()));
                    link.setData(f);
                    link.addListener(this);
                    layout.addComponent(link);
                }
                if (link != null) {
                    link.setStyleName("bold");
                }
            }

        }

        public void linkActivated(LinkActivatedEvent event) {
            if (!event.isLinkOpened()) {
                ((SamplerWindow) getWindow()).setFeature((Feature) event
                        .getActiveLink().getData());
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
            setStyleName("featuretable");
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
                    ActiveLink b = new ActiveLink(
                            (feature instanceof FeatureSet ? "View section ‣"
                                    : "View sample ‣"), new ExternalResource(
                                    "#" + getPathFor(feature)));
                    b.addListener(new ActiveLink.LinkActivatedListener() {
                        public void linkActivated(LinkActivatedEvent event) {
                            if (!event.isLinkOpened()) {
                                ((SamplerWindow) getWindow())
                                        .setFeature(feature);
                            }
                        }
                    });
                    b.setStyleName(Button.STYLE_LINK);
                    return b;
                }

            });

            addListener(new ItemClickListener() {
                public void itemClick(ItemClickEvent event) {
                    Feature f = (Feature) event.getItemId();
                    if (event.getButton() == ItemClickEvent.BUTTON_MIDDLE
                            || event.isCtrlKey() || event.isShiftKey()) {
                        getWindow().open(
                                new ExternalResource(getURL() + "#"
                                        + getPathFor(f)), "_blank");
                    } else {
                        ((SamplerWindow) getWindow()).setFeature(f);
                    }
                }
            });

            setCellStyleGenerator(new CellStyleGenerator() {
                public String getStyle(Object itemId, Object propertyId) {
                    if (propertyId == null && itemId instanceof FeatureSet) {
                        if (allFeatures.isRoot(itemId)) {
                            return "section";
                        } else {
                            return "subsection";
                        }

                    }
                    return null;
                }
            });
        }

        public void setFeatureContainer(HierarchicalContainer c) {
            setContainerDataSource(c);
            setVisibleColumns(new Object[] { Feature.PROPERTY_ICON,
                    Feature.PROPERTY_NAME, "" });
            setColumnWidth(Feature.PROPERTY_ICON, 60);

        }

    }

    private class FeatureGrid extends Panel implements FeatureList {

        GridLayout grid = new GridLayout(5, 1);

        FeatureGrid() {
            setSizeFull();
            getLayout().setWidth("100%");
            grid.setWidth("100%");
            grid.setSpacing(true);
            addComponent(grid);
            setStyleName(Panel.STYLE_LIGHT);
        }

        private void newRow() {
            while (grid.getCursorX() > 0) {
                grid.space();
            }
            grid.setRows(grid.getRows() + 1);
        }

        public void setFeatureContainer(HierarchicalContainer c) {
            grid.removeAllComponents();
            Collection features = c.getItemIds();
            for (Iterator it = features.iterator(); it.hasNext();) {
                final Feature f = (Feature) it.next();
                if (f instanceof FeatureSet) {
                    newRow();
                    Label title = new Label(f.getName());
                    if (c.isRoot(f)) {
                        title.setWidth("100%");
                        title.setStyleName("section");
                        grid.addComponent(title, 0, grid.getCursorY(), grid
                                .getColumns() - 1, grid.getCursorY());
                        grid.setComponentAlignment(title,
                                GridLayout.ALIGNMENT_LEFT,
                                GridLayout.ALIGNMENT_VERTICAL_CENTER);
                    } else {
                        title.setStyleName("subsection");
                        grid.addComponent(title);
                        grid.setComponentAlignment(title,
                                GridLayout.ALIGNMENT_LEFT,
                                GridLayout.ALIGNMENT_TOP);
                    }

                } else {
                    if (grid.getCursorX() == 0) {
                        grid.space();
                    }
                    Button b = new Button();
                    b.setStyleName(Button.STYLE_LINK);
                    b.addStyleName("screenshot");
                    b.setIcon(new ClassResource(f.getClass(), f.getIconName(),
                            SamplerApplication.this));
                    b.setWidth("120px");
                    b.setHeight("120px");
                    b.setDescription("<h3>" + f.getName() + "</h3>"
                            + f.getDescription());
                    b.addListener(new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            ((SamplerWindow) getWindow()).setFeature(f);
                        }
                    });
                    grid.addComponent(b);
                }
            }
        }
    }

}
