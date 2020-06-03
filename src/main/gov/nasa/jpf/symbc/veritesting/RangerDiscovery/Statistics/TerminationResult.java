package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

public enum TerminationResult {
    ALREADY_MATCHING, //outer loop related
    NO_VALID_SYNTHESIS_FOR_GRAMMAR, //outer loop related
//    OUTERLOOP_TIMED_OUT,
    OUTERLOOP_EXISTS_UNKNOWN,
    OUTERLOOP_FORALL_UKNOWN,
    OUTERLOOP_MAX_LOOP_REACHED,
    TIGHTEST_REACHED,
//    MINIMAL_TIMED_OUT,
    MINIMAL_FORALL_UNKNOWN, //can be because of timeout as well from jkind.
    MINIMAL_EXISTS_UKNOWN,
    MINIMAL_MAX_LOOP_REACHED,
    UNEXPECTED_MINIMAL_RESULT,
    OTHER_JKIND_EXCEPTION,
    MUTANT_TIME_OUT,
    TRUE_FOR_MAX_STEPS
}
