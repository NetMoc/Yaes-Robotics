package yaes.rcta.scenarios.Basic_Scenario_02;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;
import yaes.world.physical.location.Location;

/**
 * This class creates different environments for different simulations
 * 
 * @author SaadKhan
 *
 */
public class Helper implements constRCTA, Serializable {

	private static final long serialVersionUID = 2927271217505318607L;
	
	public static AbstractHumanAgent agent;

	/**
	 * Initializes the Environment in the scenario
	 * 
	 * @param sip
	 * @param sop
	 */
	public static void initializeEnvironment(SimulationInput sip, SimulationOutput sop, Context context) {
		String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
		String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
		EnvironmentModel envMdl = createEM(obstacleMapFile, backgroundMapFile, context);
		context.setEnvironmentModel(envMdl);
	}

	/**
	 * Utility function to create a specific environment model corresponding to
	 * the RCTA stuff. It is a static function to allow being called from
	 * outside (for instance from unit tests)
	 * 
	 * @param obstacleMapFile
	 * @return
	 */
	public static EnvironmentModel createEM(String obstacleMapFile, String backgroundMapFile, Context context) {

		File fileObstacles = RctaResourceHelper.getFile(obstacleMapFile);
		LinearColorToValue lctv = new LinearColorToValue(0, 100);

		EnvironmentModel retval = new EnvironmentModel("TheModel", 0, 0,
				context.getSimulationInput().getParameterInt(MAP_WIDTH),
				context.getSimulationInput().getParameterInt(MAP_HEIGHT), 1, 1);
		retval.createProperty(MAP_OBSTACLES);
		retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

		if (backgroundMapFile != null) {
			File fileBackground = RctaResourceHelper.getFile(backgroundMapFile);

			try {
				retval.loadBackgroundImage(fileBackground);
			} catch (IOException ioex) {
				TextUi.errorPrint("could not load background image file:" + fileBackground);
			}
		}

		lctv = new LinearColorToValue(0, 100);

		retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

		if (backgroundMapFile != null) {
			File fileBackground = RctaResourceHelper.getFile(backgroundMapFile);
			try {
				retval.loadBackgroundImage(fileBackground);
			} catch (IOException ioex) {
				TextUi.errorPrint("could not load background image file:" + fileBackground);
			}
		}

		return retval;
	}

	/**
	 * Checks if a particular location is occupied or not. Checks for all agents
	 * (robots, civilians, obstacles) returns true if occupied, and false
	 * otherwise.
	 * 
	 * To include zones ( 1-Physical Zone, 2-Personal zone, 3-MovementCone)
	 * 
	 * Note: Not implemented for 3-MovementCone yet.
	 * 
	 * @param loc
	 * @param includeZones
	 * @return
	 */
	public static boolean isLocationOccupied(Context context, Location loc, int includeZones) {
		if (loc == null)
			return false;

		for (AbstractHumanAgent s : context.getHumans()) {
			if (includeZones == 2) {
				if (s.getAzPersonalSpace().getValue(loc) != 0) {
					return true;
				}
			} else if (includeZones == 1) {
				if (s.getAzPhysical().getValue(loc) != 0)
					return true;
			} else {
				if (s.getLocation().equals(loc))
					return true;
			}
		}

		double val = (double) context.getEnvironmentModel().getPropertyAt(MAP_OBSTACLES, loc.getX(), loc.getY());
		// TextUi.println(val);
		if (val > 0)
			return true;

		if (loc.getX() > context.getEnvironmentModel().getXHigh() || loc.getX() < 0
				|| loc.getY() > context.getEnvironmentModel().getYHigh() || loc.getY() < 0)
			return true;
		return false;
	}
	
	public static boolean isLocationOccupied(Context context,AbstractHumanAgent agent, Location loc, int includeZones) {
		if (loc == null)
			return false;

		for (AbstractHumanAgent s : context.getHumans()) {
			if(s.equals(agent))
				continue;
			if (includeZones == 2) {
				if (s.getAzPersonalSpace().getValue(loc) != 0) {
					return true;
				}
			} else if (includeZones == 1) {
				if (s.getAzPhysical().getValue(loc) != 0)
					return true;
			} else {
				if (s.getLocation().equals(loc))
					return true;
			}
		}

		double val = (double) context.getEnvironmentModel().getPropertyAt(MAP_OBSTACLES, loc.getX(), loc.getY());
		// TextUi.println(val);
		if (val > 0)
			return true;

		if (loc.getX() > context.getEnvironmentModel().getXHigh() || loc.getX() < 0
				|| loc.getY() > context.getEnvironmentModel().getYHigh() || loc.getY() < 0)
			return true;
		return false;
	}
}
