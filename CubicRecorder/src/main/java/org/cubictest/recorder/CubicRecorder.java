package org.cubictest.recorder;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.ChangeAbstractPageNameCommand;
import org.cubictest.ui.gef.command.CreatePageElementCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.eclipse.gef.commands.CommandStack;

public class CubicRecorder implements IRecorder {
	private Test test;
	private AbstractPage cursor;
	private UserInteractionsTransition userInteractionsTransition;
	private final CommandStack commandStack;
	private final AutoLayout autoLayout;
	
	public CubicRecorder(Test test, CommandStack comandStack, AutoLayout autoLayout) {
		this.test = test;
		this.commandStack = comandStack;
		this.autoLayout = autoLayout;
		for(Transition t : test.getStartPoint().getOutTransitions()) {
			if(t.getEnd() instanceof Page && ((Page)t.getEnd()).getElements().size() == 0) {
				this.cursor = (Page) t.getEnd();
			}
		}
		
		if(this.cursor == null) {
			this.cursor = this.addUserActions(test.getStartPoint());
		}
	}
	
	public CubicRecorder(Test test, Page cursor, CommandStack commandStack, AutoLayout autoLayout) {
		this.test = test;
		this.cursor = cursor;
		this.commandStack = commandStack;
		this.autoLayout = autoLayout;
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#setCursor(org.cubictest.model.AbstractPage)
	 */
	public void setCursor(AbstractPage page) {
		this.cursor = page;
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addPageElementToCurrentPage(org.cubictest.model.PageElement)
	 */
	public void addPageElementToCurrentPage(PageElement element) {
		CreatePageElementCommand createElementCmd = new CreatePageElementCommand();
		createElementCmd.setContext(this.cursor);
		createElementCmd.setPageElement(element);
		
		this.commandStack.execute(createElementCmd);
		this.autoLayout.layout(cursor);
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addPageElement(org.cubictest.model.PageElement)
	 */
	public void addPageElement(PageElement element) {
		if(this.userInteractionsTransition != null) {
			// Advance the cursor
			this.cursor = (AbstractPage) this.userInteractionsTransition.getEnd();
			this.userInteractionsTransition = null;
		}
		this.addPageElementToCurrentPage(element);
	}
	
	/* (non-Javadoc)
	 * @see org.cubictest.recorder.IRecorder#addUserInput(org.cubictest.model.UserInteraction)
	 */
	public void addUserInput(UserInteraction action) {
		boolean elementExists = false;
		for(PageElement element : cursor.getElements()) {
			if(action.getElement() == element) {
				elementExists = true;
			}
		}

		if(!elementExists) {
			this.addPageElement((PageElement) action.getElement());
		}
		
		if(this.userInteractionsTransition == null) {
			addUserActions(this.cursor);
		}
		
		this.userInteractionsTransition.addUserInteraction(action);
	}
	
	/**
	 * Create a new Page and a UserInteractionsTransition transition to it
	 */
	private AbstractPage addUserActions(TransitionNode from) {
		Page page = new Page();
		page.setAutoPosition(true);
				
		/* Add Page */
		AddAbstractPageCommand addPageCmd = new AddAbstractPageCommand();
		addPageCmd.setPage(page);
		addPageCmd.setTest(test);
		commandStack.execute(addPageCmd);

		/* Change Page Name */
		ChangeAbstractPageNameCommand changePageNameCmd = new ChangeAbstractPageNameCommand();
		changePageNameCmd.setAbstractPage(page);
		changePageNameCmd.setOldName("");
		if(cursor != null) {
			changePageNameCmd.setName(cursor.getName());	
		} else {
			changePageNameCmd.setName("untitled");
		}
		commandStack.execute(changePageNameCmd);
		
		/* Add Transition */
		CreateTransitionCommand createTransitionCmd = new CreateTransitionCommand();
		createTransitionCmd.setTest(test);
		createTransitionCmd.setSource(from);
		createTransitionCmd.setTarget(page);
		commandStack.execute(createTransitionCmd);

		autoLayout.layout(page);
		
		userInteractionsTransition = new UserInteractionsTransition(from, page);
		return page;
	}

	public void setStateTitle(String title) {
		ChangeAbstractPageNameCommand changePageNameCmd = new ChangeAbstractPageNameCommand();
		if(userInteractionsTransition != null) {
			changePageNameCmd.setAbstractPage((AbstractPage) userInteractionsTransition.getEnd());
			changePageNameCmd.setOldName(userInteractionsTransition.getEnd().getName());
		} else {
			changePageNameCmd.setAbstractPage(cursor);
			changePageNameCmd.setOldName(cursor.getName());
		}
		changePageNameCmd.setName(title);	
		this.commandStack.execute(changePageNameCmd);
	}
}
