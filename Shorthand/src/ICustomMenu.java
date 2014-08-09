
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface ICustomMenu {
    
    int getNumberOfItem();
    
    void rotateMenu(short pos,byte numberOfMenuItems);
    
    void unLoad();

    void handleMenu(int keyCode);
    
    void drawScreen(Graphics g,byte itemFocused,
            byte lastItemfocused, boolean isMessagebox, String letterCount);
    
    void setMenu(String[] seqList, String[] iNames, String[] tName, 
            int[] ids, int[] styles, boolean isSearch, 
            byte[] displayStyle, int totalItemCount);
    
    void reLoadFooterMenu();
    
    void deInitialize();
    
    byte getSelectedIndex();
    
    int getSmartPopupyPos(int keyCode);
    
    void changeMenuItemName(int itemId, String itemName);

    //CR 12118
    //bug 14155
    //bug 14156
    void updateManuItem(String iName, String dName, byte type, String msgPlus);
    
    void changeMenuItemStyle(int itemId, byte style);
    
    void removeMenuItem(int iId, String iName);
    
    void selectLastAccessedItem(String iName, int iId);
    
    String getSelectedMenuValue();

    String getSelectedDisplayMenuValue();
    
    void setMenuPosition(short pos, byte nMenuItems, byte robyte, boolean  isNotEscapeText);
    
    int getSelectedItemId();
    
    boolean isMenuPresent();
    
    int getMenuPosition(boolean isChecking);
    
    void removeOption();
    
    void selectLastItem();

    boolean pointerPressed(int x, int y, boolean isPointed, 
            boolean isReleased, boolean isPressed);

    //CR 14672, 14675, 14698
    void updateMenuItems(String[] contacts,int[] contactId);
}
