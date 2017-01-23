package com.vaadin.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.shared.ui.ui.UIState.LocaleData;
import com.vaadin.shared.ui.ui.UIState.LocaleServiceState;
import com.vaadin.ui.UI;

public class LocaleServiceTest {

    private static Set<String> JAVA8_SUPPORTED_LOCALES = new HashSet<String>();
    static {
        // From
        // http://www.oracle.com/technetwork/java/javase/java8locales-2095355.html
        JAVA8_SUPPORTED_LOCALES.add("sq-AL");
        JAVA8_SUPPORTED_LOCALES.add("ar-DZ");
        JAVA8_SUPPORTED_LOCALES.add("ar-BH");
        JAVA8_SUPPORTED_LOCALES.add("ar-EG");
        JAVA8_SUPPORTED_LOCALES.add("ar-IQ");
        JAVA8_SUPPORTED_LOCALES.add("ar-JO");
        JAVA8_SUPPORTED_LOCALES.add("ar-KW");
        JAVA8_SUPPORTED_LOCALES.add("ar-LB");
        JAVA8_SUPPORTED_LOCALES.add("ar-LY");
        JAVA8_SUPPORTED_LOCALES.add("ar-MA");
        JAVA8_SUPPORTED_LOCALES.add("ar-OM");
        JAVA8_SUPPORTED_LOCALES.add("ar-QA");
        JAVA8_SUPPORTED_LOCALES.add("ar-SA");
        JAVA8_SUPPORTED_LOCALES.add("ar-SD");
        JAVA8_SUPPORTED_LOCALES.add("ar-SY");
        JAVA8_SUPPORTED_LOCALES.add("ar-TN");
        JAVA8_SUPPORTED_LOCALES.add("ar-AE");
        JAVA8_SUPPORTED_LOCALES.add("ar-YE");
        JAVA8_SUPPORTED_LOCALES.add("be-BY");
        JAVA8_SUPPORTED_LOCALES.add("bg-BG");
        JAVA8_SUPPORTED_LOCALES.add("ca-ES");
        JAVA8_SUPPORTED_LOCALES.add("zh-CN");
        JAVA8_SUPPORTED_LOCALES.add("zh-SG");
        JAVA8_SUPPORTED_LOCALES.add("zh-HK");
        JAVA8_SUPPORTED_LOCALES.add("zh-TW");
        JAVA8_SUPPORTED_LOCALES.add("hr-HR");
        JAVA8_SUPPORTED_LOCALES.add("cs-CZ");
        JAVA8_SUPPORTED_LOCALES.add("da-DK");
        JAVA8_SUPPORTED_LOCALES.add("nl-BE");
        JAVA8_SUPPORTED_LOCALES.add("nl-NL");
        JAVA8_SUPPORTED_LOCALES.add("en-AU");
        JAVA8_SUPPORTED_LOCALES.add("en-CA");
        JAVA8_SUPPORTED_LOCALES.add("en-IN");
        JAVA8_SUPPORTED_LOCALES.add("en-IE");
        JAVA8_SUPPORTED_LOCALES.add("en-MT");
        JAVA8_SUPPORTED_LOCALES.add("en-NZ");
        JAVA8_SUPPORTED_LOCALES.add("en-PH");
        JAVA8_SUPPORTED_LOCALES.add("en-SG");
        JAVA8_SUPPORTED_LOCALES.add("en-ZA");
        JAVA8_SUPPORTED_LOCALES.add("en-GB");
        JAVA8_SUPPORTED_LOCALES.add("en-US");
        JAVA8_SUPPORTED_LOCALES.add("et-EE");
        JAVA8_SUPPORTED_LOCALES.add("fi-FI");
        JAVA8_SUPPORTED_LOCALES.add("fr-BE");
        JAVA8_SUPPORTED_LOCALES.add("fr-CA");
        JAVA8_SUPPORTED_LOCALES.add("fr-FR");
        JAVA8_SUPPORTED_LOCALES.add("fr-LU");
        JAVA8_SUPPORTED_LOCALES.add("fr-CH");
        JAVA8_SUPPORTED_LOCALES.add("de-AT");
        JAVA8_SUPPORTED_LOCALES.add("de-DE");
        JAVA8_SUPPORTED_LOCALES.add("de-LU");
        JAVA8_SUPPORTED_LOCALES.add("de-CH");
        JAVA8_SUPPORTED_LOCALES.add("el-CY");
        JAVA8_SUPPORTED_LOCALES.add("el-GR");
        JAVA8_SUPPORTED_LOCALES.add("iw-IL");
        JAVA8_SUPPORTED_LOCALES.add("hi-IN");
        JAVA8_SUPPORTED_LOCALES.add("hu-HU");
        JAVA8_SUPPORTED_LOCALES.add("is-IS");
        JAVA8_SUPPORTED_LOCALES.add("in-ID");
        JAVA8_SUPPORTED_LOCALES.add("ga-IE");
        JAVA8_SUPPORTED_LOCALES.add("it-IT");
        JAVA8_SUPPORTED_LOCALES.add("it-CH");
        JAVA8_SUPPORTED_LOCALES.add("ja-JP");
        JAVA8_SUPPORTED_LOCALES.add("ja-JP-u-ca-japanese");
        JAVA8_SUPPORTED_LOCALES.add("ja-JP-x-lvariant-JP");
        JAVA8_SUPPORTED_LOCALES.add("ko-KR");
        JAVA8_SUPPORTED_LOCALES.add("lv-LV");
        JAVA8_SUPPORTED_LOCALES.add("lt-LT");
        JAVA8_SUPPORTED_LOCALES.add("mk-MK");
        JAVA8_SUPPORTED_LOCALES.add("ms-MY");
        JAVA8_SUPPORTED_LOCALES.add("mt-MT");
        JAVA8_SUPPORTED_LOCALES.add("no-NO");
        JAVA8_SUPPORTED_LOCALES.add("nb-NO");
        JAVA8_SUPPORTED_LOCALES.add("nn-NO");
        JAVA8_SUPPORTED_LOCALES.add("no-NO-x-lvariant-NY");
        JAVA8_SUPPORTED_LOCALES.add("pl-PL");
        JAVA8_SUPPORTED_LOCALES.add("pt-BR");
        JAVA8_SUPPORTED_LOCALES.add("pt-PT");
        JAVA8_SUPPORTED_LOCALES.add("ro-RO");
        JAVA8_SUPPORTED_LOCALES.add("ru-RU");
        JAVA8_SUPPORTED_LOCALES.add("sr-BA");
        JAVA8_SUPPORTED_LOCALES.add("sr-ME");
        JAVA8_SUPPORTED_LOCALES.add("sr-RS");
        JAVA8_SUPPORTED_LOCALES.add("sr-Latn-BA");
        JAVA8_SUPPORTED_LOCALES.add("sr-Latn-ME");
        JAVA8_SUPPORTED_LOCALES.add("sr-Latn-RS");
        JAVA8_SUPPORTED_LOCALES.add("sk-SK");
        JAVA8_SUPPORTED_LOCALES.add("sl-SI");
        JAVA8_SUPPORTED_LOCALES.add("es-AR");
        JAVA8_SUPPORTED_LOCALES.add("es-BO");
        JAVA8_SUPPORTED_LOCALES.add("es-CL");
        JAVA8_SUPPORTED_LOCALES.add("es-CO");
        JAVA8_SUPPORTED_LOCALES.add("es-CR");
        JAVA8_SUPPORTED_LOCALES.add("es-DO");
        JAVA8_SUPPORTED_LOCALES.add("es-EC");
        JAVA8_SUPPORTED_LOCALES.add("es-SV");
        JAVA8_SUPPORTED_LOCALES.add("es-GT");
        JAVA8_SUPPORTED_LOCALES.add("es-HN");
        JAVA8_SUPPORTED_LOCALES.add("es-MX");
        JAVA8_SUPPORTED_LOCALES.add("es-NI");
        JAVA8_SUPPORTED_LOCALES.add("es-PA");
        JAVA8_SUPPORTED_LOCALES.add("es-PY");
        JAVA8_SUPPORTED_LOCALES.add("es-PE");
        JAVA8_SUPPORTED_LOCALES.add("es-PR");
        JAVA8_SUPPORTED_LOCALES.add("es-ES");
        JAVA8_SUPPORTED_LOCALES.add("es-US");
        JAVA8_SUPPORTED_LOCALES.add("es-UY");
        JAVA8_SUPPORTED_LOCALES.add("es-VE");
        JAVA8_SUPPORTED_LOCALES.add("sv-SE");
        JAVA8_SUPPORTED_LOCALES.add("th-TH");
        JAVA8_SUPPORTED_LOCALES.add("th-TH-u-ca-buddhist");
        JAVA8_SUPPORTED_LOCALES.add("th-TH-u-ca-buddhist-nu-thai");
        JAVA8_SUPPORTED_LOCALES.add("th-TH-x-lvariant-TH");
        JAVA8_SUPPORTED_LOCALES.add("tr-TR");
        JAVA8_SUPPORTED_LOCALES.add("uk-UA");
        JAVA8_SUPPORTED_LOCALES.add("vi-VN");
    }
    private static Map<Locale, LocaleData> expectedLocaleData = new HashMap<Locale, LocaleData>();

