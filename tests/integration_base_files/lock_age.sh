#! /bin/bash
if [ -a /home/integration/deploy/lock.file ]
 then
  DATE=$(date +%s)
  LOCK_AGE=$(stat -c %Z /home/integration/deploy/lock.file)

  AGE=$[($DATE - $LOCK_AGE)/60]

  if [ "$AGE" -gt "15" ]
    then
     echo lock.file is $AGE min old.
     ./cleanup.sh
#    else
#     echo lock.file is $AGE min old.
  fi
fi
