package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

public enum TerminationResult {
    ALREADY_MATCHING, //outer loop related
    NO_VALID_SYNTHESIS_FOR_GRAMMAR, //outer loop related
    OUTERLOOP_TIMED_OUT,
    OUTERLOOP_UNKNOWN,
    OUTERLOOP_MAX_LOOP_REACHED,
    TIGHTEST_REACHED,
    MINIMAL_TIMED_OUT,
    MINIMAL_UNKNOWN,
    MINIMAL_MAX_LOOP_REACHED,
    UNEXPECTED_MINIMAL_RESULT
}
