package device;

import env.EnvCondition;
import env.Environment;



/**
 * Релизация интерфейса осветительного прибора. Класс наследует раелизацию поведение абстрактного класса
 * {@Link ElectronicDevice} включая свои характерные признахи и параметры состояния устройства и
 * раелизует интерфейс {@Link Influenser} устройства, оказывающего воздействие на среду
 */
public class Illumination extends ElectronicDevice implements Adjustable, Influencer {
	final private EnvCondition influenced;
	final private double efficiency;
	private final Environment env;
	protected double currentPower;
	private int suspendFactor = 30;
	private int workingFactor = 20;
	protected boolean isOnline;
	static int ID = 1;

	/**
	 * Наследуется реализация создания структуры объекта (конструктора) и добавляются характерные для устройства
	 * данные о состоянии
	 * @param description - описание устройтва
	 * @param power - мощность
	 * @param efficiency - условная величина влияния на параметр климата
	 *                      (в единицу времени исходя при максимальной мощности)
	 * @param env - окружающая среда
	 */
	public Illumination(String description, int power, double efficiency, Environment env) {
		super(description, power, ID++);
		this.env = env;
		influenced = EnvCondition.ILLUMINANCE;
		this.efficiency = efficiency;
	}

	@Override
	public void powerOn() {
		isOnline = true;
		currentPower = suspendFactor;
		showMsg("powered on");
		env.addInfluencer(this);
	}

	@Override
	public void powerOff() {
		suspendFactor = workingFactor;
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

	@Override
	public void setPowerFactor(int factorPercent) {
		workingFactor = factorPercent;
		showMsg(String.format("shining at %d%% power", factorPercent));
		currentPower = workingFactor * power / 100;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Illumination heater = (Illumination) o;
		return devID == heater.devID && description.equals(heater.description);
	}

	@Override
	public void showStatus() {
		showMsg(" is " + (isOnline ? "on" : "off"));
	}

	@Override
	public void run() {

	}
}
