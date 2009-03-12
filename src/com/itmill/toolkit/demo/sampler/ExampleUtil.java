package com.itmill.toolkit.demo.sampler;

import java.util.Locale;

import com.itmill.toolkit.data.Container;
import com.itmill.toolkit.data.Item;
import com.itmill.toolkit.data.util.HierarchicalContainer;
import com.itmill.toolkit.data.util.IndexedContainer;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;

public final class ExampleUtil {
    public static final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut ut massa eget erat dapibus sollicitudin. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Pellentesque a augue. Praesent non elit. Duis sapien dolor, cursus eget, pulvinar eget, eleifend a, est. Integer in nunc. Vivamus consequat ipsum id sapien. Duis eu elit vel libero posuere luctus. Aliquam ac turpis. Aenean vitae justo in sem iaculis pulvinar. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam sit amet mi. "
            + "<br/>"
            + "Aenean auctor, mi sit amet ultricies pulvinar, dui urna adipiscing odio, ut faucibus odio mauris eget justo. Mauris quis magna quis augue interdum porttitor. Sed interdum, tortor laoreet tincidunt ullamcorper, metus velit hendrerit nunc, id laoreet mauris arcu vitae est. Nulla nec nisl. Mauris orci nibh, tempor nec, sollicitudin ac, venenatis sed, lorem. Quisque dignissim tempus erat. Maecenas molestie, pede ac ultrices interdum, felis neque vulputate quam, in sodales felis odio quis mi. Aliquam massa pede, pharetra quis, tincidunt quis, fringilla at, mauris. Vestibulum a massa. Vestibulum luctus odio ut quam. Maecenas congue convallis diam. Cras urna arcu, vestibulum vitae, blandit ut, laoreet id, risus. Ut condimentum, arcu sit amet placerat blandit, augue nibh pretium nunc, in tempus sem dolor non leo. Etiam fringilla mauris a odio. Nunc lorem diam, interdum eget, lacinia in, scelerisque sit amet, purus. Nam ornare. "
            + "<br/>"
            + "Donec placerat dui ut orci. Phasellus quis lacus at nisl elementum cursus. Cras bibendum egestas nulla. Phasellus pulvinar ullamcorper odio. Etiam ipsum. Proin tincidunt. Aliquam aliquet. Etiam purus odio, commodo sed, feugiat volutpat, scelerisque molestie, velit. Aenean sed sem sit amet libero sodales ultrices. Donec dictum, arcu sed iaculis porttitor, est mauris pulvinar purus, sit amet porta purus neque in risus. Mauris libero. Maecenas rhoncus. Morbi quis nisl. "
            + "<br/>"
            + "Vestibulum laoreet tortor eu elit. Cras euismod nulla eu sapien. Sed imperdiet. Maecenas vel sapien. Nulla at purus eu diam auctor lobortis. Donec pede eros, lacinia tincidunt, tempus eu, molestie nec, velit. Nullam ipsum odio, euismod non, aliquet nec, consequat ac, felis. Duis fermentum mauris sed justo. Suspendisse potenti. Praesent at libero sit amet ipsum imperdiet fermentum. Aliquam enim nisl, dictum id, lacinia sit amet, elementum posuere, ipsum. Integer luctus dictum libero. Pellentesque sed pede sed nisl bibendum porttitor. Phasellus tempor interdum nisi. Mauris nec magna. Phasellus massa pede, vehicula sed, ornare at, ullamcorper ut, nisl. Sed turpis nisl, hendrerit sit amet, consequat id, auctor nec, arcu. Quisque fringilla tincidunt massa. In eleifend, nulla sed mollis vestibulum, mauris orci facilisis ante, id pharetra dolor ipsum vitae sem. Integer dictum. "
            + "<br/>"
            + "Nunc ut odio. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec mauris tellus, dapibus vel, hendrerit vel, sollicitudin ut, ligula. Ut justo metus, accumsan placerat, ultrices sit amet, congue at, nulla. Integer in quam. Cras sollicitudin mattis magna. Vestibulum neque eros, egestas ut, tincidunt vel, ullamcorper non, ligula. Vivamus eu lacus. Donec rhoncus metus et odio. Donec est. Nulla facilisi. Suspendisse potenti. Etiam tempor pede nec ante. Vestibulum adipiscing velit vel neque. "
            + "<br/>"
            + "Quisque ornare erat rhoncus lectus. Donec vitae ante at enim mollis egestas. Mauris convallis. Fusce convallis, nisl eu sagittis suscipit, risus ligula aliquam libero, in imperdiet neque mi non risus. Aenean dictum ultricies risus. Praesent ut ligula vitae purus ornare auctor. Cras tellus mauris, adipiscing ac, dignissim auctor, faucibus in, sem. Cras mauris libero, pharetra sit amet, lacinia eu, vehicula eleifend, sapien. Donec ac tellus. Sed eros dui, vulputate vel, auctor pharetra, tincidunt at, ipsum. Duis at dolor ac leo condimentum pulvinar. Donec molestie, dolor et fringilla elementum, nibh nibh iaculis orci, eu elementum odio turpis et odio. Phasellus fermentum, justo id placerat egestas, arcu nunc molestie ante, non imperdiet ligula lectus sed erat. Quisque sed ligula. Sed ac nulla. Nullam massa. "
            + "<br/>"
            + "Sed a purus. Mauris non nibh blandit neque cursus scelerisque. Quisque ultrices sem nec dolor. Donec non diam ut dui consequat venenatis. Nullam risus massa, egestas in, facilisis tristique, molestie sed, mi. Duis euismod turpis sit amet quam. Vestibulum ornare felis eget dolor. Phasellus ac urna vel sapien lacinia adipiscing. Donec egestas felis id mi. Sed erat. Vestibulum porta vulputate neque. Maecenas scelerisque, sem id sodales pretium, sem mauris rhoncus magna, at scelerisque tortor mauris nec dui. Nullam blandit rhoncus velit. Nam accumsan, enim id vestibulum feugiat, lorem nibh placerat urna, eget laoreet diam tortor at lorem. Suspendisse imperdiet consectetur dolor. ";
    private static final String[] iso3166 = new String[] { "AFGHANISTAN", "AF",
            "ÅLAND ISLANDS", "AX", "ALBANIA", "AL", "ALGERIA", "DZ",
            "AMERICAN SAMOA", "AS", "ANDORRA", "AD", "ANGOLA", "AO",
            "ANGUILLA", "AI", "ANTARCTICA", "AQ", "ANTIGUA AND BARBUDA", "AG",
            "ARGENTINA", "AR", "ARMENIA", "AM", "ARUBA", "AW", "AUSTRALIA",
            "AU", "AUSTRIA", "AT", "AZERBAIJAN", "AZ", "BAHAMAS", "BS",
            "BAHRAIN", "BH", "BANGLADESH", "BD", "BARBADOS", "BB", "BELARUS",
            "BY", "BELGIUM", "BE", "BELIZE", "BZ", "BENIN", "BJ", "BERMUDA",
            "BM", "BHUTAN", "BT", "BOLIVIA", "BO", "BOSNIA AND HERZEGOVINA",
            "BA", "BOTSWANA", "BW", "BOUVET ISLAND", "BV", "BRAZIL", "BR",
            "BRITISH INDIAN OCEAN TERRITORY", "IO", "BRUNEI DARUSSALAM", "BN",
            "BULGARIA", "BG", "BURKINA FASO", "BF", "BURUNDI", "BI",
            "CAMBODIA", "KH", "CAMEROON", "CM", "CANADA", "CA", "CAPE VERDE",
            "CV", "CAYMAN ISLANDS", "KY", "CENTRAL AFRICAN REPUBLIC", "CF",
            "CHAD", "TD", "CHILE", "CL", "CHINA", "CN", "CHRISTMAS ISLAND",
            "CX", "COCOS (KEELING) ISLANDS", "CC", "COLOMBIA", "CO", "COMOROS",
            "KM", "CONGO", "CG", "CONGO, THE DEMOCRATIC REPUBLIC OF THE", "CD",
            "COOK ISLANDS", "CK", "COSTA RICA", "CR", "CÔTE D'IVOIRE", "CI",
            "CROATIA", "HR", "CUBA", "CU", "CYPRUS", "CY", "CZECH REPUBLIC",
            "CZ", "DENMARK", "DK", "DJIBOUTI", "DJ", "DOMINICA", "DM",
            "DOMINICAN REPUBLIC", "DO", "ECUADOR", "EC", "EGYPT", "EG",
            "EL SALVADOR", "SV", "EQUATORIAL GUINEA", "GQ", "ERITREA", "ER",
            "ESTONIA", "EE", "ETHIOPIA", "ET", "FALKLAND ISLANDS (MALVINAS)",
            "FK", "FAROE ISLANDS", "FO", "FIJI", "FJ", "FINLAND", "FI",
            "FRANCE", "FR", "FRENCH GUIANA", "GF", "FRENCH POLYNESIA", "PF",
            "FRENCH SOUTHERN TERRITORIES", "TF", "GABON", "GA", "GAMBIA", "GM",
            "GEORGIA", "GE", "GERMANY", "DE", "GHANA", "GH", "GIBRALTAR", "GI",
            "GREECE", "GR", "GREENLAND", "GL", "GRENADA", "GD", "GUADELOUPE",
            "GP", "GUAM", "GU", "GUATEMALA", "GT", "GUERNSEY", "GG", "GUINEA",
            "GN", "GUINEA-BISSAU", "GW", "GUYANA", "GY", "HAITI", "HT",
            "HEARD ISLAND AND MCDONALD ISLANDS", "HM",
            "HOLY SEE (VATICAN CITY STATE)", "VA", "HONDURAS", "HN",
            "HONG KONG", "HK", "HUNGARY", "HU", "ICELAND", "IS", "INDIA", "IN",
            "INDONESIA", "ID", "IRAN, ISLAMIC REPUBLIC OF", "IR", "IRAQ", "IQ",
            "IRELAND", "IE", "ISLE OF MAN", "IM", "ISRAEL", "IL", "ITALY",
            "IT", "JAMAICA", "JM", "JAPAN", "JP", "JERSEY", "JE", "JORDAN",
            "JO", "KAZAKHSTAN", "KZ", "KENYA", "KE", "KIRIBATI", "KI",
            "KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF", "KP",
            "KOREA, REPUBLIC OF", "KR", "KUWAIT", "KW", "KYRGYZSTAN", "KG",
            "LAO PEOPLE'S DEMOCRATIC REPUBLIC", "LA", "LATVIA", "LV",
            "LEBANON", "LB", "LESOTHO", "LS", "LIBERIA", "LR",
            "LIBYAN ARAB JAMAHIRIYA", "LY", "LIECHTENSTEIN", "LI", "LITHUANIA",
            "LT", "LUXEMBOURG", "LU", "MACAO", "MO",
            "MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "MK", "MADAGASCAR",
            "MG", "MALAWI", "MW", "MALAYSIA", "MY", "MALDIVES", "MV", "MALI",
            "ML", "MALTA", "MT", "MARSHALL ISLANDS", "MH", "MARTINIQUE", "MQ",
            "MAURITANIA", "MR", "MAURITIUS", "MU", "MAYOTTE", "YT", "MEXICO",
            "MX", "MICRONESIA, FEDERATED STATES OF", "FM",
            "MOLDOVA, REPUBLIC OF", "MD", "MONACO", "MC", "MONGOLIA", "MN",
            "MONTENEGRO", "ME", "MONTSERRAT", "MS", "MOROCCO", "MA",
            "MOZAMBIQUE", "MZ", "MYANMAR", "MM", "NAMIBIA", "NA", "NAURU",
            "NR", "NEPAL", "NP", "NETHERLANDS", "NL", "NETHERLANDS ANTILLES",
            "AN", "NEW CALEDONIA", "NC", "NEW ZEALAND", "NZ", "NICARAGUA",
            "NI", "NIGER", "NE", "NIGERIA", "NG", "NIUE", "NU",
            "NORFOLK ISLAND", "NF", "NORTHERN MARIANA ISLANDS", "MP", "NORWAY",
            "NO", "OMAN", "OM", "PAKISTAN", "PK", "PALAU", "PW",
            "PALESTINIAN TERRITORY, OCCUPIED", "PS", "PANAMA", "PA",
            "PAPUA NEW GUINEA", "PG", "PARAGUAY", "PY", "PERU", "PE",
            "PHILIPPINES", "PH", "PITCAIRN", "PN", "POLAND", "PL", "PORTUGAL",
            "PT", "PUERTO RICO", "PR", "QATAR", "QA", "REUNION", "RE",
            "ROMANIA", "RO", "RUSSIAN FEDERATION", "RU", "RWANDA", "RW",
            "SAINT BARTHÉLEMY", "BL", "SAINT HELENA", "SH",
            "SAINT KITTS AND NEVIS", "KN", "SAINT LUCIA", "LC", "SAINT MARTIN",
            "MF", "SAINT PIERRE AND MIQUELON", "PM",
            "SAINT VINCENT AND THE GRENADINES", "VC", "SAMOA", "WS",
            "SAN MARINO", "SM", "SAO TOME AND PRINCIPE", "ST", "SAUDI ARABIA",
            "SA", "SENEGAL", "SN", "SERBIA", "RS", "SEYCHELLES", "SC",
            "SIERRA LEONE", "SL", "SINGAPORE", "SG", "SLOVAKIA", "SK",
            "SLOVENIA", "SI", "SOLOMON ISLANDS", "SB", "SOMALIA", "SO",
            "SOUTH AFRICA", "ZA",
            "SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS", "GS", "SPAIN",
            "ES", "SRI LANKA", "LK", "SUDAN", "SD", "SURINAME", "SR",
            "SVALBARD AND JAN MAYEN", "SJ", "SWAZILAND", "SZ", "SWEDEN", "SE",
            "SWITZERLAND", "CH", "SYRIAN ARAB REPUBLIC", "SY",
            "TAIWAN, PROVINCE OF CHINA", "TW", "TAJIKISTAN", "TJ",
            "TANZANIA, UNITED REPUBLIC OF", "TZ", "THAILAND", "TH",
            "TIMOR-LESTE", "TL", "TOGO", "TG", "TOKELAU", "TK", "TONGA", "TO",
            "TRINIDAD AND TOBAGO", "TT", "TUNISIA", "TN", "TURKEY", "TR",
            "TURKMENISTAN", "TM", "TURKS AND CAICOS ISLANDS", "TC", "TUVALU",
            "TV", "UGANDA", "UG", "UKRAINE", "UA", "UNITED ARAB EMIRATES",
            "AE", "UNITED KINGDOM", "GB", "UNITED STATES", "US",
            "UNITED STATES MINOR OUTLYING ISLANDS", "UM", "URUGUAY", "UY",
            "UZBEKISTAN", "UZ", "VANUATU", "VU", "VENEZUELA", "VE", "VIET NAM",
            "VN", "VIRGIN ISLANDS, BRITISH", "VG", "VIRGIN ISLANDS, U.S.",
            "VI", "WALLIS AND FUTUNA", "WF", "WESTERN SAHARA", "EH", "YEMEN",
            "YE", "ZAMBIA", "ZM", "ZIMBABWE", "ZW" };
    public static final Object iso3166_PROPERTY_NAME = "name";
    public static final Object iso3166_PROPERTY_SHORT = "short";
    public static final Object iso3166_PROPERTY_FLAG = "flag";
    public static final Object hw_PROPERTY_NAME = "name";

