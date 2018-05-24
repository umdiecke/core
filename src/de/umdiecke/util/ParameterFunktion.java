/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Liste von möglichen Bearbeitungszuständen.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:25:11
 *
 */
public enum ParameterFunktion {

	REPLACE_BLANKS_WITH_UNDERLINING("rBwU", "Funktion zum Ersetzen von Leerzeichen durch Unterstriche."),
	TO_LOWER_CASE("tL", "Funktion zum Umwandeln zur Kleinschreibung."),
	TO_UPPER_CASE("tU", "Funktion zum Umwandeln zur Gro�schreibung."),
	UNKNOWN("", "Keine Funktion definiert!");

	private String abbreviation;
	private String description;

	private ParameterFunktion(final String abbreviation, final String description) {
		this.abbreviation = abbreviation;
		this.description = description;
	}

	public static ParameterFunktion fetchParameterFunktionen(final String name) {
		if (StringUtils.isNotBlank(name)) {
			try {
				return ParameterFunktion.valueOf(name.trim().toUpperCase());
			} catch (IllegalArgumentException ex) {
			}
		}
		return UNKNOWN;
	}

	public static ParameterFunktion fetchParameterFunktionenByAbbreviation(final String abbreviation) {
		if (StringUtils.isNotBlank(abbreviation)) {
			ParameterFunktion[] values = ParameterFunktion.values();
			for (ParameterFunktion parameterFunktion : values) {
				if (parameterFunktion.getAbbreviation().equalsIgnoreCase(StringUtils.trim(abbreviation))) {
					return parameterFunktion;
				}
			}
		}
		return UNKNOWN;
	}

	public static String applyFunctionsToContentString(final String functionParameter, final String contentString) {
		String result = contentString;
		String functionParameterNT = StringUtils.trim(functionParameter);
		if (StringUtils.isNotBlank(functionParameterNT) && functionParameterNT.matches("\\[.*\\].+")) {
			String strParameterList = StringUtils.trim(functionParameterNT.substring(functionParameterNT.indexOf('[') + 1, functionParameterNT.lastIndexOf(']')));

			if (StringUtils.isNotBlank(strParameterList)) {
				String[] functionArray = strParameterList.split("-");
				if (functionArray != null && functionArray.length > 0) {
					for (String function : functionArray) {
						ParameterFunktion parameterFunktion = ParameterFunktion.fetchParameterFunktionenByAbbreviation(function);
						result = parameterFunktion.run(result);
					}
				}
			}
		}
		return result;
	}

	public String run(final String contentString) {
		String returnParameter = contentString;
		if (StringUtils.isNotBlank(contentString)) {
			switch (this) {
			case REPLACE_BLANKS_WITH_UNDERLINING:
				returnParameter = contentString.replace(" ", "_");
				break;
			case TO_LOWER_CASE:
				returnParameter = contentString.toLowerCase();
				break;
			case TO_UPPER_CASE:
				returnParameter = contentString.toUpperCase();
				break;
			default:
				break;
			}
		}
		return returnParameter;
	}

	public static String extractParameterFromFunctionParameter(final String functionParameter) {
		String parameter = functionParameter;
		String functionParameterNT = StringUtils.trim(functionParameter);
		if (StringUtils.isNotBlank(functionParameterNT) && functionParameterNT.matches("\\[.*\\].+")) {
			parameter = StringUtils.trim(functionParameterNT.substring(functionParameterNT.lastIndexOf("]") + 1, functionParameterNT.length()));
		}
		return parameter;
	}


	/**
	 * @return the abbreviation
	 */
	public String getAbbreviation() {
		return this.abbreviation;
	}

	/**
	 * @param abbreviation
	 *            the abbreviation to set
	 */
	public void setAbbreviation(final String abbreviation) {
		this.abbreviation = abbreviation;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
}