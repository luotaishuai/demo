#/bin/bash

ps -ef | grep demo

pid=`netstat -anp | grep 1212 | awk '{printf $7}' | cut -d/ -f1`

if [ ! $pid ] ;then
   echo "端口：1212可以使用"
else
   echo "端口：1212已被占用, 进程：$pid"
   rm -f /opt/demo/demo-1212.log
   kill -9 $pid
fi

sleep 1

ps -ef | grep demo
if [ ! -f "/opt/demo/demo.jar"  ] ;then
    echo "-----------------------------------"
    echo "启动jar包不存在"
else
    echo "-----------------------------------"
    echo "准备就绪"
fi

if [ ! -d "/opt/demo/demo" ] ;then
    echo "--------------------------------------"
    echo "|          从GITHUB上拉取项目           |"
    echo "--------------------------------------"
    cd /opt/demo/
    git clone https://github.com/luotaishuai/demo.git
else
   echo "--------------------------------------"
   echo "项目已经存在，准备拉取最新代码"
fi

cd /opt/demo/demo/
git pull

mvn clean
mvn package

rm /opt/demo/demo.jar
cp /opt/demo/demo/target/demo.jar /opt/demo/demo.jar

sleep 1
cd /opt/demo/

nohup java -jar demo.jar >> demo-1212.log 2>&1  &


echo "-----------------------------------"
echo "|**********     完成     **********|"
echo "-----------------------------------"
tail -f demo-1212.log
