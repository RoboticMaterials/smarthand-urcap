package com.roboticmaterials.smarthand.impl;

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
	private JTextField jTextField;
	//private JLabel RETURN_VALUE = new JLabel();
	private final JComboBox<String> objectsComboBox = new JComboBox<String>();

	public SmartHandInstallationNodeView(Style style) {
		this.style = style;
	}

	@Override
	public void buildUI(JPanel jPanel, final SmartHandInstallationNodeContribution contribution) {
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

		jPanel.add(createInfo("Please provide the SmartHand IP address:"));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createIPAddressInput(contribution));
		
		jPanel.add(createVerticalSpacing());
		jPanel.add(createRequestObjectsButton(contribution));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createObjectsComboBox(objectsComboBox, contribution));

		jPanel.add(createVerticalSpacing());
		jPanel.add(createInfo("Open and close gripper:"));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderOpenGripperButton(contribution));
		jPanel.add(createVerticalSpacing());
		jPanel.add(createSenderCloseGripperButton(contribution));
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
		
		JButton button = new JButton("Request objects");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.importKnownObjects();
			}
		});

		box.add(createVerticalSpacing());
		box.add(button);
		//box.add(new JLabel("Returned value:"));
		//box.add(this.RETURN_VALUE);
		
		return box;
	}
	
	private Box createSenderOpenGripperButton(final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createVerticalBox();
		
	
		JButton button = new JButton("Open Gripper");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.sendScriptOpenGripper();
			}
		});
		
		//box.add(createVerticalSpacing());
		box.add(button);
		
		return box;
	}
	
	private Box createSenderCloseGripperButton(final SmartHandInstallationNodeContribution contribution) {
		Box box = Box.createVerticalBox();
		
	
		JButton button = new JButton("Close Gripper");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				contribution.sendScriptCloseGripper();
			}
		});
		box.add(button);
		
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

	private Box createIPAddressInput(final SmartHandInstallationNodeContribution contribution) {
		Box inputBox = Box.createHorizontalBox();
		inputBox.setAlignmentX(Component.LEFT_ALIGNMENT);

		inputBox.add(new JLabel("IP:"));
		inputBox.add(createHorizontalSpacing());

		jTextField = new JTextField();
		jTextField.setFocusable(false);
		jTextField.setPreferredSize(style.getInputfieldSize());
		jTextField.setMaximumSize(jTextField.getPreferredSize());
		jTextField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = contribution.getInputForTextField();
				keyboardInput.show(jTextField, contribution.getCallbackForTextField());
			}
		});
		inputBox.add(jTextField);

		return inputBox;
	}

	private Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension(style.getHorizontalSpacing(), 0));
	}

	private Component createVerticalSpacing() {
		return Box.createRigidArea(new Dimension(0, style.getVerticalSpacing()));
	}

	public void setIPAddress(String t) {
		jTextField.setText(t);
	}

}
