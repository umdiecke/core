/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;


/**
 *
 * Utility class for handling mathematical operaions on BigDecimal instances.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:24:59
 *
 */
public abstract class MathUtil {

	public static final int DEFAULT_ROUNDING_VAL = 10;
	public static final BigDecimal BASE_100 = new BigDecimal("100");

	/**
	 * Hide the constructor. No object initializing possible.
	 */
	private MathUtil() {
		/* Nothing to do */
	}

	/**
	 * <p>
	 * Calculate rule of three: Performs a classical rule of three operation to calculate a part of the given value in referenceToBase.
	 * </p>
	 *
	 * @param referenceToBase
	 *           The value to calculate a proportion from.
	 * @param baseAmount
	 *           The amount to multiply the reference value with.
	 * @param base
	 *           The base by which to divide the previous multiplication.
	 *
	 * @return The part of referenceToBase that is proportional to the part of baseAmount from base.
	 */
	public static BigDecimal calcRO3(final BigDecimal referenceToBase, final BigDecimal baseAmount, final BigDecimal base) {
		BigDecimal result = BigDecimal.ZERO;

		if (MathUtil.notZero(referenceToBase) && MathUtil.notZero(baseAmount) && MathUtil.notZero(base)) {
			result = referenceToBase.multiply(baseAmount).divide(base, MathUtil.DEFAULT_ROUNDING_VAL, RoundingMode.HALF_UP);
		}

		return result;
	}

	/**
	 *
	 * <p>
	 * Allow conversion of amounts between a local currency and a foreign currency.
	 * </p>
	 *
	 * @param exchangeRate The exchange rate between the two currencies.
	 * @param baseAmount The amount to convert.
	 * @param fromLcToFc Decided the direction of the conversion.
	 *
	 * @return The converted amount, or ZERO if the exchange rate or the base amount is ZERO.
	 */
	public static BigDecimal translateCurrency(final BigDecimal exchangeRate, final BigDecimal baseAmount, final boolean fromLcToFc) {
		BigDecimal result = BigDecimal.ZERO;
		if (MathUtil.notZero(exchangeRate) && MathUtil.notZero(baseAmount)) {
			if (fromLcToFc) {
				result = baseAmount.divide(exchangeRate, MathUtil.DEFAULT_ROUNDING_VAL, RoundingMode.HALF_UP);
			} else {
				result = baseAmount.multiply(exchangeRate).setScale(MathUtil.DEFAULT_ROUNDING_VAL, RoundingMode.HALF_UP);
			}
		}
		return result;
	}

	/**
	 * �berpr�ft einen formatierten W�hrungsbetrag (z.B. 12,00) und gibt einen Leerstring zur�ck, wenn der Betrag 0 ist, ansonsten den Eingabebetrag.
	 *
	 * @param value
	 * @return
	 */
	public static String formatCurrencyValueNotZero(final String value) {
		if (StringUtils.isEmpty(value) || value.equals("0,00")) {
			return "";
		}

		return value;
	}

	/**
	 * Parst den Currency String Wert und erzeugt ein BigDecimal.
	 *
	 * @param value
	 * @return
	 */
	public static BigDecimal parseCurrencyValue(final String value) {
		if (StringUtils.isEmpty(value)) {
			return BigDecimal.ZERO;
		}

		String strValue = value.replace(",", ".");
		double dValue = Double.parseDouble(strValue);
		return BigDecimal.valueOf(dValue);
	}

	/**
	 *
	 * <p>
	 * Resets the scale of a given BigDecimal value.
	 * </p>
	 *
	 * @param value
	 * @param newScale
	 * @param roundingMode
	 * @return
	 */
	public static BigDecimal reScale(final BigDecimal value, final int newScale, final RoundingMode roundingMode) {

		if (value == null) {
			return null;
		}
		return value.setScale(newScale, roundingMode);
	}

