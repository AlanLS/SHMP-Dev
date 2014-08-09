
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class CustomBanner implements IBannerHandler{

    private IMenuHandler menuHandler = null;
    
    private String bannerText = null;
    
    private byte bannerStyle =-1;
    
    private byte rOByte = -1;
    
    private boolean isView = false;
    
    private Timer banner_Timer = null;
    
    private boolean isBannerMove = false;
    
    public CustomBanner(IMenuHandler iMenuHandler){
        menuHandler = iMenuHandler;
    }
    
    public void removeOption(){
        rOByte = -1;
    }
    
    /**
     * 
     * @param g
     * @param itemFocused
     */
    public void drawScreen(Graphics g, byte itemFocused){
        if (null != bannerText) {
            if(itemFocused == UISettings.BANNER && (bannerStyle == 2 || bannerStyle == 3))
                CustomCanvas.drawBanner(bannerText, bannerStyle, true, g,isBannerMove);
            else CustomCanvas.drawBanner(bannerText, bannerStyle, false, g,isBannerMove);
            isBannerMove = false;
        }
    }
    
    /**
     * 
     */
    private void getOption(){
        if (UISettings.lOByte > -1) {
            byte[] optbyte = ObjectBuilderFactory.GetKernel().getOptions(-2, null);
            if (null != optbyte) {
                CustomCanvas.setOptionsMenuArray(optbyte);
                menuHandler.setItemfocuse(UISettings.OPTIONS);
                optbyte = null;
            }
        }
    }
    
    /**
     * 
     * @param keyCode
     */
    public void handleBanner(int keyCode){
        if (UISettings.UPKEY == keyCode) {
            menuHandler.enableUpSelection();
        } else if (UISettings.FIREKEY == keyCode) {
            if (bannerStyle == 2 || bannerStyle == 3) {
                ObjectBuilderFactory.GetKernel().handleAdSelection();
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                menuHandler.handleOptionSelected(UISettings.rOByte);
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.rOByte == 22)
                menuHandler.handleOptionSelected(UISettings.rOByte);
        }else if(keyCode == UISettings.LEFTOPTION){
            getOption();
        } else if(isView) {
            if(keyCode == UISettings.RIGHTARROW){
                menuHandler.handleOptionSelected((byte)27);
            } else if(keyCode == UISettings.LEFTARROW){
                menuHandler.handleOptionSelected((byte)26);
            }
        }
    }
    
    public void pointerPressed(int x, int y){
        
    }
    
    public void pointerDragged(int x, int y){
        
    }
    
    public void pointerReleased(int x, int y){
        
    }
    
    /**
     * 
     */
    public void reLoadFooterMenu(){
        UISettings.rOByte = rOByte;
        UISettings.lOByte = PresenterDTO.setLOptByte();
    }
    
    /**
     * 
     * @return
     */
    public String getBannerText(){
        return bannerText;
    }
    
    /**
     * 
     * @return
     */
    public boolean isBannerSelect(){
        if(bannerStyle == 2 || bannerStyle == 3)
            return true;
        return false;
    }
    
    /**
     * Method to Set the Banner Setting
     * @param text Banner Text
     * @param style Banner Style
     *  <li> 0. No Scroll and No Select </li>
     *  <li> 1. Scroll and No Select </li>
     *  <li> 2. No Scroll and Select </li>
     *  <li> 3. Scroll and Select </li>
     */
    public boolean setBanner(String bText, byte bStyle, byte roByte, boolean isView){
        CustomCanvas.resetBannerPosition();
        this.isView = isView;
        rOByte = roByte;
        bannerText = bText;
        bannerStyle = bStyle;
        startTimer();
        if(null != bText)
            return true;
        return false;
    }
    
    private void startTimer(){
        stopTimer();
        if(null == banner_Timer){
            banner_Timer = new Timer();
            banner_Timer.schedule(new bannerClass(), 100,100);
        }
    }
    
    private void stopTimer(){
        isBannerMove = false;
        if(null != banner_Timer){
            banner_Timer.cancel();
            banner_Timer = null;
        }
    }
    
    /**
     * 
     */
    public void deInitialize(){
        stopTimer();
        bannerText = null;
        rOByte = -1;
        bannerStyle = 0;
        isView = false;
        isBannerMove = false;
    }
    
    public void unLoad(){
        menuHandler = null;
    }

    class bannerClass extends TimerTask{
        public void run(){
            ShortHandCanvas.IsNeedPaint();
            isBannerMove = true;
        }
    }
}
