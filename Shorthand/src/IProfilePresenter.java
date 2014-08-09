
import javax.microedition.lcdui.Graphics;



/**
 * Interface for ProfileCanvas
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */
public interface IProfilePresenter {

    /**
     * Method to load the profile presenter canvas
     * 
     * @param responseDTO 
     *           Instance of ProfileResponseDTO. Please refer 
     *           ProfileResponseDTO for the description of its attributes
     */
    void load(ProfileResponseDTO responseDTO);
    
    void rotateScreen(boolean isLandScape);

    /**
     * Method to unload the view
     */
    void unLoad();

    /**
     * Method to load message box
     * 
     * @param type
     *          <li> 1 - Smartpopup without any options that last for 
     *              predefined time </li>
     *          <li> 2,3,5 - Message box with options menu </li>
     *          <li> 4,6 - Notification window </li>
     * @param msg  Message
     */
//    void loadMessageBox(byte type, String msg);

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

    /**
     * Method to rename phone number
     * 
     * @param itemId  Item Id
     * @param itemName   Item Name
     */
    void renameIndexedName(String[] msgUnReadCount);

     /**
     * Method to remove menu item
     * 
     * @param itemId item Id
     * @param itemName item Name
     */
    void removeMenuItem(int itemId, String itemName);

    /**
     * Method to select the last accessed menu item
     * 
     * @param itemName Item Name to be selected
     */
    void selectLastAccessedItem(String itemName);
    
    void keyPressed(int keycode);
    
    void paintGameView(Graphics g);
    
//    void displayMessageSendSprite();
    
    boolean pointerPressed(int x, int y, 
            boolean isPointed, boolean isReleased, boolean isPressed);

    //CR 12318
    public void updateChatNotification(String[] msg);
}
