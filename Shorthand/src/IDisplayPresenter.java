
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;

/**
 * 
 * @author hakuna matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */


/**
 * Interface class for the Display Canvas
 */
public interface IDisplayPresenter {

    /**
     * Method to load the display canvas
     * 
     * @param responseDTO An instance of DisplayResponseDTO which
     *          contains attribute to load the Display Canvas
     */
    void load(DisplayResponseDTO responseDTO);
    
    void rotateScreen(boolean isLandScape);

    /**
     * Unloads the view
     */
    void unLoad();

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
    
    void handleDisplayTimer();
    
    void removeOptions();
    
//    void displayMessageSendSprite();
    
    boolean pointerPressed(int x, int y, boolean isPointed,
            boolean isReleased, boolean isPressed);

    void displayMultiPartMessage(String displayString);

    void invokeTimer();

    void setImage(ByteArrayOutputStream byteArrayOutputStream); //CR 14694

//    //CR 12318
//    public void updateChatNotification(String[] msg);
}
