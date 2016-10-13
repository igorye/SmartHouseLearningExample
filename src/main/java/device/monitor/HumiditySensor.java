package device.monitor;

import env.EnvCondition;
import env.Environment;
import env.Measurable;



/**
 * Класс реализация измерительного устройства - гигрометра
 */
public class HumiditySensor extends MeasuringDevice {

	public HumiditySensor(String description, int power, Environment env) {
		super(1, description, power, env, EnvCondition.Constants.RELATIVE_HUMIDITY);
	}

	public void powerOn() {
		super.powerOn();
		showMsg("powered on");
		showMsg("testing...");
		showMsg("ready");
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
