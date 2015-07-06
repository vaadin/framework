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

package com.vaadin.event;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.server.Resource;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

/**
 * Shortcuts are a special type of {@link Action}s used to create keyboard
 * shortcuts.
 * <p>
 * The ShortcutAction is triggered when the user presses a given key in
 * combination with the (optional) given modifier keys.
 * </p>
 * <p>
 * ShortcutActions can be global (by attaching to the {@link Window}), or
 * attached to different parts of the UI so that a specific shortcut is only
 * valid in part of the UI. For instance, one can attach shortcuts to a specific
 * {@link Panel} - look for {@link ComponentContainer}s implementing
 * {@link Handler Action.Handler} or {@link Notifier Action.Notifier}.
 * </p>
 * <p>
 * ShortcutActions have a caption that may be used to display the shortcut
 * visually. This allows the ShortcutAction to be used as a plain Action while
 * still reacting to a keyboard shortcut. Note that this functionality is not
 * very well supported yet, but it might still be a good idea to give a caption
 * to the shortcut.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 4.0.1
 */
@SuppressWarnings("serial")
public class ShortcutAction extends Action {

    private final int keyCode;

    private final int[] modifiers;

    /**
     * Creates a shortcut that reacts to the given {@link KeyCode} and
     * (optionally) {@link ModifierKey}s. <br/>
     * The shortcut might be shown in the UI (e.g context menu), in which case
     * the caption will be used.
     * 
     * @param caption
     *            used when displaying the shortcut visually
     * @param kc
     *            KeyCode that the shortcut reacts to
     * @param m
     *            optional modifier keys
     */
    public ShortcutAction(String caption, int kc, int... m) {
        super(caption);
        keyCode = kc;
        modifiers = m;
    }

    /**
     * Creates a shortcut that reacts to the given {@link KeyCode} and
     * (optionally) {@link ModifierKey}s. <br/>
     * The shortcut might be shown in the UI (e.g context menu), in which case
     * the caption and icon will be used.
     * 
     * @param caption
     *            used when displaying the shortcut visually
     * @param icon
     *            used when displaying the shortcut visually
     * @param kc
     *            KeyCode that the shortcut reacts to
     * @param m
     *            optional modifier keys
     */
    public ShortcutAction(String caption, Resource icon, int kc, int... m) {
        super(caption, icon);
        keyCode = kc;
        modifiers = m;
    }

    /**
     * Used in the caption shorthand notation to indicate the ALT modifier.
     */
    public static final char SHORTHAND_CHAR_ALT = '&';
    /**
     * Used in the caption shorthand notation to indicate the SHIFT modifier.
     */
    public static final char SHORTHAND_CHAR_SHIFT = '_';
    /**
     * Used in the caption shorthand notation to indicate the CTRL modifier.
     */
    public static final char SHORTHAND_CHAR_CTRL = '^';

    // regex-quote (escape) the characters
    private static final String SHORTHAND_ALT = Pattern.quote(Character
            .toString(SHORTHAND_CHAR_ALT));
    private static final String SHORTHAND_SHIFT = Pattern.quote(Character
            .toString(SHORTHAND_CHAR_SHIFT));
    private static final String SHORTHAND_CTRL = Pattern.quote(Character
            .toString(SHORTHAND_CHAR_CTRL));
    // Used for replacing escaped chars, e.g && with &
    private static final Pattern SHORTHAND_ESCAPE = Pattern.compile("("
            + SHORTHAND_ALT + "?)" + SHORTHAND_ALT + "|(" + SHORTHAND_SHIFT
            + "?)" + SHORTHAND_SHIFT + "|(" + SHORTHAND_CTRL + "?)"
            + SHORTHAND_CTRL);
    // Used for removing escaped chars, only leaving real shorthands
    private static final Pattern SHORTHAND_REMOVE = Pattern.compile("(["
            + SHORTHAND_ALT + "|" + SHORTHAND_SHIFT + "|" + SHORTHAND_CTRL
            + "])\\1");
    // Mnemonic char, optionally followed by another, and optionally a third
    private static final Pattern SHORTHANDS = Pattern.compile("("
            + SHORTHAND_ALT + "|" + SHORTHAND_SHIFT + "|" + SHORTHAND_CTRL
            + ")(?!\\1)(?:(" + SHORTHAND_ALT + "|" + SHORTHAND_SHIFT + "|"
            + SHORTHAND_CTRL + ")(?!\\1|\\2))?(?:(" + SHORTHAND_ALT + "|"
            + SHORTHAND_SHIFT + "|" + SHORTHAND_CTRL + ")(?!\\1|\\2|\\3))?.");

    /**
     * Constructs a ShortcutAction using a shorthand notation to encode the
     * keycode and modifiers in the caption.
     * <p>
     * Insert one or more modifier characters before the character to use as
     * keycode. E.g <code>"&Save"</code> will make a shortcut responding to
     * ALT-S, <code>"E^xit"</code> will respond to CTRL-X.<br/>
     * Multiple modifiers can be used, e.g <code>"&^Delete"</code> will respond
     * to CTRL-ALT-D (the order of the modifier characters is not important).
     * </p>
     * <p>
     * The modifier characters will be removed from the caption. The modifier
     * character is be escaped by itself: two consecutive characters are turned
     * into the original character w/o the special meaning. E.g
     * <code>"Save&&&close"</code> will respond to ALT-C, and the caption will
     * say "Save&close".
     * </p>
     * 
     * @param shorthandCaption
     *            the caption in modifier shorthand
     */
    public ShortcutAction(String shorthandCaption) {
        this(shorthandCaption, null);
    }

