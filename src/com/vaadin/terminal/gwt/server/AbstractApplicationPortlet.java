package com.vaadin.terminal.gwt.server;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.vaadin.Application;

public abstract class AbstractApplicationPortlet implements Portlet {

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void init(PortletConfig config) throws PortletException {
		// TODO Auto-generated method stub

	}

	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
		// TODO Auto-generated method stub

	}

	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		// TODO Auto-generated method stub

	}

	protected abstract Class<? extends Application> getApplicationClass()
			throws ClassNotFoundException;

	protected Application getNewApplication(PortletRequest request)
			throws PortletException {
		try {
			final Application application = getApplicationClass().newInstance();
			return application;
		} catch (final IllegalAccessException e) {
			throw new PortletException("getNewApplication failed", e);
		} catch (final InstantiationException e) {
			throw new PortletException("getNewApplication failed", e);
		} catch (final ClassNotFoundException e) {
			throw new PortletException("getNewApplication failed", e);
		}
	}

	protected ClassLoader getClassLoader() throws PortletException {
		// TODO Add support for custom class loader
		return getClass().getClassLoader();
	}
}
