#! /bin/sh
 
start(){  
 now=`date "+%Y%m%d%H%M%S"`
 exec java -jar   ./wcs-admin.jar --server.port=8007  5  >/dev/null &
 java -Xms128m -Xmx2048m -jar cmpp.jar 5 > log.log &  
 tail -f result.log
}  
 
stop(){  
 ps -ef|grep java|awk '/wcs-admin.jar/{print $2}'|while read pid
 do  
    kill -9 $pid  
 done  
} 
  
case "$1" in  
start)  
start  
;;  
stop)  
stop  
;;    
restart)  
stop  
start  
;;  
*)  
printf 'Usage: %s {start|stop|restart}\n' "$prog"  
exit 1  
;;  
esac
