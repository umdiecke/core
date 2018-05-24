/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Class with methods for handling date objects.
 * </p>
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:25:42
 *
 */
public class TimeUtil {

	protected static final Logger LOGGER = LogManager.getLogger();

	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ssZ";
	public static final TimeZone TIMEZONE_GMT = TimeZone.getTimeZone("GMT");

	/**
	 * <p>
	 * Enumeration with defined ranges of time in millis.
	 * </p>
	 *
	 * @author RHildebrand
	 *
	 */
	public enum TimePart {
		DAYS(86400000), HOURS(3600000), MINUTES(60000), SECONDS(1000), MILLIS(1);

		private long timePart;

		private TimePart(final long timePart) {
			this.timePart = timePart;
		}

		public long getTimePart() {
			return this.timePart;
		}
	}

	/**
	 * <p>
	 * Getting the time part between two calandar objects.
	 * </p>
	 *
	 * @param olderDate the older date
	 * @param youngerDate the younger date
	 * @param timePart the time part to get
	 *
	 * @return the time part
	 */
	public static long getTimePartInBetween(final Calendar olderDate, final Calendar youngerDate, final TimePart timePart) {
		long timeDifference = youngerDate.getTimeInMillis() - olderDate.getTimeInMillis();
		return TimeUtil.getTimePartForMillis(timeDifference, timePart);
	}

	/**
	 * <p>
	 * Getting the time part for the given time in millis.
	 * </p>
	 *
	 * @param millis the given time in millis
	 * @param timePart the time part to get
	 *
	 * @return the time part
	 */
	public static long getTimePartForMillis(final long millis, final TimePart timePart) {
		long result = millis;
		if (timePart != null) {
			result = millis / timePart.getTimePart();
		}
		return result;
	}

	/**
	 * <p>
	 * Getting the time part in millis for a given quantity.
	 * </p>
	 *
	 * @param quantity the quantity
	 * @param timePart the time part to get
	 * @return the time part
	 */
	public static long getMillisForTimePart(final long quantity, final long timePart) {
		return quantity * timePart;
	}

	/**
	 * <p>
	 * Formatting a date string into a gmt calendar object.
	 * </p>
	 *
	 * @param dateString the date string
	 *
	 * @return the gmt calendar object
	 */
	public static Calendar formatDateStringToGMTCalendar(final String dateString) {
		return TimeUtil.formatDateStringToCalendar(dateString, TimeUtil.TIMEZONE_GMT);
	}

	/**
	 * <p>
	 * Formatting a date string into a calendar object for the given time zone.
	 * </p>
	 *
	 * @param dateString the date string
	 * @param timezone the time zone
	 *
	 * @return the calendar object for the given time zone
	 */
	public static Calendar formatDateStringToCalendar(final String dateString, final TimeZone timezone) {
		return TimeUtil.formatDateStringToCalendar(dateString, TimeUtil.DATETIME_PATTERN, timezone);
	}

