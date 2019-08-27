#!/bin/bash
 cd `dirname $0`
cd ..
deploy_dir=`pwd`
logs_dir=${deploy_dir}/logs
if [[ ! -d ${logs_dir} ]]; then
    mkdir ${logs_dir}
fi
stdout_file=${logs_dir}/stdout.log
pid=`ps -ef | grep -v grep | grep "$deploy_dir/conf" | awk '{print $2}'`
echo "PID: ${pid}"
if [[ -z "$pid" ]]; then
    echo "error: the sever does not started!"
    exit 1
fi

echo -e "stopping the server ..."
kill ${pid} > ${stdout_file} 2>&1
echo "stopped"
echo "pid: ${pid}"