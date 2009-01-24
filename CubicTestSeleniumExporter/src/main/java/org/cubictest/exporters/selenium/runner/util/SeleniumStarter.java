/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.util;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.runner.RunnerStarter;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.UrlStartPoint;

import com.thoughtworks.selenium.Selenium;

/**
 * Controller that starts/stops the Selenium Server and Selenium test system (SeleniumHolder).
 * Implements callable, and hence supports timeout of start/stop of Selenium.
 * 
 * @author Christian Schwarz
 */
public class SeleniumStarter extends RunnerStarter<SeleniumHolder> {

	SeleniumProxyServer server;
	SeleniumHolder seleniumHolder;
	UrlStartPoint initialUrlStartPoint;
	boolean seleniumStarted;
	Selenium selenium;
	private boolean startNewSeleniumServer = true;
	private SeleniumRunnerConfiguration config;
	
	
	@SuppressWarnings("unused")
	private SeleniumStarter(){}
	
	public SeleniumStarter(SeleniumRunnerConfiguration configuration) {
		this.config = configuration;
	}
	
	/**
	 * Start the Selenium Proxy Server and start and return the Selenium test object.
	 */
	@Override
	public SeleniumHolder doStart() {
		if (selenium == null && startNewSeleniumServer) {
			server = new SeleniumProxyServer(config);
			server.start();
			while (!server.isStarted()) {
				//wait for server thread to start
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new ExporterException(e);
				}
			}
		}
		else {
			//We got a Selenium from Client process. It should already have a proxy server configured and started
		}

		if (selenium == null) {
			String browserName = config.getBrowser().getId();
			browserName = browserName.startsWith("*") ? browserName.substring(1) : browserName;
			Logger.info("Opening " + browserName + " and connecting to Selenium Proxy at port " + config.getPort() + ", " + initialUrlStartPoint);
			String initUrl = initialUrlStartPoint.getBeginAt();
			String baseUrl = initUrl.substring(0, initUrl.lastIndexOf("/") + 1);
			if (baseUrl.endsWith("://")) {
				baseUrl = initUrl;
			}
			seleniumHolder = new SeleniumHolder(config.getSeleniumServerHostname(), config.getPort(), config.getBrowser().getId(), baseUrl, display, settings);
			seleniumHolder.getSelenium().start();
			//using selenium default timeout, open start URL and check connection (that browser profiles has been set correctly):
			seleniumHolder.getSelenium().open(initUrl);

			int timeout = SeleniumUtils.getTimeout(settings);
			seleniumHolder.getSelenium().setTimeout((timeout * 1000) + "");
			seleniumHolder.setNextPageElementTimeout(timeout);
		}
		else {
			//use custom Selenium, e.g. from the CubicRecorder.
			Logger.info("Using Selenium from another plugin.");
			seleniumHolder = new SeleniumHolder(selenium, display, settings);
		}
		seleniumStarted = true;
		
		//using two started variables, as one of them has sanity check of invoking start URL built into it.
		seleniumHolder.setSeleniumStarted(true);
		
		seleniumHolder.setHandledUrlStartPoint(initialUrlStartPoint);
		return seleniumHolder;
	}
		
	
	
	@Override
	protected SeleniumHolder doStop() {
		try {
			if (seleniumHolder != null && seleniumStarted) {
				seleniumHolder.getSelenium().stop();
				seleniumHolder.setSeleniumStarted(false);
				//two started variables, as one of them has sanity check of invoking start URL built into it.
				seleniumStarted = false;
			}
		} 
		catch (Exception e) {
			ErrorHandler.logAndRethrow("Error when stopping selenium test system", e);
		}
		finally {
			try {
				if (server != null) {
					server.stop();
				}
			} 
			catch (InterruptedException e) {
				ErrorHandler.logAndRethrow("Error when stopping server", e);
			}
		}	
		return null;
	}

	
	public void setInitialUrlStartPoint(UrlStartPoint initialUrlStartPoint) {
		this.initialUrlStartPoint = initialUrlStartPoint;
	}

	public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}

	public void setStartNewSeleniumServer(boolean startNewSeleniumServer) {
		this.startNewSeleniumServer = startNewSeleniumServer;
	}
}
