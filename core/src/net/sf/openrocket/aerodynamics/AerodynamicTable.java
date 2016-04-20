package net.sf.openrocket.aerodynamics;

import net.sf.openrocket.rocketcomponent.Configuration;

/**
 * This class iterates over values of Mach and Angle Of Attack (AOA) and
 * returns a table of values for Center of Pressure (CP), Coefficient of Drag
 * (C_D) and other computed aerodynamic approximations.
 *
 * <p>The idea behind this is to be able to compare OpenRocket aerodynamics to
 * other systems. Often a qualitative assessment of aerodynamics can be made by
 * looking at the shape of a curve made by charting Mach vs. C_D or other
 * coefficients. Often technical reports of real rockets include these charts.
 * This is a class to create such output.
 *
 * @author Nathan Bergey <nathan.bergey@gmail.com>
 */

public class AerodynamicTable {

	/**
     * Rocket configuration to be iterated over.
	 */
	private static Configuration configuration;

	/**
	 * Aerodynamic calculator to use. There is currently only one in
	 * OpenRocket: OpenRocket's implementation of an extended Barrowman's
	 * method.
	 */
	private static AerodynamicCalculator calculator;

	/** Helper function to create a range of Mach values to iterate over.
	 *
	 * @param start value to begin range
	 * @param stop value to stop range
	 * @param step how much to increment each step of the range
	 * @return list of Mach numbers
	 */
	private static double[] MachRange(final double start, final double stop, final double step) {
		int size = (int) ((stop - start) / step);
		double[] result = new double[size];

		for (int i = 0; i < size; i++) {
			result[i] = start + (step * i);
		}

		return result;
	}

	/**
	 * Constructor.
	 *
	 * @param configuration (required) configuration to compute table for.
	 */
	public AerodynamicTable(final Configuration configuration) {
		this.configuration = configuration;
		this.calculator = new BarrowmanCalculator();
	}

	/**
	 * This is where the work is done. Iterate over Mach and Angle of
	 * Attack.
	 */
	public final void getTable() {
		// Initialize a set of flight conditions (the independent variables in our table)
		FlightConditions conditions = new FlightConditions(this.configuration);

		// Hard coding most to 0 for now
		conditions.setTheta(0);
		conditions.setMach(0);
		conditions.setRollRate(0);
		conditions.setAOA(0, 1);

		// Iterate through the Mach Range
		for (double M : this.MachRange(0.0, 2.0, 0.5)) {
			// Compute forces for these conditions
			conditions.setMach(M);
			AerodynamicForces forces = this.calculator.getAerodynamicForces(this.configuration, conditions, new WarningSet());

			System.out.println(forces.getCD());
		}

	}
}
