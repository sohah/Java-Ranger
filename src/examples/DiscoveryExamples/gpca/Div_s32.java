package DiscoveryExamples.gpca;

public class Div_s32 {

    static int MAX_int = 2147483647;
    static int MIN_int = -2147483647 - 1;

    public static int div_s32(int numerator, int denominator) {
        int quotient;
        int tempAbsQuotient;
        boolean quotientNeedsNegation;
        if (denominator == 0) {
            quotient = numerator >= 0 ? MAX_int : MIN_int;

            /* Divide by zero handler */
        } else {
            quotientNeedsNegation = ((numerator < 0) != (denominator < 0));
            tempAbsQuotient = (int) (numerator >= 0 ? numerator : -numerator) /
                    (denominator >= 0 ? denominator : -denominator);
            quotient = quotientNeedsNegation ? -(int) tempAbsQuotient : (int)
                    tempAbsQuotient;
        }

        return quotient;
    }

}
