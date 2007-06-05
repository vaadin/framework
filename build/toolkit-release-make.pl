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
  "Usage: toolkit-release-make.pl MERGE BRANCH VERSION TARGET [SOURCE]\n".
  " MERGE is of value nomerge, merge or continue\n".
  " BRANCH is new major.minor version, e.g. 4.0\n".
  " VERSION is new version, e.g. 4.0.1-rc3\n".
  " TARGET is directory to store release zip file, e.g. internal/4.0.1-rc/\n".
  " SOURCE is SVN URL used to create release, if omitted then trunk used\n".
  "\n".
  "Description of MERGE parameter\n".
  " nomerge  : no merging is performed, build is fully based on SOURCE.\n".
  " merge    : after BRANCH created, allows manual merging.\n".
  " continue : called after manual merging completed.\n\n".
  "Examples:\n\n".
  "No merging example:\n".
  "toolkit-release-make.pl nomerge 4.0 4.0.1-testing2 internal/4.0.1-rc\n".
  "  Recreates 4.0 branch from trunk,\n".
  "  continues automatically with build (no merging allowed),\n".
  "  release is fully based on trunk.\n\n".
  "No merging example:\n".
  "toolkit-release-make.pl nomerge 4.1 4.1.0 release\n".
  "  Recreates 4.1 branch from trunk,\n".
  "  continues automatically with build (no merging allowed),\n".
  "  release is fully based on trunk.\n\n".
  "Merging example (two phases required):\n".
  "Phase 1\n".
  " toolkit-release-make.pl merge 4.0 4.0.1-pre_mantis release tags/4.0.0\n".
  "  Recreates 4.0 branch from tags/4.0.0,\n".
  "  buildprocess is suspended,\n".
  "  buildmaster performs merging manually,\n".
  "  buildmaster executes script again (see below).\n".
  "Phase 2\n".
  " toolkit-release-make.pl continue 4.0 4.0.1-pre_mantis release tags/4.0.0\n".
  "  Buildprocess continues (buildmaster has done merging).\n";

my $MERGE = shift(@ARGV) || &failure($usage);
my $BRANCH = shift(@ARGV) || &failure($usage);
my $VERSION = shift(@ARGV) || &failure($usage);
my $TARGET = shift(@ARGV) || &failure($usage);
my $SOURCE = shift(@ARGV) || 'trunk';
  
my $t = "";

if (
  ($MERGE ne "merge" ) &&
  ($MERGE ne "continue" )
  ) {
  &failure ("\nMERGE must be of value merge or continue\n");
}
if (!$BRANCH =~ /([4-9]{1}\.[0-9]{1})/) {
  &failure ("\nBRANCH must be format {x}.{y} where {x}=major (4-9), ".
      "{y}=minor (0-9).\n");
}
if ( 
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$VERSION =~ /[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  &failure ("\nVERSION must be format {x}.{y}.{z} or x.y.z-rc{m} ".
      "where {x}=major (4-9), {y}=minor (0-99), {z}=revision ".
      "(0-999) and optional release candidate number {m}=(0-99).\n");
}
if (
  (!$TARGET =~ /release\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}/) &&
  (!$TARGET =~ /internal\/[4-9]{1}\.[0-9]{1,2}.[0-9]{1,3}-rc[0-9]{1,2}/)
  ) {
  &failure ("\nTARGET must be e.g. internal/4.0.1-rc/ or release/4.0.\n");
}

# Open log file
open(LOG, ">>$WORKDIR/builds/$TARGET/itmill-toolkit-$VERSION.make.log");

#
# If MERGE=continue, buildmaster has performed merging and now asks
# build script to continue making the release.
#
if ($MERGE eq "continue") {
  &message(" Buildprocess continued. Buildmaster has performed merging. ");
  goto CONTINUE;
}


# Make sure $WORKDIR directory exists
&message(
  "\n  MERGE [$MERGE]\n  BRANCH [$BRANCH]\n  VERSION [$VERSION]\n".
  "  DIR [$SVN_URL_BUILDS/$TARGET]\n".
  "  SOURCE [$SOURCE]\n"
);

# collect data from host
&message(" Host information ");
&execute("date -R");
&execute("uname -a");
&execute("java -version");
&execute("ant -version");

&message(" Initializing repositories ");
# go to directory where repository working copies (WC) are
chdir($WORKDIR) || &failure("Could not chdir to $WORKDIR.\n");
# delete old repo
&execute("rm -rf $BRANCH");
# ensure that target directory exists
&execute("svn mkdir $SVN_ROOT/builds/$TARGET -m \"Added $TARGET directory\"");
&execute("svn ci $SVN_ROOT/builds/$TARGET");
# checkout (if missing) build repository
&execute("svn co $SVN_ROOT/builds | grep \"Checked out\"");
# it's safest to replace $BRANCH from $SOURCE (but you could use also merging)
&execute(
  "svn rm $SVN_ROOT/branches/$BRANCH ".
  "-m \"Recreating $BRANCH branch from $SOURCE. Removing old $BRANCH.\""
);
&execute(
  "svn copy $SVN_ROOT/$SOURCE $SVN_ROOT/branches/$BRANCH ".
  "-m \"Recreating $BRANCH branch from $SOURCE. Copying new $BRANCH.\""
);
# checkout $BRANCH
&execute("svn co $SVN_ROOT/branches/$BRANCH | grep \"Checked out\"");

# Use brakepoint if you need to do additional merging or 
# file based revision changes
# BRAKEPOINT, disabled

#
# Check if buildmaster wishes to do merging at this point
# or continue directly with building?
#
if ($MERGE eq "merge") {
  # Yes, merging required
  &message(" Buildprocess suspended. Buildmaster now performs merging manually ");
  print "".
      "Please now perform merging manually and after that execute:\n".
      "toolkit-release-make.pl continue $BRANCH $VERSION $TARGET $SOURCE\n".
  close(LOG);
  exit;
} else {
  # No, merging not required
  # Either merging is complete and building is continued,
  # OR merging not even performed, build is based on e.g. trunk
}
CONTINUE:
  
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

&message(" Copying branch $BRANCH under tags branch");
# copy branch $BRANCH into tags directory (some may interpret this as tagging)
&execute(
  "svn copy $SVN_ROOT/branches/$BRANCH $SVN_ROOT/tags/$VERSION ".
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