	/**
	 * Compare BigDecimals for equality.
	 *
	 * @param base the base value
	 * @param comperator the value to compare with
	 *
	 * @return TRUE, if valid else FALSE
	 */
	public static boolean compare(final BigDecimal base, final BigDecimal comperator) {
		return MathUtil.compare(base, comperator, -1, null);
	}

	/**
	 * Compare BigDecimals for equality.
	 *
	 * @param base the base value
	 * @param comperator the value to compare with
	 * @param scale the scale for rounding (e.g. 2 for two decimal places 1.00 )
	 * @param tolerance the tolerance. (e.g. (comparator - tolerance) leq base leq (comparator + tolerance))
	 *
	 * @return TRUE, if valid else FALSE
	 */
	public static boolean compare(final BigDecimal base, final BigDecimal comperator, final int scale, final RoundingMode roundingMode) {
		return MathUtil.compare(base, comperator, scale, roundingMode, null);
	}

	/**
	 * Compare BigDecimals for equality.
	 *
	 * @param base the base value
	 * @param comperator the value to compare with
	 * @param tolerance the tolerance. (e.g. (comparator - tolerance) leq base leq (comperator + tolerance))
	 *
	 * @return TRUE, if valid else FALSE
	 */
	public static boolean compare(final BigDecimal base, final BigDecimal comperator, final BigDecimal tolerance) {
		return MathUtil.compare(base, comperator, -1, null, tolerance);
	}

	/**
	 * Compare BigDecimals for equality.
	 *
	 * @param base the base value
	 * @param comperator the value to compare with
	 * @param scale the scale for rounding (e.g. 2 for two decimal places 1.00 )
	 * @param roundingMode the rounding mode (e.g. RoundingMode.HALF_UP)
	 * @param tolerance the tolerance. (e.g. (comperator - tolerance) leq base leq (comperator + tolerance))
	 *
	 * @return TRUE, if valid else FALSE
	 */
	public static boolean compare(final BigDecimal base, final BigDecimal comperator, final int scale, final RoundingMode roundingMode, final BigDecimal tolerance) {
		boolean result = false;

		BigDecimal calcBase = base;
		BigDecimal calcComperator = comperator;

		// Rundungsbasis setzen
		if (roundingMode != null && scale != -1) {
			calcBase = calcBase.setScale(scale, roundingMode);
			calcComperator = calcComperator.setScale(scale, roundingMode);
		}

		// First check
		if (calcBase.compareTo(calcComperator) == 0) {
			result = true;
		} else if (tolerance != null) {
			// Second check with tolerance +- x
			BigDecimal leftComperator = comperator.subtract(tolerance);
			BigDecimal rightComperator = comperator.add(tolerance);
			// If leftComperator <= calcBase <= rightComperator
			if (leftComperator.compareTo(calcBase) <= 0 && calcBase.compareTo(rightComperator) <= 0) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Check if the BigDecimal value is null or zero.
	 *
	 * @return TRUE, if the value is null or zero else FALSE
	 */
	public static boolean notZero(final BigDecimal bigDacimalValue) {
		return bigDacimalValue != null && bigDacimalValue.compareTo(BigDecimal.ZERO) != 0;
	}

	/**
	 * Check if the BigInteger value is null or zero.
	 *
	 * @return TRUE, if the value is null or zero else FALSE
	 */
	public static boolean notZero(final BigInteger bigIntegerValue) {
		return bigIntegerValue != null && bigIntegerValue.compareTo(BigInteger.ZERO) != 0;
	}

	/**
	 * Prüfen eines Strings, ob er nur aus Ziffern zusammengesetzt ist.
	 *
	 * @param str
	 *           Der zu pr�fenden String.
	 * @return True, wenn der String nur aus Ziffern zusammen gesetzt ist und sonst false.
	 */
	public static boolean numeric(final String str) {
		if (str == null) {
			return false;
		}
		return str.matches("-?\\d+");
	}
}