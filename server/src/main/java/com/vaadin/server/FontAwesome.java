/*
 * Copyright 2000-2016 Vaadin Ltd.
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
 * <p>
 * The Font Awesome version currently included is 4.4.0.
 * </p>
 *
 * @since 7.2
 * @author Vaadin Ltd
 * @see http://fontawesome.github.io/Font-Awesome/
 * @deprecated Since 8.0 replaced with included Vaadin Icons
 *             https://vaadin.com/icons (#7979). Will not be updated to include
 *             new icons.
 */
@Deprecated
public enum FontAwesome implements FontIcon {
    _500PX(0XF26E), //
    ADJUST(0XF042), //
    ADN(0XF170), //
    ALIGN_CENTER(0XF037), //
    ALIGN_JUSTIFY(0XF039), //
    ALIGN_LEFT(0XF036), //
    ALIGN_RIGHT(0XF038), //
    AMAZON(0XF270), //
    AMBULANCE(0XF0F9), //
    ANCHOR(0XF13D), //
    ANDROID(0XF17B), //
    ANGELLIST(0XF209), //
    ANGLE_DOUBLE_DOWN(0XF103), //
    ANGLE_DOUBLE_LEFT(0XF100), //
    ANGLE_DOUBLE_RIGHT(0XF101), //
    ANGLE_DOUBLE_UP(0XF102), //
    ANGLE_DOWN(0XF107), //
    ANGLE_LEFT(0XF104), //
    ANGLE_RIGHT(0XF105), //
    ANGLE_UP(0XF106), //
    APPLE(0XF179), //
    ARCHIVE(0XF187), //
    AREA_CHART(0XF1FE), //
    ARROW_CIRCLE_DOWN(0XF0AB), //
    ARROW_CIRCLE_LEFT(0XF0A8), //
    ARROW_CIRCLE_O_DOWN(0XF01A), //
    ARROW_CIRCLE_O_LEFT(0XF190), //
    ARROW_CIRCLE_O_RIGHT(0XF18E), //
    ARROW_CIRCLE_O_UP(0XF01B), //
    ARROW_CIRCLE_RIGHT(0XF0A9), //
    ARROW_CIRCLE_UP(0XF0AA), //
    ARROW_DOWN(0XF063), //
    ARROW_LEFT(0XF060), //
    ARROW_RIGHT(0XF061), //
    ARROW_UP(0XF062), //
    ARROWS(0XF047), //
    ARROWS_ALT(0XF0B2), //
    ARROWS_H(0XF07E), //
    ARROWS_V(0XF07D), //
    ASTERISK(0XF069), //
    AT(0XF1FA), //
    AUTOMOBILE(0XF1B9), //
    BACKWARD(0XF04A), //
    BALANCE_SCALE(0XF24E), //
    BAN(0XF05E), //
    BANK(0XF19C), //
    BAR_CHART(0XF080), //
    BAR_CHART_O(0XF080), //
    BARCODE(0XF02A), //
    BARS(0XF0C9), //
    BATTERY_0(0XF244), //
    BATTERY_1(0XF243), //
    BATTERY_2(0XF242), //
    BATTERY_3(0XF241), //
    BATTERY_4(0XF240), //
    BATTERY_EMPTY(0XF244), //
    BATTERY_FULL(0XF240), //
    BATTERY_HALF(0XF242), //
    BATTERY_QUARTER(0XF243), //
    BATTERY_THREE_QUARTERS(0XF241), //
    BED(0XF236), //
    BEER(0XF0FC), //
    BEHANCE(0XF1B4), //
    BEHANCE_SQUARE(0XF1B5), //
    BELL(0XF0F3), //
    BELL_O(0XF0A2), //
    BELL_SLASH(0XF1F6), //
    BELL_SLASH_O(0XF1F7), //
    BICYCLE(0XF206), //
    BINOCULARS(0XF1E5), //
    BIRTHDAY_CAKE(0XF1FD), //
    BITBUCKET(0XF171), //
    BITBUCKET_SQUARE(0XF172), //
    BITCOIN(0XF15A), //
    BLACK_TIE(0XF27E), //
    BLUETOOTH(0XF293), //
    BLUETOOTH_B(0XF294), //
    BOLD(0XF032), //
    BOLT(0XF0E7), //
    BOMB(0XF1E2), //
    BOOK(0XF02D), //
    BOOKMARK(0XF02E), //
    BOOKMARK_O(0XF097), //
    BRIEFCASE(0XF0B1), //
    BTC(0XF15A), //
    BUG(0XF188), //
    BUILDING(0XF1AD), //
    BUILDING_O(0XF0F7), //
    BULLHORN(0XF0A1), //
    BULLSEYE(0XF140), //
    BUS(0XF207), //
    BUYSELLADS(0XF20D), //
    CAB(0XF1BA), //
    CALCULATOR(0XF1EC), //
    CALENDAR(0XF073), //
    CALENDAR_CHECK_O(0XF274), //
    CALENDAR_MINUS_O(0XF272), //
    CALENDAR_O(0XF133), //
    CALENDAR_PLUS_O(0XF271), //
    CALENDAR_TIMES_O(0XF273), //
    CAMERA(0XF030), //
    CAMERA_RETRO(0XF083), //
    CAR(0XF1B9), //
    CARET_DOWN(0XF0D7), //
    CARET_LEFT(0XF0D9), //
    CARET_RIGHT(0XF0DA), //
    CARET_SQUARE_O_DOWN(0XF150), //
    CARET_SQUARE_O_LEFT(0XF191), //
    CARET_SQUARE_O_RIGHT(0XF152), //
    CARET_SQUARE_O_UP(0XF151), //
    CARET_UP(0XF0D8), //
    CART_ARROW_DOWN(0XF218), //
    CART_PLUS(0XF217), //
    CC(0XF20A), //
    CC_AMEX(0XF1F3), //
    CC_DINERS_CLUB(0XF24C), //
    CC_DISCOVER(0XF1F2), //
    CC_JCB(0XF24B), //
    CC_MASTERCARD(0XF1F1), //
    CC_PAYPAL(0XF1F4), //
    CC_STRIPE(0XF1F5), //
    CC_VISA(0XF1F0), //
    CERTIFICATE(0XF0A3), //
    CHAIN(0XF0C1), //
    CHAIN_BROKEN(0XF127), //
    CHECK(0XF00C), //
    CHECK_CIRCLE(0XF058), //
    CHECK_CIRCLE_O(0XF05D), //
    CHECK_SQUARE(0XF14A), //
    CHECK_SQUARE_O(0XF046), //
    CHEVRON_CIRCLE_DOWN(0XF13A), //
    CHEVRON_CIRCLE_LEFT(0XF137), //
    CHEVRON_CIRCLE_RIGHT(0XF138), //
    CHEVRON_CIRCLE_UP(0XF139), //
    CHEVRON_DOWN(0XF078), //
    CHEVRON_LEFT(0XF053), //
    CHEVRON_RIGHT(0XF054), //
    CHEVRON_UP(0XF077), //
    CHILD(0XF1AE), //
    CHROME(0XF268), //
    CIRCLE(0XF111), //
    CIRCLE_O(0XF10C), //
    CIRCLE_O_NOTCH(0XF1CE), //
    CIRCLE_THIN(0XF1DB), //
    CLIPBOARD(0XF0EA), //
    CLOCK_O(0XF017), //
    CLONE(0XF24D), //
    CLOSE(0XF00D), //
    CLOUD(0XF0C2), //
    CLOUD_DOWNLOAD(0XF0ED), //
    CLOUD_UPLOAD(0XF0EE), //
    CNY(0XF157), //
    CODE(0XF121), //
    CODE_FORK(0XF126), //
    CODEPEN(0XF1CB), //
    CODIEPIE(0XF284), //
    COFFEE(0XF0F4), //
    COG(0XF013), //
    COGS(0XF085), //
    COLUMNS(0XF0DB), //
    COMMENT(0XF075), //
    COMMENT_O(0XF0E5), //
    COMMENTING(0XF27A), //
    COMMENTING_O(0XF27B), //
    COMMENTS(0XF086), //
    COMMENTS_O(0XF0E6), //
    COMPASS(0XF14E), //
    COMPRESS(0XF066), //
    CONNECTDEVELOP(0XF20E), //
    CONTAO(0XF26D), //
    COPY(0XF0C5), //
    COPYRIGHT(0XF1F9), //
    CREATIVE_COMMONS(0XF25E), //
    CREDIT_CARD(0XF09D), //
    CREDIT_CARD_ALT(0XF283), //
    CROP(0XF125), //
    CROSSHAIRS(0XF05B), //
    CSS3(0XF13C), //
    CUBE(0XF1B2), //
    CUBES(0XF1B3), //
    CUT(0XF0C4), //
    CUTLERY(0XF0F5), //
    DASHBOARD(0XF0E4), //
    DASHCUBE(0XF210), //
    DATABASE(0XF1C0), //
    DEDENT(0XF03B), //
    DELICIOUS(0XF1A5), //
    DESKTOP(0XF108), //
    DEVIANTART(0XF1BD), //
    DIAMOND(0XF219), //
    DIGG(0XF1A6), //
    DOLLAR(0XF155), //
    DOT_CIRCLE_O(0XF192), //
    DOWNLOAD(0XF019), //
    DRIBBBLE(0XF17D), //
    DROPBOX(0XF16B), //
    DRUPAL(0XF1A9), //
    EDGE(0XF282), //
    EDIT(0XF044), //
    EJECT(0XF052), //
    ELLIPSIS_H(0XF141), //
    ELLIPSIS_V(0XF142), //
    EMPIRE(0XF1D1), //
    ENVELOPE(0XF0E0), //
    ENVELOPE_O(0XF003), //
    ENVELOPE_SQUARE(0XF199), //
    ERASER(0XF12D), //
    EUR(0XF153), //
    EURO(0XF153), //
    EXCHANGE(0XF0EC), //
    EXCLAMATION(0XF12A), //
    EXCLAMATION_CIRCLE(0XF06A), //
    EXCLAMATION_TRIANGLE(0XF071), //
    EXPAND(0XF065), //
    EXPEDITEDSSL(0XF23E), //
    EXTERNAL_LINK(0XF08E), //
    EXTERNAL_LINK_SQUARE(0XF14C), //
    EYE(0XF06E), //
    EYE_SLASH(0XF070), //
    EYEDROPPER(0XF1FB), //
    FACEBOOK(0XF09A), //
    FACEBOOK_F(0XF09A), //
    FACEBOOK_OFFICIAL(0XF230), //
    FACEBOOK_SQUARE(0XF082), //
    FAST_BACKWARD(0XF049), //
    FAST_FORWARD(0XF050), //
    FAX(0XF1AC), //
    FEED(0XF09E), //
    FEMALE(0XF182), //
    FIGHTER_JET(0XF0FB), //
    FILE(0XF15B), //
    FILE_ARCHIVE_O(0XF1C6), //
    FILE_AUDIO_O(0XF1C7), //
    FILE_CODE_O(0XF1C9), //
    FILE_EXCEL_O(0XF1C3), //
    FILE_IMAGE_O(0XF1C5), //
    FILE_MOVIE_O(0XF1C8), //
    FILE_O(0XF016), //
    FILE_PDF_O(0XF1C1), //
    FILE_PHOTO_O(0XF1C5), //
    FILE_PICTURE_O(0XF1C5), //
    FILE_POWERPOINT_O(0XF1C4), //
    FILE_SOUND_O(0XF1C7), //
    FILE_TEXT(0XF15C), //
    FILE_TEXT_O(0XF0F6), //
    FILE_VIDEO_O(0XF1C8), //
    FILE_WORD_O(0XF1C2), //
    FILE_ZIP_O(0XF1C6), //
    FILES_O(0XF0C5), //
    FILM(0XF008), //
    FILTER(0XF0B0), //
    FIRE(0XF06D), //
    FIRE_EXTINGUISHER(0XF134), //
    FIREFOX(0XF269), //
    FLAG(0XF024), //
    FLAG_CHECKERED(0XF11E), //
    FLAG_O(0XF11D), //
    FLASH(0XF0E7), //
    FLASK(0XF0C3), //
    FLICKR(0XF16E), //
    FLOPPY_O(0XF0C7), //
    FOLDER(0XF07B), //
    FOLDER_O(0XF114), //
    FOLDER_OPEN(0XF07C), //
    FOLDER_OPEN_O(0XF115), //
    FONT(0XF031), //
    FONTICONS(0XF280), //
    FORT_AWESOME(0XF286), //
    FORUMBEE(0XF211), //
    FORWARD(0XF04E), //
    FOURSQUARE(0XF180), //
    FROWN_O(0XF119), //
    FUTBOL_O(0XF1E3), //
    GAMEPAD(0XF11B), //
    GAVEL(0XF0E3), //
    GBP(0XF154), //
    GE(0XF1D1), //
    GEAR(0XF013), //
    GEARS(0XF085), //
    GENDERLESS(0XF22D), //
    GET_POCKET(0XF265), //
    GG(0XF260), //
    GG_CIRCLE(0XF261), //
    GIFT(0XF06B), //
    GIT(0XF1D3), //
    GIT_SQUARE(0XF1D2), //
    GITHUB(0XF09B), //
    GITHUB_ALT(0XF113), //
    GITHUB_SQUARE(0XF092), //
    GITTIP(0XF184), //
    GLASS(0XF000), //
    GLOBE(0XF0AC), //
    GOOGLE(0XF1A0), //
    GOOGLE_PLUS(0XF0D5), //
    GOOGLE_PLUS_SQUARE(0XF0D4), //
    GOOGLE_WALLET(0XF1EE), //
    GRADUATION_CAP(0XF19D), //
    GRATIPAY(0XF184), //
    GROUP(0XF0C0), //
    H_SQUARE(0XF0FD), //
    HACKER_NEWS(0XF1D4), //
    HAND_GRAB_O(0XF255), //
    HAND_LIZARD_O(0XF258), //
    HAND_O_DOWN(0XF0A7), //
    HAND_O_LEFT(0XF0A5), //
    HAND_O_RIGHT(0XF0A4), //
    HAND_O_UP(0XF0A6), //
    HAND_PAPER_O(0XF256), //
    HAND_PEACE_O(0XF25B), //
    HAND_POINTER_O(0XF25A), //
    HAND_ROCK_O(0XF255), //
    HAND_SCISSORS_O(0XF257), //
    HAND_SPOCK_O(0XF259), //
    HAND_STOP_O(0XF256), //
    HASHTAG(0XF292), //
    HDD_O(0XF0A0), //
    HEADER(0XF1DC), //
    HEADPHONES(0XF025), //
    HEART(0XF004), //
    HEART_O(0XF08A), //
    HEARTBEAT(0XF21E), //
    HISTORY(0XF1DA), //
    HOME(0XF015), //
    HOSPITAL_O(0XF0F8), //
    HOTEL(0XF236), //
    HOURGLASS(0XF254), //
    HOURGLASS_1(0XF251), //
    HOURGLASS_2(0XF252), //
    HOURGLASS_3(0XF253), //
    HOURGLASS_END(0XF253), //
    HOURGLASS_HALF(0XF252), //
    HOURGLASS_O(0XF250), //
    HOURGLASS_START(0XF251), //
    HOUZZ(0XF27C), //
    HTML5(0XF13B), //
    I_CURSOR(0XF246), //
    ILS(0XF20B), //
    IMAGE(0XF03E), //
    INBOX(0XF01C), //
    INDENT(0XF03C), //
    INDUSTRY(0XF275), //
    INFO(0XF129), //
    INFO_CIRCLE(0XF05A), //
    INR(0XF156), //
    INSTAGRAM(0XF16D), //
    INSTITUTION(0XF19C), //
    INTERNET_EXPLORER(0XF26B), //
    INTERSEX(0XF224), //
    IOXHOST(0XF208), //
    ITALIC(0XF033), //
    JOOMLA(0XF1AA), //
    JPY(0XF157), //
    JSFIDDLE(0XF1CC), //
    KEY(0XF084), //
    KEYBOARD_O(0XF11C), //
    KRW(0XF159), //
    LANGUAGE(0XF1AB), //
    LAPTOP(0XF109), //
    LASTFM(0XF202), //
    LASTFM_SQUARE(0XF203), //
    LEAF(0XF06C), //
    LEANPUB(0XF212), //
    LEGAL(0XF0E3), //
    LEMON_O(0XF094), //
    LEVEL_DOWN(0XF149), //
    LEVEL_UP(0XF148), //
    LIFE_BOUY(0XF1CD), //
    LIFE_BUOY(0XF1CD), //
    LIFE_RING(0XF1CD), //
    LIFE_SAVER(0XF1CD), //
    LIGHTBULB_O(0XF0EB), //
    LINE_CHART(0XF201), //
    LINK(0XF0C1), //
    LINKEDIN(0XF0E1), //
    LINKEDIN_SQUARE(0XF08C), //
    LINUX(0XF17C), //
    LIST(0XF03A), //
    LIST_ALT(0XF022), //
    LIST_OL(0XF0CB), //
    LIST_UL(0XF0CA), //
    LOCATION_ARROW(0XF124), //
    LOCK(0XF023), //
    LONG_ARROW_DOWN(0XF175), //
    LONG_ARROW_LEFT(0XF177), //
    LONG_ARROW_RIGHT(0XF178), //
    LONG_ARROW_UP(0XF176), //
    MAGIC(0XF0D0), //
    MAGNET(0XF076), //
    MAIL_FORWARD(0XF064), //
    MAIL_REPLY(0XF112), //
    MAIL_REPLY_ALL(0XF122), //
    MALE(0XF183), //
    MAP(0XF279), //
    MAP_MARKER(0XF041), //
    MAP_O(0XF278), //
    MAP_PIN(0XF276), //
    MAP_SIGNS(0XF277), //
    MARS(0XF222), //
    MARS_DOUBLE(0XF227), //
    MARS_STROKE(0XF229), //
    MARS_STROKE_H(0XF22B), //
    MARS_STROKE_V(0XF22A), //
    MAXCDN(0XF136), //
    MEANPATH(0XF20C), //
    MEDIUM(0XF23A), //
    MEDKIT(0XF0FA), //
    MEH_O(0XF11A), //
    MERCURY(0XF223), //
    MICROPHONE(0XF130), //
    MICROPHONE_SLASH(0XF131), //
    MINUS(0XF068), //
    MINUS_CIRCLE(0XF056), //
    MINUS_SQUARE(0XF146), //
    MINUS_SQUARE_O(0XF147), //
    MIXCLOUD(0XF289), //
    MOBILE(0XF10B), //
    MOBILE_PHONE(0XF10B), //
    MODX(0XF285), //
    MONEY(0XF0D6), //
    MOON_O(0XF186), //
    MORTAR_BOARD(0XF19D), //
    MOTORCYCLE(0XF21C), //
    MOUSE_POINTER(0XF245), //
    MUSIC(0XF001), //
    NAVICON(0XF0C9), //
    NEUTER(0XF22C), //
    NEWSPAPER_O(0XF1EA), //
    OBJECT_GROUP(0XF247), //
    OBJECT_UNGROUP(0XF248), //
    ODNOKLASSNIKI(0XF263), //
    ODNOKLASSNIKI_SQUARE(0XF264), //
    OPENCART(0XF23D), //
    OPENID(0XF19B), //
    OPERA(0XF26A), //
    OPTIN_MONSTER(0XF23C), //
    OUTDENT(0XF03B), //
    PAGELINES(0XF18C), //
    PAINT_BRUSH(0XF1FC), //
    PAPER_PLANE(0XF1D8), //
    PAPER_PLANE_O(0XF1D9), //
    PAPERCLIP(0XF0C6), //
    PARAGRAPH(0XF1DD), //
    PASTE(0XF0EA), //
    PAUSE(0XF04C), //
    PAUSE_CIRCLE(0XF28B), //
    PAUSE_CIRCLE_O(0XF28C), //
    PAW(0XF1B0), //
    PAYPAL(0XF1ED), //
    PENCIL(0XF040), //
    PENCIL_SQUARE(0XF14B), //
    PENCIL_SQUARE_O(0XF044), //
    PERCENT(0XF295), //
    PHONE(0XF095), //
    PHONE_SQUARE(0XF098), //
    PHOTO(0XF03E), //
    PICTURE_O(0XF03E), //
    PIE_CHART(0XF200), //
    PIED_PIPER(0XF1A7), //
    PIED_PIPER_ALT(0XF1A8), //
    PINTEREST(0XF0D2), //
    PINTEREST_P(0XF231), //
    PINTEREST_SQUARE(0XF0D3), //
    PLANE(0XF072), //
    PLAY(0XF04B), //
    PLAY_CIRCLE(0XF144), //
    PLAY_CIRCLE_O(0XF01D), //
    PLUG(0XF1E6), //
    PLUS(0XF067), //
    PLUS_CIRCLE(0XF055), //
    PLUS_SQUARE(0XF0FE), //
    PLUS_SQUARE_O(0XF196), //
    POWER_OFF(0XF011), //
    PRINT(0XF02F), //
    PRODUCT_HUNT(0XF288), //
    PUZZLE_PIECE(0XF12E), //
    QQ(0XF1D6), //
    QRCODE(0XF029), //
    QUESTION(0XF128), //
    QUESTION_CIRCLE(0XF059), //
    QUOTE_LEFT(0XF10D), //
    QUOTE_RIGHT(0XF10E), //
    RA(0XF1D0), //
    RANDOM(0XF074), //
    REBEL(0XF1D0), //
    RECYCLE(0XF1B8), //
    REDDIT(0XF1A1), //
    REDDIT_ALIEN(0XF281), //
    REDDIT_SQUARE(0XF1A2), //
    REFRESH(0XF021), //
    REGISTERED(0XF25D), //
    REMOVE(0XF00D), //
    RENREN(0XF18B), //
    REORDER(0XF0C9), //
    REPEAT(0XF01E), //
    REPLY(0XF112), //
    REPLY_ALL(0XF122), //
    RETWEET(0XF079), //
    RMB(0XF157), //
    ROAD(0XF018), //
    ROCKET(0XF135), //
    ROTATE_LEFT(0XF0E2), //
    ROTATE_RIGHT(0XF01E), //
    ROUBLE(0XF158), //
    RSS(0XF09E), //
    RSS_SQUARE(0XF143), //
    RUB(0XF158), //
    RUBLE(0XF158), //
    RUPEE(0XF156), //
    SAFARI(0XF267), //
    SAVE(0XF0C7), //
    SCISSORS(0XF0C4), //
    SCRIBD(0XF28A), //
    SEARCH(0XF002), //
    SEARCH_MINUS(0XF010), //
    SEARCH_PLUS(0XF00E), //
    SELLSY(0XF213), //
    SEND(0XF1D8), //
    SEND_O(0XF1D9), //
    SERVER(0XF233), //
    SHARE(0XF064), //
    SHARE_ALT(0XF1E0), //
    SHARE_ALT_SQUARE(0XF1E1), //
    SHARE_SQUARE(0XF14D), //
    SHARE_SQUARE_O(0XF045), //
    SHEKEL(0XF20B), //
    SHEQEL(0XF20B), //
    SHIELD(0XF132), //
    SHIP(0XF21A), //
    SHIRTSINBULK(0XF214), //
    SHOPPING_BAG(0XF290), //
    SHOPPING_BASKET(0XF291), //
    SHOPPING_CART(0XF07A), //
    SIGN_IN(0XF090), //
    SIGN_OUT(0XF08B), //
    SIGNAL(0XF012), //
    SIMPLYBUILT(0XF215), //
    SITEMAP(0XF0E8), //
    SKYATLAS(0XF216), //
    SKYPE(0XF17E), //
    SLACK(0XF198), //
    SLIDERS(0XF1DE), //
    SLIDESHARE(0XF1E7), //
    SMILE_O(0XF118), //
    SOCCER_BALL_O(0XF1E3), //
    SORT(0XF0DC), //
    SORT_ALPHA_ASC(0XF15D), //
    SORT_ALPHA_DESC(0XF15E), //
    SORT_AMOUNT_ASC(0XF160), //
    SORT_AMOUNT_DESC(0XF161), //
    SORT_ASC(0XF0DE), //
    SORT_DESC(0XF0DD), //
    SORT_DOWN(0XF0DD), //
    SORT_NUMERIC_ASC(0XF162), //
    SORT_NUMERIC_DESC(0XF163), //
    SORT_UP(0XF0DE), //
    SOUNDCLOUD(0XF1BE), //
    SPACE_SHUTTLE(0XF197), //
    SPINNER(0XF110), //
    SPOON(0XF1B1), //
    SPOTIFY(0XF1BC), //
    SQUARE(0XF0C8), //
    SQUARE_O(0XF096), //
    STACK_EXCHANGE(0XF18D), //
    STACK_OVERFLOW(0XF16C), //
    STAR(0XF005), //
    STAR_HALF(0XF089), //
    STAR_HALF_EMPTY(0XF123), //
    STAR_HALF_FULL(0XF123), //
    STAR_HALF_O(0XF123), //
    STAR_O(0XF006), //
    STEAM(0XF1B6), //
    STEAM_SQUARE(0XF1B7), //
    STEP_BACKWARD(0XF048), //
    STEP_FORWARD(0XF051), //
    STETHOSCOPE(0XF0F1), //
    STICKY_NOTE(0XF249), //
    STICKY_NOTE_O(0XF24A), //
    STOP(0XF04D), //
    STOP_CIRCLE(0XF28D), //
    STOP_CIRCLE_O(0XF28E), //
    STREET_VIEW(0XF21D), //
    STRIKETHROUGH(0XF0CC), //
    STUMBLEUPON(0XF1A4), //
    STUMBLEUPON_CIRCLE(0XF1A3), //
    SUBSCRIPT(0XF12C), //
    SUBWAY(0XF239), //
    SUITCASE(0XF0F2), //
    SUN_O(0XF185), //
    SUPERSCRIPT(0XF12B), //
    SUPPORT(0XF1CD), //
    TABLE(0XF0CE), //
    TABLET(0XF10A), //
    TACHOMETER(0XF0E4), //
    TAG(0XF02B), //
    TAGS(0XF02C), //
    TASKS(0XF0AE), //
    TAXI(0XF1BA), //
    TELEVISION(0XF26C), //
    TENCENT_WEIBO(0XF1D5), //
    TERMINAL(0XF120), //
    TEXT_HEIGHT(0XF034), //
    TEXT_WIDTH(0XF035), //
    TH(0XF00A), //
    TH_LARGE(0XF009), //
    TH_LIST(0XF00B), //
    THUMB_TACK(0XF08D), //
    THUMBS_DOWN(0XF165), //
    THUMBS_O_DOWN(0XF088), //
    THUMBS_O_UP(0XF087), //
    THUMBS_UP(0XF164), //
    TICKET(0XF145), //
    TIMES(0XF00D), //
    TIMES_CIRCLE(0XF057), //
    TIMES_CIRCLE_O(0XF05C), //
    TINT(0XF043), //
    TOGGLE_DOWN(0XF150), //
    TOGGLE_LEFT(0XF191), //
    TOGGLE_OFF(0XF204), //
    TOGGLE_ON(0XF205), //
    TOGGLE_RIGHT(0XF152), //
    TOGGLE_UP(0XF151), //
    TRADEMARK(0XF25C), //
    TRAIN(0XF238), //
    TRANSGENDER(0XF224), //
    TRANSGENDER_ALT(0XF225), //
    TRASH(0XF1F8), //
    TRASH_O(0XF014), //
    TREE(0XF1BB), //
    TRELLO(0XF181), //
    TRIPADVISOR(0XF262), //
    TROPHY(0XF091), //
    TRUCK(0XF0D1), //
    TRY(0XF195), //
    TTY(0XF1E4), //
    TUMBLR(0XF173), //
    TUMBLR_SQUARE(0XF174), //
    TURKISH_LIRA(0XF195), //
    TV(0XF26C), //
    TWITCH(0XF1E8), //
    TWITTER(0XF099), //
    TWITTER_SQUARE(0XF081), //
    UMBRELLA(0XF0E9), //
    UNDERLINE(0XF0CD), //
    UNDO(0XF0E2), //
    UNIVERSITY(0XF19C), //
    UNLINK(0XF127), //
    UNLOCK(0XF09C), //
    UNLOCK_ALT(0XF13E), //
    UNSORTED(0XF0DC), //
    UPLOAD(0XF093), //
    USB(0XF287), //
    USD(0XF155), //
    USER(0XF007), //
    USER_MD(0XF0F0), //
    USER_PLUS(0XF234), //
    USER_SECRET(0XF21B), //
    USER_TIMES(0XF235), //
    USERS(0XF0C0), //
    VENUS(0XF221), //
    VENUS_DOUBLE(0XF226), //
    VENUS_MARS(0XF228), //
    VIACOIN(0XF237), //
    VIDEO_CAMERA(0XF03D), //
    VIMEO(0XF27D), //
    VIMEO_SQUARE(0XF194), //
    VINE(0XF1CA), //
    VK(0XF189), //
    VOLUME_DOWN(0XF027), //
    VOLUME_OFF(0XF026), //
    VOLUME_UP(0XF028), //
    WARNING(0XF071), //
    WECHAT(0XF1D7), //
    WEIBO(0XF18A), //
    WEIXIN(0XF1D7), //
    WHATSAPP(0XF232), //
    WHEELCHAIR(0XF193), //
    WIFI(0XF1EB), //
    WIKIPEDIA_W(0XF266), //
    WINDOWS(0XF17A), //
    WON(0XF159), //
    WORDPRESS(0XF19A), //
    WRENCH(0XF0AD), //
    XING(0XF168), //
    XING_SQUARE(0XF169), //
    Y_COMBINATOR(0XF23B), //
    Y_COMBINATOR_SQUARE(0XF1D4), //
    YAHOO(0XF19E), //
    YC(0XF23B), //
    YC_SQUARE(0XF1D4), //
    YELP(0XF1E9), //
    YEN(0XF157), //
    YOUTUBE(0XF167), //
    YOUTUBE_PLAY(0XF16A), //
    YOUTUBE_SQUARE(0XF166), //
    ;

    public static final String FONT_FAMILY = "FontAwesome";
    private final int codepoint;

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
        return FontAwesome.FONT_FAMILY;
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
        return GenericFontIcon.getHtml(FontAwesome.FONT_FAMILY, codepoint);
    }

    /**
     * Finds an instance of FontAwesome with given codepoint
     *
     * @since 7.5.0
     * @param codepoint
     * @return FontAwesome instance with a specific codepoint or null if there
     *         isn't any
     */
    public static FontAwesome fromCodepoint(final int codepoint) {
        for (FontAwesome f : values()) {
            if (f.getCodepoint() == codepoint) {
                return f;
            }
        }
        throw new IllegalArgumentException(
                "Codepoint " + codepoint + " not found in FontAwesome");
    }

}
