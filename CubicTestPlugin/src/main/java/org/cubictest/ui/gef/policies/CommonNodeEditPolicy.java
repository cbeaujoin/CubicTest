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
package org.cubictest.ui.gef.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

/**
 * @author Stein K�re Skytteren
 *
 */
public class CommonNodeEditPolicy extends AbstractNodeEditPolicy {
	@Override
	protected Command getConnectionCompleteCommand( CreateConnectionRequest request) {
		return null;
	}
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}
}
