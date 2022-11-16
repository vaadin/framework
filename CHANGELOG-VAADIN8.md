# Vaadin 8 extended maintenance version changelog

## Vaadin 8.18.0

* Fixed issue [#12560](https://github.com/vaadin/framework/issues/12560) by improving Grid's horizontal scrolling scrolling logic.
* Fixed an issue in Combobox where scrolling to selection would fail if the user had typed into the input field. Possibly related to [#12562](https://github.com/vaadin/framework/issues/12562).
* Added `runAfterRoundTrip` API to the UI class for improved sequence control, allowing execution of a callback after one or more client-server round trips have been completed.
* Separated portlet support code out of `vaadin-server` into its own package, `vaadin-portlet`. **This will break your build** if your application makes use of Portlet classes.
* Added support for Vaadin Multiplatform Runtime version 24+ by adding packages `vaadin-server-mpr-jakarta` and `vaadin-compatibility-server-mpr-jakarta`

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
