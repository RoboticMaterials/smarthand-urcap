package com.roboticmaterials.smarthand.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.roboticmaterials.smarthand.impl.SmartHandInstallationNodeContribution;
import com.roboticmaterials.smarthand.impl.Style;

public class SmartHandInstallationNodeView implements SwingInstallationNodeView<SmartHandInstallationNodeContribution>{

	
	private final Style style;
	private JTextField ipAddress = new JTextField();
	
	private JButton openGripperButton = new JButton("Open");
	private JButton closeGripperButton = new JButton("Close");;
	private final JButton scanNetworkButton = new JButton("Scan");
	private final JButton initGripperButton = new JButton("Connect");
	//private final JButton stopGripperButton = new JButton("Stop");


    @Override
    public void buildUI(JPanel jPanel, final SmartHandInstallationNodeContribution contribution) {
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        ipAddress.setHorizontalAlignment(JTextField.RIGHT);
        scanNetworkButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
				try {
					String robotAddress = getHost4Address();
					if(robotAddress != null) {
						String handAddress = contribution.scanIPAddress(robotAddress);//InetAddress.getLocalHost().getHostAddress());
						setIPAddress(handAddress);
						contribution.setIPAddress(handAddress);	
					} else {
						JOptionPane.showMessageDialog(null, "Cannot determine the robot's IP address. Is the network cable connected?", "No IP address", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
        });
        
        jPanel.add(createLabelInputField("IP Address: ", ipAddress, scanNetworkButton, new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setButtonEnabled(false);
                KeyboardTextInput keyboardInput = contribution.getKeyboardForIpAddress();
                keyboardInput.show(ipAddress, contribution.getCallbackForIpAddress());
            }
        }));

        jPanel.add(createVerticalSpacing());
		jPanel.add(createInfo("Open and close gripper:"));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderOpenGripperButton(contribution));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderCloseGripperButton(contribution));
    }


	private Box createInfo(String text) {
		Box infoBox = Box.createVerticalBox();
		infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		JTextPane pane = new JTextPane();
		pane.setBorder(BorderFactory.createEmptyBorder());
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attributeSet, 0.5f);
		StyleConstants.setLeftIndent(attributeSet, 0f);
		pane.setParagraphAttributes(attributeSet, false);
		pane.setText(text);
		pane.setEditable(false);
		pane.setMaximumSize(pane.getPreferredSize());
		pane.setBackground(infoBox.getBackground());
		infoBox.add(pane);
		return infoBox;
	}

    private Component createVerticalSpacing() {
		return Box.createRigidArea(new Dimension(0, style.getVerticalSpacing()));
    }

    private Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(style.getHorizontalSpacing(), 0));
	}

    
	private Box createLabelInputField(String label, final JTextField inputField, final JButton testNetworkButton, MouseAdapter mouseAdapter) {
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel jLabel = new JLabel(label);
		inputField.setFocusable(false);
		inputField.setPreferredSize(style.getInputfieldSize());
		inputField.setMaximumSize(inputField.getPreferredSize());
		inputField.addMouseListener(mouseAdapter);
		
		horizontalBox.add(jLabel);
		horizontalBox.add(inputField);
		horizontalBox.add(Box.createRigidArea(new Dimension(5,0)));
		horizontalBox.add(testNetworkButton);
		
		return horizontalBox;
	}
	

	/**
	 * Returns this host's non-loopback IPv4 addresses.
	 * 
	 * @return
	 * @throws SocketException 
	 */
	private static List<Inet4Address> getInet4Addresses() throws SocketException{
	    List<Inet4Address> ret = new ArrayList<Inet4Address>();

	    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	    for (NetworkInterface netint : Collections.list(nets)) {
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	            if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
	                ret.add((Inet4Address)inetAddress);
	            }
	        }
	    }

	    return ret;
    }
    
    	/**
	 * Returns this host's first non-loopback IPv4 address string in textual
	 * representation.
	 * 
	 * @return
	 * @throws SocketException
	 */
	public static String getHost4Address() throws SocketException {
	    List<Inet4Address> inet4 = getInet4Addresses();
	    return !inet4.isEmpty()
	            ? inet4.get(0).getHostAddress()
	            : null;
	}
	
    public SmartHandInstallationNodeView(Style style) {
        this.style = style;
    }

    public void setIPAddress(String t) {
		ipAddress.setText(t);
    }
    
    public void setButtonEnabled(boolean b) {
        //requestObjectsButton.setEnabled(b);
        openGripperButton.setEnabled(b);
        closeGripperButton.setEnabled(b);
        initGripperButton.setEnabled(b);
    }

    public void setButtonText(String status) {
        if(status.contentEquals("offline")) {
            scanNetworkButton.setBackground(Color.red);
            initGripperButton.setText("Offline");
        }
        else if (status.contentEquals("idle")) {
            scanNetworkButton.setBackground(Color.orange);
            initGripperButton.setText("Connect");
        }
        else if (status.contentEquals("online")) {
            scanNetworkButton.setBackground(Color.green);
            initGripperButton.setText("Disconnect");
        }
    }

    private Box createSenderInitGripperButton(final SmartHandInstallationNodeContribution contribution) {
        Box box = Box.createHorizontalBox();
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        initGripperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(contribution.getStatus().contentEquals("idle"))
                    contribution.sendScriptInitGripper();
                else if(contribution.getStatus().contentEquals("online"))
                    contribution.sendScriptStopGripper();
            }
        });

        box.add(initGripperButton);
        box.add(createHorizontalSpacing());
        return box;
    }

    private Box createSenderOpenGripperButton(final SmartHandInstallationNodeContribution contribution) {
        Box box = Box.createHorizontalBox();

        openGripperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contribution.sendScriptOpenGripper();
            }
        });

        box.add(openGripperButton);
        
        return box;
    }

    private Box createSenderCloseGripperButton(final SmartHandInstallationNodeContribution contribution) {
        Box box = Box.createHorizontalBox();

        closeGripperButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contribution.sendScriptCloseGripper();
            }
        });

        box.add(closeGripperButton);
        return box;
    }
}