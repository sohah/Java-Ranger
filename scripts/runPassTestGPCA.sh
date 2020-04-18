# this is regression test for passing GPCA_Alarm

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -Xmx4000m -ea -Dfile.encoding=UTF-8 -jar /home/soha/git_pass.jpf-core/build/RunJPF.jar '

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop1/prop1_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop1_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop2/prop2_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop2_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop3/prop3_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop3_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop4/prop4_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop4_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop5/prop5_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop5_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop6/prop6_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop6_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop7/prop7_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop7_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop8/prop8_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop8_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop9/prop9_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop9_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Alarm/Prop10/prop10_pass.jpf >& $DISCOVERYDIR/logs/pass/GPCA/GPCA_Prop10_pass.log
