/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.resources.interfaces.IResourceListener;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;


public class ResourceMonitor implements IResourceMonitor, IResourceChangeListener {

	private Map<IResource, List<IResourceListener>> listeners = new HashMap<IResource, List<IResourceListener>>();

	public ResourceMonitor(IProject project) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_BUILD);
	}
	
	private List<IResourceListener> getResourceListeners(IResource resource) {
		if(listeners.get(resource) == null) {
			return new ArrayList<IResourceListener>();
		} 
		return listeners.get(resource);
	}
	
	public void registerResourceListener(IResource resource, IResourceListener listener) {
		List<IResourceListener> list = getResourceListeners(resource);
		list.add(listener);
		listeners.put(resource, list);
	}

	public void unregisterResourceListener(IResourceListener listener) {
		for(IResource resource : listeners.keySet()) {
			listeners.get(resource).remove(listener);
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		for(IResource resource : listeners.keySet()) {
			if(event.getDelta().findMember(resource.getFullPath()) != null) {
				for(IResourceListener listener : listeners.get(resource)) {
					listener.notifyResourceChange(resource);
				}
			}
		}
		
	}

	public void dispose() {
		listeners.clear();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(this);
	}
}
