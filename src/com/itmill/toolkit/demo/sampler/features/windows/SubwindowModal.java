package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.demo.sampler.APIResource;
import com.itmill.toolkit.demo.sampler.Feature;
import com.itmill.toolkit.demo.sampler.NamedExternalResource;
import com.itmill.toolkit.demo.sampler.features.blueprints.ProminentPrimaryAction;
import com.itmill.toolkit.ui.Window;

public class SubwindowModal extends Feature {

    @Override
    public String getName() {
        return "Modal window";
    }

    @Override
    public String getDescription() {
        return "A <i>modal window</i> blocks access to the rest of the application, "
                + "until the window is closed (or made non-modal).<br/>"
                + " Use modal windows when the user must finish the task in the"
                + " window before continuing.";
    }

    @Override
    public APIResource[] getRelatedAPI() {
        return new APIResource[] { new APIResource(Window.class) };
    }

    @Override
    public Class[] getRelatedFeatures() {
        return new Class[] {
        //
                Subwindow.class, //
                ProminentPrimaryAction.class, //
        };
    }

    @Override
    public NamedExternalResource[] getRelatedResources() {
        return new NamedExternalResource[] {
        //
        new NamedExternalResource("Wikipedia: Modal window",
                "http://en.wikipedia.org/wiki/Modal_window"), //

        };
    }

}
