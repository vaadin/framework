package com.vaadin.client.debug.internal;

public enum Icon {

    SEARCH("&#xf002;"), //
    OK("&#xf00c;"), //
    REMOVE("&#xf00d;"), //
    CLOSE("&#xf011;"), //
    CLEAR("&#xf014;"), //
    RESET_TIMER("&#xf017;"), //
    MINIMIZE("&#xf066;"), //
    WARNING("&#xf071;"), //
    INFO("&#xf05a;"), //
    ERROR("&#xf06a;"), //
    HIGHLIGHT("&#xf05b;"), //
    LOG("&#xf0c9;"), //
    OPTIMIZE("&#xf0d0;"), //
    HIERARCHY("&#xf0e8;"), //
    MENU("&#xf013;"), //
    NETWORK("&#xf0ec;"), //
    ANALYZE("&#xf0f0;"), //
    SCROLL_LOCK("&#xf023;"), //
    DEVMODE_OFF("&#xf10c;"), //
    DEVMODE_SUPER("&#xf111;"), //
    DEVMODE_ON("&#xf110;"), //
    // BAN_CIRCLE("&#xf05e;"), //
    MAXIMIZE("&#xf065;"), //
    RESET("&#xf021;"), //
    PERSIST("&#xf02e"); //

    private String id;

    private Icon(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "<i data-icon=\"" + id + "\"></i>";
    }

    public String getId() {
        return id;
    }

}
