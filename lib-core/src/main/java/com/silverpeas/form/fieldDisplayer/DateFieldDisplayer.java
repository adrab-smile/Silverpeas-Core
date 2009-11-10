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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import org.apache.ecs.AlignType;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.A;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Span;

import com.silverpeas.form.Field;
import com.silverpeas.form.FieldDisplayer;
import com.silverpeas.form.FieldTemplate;
import com.silverpeas.form.Form;
import com.silverpeas.form.FormException;
import com.silverpeas.form.PagesContext;
import com.silverpeas.form.Util;
import com.silverpeas.form.fieldType.DateField;
import com.silverpeas.util.EncodeHelper;
import com.stratelia.silverpeas.silvertrace.SilverTrace;
import com.stratelia.webactiv.util.DateUtil;
import java.util.ArrayList;
import java.util.List;


/**
 * A DateFieldDisplayer is an object which can display a TextFiel in HTML the content of a TextFiel to a end user
 * and can retrieve via HTTP any updated value.
 * 
 * 
 *
 * @see Field
 * @see FieldTemplate
 * @see Form
 * @see FieldDisplayer
 */
public class DateFieldDisplayer extends AbstractFieldDisplayer
{
	
	/**
	 * Constructeur
	 */
	public DateFieldDisplayer()
	{
	}

	/**
	 * Returns the name of the managed types.
	 */
	public String[] getManagedTypes()
	{
		return new String[] {DateField.TYPE};
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
    public void displayScripts(PrintWriter out, FieldTemplate template, PagesContext pagesContext)
    	throws IOException
    {
		String language = pagesContext.getLanguage();
 		
		if (! template.getTypeName().equals(DateField.TYPE))
		{
			SilverTrace.info("form", "DateFieldDisplayer.displayScripts", "form.INFO_NOT_CORRECT_TYPE", DateField.TYPE);
		}

	 	if (template.isMandatory() && pagesContext.useMandatory())
	 	{
			out.println("		if (isWhitespace(stripInitialWhitespace(field.value))) {");
			out.println("			errorMsg+=\"  - '" + EncodeHelper.javaStringToJsString(template.getLabel(language)) + "' " + Util.getString("GML.MustBeFilled", language) + "\\n \";");
			out.println("			errorNb++;");
			out.println("		}");
    	} 				
		out.println("		if (! isWhitespace(stripInitialWhitespace(field.value))) {");
        out.println("			if (! isCorrectDate(extractYear(field.value, '" + language + "'), extractMonth(field.value, '" + language + "'), extractDay(field.value, '" + language + "'))) {");
		out.println("				errorMsg+=\"  - '" + EncodeHelper.javaStringToJsString(template.getLabel(language)) + "' " + Util.getString("GML.MustContainsCorrectDate", language) + "\\n \";");
		out.println("				errorNb++;");
		out.println("		}}");

		out.println("		if (! isValidText(field, " + Util.getSetting("nbMaxCar") + ")) {");
    	out.println("			errorMsg+=\"  - '" + template.getLabel(language) + "' " + Util.getString("ContainsTooLargeText", language) + Util.getSetting("nbMaxCar") + " " + Util.getString("Characters", language) + "\\n \";");
       	out.println("			errorNb++;");
     	out.println("		}");
     	
     	Util.getJavascriptChecker(template.getFieldName(), pagesContext, out);
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
    public void display(PrintWriter out, Field field, FieldTemplate template, PagesContext pagesContext)
    	throws FormException
    {
		if (! field.getTypeName().equals(DateField.TYPE))
		{
			SilverTrace.info("form", "DateFieldDisplayer.display", "form.INFO_NOT_CORRECT_TYPE", DateField.TYPE);
		}
		
    	String language = pagesContext.getLanguage();
		Map parameters = template.getParameters(language);
		String fieldName = template.getFieldName();
		
		String defaultParam = (parameters.containsKey("default") ? (String)parameters.get("default") : "");
		String defaultValue = "";
		if ("now".equalsIgnoreCase(defaultParam) && !pagesContext.isIgnoreDefaultValues())
			defaultValue = DateUtil.dateToString(new Date(), pagesContext.getLanguage());
		
		String value = (!field.isNull() ? field.getValue(language) : defaultValue);
		if (pagesContext.isBlankFieldsUse())
			value = "";

		Input input = new Input();
		input.setID(fieldName);
		input.setName(fieldName);
		input.setValue(EncodeHelper.javaStringToHtmlString(value));
		input.setType(template.isHidden() ? Input.hidden : Input.text);
		input.setMaxlength(parameters.containsKey("maxLength") ? (String)parameters.get("maxLength") : "10");
		input.setSize(parameters.containsKey("size") ? (String)parameters.get("size") : "13");
		if (parameters.containsKey("border"))
		{
			input.setBorder(Integer.parseInt((String)parameters.get("border")));
		}
		if (template.isDisabled())
		{
			input.setDisabled(true);
		}
		else if (template.isReadOnly())
		{
			input.setReadOnly(true);
		}
		
		if (!template.isHidden() && !template.isDisabled() && !template.isReadOnly())
		{
			ElementContainer container = new ElementContainer();
			container.addElement(input);
			
			container.addElement("&nbsp;");
			
			A link = new A();
			link.setHref("javascript:calendar('" + fieldName + "');");
			IMG calendarImg = new IMG();
			calendarImg.setSrc(Util.getIcon("calendar"));
			calendarImg.setWidth(15);
			calendarImg.setHeight(15);
			calendarImg.setBorder(0);
			String calendarLab = Util.getString("GML.viewCalendar", language);
			calendarImg.setAlt(calendarLab);
			calendarImg.setTitle(calendarLab);
			calendarImg.setAlign(AlignType.absmiddle);
			link.addElement(calendarImg);
			container.addElement(link);
			
			container.addElement("&nbsp;");
			Span span = new Span();
			span.setClass("txtnote");
			span.addElement("(" + Util.getString("GML.dateFormatExemple", language) + ")");
			container.addElement(span);
			
			if (template.isMandatory() && pagesContext.useMandatory()) {
				container.addElement("&nbsp;");
				
				IMG img = new IMG();
				img.setSrc(Util.getIcon("mandatoryField"));
				img.setWidth(5);
				img.setHeight(5);
				img.setBorder(0);
				container.addElement(img);
			}
			
			out.println(container.toString());
		}
		else
		{
			out.println(input.toString());
		}
    }

    /**
	 * Updates the value of the field.
	 * 
	 * The fieldName must be used to retrieve the HTTP parameter from the
	 * request.
	 * 
	 * @throw FormException if the field type is not a managed type.
	 * @throw FormException if the field doesn't accept the new value.
	 */
	public List<String> update(String newValue, Field field, FieldTemplate template, PagesContext pagesContext)
		throws FormException
	{
		if (field.acceptValue(newValue, pagesContext.getLanguage()))
		{
			field.setValue(newValue, pagesContext.getLanguage());
		}
		else
		{
			throw new FormException("DateFieldDisplayer.update", "form.EX_NOT_CORRECT_VALUE", DateField.TYPE);
		}
    return new ArrayList<String>();
	}

	public boolean isDisplayedMandatory()
	{
		return true;
	}

	public int getNbHtmlObjectsDisplayed(FieldTemplate template, PagesContext pagesContext)
	{
		return 1;
	}
	
}