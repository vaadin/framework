#!/usr/bin/perl -w

use strict;

#
# NOTE: tested only with Jani Laakso's environment
#

#
# NOTE: if you need manual intervention at any point
# edit this script at set an "breakpoint"
# continue later with "goto STEP" and "STEP:"
#

# working directory to make releases
my $WORKDIR = "/home/jani/crypt/tk";

# directory prefix where release package zip file is stored
my $SVN_ROOT = "https://svn.itmill.com/svn/itmill-toolkit";
my $SVN_URL_BUILDS = $SVN_ROOT."/builds";

my $usage = 
  "Usage: $0 <branch> <version> <dir>\n".
  " <branch> is new major.minor version, e.g. 4.0\n".
  " <version> is new version, e.g. 4.0.1-rc3\n".
  " <target> is directory to store release zip file, e.g. internal/4.0.1-rc/\n";

my $BRANCH = shift(@ARGV) || &failure($usage);
my $VERSION = shift(@ARGV) || &failure($usage);
my $TARGET = shift(@ARGV) || &failure($usage);
  
my $t = "";

if (!$BRANCH =~ /([4-9]{1}\.[0-9]{1})/) {
  &failure ("\n<branch> must be format {x}.{y} where {x}=major (4-9), ".
      "{y}=minor (0-9).\n");
}
if ( 
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  &failure ("\n<version> must be format {x}.{y}.{z} or x.y.z-rc{m} ".
      "where {x}=major (4-9), {y}=minor (0-99), {z}=revision ".
      "(0-999) and optional release candidate number {m}=(0-99).\n");
}
if (
  (!$TARGET =~ /release\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$TARGET =~ /internal\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  &failure ("\n<target> must be e.g. internal/4.0.1-rc/ or release/4.0.\n");
}

# Open log file
open(LOG, ">>$WORKDIR/builds/$TARGET/itmill-toolkit-$VERSION.make.log");

# BRAKEPOINT
# goto STEP;
  
# Make sure $WORKDIR directory exists
&message(
  "\n  BRANCH [$BRANCH]\n  VERSION [$VERSION]\n".
  "  DIR [$SVN_URL_BUILDS/$TARGET]\n"
);

&message(" Initializing repositories ");
# go to directory where repository working copies (WC) are
chdir($WORKDIR) || &failure("Could not chdir to $WORKDIR.\n");
# delete old repo
&execute("rm -rf $BRANCH");
# checkout (if missing) build repository
&execute("svn co $SVN_ROOT/builds | grep \"Checked out\"");
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
&execute("svn co $SVN_ROOT/branches/$BRANCH | grep \"Checked out\"");

# Use brakepoint if you need to do additional merging or 
# file based revision changes
# BRAKEPOINT, disabled
# exit;
# STEP:
  
&message(" Changing VERSION");
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

&message(" Commit changes ");
# commit changes
&execute("svn ci -m \"Building $VERSION release.\"");

&message(" Executing ant ");
# execute build script, takes 5-40 minutes depending on hw
chdir("$WORKDIR/$BRANCH/build");
&execute("ant");

&message(" Copying branch 4.0 under tags branch");
# copy branch 4.0 into tags directory (some may interpret this as tagging)
&execute(
  "svn copy $SVN_ROOT/branches/4.0 $SVN_ROOT/tags/$VERSION ".
  "-m \"Copying $VERSION release into tags.\""
);

&message(" Committing release package zip file to builds dir ");
# commit release package zip to SVN
&execute("cp result/itmill-toolkit-$VERSION.zip $WORKDIR/builds/$TARGET");
chdir("$WORKDIR/builds/$TARGET");
&execute("svn add itmill-toolkit-$VERSION.zip");
&execute(
  "svn ci itmill-toolkit-$VERSION.zip ".
  "-m \"Added $VERSION release package.\""
);
&message(" Done ");

# store log to SVN
close(LOG);
`svn add $WORKDIR/builds/$TARGET/itmill-toolkit-$VERSION.make.log`;
`svn ci $WORKDIR/builds/$TARGET/itmill-toolkit-$VERSION.make.log -m \"Release $VERSION build completed. See toolkit-release-make log file.\"`;

exit;

sub message() {
  my $msg = shift;
  $msg = "\n***".$msg."***\n";
  print $msg;
  print LOG $msg;
}

sub execute() {
    my $cmd = shift;   
    print "  $cmd\n";
    print LOG "  $cmd\n";
    my $result = `$cmd 2>/dev/stdout`;
    print $result."\n";
    print LOG $result."\n";
}

sub failure() {
    my $msg = shift;
    print $msg."\n";
    exit;
}