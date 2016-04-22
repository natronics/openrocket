package net.sf.openrocket.aerodynamics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
 * <p>A table (list of lists) can be used internally using the getTable
 * method, or to simply write a report call writeTable which will call
 * getTable for you and then write the contents to disk.
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

	/**
	 * Helper function to create a range of Mach values to iterate over.
	 *
	 * @param start value to begin range
	 * @param stop value to stop range
	 * @param step how much to increment each step of the range
	 * @return list of Mach numbers
	 */
	private static double[] machRange(final double start, final double stop, final double step) {
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
	 *
	 * @return List of list of doubles of the 2D table of results
	 */
	public final ArrayList<ArrayList<Double>> getTable() {

		// Initialize space for the table, a list of list of doubles
		ArrayList<ArrayList<Double>> table = new ArrayList<ArrayList<Double>>();

		// Initialize a set of flight conditions
		FlightConditions conditions = new FlightConditions(this.configuration);

		// Set initial conditions, some may be changed in loop.
		conditions.setTheta(0);
		conditions.setMach(0);
		conditions.setRollRate(0);
		conditions.setAOA(0, 1);

		// Iterate through a range of Mach numbers
		// TODO: Configure mach range
		for (double mach : this.machRange(0.0, 3.01, 0.01)) {
			// Values for this row of data stored here
			ArrayList<Double> row = new ArrayList<Double>();

			// Compute the forces for these conditions
			conditions.setMach(mach);
			AerodynamicForces forces = this.calculator.getAerodynamicForces(this.configuration, conditions, new WarningSet());

			// Build row of data
			row.add(mach);
			row.add(forces.getCD());           // Drag
			row.add(forces.getCP().length());  // CP distance from nose
			row.add(forces.getCN());           // Normal
			row.add(forces.getCNa());          // Normal/alpha

			// Add row to table
			table.add(row);
		}
		return table;
	}

	/**
	 * Computes and writes a table of aerodynamics to disk.
	 *
	 * @param outputFile File to write to. Will be overwritten if exists!
	 */
	public final void writeTable(final File outputFile) {
		// Wrap in a try-catch to try and deal with FileIO problems
		try (BufferedWriter bw = new BufferedWriter(
			new OutputStreamWriter(
			new FileOutputStream(outputFile.getAbsoluteFile()), "UTF-8"))) {

			outputFile.createNewFile();

			// Generate the table
			ArrayList<ArrayList<Double>> table = this.getTable();

			// Write the header to the file
			bw.write("# Mach, CD, CP [meters, 0=nosecone], CN, CNa\n");

			// Loop through the table
			for (ArrayList<Double> row : table) {
				Iterator<Double> rowiterator = row.iterator();
				if (rowiterator.hasNext()) {

					// Only on the first element of the row, no comma and less
					// precision. This is always the Mach field.
					bw.write(String.format("%.3f", rowiterator.next()));

					// Print rest of row with comma separator
					while (rowiterator.hasNext()) {
						bw.write(",");
						bw.write(String.format("%.6f", rowiterator.next()));
    				}
				}
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
