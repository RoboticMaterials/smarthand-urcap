package com.roboticmaterials.smarthand.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

public class SmartHandInstallationNodeService implements SwingInstallationNodeService<SmartHandInstallationNodeContribution, SmartHandInstallationNodeView>{

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTitle(Locale locale) {
		// TODO Auto-generated method stub
		return "SmartHand";
	}

	@Override
	public SmartHandInstallationNodeView createView(ViewAPIProvider apiProvider) {
		SystemAPI systemAPI = apiProvider.getSystemAPI();
		Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
		return new SmartHandInstallationNodeView(style);
	}

	@Override
	public SmartHandInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
			SmartHandInstallationNodeView view, DataModel model, CreationContext context) {
		return new SmartHandInstallationNodeContribution(apiProvider, model, view);
	}
	
}



