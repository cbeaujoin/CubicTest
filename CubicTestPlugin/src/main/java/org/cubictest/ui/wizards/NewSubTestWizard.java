/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.cubictest.model.Test;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.ui.INewWizard;


/**
 * Wizard for creating new sub tests.  The wizard creates one file with the extension "aat".
 * If the container resource (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target container.
 * 
 * @author Christian Schwarz 
 */

public class NewSubTestWizard extends AbstractNewSimpleStartPointTestWizard implements INewWizard {
	
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		testDetailsPage = new TestDetailsPage(selection, !extensionPointMap.isEmpty(), "sub test");
		addPage(testDetailsPage);
	}
	
	public Test createEmptyTest(String name, String description) {
		Test emptyTest = WizardUtils.createEmptyTestWithSubTestStartPoint("test" + System.currentTimeMillis(), name, description);
		return emptyTest;
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest sub test");
	}

}