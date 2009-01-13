package com.itmill.toolkit.demo.sampler;

import java.util.Collections;
import java.util.LinkedList;

import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.demo.sampler.features.accordions.AccordionDisabled;
import com.itmill.toolkit.demo.sampler.features.accordions.AccordionIcons;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonLink;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonPush;
import com.itmill.toolkit.demo.sampler.features.buttons.ButtonSwitch;
import com.itmill.toolkit.demo.sampler.features.commons.Icons;
import com.itmill.toolkit.demo.sampler.features.commons.Tooltips;
import com.itmill.toolkit.demo.sampler.features.layouts.HorizontalLayoutBasic;
import com.itmill.toolkit.demo.sampler.features.layouts.LayoutAlignment;
import com.itmill.toolkit.demo.sampler.features.layouts.LayoutSpacing;
import com.itmill.toolkit.demo.sampler.features.layouts.VerticalLayoutBasic;
import com.itmill.toolkit.demo.sampler.features.link.LinkCurrentWindow;
import com.itmill.toolkit.demo.sampler.features.link.LinkNoDecorations;
import com.itmill.toolkit.demo.sampler.features.link.LinkSizedWindow;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationCustom;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationError;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationHumanized;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationTray;
import com.itmill.toolkit.demo.sampler.features.notifications.NotificationWarning;
import com.itmill.toolkit.demo.sampler.features.panels.PanelBasic;
import com.itmill.toolkit.demo.sampler.features.panels.PanelLight;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxContains;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxNewItems;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxPlain;
import com.itmill.toolkit.demo.sampler.features.selects.ComboBoxStartsWith;
import com.itmill.toolkit.demo.sampler.features.selects.ListSelectMultiple;
import com.itmill.toolkit.demo.sampler.features.selects.ListSelectSingle;
import com.itmill.toolkit.demo.sampler.features.selects.NativeSelection;
import com.itmill.toolkit.demo.sampler.features.selects.TwinColumnSelect;
import com.itmill.toolkit.demo.sampler.features.tabsheets.TabSheetDisabled;
import com.itmill.toolkit.demo.sampler.features.tabsheets.TabSheetIcons;
import com.itmill.toolkit.demo.sampler.features.tabsheets.TabSheetScrolling;
import com.itmill.toolkit.demo.sampler.features.windows.WindowChild;
import com.itmill.toolkit.demo.sampler.features.windows.WindowNativeNew;
import com.itmill.toolkit.demo.sampler.features.windows.WindowNativeShared;

/**
 * Contains the FeatureSet implementation and the structure for the feature
 * 'tree'.
 * <p>
 * Each set is implemented as it's own class to facilitate linking to sets in
 * the same way as linking to individual features.
 * </p>
 * 
 */
public class FeatureSet extends Feature {

    /*
     * MAIN structure; root is always a FeatureSet that is not shown
     */
    static final FeatureSet FEATURES = new FeatureSet("All", new Feature[] {
    // Main sets
            new Blueprints(), //
            new Components(), //
    });

    /*
     * TOP LEVEL
     */
    public static class Blueprints extends FeatureSet {
        public Blueprints() {
            super("Blueprints", new Feature[] {
            // Blueprints
                    new ProminentPrimaryAction(), //
                    });
        }
    }

    public static class Components extends FeatureSet {
        public Components() {
            super("Components", new Feature[] {
            //
                    new Commons(), //
                    new Buttons(), //
                    new Links(), //
                    new Notifications(), //
                    new Selects(), //
                    new Layouts(), //
                    new Tabsheets(), //
                    new Accordions(), //
                    new Panels(), //
                    new Windows(), //
            });
        }
    }

    /*
     * LEVEL 2
     */
    public static class Buttons extends FeatureSet {
        public Buttons() {
            super("Buttons", new Feature[] {
            //
                    new ButtonPush(), // basic
                    new ButtonLink(), // link
                    new ButtonSwitch(), // switch/checkbox

            });
        }
    }

    public static class Links extends FeatureSet {
        public Links() {
            super("Links", new Feature[] {
            //
                    new LinkCurrentWindow(), // basic
                    new LinkNoDecorations(), // new win
                    new LinkSizedWindow(), // new win

            });
        }
    }

    public static class Notifications extends FeatureSet {
        public Notifications() {
            super("Notifications", new Feature[] {
            //
                    new NotificationHumanized(), // humanized
                    new NotificationWarning(), // warning
                    new NotificationTray(), // tray
                    new NotificationError(), // error
                    new NotificationCustom(), // error
            });
        }
    }

    public static class Commons extends FeatureSet {
        public Commons() {
            super("Commons", new Feature[] {
            //
                    new Tooltips(), // tooltips
                    new Icons(), // icons
            });
        }
    }

    public static class Selects extends FeatureSet {
        public Selects() {
            super("Selects", new Feature[] {
            //
                    new ListSelectSingle(), //  
                    new ListSelectMultiple(), //
                    new TwinColumnSelect(), //
                    new NativeSelection(), //
                    new ComboBoxPlain(), //
                    new ComboBoxStartsWith(), //
                    new ComboBoxContains(), //
                    new ComboBoxNewItems(), //

            });
        }
    }

    public static class Layouts extends FeatureSet {
        public Layouts() {
            super("Layouts", new Feature[] {
            //
                    new VerticalLayoutBasic(), //
                    new HorizontalLayoutBasic(), //
                    new LayoutSpacing(), //
                    new LayoutAlignment(), //
            });
        }
    }

    public static class Tabsheets extends FeatureSet {
        public Tabsheets() {
            super("Tabsheets", new Feature[] {
            //        
                    new TabSheetIcons(), //
                    new TabSheetScrolling(), //
                    new TabSheetDisabled(), //
            });
        }
    }

    public static class Accordions extends FeatureSet {
        public Accordions() {
            super("Accordions", new Feature[] {
            //        
                    new AccordionIcons(), //
                    new AccordionDisabled(), //
            });
        }
    }

    public static class Panels extends FeatureSet {
        public Panels() {
            super("Panels", new Feature[] {
            //        
                    new PanelBasic(), //
                    new PanelLight(), //
            });
        }
    }

    public static class Windows extends FeatureSet {
        public Windows() {
            super("Windows", new Feature[] {
            //        
                    new WindowNativeShared(), //
                    new WindowNativeNew(), //
                    new WindowChild(), //
            });
        }
    }

    // ----------------------------------------------------------
    /*
     * FeatureSet implementation follows.
     */

    private String pathname;

    private String name;

    private String desc;

    private String icon = "folder.gif";

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
                if (fs[i].getPathName().equalsIgnoreCase(part)) {
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
            container.addContainerProperty(PROPERTY_DESCRIPTION, String.class,
                    "");
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

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public String getPathName() {
        return pathname;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIconName() {
        return icon;
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return null;
    }

    @Override
    public Class[] getRelatedFeatures() {
        return null;
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return null;
    }

}
