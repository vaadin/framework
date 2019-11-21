package com.vaadin.tests.tb3;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Stream;

import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * Provides values for parameters which depend on where the test is run.
 * Parameters should be configured in work/eclipse-run-selected-test.properties.
 * A template is available in uitest/.
 *
 * @author Vaadin Ltd
 */
@RunOnHub("tb3-hub.intra.itmill.com")
@BrowserFactory(VaadinBrowserFactory.class)
public abstract class PrivateTB3Configuration extends ScreenshotTB3Test {
    private static final String BROWSER_FACTORY = "browser.factory";
    public static final String SCREENSHOT_DIRECTORY = "com.vaadin.testbench.screenshot.directory";
    private static final String HOSTNAME_PROPERTY = "com.vaadin.testbench.deployment.hostname";
    private static final String RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.runLocally";
    private static final String ALLOW_RUN_LOCALLY_PROPERTY = "com.vaadin.testbench.allowRunLocally";
    private static final String PORT_PROPERTY = "com.vaadin.testbench.deployment.port";
    private static final String DEPLOYMENT_PROPERTY = "com.vaadin.testbench.deployment.url";
    private static final String HUB_URL = "com.vaadin.testbench.hub.url";
    private static final Properties properties = new Properties();
    private static final File propertiesFile = new File("../work",
            "eclipse-run-selected-test.properties");
    private static final String FIREFOX_PATH = "firefox.path";
    private static final String PHANTOMJS_PATH = "phantomjs.binary.path";
    private static final String BROWSERS_EXCLUDE = "browsers.exclude";
    private static final String MAX_ATTEMPTS = "com.vaadin.testbench.Parameters.maxAttempts";

    static {
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
            System.setProperty("useLocalWebDriver", "true");
            DesiredCapabilities localBrowser = getRunLocallyCapabilities();
            System.setProperty("browsers.include",
                    localBrowser.getBrowserName() + localBrowser.getVersion());
        }

        // Read properties from the file.
        Stream.of(FIREFOX_PATH, PHANTOMJS_PATH, BROWSER_FACTORY,
                BROWSERS_EXCLUDE).filter(properties::containsKey)
                .forEach(property -> System.setProperty(property,
                        properties.getProperty(property)));

        if (properties.containsKey(MAX_ATTEMPTS)) {
            Parameters.setMaxAttempts(
                    Integer.parseInt(properties.getProperty(MAX_ATTEMPTS)));
        }

        String dir = System.getProperty(SCREENSHOT_DIRECTORY,
                properties.getProperty(SCREENSHOT_DIRECTORY));
        if (dir != null && !dir.isEmpty()) {
            String reference = Paths.get(dir, "reference-screenshots")
                    .toString();
            String errors = Paths.get(dir, "error-screenshots").toString();
            Parameters.setScreenshotReferenceDirectory(reference);
            Parameters.setScreenshotErrorDirectory(errors);
        } else {
            // Attempt to pass specific values to Parameters based on
            // real property name
            final String base = Parameters.class.getName() + ".";
            if (properties.containsKey(base + "screenshotReferenceDirectory")) {
                Parameters.setScreenshotReferenceDirectory(properties
                        .getProperty(base + "screenshotReferenceDirectory"));
            }
            if (properties.containsKey(base + "screenshotErrorDirectory")) {
                Parameters.setScreenshotErrorDirectory(properties
                        .getProperty(base + "screenshotErrorDirectory"));
            }
        }

