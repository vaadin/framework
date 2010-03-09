package com.vaadin.tests.dd;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.event.Action;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptCriteria.AcceptAll;
import com.vaadin.event.dd.acceptCriteria.AcceptCriterion;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Tree.TreeDropTargetDetails;

public class DDTest6 extends TestBase {

    java.util.Random r = new java.util.Random(1);

    File[] files = new File[] { new Folder("Docs"), new Folder("Music"),
            new Folder("Images"), new File("document.doc"),
            new File("song.mp3"), new File("photo.jpg") };

    private DropHandler dh;

    private static int count;

    @Override
    protected void setup() {
        SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

        CssLayout l = new CssLayout();
        sp.setFirstComponent(l);
        CssLayout l2 = new CssLayout();
        sp.setSecondComponent(l2);

        final Tree tree1 = new Tree("Volume 1");

        BeanItemContainer<File> fs1 = new BeanItemContainer<File>(File.class);
        tree1.setContainerDataSource(fs1);
        for (int i = 0; i < files.length; i++) {
            fs1.addBean(files[i]);
            if (files[i] instanceof Folder) {
                tree1.setChildrenAllowed(files[i], true);
            } else {
                tree1.setChildrenAllowed(files[i], false);
            }
            if (i >= files.length / 2) {
                tree1.setParent(files[i], files[i - files.length / 2]);
            }
        }
        tree1.setItemCaptionPropertyId("name");
        tree1.setItemIconPropertyId("icon");

        tree1.setDragMode(TreeDragMode.NODE);

        DropHandler dropHandler = new DropHandler() {
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            public void drop(DragAndDropEvent dropEvent) {
                DataBoundTransferable transferable = (DataBoundTransferable) dropEvent
                        .getTransferable();
                TreeDropTargetDetails dropTargetData = (TreeDropTargetDetails) dropEvent
                        .getDropTargetDetails();

                tree1.setParent(transferable.getItemId(), dropTargetData
                        .getItemIdInto());

            }
        };

        tree1.setDropHandler(dropHandler);

        Handler actionHandler = new Handler() {

            private Action[] actions = new Action[] { new Action("Remove") };

            public void handleAction(Action action, Object sender, Object target) {
                ContainerHierarchicalWrapper containerDataSource = (ContainerHierarchicalWrapper) tree1
                        .getContainerDataSource();
                containerDataSource.removeItemRecursively(target);
            }

            public Action[] getActions(Object target, Object sender) {
                return actions;
            }
        };
        tree1.addActionHandler(actionHandler);

        l.addComponent(tree1);

        getLayout().setSizeFull();
        getLayout().addComponent(sp);
        TestUtils
                .injectCSS(
                        getLayout().getWindow(),
                        ""
                                + ".v-tree-node-caption-drag-top {/*border-top: none;*/} "
                                + ".v-tree-node-caption-drag-bottom {border-bottom: none ;} "
                                + ".v-tree-node-caption-drag-center {background-color: transparent;}"
                                + ".v-tree-node-caption-dragfolder { background-color: cyan;} ");

    }

    private final static ThemeResource FOLDER = new ThemeResource(
            "../runo/icons/16/folder.png");
    private final static ThemeResource DOC = new ThemeResource(
            "../runo/icons/16/document.png");

    public static class File {
        private Resource icon = DOC;
        private String name;

        public File(String fileName) {
            name = fileName;
        }

        public void setIcon(Resource icon) {
            this.icon = icon;
        }

        public Resource getIcon() {
            return icon;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Folder extends File {

        public Folder(String fileName) {
            super(fileName);
            setIcon(FOLDER);
        }

    }

    @Override
    protected String getDescription() {
        return "dd: tree and web desktop tests. TODO add traditional icon area on right side with DragAndDropWrapper and absolutelayouts + more files, auto-opening folders";
    }

    @Override
    protected Integer getTicketNumber() {
        return 119;
    }

}
