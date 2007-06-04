package org.cubictest.model.popup;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.IdentifierType;


public class OKButton extends JavaScriptButton {

	@Override
	public String getType() {
		return "OK button";
	}
	
	@Override
	public String getText() {
		return "OK";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {
		ArrayList<IdentifierType> result = new ArrayList<IdentifierType>();
		return result;
	}
	
}
