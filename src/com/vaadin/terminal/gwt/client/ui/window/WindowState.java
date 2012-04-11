package com.vaadin.terminal.gwt.client.ui.window;

import com.vaadin.terminal.gwt.client.ui.panel.PanelState;

public class WindowState extends PanelState {
    private boolean modal = false;
    private boolean resizable = true;
    private boolean resizeLazy = false;
    private boolean draggable = true;
    private boolean centered = false;;
    private int positionX = -1;
    private int positionY = -1;

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public boolean isResizeLazy() {
        return resizeLazy;
    }

    public void setResizeLazy(boolean resizeLazy) {
        this.resizeLazy = resizeLazy;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

}