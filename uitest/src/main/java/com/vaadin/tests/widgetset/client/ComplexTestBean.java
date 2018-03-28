package com.vaadin.tests.widgetset.client;

import java.util.List;

import com.vaadin.shared.communication.SharedState;

@SuppressWarnings("javadoc")
public class ComplexTestBean extends SharedState {
    private SimpleTestBean innerBean1;
    private SimpleTestBean innerBean2;
    private List<SimpleTestBean> innerBeanCollection;
    private int privimite;

    public ComplexTestBean() {
        // Default
    }

    public ComplexTestBean(SimpleTestBean innerBean1, SimpleTestBean innerBean2,
            List<SimpleTestBean> innerBeanCollection, int privimite) {
        this.innerBean1 = innerBean1;
        this.innerBean2 = innerBean2;
        this.innerBeanCollection = innerBeanCollection;
        this.privimite = privimite;
    }

    public SimpleTestBean getInnerBean1() {
        return innerBean1;
    }

    public void setInnerBean1(SimpleTestBean innerBean) {
        innerBean1 = innerBean;
    }

    public SimpleTestBean getInnerBean2() {
        return innerBean2;
    }

    public void setInnerBean2(SimpleTestBean innerBean2) {
        this.innerBean2 = innerBean2;
    }

    public List<SimpleTestBean> getInnerBeanCollection() {
        return innerBeanCollection;
    }

    public void setInnerBeanCollection(
            List<SimpleTestBean> innerBeanCollection) {
        this.innerBeanCollection = innerBeanCollection;
    }

    public int getPrivimite() {
        return privimite;
    }

    public void setPrivimite(int privimite) {
        this.privimite = privimite;
    }

    @Override
    public String toString() {
        return "ComplexTestBean [innerBean1=" + innerBean1 + ", innerBean2="
                + innerBean2 + ", innerBeanCollection=" + innerBeanCollection
                + ", privimite=" + privimite + "]";
    }

}
