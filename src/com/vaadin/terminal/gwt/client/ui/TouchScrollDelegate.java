/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.vaadin.terminal.gwt.client.VConsole;

/**
 * Provides one finger touch scrolling for elements with once scrollable
 * elements inside. One widget can have several of these scrollable elements.
 * Scrollable elements are provided in the constructor. Users must pass
 * touchStart events to this delegate, from there on the delegate takes over
 * with an event preview. Other touch events needs to be sunken though.
 * <p>
 * This is bit similar as Scroller class in GWT expenses example, but ideas
 * drawn from iscroll.js project:
 * <ul>
 * <li>uses GWT event mechanism.
 * <li>uses modern CSS trick during scrolling for smoother experience:
 * translate3d and transitions
 * </ul>
 * <p>
 * Scroll event should only happen when the "touch scrolling actually ends".
 * Later we might also tune this so that a scroll event happens if user stalls
 * her finger long enought.
 * 
 * TODO static getter for active touch scroll delegate. Components might need to
 * prevent scrolling in some cases. Consider Table with drag and drop, or drag
 * and drop in scrollable area. Optimal implementation might be to start the
 * drag and drop only if user keeps finger down for a moment, otherwise do the
 * scroll. In this case, the draggable component would need to cancel scrolling
 * in a timer after touchstart event and take over from there.
 * 
 * TODO support scrolling horizontally
 * 
 * TODO cancel if user add second finger to the screen (user expects a gesture).
 * 
 * TODO "scrollbars", see e.g. iscroll.js
 * 
 * TODO write an email to sjobs √§t apple dot com and beg for this feature to be
 * built into webkit. Seriously, we should try to lobbying this to webkit folks.
 * This sure ain't our business to implement this with javascript.
 * 
 * TODO collect all general touch related constant to better place.
 * 
 * @author Matti Tahvonen, Vaadin Ltd
 */
public class TouchScrollDelegate implements NativePreviewHandler {

    private static final double FRICTION = 0.002;
    private static final double DECELERATION = 0.002;
    private static final int MAX_DURATION = 1500;
    private int origX;
    private int origY;
    private Element[] scrollableElements;
    private Element scrolledElement;
    private int origScrollTop;
    private HandlerRegistration handlerRegistration;
    private int lastClientY;
    private double pixxelsPerMs;
    private boolean transitionPending = false;
    private int deltaScrollPos;
    private boolean transitionOn = false;
    private int finalScrollTop;
    private ArrayList<Element> layers;
    private boolean moved;

    private static TouchScrollDelegate activeScrollDelegate;

    public TouchScrollDelegate(Element... elements) {
        scrollableElements = elements;
    }

    public static TouchScrollDelegate getActiveScrollDelegate() {
        return activeScrollDelegate;
    }

    /**
     * Has user moved the touch.
     * 
     * @return
     */
    public boolean isMoved() {
        return moved;
    }

    /**
     * Forces the scroll delegate to cancels scrolling process. Can be called by
     * users if they e.g. decide to handle touch event by themselves after all
     * (e.g. a pause after touch start before moving touch -> interpreted as
     * long touch/click or drag start).
     */
    public void stopScrolling() {
        handlerRegistration.removeHandler();
        handlerRegistration = null;
        if (moved) {
            moveTransformationToScrolloffset();
        } else {
            activeScrollDelegate = null;
        }
    }

