/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.HREF;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.TITLE;

import java.util.ArrayList;
import java.util.List;


/**
 * Hyperlink on page.
 * 
 * @author skyttere
 */
public class Link extends PageElement {
	
	@Override
	public String getType(){
		return "Link";
	}

	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(ID);
		list.add(HREF);
		list.add(TITLE);
		list.add(CLASS);
		return list;
	}	
}
