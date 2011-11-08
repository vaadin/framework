/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;

import com.vaadin.Application;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.VariableOwner;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Component;
import com.vaadin.ui.Root;

/**
 * TODO document me!
 * 
 * @author peholmst
 * 
 */
@SuppressWarnings("serial")
public class PortletCommunicationManager extends AbstractCommunicationManager {

    private transient ResourceResponse currentUidlResponse;

    public PortletCommunicationManager(Application application) {
        super(application);
    }

    public void handleFileUpload(WrappedRequest request,
            WrappedResponse response) throws IOException {
        String contentType = request.getContentType();
        String name = request.getParameter("name");
        String ownerId = request.getParameter("rec-owner");
        VariableOwner variableOwner = getVariableOwner(ownerId);
        StreamVariable streamVariable = ownerToNameToStreamVariable.get(
                variableOwner).get(name);

        if (contentType.contains("boundary")) {
            doHandleSimpleMultipartFileUpload(request, response,
                    streamVariable, name, variableOwner,
                    contentType.split("boundary=")[1]);
        } else {
            doHandleXhrFilePost(request, response, streamVariable, name,
                    variableOwner, request.getContentLength());
        }

    }

    @Override
    protected void unregisterPaintable(Component p) {
        super.unregisterPaintable(p);
        if (ownerToNameToStreamVariable != null) {
            ownerToNameToStreamVariable.remove(p);
        }
    }

    @Override
    public void handleUidlRequest(WrappedRequest request,
            WrappedResponse response, Callback callback, Root root)
            throws IOException, InvalidUIDLSecurityKeyException {
        currentUidlResponse = (ResourceResponse) ((WrappedPortletResponse) response)
                .getPortletResponse();
        super.handleUidlRequest(request, response, callback, root);
        currentUidlResponse = null;
    }

    private Map<VariableOwner, Map<String, StreamVariable>> ownerToNameToStreamVariable;

    @Override
    String getStreamVariableTargetUrl(VariableOwner owner, String name,
            StreamVariable value) {
        if (ownerToNameToStreamVariable == null) {
            ownerToNameToStreamVariable = new HashMap<VariableOwner, Map<String, StreamVariable>>();
        }
        Map<String, StreamVariable> nameToReceiver = ownerToNameToStreamVariable
                .get(owner);
        if (nameToReceiver == null) {
            nameToReceiver = new HashMap<String, StreamVariable>();
            ownerToNameToStreamVariable.put(owner, nameToReceiver);
        }
        nameToReceiver.put(name, value);
        ResourceURL resurl = currentUidlResponse.createResourceURL();
        resurl.setResourceID("UPLOAD");
        resurl.setParameter("name", name);
        resurl.setParameter("rec-owner", getPaintableId((Paintable) owner));
        resurl.setProperty("name", name);
        resurl.setProperty("rec-owner", getPaintableId((Paintable) owner));
        return resurl.toString();
    }

    @Override
    protected void cleanStreamVariable(VariableOwner owner, String name) {
        Map<String, StreamVariable> map = ownerToNameToStreamVariable
                .get(owner);
        map.remove(name);
        if (map.isEmpty()) {
            ownerToNameToStreamVariable.remove(owner);
        }
    }

}
