package device;

import env.EnvCondition;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;



/**
 * @Controller прототип реального объекта - контроллер умного дома, явлется примером реализации принципов ООП.
 * Он предоставляет пользователю(своему клиенту) опосредованно управлять имеющимися климатическими устройствами.
 * Явного управления устройствами нет. Пользователь лиш адресует комманды контроллеру по установке климатических
 * параметров в помещении (речь идет о микроклимате, но для краткости будет применяться понятие "климат").
 * Детали реализации управления устройствами скрыты. Пользователь ограничен перечнем комманд,
 * которые понимает контроллер, составляющими интерфейс взаимодействия.
 * Далее будут описаны лишь некоторые методы. Методы и данные не обозначенные модификатором public не доступны
 * пользователю (сокрыты).
 *
 * Таким образом контроллер инкапсулирует данные (данные об устройствах == состояния) и методы для их изменения.
 *
 */
public class Controller extends ElectronicDevice {
	volatile private ExecutorService es = Executors.newCachedThreadPool();
	/**
	 * Список устройств, доступных контроллеру для управления. Устройство представленно базовым интерфесом
	 * @Switchable - таким образом, контроллер не зависит от особенностей реализации конкретного устройства конкретным
	 * производителем. Минимальная информация об устройстве, доступная и достаточная контроллеру для управления:
	 * - устройство можно включить {@Link powerOn() void}
	 * - устройство можно выключить {@Link powerOff() void}
	 * - узнать включено ли устройство {@Link isOnline() boolean}
	 * - запросить детальное состояние устройства {@Link showStatus() void}
	 *
	 */
	private List<Switchable> devices = new ArrayList<>();

	/**
	 * Список параметров климата, доступные контроллеру через зарегистрированные устройства
	 */
	private Map<EnvCondition, Double> preferences = Collections.synchronizedMap(new HashMap<>());

	/**
	 * точность измерения парамтров
	 */
	private double ACCURACY = 0.5;


	public Controller(String description, int power) {
		super(description, power, Influencer.NO_INFLUENCE);
	}

	/**
	 * возможность изменять точность параметров климата, отслеживаемых контроллером
	 * @param ACCURACY
	 */
	public void setAccuracy(double ACCURACY) {
		this.ACCURACY = ACCURACY;
	}

	/**
	 * Метод регистрации устройства
	 * @param device - устройство добавляемое в систему
	 */
	public void registerDevice(Switchable device) {
		device.powerOn();
		showMsg("testing " + device);
		if (device.isOnline()) {
			devices.add(device);
		}
		device.powerOff();
		es.execute((Runnable) device);
		showMsg(device + " successfully registered");
		if(device instanceof Measurer) device.powerOn();

	}

	public void powerOn() {
		showMsg("powering on");
		devices.forEach(Switchable::powerOn);
		showMsg("powered on");
	}

	public void powerOff() {
		showMsg("powering off");
		devices.forEach(Switchable::powerOff);
		showMsg("powered off");
	}

	public boolean isOnline() {
		return true;
	}

	/**
	 * яркий пример проявления полиморфизма. Запрашиватеся статус всех "включаемых" устройств.
	 * Несмотря на то, что для контроллера они одинаковые - все они реализуют базовый интерфейс
	 * {@Link Switchable}, результат(детальная информация о состоянии) зависит от фактического
	 * типа устройтва - для каждого объета будет вызвана его собственная версия
	 * (переопределенная в его классе) метода {@Link Switchable::showStatus()}
	 */
	public void showStatus() {
		devices.stream().forEach(Switchable::showStatus);
	}

	/**
	 * Получает отклонение параметров климата от заданных пользователем
	 * @param condition - конкретный параметр климата
	 * @param value - сопоставляемое значение
	 * @return - величину отклонения
	 */
	private double desiredConditionDiff(EnvCondition condition, Double value) {
		double cuurentVal = getConditionValue(condition);
		double delta = value - cuurentVal;
		return delta;
	}

	public void setPrefference (EnvCondition condition, double value) {
		preferences.put(condition, value);
	}

	/**
	 * Основной метод управления параметрами климата - выбор, и активация устройтва для воздействия на климат
	 * @param condition - конкретный параметр климата
	 * @param value - желаемая величина
	 */
	public void setCondition(EnvCondition condition, double value) {
		double delta = desiredConditionDiff(condition, value);
//		showErr(String.format("Desired condition [%s %5.2f] (delta is %5.2f)", condition, value, delta));
		if (Math.abs(delta) < ACCURACY) return;
		else if (delta > 0) increase(condition); else decrease(condition);
	}

