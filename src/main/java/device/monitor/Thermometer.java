package device.monitor;

import env.EnvCondition;
import env.Environment;
import env.Measurable;



/**
 * Класс устройства - термометра
 * как и большинство других классов-наследников переопределяет реализацию базовых методов,
 * а в некоторых случаях частично использует, и использует данные состояния(поля) базового класса,
 * тем самым устраняя дублирование спецификации классов
 */
public class Thermometer extends MeasuringDevice {

	public Thermometer(String description, int power, Environment env) {
		super(1, description, power, env, EnvCondition.Constants.TEMPERATURE_C);
	}

	public void powerOn() {
		super.powerOn();
		showMsg("powered on");
		super.measure();
	}

	public void powerOff() {
		super.powerOff();
		showMsg("powered off");
	}

	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public String getMeasuredCharacterictic() {
		return MEASURED_CONDITION;
	}

	@Override
	public Measurable getMeasured() {
		return measured;
	}

	@Override
	public void showStatus() {
		showMsg(getMeasures());
	}
}
