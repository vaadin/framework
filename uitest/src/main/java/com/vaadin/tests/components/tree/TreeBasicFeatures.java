package com.vaadin.tests.components.tree;

import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.ui.Component;
import com.vaadin.ui.IconGenerator;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

@Theme("tests-valo-disabled-animations")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeBasicFeatures extends AbstractTestUIWithLog {

    private Tree<HierarchicalTestBean> tree;
    private TreeDataProvider<HierarchicalTestBean> inMemoryDataProvider;
    private IconGenerator<HierarchicalTestBean> iconGenerator = i -> {
        switch (i.getDepth()) {
        case 0:
            return new ThemeResource("../reindeer/common/icons/bullet.png");
        case 1:
            return VaadinIcons.FLIGHT_TAKEOFF;
        case 2:
            return new ClassResource("/com/vaadin/tests/m.gif");
        default:
            return null;
        }
    };

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        tree = new Tree<>();
        setupDataProvider();
        tree.setDataProvider(inMemoryDataProvider);

        tree.addSelectionListener(
                e -> log("SelectionEvent: " + e.getAllSelectedItems()));

        tree.addExpandListener(e -> log("ExpandEvent: " + e.getExpandedItem()));
        tree.addCollapseListener(
                e -> log("ExpandEvent: " + e.getCollapsedItem()));

        layout.addComponents(createMenu(), tree);

        addComponent(layout);
    }

    private Component createMenu() {
        MenuBar menu = new MenuBar();
        menu.setErrorHandler(error -> log("Exception occured, "
                + error.getThrowable().getClass().getName() + ": "
                + error.getThrowable().getMessage()));
        MenuItem componentMenu = menu.addItem("Component", null);
        createIconMenu(componentMenu.addItem("Icons", null));
        createCaptionMenu(componentMenu.addItem("Captions", null));
        createSelectionModeMenu(componentMenu.addItem("Selection Mode", null));
        componentMenu.addItem("Item Click Listener", new Command() {

            private Registration registration;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                removeRegistration();

                if (selectedItem.isChecked()) {
                    registration = tree.addItemClickListener(
                            e -> log("ItemClick: " + e.getItem()));
                }
            }

            private void removeRegistration() {
                if (registration != null) {
                    registration.remove();
                    registration = null;
                }
            }

        }).setCheckable(true);
        MenuItem collapseAllowed = componentMenu.addItem("Collapse Allowed",
                menuItem -> tree.setItemCollapseAllowedProvider(
                        t -> menuItem.isChecked()));
        collapseAllowed.setCheckable(true);

        // Simulate the first click
        collapseAllowed.setChecked(true);
        collapseAllowed.getCommand().menuSelected(collapseAllowed);

        componentMenu
                .addItem("Style Generator",
                        menuItem -> tree.setStyleGenerator(menuItem.isChecked()
                                ? t -> "level" + t.getDepth() : t -> null))
                .setCheckable(true);

        return menu;
    }

    private void createSelectionModeMenu(MenuItem modeMenu) {
        for (SelectionMode mode : SelectionMode.values()) {
            modeMenu.addItem(mode.name(), item -> tree.setSelectionMode(mode));
        }
    }

    private void createCaptionMenu(MenuItem captionMenu) {
        captionMenu.addItem("String.valueOf",
                menu -> tree.setItemCaptionGenerator(String::valueOf));
        captionMenu
                .addItem("Custom caption",
                        menu -> tree.setItemCaptionGenerator(i -> "Id: "
                                + i.getId() + ", Depth: " + i.getDepth()
                                + ", Index: " + i.getIndex()));
    }

    private void createIconMenu(MenuItem iconMenu) {
        iconMenu.addItem("No icons",
                menu -> tree.setItemIconGenerator(t -> null));
        iconMenu.addItem("By Depth",
                menu -> tree.setItemIconGenerator(iconGenerator));
    }

    private void setupDataProvider() {
        TreeData<HierarchicalTestBean> data = new TreeData<>();

        List<Integer> ints = Arrays.asList(0, 1, 2);

        ints.stream().forEach(index -> {
            HierarchicalTestBean bean = new HierarchicalTestBean(null, 0,
                    index);
            data.addItem(null, bean);
            ints.stream().forEach(childIndex -> {
                HierarchicalTestBean childBean = new HierarchicalTestBean(
                        bean.getId(), 1, childIndex);
                data.addItem(bean, childBean);
                ints.stream()
                        .forEach(grandChildIndex -> data.addItem(childBean,
                                new HierarchicalTestBean(childBean.getId(), 2,
                                        grandChildIndex)));
            });
        });

        inMemoryDataProvider = new TreeDataProvider<>(data);
    }

}
