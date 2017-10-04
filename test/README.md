# Vaadin Test

This is a test project to verify the basic functionality of various related plugins and addons.

## General use

To validate a given Vaadin Version, run `mvn clean verify -Dvaadin.version=VERSIONNUMBER`.

## Server testing 

To validate only server compatibility, run `mvn clean verify -Dvaadin.version=VERSIONNUMBER` in the servlet-containers module.

To test only a subset of the servers, there are profiles with the server name. For example `-P wildfly` runs all Wildfly tests, `-P glassfish,jetty` runs all the Glassfish and Jetty tests.

## Running an individual test locally

To install dependencies for testing an individual module locally, run `mvn clean install -Dintall.skip=false` in the dependency module.

When running against a remote WebDriver to test servers, use `-Ddeployment.url=http://<yourip>:8080` on the Maven command line. To test with local WebDriver, use `-DuseLocalWebDriver`.
