package device.monitor;

import device.ElectronicDevice;
import device.Measurer;
import env.Measurable;
import env.Environment;



/**
 * Абстрактный класс-наследник базовой реализации электронного устройства, задающего базовую
 * реализацию для отдельного класса устройств - измерительных приборов. И частично реализующего
 * интерфейс {@link Measurer}. Класс абстрактный, поскольку, хотя и содержит базовые данные,
 * описываюие возможное состояние устройтсва, и методы для изменения состояния, но может
 * существовать лишь как прототип для других реализаций классов (устройств), с конкретными
 * специфичными данными. Конкретные типы измерительных устройств наследуются от этого класса
 */
abstract public class MeasuringDevice extends ElectronicDevice implements Measurer, Runnable {
	protected Measurable measured;
	Environment measuredEnv;
	protected boolean isOnline;
	final protected String MEASURED_CONDITION;
	static int ID = 0;

	protected MeasuringDevice(int startID, String description, int power, Environment env, String conditionName) {
		super(description, power, startID);
		if (ID == 0) {
			ID = ++startID;
		}
		MEASURED_CONDITION = conditionName;
		measuredEnv = env;
	}

	@Override
	public void powerOff() {
		isOnline = false;
	}

	@Override
	public void powerOn() {
		isOnline = true;
	}

	protected void measure() {
		measured = measuredEnv.getCondition(MEASURED_CONDITION);
	}

	public String getMeasures() {
		return String.format("%s %5.2f%s", measured.getName(), measured.getValue(), measured.getUnit());
	}

	/**
	 * жизненный цикл работы Измерителя - сообщать о своем состоянии (измеренных данных)
	 */
	@Override
	public void run() {
		while (true) {
			showStatus();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
