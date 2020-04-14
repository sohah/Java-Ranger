# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/src/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -Xmx12288m -ea -Dfile.encoding=UTF-8 -jar /home/soha/git_pass.jpf-core/build/RunJPF.jar '

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop1/prop1_pass.jpf >& $DISCOVERYDIR/logs/TCAS_Prop1_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop2/prop2_pass.jpf >& $DISCOVERYDIR/logs/TCAS_Prop2_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop3/prop3_pass.jpf >& $DISCOVERYDIR/logs/TCAS_Prop3_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop4/prop4_pass.jpf >& $DISCOVERYDIR/logs/TCAS_Prop4_pass.log
