#!/bin/bash
cd `dirname $0`
cd ..
deploy_dir=`pwd`
conf_dir=${deploy_dir}/conf
logs_dir=${deploy_dir}/logs

app_main_class=org.yinan.ddns.server.DDNSApplication

pids=`ps -ef | grep java | grep "$app_main_class" | awk '{print $2}'`
if [[ -n "${pids}" ]]; then
    echo "error: The port is already occupied!"
    echo "pid: $pids"
    exit 1
fi

if [[ ! -d ${logs_dir} ]]; then
    mkdir ${logs_dir}
fi

stdout_file=${logs_dir}/stdout.log
gclog_file=${logs_dir}/gc.log

lib_dir=${deploy_dir}/lib
lib_jars=`ls ${lib_dir} | grep .jar | awk '{print "'${lib_dir}'/"$0}' | xargs | sed "s/ /:/g"`

java_opts=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
java_debug_opts=""
if [[ "$1" = "debug" ]]; then
    java_debug_opts=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4901,server=y,suspend=n "
fi
java_jmx_opts=""
if [[ "$1" = "jmx" ]]; then
    java_jmx_opts=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

java_mem_opts="-server -Xms5120M -Xmx5120M -Xmn1024M -Xnoclassgc -XX:+CMSClassUnloadingEnabled -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -Xloggc:$gclog_file"

echo -e "starting the ddns server ..."
nohup java -Dapp.home=${deploy_dir} ${java_debug_opts}  -classpath ${conf_dir}:${lib_jars} ${app_main_class} > ${stdout_file} 2>&1 &

sleep 1
echo "started"
pids=`ps -ef | grep java | grep "$app_main_class" | awk '{print $2}'`
echo "PID: $pids"
