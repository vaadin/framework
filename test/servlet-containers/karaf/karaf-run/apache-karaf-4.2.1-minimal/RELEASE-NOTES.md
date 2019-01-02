<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

## Apache Karaf 4.2.1

Apache Karaf 4.2.1 is a major update on the 4.2.x series. It brings bunch of fixes, dependencies updates
and new features, especially:

* new assembly tooling to create Karaf Docker images
* new Docker feature allowing you to manipulate Docker directly from a Karaf instance
* Better Java 9/10/11 support
* new examples directly as part of the Karaf distribution
* improved KarafTestSupport allowing you to easily implement your itests

### ChangeLog

#### Bug
    * [KARAF-4996] - Missing packages in created instances 
    * [KARAF-5422] - Feature Repository with Spaces in Path
    * [KARAF-5683] - Completion is "weird" on Windows 8
    * [KARAF-5689] - Console is broken after Ctrl+C
    * [KARAF-5690] - Add missing jaxb endorsed / osgi classes
    * [KARAF-5692] - Alias not honoured in config:edit --factory --alias
    * [KARAF-5694] - strip url to ensure it's a valid one which could download
    * [KARAF-5695] - Starting Karaf Container 4.2.0 in Ubuntu 17.10 with OpenJDK 9 fails
    * [KARAF-5696] - Java detection is broken on windows
    * [KARAF-5697] - feature:start and feature:stop should be able to select multiple features
    * [KARAF-5699] - Upgrade to jolokia 1.5.0
    * [KARAF-5701] - feature installation: Crash and ResolutionException
    * [KARAF-5705] - Java 10 issues with jetty
    * [KARAF-5729] - Karaf won't start on Solaris 11 and AIX 7.2
    * [KARAF-5748] - Command results are not printed anymore unless they are strings
    * [KARAF-5749] - Possible shell crash when executing malformed script
    * [KARAF-5750] - Karaf console not calling Converter for custom gogo commands
    * [KARAF-5753] - Karaf won't start correctly on HP-UX
    * [KARAF-5760] - VerifyMojo should allow blacklisting feature repositories
    * [KARAF-5765] - karaf-service script not working on HP-UX
    * [KARAF-5768] - karaf-service script not working on AIX platforms
    * [KARAF-5781] - Properties edit doesn't conserve the existing ones
    * [KARAF-5791] - need to check the blacklist when we add feature repo through JMX
    * [KARAF-5798] - Karaf slave instance does not write pid or port file until it becomes master
    * [KARAF-5809] - 'simple' host.key files no longer work
    * [KARAF-5840] - Karaf specs activator is missing when used with wrapper
    * [KARAF-5842] - Console unusable in docker
    * [KARAF-5850] - JPA features should provide the engine capability
    * [KARAF-5851] - Remove heading spaces in the cfg files
    * [KARAF-5862] - org.apache.karaf.specs.java.xml doesn't work with IBM JDK

#### New Feature
    * [KARAF-5761] - Print better usage of commands in karaf shell
    * [KARAF-5867] - Provide openjpa 3.0.0 support
    * [KARAF-5870] - Upgrade to Hibernate Validator 6.0.12.Final
    * [KARAF-5871] - Upgrade to ASM 6.2.1
    * [KARAF-5872] - Upgrade to Spring 5.0.8.RELEASE

#### Improvement
    * [KARAF-3235] - Provide karaf itest common bundle
    * [KARAF-5363] - Add --no-start option to kar:install, kar cfg and kar MBean
    * [KARAF-5644] - Add docker feature
    * [KARAF-5685] - Add ProfileMBean
    * [KARAF-5700] - handle \* scope specifically for ACL match
    * [KARAF-5706] - Upgrade to Felix Utils 1.11.0
    * [KARAF-5742] - Possibility to configure colors for karaf shell
    * [KARAF-5752] - Add bundle ID in bundle:classes output
    * [KARAF-5759] - Add an option to config:list to list only the PIDs
    * [KARAF-5778] - NPE in the ssh client if TERM is null
    * [KARAF-5787] - Improve scheduler whiteboard to avoid ClassCastException
    * [KARAF-5796] - Heap dump needs to end in .hprof w/ newer JDK
    * [KARAF-5804] - FastDateFormatTest fails on EDT (jdk 1.8.0_151) 
    * [KARAF-5805] - Add feature required item field to JmxFeature CompositeData
    * [KARAF-5839] - Add assertServiceAvailable() in KarafTestSupport
    * [KARAF-5847] - org.apache.felix.coordinator could be installed with configadmin, to prevent its refreshes
    * [KARAF-5868] - be able to remove properties during distribution assembly

#### Test
    * [KARAF-5845] - JMXSecurityTest.testJMXSecurityAsManager is flaky
    * [KARAF-5846] - ConfigManagedServiceFactoryTest.updateProperties is flacky

#### Task
    * [KARAF-2511] - Review and update documentation
    * [KARAF-5764] - ensure we can build and run Karaf master with JDK11

