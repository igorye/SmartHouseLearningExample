package env;

import device.Influencer;

import java.util.*;



/**
 * Класс, описывающий реализацию интерфейса {@Link Environment} - среды(окружения), в которой работает контроллер
 * и устройства. Интерфейс на примитивном уровне описывает физические законы в данной среде.
 */
public class Room implements Environment, Runnable {
	/**
	 * Данные о имеющихся параметрах климата, а так устройствах, находящихся в этой среде и воздействующих на нее.
	 */
	private Map<String, Condition> conditions;
	private List<Influencer> influencers;
	/**
	 * параметр внешнего воздействия на среду
	 */
	volatile double outsideTemp;

	public Room() {
		conditions = new HashMap<>(4);
		conditions.put(EnvCondition.Constants.TEMPERATURE_C, new Condition(EnvCondition.TEMPERATURE, 5, 45));
		conditions.put(EnvCondition.Constants.RELATIVE_HUMIDITY, new Condition(EnvCondition.HUMIDITY, 40, 86));
		conditions.put(EnvCondition.Constants.LIGHT_ILLUMINANCE, new Condition(EnvCondition.ILLUMINANCE, 0, 1000));
		influencers = new ArrayList<>();
		Random rand = new Random(System.currentTimeMillis());
		outsideTemp = rand.nextInt(35);
		outsideTemp *= rand.nextBoolean() ? 1 : -1;
	}

	public Measurable getCondition(String conditionName) {
		return conditions.get(conditionName);
	}

	@Override
	public void addInfluencer(Influencer influencer) {
		influencers.add(influencer);
	}

	@Override
	public void removeInfluencer(Influencer influencer) {
		influencers.remove(influencer);
	}

	@Override
	public void run() {
		Map<String, Condition> conditionsSnapshot;
		while (true) {
//			conditionsSnapshot = new HashMap<>(conditions);
			conditions.values().forEach(this::update);
//			conditions = new HashMap<>(conditionsSnapshot);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void update(Condition condition) {
		Random rand = new Random(System.currentTimeMillis());
		double step = (condition.max - condition.min) / 300;
		double sign = rand.nextBoolean() ? -1f : 1f;
		if(condition.envCondition.equals(EnvCondition.TEMPERATURE)) {
			sign = 1f;
			step = (outsideTemp - condition.value) / 200;
		}
		condition.value += sign*step;
		if (condition.value > condition.max) condition.value = condition.max;
		if (condition.value < condition.min) condition.value = condition.min;
		influencers.stream()
				.filter(influencer -> influencer.getInfluenced().equals(condition.envCondition))
				.forEach(influencer ->  condition.value += (influencer.getActualInfluence()/60f));
	}

	public void setOutSideTemp(double outsideTemp) {
		this.outsideTemp = outsideTemp;
	}

}
