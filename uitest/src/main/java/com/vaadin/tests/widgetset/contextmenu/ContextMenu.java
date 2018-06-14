package com.vaadin.tests.widgetset.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.vaadin.event.ContextClickEvent;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.util.ReflectTools;

public class ContextMenu extends AbstractExtension {

    private final MenuItem rootItem;
    private final MenuBar innerMenubar;

    public ContextMenu(AbstractComponent parentComponent,
            boolean setAsMenuForParentComponent) {

        innerMenubar = new MenuBar();
        rootItem = innerMenubar.addItem("");
        // todo
        extend(parentComponent);

        // todo enable registerRpc(new ContextMenuServerRpc() {
        // @Override
        // public void itemClicked(int itemId, boolean menuClosed) {
        // menu.itemClicked(itemId);
        // }
        // });
        //
        // todo enable if (setAsMenuForParentComponent) {
        // setAsContextMenuOf(parentComponent);
        // }
    }

    public void setAsContextMenuOf(
            ContextClickEvent.ContextClickNotifier component) {
        // todo
    }

    // Should these also be in MenuInterface and then throw exception for
    // MenuBar?
    public MenuItem addSeparator() {
        return rootItem.addSeparator();
    }

    public MenuItem addSeparatorBefore(MenuItem itemToAddBefore) {
        return rootItem.addSeparatorBefore(itemToAddBefore);
    }

    public MenuItem addItem(String caption, Command command) {
        return rootItem.addItem(caption, command);
    }

    public MenuItem addItem(String caption, Resource icon, Command command) {
        return rootItem.addItem(caption, icon, command);
    }

    public MenuItem addItemBefore(String caption, Resource icon,
            Command command, MenuItem itemToAddBefore) {
        return rootItem.addItemBefore(caption, icon, command, itemToAddBefore);
    }

    public List<MenuItem> getItems() {
        return rootItem.getChildren();
    }

    public void removeItem(MenuItem item) {
        rootItem.removeChild(item);

    }

    public void removeItems() {
        rootItem.removeChildren();
    }

    public int getSize() {
        return rootItem.getSize();
    }

    public boolean isHtmlContentAllowed() {
        return innerMenubar.isHtmlContentAllowed();
    }

    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        innerMenubar.setHtmlContentAllowed(true);
    }

    public Registration addContextMenuOpenListener(
            ContextMenuOpenListener contextMenuOpenListener) {
        // todo
        return null;

    }

    public interface ContextMenuOpenListener
            extends EventListener, Serializable {

        public static final Method MENU_OPENED = ReflectTools.findMethod(
                ContextMenuOpenListener.class, "onContextMenuOpen",
                ContextMenuOpenEvent.class);

        public void onContextMenuOpen(ContextMenuOpenEvent event);

        public static class ContextMenuOpenEvent extends EventObject {
            private final ContextMenu contextMenu;

            private final int x;
            private final int y;

            private ContextClickEvent contextClickEvent;

            public ContextMenuOpenEvent(ContextMenu contextMenu,
                    ContextClickEvent contextClickEvent) {
                super(contextClickEvent.getComponent());

                this.contextMenu = contextMenu;
                this.contextClickEvent = contextClickEvent;
                x = contextClickEvent.getClientX();
                y = contextClickEvent.getClientY();
            }

            /**
             * @return ContextMenu that was opened.
             */
            public ContextMenu getContextMenu() {
                return contextMenu;
            }

            /**
             * @return Component which initiated the context menu open request.
             */
            public Component getSourceComponent() {
                return (Component) getSource();
            }

            /**
             * @return x-coordinate of open position.
             */
            public int getX() {
                return x;
            }

            /**
             * @return y-coordinate of open position.
             */
            public int getY() {
                return y;
            }

            public ContextClickEvent getContextClickEvent() {
                return contextClickEvent;
            }
        }
    }
}
