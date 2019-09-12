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
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.roboticmaterials.smarthand.communicator.ScriptCommand;
import com.roboticmaterials.smarthand.communicator.ScriptExporter;
import com.roboticmaterials.smarthand.communicator.ScriptSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SmartHandInstallationNodeContribution implements InstallationNodeContribution {
	private static final String IPADDRESS_KEY = "ipaddress";
	private static final String DEFAULT_IP = "192.168.1.2";
	private static final String VALIDIP_KEY = "validip";
	private static final String OBJECTS_KEY = "objects";
	private static final String DEFAULT_OBJECT = "generic";
	private static final String CARTWAYPOINTS_KEY = "waypoints";
	private static final String DEFAULT_WAYPOINT = "home";
	
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
	
	private Timer timer;
	private boolean areThereChildren = false;
		
	public SmartHandInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, final SmartHandInstallationNodeView view) {
		this.keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		this.model = model;
		this.view = view;
		

				
		this.sender = new ScriptSender();
		this.exporter = new ScriptExporter();
		
		ActionListener taskPerformer = new ActionListener() {
	          public void actionPerformed(ActionEvent evt) {
	              //view.updateCameraFeed();
	              //System.out.println("Timer: Camera update");
	        	  status=testHandStatus();
	        	  view.setButtonText(status);
	      		if(!getStatus().contentEquals("offline")) {
	      			view.setButtonEnabled(true);
	      		}
	      		else {
	      			view.setButtonEnabled(false);
	      		}
	          }
	      };
	    timer = new Timer(1000,taskPerformer);
	}

	@Override
	public void openView() {
		timer.restart();
		view.setIPAddress(getIPAddress());
		view.setKnownObjects(getKnownObjects());
		view.setKnownWaypoints(getKnownWaypoints());
		view.setButtonEnabled(model.get(VALIDIP_KEY, false));
		view.setButtonText(status);
	}

	@Override
	public void closeView() {
		timer.stop();
	}
	
	public String testHandStatus()
	{
	    Socket s1 = null;
	    try
	    {
	    	System.out.printf("Pinging" + getIPAddress() +":8101...\n");
	        //s = new Socket(getIPAddress(),8101);
	        s1 = new Socket();
	        s1.connect(new InetSocketAddress(getIPAddress(), 8101), 30);
	      
	        // At this point, the IP address is correct and the hand
	        // responds via the XML-RPC server. We now test for RMLib
	        // being started:
	        
	        Socket s2 = null;
	        try
	        {
	        	System.out.printf("Pinging" + getIPAddress() +":8100...\n");
		        //s2 = new Socket(getIPAddress(),8001);
		        s2 = new Socket();
	        	s2.connect(new InetSocketAddress(getIPAddress(), 8100), 30);
		        // At this point RMLib has also started or an exception has been
	        	// thrown
		        status=SHS_ONLINE;
		        model.set(VALIDIP_KEY,true);
		        return SHS_ONLINE;
		        
	        }
	        catch (Exception e2)
	        {
	            // at least the hand replied at the IP address given
		        status=SHS_IDLE;
		        model.set(VALIDIP_KEY,true);
	        	System.out.printf("8100 (RMLIB) FAILED\n");
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
	
	public String scanIPAddress(String range) {
		String cand = "0.0.0.0";
		String[] address = range.split("\\.");
		for(int i=1;i<=255;i++) {
			cand=address[0]+"."+address[1]+"."+address[2]+"."+i;
	        Socket s = null;
	        try
	        {
	        	System.out.printf("Pinging" + cand +":8101...\n");
		        s = new Socket();
	        	s.connect(new InetSocketAddress(cand, 8101), 30);
		        // At this point the hand replied or an exception has been
	        	// thrown
		        return cand;
		        
	        }
	        catch (Exception e)
	        {
	            if(i==255) return "0.0.0.0";
	        }
	        finally
	        {
		        if(s != null)
		            try {s.close();}
		            catch(Exception e){}
	
	        }
		}
		return cand;
	}
	
	
	public String getStatus() {
		testHandStatus();
		return status;
	}

	
	public void importKnownObjects() {
		// Create a new ScriptCommand called "exportVariable"
		testHandStatus();
		timer.stop();
		if(getStatus().contentEquals(SHS_ONLINE)) {
		ScriptCommand exportTestCommand = new ScriptCommand("exportVariable");
		
		// Add the calculation script to the command
		exportTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");
		//exportTestCommand.appendLine("smarthand.init()");
		exportTestCommand.appendLine("objectIDs = smarthand.get_object_defs()");
		
		// Use the exporter to send the script
		// Note the String name of the variable (objectIDs) to be returned
		String returnValue = exporter.exportStringFromURScript(exportTestCommand,
				"objectIDs");
		
		// Put the result back in the View
		view.setKnownObjects(returnValue);
		setKnownObjects(returnValue);
		} else {
			// Place warning pop-up here
		}
		timer.restart();
	}
	
	public void requestKnownWaypoints() {
		testHandStatus();
		System.out.println("Tested hand status at request Known waypoints"); //delete
		timer.stop();
		if(getStatus().contentEquals(SHS_ONLINE)) {
			ScriptCommand sendTestCommand = new ScriptCommand("testSend");
			// Add the calculation script to the command
			sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");
			//exportTestCommand.appendLine("smarthand.init()");
			sendTestCommand.appendLine("smarthand.request_waypoints()");
			sender.sendScriptCommand(sendTestCommand);
			System.out.println("Succesfully sent request"); //delete
		}
		timer.restart();
	}
	
	public void importKnownWaypoints() {
		// Create a new ScriptCommand called "exportVariable"
		testHandStatus();
		System.out.println("Tested hand status at known waypoints"); //delete
		timer.stop();
		if(getStatus().contentEquals(SHS_ONLINE)) {
		ScriptCommand exportTestCommand = new ScriptCommand("exportVariable");
		
		// Add the calculation script to the command
		exportTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");
		
		
		//exportTestCommand.appendLine("smarthand.init()");
		exportTestCommand.appendLine("waypoints = smarthand.get_waypoints()");
		System.out.println("Appended Lines"); //delete
		// Use the exporter to send the script
		// Note the String name of the variable (objectIDs) to be returned
		String returnValue = exporter.exportStringFromURScript(exportTestCommand,
				"waypoints");
				System.out.printf("values"); //delete
		
		// Put the result back in the View
		view.setKnownWaypoints(returnValue);
		System.out.println("view set"); //delete
		setKnownWaypoints(returnValue);
		System.out.println("Values Returned"); //delete
		} else {
			// Place warning pop-up here
		}
		System.out.println("known waypoints succesfully imported"); //delete
		timer.restart();
	}

	public void sendScriptInitGripper() {
		testHandStatus();
		timer.stop();
		// Create a new ScriptCommand called "testSend"
		if(!getStatus().contentEquals(SHS_OFFLINE)) {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8101/RPC2\")");
		try {
			sendTestCommand.appendLine("smarthand.set_robot_ip(\""+view.getHost4Address() +"\")");
			System.out.print("Sending "+view.getHost4Address()+" as robot address to hand");
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendTestCommand.appendLine("smarthand.init()");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
		timer.restart();
		}	
	}
	
	public void sendScriptStopGripper() {
		testHandStatus();
		timer.stop();
		// Create a new ScriptCommand called "testSend"
		if(!getStatus().contentEquals(SHS_OFFLINE)) {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.stop()");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
		}	
		timer.restart();
	}
	
	public void sendScriptOpenGripper() {
		testHandStatus();
		timer.stop();
		// Create a new ScriptCommand called "testSend"
		if(!getStatus().contentEquals(SHS_OFFLINE)) {
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");
		sendTestCommand.appendLine("smarthand.set_gripper_torque(1.0)");
		sendTestCommand.appendLine("smarthand.open_gripper()");
		
		// Use the ScriptSender to send the command for immediate execution
		sender.sendScriptCommand(sendTestCommand);
		}
		timer.restart();
	}
	
	public void sendScriptCloseGripper() {
		testHandStatus();
		timer.stop();
		if(!getStatus().contentEquals(SHS_OFFLINE)) { // only if not offline
		ScriptCommand sendTestCommand = new ScriptCommand("testSend");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8101/RPC2\")");
		sendTestCommand.appendLine("smarthand.init()");
		sendTestCommand.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");
		sendTestCommand.appendLine("smarthand.set_gripper_torque(1.0)");
		sendTestCommand.appendLine("smarthand.close_gripper()");
		sender.sendScriptCommand(sendTestCommand);
		}
		timer.restart();
	}
	public boolean isDefined() {
		return !getIPAddress().isEmpty();
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		writer.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8101/RPC2\")");
		// Only add the init call when a smarthand command is used in the code
		if(areThereChildren) {
			writer.appendLine("return_value = smarthand.init()");
			writer.appendLine("smarthand = rpc_factory(\"xmlrpc\",\"http://" + model.get(IPADDRESS_KEY, DEFAULT_IP) +":8100/RPC2\")");

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
		return model.get(IPADDRESS_KEY, DEFAULT_IP);
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
	
	public String getKnownWaypoints() {
		return model.get(CARTWAYPOINTS_KEY, DEFAULT_WAYPOINT);
	}
	
	public void setKnownObjects(String message) {
		if ("".equals(message)) {
			resetToDefaultValue();
		} else {
			model.set(OBJECTS_KEY, message);
		}
	}
	
	public void setKnownWaypoints(String message) {
		if ("".equals(message)) {
			resetToDefaultValue();
		} else {
			model.set(CARTWAYPOINTS_KEY, message);
		}
	}

	private void resetToDefaultValue() {
		view.setIPAddress(DEFAULT_IP);
		model.set(IPADDRESS_KEY, DEFAULT_IP);
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
	
	public void setButtonEnabled(boolean b) {
		view.setButtonEnabled(b);
	}
}
