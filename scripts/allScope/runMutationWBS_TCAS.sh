# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -Xmx8000m -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/allScope/mutation/WBS/WBS_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/allScope/mutation/WBS/WBS_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/allScope/mutation/TCAS/TCAS_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop2/prop2.jpf >& $DISCOVERYDIR/logs/allScope/mutation/TCAS/TCAS_Prop2.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop4/prop4.jpf >& $DISCOVERYDIR/logs/allScope/mutation/TCAS/TCAS_Prop4.log
