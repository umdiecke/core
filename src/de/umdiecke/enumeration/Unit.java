/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.enumeration;

import org.apache.commons.lang3.StringUtils;

/**
 * Enumeration von Einheiten.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:56:32
 *
 */
public enum Unit {

	/**
	 * Unit Euro
	 */
	EURO("�", "\u20AC", true, "Euro", "EUR"),
	/**
	 * Unit Prozent
	 */
	PROZENT("%", "\u0025", true, "Prozent", "Proz.");

	private final String sign;
	private final String encodedSign;
	private final boolean signSpace;
	private final String name;
	private final String abbreviation;

	/**
	 * Konstruktor
	 *
	 * @param sign
	 * @param name
	 * @param abbreviation
	 */
	private Unit(final String sign, final String encodedSign, final boolean signSpace, final String name, final String abbreviation) {
		this.sign = sign;
		this.encodedSign = encodedSign;
		this.signSpace = signSpace;
		this.name = name;
		this.abbreviation = abbreviation;
	}

	/**
	 * Hinzuf�gen einer Einheit hinter den Wert.
	 *
	 * @param val
	 *            Wert
	 * @return
	 */
	public String addUnitSign(final String val) {
		return addUnitSign(val, true);
	}

	/**
	 * Hinzuf�gen einer codierten Einheit hinter den Wert.
	 *
	 * @param val
	 *            Wert
	 * @return
	 */
	public String addEncodedUnitSign(final String val) {
		return addEncodedUnitSign(val, true);
	}

	/**
	 * Hinzuf�gen einer Einheit hinter bzw. vor den Wert.
	 *
	 * @param val
	 *            Wert
	 * @param after
	 *            True hinter den Wert und false vor den Wert
	 * @return
	 */
	public String addUnitSign(final String val, final boolean after) {
		String result = val;

		if (StringUtils.isNotBlank(val)) {
			if(after) {
				result += (this.signSpace ? " " : "") + getSign();
			} else {
				result = getSign() + " " + result;
			}
		}

		return result;
	}

	/**
	 * Hinzuf�gen einer codierten Einheit hinter bzw. vor den Wert.
	 *
	 * @param val
	 *            Wert
	 * @param after
	 *            True hinter den Wert und false vor den Wert
	 * @return
	 */
	public String addEncodedUnitSign(final String val, final boolean after) {
		String result = val;

		if (StringUtils.isNotBlank(val)) {
			if (after) {
				result += (this.signSpace ? " " : "") + getEncodedSign();
			} else {
				result = getEncodedSign() + " " + result;
			}
		}

		return result;
	}

	/**
	 * Hinzuf�gen einer Einheit hinter den Wert.
	 *
	 * @param val
	 *            Wert
	 * @return
	 */
	public String addUnitName(final String val) {
		return addUnitName(val, true);
	}

	/**
	 * Hinzuf�gen einer Einheit hinter bzw. vor den Wert.
	 *
	 * @param val
	 *            Wert
	 * @param after
	 *            True hinter den Wert und false vor den Wert
	 * @return
	 */
	public String addUnitName(final String val, final boolean after) {
		String result = val;

		if (StringUtils.isNotBlank(val)) {
			if (after) {
				result += " " + getName();
			} else {
				result = getName() + " " + result;
			}
		}

		return result;
	}

	/**
	 * Hinzuf�gen einer Einheit hinter den Wert.
	 *
	 * @param val
	 *            Wert
	 * @return
	 */
	public String addUnitAbbreviation(final String val) {
		return addUnitAbbreviation(val, true);
	}

	/**
	 * Hinzuf�gen einer Einheit hinter bzw. vor den Wert.
	 *
	 * @param val
	 *            Wert
	 * @param after
	 *            True hinter den Wert und false vor den Wert
	 * @return
	 */
	public String addUnitAbbreviation(final String val, final boolean after) {
		String result = val;

		if (StringUtils.isNotBlank(val)) {
			if (after) {
				result += " " + getAbbreviation();
			} else {
				result = getAbbreviation() + " " + result;
			}
		}

		return result;
	}

	/**
	 * @return the sign
	 */
	public String getSign() {
		return this.sign;
	}

	/**
	 * @return the encodedSign
	 */
	public String getEncodedSign() {
		return this.encodedSign;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return this.abbreviation;
	}
}
