/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The {@link BundleActivator} for the MVC.FX bundle.
 */
public class MvcFxBundle implements BundleActivator {

	/**
	 * If the bundle has been started, returns the {@link BundleContext}
	 * associated to it.
	 *
	 * @return The {@link BundleContext} of the module if this bundle was
	 *         started ({@link #start(BundleContext)}) and has since not been
	 *         stopped ( {@link #stop(BundleContext)}), <code>null</code>
	 *         otherwise.
	 */
	public static BundleContext getContext() {
		return context;
	}

	/**
	 * The plug-in id of the MVC.FX bundle.
	 */
	public static final String PLUGIN_ID = "org.eclipse.gef.mvc.fx"; //$NON-NLS-1$

	private static BundleContext context;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		MvcFxBundle.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		MvcFxBundle.context = null;
	}

}
