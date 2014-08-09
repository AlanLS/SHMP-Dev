
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public interface ICaptureImage {

    boolean pointerPressed(int xPosition, int yPosition, boolean isNotDrag,
            boolean isDragEnd, boolean isPressed);

    void drawCaptureImage(Graphics graphics);
    
    boolean isCapture(int fileFormat);

    boolean isCameraScreen();

    boolean isAudioScreen();

    boolean isCurrentScreen();

    boolean loadCamera();

    void keyPressed(int keyCode);

    void rotateScreen();

    void deInitialize(boolean isReEnable);

    void setChatId(String chatId);

    void reLoadFooterMenu();

    byte commandAction(byte priority);

    String SaveImage_Audio(String imageName);

}
