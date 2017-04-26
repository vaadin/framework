package com.vaadin.test.dependencyrewriteaddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.server.DependencyFilter;
import com.vaadin.ui.Dependency;
import com.vaadin.ui.Dependency.Type;

public class RewriteJQueryFilter implements DependencyFilter {

    @Override
    public List<Dependency> filter(List<Dependency> dependencies,
            FilterContext filterContext) {
        List<Dependency> filtered = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (dependency.getType() == Type.JAVASCRIPT && dependency.getUrl()
                    .toLowerCase(Locale.ENGLISH).contains("jquery")) {
                filtered.add(
                        new Dependency(Type.JAVASCRIPT, "vaadin://jquery.js"));
            } else {
                filtered.add(dependency);
            }
        }
        return filtered;
    }
}
