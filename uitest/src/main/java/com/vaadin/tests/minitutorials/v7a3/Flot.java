package com.vaadin.tests.minitutorials.v7a3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;

import elemental.json.JsonArray;

@JavaScript({
        "https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js",
        "jquery.flot.js", "flot_connector.js" })
public class Flot extends AbstractJavaScriptComponent {
    public Flot() {
        registerRpc(new FlotClickRpc() {
            @Override
            public void onPlotClick(int seriesIndex, int dataIndex) {
                Notification.show(
                        "Clicked on [" + seriesIndex + ", " + dataIndex + "]");
            }
        });
        addFunction("onPlotClick", new JavaScriptFunction() {
            @Override
            public void call(JsonArray arguments) {
                int seriesIndex = (int) arguments.getNumber(0);
                int dataIndex = (int) arguments.getNumber(1);
                Notification.show(
                        "Clicked on [" + seriesIndex + ", " + dataIndex + "]");
            }
        });
    }

    public void addSeries(double... points) {
        List<List<Double>> pointList = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            pointList.add(Arrays.asList(Double.valueOf(i),
                    Double.valueOf(points[i])));
        }

        getState().series.add(pointList);
    }

    public void highlight(int seriesIndex, int dataIndex) {
        getRpcProxy(FlotHighlightRpc.class).highlight(seriesIndex, dataIndex);
        callFunction("highlight", seriesIndex, dataIndex);
    }

    @Override
    public FlotState getState() {
        return (FlotState) super.getState();
    }
}
