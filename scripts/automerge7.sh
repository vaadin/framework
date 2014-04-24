#!/bin/bash

IGNORE=7.0
FROM=7.2
TO=master

IGNORE_HEAD=origin/$IGNORE
FROM_HEAD=origin/$FROM
PUSH="origin HEAD:refs/for/$TO"
EMAIL_AUTHOR=
if [ "$1" = "email" ]
then
	EMAIL_AUTHOR=1
fi

show() {
        sCommit=$1
        if [ "$sCommit" == "" ]
        then
                echo "show() missing commit id"
                exit 1
        fi
        git show -s  $sCommit
}
merge() {
        mCommit=$1
        if [ "$mCommit" == "" ]
        then
                echo "merge() missing commit id"
                exit 2
        fi

#       echo "merge($mCommit)"

        git merge -m "Should be overwritten by merge script" $mCommit $2
        if [ "$?" != "0" ]
        then
                echo "Merge failed for commit $mCommit"
                echo "Manual merge is needed"
                exit 3
        fi
        # Add a change id using git hook
        git commit --amend --no-edit

}

pushMerged() {
#       echo "pushMerged()"
        git push $PUSH
        if [ "$?" != "0" ]
        then
                echo "Push failed!"
                exit 4
        fi
}

maybe_commit_and_push() {
#       echo "maybe_commit_and_push()"
        cpCommit=$1
        if [ "$cpCommit" == "" ]
        then
                # Nothing to merge currently
                return
        fi
        cpCommitMsg=$2
        if [ "$cpCommitMsg" == "" ]
        then
        	echo "Internal error, no commit message passed to maybe_commit_and_push()"
        	exit 5
        fi
#       echo "maybe_commit_and_push: Merging $cpCommit"
        merge $cpCommit
	echo -e "Merge changes from $FROM_HEAD\n\n$cpCommitMsg"|git commit --amend -F -
        pushMerged
}

can_merge() {
    commit=$1
    git merge --no-commit --no-ff $commit > /dev/null 2>&1
    result=$?
    git reset --hard HEAD  > /dev/null 2>&1
    return $result
}

nothingToCommit=`git status | grep "nothing to commit"`
if [ "$nothingToCommit" == "" ]
then
	git status
	echo "Can not merge when there are unstaged changes."
	exit 6
fi 

git checkout $TO
git fetch

pending=`git log $TO..$FROM_HEAD ^$IGNORE_HEAD --reverse|grep "^commit "|sed "s/commit //"`

pendingCommit=
pendingCommitMessage=
for commit in $pending
do
        echo "Checking $commit..."
        mergeDirective=`git log -n 1 --format=%B $commit|grep "^Merge:"|sed "s/Merge: //"`
        commitMsg=`git log -n 1 --format=oneline --abbrev-commit $commit | sed 's/\\\\/\\\\\\\\/g'` #Multiple levels of unescaping, sed just changes \ to \\
        if [ "$mergeDirective" == "" ]
        then
                if can_merge $commit
                then
                        pendingCommit=$commit
                        pendingCommitMessage=$pendingCommitMessage"$commitMsg\n"
                        echo pendingCommitMessage: $pendingCommitMessage
                else
                        maybe_commit_and_push $pendingCommit "$pendingCommitMessage"
                        pendingCommit=
                        pendingCommitMessage=
                        echo
                        echo "Stopping merge at $commit because of merge conflicts"
                        echo "The following commit must be manually merged."
                        show $commit
                        
                        if [ "$EMAIL_AUTHOR" = "1" ]
                        then
                        	author=`git show --format=%aE -s $commit`
                        	echo "Email sent to $author"
                        	(show $commit ; echo ; git merge $commit) |mail -s "Merge of your commit $commit to $TO failed" $author
                        fi
                        exit 7
		        fi
        elif [ "$mergeDirective" == "no" ]
        then
                maybe_commit_and_push $pendingCommit "$pendingCommitMessage"
                pendingCommit=
                pendingCommitMessage=
                echo
                echo "Doing a no-op merge because of Merge: no for $commit"
                git log -n 1 --format=%B $commit
                echo
                # Do a no-op merge
                git merge $commit -s ours
                echo -e "No-op merge from $FROM_HEAD\n\n$commitMsg"|git commit --amend -F -
                pushMerged
        elif [ "$mergeDirective" == "manual" ]
        then
                maybe_commit_and_push $pendingCommit "$pendingCommitMessage"
                pendingCommit=
                pendingCommitMessage=
                echo
                echo "Stopping merge at $commit (merge: manual)"
                echo "The following commit must be manually merged."
                show $commit
                exit 8
        else
                maybe_commit_and_push $pendingCommit "$pendingCommitMessage"
                pendingCommit=
                pendingCommitMessage=
                echo
                echo "Commit $commit contains an unknown merge directive, Merge: $mergeDirective"
                echo "Stopping merge."
                show $commit
                exit 9
        fi
done

# Push any pending merges
maybe_commit_and_push $pendingCommit "$pendingCommitMessage"
