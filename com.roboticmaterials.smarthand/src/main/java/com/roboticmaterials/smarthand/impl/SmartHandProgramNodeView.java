package com.roboticmaterials.smarthand.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;

public class SmartHandProgramNodeView implements SwingProgramNodeView<SmartHandProgramNodeContribution>{

	private final ViewAPIProvider apiProvider;
	
	public SmartHandProgramNodeView(ViewAPIProvider apiProvider) {
		this.apiProvider = apiProvider;
	}
	
	
	
	private JComboBox<String> commandComboBox = new JComboBox<String>();
	private JComboBox<String> objectsComboBox = new JComboBox<String>();

	private JSlider forceSliderO = new JSlider();
	private JSlider forceSliderC = new JSlider();
	
	private JSlider apertureSliderO = new  JSlider();
	private JSlider apertureSliderC = new  JSlider();

	
	private JLabel imageLabel;
	private BufferedImage image;
	private ContributionProvider<SmartHandProgramNodeContribution> provider;

	private JPanel cards;
	//final static String CMDOPENGRIPPER = "Open Gripper";
	//final static String CMDCLOSEGRIPPER = "Close Gripper";
	private static final String[] commands = {"Open Gripper","Close Gripper","Get Object Pose"};
	
	@Override
	public void buildUI(JPanel panel, final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		
		this.provider=provider;
		JPanel card1 = new JPanel();
		JPanel card2 = new JPanel();
		JPanel card3 = new JPanel();

		//card1.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		card1.setLayout(new BoxLayout(card1, BoxLayout.Y_AXIS));
		card1.add(createDescription("Force:"));
		card1.add(createSpacer(5));
		card1.add(createForceOSlider(forceSliderO, 0, 100, provider));
		card1.add(createDescription("Aperture:"));
		card1.add(createSpacer(5));
		card1.add(createApertureOSlider(apertureSliderO, 0, 100, provider));
		
		//closeGripperPanel.add(createIOComboBox(ioComboBox, provider));
		//closeGripperPanel.add(createSpacer(20));
		
		card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));
		card2.add(createDescription("Force:"));
		card2.add(createSpacer(5));
		card2.add(createForceCSlider(forceSliderC, 0, 100, provider));		
		card2.add(createDescription("Aperture:"));
		card2.add(createSpacer(5));
		card2.add(createApertureCSlider(apertureSliderC, 0, 100, provider));
	
		/*try {
			image = ImageIO.read(new URL("http://192.168.2.119:8000/snapshot.png"));
			imageLabel = new JLabel(new ImageIcon(image));
			imageLabel.setPreferredSize(new Dimension(500,375));
			imageLabel.setMaximumSize(imageLabel.getPreferredSize());
			card3.add(imageLabel);
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		//card3.add(imageLabel);
		
		card3.add(createObjectsComboBox(objectsComboBox, provider));
		
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(card1, commands[0]);
		cards.add(card2, commands[1]);
		cards.add(card3, commands[2]);
		

		panel.add(createCommandComboBox(commandComboBox, provider));
		panel.add(createSpacer(10));
		panel.add(createDescription("Open and close gripper:"));
		panel.add(createSpacer(10));
		panel.add(createSenderOpenGripperButton(provider));
		panel.add(createSpacer(10));
		panel.add(createSenderCloseGripperButton(provider));
		
		panel.add(cards, BorderLayout.CENTER);
	}
	
	public void updateCameraFeed() {
		try {
			image = ImageIO.read(new URL("http://" +
					provider.get().getInstallation().getIPAddress() + 
					":8000/snapshot.png"));
			//imageLabel = new JLabel(new ImageIcon(image));
			System.out.println("Read from: " + "http://" + 
					provider.get().getInstallation().getIPAddress() + 
				    ":8000/snapshot.png");
			imageLabel.setIcon(new ImageIcon(image));
			imageLabel.repaint();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	public void setCard(String card) {
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards,card);
	}
	
	public void setCommandComboBoxItems(String[] items) {
		commandComboBox.removeAllItems();
		commandComboBox.setModel(new DefaultComboBoxModel<String>(items));
	}
	
	public void setCommandComboBoxSelection(String item) {
		commandComboBox.setSelectedItem(item);
	}
	
	public void setObjectsComboBoxItems(String objects) {
		objectsComboBox.removeAllItems();
		String[] objArray = objects.split("%");
		objectsComboBox.setModel(new DefaultComboBoxModel<String>(objArray));
	}
	
	public void setObjectsComboBoxSelection(String object) {
		objectsComboBox.setSelectedItem(object);
	}
	
	public void setForceSliderO(int value) {
		forceSliderO.setValue(value);
	}
	
	public void setForceSliderC(int value) {
		forceSliderC.setValue(value);
	}
	
	public void setApertureSliderO(int value) {
		forceSliderO.setValue(value);
	}
	
	public void setApertureSliderC(int value) {
		forceSliderC.setValue(value);
	}
	
	private Box createDescription(String desc){
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel label = new JLabel(desc);
		
		box.add(label);
		
		return box;
	}
	
	private Box createObjectsComboBox(final JComboBox<String> combo, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onObjectSelection((String) e.getItem());
				}
			}
		});
		box.add(combo);
		return box;
	}
	
	private Box createCommandComboBox(final JComboBox<String> combo, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel label = new JLabel("Available commands");
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onCommandSelection((String) e.getItem());
					setCard((String)e.getItem());
				}
			}
		});
		box.add(label);
		box.add(combo);
		
		return box;
	}
	
	private Box createForceOSlider(final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		slider.setMinimum(min);
		slider.setMaximum(max);
	
		slider.setOrientation(JSlider.HORIZONTAL);
		
		slider.setPreferredSize(new Dimension(275,30));
		slider.setMaximumSize(slider.getPreferredSize());
		
		final JLabel value = new JLabel(Integer.toString(slider.getValue()) +" %");
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = slider.getValue();
				value.setText(Integer.toString(newValue)+" %");
				provider.get().onForceOSelection(newValue);
				
			}
		});
		
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createForceCSlider(final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		slider.setMinimum(min);
		slider.setMaximum(max);
	
		slider.setOrientation(JSlider.HORIZONTAL);
		
		slider.setPreferredSize(new Dimension(275,30));
		slider.setMaximumSize(slider.getPreferredSize());
		
		final JLabel value = new JLabel(Integer.toString(slider.getValue()) +" %");
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = slider.getValue();
				value.setText(Integer.toString(newValue)+" %");
				provider.get().onForceCSelection(newValue);
				
			}
		});
		
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createApertureOSlider(final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		slider.setMinimum(min);
		slider.setMaximum(max);
	
		slider.setOrientation(JSlider.HORIZONTAL);
		
		slider.setPreferredSize(new Dimension(275,30));
		slider.setMaximumSize(slider.getPreferredSize());
		
		final JLabel value = new JLabel(Integer.toString(slider.getValue()) +" %");
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = slider.getValue();
				value.setText(Integer.toString(newValue)+" %");
				provider.get().onApertureOSelection(newValue);
				
			}
		});
		
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createApertureCSlider(final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		slider.setMinimum(min);
		slider.setMaximum(max);
	
		slider.setOrientation(JSlider.HORIZONTAL);
		
		slider.setPreferredSize(new Dimension(275,30));
		slider.setMaximumSize(slider.getPreferredSize());
		
		final JLabel value = new JLabel(Integer.toString(slider.getValue()) +" %");
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = slider.getValue();
				value.setText(Integer.toString(newValue)+" %");
				provider.get().onApertureCSelection(newValue);
				
			}
		});
		
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Component createSpacer(int height) {
		return Box.createRigidArea(new Dimension(0,height));
	}

	public String[] getCommands() {
		// TODO Auto-generated method stub
		return commands;
	}

	private Box createSenderOpenGripperButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
	
		JButton button = new JButton("Open Gripper");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().sendScriptOpenGripper();
			}
		});
		
		//box.add(createVerticalSpacing());
		box.add(button);
		
		return box;
	}
	
	private Box createSenderCloseGripperButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
	
		JButton button = new JButton("Close Gripper");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().sendScriptCloseGripper();
			}
		});
		box.add(button);
		
		return box;
	}

}
