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
	
	//private GlobalVariable targetVariable;
	//private GlobalVariable successVariable;
	//private GlobalVariable widthVariable;
	
	public SmartHandProgramNodeContribution(ProgramAPIProvider apiProvider, 
			final SmartHandProgramNodeView view, DataModel model) {
		commands = view.getCommands();
		this.apiProvider = apiProvider;
		this.programAPI = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
		this.undoRedoManager = this.apiProvider.getProgramAPI().getUndoRedoManager();
		
		this.IPADDRESS=getInstallation().getIPAddress();
		
		this.sender = new ScriptSender();
		this.exporter = new ScriptExporter();
		
		ActionListener taskPerformer = new ActionListener() {
	          public void actionPerformed(ActionEvent evt) {
	           
	        	setStatus(getInstallation().testHandStatus());
	        	view.setTestButtonText(getStatus());
	      		if(!getStatus().contentEquals("offline")) {
	      			view.setButtonsEnabled(true);
	      		}
	      		else {
	      			view.setButtonsEnabled(false);
	      		}
	          }
	      };
	    //timer = new Timer(1000,taskPerformer);
		
		try {
			ExpressionBuilder expressionBuilder = programAPI.getValueFactoryProvider().createExpressionBuilder();
			Expression initialValue = expressionBuilder.append("get_actual_tcp_pose()").build();
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			GlobalVariable targetVariable = variableFactory.createGlobalVariable("t_1",initialValue);
			
			model.set(TARGETVAR_KEY, targetVariable);        
    
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}	
		
		try {
			ExpressionBuilder expressionBuilder = programAPI.getValueFactoryProvider().createExpressionBuilder();
			Expression initialValue = expressionBuilder.append("get_actual_tcp_pose()").build();
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			GlobalVariable targetVariable = variableFactory.createGlobalVariable("a_1",initialValue);
			
			model.set(APPROACHVAR_KEY, targetVariable);        
    
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}
		
		try {
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			GlobalVariable widthVariable = variableFactory.createGlobalVariable("w_1",
					programAPI.getValueFactoryProvider().createExpressionBuilder().append("0.108").build());
			
			
			model.set(WIDTHVAR_KEY, widthVariable);        
    
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}	
		
		try {
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			GlobalVariable successVariable = variableFactory.createGlobalVariable("s_1",
					programAPI.getValueFactoryProvider().createExpressionBuilder().append("True").build());
			
			
			model.set(SUCCESSVAR_KEY,successVariable);
			
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}	
	}
	
	public String getResolvedVarName(String key) {
		if(model.isSet(key)) {
			return model.get(key, (GlobalVariable) null).getDisplayName();
		} else 
			return "Not found " + key;
	}
	
	public String getIPAddress() {
	return IPADDRESS;
	}
	
	public void onCartWaypointsSelection(final String output) {
		undoRedoManager.recordChanges(new UndoableChanges() {

			@Override
			public void executeChanges() {
				model.set(CARTWAYPOINT_KEY, output);
				
			}
			
		});
	}
	
	public void onInfoObjectSelection(final String output) {
		undoRedoManager.recordChanges(new UndoableChanges() {

			@Override
			public void executeChanges() {
				model.set(INFOOBJECT_KEY, output);
				
			}
			
		});
	}
	
	public void onCommandSelection(final String output) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(OUTPUT_KEY, output);
				
				// Add the target and grasp variables only for the "get object pose" 
				// and open/close gripper commands.
				// Remove it, if it has been added previously when the user changed
				// the command type. 
				/*if(output.matches("Get Object Pose") || output.matches("Get Object Info")) {
				  model.set("var_target", targetVariable);
				  model.set("var_success", successVariable);
					if(output.matches("Get Object Info")) {
						model.set("var_width",widthVariable);
					} else {
						model.remove("var_width");
					}
				} else {
					if(model.isSet("var_target")) {
						model.remove("var_target");
					}
					if(model.isSet("var_width")) {
						model.remove("var_width");
					}
				}
				if(output.matches("Open Gripper") || 
						output.matches("Close Gripper") ||
						output.matches("Set Gripper Width")) {
					model.set("var_success",successVariable);
					
				} */
			}
		});
	}
	
	public void onForceOSelection(final Integer force) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(FORCEO_KEY, force);
				
			}
		});
		
	}
	
	public void onForceCSelection(final Integer force) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(FORCEC_KEY, force);
				
			}
		});
		
	}

	public void onForceWSelection(final Integer force) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(FORCEW_KEY, force);
				
			}
		});
		
	}

	
	public void onApertureSelection(final Integer aperture) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(APERTURE_KEY, aperture);
				
			}
		});
		
	}
	
	/*public void onApertureVarSelection(final String var) {
		undoRedoManager.recordChanges(new UndoableChanges() {
			
			@Override
			public void executeChanges() {
				model.set(APERTUREVAR_KEY, var);
				
			}
		});
	}*/
	
	private Boolean getPlaneFilter() {
		return model.get(PLANEFILTERVAR_KEY, DEFAULT_PLANEFILTER);
	}
	private String getCartWaypoint() {
		return model.get(CARTWAYPOINT_KEY, DEFAULT_WAYPOINT);
	}
	
	private String getInfoObject() {
		return model.get(INFOOBJECT_KEY, DEFAULT_OBJECT);
	}
	
	private String getCommand() {
		return model.get(OUTPUT_KEY, DEFAULT_OUTPUT);
	}
	
	private int getForceO() {
		return model.get(FORCEO_KEY, DEFAULT_FORCE);
	}
	
	private int getForceC() {
		return model.get(FORCEC_KEY, DEFAULT_FORCE);
	}
	
	private int getForceW() {
		return model.get(FORCEW_KEY, DEFAULT_FORCE);
	}
	
	private int getAperture() {
		return model.get(APERTURE_KEY, DEFAULT_APERTURE);
	}
	
	private String getApertureVarStr() {
		if(model.isSet(APERTUREVAR_KEY))
			return model.get(APERTUREVAR_KEY, (Variable) null).getDisplayName();
		else
			return DEFAULT_APERTUREVAR;
	}
	
	@Override
	public void openView() {
		setStatus(getInstallation().testHandStatus());
		view.setTestButtonText(getStatus());
		if(!getStatus().contentEquals("offline"))
			view.setButtonsEnabled(true);
		else
			view.setButtonsEnabled(false);
		
		view.setCommandComboBoxItems(getCommandItems());
		view.setCommandComboBoxSelection(getCommand());
		
		view.setForceSliderO(getForceO());
		
		view.setForceSliderC(getForceC());
		
		view.setForceSliderW(getForceW());
		view.setApertureSlider(getAperture());
		//view.setApertureVarComboBoxItems(getApertureVarItems());
		//view.setApertureVarComboBoxSelection(getApertureVar());
		view.updateApertureVarComboBox(this);
		
		view.setCartWaypointsComboBoxItems(getInstallation().getKnownWaypoints());
		view.setCartWaypointsComboBoxSelection(getCartWaypoint());
		
		view.setObjectsInfoComboBoxItems(getInstallation().getKnownObjects());
		view.setObjectsInfoComboBoxSelection(getInfoObject());
		view.setPlaneFilterSelection(getPlaneFilter());
		
		getInstallation().setChildren(true);
		

		//System.out.println("Open view: " + getCommandId(getCommand()));
		view.setCard(getCommand());
		//timer.start();
	}

	public Variable getSelectedApertureVar() {
		return model.get(APERTUREVAR_KEY, (Variable) null);
	}
	
	public Collection<Variable> getGlobalVariables() {
		return programAPI.getVariableModel().get(new Filter<Variable>() {
			@Override
			public boolean accept(Variable element) {
				return element.getType().equals(Variable.Type.GLOBAL) || element.getType().equals(Variable.Type.VALUE_PERSISTED);
			}
		});
	}
	
	public void setApertureVar(final Variable variable) {
		programAPI.getUndoRedoManager().recordChanges(new UndoableChanges() {
			@Override
			public void executeChanges() {
				model.set(APERTUREVAR_KEY, variable);
			}
		});
	}

	public void removeApertureVar() {
		programAPI.getUndoRedoManager().recordChanges(new UndoableChanges() {
			@Override
			public void executeChanges() {
				model.remove(APERTUREVAR_KEY);
			}
		});
	}
	
	private String[] getCommandItems() {
		/*Integer[] items = new Integer[8];
		for(int i=0; i<8; i++) {
			items[i] = i;
		}*/
		String[] items = commands;
		return items;
	}
	
	@Override
	public void closeView() {
		// Stop image reload, only needed when image is shown
	//	timer.stop(); 
		
	}

	@Override
	public String getTitle() {
		switch(getCommandId(getCommand())) {
		// Open
		case 0 : return getResolvedVarName(SUCCESSVAR_KEY)+"="+getCommand()+"(@"+getForceO()+"% force)";
		// Close
		case 1 : return getResolvedVarName(SUCCESSVAR_KEY)+"="+getCommand()+"(@"+getForceC()+"% force)";
		// Set Width
		case 2 : // Differentiate between variable and slider to provide width
			if(getApertureVarStr().contentEquals("<none>")) {
				return getResolvedVarName(SUCCESSVAR_KEY)+"="+getCommand()+"("+getAperture()+"mm, @"+getForceW()+"% force)";
			} else {
				return getResolvedVarName(SUCCESSVAR_KEY)+"="+getCommand()+"("+getApertureVarStr() +", @"+getForceW()+"% force)"; 
			}
		// Get Pose
		//case 3 : return   getResolvedVarName(TARGETVAR_KEY) + "=" + getCommand()+"("+getPoseObject()+")";
		// Get Object pose
		case 3 : return  "(" + getResolvedVarName(SUCCESSVAR_KEY) + "," + getResolvedVarName(APPROACHVAR_KEY) + "," + getResolvedVarName(TARGETVAR_KEY) +"," + getResolvedVarName(WIDTHVAR_KEY) + ")=" + getCommand()+"("+getInfoObject()+","+getPlaneFilter()+")";
		case 4 : return getResolvedVarName(SUCCESSVAR_KEY)+"="+getCommand()+"("+getCartWaypoint()+")";
		default: return "";
		}
	}

	@Override
	public boolean isDefined() {
		return true;
	}
	
	private int getCommandId(String string) {
		for(int i=0;i  < commands.length;i++) {
			if(commands[i].equals(string)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void generateScript(ScriptWriter writer) {
	/*	writer.appendLine("set_standard_digital_out("+getOutput()+", True)");
		writer.sleep(getDuration());
		writer.appendLine("set_standard_digital_out("+getOutput()+", False)");*/
		switch(getCommandId(getCommand())) {
		case 0 : writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_torque("+
			 	 getForceO()/100.0+")"); 
				 writer.assign(getResolvedVarName(SUCCESSVAR_KEY), "smarthand.open_gripper()");
				 break;
		case 1 : writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_torque("+getForceC()/100.0+")");
				 writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.close_gripper()");
				 break;
		case 2 : if(getApertureVarStr().contentEquals("<none>")) {
					 writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_torque("+getForceW()/100.0+")");
					 writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_width("+getAperture()/1000.0+")");
				 } else {
					 writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_torque("+getForceW()/100.0+")");
					 writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.set_gripper_width("+getApertureVarStr()+")");					 
				 }
				 
				 break;
		//case 3 : writer.assign(getResolvedVarName(TARGETVAR_KEY),"smarthand.get_object_pose(\""+getPoseObject()+"\")");
		//		 break;
		case 3 : if(getPlaneFilter()) {
				 	writer.assign("_t","smarthand.get_object_info(\""+getInfoObject()+"\",True)");
					}
				else {
					writer.assign("_t","smarthand.get_object_info(\""+getInfoObject()+"\",False)");				
				}
			     writer.appendLine(getResolvedVarName(TARGETVAR_KEY)+"=p[_t[0],_t[1],_t[2],_t[3],_t[4],_t[5]]");
			     writer.appendLine(getResolvedVarName(APPROACHVAR_KEY)+"=p[_t[6],_t[7],_t[8],_t[9],_t[10],_t[11]]");
			     writer.assign(getResolvedVarName(WIDTHVAR_KEY),"_t[12]");
			     //writer.assign(writer.getResolvedVariableName(successVariable),"_t[7]");
				 writer.appendLine("if _t[13] == 0 :");
				 writer.assign(getResolvedVarName(SUCCESSVAR_KEY), "False");
				 writer.appendLine("else:");
				 writer.assign(getResolvedVarName(SUCCESSVAR_KEY), "True");
			     writer.appendLine("end");
				 break;
		case 4 : writer.assign(getResolvedVarName(SUCCESSVAR_KEY),"smarthand.movecart(\""+getCartWaypoint()+"\")");
				 break;
		}
		
	}
	
	
	public SmartHandInstallationNodeContribution getInstallation() {
		return programAPI.getInstallationNode(SmartHandInstallationNodeContribution.class);
	}
	
	
	
	public void sendScriptOpenGripper() {
		// Create a new ScriptCommand called "testSend"
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		// Append a popup command to the ScriptCommand
		//sendTestCommand.appendLine("popup(\"This is a popup\")");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8100/RPC2\")");
		sendTestCommand.appendLine("smarthand.open_gripper()");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
	}
	
	public void sendScriptCloseGripper() {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8100/RPC2\")");
		sendTestCommand.appendLine("smarthand.close_gripper()");
		sender.sendScriptCommand(sendTestCommand);
	}
	
/*	public void requestPoseAndImage() {
		if(!getInstallation().getStatus().contentEquals("offline")) {
		
		ScriptCommand exportTestCommand = new ScriptCommand("exportVariable");
		
		// Add the calculation script to the command
		exportTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8100/RPC2\")");
		exportTestCommand.appendLine("smarthand.init()");
		exportTestCommand.appendLine(getResolvedVarName(TARGETVAR_KEY)+"=smarthand.get_object_pose(\""+getPoseObject()+"\")");
		exportTestCommand.appendLine("smarthand.irimage()");
		//exportTestCommand.appendLine("z_value = pose[2]");
		
		// Use the exporter to send the script
		// Note the String name of the variable (z_value) to be returned
		String returnValue = exporter.exportStringFromURScript(exportTestCommand,
				getResolvedVarName(TARGETVAR_KEY));
		
		
		/*ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine(targetVariable.getDisplayName()+"=smarthand.get_object_pose(\""+getObject()+"\")");
		sendTestCommand.appendLine("smarthand.irimage()");
		sender.sendScriptCommand(sendTestCommand);*/
	/*	}
		
	}*/
	
	private void insertGetInfoNode() {
		try {
			ProgramNodeFactory programNodeFactory = programAPI.getProgramModel().getProgramNodeFactory();
			//WaitNode waitNode = programNodeFactory.createWaitNode();
			TreeNode treeNode = programAPI.getProgramModel().getRootTreeNode(this);

			//Time oneSecondWait = programAPI.getValueFactoryProvider().getSimpleValueFactory().createTime(1, Time.Unit.S);
			//TimeWaitNodeConfig config = waitNode.getConfigFactory().createTimeConfig(oneSecondWait, ErrorHandler.AUTO_CORRECT);

			//programAPI.getProgramModel().getRootTreeNode(this).addChild(waitNode.setConfig(config));
			treeNode.addChild(programNodeFactory.createURCapProgramNode(SmartHandProgramNodeService.class));

		} catch (TreeStructureException e) {
			e.printStackTrace();
		}
	}

	public void setStatus(String testHandStatus) {
		STATUS=testHandStatus;
	}
	
	public String getStatus() {
		return STATUS;
	}

	public void onPlaneFilterSelection(final Boolean item) {
		undoRedoManager.recordChanges(new UndoableChanges() {

			@Override
			public void executeChanges() {
				model.set(PLANEFILTERVAR_KEY, item);
				System.out.println(item);
				
			}
			
		});
	}
}