    static {

        LocaleData en_IE = new LocaleData();
        en_IE.dateFormat = "dd/MM/yy";
        en_IE.twelveHourClock = false;
        en_IE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("en", "IE"), en_IE);

        LocaleData ar_KW = new LocaleData();
        ar_KW.dateFormat = "dd/MM/yy";
        ar_KW.twelveHourClock = true;
        ar_KW.hourMinuteDelimiter = ":";
        ar_KW.am = "ص";
        ar_KW.pm = "م";
        expectedLocaleData.put(new Locale("ar", "KW"), ar_KW);

        LocaleData ms_MY = new LocaleData();
        ms_MY.dateFormat = "dd/MM/yyyy";
        ms_MY.twelveHourClock = false;
        ms_MY.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ms", "MY"), ms_MY);

        LocaleData en_IN = new LocaleData();
        en_IN.dateFormat = "d/M/yy";
        en_IN.twelveHourClock = true;
        en_IN.hourMinuteDelimiter = ":";
        en_IN.am = "AM";
        en_IN.pm = "PM";
        expectedLocaleData.put(new Locale("en", "IN"), en_IN);

        LocaleData es_BO = new LocaleData();
        es_BO.dateFormat = "dd-MM-yy";
        es_BO.twelveHourClock = true;
        es_BO.hourMinuteDelimiter = ":";
        es_BO.am = "AM";
        es_BO.pm = "PM";
        expectedLocaleData.put(new Locale("es", "BO"), es_BO);

        LocaleData ar_SY = new LocaleData();
        ar_SY.dateFormat = "dd/MM/yy";
        ar_SY.twelveHourClock = true;
        ar_SY.hourMinuteDelimiter = ":";
        ar_SY.am = "ص";
        ar_SY.pm = "م";
        expectedLocaleData.put(new Locale("ar", "SY"), ar_SY);

