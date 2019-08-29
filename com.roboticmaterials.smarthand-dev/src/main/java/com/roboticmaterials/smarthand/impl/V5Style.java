package com.roboticmaterials.smarthand.impl;

import java.awt.Dimension;

import com.roboticmaterials.smarthand.impl.Style;

public class V5Style extends Style {
	private static final Dimension INPUTFIELD_SIZE = new Dimension(200, 30);

	@Override
	public Dimension getInputfieldSize() {
		return INPUTFIELD_SIZE;
	}
}
