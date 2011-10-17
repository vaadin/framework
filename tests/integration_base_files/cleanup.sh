echo checking and killing open servers
    
ps x | grep -E bin/java | grep -v grep | grep -v get-lock | awk '{print $1}' > temp
     
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
