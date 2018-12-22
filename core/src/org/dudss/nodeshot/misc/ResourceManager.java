package org.dudss.nodeshot.misc;

/**A manager class that holds resource amount information*/
public class ResourceManager {
	public int power;
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
		this.power += add;
	}
	
	public void removePower(int remove) {
		this.power -= remove;
	}
	
	public void addBits(int add) {
		this.bits += add;
	}
	
	public void removeBits(int remove) {
		this.bits -= remove;
	}
	
	public void setPower(int power) {
		this.power = power;
	}
}
