/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ReconnectRequest;

/**
 * @author Stein K�re Skytteren
 *
 */
public class StartPointNodeEditPolicy extends AbstractNodeEditPolicy {
	
	protected Command getConnectionCompleteCommand(){
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}
}
