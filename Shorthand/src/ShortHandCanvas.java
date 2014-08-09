
import generated.Build;
import generated.RP;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import jg.JgCanvas;
import javax.microedition.lcdui.Graphics;
import jg.Resources;

public class ShortHandCanvas extends JgCanvas implements CommandListener {

    boolean isTimer = false;
    boolean isAppStart = true;
    private byte cust = -1;
    private int lastPressed;
    //private boolean isNotification = false;
    private static IMenuPresenter iMenuPresenter = null;
    private IProfilePresenter iProfilePresenter = null;
    private IGetEntryPresenter iGetEntryPreseneter = null;
    private IDisplayPresenter iDisplayPresenter = null;
    private IViewSmsPresenter iViewPresenter = null;
    private IInboxPresenter iInboxPresenter = null;
    private IDownloadCanvas iDownloadPresenter = null;
    private IFlashPresenter iFlashPresenter = null;
    private IChatPresenter iChatPresenter = null;
    //CR 14727
    private IUserProfilePresenter iUserProfilePresenter = null;

    private byte pState = -1;
    private boolean isRefresh = false;
//    private Display display = null;
    private boolean isDAPP = false;
    //private boolean isExitCalled = true;
    private boolean isExit = false;
    private boolean isLandScpage = false;
//    private boolean isNotPointer = true;
    public static boolean isNeedPaint = true;
    private boolean isPressedAndReleased = false;
    private byte paintingTime = 0;
    private byte selectTime = -1;
    private int dragX = -1;
    private int dragY = -1;
    private boolean isMultiTouch = false;

//    public static long currentDate = -1;

    /**
     *
     * @param midlet - Main Shorthand Display Midlet
     * @param keypad - Mobile KeypadType
     * @param dURl - Initial download Url
     * @param serName - Application download From Which Server(YASA/DOODAD)
     * @param serCode - Server code(56767/95495)
     * @param pNumber - application wakeup portNumber
     * @param pName - Applciation Provenence Name
     * @param cUrl - Application Compress URL
     */
    public ShortHandCanvas(Shorthand midlet) {
        super(midlet);
        ObjectBuilderFactory.setProgramCanvas(midlet, this);
        this.setFullScreenMode(true);
        pointerInputSetEventsEnabled(true);
        cust = 0;
        setGenericBuildProperty();
        iFlashPresenter = new FlashCanvas();
        isMultiTouch = System.getProperty("com.nokia.mid.ui.multipointtouch.version") != null;
//        currentDate = Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat());
//        setArabicProperty();
    }

//    public void ChangeLanguage(){
//        if(UISettings.TEXT_DIRECTION_SYMBOL == 1){
//            UISettings.TEXT_LEFT_DIRECTION =  Graphics.RIGHT;
//            UISettings.TEXT_RIGHT_DIRECTION = Graphics.LEFT;
//            UISettings.TEXT_DIRECTION_SYMBOL = -1;
//            UISettings.TEXT_START_X_POSITION = UISettings.formWidth;
//            int rightKey = UISettings.LEFTOPTION;
//            UISettings.LEFTOPTION = UISettings.RIGHTOPTION;
//            UISettings.RIGHTOPTION = rightKey;
//        } else {
//            UISettings.TEXT_LEFT_DIRECTION =  Graphics.LEFT;
//            UISettings.TEXT_RIGHT_DIRECTION = Graphics.RIGHT;
//            UISettings.TEXT_DIRECTION_SYMBOL = 1;
//            UISettings.TEXT_START_X_POSITION = 0;
//        }
//    }

//    private void setArabicProperty(){
//        String local = System.getProperty("microedition.locale");
////        String[] RIGHT_TO_LEFT = {
////            "ar", // Arabic
////            "az", // Azerbaijani
////            "he", // Hebrew
////            "jv", // Javanese
////            "ks", // Kashmiri
////            "ml", // Malayalam
////            "ms", // Malay
////            "pa", // Panjabi
////            "fa", // Persian
////            "ps", // Pushto
////            "sd", // Sindhi
////            "so", // Somali
////            "tk", // Turkmen
////            "ug", // Uighur
////            "ur", // Urdu
////            "yi" // Yiddish
////        };
//        if(null != local){
////            local = "en_ar";
//            local = local.toLowerCase();
//            if(local.indexOf("_ar")>-1 || local.indexOf("_az")>-1 || local.indexOf("_he")>-1 ||
//                    local.indexOf("_jv")>-1 || local.indexOf("_ks")>-1 || local.indexOf("_ml")>-1 ||
//                    local.indexOf("_ms")>-1 || local.indexOf("_sd")>-1 || local.indexOf("_ug")>-1 ||
//                    local.indexOf("_pa")>-1 || local.indexOf("_so")>-1 || local.indexOf("_ur")>-1 ||
//                    local.indexOf("_fa")>-1 || local.indexOf("_tk")>-1 || local.indexOf("_yi")>-1 ||
//                    local.indexOf("_ps")>-1){
//                UISettings.TEXT_LEFT_DIRECTION =  Graphics.RIGHT;
//                UISettings.TEXT_RIGHT_DIRECTION = Graphics.LEFT;
//                UISettings.TEXT_DIRECTION_SYMBOL = -1;
//                UISettings.TEXT_START_X_POSITION = UISettings.formWidth;
//                int rightKey = UISettings.LEFTOPTION;
//                UISettings.LEFTOPTION = UISettings.RIGHTOPTION;
//                UISettings.RIGHTOPTION = rightKey;
//            } else local = null;
//        }
//
//        if(null == local){
//            UISettings.TEXT_LEFT_DIRECTION =  Graphics.LEFT;
//            UISettings.TEXT_RIGHT_DIRECTION = Graphics.RIGHT;
//            UISettings.TEXT_DIRECTION_SYMBOL = 1;
//            UISettings.TEXT_START_X_POSITION = 0;
//        }
//
//    }

