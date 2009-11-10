/**
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
 * "http://repository.silverpeas.com/legal/licensing"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.silverpeas.form.fieldDisplayer;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import com.silverpeas.form.Field;
import com.silverpeas.form.FieldDisplayer;
import com.silverpeas.form.FieldTemplate;
import com.silverpeas.form.Form;
import com.silverpeas.form.FormException;
import com.silverpeas.form.PagesContext;
import com.silverpeas.form.fieldType.DateField;
import com.silverpeas.form.fieldType.TextField;
import com.silverpeas.form.record.GenericFieldTemplate;
import com.silverpeas.util.EncodeHelper;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DateUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * A TextFieldDisplayer is an object which can display a TextFiel in HTML
 * the content of a TextFiel to a end user
 * and can retrieve via HTTP any updated value.
 * 
 * 
 *
 * @see Field
 * @see FieldTemplate
 * @see Form
 * @see FieldDisplayer
 */
public class TextDisplayer extends AbstractFieldDisplayer {

  /**
   * Constructeur
   */
  public TextDisplayer() {
  }

  /**
   * Returns the name of the managed types.
   */
  public String[] getManagedTypes() {
    String[] s = new String[2];
    s[0] = TextField.TYPE;
    s[1] = DateField.TYPE;
    return s;
  }

  /**
   * Prints the javascripts which will be used to control
   * the new value given to the named field.
   *
   * The error messages may be adapted to a local language.
   * The FieldTemplate gives the field type and constraints.
   * The FieldTemplate gives the local labeld too.
   *
   * Never throws an Exception
   * but log a silvertrace and writes an empty string when :
   * <UL>
   * <LI> the fieldName is unknown by the template.
   * <LI> the field type is not a managed type.
   * </UL>
   */
  public void displayScripts(PrintWriter out,
      FieldTemplate template,
      PagesContext PagesContext) throws java.io.IOException {
  }

  /**
   * Prints the HTML value of the field.
   * The displayed value must be updatable by the end user.
   *
   * The value format may be adapted to a local language.
   * The fieldName must be used to name the html form input.
   *
   * Never throws an Exception
   * but log a silvertrace and writes an empty string when :
   * <UL>
   * <LI> the field type is not a managed type.
   * </UL>
   */
  public void display(PrintWriter out,
      Field field,
      FieldTemplate template,
      PagesContext PagesContext) throws FormException {

    String value = "";
    String classe = "";
    String size = "";
    String color = "";
    String face = "";
    String bold = "";
    String html = "";
    String language = PagesContext.getLanguage();

    Map parameters = template.getParameters(language);

    if (!field.isNull()) {
      value = field.getValue(language);
    }

    if (field.getTypeName().equals(DateField.TYPE)) {
      try {
        value = DateUtil.getOutputDate(field.getValue(), PagesContext.getLanguage());
      } catch (Exception e) {
        SilverTrace.error("form", "TextDisplayer.display", "form.INFO_NOT_CORRECT_TYPE", "value = " + field.getValue(),
            e);
      }
    }

    if (parameters.containsKey("class")) {
      classe = (String) parameters.get("class");
      if (classe != null) {
        classe = "class=" + classe;
      }
    }

    if (parameters.containsKey("values") || parameters.containsKey("keys")) {
      Hashtable keyValuePairs = ((GenericFieldTemplate) template).getKeyValuePairs(language);
      String newValue = "";
      if (value.indexOf("##") != -1) {
        //Try to display a checkbox list
        StringTokenizer tokenizer = new StringTokenizer(value, "##");
        String t = null;
        while (tokenizer.hasMoreTokens()) {
          t = tokenizer.nextToken();

          t = (String) keyValuePairs.get(t);
          newValue += t;

          if (tokenizer.hasMoreTokens()) {
            newValue += ", ";
          }
        }
      } else if (value != null && value.length() > 0) {
        newValue = (String) keyValuePairs.get(value);
      }
      value = newValue;
    }

    if (classe.length() > 0) {
      html += "<span " + classe + ">";
    }

    if (parameters.containsKey("fontSize") || parameters.containsKey("fontColor") || parameters.containsKey("fontFace")) {
      html += "<font";
    }

    if (parameters.containsKey("fontSize")) {
      size = (String) parameters.get("fontSize");
      html += " size=\"" + size + "\"";
    }

    if (parameters.containsKey("fontColor")) {
      color = (String) parameters.get("fontColor");
      html += " color=\"" + color + "\"";
    }

    if (parameters.containsKey("fontFace")) {
      face = (String) parameters.get("fontFace");
      html += " face=\"" + face + "\"";
    }

    if (size.length() > 0 || color.length() > 0 || face.length() > 0) {
      html += ">";
    }

    if (parameters.containsKey("bold")) {
      bold = (String) parameters.get("bold");
      if ("true".equals(bold)) {
        html += "<b>";
      }
    }

    html += EncodeHelper.javaStringToHtmlParagraphe(value);

    if (bold.length() > 0) {
      html += "</b>";
    }

    if (size.length() > 0 || color.length() > 0 || face.length() > 0) {
      html += "</font>";
    }

    if (classe.length() > 0) {
      html += "</span>";
    }


    out.println(html);
  }

  /**
   * Updates the value of the field.
   *
   * The fieldName must be used to retrieve the HTTP parameter from the request.
   *
   * @throw FormException if the field type is not a managed type.
   * @throw FormException if the field doesn't accept the new value.
   */
  public List<String> update(String newValue,
      Field field,
      FieldTemplate template,
      PagesContext PagesContext)
      throws FormException {
    return new ArrayList<String>();
  }

  public boolean isDisplayedMandatory() {
    return false;
  }

  public int getNbHtmlObjectsDisplayed(FieldTemplate template, PagesContext pagesContext) {
    return 0;
  }
}