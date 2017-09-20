#! /bin/bash
echo checking and killing open servers

# Find all java processes, except
# * grep, as we're running it
# * get-lock, as that one is just waiting for this cleanup to happen
# * shutdown-and-cleanup, as that could be the one we're running from 
ps x | grep -E bin/java | grep -v grep | grep -v get-lock | grep -v shutdown-and-cleanup | awk '{print $1}' > temp
     
#Read and kill processes marked to temp
while read line
do
  kill -9 $line
done < temp
      
#Remove temp
rm temp
   
if [ -a /home/integration/demo.war ]
  then
    echo removing old demo.war
    rm /home/integration/demo.war
fi
     
echo Cleaning deploy dir
rm -rf /home/integration/deploy/*
