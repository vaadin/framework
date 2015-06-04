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

package com.vaadin.tools;

import static java.lang.Integer.parseInt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;

import elemental.json.JsonException;
import elemental.json.JsonNull;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

/**
 * This class is able to validate the vaadin CVAL license.
 *
 * It reads the developer license file and asks the server to validate the
 * licenseKey. If the license is invalid it throws an exception with the
 * information about the problem and the server response.
 *
 * @since 7.3
 */
public final class CvalChecker {

    /*
     * Class used for binding the JSON gotten from server.
     * 
     * It is not in a separate f le, so as it is easier to copy into any product
     * which does not depend on vaadin core.
     * 
     * We are using elemental.json in order not to use additional dependency
     * like auto-beans, gson, etc.
     */
    public static class CvalInfo {

        public static class Product {
            private JsonObject o;

            public Product(JsonObject o) {
                this.o = o;
            }

            public String getName() {
                return get(o, "name", String.class);
            }

            public Integer getVersion() {
                return get(o, "version", Integer.class);
            }
        }

        @SuppressWarnings("unchecked")
        private static <T> T get(JsonObject o, String k, Class<T> clz) {
            Object ret = null;
            try {
                if (o == null || o.get(k) == null
                        || o.get(k) instanceof JsonNull) {
                    return null;
                }
                if (clz == String.class) {
                    ret = o.getString(k);
                } else if (clz == JsonObject.class) {
                    ret = o.getObject(k);
                } else if (clz == Integer.class) {
                    ret = Integer.valueOf((int) o.getNumber(k));
                } else if (clz == Date.class) {
                    ret = new Date((long) o.getNumber(k));
                } else if (clz == Boolean.class) {
                    ret = o.getBoolean(k);
                }
            } catch (JsonException e) {
            }
            return (T) ret;
        }

        private JsonObject o;

        private Product product;

        public CvalInfo(JsonObject o) {
            this.o = o;
            product = new Product(get(o, "product", JsonObject.class));
        }

        public Boolean getExpired() {
            return get(o, "expired", Boolean.class);
        }

        public Date getExpiredEpoch() {
            return get(o, "expiredEpoch", Date.class);
        }

        public String getLicensee() {
            return get(o, "licensee", String.class);
        }

        public String getLicenseKey() {
            return get(o, "licenseKey", String.class);
        }

        public String getMessage() {
            return get(o, "message", String.class);
        }

        public Product getProduct() {
            return product;
        }

        public String getType() {
            return get(o, "type", String.class);
        }

        public void setExpiredEpoch(Date expiredEpoch) {
            o.put("expiredEpoch", expiredEpoch.getTime());
        }

        public void setMessage(String msg) {
            o.put("message", msg);
        }

        @Override
        public String toString() {
            return o.toString();
        }

        public boolean isLicenseExpired() {
            return (getExpired() != null && getExpired())
                    || (getExpiredEpoch() != null && getExpiredEpoch().before(
                            new Date()));
        }

        public boolean isValidVersion(int majorVersion) {
            return getProduct().getVersion() == null
                    || getProduct().getVersion() >= majorVersion;

        }

        private boolean isValidInfo(String name, String key) {
            return getProduct() != null && getProduct().getName() != null
                    && getLicenseKey() != null
                    && getProduct().getName().equals(name)
                    && getLicenseKey().equals(key);
        }
    }

    /*
     * The class with the method for getting json from server side. It is here
     * and protected just for replacing it in tests.
     */
    public static class CvalServer {
        protected String licenseUrl = LICENSE_URL_PROD;

