/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.persistence.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Handler-Klasse für die Erzeugung von JAXB-Objekten aus XML-Dateien und umgekehrt.
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:50:01
 *
 * @param <T>
 *           Typ der zu verwalten Objekte.
 */
public class JAXBHandler<T> {

	/**
	 * Typ-Klasse der zu verwalten Objekte.
	 */
	private final Class<T> type;

	/**
	 * Default-Konstruktor
	 *
	 * @param type
	 *            Typ-Klasse der zu verwalten Objekte.
	 */
	public JAXBHandler(final Class<T> type) {
		this.type = type;
	}

	/**
	 * Erzeugung einer XML-Ausgabe-Stream aus einem JAXB-Objekt.
	 *
	 * @param createObject
	 *            Das zu �bersetzende JAXB-Objekt.
	 * @param outputStream
	 *            XML-Ausgabe-Stream
	 * @return true/false
	 * @throws JAXBException
	 */
	public boolean createXMLByObject(final T createObject, final OutputStream outputStream) throws JAXBException {
		boolean result = false;
		try {
			Marshaller marshaller = createMarshaller();
			marshaller.marshal(createObject, outputStream);
			result = true;

		} catch (JAXBException e) {
			throw new JAXBException(
					"Fehler bei der Ausgabe eines JAXB-Objekts" + ((createObject != null) ? " " + createObject.getClass().getName() : "") + " �ber einen OutputStream.", e);
		}
		return result;
	}

	/**
	 * Erzeugung einer XML-Datei aus einem JAXB-Objekt.
	 *
	 * @param createObject
	 *            Das zu �bersetzende JAXB-Objekt.
	 * @param file
	 *            Ausgabedatei
	 * @return true/false
	 * @throws JAXBException
	 */
	public boolean createXMLByObject(final T createObject, final File file) throws JAXBException {
		boolean result = false;
		try {
			Marshaller marshaller = createMarshaller();
			marshaller.marshal(createObject, file);
			result = true;
		} catch (JAXBException e) {
			throw new JAXBException("Fehler bei der Ausgabe eines JAXB-Objekts in eine Datei" + ((file != null) ? " (" + file.getAbsolutePath() + ")" : "") + ".", e);
		}
		return result;
	}

	/**
	 * Erzeugung eines JAXB-Objekt aus einer entsprechenden XML-Datei.
	 *
	 * @param file
	 *            XML-Datei
	 * @return �bersetztes JAXB-Objekt
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public T createObjectByXML(final File file) throws JAXBException {
		T resultObj = null;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			if (unmarshaller != null) {
				Object unmarshalObj = unmarshaller.unmarshal(file);
				if (this.type.isInstance(unmarshalObj)) {
					resultObj = (T) unmarshalObj;
				} else {
					throw new JAXBException(
							"Fehler beim Erzeugen eines JAXB-Objekts aus einer Datei" + ((file != null) ? " (" + file.getAbsolutePath() + ")" : "") + ". Erwartet wurde "
									+ this.type.getName() + " erzeugt wurde jedoch " + ((unmarshalObj != null) ? unmarshalObj.getClass().getName() : "null"));
				}
			}
		} catch (JAXBException e) {
			throw new JAXBException("Fehler beim Erzeugen eines JAXB-Objekts aus einer Datei" + ((file != null) ? " (" + file.getAbsolutePath() + ")" : "") + ".", e);
		}
		return resultObj;
	}

	/**
	 * Erzeugung eines JAXB-Objekt aus einem entsprechenden XML-InputStream.
	 *
	 * @param is
	 *           XML-InputStream
	 * @return Übersetztes JAXB-Objekt
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public T createObjectByXML(final InputStream is) throws JAXBException {
		T resultObj = null;
		try {
			Unmarshaller unmarshaller = createUnmarshaller();
			if (unmarshaller != null) {
				Object unmarshalObj = unmarshaller.unmarshal(is);
				if (this.type.isInstance(unmarshalObj)) {
					resultObj = (T) unmarshalObj;
				} else {
					throw new JAXBException("Fehler beim Erzeugen eines JAXB-Objekts aus einem InputStream. Erwartet wurde " + this.type.getName() + " erzeugt wurde jedoch "
							+ ((unmarshalObj != null) ? unmarshalObj.getClass().getName() : "null"));
				}
			}
		} catch (JAXBException e) {
			throw new JAXBException("Fehler beim Erzeugen eines JAXB-Objekts aus einem InputStream.", e);
		}
		return resultObj;
	}


	/**
	 * Erzeugung des Marshaller-Objekts.
	 *
	 * @return Marshaller-Objekt
	 * @throws JAXBException
	 */
	private Marshaller createMarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(this.type);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return marshaller;
	}

	/**
	 * Erzeugung eines Unmarshaller-Objekts.
	 *
	 * @return Unmarshaller-Objekt
	 * @throws JAXBException
	 */
	private Unmarshaller createUnmarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(this.type);
		return context.createUnmarshaller();
	}
}