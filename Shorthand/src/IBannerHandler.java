
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface IBannerHandler {


    void drawScreen(Graphics g, byte itemFocused);
    
    void handleBanner(int keyCode);
    
    void reLoadFooterMenu();
    
    void deInitialize();
    
    String getBannerText();
    
    boolean isBannerSelect();
    
    boolean setBanner(String bText, byte bStyle, byte roByte, boolean isView);
    
    void unLoad();
    
    void removeOption();
    
    void pointerPressed(int x, int y);
    
    void pointerDragged(int x, int y);
    
    void pointerReleased(int x, int y);
    
}
