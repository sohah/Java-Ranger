# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib  -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

#-Xmx1024m

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop2/prop2.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop2.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop4/prop4.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop4.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop5/prop5.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop5.log
#runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop6/prop6.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop6.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop7/prop7.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop7.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop8/prop8.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop8.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop9/prop9.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop9.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop10/prop10.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop10.log
