package com.roboticmaterials.smarthand.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeView;

import com.roboticmaterials.smarthand.communicator.ScriptCommand;
import com.roboticmaterials.smarthand.communicator.ScriptExporter;
import com.roboticmaterials.smarthand.communicator.ScriptSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SmartHandInstallationNodeContribution implements InstallationNodeContribution {
	private static final String IPADDRESS_KEY = "ipaddress";
	private static final String DEFAULT_VALUE = "192.168.1.2";
	private static final String OBJECTS_KEY = "objects";
	private static final String DEFAULT_OBJECT = "generic";
	
	private final SmartHandInstallationNodeView view;
	private final KeyboardInputFactory keyboardFactory;

	private DataModel model;
	
	private final ScriptSender sender;
	private final ScriptExporter exporter;

	public SmartHandInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, SmartHandInstallationNodeView view) {
		this.keyboardFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		this.model = model;
		this.view = view;
		
		this.sender = new ScriptSender();
		this.exporter = new ScriptExporter();
	}

	@Override
	public void openView() {
		view.setIPAddress(getIPAddress());
		view.setKnownObjects(getKnownObjects());
	}

	@Override
	public void closeView() {

	}
	
	public void importKnownObjects() {
		// Create a new ScriptCommand called "exportVariable"
		ScriptCommand exportTestCommand = new ScriptCommand("exportVariable");
		
		// Add the calculation script to the command
		exportTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		exportTestCommand.appendLine("objectIDs = smarthand.get_object_defs()");
		//exportTestCommand.appendLine("z_value = pose[2]");
		
		// Use the exporter to send the script
		// Note the String name of the variable (z_value) to be returned
		String returnValue = exporter.exportStringFromURScript(exportTestCommand,
				"objectIDs");
		
		// Put the result back in the View
		view.setKnownObjects(returnValue);
		setKnownObjects(returnValue);
	}

	public void sendScriptOpenGripper() {
		// Create a new ScriptCommand called "testSend"
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		// Append a popup command to the ScriptCommand
		//sendTestCommand.appendLine("popup(\"This is a popup\")");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.open_gripper()\")");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
	}
	
	public void sendScriptCloseGripper() {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.close_gripper()\")");
		sender.sendScriptCommand(sendTestCommand);
	}
	public boolean isDefined() {
		return !getIPAddress().isEmpty();
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		// Store the popup title in a global variable so it is globally available to all Hello World Swing program nodes.
		writer.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		if(areThereChildren) {
			writer.appendLine("return_value = smarthand.init()");
		}
	}

	public String getIPAddress() {
		return model.get(IPADDRESS_KEY, DEFAULT_VALUE);
	}

	public void setIPAddress(String message) {
		if ("".equals(message)) {
			resetToDefaultValue();
		} else {
			model.set(IPADDRESS_KEY, message);
		}
	}
	
	public String getKnownObjects() {
		return model.get(OBJECTS_KEY, DEFAULT_OBJECT);
	}
	
	public void setKnownObjects(String message) {
		if ("".equals(message)) {
			resetToDefaultValue();
		} else {
			model.set(OBJECTS_KEY, message);
		}
	}

	private void resetToDefaultValue() {
		view.setIPAddress(DEFAULT_VALUE);
		model.set(IPADDRESS_KEY, DEFAULT_VALUE);
	}

	public KeyboardTextInput getInputForTextField() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue(getIPAddress());
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForTextField() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setIPAddress(value);
				view.setIPAddress(value);
			}
		};
	}
	
	private boolean areThereChildren = false;
	
	public void setChildren(boolean b) {
		areThereChildren=b;
	}
}
