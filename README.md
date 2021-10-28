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