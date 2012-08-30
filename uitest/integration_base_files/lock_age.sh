#! /bin/bash
if lockfile -r0 -! /home/integration/deploy/lock.file &> /dev/null
 then
  # If we could not get the lock, check how old the lock file is
  DATE=$(date +%s)
  # What if the file is not there any more?
  LOCK_AGE=$(stat -c %Z /home/integration/deploy/lock.file)

  AGE=$[($DATE - $LOCK_AGE)/60]

  if [ "$AGE" -gt "20" ]
    then
     echo lock.file is $AGE min old.
     ./cleanup.sh
#    else
#     echo lock.file is $AGE min old.
  fi
 else
   # If we got the lock, do a cleanup (releasing the lock) just in case something has still been left running
   ./cleanup.sh &> /dev/null
fi