    /**
     * Constructs a ShortcutAction using a shorthand notation to encode the
     * keycode a in the caption.
     * <p>
     * This works the same way as {@link #ShortcutAction(String)}, with the
     * exception that the modifiers given override those indicated in the
     * caption. I.e use any of the modifier characters in the caption to
     * indicate the keycode, but the modifier will be the given set.<br/>
     * E.g
     * <code>new ShortcutAction("Do &stuff", new int[]{ShortcutAction.ModifierKey.CTRL}));</code>
     * will respond to CTRL-S.
     * </p>
     * 
     * @param shorthandCaption
     * @param modifierKeys
     */
    public ShortcutAction(String shorthandCaption, int... modifierKeys) {
        // && -> & etc
        super(SHORTHAND_ESCAPE.matcher(shorthandCaption).replaceAll("$1$2$3"));
        // replace escaped chars with something that won't accidentally match
        shorthandCaption = SHORTHAND_REMOVE.matcher(shorthandCaption)
                .replaceAll("\u001A");
        Matcher matcher = SHORTHANDS.matcher(shorthandCaption);
        if (matcher.find()) {
            String match = matcher.group();

            // KeyCode from last char in match, uppercase
            keyCode = Character.toUpperCase(matcher.group().charAt(
                    match.length() - 1));

            // Given modifiers override this indicated in the caption
            if (modifierKeys != null) {
                modifiers = modifierKeys;
            } else {
                // Read modifiers from caption
                int[] mod = new int[match.length() - 1];
                for (int i = 0; i < mod.length; i++) {
                    int kc = match.charAt(i);
                    switch (kc) {
                    case SHORTHAND_CHAR_ALT:
                        mod[i] = ModifierKey.ALT;
                        break;
                    case SHORTHAND_CHAR_CTRL:
                        mod[i] = ModifierKey.CTRL;
                        break;
                    case SHORTHAND_CHAR_SHIFT:
                        mod[i] = ModifierKey.SHIFT;
                        break;
                    }
                }
                modifiers = mod;
            }

        } else {
            keyCode = -1;
            modifiers = modifierKeys;
        }
    }

    /**
     * Get the {@link KeyCode} that this shortcut reacts to (in combination with
     * the {@link ModifierKey}s).
     * 
     * @return keycode for this shortcut
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * Get the {@link ModifierKey}s required for the shortcut to react.
     * 
     * @return modifier keys for this shortcut
     */
    public int[] getModifiers() {
        return modifiers;
    }

    /**
     * Key codes that can be used for shortcuts
     * 
     */
    public interface KeyCode extends Serializable {
        public static final int ENTER = 13;

        public static final int ESCAPE = 27;

        public static final int PAGE_UP = 33;

        public static final int PAGE_DOWN = 34;

        public static final int TAB = 9;

        public static final int ARROW_LEFT = 37;

        public static final int ARROW_UP = 38;

        public static final int ARROW_RIGHT = 39;

        public static final int ARROW_DOWN = 40;

        public static final int BACKSPACE = 8;

        public static final int DELETE = 46;

        public static final int INSERT = 45;

        public static final int END = 35;

        public static final int HOME = 36;

        public static final int F1 = 112;

        public static final int F2 = 113;

        public static final int F3 = 114;

        public static final int F4 = 115;

        public static final int F5 = 116;

        public static final int F6 = 117;

        public static final int F7 = 118;

        public static final int F8 = 119;

        public static final int F9 = 120;

        public static final int F10 = 121;

        public static final int F11 = 122;

        public static final int F12 = 123;

        public static final int A = 65;

        public static final int B = 66;

        public static final int C = 67;

        public static final int D = 68;

        public static final int E = 69;

        public static final int F = 70;

        public static final int G = 71;

        public static final int H = 72;

        public static final int I = 73;

        public static final int J = 74;

        public static final int K = 75;

        public static final int L = 76;

        public static final int M = 77;

        public static final int N = 78;

        public static final int O = 79;

        public static final int P = 80;

        public static final int Q = 81;

        public static final int R = 82;

        public static final int S = 83;

        public static final int T = 84;

        public static final int U = 85;

        public static final int V = 86;

        public static final int W = 87;

        public static final int X = 88;

        public static final int Y = 89;

        public static final int Z = 90;

        public static final int NUM0 = 48;

        public static final int NUM1 = 49;

        public static final int NUM2 = 50;

        public static final int NUM3 = 51;

        public static final int NUM4 = 52;

        public static final int NUM5 = 53;

        public static final int NUM6 = 54;

        public static final int NUM7 = 55;

        public static final int NUM8 = 56;

        public static final int NUM9 = 57;

        public static final int SPACEBAR = 32;
    }

    /**
     * Modifier key constants
     * 
     */
    public interface ModifierKey extends Serializable {
        public static final int SHIFT = 16;

        public static final int CTRL = 17;

        public static final int ALT = 18;

        public static final int META = 91;
    }
}
