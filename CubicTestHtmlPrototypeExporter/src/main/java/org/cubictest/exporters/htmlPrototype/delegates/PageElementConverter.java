/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.delegates;

import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter;
import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter.UnknownPageElementException;
import org.cubictest.exporters.htmlPrototype.utils.TextUtil;
import org.cubictest.model.FormElement;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.jdom.Element;

public class PageElementConverter implements IPageElementConverter {
	
	/* (non-Javadoc)
	 * @see org.cubictest.export.htmlSkeleton.interfaces.IPageElementConverter#convert(org.cubictest.model.PageElement)
	 */
	public Element convert(PageElement pe) throws UnknownPageElementException {
		Element result;
		if (pe instanceof Link) {
			result = fromLink((Link) pe);
		}
		else if (pe instanceof Text) {
			result = fromText((Text) pe);
		}
		else if (pe instanceof FormElement) {
			result = fromFormElement((FormElement) pe);
		} else {
			throw new UnknownPageElementException();
		}
		
		if(pe.isNot()) {
			result.setAttribute("class", (result.getAttributeValue("class") != null ? result.getAttributeValue("class") + " " : "") + "not");
		}
		
		return result;
	}

	private Element fromFormElement(FormElement fe) {
		String name = TextUtil.camel(fe.getText());
		String value = fe.getDescription();
		String type = fe.getClass().getSimpleName().toLowerCase();
		Element input;
		
		if(!type.equals("select")) {
			input = new Element("input");
			input.setAttribute("type", fe.getClass().getSimpleName().toLowerCase());
			input.setAttribute("value", value);
		} else {
			input = new Element("select");
			Element option = new Element("option");
			option.setAttribute("value", value);
			option.setText(value);
			input.addContent(option);
		}
		input.setAttribute("name", name);
		input.setAttribute("id", name);											
		return input;
	}
	
	public Element labelFormElement(FormElement fe, Element input) {
			Element labeledElement = new Element("div");
			String name = TextUtil.camel(fe.getText());
			String type = fe.getClass().getSimpleName().toLowerCase();
			
			if(!type.equals("submit") && !type.equals("button")) {
				Element label = new Element("label");
				label.setText(fe.getDescription());
				label.setAttribute("for", name);				
				labeledElement.addContent(label);
			}
			
			labeledElement.addContent(input);
			
			return labeledElement;
	}

	private Element fromText(Text text) {
		Element textElement = new Element("p");
		textElement.setText(text.getDescription());
		return textElement;
	}

	private Element fromLink(Link link) {
		Element linkElement = new Element("a");
		linkElement.addContent(link.getDescription());
		linkElement.setAttribute("href", "#");
		return linkElement;
	}

}