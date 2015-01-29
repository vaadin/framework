#!/bin/bash
dir=`dirname $0`
ARCHETYPE_ARTIFACT=vaadin-archetype-application-multimodule
. $dir/validate-archetype-common.sh


mvn archetype:generate -DarchetypeGroupId=$ARCHETYPE_GROUP -DarchetypeArtifactId=$ARCHETYPE_ARTIFACT -DarchetypeVersion=$ARCHETYPE_VERSION -DarchetypeRepository=$ARCHETYPEREPO -DgroupId=$GROUP -DartifactId=$ART -Dversion=1.0-SNAPSHOT -DinteractiveMode=false
pushd $ART
# Add vaadin repo
$SED -i "s#<repositories>#<repositories><repository><id>vaadin-$ARCHETYPE_VERSION-staging</id><url>$VAADINREPO</url></repository>#" pom.xml
# Add vaadin and plugin repo as plugin repos
$SED -i "s#</repositories>#</repositories><pluginRepositories><pluginRepository><id>vaadin-$ARCHETYPE_VERSION-plugin-staging</id><url>$PLUGINREPO</url></pluginRepository></pluginRepositories>#" pom.xml

mvn install && pushd $ART-ui && mvn jetty:run
popd
popd
