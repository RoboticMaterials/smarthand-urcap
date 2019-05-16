package com.roboticmaterials.smarthand.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.toolbar.swing.SwingToolbarService;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeView;

import java.net.InetSocketAddress;
import java.net.Socket;

import com.roboticmaterials.smarthand.communicator.ScriptCommand;
import com.roboticmaterials.smarthand.communicator.ScriptExporter;
import com.roboticmaterials.smarthand.communicator.ScriptSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SmartHandInstallationNodeContribution implements InstallationNodeContribution {
	private static final String IPADDRESS_KEY = "ipaddress";
	private static final String DEFAULT_VALUE = "192.168.1.2";
	private static final String VALIDIP_KEY = "validip";
	private static final String OBJECTS_KEY = "objects";
	private static final String DEFAULT_OBJECT = "generic";
	
	// Variables to manage the status of the hand. 'status' is shown at 'statusLabel'.
	final static String SHS_OFFLINE = "offline";
	final static String SHS_IDLE = "idle";
	final static String SHS_ONLINE = "online";		
	private String status=SHS_OFFLINE;
	
	private final SmartHandInstallationNodeView view;
	private final KeyboardInputFactory keyboardInputFactory;

	private DataModel model;
	
	private final ScriptSender sender;
	private final ScriptExporter exporter;
	
	private boolean areThereChildren = false;
		
	public SmartHandInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, SmartHandInstallationNodeView view) {
		this.keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		this.model = model;
		this.view = view;
				
		this.sender = new ScriptSender();
		this.exporter = new ScriptExporter();
	}

	@Override
	public void openView() {
		view.setIPAddress(getIPAddress());
		view.setKnownObjects(getKnownObjects());
		view.setButtonEnabled(model.get(VALIDIP_KEY, false));
		view.setStatusLabel(getStatus());
	}

	@Override
	public void closeView() {

	}
	
	public String testHandStatus()
	{
	    Socket s1 = null;
	    try
	    {
	    	System.out.printf("Pinging" + getIPAddress() +":8101...\n");
	        //s = new Socket(getIPAddress(),8101);
	        s1 = new Socket();
	        s1.connect(new InetSocketAddress(getIPAddress(), 8101), 20);
	      
	        // At this point, the IP address is correct and the hand
	        // responds via the XML-RPC server. We now test for RMLib
	        // being started:
	        
	        Socket s2 = null;
	        try
	        {
	        	System.out.printf("Pinging" + getIPAddress() +":8001...\n");
		        //s2 = new Socket(getIPAddress(),8001);
		        s2 = new Socket();
	        	s2.connect(new InetSocketAddress(getIPAddress(), 8001), 20);
		        // At this point RMLib has also started or an exception has been
	        	// thrown
		        status=SHS_ONLINE;
		        view.setStatusLabel(SHS_ONLINE);
		        model.set(VALIDIP_KEY,true);
		        return SHS_ONLINE;
		        
	        }
	        catch (Exception e2)
	        {
	            // at least the hand replied at the IP address given
		        status=SHS_IDLE;
		        view.setStatusLabel(status);
		        model.set(VALIDIP_KEY,true);
	        	System.out.printf("8001 (RMLIB) FAILED\n");
	        	return SHS_IDLE;
	        }
	        finally
	        {
		        if(s2 != null)
		            try {s2.close();}
		            catch(Exception e2){}
	
	        }
	    }
	    catch (Exception e1)
	    {
	    	System.out.printf("8101 (XML-RPC) FAILED\n");
	    	status=SHS_OFFLINE;
	    	view.setStatusLabel(status);
	    	model.set(VALIDIP_KEY,false);
	        return SHS_OFFLINE;
	    }
	    finally
	    {
	        if(s1 != null)
	            try {s1.close();}
	            catch(Exception e1){}
	    }
	}
	
	
	public String getStatus() {
		testHandStatus();
		return status;
	}
	
	public void importKnownObjects() {
		// Create a new ScriptCommand called "exportVariable"
		testHandStatus();
		if(!getStatus().contentEquals(SHS_OFFLINE)) {
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
	}

	public void sendScriptOpenGripper() {
		testHandStatus();
		// Create a new ScriptCommand called "testSend"
		if(!getStatus().contentEquals(SHS_OFFLINE)) {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		// Append a popup command to the ScriptCommand
		//sendTestCommand.appendLine("popup(\"This is a popup\")");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.open_gripper()\")");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
		}
	}
	
	public void sendScriptCloseGripper() {
		testHandStatus();
		if(!getStatus().contentEquals(SHS_OFFLINE)) { // only if not offline
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand.run_cmd(\"rm.close_gripper()\")");
		sender.sendScriptCommand(sendTestCommand);
		}
	}
	public boolean isDefined() {
		return !getIPAddress().isEmpty();
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		writer.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_VALUE) +":8101/RPC2\")");
		// Only add the init call when a smarthand command is used in the code
		if(areThereChildren) {
			writer.appendLine("return_value = smarthand.init()");
		}
	}

	public KeyboardTextInput getKeyboardForIpAddress() {
		KeyboardTextInput keyboard = keyboardInputFactory.createIPAddressKeyboardInput();
		keyboard.setInitialValue(model.get(IPADDRESS_KEY, ""));
		return keyboard;
	}

	public KeyboardInputCallback<String> getCallbackForIpAddress() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				model.set(IPADDRESS_KEY, value);
				model.set(VALIDIP_KEY,true);
				view.setIPAddress(value);
				view.setButtonEnabled(true);
			}
			
			@Override
			public void onCancel() {
				model.set(VALIDIP_KEY,false);
				view.setButtonEnabled(false);
			}
		};
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


/*	public KeyboardInputCallback<String> getCallbackForTextField() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setIPAddress(value);
				view.setIPAddress(value);
				view.setButtonEnabled(true);
			}
			
			@Override
			public void onCancel() {
				view.setButtonEnabled(false);
			}
		};
	}*/
	
	
	public void setChildren(boolean b) {
		areThereChildren=b;
	}
}
