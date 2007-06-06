/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;
import java.util.List;

import org.cubictest.common.utils.UserInfo;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for adding a user interaction transition to a page.
 * 
 * @author Christian Schwarz
 *
 */
public class AddUserInteractionTransitionAction extends BaseEditorAction {

	public static final String ACTION_ID = "CubicTestPlugin.action.addUserInteractionTransition";

	
	public AddUserInteractionTransitionAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if(parts != null) {
			for(Object element : parts) {
				if(element instanceof PageEditPart || element instanceof PageElementEditPart) {
					return true;
				}
			}			
		}
		return false;
	}

	@Override
	protected void init() {
		super.init();
		setText("Add User Interaction");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		for (Iterator iter = this.parts.iterator(); iter.hasNext();) {
			Object element = iter.next();
			PageEditPart pageEditPart = null;
			if (element instanceof PageElementEditPart) {
				pageEditPart = (PageEditPart) ViewUtil.getSurroundingPagePart((EditPart) element);
			}
			if(element instanceof PageEditPart) {
				pageEditPart = (PageEditPart) element;
			}
			
			Page page = (Page) pageEditPart.getModel();
			if (page.getElements().size() == 0) {
				UserInfo.showWarnDialog("Cannot create user interaction. Page must have at least one page element.");
				return;
			}
			CreateTransitionCommand cmd = new CreateTransitionCommand();
			cmd.setTest((Test) pageEditPart.getParent().getModel());
			cmd.setSource((TransitionNode) pageEditPart.getModel());
			cmd.setPageEditPart(pageEditPart);
			cmd.setAutoCreateTargetPage(true);

			getCommandStack().execute(cmd);
		}
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.USER_INTERACTION_IMAGE);
	}
}
