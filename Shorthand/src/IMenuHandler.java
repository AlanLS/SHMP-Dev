/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface IMenuHandler {
    
    
    


    //1- DownSelection
    //2 - UpSelection
    //3 - DisplaySelection
    //0 - Not Do

    byte enableUpSelection();
    byte enableDownSelection();

    //#if KEYPAD
    //|JG|    
    //|JG|    boolean isSearchText(int keyCode);
    //|JG|
    //|JG|    String getSearchTempText();
    //|JG|
    //|JG|    String getSearchText();
    //|JG|
    //|JG|    void resetSearchValue();
    //|JG|
    //|JG|    void handleInput(int keyCode);
    //|JG|    
    //#endif

    void setItemfocuse(byte itemFocuse);

    
    
    void handleOptionSelected(byte sOption);
    
    void sendSelectedValue(int id, String value);
}