    private void setGenericBuildProperty() {
        int keyCode = getKeyCode(UP);
        String kName = "";
        if (UISettings.GENERIC) {
            if(keyCode>0){ //Its For Nokia and Default
                UISettings.UPKEY = -1;
                UISettings.DOWNKEY = -2;
                UISettings.LEFTARROW = -3;
                UISettings.RIGHTARROW = -4;
                UISettings.FIREKEY = -5;
                UISettings.LEFTOPTION = -6;
                UISettings.RIGHTOPTION = -7;
            } else { // Its for all other mobiles
                UISettings.UPKEY = keyCode;
                UISettings.DOWNKEY = getKeyCode(DOWN);
                UISettings.LEFTARROW = getKeyCode(LEFT);
                UISettings.RIGHTARROW = getKeyCode(RIGHT);
                keyCode = getKeyCode(FIRE); //CR 8578
                UISettings.FIREKEY = keyCode;
                kName = getKeyName(keyCode).toUpperCase();
                if ( !kName.equals("SELECT") && !kName.equals("FIRE") ) // Canvas.FIRE is mapped to a weird physical key, so lets hardcode it
                	UISettings.FIREKEY = -5;
                UISettings.LEFTOPTION = (keyCode-1);
                UISettings.RIGHTOPTION = (keyCode-2);
            }
            int count = 0;
            keyCode = 0;
            while (keyCode <= 255 && count<2) {
                try {
                    kName = getKeyName(keyCode).toUpperCase();
                    if ((kName.indexOf("SOFT") > -1) || (kName.indexOf("SELECTION") > -1)) {
                        if ((kName.indexOf("1") > -1) || (kName.indexOf("LEFT") > -1)) {
                            UISettings.LEFTOPTION = keyCode;
                            count++;
                        } else if ((kName.indexOf("2") > -1) || (kName.indexOf("RIGHT") > -1)) {
                            UISettings.RIGHTOPTION = keyCode;
                            count++;
                        }
                    }
                } catch (Exception e) { }

                try {
                    kName = getKeyName(keyCode*-1).toUpperCase();
                    if ((kName.indexOf("SOFT") > -1)  || (kName.indexOf("SELECTION") > -1)) {
                        if ((kName.indexOf("1") > -1) || (kName.indexOf("LEFT") > -1)) {
                            UISettings.LEFTOPTION = (keyCode*-1);
                            count++;
                        } else if ((kName.indexOf("2") > -1) || (kName.indexOf("RIGHT") > -1)) {
                            UISettings.RIGHTOPTION = (keyCode*-1);
                            count++;
                        }
                    }
                } catch (Exception e) { }
                keyCode++;
            }
        }
        //SOFT3 key identification for the nativetextbox
        keyCode = UISettings.FIREKEY;
        if(keyCode>0){
            keyCode = -8;
        } else keyCode-=2;
        try{
            kName = getKeyName(keyCode).toUpperCase();
            if((kName.indexOf("SOFT")>-1) || kName.indexOf("SELECTION")>-1){
                if(kName.indexOf("3")>-1){
                    UISettings.HASTHIRDSOFTKEY = true;
                }
            }
        }catch(Exception e){}
    }

