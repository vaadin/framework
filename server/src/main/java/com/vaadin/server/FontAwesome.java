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
 * @see http://fontawesome.io
 * @deprecated Since 8.0 replaced with included Vaadin Icons
 *             https://vaadin.com/icons (#7979). Will not be updated to include
 *             new icons.
 */
@Deprecated
public enum FontAwesome implements FontIcon {
    /** The <a href="http://fontawesome.io/icon/500px/">500px</a> icon. */
    _500PX(0XF26E),
    /** The <a href="http://fontawesome.io/icon/adjust/">adjust</a> icon. */
    ADJUST(0XF042),
    /** The <a href="http://fontawesome.io/icon/adn/">adn</a> icon. */
    ADN(0XF170),
    /**
     * The <a href="http://fontawesome.io/icon/align-center/">align-center</a>
     * icon.
     */
    ALIGN_CENTER(0XF037),
    /**
     * The <a href="http://fontawesome.io/icon/align-justify/">align-justify</a>
     * icon.
     */
    ALIGN_JUSTIFY(0XF039),
    /**
     * The <a href="http://fontawesome.io/icon/align-left/">align-left</a> icon.
     */
    ALIGN_LEFT(0XF036),
    /**
     * The <a href="http://fontawesome.io/icon/align-right/">align-right</a>
     * icon.
     */
    ALIGN_RIGHT(0XF038),
    /** The <a href="http://fontawesome.io/icon/amazon/">amazon</a> icon. */
    AMAZON(0XF270),
    /**
     * The <a href="http://fontawesome.io/icon/ambulance/">ambulance</a> icon.
     */
    AMBULANCE(0XF0F9),
    /** The <a href="http://fontawesome.io/icon/anchor/">anchor</a> icon. */
    ANCHOR(0XF13D),
    /** The <a href="http://fontawesome.io/icon/android/">android</a> icon. */
    ANDROID(0XF17B),
    /**
     * The <a href="http://fontawesome.io/icon/angellist/">angellist</a> icon.
     */
    ANGELLIST(0XF209),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/angle-double-down/">angle-double-down</a>
     * icon.
     */
    ANGLE_DOUBLE_DOWN(0XF103),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/angle-double-left/">angle-double-left</a>
     * icon.
     */
    ANGLE_DOUBLE_LEFT(0XF100),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/angle-double-right/">angle-double-right</a>
     * icon.
     */
    ANGLE_DOUBLE_RIGHT(0XF101),
    /**
     * The
     * <a href="http://fontawesome.io/icon/angle-double-up/">angle-double-up</a>
     * icon.
     */
    ANGLE_DOUBLE_UP(0XF102),
    /**
     * The <a href="http://fontawesome.io/icon/angle-down/">angle-down</a> icon.
     */
    ANGLE_DOWN(0XF107),
    /**
     * The <a href="http://fontawesome.io/icon/angle-left/">angle-left</a> icon.
     */
    ANGLE_LEFT(0XF104),
    /**
     * The <a href="http://fontawesome.io/icon/angle-right/">angle-right</a>
     * icon.
     */
    ANGLE_RIGHT(0XF105),
    /** The <a href="http://fontawesome.io/icon/angle-up/">angle-up</a> icon. */
    ANGLE_UP(0XF106),
    /** The <a href="http://fontawesome.io/icon/apple/">apple</a> icon. */
    APPLE(0XF179),
    /** The <a href="http://fontawesome.io/icon/archive/">archive</a> icon. */
    ARCHIVE(0XF187),
    /**
     * The <a href="http://fontawesome.io/icon/area-chart/">area-chart</a> icon.
     */
    AREA_CHART(0XF1FE),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-down/">arrow-circle-down</a>
     * icon.
     */
    ARROW_CIRCLE_DOWN(0XF0AB),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-left/">arrow-circle-left</a>
     * icon.
     */
    ARROW_CIRCLE_LEFT(0XF0A8),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-o-down/">arrow-circle-o-down</a>
     * icon.
     */
    ARROW_CIRCLE_O_DOWN(0XF01A),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-o-left/">arrow-circle-o-left</a>
     * icon.
     */
    ARROW_CIRCLE_O_LEFT(0XF190),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-o-right/">arrow-circle-o-right</a>
     * icon.
     */
    ARROW_CIRCLE_O_RIGHT(0XF18E),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-o-up/">arrow-circle-o-up</a>
     * icon.
     */
    ARROW_CIRCLE_O_UP(0XF01B),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/arrow-circle-right/">arrow-circle-right</a>
     * icon.
     */
    ARROW_CIRCLE_RIGHT(0XF0A9),
    /**
     * The
     * <a href="http://fontawesome.io/icon/arrow-circle-up/">arrow-circle-up</a>
     * icon.
     */
    ARROW_CIRCLE_UP(0XF0AA),
    /**
     * The <a href="http://fontawesome.io/icon/arrow-down/">arrow-down</a> icon.
     */
    ARROW_DOWN(0XF063),
    /**
     * The <a href="http://fontawesome.io/icon/arrow-left/">arrow-left</a> icon.
     */
    ARROW_LEFT(0XF060),
    /**
     * The <a href="http://fontawesome.io/icon/arrow-right/">arrow-right</a>
     * icon.
     */
    ARROW_RIGHT(0XF061),
    /** The <a href="http://fontawesome.io/icon/arrow-up/">arrow-up</a> icon. */
    ARROW_UP(0XF062),
    /** The <a href="http://fontawesome.io/icon/arrows/">arrows</a> icon. */
    ARROWS(0XF047),
    /**
     * The <a href="http://fontawesome.io/icon/arrows-alt/">arrows-alt</a> icon.
     */
    ARROWS_ALT(0XF0B2),
    /** The <a href="http://fontawesome.io/icon/arrows-h/">arrows-h</a> icon. */
    ARROWS_H(0XF07E),
    /** The <a href="http://fontawesome.io/icon/arrows-v/">arrows-v</a> icon. */
    ARROWS_V(0XF07D),
    /** The <a href="http://fontawesome.io/icon/asterisk/">asterisk</a> icon. */
    ASTERISK(0XF069),
    /** The <a href="http://fontawesome.io/icon/at/">at</a> icon. */
    AT(0XF1FA),
    /**
     * The <a href="http://fontawesome.io/icon/car/">automobile (alias)</a>
     * icon.
     */
    AUTOMOBILE(0XF1B9),
    /** The <a href="http://fontawesome.io/icon/backward/">backward</a> icon. */
    BACKWARD(0XF04A),
    /**
     * The <a href="http://fontawesome.io/icon/balance-scale/">balance-scale</a>
     * icon.
     */
    BALANCE_SCALE(0XF24E),
    /** The <a href="http://fontawesome.io/icon/ban/">ban</a> icon. */
    BAN(0XF05E),
    /**
     * The <a href="http://fontawesome.io/icon/university/">bank (alias)</a>
     * icon.
     */
    BANK(0XF19C),
    /**
     * The <a href="http://fontawesome.io/icon/bar-chart/">bar-chart</a> icon.
     */
    BAR_CHART(0XF080),
    /**
     * The <a href="http://fontawesome.io/icon/bar-chart/">bar-chart-o
     * (alias)</a> icon.
     */
    BAR_CHART_O(0XF080),
    /** The <a href="http://fontawesome.io/icon/barcode/">barcode</a> icon. */
    BARCODE(0XF02A),
    /** The <a href="http://fontawesome.io/icon/bars/">bars</a> icon. */
    BARS(0XF0C9),
    /**
     * The <a href="http://fontawesome.io/icon/battery-empty/">battery-0
     * (alias)</a> icon.
     */
    BATTERY_0(0XF244),
    /**
     * The <a href="http://fontawesome.io/icon/battery-quarter/">battery-1
     * (alias)</a> icon.
     */
    BATTERY_1(0XF243),
    /**
     * The <a href="http://fontawesome.io/icon/battery-half/">battery-2
     * (alias)</a> icon.
     */
    BATTERY_2(0XF242),
    /**
     * The
     * <a href= "http://fontawesome.io/icon/battery-three-quarters/">battery-3
     * (alias)</a> icon.
     */
    BATTERY_3(0XF241),
    /**
     * The <a href="http://fontawesome.io/icon/battery-full/">battery-4
     * (alias</a> icon.
     */
    BATTERY_4(0XF240),
    /**
     * The <a href="http://fontawesome.io/icon/battery-empty/">battery-empty</a>
     * icon.
     */
    BATTERY_EMPTY(0XF244),
    /**
     * The <a href="http://fontawesome.io/icon/battery-full/">battery-full</a>
     * icon.
     */
    BATTERY_FULL(0XF240),
    /**
     * The <a href="http://fontawesome.io/icon/battery-half/">battery-half</a>
     * icon.
     */
    BATTERY_HALF(0XF242),
    /**
     * The
     * <a href="http://fontawesome.io/icon/battery-quarter/">battery-quarter</a>
     * icon.
     */
    BATTERY_QUARTER(0XF243),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/battery-three-quarters/">battery-three-quarters</a>
     * icon.
     */
    BATTERY_THREE_QUARTERS(0XF241),
    /** The <a href="http://fontawesome.io/icon/bed/">bed</a> icon. */
    BED(0XF236),
    /** The <a href="http://fontawesome.io/icon/beer/">beer</a> icon. */
    BEER(0XF0FC),
    /** The <a href="http://fontawesome.io/icon/behance/">behance</a> icon. */
    BEHANCE(0XF1B4),
    /**
     * The
     * <a href="http://fontawesome.io/icon/behance-square/">behance-square</a>
     * icon.
     */
    BEHANCE_SQUARE(0XF1B5),
    /** The <a href="http://fontawesome.io/icon/bell/">bell</a> icon. */
    BELL(0XF0F3),
    /** The <a href="http://fontawesome.io/icon/bell-o/">bell-o</a> icon. */
    BELL_O(0XF0A2),
    /**
     * The <a href="http://fontawesome.io/icon/bell-slash/">bell-slash</a> icon.
     */
    BELL_SLASH(0XF1F6),
    /**
     * The <a href="http://fontawesome.io/icon/bell-slash-o/">bell-slash-o</a>
     * icon.
     */
    BELL_SLASH_O(0XF1F7),
    /** The <a href="http://fontawesome.io/icon/bicycle/">bicycle</a> icon. */
    BICYCLE(0XF206),
    /**
     * The <a href="http://fontawesome.io/icon/binoculars/">binoculars</a> icon.
     */
    BINOCULARS(0XF1E5),
    /**
     * The <a href="http://fontawesome.io/icon/birthday-cake/">birthday-cake</a>
     * icon.
     */
    BIRTHDAY_CAKE(0XF1FD),
    /**
     * The <a href="http://fontawesome.io/icon/bitbucket/">bitbucket</a> icon.
     */
    BITBUCKET(0XF171),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/bitbucket-square/">bitbucket-square</a> icon.
     */
    BITBUCKET_SQUARE(0XF172),
    /**
     * The <a href="http://fontawesome.io/icon/btc/">bitcoin (alias)</a> icon.
     */
    BITCOIN(0XF15A),
    /**
     * The <a href="http://fontawesome.io/icon/black-tie/">black-tie</a> icon.
     */
    BLACK_TIE(0XF27E),
    /**
     * The <a href="http://fontawesome.io/icon/bluetooth/">bluetooth</a> icon.
     */
    BLUETOOTH(0XF293),
    /**
     * The <a href="http://fontawesome.io/icon/bluetooth-b/">bluetooth-b</a>
     * icon.
     */
    BLUETOOTH_B(0XF294),
    /** The <a href="http://fontawesome.io/icon/bold/">bold</a> icon. */
    BOLD(0XF032),
    /** The <a href="http://fontawesome.io/icon/bolt/">bolt</a> icon. */
    BOLT(0XF0E7),
    /** The <a href="http://fontawesome.io/icon/bomb/">bomb</a> icon. */
    BOMB(0XF1E2),
    /** The <a href="http://fontawesome.io/icon/book/">book</a> icon. */
    BOOK(0XF02D),
    /** The <a href="http://fontawesome.io/icon/bookmark/">bookmark</a> icon. */
    BOOKMARK(0XF02E),
    /**
     * The <a href="http://fontawesome.io/icon/bookmark-o/">bookmark-o</a> icon.
     */
    BOOKMARK_O(0XF097),
    /**
     * The <a href="http://fontawesome.io/icon/briefcase/">briefcase</a> icon.
     */
    BRIEFCASE(0XF0B1),
    /** The <a href="http://fontawesome.io/icon/btc/">btc</a> icon. */
    BTC(0XF15A),
    /** The <a href="http://fontawesome.io/icon/bug/">bug</a> icon. */
    BUG(0XF188),
    /** The <a href="http://fontawesome.io/icon/building/">building</a> icon. */
    BUILDING(0XF1AD),
    /**
     * The <a href="http://fontawesome.io/icon/building-o/">building-o</a> icon.
     */
    BUILDING_O(0XF0F7),
    /** The <a href="http://fontawesome.io/icon/bullhorn/">bullhorn</a> icon. */
    BULLHORN(0XF0A1),
    /** The <a href="http://fontawesome.io/icon/bullseye/">bullseye</a> icon. */
    BULLSEYE(0XF140),
    /** The <a href="http://fontawesome.io/icon/bus/">bus</a> icon. */
    BUS(0XF207),
    /**
     * The <a href="http://fontawesome.io/icon/buysellads/">buysellads</a> icon.
     */
    BUYSELLADS(0XF20D),
    /** The <a href="http://fontawesome.io/icon/taxi/">cab (alias)</a> icon. */
    CAB(0XF1BA),
    /**
     * The <a href="http://fontawesome.io/icon/calculator/">calculator</a> icon.
     */
    CALCULATOR(0XF1EC),
    /** The <a href="http://fontawesome.io/icon/calendar/">calendar</a> icon. */
    CALENDAR(0XF073),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/calendar-check-o/">calendar-check-o</a> icon.
     */
    CALENDAR_CHECK_O(0XF274),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/calendar-minus-o/">calendar-minus-o</a> icon.
     */
    CALENDAR_MINUS_O(0XF272),
    /**
     * The <a href="http://fontawesome.io/icon/calendar-o/">calendar-o</a> icon.
     */
    CALENDAR_O(0XF133),
    /**
     * The
     * <a href="http://fontawesome.io/icon/calendar-plus-o/">calendar-plus-o</a>
     * icon.
     */
    CALENDAR_PLUS_O(0XF271),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/calendar-times-o/">calendar-times-o</a> icon.
     */
    CALENDAR_TIMES_O(0XF273),
    /** The <a href="http://fontawesome.io/icon/camera/">camera</a> icon. */
    CAMERA(0XF030),
    /**
     * The <a href="http://fontawesome.io/icon/camera-retro/">camera-retro</a>
     * icon.
     */
    CAMERA_RETRO(0XF083),
    /** The <a href="http://fontawesome.io/icon/car/">car</a> icon. */
    CAR(0XF1B9),
    /**
     * The <a href="http://fontawesome.io/icon/caret-down/">caret-down</a> icon.
     */
    CARET_DOWN(0XF0D7),
    /**
     * The <a href="http://fontawesome.io/icon/caret-left/">caret-left</a> icon.
     */
    CARET_LEFT(0XF0D9),
    /**
     * The <a href="http://fontawesome.io/icon/caret-right/">caret-right</a>
     * icon.
     */
    CARET_RIGHT(0XF0DA),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/caret-square-o-down/">caret-square-o-down</a>
     * icon.
     */
    CARET_SQUARE_O_DOWN(0XF150),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/caret-square-o-left/">caret-square-o-left</a>
     * icon.
     */
    CARET_SQUARE_O_LEFT(0XF191),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/caret-square-o-right/">caret-square-o-right</a>
     * icon.
     */
    CARET_SQUARE_O_RIGHT(0XF152),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/caret-square-o-up/">caret-square-o-up</a>
     * icon.
     */
    CARET_SQUARE_O_UP(0XF151),
    /** The <a href="http://fontawesome.io/icon/caret-up/">caret-up</a> icon. */
    CARET_UP(0XF0D8),
    /**
     * The
     * <a href="http://fontawesome.io/icon/cart-arrow-down/">cart-arrow-down</a>
     * icon.
     */
    CART_ARROW_DOWN(0XF218),
    /**
     * The <a href="http://fontawesome.io/icon/cart-plus/">cart-plus</a> icon.
     */
    CART_PLUS(0XF217),
    /** The <a href="http://fontawesome.io/icon/cc/">cc</a> icon. */
    CC(0XF20A),
    /** The <a href="http://fontawesome.io/icon/cc-amex/">cc-amex</a> icon. */
    CC_AMEX(0XF1F3),
    /**
     * The
     * <a href="http://fontawesome.io/icon/cc-diners-club/">cc-diners-club</a>
     * icon.
     */
    CC_DINERS_CLUB(0XF24C),
    /**
     * The <a href="http://fontawesome.io/icon/cc-discover/">cc-discover</a>
     * icon.
     */
    CC_DISCOVER(0XF1F2),
    /** The <a href="http://fontawesome.io/icon/cc-jcb/">cc-jcb</a> icon. */
    CC_JCB(0XF24B),
    /**
     * The <a href="http://fontawesome.io/icon/cc-mastercard/">cc-mastercard</a>
     * icon.
     */
    CC_MASTERCARD(0XF1F1),
    /**
     * The <a href="http://fontawesome.io/icon/cc-paypal/">cc-paypal</a> icon.
     */
    CC_PAYPAL(0XF1F4),
    /**
     * The <a href="http://fontawesome.io/icon/cc-stripe/">cc-stripe</a> icon.
     */
    CC_STRIPE(0XF1F5),
    /** The <a href="http://fontawesome.io/icon/cc-visa/">cc-visa</a> icon. */
    CC_VISA(0XF1F0),
    /**
     * The <a href="http://fontawesome.io/icon/certificate/">certificate</a>
     * icon.
     */
    CERTIFICATE(0XF0A3),
    /**
     * The <a href="http://fontawesome.io/icon/link/">chain (alias)</a> icon.
     */
    CHAIN(0XF0C1),
    /**
     * The <a href="http://fontawesome.io/icon/chain-broken/">chain-broken</a>
     * icon.
     */
    CHAIN_BROKEN(0XF127),
    /** The <a href="http://fontawesome.io/icon/check/">check</a> icon. */
    CHECK(0XF00C),
    /**
     * The <a href="http://fontawesome.io/icon/check-circle/">check-circle</a>
     * icon.
     */
    CHECK_CIRCLE(0XF058),
    /**
     * The
     * <a href="http://fontawesome.io/icon/check-circle-o/">check-circle-o</a>
     * icon.
     */
    CHECK_CIRCLE_O(0XF05D),
    /**
     * The <a href="http://fontawesome.io/icon/check-square/">check-square</a>
     * icon.
     */
    CHECK_SQUARE(0XF14A),
    /**
     * The
     * <a href="http://fontawesome.io/icon/check-square-o/">check-square-o</a>
     * icon.
     */
    CHECK_SQUARE_O(0XF046),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/chevron-circle-down/">chevron-circle-down</a>
     * icon.
     */
    CHEVRON_CIRCLE_DOWN(0XF13A),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/chevron-circle-left/">chevron-circle-left</a>
     * icon.
     */
    CHEVRON_CIRCLE_LEFT(0XF137),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/chevron-circle-right/">chevron-circle-right</a>
     * icon.
     */
    CHEVRON_CIRCLE_RIGHT(0XF138),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/chevron-circle-up/">chevron-circle-up</a>
     * icon.
     */
    CHEVRON_CIRCLE_UP(0XF139),
    /**
     * The <a href="http://fontawesome.io/icon/chevron-down/">chevron-down</a>
     * icon.
     */
    CHEVRON_DOWN(0XF078),
    /**
     * The <a href="http://fontawesome.io/icon/chevron-left/">chevron-left</a>
     * icon.
     */
    CHEVRON_LEFT(0XF053),
    /**
     * The <a href="http://fontawesome.io/icon/chevron-right/">chevron-right</a>
     * icon.
     */
    CHEVRON_RIGHT(0XF054),
    /**
     * The <a href="http://fontawesome.io/icon/chevron-up/">chevron-up</a> icon.
     */
    CHEVRON_UP(0XF077),
    /** The <a href="http://fontawesome.io/icon/child/">child</a> icon. */
    CHILD(0XF1AE),
    /** The <a href="http://fontawesome.io/icon/chrome/">chrome</a> icon. */
    CHROME(0XF268),
    /** The <a href="http://fontawesome.io/icon/circle/">circle</a> icon. */
    CIRCLE(0XF111),
    /** The <a href="http://fontawesome.io/icon/circle-o/">circle-o</a> icon. */
    CIRCLE_O(0XF10C),
    /**
     * The
     * <a href="http://fontawesome.io/icon/circle-o-notch/">circle-o-notch</a>
     * icon.
     */
    CIRCLE_O_NOTCH(0XF1CE),
    /**
     * The <a href="http://fontawesome.io/icon/circle-thin/">circle-thin</a>
     * icon.
     */
    CIRCLE_THIN(0XF1DB),
    /**
     * The <a href="http://fontawesome.io/icon/clipboard/">clipboard</a> icon.
     */
    CLIPBOARD(0XF0EA),
    /** The <a href="http://fontawesome.io/icon/clock-o/">clock-o</a> icon. */
    CLOCK_O(0XF017),
    /** The <a href="http://fontawesome.io/icon/clone/">clone</a> icon. */
    CLONE(0XF24D),
    /**
     * The <a href="http://fontawesome.io/icon/times/">close (alias)</a> icon.
     */
    CLOSE(0XF00D),
    /** The <a href="http://fontawesome.io/icon/cloud/">cloud</a> icon. */
    CLOUD(0XF0C2),
    /**
     * The
     * <a href="http://fontawesome.io/icon/cloud-download/">cloud-download</a>
     * icon.
     */
    CLOUD_DOWNLOAD(0XF0ED),
    /**
     * The <a href="http://fontawesome.io/icon/cloud-upload/">cloud-upload</a>
     * icon.
     */
    CLOUD_UPLOAD(0XF0EE),
    /** The <a href="http://fontawesome.io/icon/jpy/">cny (alias)</a> icon. */
    CNY(0XF157),
    /** The <a href="http://fontawesome.io/icon/code/">code</a> icon. */
    CODE(0XF121),
    /**
     * The <a href="http://fontawesome.io/icon/code-fork/">code-fork</a> icon.
     */
    CODE_FORK(0XF126),
    /** The <a href="http://fontawesome.io/icon/codepen/">codepen</a> icon. */
    CODEPEN(0XF1CB),
    /** The <a href="http://fontawesome.io/icon/codiepie/">codiepie</a> icon. */
    CODIEPIE(0XF284),
    /** The <a href="http://fontawesome.io/icon/coffee/">coffee</a> icon. */
    COFFEE(0XF0F4),
    /** The <a href="http://fontawesome.io/icon/cog/">cog</a> icon. */
    COG(0XF013),
    /** The <a href="http://fontawesome.io/icon/cogs/">cogs</a> icon. */
    COGS(0XF085),
    /** The <a href="http://fontawesome.io/icon/columns/">columns</a> icon. */
    COLUMNS(0XF0DB),
    /** The <a href="http://fontawesome.io/icon/comment/">comment</a> icon. */
    COMMENT(0XF075),
    /**
     * The <a href="http://fontawesome.io/icon/comment-o/">comment-o</a> icon.
     */
    COMMENT_O(0XF0E5),
    /**
     * The <a href="http://fontawesome.io/icon/commenting/">commenting</a> icon.
     */
    COMMENTING(0XF27A),
    /**
     * The <a href="http://fontawesome.io/icon/commenting-o/">commenting-o</a>
     * icon.
     */
    COMMENTING_O(0XF27B),
    /** The <a href="http://fontawesome.io/icon/comments/">comments</a> icon. */
    COMMENTS(0XF086),
    /**
     * The <a href="http://fontawesome.io/icon/comments-o/">comments-o</a> icon.
     */
    COMMENTS_O(0XF0E6),
    /** The <a href="http://fontawesome.io/icon/compass/">compass</a> icon. */
    COMPASS(0XF14E),
    /** The <a href="http://fontawesome.io/icon/compress/">compress</a> icon. */
    COMPRESS(0XF066),
    /**
     * The
     * <a href="http://fontawesome.io/icon/connectdevelop/">connectdevelop</a>
     * icon.
     */
    CONNECTDEVELOP(0XF20E),
    /** The <a href="http://fontawesome.io/icon/contao/">contao</a> icon. */
    CONTAO(0XF26D),
    /**
     * The <a href="http://fontawesome.io/icon/files-o/">copy (alias)</a> icon.
     */
    COPY(0XF0C5),
    /**
     * The <a href="http://fontawesome.io/icon/copyright/">copyright</a> icon.
     */
    COPYRIGHT(0XF1F9),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/creative-commons/">creative-commons</a> icon.
     */
    CREATIVE_COMMONS(0XF25E),
    /**
     * The <a href="http://fontawesome.io/icon/credit-card/">credit-card</a>
     * icon.
     */
    CREDIT_CARD(0XF09D),
    /**
     * The
     * <a href="http://fontawesome.io/icon/credit-card-alt/">credit-card-alt</a>
     * icon.
     */
    CREDIT_CARD_ALT(0XF283),
    /** The <a href="http://fontawesome.io/icon/crop/">crop</a> icon. */
    CROP(0XF125),
    /**
     * The <a href="http://fontawesome.io/icon/crosshairs/">crosshairs</a> icon.
     */
    CROSSHAIRS(0XF05B),
    /** The <a href="http://fontawesome.io/icon/css3/">css3</a> icon. */
    CSS3(0XF13C),
    /** The <a href="http://fontawesome.io/icon/cube/">cube</a> icon. */
    CUBE(0XF1B2),
    /** The <a href="http://fontawesome.io/icon/cubes/">cubes</a> icon. */
    CUBES(0XF1B3),
    /**
     * The <a href="http://fontawesome.io/icon/scissors/">cut (alias)</a> icon.
     */
    CUT(0XF0C4),
    /** The <a href="http://fontawesome.io/icon/cutlery/">cutlery</a> icon. */
    CUTLERY(0XF0F5),
    /**
     * The <a href="http://fontawesome.io/icon/tachometer/">dashboard
     * (alias)</a> icon.
     */
    DASHBOARD(0XF0E4),
    /** The <a href="http://fontawesome.io/icon/dashcube/">dashcube</a> icon. */
    DASHCUBE(0XF210),
    /** The <a href="http://fontawesome.io/icon/database/">database</a> icon. */
    DATABASE(0XF1C0),
    /**
     * The <a href="http://fontawesome.io/icon/outdent/">dedent (alias)</a>
     * icon.
     */
    DEDENT(0XF03B),
    /**
     * The <a href="http://fontawesome.io/icon/delicious/">delicious</a> icon.
     */
    DELICIOUS(0XF1A5),
    /** The <a href="http://fontawesome.io/icon/desktop/">desktop</a> icon. */
    DESKTOP(0XF108),
    /**
     * The <a href="http://fontawesome.io/icon/deviantart/">deviantart</a> icon.
     */
    DEVIANTART(0XF1BD),
    /** The <a href="http://fontawesome.io/icon/diamond/">diamond</a> icon. */
    DIAMOND(0XF219),
    /** The <a href="http://fontawesome.io/icon/digg/">digg</a> icon. */
    DIGG(0XF1A6),
    /**
     * The <a href="http://fontawesome.io/icon/usd/">dollar (alias)</a> icon.
     */
    DOLLAR(0XF155),
    /**
     * The <a href="http://fontawesome.io/icon/dot-circle-o/">dot-circle-o</a>
     * icon.
     */
    DOT_CIRCLE_O(0XF192),
    /** The <a href="http://fontawesome.io/icon/download/">download</a> icon. */
    DOWNLOAD(0XF019),
    /** The <a href="http://fontawesome.io/icon/dribbble/">dribbble</a> icon. */
    DRIBBBLE(0XF17D),
    /** The <a href="http://fontawesome.io/icon/dropbox/">dropbox</a> icon. */
    DROPBOX(0XF16B),
    /** The <a href="http://fontawesome.io/icon/drupal/">drupal</a> icon. */
    DRUPAL(0XF1A9),
    /** The <a href="http://fontawesome.io/icon/edge/">edge</a> icon. */
    EDGE(0XF282),
    /**
     * The <a href="http://fontawesome.io/icon/pencil-square-o/">edit
     * (alias)</a> icon.
     */
    EDIT(0XF044),
    /** The <a href="http://fontawesome.io/icon/eject/">eject</a> icon. */
    EJECT(0XF052),
    /**
     * The <a href="http://fontawesome.io/icon/ellipsis-h/">ellipsis-h</a> icon.
     */
    ELLIPSIS_H(0XF141),
    /**
     * The <a href="http://fontawesome.io/icon/ellipsis-v/">ellipsis-v</a> icon.
     */
    ELLIPSIS_V(0XF142),
    /** The <a href="http://fontawesome.io/icon/empire/">empire</a> icon. */
    EMPIRE(0XF1D1),
    /** The <a href="http://fontawesome.io/icon/envelope/">envelope</a> icon. */
    ENVELOPE(0XF0E0),
    /**
     * The <a href="http://fontawesome.io/icon/envelope-o/">envelope-o</a> icon.
     */
    ENVELOPE_O(0XF003),
    /**
     * The
     * <a href="http://fontawesome.io/icon/envelope-square/">envelope-square</a>
     * icon.
     */
    ENVELOPE_SQUARE(0XF199),
    /** The <a href="http://fontawesome.io/icon/eraser/">eraser</a> icon. */
    ERASER(0XF12D),
    /** The <a href="http://fontawesome.io/icon/eur/">eur</a> icon. */
    EUR(0XF153),
    /** The <a href="http://fontawesome.io/icon/eur/">euro (alias)</a> icon. */
    EURO(0XF153),
    /** The <a href="http://fontawesome.io/icon/exchange/">exchange</a> icon. */
    EXCHANGE(0XF0EC),
    /**
     * The <a href="http://fontawesome.io/icon/exclamation/">exclamation</a>
     * icon.
     */
    EXCLAMATION(0XF12A),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/exclamation-circle/">exclamation-circle</a>
     * icon.
     */
    EXCLAMATION_CIRCLE(0XF06A),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/exclamation-triangle/">exclamation-triangle</a>
     * icon.
     */
    EXCLAMATION_TRIANGLE(0XF071),
    /** The <a href="http://fontawesome.io/icon/expand/">expand</a> icon. */
    EXPAND(0XF065),
    /**
     * The <a href="http://fontawesome.io/icon/expeditedssl/">expeditedssl</a>
     * icon.
     */
    EXPEDITEDSSL(0XF23E),
    /**
     * The <a href="http://fontawesome.io/icon/external-link/">external-link</a>
     * icon.
     */
    EXTERNAL_LINK(0XF08E),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/external-link-square/">external-link-square</a>
     * icon.
     */
    EXTERNAL_LINK_SQUARE(0XF14C),
    /** The <a href="http://fontawesome.io/icon/eye/">eye</a> icon. */
    EYE(0XF06E),
    /**
     * The <a href="http://fontawesome.io/icon/eye-slash/">eye-slash</a> icon.
     */
    EYE_SLASH(0XF070),
    /**
     * The <a href="http://fontawesome.io/icon/eyedropper/">eyedropper</a> icon.
     */
    EYEDROPPER(0XF1FB),
    /** The <a href="http://fontawesome.io/icon/facebook/">facebook</a> icon. */
    FACEBOOK(0XF09A),
    /**
     * The <a href="http://fontawesome.io/icon/facebook/">facebook-f (alias)</a>
     * icon.
     */
    FACEBOOK_F(0XF09A),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/facebook-official/">facebook-official</a>
     * icon.
     */
    FACEBOOK_OFFICIAL(0XF230),
    /**
     * The
     * <a href="http://fontawesome.io/icon/facebook-square/">facebook-square</a>
     * icon.
     */
    FACEBOOK_SQUARE(0XF082),
    /**
     * The <a href="http://fontawesome.io/icon/fast-backward/">fast-backward</a>
     * icon.
     */
    FAST_BACKWARD(0XF049),
    /**
     * The <a href="http://fontawesome.io/icon/fast-forward/">fast-forward</a>
     * icon.
     */
    FAST_FORWARD(0XF050),
    /** The <a href="http://fontawesome.io/icon/fax/">fax</a> icon. */
    FAX(0XF1AC),
    /** The <a href="http://fontawesome.io/icon/rss/">feed (alias)</a> icon. */
    FEED(0XF09E),
    /** The <a href="http://fontawesome.io/icon/female/">female</a> icon. */
    FEMALE(0XF182),
    /**
     * The <a href="http://fontawesome.io/icon/fighter-jet/">fighter-jet</a>
     * icon.
     */
    FIGHTER_JET(0XF0FB),
    /** The <a href="http://fontawesome.io/icon/file/">file</a> icon. */
    FILE(0XF15B),
    /**
     * The
     * <a href="http://fontawesome.io/icon/file-archive-o/">file-archive-o</a>
     * icon.
     */
    FILE_ARCHIVE_O(0XF1C6),
    /**
     * The <a href="http://fontawesome.io/icon/file-audio-o/">file-audio-o</a>
     * icon.
     */
    FILE_AUDIO_O(0XF1C7),
    /**
     * The <a href="http://fontawesome.io/icon/file-code-o/">file-code-o</a>
     * icon.
     */
    FILE_CODE_O(0XF1C9),
    /**
     * The <a href="http://fontawesome.io/icon/file-excel-o/">file-excel-o</a>
     * icon.
     */
    FILE_EXCEL_O(0XF1C3),
    /**
     * The <a href="http://fontawesome.io/icon/file-image-o/">file-image-o</a>
     * icon.
     */
    FILE_IMAGE_O(0XF1C5),
    /**
     * The <a href="http://fontawesome.io/icon/file-video-o/">file-movie-o
     * (alias)</a> icon.
     */
    FILE_MOVIE_O(0XF1C8),
    /** The <a href="http://fontawesome.io/icon/file-o/">file-o</a> icon. */
    FILE_O(0XF016),
    /**
     * The <a href="http://fontawesome.io/icon/file-pdf-o/">file-pdf-o</a> icon.
     */
    FILE_PDF_O(0XF1C1),
    /**
     * The <a href="http://fontawesome.io/icon/file-image-o/">file-photo-o
     * (alias)</a> icon.
     */
    FILE_PHOTO_O(0XF1C5),
    /**
     * The <a href="http://fontawesome.io/icon/file-image-o/">file-picture-o
     * (alias)</a> icon.
     */
    FILE_PICTURE_O(0XF1C5),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/file-powerpoint-o/">file-powerpoint-o</a>
     * icon.
     */
    FILE_POWERPOINT_O(0XF1C4),
    /**
     * The <a href="http://fontawesome.io/icon/file-audio-o/">file-sound-o
     * (alias)</a> icon.
     */
    FILE_SOUND_O(0XF1C7),
    /**
     * The <a href="http://fontawesome.io/icon/file-text/">file-text</a> icon.
     */
    FILE_TEXT(0XF15C),
    /**
     * The <a href="http://fontawesome.io/icon/file-text-o/">file-text-o</a>
     * icon.
     */
    FILE_TEXT_O(0XF0F6),
    /**
     * The <a href="http://fontawesome.io/icon/file-video-o/">file-video-o</a>
     * icon.
     */
    FILE_VIDEO_O(0XF1C8),
    /**
     * The <a href="http://fontawesome.io/icon/file-word-o/">file-word-o</a>
     * icon.
     */
    FILE_WORD_O(0XF1C2),
    /**
     * The <a href="http://fontawesome.io/icon/file-archive-o/">file-zip-o
     * (alias)</a> icon.
     */
    FILE_ZIP_O(0XF1C6),
    /** The <a href="http://fontawesome.io/icon/files-o/">files-o</a> icon. */
    FILES_O(0XF0C5),
    /** The <a href="http://fontawesome.io/icon/film/">film</a> icon. */
    FILM(0XF008),
    /** The <a href="http://fontawesome.io/icon/filter/">filter</a> icon. */
    FILTER(0XF0B0),
    /** The <a href="http://fontawesome.io/icon/fire/">fire</a> icon. */
    FIRE(0XF06D),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/fire-extinguisher/">fire-extinguisher</a>
     * icon.
     */
    FIRE_EXTINGUISHER(0XF134),
    /** The <a href="http://fontawesome.io/icon/firefox/">firefox</a> icon. */
    FIREFOX(0XF269),
    /** The <a href="http://fontawesome.io/icon/flag/">flag</a> icon. */
    FLAG(0XF024),
    /**
     * The
     * <a href="http://fontawesome.io/icon/flag-checkered/">flag-checkered</a>
     * icon.
     */
    FLAG_CHECKERED(0XF11E),
    /** The <a href="http://fontawesome.io/icon/flag-o/">flag-o</a> icon. */
    FLAG_O(0XF11D),
    /**
     * The <a href="http://fontawesome.io/icon/bolt/">flash (alias)</a> icon.
     */
    FLASH(0XF0E7),
    /** The <a href="http://fontawesome.io/icon/flask/">flask</a> icon. */
    FLASK(0XF0C3),
    /** The <a href="http://fontawesome.io/icon/flickr/">flickr</a> icon. */
    FLICKR(0XF16E),
    /** The <a href="http://fontawesome.io/icon/floppy-o/">floppy-o</a> icon. */
    FLOPPY_O(0XF0C7),
    /** The <a href="http://fontawesome.io/icon/folder/">folder</a> icon. */
    FOLDER(0XF07B),
    /** The <a href="http://fontawesome.io/icon/folder-o/">folder-o</a> icon. */
    FOLDER_O(0XF114),
    /**
     * The <a href="http://fontawesome.io/icon/folder-open/">folder-open</a>
     * icon.
     */
    FOLDER_OPEN(0XF07C),
    /**
     * The <a href="http://fontawesome.io/icon/folder-open-o/">folder-open-o</a>
     * icon.
     */
    FOLDER_OPEN_O(0XF115),
    /** The <a href="http://fontawesome.io/icon/font/">font</a> icon. */
    FONT(0XF031),
    /**
     * The <a href="http://fontawesome.io/icon/fonticons/">fonticons</a> icon.
     */
    FONTICONS(0XF280),
    /**
     * The <a href="http://fontawesome.io/icon/fort-awesome/">fort-awesome</a>
     * icon.
     */
    FORT_AWESOME(0XF286),
    /** The <a href="http://fontawesome.io/icon/forumbee/">forumbee</a> icon. */
    FORUMBEE(0XF211),
    /** The <a href="http://fontawesome.io/icon/forward/">forward</a> icon. */
    FORWARD(0XF04E),
    /**
     * The <a href="http://fontawesome.io/icon/foursquare/">foursquare</a> icon.
     */
    FOURSQUARE(0XF180),
    /** The <a href="http://fontawesome.io/icon/frown-o/">frown-o</a> icon. */
    FROWN_O(0XF119),
    /** The <a href="http://fontawesome.io/icon/futbol-o/">futbol-o</a> icon. */
    FUTBOL_O(0XF1E3),
    /** The <a href="http://fontawesome.io/icon/gamepad/">gamepad</a> icon. */
    GAMEPAD(0XF11B),
    /** The <a href="http://fontawesome.io/icon/gavel/">gavel</a> icon. */
    GAVEL(0XF0E3),
    /** The <a href="http://fontawesome.io/icon/gbp/">gbp</a> icon. */
    GBP(0XF154),
    /** The <a href="http://fontawesome.io/icon/empire/">ge (alias)</a> icon. */
    GE(0XF1D1),
    /** The <a href="http://fontawesome.io/icon/cog/">gear (alias)</a> icon. */
    GEAR(0XF013),
    /**
     * The <a href="http://fontawesome.io/icon/cogs/">gears (alias)</a> icon.
     */
    GEARS(0XF085),
    /**
     * The <a href="http://fontawesome.io/icon/genderless/">genderless</a> icon.
     */
    GENDERLESS(0XF22D),
    /**
     * The <a href="http://fontawesome.io/icon/get-pocket/">get-pocket</a> icon.
     */
    GET_POCKET(0XF265),
    /** The <a href="http://fontawesome.io/icon/gg/">gg</a> icon. */
    GG(0XF260),
    /**
     * The <a href="http://fontawesome.io/icon/gg-circle/">gg-circle</a> icon.
     */
    GG_CIRCLE(0XF261),
    /** The <a href="http://fontawesome.io/icon/gift/">gift</a> icon. */
    GIFT(0XF06B),
    /** The <a href="http://fontawesome.io/icon/git/">git</a> icon. */
    GIT(0XF1D3),
    /**
     * The <a href="http://fontawesome.io/icon/git-square/">git-square</a> icon.
     */
    GIT_SQUARE(0XF1D2),
    /** The <a href="http://fontawesome.io/icon/github/">github</a> icon. */
    GITHUB(0XF09B),
    /**
     * The <a href="http://fontawesome.io/icon/github-alt/">github-alt</a> icon.
     */
    GITHUB_ALT(0XF113),
    /**
     * The <a href="http://fontawesome.io/icon/github-square/">github-square</a>
     * icon.
     */
    GITHUB_SQUARE(0XF092),
    /**
     * The <a href="http://fontawesome.io/icon/gratipay/">gittip (alias)</a>
     * icon.
     */
    GITTIP(0XF184),
    /** The <a href="http://fontawesome.io/icon/glass/">glass</a> icon. */
    GLASS(0XF000),
    /** The <a href="http://fontawesome.io/icon/globe/">globe</a> icon. */
    GLOBE(0XF0AC),
    /** The <a href="http://fontawesome.io/icon/google/">google</a> icon. */
    GOOGLE(0XF1A0),
    /**
     * The <a href="http://fontawesome.io/icon/google-plus/">google-plus</a>
     * icon.
     */
    GOOGLE_PLUS(0XF0D5),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/google-plus-square/">google-plus-square</a>
     * icon.
     */
    GOOGLE_PLUS_SQUARE(0XF0D4),
    /**
     * The <a href="http://fontawesome.io/icon/google-wallet/">google-wallet</a>
     * icon.
     */
    GOOGLE_WALLET(0XF1EE),
    /**
     * The
     * <a href="http://fontawesome.io/icon/graduation-cap/">graduation-cap</a>
     * icon.
     */
    GRADUATION_CAP(0XF19D),
    /** The <a href="http://fontawesome.io/icon/gratipay/">gratipay</a> icon. */
    GRATIPAY(0XF184),
    /**
     * The <a href="http://fontawesome.io/icon/users/">group (alias)</a> icon.
     */
    GROUP(0XF0C0),
    /** The <a href="http://fontawesome.io/icon/h-square/">h-square</a> icon. */
    H_SQUARE(0XF0FD),
    /**
     * The <a href="http://fontawesome.io/icon/hacker-news/">hacker-news</a>
     * icon.
     */
    HACKER_NEWS(0XF1D4),
    /**
     * The <a href="http://fontawesome.io/icon/hand-rock-o/">hand-grab-o
     * (alias)</a> icon.
     */
    HAND_GRAB_O(0XF255),
    /**
     * The <a href="http://fontawesome.io/icon/hand-lizard-o/">hand-lizard-o</a>
     * icon.
     */
    HAND_LIZARD_O(0XF258),
    /**
     * The <a href="http://fontawesome.io/icon/hand-o-down/">hand-o-down</a>
     * icon.
     */
    HAND_O_DOWN(0XF0A7),
    /**
     * The <a href="http://fontawesome.io/icon/hand-o-left/">hand-o-left</a>
     * icon.
     */
    HAND_O_LEFT(0XF0A5),
    /**
     * The <a href="http://fontawesome.io/icon/hand-o-right/">hand-o-right</a>
     * icon.
     */
    HAND_O_RIGHT(0XF0A4),
    /**
     * The <a href="http://fontawesome.io/icon/hand-o-up/">hand-o-up</a> icon.
     */
    HAND_O_UP(0XF0A6),
    /**
     * The <a href="http://fontawesome.io/icon/hand-paper-o/">hand-paper-o</a>
     * icon.
     */
    HAND_PAPER_O(0XF256),
    /**
     * The <a href="http://fontawesome.io/icon/hand-peace-o/">hand-peace-o</a>
     * icon.
     */
    HAND_PEACE_O(0XF25B),
    /**
     * The
     * <a href="http://fontawesome.io/icon/hand-pointer-o/">hand-pointer-o</a>
     * icon.
     */
    HAND_POINTER_O(0XF25A),
    /**
     * The <a href="http://fontawesome.io/icon/hand-rock-o/">hand-rock-o</a>
     * icon.
     */
    HAND_ROCK_O(0XF255),
    /**
     * The
     * <a href="http://fontawesome.io/icon/hand-scissors-o/">hand-scissors-o</a>
     * icon.
     */
    HAND_SCISSORS_O(0XF257),
    /**
     * The <a href="http://fontawesome.io/icon/hand-spock-o/">hand-spock-o</a>
     * icon.
     */
    HAND_SPOCK_O(0XF259),
    /**
     * The <a href="http://fontawesome.io/icon/hand-paper-o/">hand-stop-o
     * (alias)</a> icon.
     */
    HAND_STOP_O(0XF256),
    /** The <a href="http://fontawesome.io/icon/hashtag/">hashtag</a> icon. */
    HASHTAG(0XF292),
    /** The <a href="http://fontawesome.io/icon/hdd-o/">hdd-o</a> icon. */
    HDD_O(0XF0A0),
    /** The <a href="http://fontawesome.io/icon/header/">header</a> icon. */
    HEADER(0XF1DC),
    /**
     * The <a href="http://fontawesome.io/icon/headphones/">headphones</a> icon.
     */
    HEADPHONES(0XF025),
    /** The <a href="http://fontawesome.io/icon/heart/">heart</a> icon. */
    HEART(0XF004),
    /** The <a href="http://fontawesome.io/icon/heart-o/">heart-o</a> icon. */
    HEART_O(0XF08A),
    /**
     * The <a href="http://fontawesome.io/icon/heartbeat/">heartbeat</a> icon.
     */
    HEARTBEAT(0XF21E),
    /** The <a href="http://fontawesome.io/icon/history/">history</a> icon. */
    HISTORY(0XF1DA),
    /** The <a href="http://fontawesome.io/icon/home/">home</a> icon. */
    HOME(0XF015),
    /**
     * The <a href="http://fontawesome.io/icon/hospital-o/">hospital-o</a> icon.
     */
    HOSPITAL_O(0XF0F8),
    /** The <a href="http://fontawesome.io/icon/bed/">hotel (alias)</a> icon. */
    HOTEL(0XF236),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass/">hourglass</a> icon.
     */
    HOURGLASS(0XF254),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass-start/">hourglass-1
     * (alias)</a> icon.
     */
    HOURGLASS_1(0XF251),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass-half/">hourglass-2
     * (alias)</a> icon.
     */
    HOURGLASS_2(0XF252),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass-end/">hourglass-3
     * (alias)</a> icon.
     */
    HOURGLASS_3(0XF253),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass-end/">hourglass-end</a>
     * icon.
     */
    HOURGLASS_END(0XF253),
    /**
     * The
     * <a href="http://fontawesome.io/icon/hourglass-half/">hourglass-half</a>
     * icon.
     */
    HOURGLASS_HALF(0XF252),
    /**
     * The <a href="http://fontawesome.io/icon/hourglass-o/">hourglass-o</a>
     * icon.
     */
    HOURGLASS_O(0XF250),
    /**
     * The
     * <a href="http://fontawesome.io/icon/hourglass-start/">hourglass-start</a>
     * icon.
     */
    HOURGLASS_START(0XF251),
    /** The <a href="http://fontawesome.io/icon/houzz/">houzz</a> icon. */
    HOUZZ(0XF27C),
    /** The <a href="http://fontawesome.io/icon/html5/">html5</a> icon. */
    HTML5(0XF13B),
    /** The <a href="http://fontawesome.io/icon/i-cursor/">i-cursor</a> icon. */
    I_CURSOR(0XF246),
    /** The <a href="http://fontawesome.io/icon/ils/">ils</a> icon. */
    ILS(0XF20B),
    /**
     * The <a href="http://fontawesome.io/icon/picture-o/">image (alias)</a>
     * icon.
     */
    IMAGE(0XF03E),
    /** The <a href="http://fontawesome.io/icon/inbox/">inbox</a> icon. */
    INBOX(0XF01C),
    /** The <a href="http://fontawesome.io/icon/indent/">indent</a> icon. */
    INDENT(0XF03C),
    /** The <a href="http://fontawesome.io/icon/industry/">industry</a> icon. */
    INDUSTRY(0XF275),
    /** The <a href="http://fontawesome.io/icon/info/">info</a> icon. */
    INFO(0XF129),
    /**
     * The <a href="http://fontawesome.io/icon/info-circle/">info-circle</a>
     * icon.
     */
    INFO_CIRCLE(0XF05A),
    /** The <a href="http://fontawesome.io/icon/inr/">inr</a> icon. */
    INR(0XF156),
    /**
     * The <a href="http://fontawesome.io/icon/instagram/">instagram</a> icon.
     */
    INSTAGRAM(0XF16D),
    /**
     * The <a href="http://fontawesome.io/icon/university/">institution
     * (alias)</a> icon.
     */
    INSTITUTION(0XF19C),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/internet-explorer/">internet-explorer</a>
     * icon.
     */
    INTERNET_EXPLORER(0XF26B),
    /**
     * The <a href="http://fontawesome.io/icon/transgender/">intersex
     * (alias)</a> icon.
     */
    INTERSEX(0XF224),
    /** The <a href="http://fontawesome.io/icon/ioxhost/">ioxhost</a> icon. */
    IOXHOST(0XF208),
    /** The <a href="http://fontawesome.io/icon/italic/">italic</a> icon. */
    ITALIC(0XF033),
    /** The <a href="http://fontawesome.io/icon/joomla/">joomla</a> icon. */
    JOOMLA(0XF1AA),
    /** The <a href="http://fontawesome.io/icon/jpy/">jpy</a> icon. */
    JPY(0XF157),
    /** The <a href="http://fontawesome.io/icon/jsfiddle/">jsfiddle</a> icon. */
    JSFIDDLE(0XF1CC),
    /** The <a href="http://fontawesome.io/icon/key/">key</a> icon. */
    KEY(0XF084),
    /**
     * The <a href="http://fontawesome.io/icon/keyboard-o/">keyboard-o</a> icon.
     */
    KEYBOARD_O(0XF11C),
    /** The <a href="http://fontawesome.io/icon/krw/">krw</a> icon. */
    KRW(0XF159),
    /** The <a href="http://fontawesome.io/icon/language/">language</a> icon. */
    LANGUAGE(0XF1AB),
    /** The <a href="http://fontawesome.io/icon/laptop/">laptop</a> icon. */
    LAPTOP(0XF109),
    /** The <a href="http://fontawesome.io/icon/lastfm/">lastfm</a> icon. */
    LASTFM(0XF202),
    /**
     * The <a href="http://fontawesome.io/icon/lastfm-square/">lastfm-square</a>
     * icon.
     */
    LASTFM_SQUARE(0XF203),
    /** The <a href="http://fontawesome.io/icon/leaf/">leaf</a> icon. */
    LEAF(0XF06C),
    /** The <a href="http://fontawesome.io/icon/leanpub/">leanpub</a> icon. */
    LEANPUB(0XF212),
    /**
     * The <a href="http://fontawesome.io/icon/gavel/">legal (alias)</a> icon.
     */
    LEGAL(0XF0E3),
    /** The <a href="http://fontawesome.io/icon/lemon-o/">lemon-o</a> icon. */
    LEMON_O(0XF094),
    /**
     * The <a href="http://fontawesome.io/icon/level-down/">level-down</a> icon.
     */
    LEVEL_DOWN(0XF149),
    /** The <a href="http://fontawesome.io/icon/level-up/">level-up</a> icon. */
    LEVEL_UP(0XF148),
    /**
     * The <a href="http://fontawesome.io/icon/life-ring/">life-bouy (alias)</a>
     * icon.
     */
    LIFE_BOUY(0XF1CD),
    /**
     * The <a href="http://fontawesome.io/icon/life-ring/">life-buoy (alias)</a>
     * icon.
     */
    LIFE_BUOY(0XF1CD),
    /**
     * The <a href="http://fontawesome.io/icon/life-ring/">life-ring</a> icon.
     */
    LIFE_RING(0XF1CD),
    /**
     * The <a href="http://fontawesome.io/icon/life-ring/">life-saver
     * (alias)</a> icon.
     */
    LIFE_SAVER(0XF1CD),
    /**
     * The <a href="http://fontawesome.io/icon/lightbulb-o/">lightbulb-o</a>
     * icon.
     */
    LIGHTBULB_O(0XF0EB),
    /**
     * The <a href="http://fontawesome.io/icon/line-chart/">line-chart</a> icon.
     */
    LINE_CHART(0XF201),
    /** The <a href="http://fontawesome.io/icon/link/">link</a> icon. */
    LINK(0XF0C1),
    /** The <a href="http://fontawesome.io/icon/linkedin/">linkedin</a> icon. */
    LINKEDIN(0XF0E1),
    /**
     * The
     * <a href="http://fontawesome.io/icon/linkedin-square/">linkedin-square</a>
     * icon.
     */
    LINKEDIN_SQUARE(0XF08C),
    /** The <a href="http://fontawesome.io/icon/linux/">linux</a> icon. */
    LINUX(0XF17C),
    /** The <a href="http://fontawesome.io/icon/list/">list</a> icon. */
    LIST(0XF03A),
    /** The <a href="http://fontawesome.io/icon/list-alt/">list-alt</a> icon. */
    LIST_ALT(0XF022),
    /** The <a href="http://fontawesome.io/icon/list-ol/">list-ol</a> icon. */
    LIST_OL(0XF0CB),
    /** The <a href="http://fontawesome.io/icon/list-ul/">list-ul</a> icon. */
    LIST_UL(0XF0CA),
    /**
     * The
     * <a href="http://fontawesome.io/icon/location-arrow/">location-arrow</a>
     * icon.
     */
    LOCATION_ARROW(0XF124),
    /** The <a href="http://fontawesome.io/icon/lock/">lock</a> icon. */
    LOCK(0XF023),
    /**
     * The
     * <a href="http://fontawesome.io/icon/long-arrow-down/">long-arrow-down</a>
     * icon.
     */
    LONG_ARROW_DOWN(0XF175),
    /**
     * The
     * <a href="http://fontawesome.io/icon/long-arrow-left/">long-arrow-left</a>
     * icon.
     */
    LONG_ARROW_LEFT(0XF177),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/long-arrow-right/">long-arrow-right</a> icon.
     */
    LONG_ARROW_RIGHT(0XF178),
    /**
     * The <a href="http://fontawesome.io/icon/long-arrow-up/">long-arrow-up</a>
     * icon.
     */
    LONG_ARROW_UP(0XF176),
    /** The <a href="http://fontawesome.io/icon/magic/">magic</a> icon. */
    MAGIC(0XF0D0),
    /** The <a href="http://fontawesome.io/icon/magnet/">magnet</a> icon. */
    MAGNET(0XF076),
    /**
     * The <a href="http://fontawesome.io/icon/mail-share/">mail-forward
     * (alias)</a> icon.
     */
    MAIL_FORWARD(0XF064),
    /**
     * The <a href="http://fontawesome.io/icon/reply/">mail-reply (alias)</a>
     * icon.
     */
    MAIL_REPLY(0XF112),
    /**
     * The <a href="http://fontawesome.io/icon/reply-all/">mail-reply-all
     * (alias)</a> icon.
     */
    MAIL_REPLY_ALL(0XF122),
    /** The <a href="http://fontawesome.io/icon/male/">male</a> icon. */
    MALE(0XF183),
    /** The <a href="http://fontawesome.io/icon/map/">map</a> icon. */
    MAP(0XF279),
    /**
     * The <a href="http://fontawesome.io/icon/map-marker/">map-marker</a> icon.
     */
    MAP_MARKER(0XF041),
    /** The <a href="http://fontawesome.io/icon/map-o/">map-o</a> icon. */
    MAP_O(0XF278),
    /** The <a href="http://fontawesome.io/icon/map-pin/">map-pin</a> icon. */
    MAP_PIN(0XF276),
    /**
     * The <a href="http://fontawesome.io/icon/map-signs/">map-signs</a> icon.
     */
    MAP_SIGNS(0XF277),
    /** The <a href="http://fontawesome.io/icon/mars/">mars</a> icon. */
    MARS(0XF222),
    /**
     * The <a href="http://fontawesome.io/icon/mars-double/">mars-double</a>
     * icon.
     */
    MARS_DOUBLE(0XF227),
    /**
     * The <a href="http://fontawesome.io/icon/mars-stroke/">mars-stroke</a>
     * icon.
     */
    MARS_STROKE(0XF229),
    /**
     * The <a href="http://fontawesome.io/icon/mars-stroke-h/">mars-stroke-h</a>
     * icon.
     */
    MARS_STROKE_H(0XF22B),
    /**
     * The <a href="http://fontawesome.io/icon/mars-stroke-v/">mars-stroke-v</a>
     * icon.
     */
    MARS_STROKE_V(0XF22A),
    /** The <a href="http://fontawesome.io/icon/maxcdn/">maxcdn</a> icon. */
    MAXCDN(0XF136),
    /** The <a href="http://fontawesome.io/icon/meanpath/">meanpath</a> icon. */
    MEANPATH(0XF20C),
    /** The <a href="http://fontawesome.io/icon/medium/">medium</a> icon. */
    MEDIUM(0XF23A),
    /** The <a href="http://fontawesome.io/icon/medkit/">medkit</a> icon. */
    MEDKIT(0XF0FA),
    /** The <a href="http://fontawesome.io/icon/meh-o/">meh-o</a> icon. */
    MEH_O(0XF11A),
    /** The <a href="http://fontawesome.io/icon/mercury/">mercury</a> icon. */
    MERCURY(0XF223),
    /**
     * The <a href="http://fontawesome.io/icon/microphone/">microphone</a> icon.
     */
    MICROPHONE(0XF130),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/microphone-slash/">microphone-slash</a> icon.
     */
    MICROPHONE_SLASH(0XF131),
    /** The <a href="http://fontawesome.io/icon/minus/">minus</a> icon. */
    MINUS(0XF068),
    /**
     * The <a href="http://fontawesome.io/icon/minus-circle/">minus-circle</a>
     * icon.
     */
    MINUS_CIRCLE(0XF056),
    /**
     * The <a href="http://fontawesome.io/icon/minus-square/">minus-square</a>
     * icon.
     */
    MINUS_SQUARE(0XF146),
    /**
     * The
     * <a href="http://fontawesome.io/icon/minus-square-o/">minus-square-o</a>
     * icon.
     */
    MINUS_SQUARE_O(0XF147),
    /** The <a href="http://fontawesome.io/icon/mixcloud/">mixcloud</a> icon. */
    MIXCLOUD(0XF289),
    /** The <a href="http://fontawesome.io/icon/mobile/">mobile</a> icon. */
    MOBILE(0XF10B),
    /**
     * The <a href="http://fontawesome.io/icon/mobile/">mobile-phone (alias)</a>
     * icon.
     */
    MOBILE_PHONE(0XF10B),
    /** The <a href="http://fontawesome.io/icon/modx/">modx</a> icon. */
    MODX(0XF285),
    /** The <a href="http://fontawesome.io/icon/money/">money</a> icon. */
    MONEY(0XF0D6),
    /** The <a href="http://fontawesome.io/icon/moon-o/">moon-o</a> icon. */
    MOON_O(0XF186),
    /**
     * The <a href="http://fontawesome.io/icon/graduation-cap/">mortar-board
     * (alias)</a> icon.
     */
    MORTAR_BOARD(0XF19D),
    /**
     * The <a href="http://fontawesome.io/icon/motorcycle/">motorcycle</a> icon.
     */
    MOTORCYCLE(0XF21C),
    /**
     * The <a href="http://fontawesome.io/icon/mouse-pointer/">mouse-pointer</a>
     * icon.
     */
    MOUSE_POINTER(0XF245),
    /** The <a href="http://fontawesome.io/icon/music/">music</a> icon. */
    MUSIC(0XF001),
    /**
     * The <a href="http://fontawesome.io/icon/bars/">navicon (alias)</a> icon.
     */
    NAVICON(0XF0C9),
    /** The <a href="http://fontawesome.io/icon/neuter/">neuter</a> icon. */
    NEUTER(0XF22C),
    /**
     * The <a href="http://fontawesome.io/icon/newspaper-o/">newspaper-o</a>
     * icon.
     */
    NEWSPAPER_O(0XF1EA),
    /**
     * The <a href="http://fontawesome.io/icon/object-group/">object-group</a>
     * icon.
     */
    OBJECT_GROUP(0XF247),
    /**
     * The
     * <a href="http://fontawesome.io/icon/object-ungroup/">object-ungroup</a>
     * icon.
     */
    OBJECT_UNGROUP(0XF248),
    /**
     * The <a href="http://fontawesome.io/icon/odnoklassniki/">odnoklassniki</a>
     * icon.
     */
    ODNOKLASSNIKI(0XF263),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/odnoklassniki-square/">odnoklassniki-square</a>
     * icon.
     */
    ODNOKLASSNIKI_SQUARE(0XF264),
    /** The <a href="http://fontawesome.io/icon/opencart/">opencart</a> icon. */
    OPENCART(0XF23D),
    /** The <a href="http://fontawesome.io/icon/openid/">openid</a> icon. */
    OPENID(0XF19B),
    /** The <a href="http://fontawesome.io/icon/opera/">opera</a> icon. */
    OPERA(0XF26A),
    /**
     * The <a href="http://fontawesome.io/icon/optin-monster/">optin-monster</a>
     * icon.
     */
    OPTIN_MONSTER(0XF23C),
    /** The <a href="http://fontawesome.io/icon/outdent/">outdent</a> icon. */
    OUTDENT(0XF03B),
    /**
     * The <a href="http://fontawesome.io/icon/pagelines/">pagelines</a> icon.
     */
    PAGELINES(0XF18C),
    /**
     * The <a href="http://fontawesome.io/icon/paint-brush/">paint-brush</a>
     * icon.
     */
    PAINT_BRUSH(0XF1FC),
    /**
     * The <a href="http://fontawesome.io/icon/paper-plane/">paper-plane</a>
     * icon.
     */
    PAPER_PLANE(0XF1D8),
    /**
     * The <a href="http://fontawesome.io/icon/paper-plane-o/">paper-plane-o</a>
     * icon.
     */
    PAPER_PLANE_O(0XF1D9),
    /**
     * The <a href="http://fontawesome.io/icon/paperclip/">paperclip</a> icon.
     */
    PAPERCLIP(0XF0C6),
    /**
     * The <a href="http://fontawesome.io/icon/paragraph/">paragraph</a> icon.
     */
    PARAGRAPH(0XF1DD),
    /** The <a href="http://fontawesome.io/icon/paste/">paste</a> icon. */
    PASTE(0XF0EA),
    /** The <a href="http://fontawesome.io/icon/pause/">pause</a> icon. */
    PAUSE(0XF04C),
    /**
     * The <a href="http://fontawesome.io/icon/pause-circle/">pause-circle</a>
     * icon.
     */
    PAUSE_CIRCLE(0XF28B),
    /**
     * The
     * <a href="http://fontawesome.io/icon/pause-circle-o/">pause-circle-o</a>
     * icon.
     */
    PAUSE_CIRCLE_O(0XF28C),
    /** The <a href="http://fontawesome.io/icon/paw/">paw</a> icon. */
    PAW(0XF1B0),
    /** The <a href="http://fontawesome.io/icon/paypal/">paypal</a> icon. */
    PAYPAL(0XF1ED),
    /** The <a href="http://fontawesome.io/icon/pencil/">pencil</a> icon. */
    PENCIL(0XF040),
    /**
     * The <a href="http://fontawesome.io/icon/pencil-square/">pencil-square</a>
     * icon.
     */
    PENCIL_SQUARE(0XF14B),
    /**
     * The
     * <a href="http://fontawesome.io/icon/pencil-square-o/">pencil-square-o</a>
     * icon.
     */
    PENCIL_SQUARE_O(0XF044),
    /** The <a href="http://fontawesome.io/icon/percent/">percent</a> icon. */
    PERCENT(0XF295),
    /** The <a href="http://fontawesome.io/icon/phone/">phone</a> icon. */
    PHONE(0XF095),
    /**
     * The <a href="http://fontawesome.io/icon/phone-square/">phone-square</a>
     * icon.
     */
    PHONE_SQUARE(0XF098),
    /**
     * The <a href="http://fontawesome.io/icon/picture-o/">photo (alias)</a>
     * icon.
     */
    PHOTO(0XF03E),
    /**
     * The <a href="http://fontawesome.io/icon/picture-o/">picture-o</a> icon.
     */
    PICTURE_O(0XF03E),
    /**
     * The <a href="http://fontawesome.io/icon/pie-chart/">pie-chart</a> icon.
     */
    PIE_CHART(0XF200),
    /**
     * The <a href="http://fontawesome.io/icon/pied-piper/">pied-piper</a> icon.
     */
    PIED_PIPER(0XF1A7),
    /**
     * The
     * <a href="http://fontawesome.io/icon/pied-piper-alt/">pied-piper-alt</a>
     * icon.
     */
    PIED_PIPER_ALT(0XF1A8),
    /**
     * The <a href="http://fontawesome.io/icon/pinterest/">pinterest</a> icon.
     */
    PINTEREST(0XF0D2),
    /**
     * The <a href="http://fontawesome.io/icon/pinterest-p/">pinterest-p</a>
     * icon.
     */
    PINTEREST_P(0XF231),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/pinterest-square/">pinterest-square</a> icon.
     */
    PINTEREST_SQUARE(0XF0D3),
    /** The <a href="http://fontawesome.io/icon/plane/">plane</a> icon. */
    PLANE(0XF072),
    /** The <a href="http://fontawesome.io/icon/play/">play</a> icon. */
    PLAY(0XF04B),
    /**
     * The <a href="http://fontawesome.io/icon/play-circle/">play-circle</a>
     * icon.
     */
    PLAY_CIRCLE(0XF144),
    /**
     * The <a href="http://fontawesome.io/icon/play-circle-o/">play-circle-o</a>
     * icon.
     */
    PLAY_CIRCLE_O(0XF01D),
    /** The <a href="http://fontawesome.io/icon/plug/">plug</a> icon. */
    PLUG(0XF1E6),
    /** The <a href="http://fontawesome.io/icon/plus/">plus</a> icon. */
    PLUS(0XF067),
    /**
     * The <a href="http://fontawesome.io/icon/plus-circle/">plus-circle</a>
     * icon.
     */
    PLUS_CIRCLE(0XF055),
    /**
     * The <a href="http://fontawesome.io/icon/plus-square/">plus-square</a>
     * icon.
     */
    PLUS_SQUARE(0XF0FE),
    /**
     * The <a href="http://fontawesome.io/icon/plus-square-o/">plus-square-o</a>
     * icon.
     */
    PLUS_SQUARE_O(0XF196),
    /**
     * The <a href="http://fontawesome.io/icon/power-off/">power-off</a> icon.
     */
    POWER_OFF(0XF011),
    /** The <a href="http://fontawesome.io/icon/print/">print</a> icon. */
    PRINT(0XF02F),
    /**
     * The <a href="http://fontawesome.io/icon/product-hunt/">product-hunt</a>
     * icon.
     */
    PRODUCT_HUNT(0XF288),
    /**
     * The <a href="http://fontawesome.io/icon/puzzle-piece/">puzzle-piece</a>
     * icon.
     */
    PUZZLE_PIECE(0XF12E),
    /** The <a href="http://fontawesome.io/icon/qq/">qq</a> icon. */
    QQ(0XF1D6),
    /** The <a href="http://fontawesome.io/icon/qrcode/">qrcode</a> icon. */
    QRCODE(0XF029),
    /** The <a href="http://fontawesome.io/icon/question/">question</a> icon. */
    QUESTION(0XF128),
    /**
     * The
     * <a href="http://fontawesome.io/icon/question-circle/">question-circle</a>
     * icon.
     */
    QUESTION_CIRCLE(0XF059),
    /**
     * The <a href="http://fontawesome.io/icon/quote-left/">quote-left</a> icon.
     */
    QUOTE_LEFT(0XF10D),
    /**
     * The <a href="http://fontawesome.io/icon/quote-right/">quote-right</a>
     * icon.
     */
    QUOTE_RIGHT(0XF10E),
    /** The <a href="http://fontawesome.io/icon/rebel/">ra (alias)</a> icon. */
    RA(0XF1D0),
    /** The <a href="http://fontawesome.io/icon/random/">random</a> icon. */
    RANDOM(0XF074),
    /** The <a href="http://fontawesome.io/icon/rebel/">rebel</a> icon. */
    REBEL(0XF1D0),
    /** The <a href="http://fontawesome.io/icon/recycle/">recycle</a> icon. */
    RECYCLE(0XF1B8),
    /** The <a href="http://fontawesome.io/icon/reddit/">reddit</a> icon. */
    REDDIT(0XF1A1),
    /**
     * The <a href="http://fontawesome.io/icon/reddit-alien/">reddit-alien</a>
     * icon.
     */
    REDDIT_ALIEN(0XF281),
    /**
     * The <a href="http://fontawesome.io/icon/reddit-square/">reddit-square</a>
     * icon.
     */
    REDDIT_SQUARE(0XF1A2),
    /** The <a href="http://fontawesome.io/icon/refresh/">refresh</a> icon. */
    REFRESH(0XF021),
    /**
     * The <a href="http://fontawesome.io/icon/registered/">registered</a> icon.
     */
    REGISTERED(0XF25D),
    /**
     * The <a href="http://fontawesome.io/icon/times/">remove (alias)</a> icon.
     */
    REMOVE(0XF00D),
    /** The <a href="http://fontawesome.io/icon/renren/">renren</a> icon. */
    RENREN(0XF18B),
    /**
     * The <a href="http://fontawesome.io/icon/bars/">reorder (alias)</a> icon.
     */
    REORDER(0XF0C9),
    /** The <a href="http://fontawesome.io/icon/repeat/">repeat</a> icon. */
    REPEAT(0XF01E),
    /** The <a href="http://fontawesome.io/icon/reply/">reply</a> icon. */
    REPLY(0XF112),
    /**
     * The <a href="http://fontawesome.io/icon/reply-all/">reply-all</a> icon.
     */
    REPLY_ALL(0XF122),
    /** The <a href="http://fontawesome.io/icon/retweet/">retweet</a> icon. */
    RETWEET(0XF079),
    /** The <a href="http://fontawesome.io/icon/jpy/">rmb (alias)</a> icon. */
    RMB(0XF157),
    /** The <a href="http://fontawesome.io/icon/road/">road</a> icon. */
    ROAD(0XF018),
    /** The <a href="http://fontawesome.io/icon/rocket/">rocket</a> icon. */
    ROCKET(0XF135),
    /**
     * The <a href="http://fontawesome.io/icon/undo/">rotate-left (alias)</a>
     * icon.
     */
    ROTATE_LEFT(0XF0E2),
    /**
     * The <a href="http://fontawesome.io/icon/repeat/">rotate-right (alias)</a>
     * icon.
     */
    ROTATE_RIGHT(0XF01E),
    /**
     * The <a href="http://fontawesome.io/icon/rub/">rouble (alias)</a> icon.
     */
    ROUBLE(0XF158),
    /** The <a href="http://fontawesome.io/icon/rss/">rss</a> icon. */
    RSS(0XF09E),
    /**
     * The <a href="http://fontawesome.io/icon/rss-square/">rss-square</a> icon.
     */
    RSS_SQUARE(0XF143),
    /** The <a href="http://fontawesome.io/icon/rub/">rub</a> icon. */
    RUB(0XF158),
    /** The <a href="http://fontawesome.io/icon/rub/">ruble (alias)</a> icon. */
    RUBLE(0XF158),
    /** The <a href="http://fontawesome.io/icon/inr/">rupee (alias)</a> icon. */
    RUPEE(0XF156),
    /** The <a href="http://fontawesome.io/icon/safari/">safari</a> icon. */
    SAFARI(0XF267),
    /**
     * The <a href="http://fontawesome.io/icon/floppy-o/">save (alias)</a> icon.
     */
    SAVE(0XF0C7),
    /** The <a href="http://fontawesome.io/icon/scissors/">scissors</a> icon. */
    SCISSORS(0XF0C4),
    /** The <a href="http://fontawesome.io/icon/scribd/">scribd</a> icon. */
    SCRIBD(0XF28A),
    /** The <a href="http://fontawesome.io/icon/search/">search</a> icon. */
    SEARCH(0XF002),
    /**
     * The <a href="http://fontawesome.io/icon/search-minus/">search-minus</a>
     * icon.
     */
    SEARCH_MINUS(0XF010),
    /**
     * The <a href="http://fontawesome.io/icon/search-plus/">search-plus</a>
     * icon.
     */
    SEARCH_PLUS(0XF00E),
    /** The <a href="http://fontawesome.io/icon/sellsy/">sellsy</a> icon. */
    SELLSY(0XF213),
    /**
     * The <a href="http://fontawesome.io/icon/paper-plane/">send (alias)</a>
     * icon.
     */
    SEND(0XF1D8),
    /**
     * The <a href="http://fontawesome.io/icon/paper-plane-o/">send-o
     * (alias)</a> icon.
     */
    SEND_O(0XF1D9),
    /** The <a href="http://fontawesome.io/icon/server/">server</a> icon. */
    SERVER(0XF233),
    /** The <a href="http://fontawesome.io/icon/share/">share</a> icon. */
    SHARE(0XF064),
    /**
     * The <a href="http://fontawesome.io/icon/share-alt/">share-alt</a> icon.
     */
    SHARE_ALT(0XF1E0),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/share-alt-square/">share-alt-square</a> icon.
     */
    SHARE_ALT_SQUARE(0XF1E1),
    /**
     * The <a href="http://fontawesome.io/icon/share-square/">share-square</a>
     * icon.
     */
    SHARE_SQUARE(0XF14D),
    /**
     * The
     * <a href="http://fontawesome.io/icon/share-square-o/">share-square-o</a>
     * icon.
     */
    SHARE_SQUARE_O(0XF045),
    /**
     * The <a href="http://fontawesome.io/icon/ils/">shekel (alias)</a> icon.
     */
    SHEKEL(0XF20B),
    /**
     * The <a href="http://fontawesome.io/icon/ils/">sheqel (alias)</a> icon.
     */
    SHEQEL(0XF20B),
    /** The <a href="http://fontawesome.io/icon/shield/">shield</a> icon. */
    SHIELD(0XF132),
    /** The <a href="http://fontawesome.io/icon/ship/">ship</a> icon. */
    SHIP(0XF21A),
    /**
     * The <a href="http://fontawesome.io/icon/shirtsinbulk/">shirtsinbulk</a>
     * icon.
     */
    SHIRTSINBULK(0XF214),
    /**
     * The <a href="http://fontawesome.io/icon/shopping-bag/">shopping-bag</a>
     * icon.
     */
    SHOPPING_BAG(0XF290),
    /**
     * The
     * <a href="http://fontawesome.io/icon/shopping-basket/">shopping-basket</a>
     * icon.
     */
    SHOPPING_BASKET(0XF291),
    /**
     * The <a href="http://fontawesome.io/icon/shopping-cart/">shopping-cart</a>
     * icon.
     */
    SHOPPING_CART(0XF07A),
    /** The <a href="http://fontawesome.io/icon/sign-in/">sign-in</a> icon. */
    SIGN_IN(0XF090),
    /** The <a href="http://fontawesome.io/icon/sign-out/">sign-out</a> icon. */
    SIGN_OUT(0XF08B),
    /** The <a href="http://fontawesome.io/icon/signal/">signal</a> icon. */
    SIGNAL(0XF012),
    /**
     * The <a href="http://fontawesome.io/icon/simplybuilt/">simplybuilt</a>
     * icon.
     */
    SIMPLYBUILT(0XF215),
    /** The <a href="http://fontawesome.io/icon/sitemap/">sitemap</a> icon. */
    SITEMAP(0XF0E8),
    /** The <a href="http://fontawesome.io/icon/skyatlas/">skyatlas</a> icon. */
    SKYATLAS(0XF216),
    /** The <a href="http://fontawesome.io/icon/skype/">skype</a> icon. */
    SKYPE(0XF17E),
    /** The <a href="http://fontawesome.io/icon/slack/">slack</a> icon. */
    SLACK(0XF198),
    /** The <a href="http://fontawesome.io/icon/sliders/">sliders</a> icon. */
    SLIDERS(0XF1DE),
    /**
     * The <a href="http://fontawesome.io/icon/slideshare/">slideshare</a> icon.
     */
    SLIDESHARE(0XF1E7),
    /** The <a href="http://fontawesome.io/icon/smile-o/">smile-o</a> icon. */
    SMILE_O(0XF118),
    /**
     * The <a href="http://fontawesome.io/icon/futbol-o/">soccer-ball-o
     * (alias)</a> icon.
     */
    SOCCER_BALL_O(0XF1E3),
    /** The <a href="http://fontawesome.io/icon/sort/">sort</a> icon. */
    SORT(0XF0DC),
    /**
     * The
     * <a href="http://fontawesome.io/icon/sort-alpha-asc/">sort-alpha-asc</a>
     * icon.
     */
    SORT_ALPHA_ASC(0XF15D),
    /**
     * The
     * <a href="http://fontawesome.io/icon/sort-alpha-desc/">sort-alpha-desc</a>
     * icon.
     */
    SORT_ALPHA_DESC(0XF15E),
    /**
     * The
     * <a href="http://fontawesome.io/icon/sort-amount-asc/">sort-amount-asc</a>
     * icon.
     */
    SORT_AMOUNT_ASC(0XF160),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/sort-amount-desc/">sort-amount-desc</a> icon.
     */
    SORT_AMOUNT_DESC(0XF161),
    /** The <a href="http://fontawesome.io/icon/sort-asc/">sort-asc</a> icon. */
    SORT_ASC(0XF0DE),
    /**
     * The <a href="http://fontawesome.io/icon/sort-desc/">sort-desc</a> icon.
     */
    SORT_DESC(0XF0DD),
    /**
     * The <a href="http://fontawesome.io/icon/sort-desc/">sort-down (alias)</a>
     * icon.
     */
    SORT_DOWN(0XF0DD),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/sort-numeric-asc/">sort-numeric-asc</a> icon.
     */
    SORT_NUMERIC_ASC(0XF162),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/sort-numeric-desc/">sort-numeric-desc</a>
     * icon.
     */
    SORT_NUMERIC_DESC(0XF163),
    /**
     * The <a href="http://fontawesome.io/icon/sort-asc/">sort-up (alias)</a>
     * icon.
     */
    SORT_UP(0XF0DE),
    /**
     * The <a href="http://fontawesome.io/icon/soundcloud/">soundcloud</a> icon.
     */
    SOUNDCLOUD(0XF1BE),
    /**
     * The <a href="http://fontawesome.io/icon/space-shuttle/">space-shuttle</a>
     * icon.
     */
    SPACE_SHUTTLE(0XF197),
    /** The <a href="http://fontawesome.io/icon/spinner/">spinner</a> icon. */
    SPINNER(0XF110),
    /** The <a href="http://fontawesome.io/icon/spoon/">spoon</a> icon. */
    SPOON(0XF1B1),
    /** The <a href="http://fontawesome.io/icon/spotify/">spotify</a> icon. */
    SPOTIFY(0XF1BC),
    /** The <a href="http://fontawesome.io/icon/square/">square</a> icon. */
    SQUARE(0XF0C8),
    /** The <a href="http://fontawesome.io/icon/square-o/">square-o</a> icon. */
    SQUARE_O(0XF096),
    /**
     * The
     * <a href="http://fontawesome.io/icon/stack-exchange/">stack-exchange</a>
     * icon.
     */
    STACK_EXCHANGE(0XF18D),
    /**
     * The
     * <a href="http://fontawesome.io/icon/stack-overflow/">stack-overflow</a>
     * icon.
     */
    STACK_OVERFLOW(0XF16C),
    /** The <a href="http://fontawesome.io/icon/star/">star</a> icon. */
    STAR(0XF005),
    /**
     * The <a href="http://fontawesome.io/icon/star-half/">star-half</a> icon.
     */
    STAR_HALF(0XF089),
    /**
     * The <a href="http://fontawesome.io/icon/star-half-o/">star-half-empty
     * (alias)</a> icon.
     */
    STAR_HALF_EMPTY(0XF123),
    /**
     * The <a href="http://fontawesome.io/icon/star-half-o/">star-half-full
     * (alias)</a> icon.
     */
    STAR_HALF_FULL(0XF123),
    /**
     * The <a href="http://fontawesome.io/icon/star-half-o/">star-half-o</a>
     * icon.
     */
    STAR_HALF_O(0XF123),
    /** The <a href="http://fontawesome.io/icon/star-o/">star-o</a> icon. */
    STAR_O(0XF006),
    /** The <a href="http://fontawesome.io/icon/steam/">steam</a> icon. */
    STEAM(0XF1B6),
    /**
     * The <a href="http://fontawesome.io/icon/steam-square/">steam-square</a>
     * icon.
     */
    STEAM_SQUARE(0XF1B7),
    /**
     * The <a href="http://fontawesome.io/icon/step-backward/">step-backward</a>
     * icon.
     */
    STEP_BACKWARD(0XF048),
    /**
     * The <a href="http://fontawesome.io/icon/step-forward/">step-forward</a>
     * icon.
     */
    STEP_FORWARD(0XF051),
    /**
     * The <a href="http://fontawesome.io/icon/stethoscope/">stethoscope</a>
     * icon.
     */
    STETHOSCOPE(0XF0F1),
    /**
     * The <a href="http://fontawesome.io/icon/sticky-note/">sticky-note</a>
     * icon.
     */
    STICKY_NOTE(0XF249),
    /**
     * The <a href="http://fontawesome.io/icon/sticky-note-o/">sticky-note-o</a>
     * icon.
     */
    STICKY_NOTE_O(0XF24A),
    /** The <a href="http://fontawesome.io/icon/stop/">stop</a> icon. */
    STOP(0XF04D),
    /**
     * The <a href="http://fontawesome.io/icon/stop-circle/">stop-circle</a>
     * icon.
     */
    STOP_CIRCLE(0XF28D),
    /**
     * The <a href="http://fontawesome.io/icon/stop-circle-o/">stop-circle-o</a>
     * icon.
     */
    STOP_CIRCLE_O(0XF28E),
    /**
     * The <a href="http://fontawesome.io/icon/street-view/">street-view</a>
     * icon.
     */
    STREET_VIEW(0XF21D),
    /**
     * The <a href="http://fontawesome.io/icon/strikethrough/">strikethrough</a>
     * icon.
     */
    STRIKETHROUGH(0XF0CC),
    /**
     * The <a href="http://fontawesome.io/icon/stumbleupon/">stumbleupon</a>
     * icon.
     */
    STUMBLEUPON(0XF1A4),
    /**
     * The <a href=
     * "http://fontawesome.io/icon/stumbleupon-circle/">stumbleupon-circle</a>
     * icon.
     */
    STUMBLEUPON_CIRCLE(0XF1A3),
    /**
     * The <a href="http://fontawesome.io/icon/subscript/">subscript</a> icon.
     */
    SUBSCRIPT(0XF12C),
    /** The <a href="http://fontawesome.io/icon/subway/">subway</a> icon. */
    SUBWAY(0XF239),
    /** The <a href="http://fontawesome.io/icon/suitcase/">suitcase</a> icon. */
    SUITCASE(0XF0F2),
    /** The <a href="http://fontawesome.io/icon/sun-o/">sun-o</a> icon. */
    SUN_O(0XF185),
    /**
     * The <a href="http://fontawesome.io/icon/superscript/">superscript</a>
     * icon.
     */
    SUPERSCRIPT(0XF12B),
    /**
     * The <a href="http://fontawesome.io/icon/life-ring/">support (alias)</a>
     * icon.
     */
    SUPPORT(0XF1CD),
    /** The <a href="http://fontawesome.io/icon/table/">table</a> icon. */
    TABLE(0XF0CE),
    /** The <a href="http://fontawesome.io/icon/tablet/">tablet</a> icon. */
    TABLET(0XF10A),
    /**
     * The <a href="http://fontawesome.io/icon/tachometer/">tachometer</a> icon.
     */
    TACHOMETER(0XF0E4),
    /** The <a href="http://fontawesome.io/icon/tag/">tag</a> icon. */
    TAG(0XF02B),
    /** The <a href="http://fontawesome.io/icon/tags/">tags</a> icon. */
    TAGS(0XF02C),
    /** The <a href="http://fontawesome.io/icon/tasks/">tasks</a> icon. */
    TASKS(0XF0AE),
    /** The <a href="http://fontawesome.io/icon/taxi/">taxi</a> icon. */
    TAXI(0XF1BA),
    /**
     * The <a href="http://fontawesome.io/icon/television/">television</a> icon.
     */
    TELEVISION(0XF26C),
    /**
     * The <a href="http://fontawesome.io/icon/tencent-weibo/">tencent-weibo</a>
     * icon.
     */
    TENCENT_WEIBO(0XF1D5),
    /** The <a href="http://fontawesome.io/icon/terminal/">terminal</a> icon. */
    TERMINAL(0XF120),
    /**
     * The <a href="http://fontawesome.io/icon/text-height/">text-height</a>
     * icon.
     */
    TEXT_HEIGHT(0XF034),
    /**
     * The <a href="http://fontawesome.io/icon/text-width/">text-width</a> icon.
     */
    TEXT_WIDTH(0XF035),
    /** The <a href="http://fontawesome.io/icon/th/">th</a> icon. */
    TH(0XF00A),
    /** The <a href="http://fontawesome.io/icon/th-large/">th-large</a> icon. */
    TH_LARGE(0XF009),
    /** The <a href="http://fontawesome.io/icon/th-list/">th-list</a> icon. */
    TH_LIST(0XF00B),
    /**
     * The <a href="http://fontawesome.io/icon/thumb-tack/">thumb-tack</a> icon.
     */
    THUMB_TACK(0XF08D),
    /**
     * The <a href="http://fontawesome.io/icon/thumbs-down/">thumbs-down</a>
     * icon.
     */
    THUMBS_DOWN(0XF165),
    /**
     * The <a href="http://fontawesome.io/icon/thumbs-o-down/">thumbs-o-down</a>
     * icon.
     */
    THUMBS_O_DOWN(0XF088),
    /**
     * The <a href="http://fontawesome.io/icon/thumbs-o-up/">thumbs-o-up</a>
     * icon.
     */
    THUMBS_O_UP(0XF087),
    /**
     * The <a href="http://fontawesome.io/icon/thumbs-up/">thumbs-up</a> icon.
     */
    THUMBS_UP(0XF164),
    /** The <a href="http://fontawesome.io/icon/ticket/">ticket</a> icon. */
    TICKET(0XF145),
    /** The <a href="http://fontawesome.io/icon/times/">times</a> icon. */
    TIMES(0XF00D),
    /**
     * The <a href="http://fontawesome.io/icon/times-circle/">times-circle</a>
     * icon.
     */
    TIMES_CIRCLE(0XF057),
    /**
     * The
     * <a href="http://fontawesome.io/icon/times-circle-o/">times-circle-o</a>
     * icon.
     */
    TIMES_CIRCLE_O(0XF05C),
    /** The <a href="http://fontawesome.io/icon/tint/">tint</a> icon. */
    TINT(0XF043),
    /**
     * The <a href="http://fontawesome.io/icon/caret-square-o-down/">toggle-down
     * (alias)</a> icon.
     */
    TOGGLE_DOWN(0XF150),
    /**
     * The <a href="http://fontawesome.io/icon/caret-square-o-left/">toggle-left
     * (alias)</a> icon.
     */
    TOGGLE_LEFT(0XF191),
    /**
     * The <a href="http://fontawesome.io/icon/toggle-off/">toggle-off</a> icon.
     */
    TOGGLE_OFF(0XF204),
    /**
     * The <a href="http://fontawesome.io/icon/toggle-on/">toggle-on</a> icon.
     */
    TOGGLE_ON(0XF205),
    /**
     * The
     * <a href="http://fontawesome.io/icon/caret-square-o-right/">toggle-right
     * (alias)</a> icon.
     */
    TOGGLE_RIGHT(0XF152),
    /**
     * The <a href="http://fontawesome.io/icon/caret-square-o-up/">toggle-up
     * (alias)</a> icon.
     */
    TOGGLE_UP(0XF151),
    /**
     * The <a href="http://fontawesome.io/icon/trademark/">trademark</a> icon.
     */
    TRADEMARK(0XF25C),
    /** The <a href="http://fontawesome.io/icon/train/">train</a> icon. */
    TRAIN(0XF238),
    /**
     * The <a href="http://fontawesome.io/icon/transgender/">transgender</a>
     * icon.
     */
    TRANSGENDER(0XF224),
    /**
     * The
     * <a href="http://fontawesome.io/icon/transgender-alt/">transgender-alt</a>
     * icon.
     */
    TRANSGENDER_ALT(0XF225),
    /** The <a href="http://fontawesome.io/icon/trash/">trash</a> icon. */
    TRASH(0XF1F8),
    /** The <a href="http://fontawesome.io/icon/trash-o/">trash-o</a> icon. */
    TRASH_O(0XF014),
    /** The <a href="http://fontawesome.io/icon/tree/">tree</a> icon. */
    TREE(0XF1BB),
    /** The <a href="http://fontawesome.io/icon/trello/">trello</a> icon. */
    TRELLO(0XF181),
    /**
     * The <a href="http://fontawesome.io/icon/tripadvisor/">tripadvisor</a>
     * icon.
     */
    TRIPADVISOR(0XF262),
    /** The <a href="http://fontawesome.io/icon/trophy/">trophy</a> icon. */
    TROPHY(0XF091),
    /** The <a href="http://fontawesome.io/icon/truck/">truck</a> icon. */
    TRUCK(0XF0D1),
    /** The <a href="http://fontawesome.io/icon/try/">try</a> icon. */
    TRY(0XF195),
    /** The <a href="http://fontawesome.io/icon/tty/">tty</a> icon. */
    TTY(0XF1E4),
    /** The <a href="http://fontawesome.io/icon/tumblr/">tumblr</a> icon. */
    TUMBLR(0XF173),
    /**
     * The <a href="http://fontawesome.io/icon/tumblr-square/">tumblr-square</a>
     * icon.
     */
    TUMBLR_SQUARE(0XF174),
    /**
     * The <a href="http://fontawesome.io/icon/try/">turkish-lira (alias)</a>
     * icon.
     */
    TURKISH_LIRA(0XF195),
    /**
     * The <a href="http://fontawesome.io/icon/television/">tv (alias)</a> icon.
     */
    TV(0XF26C),
    /** The <a href="http://fontawesome.io/icon/twitch/">twitch</a> icon. */
    TWITCH(0XF1E8),
    /** The <a href="http://fontawesome.io/icon/twitter/">twitter</a> icon. */
    TWITTER(0XF099),
    /**
     * The
     * <a href="http://fontawesome.io/icon/twitter-square/">twitter-square</a>
     * icon.
     */
    TWITTER_SQUARE(0XF081),
    /** The <a href="http://fontawesome.io/icon/umbrella/">umbrella</a> icon. */
    UMBRELLA(0XF0E9),
    /**
     * The <a href="http://fontawesome.io/icon/underline/">underline</a> icon.
     */
    UNDERLINE(0XF0CD),
    /** The <a href="http://fontawesome.io/icon/undo/">undo</a> icon. */
    UNDO(0XF0E2),
    /**
     * The <a href="http://fontawesome.io/icon/university/">university</a> icon.
     */
    UNIVERSITY(0XF19C),
    /**
     * The <a href="http://fontawesome.io/icon/chain-broken/">unlink (alias)</a>
     * icon.
     */
    UNLINK(0XF127),
    /** The <a href="http://fontawesome.io/icon/unlock/">unlock</a> icon. */
    UNLOCK(0XF09C),
    /**
     * The <a href="http://fontawesome.io/icon/unlock-alt/">unlock-alt</a> icon.
     */
    UNLOCK_ALT(0XF13E),
    /**
     * The <a href="http://fontawesome.io/icon/sort/">unsorted (alias)</a> icon.
     */
    UNSORTED(0XF0DC),
    /** The <a href="http://fontawesome.io/icon/upload/">upload</a> icon. */
    UPLOAD(0XF093),
    /** The <a href="http://fontawesome.io/icon/usb/">usb</a> icon. */
    USB(0XF287),
    /** The <a href="http://fontawesome.io/icon/usd/">usd</a> icon. */
    USD(0XF155),
    /** The <a href="http://fontawesome.io/icon/user/">user</a> icon. */
    USER(0XF007),
    /** The <a href="http://fontawesome.io/icon/user-md/">user-md</a> icon. */
    USER_MD(0XF0F0),
    /**
     * The <a href="http://fontawesome.io/icon/user-plus/">user-plus</a> icon.
     */
    USER_PLUS(0XF234),
    /**
     * The <a href="http://fontawesome.io/icon/user-secret/">user-secret</a>
     * icon.
     */
    USER_SECRET(0XF21B),
    /**
     * The <a href="http://fontawesome.io/icon/user-times/">user-times</a> icon.
     */
    USER_TIMES(0XF235),
    /** The <a href="http://fontawesome.io/icon/users/">users</a> icon. */
    USERS(0XF0C0),
    /** The <a href="http://fontawesome.io/icon/venus/">venus</a> icon. */
    VENUS(0XF221),
    /**
     * The <a href="http://fontawesome.io/icon/venus-double/">venus-double</a>
     * icon.
     */
    VENUS_DOUBLE(0XF226),
    /**
     * The <a href="http://fontawesome.io/icon/venus-mars/">venus-mars</a> icon.
     */
    VENUS_MARS(0XF228),
    /** The <a href="http://fontawesome.io/icon/viacoin/">viacoin</a> icon. */
    VIACOIN(0XF237),
    /**
     * The <a href="http://fontawesome.io/icon/video-camera/">video-camera</a>
     * icon.
     */
    VIDEO_CAMERA(0XF03D),
    /** The <a href="http://fontawesome.io/icon/vimeo/">vimeo</a> icon. */
    VIMEO(0XF27D),
    /**
     * The <a href="http://fontawesome.io/icon/vimeo-square/">vimeo-square</a>
     * icon.
     */
    VIMEO_SQUARE(0XF194),
    /** The <a href="http://fontawesome.io/icon/vine/">vine</a> icon. */
    VINE(0XF1CA),
    /** The <a href="http://fontawesome.io/icon/vk/">vk</a> icon. */
    VK(0XF189),
    /**
     * The <a href="http://fontawesome.io/icon/volume-down/">volume-down</a>
     * icon.
     */
    VOLUME_DOWN(0XF027),
    /**
     * The <a href="http://fontawesome.io/icon/volume-off/">volume-off</a> icon.
     */
    VOLUME_OFF(0XF026),
    /**
     * The <a href="http://fontawesome.io/icon/volume-up/">volume-up</a> icon.
     */
    VOLUME_UP(0XF028),
    /**
     * The <a href="http://fontawesome.io/icon/exclamation-triangle/">warning
     * (alias)</a> icon.
     */
    WARNING(0XF071),
    /**
     * The <a href="http://fontawesome.io/icon/weixin/">wechat (alias)</a> icon.
     */
    WECHAT(0XF1D7),
    /** The <a href="http://fontawesome.io/icon/weibo/">weibo</a> icon. */
    WEIBO(0XF18A),
    /** The <a href="http://fontawesome.io/icon/weixin/">weixin</a> icon. */
    WEIXIN(0XF1D7),
    /** The <a href="http://fontawesome.io/icon/whatsapp/">whatsapp</a> icon. */
    WHATSAPP(0XF232),
    /**
     * The <a href="http://fontawesome.io/icon/wheelchair/">wheelchair</a> icon.
     */
    WHEELCHAIR(0XF193),
    /** The <a href="http://fontawesome.io/icon/wifi/">wifi</a> icon. */
    WIFI(0XF1EB),
    /**
     * The <a href="http://fontawesome.io/icon/wikipedia-w/">wikipedia-w</a>
     * icon.
     */
    WIKIPEDIA_W(0XF266),
    /** The <a href="http://fontawesome.io/icon/windows/">windows</a> icon. */
    WINDOWS(0XF17A),
    /** The <a href="http://fontawesome.io/icon/krw/">won (alias)</a> icon. */
    WON(0XF159),
    /**
     * The <a href="http://fontawesome.io/icon/wordpress/">wordpress</a> icon.
     */
    WORDPRESS(0XF19A),
    /** The <a href="http://fontawesome.io/icon/wrench/">wrench</a> icon. */
    WRENCH(0XF0AD),
    /** The <a href="http://fontawesome.io/icon/xing/">xing</a> icon. */
    XING(0XF168),
    /**
     * The <a href="http://fontawesome.io/icon/xing-square/">xing-square</a>
     * icon.
     */
    XING_SQUARE(0XF169),
    /**
     * The <a href="http://fontawesome.io/icon/y-combinator/">y-combinator</a>
     * icon.
     */
    Y_COMBINATOR(0XF23B),
    /**
     * The
     * <a href= "http://fontawesome.io/icon/y-hacker-news/">y-combinator-square
     * (alias)</a> icon.
     */
    Y_COMBINATOR_SQUARE(0XF1D4),
    /** The <a href="http://fontawesome.io/icon/yahoo/">yahoo</a> icon. */
    YAHOO(0XF19E),
    /**
     * The <a href="http://fontawesome.io/icon/y-combinator/">yc (alias)</a>
     * icon.
     */
    YC(0XF23B),
    /**
     * The <a href="http://fontawesome.io/icon/hacker-news/">yc-square
     * (alias)</a> icon.
     */
    YC_SQUARE(0XF1D4),
    /** The <a href="http://fontawesome.io/icon/yelp/">yelp</a> icon. */
    YELP(0XF1E9),
    /** The <a href="http://fontawesome.io/icon/jpy/">yen (alias)</a> icon. */
    YEN(0XF157),
    /** The <a href="http://fontawesome.io/icon/youtube/">youtube</a> icon. */
    YOUTUBE(0XF167),
    /**
     * The <a href="http://fontawesome.io/icon/youtube-play/">youtube-play</a>
     * icon.
     */
    YOUTUBE_PLAY(0XF16A),
    /**
     * The
     * <a href="http://fontawesome.io/icon/youtube-square/">youtube-square</a>
     * icon.
     */
    YOUTUBE_SQUARE(0XF166);

    /**
     * The font family of {@link FontAwesome}.
     */
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
     * Finds an instance of FontAwesome with given codepoint.
     *
     * @since 7.5.0
     * @param codepoint
     *            The codepoint to search by
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
