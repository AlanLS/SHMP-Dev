
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;





/**
 * Interface for GetEntryPresenter canvas
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */
public interface IGetEntryPresenter {

    /**
     * Method to load the entry presenter screen
     * 
     * @param responseDTO  An instance of GetEntryResponseDTO which contains
     *                variables that determine the loading/handling of
     *                entry canvas.
     */
    void load(GetEntryResponseDTO responseDTO);
    
    void rotateScreen(boolean isLandScape);

    /**
     * Unloads the view
     */
    void unLoad();

    /**
     * Method to remove a menu item from the view
     * 
     * @param itemId  Item Id of the menu item
     * @param itemName Item Name of the menu item
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
    void changeMenuItemName(String itemId, String itemName);

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
     */
    void selectLastAccessedItem(String itemId);

    /**
     * Method to copy the text to the text box
     * 
     * @param txt Text to be copied
     */
    void copyTextToTextBox(String text,boolean isMaxSet);

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
//    void showNotification(byte isGoto);
    
    void keyPressed(int keycode);
    
    void paintGameView(Graphics g);
    
    byte commandAction(byte priority);
    
//    void displayMessageSendSprite();
    
    boolean pointerPressed(int x, int y, boolean isPointed, boolean isReleased, boolean isPressed);

//    //CR 12318
//    public void updateChatNotification(String[] msg);

    //CR 12118
    //bug 14155
    //bug 14156
    void changeMenuItemName(String itemName, byte type, String msgPlus);

    //CR 14672, 14675
    void refreshList(String[] contacts, int[] contactId);

    void setImage(ByteArrayOutputStream byteArrayOutputStream); //CR 14694
}
