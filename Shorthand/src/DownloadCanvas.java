/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Sasikumar
 */
public class DownloadCanvas implements IDownloadCanvas, IPopupHandler {

    private int imgrotType = 0;
    
    private byte rStart = 0;

   // private String dString = "Apps Downloading. Please Wait...";

    private boolean isDraw = false;

   // private int len = 0;
    
    private ICustomPopup iCustomPopup = null;
    
    private byte itemFocused = 0;
    
    private String[] dText = null;

    private Timer rotateImageTimer = null;

    public DownloadCanvas() {
        iCustomPopup = new CustomPopup(this);
        PresenterDTO.setHdrtxt(ChannelData.getClientName());
    }

  public void paintGameView(Graphics g) {

        if(iCustomPopup.isCustomPopupState()){
            itemFocused = UISettings.POPUPSCREN;
        }

        clearScreen(g);
        
        //Draw background image
       
        CustomCanvas.drawBackgroundImage(g);
        
        CustomCanvas.drawSecondaryHeader("", g,true,false);

        //Draw primary header
        CustomCanvas.drawHeader(g);

        //Draw the rest of the screen and the elements present in it.
        if (null != CustomCanvas.images && null != CustomCanvas.images[UISettings.HG]) {
            drawDisplayImage(g);
        }
        
        if(itemFocused == UISettings.POPUPSCREN && !iCustomPopup.isMessageFocused()){
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
        } else CustomCanvas.DrawOptionsMenu("", (byte)-1, (byte)-1, g);
        
        iCustomPopup.drawScreen(g);
        
    }
  
//   public void displayMessageSendSprite(){
//        iCustomPopup.setMessageSendSpritTimer();
//    }

    private void clearScreen(Graphics g) {
        g.setColor(0x21519c);
        g.fillRect(0, 0, UISettings.formWidth, UISettings.footerHeight);
    }

    private void rotateImage(){
        rStart++;
        if(rStart >= 5){
            imgrotType++;
            if (imgrotType > 3) {
                imgrotType = 0;
            }
            rStart = 0;
        }
    }

    private void drawDisplayImage(Graphics g) {
        int size = CustomCanvas.drawProcessImage(g,imgrotType);
        g.setColor(0xffffff);
        isDraw = !isDraw;
        if (isDraw) {
            if (null != dText) {
                g.drawString(dText[0], (UISettings.formWidth - CustomCanvas.font.stringWidth(dText[0])) / 2, (UISettings.headerHeight) + 20, Graphics.TOP | Graphics.LEFT);
                g.drawString(dText[1], (UISettings.formWidth - CustomCanvas.font.stringWidth(dText[1])) / 2, (UISettings.formHeight - UISettings.footerHeight) - 20, Graphics.TOP | Graphics.LEFT);
//            bug 9562
//            if(dText[0].length()>0)
 //               g.drawString(dText[0], (UISettings.formWidth - CustomCanvas.font.stringWidth(dText[0])) / 2, ((UISettings.formHeight - size) / 2) - CustomCanvas.font.getHeight(), Graphics.TOP | Graphics.LEFT);
            }
        }
    }

    private void stopRotateImageTimer(){
        if(null != rotateImageTimer){
            rotateImageTimer.cancel();
            rotateImageTimer = null;
        }
    }


    private void startRotateImateTimer(){
        rotateImageTimer = new Timer();
        rotateImageTimer.schedule(new DisplayImageTimer(), 0, 100);
    }
    /**
     * Method to handle the keypressed event based on the focus of
     * component
     *
     * @param type
     */
    public void keyPressed(int keyCode) {
        if(itemFocused == UISettings.POPUPSCREN)
            iCustomPopup.keyPressed(keyCode);
    }

    /**
     * Method to load the display canvas
     *
     * @param resDTO An instance of DisplayResponseDTO
     */
//
    public void load(String[] dText) {
        if(null != dText[0] && dText[0].length()>0){
            PresenterDTO.setHdrtxt(dText[0]);
        }
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(dText[1], "",0);
        deInitialize();
        startRotateImateTimer();
        ShortHandCanvas.IsNeedPaint();
    }
    
    public void changeHeaderText(String[] hText){
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(hText[1], "",0);
    }

    /**
     * De Initialize method
     */
    private void deInitialize() {

        stopRotateImageTimer();

        rStart = 0;

        //Int
        imgrotType = 0;
        
        itemFocused = 0;
        
        dText = null;
        
        iCustomPopup.deinitialize();
    }


    /**
     * Method to unload the canvas
     */
    public void unLoad() {
        deInitialize();
    }

//    public void loadMessageBox(byte type, String msg) {
//        if(iCustomPopup.loadMessageBox(type, msg))
//            itemFocused = UISettings.POPUPSCREN;
//    }

//    public void showNotification(byte isGoto) {
//        iCustomPopup.showNotification(isGoto);
//        itemFocused = UISettings.POPUPSCREN;
//    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if(isReLoad)
            itemFocused  = 0;
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    public void handleOptionSelected(byte oIndex) {
        
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType,boolean isReload) {
        if(isReload)
            itemFocused = 0;
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
    }

    public void enablePreviousSelection() {
        
    }

    public int getSmartPopupyPos(int keyCode) {
         return  0;
    }

    public void rotateScreen(boolean isLandScape) {
        iCustomPopup.rotatePopup();
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload,boolean isSet) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

    
    public boolean pointerPressed(int x, int y, boolean isPressed, boolean isReleased) {
        if(itemFocused == UISettings.POPUPSCREN){
            return  iCustomPopup.pointerPressed(x, y, isPressed,isReleased, false);
        }
        return false;
    }

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }

    class DisplayImageTimer extends TimerTask{
        public void run(){
            rotateImage();
            ShortHandCanvas.IsNeedPaint();
        }
    }
}
