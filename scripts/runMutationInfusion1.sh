# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '


#-Xmx1024m


shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery

#runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop1/prop1.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop1.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop2/prop2.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop2.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop3/prop3.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop3.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop5/prop5.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop5.log
#runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop6/prop6.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop6.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop7/prop7.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop7.log
