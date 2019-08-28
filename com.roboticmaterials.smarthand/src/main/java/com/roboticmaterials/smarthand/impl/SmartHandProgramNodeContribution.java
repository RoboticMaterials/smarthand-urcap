package com.roboticmaterials.smarthand.impl;
//Kalervo Test
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.program.nodes.ProgramNodeFactory;
import com.ur.urcap.api.domain.program.nodes.builtin.InvalidDomainException;
import com.ur.urcap.api.domain.program.nodes.builtin.WaitNode;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waitnode.TimeWaitNodeConfig;
import com.ur.urcap.api.domain.program.structure.TreeNode;
import com.ur.urcap.api.domain.program.structure.TreeStructureException;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.util.Filter;
import com.ur.urcap.api.domain.validation.ErrorHandler;
import com.ur.urcap.api.domain.value.expression.Expression;
import com.ur.urcap.api.domain.value.expression.ExpressionBuilder;
import com.ur.urcap.api.domain.value.expression.InvalidExpressionException;
import com.ur.urcap.api.domain.value.simple.Time;
import com.ur.urcap.api.domain.variable.GlobalVariable;
import com.ur.urcap.api.domain.variable.Variable;
import com.ur.urcap.api.domain.variable.VariableException;
import com.ur.urcap.api.domain.variable.VariableFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import javax.swing.Timer;

import com.roboticmaterials.smarthand.communicator.ScriptCommand;
import com.roboticmaterials.smarthand.communicator.ScriptExporter;
import com.roboticmaterials.smarthand.communicator.ScriptSender;
import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeContribution;

public class SmartHandProgramNodeContribution implements ProgramNodeContribution {

    public SmartHandProgramNodeContribution(ProgramAPIProvider apiProvider, 
            final SmartHandProgramNodeView view, DataModel model) {
        commands = view.getCommands();
        this.apiProvider = apiProvider;
        this.programAPI = apiProvider.getProgramAPI();
        this.view = view;
        this.model = model;
        this.undoRedoManager = this.apiProvider.getProgramAPI().getUndoRedoManager();

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

    public void genereateScript(ScriptWriter writer) {
        
    }

}
