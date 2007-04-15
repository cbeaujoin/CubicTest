/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import static org.cubictest.model.IdentifierType.LABEL;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Base class for elements on a page.
 * 
 * @author skyttere
 * @author chr_schwarz
 *
 */
public abstract class PageElement extends PropertyAwareObject 
			implements Cloneable, IActionElement, IDescription {
	
	public static final String DIRECT_EDIT_IDENTIFIER = "directEditIdentifier";
	private String description = "";
	private List<Identifier> identifiers;
	private Identifier directEditIdentifier;
	private boolean not;
	private transient boolean idListWashed;
	
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[" + getType() + "] ");
		int i = 0;
		for (Identifier id : getNonIndifferentIdentifierts()) {
			if (StringUtils.isNotBlank(id.getValue())) {
				if (i > 0) {
					buff.append(", ");
				}
				buff.append(id);
			}
			i++;
		}
		if (not) {
			buff.append(", not = " + this.not);
		}
		if (StringUtils.isNotBlank(description)) {
			buff.append(", description = \"" + this.description + "\"");
		}
		return buff.toString();
	}
	
	/**
	 * Get the user interaction types that is possible on this page elemenent.
	 */
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.CLICK);
		actions.add(ActionType.MOUSE_OVER);
		actions.add(ActionType.MOUSE_OUT);
		actions.add(ActionType.DBLCLICK);
		return actions;
	}
	
	@Override
	public void resetStatus() {
		setStatus(TestPartStatus.UNKNOWN);		
	}
	
	@Override
	public PageElement clone() throws CloneNotSupportedException {
		PageElement clone = (PageElement) super.clone();
		clone.setDescription(description);
		clone.setDirectEditIdentifier(null);
		List<Identifier> clonedIdentifiers = new ArrayList<Identifier>();
		for(Identifier id : identifiers){
			Identifier idClone = id.clone();
			clonedIdentifiers.add(idClone);
			if (idClone.equals(directEditIdentifier)) {
				clone.setDirectEditIdentifier(idClone);
			}
		}
		clone.setIdentifiers(clonedIdentifiers);
		return clone;
	}
	
	/**
	 * Get the text that is shown in the CubicTest GUI for the page element.
	 * @return the text that is shown in the CubicTest GUI for the page element.
	 */
	public String getDescription() {
		if (description == null)
			return "";
		return description;
	}

	/**
	 * Set the text that is shown in the CubicTest GUI for the page element.
	 * @param text The text that is shown in the CubicTest GUI for the page element.
	 */
	public void setDescription(String desc) {
		String oldDesc = this.description;
		this.description = desc;
		firePropertyChange(PropertyAwareObject.NAME, oldDesc, desc);
	}
	
	public String getText() {
		return getDirectEditIdentifier().getValue();
	}
	
	/**
	 * Set the value of the Direct Edit identifieer.
	 */
	public void setText(String text) {
		getDirectEditIdentifier().setValue(text);
	}
	
	/**
	 * Get the user friendly type-name of this page element.
	 */
	public abstract String getType();
	
	/**
	 * Get the default action for user interaction with this page element.
	 */
	public ActionType getDefaultAction(){
		return getActionTypes().get(0);
	}
	
	public void setIdentifiers(List<Identifier> identifiers) {
		this.identifiers = identifiers;
	}
	
	public List<Identifier> getIdentifiers() {
		if(identifiers == null){
			identifiers = new ArrayList<Identifier>();
			for(IdentifierType type : getIdentifierTypes()){
				Identifier identifier = new Identifier();
				identifier.setType(type);
				identifiers.add(identifier);
			}
			if(identifiers.size() > 0)
				identifiers.get(0).setProbability(Identifier.MAX_PROBABILITY);
		}
		
		//washing list (possibly add / remove IDs according to type list)
		washIdList();
		return identifiers;
	}

	private void washIdList() {
		if (idListWashed) {
			return;
		}
		//washing list (possibly adding new ids from type list)
		for (IdentifierType type : getIdentifierTypes()) {
			boolean found = false;
			for (Identifier id : identifiers) {
				if (id.getType().equals(type)) {
					found = true;
				}
			}
			if (!found) {
				Identifier newId = new Identifier();
				newId.setType(type);
				identifiers.add(newId);
			}
		}

		//washing list (possibly removing old id types that are no longer in type list)
		List<Identifier> toRemove = new ArrayList<Identifier>();
		for (Identifier id : identifiers) {
			if (!getIdentifierTypes().contains(id.getType())) {
				toRemove.add(id);
			}
		}
		identifiers.removeAll(toRemove);
		idListWashed = true;
	}
	
	
	/**
	 * Get the identifiers that have a value and do not have probability = indifferent.
	 * @return
	 */
	public List<Identifier> getNonIndifferentIdentifierts() {
		List<Identifier> list = new ArrayList<Identifier>();
		for (Identifier id : getIdentifiers()) {
			if (id.getProbability() != 0) {
				list.add(id);
			}
		}
		return list;
	}
	
	/**
	 * Set the identifier types that this page elements supports.
	 */
	public void addIdentifier(Identifier identifier) {
		identifiers.add(identifier);
		firePropertyChange(PropertyAwareObject.NAME, null, identifier);
	}
	
	/**
	 * Get the identifier types that this page elements supports.
	 * @return the identifier types that this page elements supports.
	 */
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		return list;
	}
	
	public void setDirectEditIdentifier(Identifier directEditIdentifier) {
		Identifier oldDirectEditIdentifier = this.directEditIdentifier;
		this.directEditIdentifier = directEditIdentifier;
		firePropertyChange(DIRECT_EDIT_IDENTIFIER, oldDirectEditIdentifier, directEditIdentifier);
	}
	
	public Identifier getDirectEditIdentifier() {
		if(directEditIdentifier == null){
			directEditIdentifier = getIdentifiers().get(0);
		}
		return directEditIdentifier;
	}
	
	public boolean isNot(){
		return not;
	}
	
	public void setNot(boolean not){
		boolean oldNot = this.not;
		this.not = not;
		firePropertyChange(PropertyAwareObject.NOT, oldNot, not);
	}

	
	/**
	 * Gets the Identifier with the highest probability (must be greater than "indifferent").
	 * If more than one has the same probability, returns the one with DirectEdit or the first.
	 */
	public Identifier getMainIdentifier() {
		int highestProbability = 0;
		Identifier result = null;
		
		for (Identifier identifier : getIdentifiers()) {
			if (identifier.getProbability() >  highestProbability) {
				result = identifier;
				highestProbability = identifier.getProbability();
			}
			else if (identifier.getProbability() ==  highestProbability) {
				//direct edit has precedence:
				if (getDirectEditIdentifier().equals(identifier)) {
					result = identifier;
					highestProbability = identifier.getProbability();
				}
				else if (StringUtils.isNotBlank(identifier.getValue()) && (result == null || StringUtils.isBlank(result.getValue()))) {
					//the ID with a non-null value has precedence:
					result = identifier;
					highestProbability = identifier.getProbability();
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the type of the Identifier with the highest probability (must be greater than "indifferent").
	 * If more than one has the same probability, returns the one with DirectEdit or the first.
	 */
	public IdentifierType getMainIdentifierType() {
		return getMainIdentifier().getType();
	}
	
	/**
	 * Gets the value of the Identifier with the highest probability (must be greater than "indifferent").
	 * If more than one has the same probability, returns the one with DirectEdit or the first.
	 */
	public String getMainIdentifierValue() {
		return getMainIdentifier().getValue();
	}
	
	/**
	 * Set identifier on page element.
	 */
	public void setIdentifier(IdentifierType idType, String value, int probability, boolean directEdit) {
		getIdentifier(idType).setValue(value);
		getIdentifier(idType).setProbability(probability);
		if (directEdit) {
			setDirectEditIdentifier(getIdentifier(idType));
		}
	}
	
	/**
	 * Get the identifier of the supplied type.
	 * If element does not have the type as legal identifieer, returns null.
	 * @param idType
	 * @return
	 */
	public Identifier getIdentifier(IdentifierType idType) {
		for (Identifier identifier : getIdentifiers()) {
			if (identifier.getType().equals(idType)) {
				return identifier;
			}
		}
		return null;
	}
}
