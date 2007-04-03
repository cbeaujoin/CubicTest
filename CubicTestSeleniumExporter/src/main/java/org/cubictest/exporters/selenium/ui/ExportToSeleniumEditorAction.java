/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.selenium.selenese.converters.ContextConverter;
import org.cubictest.exporters.selenium.selenese.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.selenese.converters.PageElementConverter;
import org.cubictest.exporters.selenium.selenese.converters.TransitionConverter;
import org.cubictest.exporters.selenium.selenese.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Action for exporting to HTML prototype.
 * 
 * @author Christian Schwarz
 *
 */
public class ExportToSeleniumEditorAction implements IEditorActionDelegate {
	ISelection selection;
	
	public ExportToSeleniumEditorAction() {
		super();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (selection == null) {
			ErrorHandler.showErrorDialog("Could not export test (selection unavailable).");
		}
		
		try {
			//callback to CubicTest with the selected files
			CubicTestExport.exportSelection((IStructuredSelection) selection, "html",
					UrlStartPointConverter.class, 
					TransitionConverter.class, 
					CustomTestStepConverter.class, 
					PageElementConverter.class, 
					ContextConverter.class,
					SeleneseDocument.class);
			ErrorHandler.showInfoDialog(ExportToSeleniumAction.OK_MESSAGE);
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error occured in Selenium export.");
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor != null) {
			selection = new StructuredSelection(((FileEditorInput)targetEditor.getEditorInput()).getFile());
		}
	}

}