    public static final Object locale_PROPERTY_LOCALE = "locale";
    public static final Object locale_PROPERTY_NAME = "name";
    private static final String[][] locales = { { "fi", "FI", "Finnish" },
            { "de", "DE", "German" }, { "en", "US", "US - English" },
            { "sv", "SE", "Swedish" } };
    private static final String[][] hardware = { //
            { "Desktops", "Dell OptiPlex GX240", "Dell OptiPlex GX260",
                    "Dell OptiPlex GX280" },
            { "Monitors", "Benq T190HD", "Benq T220HD", "Benq T240HD" },
            { "Laptops", "IBM ThinkPad T40", "IBM ThinkPad T43",
                    "IBM ThinkPad T60" } };

    public static final Object PERSON_PROPERTY_FIRSTNAME = "First Name";
    public static final Object PERSON_PROPERTY_LASTNAME = "Last Name";
    private static final String[] firstnames = new String[] { "John", "Mary",
            "Joe", "Sarah", "Jeff", "Jane", "Peter", "Marc", "Robert", "Paula",
            "Lenny", "Kenny", "Nathan", "Nicole", "Laura", "Jos", "Josie",
            "Linus" };
    private static final String[] lastnames = new String[] { "Torvalds",
            "Smith", "Adams", "Black", "Wilson", "Richards", "Thompson",
            "McGoff", "Halas", "Jones", "Beck", "Sheridan", "Picard", "Hill",
            "Fielding", "Einstein" };

