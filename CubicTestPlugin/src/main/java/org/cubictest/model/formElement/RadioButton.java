/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.formElement;

import static org.cubictest.model.IdentifierType.CHECKED;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.TITLE;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.IdentifierType;


public class RadioButton extends Checkable{

	@Override
	public String getType() {
		return "RadioButton";
	}
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(ID);
		list.add(IdentifierType.NAME);
		list.add(CHECKED);
		list.add(TITLE);
		return list;
	}

}
