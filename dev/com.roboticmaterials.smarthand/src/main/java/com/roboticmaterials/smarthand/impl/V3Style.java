package com.roboticmaterials.smarthand.impl;

import java.awt.Dimension;

import com.roboticmaterials.smarthand.impl.Style;

public class V3Style extends Style {
	private static final Dimension INPUTFIELD_SIZE = new Dimension(200, 24);

	@Override
	public Dimension getInputfieldSize() {
		return INPUTFIELD_SIZE;
	}
}