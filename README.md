# ContractDR
ContractDR is a specification repair tool. It uses Counterexample-Guided Inductive Repair and Sketching to first find a repair for the specification and then tighten it, by finding the minimal repair that the repair sketch can synthesize.

ContractDR's repairs are sound, semantically minimal, that theoretically terminates.

## ContractDR Setup
Assuming you have Java 8, gradle, and git installed on your machine, you can follow these steps to setup ContractDR:

1. Clone JPF, and compile it:

   <code> $ git clone https://github.com/javapathfinder/jpf-core.git </code>
   
   <code> $ cd jpf-core </code>
   
   <code> $ gradle </code>
    
2. Install ContractDR

   <code> $ cd .. </code>
   
   <code> $ git clone https://github.com/sohah/ranger-discovery.git </code>
   
   <code> $ cd ranger-discovery </code>
   
   <code> $ ant </code>
   
   <code> $ cd scripts </code>
   
   <code> $ vim ToolExample.sh </code>
   
   
3. update <code>$DISCOVERYDIR, $JPFCORE</code>, and relative directories inside <code>runDiscovery</code> alias to the appropriate 
   <code>ranger-discovery</code> home directory installed on your machine. 
   
4. Setup Z3
   * download Z3 release. ContractDR was tested on release(4.8.7) z3-4.8.7-x64-ubuntu-16.04.zip
   * extract Z3 into a location of your choice
   * make an environment variable for <code>Z3_HOME</code>, in <code>.bash_profile</code>
      <code>export Z3_HOME=/z3installation</code>
   * reload bash_profile
      <code>source ~/.bash_profile</code>
     
5. Setup JKIND
   * ContractDR includes JKIND version 4.08, which is what it was tried on. For more infomration about JKIND please visit https://github.com/loonwerks/jkind.

6. Run ContractDR
   * <code>cd $ContractDR_HOME/script</code>
   * <code>./ToolExample.sh</code>
   This command is going to repair a faulty WBS property in <code>$ContractDR_HOME/src/DiscoveryExamples/ToolExample/Prop1/WBS_InvalidProp1. </code>
   <code>prop1.jpf</code> contains the configuration for ContractDR.
   
7. ContractDR's results
   * Repair attempts:
     ContractDR will attempt various dubious expressions:
     
      * WBS_InvalidProp1-REXPR-0--1
     
     * WBS_InvalidProp1-REXPR-1--1
     
     * WBS_InvalidProp1-REXPR-2--1
     
     * WBS_InvalidProp1-REXPR-3--1
     
     * WBS_InvalidProp1-REXPR-4--1
     
     * WBS_InvalidProp1-REXPR-5--1
     
     * WBS_InvalidProp1-REXPR-6--1
     
     * WBS_InvalidProp1-REXPR-7--1
     
     * WBS_InvalidProp1-REXPR-8--1
     
   * Repairs:
     You can find ContractDR's repairs in
     <code>src/DiscoveryExamples/ToolExample/Prop1/wbs_all_prop1.txt</code>
     
     And generated unique repairs are in
     <code>src/DiscoveryExamples/ToolExample/Prop1/wbs_unique_prop1.txt</code>
     
   * Repair Statistics:
     <code>src/DiscoveryExamples/ToolExample/Prop1/wbs_prop1_stats.txt</code>
