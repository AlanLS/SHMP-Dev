
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sasi
 */
public interface IChatPresenter {

    	/**
	 * Loads the View
	 */
	void load(ChatResponseDTO responseDTO);

        void rotateScreen(boolean isLandScape);

	/**
	 * Unloads the view
	 */
	void unLoad();

	/**
	 * Loads the MessageBox
	 */
//	void loadMessageBox(byte type, String msg);

	/**
	 * Shows the Notofication
	 */
//	void showNotification(byte isGoto);

        void keyPressed(int keycode);

        void paintGameView(Graphics g);

//        void displayMessageSendSprite();

        boolean pointerPressed(int x, int y, boolean isPointed, 
                boolean isReleased, boolean isPressed);

        byte commandAction(byte priority);

        boolean updateReceiveMessage(Message messageDto, boolean isNotReceiveMessage);

        //CR 14112
        void setImage(ByteArrayOutputStream byteArrayOutputStream);

            void changeChatStatus(String searchValue,String chatId, int status, int type);
  //13837
//        //CR 12318
//        public void updateChatNotification(String[] msg);

}
