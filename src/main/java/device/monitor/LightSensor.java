package device.monitor;

import env.EnvCondition;
import env.Environment;
import env.Measurable;



/**
 * Класс-наследник {@link MeasuringDevice} описывающий конкретное измерительное устройство
 * и окончательно реализующий интерфейс {@link device.Measurer}
 */
public class LightSensor extends MeasuringDevice {

	public LightSensor(String description, int power, Environment env) {
		super(1, description, power, env, EnvCondition.Constants.LIGHT_ILLUMINANCE);
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
