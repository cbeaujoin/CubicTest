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
package org.cubictest.ui.gef.command;

import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.wizards.NewCustomTestStepWizard;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class AddCustomTestStepCommand extends Command {

	private Test test;
	private CustomTestStepHolder customTestStep;

	public AddCustomTestStepCommand(Test test, CustomTestStepHolder customTestStep) {
		this.test = test;
		this.customTestStep = customTestStep;
	}
	
	@Override
	public void execute() {
		if(customTestStep.getFilePath() == null) {
			NewCustomTestStepWizard wizard = new NewCustomTestStepWizard(customTestStep);
			WizardDialog dlg = new WizardDialog(new Shell(),
					wizard);
			if (dlg.open() == WizardDialog.CANCEL) {
				return;
			}
		}

		test.addCustomTestStep(customTestStep);
	}

	public void setTest(Test test) {
		this.test = test;
	}


	
	@Override
	public void undo() {
		if(customTestStep != null) {
			test.removeCustomTestSteps(customTestStep);
		}
	}

	@Override
	public void redo() {
		if(customTestStep != null) {
			test.addCustomTestStep(customTestStep);
		}
	}	
}
