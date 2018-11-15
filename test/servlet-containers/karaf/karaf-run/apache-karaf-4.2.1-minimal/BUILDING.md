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

# Building Apache Karaf

## Initial Setup

1. Install J2SE 8.0 SDK (or later), which can be downloaded from 
   http://www.oracle.com/technetwork/java/javase/downloads/index.html
   Use version of "JDK 8.0 Update 131" (or later).

2. Make sure that your JAVA_HOME environment variable is set to the newly installed 
   JDK location, and that your PATH includes `%JAVA_HOME%\bin` (windows) or 
   `$JAVA_HOME$/bin` (unix).

3. Install Maven 3.0.3 (or later), which can be downloaded from 
   http://maven.apache.org/download.html. Make sure that your PATH includes 
   the `$MVN_HOME/bin` directory. 


## Building

1. Change to the top level directory of Apache Karaf source distribution.
2. Run

    ```
    $> mvn clean install
    ```

   This will compile Apache Karaf and run all of the tests in the
   Apache Karaf source distribution. Alternatively, you can run:
   
   ```
   $> mvn clean install -DskipTests
   ```
   
   This will compile Apache Karaf without running the tests and takes less
   time to build.
   
3. The distributions will be available under `assemblies/apache-karaf/target`
   and `assemblies/apache-karaf-minimal/target` directories.

