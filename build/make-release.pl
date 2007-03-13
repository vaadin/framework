#!/usr/bin/perl -w

use strict;

my $usage = 
  "Usage: make-release.sh <branch> <version> <dir>\n".
  " <branch> is new major.minor version, e.g. 4.0\n".
  " <version> is new version, e.g. 4.0.1-rc3\n".
  " <dir> is dir to store release zip file, e.g. internal/4.0.1-rc/\n";

die ("This is not yet tested, do not use.\n");

my $BRANCH = shift(@ARGV) || die($usage);
my $VERSION = shift(@ARGV) || die($usage);
my $DIR = shift(@ARGV) || die($usage);
  
my $t = "";

if (!$BRANCH =~ /([4-9]{1}\.[0-9]{1})/) {
  die ("<branch> must be format {x}.{y} where {x}=major (4-9), ".
      "{y}=minor (0-9).\n");
}
if ( 
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  die ("<version> must be format {x}.{y}.{z} or x.y.z-rc{m} ".
      "where {x}=major (4-9), {y}=minor (0-99), {z}=revision ".
      "(0-999) and optional release candidate number {m}=(0-99).\n");
}
if (
  (!$DIR =~ /release\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$DIR =~ /internal\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  die ("<dir> must be e.g. internal/4.0.1-rc/ or release/4.0.\n");
}

# go to directory where repository working copies (WC) are
`cd ~/toolkit`;
# it's safest to replace 4.0 from trunk (but you could use also merging)
`svn rm https://svn.itmill.com/svn/itmill-toolkit/branches/$BRANCH -m "Recreating $BRANCH branch from trunk. Removing old $BRANCH."`;
`svn copy https://svn.itmill.com/svn/itmill-toolkit/trunk https://svn.itmill.com/svn/itmill-toolkit/branches/$BRANCH -m "Recreating $BRANCH branch from trunk. Copying new $BRANCH."`;
# checkout $BRANCH
`svn co https://svn.itmill.com/svn/itmill-toolkit/branches/$BRANCH`;

# go to $BRANCH directory
chdir("$BRANCH");
# fix links as VERSION changes
`sed s/cat build/VERSION | cut -f2 -d'='/$VERSION/ index.html >index.html`;
# increment VERSION
`echo "version=$VERSION" >build/VERSION`;

# commit changes
`svn ci -m "Building <VERSION> release."`;

# execute build script, takes 5-40 minutes depending on hw
`ant`;

# copy branch 4.0 into tags directory (some may interpret this as tagging)
`svn copy https://svn.itmill.com/svn/itmill-toolkit/branches/4.0 https://svn.itmill.com/svn/itmill-toolkit/tags/<VERSION> -m "Copying $VERSION into tags."`;

# commit release package zip to SVN
`cp result/itmill-toolkit-$VERSION.zip ~/toolkit/builds/$DIR`;
chdir("~/toolkit/builds/$DIR");
`svn add itmill-toolkit-$VERSION.zip`;
`svn ci -m "Added $VERSION release."`;
