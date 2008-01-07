package org.cubictest.exporters.selenium.runner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

public class SeleniumClasspathVariableInitializer extends
		ClasspathVariableInitializer {
	
	@Override
	public void initialize(String variable) {
		try {
			IPath location= getBundleLocation();
			if (location != null) {
				JavaCore.setClasspathVariable(variable, location, null);
			} else {
				JavaCore.removeClasspathVariable(variable, null);
			}
		} catch (JavaModelException e1) {
			JavaCore.removeClasspathVariable(variable, null);
		}
	}
	
	private IPath getBundleLocation() {
		Bundle bundle = getBundle();
		if (bundle == null)
			return null;
		
		URL local= null;
		try {
			local= FileLocator.toFileURL(bundle.getEntry("/")); //$NON-NLS-1$
		} catch (IOException e) {
			return null;
		}
		String fullPath= new File(local.getPath()).getAbsolutePath();
		return Path.fromOSString(fullPath);
	}
	
	private Bundle getBundle(){
		Bundle[] bundles= SeleniumExporterPlugin.getDefault().getBundles("org.cubictest.exporter.selenium.server", null);
		if (bundles != null) {
			for (int i= 0; i < bundles.length; i++) {
				Bundle curr= bundles[i];
				String version= (String) curr.getHeaders().get(Constants.BUNDLE_VERSION);
				try {
					if (new VersionRange("[0.0,1.1)").isIncluded(Version.parseVersion(version))) {
						return curr;
					}
				} catch (IllegalArgumentException e) {
					// ignore
				}
			}
		}
		return null;
	}

}