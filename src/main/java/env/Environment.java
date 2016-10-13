package env;

import device.Influencer;


public interface Environment extends Runnable{
	Measurable getCondition(String conditionName);
	void addInfluencer(Influencer influencer);
	void removeInfluencer(Influencer influencer);
}
