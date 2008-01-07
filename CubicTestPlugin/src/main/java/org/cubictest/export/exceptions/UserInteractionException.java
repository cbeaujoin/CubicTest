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
package org.cubictest.export.exceptions;


/**
 * Exception indicating that a user interaction has failed and test cannot continue.
 * 
 * @author Christian Schwarz
 *
 */
public class UserInteractionException extends ExporterException {


	public UserInteractionException(String message) {
		super(message);
	}

	public UserInteractionException(String message, Exception cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1L;

}
