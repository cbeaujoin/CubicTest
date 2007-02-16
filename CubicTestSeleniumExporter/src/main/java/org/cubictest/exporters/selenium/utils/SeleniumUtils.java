/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.utils;

import static org.cubictest.model.ActionType.BLUR;
import static org.cubictest.model.ActionType.CHECK;
import static org.cubictest.model.ActionType.CLEAR_ALL_TEXT;
import static org.cubictest.model.ActionType.CLICK;
import static org.cubictest.model.ActionType.DBLCLICK;
import static org.cubictest.model.ActionType.DRAG_END;
import static org.cubictest.model.ActionType.DRAG_START;
import static org.cubictest.model.ActionType.ENTER_PARAMETER_TEXT;
import static org.cubictest.model.ActionType.ENTER_TEXT;
import static org.cubictest.model.ActionType.FOCUS;
import static org.cubictest.model.ActionType.GO_BACK;
import static org.cubictest.model.ActionType.GO_FORWARD;
import static org.cubictest.model.ActionType.KEY_PRESSED;
import static org.cubictest.model.ActionType.MOUSE_OUT;
import static org.cubictest.model.ActionType.MOUSE_OVER;
import static org.cubictest.model.ActionType.NEXT_WINDOW;
import static org.cubictest.model.ActionType.NO_ACTION;
import static org.cubictest.model.ActionType.PREVIOUS_WINDOW;
import static org.cubictest.model.ActionType.REFRESH;
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.VALUE;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.formElement.Select;


/**
 * Util class for Selenium export.
 * 
 * @author chr_schwarz
 */
public class SeleniumUtils {

	public static String getLocator(PageElement pe) {
		IdentifierType type = pe.getIdentifierType();
		String text = pe.getText();
		
		if (type.equals(ID)) {
			return "id=" + text;
		}
		if (type.equals(NAME)) {
			return "name=" + text;
		}
		if (type.equals(VALUE)) {
			throw new ExporterException("VALUE IdentifierType not supported.");
		}
		if (type.equals(LABEL)) {
			if (pe instanceof Link) {
				return "link=" + text;
			}
			else {
				//get first element that has "id" attribute equal to the "for" attribute of label with the specified text:
				return "xpath=//" + getElementType(pe) + "[@id=(//label[text()=\"" + text + "\"][1]/@for)][1]";
			}
		}
		else {
			throw new ExporterException("Identifier type not recognized.");
		}
	}
	
	
	public static String getElementType(PageElement pe) {
		if (pe instanceof Select)
			return "select";
		if (pe instanceof FormElement)
			return "input";
		if (pe instanceof Link)
			return "a";
		if (pe instanceof Image)
			return "img";
		if (pe instanceof AbstractContext)
			return "div";
		if (pe instanceof Text)
			throw new ExporterException("Text is not a supported element type for identification.");
		
		throw new ExporterException("Unknown element type");
	}
	
	/**
	 * Get the JavaScript event type of the specified ActionType.
	 * @param a the Action type to convert.
	 * @return the JavaScript event type.
	 */
	public static String getEventType(ActionType a) {
		if (a.equals(CLICK))
			throw new ExporterException("Internal error: \"Click\" should not be used as javascript event. Use action instead.");
		if (a.equals(CHECK))
			throw new ExporterException("Internal error: \"Check\" should not be used as javascript event. Use action instead.");
		if (a.equals(UNCHECK))
			throw new ExporterException("Internal error: \"Uncheck\" should not be used as javascript event. Use action instead.");
		if (a.equals(ENTER_TEXT))
			throw new ExporterException("Internal error: \"Enter text\" should not be used as javascript event. Use action instead.");
		if (a.equals(KEY_PRESSED))
			return "onkeypress";
		if (a.equals(CLEAR_ALL_TEXT))
			throw new ExporterException("Internal error: \"Clear all text\" should not be used as javascript event. Use action instead.");
		if (a.equals(MOUSE_OVER))
			return "onmouseover";
		if (a.equals(MOUSE_OUT))
			return "onmouseout";
		if (a.equals(DBLCLICK))
			return "ondblclick";
		if (a.equals(FOCUS))
			return "onfocus";
		if (a.equals(BLUR))
			return "onblur";
		if (a.equals(DRAG_START))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(DRAG_END))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(NO_ACTION))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(GO_BACK))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(GO_FORWARD))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(REFRESH))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(NEXT_WINDOW))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(PREVIOUS_WINDOW))
			throw new ExporterException(a.getText() + " is not a supported action type");
		if (a.equals(ENTER_PARAMETER_TEXT))
			throw new ExporterException(a.getText() + " is not a supported action type");
		

		throw new ExporterException("Unknown ActionType type");
	}

}