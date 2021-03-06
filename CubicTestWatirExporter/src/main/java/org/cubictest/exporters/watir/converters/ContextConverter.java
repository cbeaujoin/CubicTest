/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.watir.converters.delegates.ContextAsserterXPath;
import org.cubictest.exporters.watir.converters.delegates.PageElementAsserterPlain;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<WatirHolder> {

	public PreContextHandle handlePreContext(WatirHolder watirHolder, IContext ctx) {
		if (ctx instanceof AbstractPage) {
			return PreContextHandle.CONTINUE;
		}
		else if (ctx instanceof Select) {
			new PageElementConverter().handlePageElement(watirHolder, (Select) ctx);
			return PreContextHandle.CONTINUE;
		}
		
		PageElement pe = (PageElement) ctx;
		watirHolder.registerPageElement(pe);
		watirHolder.addSeparator();
		IContext context = (IContext) ctx;
		watirHolder.pushContext(context);

		watirHolder.add("# asserting " + context.toString() + " present", 2);
		watirHolder.add("begin", 2);
		
		if (pe instanceof Frame) {
			//Watir does not support XPath locator for frames. Use plain asserter:
			PageElementAsserterPlain.handle(watirHolder, pe);
			watirHolder.pushContainer(watirHolder.getVariableName(pe));
		}
		else {
			ContextAsserterXPath.handle(watirHolder, pe);
		}

		PageElement element = (PageElement) ctx;
		watirHolder.add("puts \"" + WatirHolder.PASS + watirHolder.getId(element) + "\"", 3);
		watirHolder.add("passedSteps += 1 ", 3);
		watirHolder.add("rescue " + WatirHolder.TEST_STEP_FAILED, 2);
		watirHolder.add("puts \"" + WatirHolder.FAIL + watirHolder.getId(element) + "\"", 3);
		watirHolder.add("failedSteps += 1 ", 3);
		watirHolder.add("puts \"Step failed: Check " + element.toString() + " present\"", 3);
		if (ctx instanceof Frame) {
			//frames fail hard when not found. Catch all exceptions:
			watirHolder.add("rescue", 2);
			if (((PageElement) ctx).isNot()) {
				//we actually have a passed asserton
				watirHolder.add("puts \"" + WatirHolder.PASS + watirHolder.getId(element) + "\"", 3);
				watirHolder.add("passedSteps += 1 ", 3);
			} else {
				watirHolder.add("puts \"" + WatirHolder.FAIL + watirHolder.getId(element) + "\"", 3);
				watirHolder.add("failedSteps += 1 ", 3);
				watirHolder.add("puts \"Step failed: Check " + element.toString() + " present\"", 3);
			}
		}
		watirHolder.add("end", 2);
		watirHolder.add("STDOUT.flush", 2);
		
		return PreContextHandle.CONTINUE;
	}


	public PostContextHandle handlePostContext(WatirHolder watirHolder, IContext ctx) {
		if (ctx instanceof Frame) {
			watirHolder.popContainer();
		}
		return PostContextHandle.DONE;
	}
}
