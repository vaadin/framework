package com.vaadin.tests.layouts.layouttester.GridLayout;

import java.io.IOException;

import com.vaadin.tests.layouts.layouttester.BaseLayoutSizingTest;

public class GridLayoutSizingTest extends BaseLayoutSizingTest {

    @Override
    public void LayoutSizing() throws IOException, InterruptedException {
        states[0] = "setSize600px";
        super.LayoutSizing();
    }
}
