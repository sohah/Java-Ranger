# this is regression test for passing wbs

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -Xmx4000m -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop1/prop1_pass.jpf >& $DISCOVERYDIR/logs/pass/WBS/WBS_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop2/prop2_pass.jpf >& $DISCOVERYDIR/logs/pass/WBS/WBS_Prop2.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop3/prop3_pass.jpf >& $DISCOVERYDIR/logs/pass/WBS/WBS_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop4/prop4_pass.jpf >& $DISCOVERYDIR/logs/pass/WBS/WBS_Prop4.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/WBS/Prop5/prop5_pass.jpf >& $DISCOVERYDIR/logs/pass/WBS/WBS_Prop5.log