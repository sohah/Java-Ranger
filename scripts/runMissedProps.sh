# trying to repair invalid properties

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

#-Xmx1024m

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop4/prop4.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop4.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop7/prop7.jpf >& $DISCOVERYDIR/logs/mutation/GPCA/GPCA_Prop7.log


runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop1.log


runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/mutation/WBS/WBS_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/mutation/WBS/WBS_Prop3.log