    public void onTouchStart(TouchStartEvent event) {
        if (activeScrollDelegate == null && event.getTouches().length() == 1) {

            Touch touch = event.getTouches().get(0);
            if (detectScrolledElement(touch)) {
                VConsole.log("TouchDelegate takes over");
                event.stopPropagation();
                handlerRegistration = Event.addNativePreviewHandler(this);
                activeScrollDelegate = this;
                hookTransitionEndListener(scrolledElement
                        .getFirstChildElement());
                origX = touch.getClientX();
                origY = touch.getClientY();
                yPositions[0] = origY;
                eventTimeStamps[0] = new Date();
                nextEvent = 1;

                if (transitionOn) {
                    // TODO calculate current position of ongoing transition,
                    // fix to that and start scroll from there. Another option
                    // is to investigate if we can get even close the same
                    // framerate with scheduler based impl instead of using
                    // transitions (GWT examples has impl of this, with jsni
                    // though). This is very smooth on native ipad, now we
                    // ignore touch starts during animation.
                    origScrollTop = scrolledElement.getScrollTop();
                } else {
                    origScrollTop = scrolledElement.getScrollTop();
                }
                moved = false;
                // event.preventDefault();
                // event.stopPropagation();
            }
        } else {
            /*
             * Touch scroll is currenly on (possibly bouncing). Ignore.
             */
        }
    }

    private native void hookTransitionEndListener(Element element)
    /*-{
        if(!element.hasTransitionEndListener) {
            var that = this;
            element.addEventListener("webkitTransitionEnd",function(event){
                that.@com.vaadin.terminal.gwt.client.ui.TouchScrollDelegate::onTransitionEnd()();
            },false);
            element.hasTransitionEndListener = true;
        }
    }-*/;

    private void onTransitionEnd() {
        if (finalScrollTop < 0) {
            animateToScrollPosition(0, finalScrollTop);
            finalScrollTop = 0;
        } else if (finalScrollTop > getMaxFinalY()) {
            animateToScrollPosition(getMaxFinalY(), finalScrollTop);
            finalScrollTop = getMaxFinalY();
        } else {
            moveTransformationToScrolloffset();
        }
        transitionOn = false;
    }

    private void animateToScrollPosition(int to, int from) {
        int dist = Math.abs(to - from);
        int time = getAnimationTimeForDistance(dist);
        if (time <= 0) {
            time = 1; // get animation and transition end event
        }
        translateTo(time, -to + origScrollTop);
    }

    private int getAnimationTimeForDistance(int dist) {
        return 350; // 350ms seems to work quite fine for all distances
        // if (dist < 0) {
        // dist = -dist;
        // }
        // return MAX_DURATION * dist / (scrolledElement.getClientHeight() * 3);
    }

    /**
     * Called at the end of scrolling. Moves possible translate values to
     * scrolltop, causing onscroll event.
     */
    private void moveTransformationToScrolloffset() {
        for (Element el : layers) {
            Style style = el.getStyle();
            style.setProperty("webkitTransitionProperty", "none");
            style.setProperty("webkitTransform", "translate3d(0,0,0)");
        }
        scrolledElement.setScrollTop(finalScrollTop);
        activeScrollDelegate = null;
        handlerRegistration.removeHandler();
        handlerRegistration = null;

    }

