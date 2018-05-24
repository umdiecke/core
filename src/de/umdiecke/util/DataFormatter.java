/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:24:12
 *
 */
public class DataFormatter {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Default-Datumsformat (dd.MM.yyyy)
	 */
	public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

	/**
	 * Datumsformat (dd.MM.yy)
	 */
	public static final String DATE_FORMAT_SHORT = "dd.MM.yy";

	/**
	 * Zahlenformat f�r Betr�ge (BigDecimal)
	 */
	private static final ThreadLocal<NumberFormat> NF = new ThreadLocal<>() {
		@Override
		protected NumberFormat initialValue() {

			NumberFormat threadNF = NumberFormat.getNumberInstance(Locale.GERMANY);
			threadNF.setMinimumFractionDigits(2);
			threadNF.setMaximumFractionDigits(2);
			return threadNF;
		}
	};

	/**
	 * Formatiert ein Object zu einem String, soweit eine Formatierungsmethode
	 * vorgesehen ist. Ist dem nicht so, wird ein Leer-String zur�ckgegeben.
	 *
	 * @param objValue
	 *            R�ckgabewert als Objekt
	 * @return formatierter R�ckgebestring bzw. Leer-String, wenn das
	 *         Formatierungsobjekt undefiniert ist.
	 */
	public static String formatObjectToString(final Object objValue) {
		String strValue = "";

		if (objValue == null) {
			return strValue;
		}

		if (objValue instanceof String) {
			strValue = StringUtils.trim((String) objValue);
		} else if (objValue instanceof Boolean) {
			strValue = DataFormatter.booleanToString((Boolean) objValue);
		} else if (objValue instanceof Long) {
			strValue = DataFormatter.longToString((Long) objValue);
		} else if (objValue instanceof BigDecimal) {
			strValue = DataFormatter.bigdecimalToString((BigDecimal) objValue);
		} else if (objValue instanceof BigInteger) {
			strValue = DataFormatter.bigintegerToString((BigInteger) objValue);
		} else if (objValue instanceof Double) {
			strValue = DataFormatter.doubleToString((Double) objValue);
		} else if (objValue instanceof Integer) {
			strValue = StringUtils.trim(String.valueOf(objValue));
		} else if (objValue instanceof Date) {
			strValue = DataFormatter.convertDate((Date) objValue);
		} else {
			DataFormatter.LOGGER.warn("Die Formatierung des Objektes in einen String, wird für den Typ '" + objValue.getClass().getName() + "' nicht unterstützt.");
		}
		return strValue;
	}

	public static Long formatObjectToLong(final Object objValue) {
		Long value = null;

		if (objValue == null) {
			return value;
		}

		value = Long.valueOf(DataFormatter.formatObjectToPrimitiveLong(objValue));
		return value;
	}

	public static long formatObjectToPrimitiveLong(final Object objValue) {
		long value = -1;

		if (objValue == null) {
			return value;
		}

		if (objValue instanceof String) {
			if (MathUtil.numeric((String) objValue)) {
				value = Long.valueOf((String) objValue);
			}
		} else if (objValue instanceof Boolean) {
			value = (Boolean) objValue ? 1 : 0;
		} else if (objValue instanceof Long) {
			value = (Long) objValue;
		} else if (objValue instanceof BigDecimal) {
			value = ((BigDecimal) objValue).longValue();
		} else if (objValue instanceof BigInteger) {
			value = ((BigInteger) objValue).longValue();
		} else if (objValue instanceof Double) {
			value = ((Double) objValue).longValue();
		} else if (objValue instanceof Integer) {
			value = ((Integer) objValue).longValue();
		} else if (objValue instanceof Date) {
			value = ((Date) objValue).getTime();
		} else {
			DataFormatter.LOGGER.warn("Die Formatierung des Objektes in einen long, wird für den Typ '" + objValue.getClass().getName() + "' nicht unterstützt.");
		}
		return value;
	}

	public static Integer formatObjectToInteger(final Object objValue) {
		Integer value = null;

		if (objValue == null) {
			return value;
		}

		value = Integer.valueOf(DataFormatter.formatObjectToPrimitiveInteger(objValue));
		return value;
	}

	public static int formatObjectToPrimitiveInteger(final Object objValue) {
		int value = -1;

		if (objValue == null) {
			return value;
		}

		if (objValue instanceof String) {
			if (MathUtil.numeric((String) objValue)) {
				value = Integer.valueOf((String) objValue);
			}
		} else if (objValue instanceof Boolean) {
			value = (Boolean) objValue ? 1 : 0;
		} else if (objValue instanceof Long) {
			value = ((Long) objValue).intValue();
		} else if (objValue instanceof BigDecimal) {
			value = ((BigDecimal) objValue).intValue();
		} else if (objValue instanceof BigInteger) {
			value = ((BigInteger) objValue).intValue();
		} else if (objValue instanceof Double) {
			value = ((Double) objValue).intValue();
		} else if (objValue instanceof Integer) {
			value = (Integer) objValue;
		} else if (objValue instanceof Date) {
			value = (int) ((Date) objValue).getTime();
		} else {
			DataFormatter.LOGGER.warn("Die Formatierung des Objektes in einen long, wird für den Typ '" + objValue.getClass().getName() + "' nicht unterstützt.");
		}
		return value;
	}

