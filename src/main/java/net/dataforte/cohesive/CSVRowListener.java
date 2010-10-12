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

import java.util.EventListener;

/**
 * This interface defines the row listeners which should be added to the {@link CSVImporter}.
 * 
 * @author Tristan Tarrant
 */
public interface CSVRowListener extends EventListener {
	/**
	 * This method will be invoked for each new row in the source data
	 * @param e the row data
	 */
	void rowParsed(CSVRowEvent e);
}
