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

import java.util.EventObject;
import java.util.Map;

/**
 * This class wraps a row of parsed CSV data
 * @author Tristan Tarrant
 *
 */
public class CSVRowEvent extends EventObject {
	
	Map<String,String> data;
	int row;
	
	public CSVRowEvent(Object source, Map<String,String> data, int row) {
		super(source);
		this.data = data;
		this.row = row;
	}

	/**
	 * Returns a map of the data keyed by its header field name (if available) or a 0-based index
	 * 
	 * @return
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * Returns the index of the current row
	 * 
	 * @return
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Returns the field by its name (obtained either by the header or as a 0-based index)
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return data.get(key);
	}
	
	/**
	 * Returns an array of strings containing the fields
	 * 
	 * @return
	 */
	public String[] getFields() {
		return data.values().toArray(new String[]{});
	}
}
