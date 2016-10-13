package device;

import env.EnvCondition;
import env.Environment;



/**
 * Класс простейщего устройства-увлажнителя воздуха, наследующего реализацию
 * базового класса {@Link ElectronicDevice} и реализующего интерфейс {@Link Influencer}
 */
public class Humidifier extends ElectronicDevice implements Influencer {
	final private EnvCondition influenced;
	final private double efficiency;
	private final Environment env;
	protected double currentPower;
	protected boolean isOnline;
	static int ID = 1;

	public Humidifier(String description, int power, double efficiency, Environment env) {
		super(description, power, ID++);
		this.env = env;
		influenced = EnvCondition.TEMPERATURE;
		this.efficiency = efficiency;
	}

	@Override
	public void powerOn() {
		isOnline = true;
		currentPower = power;
		showMsg("powered on");
		env.addInfluencer(this);
		showMsg("humidifying");
	}

	@Override
	public void powerOff() {
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
		return efficiency;
	}

	@Override
	public EnvCondition getInfluenced() {
		return influenced;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Humidifier device = (Humidifier) o;
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