    public Object getScreenObject(int state) {
        Object obj = null;
        if (1 == state) {
            iProfilePresenter = new ProfileCanvas();
            pState = cust;
            obj = iProfilePresenter;
        } else if (2 == state) {
            iInboxPresenter = new InboxCanvas();
            pState = cust;
            obj = iInboxPresenter;
        } else if (3 == state) {
            iMenuPresenter = new MenuCanvas();
            pState = cust;
            obj = iMenuPresenter;
        } else if (4 == state) {
            iGetEntryPreseneter = new EntryCanvas();
            pState = cust;
            obj = iGetEntryPreseneter;
        } else if (5 == state) {
            iDisplayPresenter = new DisplayCanvas();
            pState = cust;
            obj = iDisplayPresenter;
        } else if (6 == state) {
            iViewPresenter = new ViewSmsCanvas();
            pState = cust;
            obj = iViewPresenter;
        } else if (7 == state) {
            //startKey = false;
            iDownloadPresenter = new DownloadCanvas();
            pState = cust;
            obj = iDownloadPresenter;
        } else if(8 == state){
            iChatPresenter = new ChatCanvas();
            pState = cust;
            obj = iChatPresenter;
        } else if(9 == state){
            iUserProfilePresenter = new UserProfileCanvas();
            pState = cust;
            obj = iUserProfilePresenter;
        }
        cust = (byte) state;
        isNeedPaint = true;
        return obj;

    }

    private void unLoadPreseneter() {
        if (pState == 0) {
            iFlashPresenter.unLoad();
            iFlashPresenter = null;
        } else if (pState == 7) {
            iDownloadPresenter.unLoad();
            iDownloadPresenter = null;
        }
    }

    public void onSystemEvent(int event) {
        if (event == SYSTEM_EVENT_START) {
            // Create MediaManager; allocate background music
            // loading the Resources
            CustomCanvas.images = Resources.getGobs(RP.GOB_SMART);
            CustomCanvas.smartOne = Resources.getGobs(RP.GOB_SMART1);
            //CR 12985
            CustomCanvas.splash = Resources.getGobs(RP.GOB_SPLASH);
            //CustomCanvas.eMotic = Resources.getGobs(RP.GOB_EMOTIC);
            
            //CR 12985
            Resources.getImage(RP.IMG_SPLASH).getRGB(CustomCanvas.splashRGB, 0, 1, 1, 1, 1, 1);
            Resources.getImage(RP.IMG_SMART).getRGB(CustomCanvas.gobRGB, 0, 1, 1, 1, 1, 1);

            SoundManager.getInstance().setData(RP.SND_S);

            Resources.deactivatePack();
        } else if (event == SYSTEM_EVENT_INTERRUPT) {
//            currentDate = Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat());
//            Logger.debugOnError("PAUSE");
            //isNeedPaint = true;
            //pointerInputSetEventsEnabled(true);
        // set the pause mode
        } else if (event == SYSTEM_EVENT_RESUME) {
//            currentDate = Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat());
//            Logger.debugOnError("RESUME");
            isNeedPaint = true;
//            pointerInputSetEventsEnabled(true);
//            ObjectBuilderFactory.GetKernel().pendingMsgSnd();
        // Perform any processing required when the application begins running again
        // NOTE: it's not guaranteed that audio will be available here, so resume it after a resume audio event
        } else if (event == SYSTEM_EVENT_RESUME_AUDIO) {
        // Restart background music when resuming from a system event
//           SoundManager.mediaManager.setEnabled(true);
        // TODO mediaManager.startChannel(CHANNEL_SOUND, MEDIA_ID_MUSIC);
        } else if(event == SYSTEM_EVENT_EXIT) {
            isNeedPaint = true;
//            if(currentDate == Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat())){
                Logger.debugOnError("GOING TO EXIT");
                //CR 13278
                ObjectBuilderFactory.GetKernel().exitShorthand();
                isExit = true;
//            } else {
//                event = SYSTEM_EVENT_RESUME;
//                currentDate = Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat());
//            }
        } else if (event == SYSTEM_EVENT_CANVAS_SIZE_CHANGED) {
            isNeedPaint = true;
            paintingTime = 1;
            rotateScreen();
        }
        super.onSystemEvent(event);
    }

