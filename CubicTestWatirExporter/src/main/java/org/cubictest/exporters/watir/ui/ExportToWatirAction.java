/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.CubicTestExport;
import org.cubictest.exporters.watir.converters.ContextConverter;
import org.cubictest.exporters.watir.converters.CustomTestStepConverter;
import org.cubictest.exporters.watir.converters.PageElementConverter;
import org.cubictest.exporters.watir.converters.TransitionConverter;
import org.cubictest.exporters.watir.converters.UrlStartPointConverter;
import org.cubictest.exporters.watir.holders.StepList;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;

/**
 * Action for starting Watir export. Will be in context menu in the 
 * Navigator or Package Explorer.
 * 
 * @author chr_schwarz
 */
public class ExportToWatirAction implements IActionDelegate {
	ISelection selection;
	
	public static final String OK_MESSAGE = "Test exported OK to the \"generated\" directory.\n\n" +
	"The exported test (.rb file) can be run by right clicking  -> " +
	"Open With -> System Editor (Watir and Ruby must be installed)";
	
	/* 
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		try {
			//callback to CubicTest with the selected files
			CubicTestExport.exportSelection((IStructuredSelection) selection, "rb",
					UrlStartPointConverter.class, 
					TransitionConverter.class, 
					CustomTestStepConverter.class, 
					PageElementConverter.class, 
					ContextConverter.class,
					StepList.class);
			ErrorHandler.showInfoDialog(OK_MESSAGE);
		} 
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error occured in CubicTest Watir exporter.");
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
