# trying to repair invalid properties

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

#-Xmx1024m
shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop4/prop4.jpf >& $DISCOVERYDIR/logs/InvalidProp/Infusion_Prop4.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop14/prop14.jpf >& $DISCOVERYDIR/logs/InvalidProp/Infusion_Prop14.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/InvalidProp/TCAS_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop2/prop2.jpf >& $DISCOVERYDIR/logs/InvalidProp/WBS_Prop2.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop4/prop4.jpf >& $DISCOVERYDIR/logs/InvalidProp/WBS_Prop4.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop5/prop5.jpf >& $DISCOVERYDIR/logs/InvalidProp/WBS_Prop5.log