    public static void IsNeedPaint() {
        isNeedPaint = true;
    }

    public static void isNoNeedPaint() {
        isNeedPaint = false;
    }

    public void tickGameLogic() {
        keyUpdateStates();
        try {
            if (isExit) {
                exitShortHand();
            } else {
                for (int i = 0; i < pointerInputGetEventCount() && pointerInputPopNextEvent(); i++) {
                    processPointerInputEvent();
                    isNeedPaint = true;
                }

                if (selectTime >= 0) {
                    if (selectTime == 0) {
                        if(UISettings.GENERIC)
                            keyPressedCustom(UISettings.FIREKEY);
                        else keyPressedCustom(JG_KEY_SELECT);
                    }
                    selectTime--;
                    isNeedPaint = true;
                }

                if (keyIsAnyTyped()) {
                    isNeedPaint = true;
                    if (UISettings.GENERIC) {
                        keyPressedCustom(lastPressed);
                    } else {
                        if (Build.BLACK_KEY_CODE != 0 && Build.BLACK_KEY_CODE == lastPressed) {
                            return;
                        } else if (keyIsPressed(JG_KEY_BACK)) {
                            keyPressedCustom(JG_KEY_BACK);
                        } else if (keyIsPressed(JG_KEY_CLEAR)) {
                            keyPressedCustom(JG_KEY_CLEAR);
                        } else if (keyIsPressed(JG_KEY_DOWN)) {
                            keyPressedCustom(JG_KEY_DOWN);
                        } else if (keyIsPressed(JG_KEY_UP)) {
                            keyPressedCustom(JG_KEY_UP);
                        } else if (keyIsPressed(JG_KEY_LEFT)) {
                            keyPressedCustom(JG_KEY_LEFT);
                        } else if (keyIsPressed(JG_KEY_RIGHT)) {
                            keyPressedCustom(JG_KEY_RIGHT);
                        } else if (keyIsPressed(JG_KEY_SOFTKEY_LEFT)) {// || lastPressed == UISettings.LEFTOPTIONKEY) {
                            keyPressedCustom(JG_KEY_SOFTKEY_LEFT);
                        } else if (keyIsPressed(JG_KEY_SOFTKEY_RIGHT)) {// || lastPressed == UISettings.RIGHTOPTIONKEY) {
                            keyPressedCustom(JG_KEY_SOFTKEY_RIGHT);
                        } else if (keyIsPressed(JG_KEY_WHEEL_DOWN)) {
                            keyPressedCustom(JG_KEY_DOWN);
                        } else if (keyIsPressed(JG_KEY_WHEEL_UP)) {
                            keyPressedCustom(JG_KEY_UP);
                        } else if (keyIsPressed(JG_KEY_WHEEL_RIGHT)) {
                            keyPressedCustom(JG_KEY_RIGHT);
                        } else if (keyIsPressed(JG_KEY_WHEEL_LEFT)) {
                            keyPressedCustom(JG_KEY_LEFT);
                        } else if (UISettings.SELECTKEY != 0 && lastPressed == UISettings.SELECTKEY) {
                            keyPressedCustom(UISettings.FIREKEY);
                        } else if (UISettings.BACK != -1 && lastPressed == UISettings.BACK) {
                            keyPressedCustom(UISettings.BACKSPACE);
                        } else if (keyIsPressed(JG_KEY_SELECT)) {// || lastPressed == UISettings.OKKEY) {
                            keyPressedCustom(JG_KEY_SELECT);
                        } else {
                            keyPressedCustom(lastPressed);
                        }
                    }
                    //isExitCalled = true;
                } else if (isDAPP) {
                    isDAPP = false;
                    ObjectBuilderFactory.GetKernel().startDAPPDownload();
                }
            }
        } catch (Exception e) {
            Logger.loggerError("tickGameLogic error " + e.toString());
        }

    }

