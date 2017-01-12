/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.gef.fx.nodes.InfiniteCanvas;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.policies.ViewportPolicy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import javafx.geometry.Point2D;
import javafx.scene.Parent;

/**
 * The {@link AbstractZoomAction} is an {@link AbstractViewerAction} that alters
 * the zoom level while preserving the center of the diagram. The new zoom level
 * for the diagram is computed by {@link #determineZoomFactor(double, Event)}.
 *
 * @author mwienand
 *
 */
public abstract class AbstractZoomAction extends AbstractViewerAction {

	/**
	 * Constructs a new {@link AbstractZoomAction}.
	 *
	 * @param text
	 *            Text for the action.
	 */
	protected AbstractZoomAction(String text) {
		this(text, IAction.AS_PUSH_BUTTON, null);
	}

	/**
	 * Constructs a new {@link AbstractZoomAction} with the given text and
	 * style. Also sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected AbstractZoomAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected ITransactionalOperation createOperation(Event event) {
		InfiniteCanvas infiniteCanvas = getInfiniteCanvas();
		if (infiniteCanvas == null) {
			throw new IllegalStateException(
					"Cannot perform AbstractZoomAction, because no InfiniteCanvas can be determiend.");
		}

		// compute zoom factor
		AffineTransform contentTransform = FX2Geometry
				.toAffineTransform(infiniteCanvas.getContentTransform());
		double sx = determineZoomFactor(contentTransform.getScaleX(), event);

		// compute pivot point
		Point2D pivotInScene = infiniteCanvas.localToScene(
				infiniteCanvas.getWidth() / 2, infiniteCanvas.getHeight() / 2);

		// determine viewport policy
		ViewportPolicy viewportPolicy = getViewer().getRootPart()
				.getAdapter(ViewportPolicy.class);
		if (viewportPolicy == null) {
			throw new IllegalStateException(
					"Cannot perform AbstractZoomAction, because no ViewportPolicy can be determined.");
		}

		// build zoom operation
		viewportPolicy.init();
		viewportPolicy.zoom(false, false, sx, pivotInScene.getX(),
				pivotInScene.getY());
		ITransactionalOperation operation = viewportPolicy.commit();
		return operation;
	}

	/**
	 * Returns the zoom factor that is applied when performing this action.
	 *
	 * @param currentZoomFactor
	 *            The current zoom factor.
	 * @param event
	 *            TODO
	 * @return The zoom factor that is applied when performing this action.
	 */
	protected abstract double determineZoomFactor(double currentZoomFactor,
			Event event);

	/**
	 * Returns the {@link InfiniteCanvas} of the viewer where this action is
	 * installed.
	 *
	 * @return The {@link InfiniteCanvas} of the viewer.
	 */
	protected InfiniteCanvas getInfiniteCanvas() {
		Parent canvas = getViewer().getCanvas();
		if (canvas instanceof InfiniteCanvas) {
			return (InfiniteCanvas) canvas;
		}
		return null;
	}
}