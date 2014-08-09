/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface IPopupHandler {

    void handleNotificationSelected(boolean isReLoad,boolean isSend);
    
    void handleOptionSelected(byte oIndex);
    
    void handleMessageBoxSelected(boolean isSend,byte msgType,boolean isReload);

    //#if KEYPAD
    //|JG|    void handleSymbolpopup(char selSymbol,boolean isReload,boolean isSet);
    //#endif
    
    void enablePreviousSelection();
    
    int getSmartPopupyPos(int keyCode);
    
}
