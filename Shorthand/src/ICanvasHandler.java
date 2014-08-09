/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hakuna
 */
public interface ICanvasHandler {
    
//    void loadMessageBox(byte type,String popupText);
    
    void handleSmartPopup(int type);
    
    void reLoadFooterMenu();
    
    void showDateForm();
    
    void showNativeTextbox(int maxChar,byte type,boolean isMask);
    
    void loadSympolPopup();

}
