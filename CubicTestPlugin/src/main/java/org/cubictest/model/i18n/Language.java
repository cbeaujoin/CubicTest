/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model.i18n;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

public class Language{
	private Properties properties;
	private String name = "";
	private transient IFile file;
	private String fileName = "";
	
	
	private IFile getFile(){
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
	}
	
	public Language(){
		properties = new Properties();
	}
	
	public Language(IFile inFile){
		this();
		if(inFile == null)
			this.file = getFile();
		else {
			this.file = inFile;
			this.fileName = file.getFullPath().toOSString();
		}
		
		updateLanguage();
	}

	public String get(String key) {
		if(key == null)
			key = keySet().iterator().next();
		if(key == null)
			return "";
		return getProperties().getProperty(key);
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for(Object o : getProperties().keySet()){
			result.add((String) o);
		}
		return result;
	}

	public String getFileName() {
		return fileName;
	}

	private Properties getProperties() {
		if (properties == null) {
			try {
				properties = new Properties();
				properties.load(getFile().getContents());
			} catch (IOException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			} catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			}
		}
		return properties;
	}

	public void updateLanguage() {
		try {
			properties.load(getFile().getContents());
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
}