    /**
     * Detects if a touch happens on a predefined element and the element has
     * something to scroll.
     * 
     * @param touch
     * @return
     */
    private boolean detectScrolledElement(Touch touch) {
        Element target = touch.getTarget().cast();
        for (Element el : scrollableElements) {
            if (el.isOrHasChild(target)
                    && el.getScrollHeight() > el.getClientHeight()) {
                scrolledElement = el;
                NodeList<Node> childNodes = scrolledElement.getChildNodes();
                layers = new ArrayList<Element>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node item = childNodes.getItem(i);
                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        layers.add((Element) item);
                    }
                }
                return true;

            }
        }
        return false;
    }

    private void onTouchMove(NativeEvent event) {
        if (!moved) {
            Date date = new Date();
            long l = (date.getTime() - eventTimeStamps[0].getTime());
            VConsole.log(l + " ms from start to move");
        }
        boolean handleMove = readPositionAndSpeed(event);
        if (handleMove) {
            int deltaScrollTop = origY - lastClientY;
            int finalPos = origScrollTop + deltaScrollTop;
            if (finalPos > getMaxFinalY()) {
                // spring effect at the end
                int overscroll = (deltaScrollTop + origScrollTop)
                        - getMaxFinalY();
                overscroll = overscroll / 2;
                if (overscroll > scrolledElement.getClientHeight() / 2) {
                    overscroll = scrolledElement.getClientHeight() / 2;
                }
                deltaScrollTop = getMaxFinalY() + overscroll - origScrollTop;
            } else if (finalPos < 0) {
                // spring effect at the beginning
                int overscroll = finalPos / 2;
                if (-overscroll > scrolledElement.getClientHeight() / 2) {
                    overscroll = -scrolledElement.getClientHeight() / 2;
                }
                deltaScrollTop = overscroll - origScrollTop;
            }
            quickSetScrollPosition(0, deltaScrollTop);
            moved = true;
            event.preventDefault();
            event.stopPropagation();
        }
    }

    private void quickSetScrollPosition(int deltaX, int deltaY) {
        deltaScrollPos = deltaY;
        translateTo(0, -deltaScrollPos);
    }

    private static final int EVENTS_FOR_SPEED_CALC = 3;
    public static final int SIGNIFICANT_MOVE_THRESHOLD = 3;
    private int[] yPositions = new int[EVENTS_FOR_SPEED_CALC];
    private Date[] eventTimeStamps = new Date[EVENTS_FOR_SPEED_CALC];
    private int nextEvent = 0;
    private Date transitionStart;
    private Date transitionDuration;

    /**
     * 
     * @param event
     * @return
     */
    private boolean readPositionAndSpeed(NativeEvent event) {
        Date now = new Date();
        Touch touch = event.getChangedTouches().get(0);
        lastClientY = touch.getClientY();
        int eventIndx = nextEvent++;
        eventIndx = eventIndx % EVENTS_FOR_SPEED_CALC;
        eventTimeStamps[eventIndx] = now;
        yPositions[eventIndx] = lastClientY;
        return isMovedSignificantly();
    }

    private boolean isMovedSignificantly() {
        return moved ? moved
                : Math.abs(origY - lastClientY) >= SIGNIFICANT_MOVE_THRESHOLD;
    }

    private void onTouchEnd(NativeEvent event) {
        if (!moved) {
            activeScrollDelegate = null;
            handlerRegistration.removeHandler();
            handlerRegistration = null;
            return;
        }

        int currentY = origScrollTop + deltaScrollPos;

        int maxFinalY = getMaxFinalY();

        int pixelsToMove;
        int finalY;
        int duration = -1;
        if (currentY > maxFinalY) {
            // we are over the max final pos, animate to end
            pixelsToMove = maxFinalY - currentY;
            finalY = maxFinalY;
        } else if (currentY < 0) {
            // we are below the max final pos, animate to beginning
            pixelsToMove = -currentY;
            finalY = 0;
        } else {
            double pixelsPerMs = calculateSpeed();
            // we are currently within scrollable area, calculate pixels that
            // we'll move due to momentum
            VConsole.log("pxPerMs" + pixelsPerMs);
            pixelsToMove = (int) (0.5 * pixelsPerMs * pixelsPerMs / FRICTION);
            if (pixelsPerMs < 0) {
                pixelsToMove = -pixelsToMove;
            }
            // VConsole.log("pixels to move" + pixelsToMove);

            finalY = currentY + pixelsToMove;

            if (finalY > maxFinalY + getMaxOverScroll()) {
                // VConsole.log("To max overscroll");
                finalY = getMaxFinalY() + getMaxOverScroll();
                int fixedPixelsToMove = finalY - currentY;
                pixelsPerMs = pixelsPerMs * pixelsToMove / fixedPixelsToMove
                        / FRICTION;
                pixelsToMove = fixedPixelsToMove;
            } else if (finalY < 0 - getMaxOverScroll()) {
                // VConsole.log("to min overscroll");
                finalY = -getMaxOverScroll();
                int fixedPixelsToMove = finalY - currentY;
                pixelsPerMs = pixelsPerMs * pixelsToMove / fixedPixelsToMove
                        / FRICTION;
                pixelsToMove = fixedPixelsToMove;
            } else {
                duration = (int) (Math.abs(pixelsPerMs / DECELERATION));
            }
        }
        if (duration == -1) {
            // did not keep in side borders or was outside borders, calculate
            // a good enough duration based on pixelsToBeMoved.
            duration = getAnimationTimeForDistance(pixelsToMove);
        }
        if (duration > MAX_DURATION) {
            VConsole.log("Max animation time. " + duration);
            duration = MAX_DURATION;
        }
        finalScrollTop = finalY;

        if (Math.abs(pixelsToMove) < 3 || duration < 20) {
            VConsole.log("Small 'momentum' " + pixelsToMove + " |  " + duration
                    + " Skipping animation,");
            moveTransformationToScrolloffset();
            return;
        }

        int translateY = -finalY + origScrollTop;
        translateTo(duration, translateY);
    }

    private double calculateSpeed() {
        if (nextEvent < EVENTS_FOR_SPEED_CALC) {
            VConsole.log("Not enough data for speed calculation");
            // not enough data for decent speed calculation, no momentum :-(
            return 0;
        }
        int idx = nextEvent % EVENTS_FOR_SPEED_CALC;
        final int firstPos = yPositions[idx];
        final Date firstTs = eventTimeStamps[idx];
        idx += EVENTS_FOR_SPEED_CALC;
        idx--;
        idx = idx % EVENTS_FOR_SPEED_CALC;
        final int lastPos = yPositions[idx];
        final Date lastTs = eventTimeStamps[idx];
        // speed as in change of scrolltop == -speedOfTouchPos
        return (firstPos - lastPos)
                / (double) (lastTs.getTime() - firstTs.getTime());

    }

    /**
     * Note positive scrolltop moves layer up, positive translate moves layer
     * down.
     * 
     * @param duration
     * @param translateY
     */
    private void translateTo(int duration, int translateY) {
        for (Element el : layers) {
            final Style style = el.getStyle();
            if (duration > 0) {
                style.setProperty("webkitTransitionDuration", duration + "ms");
                style.setProperty("webkitTransitionTimingFunction",
                        "cubic-bezier(0,0,0.25,1)");
                style.setProperty("webkitTransitionProperty",
                        "-webkit-transform");
                transitionOn = true;
                transitionStart = new Date();
                transitionDuration = new Date();
            } else {
                style.setProperty("webkitTransitionProperty", "none");
            }
            style.setProperty("webkitTransform", "translate3d(0px,"
                    + translateY + "px,0px)");
        }
    }

    private int getMaxOverScroll() {
        return scrolledElement.getClientHeight() / 4;
    }

    private int getMaxFinalY() {
        return scrolledElement.getScrollHeight()
                - scrolledElement.getClientHeight();
    }

    public void onPreviewNativeEvent(NativePreviewEvent event) {
        if (transitionOn) {
            /*
             * TODO allow starting new events. See issue in onTouchStart
             */
            event.cancel();
            return;
        }
        int typeInt = event.getTypeInt();
        switch (typeInt) {
        case Event.ONTOUCHMOVE:
            if (!event.isCanceled()) {
                onTouchMove(event.getNativeEvent());
                if (moved) {
                    event.cancel();
                }
            }
            break;
        case Event.ONTOUCHEND:
        case Event.ONTOUCHCANCEL:
            if (!event.isCanceled()) {
                if (moved) {
                    event.cancel();
                }
                onTouchEnd(event.getNativeEvent());
            }
            break;
        case Event.ONMOUSEMOVE:
            if (moved) {
                // no debug message, mobile safari generates these for some
                // compatibility purposes.
                event.cancel();
            }
            break;
        default:
            VConsole.log("Non touch event:" + event.getNativeEvent().getType());
            event.cancel();
            break;
        }
    }

    public void setElements(com.google.gwt.user.client.Element[] elements) {
        scrollableElements = elements;
    }
}
