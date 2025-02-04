# this is running the valid properties and using mutation for them

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar /home/soha/git/jpf-core/build/RunJPF.jar '

#-Xmx1024m

shopt -s expand_aliases

DISCOVERYDIR=/home/soha/git/ranger-discovery


runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop10/prop10.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop10.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop11/prop11.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop11.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop12/prop12.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop12.log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/GPCA_Infusion/Prop13/prop13.jpf >& $DISCOVERYDIR/logs/mutation/Infusion/Infusion_Prop13.log
