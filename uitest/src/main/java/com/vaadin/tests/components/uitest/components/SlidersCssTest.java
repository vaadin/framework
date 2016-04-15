package com.vaadin.tests.components.uitest.components;

import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Slider;

public class SlidersCssTest {

    private int debugIdCounter = 0;

    public SlidersCssTest(TestSampler parent) {
        Slider slide = new Slider();
        slide.setId("slider" + debugIdCounter++);
        parent.addComponent(slide);

        slide = new Slider();
        slide.setOrientation(SliderOrientation.VERTICAL);
        slide.setId("slider" + debugIdCounter++);
        parent.addComponent(slide);
    }
}
