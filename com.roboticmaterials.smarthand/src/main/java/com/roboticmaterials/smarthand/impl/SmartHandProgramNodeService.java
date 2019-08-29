package com.roboticmaterials.smarthand.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class SmartHandProgramNodeService implements SwingProgramNodeService<SmartHandProgramNodeContribution, SmartHandProgramNodeView>{

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "SmartHandNode";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setChildrenAllowed(false);
		
	}

	@Override
	public String getTitle(Locale locale) {
		return "SmartHand Basic Commands";
	}

	@Override
	public SmartHandProgramNodeView createView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated method stub
		return new SmartHandProgramNodeView(apiProvider);
	}

	@Override
	public SmartHandProgramNodeContribution createNode(ProgramAPIProvider apiProvider, SmartHandProgramNodeView view,
			DataModel model, CreationContext context) {
		return new SmartHandProgramNodeContribution(apiProvider, view, model);
	}

}
