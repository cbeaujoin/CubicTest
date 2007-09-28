/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import static org.cubictest.model.ActionType.SELECT;

import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Transition converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class TransitionHandlerPlain {


	public static void handle(WatirHolder watirHolder, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();
		String idType = WatirUtils.getMainIdType(pe);
		String idValue = "\"" + pe.getMainIdentifierValue() + "\"";

		//Handle Label identifier:
		if (WatirUtils.shouldGetLabelTargetId(pe)) {
			watirHolder.add(WatirUtils.getLabelTargetId(pe, watirHolder));
			watirHolder.addSeparator();
			idValue = watirHolder.getVariableName(pe);
			idType = ":id";
		}
		
		if (userInteraction.getActionType().equals(SELECT) && userInteraction.getElement() instanceof Option) {
			//select option in select list:
			selectOptionInSelectList(watirHolder, (Option) pe, idType, idValue);	
		}
		else {
			//handle all other interaction types:
			watirHolder.add("ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idValue + ")." + WatirUtils.getInteraction(userInteraction), 3);
		}
	}

		
		
	/**
	 * Selects the specified option in a select list.
	 */
	private static void selectOptionInSelectList(WatirHolder watirHolder, Option option, String idType, String idText) {
		Select select = (Select) option.getParent();
		String selectIdValue = "\"" + select.getMainIdentifierValue() + "\"";
		String selectIdType = WatirUtils.getMainIdType(select);
		
		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			//Handle label:
			watirHolder.add(WatirUtils.getLabelTargetId(select, watirHolder));
			selectIdValue = watirHolder.getVariableName(select);
			selectIdType = ":id";
		}
		
		String selectList = "ie.select_list(" + selectIdType + ", " + selectIdValue + ")";
		
		//Select the option:
		if (option.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			watirHolder.add(selectList + ".select(" + idText + ")", 3);
		}
		else if (option.getMainIdentifierType().equals(IdentifierType.VALUE)) {
			watirHolder.add(selectList + ".option(" + idType + ", " + idText + ").select()", 3);
		}
		else if (option.getMainIdentifierType().equals(IdentifierType.INDEX)) {
			//select optionLabel found earlier
			watirHolder.add(selectList + ".select(" + watirHolder.getVariableName(option) + ")", 3);
		}
	}
		
}
