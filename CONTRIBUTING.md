# Contributing to Vaadin Framework

*There are many ways to participate to the Vaadin project. You can ask questions and participate to discussion in the [forum](https://vaadin.com/forum), [fill bug reports](https://dev.vaadin.com/) and enhancement suggestion, [create add-ons](https://vaadin.com/directory) and contribute code. These instructions are for contributing code the the core framework.*

# Summary

We like quality patches that solve problems. A quality patch follows good coding practices - it’s easy to read and understand. For more complicated fixes or features, the change should be broken down into several smaller easy to understand patches. Most of this is really just what we consider to be common sense and best development practices.  

In other words: 

 * Describe your changes: what did you change, why did you change it, how did you change it? 
 * Separate your changes: one change per commit, many small changes are easier to review
 * Include a test to prove your patch works, or a benchmark if it’s a performance improvement.
 * Style-check your changes: it’s okay to have a separate commit to fix style issues.
 * Ensure you have Contributor Agreement signed up. This can be signed digitally in the code review system.
 * Submit your patch; it will then be reviewed by the Framework team, who will provide actionable feedback in a timely fashion if necessary. 
 * Respond to review comments: review comments are meant to improve the quality of the code by pointing out defects or readability issues.
 * Don't get discouraged - or impatient: Reviewers are people too and will sometimes forget. Give them a friendly poke if you feel the patch has been forgotten about. 
 * Most patches take a few iterations of review before they are merged. 

# We encourage you to get in touch

Getting in touch with us on the [Vaadin forum](https://vaadin.com/forum) or on the Vaadin IRC channel (#vaadin on irc.freenode.net) is not a bad place to start. We’re more than happy to help you get started, and we’re happy to engage in conversation about feature suggestions and bug fixes. We welcome contributors and contributions and we’re here to help.

Getting in touch with us early will also help us co-ordinate efforts so that not everyone ends up working on the same bug or feature at the same time.

# Obtain a current source tree

The Vaadin repository can be cloned using `git clone https://github.com/vaadin/vaadin.git` or using your favorite Git tool.

Remember to do `git checkout master` and `git pull` to make sure you are creating your commits on top of a recent enough version. 

## Set up your development environment

To set up the project to your IDE, follow the instructions in the [README.md](https://github.com/vaadin/vaadin).

# Describe your changes

## Describe your problem

Whether your patch is a one-line bug fix or 5000 lines of a new feature, there must be an underlying problem that motivated you to do this work. Convince the reviewer that there is a problem worth fixing and that it makes sense for them to read past the first paragraph. This is often already described in bug/enhancement issue, but also summarise it in your commit message.

## Describe user-visible impact

Straight up crashes and lockups are pretty convincing, but not all bugs are that blatant.  Even if the problem was spotted during code review, describe the impact you think it can have on users. 

## Describe the solution

Once the problem is established, describe what you are actually doing about it in technical detail.  It's important to describe the change in plain English for the reviewer to verify that the code is behaving as you intend it to.

## Solve only one problem per patch

If your description starts to get long, that's a sign that you probably need to split up your patch. See “Only one logical change per patch”

Describe your changes in imperative mood, e.g. "make xyzzy do frotz". If the patch fixes a logged bug entry, refer to that bug entry by number or URL. 

However, try to make your explanation understandable without external resources.  In addition to giving a URL to a ticket or bug description, summarise the relevant points of the discussion that led to the patch as submitted.

# Separate your changes

Separate each logical change into a separate patch.

For example, if your changes include both bug fixes and performance enhancements, separate those changes into two or more patches. If your changes include an API update, and a new component which uses that new API, separate those into two patches.

On the other hand, if you make a single change to numerous files, group those changes into a single patch.  Thus a single logical change is contained within a single patch.

The point to remember is that each patch should make an easily understood change that can be verified by reviewers.  Each patch should be justifiable on its own merits.

If one patch depends on another patch in order for a change to be complete, that is OK.  Simply note "this patch depends on patch X" in your patch description.

When dividing your change into a series of patches, take special care to ensure that the project builds and runs properly after each patch in the series.  Developers using "git bisect" to track down a problem can end up splitting your patch series at any point; they will not thank you if you introduce bugs in the middle. Compilation failures are especially annoying to deal with. 

# Style-check your changes

Check your patch for basic style violations. If you use eclipse, use the formatting rules preconfigured in the project to make life easier for all involved, and configure save actions as described in [README.md](https://github.com/vaadin/vaadin/blob/master/README.md). 

Patches causing unnecessary style/whitespace changes are messy and will likely be bounced back. 

If you are touching old files and want to update them to current style conventions, please do so in a separate commit. It is usually best to have this commit be the first in the series.

# Example of a good commit message

    Create a Valo icon font for icons used in Valo (#18472)
    
    Valo uses only a handful of icons from Font Awesome. This change introduces a separate icon font for valo (9KB instead of 80KB) and decouples Valo from Font Awesome to enable updating Font Awesome without taking Valo into account.
    
    This change also makes it easy to not load Font Awesome when using Valo by setting $v-font-awesome:false
    
    For backwards compatibility, Font Awesome is loaded by default

## Example breakdown

Start with a good Commit message in imperative form. Reference a ticket number if applicable: 

    Create a Valo icon font for icons used in Valo (#18472)

### Describe the problem:

    Valo uses only a handful of icons from Font Awesome.

### Describe the user impact & describe what was done to solve the problem:

    This change introduces a separate icon font for valo (9KB instead of 80KB) and decouples Valo from Font Awesome to enable updating Font Awesome without taking Valo into account.
    
    This change also makes it easy to not load Font Awesome when using Valo by setting $v-font-awesome:false
    
    For backwards compatibility, Font Awesome is loaded by default

# Include a test

Ideally, we would like all patches to include automated tests. Unit tests are preferred. If there’s a change to UI Code, we would prefer a TestBench test. If that’s not possible (you as a contributor lack a TestBench license), we can make do with a Test UI class that contains a test case as well a clear instructions for how to perform the test. This also goes for features that are hard to test automatically. 

Test cases should succeed with the patch and fail without the patch. That way, it’s clear to everyone that the test does in fact test what it is supposed to test. 

If the patch is a performance improvement, please include some benchmark data that tells us how much the performance is improved. You should also include the test code or UI class you used to benchmark. 

If you can clearly prove that the patch works, it dramatically increases the odds of it being included in a quick and timely fashion.

# Respond to review comments

Your patch will almost certainly get comments from reviewers on ways in which the patch can be improved.  You must respond to those comments; ignoring reviewers is a good way to get ignored in return.  Review comments or questions that do not lead to a code change should almost certainly bring about a comment or changelog entry so that the next reviewer better understands what is going on.

Be sure to tell the reviewers what changes you are making. Respond politely to comments and address the problems they have pointed out. 

If there is feedback that is blocking merging of the pull request, and there is no response from the author in a reasonable time, we may reject it. You are then of course free to resubmit the pull request. The rejection is done not out of spite, but to keep the queue of incoming pull requests manageable and to prevent the queue from spiraling out of control. 

# Don't get discouraged - or impatient.

After you have submitted your change, be patient and wait.  Reviewers are busy people and may not get to your patch right away. Ideally we try to get a response within one business day.

You should receive comments within a week or so; if that does not happen, make sure that you have sent your patches to the right place.  Wait for a minimum of one week before resubmitting or pinging reviewers - possibly longer during busy times like merge windows for minor or major release versions. 

# Submitting the patches

## Submitting patches to gerrit

### Register

Vaadin Gerrit requires you to register separately even though you already have a vaadin.com or dev.vaadin.com account. Go to http://dev.vaadin.com/review and click “register” in the top right corner. The code review uses OpenID and the simplest way to register is using a Google account. Click “Sign in with a Google Account” and enter your Google account details. 

You need to select a username for your account. This is used for submitting contributions for review so remember it. 

### Submit an SSH key

You also need to add the public part of an SSH key, which is used to authenticate you when you are submitting patches. 

Before any of your patches can be accepted you need to complete a contribution agreement. You can do that while registering your username by clicking on the "New Contributor Agreement" and following the instructions. You can do this later also, but you won't be able to submit anything before the agreement is completed. 

Once you are done, you can verify everything is setup correctly using ssh:

    ssh -p 29418 <username>@dev.vaadin.com

If you cannot connect, recheck your user name and ssh key.

### Prepare commit hook for Change-Id

Gerrit tracks changes based on a “Change-Id:” field which must be present in the commit message. To automatically generate a change id you must either copy a Git hook made for this purpose by running this command:

    scp -p -P 29418 <username>@dev.vaadin.com:hooks/commit-msg .git/hooks/

or, if you are using EGit, remember to click the Add Change-Id button in the commit dialog before committing.

### Add "gerrit" remote

You also need to add Gerrit as a remote in your git to be able to later push changes for review:

    git remote add gerrit ssh://<username>@dev.vaadin.com:29418/vaadin.git

In EGit this is configured when you push the first change for review

### Pushing changes

All contributions should be made to the master branch. The contribution might be included in the current maintenance branch depending on the nature of the patch.

The recommended workflow is to checkout master and then create a separate branch for the ticket based on that

    git checkout master ; git checkout -b richtext-race-condition

Then do the actual work as described above and pay attention to a decent commit message.

Gerrit uses a special target “refs/for/<branch>” to which changes should be pushed. Since everything should be pushed through master you should always use refs/for/master.

To push the change for review, use the following command:

    git push gerrit HEAD:refs/for/master

To give a "topic" to your change, you can use the format

    git push gerrit HEAD:refs/for/master/myfancytopic

or in EGit: Team -> Remote -> Push To Gerrit... and Update the URI if incorrect to be ssh://<username>@dev.vaadin.com:29418/vaadin.git . The branch should be refs/for/master

Pushing the commit should result in a message containing a link to the review change:

    [...]
    New Changes:
      https://dev.vaadin.com/review/1381

Open the change in Gerrit and review it to see that you did not accidentally push something else than you thought. If you want to modify the changes, you made, just do [an "amend" commit](https://www.atlassian.com/git/tutorials/rewriting-history/git-commit--amend/) and push the changes again to Gerrit.

A message about the change is automatically sent to reviewers so there is no need to manually add reviewers in Gerrit.

## Submitting patches to github

For some projects which are only available on Github ([Vaadin Plug-in for Eclipse](http://github.com/vaadin/eclipse-plugin), [Vaadin ContextMenu](https://github.com/vaadin/context-menu)), we prefer Pull Requests. For GitHub projects it would make sense to fork the project to create your pull request based on your fork. 

https://yangsu.github.io/pull-request-tutorial/ has instructions on how to create a pull request.

Contributing to these projects also needs a valid Contributor Agrement. See above Gerrit instructions how to digitally sign the agrement.