	/**
	 * Ряд служебных методов для выбора конкретного типа устройства. Как упоминалось, контроллер не знает конкретного типа
	 * устройства. Однако он имеет информацию (предположения) о характере устройств по назначению, с которыми он может
	 * взаимодействовать посредством ряда интерфейсов. Устройства подразделяются на:
	 * @Switchable - упоминался ранее
	 * @Adjustable - устройство с возможностью регулирования параметров работы устройства
	 * @Measurer - устройтсва контроля параметров климата
	 * @influencer - устройства влияния на параметры климата
	 *
	 * @param condition
	 * @param influenceSign
	 * @return
	 */
	private Predicate<Influencer> matchesInfluence(EnvCondition condition, int influenceSign) {
		return influencer -> influencer.getInfluenced().equals(condition)
				                     && influenceSign * influencer.getMaxInfluence() > 0;
	}

	private Predicate<Influencer> matchesCondition(EnvCondition condition) {
		return influencer -> influencer.getInfluenced().equals(condition);
	}

	private Predicate<Switchable> activeInfluencer() {
		return device -> device.isOnline() && device instanceof Influencer;
	}

	private Predicate<Switchable> inactiveInfluencer() {
		return device -> device instanceof Influencer && !device.isOnline();
	}

	private Predicate<Switchable> influencerDevice() {
		return device -> device instanceof Influencer;
	}

	private Consumer<Influencer> influencerOff() {
		return influencer -> ((Switchable) influencer).powerOff();
	}

	private Consumer<Influencer> influencerOn() {
		return influencer -> ((Switchable) influencer).powerOn();
	}

	private Function<Switchable, Influencer> castToInfluenser() {
		return device -> (Influencer) device;
	}

	private Function<Switchable, Adjustable> castToAdjustable() {
		return device -> (Adjustable) device;
	}

	private Predicate<? super Switchable> adjustableDevice() {
		return device -> device instanceof Adjustable;
	}

	/**
	 * Методы раелизующие действия для воздействия на параметры климата - уменьшения или увеличения значения
	 * характеристик
	 * @param condition - конкретный параметр климата
	 */
	private void decrease(EnvCondition condition) {
		//power off heaters
		devices.stream().filter(activeInfluencer())
				.map(castToInfluenser())
				.filter(matchesInfluence(condition, Influencer.POSITIVE_INFLUENCE))
				.forEach(influencerOff());
		devices.stream().filter(inactiveInfluencer())
				.map(castToInfluenser())
				.filter(matchesInfluence(condition, Influencer.NEGATIVE_INFLUENCE))
				.forEach(influencerOn());
	}

	private void increase(EnvCondition condition) {
		Collection<Influencer> influencers = devices.stream().filter(activeInfluencer())
				                                     .map(castToInfluenser())
				                                     .filter(matchesInfluence(condition, Influencer.NEGATIVE_INFLUENCE))
				                                     .collect(Collectors.toList());
		influencers.forEach(influencerOff());
		influencers = devices.stream()
				              .filter(inactiveInfluencer())
				              .map(castToInfluenser())
				              .filter(matchesInfluence(condition, Influencer.POSITIVE_INFLUENCE))
				              .collect(Collectors.toList());
		influencers.forEach(influencerOn());

	}

	private void cancelInfluence(EnvCondition condition, int influenceKind) {
		devices.stream().filter(activeInfluencer())
				.map(castToInfluenser())
				.filter(matchesInfluence(condition, influenceKind))
				.forEach(influencerOff());
	}

	private double getConditionValue(EnvCondition condition) {
		return devices.stream()
				       .filter(switchable -> switchable instanceof Measurer)
				       .map(switchable -> (Measurer) switchable)
				       .filter(measurer -> measurer.getMeasured().getEnvCondition().equals(condition))
				       .mapToDouble(measurer -> measurer.getMeasured().getValue())
				       .average().getAsDouble();
	}

	/**
	 * Прототип метода изменяющий параметры устройтсв, реализующих итерфейс {@Link Adjustable}
	 * @param arg
	 */
	public void setEcoParam(int arg) {
		if (arg > 100) arg = 100;
		double d = arg;
		Collection<Adjustable> adj = devices.stream()
				                             .filter(influencerDevice())
				                             .filter(adjustableDevice())
				                             .map(castToAdjustable())
				                             .collect(Collectors.toList());
		final int finalArg = arg;
		adj.forEach(adjustable -> adjustable.setPowerFactor(finalArg));
	}

	/**
	 * Метод реализующий интерфейс {@Link Runnable}, выражающий основную задачу работы контроллера - контроль параметров
	 * климата и воздейтсвие на их величину посредством имеющихся устройств.
	 */
	@Override
	public void run() {
		long statusRate = 0;
		while(true) {
			if(statusRate++ % 5 == 0) showStatus();
			preferences.forEach((condition, value) -> {
				double delta = desiredConditionDiff(condition, value);
				if (Math.abs(delta) < ACCURACY) cancelInfluence(condition, (int) Math.signum(delta));
				else if (Math.abs(delta) > (1.2 * ACCURACY)) setCondition(condition, value);
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				es.shutdownNow();
			}
		}
	}

}
