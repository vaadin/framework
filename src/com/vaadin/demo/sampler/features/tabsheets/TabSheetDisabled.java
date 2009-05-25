package com.vaadin.demo.sampler.features.tabsheets;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class TabSheetDisabled extends Feature {
    @Override
    public String getName() {
        return "Tabsheet, disabled tabs";
    }

    @Override
    public String getDescription() {
        return "Individual tabs can be enabled, disabled, hidden or visible.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TabSheet.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { TabSheetIcons.class, TabSheetScrolling.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
