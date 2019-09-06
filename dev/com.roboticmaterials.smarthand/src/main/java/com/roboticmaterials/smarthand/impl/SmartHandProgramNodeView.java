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
		
	private final JComboBox<String> commandComboBox = new JComboBox<String>();
	private final JComboBox<String> cartWaypointsComboBox = new JComboBox<String>();
	private final JComboBox<String> objectsInfoComboBox = new JComboBox<String>();
	private final JCheckBox plane_filter = new JCheckBox("Plane filter");
	private final JComboBox apertureVarComboBox = new JComboBox();
	
	private final JSlider forceSliderO = new JSlider();
	private final JSlider forceSliderC = new JSlider();
	private final JSlider forceSliderW = new JSlider();
	
	private final JSlider apertureSlider = new  JSlider();
	
	private final JButton testButton = new JButton();
	private final JButton openButton = new JButton();
	private final JButton closeButton = new JButton();
	private final JButton snapshotButton = new JButton();
	
	private final JLabel openGripperLabel = new JLabel();
	private final JLabel closeGripperLabel = new JLabel();
	private final JLabel widthGripperLabel = new JLabel();
	private final JLabel objectsInfoLabel = new JLabel();
	private final JLabel cartLabel = new JLabel();
	
	//private JLabel imageLabel;
	//private BufferedImage image;
	private ContributionProvider<SmartHandProgramNodeContribution> provider;

	private JPanel cards;
	private static final String[] commands = {"Open Gripper","Close Gripper","Set Gripper Width","Get Object Pose","Move Cart"};
	
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
		toolbox.add(createHorizontalSpacing(10));
		toolbox.add(createTestButton(provider));
		
		
		box.add(toolbox);
		box.add(createSpacer(15));
		box.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		panel.add(box);
		
		
		
		// First card: open gripper
		card1.setLayout(new BoxLayout(card1, BoxLayout.Y_AXIS));
		//card1.setAlignmentX(Component.CENTER_ALIGNMENT);
		//card1.setAlignmentY(Component.LEFT_ALIGNMENT);
		card1.add(createForceOSlider("Force:",forceSliderO, 0, 100, provider));
		card1.add(createDescription(openGripperLabel, "<html><b>Input:</b><br><br><i>force</i>: Maximum force during opening<br><br><b>Output:</b><br><br><i>s</i>: <i>success</i>, binary variable representing successful execution</html>"));
	
		// Second card: close gripper	
		card2.setLayout(new BoxLayout(card2, BoxLayout.Y_AXIS));
		card2.add(createForceCSlider("Force:", forceSliderC, 0, 100, provider));		
		card2.add(createDescription(closeGripperLabel, "<html><b>Input:</b><br><br><i>force</i>: Maximum force during closing<br><br><b>Output:</b><br><br><i>s</i>: <i>success</i>, binary variable representing successful execution</html>"));
		
		// Third card: set gripper width
		card3.setLayout(new BoxLayout(card3, BoxLayout.Y_AXIS));
		card3.add(createForceWSlider("Force:",forceSliderW, 0, 100, provider));		
		card3.add(createApertureSlider("Aperture:", apertureSlider, 0, 108, provider));
		card3.add(createDescription(widthGripperLabel, "<html>"
				+ "<table>"
				+ "<tr><td colspan=2><b>Input:</b></td></tr>"
				+ "<tr><td><i>force</i>:</td><td>Maximum force during gripper motion</td></tr>"
				+ "<tr><td><i>aperture</i>:</td><td>Gripper opening in mm. Either use slider or chose a variable.</td></tr>"
				+ "<tr><td colspan=2>&nbsp;</td></tr>"
				+ "<tr><td colspan=2><b>Output:</b></td></tr>"
				+ "<tr><td><i>s</i>:</td><td><i>success</i>, binary variable representing successful execution.</td></tr>"
				+ "</table></html>"));
		
		
		// Fourth card: get object info
		card4.add(createObjectsInfoComboBox(objectsInfoComboBox, plane_filter, provider));
		card4.add(createDescription(objectsInfoLabel, "<html><table>"
				+ "<tr><td colspan=2><b>Input:</b></td></tr>"
				+ "<tr><td><i>type</i>:</td><td>String determining the specific parameters that the SmartHand will use</td></tr>"
				+ "<tr><td></td><td>for object recognition. Use the 'installation' tab to retrieve a</td></tr>"
				+ "<tr><td></td><td>list of supported objects.</td></tr>"
				+ "<tr><td><i>filter_plane</i>:</td><td>Tick if objects are presented on a plane such as a table, leave</td></tr>"
				+ "<tr><td></td><td>empty when objects are cluttered</td></tr>"
				+ "<tr><td><b>Output:</b></td></tr>"
				+ "<tr><td><i>s</i>:</td><td><i>success</i>, binary variable indicating whether an object was found.</td></tr>"
				+ "<tr><td><i>a</i>:</td><td><i>approach<i>, a <i>pose</i> to approach just above the object.</td></tr>"
				+ "<tr><td><i>t</i>:</td><td><i>target</i>, a <i>pose</i> to grasp the object. Yields current pose if</td></tr>"
				+ "<tr><td><i>s</i> is <i>false</i>.</td></tr>"
				+ "<tr><td><i>w</i>:</td><td><i>width</i>, optimal opening aperture to grasp the object.</td></tr>"
				+ "</table></html>"));
		
		// Fifth card: move cart
		card5.add(createCartWaypointsComboBox(cartWaypointsComboBox, provider));
		card5.add(createDescription(cartLabel, "<html><table>"
				+ "<tr><td colspan=2><b>Input:</b></td></tr>"
				+ "<tr><td><i>type</i>:</td><td>String determining the specific waypoint to move to.</td></tr>"
				+ "<tr><td></td><td>Use the 'installation' tab to retrieve a list of supported waypoints.</td></tr>"
				+ "<tr><td><b>Output:</b></td></tr>"
				+ "<tr><td><i>s</i>:</td><td><i>success</i>, binary variable indicating whether waypoint could be reached.</td></tr>"
				+ "</table></html>"));
	
		
		//Create the panel that contains the "cards".
		cards = new JPanel(new CardLayout());
		cards.add(card1, commands[0]);
		cards.add(card2, commands[1]);
		cards.add(card3, commands[2]);
		cards.add(card4, commands[3]);
		cards.add(card5, commands[4]);
		

		panel.add(cards, BorderLayout.CENTER);
		setButtonsEnabled(false);
	}
	
	private Component createHorizontalSpacing(int width) {
		//return Box.createHorizontalGlue();
		return Box.createRigidArea(new Dimension(width,0));
	}
	
	public void setButtonsEnabled(boolean b) {
		openButton.setEnabled(b);
		closeButton.setEnabled(b);
		snapshotButton.setEnabled(b);
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
	
	public void setCartWaypointsComboBoxItems(String objects) {
		cartWaypointsComboBox.removeAllItems();
		String[] objArray = objects.split("%");
		cartWaypointsComboBox.setModel(new DefaultComboBoxModel<String>(objArray));
	}
	
	
	public void setCartWaypointsComboBoxSelection(String object) {
		cartWaypointsComboBox.setSelectedItem(object);
	}

	public void setObjectsInfoComboBoxItems(String objects) {
		objectsInfoComboBox.removeAllItems();
		String[] objArray = objects.split("%");
		objectsInfoComboBox.setModel(new DefaultComboBoxModel<String>(objArray));
	}
	
	public void setObjectsInfoComboBoxSelection(String object) {
		objectsInfoComboBox.setSelectedItem(object);
	}
	

/*	public void setApertureVarComboBoxItems(String[] vars) {
		apertureVarComboBox.removeAllItems();
		apertureVarComboBox.setModel(new DefaultComboBoxModel<String>(vars));
	}*/
	
	/*public void setApertureVarComboBoxSelection(String var) {
		apertureVarComboBox.setSelectedItem(var);
	}*/
	
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
	
	private Box createDescription(JLabel label, String desc){
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		label.setText(desc);
		
		box.add(label);
		
		return box;
	}
	
	private Box createCartWaypointsComboBox(final JComboBox<String> combo, 
			final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		combo.setPreferredSize(new Dimension(160,30));
		combo.setMaximumSize(combo.getPreferredSize());
		
		combo.addItemListener(new  ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onCartWaypointsSelection((String) e.getItem());
				}
			}
		});
		box.add(combo);
		return box;
	}
	
	private Box createObjectsInfoComboBox(final JComboBox<String> combo, final JCheckBox plane_filter,
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
		
		plane_filter.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				//if(e.getStateChange() == ItemEvent.SELECTED) {
					provider.get().onPlaneFilterSelection(((JCheckBox) e.getItem()).isSelected());
				//}
			}
			
		});
		
		box.add(combo);
		box.createRigidArea(new Dimension(10,0));
		box.add(plane_filter);
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
		 
		apertureVarComboBox.setFocusable(false);
		apertureVarComboBox.setPreferredSize(new Dimension(160,30));
		apertureVarComboBox.setMaximumSize(apertureVarComboBox.getPreferredSize());
		apertureVarComboBox.addItemListener(new ItemListener() {
		            @Override
		            public void itemStateChanged(ItemEvent itemEvent) {
		                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
		                    if (itemEvent.getItem() instanceof Variable) {
		                        provider.get().setApertureVar(((Variable) itemEvent.getItem()));
		                    } else {
		                        provider.get().removeApertureVar();
		                    }
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
	
    public void updateApertureVarComboBox(SmartHandProgramNodeContribution contribution) {
        List<Object> items = new ArrayList<Object>();
        items.addAll(contribution.getGlobalVariables());

        Collections.sort(items, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase()) == 0) {
                    //Sort lowercase/uppercase consistently
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
                }
            }
        });

        //Insert at top after sorting
        items.add(0, "<none>");

        apertureVarComboBox.setModel(new DefaultComboBoxModel(items.toArray()));

        Variable selectedVar = contribution.getSelectedApertureVar();
        if (selectedVar != null) {
        	apertureVarComboBox.setSelectedItem(selectedVar);
        }
    }
		
	private Component createSpacer(int height) {
		return Box.createRigidArea(new Dimension(0,height));
	}

	public String[] getCommands() {
		return commands;
	}

	
	private Box createSenderOpenGripperButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
	
		openButton.setText("Open");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().sendScriptOpenGripper();
			}
		});
		
		//box.add(createVerticalSpacing());
		box.add(openButton);
		
		return box;
	}
	
	private Box createSenderCloseGripperButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
	
		closeButton.setText("Close");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().sendScriptCloseGripper();
			}
		});
		box.add(closeButton);
		
		return box;
	}
	
	private Box createTestButton(final ContributionProvider<SmartHandProgramNodeContribution> provider) {
		Box box = Box.createVerticalBox();
		
		testButton.setText("Test");
		testButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				provider.get().setStatus(provider.get().getInstallation().testHandStatus());
				setTestButtonText(provider.get().getStatus());	
				if(!provider.get().getStatus().contentEquals("offline"))
	      			setButtonsEnabled(true);
	      		else
	      			setButtonsEnabled(false);
			}
		});
		box.add(testButton);
		
		return box;
	}
	
	public void setTestButtonText(String status) {
		testButton.setText(status);
		if(status.contentEquals("offline")) testButton.setBackground(Color.red);
		else
		if(status.contentEquals("idle")) testButton.setBackground(Color.orange);
		else
			if(status.contentEquals("online")) testButton.setBackground(Color.green);
	}

	public void setPlaneFilterSelection(Boolean value) {
		// TODO Auto-generated method stub
		plane_filter.setSelected(value);
		
	}

}
