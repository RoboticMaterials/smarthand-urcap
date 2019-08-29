package com.roboticmaterials.smarthand.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarService;
import com.roboticmaterials.smarthand.impl.ToolbarService;
import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("SmartHand registering!");
		
		bundleContext.registerService(SwingInstallationNodeService.class, new SmartHandInstallationNodeService(), null);
		bundleContext.registerService(SwingProgramNodeService.class, new SmartHandProgramNodeService(), null);
	//	bundleContext.registerService(SwingToolbarService.class, new ToolbarService(), null);	
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		
	}
}

