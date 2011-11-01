/* 
@ITMillApache2LicenseForJavaFiles@
 */
//
//package com.vaadin.terminal.gwt.server;
//
//import javax.portlet.PortletConfig;
//import javax.portlet.PortletException;
//
//import com.vaadin.Application;
//
///**
// * TODO Write documentation, fix JavaDoc tags.
// * 
// * @author peholmst
// */
//public class ApplicationPortlet2 extends AbstractApplicationPortlet {
//
//    private Class<? extends Application> applicationClass;
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void init(PortletConfig config) throws PortletException {
//        super.init(config);
//        final String applicationClassName = config
//                .getInitParameter("application");
//        if (applicationClassName == null) {
//            throw new PortletException(
//                    "Application not specified in portlet parameters");
//        }
//
//        try {
//            applicationClass = (Class<? extends Application>) getClassLoader()
//                    .loadClass(applicationClassName);
//        } catch (final ClassNotFoundException e) {
//            throw new PortletException("Failed to load application class: "
//                    + applicationClassName);
//        }
//    }
//
//    @Override
//    protected Class<? extends Application> getApplicationClass() {
//        return applicationClass;
//    }
//
// }
