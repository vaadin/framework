package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class NestedOverlayTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Button openWindowButton = createOpenWindowButton();
        PopupView openPopUpViewLink = createOpenPopUpViewButton();

        MenuBar menuBar = new MenuBar();
        MenuItem menuItem = menuBar.addItem("First item");
        MenuItem firstDropdownItem = menuItem.addItem("Dropdown item");
        firstDropdownItem.setDescription("Dropdown Item tooltip");

        addComponents(openWindowButton, openPopUpViewLink, menuBar);
    }

    private Button createOpenWindowButton() {
        Button openWindowButton = new Button("Open window");
        openWindowButton.setDescription("Button tooltip");
        openWindowButton.addClickListener(event -> {
            Window window = new Window();
            window.center();
            window.setWidth("500px");
            window.setHeight("500px");

            Button openInnerWindowButton = new Button("Open inner window");
            openInnerWindowButton.setDescription("Button tooltip");
            openInnerWindowButton.addClickListener(innerEvent -> {
                Window innerWindow = new Window();
                innerWindow.center();
                innerWindow.setWidth("400px");
                innerWindow.setHeight("400px");

                Button dummyButton = new Button(
                        "This button doesn't do a thing");
                dummyButton.setDescription("DummyButton tooltip");

                MenuBar innerWindowMenuBar = new MenuBar();
                innerWindowMenuBar.setDescription("MenuBar tooltip");
                MenuItem menuItem = innerWindowMenuBar
                        .addItem("First item in inner window");
                MenuItem firstDropdownItem = menuItem
                        .addItem("Inner window dropdown item");
                firstDropdownItem
                        .setDescription("Inner window dropdown item tooltip");

                innerWindow.setContent(
                        new VerticalLayout(dummyButton, innerWindowMenuBar));
                addWindow(innerWindow);
            });

            MenuBar windowMenuBar = new MenuBar();
            windowMenuBar.setDescription("MenuBar tooltip");
            MenuItem menuItem = windowMenuBar.addItem("First item in window");
            MenuItem firstDropdownItem = menuItem
                    .addItem("Window dropdown item");
            firstDropdownItem.setDescription("Window dropdown item tooltip");

            window.setContent(
                    new VerticalLayout(openInnerWindowButton, windowMenuBar));
            addWindow(window);
        });
        return openWindowButton;
    }

    private PopupView createOpenPopUpViewButton() {
        PopupView dummyView = new PopupView("Open empty popupView",
                new VerticalLayout());
        dummyView.setDescription("This dummy popupView has no content");

        MenuBar innerPopupMenuBar = new MenuBar();
        innerPopupMenuBar.setDescription("MenuBar tooltip");
        MenuItem innerPopupMenuItem = innerPopupMenuBar
                .addItem("First item in inner popupView");
        MenuItem firstDropdownInnerPopupItem = innerPopupMenuItem
                .addItem("Inner popupView dropdown item");
        firstDropdownInnerPopupItem
                .setDescription("Inner popupView dropdown item tooltip");

        PopupView innerView = new PopupView("Open inner popupView",
                new VerticalLayout(dummyView, innerPopupMenuBar));
        innerView.setDescription("This inner popupView has content");

        MenuBar popupMenuBar = new MenuBar();
        popupMenuBar.setDescription("MenuBar tooltip");
        MenuItem popupMenuItem = popupMenuBar
                .addItem("First item in popupView");
        MenuItem firstDropdownPopupItem = popupMenuItem
                .addItem("PopupView dropdown item");
        firstDropdownPopupItem
                .setDescription("PopupView dropdown item tooltip");

        PopupView popupView = new PopupView("Open popupView",
                new VerticalLayout(innerView, innerView, popupMenuBar));
        popupView.setDescription("This popupView has content");
        return popupView;
    }

    @Override
    protected String getTestDescription() {
        return "MenuItem's tooltip should be visible even when "
                + "the MenuBar is located within a Window, PopupView, "
                + "or some other component that extends VOverlay";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11212;
    }
}
