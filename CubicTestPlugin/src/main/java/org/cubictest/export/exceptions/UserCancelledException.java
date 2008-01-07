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

import org.cubictest.common.exception.CubicException;

/**
 * Exception indicating user cancel.
 * 
 * @author Christian Schwarz
 */
public class UserCancelledException extends CubicException {

	private static final long serialVersionUID = 1L;

	public UserCancelledException(String message) {
		super(message);
	}
}
