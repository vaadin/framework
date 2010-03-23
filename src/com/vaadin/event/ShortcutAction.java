/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.event;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.terminal.Resource;

/**
 * Extends Action class with keyboard bindings. TODO: fix documentation.
 * 
 * @author IT Mill Ltd.
 * @version
 * @since 4.0.1
 */
@SuppressWarnings("serial")
public class ShortcutAction extends Action {

    private final int keyCode;

    private final int[] modifiers;

    public ShortcutAction(String caption, int kc, int[] m) {
        super(caption);
        keyCode = kc;
        modifiers = m;
    }

    public ShortcutAction(String caption, Resource icon, int kc, int[] m) {
        super(caption, icon);
        keyCode = kc;
        modifiers = m;
    }

    /**
     * Used in the caption shorthand notation to indicate the ALT modifier.
     */
    public static final char MNEMONIC_CHAR_ALT = '&';
    /**
     * Used in the caption shorthand notation to indicate the SHIFT modifier.
     */
    public static final char MNEMONIC_CHAR_SHIFT = '%';
    /**
     * Used in the caption shorthand notation to indicate the CTRL modifier.
     */
    public static final char MNEMONIC_CHAR_CTRL = '^';

    // regex-quote (escape) the characters
    private static final String MNEMONIC_ALT = Pattern.quote(Character
            .toString(MNEMONIC_CHAR_ALT));
    private static final String MNEMONIC_SHIFT = Pattern.quote(Character
            .toString(MNEMONIC_CHAR_SHIFT));
    private static final String MNEMONIC_CTRL = Pattern.quote(Character
            .toString(MNEMONIC_CHAR_CTRL));
    // Used for replacing escaped chars, e.g && with &
    private static final Pattern MNEMONICS_ESCAPE = Pattern.compile("("
            + MNEMONIC_ALT + "?)" + MNEMONIC_ALT + "|(" + MNEMONIC_SHIFT + "?)"
            + MNEMONIC_SHIFT + "|(" + MNEMONIC_CTRL + "?)" + MNEMONIC_CTRL);
    // Used for removing escaped chars, only leaving real mnemonics
    private static final Pattern MNEMONICS_REMOVE = Pattern.compile("(["
            + MNEMONIC_ALT + "|" + MNEMONIC_SHIFT + "|" + MNEMONIC_CTRL
            + "])\\1");
    // Mnemonic char, optionally followed by another, and optionally a third
    private static final Pattern MNEMONICS = Pattern.compile("(" + MNEMONIC_ALT
            + "|" + MNEMONIC_SHIFT + "|" + MNEMONIC_CTRL + ")(?!\\1)(?:("
            + MNEMONIC_ALT + "|" + MNEMONIC_SHIFT + "|" + MNEMONIC_CTRL
            + ")(?!\\1|\\2))?(?:(" + MNEMONIC_ALT + "|" + MNEMONIC_SHIFT + "|"
            + MNEMONIC_CTRL + ")(?!\\1|\\2|\\3))?.");

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
    public ShortcutAction(String shorthandCaption, int[] modifierKeys) {
        // && -> & etc
        super(MNEMONICS_ESCAPE.matcher(shorthandCaption).replaceAll("$1$2$3"));
        // replace escaped chars with something that won't accidentally match
        shorthandCaption = MNEMONICS_REMOVE.matcher(shorthandCaption)
                .replaceAll("\u001A");
        Matcher matcher = MNEMONICS.matcher(shorthandCaption);
        if (matcher.find()) {
            String match = matcher.group();

            // KeyCode from last char in match, lowercase
            keyCode = Character.toLowerCase(matcher.group().charAt(
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
                    case MNEMONIC_CHAR_ALT:
                        mod[i] = ModifierKey.ALT;
                        break;
                    case MNEMONIC_CHAR_CTRL:
                        mod[i] = ModifierKey.CTRL;
                        break;
                    case MNEMONIC_CHAR_SHIFT:
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

    public int getKeyCode() {
        return keyCode;
    }

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
    }
}
