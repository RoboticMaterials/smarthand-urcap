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
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
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
	private JComboBox<String> objectsPoseComboBox = new JComboBox<String>();
	private JComboBox<String> objectsInfoComboBox = new JComboBox<String>();
	private JComboBox<String> apertureVarComboBox = new JComboBox<String>();
	
	private JSlider forceSliderO = new JSlider();
	private JSlider forceSliderC = new JSlider();
	private JSlider forceSliderW = new JSlider();
	
	private JSlider apertureSlider = new  JSlider();
	
	
	//private JLabel imageLabel;
	//private BufferedImage image;
	private ContributionProvider<SmartHandProgramNodeContribution> provider;

	private JPanel cards;
	private static final String[] commands = {"Open Gripper","Close Gripper","Set Gripper Width","Get Object Pose","Get Object Info"};
	
	@Override
	public void buildUI(JPanel panel, final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		
		this.provider=provider;
		JPanel card1 = new JPanel();
		JPanel card2 = new JPanel();
		JPanel card3 = new JPanel();
		JPanel card4 = new JPanel();
		JPanel card5 = new JPanel();
	
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		

		Box box = Box.createVerticalBox();
		
		Box toolbox = Box.createHorizontalBox();
		toolbox.setAlignmentX(Component.CENTER_ALIGNMENT);
		toolbox.add(createCommandComboBox(commandComboBox, provider));
		toolbox.add(createHorizontalSpacing(10));
		toolbox.add(createSenderOpenGripperButton(provider));
		toolbox.add(createHorizontalSpacing(10));
		toolbox.add(createSenderCloseGripperButton(provider));
		
		box.add(toolbox);
		box.add(createSpacer(15));
		box.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		panel.add(box);
		
		
		
		// First card: open gripper
		card1.setLayout(new BoxLayout(card1, BoxLayout.Y_AXIS));
		card1.add(createForceOSlider("Force:",forceSliderO, 0, 100, provider));
		
		// Second card: close gripper	
		card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));
		card2.add(createForceCSlider("Force:", forceSliderC, 0, 100, provider));		
		
		// Third card: set gripper width
		card3.setLayout(new BoxLayout(card3, BoxLayout.Y_AXIS));
		card3.add(createForceWSlider("Force:",forceSliderW, 0, 100, provider));		
		card3.add(createApertureSlider("Aperture:", apertureSlider, 0, 108, provider));
	

		
