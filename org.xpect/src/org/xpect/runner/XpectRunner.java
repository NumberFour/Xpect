/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.xpect.runner;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.xpect.XpectJavaModel;
import org.xpect.XpectStandaloneSetup;
import org.xpect.setup.IXpectRunnerSetup;
import org.xpect.setup.SetupContext;
import org.xpect.util.AnnotationUtil;
import org.xpect.util.EnvironmentUtil;
import org.xpect.util.XpectJavaModelFactory;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class XpectRunner extends ParentRunner<XpectFileRunner> {

	private List<XpectFileRunner> children;
	private Collection<URI> files;
	private final XpectJavaModel xpectJavaModel;
	private final IXpectURIProvider uriProvider;
	private final Injector xpectInjector;
	public static ClassLoader testClassloader = null;

	public XpectRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		testClassloader = testClass.getClassLoader();
		this.uriProvider = findUriProvider(testClass);
		this.xpectInjector = findXpectInjector();
		this.xpectJavaModel = this.xpectInjector.getInstance(XpectJavaModelFactory.class).createJavaModel(testClass);
	}

	protected Injector getXpectInjector() {
		return xpectInjector;
	}

	public XpectJavaModel getXpectJavaModel() {
		return xpectJavaModel;
	}

	protected Injector findXpectInjector() {
		IResourceServiceProvider rssp = IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("foo.xpect"));
		if (rssp != null)
			return rssp.get(Injector.class);
		if (!EcorePlugin.IS_ECLIPSE_RUNNING)
			return new XpectStandaloneSetup().createInjectorAndDoEMFRegistration();
		throw new IllegalStateException("The language *.xpect is not activated");
	}

	protected XpectFileRunner createChild(Class<?> clazz, URI uri) {
		return new XpectFileRunner(this, uri);
	}

	protected List<XpectFileRunner> createChildren(Class<?> clazz) {
		List<XpectFileRunner> result = Lists.newArrayList();
		for (URI uri : getFiles())
			result.add(createChild(clazz, uri));
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IXpectRunnerSetup<Object, Object, Object, Object> createSetup() {
		List<IXpectRunnerSetup> setups = xpectJavaModel.getSetups(IXpectRunnerSetup.class, EnvironmentUtil.ENVIRONMENT);
		if (setups.isEmpty())
			return null;
		if (setups.size() != 1)
			throw new IllegalStateException("For now, only one setup per test/suite is supported.");
		return setups.get(0);
	}

	protected SetupContext createSetupContext(IXpectRunnerSetup<Object, Object, Object, Object> setup) {
		return new SetupContext();
	}

	@Override
	protected Description describeChild(XpectFileRunner child) {
		return child.getDescription();
	}

	protected IXpectURIProvider findUriProvider(Class<?> clazz) throws InitializationError {
		IXpectURIProvider provider = AnnotationUtil.newInstanceViaMetaAnnotation(clazz, XpectURIProvider.class, IXpectURIProvider.class);
		if (provider == null)
			provider = XpectTestFiles.Default.create(clazz);
		return provider;
	}

	@Override
	protected List<XpectFileRunner> getChildren() {
		if (children == null)
			children = createChildren(getTestClass().getJavaClass());
		return children;
	}

	protected Collection<URI> getFiles() {
		if (files == null)
			files = uriProvider.getAllURIs();
		return files;
	}

	public IXpectURIProvider getUriProvider() {
		return uriProvider;
	}

	@Override
	protected void runChild(XpectFileRunner child, RunNotifier notifier) {
		IXpectRunnerSetup<Object, Object, Object, Object> setup = createSetup();
		SetupContext ctx = createSetupContext(setup);
		ctx.setXpectJavaModel(xpectJavaModel);
		ctx.setAllFiles(getFiles());
		ctx.setTestClass(getTestClass().getJavaClass());
		ctx.setUriProvider(uriProvider);
		try {
			if (setup != null)
				setup.beforeClass(ctx);
			child.run(notifier, setup, ctx);
		} catch (Throwable t) {
			notifier.fireTestFailure(new Failure(getDescription(), t));
		} finally {
			try {
				if (setup != null)
					setup.afterClass(ctx, ctx.getUserClassCtx());
			} catch (Throwable t) {
				notifier.fireTestFailure(new Failure(getDescription(), t));
			}
		}
	}
}
