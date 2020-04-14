# this is regression test for passing Infusion

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/src/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -Xmx12288m -ea -Dfile.encoding=UTF-8 -jar /home/soha/git_pass.jpf-core/build/RunJPF.jar '

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop1/prop1_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion_Prop1_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop2/prop2_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion_Prop2_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop3/prop3_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion/Infusion_Prop3_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop4/prop4_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion_Prop4_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop5/prop5_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion_Prop5_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop6/prop6_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion/Infusion_Prop6_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop7/prop7_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop7_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop8/prop8_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop8_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop9/prop9_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop9_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop10/prop10_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop10_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop11/prop11_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop11_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop12/prop12_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop12_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop13/prop13_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop13_pass.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop14/prop14_pass.jpf >& $DISCOVERYDIR/logs/pass/Infusion/Infusion_Prop14_pass.log
