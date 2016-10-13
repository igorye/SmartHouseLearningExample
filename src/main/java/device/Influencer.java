package device;

import env.EnvCondition;



/**
 * Интерфейс описывающий устройтство воздействующий на окружающую среду
 * Возможно наследование в более широкий Интерфейс {@Link MultiInfluencer}
 * описывающий устройство, способное влиять одновременно на несколько параметров
 * окружающей среды
 */
public interface Influencer {

	int POSITIVE_INFLUENCE = 1;
	int NEGATIVE_INFLUENCE = -1;
	int NO_INFLUENCE = 0;

	/**
	 * спецификация методов, необходимых конкретной реализации-классу для взаимодействия с ним
	 * - получение данных о величине максимального воздействия на среду
	 * - получение данных о величине текущего уровня воздействия на среду ()
	 * - информация о параметре среды, на которую воздействует устройство
	 */
	double getMaxInfluence();
	double getActualInfluence();
	EnvCondition getInfluenced();
}
