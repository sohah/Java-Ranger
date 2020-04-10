package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

public enum TerminationResult {
    ALREADY_MATCHING, //outer loop related
    NO_VALID_SYNTHESIS_FOR_GRAMMAR, //outer loop related
    OUTERLOOP_TIMED_OUT,
    OUTERLOOP_UNKNOWN,
    TIGHTEST_REACHED,
    MINIMAL_TIMED_OUT,
    MINIMAL_UNKNOWN,
    UNEXPECTED_MINIMAL_RESULT
}
