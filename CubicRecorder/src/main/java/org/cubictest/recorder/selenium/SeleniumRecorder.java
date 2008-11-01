/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder.selenium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.recorder.IRecorder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.metaparadigm.jsonrpc.JSONRPCServlet;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumRecorder implements IRunnableWithProgress {
	private boolean seleniumStarted;
	private Selenium selenium;
	BrowserType browser;
	private SeleniumServer seleniumProxy;
	private int port = -1;
	private final String url;
	private Thread serverThread;
	protected Shell shell;

	public SeleniumRecorder(IRecorder recorder, String url, Shell shell, BrowserType browser) {
		this.url = url;
		this.shell = shell;
		this.browser = browser;
		
		// start server
		
		try {
			port = ExportUtils.findAvailablePort();
			System.out.println("Port: " + port);
			RemoteControlConfiguration config = new RemoteControlConfiguration();
			config.setMultiWindow(false);
			config.setPort(port);
			config.setPortDriversShouldContact(port);
			seleniumProxy = new SeleniumServer(false, config);
			
			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = server.getContext("/selenium-server/cubic-recorder/");
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServlet("JSON-RPC", "/JSON-RPC", JSONRPCServlet.class.getName());
			cubicRecorder.addHandler(servletHandler);
			String baseUrl = getBaseUrl(url);
			servletHandler.getSessionManager().addEventListener(new SeleniumRecorderSessionListener(recorder, baseUrl));
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Got an error when starting the recorder.", e);
		}
	}
	
	private String getBaseUrl(String u) {
		if (u.indexOf(".") < 0) {
			//url is already relative
			return u;
		}
		return u.substring(0, u.lastIndexOf("/") + 1);
	}

	public void stop() {
		try {
			if (selenium != null)
				selenium.stop();
		} catch(SeleniumException e) {
			Logger.error(e.getMessage(), e);
		}
		try {
			if (seleniumProxy != null)
				seleniumProxy.stop();
		} catch(SeleniumException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		serverThread = new Thread() {

			@Override
			public void run() {
		        try {
					seleniumProxy.start();
					selenium = new DefaultSelenium("localhost", seleniumProxy.getPort(), browser.getId(), url);
					selenium.start();
					selenium.open(url);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						ErrorHandler.logAndShowErrorDialogAndRethrow(e);
					}
					seleniumStarted = true;
		        } catch (final Exception e) {
		        	String msg = "";
		        	if (e.toString().indexOf("Location.href") >= 0 || e.toString().indexOf("Security error") >= 0) {
		        		msg += "Looks like Selenium failed when following a redirect. If this occured at start of test, " +
		        				"try modifying the start point URL to the correct/redirected address.\n\n";
		        	}
		        	msg += "Error occured when recording test. Recording might not work.";
		        	final String finalMsg = msg;
		    		shell.getDisplay().asyncExec(new Runnable() {
		    			public void run() {
		    				UserInfo.showErrorDialog(e, finalMsg);
		    			}
		    		});
					ErrorHandler.logAndRethrow(finalMsg, e);
				}
			}
			
			
		};
		
		serverThread.start();
	}
	


	public Selenium getSelenium() {
		return selenium;
	}

	public boolean isSeleniumStarted() {
		return seleniumStarted;
	}

}
