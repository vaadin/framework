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
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ComboBox;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.PopupView;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.Table;
import com.itmill.toolkit.ui.Tree;
import com.itmill.toolkit.ui.UriFragmentUtility;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;
import com.itmill.toolkit.ui.PopupView.PopupVisibilityEvent;
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

    @Override
    public void init() {
        setTheme("sampler");
        setMainWindow(new SamplerWindow());
        APP_URL = getURL().toString();
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
    @Override
    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
            w = new SamplerWindow();
            w.setName(name);
            addWindow(w);
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

        private MainArea mainArea = new MainArea();

        private ModeSwitch mode;

        private SplitPanel mainSplit;
        private Tree navigationTree;
        // itmill: UA-658457-6
        private GoogleAnalytics webAnalytics = new GoogleAnalytics(
                "UA-658457-6", "none");
        // "backbutton"
        UriFragmentUtility uriFragmentUtility = new UriFragmentUtility();

        // breadcrumbs
        BreadCrumbs breadcrumbs = new BreadCrumbs();

        Button previousSample;
        Button nextSample;

        SamplerWindow() {
            // Main top/expanded-bottom layout
            VerticalLayout mainExpand = new VerticalLayout();
            setLayout(mainExpand);
            setSizeFull();
            mainExpand.setSizeFull();

            // topbar (navigation)
            HorizontalLayout nav = new HorizontalLayout();
            mainExpand.addComponent(nav);
            nav.setHeight("44px");
            nav.setWidth("100%");
            nav.setStyleName("topbar");
            nav.setSpacing(true);
            nav.setMargin(false, true, false, false);

            // Upper left logo
            Component logo = createLogo();
            nav.addComponent(logo);
            nav.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

            // Breadcrumbs
            nav.addComponent(breadcrumbs);
            nav.setExpandRatio(breadcrumbs, 1);
            nav.setComponentAlignment(breadcrumbs, Alignment.MIDDLE_LEFT);

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

            // List/grid/coverflow
            mode = createModeSwitch();
            mode.setMode(currentList);
            nav.addComponent(mode);
            nav.setComponentAlignment(mode, Alignment.MIDDLE_LEFT);

            // Layouts for top area buttons
            HorizontalLayout quicknav = new HorizontalLayout();
            HorizontalLayout arrows = new HorizontalLayout();
            nav.addComponent(quicknav);
            nav.addComponent(arrows);
            nav.setComponentAlignment(quicknav, Alignment.MIDDLE_LEFT);
            nav.setComponentAlignment(arrows, Alignment.MIDDLE_LEFT);
            quicknav.setStyleName("segment");
            arrows.setStyleName("segment");

            // Previous sample
            previousSample = createPrevButton();
            arrows.addComponent(previousSample);
            // Next sample
            nextSample = createNextButton();
            arrows.addComponent(nextSample);
            // "Search" combobox
            // TODO add input prompt
            Component search = createSearch();
            quicknav.addComponent(search);

            // Main left/right split; hidden menu tree
            mainSplit = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
            mainSplit.setSizeFull();
            mainSplit.setStyleName("main-split");
            mainExpand.addComponent(mainSplit);
            mainExpand.setExpandRatio(mainSplit, 1);

            // Menu tree, initially hidden
            navigationTree = createMenuTree();
            mainSplit.setFirstComponent(navigationTree);

            // Main Area
            mainSplit.setSecondComponent(mainArea);

            // Show / hide tree
            Component treeSwitch = createTreeSwitch();
            quicknav.addComponent(treeSwitch);

            addListener(new CloseListener() {
                public void windowClose(CloseEvent e) {
                    if (getMainWindow() != SamplerWindow.this) {
                        SamplerApplication.this
                                .removeWindow(SamplerWindow.this);
                    }
                }
            });
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

            previousSample.setEnabled(f != null);
            nextSample.setEnabled(!allFeatures.isLastId(f));

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

        /*
         * SamplerWindow helpers
         */

        private Component createSearch() {
            final ComboBox search = new ComboBox();
            search.setWidth("160px");
            search.setNewItemsAllowed(false);
            search.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
            search.setNullSelectionAllowed(true);
            search.setImmediate(true);
            search.setContainerDataSource(allFeatures);
            for (Iterator it = allFeatures.getItemIds().iterator(); it
                    .hasNext();) {
                Object id = it.next();
                if (id instanceof FeatureSet) {
                    search.setItemIcon(id, new ClassResource("folder.gif",
                            SamplerApplication.this));
                }
            }
            search.addListener(new ComboBox.ValueChangeListener() {
                public void valueChange(ValueChangeEvent event) {
                    Feature f = (Feature) event.getProperty().getValue();
                    if (f != null) {
                        SamplerWindow.this.setFeature(f);
                        event.getProperty().setValue(null);
                    }

                }
            });
            // TODO add icons for section/sample
            /*
             * PopupView pv = new PopupView("", search) { public void
             * changeVariables(Object source, Map variables) {
             * super.changeVariables(source, variables); if (isPopupVisible()) {
             * search.focus(); } } };
             */
            PopupView pv = new PopupView("<span></span>", search);
            pv.addListener(new PopupView.PopupVisibilityListener() {
                public void popupVisibilityChange(PopupVisibilityEvent event) {
                    if (event.isPopupVisible()) {
                        search.focus();
                    }
                }
            });
            pv.setStyleName("quickjump");
            pv.setDescription("Quick jump");

            return pv;
        }

        private Component createLogo() {
            Button logo = new Button("", new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    setFeature((Feature) null);
                }
            });
            logo.setDescription("↶ Home");
            logo.setStyleName(Button.STYLE_LINK);
            logo.addStyleName("logo");
            logo.setIcon(new ThemeResource("sampler/sampler.png"));
            return logo;
        }

        private Button createNextButton() {
            Button b = new Button("", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object next = allFeatures.nextItemId(curr);
                    while (next != null && next instanceof FeatureSet) {
                        next = allFeatures.nextItemId(next);
                    }
                    if (next != null) {
                        currentFeature.setValue(next);
                    } else {
                        // could potentially occur if there is an empty section
                        showNotification("Last sample");
                    }
                }
            });
            b.setStyleName("next");
            b.setDescription("Jump to the next sample");
            return b;
        }

        private Button createPrevButton() {
            Button b = new Button("", new ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Object curr = currentFeature.getValue();
                    Object prev = allFeatures.prevItemId(curr);
                    while (prev != null && prev instanceof FeatureSet) {
                        prev = allFeatures.prevItemId(prev);
                    }
                    currentFeature.setValue(prev);
                }
            });
            b.setEnabled(false);
            b.setStyleName("previous");
            b.setDescription("Jump to the previous sample");
            return b;
        }

        private Component createTreeSwitch() {
            final Button b = new Button();
            b.setStyleName("tree-switch");
            b.setDescription("Toggle sample tree visibility");
            b.addListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    if (b.getStyleName().contains("down")) {
                        b.removeStyleName("down");
                        mainSplit.setSplitPosition(0);
                        navigationTree.setVisible(false);
                        mainSplit.setLocked(true);
                    } else {
                        b.addStyleName("down");
                        mainSplit.setSplitPosition(20);
                        mainSplit.setLocked(false);
                        navigationTree.setVisible(true);
                    }
                }
            });
            mainSplit.setSplitPosition(0);
            navigationTree.setVisible(false);
            mainSplit.setLocked(true);
            return b;
        }

        private ModeSwitch createModeSwitch() {
            ModeSwitch m = new ModeSwitch();
            m.addMode(currentList, "", "View as Icons", new ThemeResource(
                    "sampler/grid.gif"));
            /*- no CoverFlow yet
            m.addMode(coverFlow, "", "View as Icons", new ThemeResource(
                    "sampler/flow.gif"));
             */
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
            return m;
        }

        private Tree createMenuTree() {
            final Tree tree = new Tree();
            tree.setImmediate(true);
            tree.setStyleName("menu");
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
                mode.setVisible(true);
            } else if (val instanceof FeatureSet) {
                currentList.setFeatureContainer(((FeatureSet) val)
                        .getContainer(true));
                mainArea.show(currentList);
                mode.setVisible(true);
            } else {
                mainArea.show(featureView);
                featureView.setFeature(val);
                mode.setVisible(false);
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
                // c.setSizeFull();
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
                            + getPathFor(f)));
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
                            if (f instanceof FeatureSet) {
                                // no icon for sections
                                return null;
                            }
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

        GridLayout grid = new GridLayout(11, 1);

        FeatureGrid() {
            setSizeFull();
            getLayout().setSizeUndefined();
            // grid.setWidth("100%");
            grid.setSpacing(true);
            addComponent(grid);
            setStyleName(Panel.STYLE_LIGHT);
        }

        public void setFeatureContainer(HierarchicalContainer c) {
            grid.removeAllComponents();
            Collection features = c.getItemIds();
            for (Iterator it = features.iterator(); it.hasNext();) {
                final Feature f = (Feature) it.next();
                if (f instanceof FeatureSet) {
                    grid.newLine();
                    Label title = new Label(f.getName());
                    if (c.isRoot(f)) {
                        title.setWidth("100%");
                        title.setStyleName("section");
                        grid.setRows(grid.getCursorY() + 1);
                        grid.addComponent(title, 0, grid.getCursorY(), grid
                                .getColumns() - 1, grid.getCursorY());
                        grid
                                .setComponentAlignment(title,
                                        Alignment.MIDDLE_LEFT);
                    } else {
                        title.setStyleName("subsection");
                        grid.addComponent(title);
                        grid
                                .setComponentAlignment(title,
                                        Alignment.MIDDLE_LEFT);
                    }

                } else {
                    if (grid.getCursorX() == 0) {
                        grid.space();
                    }
                    Button b = new Button();
                    b.setStyleName(Button.STYLE_LINK);
                    b.addStyleName("screenshot");
                    b.setIcon(new ClassResource(f.getClass(), "75-"
                            + f.getIconName(), SamplerApplication.this));
                    b.setWidth("75px");
                    b.setHeight("75px");
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
