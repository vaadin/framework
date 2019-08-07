package com.vaadin.tests.components.uitest.components;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Slider;

public class SlidersCssTest {

    private int debugIdCounter = 0;

    public SlidersCssTest(TestSampler parent) {
        Slider slide1 = new Slider();
        slide1.setId("slider" + debugIdCounter++);
        parent.addComponent(slide1);

        Slider slide2 = new Slider();
        slide2.setOrientation(SliderOrientation.VERTICAL);
        slide2.setId("slider" + debugIdCounter++);
        parent.addComponent(slide2);

        parent.addReadOnlyChangeListener(event -> {
            slide1.setReadOnly(!slide1.isReadOnly());
            slide2.setReadOnly(!slide2.isReadOnly());
        });
    }
}
