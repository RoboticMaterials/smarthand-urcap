package com.roboticmaterials.smarthand.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.variable.Variable;

public class SmartHandProgramNodeView implements SwingProgramNodeView<SmartHandProgramNodeContribution>{

    private final ViewAPIProvider apiProvider;
	
	public SmartHandProgramNodeView(ViewAPIProvider apiProvider) {
		this.apiProvider = apiProvider;
    }
    
    Icon newpic = new ImageIcon("com.roboticmaterials.smarthand\src\main\resources\rmlogo.png");

    //Basic Function Buttons
    private final JButton openCloseButton = new JButton("Open/Close Gripper");
    private final JButton widthGripperButton = new JButton("Gripper Width");
    private final JButton objectPoseButton = new JButton("Object Pose");
    private final JButton moveCartbutton = new JButton("Move Cart");

    //Advance Function Buttons
    private final JButton pickPlaceButton = new JButton(newpic);
    private final JButton assemblyButton = new JButton("Assembly");
    private final JButton binPickingButton = new JButton("Bin Picking");
    private final JButton restockingButton = new JButton("Restocking");

    private final JSlider openCloseForceSlider = new JSlider();

    private final JCheckBox planeFileter = new JCheckBox("Plane Filter");


    @Override
    public void buildUI(JPanel panel, final ContributionProvider<SmartHandProgramNodeContribution> provider) {

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        //Panel for basic tasks
        panel.add(createDescription("Basic"));
        panel.add(openCloseButton);
        panel.add(widthGripperButton);
        panel.add(objectPoseButton);
        panel.add(moveCartbutton);

        //Panel for advance tasks
        panel.add(createDescription("Advance"));
        panel.add(pickPlaceButton);
        panel.add(assemblyButton);
        panel.add(binPickingButton);
        panel.add(restockingButton);

    }
        private Box createDescription(String desc) {
            Box box = Box.createHorizontalBox();
            box.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel label = new JLabel(desc);

            box.add(label);

            return box;
        }


        // Box box = Box.createVerticalBox();
		
		// Box toolbox = Box.createHorizontalBox();
		// toolbox.setAlignmentX(Component.CENTER_ALIGNMENT);
		// toolbox.add(createCommandComboBox(commandComboBox, provider));
		// toolbox.add(createHorizontalSpacing(10));
		// toolbox.add(createSenderOpenGripperButton(provider));
		// toolbox.add(createHorizontalSpacing(10));
		// toolbox.add(createSenderCloseGripperButton(provider));
		// toolbox.add(createHorizontalSpacing(10));
        // toolbox.add(createTestButton(provider));
        
        //this needs to be refined
    
}
