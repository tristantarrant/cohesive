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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.activation.DataSource;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a CSV exporter
 * 
 * @author Tristan Tarrant
 */
public class CSVExporter implements DataSource {
	static final Logger log = LoggerFactory.getLogger(CSVExporter.class);
	char delimiter = ',';
	boolean useQuotes = true;
	boolean header = false;
	char quote = '"';
	List<CSVColumn> columns = new ArrayList<CSVColumn>();
	String name;
	Collection<?> data;

	public class CSVColumn {
		String property;
		String title;
		MessageFormat format;
		Integer startIndex;
		Integer endIndex;

		public CSVColumn(String property, String title, MessageFormat format, Integer start, Integer end) {
			this.property = property;
			this.title = title;
			this.format = format;
			startIndex = start;
			endIndex = end;
		}
	}
	
	public CSVExporter() {
		
	}

	public CSVExporter(Collection<?> data) {
		setData(data);
	}

	public void addProperty(String name) {
		addProperty(name, name, null, null, null);
	}

	public void addProperty(String name, String title) {
		addProperty(name, title, null, null, null);
	}

	public void addProperty(String name, Integer start, Integer end) {
		addProperty(name, name, null, start, end);
	}

	public void addProperty(String name, String title, Integer start, Integer end) {
		addProperty(name, title, null, start, end);
	}
	
	public void addProperty(String name, String title, String format) {
		addProperty(name, title, format, null, null);
	}

	public void addProperty(String name, String title, String format, Integer start, Integer end) {
		if (format != null) {
			columns.add(new CSVColumn(name, title, new MessageFormat(format), start, end));
		}
		else {
			columns.add(new CSVColumn(name, title, null, start, end));
		}
	}

	public char getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isUseQuotes() {
		return useQuotes;
	}

	public void setUseQuotes(boolean useQuotes) {
		this.useQuotes = useQuotes;
	}

	public char getQuote() {
		return quote;
	}

	public void setQuote(char quote) {
		this.quote = quote;
	}

	@Override
	public String getContentType() {
		return "text/csv";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeFile(baos);
		return new ByteArrayInputStream(baos.toByteArray());
	}

	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isHeader() {
		return header;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public void writeFile(OutputStream os) throws IOException {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("it"));
		PrintWriter pw = new PrintWriter(os);
		if (header) {
			boolean sep = false;
			for (CSVColumn column : columns) {
				if (sep) {
					pw.print(delimiter);
				}
				else {
					sep = true;
				}
				if (useQuotes) {
					pw.print(quote);
				}
				pw.print(column.title);
				if (useQuotes) {
					pw.print(quote);
				}
			}
			pw.println();
		}
		for (Object row : data) {
			boolean sep = false;
			for (CSVColumn column : columns) {
				try {
					Object item = PropertyUtils.getProperty(row, column.property);
					if (sep) {
						pw.print(delimiter);
					}
					else {
						sep = true;
					}
					if (useQuotes) {
						pw.print(quote);
					}
					if ((column.startIndex != null) && (column.endIndex != null)) {
						item = ((String) item).substring(column.startIndex, column.endIndex);
					}
					else if (column.startIndex != null) {
						item = ((String) item).substring(column.startIndex);
					}
					else if (column.endIndex != null) {
						item = ((String) item).substring(0, column.endIndex);
					}
					if (column.format != null) {
						pw.print(column.format.format(new Object[]{item}));
					}
					else if (item instanceof java.util.Date) {
						
						pw.print(df.format((Date) item));
					}
					else {
						pw.print(item.toString());
					}
					if (useQuotes) {
						pw.print(quote);
					}
				} catch (Throwable t) {
					log.error("", t);
				}
			}
			pw.println();
		}
		pw.close();
	}

	public Collection<?> getData() {
		return data;
	}

	public void setData(Collection<?> data) {
		this.data = data;
	}
}
