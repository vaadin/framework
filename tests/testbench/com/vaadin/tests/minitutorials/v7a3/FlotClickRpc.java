package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface FlotClickRpc extends ServerRpc {
    public void onPlotClick(int seriesIndex, int dataIndex);
}
