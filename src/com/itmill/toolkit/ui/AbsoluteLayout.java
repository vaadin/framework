package com.itmill.toolkit.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.gwt.client.ui.IAbsoluteLayout;

/**
 * AbsoluteLayout is a layout implementation that mimics html absolute
 * positioning.
 * 
 */
public class AbsoluteLayout extends AbstractLayout {

    private Collection<Component> components = new HashSet<Component>();
    private Map<Component, ComponentPosition> componentToCoordinates = new HashMap<Component, ComponentPosition>();

    public AbsoluteLayout() {
        setSizeFull();
    }

    @Override
    public String getTag() {
        return IAbsoluteLayout.TAGNAME;
    }

    public Iterator<Component> getComponentIterator() {
        return components.iterator();
    }

    public void replaceComponent(Component oldComponent, Component newComponent) {
        ComponentPosition position = getPosition(oldComponent);
        removeComponent(oldComponent);
        addComponent(newComponent);
        componentToCoordinates.put(newComponent, position);
    }

    @Override
    public void addComponent(Component c) {
        components.add(c);
        super.addComponent(c);
    }

    @Override
    public void removeComponent(Component c) {
        components.remove(c);
        super.removeComponent(c);
    }

    public void addComponent(Component c, String cssPosition) {
        addComponent(c);
        getPosition(c).setCSSString(cssPosition);
    }

    public ComponentPosition getPosition(Component component) {
        if (componentToCoordinates.containsKey(component)) {
            return componentToCoordinates.get(component);
        } else {
            ComponentPosition coords = new ComponentPosition();
            componentToCoordinates.put(component, coords);
            return coords;
        }
    }

    /**
     * TODO symmetric getters and setters for fields to make this simpler to use
     * in generic java tools
     * 
     */
    public class ComponentPosition {

        private int zIndex = -1;
        private float top = -1;
        private float right = -1;
        private float bottom = -1;
        private float left = -1;

        private int topUnits;
        private int rightUnits;
        private int bottomUnits;
        private int leftUnits;

        /**
         * Sets the position attributes using CSS syntax. Example usage:
         * 
         * <code><pre>
         * setCSSString("top:10px;left:20%;z-index:16;");
         * </pre></code>
         * 
         * @param css
         */
        public void setCSSString(String css) {
            String[] cssProperties = css.split(";");
            for (int i = 0; i < cssProperties.length; i++) {
                String[] keyValuePair = cssProperties[i].split(":");
                String key = keyValuePair[0].trim();
                if (key.equals("z-index")) {
                    zIndex = Integer.parseInt(keyValuePair[1]);
                } else {
                    String value = keyValuePair[1].trim();
                    String unit = value.replaceAll("[0-9\\.]+", "");
                    if (!unit.equals("")) {
                        value = value.substring(0, value.indexOf(unit)).trim();
                    }
                    float v = Float.parseFloat(value);
                    int unitInt = parseCssUnit(unit);
                    if (key.equals("top")) {
                        top = v;
                        topUnits = unitInt;
                    } else if (key.equals("right")) {
                        right = v;
                        rightUnits = unitInt;
                    } else if (key.equals("bottom")) {
                        bottom = v;
                        bottomUnits = unitInt;
                    } else if (key.equals("left")) {
                        left = v;
                        leftUnits = unitInt;
                    }
                }
            }
            requestRepaint();
        }

        private int parseCssUnit(String string) {
            for (int i = 0; i < UNIT_SYMBOLS.length; i++) {
                if (UNIT_SYMBOLS[i].equals(string)) {
                    return i;
                }
            }
            return 0; // defaults to px (eg. top:0;)
        }

        public String getCSSString() {
            String s = "";
            if (top >= 0) {
                s += "top:" + top + UNIT_SYMBOLS[topUnits] + ";";
            }
            if (right >= 0) {
                s += "right:" + right + UNIT_SYMBOLS[rightUnits] + ";";
            }
            if (bottom >= 0) {
                s += "bottom:" + bottom + UNIT_SYMBOLS[bottomUnits] + ";";
            }
            if (left >= 0) {
                s += "left:" + left + UNIT_SYMBOLS[leftUnits] + ";";
            }
            if (zIndex >= 0) {
                s += "z-index:" + zIndex + ";";
            }
            return s;
        }

        public void setTop(float topValue, int topUnits) {
            validateLength(topValue, topUnits);
            top = topValue;
            this.topUnits = topUnits;
            requestRepaint();
        }

        public void setRight(float rightValue, int rightUnits) {
            validateLength(rightValue, rightUnits);
            right = rightValue;
            this.rightUnits = rightUnits;
            requestRepaint();
        }

        public void setBottom(float bottomValue, int units) {
            validateLength(bottomValue, units);
            bottom = bottomValue;
            bottomUnits = units;
            requestRepaint();
        }

        public void setLeft(float leftValue, int units) {
            validateLength(leftValue, units);
            left = leftValue;
            leftUnits = units;
            requestRepaint();
        }

        public void setZIndex(int zIndex) {
            this.zIndex = zIndex;
            requestRepaint();
        }

    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        for (Component component : components) {
            target.startTag("cc");
            target.addAttribute("css", getPosition(component).getCSSString());
            component.paint(target);
            target.endTag("cc");
        }
    }

    private static void validateLength(float topValue, int topUnits2) {
        // TODO throw on invalid value

    }

}
