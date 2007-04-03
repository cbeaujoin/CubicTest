/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<SeleniumHolder> {

	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(SeleniumHolder seleniumHolder, IContext ctx) {
		
		if (ctx instanceof AbstractContext) {
			seleniumHolder.pushContext(ctx);
		}
		return PreContextHandle.CONTINUE;
	}

	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(SeleniumHolder seleniumHolder, IContext ctx) {
		
		if (ctx instanceof AbstractContext) {
			seleniumHolder.popContext();
		}
		return PostContextHandle.DONE;
	}
}