#### Dependency upgrade
    * [KARAF-5698] - Upgrade to Felix Gogo Runtime / JLine 1.0.12
    * [KARAF-5710] - Upgrade to Felix Resolver 1.16.0
    * [KARAF-5713] - Upgrade to Maven API 3.5.3
    * [KARAF-5714] - Upgrade to ServiceMix Spec Locator 2.10
    * [KARAF-5715] - Upgrade to XBean 4.8
    * [KARAF-5716] - Upgrade to awaitability 3.1.0
    * [KARAF-5717] - Upgrade to easymock 3.6
    * [KARAF-5718] - Upgrade to Equinox 3.12.100
    * [KARAF-5719] - Upgrade to Jansi 1.17.1
    * [KARAF-5720] - Upgrade to JLine 3.7.1
    * [KARAF-5721] - Upgrade to Pax JMS 1.0.1
    * [KARAF-5722] - Upgrade to ASM 6.2 & Aries Proxy Impl 1.1.2
    * [KARAF-5723] - Upgrade to Pax JDBC 1.3.0
    * [KARAF-5726] - Upgrade to Aries Proxy version Java 10 compliant
    * [KARAF-5728] - Upgrade to Pax Web 7.1.1 & Jetty 9.4.10.v20180503
    * [KARAF-5732] - Upgrade to Felix ConfigAdmin 1.9.0
    * [KARAF-5733] - Upgrade to Felix EventAdmin 1.5.0
    * [KARAF-5734] - Upgrade to Felix Metatype 1.2.0
    * [KARAF-5735] - Upgrade to Felix SCR 2.1.0
    * [KARAF-5736] - Upgrade to Narayana 5.8.1.Final
    * [KARAF-5737] - Upgrade to Aries JPA 2.7.0
    * [KARAF-5738] - Upgrade to maven-resources-plugin 3.1.0
    * [KARAF-5745] - Upgrade to Spring 5.0.5.RELEASE
    * [KARAF-5758] - Update to Hibernate Validator 6.0.10.Final
    * [KARAF-5766] - Upgrade to Felix Connect 0.2.0
    * [KARAF-5771] - Upgrade to Pax Transx 0.3.0
    * [KARAF-5779] - Upgrade to Spring 4.3.17.RELEASE and 5.0.6.RELEASE
    * [KARAF-5800] - Upgrade to Felix Gogo 1.1.0
    * [KARAF-5807] - Upgrade to Pax Exam 4.12.0
    * [KARAF-5812] - Upgrade to Spring 4.3.18.RELEASE
    * [KARAF-5813] - Upgrade to Spring 5.0.7.RELEASE
    * [KARAF-5815] - Upgrade to commons-compress 1.17
    * [KARAF-5816] - Upgrade to Aries Transaction Blueprint 2.2.0
    * [KARAF-5817] - Upgrade to maven-bundle-plugin 3.5.1
    * [KARAF-5818] - Upgrade to Felix ConfigAdmin 1.9.2
    * [KARAF-5821] - Upgrade to Maven API 3.5.4
    * [KARAF-5822] - Upgrade to Maven Wagon 3.1.0
    * [KARAF-5824] - Upgrade to awaitility 3.1.1
    * [KARAF-5826] - Upgrade to narayana 5.9.0.Final
    * [KARAF-5827] - Upgrade to jline 3.9.0
    * [KARAF-5829] - Upgrade to Xerces 2.12.0
    * [KARAF-5830] - Upgrade to tagsoup 1.2.1
    * [KARAF-5831] - Upgrade to maven-enforcer-plugin 3.0.0-M2
    * [KARAF-5832] - Upgrade to maven-jar-plugin 3.1.0
    * [KARAF-5833] - Upgrade to maven-project-info-reports-plugin 3.0.0
    * [KARAF-5834] - Upgrade to maven-site-plugin 3.7.1
    * [KARAF-5835] - Upgrade to maven-surefire-plugin 2.22.0
    * [KARAF-5836] - Upgrade to maven-war-plugin 3.2.2
    * [KARAF-5837] - Upgrade to maven-jacoco-plugin 0.8.1
    * [KARAF-5838] - Upgrade to eclipselink 2.7.2
    * [KARAF-5841] - Upgrade to Pax Web 7.2.1
    * [KARAF-5849] - Upgrade to Pax Transx 0.4.0
    * [KARAF-5856] - Upgrade to Pax Web 7.2.2
    * [KARAF-5857] - Upgrade to maven-compiler-plugin 3.8.0
    * [KARAF-5858] - Upgrade to Felix ConfigAdmin 1.9.4
    * [KARAF-5859] - Upgrade to Hibernate Validator 6.0.11.Final
    * [KARAF-5861] - Upgrade to Pax Web 7.2.3 / Jetty 9.4.11.v20180605
    * [KARAF-5865] - Upgrade to eclipselink 2.7.3
    * [KARAF-5866] - Upgrade to Felix SCR 2.1.2
    * [KARAF-5869] - Upgrade to awaitility 3.1.2

## Apache Karaf 4.2.0

Apache Karaf 4.2.0 is the first GA release on the 4.2.x series. We encourage all users to upgrade to this
 new stable series, bringing a lot of fixes, improvements and new features.

### ChangeLog

