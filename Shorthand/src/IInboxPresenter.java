
import javax.microedition.lcdui.Graphics;



/**
 * 
 * Interface for InboxPresenter canvas class.
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */
public interface IInboxPresenter {

    /**
     * Method to load the InboxCanvas based on the InboxResponse DTO
     * 
     * @param resDTO  An instance of InboxResponseDTO which contains attributes
     *                to load the canvas.
     */
    void load(InboxResponseDTO responseDTO);
    
    void rotateScreen(boolean isLandScape);

    /**
     * Method to unload the canvas
     */
    void unLoad();

    /**
     * Method to load message box based on the type
     * 
     * @param type  Type of the message box.
     *              <li> 0 - smartPopup </li>
     *              <li> 1 - MessageBox (ok)  </li>
     *              <li> 2 - MessageBox (OK) and (Cancel) </li>
     *              <li> 3 - MessageBox (RemindMe) and (Don't Remind Me) </li>
     * 
     * @param msg  Message box message
     */
//    void loadMessageBox(byte type, String msg);

    /**
     * Method to remove a selected item. 
     * 
     * @param msgId  Message Id
     */
    void removeSelectedItem(String messageID);

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
     * Method to select the last accessed item.
     * 
     * @param msgId Previously selected message id.
     **/
    void selectLastAccessedItem(String messageId);

    /**
     * Method to reorder the inbox menu item
     * 
     * @param lasId Last selected id.
     */
    void reorder(String lasId);
    
    void keyPressed(int keycode);
    
    void paintGameView(Graphics g);
    
//    void displayMessageSendSprite();
    
    boolean pointerPressed(int x, int y, boolean isPointed, boolean isReleased, boolean isPressed);

    //CR 12318
//    public void updateChatNotification(String[] msg);
}