	/**
	 * <p>
	 * Formatting a date string into a calendar object with given date pattern.
	 * </p>
	 *
	 * @param dateString the date string
	 * @param pattern the date pattern
	 *
	 * @return the calendar object for the given date pattern
	 */
	public static Calendar formatDateStringToCalendar(final String dateString, final String pattern) {
		Calendar calendarResult = null;

		if (dateString != null && pattern != null) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				Date date = sdf.parse(dateString);
				calendarResult = Calendar.getInstance();
				calendarResult.setTime(date);
			} catch (ParseException e) {
				TimeUtil.LOGGER.warn("Error formatting datestring '" + dateString + "' to calendar with pattern '" + pattern + "'. Error: " + e.getMessage());
			} catch (IllegalArgumentException e) {
				TimeUtil.LOGGER.warn("Error formatting datestring '" + dateString + "' to calendar with pattern '" + pattern + "'. Error: " + e.getMessage());
			}
		}

		return calendarResult;
	}

	/**
	 * <p>
	 * Formatting a date string into a calendar object with given date pattern and the given time zone.
	 * </p>
	 *
	 * @param dateString the date string
	 * @param pattern the date pattern
	 * @param timezone the time zone
	 *
	 * @return the calendar object for the given date pattern and the given time zone.
	 */
	public static Calendar formatDateStringToCalendar(final String dateString, final String pattern, final TimeZone timezone) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timezone);
		Calendar cal = null;
		Date date;

		if (StringUtils.isNotEmpty(dateString)) {
			try {
				date = sdf.parse(dateString);
				date = TimeUtil.convertTimeZone(date, TimeZone.getDefault(), timezone);
				cal = Calendar.getInstance(timezone);
				cal.setTimeZone(timezone);
				cal.setTime(date);
			} catch (ParseException e) {
				TimeUtil.LOGGER.error("Can't format datestring to Calendar! Message: " + e.getMessage());
			}
		}

		return cal;
	}

	/**
	 * <p>
	 * Convert date object to calendar object.
	 * </p>
	 *
	 * @param date the date object
	 *
	 * @return the calendar object
	 */
	public static Calendar dateToCalendar(final Date date) {

		Calendar calendar = null;
		if (date != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		}
		return calendar;
	}

	/**
	 * <p>
	 * Convert date object to calendar object for given time zone.
	 * </p>
	 *
	 * @param date the date object
	 * @param timezone the time zone
	 *
	 * @return the calendar object
	 */
	public static Calendar dateToCalendar(Date date, TimeZone timezone) {

		Calendar calendar = null;
		if (date != null) {
			timezone = timezone != null ? timezone : TimeZone.getDefault();
			calendar = Calendar.getInstance(timezone);
			date = TimeUtil.convertTimeZone(date, TimeZone.getDefault(), timezone);
			calendar.setTimeZone(timezone);
			calendar.setTime(date);
		}

		return calendar;
	}

	/**
	 * <p>
	 * Formatting a calendar object to a gmt date string.
	 * </p>
	 *
	 * @param cal the calendar object
	 *
	 * @return the gmt date string
	 */
	public static String formatCalendarToGMTDateString(final Calendar cal) {
		return TimeUtil.formatCalendarToDateString(cal, TimeUtil.TIMEZONE_GMT);
	}

	/**
	 * <p>
	 * Formatting a calendar object to a date string with given time zone.
	 * </p>
	 *
	 * @param cal the calendar object
	 * @param timezone the given time zone
	 *
	 * @return the date string
	 */
	public static String formatCalendarToDateString(final Calendar cal, final TimeZone timezone) {
		return TimeUtil.formatCalendarToDateString(cal, TimeUtil.DATETIME_PATTERN, timezone);
	}

	/**
	 * <p>
	 * Formatting a calendar object to a date string with given time zone and pattern.
	 * </p>
	 *
	 * @param cal the given calendar object
	 * @param pattern the given pattern
	 * @param timezone the given time zone
	 *
	 * @return the date string
	 */
	public static String formatCalendarToDateString(final Calendar cal, final String pattern, final TimeZone timezone) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timezone);
		String dateString = null;
		Date date;
		if (cal != null && cal.getTime() != null) {
			date = cal.getTime();
			dateString = sdf.format(date);
		}
		return dateString;
	}

	/**
	 * <p>
	 * Format calendar object of any time zone to calendar object of gmt time zone.
	 * </p>
	 *
	 * @param cal the calendar object
	 * @return the calendar object of gmt time zone
	 */
	public static Calendar formatCalendarToGMTTimeZone(final Calendar cal) {
		return TimeUtil.formatCalendarToTimeZone(cal, TimeUtil.TIMEZONE_GMT);
	}

	public static Calendar formatCalendarToTimeZone(final Calendar cal, final TimeZone timezone) {
		Calendar calendar = null;
		if (cal != null && cal.getTime() != null) {
			String dateString = TimeUtil.formatCalendarToDateString(cal, timezone);
			calendar = TimeUtil.formatDateStringToCalendar(dateString, timezone);
		}
		return calendar;
	}

	/**
	 * <p>
	 * Converts the given <code>date</code> from the <code>fromTimeZone</code> to the <code>toTimeZone</code>. Since java.util.Date has does not really store time zone information,
	 * this actually converts the date to the date that it would be in the other time zone.
	 * </p>
	 *
	 * @param date the date object
	 * @param fromTimeZone the base time zone
	 * @param toTimeZone the target time zone
	 *
	 * @return hte date object in target time zone
	 */
	public static Date convertTimeZone(final Date date, final TimeZone fromTimeZone, final TimeZone toTimeZone) {
		long fromTimeZoneOffset = TimeUtil.getTimeZoneUTCAndDSTOffset(date, fromTimeZone);
		long toTimeZoneOffset = TimeUtil.getTimeZoneUTCAndDSTOffset(date, toTimeZone);

		return new Date(date.getTime() + (toTimeZoneOffset - fromTimeZoneOffset));
	}

	/**
	 * <p>
	 * Calculates the offset of the <code>timeZone</code> from UTC, factoring in any additional offset due to the time zone being in daylight savings time as of the given
	 * <code>date</code>.
	 * </p>
	 *
	 * @param date the date obejct
	 * @param timeZone the time zone
	 *
	 * @return the amount of time in milliseconds
	 */
	private static long getTimeZoneUTCAndDSTOffset(final Date date, final TimeZone timeZone) {
		long timeZoneDSTOffset = 0;
		if (timeZone.inDaylightTime(date)) {
			timeZoneDSTOffset = timeZone.getDSTSavings();
		}

		return timeZone.getRawOffset() + timeZoneDSTOffset;
	}

	/**
	 * <p>
	 * Calculate work days between two dates.
	 * </p>
	 *
	 * @param startCal the older date
	 * @param endCal the younger date
	 * @param calendarField the calendar object field id (e.g. java.util.Calendar.HOUR_OF_DAY)
	 * @param holidayDates a set of holidays. If null, no holidays are recognized.
	 * @param weekendDays a set of weekend days. If null the default weekend days (SATURDAY,SUNDAY) are recognized.
	 *
	 * @return the difference identified by calendarField (e.g. java.util.Calendar.HOUR_OF_DAY)
	 */
	public static int getWorkingTimeBetweenTwoDates(final Calendar startCal, final Calendar endCal, final int calendarField, final Set<Calendar> holidayDates, Set<Integer> weekendDays) {
		int fieldDifference = 0;

		Calendar startCalWork = Calendar.getInstance();
		Calendar endCalWork = Calendar.getInstance();

		// Preparation
		if (weekendDays == null) {
			// Set default weekend days
			weekendDays = new HashSet<>();
			weekendDays.add(Calendar.SATURDAY);
			weekendDays.add(Calendar.SUNDAY);
			if (TimeUtil.LOGGER.isDebugEnabled()) {
				TimeUtil.LOGGER.debug("No weekenddays defined! Use default instead. [SATURDAY,SUNDAY]");
			}
		}

		// Transform holiday dates to days of year
		Set<Integer> holidays = new HashSet<>();
		if (holidayDates != null && !holidayDates.isEmpty()) {
			for (Calendar holidayDate : holidayDates) {
				if (holidayDate != null) {
					holidays.add(holidayDate.get(Calendar.DAY_OF_YEAR));
				}
			}
		} else {
			if (TimeUtil.LOGGER.isDebugEnabled()) {
				TimeUtil.LOGGER.debug("No holidays defined! Ignore holidays.");
			}
		}

		if (startCal != null && endCal != null && (startCal.get(Calendar.DAY_OF_YEAR) != endCal.get(Calendar.DAY_OF_YEAR) || startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR))) {

			if (startCal.compareTo(endCal) > 0) {
				startCalWork.setTime(endCal.getTime());
				endCalWork.setTime(startCal.getTime());
			} else {
				startCalWork.setTime(startCal.getTime());
				endCalWork.setTime(endCal.getTime());
			}

			do {
				startCalWork.add(calendarField, 1);
				if (!weekendDays.contains(startCalWork.get(Calendar.DAY_OF_WEEK)) && !holidays.contains(startCalWork.get(Calendar.DAY_OF_YEAR))) {
					++fieldDifference;
				}

			} while (startCalWork.get(Calendar.YEAR) <= endCalWork.get(Calendar.YEAR) && startCalWork.get(Calendar.DAY_OF_YEAR) < endCalWork.get(Calendar.DAY_OF_YEAR));
		}

		return fieldDifference;
	}

	/**
	 * <p>
	 * Calculate a working date in past or future. Recognize holidays and weekend days.
	 * </p>
	 *
	 * @param baseCal the base date
	 * @param calendarField the calendar object field id (e.g. java.util.Calendar.HOUR_OF_DAY)
	 * @param fieldDifference the amount of time in past or future identified by parameter <i>calendarField</i> for the calculation if the target date
	 * @param getPastDate true a date in the past is calculated and false a date in the future is calculated
	 * @param holidayDates a set of holidays. If null, no holidays are recognized.
	 * @param weekendDays a set of weekend days. If null the default weekend days (SATURDAY,SUNDAY) are recognized.
	 *
	 * @return the calculated date (calendar object)
	 */
	public static Calendar getDistalWorkingDate(final Calendar baseCal, final int calendarField, int fieldDifference, final boolean getPastDate, final Set<Calendar> holidayDates, Set<Integer> weekendDays) {

		Calendar baseCalWork = Calendar.getInstance();

		// Preparation
		if (weekendDays == null) {
			// Set default weekend days
			weekendDays = new HashSet<>();
			weekendDays.add(Calendar.SATURDAY);
			weekendDays.add(Calendar.SUNDAY);
			if (TimeUtil.LOGGER.isDebugEnabled()) {
				TimeUtil.LOGGER.debug("No weekenddays defined! Use default instead. [SATURDAY,SUNDAY]");
			}
		}

		// Transform holiday dates to days of year
		Set<Integer> holidays = new HashSet<>();
		if (holidayDates != null && !holidayDates.isEmpty()) {
			for (Calendar holidayDate : holidayDates) {
				if (holidayDate != null) {
					holidays.add(holidayDate.get(Calendar.DAY_OF_YEAR));
				}
			}
		} else {
			if (TimeUtil.LOGGER.isDebugEnabled()) {
				TimeUtil.LOGGER.debug("No holidays defined! Ignore holidays.");
			}
		}

		if (baseCal != null) {
			baseCalWork.setTime(baseCal.getTime());

			do {

				if (getPastDate) {
					baseCalWork.add(calendarField, -1);
				} else {
					baseCalWork.add(calendarField, 1);
				}

				if (!weekendDays.contains(baseCalWork.get(Calendar.DAY_OF_WEEK)) && !holidays.contains(baseCalWork.get(Calendar.DAY_OF_YEAR))) {
					--fieldDifference;
				}

			} while (fieldDifference > 0);
		}

		return baseCalWork;
	}

	/**
	 * <p>
	 * Getting days between two dates.
	 * </p>
	 *
	 * @param startCal the start date
	 * @param endCal the end date
	 *
	 * @return a set of calendar objects. Each object represents one day.
	 */
	public static Set<Calendar> getDatesFromTo(final Calendar startCal, final Calendar endCal) {
		Set<Calendar> splitedDays = new HashSet<>();

		Calendar startCalWork = Calendar.getInstance();
		Calendar endCalWork = Calendar.getInstance();

		if (startCal != null && endCal != null) {

			if (startCal.compareTo(endCal) > 0) {
				startCalWork.setTime(endCal.getTime());
				endCalWork.setTime(startCal.getTime());
			} else {
				startCalWork.setTime(startCal.getTime());
				endCalWork.setTime(endCal.getTime());
			}

			do {
				Calendar storeCal = Calendar.getInstance();
				storeCal.setTime(startCalWork.getTime());
				splitedDays.add(storeCal);
				if (TimeUtil.LOGGER.isDebugEnabled()) {
					TimeUtil.LOGGER.debug("Date: " + storeCal.getTime());
				}

				startCalWork.add(Calendar.DAY_OF_MONTH, 1);

			} while (startCalWork.get(Calendar.YEAR) < endCalWork.get(Calendar.YEAR) || startCalWork.get(Calendar.DAY_OF_YEAR) <= endCalWork.get(Calendar.DAY_OF_YEAR));
		}
		return splitedDays;
	}
}