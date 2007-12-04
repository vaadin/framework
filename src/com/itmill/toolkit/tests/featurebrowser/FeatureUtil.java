/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.featurebrowser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FeatureUtil {

    private static boolean statistics = false;

    public static final SimpleDateFormat format = new SimpleDateFormat(
            "yyyyMMdd HHmmss");

    public static void debug(String userIdentity, String msg) {
        if (statistics) {
            System.out.println("[" + userIdentity + "] " + msg);
        }
    }

    public static String getTimestamp() {
        if (statistics) {
            try {
                return format.format(new Date());
            } catch (final Exception e) {
                // ignored, should never happen
            }
        }
        return "";
    }

    public static void setStatistics(boolean statistics) {
        FeatureUtil.statistics = statistics;
    }
}
