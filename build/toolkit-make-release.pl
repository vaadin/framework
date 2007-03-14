#!/usr/bin/perl -w

use strict;

#
# NOTE: tested only with Jani Laakso's environment
#

# working directory to make releases
my $WORKDIR = "/home/jani/crypt/tk";

# directory prefix where release package zip file is stored
my $SVN_ROOT = "https://svn.itmill.com/svn/itmill-toolkit";
my $SVN_URL_BUILDS = $SVN_ROOT."/builds";

my $usage = 
  "Usage: make-release.sh <branch> <version> <dir>\n".
  " <branch> is new major.minor version, e.g. 4.0\n".
  " <version> is new version, e.g. 4.0.1-rc3\n".
  " <target> is directory to store release zip file, e.g. internal/4.0.1-rc/\n";

my $BRANCH = shift(@ARGV) || die($usage);
my $VERSION = shift(@ARGV) || die($usage);
my $TARGET = shift(@ARGV) || die($usage);
  
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
  (!$TARGET =~ /release\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$TARGET =~ /internal\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  die ("<dir> must be e.g. internal/4.0.1-rc/ or release/4.0.\n");
}

print "Make sure $WORKDIR directory\n";
print "BRANCH [$BRANCH]\nVERSION [$VERSION]\nDIR [$SVN_URL_BUILDS$TARGET]\n";

&proceed("Initializing repositories");
# go to directory where repository working copies (WC) are
chdir($WORKDIR);
# delete old repo
&execute("rm -rf $BRANCH");
# checkout (if missing) build repository
&execute("svn co $SVN_ROOT/builds");
# it's safest to replace 4.0 from trunk (but you could use also merging)
&execute(
  "svn rm $SVN_ROOT/branches/$BRANCH ".
  "-m \"Recreating $BRANCH branch from trunk. Removing old $BRANCH.\""
);
&execute(
  "svn copy $SVN_ROOT/trunk $SVN_ROOT/branches/$BRANCH ".
  "-m \"Recreating $BRANCH branch from trunk. Copying new $BRANCH.\""
);
# checkout $BRANCH
&execute("svn co $SVN_ROOT/branches/$BRANCH");

&proceed("Changing VERSION");
# go to $BRANCH directory
chdir("$WORKDIR/$BRANCH");
# fix links as VERSION changes
&execute(
  "sed s/`cat build/VERSION | cut -f2 -d'='`/$VERSION/ ".
  "index.html >/tmp/index.html"
);
&execute("diff index.html /tmp/index.html");
&execute("cp /tmp/index.html index.html");
# increment VERSION
&execute("echo \"version=$VERSION\" >build/VERSION");

&proceed("Commit changes");
# commit changes
&execute("svn ci -m \"Building $VERSION release.\"");

&proceed("Executing ant");
# execute build script, takes 5-40 minutes depending on hw
chdir("$WORKDIR/$BRANCH/build");
&execute("ant");

&proceed("Copying branch 4.0 under tags branch");
# copy branch 4.0 into tags directory (some may interpret this as tagging)
&execute(
  "svn copy $SVN_ROOT/branches/4.0 $SVN_ROOT/tags/$VERSION ".
  "-m \"Copying $VERSION release into tags.\""
);

&proceed("Committing release package zip file to builds dir");
# commit release package zip to SVN
&execute("cp result/itmill-toolkit-$VERSION.zip $WORKDIR/builds/$TARGET");
chdir("$WORKDIR/builds/$TARGET");
&execute("svn add itmill-toolkit-$VERSION.zip");
&execute("svn ci -m \"Added $VERSION release package.\"");

print "Done.\n";
exit;

sub proceed() {
  my $msg = shift;
  $msg = "\n\n*** ".$msg.", press any key to continue ***";
  print $msg;
  <STDIN>;
}

sub execute() {
    my $cmd = shift;   
    print "  $cmd\n";
    my $result = `$cmd`;
    print $result."\n";
}
