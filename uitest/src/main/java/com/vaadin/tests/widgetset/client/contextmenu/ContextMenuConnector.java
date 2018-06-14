package com.vaadin.tests.widgetset.client.contextmenu;

import java.util.logging.Logger;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VMenuBar.CustomMenuItem;
import com.vaadin.client.ui.menubar.MenuBarConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.contextmenu.ContextMenu;

@SuppressWarnings("serial")
@Connect(ContextMenu.class)
public class ContextMenuConnector extends AbstractExtensionConnector {
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger("ContextMenuConnector");

    private MenuBarConnector innerConnector = new MenuBarConnector() {
        @Override
        public VContextMenuBar getWidget() {
            // todo another class?
            return (VContextMenuBar) super.getWidget();
        }
    };
    private VContextMenuBar contextMenuWidget = (VContextMenuBar) innerConnector
            .getWidget();

    @Override
    public ContextMenuState getState() {
        return (ContextMenuState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
        innerConnector.onStateChanged(stateChangeEvent);
    }

    @Override
    protected void init() {
        super.init();
        innerConnector.doInit(getConnectorId(), getConnection());
        contextMenuWidget.setPixelSize(0, 0);
        /*
         * CustomMenuItem item = GWT.create(CustomMenuItem.class);
         * dummyRootMenuBar.getItems().add(item);
         *
         * contextMenuWidget = new MyVMenuBar(true, dummyRootMenuBar);
         * contextMenuWidget.setConnection(getConnection());
         * item.setSubMenu(contextMenuWidget);
         */

        registerRpc(ContextMenuClientRpc.class, new ContextMenuClientRpc() {
            @Override
            public void showContextMenu(int x, int y) {
                showMenu(x, y);
            }
        });

        /*
         * Event.addNativePreviewHandler(new NativePreviewHandler() {
         *
         * @Override public void onPreviewNativeEvent(NativePreviewEvent event)
         * { if (event.getTypeInt() == Event.ONKEYDOWN &&
         * contextMenuWidget.isPopupShowing()) { boolean handled =
         * contextMenuWidget.handleNavigation(
         * event.getNativeEvent().getKeyCode(),
         * event.getNativeEvent().getCtrlKey(),
         * event.getNativeEvent().getShiftKey());
         *
         * if (handled) { event.cancel(); } } } });
         */
    }

    private void showMenu(int eventX, int eventY) {

        if (contextMenuWidget.getItems().size() == 0) {
            return;
        }
        CustomMenuItem firstItem = innerConnector.getWidget().getItems().get(0);
        contextMenuWidget.setSelected(firstItem);
        contextMenuWidget.showChildMenuAt(firstItem, eventY, eventX);
    }

    @Override
    protected void extend(ServerConnector target) {
        Logger.getLogger("ContextMenuConnector").info("extend");

        // Widget widget = ((AbstractComponentConnector) target).getWidget();

        // widget.addDomHandler(new ContextMenuHandler() {
        //
        // @Override
        // public void onContextMenu(ContextMenuEvent event) {
        // event.stopPropagation();
        // event.preventDefault();
        //
        // showMenu(event.getNativeEvent().getClientX(), event
        // .getNativeEvent().getClientY());
        // }
        // }, ContextMenuEvent.getType());

        // widget.addDomHandler(new KeyDownHandler() {
        // @Override
        // public void onKeyDown(KeyDownEvent event) {
        // // FIXME: check if menu is shown or handleNavigation will do it?
        //
        // boolean handled = contextMenuWidget.handleNavigation(event
        // .getNativeEvent().getKeyCode(), event.getNativeEvent()
        // .getCtrlKey(), event.getNativeEvent().getShiftKey());
        //
        // if (handled) {
        // event.stopPropagation();
        // event.preventDefault();
        // }
        // }
        // }, KeyDownEvent.getType());
    }
}
