/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.vaadin.terminal.gwt.client.MeasuredSize.MeasureResult;
import com.vaadin.terminal.gwt.client.ui.ManagedLayout;
import com.vaadin.terminal.gwt.client.ui.PostLayoutListener;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.terminal.gwt.client.ui.VNotification;
import com.vaadin.terminal.gwt.client.ui.layout.LayoutDependencyTree;

public class LayoutManager {
    private static final String LOOP_ABORT_MESSAGE = "Aborting layout after 100 passes. This would probably be an infinite loop.";
    private ApplicationConnection connection;
    private final Set<Element> nonPaintableElements = new HashSet<Element>();
    private final MeasuredSize nullSize = new MeasuredSize();
    private boolean layoutRunning = false;

    private final Collection<ManagedLayout> needsHorizontalLayout = new HashSet<ManagedLayout>();
    private final Collection<ManagedLayout> needsVerticalLayout = new HashSet<ManagedLayout>();

    public void setConnection(ApplicationConnection connection) {
        if (this.connection != null) {
            throw new RuntimeException(
                    "LayoutManager connection can never be changed");
        }
        this.connection = connection;
    }

    public static LayoutManager get(ApplicationConnection connection) {
        return connection.getLayoutManager();
    }

    public void registerDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = ensureMeasured(element);
        setNeedsUpdate(owner);
        measuredSize.addDependent(owner.getConnectorId());
    }

    private MeasuredSize ensureMeasured(Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();

            if (ConnectorMap.get(connection).getConnector(element) == null) {
                nonPaintableElements.add(element);
            }
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    private boolean needsMeasure(Element e) {
        if (connection.getConnectorMap().getConnectorId(e) != null) {
            return true;
        } else if (getMeasuredSize(e, nullSize).hasDependents()) {
            return true;
        } else {
            return false;
        }
    }

    protected native void setMeasuredSize(Element element,
            MeasuredSize measuredSize)
    /*-{
        if (measuredSize) {
            element.vMeasuredSize = measuredSize;
        } else {
            delete element.vMeasuredSize;
        }
    }-*/;

    private static native final MeasuredSize getMeasuredSize(Element element,
            MeasuredSize defaultSize)
    /*-{
        return element.vMeasuredSize || defaultSize;
    }-*/;

    private final MeasuredSize getMeasuredSize(ComponentConnector paintable) {
        Element element = paintable.getWidget().getElement();
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            measuredSize = new MeasuredSize();
            setMeasuredSize(element, measuredSize);
        }
        return measuredSize;
    }

    public void unregisterDependency(ManagedLayout owner, Element element) {
        MeasuredSize measuredSize = getMeasuredSize(element, null);
        if (measuredSize == null) {
            return;
        }
        measuredSize.removeDependent(owner.getConnectorId());
        if (!needsMeasure(element)) {
            nonPaintableElements.remove(element);
            setMeasuredSize(element, null);
        }
    }

    public boolean isLayoutRunning() {
        return layoutRunning;
    }

    public void doLayout() {
        if (layoutRunning) {
            throw new IllegalStateException(
                    "Can't start a new layout phase before the previous layout phase ends.");
        }
        VConsole.log("Starting layout phase");
        layoutRunning = true;

        ConnectorMap paintableMap = connection.getConnectorMap();
        ComponentConnector[] paintableWidgets = paintableMap
                .getComponentConnectors();

        int passes = 0;
        Duration totalDuration = new Duration();

        LayoutDependencyTree layoutDependencyTree = new LayoutDependencyTree();

        for (ComponentConnector componentConnector : paintableWidgets) {
            layoutDependencyTree.setNeedsMeasure(componentConnector, true);
            if (needsHorizontalLayout.contains(componentConnector)) {
                layoutDependencyTree.setNeedsHorizontalLayout(
                        componentConnector, true);
            }
            if (needsVerticalLayout.contains(componentConnector)) {
                layoutDependencyTree.setNeedsVerticalLayout(componentConnector,
                        true);
            }
        }
        needsHorizontalLayout.clear();
        needsVerticalLayout.clear();
        measureNonPaintables(layoutDependencyTree);

        VConsole.log("Layout init in " + totalDuration.elapsedMillis() + " ms");

        while (true) {
            Duration passDuration = new Duration();
            passes++;

            int measuredConnectorCount = measureConnectors(
                    layoutDependencyTree, passes == 1);

            int measureTime = passDuration.elapsedMillis();
            VConsole.log("Measured " + measuredConnectorCount + " elements in "
                    + measureTime + " ms");

            FastStringSet updatedSet = FastStringSet.create();

            for (ComponentConnector connector : layoutDependencyTree
                    .getHorizontalLayoutTargets()) {
                if (connector instanceof DirectionalManagedLayout) {
                    DirectionalManagedLayout cl = (DirectionalManagedLayout) connector;
                    cl.layoutHorizontally();
                    layoutDependencyTree.markAsHorizontallyLayouted(connector);
                } else if (connector instanceof SimpleManagedLayout) {
                    SimpleManagedLayout rr = (SimpleManagedLayout) connector;
                    rr.layout();
                    layoutDependencyTree.markAsHorizontallyLayouted(connector);
                    layoutDependencyTree.markAsVerticallyLayouted(connector);
                } else {
                    layoutDependencyTree.markAsHorizontallyLayouted(connector);
                    layoutDependencyTree.markAsVerticallyLayouted(connector);
                }
                updatedSet.add(connector.getConnectorId());
            }

            for (ComponentConnector connector : layoutDependencyTree
                    .getVerticalLayoutTargets()) {
                if (connector instanceof DirectionalManagedLayout) {
                    DirectionalManagedLayout cl = (DirectionalManagedLayout) connector;
                    cl.layoutVertically();
                    layoutDependencyTree.markAsVerticallyLayouted(connector);
                } else if (connector instanceof SimpleManagedLayout) {
                    SimpleManagedLayout rr = (SimpleManagedLayout) connector;
                    rr.layout();
                    layoutDependencyTree.markAsHorizontallyLayouted(connector);
                    layoutDependencyTree.markAsVerticallyLayouted(connector);
                } else {
                    layoutDependencyTree.markAsHorizontallyLayouted(connector);
                    layoutDependencyTree.markAsVerticallyLayouted(connector);
                }
                updatedSet.add(connector.getConnectorId());
            }

            JsArrayString changed = updatedSet.dump();
            VConsole.log(changed.length() + " requestLayout invocations in "
                    + (passDuration.elapsedMillis() - measureTime) + "ms");

            StringBuilder b = new StringBuilder();
            b.append(changed.length());
            b.append(" changed widgets in pass ");
            b.append(passes);
            b.append(" in ");
            b.append(passDuration.elapsedMillis());
            b.append(" ms: ");
            if (changed.length() < 10) {
                for (int i = 0; i < changed.length(); i++) {
                    if (i != 0) {
                        b.append(", ");
                    }
                    b.append(changed.get(i));
                }
            }
            VConsole.log(b.toString());

            if (changed.length() == 0) {
                VConsole.log("No more changes in pass " + passes);
                break;
            }

            if (passes > 100) {
                VConsole.log(LOOP_ABORT_MESSAGE);
                VNotification.createNotification(VNotification.DELAY_FOREVER)
                        .show(LOOP_ABORT_MESSAGE, VNotification.CENTERED,
                                "error");
                break;
            }
        }

        VConsole.log("Layout phase done");
        VConsole.log("Calling post layout listeners");

        for (ComponentConnector vPaintableWidget : paintableWidgets) {
            if (vPaintableWidget instanceof PostLayoutListener) {
                ((PostLayoutListener) vPaintableWidget).postLayout();
            }
        }

        layoutRunning = false;
        VConsole.log("Total layout phase time: "
                + totalDuration.elapsedMillis() + "ms");
    }

    private int measureConnectors(LayoutDependencyTree layoutDependencyTree,
            boolean measureAll) {

        Collection<ComponentConnector> measureTargets;
        if (measureAll) {
            measureTargets = Arrays.asList(ConnectorMap.get(connection)
                    .getComponentConnectors());
        } else {
            measureTargets = layoutDependencyTree.getMeasureTargets();
        }
        for (ComponentConnector paintableWidget : measureTargets) {
            Element element = paintableWidget.getWidget().getElement();
            MeasuredSize measuredSize = getMeasuredSize(paintableWidget);
            MeasureResult measureResult = measuredAndUpdate(element,
                    measuredSize, layoutDependencyTree);

            if (measureResult.isHeightChanged()) {
                layoutDependencyTree.markHeightAsChanged(paintableWidget);
            }
            if (measureResult.isWidthChanged()) {
                layoutDependencyTree.markWidthAsChanged(paintableWidget);
            }

            layoutDependencyTree.setNeedsMeasure(paintableWidget, false);
        }
        return measureTargets.size();
    }

    private void measureNonPaintables(LayoutDependencyTree layoutDependencyTree) {
        for (Element element : nonPaintableElements) {
            MeasuredSize measuredSize = getMeasuredSize(element, null);
            measuredAndUpdate(element, measuredSize, layoutDependencyTree);
        }
    }

    private MeasureResult measuredAndUpdate(Element element,
            MeasuredSize measuredSize, LayoutDependencyTree layoutDependencyTree) {
        MeasureResult measureResult = measuredSize.measure(element);
        if (measureResult.isChanged()) {
            JsArrayString dependents = measuredSize.getDependents();
            for (int i = 0; i < dependents.length(); i++) {
                String pid = dependents.get(i);
                ComponentConnector dependent = (ComponentConnector) connection
                        .getConnectorMap().getConnector(pid);
                if (dependent != null) {
                    if (measureResult.isHeightChanged()) {
                        layoutDependencyTree.setNeedsVerticalLayout(dependent,
                                true);
                    }
                    if (measureResult.isWidthChanged()) {
                        layoutDependencyTree.setNeedsHorizontalLayout(
                                dependent, true);
                    }
                }
            }
        }
        return measureResult;
    }

    private static boolean isManagedLayout(ComponentConnector paintable) {
        return paintable instanceof ManagedLayout;
    }

    public void forceLayout() {
        ConnectorMap paintableMap = connection.getConnectorMap();
        ComponentConnector[] paintableWidgets = paintableMap
                .getComponentConnectors();
        for (ComponentConnector connector : paintableWidgets) {
            if (connector instanceof ManagedLayout) {
                setNeedsUpdate((ManagedLayout) connector);
            }
        }
        doLayout();
    }

    // TODO Rename to setNeedsLayout
    public final void setNeedsUpdate(ManagedLayout layout) {
        setWidthNeedsUpdate(layout);
        setHeightNeedsUpdate(layout);
    }

    public final void setWidthNeedsUpdate(ManagedLayout layout) {
        needsHorizontalLayout.add(layout);
    }

    public final void setHeightNeedsUpdate(ManagedLayout layout) {
        needsVerticalLayout.add(layout);
    }

    public boolean isMeasured(Element element) {
        return getMeasuredSize(element, nullSize) != nullSize;
    }

    public final int getOuterHeight(Element element) {
        return getMeasuredSize(element, nullSize).getOuterHeight();
    }

    public final int getOuterWidth(Element element) {
        return getMeasuredSize(element, nullSize).getOuterWidth();
    }

    public final int getInnerHeight(Element element) {
        return getMeasuredSize(element, nullSize).getInnerHeight();
    }

    public final int getInnerWidth(Element element) {
        return getMeasuredSize(element, nullSize).getInnerWidth();
    }

    public final int getBorderHeight(Element element) {
        return getMeasuredSize(element, nullSize).getBorderHeight();
    }

    public int getPaddingHeight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingHeight();
    }

    public int getBorderWidth(Element element) {
        return getMeasuredSize(element, nullSize).getBorderWidth();
    }

    public int getPaddingWidth(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingWidth();
    }

    public int getPaddingTop(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingTop();
    }

    public int getPaddingLeft(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingLeft();
    }

    public int getPaddingBottom(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingBottom();
    }

    public int getPaddingRight(Element element) {
        return getMeasuredSize(element, nullSize).getPaddingRight();
    }

    public int getMarginTop(Element element) {
        return getMeasuredSize(element, nullSize).getMarginTop();
    }

    public int getMarginRight(Element element) {
        return getMeasuredSize(element, nullSize).getMarginRight();
    }

    public int getMarginBottom(Element element) {
        return getMeasuredSize(element, nullSize).getMarginBottom();
    }

    public int getMarginLeft(Element element) {
        return getMeasuredSize(element, nullSize).getMarginLeft();
    }
}
