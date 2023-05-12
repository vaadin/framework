# Vaadin 8 extended maintenance version changelog

## Vaadin 8.20.2

* Removed mentions of NetBeans Plugin, since it has not been maintained and is no longer usable in modern versions of NetBeans. Usage of NetBeans for Vaadin development is still possible, but it is not explicitly supported through a special plugin.
* Improved build system; maven-flatten-plugin is now correctly invoked and build system related version numbers were bumped. This change should not affect users.

## Vaadin 8.20.1

* Fixed a bug in TwinColSelect, which caused incorrect layouting on Firefox on some platforms. The issue was similar to the problem discussed in [#9175](https://github.com/vaadin/framework/issues/9175)
* Updated `license-checker` to the latest available version for better user experience and MPR compatibility.
* Mitigated false positive reports of security vulnerabilities by adding maven-flatten-plugin to the build process. This strips out testing artifacts from the released POMs so that the released artifacts are not marked as vulnerable.

## Vaadin 8.20.0

* Moved vaadin-portlet package contents from com.vaadin.server to com.vaadin.portlet namespace. This is a BREAKING CHANGE and will require you to update your imports. This is a necessary change in order to be able to support OSGi deployments (specifically under the latest Liferay 7 releases.
  * ***PLEASE NOTE THAT THIS IS A BREAKING CHANGE!*** - you need to update references to classes provided by `vaadin-portlet` from the `com.vaadin.server` to the `com.vaadin.portlet` namespace. The complete list of classes is as follows:
    * `com.vaadin.portlet.LegacyVaadinPortlet`
    * `com.vaadin.portlet.RestrictedRenderResponse`
    * `com.vaadin.portlet.VaadinPortlet`
    * `com.vaadin.portlet.VaadinPortletRequest`
    * `com.vaadin.portlet.VAadinPortletService`
    * `com.vaadin.portlet.VaadinPortletSession`
    * `com.vaadin.portlet.WrappedPortletSession`
    
    and
    
    * `com.vaadin.portlet.communication.PortletBootstrapHandler`
    * `com.vaadin.portlet.communication.PortletDummyRequestHandler`
    * `com.vaadin.portlet.communication.PortletListenerNotifier`
    * `com.vaadin.portlet.communication.PortletStateAwareRequestHandler`
    * `com.vaadin.portlet.communication.PortletUIInitHandler`
    
    If you've referenced any of these classes, they will have been in the `com.vaadin.server` and `com.vaadin.server.communication` packages, respectively.
* Improved OSGi packaging for vaadin-portlet. See issue [#12575](https://github.com/vaadin/framework/issues/12575).
* Fixed an issue where push connections could get stuck when using `@PreserveOnRefresh` as requests intended for a new push connection would instead be queued on an old one. Now old connections are closed immediately on reconnect. See issue [#12577](https://github.com/vaadin/framework/issues/12577).
* Fixed Push connection operations synchronization so that a connection won't be disconnected while there are messages pending. This would result in NullPointerExceptions being thrown. Makes the isConnected() call correctly reflect current state. This is a backported fix from Flow. See Flow issue [#15571](https://github.com/vaadin/flow/issues/15571).
* Fixed an issue where undelivered push messages would get lost, resulting in a need for UI resynchronization. Push messages are now kept in cache until a client acknowledges receipt. This is a backported fix from Flow. See Flow issue [#15205](https://github.com/vaadin/flow/issues/15205).
* Improved performance in UIs with assertions enabled. Some assertions would check for the presence of MPR on every run. MPR is now only detected once and the check result is cached. See issue [#12572](https://github.com/vaadin/framework/issues/12572).

## Vaadin 8.19.0

* Changed license from Commercial Vaadin Developer License 4.0 to Vaadin Commercial License version 1. This change does not affect active subscribers, but it does mean that future releases of Vaadin Framework may move from dev- and build-time license checking to runtime license checking. Version 8.19.0 does not do that yet.
See LICENSE file in the downloadable package or [the Vaadin Commercial License and service terms document](https://vaadin.com/commercial-license-and-service-terms) for more information.
* Fixed issue [#12562](https://github.com/vaadin/framework/issues/12562). The internal state of ComboBox got confused when adding new items after calling `clear()`.
* Improved the `vaadin-server-mpr-jakarta` and `vaadin-compatibility-server-mpr-jakarta` POM dependency definitions such that these packages do not pull in `vaadin-server` and `vaadin-compatibility-server` as unwanted dependencies, respectively.
* Fixed a number of assertion functions which would fail when MPR was in use. Improves testability of the framework and should allow running with assertions enabled in MPR configurations.
* Removed logging of Atmosphere version while MPR is in use. This fixes runtime failures under some configurations.
* Updated Atmosphere to version 2.4.30-vaadin5 in order to make Vaadin Framework 8 compatible with Jetty 10.
* Updated Vaadin License Checker to version 1.11.2 to support new license model.

## Vaadin 8.18.0

* Fixed issue [#12560](https://github.com/vaadin/framework/issues/12560) by improving Grid's horizontal scrolling scrolling logic.
* Fixed an issue in Combobox where scrolling to selection would fail if the user had typed into the input field. Possibly related to [#12562](https://github.com/vaadin/framework/issues/12562).
* Added `runAfterRoundTrip` API to the UI class for improved sequence control, allowing execution of a callback after one or more client-server round trips have been completed.
* Separated portlet support code out of `vaadin-server` into its own package, `vaadin-portlet`. **This will break your build** if your application makes use of Portlet classes and you do not import the `vaadin-portlet` dependency.
* Added support for Vaadin Multiplatform Runtime version 24+ by adding packages `vaadin-server-mpr-jakarta` and `vaadin-compatibility-server-mpr-jakarta`. This is only needed for MPR 24+, and is ***NOT*** guaranteed to work as generic Jakarta support (even though it does so at the moment) as we may add MPR specific functionality or even hard MPR dependencies in the future.

## Vaadin 8.17.0

* Improved hierarchical data container handling, resulting in increased speed and correcting some potential memory leaks.
* Fixed an issue in Grid's MultiSelect mode introduced by performance improvements made to Vaadin 8.16.0 which would erroneously cause a refreshed data item to become selected.
* Fixed an issue introduced in Vaadin 8.15.1's Grid focus stealing fix where the focus stealing prevention would become overly zealous.
* Updated JSoup version from 1.14.3 to 1.15.3. This is a **BREAKING CHANGE** for many projects, as it requires **you** to find all references to `org.jsoup.safety.Whitelist` and replace them with references to `org.jsoup.safety.Safelist`. The API of the two classes is the same; `Safelist` is a drop-in replacement for `Whitelist`. This change was made to all Vaadin versions simultaneously to address [CVE-2022-36033](https://ossindex.sonatype.org/vulnerability/CVE-2022-36033) and keep the dependencies in line for use with [Vaadin MPR](https://vaadin.com/docs/latest/tools/mpr/overview).
* Updated PuppyCrawl dependency from version 8.18 to 8.29
* Updated CheckStyle dependency from version 2.17 to 3.2.0

## Vaadin 8.16.1

* Fixed a failure-to-start condition on some server configurations (e.g. Tomcat) caused  by the connector map cleanup logic change in 8.16.0 
* Updated Jetty version from 9.4.43.v20210629 to 9.4.48.v20220622 to fix a potential security issue
* Updated License Checker version to support Vaadin 8 together with the latest Vaadin Flow in MPR configurations
* Updated the license information provided by vaadin-root POM to correctly show CVDL-4 as the project license instead of Apache-2.0 

## Vaadin 8.16.0

* Introduced Snippets feature for the RichTextArea component
* Moved connector map cleaning logic invocation from UI.unlock() to VaadinService.requestEnd() when not using Push
* Improved Grid multi-select performance
* Backported automatic conversion support and other Binder improvements from Vaadin Flow

## Vaadin 8.15.2

* Added support for Liferay kernel versions up to 49
* Changed all resources to use Object.class as interface type to support OSGi Portlets on Liferay CE 7.3.6 GA7 or later ([#12504](https://github.com/vaadin/framework/issues/12504))

## Vaadin 8.15.1

* Fixed an issue where Grid was moving focus away from external input controls when the datasource contents were updated
* Fixed an issue where manual field binding configurations might get overwritten by automatic binding logic
* Field level verification in Binder is no longer run twice

## Vaadin 8.15.0

* Change license from Apache 2.0 to CVDLv4
* Add more intuitive resynchronization error message
* Allow scrolling away from a Grid using touch
* Throw exception when attempting to merge BeanPropertySets with identical keys but different value types
