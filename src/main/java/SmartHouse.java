import device.*;
import device.monitor.HumiditySensor;
import device.monitor.LightSensor;
import device.monitor.Thermometer;
import env.EnvCondition;
import env.Environment;
import env.Room;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/**
 * Класс-модель системы Умный дом.
 * Состоит из контроллера и ряда устройств:
 * измерительные приборы: термометр, гигрометра, датчик освещенности
 * климатическая техника: кондиционер, нагреватель, лампы освещения, увлажнитель
 *
 * Для изменения параметров введите комманду:
 *  комманда [значение]
 *  команды:
 *  - off - выключение системы
 *  - power - задание значения используемой мощности для устройств с возможностью регулировки
 *  - accuracy - точность соблюдения заданных установок параметров климата (гестерезис)
 *  - temp - задание поддерживаемой температуры в комнате
 *  - Temp - задание температуры внешней среды (оказывает влияние на изменение температуры в комнате)
 *  - humidity - задание поддерживаемой влажности в комнате
 *  - light - задание поддерживаемого уровня освещенности в комнате
 *  - status - отображение состояния системы
 *
 *  Без участия пользователя параметры климата меняются в ту или иную сторону со случайными
 *  отклонениями (примитивная модель). Параметры для поддержания по умолчанию:
 *  - температура 25С
 *  - влажность 65%
 *  - освещенность 550lux
 *
 *  Состояние отображается по комманде и каждые 5 секунд. Изменения состояния устройств так же отображаются
 *
 */
public class SmartHouse {

	public static Environment room = new Room();


	public static void main(String[] args) throws InterruptedException {
		ExecutorService es = Executors.newFixedThreadPool(2);
		es.execute(room);
		Controller controller = new Controller("SmartHouseController", 10);
		es.execute(controller);

		controller.registerDevice(new Thermometer("ThermoWatch", 5, room));
		controller.registerDevice(new HumiditySensor("HumidityMonitor", 5, room));
		controller.registerDevice(new LightSensor("BrightnessControl", 5, room));
		controller.registerDevice(new Heater("SunHeat", 2000, 3, room));
		controller.registerDevice(new Conditioner("CoolMaster", 1500, -2, room));
		controller.registerDevice(new Illumination("MagicBright", 100, 1000, room));
		controller.registerDevice(new Humidifier("OceanMoisture", 45, 10, room));

		controller.setCondition(EnvCondition.TEMPERATURE, 25);
		controller.setCondition(EnvCondition.HUMIDITY, 65);
		controller.setCondition(EnvCondition.ILLUMINANCE, 550);

		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			String command = in.nextLine();
			float arg = 0;
			boolean validArg = false;
			String[] commandWithArg = command.split("\\s");
			command = commandWithArg[0];
			if (commandWithArg.length > 1) {
				try {
					arg = Float.valueOf(commandWithArg[1]);
					validArg = true;
				} catch (NumberFormatException e) {
					System.err.printf("invalid command argument - %s%n", commandWithArg[1]);
					continue;
				}
			}
			switch (command) {
				case "off":
					controller.powerOff();
					Thread.sleep(1500);
					System.exit(0);
				case "power":
					controller.setEcoParam((int) arg);
					break;
				case "accuracy":
					controller.setAccuracy(arg);
					break;
				case "status":
					controller.showStatus();
					break;
				case "temp":
					controller.setPrefference(EnvCondition.TEMPERATURE, arg);
					break;
				case "Temp":
					((Room)room).setOutSideTemp(arg);
					break;
				case "humidity":
					controller.setCondition(EnvCondition.HUMIDITY, arg);
					break;
				case "light":
					controller.setCondition(EnvCondition.ILLUMINANCE, arg);
					break;
			}
		}
		es.shutdownNow();
	}
}
