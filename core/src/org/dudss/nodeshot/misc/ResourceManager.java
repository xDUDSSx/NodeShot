package org.dudss.nodeshot.misc;

import org.dudss.nodeshot.Base;

/**A manager class that holds resource amount information*/
public class ResourceManager {
	public int power;
	public int maxPower = 1000;
	
	/**The in-game currency*/
	public int bits;
	
	/**A manager class that holds resource amount information.
	 * @param startPower The initial power.
	 * @param startBits The initial amount of bits
	 */
	public ResourceManager(int startPower, int startBits) {
		power = startPower;
		bits = startBits; 
	}
	
	public int getPower() {
		return power;
	}
	
	public int getBits() {
		return bits;
	}
	
	public void addPower(int add) {
		if (power + add <= maxPower) {
			this.power += add;
		} else {
			if (!Base.infiniteResources) {
				power = maxPower;
			}
		}
	}
	
	public int getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
	}

	public void removePower(int remove) {
		if (power - remove >= 0) {
			this.power -= remove;
		}
	}
	
	public void addBits(int add) {
		this.bits += add;
		//System.out.println("Added Bits " + add);
	}
	
	public void removeBits(int remove) {
		this.bits -= remove;
		if (bits < 0) {
			bits = 0;
		}
		//System.out.println("Removed bits" + remove);
	}
	
	public void setPower(int power) {
		this.power = power;
	}
}
