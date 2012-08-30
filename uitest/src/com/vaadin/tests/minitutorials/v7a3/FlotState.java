package com.vaadin.tests.minitutorials.v7a3;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.JavaScriptComponentState;

public class FlotState extends JavaScriptComponentState {
    public List<List<List<Double>>> series = new ArrayList<List<List<Double>>>();

    public List<List<List<Double>>> getSeries() {
        return series;
    }

    public void setSeries(List<List<List<Double>>> series) {
        this.series = series;
    }
}