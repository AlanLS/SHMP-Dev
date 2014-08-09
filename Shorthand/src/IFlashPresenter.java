/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Hakuna
 */
public interface IFlashPresenter {
    
//    void loadMessageBox(byte msgType,String msg);
    
    void keyPressed(int keyCode);
    
    void paintGameView(Graphics g);
    
    void unLoad();
    
//    void displayMessageSendSprite();
    
    void rotateScreen(boolean isLandScape);

}
