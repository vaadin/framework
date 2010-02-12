package com.vaadin.terminal.gwt.client.ui.dd;

public abstract class VAcceptCriterionFactory {

    public abstract VAcceptCriteria get(String name);
    // name = name.intern();
    // // FIXME make all lazy inited and possibility to use instances per
    // // handler
    // // TODO maybe just ditch singleton idea and use new instances on each
    // // fetch for all types of components.
    // if (name.equals("-ss")) {
    // return GWT.create(ServerAccept.class);
    // } else if (name.equals("com.vaadin.ui.Tree.TreeDropCriterion")) {
    // return GWT.create(LazyInitItemIdentifiers.class);
    // } else if (name == "needsItemId") {
    // return GWT.create(HasItemId.class);
    // } else if (name == "acceptAll") {
    // return GWT.create(AcceptAll.class);
    // } else if (name == "and") {
    // return GWT.create(And.class);
    // } else if (name == "overTreeNode") {
    // return GWT.create(OverTreeNode.class);
    // } else if (name == "component") {
    // return GWT.create(ComponentCriteria.class);
    // }
    // return null;
    // }
}
