package ca.myairbuddyandi;

/**
 * Created by Michel on 2020-09-03.
 * Empty stub but must contained all methods of MyCalcBlending
 */

public class MyCalcBlending {

    // Static
    private static final String LOG_TAG = "MyCalcBlending";

    // Public

    // Protected

    // Private

    // End of variables

    // Public constructor
    public MyCalcBlending() {
    }

    // Blending functions

    public int getBleedingPressure(int pressureStart, double mixO2Start, double mixHeStart, int pressureDesired, double mixO2Desired, double mixHeDesired, Double mixTopOff) {
        double pressureStartHe = pressureStart * mixHeStart / 100;
        double n2Fraction = (100.0 - mixO2Start - mixHeStart) / 100.0;
        double pressureStartAir = pressureStart * n2Fraction / (1.0 - (mixTopOff / 100));
        double pressureStartO2 = pressureStart - pressureStartHe - pressureStartAir;

        double pressureDesiredHe = pressureDesired * mixHeDesired / 100;
        n2Fraction = (100.0 - mixO2Desired - mixHeDesired) / 100.0;
        double pressureDesiredAir = pressureDesired * n2Fraction / (1.0 - (mixTopOff / 100));
        double pressureDesiredO2 = pressureDesired - pressureDesiredHe - pressureDesiredAir;

        // Check if pressureDesiredHe < pressureStartHe
        // And calculate He bleeding pressure
        double bleedingPressureHe = pressureStart;
        if (pressureDesiredHe < MyConstants.ZERO_I) {
            bleedingPressureHe = MyConstants.MINUS_ONE_I;
        } else if (pressureDesiredHe < pressureStartHe) {
            bleedingPressureHe = (pressureDesiredHe / pressureStartHe) * pressureStart;
        }

        // Check if pressureDesiredAir < pressureStartAir
        // And calculate He bleeding pressure
        double bleedingPressureAir = pressureStart;
        if (pressureDesiredAir < MyConstants.ZERO_I) {
            bleedingPressureAir = MyConstants.MINUS_ONE_I;
        } else if (pressureDesiredAir < pressureStartAir) {
            bleedingPressureAir = (pressureDesiredAir / pressureStartAir) * pressureStart;
        }

        // Check if pressureDesiredAir < pressureStartAir
        // And calculate He bleeding pressure
        double bleedingPressureO2 = pressureStart;
        if (pressureDesiredO2 < MyConstants.ZERO_I) {
            bleedingPressureO2 = MyConstants.MINUS_ONE_I;
        } else if (pressureDesiredO2 < pressureStartO2) {
            bleedingPressureO2 = (pressureDesiredO2 / pressureStartO2) * pressureStart;
        }

        // Find the bleeding pressure
        double bleedingPressure;
        bleedingPressure = Math.min(bleedingPressureHe, Math.min(bleedingPressureAir, bleedingPressureO2));

        if (bleedingPressure == pressureStart) {
            // No need to bleed the tank
            return pressureStart;
        } else {
            return (int) bleedingPressure;
        }
    }

    public int getPressureDesired(int pressureStart, double mixO2Start, double mixHeStart, double mixO2Desired, double mixHeDesired, Double mixTopOff) {
        double pressureStartHe = pressureStart * mixHeStart / 100;
        double n2Fraction = (100.0 - mixO2Start - mixHeStart) / 100.0;
        double pressureStartAir = pressureStart * n2Fraction / (1.0 - (mixTopOff / 100));
        double pressureStartO2 = pressureStart - pressureStartHe - pressureStartAir;

        int pressureDesired = pressureStart - MyConstants.ONE_I;
        double pressureDesiredHe;
        double pressureDesiredAir;
        double pressureDesiredO2;
        boolean whileCondition = true;

        do {
            pressureDesired += MyConstants.ONE_D;

            pressureDesiredHe = pressureDesired * mixHeDesired / 100;
            n2Fraction = (100.0 - mixO2Desired - mixHeDesired) / 100.0;
            pressureDesiredAir = pressureDesired * n2Fraction / (1.0 - (mixTopOff / 100));
            pressureDesiredO2 = pressureDesired - pressureDesiredHe - pressureDesiredAir;

            // Check if the mix is possible
            if (pressureDesiredHe < MyConstants.ZERO_I || pressureDesiredAir < MyConstants.ZERO_I || pressureDesiredO2 < MyConstants.ZERO_I) {
                pressureDesired = MyConstants.MINUS_ONE_I;
            }

            if (pressureDesired == MyConstants.MINUS_ONE_I) {
                whileCondition = false;
            } else if (pressureDesiredHe >= pressureStartHe && pressureDesiredAir >= pressureStartAir && pressureDesiredO2 >= pressureStartO2) {
                whileCondition = false;
            }

        } while (whileCondition);

        return pressureDesired;
    }