        LocaleData en_ZA = new LocaleData();
        en_ZA.dateFormat = "yyyy/MM/dd";
        en_ZA.twelveHourClock = true;
        en_ZA.hourMinuteDelimiter = ":";
        en_ZA.am = "AM";
        en_ZA.pm = "PM";
        expectedLocaleData.put(new Locale("en", "ZA"), en_ZA);

        LocaleData no_NO = new LocaleData();
        no_NO.dateFormat = "dd.MM.yy";
        no_NO.twelveHourClock = false;
        no_NO.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("no", "NO"), no_NO);

        LocaleData sr_LATN_BA = new LocaleData();
        sr_LATN_BA.dateFormat = "d.M.yy.";
        sr_LATN_BA.twelveHourClock = false;
        sr_LATN_BA.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("sr", "LATN", "BA"), sr_LATN_BA);

        LocaleData el_GR = new LocaleData();
        el_GR.dateFormat = "d/M/yyyy";
        el_GR.twelveHourClock = true;
        el_GR.hourMinuteDelimiter = ":";
        el_GR.am = "πμ";
        el_GR.pm = "μμ";
        expectedLocaleData.put(new Locale("el", "GR"), el_GR);

        LocaleData sr_LATN_RS = new LocaleData();
        sr_LATN_RS.dateFormat = "d.M.yy.";
        sr_LATN_RS.twelveHourClock = false;
        sr_LATN_RS.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("sr", "LATN", "RS"), sr_LATN_RS);

        LocaleData nl_NL = new LocaleData();
        nl_NL.dateFormat = "d-M-yy";
        nl_NL.twelveHourClock = false;
        nl_NL.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("nl", "NL"), nl_NL);

        LocaleData ar_LB = new LocaleData();
        ar_LB.dateFormat = "dd/MM/yy";
        ar_LB.twelveHourClock = true;
        ar_LB.hourMinuteDelimiter = ":";
        ar_LB.am = "ص";
        ar_LB.pm = "م";
        expectedLocaleData.put(new Locale("ar", "LB"), ar_LB);

        LocaleData en_AU = new LocaleData();
        en_AU.dateFormat = "d/MM/yy";
        en_AU.twelveHourClock = true;
        en_AU.hourMinuteDelimiter = ":";
        en_AU.am = "AM";
        en_AU.pm = "PM";
        expectedLocaleData.put(new Locale("en", "AU"), en_AU);

        LocaleData mk_MK = new LocaleData();
        mk_MK.dateFormat = "d.M.yy";
        mk_MK.twelveHourClock = false;
        mk_MK.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("mk", "MK"), mk_MK);

        LocaleData ar_TN = new LocaleData();
        ar_TN.dateFormat = "dd/MM/yy";
        ar_TN.twelveHourClock = true;
        ar_TN.hourMinuteDelimiter = ":";
        ar_TN.am = "ص";
        ar_TN.pm = "م";
        expectedLocaleData.put(new Locale("ar", "TN"), ar_TN);

        LocaleData ar_LY = new LocaleData();
        ar_LY.dateFormat = "dd/MM/yy";
        ar_LY.twelveHourClock = true;
        ar_LY.hourMinuteDelimiter = ":";
        ar_LY.am = "ص";
        ar_LY.pm = "م";
        expectedLocaleData.put(new Locale("ar", "LY"), ar_LY);

        LocaleData hu_HU = new LocaleData();
        hu_HU.dateFormat = "yyyy.MM.dd.";
        hu_HU.twelveHourClock = false;
        hu_HU.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("hu", "HU"), hu_HU);

        LocaleData es_SV = new LocaleData();
        es_SV.dateFormat = "MM-dd-yy";
        es_SV.twelveHourClock = true;
        es_SV.hourMinuteDelimiter = ":";
        es_SV.am = "AM";
        es_SV.pm = "PM";
        expectedLocaleData.put(new Locale("es", "SV"), es_SV);

        LocaleData es_CR = new LocaleData();
        es_CR.dateFormat = "dd/MM/yy";
        es_CR.twelveHourClock = true;
        es_CR.hourMinuteDelimiter = ":";
        es_CR.am = "AM";
        es_CR.pm = "PM";
        expectedLocaleData.put(new Locale("es", "CR"), es_CR);

        LocaleData es_CL = new LocaleData();
        es_CL.dateFormat = "dd-MM-yy";
        es_CL.twelveHourClock = false;
        es_CL.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("es", "CL"), es_CL);

        LocaleData fr_CA = new LocaleData();
        fr_CA.dateFormat = "yy-MM-dd";
        fr_CA.twelveHourClock = false;
        fr_CA.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fr", "CA"), fr_CA);

        LocaleData es_CO = new LocaleData();
        es_CO.dateFormat = "d/MM/yy";
        es_CO.twelveHourClock = true;
        es_CO.hourMinuteDelimiter = ":";
        es_CO.am = "AM";
        es_CO.pm = "PM";
        expectedLocaleData.put(new Locale("es", "CO"), es_CO);

        LocaleData pl_PL = new LocaleData();
        pl_PL.dateFormat = "dd.MM.yy";
        pl_PL.twelveHourClock = false;
        pl_PL.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("pl", "PL"), pl_PL);

        LocaleData pt_PT = new LocaleData();
        pt_PT.dateFormat = "dd-MM-yyyy";
        pt_PT.twelveHourClock = false;
        pt_PT.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("pt", "PT"), pt_PT);

        LocaleData ar_EG = new LocaleData();
        ar_EG.dateFormat = "dd/MM/yy";
        ar_EG.twelveHourClock = true;
        ar_EG.hourMinuteDelimiter = ":";
        ar_EG.am = "ص";
        ar_EG.pm = "م";
        expectedLocaleData.put(new Locale("ar", "EG"), ar_EG);

        LocaleData fr_BE = new LocaleData();
        fr_BE.dateFormat = "d/MM/yy";
        fr_BE.twelveHourClock = false;
        fr_BE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fr", "BE"), fr_BE);

        LocaleData ga_IE = new LocaleData();
        ga_IE.dateFormat = "dd/MM/yyyy";
        ga_IE.twelveHourClock = false;
        ga_IE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ga", "IE"), ga_IE);

        LocaleData ar_DZ = new LocaleData();
        ar_DZ.dateFormat = "dd/MM/yy";
        ar_DZ.twelveHourClock = true;
        ar_DZ.hourMinuteDelimiter = ":";
        ar_DZ.am = "ص";
        ar_DZ.pm = "م";
        expectedLocaleData.put(new Locale("ar", "DZ"), ar_DZ);

        LocaleData en_SG = new LocaleData();
        en_SG.dateFormat = "d/M/yy";
        en_SG.twelveHourClock = true;
        en_SG.hourMinuteDelimiter = ":";
        en_SG.am = "AM";
        en_SG.pm = "PM";
        expectedLocaleData.put(new Locale("en", "SG"), en_SG);

        LocaleData in_ID = new LocaleData();
        in_ID.dateFormat = "dd/MM/yy";
        in_ID.twelveHourClock = false;
        in_ID.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("in", "ID"), in_ID);

        LocaleData ar_MA = new LocaleData();
        ar_MA.dateFormat = "dd/MM/yy";
        ar_MA.twelveHourClock = true;
        ar_MA.hourMinuteDelimiter = ":";
        ar_MA.am = "ص";
        ar_MA.pm = "م";
        expectedLocaleData.put(new Locale("ar", "MA"), ar_MA);

        LocaleData th_TH_u_ca_buddhist_nu_thai = new LocaleData();
        th_TH_u_ca_buddhist_nu_thai.dateFormat = "d/M/yyyy";
        th_TH_u_ca_buddhist_nu_thai.twelveHourClock = false;
        th_TH_u_ca_buddhist_nu_thai.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("th", "TH", "u-ca-buddhist-nu-thai"),
                th_TH_u_ca_buddhist_nu_thai);

        LocaleData nb_NO = new LocaleData();
        nb_NO.dateFormat = "dd.MM.yy";
        nb_NO.twelveHourClock = false;
        nb_NO.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("nb", "NO"), nb_NO);

        LocaleData es_HN = new LocaleData();
        es_HN.dateFormat = "MM-dd-yy";
        es_HN.twelveHourClock = true;
        es_HN.hourMinuteDelimiter = ":";
        es_HN.am = "AM";
        es_HN.pm = "PM";
        expectedLocaleData.put(new Locale("es", "HN"), es_HN);

        LocaleData hr_HR = new LocaleData();
        hr_HR.dateFormat = "dd.MM.yy.";
        hr_HR.twelveHourClock = false;
        hr_HR.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("hr", "HR"), hr_HR);

        LocaleData es_PR = new LocaleData();
        es_PR.dateFormat = "MM-dd-yy";
        es_PR.twelveHourClock = true;
        es_PR.hourMinuteDelimiter = ":";
        es_PR.am = "AM";
        es_PR.pm = "PM";
        expectedLocaleData.put(new Locale("es", "PR"), es_PR);

        LocaleData es_PY = new LocaleData();
        es_PY.dateFormat = "dd/MM/yy";
        es_PY.twelveHourClock = true;
        es_PY.hourMinuteDelimiter = ":";
        es_PY.am = "AM";
        es_PY.pm = "PM";
        expectedLocaleData.put(new Locale("es", "PY"), es_PY);

        LocaleData sr_ME = new LocaleData();
        sr_ME.dateFormat = "d.M.yy.";
        sr_ME.twelveHourClock = false;
        sr_ME.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("sr", "ME"), sr_ME);

        LocaleData de_AT = new LocaleData();
        de_AT.dateFormat = "dd.MM.yy";
        de_AT.twelveHourClock = false;
        de_AT.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("de", "AT"), de_AT);

        LocaleData is_IS = new LocaleData();
        is_IS.dateFormat = "d.M.yyyy";
        is_IS.twelveHourClock = false;
        is_IS.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("is", "IS"), is_IS);

        LocaleData bg_BG = new LocaleData();
        bg_BG.dateFormat = "dd.MM.yy";
        bg_BG.twelveHourClock = false;
        bg_BG.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("bg", "BG"), bg_BG);

        LocaleData cs_CZ = new LocaleData();
        cs_CZ.dateFormat = "d.M.yy";
        cs_CZ.twelveHourClock = false;
        cs_CZ.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("cs", "CZ"), cs_CZ);

        LocaleData en_PH = new LocaleData();
        en_PH.dateFormat = "M/d/yy";
        en_PH.twelveHourClock = true;
        en_PH.hourMinuteDelimiter = ":";
        en_PH.am = "AM";
        en_PH.pm = "PM";
        expectedLocaleData.put(new Locale("en", "PH"), en_PH);

        LocaleData zh_TW = new LocaleData();
        zh_TW.dateFormat = "yyyy/M/d";
        zh_TW.twelveHourClock = true;
        zh_TW.hourMinuteDelimiter = ":";
        zh_TW.am = "上午";
        zh_TW.pm = "下午";
        expectedLocaleData.put(new Locale("zh", "TW"), zh_TW);

        LocaleData ko_KR = new LocaleData();
        ko_KR.dateFormat = "yy. M. d";
        ko_KR.twelveHourClock = true;
        ko_KR.hourMinuteDelimiter = ":";
        ko_KR.am = "오전";
        ko_KR.pm = "오후";
        expectedLocaleData.put(new Locale("ko", "KR"), ko_KR);

        LocaleData sk_SK = new LocaleData();
        sk_SK.dateFormat = "d.M.yyyy";
        sk_SK.twelveHourClock = false;
        sk_SK.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("sk", "SK"), sk_SK);

        LocaleData sr_LATN_ME = new LocaleData();
        sr_LATN_ME.dateFormat = "d.M.yy.";
        sr_LATN_ME.twelveHourClock = false;
        sr_LATN_ME.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("sr", "LATN", "ME"), sr_LATN_ME);

        LocaleData ar_OM = new LocaleData();
        ar_OM.dateFormat = "dd/MM/yy";
        ar_OM.twelveHourClock = true;
        ar_OM.hourMinuteDelimiter = ":";
        ar_OM.am = "ص";
        ar_OM.pm = "م";
        expectedLocaleData.put(new Locale("ar", "OM"), ar_OM);

        LocaleData ru_RU = new LocaleData();
        ru_RU.dateFormat = "dd.MM.yy";
        ru_RU.twelveHourClock = false;
        ru_RU.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ru", "RU"), ru_RU);

        LocaleData sq_AL = new LocaleData();
        sq_AL.dateFormat = "yy-MM-dd";
        sq_AL.twelveHourClock = true;
        sq_AL.hourMinuteDelimiter = ".";
        sq_AL.am = "PD";
        sq_AL.pm = "MD";
        expectedLocaleData.put(new Locale("sq", "AL"), sq_AL);

        LocaleData es_AR = new LocaleData();
        es_AR.dateFormat = "dd/MM/yy";
        es_AR.twelveHourClock = false;
        es_AR.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("es", "AR"), es_AR);

        LocaleData sv_SE = new LocaleData();
        sv_SE.dateFormat = "yyyy-MM-dd";
        sv_SE.twelveHourClock = false;
        sv_SE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("sv", "SE"), sv_SE);

        LocaleData ja_JP_x_lvariant_JP = new LocaleData();
        ja_JP_x_lvariant_JP.dateFormat = "yy/MM/dd";
        ja_JP_x_lvariant_JP.twelveHourClock = false;
        ja_JP_x_lvariant_JP.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ja", "JP", "x-lvariant-JP"),
                ja_JP_x_lvariant_JP);

        LocaleData da_DK = new LocaleData();
        da_DK.dateFormat = "dd-MM-yy";
        da_DK.twelveHourClock = false;
        da_DK.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("da", "DK"), da_DK);

        LocaleData uk_UA = new LocaleData();
        uk_UA.dateFormat = "dd.MM.yy";
        uk_UA.twelveHourClock = false;
        uk_UA.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("uk", "UA"), uk_UA);

        LocaleData th_TH_u_ca_buddhist = new LocaleData();
        th_TH_u_ca_buddhist.dateFormat = "d/M/yyyy";
        th_TH_u_ca_buddhist.twelveHourClock = false;
        th_TH_u_ca_buddhist.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("th", "TH", "u-ca-buddhist"),
                th_TH_u_ca_buddhist);

        LocaleData en_US = new LocaleData();
        en_US.dateFormat = "M/d/yy";
        en_US.twelveHourClock = true;
        en_US.hourMinuteDelimiter = ":";
        en_US.am = "AM";
        en_US.pm = "PM";
        expectedLocaleData.put(new Locale("en", "US"), en_US);

        LocaleData lv_LV = new LocaleData();
        lv_LV.dateFormat = "yy.d.M";
        lv_LV.twelveHourClock = false;
        lv_LV.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("lv", "LV"), lv_LV);

        LocaleData ja_JP_u_ca_japanese = new LocaleData();
        ja_JP_u_ca_japanese.dateFormat = "yy/MM/dd";
        ja_JP_u_ca_japanese.twelveHourClock = false;
        ja_JP_u_ca_japanese.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ja", "JP", "u-ca-japanese"),
                ja_JP_u_ca_japanese);

        LocaleData en_MT = new LocaleData();
        en_MT.dateFormat = "dd/MM/yyyy";
        en_MT.twelveHourClock = false;
        en_MT.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("en", "MT"), en_MT);

        LocaleData zh_CN = new LocaleData();
        zh_CN.dateFormat = "yy-M-d";
        zh_CN.twelveHourClock = true;
        zh_CN.hourMinuteDelimiter = ":";
        zh_CN.am = "上午";
        zh_CN.pm = "下午";
        expectedLocaleData.put(new Locale("zh", "CN"), zh_CN);

        LocaleData nl_BE = new LocaleData();
        nl_BE.dateFormat = "d/MM/yy";
        nl_BE.twelveHourClock = false;
        nl_BE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("nl", "BE"), nl_BE);

        LocaleData hi_IN = new LocaleData();
        hi_IN.dateFormat = "d/M/yy";
        hi_IN.twelveHourClock = true;
        hi_IN.hourMinuteDelimiter = ":";
        hi_IN.am = "पूर्वाह्न";
        hi_IN.pm = "अपराह्न";
        expectedLocaleData.put(new Locale("hi", "IN"), hi_IN);

        LocaleData el_CY = new LocaleData();
        el_CY.dateFormat = "dd/MM/yyyy";
        el_CY.twelveHourClock = true;
        el_CY.hourMinuteDelimiter = ":";
        el_CY.am = "ΠΜ";
        el_CY.pm = "ΜΜ";
        expectedLocaleData.put(new Locale("el", "CY"), el_CY);

        LocaleData de_CH = new LocaleData();
        de_CH.dateFormat = "dd.MM.yy";
        de_CH.twelveHourClock = false;
        de_CH.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("de", "CH"), de_CH);

        LocaleData ja_JP = new LocaleData();
        ja_JP.dateFormat = "yy/MM/dd";
        ja_JP.twelveHourClock = false;
        ja_JP.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ja", "JP"), ja_JP);

        LocaleData ar_YE = new LocaleData();
        ar_YE.dateFormat = "dd/MM/yy";
        ar_YE.twelveHourClock = true;
        ar_YE.hourMinuteDelimiter = ":";
        ar_YE.am = "ص";
        ar_YE.pm = "م";
        expectedLocaleData.put(new Locale("ar", "YE"), ar_YE);

        LocaleData ar_QA = new LocaleData();
        ar_QA.dateFormat = "dd/MM/yy";
        ar_QA.twelveHourClock = true;
        ar_QA.hourMinuteDelimiter = ":";
        ar_QA.am = "ص";
        ar_QA.pm = "م";
        expectedLocaleData.put(new Locale("ar", "QA"), ar_QA);

        LocaleData es_GT = new LocaleData();
        es_GT.dateFormat = "d/MM/yy";
        es_GT.twelveHourClock = true;
        es_GT.hourMinuteDelimiter = ":";
        es_GT.am = "AM";
        es_GT.pm = "PM";
        expectedLocaleData.put(new Locale("es", "GT"), es_GT);

        LocaleData nn_NO = new LocaleData();
        nn_NO.dateFormat = "dd.MM.yy";
        nn_NO.twelveHourClock = false;
        nn_NO.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("nn", "NO"), nn_NO);

        LocaleData es_PE = new LocaleData();
        es_PE.dateFormat = "dd/MM/yy";
        es_PE.twelveHourClock = true;
        es_PE.hourMinuteDelimiter = ":";
        es_PE.am = "AM";
        es_PE.pm = "PM";
        expectedLocaleData.put(new Locale("es", "PE"), es_PE);

        LocaleData en_NZ = new LocaleData();
        en_NZ.dateFormat = "d/MM/yy";
        en_NZ.twelveHourClock = true;
        en_NZ.hourMinuteDelimiter = ":";
        en_NZ.am = "AM";
        en_NZ.pm = "PM";
        expectedLocaleData.put(new Locale("en", "NZ"), en_NZ);

        LocaleData be_BY = new LocaleData();
        be_BY.dateFormat = "d.M.yy";
        be_BY.twelveHourClock = false;
        be_BY.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("be", "BY"), be_BY);

        LocaleData zh_SG = new LocaleData();
        zh_SG.dateFormat = "dd/MM/yy";
        zh_SG.twelveHourClock = true;
        zh_SG.hourMinuteDelimiter = ":";
        zh_SG.am = "上午";
        zh_SG.pm = "下午";
        expectedLocaleData.put(new Locale("zh", "SG"), zh_SG);

        LocaleData ro_RO = new LocaleData();
        ro_RO.dateFormat = "dd.MM.yyyy";
        ro_RO.twelveHourClock = false;
        ro_RO.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ro", "RO"), ro_RO);

        LocaleData es_PA = new LocaleData();
        es_PA.dateFormat = "MM/dd/yy";
        es_PA.twelveHourClock = true;
        es_PA.hourMinuteDelimiter = ":";
        es_PA.am = "AM";
        es_PA.pm = "PM";
        expectedLocaleData.put(new Locale("es", "PA"), es_PA);

        LocaleData mt_MT = new LocaleData();
        mt_MT.dateFormat = "dd/MM/yyyy";
        mt_MT.twelveHourClock = false;
        mt_MT.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("mt", "MT"), mt_MT);

        LocaleData et_EE = new LocaleData();
        et_EE.dateFormat = "d.MM.yy";
        et_EE.twelveHourClock = false;
        et_EE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("et", "EE"), et_EE);

        LocaleData it_CH = new LocaleData();
        it_CH.dateFormat = "dd.MM.yy";
        it_CH.twelveHourClock = false;
        it_CH.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("it", "CH"), it_CH);

        LocaleData th_TH_x_lvariant_TH = new LocaleData();
        th_TH_x_lvariant_TH.dateFormat = "d/M/yyyy";
        th_TH_x_lvariant_TH.twelveHourClock = false;
        th_TH_x_lvariant_TH.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("th", "TH", "x-lvariant-TH"),
                th_TH_x_lvariant_TH);

        LocaleData tr_TR = new LocaleData();
        tr_TR.dateFormat = "dd.MM.yyyy";
        tr_TR.twelveHourClock = false;
        tr_TR.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("tr", "TR"), tr_TR);

        LocaleData fr_FR = new LocaleData();
        fr_FR.dateFormat = "dd/MM/yy";
        fr_FR.twelveHourClock = false;
        fr_FR.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fr", "FR"), fr_FR);

        LocaleData vi_VN = new LocaleData();
        vi_VN.dateFormat = "dd/MM/yyyy";
        vi_VN.twelveHourClock = false;
        vi_VN.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("vi", "VN"), vi_VN);

        LocaleData en_GB = new LocaleData();
        en_GB.dateFormat = "dd/MM/yy";
        en_GB.twelveHourClock = false;
        en_GB.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("en", "GB"), en_GB);

        LocaleData fi_FI = new LocaleData();
        fi_FI.dateFormat = "d.M.yyyy";
        fi_FI.twelveHourClock = false;
        fi_FI.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fi", "FI"), fi_FI);

        LocaleData en_CA = new LocaleData();
        en_CA.dateFormat = "dd/MM/yy";
        en_CA.twelveHourClock = true;
        en_CA.hourMinuteDelimiter = ":";
        en_CA.am = "AM";
        en_CA.pm = "PM";
        expectedLocaleData.put(new Locale("en", "CA"), en_CA);

        LocaleData lt_LT = new LocaleData();
        lt_LT.dateFormat = "yy.M.d";
        lt_LT.twelveHourClock = false;
        lt_LT.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("lt", "LT"), lt_LT);

        LocaleData ar_AE = new LocaleData();
        ar_AE.dateFormat = "dd/MM/yy";
        ar_AE.twelveHourClock = true;
        ar_AE.hourMinuteDelimiter = ":";
        ar_AE.am = "ص";
        ar_AE.pm = "م";
        expectedLocaleData.put(new Locale("ar", "AE"), ar_AE);

        LocaleData sl_SI = new LocaleData();
        sl_SI.dateFormat = "d.M.y";
        sl_SI.twelveHourClock = false;
        sl_SI.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("sl", "SI"), sl_SI);

        LocaleData es_DO = new LocaleData();
        es_DO.dateFormat = "dd/MM/yy";
        es_DO.twelveHourClock = true;
        es_DO.hourMinuteDelimiter = ":";
        es_DO.am = "AM";
        es_DO.pm = "PM";
        expectedLocaleData.put(new Locale("es", "DO"), es_DO);

        LocaleData ar_IQ = new LocaleData();
        ar_IQ.dateFormat = "dd/MM/yy";
        ar_IQ.twelveHourClock = true;
        ar_IQ.hourMinuteDelimiter = ":";
        ar_IQ.am = "ص";
        ar_IQ.pm = "م";
        expectedLocaleData.put(new Locale("ar", "IQ"), ar_IQ);

        LocaleData fr_CH = new LocaleData();
        fr_CH.dateFormat = "dd.MM.yy";
        fr_CH.twelveHourClock = false;
        fr_CH.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fr", "CH"), fr_CH);

        LocaleData es_EC = new LocaleData();
        es_EC.dateFormat = "dd/MM/yy";
        es_EC.twelveHourClock = false;
        es_EC.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("es", "EC"), es_EC);

        LocaleData es_US = new LocaleData();
        es_US.dateFormat = "M/d/yy";
        es_US.twelveHourClock = true;
        es_US.hourMinuteDelimiter = ":";
        es_US.am = "a.m.";
        es_US.pm = "p.m.";
        expectedLocaleData.put(new Locale("es", "US"), es_US);

        LocaleData iw_IL = new LocaleData();
        iw_IL.dateFormat = "dd/MM/yy";
        iw_IL.twelveHourClock = false;
        iw_IL.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("iw", "IL"), iw_IL);

        LocaleData ar_SA = new LocaleData();
        ar_SA.dateFormat = "dd/MM/yy";
        ar_SA.twelveHourClock = true;
        ar_SA.hourMinuteDelimiter = ":";
        ar_SA.am = "ص";
        ar_SA.pm = "م";
        expectedLocaleData.put(new Locale("ar", "SA"), ar_SA);

        LocaleData ca_ES = new LocaleData();
        ca_ES.dateFormat = "dd/MM/yy";
        ca_ES.twelveHourClock = false;
        ca_ES.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("ca", "ES"), ca_ES);

        LocaleData de_DE = new LocaleData();
        de_DE.dateFormat = "dd.MM.yy";
        de_DE.twelveHourClock = false;
        de_DE.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("de", "DE"), de_DE);

        LocaleData sr_BA = new LocaleData();
        sr_BA.dateFormat = "yy-MM-dd";
        sr_BA.twelveHourClock = false;
        sr_BA.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("sr", "BA"), sr_BA);

        LocaleData zh_HK = new LocaleData();
        zh_HK.dateFormat = "yy'年'M'月'd'日'";
        zh_HK.twelveHourClock = true;
        zh_HK.hourMinuteDelimiter = ":";
        zh_HK.am = "上午";
        zh_HK.pm = "下午";
        expectedLocaleData.put(new Locale("zh", "HK"), zh_HK);

        LocaleData ar_SD = new LocaleData();
        ar_SD.dateFormat = "dd/MM/yy";
        ar_SD.twelveHourClock = true;
        ar_SD.hourMinuteDelimiter = ":";
        ar_SD.am = "ص";
        ar_SD.pm = "م";
        expectedLocaleData.put(new Locale("ar", "SD"), ar_SD);

        LocaleData pt_BR = new LocaleData();
        pt_BR.dateFormat = "dd/MM/yy";
        pt_BR.twelveHourClock = false;
        pt_BR.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("pt", "BR"), pt_BR);

        LocaleData sr_RS = new LocaleData();
        sr_RS.dateFormat = "d.M.yy.";
        sr_RS.twelveHourClock = false;
        sr_RS.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("sr", "RS"), sr_RS);

        LocaleData es_UY = new LocaleData();
        es_UY.dateFormat = "dd/MM/yy";
        es_UY.twelveHourClock = true;
        es_UY.hourMinuteDelimiter = ":";
        es_UY.am = "AM";
        es_UY.pm = "PM";
        expectedLocaleData.put(new Locale("es", "UY"), es_UY);

        LocaleData ar_BH = new LocaleData();
        ar_BH.dateFormat = "dd/MM/yy";
        ar_BH.twelveHourClock = true;
        ar_BH.hourMinuteDelimiter = ":";
        ar_BH.am = "ص";
        ar_BH.pm = "م";
        expectedLocaleData.put(new Locale("ar", "BH"), ar_BH);

        LocaleData es_ES = new LocaleData();
        es_ES.dateFormat = "d/MM/yy";
        es_ES.twelveHourClock = false;
        es_ES.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("es", "ES"), es_ES);

        LocaleData ar_JO = new LocaleData();
        ar_JO.dateFormat = "dd/MM/yy";
        ar_JO.twelveHourClock = true;
        ar_JO.hourMinuteDelimiter = ":";
        ar_JO.am = "ص";
        ar_JO.pm = "م";
        expectedLocaleData.put(new Locale("ar", "JO"), ar_JO);

        LocaleData es_VE = new LocaleData();
        es_VE.dateFormat = "dd/MM/yy";
        es_VE.twelveHourClock = true;
        es_VE.hourMinuteDelimiter = ":";
        es_VE.am = "AM";
        es_VE.pm = "PM";
        expectedLocaleData.put(new Locale("es", "VE"), es_VE);

        LocaleData es_MX = new LocaleData();
        es_MX.dateFormat = "d/MM/yy";
        es_MX.twelveHourClock = true;
        es_MX.hourMinuteDelimiter = ":";
        es_MX.am = "AM";
        es_MX.pm = "PM";
        expectedLocaleData.put(new Locale("es", "MX"), es_MX);

        LocaleData it_IT = new LocaleData();
        it_IT.dateFormat = "dd/MM/yy";
        it_IT.twelveHourClock = false;
        it_IT.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("it", "IT"), it_IT);

        LocaleData no_NO_x_lvariant_NY = new LocaleData();
        no_NO_x_lvariant_NY.dateFormat = "dd.MM.yy";
        no_NO_x_lvariant_NY.twelveHourClock = false;
        no_NO_x_lvariant_NY.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("no", "NO", "x-lvariant-NY"),
                no_NO_x_lvariant_NY);

        LocaleData de_LU = new LocaleData();
        de_LU.dateFormat = "dd.MM.yy";
        de_LU.twelveHourClock = false;
        de_LU.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("de", "LU"), de_LU);

        LocaleData fr_LU = new LocaleData();
        fr_LU.dateFormat = "dd/MM/yy";
        fr_LU.twelveHourClock = false;
        fr_LU.hourMinuteDelimiter = ":";
        expectedLocaleData.put(new Locale("fr", "LU"), fr_LU);

        LocaleData es_NI = new LocaleData();
        es_NI.dateFormat = "MM-dd-yy";
        es_NI.twelveHourClock = true;
        es_NI.hourMinuteDelimiter = ":";
        es_NI.am = "AM";
        es_NI.pm = "PM";
        expectedLocaleData.put(new Locale("es", "NI"), es_NI);

        LocaleData th_TH = new LocaleData();
        th_TH.dateFormat = "d/M/yyyy";
        th_TH.twelveHourClock = false;
        th_TH.hourMinuteDelimiter = ".";
        expectedLocaleData.put(new Locale("th", "TH"), th_TH);
    }

    private LocaleService localeService;

    @Before
    public void setup() {
        localeService = new LocaleService(Mockito.mock(UI.class),
                new LocaleServiceState());
    }

    @Test
    public void localeDateTimeFormat() {
        for (Locale l : expectedLocaleData.keySet()) {
            Assert.assertEquals("Error verifying locale " + l,
                    expectedLocaleData.get(l).dateFormat,
                    localeService.createLocaleData(l).dateFormat);
            Assert.assertEquals("Error verifying locale " + l,
                    expectedLocaleData.get(l).twelveHourClock,
                    localeService.createLocaleData(l).twelveHourClock);
            Assert.assertEquals("Error verifying locale " + l,
                    expectedLocaleData.get(l).am,
                    localeService.createLocaleData(l).am);
            Assert.assertEquals("Error verifying locale " + l,
                    expectedLocaleData.get(l).pm,
                    localeService.createLocaleData(l).pm);
        }
    }

    public static void main(String[] args) {
        for (String s : JAVA8_SUPPORTED_LOCALES) {
            String[] parts = s.split("-", 3);
            Locale l;
            if (parts.length == 1) {
                l = new Locale(parts[0]);
            } else if (parts.length == 2) {
                l = new Locale(parts[0], parts[1]);
            } else if (parts.length == 3) {
                l = new Locale(parts[0], parts[1], parts[2]);
            } else {
                throw new RuntimeException("Unexpected locale: " + s);
            }
            generateData(l);
        }
    }

    /**
     * Helper method for generating the above data using LocaleService.
     * 
     * @param locale
     *            the locale to generate data for
     */
    private static void generateData(Locale locale) {
        System.out.println();
        String id = locale.getLanguage();
        if (!locale.getCountry().equals("")) {
            id += "_" + locale.getCountry();
            if (!locale.getVariant().equals("")) {
                id += "_" + locale.getVariant();
            }
        }

        String field = id.replace('-', '_');
        LocaleService localeService = new LocaleService(Mockito.mock(UI.class),
                new LocaleServiceState());
        LocaleData localeData = localeService.createLocaleData(locale);
        System.out.println("LocaleData " + field + " = new LocaleData();");
        System.out.println(
                field + ".dateFormat = \"" + localeData.dateFormat + "\";");
        System.out.println(field + ".twelveHourClock = "
                + localeData.twelveHourClock + ";");
        System.out.println(field + ".hourMinuteDelimiter = \""
                + localeData.hourMinuteDelimiter + "\";");
        if (localeData.twelveHourClock) {
            System.out.println(field + ".am = \"" + localeData.am + "\";");
            System.out.println(field + ".pm = \"" + localeData.pm + "\";");
        }
        System.out.println("expectedLocaleData.put(new Locale(\""
                + id.replaceAll("_", "\",\"") + "\"), " + field + ");");
    }

}
