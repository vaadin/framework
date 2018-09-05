package com.vaadin.tests.tb3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageFileUtil;

/**
 * Internal hack to support capturing screenshots for elements.
 *
 * Most parts are from TestBenchCommandExecutor and the feature should be
 * integrated into TB4.
 *
 * @author Vaadin Ltd
 */
public class CustomTestBenchCommandExecutor {

    private ImageComparison imageComparison = new ImageComparison();
    private final WebDriver actualDriver;

    public CustomTestBenchCommandExecutor(WebDriver driver) {
        actualDriver = driver;
    }

    /**
     * Compares the screenshot of the given element with the reference.
     *
     * Copied from TestBenchCommandExecutor
     */
    public boolean compareScreen(WebElement element, File reference)
            throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(reference);
        } catch (IIOException e) {
            // Don't worry, an error screen shot will be generated that later
            // can be used as the reference
        }
        return compareScreen(element, image, reference.getName());
    }

    /**
     * Compares the screenshot of the given element with the reference.
     *
     * Copied from TestBenchCommandExecutor and added cropToElement
     */
    public boolean compareScreen(WebElement element, BufferedImage reference,
            String referenceName) throws IOException {
        for (int times = 0; times < Parameters
                .getMaxScreenshotRetries(); times++) {
            BufferedImage screenshotImage = cropToElement(element,
                    ImageIO.read(new ByteArrayInputStream(
                            ((TakesScreenshot) actualDriver)
                                    .getScreenshotAs(OutputType.BYTES))));
            if (reference == null) {
                // Store the screenshot in the errors directory and fail the
                // test
                ImageFileUtil.createScreenshotDirectoriesIfNeeded();
                ImageIO.write(screenshotImage, "png",
                        ImageFileUtil.getErrorScreenshotFile(referenceName));
                getLogger().severe("No reference found for " + referenceName
                        + " in "
                        + ImageFileUtil.getScreenshotReferenceDirectory());
                return false;
            }
            if (imageComparison.imageEqualToReference(screenshotImage,
                    reference, referenceName,
                    Parameters.getScreenshotComparisonTolerance())) {
                return true;
            }
            pause(Parameters.getScreenshotRetryDelay());
        }
        return false;
    }

    /**
     * Crops the image to show only the element. If the element is partly off
     * screen, crops to show the part of the element which is in the screenshot
     *
     * @param element
     *            the element to retain in the screenshot
     * @param fullScreen
     *            the full screen image
     * @return
     * @throws IOException
     */
    public static BufferedImage cropToElement(WebElement element,
            BufferedImage fullScreen) throws IOException {
        Point loc = element.getLocation();
        Dimension size = element.getSize();
        int x = loc.x, y = loc.y;
        int w = size.width;
        int h = size.height;

        if (x >= 0 && x < fullScreen.getWidth()) {
            // X loc on screen
            // Get the part of the element which is on screen
            w = Math.min(fullScreen.getWidth() - x, w);
        } else {
            throw new IOException("Element x is outside the screenshot (x: " + x
                    + ", y: " + y + ")");
        }

        if (y >= 0 && y < fullScreen.getHeight()) {
            // Y loc on screen
            // Get the part of the element which is on screen
            h = Math.min(fullScreen.getHeight() - y, h);
        } else {
            throw new IOException("Element y is outside the screenshot (x: " + x
                    + ", y: " + y + ")");
        }

        return fullScreen.getSubimage(x, y, w, h);
    }

    private void pause(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    private static Logger getLogger() {
        return Logger.getLogger(CustomTestBenchCommandExecutor.class.getName());
    }
}
