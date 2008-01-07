/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
