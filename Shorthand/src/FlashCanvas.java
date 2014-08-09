
import generated.Build;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;

public class FlashCanvas implements IFlashPresenter, IPopupHandler {

    private int rtype = 0;

    //Timer for displaying this screen.
    private Timer sAppTimer = null;
    private byte itemFocused = 0;
    //Left option text
    private byte loptxt = 15;
    //Right option text
    private byte ropttxt = 8;
    private boolean msgSel = true;
    private ICustomPopup iCustomPopup = null;
    private int iposition = 0;
    public int[] color = new int[1];

    public FlashCanvas() {
        iCustomPopup = new CustomPopup(this);
    }

    private void drawImage(Graphics g) {
        PresenterDTO.isBgGob = true;
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
        if(null != CustomCanvas.splash){
            CustomCanvas.drawSplash(g);
        } else  {
            CustomCanvas.drawBackgroundImage(g);
        }
    }

    private void start() {
        sAppTimer.cancel();
        try {
            CustomCanvas.Initialize();
            ObjectBuilderFactory.GetKernel().startApplication();
            ObjectBuilderFactory.GetKernel().launchApplication();
        } catch (Exception e) {
        }
    }

    /**
     * De-initialize method.
     */
    public void unLoad() {
        //   iCustomPopup.deinitialize();
        if (null != sAppTimer) {
            try {
                sAppTimer.cancel();
            } catch (Exception e) {
            }
            sAppTimer = null;
        }
        rtype = 0;
    }

    public void paintGameView(Graphics g) {

        if(iCustomPopup.isCustomPopupState()){
            itemFocused = UISettings.POPUPSCREN;
        }

        g.setColor(0x21519c);
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);

        if (null == sAppTimer) {
//            Logger.debugTestingOnError("Height1 = "+ObjectBuilderFactory.getPCanvas().getHeight()+" Width1 ="+
//                    ObjectBuilderFactory.getPCanvas().getWidth());
            UISettings.formHeight = (short) (ObjectBuilderFactory.getPCanvas().getHeight());
            UISettings.formWidth = (short) ObjectBuilderFactory.getPCanvas().getWidth();
            if(UISettings.GENERIC){
                if(UISettings.formHeight<=160 && UISettings.formWidth<=128) //CR 8579
                    CustomCanvas.setSmallFont();
            }
        }

        drawImage(g);

        if (itemFocused == UISettings.MESSAGEBOX) {
            CustomCanvas.drawMessageBox(g, loptxt, ropttxt, msgSel);
        }

        iCustomPopup.drawScreen(g);

        if (null == sAppTimer) {
            sAppTimer = new Timer();
            sAppTimer.schedule(new Start(), (Build.SPLASH_TIMER * 400) + 20);
        }
    }

    public void keyPressed(int keyCode) {
        if (itemFocused == UISettings.POPUPSCREN) {
            iCustomPopup.keyPressed(keyCode);
        }
    }

//    public void loadMessageBox(byte type, String msg) {
//        iCustomPopup.loadMessageBox(type, msg);
//        itemFocused = UISettings.POPUPSCREN;
//    }

//    public void displayMessageSendSprite() {
//        iCustomPopup.setMessageSendSpritTimer();
//    }

    /**
     *
     * @author Hakuna Matata
     * @version 1.00.15
     * @copyright (c) SmartTouch Mobile Inc
     */
    class Start extends TimerTask {

        /**
         * Run method to start the timer
         */
        public void run() {
            start();
        }
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
    }

    public void handleOptionSelected(byte oIndex) {
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType, boolean isReload) {
        if (isReload) {
            itemFocused = 0;
        }
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend, msgType);
    }

    public void enablePreviousSelection() {
    }

    public int getSmartPopupyPos(int keyCode) {
        return 0;
    }

    public void rotateScreen(boolean isLandScape) {
        iCustomPopup.rotatePopup();
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload, boolean isSet) {
    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }
}