    public void setWarningpopup() {
        this.showNotify();
        Display.getDisplay(ObjectBuilderFactory.GetProgram()).setCurrent(ObjectBuilderFactory.getPCanvas());
    }

    protected void keyPressed(int arg0) {
        lastPressed = arg0;
        super.keyPressed(arg0);
    }

    protected void keyRepeated(int keyCode) {
        lastPressed = keyCode;
        super.keyRepeated(keyCode);
    }

    private void processPointerInputEvent() {
        //UISettings.POPUP_SCROLL_WIDTH = 20;
        int arg0 = pointerInputEventGetX();
        int arg1 = pointerInputEventGetY();
        if (pointerInputEventGetType() == POINTER_INPUT_EVENT_TYPE_PRESSED) {
            dragX = arg0;
            dragY = arg1;
            isPressedAndReleased = true;
            try {
                 if (cust == 1) {
                    iProfilePresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 2) {
                    iInboxPresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 3) {
                    iMenuPresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 4) {
                    iGetEntryPreseneter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 5) {
                    iDisplayPresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 6) {
                    iViewPresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if (cust == 7) {
                    iDownloadPresenter.pointerPressed(arg0, arg1, false, false);
                } else if(cust == 8){
                    iChatPresenter.pointerPressed(arg0, arg1, false, false, true);
                } else if(cust == 9){
                    iUserProfilePresenter.pointerPressed(arg0, arg1, false, false, true);
                }
            } catch (Exception e) {
            }
        } else if (pointerInputEventGetType() == POINTER_INPUT_EVENT_TYPE_RELEASED) {
            try {
                boolean isNeedSelect = false;
                if (cust == 1) {
                    isNeedSelect = iProfilePresenter.pointerPressed(arg0, arg1,
                            isPressedAndReleased,!isPressedAndReleased,false);
                } else if (cust == 2) {
                    isNeedSelect = iInboxPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if (cust == 3) {
                    isNeedSelect = iMenuPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if (cust == 4) {
                    isNeedSelect = iGetEntryPreseneter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if (cust == 5) {
                    isNeedSelect = iDisplayPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if (cust == 6) {
                    isNeedSelect = iViewPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if (cust == 7) {
                    isNeedSelect = iDownloadPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,!isPressedAndReleased);
                } else if(cust == 8){
                    isNeedSelect = iChatPresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                } else if(cust == 9){
                    isNeedSelect = iUserProfilePresenter.pointerPressed(arg0, arg1, isPressedAndReleased,
                            !isPressedAndReleased, false);
                }
                if (isNeedSelect && isPressedAndReleased) {
                    selectTime = (byte) UISettings.SELECT_TIME;
                }
            } catch (Exception e) {
            }

        } else if (pointerInputEventGetType() == POINTER_INPUT_EVENT_TYPE_DRAGGED) {
            try {
                if(!isMultiTouch || (dragY+4)<arg1 || (dragY-4)>arg1){
                    dragX = arg0;
                    dragY = arg1;
                    
                    isPressedAndReleased = false;
                    if (cust == 1) {
                        iProfilePresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 2) {
                        iInboxPresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 3) {
                        iMenuPresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 4) {
                        iGetEntryPreseneter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 5) {
                        iDisplayPresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 6) {
                        iViewPresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if (cust == 7) {
                        iDownloadPresenter.pointerPressed(arg0, arg1, false, false);
                    } else if(cust == 8){
                        iChatPresenter.pointerPressed(arg0, arg1, false, false, false);
                    } else if(cust == 9){
                        iUserProfilePresenter.pointerPressed(arg0, arg1, false, false, false);
                    }
                } 
            } catch (Exception e) { }
        }
    }

    public void keyPressedCustom(int keycode) {
        try {
            if (cust == 0) {
                iFlashPresenter.keyPressed(keycode);
            } else if (cust == 1) {
                iProfilePresenter.keyPressed(keycode);
            } else if (cust == 2) {
                iInboxPresenter.keyPressed(keycode);
            } else if (cust == 3) {
                iMenuPresenter.keyPressed(keycode);
            } else if (cust == 4) {
                iGetEntryPreseneter.keyPressed(keycode);
            } else if (cust == 5) {
                iDisplayPresenter.keyPressed(keycode);
            } else if (cust == 6) {
                iViewPresenter.keyPressed(keycode);
            } else if (cust == 7) {
                iDownloadPresenter.keyPressed(keycode);
            } else if(cust == 8){
                iChatPresenter.keyPressed(keycode);
            } else if(cust == 9){
                iUserProfilePresenter.keyPressed(keycode);
            }
        } catch (Exception exception) {
            Logger.loggerError("ShorthandCanvas keyPressedCustom Exception " + exception.toString() + cust);
        } catch(OutOfMemoryError outOfMemoryError){
            Logger.loggerError("ShorthandCanvas keyPressedCustom OutofMemory Exception " + outOfMemoryError.toString());
        }
    }

    public void exitShortHand() {

		//#if VERBOSELOGGING
  //|JG|Logger.loggerError("ShorthandCanvas->exitShorthand");
        //#endif
        //ObjectBuilderFactory.GetProgram().destroyApp(true);
        Logger.loggerError("exitShorthand");
        postSystemEvent(SYSTEM_EVENT_EXIT);
    }

    public void paintGameView(Graphics g) {
        try {

            if(Settings.isSocketThreadStart){
                Settings.isSocketThreadStart = false;
                ObjectBuilderFactory.GetKernel().startSocketReaderThread();
            }
            if(CustomCanvas.isNotificationGoto>-1 || CustomCanvas.msgType>-1
                    || CustomCanvas.isShowMessageSendSprit
                    || CustomCanvas.isChatNotification) {
                isNeedPaint = true;
            }

            //#if GENERIC
 if(UISettings.formWidth != getWidth() || UISettings.formHeight != getHeight()){
      UISettings.formWidth = (short)getWidth();
      UISettings.formHeight = (short)getHeight();
      isNeedPaint = true;
      paintingTime = 1;
      rotateScreen();
 }
            //#endif

            if (isExit) {
                exitShortHand();
            } else {
                if (pState > -1) {
                    unLoadPreseneter();
                    pState = -1;
                }
                if (isNeedPaint) {
                    if (Settings.isIsScreenRefresh()) { //CR 6986
                        if (paintingTime <= 0) {
                            paintingTime = 3;
                        }
                        paintingTime--;
                    } else {
                        paintingTime = 0;
                    }

                    if (paintingTime == 0) { //CR 6986
                        if (cust == 0) {
                            iFlashPresenter.paintGameView(g);
                        } else if (cust == 1) {
                            isNeedPaint = false;
                            iProfilePresenter.paintGameView(g);
                        } else if (cust == 2) {
                            isNeedPaint = false;
                            iInboxPresenter.paintGameView(g);
                        } else if (cust == 3) {
                            isNeedPaint = false;
                            iMenuPresenter.paintGameView(g);
                        } else if (cust == 4) {
                            isNeedPaint = false;
                            iGetEntryPreseneter.paintGameView(g);
                        } else if (cust == 5) {
                            isNeedPaint = false;
                            iDisplayPresenter.paintGameView(g);
                        } else if (cust == 6) {
                            isNeedPaint = false;
                            iViewPresenter.paintGameView(g);
                        } else if (cust == 7) {
                            isNeedPaint = false;
                            iDownloadPresenter.paintGameView(g);
                        } else if(cust == 8){
                            isNeedPaint = false;
                            iChatPresenter.paintGameView(g);
                        } else if(cust == 9){
                            isNeedPaint = false;
                            iUserProfilePresenter.paintGameView(g);
                        }
                    }
//                    if (cust != -2 && cust != 0 && isNotification) {
//                        isNotification = false;
//                        ObjectBuilderFactory.GetKernel().displayNotification();
//                    } else
                        if (isRefresh) {
                        isRefresh = false;
                        ObjectBuilderFactory.GetKernel().reLaunchProfiles();
                    }
                }
            }
        } catch (Exception exception) {
            Logger.loggerError("ShorthandCanvas paintGameView OutofMemory Exception " + exception.toString());
        } catch(OutOfMemoryError outOfMemoryError){
            Logger.loggerError("ShorthandCanvas paintGameView OutofMemory Exception " + outOfMemoryError.toString());
        }
    }

//    protected void sizeChanged(int w, int h) {
//        super.sizeChanged(w, h);
//    }

    private void rotateScreen() {
        isLandScpage = !isLandScpage;
        if(!Build.GENERIC)
            UISettings.formWidth = 0;
        Constants.setApplicationFilePath();
        if (cust == 0) {
            iFlashPresenter.rotateScreen(isLandScpage);
        } else if (cust == 1) {
            iProfilePresenter.rotateScreen(isLandScpage);
        } else if (cust == 2) {
            iInboxPresenter.rotateScreen(isLandScpage);
        } else if (cust == 3) {
            iMenuPresenter.rotateScreen(isLandScpage);
        } else if (cust == 4) {
            iGetEntryPreseneter.rotateScreen(isLandScpage);
        } else if (cust == 5) {
            iDisplayPresenter.rotateScreen(isLandScpage);
        } else if (cust == 6) {
            iViewPresenter.rotateScreen(isLandScpage);
        } else if (cust == 7) {
            iDownloadPresenter.rotateScreen(isLandScpage);
        } else if(cust == 8){
            iChatPresenter.rotateScreen(isLandScpage);
        } else if(cust == 9){
            iUserProfilePresenter.rotateScreen(isLandScpage);
        }
    }

    protected void pointerDragged(int arg0, int arg1) {
        super.pointerDragged(arg0, arg1);
    }

    protected void pointerPressed(int arg0, int arg1) {
        super.pointerPressed(arg0, arg1);
    }

    protected void pointerReleased(int arg0, int arg1) {
        super.pointerReleased(arg0, arg1);
    }

    public void commandAction(Command arg0, Displayable arg1) {
        byte rbyte = 0;
        byte priority = (byte) arg0.getPriority();
        try{
        //#if KEYPAD
        //|JG|        if (3 == cust) {
        //|JG|            synchronized (iMenuPresenter) {
        //|JG|                rbyte = iMenuPresenter.commandAction(priority);
        //|JG|                if (rbyte > 0) {
        //|JG|                    Display.getDisplay(midlet).setCurrent(this);
        //|JG|                    if (rbyte == 1) {
        //|JG|                        iMenuPresenter.keyPressed(UISettings.FIREKEY);
        //|JG|                    } else if (rbyte == 2) {
        //|JG|//bug id 4785
        //|JG|                        if (!hasPointerEvents() && !hasPointerMotionEvents()) {
        //|JG|                            iMenuPresenter.keyPressed(UISettings.RIGHTOPTION);
        //|JG|                        }
        //|JG|                    }
        //|JG|                }
        //|JG|            }
        //|JG|        } else
        //#endif
        if (4 == cust) {
            synchronized (iGetEntryPreseneter) {
                rbyte = iGetEntryPreseneter.commandAction(priority);
                if (rbyte > 0) {
                    Display.getDisplay(midlet).setCurrent(this);
                    if (rbyte == 1) {
                        iGetEntryPreseneter.keyPressed(UISettings.FIREKEY);
                    } else if (rbyte == 2) {
                        //bug id 4785
                        if (!hasPointerEvents() && !hasPointerMotionEvents()) {
                            iGetEntryPreseneter.keyPressed(UISettings.RIGHTOPTION);
                        }
                    }
                }
            }
        } else if (8 == cust) {
            synchronized (iChatPresenter) {
                rbyte = iChatPresenter.commandAction(priority);
                if (rbyte > 0) {
                    Display.getDisplay(midlet).setCurrent(this);
                    if (rbyte == 1) {
                        iChatPresenter.keyPressed(UISettings.FIREKEY);
                    } else if (rbyte == 2) {
                        //bug id 4785
                        if (!hasPointerEvents() && !hasPointerMotionEvents()) {
                            iChatPresenter.keyPressed(UISettings.RIGHTOPTION);
                        }
                    }
                }
            }
        } else {
            Display.getDisplay(midlet).setCurrent(this);
        }
        }catch(Exception exception){
            Logger.loggerError("ShorthandCanvas->CommandAction->"+exception.toString());
            Display.getDisplay(midlet).setCurrent(this);
        }
    }

//    public void setNotificationParam(boolean isSet) {
//        isNotification = isSet;
//        isNeedPaint = true;
//    }

    public void setTimer() {
        isTimer = true;
    }

    public void widgetRefresh() {
        isRefresh = true;
    }

    /**
     *
     * @param state 1. DownloadStart state
     *              2. Donload Finished State
     */
    public void DAPPDownload() {
        isDAPP = true;
    }

    public void setAsCurrentScreen(){
        Display.getDisplay(midlet).setCurrent(this);
    }
}
