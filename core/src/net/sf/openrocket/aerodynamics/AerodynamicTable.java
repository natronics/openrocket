package net.sf.openrocket.aerodynamics;

import net.sf.openrocket.aerodynamics.AerodynamicForces;
import net.sf.openrocket.util.BugException;
import net.sf.openrocket.util.Coordinate;

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

	private AerodynamicTable() {
	}

}