#### Bug
    * [KARAF-5342] - No reference to branding-ssh.properties in console branding section
    * [KARAF-5384] - Optional dependencies in MINA SSHD Core cause system bundle refreshes
    * [KARAF-5473] - Karaf SSH session timing out
    * [KARAF-5554] - the karaf.secured.command.compulsory.roles shouldn't apply for alias commands
    * [KARAF-5559] - log:tail kills ssh & karaf when root logger is in DEBUG
    * [KARAF-5563] - Enf-of-line display problem with the ShellTable on windows
    * [KARAF-5566] - Features installed through prerequisites lead to errors when uninstalling features
    * [KARAF-5569] - Cannot pass commands to client script when sftpEnabled=false
    * [KARAF-5573] - Karaf on Windows does not pass the version check when JAVA_HOME contains whitespace
    * [KARAF-5581] - bin/client -u karaf -p karaf can login if we enable jasypt for jaas 
    * [KARAF-5585] - Verify mojo configure pax-url-mvn with non existent settings.xml
    * [KARAF-5591] - Blacklisted features should be considered as dependencies and/or conditionals
    * [KARAF-5592] - Karaf shell unexpected exit when Ctrl + C during log:display or select text then press Enter
    * [KARAF-5610] - Build problems with JDK9
    * [KARAF-5611] - karaf.bat still uses endorsed dirs with Java 9 install
    * [KARAF-5634] - karaf/karaf.bat scripts do not handle lib.next->lib update correctly
    * [KARAF-5639] - NPE during instance:start
    * [KARAF-5641] - Karaf boot scripts need to deal with JDK10 version patterns
    * [KARAF-5642] - karaf:deploy goal broken
    * [KARAF-5645] - Karaf crashes when using the character ']' in the console
    * [KARAF-5646] - Support env:XXX subtitution missing for system.properties
    * [KARAF-5647] - start, stop, shell, status and client fail on Solaris Sparc 11
    * [KARAF-5657] - client.bat doesn't work on Windows
    * [KARAF-5667] - Installing the audit-log feature never ends
    * [KARAF-5670] - pax-web throws an exception when running with a security manager
    * [KARAF-5671] - Demo profiles still use "old style" pax-logging configuration
    * [KARAF-5672] - Servlets urls are displayed without the http context path
    * [KARAF-5673] - karaf-maven-plugin can be very long to apply profile
    * [KARAF-5678] - Existing configfiles (in kar) may be overwritten when building assembly
    * [KARAF-5688] - XML parsing fails when xerces is installed on JDK 8

#### New Feature
    * [KARAF-1677] - Unpacked KAR deployment
    * [KARAF-5614] - Add HttpRedirect/Proxy service with http:redirect/proxy command & MBean
    * [KARAF-5629] - Add new karaf commands shell:elif and shell:else
    * [KARAF-5635] - Integrate WebConsole Memory Usage plugin
    * [KARAF-5665] - Sometimes the command description does not show when listing commands with "TAB"
    * [KARAF-5680] - Provide support for xml parsers deployed as bundle on Java 9

#### Improvement
    * [KARAF-2688] - Karaf info - Add memory details about perm gen pool
    * [KARAF-4496] - UserPrincipal lookup in the JAAS' BackingEngine
    * [KARAF-5448] - Fix Java 9 warnings
    * [KARAF-5558] - Be able to configure the Quartz Scheduler
    * [KARAF-5568] - Karaf Commands cannot have return codes
    * [KARAF-5578] - Add repo URL for sling
    * [KARAF-5588] - Increase max number of threads in the scheduler by default
    * [KARAF-5604] - karaf:features-generate-descriptor takes long when faced with complex feature dependencies
    * [KARAF-5627] - Upgrade to PAX-JMS 0.3.0
    * [KARAF-5677] - deploy goal throws NPE with artifactLocations is not provided
    * [KARAF-5679] - Upgrade to Hibernate Validator 6.0.9.Final

#### Task
    * [KARAF-5586] - Upgrade to Hibernate-validator 5.4.2

#### Dependency upgrade
    * [KARAF-5574] - Upgrade to Pax Web 7.0.0/Jetty 9.4.6
    * [KARAF-5584] - Upgrade to SSHD 1.7.0
    * [KARAF-5595] - Upgrade toJLine 3.6.0 and Jansi 1.17
    * [KARAF-5596] - Upgrade to Spring 5.0.3.RELEASE
    * [KARAF-5597] - Upgrade to Spring 4.3.14.RELEASE
    * [KARAF-5599] - Upgrade Narayana to version 5.7.2.Final
    * [KARAF-5602] - Upgrade to Spring Security 4.2.4.RELEASE
    * [KARAF-5605] - Upgrade to OpenJPA 2.4.2
    * [KARAF-5606] - Upgrade to EclipseLink 2.7.1
    * [KARAF-5607] - Upgrade to Hibernate 5.2.9.Final
    * [KARAF-5612] - Upgrade to blueprint-core 1.9.0, blueprint-cm-1.2.0, blueprint-spring-0.6.0 and blueprint-spring-extender-0.4.0
    * [KARAF-5616] - Upgrade to SCR 2.0.14
    * [KARAF-5617] - Upgrade to JNA 4.5.1
    * [KARAF-5618] - Upgrade to Aries JMX Blueprint 1.2.0
    * [KARAF-5619] - Upgrade to Aries JMX Core 1.1.8 & JMX Whiteboard 1.2.0
    * [KARAF-5622] - Upgrade to commons-compress 1.16.1
    * [KARAF-5623] - Upgrade to maven-bundle-plugin 3.5.0
    * [KARAF-5624] - Upgrade to jline 3.6.1
    * [KARAF-5625] - Upgrade to Pax Swissbox 1.8.3
    * [KARAF-5631] - Upgrade to PAX-CDI 1.0.0
    * [KARAF-5658] - Upgrade to Spring 5.0.4.RELEASE
    * [KARAF-5668] - Upgrade to JLine 3.6.2
    * [KARAF-5675] - Upgrade to XBean 4.7

## Apache Karaf 4.2.0.M2

 Apache Karaf 4.2.0.M2 is a the second technical preview of the 4.2.x series. It's not yet a GA release. It
 brings a lot of improvements and new features, in preparation for the first 4.2.0 GA release.

