/**
 * Copyright 2010 Tristan Tarrant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dataforte.cohesive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVImporter implements an incremental event-driven CSV parser. After
 * parsing each row, it invokes one or more user-defined listeners passing
 * the data of that row. By default fields are comma-separated, but any delimiter
 * may be used. Fields may also be enclosed within quotes.
 * 
 * @author tst
 *
 */
public class CSVImporter {
	Reader r;
	List<CSVRowListener> rowListeners = new ArrayList<CSVRowListener>();
	char delimiter = ',';
	boolean header = false;
	char quote = '"';
	int rows = -1;
	
	/**
	 * Initialize the parser with the specified {@link Reader}
	 * 
	 * @param r
	 */
	public CSVImporter(Reader r) {
		this.r = r;
	}
	
	/**
	 * Parses the data, invoking the user-defined listeners for each row.
	 * This method also keeps track of the current row number
	 * 
	 * @throws IOException
	 */
	public void parse() throws IOException {
		List<String> columns = null;
		Map<String, String> data;
		rows = 0;
		BufferedReader br = (r instanceof BufferedReader) ? (BufferedReader) r : new BufferedReader(r);
		String line;
		if(header) {
			line = br.readLine();
			if(line!=null) {
				columns = parseRow(line);
			}
		}
		while((line=br.readLine())!=null) {
			List<String> fields = parseRow(line);
			if(columns==null) {
				columns = new ArrayList<String>(fields.size());
				for(int i=0; i<fields.size(); i++) {
					columns.add(Integer.toString(i));
				}
			}
			data = new LinkedHashMap<String, String>();
			for(int i=0; i<fields.size(); i++) {
				data.put(columns.get(i), fields.get(i));
			}
			for(CSVRowListener l : rowListeners) {
				l.rowParsed(new CSVRowEvent(this, data, rows++));
			}
		}
		br.close();
	}

	/**
	 * Adds a {@link CSVRowListener} to the list of listeners which will receive each row
	 * @param l
	 */
	public void addRowListener(CSVRowListener l) {
		rowListeners.add(l);
	}

	/**
	 * Returns the field delimiter character. By default it is a , (comma)
	 * @return
	 */
	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets a field delimiter character
	 * 
	 * @param delimiter
	 */
	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Returns whether the first row should be treated as a header. This means that each field in a data row will be keyed
	 * by its name in the header row
	 * 
	 * @return
	 */
	public boolean isHeader() {
		return header;
	}

	/**
	 * Sets whether the first row contains field headers
	 * @param header
	 */
	public void setHeader(boolean header) {
		this.header = header;
	}

	/**
	 * Returns the quote character which wraps fields. By default it is a ' (single quote)
	 * 
	 * @return
	 */
	public char getQuote() {
		return quote;
	}

	/**
	 * Sets the quote character that wraps fields. Quoting of fields is optional and mixed
	 * fields can be used (i.e. quoted and unquoted) in the same row
	 * 
	 * @param quote
	 */
	public void setQuote(char quote) {
		this.quote = quote;
	}
	
	private List<String> parseRow(String row) {
		List<String> fields = new ArrayList<String>();
		StringBuffer currentField = new StringBuffer();
		boolean inQuotes = false;
		for(int i=0; i<row.length(); i++) {
			char ch = row.charAt(i);
			if(ch==quote) {
				inQuotes = !inQuotes;
			} else if(ch==delimiter && !inQuotes) {
				fields.add(currentField.toString().trim());
				currentField = new StringBuffer();
			} else {
				currentField.append(ch);
			}
		}
		if(currentField.length()>0) {
			fields.add(currentField.toString().trim());
		}
		return fields;
	}

	/**
	 * Returns the number of parsed rows
	 * 
	 * @return
	 */
	public int getRows() {
		if(rows<0) 
			throw new IllegalStateException("No data parsed yet");
		return rows;
	}
}