    public static IndexedContainer getPersonContainer() {
        IndexedContainer contactContainer = new IndexedContainer();
        contactContainer.addContainerProperty(PERSON_PROPERTY_FIRSTNAME,
                String.class, "");
        contactContainer.addContainerProperty(PERSON_PROPERTY_LASTNAME,
                String.class, "");
        for (int i = 0; i < 50;) {
            String fn = firstnames[(int) (Math.random() * firstnames.length)];
            String ln = lastnames[(int) (Math.random() * lastnames.length)];
            String id = fn + ln;
            Item item = contactContainer.addItem(id);
            if (item != null) {
                i++;
                item.getItemProperty(PERSON_PROPERTY_FIRSTNAME).setValue(fn);
                item.getItemProperty(PERSON_PROPERTY_LASTNAME).setValue(ln);
            }
        }
        return contactContainer;
    }

    public static IndexedContainer getLocaleContainer() {
        IndexedContainer localeContainer = new IndexedContainer();
        localeContainer.addContainerProperty(locale_PROPERTY_LOCALE,
                Locale.class, null);
        localeContainer.addContainerProperty(locale_PROPERTY_NAME,
                String.class, null);
        for (int i = 0; i < locales.length; i++) {
            String id = locales[i][2];
            Item item = localeContainer.addItem(id);
            item.getItemProperty(locale_PROPERTY_LOCALE).setValue(
                    new Locale(locales[i][0], locales[i][1]));
            item.getItemProperty(locale_PROPERTY_NAME).setValue(locales[i][2]);
        }

        return localeContainer;
    }

