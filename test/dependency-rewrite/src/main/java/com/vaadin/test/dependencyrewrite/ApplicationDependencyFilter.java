package com.vaadin.test.dependencyrewrite;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.DependencyFilter;
import com.vaadin.ui.Dependency;

public class ApplicationDependencyFilter implements DependencyFilter {

    @Override
    public List<Dependency> filter(List<Dependency> dependencies,
            FilterContext filterContext) {
        List<Dependency> filtered = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (dependency.getUrl().startsWith("vaadin://")) {
                filtered.add(new Dependency(dependency.getType(), dependency
                        .getUrl().replace("vaadin://", "vaadin://sub/")));
            } else {
                filtered.add(dependency);
            }
        }
        return filtered;
    }

}
