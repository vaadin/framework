#!/bin/bash

if [ ! -e "vaadin-shared" ]
then
        echo "You must run this in the directory containing folders for the various vaadin-* modules."
        echo "If you run this in the project directory, go to result/artifacts/<version>/"
        echo "If you run this on build artifacts from the build server, run it in the directory where you unzipped the artifacts"
        exit 1
fi

for base in *
do 
        if [ ! -d "$base" ]
        then
                continue
        fi

        pushd "$base"
        version=`ls "$base"-*.pom|sed "s/$base-//"|sed "s/.pom//"`
        pomTemplate="$base-$version.pom"
        if [ -e "$pomTemplate" ]
        then
                id="$base-$version"
                pomFile="$pomTemplate-modified"
                file="$id.jar"
                javadocFile="$id-javadoc.jar"
                sourcesFile="$id-sources.jar"

                # Install using real version for easy testing
                cat "$pomTemplate"|sed "s/<version>7.*-SNAPSHOT</<version>$version</g" > "$pomFile"

                echo "Installing $base $version..."
                if [ -e "$javadocFile" ]
                then
                        mvn org.apache.maven.plugins:maven-install-plugin:2.5.1:install-file  -DpomFile="$pomFile" -Djavadoc="$javadocFile"  -Dsources="$sourcesFile" -Dfile="$file"
                else
                        mvn org.apache.maven.plugins:maven-install-plugin:2.5.1:install-file  -DpomFile="$pomFile" -Dfile="$file"
                fi
        fi
        popd
done