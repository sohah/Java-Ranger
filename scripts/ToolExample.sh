# this is running the valid properties and using mutation for them

DISCOVERYDIR=/home/soha/git/ranger-discovery
JPFCORE=/home/soha/git/jpf-core/build/RunJPF.jar

alias runDiscovery='LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/home/soha/git/ranger-discovery/lib TARGET_CLASSPATH_WALA=/home/soha/git/ranger-discovery/build/examples/ java -Djava.library.path=/home/soha/git/ranger-discovery/lib -ea -Dfile.encoding=UTF-8 -jar $JPFCORE '

#-Xmx1024m

shopt -s expand_aliases


mkdir $DISCOVERYDIR/src/DiscoveryExamples/ToolExample/log
runDiscovery $DISCOVERYDIR/src/DiscoveryExamples/ToolExample/Prop1/prop1.jpf >& $DISCOVERYDIR/src/DiscoveryExamples/ToolExample/log/Prop1.log
