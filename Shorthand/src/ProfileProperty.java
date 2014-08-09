/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Administrator
 */
public class ProfileProperty {
    
    private int puCount = 0;
    
    private boolean isPayForser=true;
    
    private byte interval = 0;  
    
    public byte getInterval() {
        return interval;
    }

    public void setInterval(byte interval) {
        this.interval = interval;
    }

    public int getPuCount() {
        return puCount;
    }

    public boolean isIsPayForser() {
        return isPayForser;
    }

    public void setIsPayForser(boolean isPayForser) {
        this.isPayForser = isPayForser;
    }

    public void setPuCount(int puCount) {
        this.puCount = puCount;
    }

}
