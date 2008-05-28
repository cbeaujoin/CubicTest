package org.cubictest.selenium.custom;

import java.util.Map;

import com.thoughtworks.selenium.Selenium;

/**
 * The implementation is called from CubicTest to execute an Custom Test Step.
 * 
 * @author SK Skytteren
 */
public interface ICustomTestStep {
		
	/**
	 * Execute the test this element represents
	 * 
	 * We expect you to user JUnit type assertions like assertEquals, etc. 
	 * This way CubicTest can handle the CustomTestStep interaction correctly.
	 * Please throw other kinds of exceptions as well.
	 * 
	 * @param arguments The properties that is set in the properties page in CubicTest.
	 * @param context The purpose of the context is to send messages from one custom step to another instead of using static variables. 
	 * @param selenium The Selenium Rich Client.
	 * @throws any type of exception or error. It is handled by CubicTest.
	 */
	public void execute(Map<String,String> arguments, IElementContext context, Selenium selenium) throws Exception;
}

