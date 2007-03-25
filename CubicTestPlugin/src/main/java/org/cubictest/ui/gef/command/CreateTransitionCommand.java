/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.wizards.ExposeExtensionPointWizard;
import org.cubictest.ui.gef.wizards.NewUserInteractionsWizard;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * A command that creates a <code>Transition</code>.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class CreateTransitionCommand extends Command {

	private TransitionNode sourceNode;

	private TransitionNode targetNode;

	/** Cached transition for redo */
	private Transition transition;

	private Test test;
	
	private PageEditPart pageEditPart;
	
	private boolean autoCreateTargetPage = false;


	/*
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (autoCreateTargetPage) {
			return true;
		}
		return (ModelUtil.isLegalTransition(sourceNode, targetNode, false) == ModelUtil.TRANSITION_EDIT_VALID);
	}


	/*
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();
		if (autoCreateTargetPage) {
			targetNode = new Page();
			Point position = sourceNode.getPosition().getCopy();
			if (pageEditPart == null) {
				ErrorHandler.logAndThrow("PageEditPart not set. Cannot auto create target page.");
			}
			position.y = position.y + this.pageEditPart.getContentPane().getClientArea().height + 80;
			if (sourceNode.getOutTransitions() != null) {
				int outTrans = sourceNode.getOutTransitions().size();
				position.x = position.x + (outTrans * 160);
			}
			targetNode.setPosition(position);
			test.addPage((Page) targetNode);
		}
		
 		if(transition == null) {
			if (sourceNode instanceof SubTest && (targetNode instanceof Page || targetNode instanceof SubTest)) {
				//ExtensionTransition from SubTest
				SubTest subTest = (SubTest) sourceNode;
				List<ExtensionPoint> exPoints = subTest.getTest().getAllExtensionPoints();
				if (exPoints == null || exPoints.size() == 0) {
					ErrorHandler.showErrorDialog("The \"" + subTest.getFileName() + "\" subtest does not contain any extension points.\n" +
							"To continue, first add an extension point to the subtest and then retry this operation.");
					return;
				}
				else if (exPoints.size() == 1) {
					//auto select the single exPoint 
					transition = new ExtensionTransition(sourceNode, targetNode, exPoints.get(0));
				}
				else {
					//open dialog to select which exPoint to extend from:
					ExposeExtensionPointWizard exposeExtensionPointWizard = new ExposeExtensionPointWizard(
							(SubTest) sourceNode, test);
					WizardDialog dlg = new WizardDialog(new Shell(),
							exposeExtensionPointWizard);
					if (dlg.open() == WizardDialog.CANCEL) {
						return;
					}
					transition = new ExtensionTransition(sourceNode, targetNode,
							exposeExtensionPointWizard.getSelectedExtensionPoint());
				}
			}
			else if(sourceNode instanceof Page && (targetNode instanceof Page || 
					targetNode instanceof SubTest || targetNode instanceof CustomTestStep)) {
				//User Interactions transition:
				transition = new UserInteractionsTransition(sourceNode, targetNode);
				NewUserInteractionsWizard userActionWizard = new NewUserInteractionsWizard(
						(UserInteractionsTransition) transition, test);
				WizardDialog dlg = new WizardDialog(new Shell(), userActionWizard);

				if (dlg.open() == WizardDialog.CANCEL) {
					DeleteTransitionCommand cmd = new DeleteTransitionCommand(
							transition, test);
					cmd.execute();
					transition.resetStatus();
					if (autoCreateTargetPage) {
						test.removePage((Page) targetNode);
					}
					return;
				}
			}
			else if (sourceNode instanceof Common && targetNode instanceof Page) {
				transition = new CommonTransition((Common) sourceNode,
						(Page) targetNode);
			}
			else if (sourceNode instanceof ConnectionPoint) {
				transition = new SimpleTransition((ConnectionPoint) sourceNode,
						targetNode);
			}
			else if (targetNode instanceof ExtensionPoint) {
				transition = new SimpleTransition(sourceNode, targetNode);
			}			
		}

		if(transition != null) {
			test.addTransition(transition);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.removeTransition(transition);
		if (autoCreateTargetPage) {
			test.removePage((Page) targetNode);
		}
	}

	@Override
	public void redo() {
		if(transition != null) {
			test.addTransition(transition);
		}
		if (autoCreateTargetPage) {
			test.addPage((Page) targetNode);
		}
	}


	public void setTest(Test test) {
		this.test = test;
	}

	public void setPageEditPart(PageEditPart pageEditPart) {
		this.pageEditPart = pageEditPart;
	}

	public void setSource(TransitionNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	public void setTarget(TransitionNode targetNode) {
		this.targetNode = targetNode;
	}


	public void setAutoCreateTargetPage(boolean autoCreateTargetPage) {
		this.autoCreateTargetPage = autoCreateTargetPage;
	}
}