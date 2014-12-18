#!/bin/bash

display_usage() {
echo -e "\nUsage:\n$0 <version> <framework staging id> <archetypes staging id> <plugin staging id>\n"
echo -e "Example: 7.3.7 1450 1451 1452"
}

# if less than two arguments supplied, display usage
if [  $# -le 3 ]
then
	display_usage
	exit 1
fi

# check whether user had supplied -h or --help . If yes display usage
if [[ ( $# == "--help") ||  $# == "-h" ]]
then
	display_usage
	exit 0
fi

VERSION=$1
VAADINREPO=comvaadin-$2
ARCHETYPEREPO=comvaadin-$3
PLUGINREPO=comvaadin-$4

REPOBASE=https://oss.sonatype.org/content/repositories/
ART=test-widget-$VAADINVERSION
GROUP=testpkg
mvn archetype:generate -DarchetypeGroupId=com.vaadin -DarchetypeArtifactId=vaadin-archetype-widget -DarchetypeVersion=$VERSION -DarchetypeRepository=$REPOBASE$ARCHETYPEREPO -DgroupId=$GROUP -DartifactId=$ART -Dversion=1.0-SNAPSHOT -DinteractiveMode=false  -DComponentClassName=TestComponent
pushd $ART
# Add vaadin repo
sed -i "s#<repositories>#<repositories><repository><id>vaadin-$VERSION-staging</id><url>$REPOBASE$VAADINREPO</url></repository>#" */pom.xml
# Add vaadin and plugin repo as plugin repos
sed -i "s#<pluginRepositories>#<pluginRepositories><pluginRepository><id>vaadin-$VERSION-staging</id><url>$REPOBASE$VAADINREPO</url></pluginRepository>#" */pom.xml
sed -i "s#<pluginRepositories>#<pluginRepositories><pluginRepository><id>vaadin-$VERSION-plugin-staging</id><url>$REPOBASE$PLUGINREPO</url></pluginRepository>#" */pom.xml

pushd $ART && mvn install && popd
pushd $ART-demo && mvn package jetty:run
