## Performance Management with Spark


### What to do when disk is full
cd $SPARK_HOME/work
rm -rf *

### How to build
sbt --error package

### How to run in the container
Make sure env and config files are copied correctly
docker exec -it ${CONTAINER_NAME} spark-submit --conf spark.jars.ivy=/tmp/.ivy --driver-class-path /opt/bitnami/spark/jars ${JAR_NAME}

### How to run in windows
powershell.exe -File deploy.ps1

### How to run in linux
sh deploy.sh
