#!/bin/bash

# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib  -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

#-Xmx1024m

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery
JPFCONFIG=$1
SPEC=$2
PROP=$3
MUTATIONPOSITION=$4
REPAIRNUM=$5

echo "running configuration of => "${JPFCONFIG}
runDiscovery $JPFCONFIG >& $DISCOVERYDIR/logs/${SPEC}/th_${SPEC}${PROP}_${MUTATIONPOSITION}_${REPAIRNUM}.log
#$DISCOVERYDIR/logs/mutation/${JPFCONFIG}_${REPAIRNUM}