### ChangeLog

#### Bug
    * [KARAF-2792] - shared cm-properties empty for second bundle
    * [KARAF-3875] - Karaf scheduler should wrap QuartzException in exported SchedulerException
    * [KARAF-3976] - Broken compatibility with 3.x jdbc DataSources
    * [KARAF-4181] - blacklist.properties and overrides.properties are not properties file
    * [KARAF-4662] - Unable to create Karaf Cave 4.0.0 Kar file
    * [KARAF-4684] - karaf-maven-plugin assembly goal fails to find nested features with explicit version containing qualifier
    * [KARAF-4912] - Cannot register Servlet via http-whiteboard under Java 9
    * [KARAF-5203] - KAR:Create missing bundles that are marked conditional
    * [KARAF-5210] - Seemingly random NPEs from Aether resolver
    * [KARAF-5372] - startup.properties doesn't respect overrides
    * [KARAF-5446] - Fragment bundles are not resolved properly when installing/restarting the container
    * [KARAF-5452] - [SCR] Karaf can't activate/deactivate SCR components via JMX
    * [KARAF-5455] - remove redundant sshRole comment 
    * [KARAF-5458] - karaf-maven-plugin fails to assemble artifacts if only available within local reactor
    * [KARAF-5461] - incorrect filter in EncryptionSupport of jaas modules
    * [KARAF-5464] - karaf.bat file is missing KARAF_SYSTEM_OPTS property
    * [KARAF-5466] - Karaf does not start on JDK 9.0.1
    * [KARAF-5467] - Karaf doesn't recognize Java 9 on Ubuntu 16.04
    * [KARAF-5470] - Karaf fails build with Java 9.0.1
    * [KARAF-5472] - Karaf RmiRegistryFactory throws a warning with Java 9
    * [KARAF-5478] - Provide a Version class to check Karaf version used.
    * [KARAF-5480] - The webconsole gogo plugin is broken
    * [KARAF-5495] - Upgrade SyncopeBackingEngineFactory to support Syncope 2.x
    * [KARAF-5496] - NPEs in SyncopeLoginModule if "version" is not specified
    * [KARAF-5498] - SyncopeLoginModule parses roles instead of groups for Syncope 2.0.x
    * [KARAF-5505] - Jetty version out of date
    * [KARAF-5508] - Error using OSGi JAX RS Connector in Java 9
    * [KARAF-5527] - the karaf.secured.command.compulsory.roles should only affect command ACL rules
    * [KARAF-5528] - Karaf feature deployer should stop refreshed bundles together with the updated ones
    * [KARAF-5533] - KarArtifactInstaller does not properly detect already installed KAR files
    * [KARAF-5541] - ensure check the compulsory.roles even there's no ACL for a specific command scope
    * [KARAF-5542] - Installing a feature triggers restarting previous ones
    * [KARAF-5546] - incorrect acl rules for system:start-level
    * [KARAF-5547] - Blueprint namespace handlers cause warning to be printed

#### Dependency upgrade
    * [KARAF-5412] - Upgrade to ASM 6.0
    * [KARAF-5488] - Upgrade to Felix Framework 5.6.10
    * [KARAF-5489] - Upgrade to commons-io 2.6
    * [KARAF-5490] - Upgrade to JNA 4.5.0
    * [KARAF-5491] - Upgrade to commons-compress 1.15
    * [KARAF-5516] - Upgrade to commons-lang3 3.7
    * [KARAF-5517] - Upgrade to Apache Felix Metatype 1.1.6
    * [KARAF-5518] - Upgrade to Apache Felix WebConsole DS plugin 2.0.8
    * [KARAF-5519] - Upgrade to Apache Felix WebConsole EventAdmin plugin 1.1.8
    * [KARAF-5520] - Upgrade to Maven dependencies 3.5.2
    * [KARAF-5521] - Upgrade to Maven Wagon 3.0.0
    * [KARAF-5522] - Upgrade to easymock 3.5.1
    * [KARAF-5523] - Upgrade to Equinox 3.12.50
    * [KARAF-5524] - Upgrade to maven-dependency-tree 3.0.1
    * [KARAF-5525] - Upgrade to PAX tinybundle 3.0.0 
    * [KARAF-5531] - Upgrade to maven-compiler-plugin 3.7.0
    * [KARAF-5532] - Upgrade to maven-dependency-plugin 3.0.2
    * [KARAF-5535] - Upgrade to maven-javadoc-plugin 3.0.0
    * [KARAF-5536] - Upgrade to maven-war-plugin 3.2.0
    * [KARAF-5537] - Upgrade to modello-maven-plugin 1.9.1
    * [KARAF-5538] - Upgrade to maven-invoker-plugin 3.0.1
    * [KARAF-5539] - Upgrade to maven-archetype-plugin 3.0.1
    * [KARAF-5549] - Upgrade to JLine 3.5.4
    * [KARAF-5550] - Upgrade to pax-url 2.5.4
    * [KARAF-5551] - Upgrade to Pax Web 6.1.0