    public int getPressureO2Desired(int pressureStart, Double mixO2Start, Double mixO2Desired, Double mixO2Fill, Double mixTopOff) {
        int pressureDesired = pressureStart - MyConstants.ONE_I;
        double pressureFill;

        do {
            pressureDesired += MyConstants.ONE_D;
            pressureFill = getPressureO2Fill(pressureStart, mixO2Start, pressureDesired, mixO2Desired, mixO2Fill, mixTopOff);
        } while (pressureStart + pressureFill >= pressureDesired || (pressureFill <= MyConstants.ZERO_D));

        if (mixO2Start <= mixO2Desired) {
            pressureFill = MyFunctions.roundUp(pressureFill, 0);
            return (int) pressureFill;
        } else {
            return pressureDesired;
        }
    }

    public int getPressureHeFill(int pressureStart, Double mixHeStart, int pressureDesired, Double mixHeDesired) {
        // mixHeFill should be 100%
        double pressureFill = (pressureDesired * mixHeDesired / 100) - (pressureStart * mixHeStart / 100);
        pressureFill = MyFunctions.roundUp(pressureFill,0);
        return (int) pressureFill;
    }

    public int getPressureO2Fill(int pressureStart, Double mixO2Start, int pressureDesired, Double mixO2Desired, Double mixO2Fill, Double mixTopOff) {
        // mixO2Fill should be 100%
        // mixTopOff should be 20.9%
        double pressureFill = ((pressureDesired * (mixO2Desired - mixTopOff)) - (pressureStart * (mixO2Start - mixTopOff))) / (mixO2Fill - mixTopOff);
        pressureFill = MyFunctions.roundUp(pressureFill,0);
        return (int) pressureFill;
    }

    public int getPressureO2Fill(int pressureStart, Double mixO2Start, int pressureDesired, Double mixO2Desired, Double mixTopOff, int pressureHeStart) {
        // mixTopOff should be 20.9%
        int psiLeftToFill = pressureDesired - pressureHeStart;
        double o2Needed = (pressureDesired * mixO2Desired / 100) - (pressureStart * mixO2Start / 100);
        double ratio = o2Needed / psiLeftToFill;
        double pressureFill =  (ratio - (mixTopOff / 100)) / (1 - (mixTopOff / 100)) * psiLeftToFill;
        return (int) pressureFill;
    }

    public int getPressureStart(int pressureDesired, Double mixDesired, Double mixStart, Double mixTopOff) {
        double pressureStart = (pressureDesired * (mixDesired - mixTopOff)) / (mixStart - mixTopOff);
        pressureStart = MyFunctions.roundUp(pressureStart,0);
        return (int) pressureStart;
    }

    public int getPressureStart(Double mixO2Start, int pressureDesired, Double mixO2Desired, Double mixO2Fill, Double mixTopOff) {

        double pressureStart = pressureDesired + MyConstants.ONE_D;
        double pressureFill;

        do {
            pressureStart -= MyConstants.ONE_D;
            pressureFill = ((pressureDesired * (mixO2Desired - mixTopOff)) - (pressureStart * (mixO2Start - mixTopOff))) / (mixO2Fill - mixTopOff);
        } while (pressureStart + pressureFill >= pressureDesired || (pressureFill <= MyConstants.ZERO_D));

        pressureStart = MyFunctions.roundUp(pressureStart, 0);
        return (int) pressureStart;
    }

    public int getPressureStart(Double mixO2Start, Double mixHeStart, int pressureDesired, Double mixO2Desired, Double mixHeDesired, Double mixTopOff) {

        double pressureDesiredHe = pressureDesired * mixHeDesired / 100;
        double n2Fraction = (100.0 - mixO2Desired - mixHeDesired) / 100.0;
        double pressureDesiredAir = pressureDesired * n2Fraction / (1.0 - (mixTopOff / 100));
        double pressureDesiredO2 = pressureDesired - pressureDesiredHe - pressureDesiredAir;

        int pressureStart = pressureDesired + MyConstants.ONE_I;
        double pressureStartHe;
        double pressureStartAir;
        double pressureStartO2;
        boolean whileCondition = true;

        do {
            pressureStart -= MyConstants.ONE_D;

            pressureStartHe = pressureStart * mixHeStart / 100;
            n2Fraction = (100.0 - mixO2Start - mixHeStart) / 100.0;
            pressureStartAir = pressureStart * n2Fraction / (1.0 - (mixTopOff / 100));
            pressureStartO2 = pressureStart - pressureStartHe - pressureStartAir;

            // Check if the mix is possible
            if (pressureDesiredHe < MyConstants.ZERO_I || pressureDesiredAir < MyConstants.ZERO_I || pressureDesiredO2 < MyConstants.ZERO_I) {
                pressureDesired = MyConstants.MINUS_ONE_I;
            }

            if (pressureDesired == MyConstants.MINUS_ONE_I) {
                whileCondition = false;
            } else if (pressureStartHe <= pressureDesiredHe && pressureStartAir <= pressureDesiredAir && pressureStartO2 <= pressureDesiredO2) {
                whileCondition = false;
            }

        } while (whileCondition);

        return pressureStart;
    }
}
