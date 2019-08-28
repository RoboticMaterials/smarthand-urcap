package com.roboticmaterials.smarthand.impl;

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

    private static final String FORCEOC_KEY = "forceOC";
	private final ProgramAPI programAPI;
	private final ProgramAPIProvider apiProvider;
	private final SmartHandProgramNodeView view;
	private final DataModel model;
	private final UndoRedoManager undoRedoManager;
	
	private static final String OUTPUT_KEY = "command";
	private static final String FORCEO_KEY = "forceO";
	private static final String FORCEC_KEY = "forceC";
	private static final String FORCEW_KEY = "forceW";
	private static final String APERTURE_KEY = "aperture";
	private static final String APERTUREVAR_KEY = "aperturevar";
	//private static final String POSEOBJECT_KEY = "object";
	private static final String INFOOBJECT_KEY = "object";
	private static final String CARTWAYPOINT_KEY = "waypoint";
	
	private static final String SUCCESSVAR_KEY = "var_success";
	private static final String TARGETVAR_KEY = "var_target";
	private static final String APPROACHVAR_KEY = "var_approach";
	private static final String WIDTHVAR_KEY = "var_width";
	
	private static String[] commands;
	private static final String DEFAULT_OUTPUT = "Open Gripper";
	private static final int DEFAULT_FORCE = 100;
	private static final int DEFAULT_APERTURE = 108;
	private static final String DEFAULT_APERTUREVAR = "<none>";
	private static final String DEFAULT_OBJECT = "generic";
	private static final String DEFAULT_WAYPOINT = "home";
	
	private static final String PLANEFILTERVAR_KEY = "var_planefilter";
	private static final Boolean DEFAULT_PLANEFILTER = true;
	
	private String IPADDRESS;
	private String STATUS = "offline";
	//private Timer timer; 
	
	private final ScriptSender sender;
	private final ScriptExporter exporter;
    


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

    @Override
    public void genereateScript(ScriptWriter writer) {
        
    }

}
