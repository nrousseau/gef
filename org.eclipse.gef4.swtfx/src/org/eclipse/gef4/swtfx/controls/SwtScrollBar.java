/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.Orientation;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;

public class SwtScrollBar extends SwtControlAdapterNode<ScrolledComposite> {

	// private double size;

	// TODO: this is the wrong Orientation, use enum { HORIZONTAL, VERTICAL }
	private Orientation orientation;

	public SwtScrollBar(Orientation orientation) {
		super(null);
		this.orientation = orientation;
	}

	@Override
	public double computeMaxHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			return computePrefHeight(width);
		}
		return super.computeMaxHeight(width);
	}

	@Override
	public double computeMaxWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			return computePrefWidth(height);
		}
		return super.computeMaxWidth(height);
	}

	@Override
	public double computeMinHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			return computePrefHeight(width);
		}
		return 0;
	}

	@Override
	public double computeMinWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			return computePrefWidth(height);
		}
		return 0;
	}

	@Override
	public double computePrefHeight(double width) {
		if (orientation == Orientation.HORIZONTAL) {
			if (getControl() != null) {
				return getControl().computeTrim(0, 0, 0, 0).height;
			}
		}
		return super.computePrefHeight(width);
	}

	@Override
	public double computePrefWidth(double height) {
		if (orientation == Orientation.VERTICAL) {
			if (getControl() != null) {
				return getControl().computeTrim(0, 0, 0, 0).width;
			}
		}
		return super.computePrefWidth(height);
	}

	protected ScrolledComposite createScrolled(Orientation orientation) {
		int flags;
		switch (orientation) {
		case HORIZONTAL:
			flags = SWT.H_SCROLL;
			break;
		case VERTICAL:
			flags = SWT.V_SCROLL;
			break;
		default:
			throw new IllegalStateException("Unknown Orientation: "
					+ orientation);
		}

		ScrolledComposite scrolled = new ScrolledComposite(getScene(), flags);
		scrolled.setContent(null);
		scrolled.setMinSize(0, 0);
		scrolled.setAlwaysShowScrollBars(true);

		return scrolled;
	}

	@Override
	protected void hookControl() {
		setControl(createScrolled(orientation));
		super.hookControl();
	}

	@Override
	public void resize(double width, double height) {
		/*
		 * Note: We are setting the pref-width or pref-height here so that
		 * computePrefX() returns that size. This is not how it should work.
		 */

		if (orientation == Orientation.HORIZONTAL) {
			setPrefWidth(width);
			super.resize(width, computePrefHeight(width));
		} else if (orientation == Orientation.VERTICAL) {
			setPrefHeight(height);
			super.resize(computePrefWidth(height), height);
		}
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

}
