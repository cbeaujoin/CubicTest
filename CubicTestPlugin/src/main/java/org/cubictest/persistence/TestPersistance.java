/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.exception.TestNotFoundException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * Class responsible for writing a test to file and loading a test from file. 
 * 
 * @author chr_schwarz
 */
public class TestPersistance {
	

	/**
	 * Writes a test to the given IFile. For Eclipse-internal use.
	 * 
	 * @param test The test to save.
	 * @param iFile The file to save to.
	 */
	public static void saveToFile(Test test, IFile iFile) {
		File f = iFile.getLocation().toFile();
		saveToFile(test, f);
	}
	
	
	/**
	 * Writes a test to the given file.
	 * 
	 * @param test The test to save.
	 * @param file The file to save to.
	 */
	public static void saveToFile(Test test, File file) {
		String xml = new CubicTestXStream().toXML(test);
		try {
			FileUtils.writeStringToFile(file, xml, "ISO-8859-1");
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
	}

	
	/**
	 * Reads a test from File, upgrading legacy tests if necessary.
	 * 
	 * @param file The file containing the test. 
	 * @return The test.
	 */
	public static Test loadFromFile(File file, IProject project) {
		String xml = "";
		try {
			xml = FileUtils.readFileToString(file, "ISO-8859-1");
			xml = LegacyUpgrade.upgradeIfNecessary(xml, project);
		} catch (FileNotFoundException e) {
			Logger.error("Test file not found.", e);
			throw new TestNotFoundException(e.getMessage());
		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
		
		Test test = (Test) new CubicTestXStream().fromXML(xml);
		return test;
	}

	
	/**
	 * Reads a test from IFile.
	 * 
	 * @param file The file containing the test.
	 * @return The test.
	 */	
	public static Test loadFromFile(IFile file) {
		IPath path = file.getLocation();
		if (path == null) {
			throw new CubicException("Could not get absolute path from IFile " + file);
		}
		Test test = loadFromFile(path.toFile(), file.getProject());
		test.setFile(file);
		return test;
	}
	
	
	/**
	 * Loads tests with the given file name.
	 * @param fileName
	 * @return
	 */
	public static Test loadFromFile(IProject project, String fileName) {
		if (project == null) {
			//try to get file without project
			return loadFromFile(new File(fileName), null);
		}
		try {
			IFile testFile = project.getFile(new Path(fileName));
			return loadFromFile(testFile);
		}
		catch (IllegalStateException e) {
			if (e.getMessage().indexOf("Workspace is closed") >= 0) {
				//Probably junit testing
				return loadFromFile(new File(fileName), null);
			}
		}
		throw new CubicException("Could not load test with file name = " + fileName);
	}
	

}
