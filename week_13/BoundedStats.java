/**
 * Week 13 - Practice Program 2
 * Bounded Type Parameters: <N extends Number> double average(N[] values)
 */
public class BoundedStats {

    /**
     * Generic method with an upper bounded type parameter (<N extends Number>).
     * Calculates the arithmetic average of any array whose elements extend Number.
     *
     * @param <N> any subtype of java.lang.Number (e.g. Integer, Double, Long, Float)
     * @param values array of numbers
     * @return double representation of the calculated average
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;
        for (N val : values) {
            if (val != null) {
                // Number guarantees the existence of doubleValue() method
                sum += val.doubleValue();
                count++;
            }
        }

        return count == 0 ? 0.0 : sum / count;
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   WEEK 13 - PRACTICAL 2: BOUNDED TYPE STATS      ");
        System.out.println("==================================================");

        // 1. Works for Integer[]
        Integer[] intScores = { 85, 90, 78, 92, 88 };
        double intAvg = average(intScores);
        System.out.printf("1. Average of Integer[] : %.2f\n", intAvg);

        // 2. Works for Double[]
        Double[] dblPrices = { 19.99, 25.50, 4.75, 12.00 };
        double dblAvg = average(dblPrices);
        System.out.printf("2. Average of Double[]  : %.2f\n", dblAvg);

        // 3. Works for Long[]
        Long[] longDistances = { 100000L, 250000L, 400000L };
        double longAvg = average(longDistances);
        System.out.printf("3. Average of Long[]    : %.2f\n", longAvg);

        // 4. Detailed explanation of why non-Number types are refused by compiler
        System.out.println("\n---------------- TECHNICAL NOTE ------------------");
        System.out.println("Why does <N extends Number> refuse non-Number types?");
        System.out.println("1. Compile-Time Type Safety: If we attempt to pass String[] names = {\"A\", \"B\"},");
        System.out.println("   the Java compiler produces a compilation error: 'type argument String is not");
        System.out.println("   within bounds of type-variable N'.");
        System.out.println("2. Method Availability: The bounded constraint <N extends Number> ensures");
        System.out.println("   every element has access to methods defined in java.lang.Number, such as");
        System.out.println("   doubleValue(), intValue(), and longValue(). Without this bound, Java would");
        System.out.println("   treat N as Object, where arithmetic operations are not possible.");
        System.out.println("==================================================");
    }
}
