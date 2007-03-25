/*
 * Created on 13.feb.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.command.AddExtensionPointCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PropertyChangePart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * Edit policy for objects implementing IContext. 
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 *
 */
public class ContextContainerEditPolicy extends ContainerEditPolicy {

	private ContextLayoutEditPolicy flowPolicy;
	
	public ContextContainerEditPolicy(ContextLayoutEditPolicy flowPolicy) {
		this.flowPolicy = flowPolicy;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		if(REQ_CREATE.equals(request.getType()))
			return getHost();
		return super.getTargetEditPart(request);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		
		int index = getIndex(request);
		
		Object obj = request.getNewObject();
		if(!(getHost().getModel() instanceof IContext)) {
			return null;
		}
		
		if(obj instanceof ExtensionPoint) {
			AbstractPageEditPart abstractPageEditPart = (AbstractPageEditPart) ViewUtil.getSurroundingPagePart((PropertyChangePart) getHost());
			if (!(abstractPageEditPart instanceof PageEditPart))
				return null;
			
			ExtensionPoint exPoint = new ExtensionPoint();
			Test test = (Test) abstractPageEditPart.getParent().getModel();
			PageEditPart pagePart = (PageEditPart) abstractPageEditPart;
			
			CompoundCommand compoundCmd = new CompoundCommand();
			AddExtensionPointCommand addExPointCmd = new AddExtensionPointCommand();
			addExPointCmd.setExtensionPoint(exPoint);
			addExPointCmd.setPageEditPart(pagePart);
			addExPointCmd.setTest(test);
			compoundCmd.add(addExPointCmd);
			
			CreateTransitionCommand transitionCreateCommand = new CreateTransitionCommand();
			transitionCreateCommand.setSource((Page) pagePart.getModel());
			transitionCreateCommand.setTarget(exPoint);
			transitionCreateCommand.setTest(test);
			compoundCmd.add(transitionCreateCommand);
			
			return compoundCmd;
		}
		if(obj instanceof UserInteractionsTransition) {
			AbstractPageEditPart abstractPageEditPart = (AbstractPageEditPart) ViewUtil.getSurroundingPagePart((PropertyChangePart) getHost());
			if (!(abstractPageEditPart instanceof PageEditPart))
				return null;
			
			CreateTransitionCommand cmd = new CreateTransitionCommand();
			cmd.setTest((Test) abstractPageEditPart.getParent().getModel());
			cmd.setSource((TransitionNode) abstractPageEditPart.getModel());
			cmd.setPageEditPart((PageEditPart) abstractPageEditPart);
			cmd.setAutoCreateTargetPage(true);
			return cmd;
		}		
		if (obj instanceof Option)
			return null;
		
		if (!(obj instanceof PageElement))
			return null;
		PageElement pageElement = (PageElement) obj;
			
		CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
		createElementCmd.setContext((IContext)getHost().getModel());
		createElementCmd.setPageElement(pageElement);
		createElementCmd.setIndex(index);
		
		//IContext node = (IContext) getHost().getModel(); 
		return ViewUtil.getCompoundCommandWithResize(createElementCmd, ViewUtil.ADD, getHost());
	}
	
	protected int getIndex(CreateRequest request) {
		int index = flowPolicy.getIndex(request);
		
		if (index < 0){
			
			if (getHost() instanceof ContextEditPart) {
				index = ((ContextEditPart)getHost()).getChildren().size();
			}
			else if(getHost() instanceof AbstractPageEditPart) {
				index = ((AbstractPageEditPart)getHost()).getChildren().size();
			}
			else {
				index = 0;
			}
		}
		return index;
	}
	
	
}
