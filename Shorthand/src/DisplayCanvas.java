

//import java.util.Timer;
//import java.util.TimerTask;
import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;

/**
 * Display Canvas class represents the Display presenter screen.
 *
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) ShartHand Mobile Inc
 */
public class DisplayCanvas implements IDisplayPresenter,IPopupHandler,IMenuHandler,ICanvasHandler {

    private ICustomMenu iMenu = null;

    private IBannerHandler iBannerHandler = null;
    //#if KEYPAD
    //|JG|    private IKeyHandler iKeyHandler = null;
    //#endif

    private byte itemFocused = 0;

    //Original Left Option byte
    private byte rOByte = -1;

    private byte opt = 0;

    private int imgrotType = 0;

    private byte lastItemFocused = UISettings.VIEW;

    private byte rStart = 0;

    //Display Time
    private short dTime = 0;

    private short sTime = 0;

    private boolean isDatWait = false;

    private ICustomPopup iCustomPopup = null;

    private String sHeader = null;

    private byte rEndTimer = 5;

    private int screenTimer = 0;

    private short displayTimerCount = 100;

    byte numberofItem = 0;

    private Timer screenDisplayTimer = null;

    private String multiPartReceivedString = null;

    private boolean isSmsWaitBeforeLoad = false;

     //CR 14694
    private ImageDisplay imageDisplay = null;
    private ICaptureImage iCaptureImage = null;
    private ICustomMenu iImageMenu = null;
    private byte isImageView = -1;
    private boolean isImageNotLoad = true;
    private String imageUploadNumber = null;
//    private byte imageType = -1;
//    private String userPhoneNumber = null;

//    private boolean isNotPopupLoaded = true;

    /**
     * Constructor method
     *
     * Creates a new instance of DisplayCanvas
     */
    public DisplayCanvas() {
        //CR 14694
        imageDisplay = new ImageDisplay();
        iCaptureImage = new CaptureImageAudio(this);
        iImageMenu = new CustomMenu(this);

        //#if KEYPAD
        //|JG|        iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        //|JG|        iKeyHandler.setCanvasHandler(this);
        //#endif
        iCustomPopup = new CustomPopup(this);
        iMenu = new CustomMenu(this);
        iBannerHandler = new CustomBanner(this);
    }

    /**
     *
     */
    public void removeOptions(){
        rOByte = -1;
        PresenterDTO.setLOptByte((byte)-1);
        iMenu.removeOption();
        iBannerHandler.removeOption();
        reLoadFooterMenu();
    }

