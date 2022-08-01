# Vaadin 8 extended maintenance version changelog

## Vaadin 8.16.1

* Fixed a failure-to-start condition on some server configurations (e.g. Tomcat) caused 
  by the connector map cleanup logic change in 8.16.0 
* Updated Jetty version from 9.4.43.v20210629 to 9.4.48.v20220622 to fix a potential  
  security issue
* Updated License Checker version to support Vaadin 8 together with the latest Vaadin   
  Flow in MPR configurations
* Updated the license information provided by vaadin-root POM to correctly show CVDL-4 as  
  the project license instead of Apache-2.0 

## Vaadin 8.16.0

* Introduced Snippets feature for the RichTextArea component
* Moved connector map cleaning logic invocation from UI.unlock() to VaadinService.requestEnd() 
  when not using Push
* Improved Grid multi-select performance
* Backported automatic conversion support and other Binder improvements from Vaadin Flow

## Vaadin 8.15.2

* Added support for Liferay kernel versions up to 49
* Changed all resources to use Object.class as interface type to support OSGi Portlets on 
  Liferay CE 7.3.6 GA7 or later (#12504)

## Vaadin 8.15.1

* Fixed an issue where Grid was moving focus away from external input controls when 
  the datasource contents were updated
* Fixed an issue where manual field binding configurations might get overwritten by  
  utomatic binding logic
* Field level verification in Binder is no longer run twice

## Vaadin 8.15.0

 * Change license from Apache 2.0 to CVDLv4
 * Add more intuitive resynchronization error message
 * Allow scrolling away from a Grid using touch
 * Throw exception when attempting to merge BeanPropertySets with identical keys 
   but different value types
