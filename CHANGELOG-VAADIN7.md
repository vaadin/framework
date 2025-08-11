# Vaadin 7 extended maintenance version changelog

## Vaadin 7.7.48

* Updated to Atmosphere 2.4.x, and consequently, Java 8. This bumps the minimum Java requirement from 7 to 8.
* Added changes to make interrupting uploads more reliable, and block a possible security issue in the process.

## Vaadin 7.7.47

* Fixed the text selection problems within Grid cells, so copying the contents of a single cell is possible
* Added a Jakarta-compatible Push module
* Fixed root module POM Source Code Management URLs to point to the correct repository

## Vaadin 7.7.46

* Updated maven plugin with updated dependencies
* Internal test fixes

## Vaadin 7.7.45

* Set no-store headers for error responses
* ComboBox popup size and positioning take into account the menu element’s own padding in addition to the menu item’s
* Internal changes, and a fix for a license checking issue

## Vaadin 7.7.44

* Internal changes needed for future development
* ComboBox popup position updates smoother when the page is scrolled

## Vaadin 7.7.43

* Made ComboBox rendering faster by trying to avoid constant popup size recalculations
* Backported Upload fixed from Vaadin 8, namely preventing upload if no file has been selected, and making sure the ChangeListener still works after the first upload.
* Added things necessary for JavaDoc generation.
* Updated the license checker, fixed tests, updated the copyright dates

## Vaadin 7.7.42

* Added read-only support to Grid. This allows disabling editing features in an otherwise editable Grid while still retaining scrolling. Use existing API method `Grid.setReadOnly(true)`.
* Enabled dependency convergence enforcement in Vaadin 7 to mitigate transitive dependency mismatches.
* Internal test fixes

## Vaadin 7.7.41

* fixed a regression which caused issues if the syncId check was disabled
* added the possibility to reorder UIProviders
* modified the Framework 7 project itself to compile with Java 11
* license updated to VCL-2
* fixed a version mismatch for the license checker being used
* test fixes

## Vaadin 7.7.40

* Fixed an issue where compile-time license checking would fail on CI servers with release-only license files.

## Vaadin 7.7.39

* Added the Flatten Maven Plugin
* A fix to compute the correct path to a war file from the resource URL
* General pom.xml and project structure fixes

## Vaadin 7.7.38

* A fix to preserve push messages in cache until they are seen by client
* A fix to prevent concurrent disconnect and push operations
* A change to close push connection immediately after refresh


## Vaadin 7.7.37

* license updated to VCL-1
* updated server-MPR artifact POM to remove dependency on server


## Vaadin 7.7.36

* added the server MPR build artifact
* updated license checker to version 1.11.2
* extracted portlet-related parts to a new module


## Vaadin 7.7.35

* a check for bundle resources in order not to fail with OSGi
* update license checker to version 1.9.0


## Vaadin 7.7.34

* Update Jsoup to version 1.15.3


## Vaadin 7.7.33

* Update license metainfo CVDL4 
* Update Jetty version for security
* Update license checker to version 1.5.2


## Vaadin 7.7.32

* Update Atmosphere version to 2.2.13.vaadin4

## Vaadin 7.7.31

* Don't serve directories as static files
* Update license checker version to 1.2.2
* Add JNA dependencies for license checker

## Vaadin 7.7.30

* Ensure resize and dragging curtains are removed when a window is closed
