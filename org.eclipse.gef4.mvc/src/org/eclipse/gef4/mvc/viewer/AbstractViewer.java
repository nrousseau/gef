/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 * Note: Parts of this class have been transferred from org.eclipse.gef.ui.parts.AbstractEditPartViewer.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.viewer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMap;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.DefaultContentModel;
import org.eclipse.gef4.mvc.models.DefaultFocusModel;
import org.eclipse.gef4.mvc.models.DefaultHoverModel;
import org.eclipse.gef4.mvc.models.DefaultSelectionModel;
import org.eclipse.gef4.mvc.models.DefaultViewportModel;
import org.eclipse.gef4.mvc.models.DefaultZoomModel;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IViewportModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Inject;

/**
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractViewer<VR> implements IViewer<VR> {

	private AdaptableSupport<IViewer<VR>> as = new AdaptableSupport<IViewer<VR>>(
			this);

	private Map<Object, IContentPart<VR>> contentsToContentPartMap = new HashMap<Object, IContentPart<VR>>();
	private Map<VR, IVisualPart<VR>> visualsToVisualPartMap = new HashMap<VR, IVisualPart<VR>>();

	private IDomain<VR> domain;
	private IRootPart<VR> rootPart;

	private IContentPartFactory<VR> contentPartFactory;
	private IHandlePartFactory<VR> handlePartFactory;
	private IFeedbackPartFactory<VR> feedbackPartFactory;

	/**
	 * @see IViewer#setContentPartFactory(IContentPartFactory)
	 */
	@Inject
	@Override
	public void setContentPartFactory(IContentPartFactory<VR> factory) {
		this.contentPartFactory = factory;
	}

	/**
	 * @see IViewer#getContentPartFactory()
	 */
	@Override
	public IContentPartFactory<VR> getContentPartFactory() {
		return contentPartFactory;
	}

	/**
	 * @see IViewer#getContentModel()
	 */
	@Override
	public IContentModel getContentModel() {
		IContentModel contentModel = getAdapter(AdapterKey.get(IContentModel.class));
		if (contentModel == null) {
			contentModel = new DefaultContentModel();
			setAdapter(AdapterKey.get(IContentModel.class), contentModel);
		}
		return contentModel;
	}

	/**
	 * @see IViewer#getDomain()
	 */
	@Override
	public IDomain<VR> getDomain() {
		return domain;
	}

	@Override
	public <T> T getAdapter(Class<T> classKey) {
		return as.getAdapter(classKey);
	}
	
	@Override
	public <T> T getAdapter(AdapterKey<T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> void setAdapter(AdapterKey<T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Inject
	// IMPORTANT: this method is final to ensure the binding annotation does not
	// get lost on overwriting
	public final void setAdapters(
			@AdapterMap(AbstractViewer.class) Map<AdapterKey<?>, Object> adaptersWithKeys) {
		// do not override locally registered adapters (e.g. within constructor
		// of respective AbstractViewer) with those injected by Guice
		as.setAdapters(adaptersWithKeys, false);
	}
	
	@Override
	public <T> Map<AdapterKey<? extends T>, T> getAdapters(Class<?> classKey) {
		return as.getAdapters(classKey);
	}

	@Override
	public <T> T unsetAdapter(AdapterKey<T> key) {
		return as.unsetAdapter(key);
	}

	/**
	 * @see IViewer#getContentPartMap()
	 */
	@Override
	public Map<Object, IContentPart<VR>> getContentPartMap() {
		return contentsToContentPartMap;
	}

	/**
	 * @see IViewer#getRootPart()
	 */
	@Override
	public IRootPart<VR> getRootPart() {
		return rootPart;
	}

	/**
	 * @see IViewer#getVisualPartMap()
	 */
	@Override
	public Map<VR, IVisualPart<VR>> getVisualPartMap() {
		return visualsToVisualPartMap;
	}

	@Override
	public List<Object> getContents() {
		return getContentModel().getContents();
	}

	/**
	 * @see IViewer#setContents(List)
	 */
	@Override
	public void setContents(List<Object> contents) {
		if (contentPartFactory == null) {
			throw new IllegalStateException(
					"ContentPartFactory has to be set before passing contents in.");
		}
		if (rootPart == null) {
			throw new IllegalStateException(
					"Root part has to be set before passing contents in.");
		}
		getContentModel().setContents(contents);
	}

	/**
	 * @see IViewer#setDomain(IDomain)
	 */
	@Override
	public void setDomain(IDomain<VR> domain) {
		if (this.domain == domain)
			return;
		if (this.domain != null) {
			this.domain.removeViewer(this);
			// deactive all adapters if we are unhooked
			for (Object a : as.getAdapters().values()) {
				if (a instanceof IActivatable) {
					((IActivatable) a).deactivate();
				}
			}
			if (rootPart != null && rootPart.isActive()) {
				rootPart.deactivate();
			}
		}
		this.domain = domain;
		if (this.domain != null) {
			this.domain.addViewer(this);
			if (rootPart != null && !rootPart.isActive()) {
				rootPart.activate();
			}
			// active all adapters if we are (re-)hooked
			for (Object a : as.getAdapters().values()) {
				if (a instanceof IActivatable) {
					((IActivatable) a).activate();
				}
			}
		}
	}

	@Override
	public ISelectionModel<VR> getSelectionModel() {
		@SuppressWarnings("unchecked")
		ISelectionModel<VR> selectionModel = getAdapter(AdapterKey.get(ISelectionModel.class));
		if (selectionModel == null) {
			selectionModel = new DefaultSelectionModel<VR>();
			setAdapter(AdapterKey.get(ISelectionModel.class), selectionModel);
		}
		return selectionModel;
	}

	@Override
	public IHoverModel<VR> getHoverModel() {
		@SuppressWarnings("unchecked")
		IHoverModel<VR> hoverModel = getAdapter(AdapterKey.get(IHoverModel.class));
		if (hoverModel == null) {
			hoverModel = new DefaultHoverModel<VR>();
			setAdapter(AdapterKey.get(IHoverModel.class), hoverModel);
		}
		return hoverModel;
	}

	@Override
	public IZoomModel getZoomModel() {
		IZoomModel zoomModel = getAdapter(AdapterKey.get(IZoomModel.class));
		if (zoomModel == null) {
			zoomModel = new DefaultZoomModel();
			setAdapter(AdapterKey.get(IZoomModel.class), zoomModel);
		}
		return zoomModel;
	}

	/**
	 * @see IViewer#setRootPart(IRootPart)
	 */
	@Inject
	@Override
	public void setRootPart(IRootPart<VR> rootEditPart) {
		if (this.rootPart != null) {
			if (domain != null) {
				this.rootPart.deactivate();
			}
			this.rootPart.setViewer(null);
		}
		this.rootPart = rootEditPart;
		if (this.rootPart != null) {
			this.rootPart.setViewer(this);
			if (domain != null) {
				this.rootPart.activate();
			}
		}
	}

	@Override
	public IFocusModel<VR> getFocusModel() {
		@SuppressWarnings("unchecked")
		IFocusModel<VR> focusModel = getAdapter(AdapterKey.get(IFocusModel.class));
		if (focusModel == null) {
			focusModel = new DefaultFocusModel<VR>();
			setAdapter(AdapterKey.get(IFocusModel.class), focusModel);
		}
		return focusModel;
	}

	@Override
	public IViewportModel getViewportModel() {
		IViewportModel viewportModel = getAdapter(AdapterKey.get(IViewportModel.class));
		if (viewportModel == null) {
			viewportModel = new DefaultViewportModel();
			setAdapter(AdapterKey.get(IViewportModel.class), viewportModel);
		}
		return viewportModel;
	}

	@Override
	public IHandlePartFactory<VR> getHandlePartFactory() {
		return handlePartFactory;
	}

	@Inject
	@Override
	public void setHandlePartFactory(IHandlePartFactory<VR> factory) {
		this.handlePartFactory = factory;
	}

	@Override
	public IFeedbackPartFactory<VR> getFeedbackPartFactory() {
		return feedbackPartFactory;
	}

	@Inject
	@Override
	public void setFeedbackPartFactory(IFeedbackPartFactory<VR> factory) {
		this.feedbackPartFactory = factory;
	}

}