        String askServer(String productName, String productKey, int timeoutMs)
                throws IOException {
            String url = licenseUrl + productKey;
            URLConnection con;
            try {
                // Send some additional info in the User-Agent string.
                String ua = "Cval " + productName + " " + productKey + " "
                        + getFirstLaunch();
                for (String prop : Arrays.asList("java.vendor.url",
                        "java.version", "os.name", "os.version", "os.arch")) {
                    ua += " " + System.getProperty(prop, "-").replace(" ", "_");
                }
                con = new URL(url).openConnection();
                con.setRequestProperty("User-Agent", ua);
                con.setConnectTimeout(timeoutMs);
                con.setReadTimeout(timeoutMs);
                String r = IOUtils.toString(con.getInputStream());
                return r;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        /*
         * Get the GWT firstLaunch timestamp.
         */
        String getFirstLaunch() {
            try {
                Class<?> clz = Class
                        .forName("com.google.gwt.dev.shell.CheckForUpdates");
                return Preferences.userNodeForPackage(clz).get("firstLaunch",
                        "-");
            } catch (ClassNotFoundException e) {
                return "-";
            }
        }
    }

    /**
     * Exception thrown when the user does not have a valid cval license.
     */
    public static class InvalidCvalException extends Exception {
        private static final long serialVersionUID = 1L;
        public final CvalInfo info;
        public final String name;
        public final String key;
        public final String version;
        public final String title;

        public InvalidCvalException(String name, String version, String title,
                String key, CvalInfo info) {
            super(composeMessage(title, version, key, info));
            this.info = info;
            this.name = name;
            this.key = key;
            this.version = version;
            this.title = title;
        }

        static String composeMessage(String title, String version, String key,
                CvalInfo info) {
            String msg = "";
            int majorVers = computeMajorVersion(version);

            if (info != null && !info.isValidVersion(majorVers)) {
                msg = getErrorMessage("invalid", title, majorVers);
            } else if (info != null && info.getMessage() != null) {
                msg = info.getMessage().replace("\\n", "\n");
            } else if (info != null && info.isLicenseExpired()) {
                String type = "evaluation".equals(info.getType()) ? "Evaluation license"
                        : "License";
                msg = getErrorMessage("expired", title, majorVers, type);
            } else if (key == null) {
                msg = getErrorMessage("none", title, majorVers);
            } else {
                msg = getErrorMessage("invalid", title, majorVers);
            }
            return msg;
        }
    }

    /**
     * Exception thrown when the license server is unreachable
     */
    public static class UnreachableCvalServerException extends Exception {
        private static final long serialVersionUID = 1L;
        public final String name;

        public UnreachableCvalServerException(String name, Exception e) {
            super(e);
            this.name = name;
        }
    }

    public static final String LINE = "----------------------------------------------------------------------------------------------------------------------";

    static final int GRACE_DAYS_MSECS = 2 * 24 * 60 * 60 * 1000;

    private static final String LICENSE_URL_PROD = "https://tools.vaadin.com/vaadin-license-server/licenses/";

    /*
     * used in tests
     */
    static void cacheLicenseInfo(CvalInfo info) {
        if (info != null) {
            Preferences p = Preferences.userNodeForPackage(CvalInfo.class);
            if (info.toString().length() > Preferences.MAX_VALUE_LENGTH) {
                // This should never happen since MAX_VALUE_LENGTH is big
                // enough.
                // But server could eventually send a very big message, so we
                // discard it in cache and would use hard-coded messages.
                info.setMessage(null);
            }
            p.put(info.getProduct().getName(), info.toString());
        }
    }

    /*
     * used in tests
     */
    static void deleteCache(String productName) {
        Preferences p = Preferences.userNodeForPackage(CvalInfo.class);
        p.remove(productName);
    }

    /**
     * Given a product name returns the name of the file with the license key.
     *
     * Traditionally we have delivered license keys with a name like
     * 'vaadin.touchkit.developer.license' but our database product name is
     * 'vaadin-touchkit' so we have to replace '-' by '.' to maintain
     * compatibility.
     */
    static final String computeLicenseName(String productName) {
        return productName.replace("-", ".") + ".developer.license";
    }

    static final int computeMajorVersion(String productVersion) {
        return productVersion == null || productVersion.isEmpty() ? 0
                : parseInt(productVersion.replaceFirst("[^\\d]+.*$", ""));
    }

    /*
     * used in tests
     */
    static CvalInfo parseJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            JsonObject o = JsonUtil.parse(json);
            return new CvalInfo(o);
        } catch (JsonException e) {
            return null;
        }
    }

    private CvalServer provider;

    /**
     * The constructor.
     */
    public CvalChecker() {
        setLicenseProvider(new CvalServer());
    }

