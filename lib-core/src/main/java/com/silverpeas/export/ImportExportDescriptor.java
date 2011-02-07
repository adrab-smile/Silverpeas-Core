/*
 * Copyright (C) 2000 - 2009 Silverpeas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.silverpeas.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static com.silverpeas.util.StringUtil.*;

/**
 * An import export descriptor is an object that provides useful information to exporters and
 * importers for performing their tasks.
 * Information is carried through import and export process parameters.
 */
public abstract class ImportExportDescriptor {

   /**
   * A specific value for the export-import resource format indicating that no explicit format is
   * defined. In general, this specific value means the exporter or importer processes for a single
   * predefined defined format and thus it is useless to specify it.
   */
  public static final String NO_FORMAT = "";

  private String format = NO_FORMAT;
  private Map<String, Object> parameters = new HashMap<String, Object> ();

  /**
   * Gets the format in (or from) which the resource has to be exported (or imported).
   * If no format is defined, then NO_FORMAT is returned.
   * @return the format for export and import.
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets a format into which the resource have to be exported.
   * @param format the export format to set.
   */
  public void setFormat(String format) {
    if (! isDefined(format)) {
      this.format = NO_FORMAT;
    }
    this.format = format;
  }

  /**
   * Adds a new preocess parameter. If a parameter already exists with the specifed name, the value
   * is replaced.
   * @param <T> the type of the parameter value.
   * @param name the parameter name.
   * @param value the parameter value.
   */
  public <T> void addParameter(String name, final T value) {
    this.parameters.put(name, value);
  }

  /**
   * Removes the process parameter identified by the specified name. If no parameter with the
   * specified name exists, nothing is done.
   * @param name the parameter name.
   */
  public void removeParameter(String name) {
    this.parameters.remove(name);
  }

  /**
   * Gets the parameter identified by the specified name. If no parameter with the specified name
   * exists, null is returned.
   * @param <T> the type of the parameter value.
   * @param name the parameter name.
   * @return the value of the parameter or null if no such parameter exists.
   */
  @SuppressWarnings("unchecked")
  public <T> T getParameter(String name) {
    return (T) this.parameters.get(name);
  }

  /**
   * Gets a list of the parameter names from this descriptor.
   * @return a list of parameter names.
   */
  public List<String> getParameters() {
    return new ArrayList<String>(this.parameters.keySet());
  }

  /**
   * Is the parameter identified by the specified name set within this descriptor?
   * @param name the parameter name
   * @return true if the parameter is set, false otherwise.
   */
  public boolean isParameterSet(String name) {
    return this.parameters.containsKey(name);
  }
}