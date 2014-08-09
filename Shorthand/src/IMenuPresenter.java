
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;



/**
 * Interface for MenuPresenter canvas class
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */
public interface IMenuPresenter {

    /**
     * Method to load the menu presenter screen
     * 
     * @param responseDTO  An instance of MenuResponseDTO which contains
     *                variables that determine the loading/handling of
     *                menu canvas.
     */
    void load(MenuResponseDTO responseDTO);
    
    void rotateScreen(boolean isLandScape);

    /**
     * Method to unload the view
     */
    void unLoad();

    /**
     * Method to change menu item style
     * 
     * @param itemId  Item id of the menu item whose name needs to be changed
     * @param style  style of the item
     *               <li> 0 - white foreground colour </li>
     *               <li> 1 - grey foreground colour </li>
     *               <li> 2 - red foreground colour </li>
     */
    void changeMenuItemStyle(int itemId, byte style);

    /**
     * Method to remove menu item
     * 
     * @param iId item Id
     * @param iName item Name
     */
    void removeMenuItem(int itemId, String itemName);

    /**
     * Method to rename the menu item. Method adds the item edit box to the view
     * 
     * @param itemId  Item Id of the menu item
     * @param itemName Item Name of the menu item
     */
    //#if KEYPAD
    //|JG|    void renameMenuItem(int itemId, String itemName);
    //#endif

    /**
     * Method to change a menu item name
     * 
     * @param itemId  Item id of the menu item whose name needs to be changed
     * @param itemName New item name
     */
    void changeMenuItemName(int itemId, String itemName);

    /**
     * Method to load message box
     * @param type
     *          <li> 1 - Smartpopup without any options that last for 
     *              predefined time </li>
     *          <li> 2,3,5 - Message box with options menu </li>
     *          <li> 4,6 - Notification window </li>
     * @param msg  Message
     */
//    void loadMessageBox(byte type, String msg);

     /**
     * Method to select the last accessed menu item
     * 
     * @param iName Item name to be selected
     * @param itemId   Item id.
     */
    void selectLastAccessedItem(String iName, int itemId);

    /**
     * Method to show notification. This function internally calls the the
     * handlesmartpopup with the type defined for notification window
     * 
     * @param isGoTo Boolean to indicate whether the notification window should
     *               have "Goto" option or not
     * 
     * @param dmsg   Notification message
     *               
     * @param param  String Array which consists of two elements
     *               <li> Element 0 - To represents whether the notification
     *                    is raised for message arrival or scheduler invocation
     *               </li>
     *               <li> Element 1 - Gives you the message id incase of 
     *                    message or sequence name in case of scheduler
     *               <li>
     */
//    void showNotification(byte isGoTo);
    
    void keyPressed(int keycode);
    
    void paintGameView(Graphics g);
    
//    void displayMessageSendSprite();

    byte commandAction(byte priority);
    
    boolean pointerPressed(int x, int y, boolean  isPointed, 
            boolean isReleased, boolean isPressed);

    void setImage(ByteArrayOutputStream byteArrayOutputStream); //CR 14694

//    //CR 12318
//    public void updateChatNotification(String[] msg);
}