        if ("true".equals(getProperty("browserstack"))) {
            properties.setProperty(HUB_URL,
                    "https://" + getProperty("browserstack.username") + ":"
                            + getProperty("browserstack.key")
                            + "@hub-cloud.browserstack.com/wd/hub");
        }

    }

    @Override
    public void setup() throws Exception {
        String allowRunLocally = getProperty(ALLOW_RUN_LOCALLY_PROPERTY);
        if ((allowRunLocally == null || !allowRunLocally.equals("" + true))
                && getClass().getAnnotation(RunLocally.class) != null) {
            fail("@RunLocally annotation is not allowed by default in framework tests. "
                    + "See file uitest/eclipse-run-selected-test.properties for more information.");
        }

        super.setup();
    }

    @Override
    public void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {
        super.setDesiredCapabilities(desiredCapabilities);

        if (BrowserUtil.isIE(desiredCapabilities)) {
            if (requireWindowFocusForIE()) {
                desiredCapabilities.setCapability(
                        InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
            }
            if (!usePersistentHoverForIE()) {
                desiredCapabilities.setCapability(
                        InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,
                        false);
            }
            if (!useNativeEventsForIE()) {
                desiredCapabilities.setCapability(
                        InternetExplorerDriver.NATIVE_EVENTS, false);
            }
        }

        if (desiredCapabilities.getCapability("project") == null) {
            desiredCapabilities.setCapability("project", "Vaadin Framework");
        }
        if (desiredCapabilities.getCapability("build") == null) {
            desiredCapabilities.setCapability("build", String.format("%s / %s",
                    getDeploymentHostname(), Calendar.getInstance().getTime()));
        }
        desiredCapabilities.setCapability("name", String.format("%s.%s",
                getClass().getCanonicalName(), testName.getMethodName()));
    }

    protected static DesiredCapabilities getRunLocallyCapabilities() {
        VaadinBrowserFactory factory = new VaadinBrowserFactory();

        try {
            if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
                // RunLocally defined in propeties file
                return factory.create(Browser
                        .valueOf(properties.getProperty(RUN_LOCALLY_PROPERTY)
                                .toUpperCase(Locale.ROOT)));
            } else if (System.getProperties().containsKey("browsers.include")) {
                // Use first included browser as the run locally browser.
                String property = System.getProperty("browsers.include");
                String firstBrowser = property.split(",")[0];

                return factory.create(Browser.valueOf(firstBrowser
                        .replaceAll("[0-9]+$", "").toUpperCase(Locale.ROOT)));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Falling back to FireFox");
        }
        return factory.create(Browser.FIREFOX);
    }

    protected static String getProperty(String name) {
        String property = properties.getProperty(name);
        if (property == null) {
            property = System.getProperty(name);
        }

        return property;
    }

    @Override
    protected String getHubURL() {
        String hubUrl = getProperty(HUB_URL);
        if (hubUrl == null || hubUrl.trim().isEmpty()) {
            return super.getHubURL();
        }

        return hubUrl;
    }

    @Override
    protected String getBaseURL() {
        if (isRunLocally()) {
            return "http://localhost:8888";
        }
        String url = getProperty(DEPLOYMENT_PROPERTY);
        if (url == null || url.trim().isEmpty()) {
            return super.getBaseURL();
        }
        return url;
    }

    @Override
    protected String getDeploymentHostname() {
        if (isRunLocally()) {
            return "localhost";
        }
        return getConfiguredDeploymentHostname();
    }

    protected boolean isRunLocally() {
        if (properties.containsKey(RUN_LOCALLY_PROPERTY)) {
            return true;
        }

        if (properties.containsKey(ALLOW_RUN_LOCALLY_PROPERTY)
                && properties.get(ALLOW_RUN_LOCALLY_PROPERTY).equals("true")
                && getClass().getAnnotation(RunLocally.class) != null) {
            return true;
        }

        return "true".equals(System.getProperty("useLocalWebDriver", "false"));
    }

    /**
     * Gets the hostname that tests are configured to use.
     *
     * @return the host name configuration value
     */
    public static String getConfiguredDeploymentHostname() {
        String hostName = getProperty(HOSTNAME_PROPERTY);

        if (hostName == null || hostName.isEmpty()) {
            hostName = findAutoHostname();
        }

        return hostName;
    }

    @Override
    protected int getDeploymentPort() {
        return getConfiguredDeploymentPort();
    }

    /**
     * Gets the port that tests are configured to use.
     *
     * @return the port configuration value
     */
    public static int getConfiguredDeploymentPort() {
        String portString = getProperty(PORT_PROPERTY);

        int port = 8888;
        if (portString != null && !portString.isEmpty()) {
            port = Integer.parseInt(portString);
        }

        return port;
    }

    /**
     * Tries to automatically determine the IP address of the machine the test
     * is running on.
     *
     * @return An IP address of one of the network interfaces in the machine.
     * @throws RuntimeException
     *             if there was an error or no IP was found
     */
    private static String findAutoHostname() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        throw new RuntimeException(
                "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) ip address found.");
    }
}
