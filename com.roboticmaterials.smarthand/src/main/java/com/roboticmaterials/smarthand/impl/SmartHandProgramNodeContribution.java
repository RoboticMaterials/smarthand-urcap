package com.roboticmaterials.smarthand.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;

import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeContribution;

public class SmartHandProgramNodeContribution implements ProgramNodeContribution {

    public SmartHandProgramNodeContribution(ProgramAPIProvider apiProvider, 
            final SmartHandProgramNodeView view, DataModel model) {
        // commands = view.getCommands();
        // this.apiProvider = apiProvider;
        // this.programAPI = apiProvider.getProgramAPI();
        // this.view = view;
        // this.model = model;
        // this.undoRedoManager = this.apiProvider.getProgramAPI().getUndoRedoManager();

    }

    @Override
    public void openView(){

    }

    @Override
    public void closeView(){

    }

    @Override
    public String getTitle(){
        return null;
    }

    @Override
    public boolean isDefined() {
        return false;
    }

    @Override
    public void genereateScript(ScriptWriter writer) {
        
    }

}
