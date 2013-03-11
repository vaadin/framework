#!/bin/bash

FROM=origin/7.0
TO=master
PUSH="origin HEAD:refs/for/master"

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
                exit 1
        fi

#       echo "merge($mCommit)"

        git merge $mCommit $2
        if [ "$?" != "0" ]
        then
                echo "Merge failed for commit $mCommit"
                echo "Manual merge is needed"
                exit 2
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
                exit 2
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
#       echo "maybe_commit_and_push: Merging $cpCommit"
        merge $cpCommit
        pushMerged
}

git checkout $TO
git pull --rebase

pending=`git log $TO..$FROM --reverse|grep "^commit "|sed "s/commit //"`

pendingCommit=
for commit in $pending
do
        echo "Checking $commit..."
        mergeDirective=`git log -n 1 --format=%B $commit|grep "^Merge:"|sed "s/Merge: //"`
        if [ "$mergeDirective" == "" ]
        then
                pendingCommit=$commit
        elif [ "$mergeDirective" == "no" ]
        then
                maybe_commit_and_push $pendingCommit
                pendingCommit=
                echo
                echo "Doing a no-op merge for $commit because of Merge: no"
                # Do a no-op merge
                git merge $commit -s ours
                pushMerged
        elif [ "$mergeDirective" == "manual" ]
        then
                maybe_commit_and_push $pendingCommit
                pendingCommit=
                echo
                echo "Stopping merge at $commit (merge: manual)"
                echo "The following commit must be manually merged."
                show $commit
                exit 3
        else
                maybe_commit_and_push $pendingCommit
                pendingCommit=
                echo
                echo "Commit $commit contains an unknown merge directive, Merge: $mergeDirective"
                echo "Stopping merge."
                show $commit
                exit 3
        fi
done

# Push any pending merges
maybe_commit_and_push $pendingCommit
