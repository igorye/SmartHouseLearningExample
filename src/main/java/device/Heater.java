package device;

import env.EnvCondition;
import env.Environment;



/** Класс реализация устройства-нагреватель
 *
 */
public class Heater extends ElectronicDevice implements Adjustable, Influencer {
	final private EnvCondition influenced;
	final private double efficiency;
	private final Environment env;
	protected double currentPower;
	private int suspendFactor = 10;
	private int workingFactor = 20;

	protected boolean isOnline;
	static int ID = 1;

	public Heater(String description, int power, double efficiency, Environment env) {
		super(description, power, ID++);
		this.env = env;
		influenced = EnvCondition.TEMPERATURE;
		this.efficiency = efficiency;
		suspendFactor = workingFactor;
	}

	@Override
	public void powerOn() {
		isOnline = true;
		setPowerFactor(suspendFactor);
		showMsg("powered on");
		env.addInfluencer(this);
	}

	@Override
	public void powerOff() {
		setPowerFactor(10, true);
		env.removeInfluencer(this);
		isOnline = false;
		showMsg("powered off");
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public double getMaxInfluence() {
		return efficiency;
	}

	@Override
	public double getActualInfluence() {
		return efficiency*currentPower/power;
	}

	@Override
	public EnvCondition getInfluenced() {
		return influenced;
	}

	public void setPowerFactor(int factorPercent, boolean save) {
		int tmpFactor = workingFactor;
		setPowerFactor(factorPercent);
		if(save) suspendFactor = tmpFactor;
	}

	@Override
	synchronized public void setPowerFactor(int factorPercent) {
		suspendFactor = factorPercent;
		int sign = -(int) Math.signum(workingFactor - factorPercent);
		if(isOnline) {
			while (Math.signum(workingFactor - factorPercent)*sign < 0) {
				workingFactor += 10*sign;
				showMsg(String.format("heating at %d%% power", (int) workingFactor));
				currentPower += power * workingFactor;
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			workingFactor = factorPercent;
			suspendFactor = workingFactor;
		}
		currentPower = workingFactor * power / 100;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Heater device = (Heater) o;
		return devID == device.devID && description.equals(device.description);

	}

	@Override
	public void showStatus() {
		showMsg(" is " + (isOnline ? "on" : "off"));
	}

	@Override
	public void run() {

	}
}
