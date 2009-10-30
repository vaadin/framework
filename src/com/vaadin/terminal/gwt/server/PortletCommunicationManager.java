package com.vaadin.terminal.gwt.server;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import com.vaadin.external.org.apache.commons.fileupload.FileUploadException;

/**
 * TODO Write documentation, fix JavaDoc tags.
 * 
 * @author peholmst
 */
public interface PortletCommunicationManager {

    public void handleFileUpload(ActionRequest request, ActionResponse response)
            throws FileUploadException, IOException;

    public void handleUIDLRequest(ActionRequest request, ActionResponse response)
            throws IOException, PortletException;

}
