package device;

import env.Measurable;



/**
 * Интерфейс определяющий устройство, осуществляющее измерение параметров климата
 * окружающей среды.
 */
public interface Measurer {
	String getMeasuredCharacterictic();
	Measurable getMeasured();
}
