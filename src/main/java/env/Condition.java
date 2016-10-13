package env;



/**
 * Реализация интерфейса {@Link Measurable} - измеряемой физической величины - характеристики климата
 */
public class Condition implements Measurable{
	protected EnvCondition envCondition;
	/**
	 * Здесь и в некоторых других классах применяются модификаторы открытого доступа
	 * для простоты реализации, поскольку не изменяемы и, в принципе, доступны любому
	 * пользователю или системе (как ,например, информация на шильдике устройства)
	 */
	public final double min;
	public final double max;
	volatile protected double value;


	public Condition(EnvCondition condition, double min, double max) {
		envCondition = condition;
		this.min = min;
		this.max = max;
	}

	public String getName() {
		return envCondition.getName();
	}

	public String getUnit() {
		return envCondition.getUnit();
	}

	@Override
	public EnvCondition getEnvCondition() {
		return envCondition;
	}

	public double getValue() {
		return value;
	}

	public String toString() {
		return String.format("%s %5.2f %s", envCondition.getName(), value, envCondition.getUnit());
	}

}