#### Improvement
    * [KARAF-3674] - Document and improve scheduler feature
    * [KARAF-4329] - Consider bundles from override.properties while creating the assembly
    * [KARAF-5273] - karaf-maven-plugin assembly should take feature wildcards
    * [KARAF-5323] - Set multi-location for created configurations
    * [KARAF-5339] - Allow to define blacklisted bundles in a profile
    * [KARAF-5418] - SSH public key authentication from LDAP
    * [KARAF-5448] - Fix Java 9 warnings
    * [KARAF-5456] - introduce a property karaf.shell.history.file.maxSize to configure the history file size on disk
    * [KARAF-5476] - Reduce number of logins when using the webconsole
    * [KARAF-5486] - Add a command to change job scheduling
    * [KARAF-5494] - Fix performance issue generating service metadata, change logging
    * [KARAF-5506] - ensure we also check the ACL for alias cmds before auto-completer
    * [KARAF-5511] - Proper Provide-Capability for org.apache.karaf.jaas.modules.EncryptionService
    * [KARAF-5529] - Rewrite SCR management layer to more closely follow the real object model
    * [KARAF-5544] - Provide bundle consistency report from custom Karaf distribution
    * [KARAF-5548] - Improve the find-class command to support package names

#### New Feature
    * [KARAF-5307] - Add SchedulerMBean to mimic scheduler shell commands
    * [KARAF-5447] - Support Spring 5.0.x
    * [KARAF-5475] - Provide a security audit log
    * [KARAF-5485] - Be able to disable the sftp server

#### Proposal
    * [KARAF-5376] - Processor mechanism for feature definitions (a.k.a. "better overrides")

#### Task
    * [KARAF-5468] - Clean up AssemblyMojo

## Apache Karaf 4.2.0.M1

 Apache Karaf 4.2.0.M1 is a technical preview of the 4.2.x series. It's not yet a GA release. It
 brings a lot of improvements and new features, including Java 9 support.

### ChangeLog

