package com.vaadin.tests.layouts.layouttester;

import com.vaadin.ui.Layout;

public abstract class AbstractLayoutTests {

    protected static final String FOLDER_16_PNG = "../icons/runo/16/folder.png";
    protected static final String CALENDAR_32_PNG = "../runo/icons/32/calendar.png";
    protected static final String LOCK_16_PNG = "../runo/icons/16/lock.png";
    protected static final String GLOBE_16_PNG = "../runo/icons/16/globe.png";

    abstract protected Layout getCaptionsTests();

    abstract protected Layout getIconsTests();

    abstract protected Layout getRequiredErrorIndicatorsTests();

    abstract protected Layout getAlignmentTests();

    abstract protected Layout getExpandRatiosTests();

    abstract protected Layout getMarginSpacingTests();

    abstract protected Layout getComponentAddReplaceMoveTests();

    abstract protected Layout getComponentSizingTests();

    abstract protected Layout getLayoutSizingTests();

}
