# this is regression test for passing TCAS

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar /home/soha/git_pass.jpf-core/build/RunJPF.jar '

#-Xmx1024m


shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop1/prop1_pass.jpf >& $DISCOVERYDIR/logs/pass/TCAS/Prop1_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop2/prop2_pass.jpf >& $DISCOVERYDIR/logs/pass/TCAS/Prop2_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop3/prop3_pass.jpf >& $DISCOVERYDIR/logs/pass/TCAS/Prop3_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/TCAS/Prop4/prop4_pass.jpf >& $DISCOVERYDIR/logs/pass/TCAS/Prop4_pass.log