#### Bug
    * [KARAF-3347] - 'LATEST' placeholder is not resolved correctly for descriptors and repositories
    * [KARAF-3429] - always use proxy server listed in maven settings.xml when installing features
    * [KARAF-3531] - SimpleMavenResolver does not handle wrap: prefix in mvn urls
    * [KARAF-3875] - Karaf scheduler should wrap QuartzException in exported SchedulerException
    * [KARAF-4174] - NullPointerException when running obr:info on a bundle served by cave
    * [KARAF-4380] - Remove blueprint feature in standard distribution
    * [KARAF-4490] - LDAPLoginModule use authentication to check user password
    * [KARAF-4603] - Nashorn support in Karaf
    * [KARAF-4655] - karaf-maven-plugin add-features-to-repo goal can't add Camel feature
    * [KARAF-4985] - Karaf does not start with JDK 9 in Windows 
    * [KARAF-4988] - Refreshing a feature repository from webconsole fails
    * [KARAF-5031] - Subshell doesn't show in prompt
    * [KARAF-5051] - Command "shell wrapper:install" fails
    * [KARAF-5073] - OpenSSHGeneratorFileKeyProvider is unable to write SSH keys
    * [KARAF-5078] - Shell crash
    * [KARAF-5091] - log:get does not show correct level
    * [KARAF-5094] - Remove -server option in Karaf scripts
    * [KARAF-5096] - Karaf 4.1.1 Console Issues Over SSH (PuTTY)
    * [KARAF-5103] - Quick start fails at the step "feature:install camel-spring"
    * [KARAF-5105] - Issue with bin/shell command in karaf 4.1.1
    * [KARAF-5106] - karaf-maven-plugin hangs the build (probably when having cyclic deps in the features def)
    * [KARAF-5109] - endorsed and ext directories are not set properly when using instance start
    * [KARAF-5115] - Error while installing cxf
    * [KARAF-5116] - Defining karaf.log.console as a log4j2 log level causes exceptions
    * [KARAF-5119] - log:tail on OSX does not display updates without user input and exits shell on ctrl + c
    * [KARAF-5120] - etc/org.apache.karaf.shell.cfg is "raw", all comments are lost in the distribution
    * [KARAF-5121] - blueprint created by jms:create is not correct
    * [KARAF-5123] - Executing feature:repo-remove can leave karaf in an invalid state
    * [KARAF-5124] - NPE when location information is included in console logging pattern
    * [KARAF-5128] - Upgrade to aries.proxy 1.1.1
    * [KARAF-5134] - Instance org.apache.karaf.features.cfg refers to 4.1.1-SNAPSHOT
    * [KARAF-5138] - CTRL-D on a connected instance exits from the root one
    * [KARAF-5143] - Command cannot be executed via SSH when property "karaf.shell.init.script" (etc/system.properties) has its default value
    * [KARAF-5144] - java.lang.RuntimeException: Command name evaluates to null: $.jline.terminal
    * [KARAF-5147] - Upgrade to pax-web-6.0.4
    * [KARAF-5164] - karaf-maven-plugin fails to verify artifacts if only available within local reactor
    * [KARAF-5165] - Custom Distributions: Pax-Web gets installed twice
    * [KARAF-5167] - Instance etc folder is not sync automatically
    * [KARAF-5171] - Upgrade to ServiceMix Specs 2.9.0
    * [KARAF-5174] - Uninstalling feature using liquibase-slf4j crashes karaf
    * [KARAF-5176] - Fix support for characters entered while executing a command
    * [KARAF-5179] - Setting the karaf.restart.jvm property to true causes system halt commands to behave as reboots
    * [KARAF-5180] - The framework is restarted and sometimes spits an exception when refreshing a fragment
    * [KARAF-5181] - NPE while running "threads --tree" command from console
    * [KARAF-5182] - Console command log:list returns "null"
    * [KARAF-5184] - ClassLoader leak when org.apache.karaf.shell.core bundle is refreshed
    * [KARAF-5196] - Strongly consider removing -XX:+UnsyncloadClass from start scripts
    * [KARAF-5197] - Features deployed from a KAR file do not respect the feature's install setting
    * [KARAF-5199] - Karaf installs both version of the feature (old and new) in case if referencing feature contains wrapped bundle with package import
    * [KARAF-5206] - Karaf doesn't start after not clean reboot, because stored PID corresponds to running process
    * [KARAF-5207] - Features 1.4 namespace not supported by the features deployer
    * [KARAF-5211] - NPE in StoredWiringResolver if BundleEvent.UNRESOLVED handled before BundleEvent.RESOLVED event
    * [KARAF-5216] - Exiting karaf shell, mess the bash shell
    * [KARAF-5218] - bin/client exists when typing CTRL-C
    * [KARAF-5221] - karaf-maven-plugin's pidsToExtract handled incorrectly
    * [KARAF-5223] - "Error in initialization script" messages printed to the main console when clients connect through ssh
    * [KARAF-5229] - The download manager may generate wrong jar with custom urls
    * [KARAF-5234] - Update BUILDING file to reference Java 8
    * [KARAF-5245] - Running karaf.bat inside a "Program Files (x86)" directory
    * [KARAF-5247] - java.lang.InterruptedException after logout command in shell
    * [KARAF-5250] - SNAPSHOT metadata doesn't match SNAPSHOT artifacts after mvn deploy
    * [KARAF-5252] - Upgrade Narayana to version 5.6.3.Final
    * [KARAF-5255] - Upgrade to pax-web-6.0.6
    * [KARAF-5259] - Duplicate log entries displayed when using log:tail
    * [KARAF-5260] - log:tail default should start at the end of the file
    * [KARAF-5264] - Clean up maven dependencies
    * [KARAF-5267] - Karaf does not work correctly after log:tail
    * [KARAF-5271] - Improve JDBC generic lock to better support network glitches
    * [KARAF-5276] - Do not use right prompt by default
    * [KARAF-5279] - InterruptedException when updating the shell.core bundle
    * [KARAF-5283] - Karaf in offline (no internet) environment - NamespaceHandler bugs
    * [KARAF-5298] - config:update doesn't create the cfg file in the etc folder
    * [KARAF-5304] - checkRootInstance function in karaf script fails under AIX
    * [KARAF-5305] - FeatureConfigInstaller writes incorrect config if append=true and file already exists
    * [KARAF-5311] - NPE in karaf-maven-plugin when specifying descriptor by file url
    * [KARAF-5312] - bin/stop script output some unwanted message on mac
    * [KARAF-5313] - Exception when deleting a .cfg file from hot deploy directory 
    * [KARAF-5314] - The performance of profile builder used by karaf maven plugin has reduced significantly in 4.1 compared to 4.0
    * [KARAF-5315] - Race condition during shutdown using SIGTERM
    * [KARAF-5317] - "Exception in thread "SIGWINCH handler" java.lang.UnsupportedOperationException" occurs when resizing the console while `log:tail` is run
    * [KARAF-5320] - Karaf Command Arguments escapes backslash characters
    * [KARAF-5326] - variables in cfg files are expanded
    * [KARAF-5327] - Threads not stopped on karaf.restart + bundle(0).stop()
    * [KARAF-5328] - NPE is thrown when execute source command from client/ssh
    * [KARAF-5330] - Require a specific role to access the SSH console
    * [KARAF-5331] - Use shell command access control lists during command completion
    * [KARAF-5332] - bin/stop script fails when KARAF_DEBUG is set
    * [KARAF-5333] -  UnsupportedCharsetException: cp65001 and unprintable characters from karaf 4.1.2 console
    * [KARAF-5334] - Fix broken shell.support.table.ShellTableTest on Windows
    * [KARAF-5337] - karaf-maven-plugin generates an "override.properties" instead of "overrides.properties"
    * [KARAF-5338] - Unable to access the local JMX server on OSX
    * [KARAF-5340] - A "Set<LocalDependency>" cannot contain a "Artifact" in Dependency31Helper
    * [KARAF-5343] - Upgrade to pax-web-6.0.7
    * [KARAF-5344] - Remote shell *really* doesn't like you resizing the console window
    * [KARAF-5352] - KARAF_ETC envvar ignored
    * [KARAF-5355] - The scripts triggered with {{scheduler::schedule}} command fail to execute
    * [KARAF-5361] - shell:watch is broken
    * [KARAF-5371] - Race condition between FeatureService and Fileinstall
    * [KARAF-5373] - Karaf-maven-plugin fails to create feature file
    * [KARAF-5374] - karaf-maven-plugin can't configure the start-level for the startupBundles
    * [KARAF-5375] - feature:stop command does not stop the bundles
    * [KARAF-5377] - Speed up repository loading
    * [KARAF-5382] - Karaf shell session.readLine consumes backslashes
    * [KARAF-5385] - shutdown -f command can't exit the karaf
    * [KARAF-5387] - Build fail on JLineTerminal
    * [KARAF-5388] - create dump doesn't include log file anymore
    * [KARAF-5390] - tar.gz archives contains invalid data in demos\web\src\main\webapp\WEB-INF\karaf\system\org\apache\felix
    * [KARAF-5394] - maven-metadata-local.xml in KARs cause SAXParseException
    * [KARAF-5395] - Improve memory consumption during resolution
    * [KARAF-5398] - The "cd" command should not attempt to complete multiple directories
    * [KARAF-5404] - CLI autocompletion issue
    * [KARAF-5406] - CLI error on window resize on Linux(Wayland)
    * [KARAF-5411] - Client doesn't prompt for user if no user.properties file
    * [KARAF-5413] - Missing explicit version in features
    * [KARAF-5414] - Features mentioned in feature.xml stubs aren't taken into account in dependency calculations
    * [KARAF-5420] - Bad console behavior when dealing with the input stream with the exec command
    * [KARAF-5423] - Karaf is flagged as vulnerable to CVE-2015-5262
    * [KARAF-5425] - ArrayIndexOutOfBoundsException running history | grep
    * [KARAF-5435] - BundleException when installing a bundle by API when the FeatureService install a feature
    * [KARAF-5436] - Factory configurations file in etc/ are not deleted when the configuration is deleted
    * [KARAF-5440] - No override facility for properties in system.properties