/*		try {
			ClassLoader classLoader = getClass().getClassLoader();
			image = ImageIO.read(classLoader.getResource("rmlogo.png"));
//			image = ImageIO.read(new URL("http://192.168.2.119:8000/snapshot.png"));
			imageLabel = new JLabel(new ImageIcon(image));
			imageLabel.setPreferredSize(new Dimension(320,240));
			imageLabel.setMaximumSize(imageLabel.getPreferredSize());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
			
		// Fourth card: get object pose
		card4.add(createObjectsPoseComboBox(objectsPoseComboBox, provider));
//		card4.add(createSpacer(10));
//		card4.add(createRequestImageButton(provider));
		
		// Fifth card: get object info
		card5.add(createObjectsInfoComboBox(objectsInfoComboBox, provider));
	
		
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(card1, commands[0]);
		cards.add(card2, commands[1]);
		cards.add(card3, commands[2]);
		cards.add(card4, commands[3]);
		cards.add(card5, commands[4]);
		

		panel.add(cards, BorderLayout.CENTER);
	}
	
	private Component createHorizontalSpacing(int width) {
		//return Box.createHorizontalGlue();
		return Box.createRigidArea(new Dimension(width,0));
	}
	
/*	public void updateCameraFeed() {
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
		
	}*/
	
	private Box createRequestImageButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
		box.add(new JLabel("Obtain list of available object definitions"));
		
		JButton button = new JButton("Request image");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().requestPoseAndImage();
			/*	try {
					image = ImageIO.read(new URL("http://" +
							provider.get().getIPAddress() + 
							":8000/snapshot.png"));
					
					System.out.println("Read from: " + "http://" + 
							provider.get().getIPAddress() + 
						    ":8000/snapshot.png");
					imageLabel.setIcon(new ImageIcon(image));
					imageLabel.repaint();
					
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
			}
		});
		
/*		imageLabel = new JLabel(new ImageIcon(image));
		imageLabel.setPreferredSize(new Dimension(320,240));
		imageLabel.setMaximumSize(imageLabel.getPreferredSize());
		
		box.add(imageLabel);*/
		box.add(button);
		//box.add(new JLabel("Returned value:"));
		//box.add(this.RETURN_VALUE);
		

		
		return box;
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
	
	public void setObjectsPoseComboBoxItems(String objects) {
		objectsPoseComboBox.removeAllItems();
		String[] objArray = objects.split("%");
		objectsPoseComboBox.setModel(new DefaultComboBoxModel<String>(objArray));
	}
	
	
	public void setObjectsPoseComboBoxSelection(String object) {
		objectsPoseComboBox.setSelectedItem(object);
	}

	public void setObjectsInfoComboBoxItems(String objects) {
		objectsInfoComboBox.removeAllItems();
		String[] objArray = objects.split("%");
		objectsInfoComboBox.setModel(new DefaultComboBoxModel<String>(objArray));
	}
	
	public void setObjectsInfoComboBoxSelection(String object) {
		objectsInfoComboBox.setSelectedItem(object);
	}
	
	public void setApertureVarComboBoxItems(String[] vars) {
		apertureVarComboBox.removeAllItems();
		apertureVarComboBox.setModel(new DefaultComboBoxModel<String>(vars));
	}
	
	public void setApertureVarComboBoxSelection(String var) {
		apertureVarComboBox.setSelectedItem(var);
	}
	
	public void setForceSliderO(int value) {
		forceSliderO.setValue(value);
	}
	
	public void setForceSliderC(int value) {
		forceSliderC.setValue(value);
	}
	
	public void setForceSliderW(int value) {
		forceSliderC.setValue(value);
	}
	
	public void setApertureSlider(int value) {
		apertureSlider.setValue(value);
	}
	
	
	private Box createDescription(String desc){
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel label = new JLabel(desc);
		
		box.add(label);
		
		return box;
	}
	
	private Box createObjectsPoseComboBox(final JComboBox<String> combo, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onPoseObjectSelection((String) e.getItem());
				}
			}
		});
		box.add(combo);
		return box;
	}
	
	private Box createObjectsInfoComboBox(final JComboBox<String> combo, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onInfoObjectSelection((String) e.getItem());
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
		
		final JLabel label = new JLabel("Avail. commands:");
		label.setPreferredSize(new Dimension(120,30));
		label.setMaximumSize(label.getPreferredSize());
		
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
	
	private Box createForceOSlider(String labelstr, final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		final JLabel label = new JLabel(labelstr);
		label.setPreferredSize(new Dimension(80,30));
		label.setMaximumSize(label.getPreferredSize());
		
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
		
		box.add(label);
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createForceCSlider(String labelstr, final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		final JLabel label = new JLabel(labelstr);
		label.setPreferredSize(new Dimension(80,30));
		label.setMaximumSize(label.getPreferredSize());
		
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
		
		box.add(label);
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createForceWSlider(String labelstr, final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		final JLabel label = new JLabel(labelstr);
		label.setPreferredSize(new Dimension(80,30));
		label.setMaximumSize(label.getPreferredSize());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		
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
				provider.get().onForceWSelection(newValue);
				
			}
		});
		
		box.add(label);
		box.add(slider);
		box.add(value);
		return box;		
	}
	
	private Box createApertureSlider(String labelstr, final JSlider slider, int min, int max, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		
		// Arrange all items in a horizontal box
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Start with a label
		final JLabel label = new JLabel(labelstr);
		label.setPreferredSize(new Dimension(80,30));
		label.setMaximumSize(label.getPreferredSize());
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// Add slider
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setOrientation(JSlider.HORIZONTAL);
		slider.setPreferredSize(new Dimension(115,30));
		slider.setMaximumSize(slider.getPreferredSize());
		final JLabel value = new JLabel(Integer.toString(slider.getValue()) +" mm");
		
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int newValue = slider.getValue();
				value.setText(Integer.toString(newValue)+" mm");
				provider.get().onApertureSelection(newValue);
				
			}
		});
		
		
		apertureVarComboBox.setPreferredSize(new Dimension(160,30));
		apertureVarComboBox.setMaximumSize(apertureVarComboBox.getPreferredSize());
		
		apertureVarComboBox.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onApertureVarSelection((String) e.getItem());
				}
			}
		});
		
		
		// Piece all items together
		box.add(label);
		box.add(slider);
		box.add(value);
		box.add(apertureVarComboBox);
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
