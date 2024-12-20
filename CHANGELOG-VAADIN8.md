# Vaadin 8 extended maintenance version changelog

## Vaadin 8.27.4

* Added feature to automatically resume Push connection when a client unexpectedly loses communication with the server. Pending Push messages are now cached on the server until the session times out or connectivity is restored for a better user experience.
  This is a backport of Flow pull request [#20283](https://github.com/vaadin/flow/pull/20283).
* Fixed Framework issue #11699. Previously, calling UI.getCurrent() inside a UI.access() callback could return a random UI instance under certain circumastances. CurrentInstance is now properly cleared before the current session reference is set before running any pending access callbacks.
  This is a backport of Flow pull request [#20255](https://github.com/vaadin/flow/pull/20255).
* Moved (some) blocking calls outside of session lock. Fixes a potential deadlock issue. This is a backport of Flow pull request [#20475](https://github.com/vaadin/flow/pull/20475).
* Specified pointer-events: auto as default for overlay containers in the Valo theme. This fixes an edge case where users were unable to select values from V8 comboboxes placed inside a V14 Dialog component when running under the Vaadin Multi Platform Runtime.

## Vaadin 8.27.3

* Added an option to disable Push disconnect on refresh. This is a special-case workaround. Normally when UI is refreshed when `@PreserveOnRefresh` is used, UI checks if an associated Push connection is active and disconnects it in order to avoid a race condition. This functionality was originally introduced to fix Framework [issue #12577](https://github.com/vaadin/framework/issues/12577). However, with some containers such as Payara this can have unwanted side effects, such as CDI reporting that no session scoped context is available after refresh.

  This feature can now be toggled off using the `protected` UI method `setDisconnectPushOnRefresh(false)`.

* Updated Atmosphere to detect Payara alongside Glassfish and enable async support for it. This is a workaround for a bug/feature that exists both in Glassfish and Payara that provides a null ServerContainer reference that would otherwise cause initialization to fail.

## Vaadin 8.27.2

* Included the `vaadin-portlet` package in the Vaadin 8 BOM. As a result, the version of the `vaadin-portlet` package does not need to be defined if the `vaadin-bom` artifact is imported.
  This definition was originally left out due to an oversight and the omission was discovered during internal manual testing.
* Improved initialization of the eager unload feature when using Firefox, Safari or any iOS browser.

  PLEASE NOTE: this feature will make the "are you sure you want to leave" confirmation not function as intended, as these browsers need to hook onto the `beforeunload` event. This will cause the session to become invalidated and will require reloading the page even if the user decides to stay.
  If you need to disable the eager unload functionality for Safari/Firefox/iOS in order to display the confirmation, directly assign a handler to `window.onbeforeunload` with a JavaScript call.

## Vaadin 8.27.1

* Fixed an issue with the `vaadin-push` packaging which prevented the JavaScript payload from loading.
* Internal test fixes

## Vaadin 8.27.0

* Added `vaadin-push-jakarta` package for a Jakarta-based Push implementation, based on Atmosphere 3. This package is meant to be used together with the `vaadin-server-mpr-jakarta` and `vaadin-compatibility-server-mpr-jakarta` packages in environments like Spring 6+ and Jetty 11+.
  
  This feature is to be considered experimental for the moment; please report any and all issues you encounter with it to [Vaadin Support](https://support.vaadin.com/).
* Altered packaging of `vaadin-client`, `vaadin-compatibility-client` and `vaadin-client-compiler` packages to have the `vaadin-server` and `vaadin-compatiblity-server` dependencies with provided scope.
  
  This is a **potentially breaking change** if your build expects to have a transitive `vaadin-server` dependency.
  
  This change was made in order to make it easier to use the Jakarta versions of the server and push packages, namely `vaadin-server-mpr-jakarta`, `vaadin-compatibility-server-mpr-jakarta` and `vaadin-push-jakarta`, as they provide the same API and class structure but rely on the Jakarta namespace instead of Javax.
* Improved change detection of Binder. This is a backport from Flow pull request [#19488](https://github.com/vaadin/flow/pull/19488) which fixes Flow issue [#19260](https://github.com/vaadin/flow/issues/19260).
  
  In short, previously a reverted change to a bound field would still be considered a change. With this change, the meaningfulness of the value changes is considered.
* Fixed an issue in Binder where calling `binder.removeBinding()` could result in a null pointer exception down the line, as the removed binding was not also removed from the `changedBindings` list.
  
  This is a backport of Flow pull request [#6827](https://github.com/vaadin/flow/pull/6827) which fixes Flow issue [#5384](https://github.com/vaadin/flow/issues/5384).
* Changed the internal `LayoutManager.layoutLater` method to use `requestAnimationFrame` instead of a timer with a magic 100 msec timeout value to improve rendering performance and stability. We have not detected any breakage with this change, but it should nonetheless be considered a **potentially breaking change**, as if your client-side code for whatever reason relies on the presence of that 100 msec timer between layout cycles, you may experience rendering instability.
  
  If this is the case, contact [Vaadin Support](https://support.vaadin.com/).
* Updated the license checker, which fixes an issue where licence checking could fail due to an SSL error.
* Vastly improved the ColorPicker widget, by improving the behavior of its history feature and made it render correctly on Valo-based themes.
* Improved ComboBox so that it no longer unnecessarily truncates the contents of the popup list.
* Improved ComboBox popup management. ComboBox should no longer cause constant reflows for updating the popup list position when no repositioning is necessary.
* Fixed an oversight in the eager UI closing feature of Vaadin Server, where the browser's Beacon API would be attempted to be used to signal to the server that the session can be closed and cleaned up. The original implementation assumed browsers detected as Chrome-based to support the Beacon API, but this was found to not be a correct assumption in testing, so Beacon API availability is now detected dynamically at runtime to avoid a late-stage JavaScript execution fault.
  
  Also made eager UI closing function on Firefox, which advertises Beacon API availability but does not actually send the message to the server. Firefox was special-cased to use the `beforeUnload` event instead.

## Vaadin 8.26.0

* Backported Binder fixes from Flow (pull requests [#18760](https://github.com/vaadin/flow/pull/18760), [#18770](https://github.com/vaadin/flow/pull/18770), [#18891](https://github.com/vaadin/flow/pull/18891), [#18833](https://github.com/vaadin/flow/pull/18833)). This also fixes an issue where data entry was being prohibited by required fields with no value assigned - with multiple empty required fields with input validation enabled on the same form, clicking on one of the required fields would result in loss of UI interactivity. Validation is now only run for changed fields, not an entire field group.
* Improved GridLayout layouting. Due the timing of internal measurements, GridLayout could finish its layouting logic prematurely and then fail to account for the size of its contents once the child components finished rendering, e.g. in situations where dynamically loaded styles are applied late.
* Fixed a ComboBox issue where ComboBox would open the wrong page in the dropdown option list, or prevent navigation in some cases. This fix was previously attempted in 8.18.1 but had to be reverted in 8.25.1 due to it preventing scrolling of the list as an unforeseen side effect. These side effects are now properly detected in continuous integration tests and should not recur.
* Updated client-side compilation to use GWT 2.11.0 for better Java 11 compatibility.

## Vaadin 8.25.2

* Defined Vaadin License Checker version as a variable in the root POM in order to fix OSGI packaging. The packaging change in 8.25.1 exposed this issue.
* Mitigated the performance overhead caused by the Grid changes in 8.25.0, namely the re-layouting of a Grid on scroll is now only performed when a ComponentRenderer is present.

## Vaadin 8.25.1

* Reverted a fix for a ComboBox issue where, if a filter was applied and the user opens the dropdown choice menu, the currently selected choice was not initially being scrolled into view. This fix had the side effect of making it impossible to scroll the dropdown menu in certain cases. The fix for the original issue will be re-implemented in a side-effect free manner in a future release.
* Fixed a packaging issue, where the Vaadin license checker and Vaadin Open were incorrectly being included as part of the vaadin-server artifact. This caused a cascade of classpath conflict warnings on startup. As far as we're aware, this problem appeared to be completely cosmetic but was causing concern for some users.
* Added a missing style rule for FormLayout's disabled caption opacity when using the light Valo theme.
* Updated the license checker version.

## Vaadin 8.25.0

* Added feature in Grid that allows setting the order of the hidable columns as presented in the Grid sidebar menu. The list can now be sorted either as `DEFAULT`, `ASCENDING` or `DESCENDING`. Default order is the same as the presentation order in the Grid. Ascending and descending ordering use the set title of each hidable column, sorted alphabetically.
  * The new API can be found in Grid - `grid.setSidebarColumnOrder(GridConstants.SidebarColumnOrder order)` and the corresponding getter `grid.getSidebarColumnOrder()`.
* Fixed edge-case issue [#12611](https://github.com/vaadin/framework/issues/12611) where a combination of a TabSheet inside of a FormLayout inside of a TabSheet would be rendered with 0px width.
* Added a workaround for Grid issue [#12608](https://github.com/vaadin/framework/issues/12608) where using a layout component containing multiple subcomponents inside a Grid cell with a `ComponentRenderer` would cause rendering to break.
  * This workaround schedules re-layouting of the Grid after each scroll event. As a result, minor slowdowns in complex Grids can occur in some cases.
  * If you are experiencing performance issues with your Grids after upgrading to Vaadin 8.25.0, please open a support ticket at [support.vaadin.com](https://support.vaadin.com/).
* Improved the documentation of the TestBench `TextFieldElement` class.

## Vaadin 8.24.0

* Removed support for Adobe Flash in the form of the Flash widget and Flash type support in Embed. Adobe Flash has not been supported at all in modern browsers since 2021, and has had several known security issues long before that, to the point that security auditing tools will now actively flag the dormant Flash support code in Vaadin Framework as "harmful".
* The Flash widget and the parts of the Embedded widget specifically providing Flash support are no longer present in Vaadin Framework, meaning that any software currently relying on the Flash widget will fail to compile.
  * The existing Flash widget and Embedded support is planned to be provided in the form of an add-on for those that need it, but at the time of the release of Vaadin 8.24.0 this add-on is not yet available.
  * If your application still relies on Flash support, please contact [support@vaadin.com](mailto:support@vaadin.com) or alternatively open a ticket at [support.vaadin.com](https://support.vaadin.com/).
* Removed a reference to ActiveXObject used in the bootstrap script, which was a workaround needed for Internet Explorer versions up to version 9. Support for Internet Explorer 9 finally ended on January 9, 2024 for Azure customers.
  * At this point, the only Internet Explorer version receiving any kind of support from Microsoft is Internet Explorer 11. Customers should be aware, though, that support for IE11 is only offered on a "best effort" basis, in that we will actively attempt to not break features that worked on IE11 in the past, but no new code or fixes are being built with IE11 or even tested against it.
    It should be noted that all support for Internet Explorer 11 as well as compatibility code for IE11 may be removed from Vaadin Framework after extended support for IE11 ends.
* Fixed a Drag & Drop issue that prevented dragging of certain widgets on some browsers and operating systems, but not on others. Widget dragging behavior should now be stable on all platforms.
  * See [issue #12604](https://github.com/vaadin/framework/issues/12604).
* Fixed scroll bar behavior on Firefox in several widgets.
  * See [issue #12605](https://github.com/vaadin/framework/issues/12605).
* Made sure DataProvider I/O streams are closed eagerly to avoid resource leaks. Framework cannot reliably detect which streams are affected, so all potentially susceptible streams are now handled using a try-with-resources pattern.
  * In applications that call the methods directly it's sufficient to use the pattern only with DataProviders that open I/O channels during data requests.
  * See [Flow issue #18551](https://github.com/vaadin/flow/issues/18551) and [Flow pull request #18552](https://github.com/vaadin/flow/pull/18552).
* Several improvements have been made to Binder:
  * Validation has been improved as follows
    * Once `Binder.handleFieldValueChange` runs for a binding when readBean was used, the whole binder will be silently validated also. BinderValidationStatusHandler is called like before (only contains status from changed binding), but StatusChangeEvent is now fired considering all bindings and if possible bean validators as well.
    * Once `Binder.handleFieldValueChange` runs for a binding when setBean was used, doWriteIfValid will validate all bindings, not only the changed ones. This prevents writing an invalid bean in cases where one or more of the initial values are in invalid state (but not marked as such since setBean resets validation status), but they have not been changed from their initial value(s).
    * Calling setAsRequiredEnabled with a changed value no longer triggers validation, since that validation is now handled elsewhere when needed as stated above.
    * It is now possible to check for changes for a specific binding via the `Binder.hasChanges` method. This is a backported feature from Flow.
    * See [Flow issue #17395](https://github.com/vaadin/flow/issues/17395) and [Flow pull request #17861](https://github.com/vaadin/flow/pull/17861).
  * It is now possible to only write the changed properties to a Bean through an overloaded `Binder.writeBean` method that now accepts an additional Collection parameter. This is a backported feature from Flow.
    * See [Flow issue #185383](https://github.com/vaadin/flow/issues/18583) and [Flow pull request #18636](https://github.com/vaadin/flow/pull/18636).
* Several internal tests were fixed for improved build stability.

## Vaadin 8.23.0

* Implemented eager UI cleanup through the Beacon API. Previously UIs would be cleaned up after several consequtive missed heartbeats. Now, closing the browser or tab or navigating away from the page sends a message to the server to notify it of such, allowing the UI to be destroyed immediately.
  This should result in lower server resource usage without any modifications to the software. However, this feature does come with some caveats:
  * This feature is not available for Internet Explorer clients. Our testing showed that Internet Explorer will report compatibility with the API, but fail to function as expected. As such, the feature is disabled for IE.
  * Vaadin 8 included a LegacyApplication class for Vaadin 6 compatibility that was a holdover from Vaadin 7 and should have been removed with the release of Vaadin 8. This feature may cause systems extending the LegacyApplication class to close and not reopen when the first client closes their window.
  If this is a problem for your application, please contact Vaadin Support.
  * Should you experience ANY abnormal behavior as it pertains to UI instance availability with this version of Vaadin but not with 8.22.0, please let us know by creating a support ticket.
* Improved stability of internal tests and build.
* Fixed JavaDoc generation and deployment to [vaadin.com/api](https://vaadin.com/api).

## Vaadin 8.22.0

* Added Read-Only mode support to Grid and Compatibility Grid.
  The Read-Only mode can be engaged using the *existing* API
  call `grid.setReadOnly(true)`. This mode disallows
  editing of the Grid, while still allowing scrolling. This was
  added as the previous way to disallow editing in an otherwise
  editable grid was to call `grid.setDisabled(true)`, but
  that would also stop users from scrolling through data.
* Added missing style class name strings in `ValoTheme`, to allow
  cleaner access to menu and navigation elements in the style.
  The new fields are
  * `MENU_SELECTED`
  * `MENU_TOGGLE`
  * `MENU_VISIBLE`
  * `MENU_ITEMS`
  * `MENU_USER`
  * `NAV_CONTENT`
  * `SCROLLABLE`
  
  See JavaDoc for usage descriptions. Previouly, access to these
  classes had to be done through magic strings in the application.
* Fixed an issue with `DateField` event propagation when the
  backend doesn't immediately service the request. Events would
  get queued and then sent stale and out of order, resulting in
  the server side getting false user interaction events from
  the `DateField`.
* Updated `jetty` dependency to address CVE-2023-36479.

## Vaadin 8.21.0

* Framework 8 builds are now made on Java 11. The resulting
  JARs are still fully compatible with Java 1.8 runtimes, but
  only Java 11 SDKs are supported for building Framework 8
  for the 8.21 series.

  Build-time compatibility with Java 17 SDKs is being
  investigated, but is not yet available.
  If you wish to **run** Vaadin 8 on Java 9+ JREs, you *must*
  set the Java environment value
  `java.locale.providers=COMPAT`, otherwise locale
  dependent conversions (country code, currency, etc) WILL be
  inconsistent with Java 8 behavior, potentially leading to
  data loss.

  Minimum Maven version to build Vaadin 8 is now **3.6.2**.
  Included new dependencies:

  * `javassist` version 3.29.2-GA
  * `maven-enforcer-plugin` 3.3.0
  
  Updated supporting Maven plugins:

  * `maven-clean-plugin` from 3.0.0 to 3.2.0
  * `maven-compiler-plugin` from 3.5.1 to 3.11.0
  * `maven-site-plugin` from 3.5 to 3.12.1
  * `maven-jar-plugin` from 2.6 to 3.2.2
  * `maven-surefire-plugin` from 2.19.1 to 2.22.2
  * `maven-failsafe-plugin` from 2.19.1 to 2.22.2
  * `maven-dependency-plugin` from 3.0.1 to 3.5.0
  * `exec-maven-plugin` from 1.6.0 to 3.1.0
  * `versions-maven-plugin` from 2.3 to 2.15.0
  * `build-helper-maven`-plugin 1.10 to 1.12
  * `maven-source-plugin` 3.0.1 to 3.2.1
  * `maven-checkstyle-plugin` from 3.2.0 to 3.2.2

* Upgraded GWT dependency to 2.9.0 in order to make Framework 8
  more compatible with other Vaadin products and modern build
  environments, as well as to improve compatibility with modern
  browsers.

* Fixed all JavaDoc generation errors and cleaned up some API
  documentation along the way, resulting in better IDE
  compatibility and cleaner formatting of the resulting
  documentation.

* Fixed a bug in the long polling push transport when the sync id
  check is disabled, leading to the server continuously pushing.
  This is a backported fix from Flow, see
  [issue #17237](https://github.com/vaadin/flow/issues/17237)
  [pull request #17238](https://github.com/vaadin/flow/pull/17238).

* Added new API in `VaadinSession.java` which allows
  setting priority of UIProviders. The function
  `VaadinSession.addUIProvider` now takes an extra integer
  parameter, which makes it possible to explicitly set priority
  of the UI providers as they're added.

  Additionally, the functions
  `VaadinSession.getUIProviderPriority` and
  `VaadinSession.setUIProviderPriority` were added, which
  can be used together with `VaadinSession.getUIProviders`
  to alter the ordering of all UI providers added to the Session.

  It is also possible to specify the priority of the default UI
  providers by setting the `UIPriority` value as part of
  the `DeploymentConfiguration`.
  
  This does not alter default behavior. The default `UIProvider`
  priority is 0; higher values get processed first. Providers with
  the same priority will be processed in the order they were added.

  See the VaadinSession JavaDoc for more information.

* Updated internal Jetty depdency from version `9.4.48.v20220622`
  to version `9.4.51.v20230217` to avoid a false positive
  security alert. The internal Jetty server is only used for
  running tests at build time.

* Updated plexus-archiver version in vaadin-maven-plugin to `4.8.0`
  in order to fix a potential security vulnerability.

## Vaadin 8.20.3

* Fixed an issue where compile-time license checking would fail on CI servers with release-only license files.
* Pinned `nimbus-jose-jwt` version in order to ensure that apps can still run under included Jetty on Java 8 VMs. Newer versions of `nimbus-jose-jwt` include a `meta-info` class that JVM 8 implementations cannot load. `nimbus-jose-jwt` was included as a transitive dependency.

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