#### Dependency
    * [KARAF-5345] - Upgrade to pax-jms-0.1.0 and ActiveMQ 5.15.0

#### Dependency upgrade
    * [KARAF-4921] - Upgrade to pax-logging 1.10.0
    * [KARAF-4991] - Upgrade to Narayana 5.5.2.Final
    * [KARAF-5085] - Upgrade to Aries JPA 2.6.1
    * [KARAF-5087] - Upgrade to Spring 4.3.7.RELEASE
    * [KARAF-5090] - Update equinox to 3.11.3
    * [KARAF-5112] - Upgrade to jansi 1.16
    * [KARAF-5113] - Upgrade to jline 3.3.0
    * [KARAF-5114] - Upgrade to gogo 1.0.6
    * [KARAF-5132] - Cellar: Upgrade Hazelcast to 3.8.2
    * [KARAF-5146] - Upgrade to Narayana 5.6.0.Final
    * [KARAF-5149] - Upgrade to JNA 4.4.0
    * [KARAF-5150] - Upgrade to Aries Blueprint Core 1.8.1
    * [KARAF-5151] - Upgrade to Aries Transaction Manager 1.3.3
    * [KARAF-5152] - Upgrade to commons-compress 1.14
    * [KARAF-5153] - Upgrade to Felix BundleRepository 2.0.10
    * [KARAF-5154] - Upgrade to Felix Framework 5.6.4
    * [KARAF-5155] - Upgrade to Felix HttpLite 0.1.6
    * [KARAF-5157] - Upgrade to Felix Resolver 1.14.0
    * [KARAF-5158] - Upgrade to Felix SCR 2.0.10
    * [KARAF-5159] - Upgrade to Felix WebConsole 4.3.4
    * [KARAF-5160] - Upgrade to Equinox Region 1.2.101.v20150831-1342
    * [KARAF-5214] - Upgrade to Pax Logging 1.10.1
    * [KARAF-5219] - Upgrade Narayana to version 5.6.2.Final
    * [KARAF-5220] - Cellar-Kubernetes: Bump to Kubernetes-client 2.4.1
    * [KARAF-5231] - Upgrade to jline 3.3.1
    * [KARAF-5248] - Upgrade to blueprint-core 1.8.2
    * [KARAF-5249] - Upgrade to blueprint spring 0.4.0
    * [KARAF-5253] - Update pax-jdbc to 1.1.0
    * [KARAF-5256] - Upgrade to Felix SCR 2.0.12
    * [KARAF-5257] - Upgrade to sshd 1.6.0
    * [KARAF-5258] - Upgrade to Pax Exam 4.11.0
    * [KARAF-5268] - Upgrade to commons-logging 1.2
    * [KARAF-5269] - Upgrade to commons-lang3 3.6
    * [KARAF-5278] - Update to felix framework 5.6.6
    * [KARAF-5281] - Upgrade to Spring 4.3.10.RELEASE
    * [KARAF-5288] - Cellar: Bump to Kubernetes-client 2.5.9
    * [KARAF-5289] - Upgrade to jline 3.4.0
    * [KARAF-5291] - Upgrade Narayana to version 5.6.4.Final
    * [KARAF-5293] - Upgrade to Apache POM 18
    * [KARAF-5309] - Upgrade to directory server 2.0.0-M24
    * [KARAF-5310] - Upgrade to maven surefire plugin 2.20 to get colored output
    * [KARAF-5349] - Upgrade to pax-jdbc-1.2.0
    * [KARAF-5359] - Upgrade to JLine 3.5.0
    * [KARAF-5360] - Upgrade to Felix Gogo Runtime / JLine 1.0.8
    * [KARAF-5365] - Upgrade to Aries Subsystem 2.0.10
    * [KARAF-5366] - Upgrade to Felix ConfigAdmin 1.8.16
    * [KARAF-5367] - Upgrade to Felix EventAdmin 1.4.10
    * [KARAF-5368] - Upgrade to Felix Framework & Main 5.6.8
    * [KARAF-5369] - Upgrade to Felix Metatype 1.1.4
    * [KARAF-5370] - Upgrade to Felix Resolver 1.14.0
    * [KARAF-5401] - Upgrade to Aries Blueprint Spring 0.5.0
    * [KARAF-5419] - Upgrade to Aries Blueprint Core 1.8.3
    * [KARAF-5429] - Upgrade Narayana to version 5.7.0.Final
    * [KARAF-5430] - Upgrade to Spring 4.0.9.RELEASE & 4.3.12.RELEASE
    * [KARAF-5431] - Upgrade to Felix Gogo Runtime / JLine 1.0.10
    * [KARAF-5432] - Upgrade to Felix Utils 1.10.4 and FileInstall 3.6.4
    * [KARAF-5439] - Upgrade Narayana to version 5.7.1.Final

