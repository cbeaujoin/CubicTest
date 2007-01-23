/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * Class representing a single action from a user.
 * The action is done on a IActionElement, e.g. a page element.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class UserInteraction extends PropertyAwareObject 
			implements SationObserver{
	
	/** The element upon which "the user" performs an action */
	private IActionElement element;
	
	/** The textual user input (only applicable to certain actions, like "enter text"-action) */
	protected String input = "";
	
	/** The action the user performs */
	private  ActionType action = ActionType.NO_ACTION;
	
	private SationType sationType = SationType.NONE;
	private String key = "";
	
	public UserInteraction(){}
	
	
	/**
	 * Construct a new UserInteraction representing a single user interaction on a single page element.
	 * @param element The action element (e.g. page element) that the action is applied to.
	 * @param action The action the user performs
	 * @param input The user textual input (only applicable to certain actions, like "enter text"-action).
	 */
	public UserInteraction(IActionElement element, ActionType action, String input){
		this.element = element;
		this.action = action;
		this.input = input;
	}
	
	
	/** 
	 * Get the action element (e.g. page element) that the action is applied to.
	 * @return the action element (e.g. page element) that the action is applied to.
	 */
	public IActionElement getElement() {
		return element;
	}
	
	
	/** 
	 * Set the action element (e.g. page element) that the action is applied to.
	 * @param element The action element (e.g. page element) that the action is applied to.
	 */
	public void setElement(IActionElement element) {
		IActionElement oldPE = this.element;
		this.element = element;
		firePropertyChange(CHILD, oldPE, element);
		firePropertyChange(LAYOUT, oldPE, element);
		if (element != null) {
			setActionType(element.getDefaultAction());
		}
	}
	
	/**
	 * Get the user textual input (only applicable to certain actions, like the "enter text"-action).
	 * @return the user textual input (only applicable to certain actions, like the "enter text"-action).
	 */
	public String getTextualInput(){
		return input;
	}
	
	/**
	 * Set the user textual input (only applicable to certain actions, like the "enter text"-action).
	 * @param input the user textual input (only applicable to certain actions, like the "enter text"-action).
	 */
	public void setTextualInput(String input){
		String oldInput = this.input;
		this.input = input;
		firePropertyChange(PropertyAwareObject.NAME, oldInput, input);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldInput, input);
	}
	
	/**
	 * Get the ActionType the user performs, e.g. Click. 
	 * @return the ActionType the user performs, e.g. Click.
	 */
	public ActionType getActionType(){
		return action;
	}

	/**
	 * Set the ActionType the user performs, e.g. Click. 
	 * @param action the ActionType the user performs, e.g. Click.
	 */
	public void setActionType(ActionType action){
		ActionType oldAction = this.action;
		this.action = action;
		firePropertyChange(PropertyAwareObject.NAME, oldAction, action);
		firePropertyChange(PropertyAwareObject.LAYOUT, oldAction, action);
	}
		
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);
	}
	
	public void setSationType(SationType type) {
		SationType oldType = sationType;
		sationType = type;
		firePropertyChange(PropertyAwareObject.NAME, oldType, type);
	}
	
	public SationType getSationType() {
		return sationType;
	}
	

	@Override
	public boolean equals(Object arg) {
		if (arg.toString().endsWith("-ACTION"))
			return toString().concat("-ACTION").equals(arg.toString());
		else if (arg.toString().endsWith("-INPUT"))
			return toString().concat("-INPUT").equals(arg.toString());
		else if (arg.toString().endsWith("-ELEMENT"))
			return toString().concat("-ELEMENT").equals(arg.toString());
		else
			return toString().equals(arg.toString()); 
	}

	
	public void setText(String text){
		setTextualInput(text);
	}
	public String getText() {
		return getTextualInput();
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key){
		this.key = key;
	}
	
	public String toString() {
		String result = new String();
		result += action.getText();
		if(action.acceptsInput()) {
			result += " '" + input + "'";
		}
		if (element == null)
			result += " @ " + "null element";			
		else
			result += " @ " + element.getDescription();
		return result;
	}
}