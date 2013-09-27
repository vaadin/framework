package com.vaadin.tests.components.datefield;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.abstractfield.AbstractFieldTest;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.DateField;

public class DateFieldTest<T extends DateField> extends AbstractFieldTest<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) DateField.class;
    }

    private Command<T, Date> setValue = new Command<T, Date>() {

        @Override
        public void execute(T c, Date value, Object data) {
            c.setValue(value);
        }
    };

    @Override
    protected void createActions() {
        super.createActions();
        createResolutionSelectAction(CATEGORY_FEATURES);
        createBooleanAction("Lenient", CATEGORY_FEATURES, false, lenientCommand);
        createBooleanAction("Show week numbers", CATEGORY_FEATURES, false,
                weekNumberCommand);
        createDateFormatSelectAction(CATEGORY_FEATURES);
        createSetValueAction(CATEGORY_FEATURES);

    };

    private void createSetValueAction(String category) {
        LinkedHashMap<String, Date> options = new LinkedHashMap<String, Date>();
        options.put("(null)", null);
        options.put("(current time)", new Date());
        Calendar c = Calendar.getInstance(new Locale("fi", "FI"));
        c.clear();
        c.set(2010, 12 - 1, 12, 12, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        options.put("2010-12-12 12:00:00.000", c.getTime());
        c.clear();
        c.set(2000, 1 - 1, 2, 3, 4, 5);
        c.set(Calendar.MILLISECOND, 6);
        options.put("2000-01-02 03:04:05.006", c.getTime());
        createMultiClickAction("Set value", category, options, setValue, null);
    }

    private void createDateFormatSelectAction(String category) {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();

        options.put("-", null);
        options.put("d M yyyy", "d M yyyy");
        options.put("d MM yyyy", "d MM yyyy");
        options.put("d MMM yyyy", "d MMM yyyy");
        options.put("d MMMM yyyy", "d MMMM yyyy");
        options.put("dd M yyyy", "dd M yyyy");
        options.put("ddd M yyyy", "ddd M yyyy");
        options.put("d M y", "d M y");
        options.put("d M yy", "d M yy");
        options.put("d M yyy", "d M yyy");
        options.put("d M yyyy", "d M yyyy");
        options.put("d M 'custom text' yyyy", "d M 'custom text' yyyy");
        options.put("'day:'d', month:'M', year: 'yyyy",
                "'day:'d', month:'M', year: 'yyyy");
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.LONG),
                getDatePattern(new Locale("fi", "FI"), DateFormat.LONG));
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.MEDIUM),
                getDatePattern(new Locale("fi", "FI"), DateFormat.MEDIUM));
        options.put(getDatePattern(new Locale("fi", "FI"), DateFormat.SHORT),
                getDatePattern(new Locale("fi", "FI"), DateFormat.SHORT));

        createSelectAction("Date format", category, options, "-",
                dateFormatCommand);

    }

    private String getDatePattern(Locale locale, int dateStyle) {
        DateFormat dateFormat = DateFormat.getDateInstance(dateStyle, locale);

        if (dateFormat instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) dateFormat).toPattern();
            return pattern;
        }
        return null;

    }

    private void createResolutionSelectAction(String category) {
        LinkedHashMap<String, Resolution> options = new LinkedHashMap<String, Resolution>();
        options.put("Year", Resolution.YEAR);
        options.put("Month", Resolution.MONTH);
        options.put("Day", Resolution.DAY);
        options.put("Hour", Resolution.HOUR);
        options.put("Min", Resolution.MINUTE);
        options.put("Sec", Resolution.SECOND);

        createSelectAction("Resolution", category, options, "Year",
                resolutionCommand);
    }

    private Command<T, Resolution> resolutionCommand = new Command<T, Resolution>() {

        @Override
        public void execute(T c, Resolution value, Object data) {
            c.setResolution(value);

        }
    };
    private Command<T, Boolean> lenientCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setLenient(false);

        }
    };
    private Command<T, Boolean> weekNumberCommand = new Command<T, Boolean>() {

        @Override
        public void execute(T c, Boolean value, Object data) {
            c.setShowISOWeekNumbers(value);

        }
    };
    private Command<T, String> dateFormatCommand = new Command<T, String>() {

        @Override
        public void execute(T c, String value, Object data) {
            c.setDateFormat(value);
        }
    };

    public static class Tb3DateFieldTests extends MultiBrowserTest {

        @Override
        protected boolean isDebug() {
            // run in debug to see js errors
            return true;
        }

        @Test
        public void testMakingRequired() throws InterruptedException {
            Thread.sleep(1000);
            menu("Component");
            menuSub("State");
            menu("Required");
            assertNoErrorNotification();
        }

        private void assertNoErrorNotification() {
            try {
                getDriver().findElement(
                        By.xpath("//div[contains(@class, 'v-Notification') ]"));
                Assert.fail("Error notification shown!");
            } catch (NoSuchElementException e) {
                // As expected
            }
        }

        @Test
        public void testValueAfterOpeningPopupInRequiredField()
                throws InterruptedException {
            Thread.sleep(1000);
            menu("Component");
            menuSub("State");
            menu("Required");

            menu("Component");
            menuSub("Features");
            menuSub("Resolution");
            menu("Month");

            menu("Component");
            menuSub("Listeners");
            menu("Value change listener");

            String inputtedValue = "2/12";
            getInput().sendKeys(inputtedValue);

            openPopup();
            closePopup();
            String actual = getInput().getAttribute("value");
            Assert.assertEquals(inputtedValue, actual);
            assertNoErrorNotification();

        }

        private void openPopup() throws InterruptedException {
            Dimension size = getInput().getSize();
            new Actions(getDriver()).moveToElement(getInput(), 0, 0)
                    .moveByOffset(size.getWidth() + 5, size.getHeight() / 2)
                    .click();
            // This fails in Opera for some weird reason
            // getDriver().findElement(By.className("v-datefield-button")).click();
        }

        private WebElement getInput() {
            return getDriver().findElement(By.xpath("//input"));
        }

        private void closePopup() {
            getDriver().findElement(By.tagName("body")).click();
        }

        /**
         * @since
         * @param string
         */
        private void menuSub(String string) {
            getDriver().findElement(
                    By.xpath("//span[text() = '" + string + "']")).click();
            new Actions(getDriver()).moveByOffset(100, 0).build().perform();
        }

        /**
         * @since
         * @param string
         */
        private void menu(String string) {
            getDriver().findElement(
                    By.xpath("//span[text() = '" + string + "']")).click();

        }

    }

}
