/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;

/**
 * <p>
 * A date editor component that can be bound to any bindable Property. that is
 * compatible with <code>java.util.Date</code>.
 * </p>
 * <p>
 * Since <code>DateField</code> extends <code>AbstractField</code> it
 * implements the {@link com.itmill.toolkit.data.Buffered}interface. A
 * <code>DateField</code> is in write-through mode by default, so
 * {@link com.itmill.toolkit.ui.AbstractField#setWriteThrough(boolean)}must be
 * called to enable buffering.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class DateField extends AbstractField {

	/* Private members ************************************************* */

	/**
	 * Resolution identifier: milliseconds.
	 */
	public static final int RESOLUTION_MSEC = 0;

	/**
	 * Resolution identifier: seconds.
	 */
	public static final int RESOLUTION_SEC = 1;

	/**
	 * Resolution identifier: minutes.
	 */
	public static final int RESOLUTION_MIN = 2;

	/**
	 * Resolution identifier: hours.
	 */
	public static final int RESOLUTION_HOUR = 3;

	/**
	 * Resolution identifier: days.
	 */
	public static final int RESOLUTION_DAY = 4;

	/**
	 * Resolution identifier: months.
	 */
	public static final int RESOLUTION_MONTH = 5;

	/**
	 * Resolution identifier: years.
	 */
	public static final int RESOLUTION_YEAR = 6;

	/**
	 * Specified smallest modifiable unit.
	 */
	private int resolution = RESOLUTION_MSEC;

	/**
	 * Specified largest modifiable unit.
	 */
	private static final int largestModifiable = RESOLUTION_YEAR;

	/**
	 * The internal calendar to be used in java.utl.Date conversions.
	 */
	private Calendar calendar;

	/* Constructors **************************************************** */

	/**
	 * Constructs an empty <code>DateField</code> with no caption.
	 */
	public DateField() {
	}

	/**
	 * Constructs an empty <code>DateField</code> with caption.
	 * 
	 * @param caption
	 *            the caption of the datefield.
	 */
	public DateField(String caption) {
		setCaption(caption);
	}

	/**
	 * Constructs a new <code>DateField</code> that's bound to the specified
	 * <code>Property</code> and has the given caption <code>String</code>.
	 * 
	 * @param caption
	 *            the caption <code>String</code> for the editor.
	 * @param dataSource
	 *            the Property to be edited with this editor.
	 */
	public DateField(String caption, Property dataSource) {
		this(dataSource);
		setCaption(caption);
	}

	/**
	 * Constructs a new <code>DateField</code> that's bound to the specified
	 * <code>Property</code> and has no caption.
	 * 
	 * @param dataSource
	 *            the Property to be edited with this editor.
	 */
	public DateField(Property dataSource) throws IllegalArgumentException {
		if (!Date.class.isAssignableFrom(dataSource.getType()))
			throw new IllegalArgumentException("Can't use "
					+ dataSource.getType().getName()
					+ " typed property as datasource");

		setPropertyDataSource(dataSource);
	}

	/**
	 * Constructs a new <code>DateField</code> with the given caption and
	 * initial text contents. The editor constructed this way will not be bound
	 * to a Property unless
	 * {@link com.itmill.toolkit.data.Property.Viewer#setPropertyDataSource(Property)}
	 * is called to bind it.
	 * 
	 * @param caption
	 *            the caption <code>String</code> for the editor.
	 * @param value
	 *            the Date value.
	 */
	public DateField(String caption, Date value) {
		setValue(value);
		setCaption(caption);
	}

	/* Component basic features ********************************************* */

	/*
	 * Paints this component. Don't add a JavaDoc comment here, we use the
	 * default documentation from implemented interface.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		// Adds the locale as attribute
		Locale l = getLocale();
		if (l != null) {
			target.addAttribute("locale", l.toString());
		}

		// Gets the calendar
		Calendar calendar = getCalendar();
		Date currentDate = (Date) getValue();

		for (int r = resolution; r <= largestModifiable; r++)
			switch (r) {
			case RESOLUTION_MSEC:
				target.addVariable(this, "msec", currentDate != null ? calendar
						.get(Calendar.MILLISECOND) : -1);
				break;
			case RESOLUTION_SEC:
				target.addVariable(this, "sec", currentDate != null ? calendar
						.get(Calendar.SECOND) : -1);
				break;
			case RESOLUTION_MIN:
				target.addVariable(this, "min", currentDate != null ? calendar
						.get(Calendar.MINUTE) : -1);
				break;
			case RESOLUTION_HOUR:
				target.addVariable(this, "hour", currentDate != null ? calendar
						.get(Calendar.HOUR_OF_DAY) : -1);
				break;
			case RESOLUTION_DAY:
				target.addVariable(this, "day", currentDate != null ? calendar
						.get(Calendar.DAY_OF_MONTH) : -1);
				break;
			case RESOLUTION_MONTH:
				target.addVariable(this, "month",
						currentDate != null ? calendar.get(Calendar.MONTH) + 1
								: -1);
				break;
			case RESOLUTION_YEAR:
				target.addVariable(this, "year", currentDate != null ? calendar
						.get(Calendar.YEAR) : -1);
				break;
			}
	}

	/*
	 * Gets the components UIDL tag string. Don't add a JavaDoc comment here, we
	 * use the default documentation from implemented interface.
	 */
	public String getTag() {
		return "datefield";
	}

	/*
	 * Invoked when a variable of the component changes. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	public void changeVariables(Object source, Map variables) {

		if (!isReadOnly()
				&& (variables.containsKey("year")
						|| variables.containsKey("month")
						|| variables.containsKey("day")
						|| variables.containsKey("hour")
						|| variables.containsKey("min")
						|| variables.containsKey("sec") || variables
						.containsKey("msec"))) {

			// Old and new dates
			Date oldDate = (Date) getValue();
			Date newDate = null;

			// Gets the new date in parts
			// Null values are converted to negative values.
			int year = variables.containsKey("year") ? (variables.get("year") == null ? -1
					: ((Integer) variables.get("year")).intValue())
					: -1;
			int month = variables.containsKey("month") ? (variables
					.get("month") == null ? -1 : ((Integer) variables
					.get("month")).intValue() - 1) : -1;
			int day = variables.containsKey("day") ? (variables.get("day") == null ? -1
					: ((Integer) variables.get("day")).intValue())
					: -1;
			int hour = variables.containsKey("hour") ? (variables.get("hour") == null ? -1
					: ((Integer) variables.get("hour")).intValue())
					: -1;
			int min = variables.containsKey("min") ? (variables.get("min") == null ? -1
					: ((Integer) variables.get("min")).intValue())
					: -1;
			int sec = variables.containsKey("sec") ? (variables.get("sec") == null ? -1
					: ((Integer) variables.get("sec")).intValue())
					: -1;
			int msec = variables.containsKey("msec") ? (variables.get("msec") == null ? -1
					: ((Integer) variables.get("msec")).intValue())
					: -1;

			// If all of the components is < 0 use the previous value
			if (year < 0 && month < 0 && day < 0 && hour < 0 && min < 0
					&& sec < 0 && msec < 0)
				newDate = null;
			else {

				// Clone the calendar for date operation
				Calendar cal = (Calendar) getCalendar();

				// Make sure that meaningful values exists
				// Use the previous value if some of the variables
				// have not been changed.
				year = year < 0 ? cal.get(Calendar.YEAR) : year;
				month = month < 0 ? cal.get(Calendar.MONTH) : month;
				day = day < 0 ? cal.get(Calendar.DAY_OF_MONTH) : day;
				hour = hour < 0 ? cal.get(Calendar.HOUR_OF_DAY) : hour;
				min = min < 0 ? cal.get(Calendar.MINUTE) : min;
				sec = sec < 0 ? cal.get(Calendar.SECOND) : sec;
				msec = msec < 0 ? cal.get(Calendar.MILLISECOND) : msec;

				// Sets the calendar fields
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, day);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, min);
				cal.set(Calendar.SECOND, sec);
				cal.set(Calendar.MILLISECOND, msec);

				// Assigns the date
				newDate = cal.getTime();
			}

			if (newDate != oldDate
					&& (newDate == null || !newDate.equals(oldDate)))
				setValue(newDate);
		}
	}

	/* Property features **************************************************** */

	/*
	 * Gets the edited property's type. Don't add a JavaDoc comment here, we use
	 * the default documentation from implemented interface.
	 */
	public Class getType() {
		return Date.class;
	}

	/*
	 * Returns the value of the property in human readable textual format. Don't
	 * add a JavaDoc comment here, we use the default documentation from
	 * implemented interface.
	 */
	public String toString() {
		Date value = (Date) getValue();
		if (value != null)
			return value.toString();
		return null;
	}

	/*
	 * Sets the value of the property. Don't add a JavaDoc comment here, we use
	 * the default documentation from implemented interface.
	 */
	public void setValue(Object newValue) throws Property.ReadOnlyException,
			Property.ConversionException {

		// Allows setting dates directly
		if (newValue == null || newValue instanceof Date)
			super.setValue(newValue);
		else {

			// Try to parse as string
			try {
				SimpleDateFormat parser = new SimpleDateFormat();
				Date val = parser.parse(newValue.toString());
				super.setValue(val);
			} catch (ParseException e) {
				throw new Property.ConversionException(e.getMessage());
			}
		}
	}

	/**
	 * Sets the DateField datasource. Datasource type must assignable to Date.
	 * 
	 * @see com.itmill.toolkit.data.Property.Viewer#setPropertyDataSource(Property)
	 */
	public void setPropertyDataSource(Property newDataSource) {
		if (newDataSource == null
				|| Date.class.isAssignableFrom(newDataSource.getType()))
			super.setPropertyDataSource(newDataSource);
		else
			throw new IllegalArgumentException(
					"DateField only supports Date properties");
	}

	/**
	 * Gets the resolution.
	 * 
	 * @return int
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Sets the resolution of the DateField.
	 * 
	 * @param resolution
	 *            the resolution to set.
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}

	/**
	 * Returns new instance calendar used in Date conversions.
	 * 
	 * Returns new clone of the calendar object initialized using the the
	 * current date (if available)
	 * 
	 * If this is no calendar is assigned the <code>Calendar.getInstance</code>
	 * is used.
	 * 
	 * @return the Calendar.
	 * @see #setCalendar(Calendar)
	 */
	private Calendar getCalendar() {

		// Makes sure we have an calendar instance
		if (this.calendar == null) {
			this.calendar = Calendar.getInstance();
		}

		// Clone the instance
		Calendar newCal = (Calendar) this.calendar.clone();

		// Assigns the current time tom calendar.
		Date currentDate = (Date) getValue();
		if (currentDate != null)
			newCal.setTime(currentDate);

		return newCal;
	}
}