    /**
     * Paint method which will be called everytime when one of
     * the component needs to be refreshed.
     *
     * @param g  An instance of Graphics class which is a default
     *           parameter.
     */
    public void paintGameView(Graphics g)
    {
        if (iCaptureImage.isCameraScreen()) {
            CustomCanvas.drawHeader(g);
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
        } else {
            if(itemFocused != UISettings.IMAGE_MENU && itemFocused != UISettings.CAPTURE_IMAGE 
                    && iCustomPopup.isCustomPopupState()){
                itemFocused = UISettings.POPUPSCREN;
            }

            //#if KEYPAD
            //|JG|            iKeyHandler.updateKeyTimer();
            //|JG|
            //|JG|            iKeyHandler.updateSearchTimer();
            //#endif

            //Clear Screen
            clearScreen(g);

            //Draw background image
            CustomCanvas.drawBackgroundImage(g);

            if (isImageView > 0) {
                if(!imageDisplay.drawDisplayImage(g)){
                    resetImageState(sHeader, true);
                } else CustomCanvas.drawSecondaryHeader(null, g, true, false);
            } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
                iCaptureImage.drawCaptureImage(g);
            }  else if (itemFocused == UISettings.IMAGE_MENU) { //CR 12542
                iImageMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(), "");
            } else {
                //Draw Secondary Header
                CustomCanvas.drawSecondaryHeader(null, g,true,false);

                //Draw the rest of the screen and the elements present in it.
                showScreen(g);
            }
            //Draw primary header
            CustomCanvas.drawHeader(g);

            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
            } else {
                if(itemFocused == UISettings.MENU){ // CR number 6755
                    CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
                } else if(itemFocused == UISettings.POPUPSCREN) {
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                }
                 else {
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                }
            }
        }
    }

    //CR 14694
     private void resetImageState(String option, boolean isReset){
         if(isReset){
            isImageView = -1;
         } else {
             isImageView = 0;
         }
        if(null != option){
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(option, "", 0);
        } else CustomCanvas.sHeader = option;
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    /**
     *
     */
    private boolean IsScreenDisplayTime(){
        if(screenTimer>0){
            screenTimer--;
            if(screenTimer <= 0)
                handleDisplayTimer();
            return true;
        }
        return false;
    }

    /**
     * Method to clear screen.
     * @param g
     */
    private void clearScreen(Graphics g) {
        g.setColor(0x21519c);
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
    }

    /**
     * Method to draw the screen and the elements if present.
     *
     * @param g
     */
    private void showScreen(Graphics g) {

        if (opt == 1) {
            drawDisplayImage(g);
        }

        iMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(),null);

        iBannerHandler.drawScreen(g, itemFocused);

        iCustomPopup.drawScreen(g);
    }

    private void isRotateDisplayImage(){
        rStart++;
        if(rStart >= rEndTimer){
            imgrotType++;
            if (imgrotType > 3) {
                imgrotType = 0;
                if(isDatWait) {
                    if(isImageView == -1){
                        rEndTimer = 5;
                        isDatWait = false;
                        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "",0);
                            if(isSmsWaitBeforeLoad){
                            isSmsWaitBeforeLoad = false;
                            dTime = sTime;
                            setDisplayTime(dTime,false);
                        } else setDisplayTime(dTime,true);
                    } else {
                        setDisplayTime((byte)2,false);
                    }
                }
            }
            rStart = 0;
        }
    }

    /**
     * Method to animate the waiting image.
     *
     * @param g
     */
    private void drawDisplayImage(Graphics g) {
        int position = 0;
      //  if(Settings.isIsGPRS())
      //   isDatWait=false;
        if(isDatWait)
            CustomCanvas.drawMessageSendImage(g,imgrotType);
        else position = CustomCanvas.drawProcessImage(g,imgrotType);
        //CR 10044
        if(null != multiPartReceivedString){
            g.setColor(0xffffff);
            g.drawString(multiPartReceivedString, (UISettings.formWidth - CustomCanvas.font.stringWidth(multiPartReceivedString)) / 2, ((UISettings.formHeight - position) / 2) - CustomCanvas.font.getHeight(), Graphics.TOP | Graphics.LEFT);
        }
    }

    /**
     * Method to handle the keypressed event based on the focus of
     * component
     *
     * @param type
     */
    public void keyPressed(int keyCode) {
        //CR 14694
        if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
            iCaptureImage.keyPressed(keyCode);
        } else if(itemFocused == UISettings.IMAGE_MENU){ //CR 14694
           handleImageMenu(keyCode);
        } else if (itemFocused == UISettings.VIEW ||
                itemFocused == UISettings.MENU) { //options menu
           handleKey(keyCode);
        } else if (itemFocused == UISettings.BANNER) { //Advertisement
            iBannerHandler.handleBanner(keyCode);
        } else if(itemFocused == UISettings.POPUPSCREN) {
            iCustomPopup.keyPressed(keyCode);
        }
    }

