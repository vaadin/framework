Automatic test for Karaf deployment
===

What is tested
---
* Multiple applications are deployed and work properly
* Default widgetset works
* Custom widgetset works
* Custom theme works

Tips and Tricks
---
* Karaf is downloaded and deployed by maven karaf plugin
* Karaf is run as a background process using maven `exec:exec` goal
* Karaf is shut down using maven `karaf:client` goal during `post-integration-test` phase
* All required karaf features and project bundles are deployed using maven karaf plugin with `client` goal

Running and stopping karaf manually
---
* To start karaf as a foreground process and deploy both vaadin bundles and two existing applications, run `mvn  -f karaf-run-pom.xml clean karaf:run`
in `karaf-run` module
* To start karaf as a background process and deploy both vaadin bundles and two existing applications, run `mvn clean pre-integration-test`
in `karaf-run` module
* Karaf console is available via ssh at `127.0.0.0:8101` port, username/password is `karaf/karaf` 
* To stop background karaf process, run `mvn karaf:client@karaf-client-shutdown`
in `karaf-run` module

Potential problems
---
* Maven executable should be in `PATH`
* Background karaf process may be left running for 10 minutes if the build fails prior `integration-test` phase.
