# ContractDR
ContractDR is a specification repair tool. It uses Counterexample-Guided Inductive Repair and Sketching to first find a repair for the specificat and then tighten it, by finding the minimal repair that can be synthesized by the repair sketch.

ContractDR's repairs are sound, semantically minimal that theoretically terminates.

##ContractDR Setup
Assuming you have Java 8, gradle and git installed on your machine, you can follow these steps to setup ContractDR:

1. Clone JPF, and compile it:
   $ git clone https://github.com/javapathfinder/jpf-core.git
   $ cd jpf-core
   $ gradle
    
2. Install ContractDR
   $ cd ..
   $ git clone https://github.com/sohah/ranger-discovery.git
   $ cd ranger-discovery
   $ ant
   $ cd scripts
   $ vim ToolExample.sh
   
3. update '$DISCOVERYDIR, $JPFCORE', and relative directories inside 'runDiscovery' alias to the appropriate 
   'ranger-discovery' home directory installed on your machine. 
   
4. Setup Z3
   a. download Z3 release. ContractDR was tested on release(4.8.7) z3-4.8.7-x64-ubuntu-16.04.zip
   b. extract Z3 into a location of your choice
   c. make an environment variable for Z3_HOME, in .bash_profile
      export Z3_HOME=/z3installation
   d. reload bash_profile
      source ~/.bash_profile

5. Run ContractDR
   a. cd $ContractDR_HOME/script
   b. ./ToolExample.sh
   This command is going to repair a faulty WBS property in $ContractDR_HOME/src/DiscoveryExamples/ToolExample/Prop1/WBS_InvalidProp1. 
   'prop1.jpf' contains the configuration for ContractDR.
   
6. ContractDR's results
   - Repair attempts:
     ContractDR will attempt various dubious expressions:
     
     WBS_InvalidProp1-REXPR-0--1
     
     WBS_InvalidProp1-REXPR-1--1
     
     WBS_InvalidProp1-REXPR-2--1
     
     WBS_InvalidProp1-REXPR-3--1
     
     WBS_InvalidProp1-REXPR-4--1
     
     WBS_InvalidProp1-REXPR-5--1
     
     WBS_InvalidProp1-REXPR-6--1
     
     WBS_InvalidProp1-REXPR-7--1
     
     WBS_InvalidProp1-REXPR-8--1
     
   - Repairs:
     You can find ContractDR's repairs in
     src/DiscoveryExamples/ToolExample/Prop1/wbs_all_prop1.txt
     And generated unique repairs are in
     src/DiscoveryExamples/ToolExample/Prop1/wbs_unique_prop1.txt
     
   - Repair Statistics:
     src/DiscoveryExamples/ToolExample/Prop1/wbs_prop1_stats.txt
