package com.itmill.toolkit.demo.sampler;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.ui.AbstractComponent;

public class WebAnalytics extends AbstractComponent {

    private String trackerId;
    private String pageId;
    private String domainName;

    public WebAnalytics(String trackerId) {
        this.trackerId = trackerId;
    }

    public WebAnalytics(String trackerId, String domainName) {
        this(trackerId);
        this.domainName = domainName;
    }

    public String getTag() {
        return "webanalytics";
    }

    public String getTrackerId() {
        return trackerId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void trackPageview(String pageId) {
        this.pageId = pageId;
        requestRepaint();
    }

    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute("trackerid", trackerId);
        if (pageId != null) {
            target.addAttribute("pageid", pageId);
        }
        if (domainName != null) {
            target.addAttribute("domain", domainName);
        }
    }

}
