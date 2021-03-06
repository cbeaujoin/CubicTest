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
package org.cubictest.mojos.selenium;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.EmptyTestSuiteException;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.runner.TestRunner;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;

/**
 * Goal which runs the CubicTest tests in the directory specified by "testDir" in the pom.xml.
 * 
 * @author Christian Schwarz
 * 
 * @goal run-tests
 */
public class MavenSeleniumRunner extends AbstractMojo
{
    private static final String SEPERATOR = "------------------------------------------------------------------------";
	private static final String SMALL_SEPERATOR = "-----------------------------------------------";
	private static final String LOG_PREFIX = "[CubicTest Selenium Runner] ";
	/**
     * Location of the tests.
     * @parameter
     * @required
     */
    private File testDir;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
    	String baseDir = System.getProperty("basedir", System.getProperty("user.dir"));
        CubicTestProjectSettings settings = new CubicTestProjectSettings(new File(baseDir));

        Collection files = FileUtils.listFiles(testDir, new String[] {"aat", "ats"}, true);
        Iterator iter = files.iterator();
        List<String> passedTests = new ArrayList<String>();
        List<String> failedTests = new ArrayList<String>();
        List<String> exceptionTests = new ArrayList<String>();
        List<String> notRunTests = new ArrayList<String>();
        for (Iterator fileIter = files.iterator(); fileIter.hasNext();) {
			File file = (File) fileIter.next();
			notRunTests.add(file.getName());
		}

        boolean buildOk = true;
        
        TestRunner testRunner = null;
        boolean useFreshBrowser = settings.getBoolean(SeleniumUtils.getPluginPropertyPrefix(), "useNewBrowserInstanceForEachTestSuiteFile", false);
        getLog().info(LOG_PREFIX + " Use new browser instance for each test suite file: " + useFreshBrowser);
        boolean reuseBrowser = !useFreshBrowser;
        if (reuseBrowser) {
			testRunner = new TestRunner(null, null, settings, true);
			testRunner.setReuseSelenium(true);
			testRunner.setFailOnAssertionFailure(true);
        }
        while (iter.hasNext()) {
        	File file = (File) iter.next();
        	getLog().info(LOG_PREFIX + "Running test: " + file);
        	Test test = TestPersistance.loadFromFile(file, null);
        	getLog().info(LOG_PREFIX + "Test loaded: " + test.getName());

    		try {
    			notRunTests.remove(file.getName());
    			if (reuseBrowser) {
    				testRunner.setTest(test);
        			testRunner.run(null);
        			passedTests.add(file.getName());
    			}
    			else {
    				testRunner = new TestRunner(test, null, settings,true);
        			testRunner.setFailOnAssertionFailure(true);
        			testRunner.run(null);
        			passedTests.add(file.getName());
                	smallLogSeperator();
                	getLog().info("Test run finished: " + file.getName() + ": " + testRunner.getResultMessage());
                	smallLogSeperator();
        			stopSelenium(testRunner);
        			Thread.sleep(800); //do not reopen firefox immediately
    			}
    		}
    		catch (EmptyTestSuiteException e) {
    			getLog().info(SEPERATOR);
    			getLog().warn("Test suite was empty: " + file.getName());
    			getLog().warn("Test suites should contain at least one test. " + 
						"To add a test, drag it from the package explorer into the test suite editor.");
    			return;
    		}
    		catch (ExporterException e) {
    			getLog().error(LOG_PREFIX + "Test failure detected. Stopping Selenium.");
    			stopSelenium(testRunner);
            	logSeperator();
    			getLog().error("Failure in test " + file.getName() + ": " + e.getMessage());
            	logSeperator();
            	getLog().info("Failure path: " + file.getName() + " --> " + testRunner.getCurrentBreadcrumbs());
            	if (!reuseBrowser) {
            		logSeperator();
	            	getLog().info(file.getName() + ": " + testRunner.getResultMessage());
            	}
    			failedTests.add(file.getName());
    			buildOk = false;
    			break;
    		}
    		catch (Throwable e) {
    			getLog().error(LOG_PREFIX + "Error detected during test run. Stopping Selenium.");
    			stopSelenium(testRunner);
    			getLog().error(e);
    			exceptionTests.add(file.getName());
    			buildOk = false;
    			break;
			}
        }
		if (reuseBrowser) {
			if (!files.isEmpty() && testRunner != null) {
    			stopSelenium(testRunner);
			}
	    	logSeperator();
        	getLog().info("Test run finished: " + testRunner.getResultMessage());
		}        
    	logSeperator();
        getLog().info("Tests passed: " + passedTests.toString());
        getLog().info("Tests failed: " + failedTests.toString());
        getLog().info("Threw exception: " + exceptionTests.toString());
        getLog().info("Tests not run: " + notRunTests.toString());

        if (!buildOk) {
        	throw new MojoFailureException("[CubicTest] There were test failures.");
        }
    }

	private void logSeperator() {
		getLog().info(SEPERATOR);
	}

	private void smallLogSeperator() {
		getLog().info(SMALL_SEPERATOR);
	}

	private void stopSelenium(TestRunner testRunner) {
		try {
			((TestRunner) testRunner).stopSeleniumWithTimeoutGuard(20);
		}
		catch (Exception e) {
			getLog().error("Error stopping selenium.", e);
		}
	}
 
}
