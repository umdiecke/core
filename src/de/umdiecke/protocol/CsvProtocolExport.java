/**
 * CCT-Tool (Crypto Currency Tax Tool)
 *
 * Erstellt 2018 von <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 */
package de.umdiecke.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.umdiecke.util.DataFormatter;
import de.umdiecke.util.MathUtil;

/**
 * TODO RHildebrand JavaDoc
 *
 * @author <a href="mailto:umdiecke@gmx.de">René Hildebrand</a>
 * @version 1.0
 * @since 24.05.2018, 12:28:06
 *
 */
public class CsvProtocolExport implements IProtocolExport {

	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private static final String CONST_FIELD_BESCHREIBUNG = "Beschreibung";
	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private static final String CONST_FIELD_PROTOKOLL_ZEILE = "Protokollzeile";
	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private static final String CONST_FIELD_PROTOKOLL_ID = "Protokoll-ID";
	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final File csvFileCommonData;
	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final File csvFileElemetData;
	/**
	 * FIXME RHildebrand JavaDoc
	 */
	private final boolean appendElementData;

	private static long exportedProtocolElementLines;

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param csvFileCommonData
	 * @param csvFileElemetData
	 */
	public CsvProtocolExport(final File csvFileCommonData, final File csvFileElemetData) {
		this(csvFileCommonData, csvFileElemetData, true);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param csvFileCommonData
	 * @param csvFileElemetData
	 * @param appendElementData
	 */
	public CsvProtocolExport(final File csvFileCommonData, final File csvFileElemetData, final boolean appendElementData) {
		this.csvFileCommonData = csvFileCommonData;
		this.csvFileElemetData = csvFileElemetData;
		this.appendElementData = appendElementData;
		CsvProtocolExport.exportedProtocolElementLines = 0;
	}

	@Override
	public boolean doExport(final ProtocolElement commonProtocolElement, final List<ProtocolElement> protocolElementList) throws Exception {

		boolean checkFileCommonData = checkFile(this.csvFileCommonData);
		boolean checkFileElemetData = checkFile(this.csvFileElemetData);
		boolean result = checkFileCommonData || checkFileElemetData;

		if (!this.appendElementData) {
			CsvProtocolExport.exportedProtocolElementLines = 0;
		}

		if (result && checkFileCommonData) {
			result = exportCommonData(commonProtocolElement);
		}

		if (result && checkFileElemetData) {
			result = exportElementData(protocolElementList);
		}

		return result;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolHandler
	 * @throws IOException
	 */
	private boolean exportCommonData(final ProtocolElement commonProtocolElement) throws IOException {

		List<String> headlines = new ArrayList<>();
		List<ProtocolLine> protocolLines = new ArrayList<>();

		addProtocolLine(headlines, protocolLines, 1, commonProtocolElement);
		return writeDataToCsvFile(getCsvFileCommonData(), headlines, protocolLines, false);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolHandler
	 * @param result
	 * @return
	 * @throws IOException
	 */
	private boolean exportElementData(final List<ProtocolElement> protocolElementList) throws IOException {
		boolean result = true;

		List<String> headlines = new ArrayList<>();
		List<ProtocolLine> protocolLines = new ArrayList<>();

		if (protocolElementList != null && !protocolElementList.isEmpty()) {
			long lineCount = CsvProtocolExport.exportedProtocolElementLines + 1;
			for (ProtocolElement protocolElement : protocolElementList) {
				lineCount = addProtocolLine(headlines, protocolLines, lineCount, protocolElement);
			}

			CsvProtocolExport.exportedProtocolElementLines = lineCount - 1;
			result = writeDataToCsvFile(getCsvFileElemetData(), headlines, protocolLines, true);
		}
		return result;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private boolean writeDataToCsvFile(final File csvFile, final List<String> headlines, final List<ProtocolLine> protocolLines, final boolean horizontal) throws IOException {
		boolean result = false;

		List<String> headlinesWork = headlines;

		// D�rfen Datens�tze angef�gt werden?
		boolean append = this.appendElementData && horizontal;

		// Wird eine neue Kopfzeile ben�tigt?
		boolean headlineNeeded = !append || csvFile.length() == 0l;

		// Wenn n�tig, wird die Protokolldatei mit neuen Spalten aktualisiert
		if (!headlineNeeded && isProtocolFileRefreshNeeded(csvFile, headlines)) {
			HeaderRefreshResult headerRefreshResult = refreshProtocolFileWithNewHeaders(csvFile, headlines);
			headlinesWork = headerRefreshResult.getNewHeaderList();
			refreshProtocolLineNumber(headlinesWork, protocolLines, headerRefreshResult.getNextLineNumber());
		} else {
			String currentHeadline = readHeadlineFromFile(csvFile);
			List<String> currentHeadlineList = headlineToList(currentHeadline);
			if (!currentHeadlineList.isEmpty()) {
				headlinesWork = currentHeadlineList;
			}
		}

		// Schreiben der Datei
		try (FileOutputStream fos = new FileOutputStream(csvFile, append);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));) {

			if (headlineNeeded) {
				// add BOM
				writer.write('\ufeff');
			}

			if (horizontal) {
				writeDataHorizontal(writer, headlinesWork, protocolLines, headlineNeeded);
			} else {
				writeDataVertical(writer, headlines, protocolLines);
			}

			writer.flush();
			fos.flush();

			result = true;
		}

		return result;
	}


	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param writer
	 * @param headlines
	 * @param protocolLines
	 * @throws IOException
	 */
	private void writeDataHorizontal(final BufferedWriter writer, final List<String> headlines, final List<ProtocolLine> protocolLines, final boolean headlineNeeded)
			throws IOException {

		if (headlineNeeded) {
			// add Headline
			writer.write(generateHorizontalCsvHeaderLine(headlines).toString());
			writer.write("\r\n");
			writer.flush();
		}

		// add ContentLines
		for (ProtocolLine protocolLine : protocolLines) {
			writer.write(generateHorizontalCsvContentLine(headlines, protocolLine).toString());
			writer.write("\r\n");
			writer.flush();
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @return
	 */
	private StringBuilder generateHorizontalCsvHeaderLine(final List<String> headlines) {
		StringBuilder headerBuilder = new StringBuilder();
		for (String header : headlines) {
			if (StringUtils.isNotBlank(headerBuilder.toString())) {
				headerBuilder.append(";");
			}
			headerBuilder.append("\"" + csvClean(header) + "\"");
		}
		return headerBuilder;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolLine
	 */
	private StringBuilder generateHorizontalCsvContentLine(final List<String> headlines, final ProtocolLine protocolLine) {
		StringBuilder lineBuilder = new StringBuilder();
		if (protocolLine != null) {
			for (String headline : headlines) {

				if (StringUtils.isNotBlank(lineBuilder.toString())) {
					lineBuilder.append(";");
				}

				lineBuilder.append("\"" + csvClean(protocolLine.getCellContent(headline)) + "\"");
			}
		}
		return lineBuilder;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param writer
	 * @param headlines
	 * @param protocolLines
	 * @throws IOException
	 */
	private void writeDataVertical(final BufferedWriter writer, final List<String> headlines, final List<ProtocolLine> protocolLines) throws IOException {

		// Verticalen Header erzeugen
		final Set<String> verticalHeadlines = new LinkedHashSet<>();
		verticalHeadlines.add(CsvProtocolExport.CONST_FIELD_BESCHREIBUNG);
		for (ProtocolLine protocolLine : protocolLines) {
			if (protocolLine != null) {
				verticalHeadlines.add(protocolLine.getCellContent(CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID));
			}
		}

		// add Headline
		writer.write(generateVerticalCsvHeaderLine(verticalHeadlines).toString());
		writer.write("\r\n");
		writer.flush();

		// add ContentLines
		for (String headline : headlines) {
			if (StringUtils.isNotBlank(headline) && !CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID.equals(headline)) {
				writer.write(generateVerticalCsvContentLine(headline, protocolLines).toString());
				writer.write("\r\n");
				writer.flush();
			}
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @return
	 */
	private StringBuilder generateVerticalCsvHeaderLine(final Set<String> headlines) {
		StringBuilder headerBuilder = new StringBuilder();
		for (String header : headlines) {
			if (StringUtils.isNotBlank(headerBuilder.toString())) {
				headerBuilder.append(";");
			}
			headerBuilder.append("\"" + csvClean(header) + "\"");
		}
		return headerBuilder;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolLine
	 */
	private StringBuilder generateVerticalCsvContentLine(final String headline, final List<ProtocolLine> protocolLines) {

		StringBuilder lineBuilder = new StringBuilder();

		lineBuilder.append("\"" + csvClean(headline) + "\"");
		for (ProtocolLine protocolLine : protocolLines) {
			if (protocolLine != null) {
				lineBuilder.append(";");
				lineBuilder.append("\"" + csvClean(protocolLine.getCellContent(headline)) + "\"");
			}
		}
		return lineBuilder;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param dirtyContent
	 * @return
	 */
	private String csvClean(final String dirtyContent) {
		return dirtyContent.replace("\"", "'");
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param lineNr
	 * @param protocolElement
	 */
	protected long addProtocolLine(final List<String> headlines, final List<ProtocolLine> protocolLines, final long lineNr, final ProtocolElement protocolElement) {
		long lNr = lineNr;
		if (protocolElement != null) {
			ProtocolLine protocolLine = new ProtocolLine();

			// Add Base Cells
			addLineCell(headlines, protocolLine, CsvProtocolExport.CONST_FIELD_PROTOKOLL_ZEILE, String.valueOf(lineNr));
			addLineCell(headlines, protocolLine, CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID, protocolElement.getId() != null ? protocolElement.getId() : "ID_" + lineNr);

			// Add Content-Values
			addContentValuesToLine(headlines, protocolElement, protocolLine);

			// Add Custom-Values
			addCustomLineCells(protocolElement, protocolLine);

			protocolLines.add(protocolLine);
			lNr++;
		}
		return lNr;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolLine
	 * @param protocolElement
	 */
	private void addContentValuesToLine(final List<String> headlines, final ProtocolElement protocolElement, final ProtocolLine protocolLine) {
		Map<String, Object> contentValueMap = protocolElement.getContentValueMap();
		if (contentValueMap != null && !contentValueMap.isEmpty()) {
			for (Entry<String, Object> contentValue : contentValueMap.entrySet()) {
				addLineCell(headlines, protocolLine, contentValue.getKey(), contentValue.getValue());
			}
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param headerName
	 * @param value
	 * @param protocolLine
	 */
	protected void addLineCell(final List<String> headlines, final ProtocolLine protocolLine, final String headerName, final Object value) {
		if (StringUtils.isNotBlank(headerName)) {
			if (!headlines.contains(headerName)) {
				headlines.add(headerName);
			}
			protocolLine.addLineCell(headerName, value != null ? DataFormatter.formatObjectToString(value) : "");
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param protocolElement
	 * @param protocolLine
	 */
	protected void addCustomLineCells(final ProtocolElement protocolElement, final ProtocolLine protocolLine) {
		// nothing to do here
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param file
	 * @throws IOException
	 */
	private boolean checkFile(final File file) throws IOException {
		boolean result = false;

		if (file != null) {

			if (file.exists()) {
				result = isFileWritable(file, result);
			} else {
				File path = file.getParentFile();
				path.mkdirs();
				file.createNewFile();
				result = isFileWritable(file, result);
			}
		}
		return result;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param file
	 * @param result
	 * @return
	 */
	private boolean isFileWritable(final File file, boolean result) {
		if (file.isFile() && file.canWrite()) {
			result = true;
		} else {
			throw new IllegalArgumentException("Die Protokolldatei '" + file.getAbsolutePath() + "' ist ung�ltig!");
		}
		return result;
	}

	/**
	 * @return the csvFileCommonData
	 */
	public File getCsvFileCommonData() {
		return this.csvFileCommonData;
	}

	/**
	 * @return the csvFile
	 */
	public File getCsvFileElemetData() {
		return this.csvFileElemetData;
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param csvFile
	 * @param newHeadlineList
	 * @throws IOException
	 */
	private HeaderRefreshResult refreshProtocolFileWithNewHeaders(final File csvFile, final List<String> newHeadlineList) throws IOException {

		List<String> expandedCurrentHeaderList = new ArrayList<>();
		int lineCount = 0;


		File processFile = new File(csvFile.getAbsolutePath() + ".process");

		if (processFile.exists()) {
			processFile.delete();
		}

		if (csvFile.renameTo(processFile)) {
			try (
					final BufferedReader reader = Files.newBufferedReader(processFile.toPath(), StandardCharsets.UTF_8);
					final BufferedWriter writer = Files.newBufferedWriter(csvFile.toPath(), StandardCharsets.UTF_8);) {

				boolean countLine;
				String line;
				int countNewCells = 0;
				while ((line = reader.readLine()) != null) {
					countLine = false;

					if (lineCount == 0) {
						// Refresh Headline
						List<String> currentHeadlineList = headlineToList(line);

						if (!currentHeadlineList.isEmpty()) {
							StringBuilder headlineBuilder = new StringBuilder(line);
							expandedCurrentHeaderList = expandCurrentHeader(headlineBuilder, newHeadlineList);

							if (!expandedCurrentHeaderList.isEmpty()) {
								countNewCells = expandedCurrentHeaderList.size() - currentHeadlineList.size();
								line = headlineBuilder.toString();
							}
							countLine = true;
						}

					} else {
						// Refresh Lines
						if (countNewCells > 0 && line.endsWith("\"")) {
							StringBuilder lineBuilder = new StringBuilder(line);
							List<String> expandCurrentLine = expandCurrentLine(lineBuilder, countNewCells);
							if (!expandCurrentLine.isEmpty()) {
								line = lineBuilder.toString();
							}
							countLine = true;
						}
					}

					writer.write(line);
					// must do this: .readLine() will have stripped line
					// endings
					writer.newLine();

					if (countLine) {
						lineCount++;
					}
				}
			} finally {
				if (processFile.exists()) {
					processFile.delete();
				}
			}
		}

		return new HeaderRefreshResult(expandedCurrentHeaderList, lineCount);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param headlinesWork
	 * @param protocolLines
	 * @param nextLineNumber
	 */
	private void refreshProtocolLineNumber(final List<String> headlinesWork, final List<ProtocolLine> protocolLines, final int nextLineNumber) {
		for (ProtocolLine protocolLine : protocolLines) {
			addLineCell(headlinesWork, protocolLine, CsvProtocolExport.CONST_FIELD_PROTOKOLL_ZEILE, String.valueOf(nextLineNumber));

			String protokollId = protocolLine.getCellContent(CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID);
			if (StringUtils.isNotBlank(protokollId)) {
				if (protokollId.startsWith("ID_")) {
					String protokollIdNumberPart = protokollId.substring(2, protokollId.length());
					if (MathUtil.numeric(protokollIdNumberPart)) {
						addLineCell(headlinesWork, protocolLine, CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID, "ID_" + nextLineNumber);
					}
				}
			} else {
				addLineCell(headlinesWork, protocolLine, CsvProtocolExport.CONST_FIELD_PROTOKOLL_ID, "ID_" + nextLineNumber);
			}
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param csvFile
	 * @param newHeadlineList
	 * @throws IOException
	 */
	private boolean isProtocolFileRefreshNeeded(final File csvFile, final List<String> newHeadlineList) throws IOException {

		String currentHeadline = readHeadlineFromFile(csvFile);
		List<String> currentHeadlineList = headlineToList(currentHeadline);
		List<String> newHeadlineListWork = new ArrayList<>(newHeadlineList);
		newHeadlineListWork.removeAll(currentHeadlineList);

		return !newHeadlineListWork.isEmpty();
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param headlineBuilder
	 * @param newHeadlineList
	 * @return
	 * @throws IOException
	 */
	private List<String> expandCurrentHeader(final StringBuilder headlineBuilder, final List<String> newHeadlineList) throws IOException {

		List<String> resultHeadlineList = new ArrayList<>();

		if (newHeadlineList != null && !newHeadlineList.isEmpty()) {
			String currentHeadline = headlineBuilder.toString();

			if (StringUtils.isNotBlank(currentHeadline) && currentHeadline.startsWith("\ufeff")) {
				List<String> currentHeadlineList = headlineToList(currentHeadline);
				resultHeadlineList.addAll(currentHeadlineList);

				List<String> newHeadlineListWork = new ArrayList<>(newHeadlineList);
				newHeadlineListWork.removeAll(currentHeadlineList);

				if (!newHeadlineListWork.isEmpty()) {

					StringBuilder headerBuilder = new StringBuilder();

					for (String header : newHeadlineListWork) {
						headerBuilder.append(";");
						headerBuilder.append("\"" + csvClean(header) + "\"");
					}

					headlineBuilder.append(headerBuilder);
					resultHeadlineList.addAll(newHeadlineListWork);
				}
			}
		}

		return resultHeadlineList;

	}

	private List<String> expandCurrentLine(final StringBuilder lineBuilder, final int countNewCells) throws IOException {

		List<String> resultLineList = new ArrayList<>();

		String currentLine = lineBuilder.toString();

		if (StringUtils.isNotBlank(currentLine)) {
			List<String> currentLineList = lineToList(currentLine);
			resultLineList.addAll(currentLineList);

			if (countNewCells > 0) {
				StringBuilder appendBuilder = new StringBuilder();

				for (int i = 0; i < countNewCells; i++) {
					appendBuilder.append(";");
					appendBuilder.append("\"\"");
					resultLineList.add("");
				}

				lineBuilder.append(appendBuilder);
			}
		}

		return resultLineList;

	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param headline
	 * @return
	 */
	private List<String> headlineToList(final String headline) {
		if (StringUtils.isEmpty(headline)) {
			return new ArrayList<>();
		}

		String newHeadline = headline.replace("\ufeff", "");
		return lineToList(newHeadline);
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param line
	 * @return
	 */
	private List<String> lineToList(String line) {
		line = line.replace("\"", "");
		String[] newHeadlineArray = line.split(";");
		return new ArrayList<>(Arrays.asList(newHeadlineArray));
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @param csvFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private String readHeadlineFromFile(final File csvFile) throws IOException {
		String headline = "";
		try (FileInputStream fis = new FileInputStream(csvFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));) {

			headline = reader.readLine();

			reader.close();
		}
		return headline;
	}

	private byte[] readFromFile(final String filePath, final int position, final int size)
			throws IOException {

		RandomAccessFile file = new RandomAccessFile(filePath, "r");
		file.seek(position);
		byte[] bytes = new byte[size];
		file.read(bytes);
		file.close();
		return bytes;
	}

	private void writeToFile(final String filePath, final String data, final int position)
			throws IOException {

		RandomAccessFile file = new RandomAccessFile(filePath, "rw");
		file.seek(position);
		file.write(data.getBytes(StandardCharsets.UTF_8));
		file.close();
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @author RHildebrand
	 * @version 0.0
	 * @since 13.07.2017, 10:43:15
	 *
	 */
	private class ProtocolLine {

		/**
		 * FIXME RHildebrand JavaDoc
		 */
		private final Map<String, String> lineCellMap;

		/**
		 * FIXME RHildebrand JavaDoc
		 *
		 */
		public ProtocolLine() {
			this.lineCellMap = new HashMap<>();
		}

		/**
		 * FIXME RHildebrand JavaDoc
		 *
		 * @param headline
		 * @param value
		 */
		public void addLineCell(final String headline, final String value) {
			if (StringUtils.isNotBlank(headline)) {

				// Check Value
				String contentValue = value;
				if (contentValue == null) {
					contentValue = "";
				}

				this.lineCellMap.put(headline, contentValue);
			}
		}

		/**
		 * FIXME RHildebrand JavaDoc
		 *
		 * @param headline
		 * @return
		 */
		public String getCellContent(final String headline) {
			String cellContent = "";
			if (this.lineCellMap.containsKey(headline)) {
				cellContent = this.lineCellMap.get(headline);
			}

			return cellContent;
		}
	}

	/**
	 * FIXME RHildebrand JavaDoc
	 *
	 * @author RHildebrand
	 * @version 0.0
	 * @since 06.10.2017, 19:53:03
	 *
	 */
	private class HeaderRefreshResult {

		/**
		 * FIXME RHildebrand JavaDoc
		 */
		private final List<String> newHeaderList;

		/**
		 * FIXME RHildebrand JavaDoc
		 */
		private final int nextLineNumber;

		/**
		 * FIXME RHildebrand JavaDoc
		 *
		 */
		public HeaderRefreshResult(final List<String> newHeaderList, final int nextLineNumber) {
			this.newHeaderList = newHeaderList;
			this.nextLineNumber = nextLineNumber;
		}

		/**
		 * @return the newHeaderList
		 */
		public List<String> getNewHeaderList() {
			return this.newHeaderList;
		}

		/**
		 * @return the nextLineNumber
		 */
		public int getNextLineNumber() {
			return this.nextLineNumber;
		}
	}
}