//CR 14694
     private void handleKey(int keyCode) {
         if (isImageView > 0) {
            if (imageDisplay.isBack(keyCode) == 1) {
                //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(
                       -2, null, UISettings.rOByte);
//                resetImageState(sHeader, true);
            }
        } else {
             if(itemFocused == UISettings.VIEW){
                 handleView(keyCode);
             } else iMenu.handleMenu(keyCode);
        }
    }

     private void handleImageMenu(int keyCode){
         if (isImageView == 2) {
            byte back = imageDisplay.isBack(keyCode);
            if (back == 1) {
                resetImageState(Constants.headerText[35], false);
            } else if (back == 2) {
                reLoadFooterMenu();
            }
        } else {
             iImageMenu.handleMenu(keyCode);
        }
     }

      //CR 14111
    private boolean loadMenu() {
        String[] names = Utilities.getImageGalleryNames();
        if(null != names){
            byte numOfMenuItems = (byte) (UISettings.numOfMenuItems - 1);
            if (null != iBannerHandler.getBannerText()) {
                numOfMenuItems--;
            }
            iImageMenu.setMenu(null, Utilities.getImageGalleryPath(), names, null, null, true, null, 0);
            iImageMenu.setMenuPosition((short) (UISettings.headerHeight + UISettings.secondaryHeaderHeight),
                    numOfMenuItems, (byte) -1, true);
            isImageView = 0;
            itemFocused = UISettings.IMAGE_MENU;
            lastItemFocused = UISettings.IMAGE_MENU;
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.headerText[35], "", 0);
            reLoadFooterMenu();
            return false;
        }
        return true;

    }

      private boolean isImage(byte sOption) {
        boolean isNotImage = true;
        this.isImageNotLoad = false;
        if (sOption == 53) { //List Image
            isNotImage = loadMenu();
        } else if (sOption == 56) { //Capture Image //CR 14694
            isNotImage = !iCaptureImage.isCapture(2);
            if (!isNotImage) {
                if(iCaptureImage.loadCamera()){
                    //bug 14637
                    isImageView = 0;
                    iCaptureImage.rotateScreen();
                    itemFocused = UISettings.CAPTURE_IMAGE;
                    lastItemFocused = UISettings.CAPTURE_IMAGE;
                }
                ShortHandCanvas.IsNeedPaint();
            }
        }
        isImageNotLoad = true;
        return isNotImage;
    }

    public boolean  pointerPressed(int x, int y, boolean isPointed, 
            boolean isReleased, boolean isPressed){
        boolean isNeedSelect = false;
        if(UISettings.POPUPSCREN == itemFocused){
            isNeedSelect = iCustomPopup.pointerPressed(x, y, isPointed,isReleased, isPressed);
        } else {
            if(null != iBannerHandler.getBannerText() && y>=(UISettings.formHeight-(UISettings.footerHeight+UISettings.itemHeight))
                    && y<=(UISettings.formHeight-UISettings.footerHeight)){
                if(iBannerHandler.isBannerSelect() && isPointed && itemFocused != UISettings.BANNER){
                    itemFocused = UISettings.BANNER;
                    lastItemFocused = UISettings.BANNER;
                    reLoadFooterMenu();
                }
            } else if(y>=(UISettings.formHeight-UISettings.footerHeight)){
                if(isPointed){
                    if(UISettings.rOByte>-1 && x>=(UISettings.formWidth/2)){
                        keyPressed(UISettings.RIGHTOPTION);
                    } else if(UISettings.lOByte>-1 && x<=(UISettings.formWidth/2)){
                        keyPressed(UISettings.LEFTOPTION);
                    }
                }
            } else if (itemFocused == UISettings.IMAGE_MENU && !iCaptureImage.isCurrentScreen()) { //CR 14694
                if (iImageMenu.isMenuPresent() && y >= iImageMenu.getMenuPosition(false)) {
                    int mPosition = (UISettings.formHeight - UISettings.footerHeight);
                    if (null != iBannerHandler.getBannerText()) {
                        mPosition -= UISettings.itemHeight;
                    }
                    if (y < mPosition) {
                        itemFocused = UISettings.IMAGE_MENU;
                        lastItemFocused = UISettings.IMAGE_MENU;
                        isNeedSelect = iImageMenu.pointerPressed(x, y, isPointed, isReleased, isPressed);
                    }
                }
            }  else if( isImageView == -1 && !iCaptureImage.isCurrentScreen() &&
                    iMenu.isMenuPresent() && y >= iMenu.getMenuPosition(false)){
                int mPosition =  (UISettings.formHeight-UISettings.footerHeight);
                if(null != iBannerHandler.getBannerText())
                    mPosition -= UISettings.itemHeight;
                if(y < mPosition){
                    itemFocused = UISettings.MENU;
                    lastItemFocused = UISettings.MENU;
                    isNeedSelect = iMenu.pointerPressed(x, y, isPointed, isReleased, isPressed);
                }
            }
        }
        return isNeedSelect;
    }

    /**
     * Method to handle the key press event on the right option and left
     * option.
     *
     * @param type
     */
    private void handleView(int keyCode) {
        if (keyCode == UISettings.LEFTOPTION) { //Right option menu
            if (UISettings.lOByte > -1) {
                getOptions();
            }
        } else if (keyCode == UISettings.RIGHTOPTION) { //Left option menu
            if (UISettings.rOByte > -1) {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.rOByte == 22)
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
        }
    }

    /**
     * Method to get the right options menu from the backend and to
     * draw the options menu
     */
    private void getOptions() {
        byte[] opts = ObjectBuilderFactory.GetKernel().getOptions(0, null);
        if (null != opts) {
            CustomCanvas.setOptionsMenuArray(opts);
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            itemFocused = UISettings.POPUPSCREN;
        }
    }

    /**
     *
     */
    public byte enableUpSelection(){
        byte isUpSelected = 0;
        if (itemFocused == UISettings.IMAGE_MENU) {
            isUpSelected = 1;
        } else if(itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14694
            ObjectBuilderFactory.GetKernel().handleOptionSelection(
                       -2, null, UISettings.rOByte);
//            isImageView = -1;
//            CustomCanvas.sHeader = sHeader;
//            if (iMenu.isMenuPresent()) {
//                itemFocused = UISettings.MENU;
//                lastItemFocused = UISettings.MENU;
//            } else if (iBannerHandler.isBannerSelect()) {
//                itemFocused = UISettings.BANNER;
//                lastItemFocused = UISettings.BANNER;
//            } else {
//                itemFocused = UISettings.VIEW;
//                lastItemFocused = UISettings.VIEW;
//            }
//            reLoadFooterMenu();
//            setDisplayTime(sTime, false);
        } if(itemFocused == UISettings.BANNER){
             if(isImageView>0){
                if(iImageMenu.isMenuPresent()){
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    reLoadFooterMenu();
                    isUpSelected = 1;
                }
            } else if(iMenu.isMenuPresent()){
               itemFocused = UISettings.MENU;
               lastItemFocused = UISettings.MENU;
           }
        } else if(itemFocused == UISettings.MENU){
            isUpSelected = 1;
        }
        return isUpSelected;
    }

    /**
     *
     */
     public byte enableDownSelection(){
         byte isDownSelected = 0;
         if(itemFocused == UISettings.MENU){
            if(iBannerHandler.isBannerSelect()){
                isDownSelected = 3;
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
            }
         } else if(iBannerHandler.isBannerSelect()){
             if(isImageView>0){
                if(iImageMenu.isMenuPresent()){
                    isDownSelected = 2;
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    reLoadFooterMenu();
                }
            } else if(iMenu.isMenuPresent()){
                 isDownSelected = 2;
               itemFocused = UISettings.MENU;
               lastItemFocused = UISettings.MENU;
            }
         }
         return isDownSelected;
    }

    /**
     * Method to handle the end of the display Timer. Method calls for handle
     * item selection method in the kernel.
     */
    public void handleDisplayTimer() {
		//#if VERBOSELOGGING
  //|JG|Logger.loggerError("DisplayCanvas->HandleDisplayTimer");
        //#endif
        screenTimer = 0;
        if(isDatWait){
            setDisplayTime((short)2,false);
        } else {
            if (itemFocused !=  UISettings.POPUPSCREN &&
                    isImageNotLoad &&
                    itemFocused != UISettings.IMAGE_MENU &&
                    itemFocused != UISettings.CAPTURE_IMAGE) {
                ObjectBuilderFactory.GetKernel().handleItemSelection(0, null);
            } else {
                dTime = 1;
                displayTimerCount = 10;
                setDisplayTime(dTime,false);
            }
        }
    }

    /**
     * Method to load the display canvas
     *
     * @param resDTO An instance of DisplayResponseDTO
     */
    public void load(DisplayResponseDTO resDTO) {
        deInitialize();
        //bug 14783, CR 14789
        imageUploadNumber = resDTO.getUploadId();
        iCaptureImage.setChatId(resDTO.getUploadId());
//        imageType = (byte)resDTO.getProfileImageType();
//        userPhoneNumber = resDTO.getUserNumber();
        short mPos =(short)(UISettings.formHeight - UISettings.footerHeight);
        numberofItem = 0;
        try {
            iMenu.setMenu(null, resDTO.getMItems(), null, null, null, true,null,0);
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
               // numberofItem = (byte)resDTO.getMItems().length;
                 if(resDTO.getMItems().length<4)
                    numberofItem = (byte)resDTO.getMItems().length;
                else numberofItem = 4;
                mPos -= (numberofItem * UISettings.itemHeight);
                //#if KEYPAD
                //|JG|                iKeyHandler.SetItemFocused(UISettings.SEARCH);
                //|JG|                iKeyHandler.setEntryProperty((short)0, Short.MAX_VALUE, 0,Float.MAX_VALUE, null, -1, iKeyHandler.ALPHANUMERIC,false,true);
                //#endif
            }

            sHeader = CustomCanvas.getSecondaryHeader(resDTO.getSecondaryHeaderText(), "",0);

            //CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(resDTO.getSecondaryHeaderText(), "",0);
            UISettings.lOByte = PresenterDTO.setLOptByte();
            rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
            iBannerHandler.setBanner(resDTO.getBannerText(), resDTO.getBannerStyle(),resDTO.getLeftOptionText(),false);
            if(iBannerHandler.isBannerSelect()){
                mPos -= UISettings.itemHeight;
                if(!iMenu.isMenuPresent()){
                    itemFocused = UISettings.BANNER;
                    lastItemFocused = UISettings.BANNER;
                }
            }

            iMenu.setMenuPosition(mPos, numberofItem, rOByte,false);

            sTime = resDTO.getDisplayTime();
            isSmsWaitBeforeLoad = resDTO.isIsSmsWaitBeforeLoad();
            
            // CR 11976
            if(!Settings.isIsGPRS() ){//|| null == Settings.getPhoneNumber()){
                isDatWait = resDTO.isIsDATWait();
            } else isDatWait = false;
            
            if(isDatWait)
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.headerText[15], "",0);
            else
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(resDTO.getSecondaryHeaderText(), "",0);

            if(isDatWait && resDTO.isIsAppWait())
                dTime = 0;
            else dTime = sTime;

            setDisplayTime(dTime,true);
            opt = resDTO.getDisplayImage();

            //CR 14694
            if(resDTO.getProfileImageType() == 0){
                isImage((byte)53);
            } else if(resDTO.getProfileImageType() == 1){
                isImage((byte)56);
            }

        } catch (Exception e) {
            Logger.loggerError("DisplayCanvas Load " + e.toString() + e.getMessage());
        }
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }


    /**
     * De Initialize method
     */
    private void deInitialize() {

        lastItemFocused = itemFocused = UISettings.VIEW;
        stopScreenDisplayTimer();

        //#if KEYPAD
        //|JG|        iKeyHandler.deinitialize();
        //#endif

        iBannerHandler.deInitialize();

        //byte
        displayTimerCount = 100;
        rOByte = -1;
        rEndTimer = 5;
        rStart = 0;
        screenTimer = 0;

        isSmsWaitBeforeLoad = isDatWait = false;
        isImageNotLoad = true;

        screenTimer = 0;

        //Short
        dTime = sTime = 0;

        //Int
        imgrotType = 0;

        multiPartReceivedString = null;
        //bug 12629
        iMenu.deInitialize();
        iImageMenu.deInitialize();
        iCaptureImage.deInitialize(false);
        imageDisplay.deInitialize();
        isImageView = -1;

//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);

        //bug 13169
        CustomCanvas.deinitialize();

   }


    public void invokeTimer(){
        dTime = sTime;
        setDisplayTime(dTime, false);
    }

    /**
     * Method to set the display timer
     *
     * @param time Timer value
     */
    private void setDisplayTime(short time, boolean isFirst) {
        screenTimer = 0;
        if(isDatWait){
            time = 2;
            rEndTimer = 5;
        } else {
            rEndTimer = 5;
            displayTimerCount = 10;
        }
        rStart = 0;
        if(time>0){
            screenTimer = time * displayTimerCount;
            stopScreenDisplayTimer();
            startScreenDisplayTimer();
        } else if(isFirst){
            stopScreenDisplayTimer();
            startScreenDisplayTimer();
        }
    }

    private void stopScreenDisplayTimer(){
        if(null != screenDisplayTimer){
            screenDisplayTimer.cancel();
            screenDisplayTimer = null;
        }
    }

    private  void startScreenDisplayTimer(){
        screenDisplayTimer = new Timer();
        screenDisplayTimer.schedule(new ScreenTimer(), 0,100);
    }

    /* Method to load message box
     * @param type
     *          <li> 1 - Smartpopup without any options that last for
     *              predefined time </li>
     *          <li> 2,3,5 - Message box with options menu </li>
     *          <li> 4,6 - Notification window </li>
     * @param msg  Message
     */
