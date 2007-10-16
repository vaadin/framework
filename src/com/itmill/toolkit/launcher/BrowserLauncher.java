package com.itmill.toolkit.launcher;

import java.io.IOException;

/**
 * This class opens default browser for ITMillDesktopLaunch class. Default
 * browser is detected by the operating system.
 * 
 */
public class BrowserLauncher {

	/**
	 * Open browser on specified URL.
	 * 
	 * @param url
	 */
	public static void openBrowser(String url) {

		Runtime runtime = Runtime.getRuntime();
		boolean started = false;

		String os = System.getProperty("os.name", "windows").toLowerCase();

		// Linux
		if (os.indexOf("linux") >= 0) {

			// Try x-www-browser
			if (!started) {
				try {
					runtime.exec("x-www-browser " + url);
					started = true;
				} catch (IOException e) {
				}
			}

			// Try firefox
			if (!started) {
				try {
					runtime.exec("firefox " + url);
					started = true;
				} catch (IOException e) {
				}
			}

			// Try mozilla
			if (!started) {
				try {
					runtime.exec("mozilla " + url);
					started = true;
				} catch (IOException e) {
				}
			}

			// Try konqueror
			if (!started) {
				try {
					runtime.exec("konqueror " + url);
					started = true;
				} catch (IOException e) {
				}
			}
		}

		// OS X
		if (os.indexOf("mac os x") >= 0) {

			// Try open
			if (!started) {
				try {
					runtime.exec("open " + url);
					started = true;
				} catch (IOException e) {
				}
			}
		}

		// Try cmd /start command on windows
		if (os.indexOf("win") >= 0) {
			if (!started) {
				try {
					runtime.exec("cmd /c start " + url);
					started = true;
				} catch (IOException e) {
				}
			}
		}

		if (!started)
			System.out.println("Failed to open browser. Please go to " + url);
	}

}
