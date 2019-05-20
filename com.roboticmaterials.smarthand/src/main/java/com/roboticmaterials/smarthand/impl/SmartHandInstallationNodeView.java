package com.roboticmaterials.smarthand.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
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
	
	private final JComboBox<String> objectsComboBox = new JComboBox<String>();
	private JButton requestObjectsButton;
	private JButton openGripperButton;
	private JButton closeGripperButton;
	private JButton testNetworkButton = new JButton("Test");
	
	
	
	public SmartHandInstallationNodeView(Style style) {
		this.style = style;
	}

	@Override
	public void buildUI(JPanel jPanel, final SmartHandInstallationNodeContribution contribution) {
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
	
		ipAddress.setHorizontalAlignment(JTextField.RIGHT);
		testNetworkButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!contribution.testHandStatus().equals("offline")) {
					setButtonEnabled(true);
					testNetworkButton.setText(contribution.testHandStatus());
				}
				else {
					setButtonEnabled(false);
					testNetworkButton.setText("offline");
				}
			}
		});
		jPanel.add(createLabelInputField("IP Address: ", ipAddress, testNetworkButton, new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setButtonEnabled(false);
				KeyboardTextInput keyboardInput = contribution.getKeyboardForIpAddress();
				keyboardInput.show(ipAddress, contribution.getCallbackForIpAddress());
			}
		}));	
		
		requestObjectsButton = new JButton("Request objects");
		
		jPanel.add(createVerticalSpacing());
		jPanel.add(createRequestObjectsButton(contribution));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createObjectsComboBox(objectsComboBox, contribution));

		openGripperButton = new JButton("Open");
		closeGripperButton = new JButton("Close");
		
		jPanel.add(createVerticalSpacing());
		jPanel.add(createInfo("Open and close gripper:"));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderOpenGripperButton(contribution));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderCloseGripperButton(contribution));
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
	
	public void setButtonEnabled(boolean b) {
		requestObjectsButton.setEnabled(b);
		openGripperButton.setEnabled(b);
		closeGripperButton.setEnabled(b);
	}
	
	
	public void setKnownObjects(String value) {
		String[] objects = value.split("%");
		//RETURN_VALUE.setText(Arrays.toString(objects));
		objectsComboBox.removeAllItems();
		objectsComboBox.setModel(new DefaultComboBoxModel<String>(objects));
	}

	private Box createObjectsComboBox(final JComboBox<String> combo, 
			final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					//provider.get().onCommandSelection((String) e.getItem());
					//setCard((String)e.getItem());
				}
			}
		});
		box.add(combo);
		return box;
	}
	
	private Box createRequestObjectsButton(final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createVerticalBox();
		
		box.add(new JLabel("Obtain list of available object definitions"));
		
		requestObjectsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.importKnownObjects();
			}
		});
		box.add(createVerticalSpacing());
		box.add(requestObjectsButton);
		//box.add(new JLabel("Returned value:"));
		//box.add(this.RETURN_VALUE);
		
		return box;
	}
	
	private Box createSenderOpenGripperButton(final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createVerticalBox();
		
	
		openGripperButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.sendScriptOpenGripper();
			}
		});
		
		//box.add(createVerticalSpacing());
		box.add(openGripperButton);
		
		return box;
	}
	
	private Box createSenderCloseGripperButton(final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createVerticalBox();
		
		closeGripperButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.sendScriptCloseGripper();
			}
		});
		box.add(closeGripperButton);
		
		return box;
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

	private Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(style.getHorizontalSpacing(), 0));
	}

	private Component createVerticalSpacing() {
		return Box.createRigidArea(new Dimension(0, style.getVerticalSpacing()));
	}

	public void setIPAddress(String t) {
		ipAddress.setText(t);
	}

}
