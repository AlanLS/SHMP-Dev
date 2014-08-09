
//#if KEYPAD
//|JG|import javax.microedition.lcdui.Command;
//|JG|import javax.microedition.lcdui.TextField;
//|JG|import javax.microedition.lcdui.TextBox;
//#endif
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import java.io.ByteArrayOutputStream;


/**
 * Menu canvas class for the Menu Presenter Screen
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) ShartHand Mobile Inc
 */
public class MenuCanvas implements IMenuPresenter,IPopupHandler,IMenuHandler
        //#if KEYPAD
        //|JG|        , ICanvasHandler
        //#endif
{
    
    private byte rOByte;
    
    private byte itemFocused = UISettings.MENU;
    
    private byte lastItemFocused = UISettings.MENU;

    //#if KEYPAD
    //|JG|private IKeyHandler iKeyHandler = null;
    //#endif

    private ICustomPopup iCustomPopup = null;
    
    private ICustomMenu iMenu = null;

    
    
    private IBannerHandler bannerHandler = null;

    //#if KEYPAD
    //|JG|    private TextBox nTextbox = null;
    //#endif
    private Display display = null;

    private String textboxValue = "";
    
    private String startTime = null;

    private short textboxSize = 0;



    //CR 14694
    private ImageDisplay imageDisplay = null;
    private ICaptureImage iCaptureImage = null;
    private ICustomMenu iImageMenu = null;
    private byte isImageView = -1;
    private String sHeader = null;

    private String imageUploadNumber = null;
//    private byte imageType = -1;
//    private String userPhoneNnmber = null;

    /**
     * Constructor method
     */
    public MenuCanvas() {

        //CR 14694
        imageDisplay = new ImageDisplay();
        iCaptureImage = new CaptureImageAudio(this);
        iImageMenu = new CustomMenu(this);

        //#if KEYPAD
        //|JG|iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        //|JG|iKeyHandler.setCanvasHandler(this);
        //#endif
        iCustomPopup = new CustomPopup(this);
        iMenu = new CustomMenu(this);
        
        bannerHandler = new CustomBanner(this);
        textboxSize = (short)(8 + CustomCanvas.font.getHeight());
    }

    /**
     * Method to paint the menu canvas
     * 
     * @param g  An instance of Graphics class
     */
    public  void paintGameView(Graphics g) {
        //To Avoid Flickering Issue
        if (iCaptureImage.isCameraScreen()) {
            CustomCanvas.drawHeader(g);
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
        } else {
        
            if(itemFocused != UISettings.IMAGE_MENU && itemFocused != UISettings.CAPTURE_IMAGE &&
                    iCustomPopup.isCustomPopupState()){
                itemFocused = UISettings.POPUPSCREN;
            }

            //startTime =" PS: "+Utilities.getHourMinuteSecond();
            //#if KEYPAD
            //|JG|    iKeyHandler.updateKeyTimer();
            //|JG|
            //|JG|    iKeyHandler.updateSearchTimer();
            //#endif
            clearScreen(g);
            CustomCanvas.drawBackgroundImage(g);

            
            if (isImageView > 0) {
                if(!imageDisplay.drawDisplayImage(g)){
                    resetImageState(sHeader,true);
                } else CustomCanvas.drawSecondaryHeader(null, g, true, false);
            } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
                iCaptureImage.drawCaptureImage(g);
            }  else if (itemFocused == UISettings.IMAGE_MENU) { //CR 12542
                iImageMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(), "");
            } else {
                drawScreen(g);
            }
            
            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
            } else {

                //#if KEYPAD
                //|JG|                if(itemFocused == UISettings.RENAMETEXTBOX){ // CR number 6755
                //|JG|                    CustomCanvas.DrawOptionsMenu(iKeyHandler.getKeyMode(), UISettings.lOByte, UISettings.rOByte, g);
                //|JG|                }
                //|JG|          else
                    //#endif
                    if (itemFocused == UISettings.POPUPSCREN) {
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                } else {
                    CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
                }
            }
    //        if(null != ShortHandCanvas.startTime){
    //            startTime += " PE: "+Utilities.getHourMinuteSecond();
    //            iCustomPopup.loadMessageBox((byte)20,ShortHandCanvas.startTime + startTime);
    //        }
            iCustomPopup.drawScreen(g);
            
        }
    }

    //CR 14694
     private void resetImageState(String option, boolean isRest){
        if(isRest){
            isImageView = -1;
        } else isImageView = 0;
        if(null != option){
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(option, "", 0);
        } else CustomCanvas.sHeader = option;
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    /**
     * Method to clear the screen.
     * 
     * @param g  An instance of Graphics class.
     */
    private void clearScreen(Graphics g) {
        g.setColor(0x21519c);
        g.fillRect(0, 0, UISettings.formWidth,UISettings.formHeight);
    }

    /**
     * Method to draw the rest of the screen other than the primary
     * header and secondary header.
     * 
     * @param g  An instance of Graphics class.
     */
    private void drawScreen(Graphics g) {
        
        iMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(), "");
        //#if KEYPAD
        //|JG|        if (UISettings.RENAMETEXTBOX == lastItemFocused) {  //Rename Text box
        //|JG|            try{
        //|JG|                CustomCanvas.drawTextBox(0xff35f3, iKeyHandler.getRenameTempText(),
        //|JG|                        iKeyHandler.getKeyChar(), null, iMenu.getMenuPosition(false) + iMenu.getSelectedIndex() * UISettings.itemHeight,
        //|JG|                        iKeyHandler.getRenameTextCursorPos(),textboxSize, g,1,-1);
        //|JG|            }catch(Exception e){
        //|JG|                iKeyHandler.setRenameTextValue(iKeyHandler.getRenameTempText());
        //|JG|            }
        //|JG|        }
        //#endif
        
        bannerHandler.drawScreen(g,itemFocused);
    }



    /**
     * Method to handle the key pressed event based on the item focused.
     * 
     * @param type key code
     */
    public void keyPressed(int keyCode) {
        //CR 14694
        if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
            iCaptureImage.keyPressed(keyCode);
        } else if(itemFocused == UISettings.IMAGE_MENU){ //CR 14694
           handleImageMenu(keyCode);
        } else if (itemFocused == UISettings.MENU) {
            handleMenu(keyCode);
        } else if (itemFocused == UISettings.BANNER) {
            bannerHandler.handleBanner(keyCode);
        } 
        //#if KEYPAD
        //|JG|        else if (itemFocused == UISettings.RENAMETEXTBOX) {
        //|JG|            handleRenameTextbox(keyCode);
        //|JG|        }
        //#endif
        else if(itemFocused == UISettings.POPUPSCREN){
            iCustomPopup.keyPressed(keyCode);
        }
    }

    //CR 14694
     private void handleMenu(int keyCode) {
         if (isImageView > 0) {
            if (imageDisplay.isBack(keyCode) == 1) {
                resetImageState(sHeader, true);
            }
        } else {
            iMenu.handleMenu(keyCode);
        }
    }

     private void handleImageMenu(int keyCode){
         if (isImageView == 2) {
            byte back = imageDisplay.isBack(keyCode);
            if (back == 1) {
                //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(
                        iImageMenu.getSelectedItemId(), iImageMenu.getSelectedMenuValue(), UISettings.rOByte);
//                resetImageState(Constants.headerText[35], false);
            } else if (back == 2) {
                reLoadFooterMenu();
            }
        } else iImageMenu.handleMenu(keyCode);
     }

     //CR 14111
    private boolean loadMenu() {
        String[] names = Utilities.getImageGalleryNames();
        if(null != names){
            byte numOfMenuItems = (byte) (UISettings.numOfMenuItems - 1);
            if (null != bannerHandler.getBannerText()) {
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
        if (sOption == 53) { //List Image
            isNotImage = loadMenu();
        } else if (sOption == 56) { //Capture Image //CR 14694
            isNotImage = !iCaptureImage.isCapture(2);
            if (!isNotImage) {
                if(iCaptureImage.loadCamera()){
                    //bug 14637
                    iCaptureImage.rotateScreen();
                    isImageView = 0;
                    itemFocused = UISettings.CAPTURE_IMAGE;
                    lastItemFocused = UISettings.CAPTURE_IMAGE;
                }
                ShortHandCanvas.IsNeedPaint();
            }
        }
        return isNotImage;
    }
     
    public boolean  pointerPressed(int xPosition, int yPosition, boolean isNotDrag, 
            boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;
//        startTime = " PS: "+ Utilities.getHourMinuteSecond();
        if(UISettings.POPUPSCREN == itemFocused){
            isNeedSelect = iCustomPopup.pointerPressed(xPosition, yPosition, isNotDrag,
                    isDragEnd, isPressed);
        } else if(yPosition>=(UISettings.formHeight-UISettings.footerHeight)){
            if(isNotDrag){
                if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
                    keyPressed(UISettings.RIGHTOPTION);
                } else if(UISettings.lOByte>-1 && xPosition<=(UISettings.formWidth/2)){
                    keyPressed(UISettings.LEFTOPTION);
                }
            }
        } else if (itemFocused == UISettings.IMAGE_MENU && !iCaptureImage.isCurrentScreen()) { //CR 14694
            if (iImageMenu.isMenuPresent() && yPosition >= iImageMenu.getMenuPosition(false)) {
                int mPosition = (UISettings.formHeight - UISettings.footerHeight);
                if (null != bannerHandler.getBannerText()) {
                    mPosition -= UISettings.itemHeight;
                }
                if (yPosition < mPosition) {
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    isNeedSelect = iImageMenu.pointerPressed(xPosition, yPosition, isNotDrag,
                            isDragEnd, isPressed);
                }
            }
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen()){ //CR 14694
            int mPosition = iMenu.getMenuPosition(true);
            if(yPosition > mPosition){
                mPosition = (UISettings.formHeight-UISettings.footerHeight);
                if(null != bannerHandler.getBannerText())
                    mPosition -= UISettings.itemHeight;
                 if(null != bannerHandler.getBannerText() && yPosition >= (UISettings.formHeight-(UISettings.footerHeight+UISettings.itemHeight))
                        && yPosition<= (UISettings.formHeight - UISettings.footerHeight)){
                    if(bannerHandler.isBannerSelect() && isNotDrag && itemFocused != UISettings.BANNER){
                        itemFocused = UISettings.BANNER;
                        lastItemFocused = UISettings.BANNER;
                        reLoadFooterMenu();
                    }
                } else if(yPosition <= mPosition){
                    itemFocused = UISettings.MENU;
                    lastItemFocused = UISettings.MENU;
                    isNeedSelect = iMenu.pointerPressed(xPosition, yPosition, isNotDrag, 
                            isDragEnd, isPressed);
                } 
            }
        }
//        startTime += " PE: "+ Utilities.getHourMinuteSecond();
//        iCustomPopup.loadMessageBox((byte)0, ShortHandCanvas.startTime+startTime);
        return isNeedSelect;
    }
    
    
    public void showDateForm(){
        
    }

    /**
     * Method to handle the key pressed event in the smartpopup.
     * This method hides the popup window if it is already existing
     * by calling the hideSmartPopup method and displays the smart 
     * popup using the displaySmartPopup method.
     * 
     * @param type 
     *           <li> 1 - smartPopup </li>
     *           <li> 2 - MessageBox (ok and Cancel) </li>
     *           <li> 3 - MessageBox (OK) </li>
     *           <li> 4 - Notification </li>
     *           <li> 5 - MessageBox User Option Arguments </li>
     *           <li> 6 - Notification Without Goto </li>
     */
    public void handleSmartPopup(int type) {
        if(type == 2 && itemFocused != UISettings.RENAMETEXTBOX)
            return;
        iCustomPopup.handleSmartPopup(type);
    }

    private byte[] getoption(){
        byte[] rbyte = null;
        if(itemFocused == UISettings.RENAMETEXTBOX){
            rbyte = new byte[]{18,47};
        }
        return rbyte;
    }

    /**
     * Method to handle the key pressed event in the rename text box
     * 
     * @param keyCode  key code
     * 
     */
   //#if KEYPAD
   //|JG|    private void handleRenameTextbox(int keyCode) {
   //|JG|        if (UISettings.LEFTOPTION == keyCode) {
   //|JG|            if (UISettings.lOByte > -1) {
   //|JG|                if(Constants.OPTIONS == UISettings.lOByte){
   //|JG|                    CustomCanvas.setOptionsMenuArray(getoption());
   //|JG|                    itemFocused = UISettings.POPUPSCREN;
   //|JG|                    iCustomPopup.setItemFocused(UISettings.OPTIONS);
   //|JG|                }
   //|JG|
   //|JG|                else if (18 == UISettings.lOByte) { // OPT - check for Delete
   //|JG|                    handleSmartPopup(0);
   //|JG|                    iKeyHandler.deleteCharacter(UISettings.RENAMETEXTBOX);
   //|JG|                } else if(UISettings.lOByte == 42){
   //|JG|                    handleSmartPopup(0);
   //|JG|                    iKeyHandler.clearCharcters(UISettings.RENAMETEXTBOX);
   //|JG|                }
   //|JG|                else {
   //|JG|                    ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(),
   //|JG|                            iMenu.getSelectedMenuValue(), UISettings.rOByte);
   //|JG|                }
   //|JG|            }
   //|JG|        }
   //|JG|        else if(UISettings.BACKSPACE == keyCode) {
   //|JG|            if(UISettings.rOByte == 18 || UISettings.lOByte == 18 || UISettings.lOByte == 41){
   //|JG|                handleSmartPopup(0);
   //|JG|                iKeyHandler.deleteCharacter(UISettings.RENAMETEXTBOX);
   //|JG|            }
   //|JG|        } else if(keyCode == UISettings.BACKKEY){
   //|JG|            if(UISettings.lOByte == 41 || 18 == UISettings.rOByte || 18 == UISettings.lOByte ) { // OPT - check for Delete
   //|JG|                    handleSmartPopup(0);
   //|JG|                    iKeyHandler.deleteCharacter(UISettings.RENAMETEXTBOX);
   //|JG|            } else if(UISettings.rOByte == 22){
   //|JG|                ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(),
   //|JG|                            iMenu.getSelectedMenuValue(), UISettings.rOByte);
   //|JG|            }
   //|JG|        } else if (UISettings.RIGHTOPTION == keyCode) {
   //|JG|            if (UISettings.rOByte == 42) { // Index of Clear option
   //|JG|                handleSmartPopup(0);
   //|JG|                iKeyHandler.clearCharcters(UISettings.RENAMETEXTBOX);
   //|JG|            } else if(UISettings.rOByte == 18){ // delete Option Text
   //|JG|                handleSmartPopup(0);
   //|JG|                iKeyHandler.deleteCharacter(UISettings.RENAMETEXTBOX);
   //|JG|            } else if(UISettings.rOByte>-1)
   //|JG|                ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(),
   //|JG|                            iMenu.getSelectedMenuValue(), UISettings.rOByte);
   //|JG|        } else if (UISettings.RIGHTARROW == keyCode) {
   //|JG|            iKeyHandler.HandleTextBoxRightArrow();
   //|JG|        } else if (UISettings.LEFTARROW == keyCode) {
   //|JG|            iKeyHandler.handleTextBoxLeftArrow();
   //|JG|        } else if (UISettings.FIREKEY == keyCode) {
   //|JG|            handleSmartPopup(0);
   //|JG|            iKeyHandler.keyConformed();
   //|JG|            itemFocused = UISettings.MENU;
   //|JG|            lastItemFocused = UISettings.MENU;
   //|JG|            setSearchMenuState();
   //|JG|            reLoadFooterMenu();
   //|JG|            ObjectBuilderFactory.GetKernel().handleRename(iMenu.getSelectedItemId(),
   //|JG|                iMenu.getSelectedMenuValue(), iKeyHandler.getRenameText());
   //|JG|            iKeyHandler.RenametextBoxReset();
   //|JG|        } else if (UISettings.UPKEY != keyCode && UISettings.DOWNKEY != keyCode) {
   //|JG|            iKeyHandler.SetItemFocused(UISettings.RENAMETEXTBOX);
   //|JG|            iKeyHandler.handleRenameTextKey(keyCode);
   //|JG|        }
   //|JG|    }
    //#endif
    /**
     * 
     * @param keyCode
     */
    public void handleInput(int keyCode){
        
    }

    /**
     * Method to highlight the next option available in the option menu
     */
    public byte enableDownSelection() {
        byte isDownSelected = 0;
        if (itemFocused == UISettings.MENU || itemFocused == UISettings.IMAGE_MENU) {
            if (bannerHandler.isBannerSelect()) {
                isDownSelected = 3;
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            } else isDownSelected = 1;
        } else if (itemFocused == UISettings.BANNER) {
            if(isImageView>0){
                if(iImageMenu.isMenuPresent()){
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    reLoadFooterMenu();
                    isDownSelected = 1;
                }
            } else if (iMenu.isMenuPresent()) {
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
                reLoadFooterMenu();
                isDownSelected = 1;
            }
        }
        return isDownSelected;
    }
    
    public byte enableUpSelection(){
        byte isUpSelected = 0;
        if (itemFocused == UISettings.IMAGE_MENU) {
            isUpSelected = 2;
        } else if (itemFocused == UISettings.BANNER) {
             if(isImageView>0){
                if(iImageMenu.isMenuPresent()){
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    reLoadFooterMenu();
                }
            } else if (iMenu.isMenuPresent()) {
                CustomCanvas.sHeader = sHeader;
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
                reLoadFooterMenu();
            }
        } else if(itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14694
             if (iMenu.isMenuPresent()) {
                CustomCanvas.sHeader = sHeader;
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
                reLoadFooterMenu();
            } else if (bannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            } 
        } else isUpSelected = 2;
        return isUpSelected;
    }

    /**
     * Method to reload footer menu based on the item focussed
     */
    public void reLoadFooterMenu() {
        if(UISettings.MENU == itemFocused){
             if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                iMenu.reLoadFooterMenu();
            }
        } else if (UISettings.BANNER == itemFocused) {
            bannerHandler.reLoadFooterMenu();
        }
        //#if KEYPAD
        //|JG|        else if (UISettings.RENAMETEXTBOX == itemFocused) {
        //|JG|            if(iKeyHandler.getRenameTempText().length()>0){
        //|JG|                UISettings.rOByte = 42; // OPT - index for Clear
        //|JG|                UISettings.lOByte = 41; // OPT - index for Delete
        //|JG|            } else {
        //|JG|                UISettings.rOByte = rOByte;
        //|JG|                UISettings.lOByte = -1;
        //|JG|            }
        //|JG|        }
        //#endif
        else if(itemFocused == UISettings.POPUPSCREN) {
            iCustomPopup.reLoadFooterMenu();
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


    /**
     * Method to change a menu item name
     * 
     * @param itemId  Item id of the menu item whose name needs to be changed
     * @param itemName New item name
     */
    public void changeMenuItemName(int itemId, String itemName) {
        if(itemId>-1){
            iMenu.changeMenuItemName(itemId, itemName);
        } else iMenu.updateManuItem(itemName, itemName, (byte)4, ""); //CR 13179 bug 14155,14156

    }

    /**
     * Method to change menu item style
     * 
     * @param itemId  Item id of the menu item whose name needs to be changed
     * @param style  style of the item
     */
    public void changeMenuItemStyle(int itemId, byte style) {
        iMenu.changeMenuItemStyle(itemId, style);
    }

    /**
     * Method to remove menu item
     * 
     * @param iId item Id
     * @param iName item Name
     */
    public void removeMenuItem(int iId, String iName) {
        iMenu.removeMenuItem(iId, iName);
    }

    /**
     * Method to load message box
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
     * Method to rename the menu item. Method adds the item edit box to the view
     * 
     * @param itemId  Item Id of the menu item
     * @param itemName Item Name of the menu item
     */
   //#if KEYPAD
   //|JG|    public void renameMenuItem(int itemId, String itemName) {
   //|JG|        lastItemFocused = UISettings.RENAMETEXTBOX;
   //|JG|        itemFocused = UISettings.RENAMETEXTBOX;
   //|JG|        iKeyHandler.SetItemFocused(UISettings.RENAMETEXTBOX);
   //|JG|        iKeyHandler.setEntryProperty((short)0,(short)30, 0,Float.MAX_VALUE, null, -1, iKeyHandler.ALPHANUMERIC,UISettings.isTocuhScreenNativeTextbox,false);
   //|JG|        iKeyHandler.copyTexttoRenameTextBox(itemName.trim());
   //|JG|        reLoadFooterMenu();
   //|JG|        if(UISettings.isTocuhScreenNativeTextbox)
   //|JG|            showNativeTextbox(30, iKeyHandler.ALPHANUMERIC, false);
   //|JG|    }
   //|JG|
    //#endif
    /**
     * Method to select the last accessed menu item
     * 
     * @param iName Item name to be selected
     * @param iId   Item id.
     */
    public void selectLastAccessedItem(String iName, int iId) {
        iMenu.selectLastAccessedItem(iName, iId);
    }

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
//        itemFocused = UISettings.POPUPSCREN;
//        iCustomPopup.showNotification(isGoTo);
//    }

    /**
     * Method to de-initialize variables.
     */
    private void deInitialize() {
        //#if KEYPAD
        //|JG|        iKeyHandler.deinitialize();
        //#endif
        iCustomPopup.deinitialize();
        
        iMenu.deInitialize();

        iImageMenu.deInitialize();

        imageDisplay.deInitialize();

        iCaptureImage.deInitialize(false);

        bannerHandler.deInitialize();
        
        //Byte
        //ppupkey = 
        rOByte = -1;
        isImageView = -1;
        
        //popkey =
        itemFocused = lastItemFocused = 0;
        //#if KEYPAD
        //|JG|        nTextbox = null;
        //#endif
        display = null;

        textboxValue = "";
//        userPhoneNnmber = null;

//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);

        //bug 13169
        CustomCanvas.deinitialize();
    }

    /**
     * Method to load menu canvas
     * 
     * @param resDTO  Instance of MenuResponseDTO  which contains attributes
     *                to load canvas.
     */
    public void load(MenuResponseDTO resDTO) {
        deInitialize();
        //bug 14783, CR 14789
        imageUploadNumber = resDTO.getUploadId();
        iCaptureImage.setChatId(resDTO.getUploadId());
//        userPhoneNnmber = resDTO.getUserNumber();
        itemFocused = UISettings.MENU;//CR 12767
        lastItemFocused = UISettings.MENU; 
        short mPos = UISettings.headerHeight;
        byte numOfMenuItems = UISettings.numOfMenuItems;
        try {
            if (null != resDTO.getSecHdrTxt()) {
                sHeader = CustomCanvas.getSecondaryHeader(resDTO.getSecHdrTxt(), "",0);
                CustomCanvas.sHeader = sHeader;
                //CR 12817
                mPos += UISettings.secondaryHeaderHeight;
                numOfMenuItems--;
            } else {
                sHeader = resDTO.getSecHdrTxt();
                CustomCanvas.sHeader = resDTO.getSecHdrTxt();
            }
        } catch (Exception e) {
            Logger.loggerError("MenuCanvas load " + e.toString() + e.getMessage());
        }
        iMenu.setMenu(resDTO.getSeqlist(), resDTO.getItemnamelist(),null, 
                resDTO.getItemidlist(), resDTO.getStylelist(),true,null,0);
        if(bannerHandler.setBanner(resDTO.getBannerText(), resDTO.getBannerStyle(),resDTO.getLopttext(),false))
            numOfMenuItems--;
        iMenu.setMenuPosition(mPos, numOfMenuItems, resDTO.getLopttext(),true);
        rOByte = UISettings.rOByte = resDTO.getLopttext();
        UISettings.lOByte = PresenterDTO.setLOptByte();
        //#if KEYPAD
        //|JG|setSearchMenuState();
        //#endif

        //CR 14694
        if(resDTO.getProfileImageType() == 0){
            isImage((byte)53);
        } else if(resDTO.getProfileImageType() == 1){
            isImage((byte)56);
        }

        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    //#if KEYPAD
    //|JG|    private void setSearchMenuState(){
    //|JG|        iKeyHandler.SetItemFocused(UISettings.SEARCH);
    //|JG|        iKeyHandler.setEntryProperty((short)0, Short.MAX_VALUE, 0,Float.MAX_VALUE, null, -1, iKeyHandler.ALPHANUMERIC,false,true);
    //|JG|    }
    //|JG|
    //|JG|
    //|JG|
    //|JG|    public void showNativeTextbox(int maxChar,byte type,boolean isMask){
    //|JG|        display = null;
    //|JG|        nTextbox = new TextBox(CustomCanvas.sHeader,iKeyHandler.getRenameText() , maxChar, TextField.ANY);
    //|JG|        nTextbox.setConstraints(TextField.ANY);
    //|JG|        if(UISettings.isCenterOkOption){
    //|JG|            nTextbox.addCommand(new Command(Constants.options[42], Command.ITEM, 0));
    //|JG|            nTextbox.addCommand(new Command(Constants.options[7],Command.OK , 1));
    //|JG|        } else {
    //|JG|            nTextbox.addCommand(new Command(Constants.options[7],Command.OK , 0));
    //|JG|            nTextbox.addCommand(new Command(Constants.options[42], Command.ITEM, 1));
    //|JG|        }
    //|JG|        nTextbox.setCommandListener(ObjectBuilderFactory.getPCanvas());
    //|JG|        display = Display.getDisplay(ObjectBuilderFactory.GetProgram());
    //|JG|        display.setCurrent(nTextbox);
    //|JG|        reLoadFooterMenu();
    //|JG|    }
    //|JG|
    //#endif
    public byte commandAction(byte priority) {
        //No send any key, just active the JG canvas
        byte rByte = 3;
        //#if KEYPAD
        //|JG|        try{
        //|JG|            if(UISettings.isCenterOkOption){
        //|JG|                if(priority == 0)
        //|JG|                    priority = 1;
        //|JG|                else if(priority == 1)
        //|JG|                    priority = 0;
        //|JG|            }
        //|JG|            if(priority == 0){
        //|JG|                iKeyHandler.copyTexttoRenameTextBox(nTextbox.getString().trim());
        //|JG|                nTextbox = null;
        //|JG|                //Send Fire key
        //|JG|                rByte = 1;
        //|JG|            } else if(priority == 1){
        //|JG|                 nTextbox.setString("");
        //|JG|                 display.setCurrent(nTextbox);
        //|JG|                 //Dont active the JG canvas, still Form Alive
        //|JG|                 return 0;
        //|JG|            } else rByte = 0; //Send Right Key Option
        //|JG|        }catch(Exception e){
        //|JG|            Logger.loggerError("Command Action Exception "+e.toString());
        //|JG|        }
        //|JG|        display = null;
        //|JG|        itemFocused = UISettings.RENAMETEXTBOX;
        //|JG|        lastItemFocused = UISettings.RENAMETEXTBOX;
        //|JG|        reLoadFooterMenu();
        //#endif
        return rByte;
    }


    /**
     * Method to unload the canvas
     */
    public void unLoad() {
        try{
            deInitialize();
            iMenu.unLoad();
        }catch(Exception e){
            Logger.loggerError("Menu canvas unload Error"+e.toString());
        }
    }

    /**
     * 
     * @param isReLoad
     * @param isSend
     */
    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if(isReLoad){
            enablePreviousSelection();
        }
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    /**
     * @param oIndex
     */
    public void handleOptionSelected(byte oIndex) {
        enablePreviousSelection();
        if (itemFocused == UISettings.IMAGE_MENU) {
            UiGlobalVariables.imagefile = null;
            itemFocused = UISettings.VIEW;
            lastItemFocused = UISettings.VIEW;
        } else if(lastItemFocused == UISettings.MENU && iMenu.isMenuPresent()){
            ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(),
                    iMenu.getSelectedMenuValue(),oIndex);
        } 
        //#if KEYPAD
        //|JG|        else if(itemFocused == UISettings.RENAMETEXTBOX){
        //|JG|            if(oIndex == 42){
        //|JG|                handleSmartPopup(0);
        //|JG|                iKeyHandler.clearCharcters(UISettings.RENAMETEXTBOX);
        //|JG|            } else if(oIndex == 18){
        //|JG|                handleSmartPopup(0);
        //|JG|                iKeyHandler.deleteCharacter(UISettings.RENAMETEXTBOX);
        //|JG|            }
        //|JG|        }
        //#endif
        else {
            ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, null, oIndex);
        }
    }
    
    /**
     * 
     * @param id
     * @param value
     */
    public void sendSelectedValue(int id, String value){
        if(itemFocused == UISettings.MENU){
            if(iMenu.isMenuPresent()){
                ObjectBuilderFactory.GetKernel().handleItemSelection(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
            } else {
                ObjectBuilderFactory.GetKernel().handleItemSelection(-2, null);
            }
        } else if(itemFocused == UISettings.IMAGE_MENU){ //CR 14694
            if(imageDisplay.setHeadetText(iImageMenu.getSelectedDisplayMenuValue(), imageUploadNumber, value,
                (byte)4)){
                isImageView = 2;
                reLoadFooterMenu();
            }
        }
    }

    public void handleMessageBoxSelected(boolean isSend,byte msgType,boolean isReload) {
        if(isReload)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
    }

    public void enablePreviousSelection() {
        itemFocused = lastItemFocused;
        //#if KEYPAD
        //|JG|        if(lastItemFocused == UISettings.RENAMETEXTBOX && iMenu.isMenuPresent()){
        //|JG|            renameMenuItem(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
        //|JG|        }
        //#endif
        reLoadFooterMenu();
    }

    
    public int getSmartPopupyPos(int keyCode) {
        return iMenu.getSmartPopupyPos(keyCode);
    }
//#if KEYPAD
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
//|JG|
    //#endif

    public void setItemfocuse(byte itemFocuse) {
        if(itemFocuse == UISettings.OPTIONS){
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            itemFocused = UISettings.POPUPSCREN;
        }
    }

    public void rotateScreen(boolean isLandScape) {
        byte numOfMenuItems = UISettings.numOfMenuItems;
        short mPos = UISettings.headerHeight;
        if(null != CustomCanvas.sHeader){
            mPos += UISettings.secondaryHeaderHeight;
            numOfMenuItems--;
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(CustomCanvas.sHeader, "",0);
        }
        if(bannerHandler.isBannerSelect()){
            numOfMenuItems--;
        }
        iImageMenu.rotateMenu(mPos, numOfMenuItems);
        iMenu.rotateMenu(mPos, numOfMenuItems);
        iCustomPopup.rotatePopup();
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload,boolean isSet) {
        if(isReload) {
            itemFocused = lastItemFocused;
            reLoadFooterMenu();
        }
        //#if KEYPAD
        //|JG|        if(isSet)
        //|JG|            iKeyHandler.appendCharacter(itemFocused, selSymbol);
        //#endif
    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

    public void setImage(ByteArrayOutputStream byteArrayOutputStream){
        if (isImageView == 2 || iCaptureImage.isCurrentScreen()) { //CR 14696
            //CR 14111
            isImageView = -1;
            itemFocused = UISettings.MENU;
            lastItemFocused = UISettings.MENU;
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

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }
}