	/**
	 * Formatiert einen Datumsstring von "yyyyMMdd" nach "dd.MM.yyyy", wobei
	 * unbekannte oder teilweise unbekannte Datumswerte als Leerstring
	 * zur�ckgeliefert werden.
	 *
	 * @param sDate
	 *            Datumsstring im Format "yyyyMMdd"
	 * @return Datumsstring im Format "dd.MM.yyyy"
	 */
	public static String convertDate(final String sDate) {
		return DataFormatter.convertDate(sDate, "yyyyMMdd", DataFormatter.DEFAULT_DATE_FORMAT);
	}


	/**
	 * Formatiert einen Datumsstring vom dateInPattern in dateOutPattern, wobei
	 * unbekannte oder teilweise unbekannte Datumswerte als Leerstring
	 * zur�ckgeliefert werden.
	 *
	 * @param sDate
	 *            Datumsstring im Format dateInPattern. Im default wird
	 *            "yyyyMMdd" angenommen.
	 * @param dateInPattern
	 *            Eingabeformat des Datums
	 * @param dateOutPattern
	 *            Ausgabeformat des Datums
	 * @return Datumsstring im Format dateOutPattern oder wenn dieses leer ist
	 *         im Format "dd.MM.yyyy"
	 */
	public static String convertDate(final String sDate, final String dateInPattern, final String dateOutPattern) {
		if (StringUtils.isNotBlank(sDate)) {
			try {
				String patternIn = StringUtils.isNotBlank(dateInPattern) ? dateInPattern : "yyyyMMdd";
				Date dateIn = new SimpleDateFormat(patternIn).parse(sDate);
				return DataFormatter.convertDate(dateIn, dateOutPattern);
			} catch (ParseException e) {
				return sDate;
			}
		}
		return "";
	}

	/**
	 * Formatiert ein Datumsobjekt, wobei unbekannte oder teilweise unbekannte
	 * Datumswerte als Leerstring zur�ckgeliefert werden.
	 *
	 * @param dateIn
	 *            Datum
	 * @return Datumsstring im Format "dd.MM.yyyy"
	 */
	public static String convertDate(final Date dateIn) {
		return DataFormatter.convertDate(dateIn, DataFormatter.DEFAULT_DATE_FORMAT);
	}

	/**
	 * Formatiert ein Datumsobjekt (Joda-Time), wobei unbekannte oder teilweise unbekannte Datumswerte als Leerstring zur�ckgeliefert werden.
	 *
	 * @param dateIn
	 * @param dateOutPattern
	 * @return
	 */
	public static String convertDateTime(final DateTime dateIn, final String dateOutPattern) {
		String pattern = StringUtils.isNotBlank(dateOutPattern) ? dateOutPattern : DataFormatter.DEFAULT_DATE_FORMAT;
		return dateIn.toString(pattern);
	}

	/**
	 * Formatiert ein Datumsobjekt, wobei unbekannte oder teilweise unbekannte
	 * Datumswerte als Leerstring zur�ckgeliefert werden.
	 *
	 * @param dateIn
	 *            Datum
	 * @param dateOutPattern
	 *            Ausgabeformat des Datums
	 * @return Datumsstring im Format dateOutPattern oder wenn dieses leer ist
	 *         im Format "dd.MM.yyyy"
	 */
	public static String convertDate(final Date dateIn, final String dateOutPattern) {
		String pattern = StringUtils.isNotBlank(dateOutPattern) ? dateOutPattern : DataFormatter.DEFAULT_DATE_FORMAT;
		return new SimpleDateFormat(pattern).format(dateIn);
	}

	/**
	 * Konvertiert Double in String.
	 *
	 * @param val
	 *            Double-Value
	 * @return Double als String
	 */
	public static String doubleToString(final Double val) {
		if (val == null) {
			return "";
		}
		return Double.toString(val);
	}

	/**
	 * Konvertiert Long in String.
	 *
	 * @param val
	 *            Long-Value
	 * @return Long als String
	 */
	public static String longToString(final Long val) {
		if (val == null) {
			return "";
		}
		return Long.toString(val);
	}

	/**
	 * Konvertiert Integer in String.
	 *
	 * @param val
	 *            Integer-Value
	 * @return Integer als String
	 */
	public static String integerToString(final Integer val) {
		if (val == null) {
			return "";
		}
		return Integer.toString(val);
	}

	/**
	 * Konvertiert Betr�ge (BigDecimal) in String.
	 *
	 * @param val
	 *            BigDecimal-Value
	 * @return BigDecimal als String
	 */
	public static String bigdecimalToString(final BigDecimal val) {
		return DataFormatter.bigdecimalToString(val, 2);
	}