//    public void loadMessageBox(byte type, String msg) {
//        if(iCustomPopup.loadMessageBox(type, msg))
//            itemFocused = UISettings.POPUPSCREN;
//    }

//    public void displayMessageSendSprite(){
//        iCustomPopup.setMessageSendSpritTimer();
//    }


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
//    public void showNotification(byte isGoTo) {
//        iCustomPopup.showNotification(isGoTo);
//        itemFocused = UISettings.POPUPSCREN;
//    }

    /**
     * Method to unload the canvas
     */
    public void unLoad() {
        try{
            iCustomPopup.deinitialize();
            deInitialize();
        }catch(Exception e){
            Logger.loggerError("Display Unload Erro "+e.toString());
        }
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if(isReLoad)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    /**
     *
     * @param oIndex
     */
    public void handleOptionSelected(byte oIndex) {
        enablePreviousSelection();
        if (itemFocused == UISettings.IMAGE_MENU) {
            ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, null, oIndex);
//            UiGlobalVariables.imagefile = null;
//            if(iMenu.isMenuPresent()){
//                itemFocused = UISettings.MENU;
//                lastItemFocused = UISettings.MENU;
//            } else if(iBannerHandler.isBannerSelect()){
//                itemFocused = UISettings.BANNER;
//                lastItemFocused = UISettings.BANNER;
//            } else {
//                itemFocused = UISettings.VIEW;
//                lastItemFocused = UISettings.VIEW;
//            }
//            reLoadFooterMenu();
        } else if(lastItemFocused == UISettings.MENU){
            if(iMenu.isMenuPresent()){
                ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, iMenu.getSelectedMenuValue(), oIndex);
            } else {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, null, oIndex);
            }
        } else if(lastItemFocused == UISettings.BANNER || lastItemFocused == UISettings.VIEW){
            ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, oIndex);
        }
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType,boolean isReload) {
        if(isReload)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
    }

    public void enablePreviousSelection() {
        itemFocused = lastItemFocused;
        reLoadFooterMenu();
    }

    public int getSmartPopupyPos(int keyCode) {
        return (UISettings.formHeight / 2 - UISettings.popupHeight / 2);
    }

    //#if KEYPAD
    //|JG|
    //|JG|    public boolean isSearchText(int keyCode) {
    //|JG|        return iKeyHandler.handleSearchText(keyCode);
    //|JG|    }
    //|JG|
    //|JG|    public String getSearchTempText() {
    //|JG|        return iKeyHandler.getSearchTempText();
    //|JG|    }
    //|JG|
    //|JG|    public String getSearchText() {
    //|JG|        return iKeyHandler.getSearchText();
    //|JG|    }
    //|JG|
    //|JG|    public void resetSearchValue() {
    //|JG|        iKeyHandler.SearchValueReset();
    //|JG|    }
    //#endif

    public void setItemfocuse(byte itemFocuse) {
        if(itemFocuse == UISettings.OPTIONS){
            itemFocused = UISettings.POPUPSCREN;
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
        }
    }

    public void reLoadFooterMenu() {
        if (itemFocused == UISettings.VIEW) {
            if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                UISettings.lOByte = PresenterDTO.setLOptByte();
                UISettings.rOByte = rOByte;
            }
        } else if(itemFocused == UISettings.POPUPSCREN)
            iCustomPopup.reLoadFooterMenu();
        else if(itemFocused == UISettings.BANNER){
            iBannerHandler.reLoadFooterMenu();
        } else if(itemFocused == UISettings.MENU){
             if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                iMenu.reLoadFooterMenu();
            }
        } else if (itemFocused == UISettings.IMAGE_MENU) { ///CR 14694
            if (isImageView > 0 && imageDisplay.isWait()) {
                UISettings.rOByte = -1;
                UISettings.lOByte = -1;
            } else {
                UISettings.rOByte = 22; //Back Option Index
                if (isImageView == 2) {
                    UISettings.lOByte = 61; //Upload Image option Index
                } else {
                    UISettings.lOByte = 55; //OK Option
                }
            }
        } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14418
            iCaptureImage.reLoadFooterMenu();
        }
    }

    public void sendSelectedValue(int id, String value) {
        if(itemFocused == UISettings.MENU) {
            if(iMenu.isMenuPresent())
                ObjectBuilderFactory.GetKernel().handleItemSelection(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
            else
                ObjectBuilderFactory.GetKernel().handleItemSelection(-2, null);
        } else if(itemFocused ==UISettings.IMAGE_MENU){
                //CR 14694
            if(imageDisplay.setHeadetText(iImageMenu.getSelectedDisplayMenuValue(), imageUploadNumber, value,
                    (byte)4)){
                isImageView = 2;
                reLoadFooterMenu();
            }
        }
    }

    public void handleInput(int keyCode) {

    }

    public void handleSmartPopup(int type) {

    }

    public void showDateForm() {

    }

    public void showNativeTextbox(int maxChar, byte type, boolean isMask) {

    }

    public void rotateScreen(boolean isLandScape) {
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(CustomCanvas.sHeader, "",0);
        short mPos =(short)(UISettings.formHeight - UISettings.footerHeight);
        mPos -= (numberofItem * UISettings.itemHeight);
        if(iBannerHandler.isBannerSelect()){
            mPos -= UISettings.itemHeight;
        }
        iMenu.rotateMenu(mPos,numberofItem);
        iImageMenu.rotateMenu(mPos, numberofItem);
        iCustomPopup.rotatePopup();
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload,boolean isSet) {
        if(isReload)
            enablePreviousSelection();
        //#if KEYPAD
        //|JG|        if(isSet)
        //|JG|            iKeyHandler.appendCharacter(itemFocused, selSymbol);
        //#endif
    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

    public void displayMultiPartMessage(String displayString) {
        multiPartReceivedString = displayString;
    }

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }

        public void setImage(ByteArrayOutputStream byteArrayOutputStream){
        if (isImageView == 2 || iCaptureImage.isCurrentScreen()) { //CR 14696
            //CR 14111
            isImageView = -1;
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
            } else if(iBannerHandler.isBannerSelect()){
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
            } else {
                itemFocused = UISettings.VIEW;
                lastItemFocused = UISettings.VIEW;
            }
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "", 0);
            if (null != byteArrayOutputStream && byteArrayOutputStream.size() > 0) {
                Utilities.saveProfilePicture(UiGlobalVariables.byteArrayInputStream,
                        UiGlobalVariables.imagefile, Settings.getPhoneNumber());
                //CR 14423
                if(iCaptureImage.isCurrentScreen()){
                    iCaptureImage.deInitialize(false);
                } else {
                    iImageMenu.deInitialize();
                }
            }
        }
    }


    class ScreenTimer extends TimerTask{
        public void run() {
            if (opt == 1) {
                isRotateDisplayImage();
                ShortHandCanvas.IsNeedPaint();
            }
            if(IsScreenDisplayTime()){
                ShortHandCanvas.IsNeedPaint();
            }
        }
    }
}
