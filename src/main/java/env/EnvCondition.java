package env;



/**
 * Класс-перечисление характеристик климата
 */
public enum EnvCondition {
	TEMPERATURE (Constants.TEMPERATURE_C,       "C"),
	HUMIDITY    (Constants.RELATIVE_HUMIDITY,   "%"),
	ILLUMINANCE (Constants.LIGHT_ILLUMINANCE,   "lx");

	private final String name;
	private final String unit;

	EnvCondition(String mesured, String unit) {
		this.name = mesured;
		this.unit = unit;
	}

	public static class Constants {
		public static final String TEMPERATURE_C = "temperature";
		public static final String RELATIVE_HUMIDITY = "relative humidity";
		public static final String LIGHT_ILLUMINANCE = "light illuminance";
	}

	public String getName() {
		return name;
	}

	public String getUnit(){
		return unit;
	}

	public boolean equals(EnvCondition other) {
		return name.equals(other.name);
	}
}