    /**
     * Validate whether there is a valid license key for a product.
     *
     * @param productName
     *            for example vaadin-touchkit
     * @param productVersion
     *            for instance 4.0.1
     * @return CvalInfo Server response or cache response if server is offline
     * @throws InvalidCvalException
     *             when there is no a valid license for the product
     * @throws UnreachableCvalServerException
     *             when we have license key but server is unreachable
     */
    public CvalInfo validateProduct(String productName, String productVersion,
            String productTitle) throws InvalidCvalException,
            UnreachableCvalServerException {
        String key = getDeveloperLicenseKey(productName, productVersion,
                productTitle);

        CvalInfo info = null;
        if (key != null && !key.isEmpty()) {
            info = getCachedLicenseInfo(productName);
            if (info != null && !info.isValidInfo(productName, key)) {
                deleteCache(productName);
                info = null;
            }
            info = askLicenseServer(productName, key, productVersion, info);
            if (info != null && info.isValidInfo(productName, key)
                    && info.isValidVersion(computeMajorVersion(productVersion))
                    && !info.isLicenseExpired()) {
                return info;
            }
        }

        throw new InvalidCvalException(productName, productVersion,
                productTitle, key, info);
    }

    /*
     * Change the license provider, only used in tests.
     */
    final CvalChecker setLicenseProvider(CvalServer p) {
        provider = p;
        return this;
    }

    private CvalInfo askLicenseServer(String productName, String productKey,
            String productVersion, CvalInfo info)
            throws UnreachableCvalServerException {

        int majorVersion = computeMajorVersion(productVersion);

        // If we have a valid license info here, it means that we got it from
        // cache.
        // We add a grace time when so as if the server is unreachable
        // we allow the user to use the product.
        if (info != null && info.getExpiredEpoch() != null
                && !"evaluation".equals(info.getType())) {
            long ts = info.getExpiredEpoch().getTime() + GRACE_DAYS_MSECS;
            info.setExpiredEpoch(new Date(ts));
        }

        boolean validCache = info != null
                && info.isValidInfo(productName, productKey)
                && info.isValidVersion(majorVersion)
                && !info.isLicenseExpired();

        // if we have a validCache we set the timeout smaller
        int timeout = validCache ? 2000 : 10000;

        try {
            CvalInfo srvinfo = parseJson(provider.askServer(productName + "-"
                    + productVersion, productKey, timeout));
            if (srvinfo != null && srvinfo.isValidInfo(productName, productKey)
                    && srvinfo.isValidVersion(majorVersion)) {
                // We always cache the info if it is valid although it is
                // expired
                cacheLicenseInfo(srvinfo);
                info = srvinfo;
            }
        } catch (FileNotFoundException e) {
            // 404
            return null;
        } catch (Exception e) {
            if (info == null) {
                throw new UnreachableCvalServerException(productName, e);
            }
        }
        return info;
    }

    private CvalInfo getCachedLicenseInfo(String productName) {
        Preferences p = Preferences.userNodeForPackage(CvalInfo.class);
        String json = p.get(productName, "");
        if (!json.isEmpty()) {
            CvalInfo info = parseJson(json);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    private String getDeveloperLicenseKey(String productName,
            String productVersion, String productTitle)
            throws InvalidCvalException {
        String licenseName = computeLicenseName(productName);

        String key = System.getProperty(licenseName);
        if (key != null && !key.isEmpty()) {
            return key;
        }

        try {
            String dotLicenseName = "." + licenseName;
            String userHome = System.getProperty("user.home");
            for (URL url : new URL[] {
                    new File(userHome, dotLicenseName).toURI().toURL(),
                    new File(userHome, licenseName).toURI().toURL(),
                    URL.class.getResource("/" + dotLicenseName),
                    URL.class.getResource("/" + licenseName) }) {
                if (url != null) {
                    try {
                        key = readKeyFromFile(url,
                                computeMajorVersion(productVersion));
                        if (key != null && !(key = key.trim()).isEmpty()) {
                            return key;
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new InvalidCvalException(productName, productVersion,
                productTitle, null, null);
    }

    String readKeyFromFile(URL url, int majorVersion) throws IOException {
        String majorVersionStr = String.valueOf(majorVersion);
        List<String> lines = IOUtils.readLines(url.openStream());
        String defaultKey = null;
        for (String line : lines) {
            String[] parts = line.split("\\s*=\\s*");
            if (parts.length < 2) {
                defaultKey = parts[0].trim();
            }
            if (parts[0].equals(majorVersionStr)) {
                return parts[1].trim();
            }
        }
        return defaultKey;
    }

    static String getErrorMessage(String key, Object... pars) {
        Locale loc = Locale.getDefault();
        ResourceBundle res = ResourceBundle.getBundle(
                CvalChecker.class.getName(), loc);
        String msg = res.getString(key);
        return new MessageFormat(msg, loc).format(pars);
    }
}
