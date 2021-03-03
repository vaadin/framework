# Vaadin Framework Contribution Guidelines

## Project setup

See the repository [root level README](https://github.com/vaadin/framework/blob/master/README.md) for instructions on setting up the environment to keep up the formatting and code style.

## Pull Requests

### Making your PR

A new pull request should start by forking the Vaadin Framework project (or checking that your fork is up-to-date).

Unless the issue is specific to a single version of the Framework, you should always aim to make the patch on top of the `master` branch.

In your repo, make a new branch from `master`. Make your code changes in that branch, and once you're done make a pull request to the `vaadin/framework` branch `master`.

We recommend making sure that the "Allow edits from maintainers" checkbox on the pull request page is checked. This allows us to commit minor fixes, like correcting typos, without bothering you.

### Getting feedback and responding to it

Once the pull request has been opened, it will be reviewed by the development team behind Vaadin Framework. We pay close attention to the readability of the code as well as documentation and correctness.

If this was your first pull request to the Vaadin repository, you will also have to sign our CLA. This needs to be done with the same user you use to make the commits. If you're using multiple e-mails and GitHub users, each will need to sign the CLA.

## Issues

Only use GitHub issues for bugs and feature requests. For general support from the community, see https://vaadin.com/forum

For bugs, please provide at minimum the following information:
- Vaadin version
- Description of the bug
- If possible, minimal reproducible example

We use the label `good first issue` to mark easy issues to get started with. The list is available [here](https://github.com/vaadin/framework/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22).