    @Deprecated
    public static IndexedContainer getStaticISO3166Container() {
        return getISO3166Container();
    }

    public static IndexedContainer getISO3166Container() {
        IndexedContainer c = new IndexedContainer();
        fillIso3166Container(c);
        return c;
    }

    private static void fillIso3166Container(IndexedContainer container) {
        container.addContainerProperty(iso3166_PROPERTY_NAME, String.class,
                null);
        container.addContainerProperty(iso3166_PROPERTY_SHORT, String.class,
                null);
        container.addContainerProperty(iso3166_PROPERTY_FLAG, Resource.class,
                null);
        for (int i = 0; i < iso3166.length; i++) {
            String name = iso3166[i++];
            String id = iso3166[i];
            Item item = container.addItem(id);
            item.getItemProperty(iso3166_PROPERTY_NAME).setValue(name);
            item.getItemProperty(iso3166_PROPERTY_SHORT).setValue(id);
            item.getItemProperty(iso3166_PROPERTY_FLAG).setValue(
                    new ThemeResource("flags/" + id.toLowerCase() + ".gif"));
        }
        container.sort(new Object[] { iso3166_PROPERTY_NAME },
                new boolean[] { true });
    }

    public static HierarchicalContainer getHardwareContainer() {
        Item item = null;
        int itemId = 0; // Increasing numbering for itemId:s

        // Create new container
        HierarchicalContainer hwContainer = new HierarchicalContainer();
        // Create containerproperty for name
        hwContainer.addContainerProperty(hw_PROPERTY_NAME, String.class, null);
        for (int i = 0; i < hardware.length; i++) {
            // Add new item
            item = hwContainer.addItem(itemId);
            // Add name property for item
            item.getItemProperty(hw_PROPERTY_NAME).setValue(hardware[i][0]);
            // Allow children
            hwContainer.setChildrenAllowed(itemId, true);
            itemId++;
            for (int j = 1; j < hardware[i].length; j++) {
                // Add child items
                item = hwContainer.addItem(itemId);
                item.getItemProperty(hw_PROPERTY_NAME).setValue(hardware[i][j]);
                hwContainer.setParent(itemId, itemId - j);
                hwContainer.setChildrenAllowed(itemId, false);
                itemId++;
            }
        }
        return hwContainer;
    }

    public static void fillContainerWithEmailAddresses(Container c, int amount) {
        for (int i = 0; i < amount; i++) {
            // TODO
        }
    }

}
