package device;



/**
 * Базовый абстрактный класс электроного устройства. Является прародителем любых
 * других типов электронных устройств
 */
abstract public class ElectronicDevice implements Switchable, Runnable {
	final public String description;
	int power;
	final public int devID;

	public ElectronicDevice(String description, int power, int devID) {
		this.description = description;
		this.power = power;
		this.devID = devID;
	}

	protected void showMsg(String msg) {
		System.out.println(String.format("%s: %s", description, msg));
	}

	protected void showErr(String error) {
		System.err.println(String.format("%s: %s", description, error));
	}

	public String toString() {
		return description;
	}

	/**
	 * Абстрактный метод - должен быть реализацован всеми устройствами
	 * и является выразительным проявлением полиморфизма
	 * @see {@link device.Controller::showStatus() void}
	 */
	public abstract void showStatus();
}
