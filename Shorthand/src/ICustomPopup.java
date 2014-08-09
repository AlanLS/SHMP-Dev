
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface ICustomPopup {

    boolean isCustomPopupState();

//    void setMessageSendSpritTimer();

    void unLoad();

    boolean loadMessageBox();
    
    void handleSmartPopup(int poptype);
    
    void setItemFocused(byte itemFocused);
    
    void keyPressed(int keyCode);
    
    void reLoadFooterMenu();
    
    boolean isMessageFocused();
    
    void showNotification();
    
    void deinitialize();
    
    void drawScreen(Graphics g);
    
    void rotatePopup();
    
    boolean pointerPressed(int x, int y, boolean isPointed, 
            boolean isReleased, boolean isPressed);
}
