package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput;

import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;

import java.util.ArrayList;
import java.util.List;

//defines the first ssa vairable related to the output ssa variable of an implementation
public class SSAOutToStateInput {

    List<Pair<String, List<String>>> ssaOutToStateInput = new ArrayList<>();

    public void add(String outputName, List<String> inputNames) {
        ssaOutToStateInput.add(new Pair<String, List<String>>(outputName, inputNames));
    }

    public List<String> matchingOutput(String outputName) {
        for (Pair<String, List<String>> pair : ssaOutToStateInput) {
            if (pair.getFirst().equals(outputName))
                return pair.getSecond();
        }
        return null;
    }

}
