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

package com.vaadin.server;

/**
 * FontAwesome set of font icons.
 * <p>
 * Each {@link FontIcon} comes from the FontAwesome font family, which is
 * included in the theme.<br/>
 * Consider this a starting point: it is unlikely an application needs exactly
 * these icons, and all of them, so you might want to consider making a custom
 * icon font - either to get other icons, or to minimize the size of the font.
 * </p>
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see http://fortawesome.github.io/Font-Awesome/
 */
public enum FontAwesome implements FontIcon {
    GLASS(0XF000), //
    MUSIC(0XF001), //
    SEARCH(0XF002), //
    ENVELOPE_O(0XF003), //
    HEART(0XF004), //
    STAR(0XF005), //
    STAR_O(0XF006), //
    USER(0XF007), //
    FILM(0XF008), //
    TH_LARGE(0XF009), //
    TH(0XF00A), //
    TH_LIST(0XF00B), //
    CHECK(0XF00C), //
    TIMES(0XF00D), //
    SEARCH_PLUS(0XF00E), //
    SEARCH_MINUS(0XF010), //
    POWER_OFF(0XF011), //
    SIGNAL(0XF012), //
    COG(0XF013), //
    TRASH_O(0XF014), //
    HOME(0XF015), //
    FILE_O(0XF016), //
    CLOCK_O(0XF017), //
    ROAD(0XF018), //
    DOWNLOAD(0XF019), //
    ARROW_CIRCLE_O_DOWN(0XF01A), //
    ARROW_CIRCLE_O_UP(0XF01B), //
    INBOX(0XF01C), //
    PLAY_CIRCLE_O(0XF01D), //
    REPEAT(0XF01E), //
    REFRESH(0XF021), //
    LIST_ALT(0XF022), //
    LOCK(0XF023), //
    FLAG(0XF024), //
    HEADPHONES(0XF025), //
    VOLUME_OFF(0XF026), //
    VOLUME_DOWN(0XF027), //
    VOLUME_UP(0XF028), //
    QRCODE(0XF029), //
    BARCODE(0XF02A), //
    TAG(0XF02B), //
    TAGS(0XF02C), //
    BOOK(0XF02D), //
    BOOKMARK(0XF02E), //
    PRINT(0XF02F), //
    CAMERA(0XF030), //
    FONT(0XF031), //
    BOLD(0XF032), //
    ITALIC(0XF033), //
    TEXT_HEIGHT(0XF034), //
    TEXT_WIDTH(0XF035), //
    ALIGN_LEFT(0XF036), //
    ALIGN_CENTER(0XF037), //
    ALIGN_RIGHT(0XF038), //
    ALIGN_JUSTIFY(0XF039), //
    LIST(0XF03A), //
    OUTDENT(0XF03B), //
    INDENT(0XF03C), //
    VIDEO_CAMERA(0XF03D), //
    PICTURE_O(0XF03E), //
    PENCIL(0XF040), //
    MAP_MARKER(0XF041), //
    ADJUST(0XF042), //
    TINT(0XF043), //
    PENCIL_SQUARE_O(0XF044), //
    SHARE_SQUARE_O(0XF045), //
    CHECK_SQUARE_O(0XF046), //
    ARROWS(0XF047), //
    STEP_BACKWARD(0XF048), //
    FAST_BACKWARD(0XF049), //
    BACKWARD(0XF04A), //
    PLAY(0XF04B), //
    PAUSE(0XF04C), //
    STOP(0XF04D), //
    FORWARD(0XF04E), //
    FAST_FORWARD(0XF050), //
    STEP_FORWARD(0XF051), //
    EJECT(0XF052), //
    CHEVRON_LEFT(0XF053), //
    CHEVRON_RIGHT(0XF054), //
    PLUS_CIRCLE(0XF055), //
    MINUS_CIRCLE(0XF056), //
    TIMES_CIRCLE(0XF057), //
    CHECK_CIRCLE(0XF058), //
    QUESTION_CIRCLE(0XF059), //
    INFO_CIRCLE(0XF05A), //
    CROSSHAIRS(0XF05B), //
    TIMES_CIRCLE_O(0XF05C), //
    CHECK_CIRCLE_O(0XF05D), //
    BAN(0XF05E), //
    ARROW_LEFT(0XF060), //
    ARROW_RIGHT(0XF061), //
    ARROW_UP(0XF062), //
    ARROW_DOWN(0XF063), //
    SHARE(0XF064), //
    EXPAND(0XF065), //
    COMPRESS(0XF066), //
    PLUS(0XF067), //
    MINUS(0XF068), //
    ASTERISK(0XF069), //
    EXCLAMATION_CIRCLE(0XF06A), //
    GIFT(0XF06B), //
    LEAF(0XF06C), //
    FIRE(0XF06D), //
    EYE(0XF06E), //
    EYE_SLASH(0XF070), //
    EXCLAMATION_TRIANGLE(0XF071), //
    PLANE(0XF072), //
    CALENDAR(0XF073), //
    RANDOM(0XF074), //
    COMMENT(0XF075), //
    MAGNET(0XF076), //
    CHEVRON_UP(0XF077), //
    CHEVRON_DOWN(0XF078), //
    RETWEET(0XF079), //
    SHOPPING_CART(0XF07A), //
    FOLDER(0XF07B), //
    FOLDER_OPEN(0XF07C), //
    ARROWS_V(0XF07D), //
    ARROWS_H(0XF07E), //
    BAR_CHART_O(0XF080), //
    TWITTER_SQUARE(0XF081), //
    FACEBOOK_SQUARE(0XF082), //
    CAMERA_RETRO(0XF083), //
    KEY(0XF084), //
    COGS(0XF085), //
    COMMENTS(0XF086), //
    THUMBS_O_UP(0XF087), //
    THUMBS_O_DOWN(0XF088), //
    STAR_HALF(0XF089), //
    HEART_O(0XF08A), //
    SIGN_OUT(0XF08B), //
    LINKEDIN_SQUARE(0XF08C), //
    THUMB_TACK(0XF08D), //
    EXTERNAL_LINK(0XF08E), //
    SIGN_IN(0XF090), //
    TROPHY(0XF091), //
    GITHUB_SQUARE(0XF092), //
    UPLOAD(0XF093), //
    LEMON_O(0XF094), //
    PHONE(0XF095), //
    SQUARE_O(0XF096), //
    BOOKMARK_O(0XF097), //
    PHONE_SQUARE(0XF098), //
    TWITTER(0XF099), //
    FACEBOOK(0XF09A), //
    GITHUB(0XF09B), //
    UNLOCK(0XF09C), //
    CREDIT_CARD(0XF09D), //
    RSS(0XF09E), //
    HDD_O(0XF0A0), //
    BULLHORN(0XF0A1), //
    BELL(0XF0F3), //
    CERTIFICATE(0XF0A3), //
    HAND_O_RIGHT(0XF0A4), //
    HAND_O_LEFT(0XF0A5), //
    HAND_O_UP(0XF0A6), //
    HAND_O_DOWN(0XF0A7), //
    ARROW_CIRCLE_LEFT(0XF0A8), //
    ARROW_CIRCLE_RIGHT(0XF0A9), //
    ARROW_CIRCLE_UP(0XF0AA), //
    ARROW_CIRCLE_DOWN(0XF0AB), //
    GLOBE(0XF0AC), //
    WRENCH(0XF0AD), //
    TASKS(0XF0AE), //
    FILTER(0XF0B0), //
    BRIEFCASE(0XF0B1), //
    ARROWS_ALT(0XF0B2), //
    USERS(0XF0C0), //
    LINK(0XF0C1), //
    CLOUD(0XF0C2), //
    FLASK(0XF0C3), //
    SCISSORS(0XF0C4), //
    FILES_O(0XF0C5), //
    PAPERCLIP(0XF0C6), //
    FLOPPY_O(0XF0C7), //
    SQUARE(0XF0C8), //
    BARS(0XF0C9), //
    LIST_UL(0XF0CA), //
    LIST_OL(0XF0CB), //
    STRIKETHROUGH(0XF0CC), //
    UNDERLINE(0XF0CD), //
    TABLE(0XF0CE), //
    MAGIC(0XF0D0), //
    TRUCK(0XF0D1), //
    PINTEREST(0XF0D2), //
    PINTEREST_SQUARE(0XF0D3), //
    GOOGLE_PLUS_SQUARE(0XF0D4), //
    GOOGLE_PLUS(0XF0D5), //
    MONEY(0XF0D6), //
    CARET_DOWN(0XF0D7), //
    CARET_UP(0XF0D8), //
    CARET_LEFT(0XF0D9), //
    CARET_RIGHT(0XF0DA), //
    COLUMNS(0XF0DB), //
    SORT(0XF0DC), //
    SORT_ASC(0XF0DD), //
    SORT_DESC(0XF0DE), //
    ENVELOPE(0XF0E0), //
    LINKEDIN(0XF0E1), //
    UNDO(0XF0E2), //
    GAVEL(0XF0E3), //
    TACHOMETER(0XF0E4), //
    COMMENT_O(0XF0E5), //
    COMMENTS_O(0XF0E6), //
    BOLT(0XF0E7), //
    SITEMAP(0XF0E8), //
    UMBRELLA(0XF0E9), //
    CLIPBOARD(0XF0EA), //
    LIGHTBULB_O(0XF0EB), //
    EXCHANGE(0XF0EC), //
    CLOUD_DOWNLOAD(0XF0ED), //
    CLOUD_UPLOAD(0XF0EE), //
    USER_MD(0XF0F0), //
    STETHOSCOPE(0XF0F1), //
    SUITCASE(0XF0F2), //
    BELL_O(0XF0A2), //
    COFFEE(0XF0F4), //
    CUTLERY(0XF0F5), //
    FILE_TEXT_O(0XF0F6), //
    BUILDING_O(0XF0F7), //
    HOSPITAL_O(0XF0F8), //
    AMBULANCE(0XF0F9), //
    MEDKIT(0XF0FA), //
    FIGHTER_JET(0XF0FB), //
    BEER(0XF0FC), //
    H_SQUARE(0XF0FD), //
    PLUS_SQUARE(0XF0FE), //
    ANGLE_DOUBLE_LEFT(0XF100), //
    ANGLE_DOUBLE_RIGHT(0XF101), //
    ANGLE_DOUBLE_UP(0XF102), //
    ANGLE_DOUBLE_DOWN(0XF103), //
    ANGLE_LEFT(0XF104), //
    ANGLE_RIGHT(0XF105), //
    ANGLE_UP(0XF106), //
    ANGLE_DOWN(0XF107), //
    DESKTOP(0XF108), //
    LAPTOP(0XF109), //
    TABLET(0XF10A), //
    MOBILE(0XF10B), //
    CIRCLE_O(0XF10C), //
    QUOTE_LEFT(0XF10D), //
    QUOTE_RIGHT(0XF10E), //
    SPINNER(0XF110), //
    CIRCLE(0XF111), //
    REPLY(0XF112), //
    GITHUB_ALT(0XF113), //
    FOLDER_O(0XF114), //
    FOLDER_OPEN_O(0XF115), //
    SMILE_O(0XF118), //
    FROWN_O(0XF119), //
    MEH_O(0XF11A), //
    GAMEPAD(0XF11B), //
    KEYBOARD_O(0XF11C), //
    FLAG_O(0XF11D), //
    FLAG_CHECKERED(0XF11E), //
    TERMINAL(0XF120), //
    CODE(0XF121), //
    REPLY_ALL(0XF122), //
    MAIL_REPLY_ALL(0XF122), //
    STAR_HALF_O(0XF123), //
    LOCATION_ARROW(0XF124), //
    CROP(0XF125), //
    CODE_FORK(0XF126), //
    CHAIN_BROKEN(0XF127), //
    QUESTION(0XF128), //
    INFO(0XF129), //
    EXCLAMATION(0XF12A), //
    SUPERSCRIPT(0XF12B), //
    SUBSCRIPT(0XF12C), //
    ERASER(0XF12D), //
    PUZZLE_PIECE(0XF12E), //
    MICROPHONE(0XF130), //
    MICROPHONE_SLASH(0XF131), //
    SHIELD(0XF132), //
    CALENDAR_O(0XF133), //
    FIRE_EXTINGUISHER(0XF134), //
    ROCKET(0XF135), //
    MAXCDN(0XF136), //
    CHEVRON_CIRCLE_LEFT(0XF137), //
    CHEVRON_CIRCLE_RIGHT(0XF138), //
    CHEVRON_CIRCLE_UP(0XF139), //
    CHEVRON_CIRCLE_DOWN(0XF13A), //
    HTML5(0XF13B), //
    CSS3(0XF13C), //
    ANCHOR(0XF13D), //
    UNLOCK_ALT(0XF13E), //
    BULLSEYE(0XF140), //
    ELLIPSIS_H(0XF141), //
    ELLIPSIS_V(0XF142), //
    RSS_SQUARE(0XF143), //
    PLAY_CIRCLE(0XF144), //
    TICKET(0XF145), //
    MINUS_SQUARE(0XF146), //
    MINUS_SQUARE_O(0XF147), //
    LEVEL_UP(0XF148), //
    LEVEL_DOWN(0XF149), //
    CHECK_SQUARE(0XF14A), //
    PENCIL_SQUARE(0XF14B), //
    EXTERNAL_LINK_SQUARE(0XF14C), //
    SHARE_SQUARE(0XF14D), //
    COMPASS(0XF14E), //
    CARET_SQUARE_O_DOWN(0XF150), //
    CARET_SQUARE_O_UP(0XF151), //
    CARET_SQUARE_O_RIGHT(0XF152), //
    EUR(0XF153), //
    GBP(0XF154), //
    USD(0XF155), //
    INR(0XF156), //
    JPY(0XF157), //
    RUB(0XF158), //
    KRW(0XF159), //
    BTC(0XF15A), //
    FILE(0XF15B), //
    FILE_TEXT(0XF15C), //
    SORT_ALPHA_ASC(0XF15D), //
    SORT_ALPHA_DESC(0XF15E), //
    SORT_AMOUNT_ASC(0XF160), //
    SORT_AMOUNT_DESC(0XF161), //
    SORT_NUMERIC_ASC(0XF162), //
    SORT_NUMERIC_DESC(0XF163), //
    THUMBS_UP(0XF164), //
    THUMBS_DOWN(0XF165), //
    YOUTUBE_SQUARE(0XF166), //
    YOUTUBE(0XF167), //
    XING(0XF168), //
    XING_SQUARE(0XF169), //
    YOUTUBE_PLAY(0XF16A), //
    DROPBOX(0XF16B), //
    STACK_OVERFLOW(0XF16C), //
    INSTAGRAM(0XF16D), //
    FLICKR(0XF16E), //
    ADN(0XF170), //
    BITBUCKET(0XF171), //
    BITBUCKET_SQUARE(0XF172), //
    TUMBLR(0XF173), //
    TUMBLR_SQUARE(0XF174), //
    LONG_ARROW_DOWN(0XF175), //
    LONG_ARROW_UP(0XF176), //
    LONG_ARROW_LEFT(0XF177), //
    LONG_ARROW_RIGHT(0XF178), //
    APPLE(0XF179), //
    WINDOWS(0XF17A), //
    ANDROID(0XF17B), //
    LINUX(0XF17C), //
    DRIBBBLE(0XF17D), //
    SKYPE(0XF17E), //
    FOURSQUARE(0XF180), //
    TRELLO(0XF181), //
    FEMALE(0XF182), //
    MALE(0XF183), //
    GITTIP(0XF184), //
    SUN_O(0XF185), //
    MOON_O(0XF186), //
    ARCHIVE(0XF187), //
    BUG(0XF188), //
    VK(0XF189), //
    WEIBO(0XF18A), //
    RENREN(0XF18B), //
    PAGELINES(0XF18C), //
    STACK_EXCHANGE(0XF18D), //
    ARROW_CIRCLE_O_RIGHT(0XF18E), //
    ARROW_CIRCLE_O_LEFT(0XF190), //
    CARET_SQUARE_O_LEFT(0XF191), //
    DOT_CIRCLE_O(0XF192), //
    WHEELCHAIR(0XF193), //
    VIMEO_SQUARE(0XF194), //
    TRY(0XF195), //
    PLUS_SQUARE_O(0XF196);

    private static final String fontFamily = "FontAwesome";
    private int codepoint;

    FontAwesome(int codepoint) {
        this.codepoint = codepoint;
    }

    /**
     * Unsupported: {@link FontIcon} does not have a MIME type and is not a
     * {@link Resource} that can be used in a context where a MIME type would be
     * needed.
     */
    @Override
    public String getMIMEType() {
        throw new UnsupportedOperationException(FontIcon.class.getSimpleName()
                + " should not be used where a MIME type is needed.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.FontIcon#getFontFamily()
     */
    @Override
    public String getFontFamily() {
        return fontFamily;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.FontIcon#getCodepoint()
     */
    @Override
    public int getCodepoint() {
        return codepoint;
    }

    @Override
    public String getHtml() {
        return "<span class=\"v-icon\" style=\"font-family: " + fontFamily
                + ";\">&#x" + Integer.toHexString(codepoint) + ";</span>";
    }

}
