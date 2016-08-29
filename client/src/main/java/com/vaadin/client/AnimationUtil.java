/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.vaadin.client.AnimationUtil.AnimationEndListener;

/**
 * Utility methods for working with CSS transitions and animations.
 * 
 * @author Vaadin Ltd
 * @since 7.3
 */
public class AnimationUtil {

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * Set the animation-duration CSS property.
     * 
     * @param elem
     *            the element whose animation-duration to set
     * @param duration
     *            the duration as a valid CSS value
     */
    public static void setAnimationDuration(Element elem, String duration) {
        Style style = elem.getStyle();
        style.setProperty(ANIMATION_PROPERTY_NAME + "Duration", duration);
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     * 
     * Set the animation-delay CSS property.
     * 
     * @param elem
     *            the element whose animation-delay to set
     * @param delay
     *            the delay as a valid CSS value
     */
    public static void setAnimationDelay(Element elem, String delay) {
        Style style = elem.getStyle();
        style.setProperty(ANIMATION_PROPERTY_NAME + "Delay", delay);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public static native JavaScriptObject addAnimationEndListener(Element elem,
            AnimationEndListener listener)
    /*-{
      var callbackFunc = $entry(function(e) {
        listener.@com.vaadin.client.AnimationUtil.AnimationEndListener::onAnimationEnd(Lcom/google/gwt/dom/client/NativeEvent;)(e);
      });
      callbackFunc.listener = listener;

      elem.addEventListener(@com.vaadin.client.AnimationUtil::ANIMATION_END_EVENT_NAME, callbackFunc, false);
      
      // Store function reference for later removal
      if(!elem._vaadin_animationend_callbacks) {
        elem._vaadin_animationend_callbacks = [];
      }
      elem._vaadin_animationend_callbacks.push(callbackFunc);
      
      return callbackFunc;
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public static native void removeAnimationEndListener(Element elem,
            JavaScriptObject listener)
    /*-{
      elem.removeEventListener(@com.vaadin.client.AnimationUtil::ANIMATION_END_EVENT_NAME, listener, false);
    }-*/;

    /**
     * Removes the given animation listener.
     *
     * @param element
     *            the element which has the listener
     * @param animationEndListener
     *            the listener to remove
     * @return <code>true</code> if the listener was removed, <code>false</code>
     *         if the listener was not registered to the given element
     */
    public static native boolean removeAnimationEndListener(Element elem,
            AnimationEndListener animationEndListener)
    /*-{
      if(elem._vaadin_animationend_callbacks) {
        var callbacks = elem._vaadin_animationend_callbacks;
        for(var i=0; i < callbacks.length; i++) {
          if (callbacks[i].listener == animationEndListener) {
              elem.removeEventListener(@com.vaadin.client.AnimationUtil::ANIMATION_END_EVENT_NAME, callbacks[i], false);
              return true;
          }
        }
        return false;
      }
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public static native void removeAllAnimationEndListeners(Element elem)
    /*-{
      if(elem._vaadin_animationend_callbacks) {
        var callbacks = elem._vaadin_animationend_callbacks;
        for(var i=0; i < callbacks.length; i++) {
          elem.removeEventListener(@com.vaadin.client.AnimationUtil::ANIMATION_END_EVENT_NAME, callbacks[i], false);
        }
      }
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public interface AnimationEndListener {
        public void onAnimationEnd(NativeEvent event);
    }

    /** For internal use only. May be removed or replaced in the future. */
    public static native String getAnimationName(NativeEvent event)
    /*-{
        if(event.webkitAnimationName)
            return event.webkitAnimationName;
        else if(event.animationName)
            return event.animationName;
        else if(event.mozAnimationName)
            return event.mozAnimationName;
        else if(event.oAnimationName)
            return event.oAnimationName;

        return "";
    }-*/;

    /** For internal use only. May be removed or replaced in the future. */
    public static native String getAnimationName(ComputedStyle cstyle)
    /*-{
        var cs = cstyle.@com.vaadin.client.ComputedStyle::computedStyle;

        if(!cs.getPropertyValue)
            return "";

        if(cs.getPropertyValue("-webkit-animation-name"))
            return cs.getPropertyValue("-webkit-animation-name");

        else if(cs.getPropertyValue("animation-name"))
            return cs.getPropertyValue("animation-name");

        else if(cs.getPropertyValue("-moz-animation-name"))
            return cs.getPropertyValue("-moz-animation-name");

        else if(cs.getPropertyValue("-o-animation-name"))
            return cs.getPropertyValue("-o-animation-name");

        return "";
    }-*/;

    private static final String ANIMATION_END_EVENT_NAME = whichAnimationEndEvent();

    private static native String whichAnimationEndEvent()
    /*-{
        var el = document.createElement('fakeelement');
        var anims = {
          'animationName': 'animationend',
          'OAnimationName': 'oAnimationEnd',
          'MozAnimation': 'animationend',
          'WebkitAnimation': 'webkitAnimationEnd'
        }

        for(var a in anims){
            if( el.style[a] !== undefined ){
                return anims[a];
            }
        }
    }-*/;

    private static final String ANIMATION_PROPERTY_NAME = whichAnimationProperty();

    private static native String whichAnimationProperty()
    /*-{
        var el = document.createElement('fakeelement');
        var anims = [
          'animation',
          'oAnimation',
          'mozAnimation',
          'webkitAnimation'
        ]

        for(var i=0; i < anims.length; i++) {
            if( el.style[anims[i]] !== undefined ){
                return anims[i];
            }
        }
    }-*/;

}