	/**
	 * Konvertiert Betr�ge (BigDecimal) in String.
	 *
	 * @param val
	 *            BigDecimal-Value
	 * @param fractionDigits
	 *            Anzahl der Nachkommastellen
	 * @return BigDecimal als String
	 */
	public static String bigdecimalToString(final BigDecimal val, final int fractionDigits) {
		return DataFormatter.bigdecimalToString(val, fractionDigits, false);
	}

	/**
	 * Konvertiert Betr�ge (BigDecimal) in String.
	 *
	 * @param val
	 *            BigDecimal-Value
	 * @param fractionDigits
	 *            Anzahl der Nachkommastellen
	 * @param removeFractionWhenZero
	 *            Wenn true und der Nachkommaanteil = 0 ist, sollen keine
	 *            Nachkommastellen angezeigt werden
	 * @return BigDecimal als String
	 */
	public static String bigdecimalToString(final BigDecimal val, final int fractionDigits, final boolean removeFractionWhenZero) {
		if (val == null) {
			return "";
		}

		String result;
		if (fractionDigits == 2 || fractionDigits < 0) {
			result = DataFormatter.NF.get().format(val.doubleValue());
		} else {
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
			numberFormat.setMinimumFractionDigits(fractionDigits);
			numberFormat.setMaximumFractionDigits(fractionDigits);
			result = numberFormat.format(val.doubleValue());
		}

		// das Minuszeichen vom Betrag mit einem Leerzeichen trennen
		if (result.length() > 1 && result.charAt(0) == '-' && result.charAt(1) != ' ') {
			result = result.replace("-", "- ");
		}

		// Wenn removeFractionWhenZero = true und der Nachkommaanteil = 0 ist,
		// sollen keine Nachkommastellen angezeigt werden
		if (removeFractionWhenZero && result.endsWith(",00")) {
			result = result.substring(0, result.lastIndexOf(","));
		}

		return result;
	}

	/**
	 * Konvertiert Werte (BigInteger) in String.
	 *
	 * @param val
	 *            BigInteger-Value
	 * @return BigInteger als String
	 */
	public static String bigintegerToString(final BigInteger val) {
		if (val == null) {
			return "";
		}
		String result = val.toString();
		return result;
	}

	/**
	 * Formatiert einen Booleschen Wert in den Text "Ja"/"Nein" um, wobei ein
	 * <code>null</code> Wert als "Nein" interpretiert wird.
	 *
	 * @param pBool
	 *            boolescher Wert
	 * @return Ja/Nein
	 */
	public static String booleanToString(final Boolean pBool) {
		String value = "Nein";
		if (pBool != null) {
			value = pBool.booleanValue() ? "Ja" : "Nein";
		}
		return value;
	}

	/**
	 * Konkateniert die Inhalte des pStrArray (trimmed) und verkn�pft diese mit
	 * den Trennzeichen des pDelimArray.
	 *
	 * @param pStrArray
	 *            Array der Strings, wenn ein Eintrag getrimmed leer ist, wird
	 *            kein Trennzeichen davor eingef�gt
	 * @param pDelimArray
	 *            Array der Trennzeichen (die L�nge muss pStrArray.length - 1
	 *            betragen)
	 * @return Konkatenierter String
	 */
	public static String concatTrimStr(final String[] pStrArray, final String[] pDelimArray) {
		StringBuilder sb = new StringBuilder(200);
		if (pStrArray != null && pDelimArray != null && pStrArray.length > 0 && pStrArray.length == pDelimArray.length + 1) {
			sb = sb.append(StringUtils.trim(pStrArray[0]));
			for (int i = 1; i < pStrArray.length; i++) {
				String d = pDelimArray[i - 1];
				String s = StringUtils.trim(pStrArray[i]);
				if (StringUtils.isNotBlank(s)) {
					if (sb.toString().isEmpty()) {
						sb = sb.append(s);
					} else {
						sb = sb.append(d).append(s);
					}
				}
			}
		}
		return sb.toString().trim();
	}

	/**
	 * Check if the Boolean value is null or Boolean.FALSE.
	 *
	 * @param bool
	 *            the Boolean value
	 * @return TRUE, if the value is null or FALSE
	 */
	public static boolean isNullOrFalse(final Boolean bool) {
		return bool == null || !bool.booleanValue();
	}

	/**
	 * Pr�ft, ob zwei String-Parameter gleich sind. Dabei ist das Ergebnis auch
	 * Wahr, wenn beide Strings Null sind.
	 *
	 * @param str1
	 *            String-Parameter 1
	 * @param str2
	 *            String-Parameter 2
	 * @return True, wenn beide String-Parameter gleich sind.
	 */
	public static boolean isEquals(final String str1, final String str2) {
		boolean result = false;
		if (str1 == null && str2 == null) {
			result = true;
		} else if (str1 != null && str1.equals(str2)) {
			result = true;
		}
		return result;
	}
}