package com.roboticmaterials.smarthand.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.value.expression.InvalidExpressionException;
import com.ur.urcap.api.domain.variable.GlobalVariable;
import com.ur.urcap.api.domain.variable.VariableException;
import com.ur.urcap.api.domain.variable.VariableFactory;

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
	
	private static final String OBJECT_KEY = "object";
	
	
	private static String[] commands;
	private static final String DEFAULT_OUTPUT = "Open Gripper";
	private static final int DEFAULT_FORCE = 100;
	private static final int DEFAULT_APERTURE = 108;
	private static final String DEFAULT_OBJECT = "generic";
	
	private String IPADDRESS;
	//private Timer timer; 
	
	private final ScriptSender sender;
	private final ScriptExporter exporter;
	
	private GlobalVariable targetVariable;
	private GlobalVariable graspVariable;
	
	public SmartHandProgramNodeContribution(ProgramAPIProvider apiProvider, 
			final SmartHandProgramNodeView view, DataModel model) {
		commands = view.getCommands();
		this.apiProvider = apiProvider;
		this.programAPI = apiProvider.getProgramAPI();
		this.view = view;
		this.model = model;
		this.undoRedoManager = this.apiProvider.getProgramAPI().getUndoRedoManager();
		getInstallation().setChildren(true);
		this.IPADDRESS=getInstallation().getIPAddress();
		
		this.sender = new ScriptSender();
		this.exporter = new ScriptExporter();
		
		/*ActionListener taskPerformer = new ActionListener() {
	          public void actionPerformed(ActionEvent evt) {
	              view.updateCameraFeed();
	              System.out.println("Timer: Camera update");
	          }
	      };
	    timer = new Timer(500,taskPerformer);*/
		
		try {
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			targetVariable = variableFactory.createGlobalVariable("target",
					programAPI.getValueFactoryProvider().createExpressionBuilder().append("get_actual_tcp_pose()").build());
			
			// move this to time when command is selected: model.set("var_target", targetVariable);        
    
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}	
		
		try {
			VariableFactory variableFactory = programAPI.getVariableModel().getVariableFactory();
			graspVariable = variableFactory.createGlobalVariable("grasp",
					programAPI.getValueFactoryProvider().createExpressionBuilder().append("True").build());
			
			// Set this here as "open gripper" is the default command
			model.set("var_target",graspVariable);
			
			} catch (VariableException e) {
				e.printStackTrace();
			} catch (InvalidExpressionException e1) {
				e1.printStackTrace();
			}	
			 
			   
	}
	
	public String getIPAddress() {
	return IPADDRESS;
	}
	
	public void onObjectSelection(final String output) {
		undoRedoManager.recordChanges(new UndoableChanges() {

			@Override
			public void executeChanges() {
				model.set(OBJECT_KEY, output);
				
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
				if(output.matches("Get Object Pose")) {
				  model.set("var_target", targetVariable);
					if(model.isSet("var_grasp")) {
						model.remove("var_grasp");
					}
				} else {
					if(model.isSet("var_target")) {
						model.remove("var_target");
					}
				}
				if(output.matches("Open Gripper") || 
						output.matches("Close Gripper") ||
						output.matches("Set Gripper width")) {
					model.set("var_target",graspVariable);
					
				} else {
					if(model.isSet("var_grasp")) {
						model.remove("var_grasp");
					}
				}
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
	
	private String getObject() {
		return model.get(OBJECT_KEY, DEFAULT_OBJECT);
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
	
	@Override
	public void openView() {
		view.setCommandComboBoxItems(getOutputItems());
		view.setCommandComboBoxSelection(getCommand());
		
		view.setForceSliderO(getForceO());
		
		view.setForceSliderC(getForceC());
		
		view.setForceSliderW(getForceW());
		view.setApertureSlider(getAperture());
		
		
		view.setObjectsComboBoxItems(getInstallation().getKnownObjects());
		view.setObjectsComboBoxSelection(getObject());
		

		
		/*switch(getCommandId(getCommand())){
			case 2 : 
				//view.updateCameraFeed();
				//timer.start();
			break;
		}*/
		//System.out.println("Open view: " + getCommandId(getCommand()));
		view.setCard(getCommand());
	}

	private String[] getOutputItems() {
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
		//timer.stop(); 
		
	}

	@Override
	public String getTitle() {
		switch(getCommandId(getCommand())) {
		// Open
		case 0 : return graspVariable.getDisplayName()+"="+getCommand()+"(@"+getForceO()+"% force)";
		// Close
		case 1 : return graspVariable.getDisplayName()+"="+getCommand()+"(@"+getForceC()+"% force)";
		// Set Width
		case 2 : return graspVariable.getDisplayName()+"="+getCommand()+"("+getAperture()+"mm, @"+getForceW()+"% force)";
		// Get Pose
		case 3 : return  targetVariable.getDisplayName() + "=" + getCommand()+"("+getObject()+")";
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
		// TODO Auto-generated method stub
	/*	writer.appendLine("set_standard_digital_out("+getOutput()+", True)");
		writer.sleep(getDuration());
		writer.appendLine("set_standard_digital_out("+getOutput()+", False)");*/
		switch(getCommandId(getCommand())) {
		case 0 : writer.appendLine("smarthand.run_cmd(\"rm.set_gripper_torque("+
			 	 getForceO()/100.0+")\")"); 
				 writer.appendLine("smarthand.run_cmd(\"rm.open_gripper()\")");	 
				 break;
		case 1 : writer.appendLine("smarthand.run_cmd(\"rm.set_gripper_torque("+getForceC()/100.0+")\")");
				 writer.appendLine("smarthand.run_cmd(\"rm.close_gripper()\")");
				 break;
		case 2 : writer.appendLine("smarthand.run_cmd(\"rm.set_gripper_torque("+getForceW()/100.0+")\")");
				 writer.appendLine("smarthand.run_cmd(\"rm.set_gripper_width("+getAperture()/100.0+")\")");
				 break;
		case 3 : writer.assign(writer.getResolvedVariableName(targetVariable),"smarthand.get_object_pose(\""+getObject()+"\")");
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
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.open_gripper()\")");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
	}
	
	public void sendScriptCloseGripper() {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.close_gripper()\")");
		sender.sendScriptCommand(sendTestCommand);
	}
	
	public void requestPoseAndImage() {
		
		ScriptCommand exportTestCommand = new ScriptCommand("exportVariable");
		
		// Add the calculation script to the command
		exportTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		exportTestCommand.appendLine("smarthand.init()");
		exportTestCommand.appendLine(targetVariable.getDisplayName()+"=smarthand.get_object_pose(\""+getObject()+"\")");
		exportTestCommand.appendLine("smarthand.irimage()");
		//exportTestCommand.appendLine("z_value = pose[2]");
		
		// Use the exporter to send the script
		// Note the String name of the variable (z_value) to be returned
		String returnValue = exporter.exportStringFromURScript(exportTestCommand,
				targetVariable.getDisplayName());
		
		
		/*ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + getInstallation().getIPAddress() +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine(targetVariable.getDisplayName()+"=smarthand.get_object_pose(\""+getObject()+"\")");
		sendTestCommand.appendLine("smarthand.irimage()");
		sender.sendScriptCommand(sendTestCommand);*/

	}
	
}
