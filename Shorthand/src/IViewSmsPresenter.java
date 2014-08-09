
import java.io.ByteArrayOutputStream;
import javax.microedition.lcdui.Graphics;

/*
 * IViewSmsPresenter.java
 *
 * Created on October 2, 2007, 12:29 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



public interface IViewSmsPresenter {

	/**
	 * Loads the View
	 */
	void load(ViewSmsResponseDTO responseDTO);
        
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

        //CR 13900, 14694
        void setImage(ByteArrayOutputStream byteArrayOutputStream);

//        //CR 12318
//        public void updateChatNotification(String[] msg);
}
