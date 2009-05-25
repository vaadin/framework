package com.vaadin.demo.sampler.features.tabsheets;

import com.vaadin.demo.sampler.APIResource;
import com.vaadin.demo.sampler.Feature;
import com.vaadin.demo.sampler.NamedExternalResource;
import com.vaadin.ui.TabSheet;

@SuppressWarnings("serial")
public class TabSheetScrolling extends Feature {
    @Override
    public String getName() {
        return "Tabsheet, scrolling tabs";
    }

    @Override
    public String getDescription() {
        return "If the tabs are to many to be shown at once, a scrolling control will appear automatically.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(TabSheet.class) };
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Feature>[] getRelatedFeatures() {
        return new Class[] { TabSheetIcons.class, TabSheetDisabled.class };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        // TODO Auto-generated method stub
        return null;
    }

}