#### Documentation
    * [KARAF-5357] - Help string for feature:stop is incorrect

#### Improvement
    * [KARAF-3825] - Add ability to shutdown Karaf with a disabled shutdown port
    * [KARAF-4417] - Display a summary for the verify goal
    * [KARAF-4418] - Ability to exclude a set of features from the verify goal
    * [KARAF-4748] - Make Felix Resolver Threads configurable
    * [KARAF-4785] - Use the scr gogo commands and provide completion
    * [KARAF-4803] - Allow to turn off Karaf configuration persistence manager
    * [KARAF-4932] - Remove blueprint compat and blueprint annotations bundles
    * [KARAF-4973] - Refactoring of features extension
    * [KARAF-5004] - Discover the artifact type instead of relying on the artifact type/classifier string (kar / features / bundle)
    * [KARAF-5023] - Improve config commands to better support substituted and typed properties
    * [KARAF-5072] - Add setting to ssh server for forcing a provided key
    * [KARAF-5080] - Use the full ttop command from gogo-jline
    * [KARAF-5102] - org.ops4j.pax.logging.cfg contains non-ASCII character
    * [KARAF-5104] - karaf:run should support a features set
    * [KARAF-5118] - Make SSHD server threads configurable
    * [KARAF-5126] - Use awaitility and matchers in JmsTest
    * [KARAF-5131] - XA + JMS support
    * [KARAF-5162] - Code can be simplified using new Map methods
    * [KARAF-5168] - Replace old-style loops with foreach loops or streams
    * [KARAF-5169] - Remove redundant type information
    * [KARAF-5170] - Use try-with-resources
    * [KARAF-5173] - Some tests could benefit from a common CallbackHandler
    * [KARAF-5178] - Code can be simplified using lambdas
    * [KARAF-5185] - Karaf enterprise feature shall omit the jpa feature in favor of the aries jpa feature
    * [KARAF-5205] - Add -r/--refresh option to bundle:update command
    * [KARAF-5208] - Improve feature:install error message
    * [KARAF-5222] - Make possible to force the start of a karaf instance even if another one has been detected as running.
    * [KARAF-5230] - Support version range when installing features
    * [KARAF-5235] - Remove null values from AssemblyMojo configuration
    * [KARAF-5241] - Improve RBAC logging for JMX
    * [KARAF-5243] - add -p option for bin/client
    * [KARAF-5266] - log commands should limit number of lines printed instead of number of log entries
    * [KARAF-5272] - Enhance the features deployer so that it performs a real upgrade
    * [KARAF-5280] - Shell should not display the welcome message again when it is restarted
    * [KARAF-5282] - SyncopeLoginModule should support Syncope 2.x response format
    * [KARAF-5286] - Separate server key generation from key reading
    * [KARAF-5287] - Provide a way to hide passwords in shell
    * [KARAF-5292] - uneeded dependency to dbcp in eclipselink feature
    * [KARAF-5294] - Cleanup Maven repository
    * [KARAF-5308] - Remove RepositoryImpl lazy loading as we always load it upfront anyway
    * [KARAF-5316] - Jaas Encryption should be easier to use
    * [KARAF-5319] - the jetty feature in karaf shouldn't depend on pax-jetty feature
    * [KARAF-5363] - Add --no-start option to kar:install, kar cfg and kar MBean
    * [KARAF-5380] - Fix typo in JDBC lock implementation
    * [KARAF-5400] - Remove usage of felix scr compatibility bundle
    * [KARAF-5407] - Allow feature:info to print the xml for a given feature
    * [KARAF-5426] - Print type of wiring resource
    * [KARAF-5427] - Add RBAC support for reflection invocation and redirections in the console
    * [KARAF-5437] - Use named thread pools to help identifying threads
    * [KARAF-5443] - Add a completer for bundle symbolic names
    * [KARAF-5445] - Completers should be followed by a space when complete

#### New Feature
    * [KARAF-2401] - Improve log coloring
    * [KARAF-3270] - Add command/MBean operation to give current user and his roles
    * [KARAF-4188] - Add support for Systemd's watchdog
    * [KARAF-5008] - Provide Maven diagnostic commands
    * [KARAF-5074] - Support for typed config files (as in Felix ConfigAdmin config files) in features
    * [KARAF-5082] - Allow the use of external data for features configuration
    * [KARAF-5107] - Allow hooking into the feature installation process
    * [KARAF-5129] - JMS Pooling and better Artemis support
    * [KARAF-5172] - Add simple LDAPBackingEngine
    * [KARAF-5175] - Provide a debugs option for the karaf script to make it easier to debug karaf startup sequence
    * [KARAF-5306] - Add scheduler:trigger command
    * [KARAF-5354] - The log:get and log:set commands should support etc/log4j2.xml configuration
    * [KARAF-5416] - Remove support for ext and endorsed libraries for Java 9 compatibility

#### Task
    * [KARAF-5125] - Upgrade to Narayana 5.5.6.Final
    * [KARAF-5148] - Replace use of org.json
    * [KARAF-5225] - Add Narayana dependencies to DependencyManagement
    * [KARAF-5226] - Add Hibernate-validator dependency to DependencyManagement
    * [KARAF-5227] - Use an explicit Awaitility version property 
    * [KARAF-5396] - Ensure Karaf can build with JDK9 GA(build 9+181)
    * [KARAF-5417] - Trim down distributions

#### Test
    * [KARAF-4936] - FeatureTest#repoRefreshCommand failure
