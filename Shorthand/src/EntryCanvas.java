
import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * Canvas class for the Entry Presenter Screen
 * sho
 * @author Hakuna Matata
 * @version 2.0
 * @copyright (c) ShartHand Mobile Inc
 */
public class EntryCanvas implements ICanvasHandler, IPopupHandler, IMenuHandler,IGetEntryPresenter
{

    private IKeyHandler iKeyHandler = null;
    private ICustomPopup iCustomPopup = null;
    private ICustomMenu iMenu = null;
    private IBannerHandler iBannerHandler = null;
    //Entry Type
    private int entryType = 0;
    private int tvar = 0;
    private boolean isEntryBoxEnabled;
    private byte rOByte;
    private byte itemFocused,  lastItemFocused;
    int lCount = -1;
//    private Display display = null;
    private DateField dt = null;
    private Form dateForm = null;
    private TextBox nTextbox = null;
    byte nLine = 1;
    //Text box Position
    private short textbPos = 0;
    //Native Textbox
    private boolean isNativeTextbox = false;
    private boolean isNative = false;
    private int minChar = 0;
    private int maxChar = UISettings.MAX_COUNT;
    private float minValue = 0;
    private float maxValue = UISettings.MAX_COUNT;
    private String mask = null;
    private boolean isNotQuery = false;
    private byte numOfMenuItems = 0;
    private String sHeader = null;
    private short textboxSize = 0;

    //CR 14694
    private ImageDisplay imageDisplay = null;
    private ICaptureImage iCaptureImage = null;
    private ICustomMenu iImageMenu = null;
    private byte isImageView = -1;

    private String imageUploadNumber = null;
    
    /**
     * Constructor method
     */
    public EntryCanvas() {

        //CR 14694
        imageDisplay = new ImageDisplay();
        iCaptureImage = new CaptureImageAudio(this);
        iImageMenu = new CustomMenu(this);

        iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        iKeyHandler.setCanvasHandler(this);
        iCustomPopup = new CustomPopup(this);
        iMenu = new CustomMenu(this);
        iBannerHandler = new CustomBanner(this);
    }

    /**
     * Method to paint
     * 
     * @param g
     */
    public void paintGameView(Graphics g) {

        boolean isFirst = false;

        if (iCaptureImage.isCameraScreen()) {
            CustomCanvas.drawHeader(g);
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
        } else {
            if(itemFocused != UISettings.IMAGE_MENU && itemFocused != UISettings.CAPTURE_IMAGE &&
                    iCustomPopup.isCustomPopupState()){
                itemFocused = UISettings.POPUPSCREN;
            }

            //#if KEYPAD
            //|JG|            iKeyHandler.updateKeyTimer();
            //|JG|
            //|JG|            iKeyHandler.updateSearchTimer();
            //#endif

            clearScreen(g);
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
                isFirst = true;
                showScreen(g);
            }
            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
            } else {
                if(itemFocused == UISettings.RENAMETEXTBOX || UISettings.TEXTBOX == itemFocused){ // CR number 6755
                    CustomCanvas.DrawOptionsMenu(iKeyHandler.getKeyMode(), UISettings.lOByte, UISettings.rOByte, g);
                } else if(itemFocused == UISettings.POPUPSCREN) { // 6838
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                }else{
                    CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
                }
            }

            iCustomPopup.drawScreen(g);

            if (isFirst && isNative && itemFocused == UISettings.TEXTBOX) {
                isNative = false;
                iKeyHandler.invokeNativeTextbox();
            }
        }
    }

     //CR 14694
     private void resetImageState(String option, boolean isRest){
         if(isRest){
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
     * Method to clear the Screen
     * 
     * @param g An instance of Graphics class
     */
    private void clearScreen(Graphics g) {
        g.setColor(0x21519c);
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
    }

    /**
     * Method to show the rest of the screen other than primary header and 
     * 
     * @param g An instance of Graphics object
     */
    private void showScreen(Graphics g) {

        if(null != iKeyHandler.getLetterCount()){
            iMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(),
                    iKeyHandler.getLetterCount());
        } else iMenu.drawScreen(g, itemFocused, lastItemFocused, iCustomPopup.isMessageFocused(),"");
        
        drawTextBox(g);

        iBannerHandler.drawScreen(g, itemFocused);

        //#if KEYPAD
        //|JG|        if (UISettings.RENAMETEXTBOX == itemFocused) {
        //|JG|            try {
        //|JG|                CustomCanvas.drawTextBox(0xff35f3, iKeyHandler.getRenameTempText(),
        //|JG|                        iKeyHandler.getKeyChar(), iMenu.getSelectedMenuValue(),
        //|JG|                        iMenu.getMenuPosition(false) + iMenu.getSelectedIndex() * UISettings.itemHeight,
        //|JG|                        iKeyHandler.getRenameTextCursorPos(), textboxSize, g,nLine,-1);
        //|JG|            } catch (Exception e) {
        //|JG|                iKeyHandler.setRenameTextValue(iKeyHandler.getRenameTempText());
        //|JG|            }
        //|JG|        }
        //#endif

        //iCustomPopup.drawScreen(g);
    }

    /**
     * Method to draw text box
     * 
     * @param g   An instance of Graphics class
     */
    private void drawTextBox(Graphics g) {
        if (isEntryBoxEnabled) {
            try {
                if (UISettings.TEXTBOX == lastItemFocused) {
                    CustomCanvas.drawTextBox(0xff35f3, iKeyHandler.getEntryTempText(),
                            iKeyHandler.getKeyChar(), null, textbPos, iKeyHandler.getTextboxCursorPos(),
                            textboxSize, g,nLine,-1);
                } else {
                    CustomCanvas.drawTextBox(00000000, iKeyHandler.getEntryTempText(), ' ', 
                            null, textbPos, iKeyHandler.getTextboxCursorPos(), textboxSize, g,nLine,-1);
                }
            } catch (Exception e) {
                iKeyHandler.setTextboxValue(iKeyHandler.getEntryTempText(), false);
            }
        }
    }

    /**
     * Method to handle the key pressed event based on the item focussed.
     * 
     * @param type Key Code
     **/
    public void keyPressed(int keyCode) {
         //CR 14694
        if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
            iCaptureImage.keyPressed(keyCode);
        } else if(itemFocused == UISettings.IMAGE_MENU){ //CR 14694
           handleImageMenu(keyCode);
        } else if (itemFocused == UISettings.MENU) { //options menu
            handleMenu(keyCode);
        } else if (itemFocused == UISettings.BANNER) {
            iBannerHandler.handleBanner(keyCode);
        } else if (itemFocused == UISettings.RENAMETEXTBOX) {
            handleRenameTextbox(keyCode);
        } else if (itemFocused == UISettings.TEXTBOX) {
            handleTextbox(keyCode);
        } else if (itemFocused == UISettings.POPUPSCREN) {
            iCustomPopup.keyPressed(keyCode);
        }
    }

    //CR 14694
     private void handleMenu(int keyCode) {
         if (isImageView > 0) {
            if (imageDisplay.isBack(keyCode) == 1) {
                //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0,iKeyHandler.getEntryText(), UISettings.rOByte);
                //resetImageState(sHeader,true);
            }
        } else {
            iMenu.handleMenu(keyCode);
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
        } else iImageMenu.handleMenu(keyCode);
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
        return isNotImage;
    }
    
    public boolean  pointerPressed(int x, int y, boolean isPointed, 
            boolean isReleased, boolean isPressed){
        boolean isNeedSelect = false;
        if(UISettings.POPUPSCREN == itemFocused){
            isNeedSelect = iCustomPopup.pointerPressed(x, y, isPointed,isReleased, isPressed);
        } else if(y >= (UISettings.formHeight- UISettings.footerHeight)){
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
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen() &&
                isEntryBoxEnabled && y>=textbPos && y<= (textbPos+textboxSize)) {
            if(isPointed && itemFocused != UISettings.TEXTBOX){
                //#if KEYPAD
                //|JG|                iKeyHandler.SearchValueReset();
                //#endif
                lastItemFocused = UISettings.TEXTBOX;
                itemFocused = UISettings.TEXTBOX;
                iKeyHandler.SetItemFocused(itemFocused);
                setTextboxConstraints();
                reLoadFooterMenu();
                isNative = isNativeTextbox;
            }
        } else if(null != iBannerHandler.getBannerText() && y >= (UISettings.formHeight-(UISettings.footerHeight+UISettings.itemHeight))
                && y<= (UISettings.formHeight - UISettings.footerHeight)){
            if(iBannerHandler.isBannerSelect() && isPointed && itemFocused != UISettings.BANNER){
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            }
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen() &&
                iMenu.isMenuPresent() && y >= iMenu.getMenuPosition(true) && (y <= textbPos || textbPos == 0)){ //Bug Id 8267
            itemFocused = UISettings.MENU;
            lastItemFocused = UISettings.MENU;
            isNeedSelect = iMenu.pointerPressed(x, y, isPointed, isReleased, isPressed);
        }
        return isNeedSelect;
    }

    /**
     * Method to highlight the next option available in the option menu
     */
    public byte enableDownSelection() {
        byte isDownSelected = 0;
        if (itemFocused == UISettings.MENU) {
            if (isEntryBoxEnabled) {
                //isDownSelected = 1;
                //#if KEYPAD
                //|JG|                iKeyHandler.SearchValueReset();
                //#endif
                lastItemFocused = UISettings.TEXTBOX;
                itemFocused = UISettings.TEXTBOX;
                iKeyHandler.SetItemFocused(itemFocused);
                setTextboxConstraints();
                reLoadFooterMenu();
                isNative = isNativeTextbox;
            } else if (iBannerHandler.isBannerSelect()) {
                //isDownSelected = 1;
                lastItemFocused = UISettings.BANNER;
                itemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            } 
        } else if (itemFocused == UISettings.TEXTBOX) {
            if (iBannerHandler.isBannerSelect()) {
                handleSmartPopup(0);
                lastItemFocused = UISettings.BANNER;
                itemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            } else if (iMenu.isMenuPresent()) {
                    handleSmartPopup(0);
                    lastItemFocused = UISettings.MENU;
                    itemFocused = UISettings.MENU;
                    //#if KEYPAD
                    //|JG|                    setMenuConstraints();
                    //#endif
                    reLoadFooterMenu();
                    //Bug 3916
                    //iMenu.selectLastItem();
                    //isDownSelected = 1;
                
            }
        } else if (itemFocused == UISettings.IMAGE_MENU) {
            if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            } 
        } else if(itemFocused == UISettings.BANNER){
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

    /**
     * Method to select the previously focussed component.
     * 
     */
    public byte enableUpSelection() {
        byte isUpSelected = 0;
        if (itemFocused == UISettings.IMAGE_MENU) {
            isUpSelected = 2;
        } else if (itemFocused == UISettings.TEXTBOX) {
            if (iMenu.isMenuPresent()) {
                handleSmartPopup(0);
                lastItemFocused = UISettings.MENU;
                itemFocused = UISettings.MENU;
                //#if KEYPAD
                //|JG|                setMenuConstraints();
                //#endif
                reLoadFooterMenu();
                //Bug 3916
                iMenu.selectLastItem();
            }
        } else if (itemFocused == UISettings.BANNER) {
             if(isImageView>0){
                if(iImageMenu.isMenuPresent()){
                    itemFocused = UISettings.IMAGE_MENU;
                    lastItemFocused = UISettings.IMAGE_MENU;
                    reLoadFooterMenu();
                }
            } else if (isEntryBoxEnabled) {
                lastItemFocused = UISettings.TEXTBOX;
                itemFocused = UISettings.TEXTBOX;
                iKeyHandler.SetItemFocused(itemFocused);
                reLoadFooterMenu();
            } else if (iMenu.isMenuPresent()) {
                lastItemFocused = UISettings.MENU;
                itemFocused = UISettings.MENU;
                reLoadFooterMenu();
            }
        } else if(itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14694
//             if (iMenu.isMenuPresent()) {
//                CustomCanvas.sHeader = sHeader;
//                itemFocused = UISettings.MENU;
//                lastItemFocused = UISettings.MENU;
//                reLoadFooterMenu();
//            } else if (iBannerHandler.isBannerSelect()) {
//                itemFocused = UISettings.BANNER;
//                lastItemFocused = UISettings.BANNER;
//                reLoadFooterMenu();
//            } else  if (isEntryBoxEnabled) {
//                //isDownSelected = 1;
//                //#if KEYPAD
//                iKeyHandler.SearchValueReset();
//                //#endif
//                lastItemFocused = UISettings.TEXTBOX;
//                itemFocused = UISettings.TEXTBOX;
//                iKeyHandler.SetItemFocused(itemFocused);
//                setTextboxConstraints();
//                reLoadFooterMenu();
//                isNative = isNativeTextbox;
//            }
            //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(
                        iImageMenu.getSelectedItemId(), iKeyHandler.getEntryText(), UISettings.rOByte);
        }  else isUpSelected = 2;
        return isUpSelected;
    }

    /**
     * Method to get the options from the back end.
     * 
     */
    private void getOptions() {
        if (UISettings.lOByte > -1) {
            byte[] opts = ObjectBuilderFactory.GetKernel().getOptions(-1, null);
            if (iKeyHandler.getEntryTempText().length() > 0) {
                opts = addClearOption(opts);
            }
            //bug no 12082
            //#if KEYPAD
            //|JG|            else {
            //|JG|                opts = addSymbols(opts);
            //|JG|            }
            //#endif
            if (null != opts) {
                CustomCanvas.setOptionsMenuArray(opts);
                iCustomPopup.setItemFocused(UISettings.OPTIONS);
                itemFocused = UISettings.POPUPSCREN;
            }
        }
    }

    private byte[] addSymbols(byte[] opts) {
        if (null != opts) {
            if (entryType == IKeyHandler.ALPHANUMERIC) {
                byte[] optTemp = new byte[opts.length + 1];
                optTemp[0] = 47;
                System.arraycopy(opts, 0, optTemp, 1, opts.length);
                opts = optTemp;
            }
        } else {
            opts = new byte[]{47};
        }
        return opts;
    }

    /**
     * Method to add the option "clear" to the existing options array.
     * 
     * @param opts Byte Array which contains the existing options
     * @return byte[]
     */
    private byte[] addClearOption(byte[] opts) {
        if (null != opts) {
            byte[] optTemp = null;
            byte count = 1;
            //bug no 12082
            //#if KEYPAD
            //|JG|            if (entryType == IKeyHandler.ALPHANUMERIC) {
            //|JG|                optTemp = new byte[opts.length + 2];
            //|JG|                count = 2;
            //|JG|                optTemp[1] = 47;
            //|JG|            } else 
            //#endif
            {
                optTemp = new byte[opts.length + 1];
            }
            optTemp[0] = 42;
            System.arraycopy(opts, 0, optTemp, count, opts.length);
            opts = optTemp;
            optTemp = null;
        } else {
            if (entryType == IKeyHandler.ALPHANUMERIC) {
                opts = new byte[]{42, 46};
            } else {
                opts = new byte[]{42};
            }
        }
        return opts;
    }

    /**
     * Method to handle the key pressed event in the rename text box.
     * 
     * @param type
     */
    private void handleRenameTextbox(int keyCode) {
        if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte == 42) { //Delete Option Text 
                handleSmartPopup(0);
                iKeyHandler.clearCharcters(lastItemFocused);
            } else if (UISettings.rOByte == 18) { //Clear option Text
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            } else if (UISettings.rOByte > -1) {
                sendTextboxValue(keyCode);
            }
        } else if (UISettings.BACKSPACE == keyCode) {
            if (UISettings.rOByte == 18 || UISettings.lOByte == 18) { //Delte Option  Text
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            }
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                if (UISettings.lOByte == 18) { //Delte option Text
                    handleSmartPopup(0);
                    iKeyHandler.deleteCharacter(itemFocused);
                } else if (UISettings.rOByte == 42) { //Clear option Text
                    handleSmartPopup(0);
                    iKeyHandler.deleteCharacter(itemFocused);
                }
            }
        } else if (UISettings.FIREKEY == keyCode) {
            sendTextboxValue(keyCode);
        } else if (keyCode == UISettings.BACKKEY) {
            if (UISettings.rOByte == 18 || UISettings.lOByte == 18) {
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            } else if (UISettings.rOByte == 22) {
                sendTextboxValue(keyCode);
            }
        } 
        //#if KEYPAD
        //|JG|        else if (UISettings.RIGHTARROW == keyCode) {
        //|JG|            if (iKeyHandler.getRenameTextCursorPos() < iKeyHandler.getRenameTempText().length()) {
        //|JG|                iKeyHandler.keyConformed();
        //|JG|                iKeyHandler.addRenameTextboxCurPos(1);
        //|JG|            }
        //|JG|        } else if (UISettings.LEFTARROW == keyCode) {
        //|JG|            if (iKeyHandler.getRenameTextCursorPos() > 0) {
        //|JG|                iKeyHandler.keyConformed();
        //|JG|                iKeyHandler.addRenameTextboxCurPos(-1);
        //|JG|            }
        //|JG|        } else if (UISettings.UPKEY != keyCode && UISettings.DOWNKEY != keyCode) {
        //|JG|            iKeyHandler.handleRenameTextKey(keyCode);
        //|JG|        }
        //#endif
    }

    /**
     * Method to handle the fire key in the  text box.
     * 
     * @param type Key Code
     **/
    private void sendTextboxValue(int keyCode) {
        handleSmartPopup(0);
        //#if KEYPAD
        //|JG|        iKeyHandler.keyConformed();
        //|JG|        if (UISettings.RENAMETEXTBOX == itemFocused) {
        //|JG|            lastItemFocused = UISettings.MENU;
        //|JG|            itemFocused = UISettings.MENU;
        //|JG|            if (keyCode == UISettings.FIREKEY) {
        //|JG|                ObjectBuilderFactory.GetKernel().handleRename(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue(), iKeyHandler.getRenameText());
        //|JG|            } else if (keyCode == UISettings.RIGHTOPTION || keyCode == UISettings.BACKKEY) {
        //|JG|                ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(), iKeyHandler.getRenameText(), UISettings.rOByte);
        //|JG|            }
        //|JG|            iKeyHandler.RenametextBoxReset();
        //|JG|        } else
            //#endif
            if (UISettings.TEXTBOX == itemFocused) {
            ObjectBuilderFactory.GetKernel().handleOptionSelection(0, iKeyHandler.getEntryText(), UISettings.rOByte);
            iKeyHandler.EntryTextBoxReset();
        }
        reLoadFooterMenu();
    }

    /**
     * Method to handle the key pressed event in the handle text box.
     * 
     * @param type Key Code
     */
    private void handleTextbox(int keyCode) {
        iKeyHandler.SetItemFocused(itemFocused);
        if (UISettings.DOWNKEY == keyCode) {  //Down Arrow Key
            //#if KEYPAD
            //|JG|            iKeyHandler.keyConformed();
            //#endif
            if (nLine > 1) {
                tvar = CustomCanvas.getUpOrDownPos(iKeyHandler.getEntryText(), false, iKeyHandler.getTextboxCursorPos());
                if (tvar > -1) {
                    iKeyHandler.addTextboxCurPos(tvar);
                } else {
                    enableDownSelection();
                }
            } else {
                enableDownSelection();
            }
        } else if (UISettings.UPKEY == keyCode) {  //Up Arrow Key
            //#if KEYPAD
            //|JG|            iKeyHandler.keyConformed();
            //#endif
            if (nLine > 1) {
                tvar = CustomCanvas.getUpOrDownPos(iKeyHandler.getEntryText(), true, iKeyHandler.getTextboxCursorPos());
                if (tvar > -1) {
                    iKeyHandler.addTextboxCurPos(-tvar);
                } else {
                    enableUpSelection();
                }
            } else {
                enableUpSelection();
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {  //Right option Key
            if (UISettings.rOByte > -1) {
                if (UISettings.rOByte == 42) { //Clear Option Text
                    handleSmartPopup(0);
                    iKeyHandler.clearCharcters(lastItemFocused);
                } else if (UISettings.rOByte == 18) { //Delte option Text
                    handleSmartPopup(0);
                    iKeyHandler.deleteCharacter(itemFocused);
                } else { //Back Key
                    sendTextboxValue(keyCode);
                }
            } 
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                if (UISettings.lOByte == 18) { //Delte Option Text
                    handleSmartPopup(0);
                    iKeyHandler.deleteCharacter(itemFocused);
                } else if (UISettings.lOByte == 42) { //Cleat option Text
                    handleSmartPopup(0);
                    iKeyHandler.clearCharcters(itemFocused);
                } else {
                    //#if KEYPAD
                    //|JG|                    iKeyHandler.keyConformed();
                    //#endif
                    getOptions();
                }
            }
        } else if (UISettings.BACKSPACE == keyCode) {
            if (UISettings.lOByte == 18 || UISettings.rOByte == 18) { //Delete Option  Text
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            }
        } 
        //#if KEYPAD
        //|JG|        else if (UISettings.LEFTARROW == keyCode) { //Left Arrow Key
        //|JG|            iKeyHandler.handleTextBoxLeftArrow();
        //|JG|        } else if (UISettings.RIGHTARROW == keyCode) {  //Right Arrow Key
        //|JG|            iKeyHandler.HandleTextBoxRightArrow();
        //|JG|        }
        //#endif
        else if (UISettings.FIREKEY == keyCode) {  //Enter key
            handleSmartPopup(0);
            if (textbox_EnterPressed()) {
                reLoadFooterMenu();
            } else {
                if (isNativeTextbox) {
                    iKeyHandler.invokeNativeTextbox();
                } else {
                    handleSmartPopup(2);
                }
            } //ImproperEntry SmartPopup
        } else if (UISettings.BACKKEY == keyCode) {
            if (UISettings.rOByte == 22) {
                sendTextboxValue(keyCode);
            } else if (UISettings.rOByte == 18 || UISettings.lOByte == 18) {
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            }
        }else {
        //#if KEYPAD
        //|JG|            handleSmartPopup(0);
        //#endif
                    iKeyHandler.handleInputForTextBox(keyCode);
                }
        
    }

    //#if KEYPAD
    //|JG|    public void handleInput(int keyCode) {
    //|JG|        iKeyHandler.handleInputForTextBox(keyCode);
    //|JG|    }
    //#endif

    /**
     * Method to the Y position of the smartpopup
     * 
     * @param type
     * 
     * @return int Y position
     */
    public int getSmartPopupyPos(int keyCode) {
        int y = 0;
        if (UISettings.TEXTBOX == itemFocused && keyCode == 2) {
            y = textbPos - UISettings.popupHeight;
        } else {
            y = iMenu.getSmartPopupyPos(keyCode);
        }
        return y;
    }

    /**
     * Method to show the calendar form. Adds the calendar to the view.
     **/
    public void showDateForm() {
//        display = null;
        Date date = new Date();
        dt = new DateField("", DateField.DATE);
        dt.setDate(date);
        date = null;
        dateForm = new Form(ChannelData.getClientName());
        dateForm.append(dt);
        dateForm.addCommand(new Command(Constants.options[7], Command.EXIT, 0));
        dateForm.addCommand(new Command(Constants.options[8], Command.EXIT, 1));
        dateForm.setCommandListener(ObjectBuilderFactory.getPCanvas());
        Display.getDisplay(ObjectBuilderFactory.GetProgram()).setCurrent(dateForm);
        reLoadFooterMenu();
    }

    /**
     * 
     * @param maxChar
     * @param type
     * @param isMask
     */
    public void showNativeTextbox(int maxChar, byte type, boolean isMask) {
        if (isMask) {
            nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.PASSWORD);
            nTextbox.setConstraints(TextField.PASSWORD);
        } else {
            if (type == IKeyHandler.ALPHA || type == IKeyHandler.ALPHANUMERIC) {
                nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.ANY);
                nTextbox.setConstraints(TextField.ANY);
            } else if (type == IKeyHandler.DECIMAL || type == IKeyHandler.DOLLARCENTS) {
                nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.NUMERIC);
                nTextbox.setConstraints(TextField.NUMERIC);
            } else if (type == IKeyHandler.PHONENUMBER) {
                nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.PHONENUMBER);
                nTextbox.setConstraints(TextField.PHONENUMBER);
            } else {
                nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.NUMERIC);
                nTextbox.setConstraints(TextField.NUMERIC);
            }
        }
        if (UISettings.isCenterOkOption) { //bug id 3619
            nTextbox.addCommand(new Command(Constants.options[7], Command.OK, 0));
            nTextbox.addCommand(new Command(Constants.options[22], Command.EXIT, 1));
        } else {
            nTextbox.addCommand(new Command(Constants.options[22], Command.EXIT, 0));
            nTextbox.addCommand(new Command(Constants.options[7], Command.OK, 1));
        }

        if(UISettings.HASTHIRDSOFTKEY){ //bug id 3619
            nTextbox.addCommand(new Command(Constants.options[42], Command.ITEM, 2));
        }
        //nTextbox.addCommand(new Command(Constants.options[42], Command.ITEM, 2));
        //nTextbox.addCommand(new Command("Save",Command.EXIT , 3));
        nTextbox.setCommandListener(ObjectBuilderFactory.getPCanvas());
        Display.getDisplay(ObjectBuilderFactory.GetProgram()).setCurrent(nTextbox);
        reLoadFooterMenu();
    }

    /**
     * Method to handle enter key press inside the text box
     */
    private boolean textbox_EnterPressed() {
        boolean isSend = true;
        int lco = lCount;
        if (lCount == -1) {
            lco = 0;
        }
        if (entryType == iKeyHandler.ALPHA || entryType == iKeyHandler.ALPHANUMERIC) {
            //#if KEYPAD
            //|JG|            iKeyHandler.keyConformed();
            //#endif
            String temp;//bug 4396
            if (isSend = iKeyHandler.isAlphaMaxCheck(lco)) {
                if (iKeyHandler.isMask()) {
                    temp = Utilities.replace(iKeyHandler.getEntryText(), "[", "(");
                    temp = Utilities.replace(temp, "]", ")");
                    ObjectBuilderFactory.GetKernel().handleItemSelection(0, temp);
                    iKeyHandler.EntryTextBoxReset();
                } else {
                    temp = Utilities.replace(iKeyHandler.getEntryTempText(), "[", "(");
                    temp = Utilities.replace(temp, "]", ")");
                    ObjectBuilderFactory.GetKernel().handleItemSelection(0, temp);
                }
            }
        } else if (entryType == iKeyHandler.NUMERIC || entryType == iKeyHandler.DOLLARCENTS || entryType == iKeyHandler.DECIMAL) {
            if (isSend = iKeyHandler.isNumericCheck(lco)) {
                if (iKeyHandler.isMask()) {
                    ObjectBuilderFactory.GetKernel().handleItemSelection(0, iKeyHandler.getEntryText());
                    iKeyHandler.EntryTextBoxReset();
                } else {
                    ObjectBuilderFactory.GetKernel().handleItemSelection(0, iKeyHandler.getEntryTempText());
                }
            }
        } else if (entryType == iKeyHandler.PHONENUMBER) {
            if (isSend = iKeyHandler.isPhoneCheck()) {
                ObjectBuilderFactory.GetKernel().handleItemSelection(0, iKeyHandler.getEntryText());
            }
        } else if (entryType == iKeyHandler.DATE) {
            if (iKeyHandler.getEntryText().length() > 0) {
                ObjectBuilderFactory.GetKernel().handleItemSelection(0, iKeyHandler.getEntryText());
            } else {
                isSend = false;
            }
        }
        return isSend;
    }

    /**
     * Method to reload footer menu based on the item focussed
     */
    public void reLoadFooterMenu() {
        if (UISettings.MENU == itemFocused) {
            if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                iMenu.reLoadFooterMenu();
            }
        } else if (UISettings.TEXTBOX == itemFocused) {
            UISettings.lOByte = PresenterDTO.setLOptByte();
            if (iKeyHandler.getEntryTempText().length() > 0) {
                if (UISettings.lOByte == -1) {
                    UISettings.rOByte = 42; //Opt Index for the Clear
                    UISettings.lOByte = 18; //Opt Index for the Delete
                } else {
                    UISettings.rOByte = 18; //Opt Index for the Delete
                }
            } else {
                UISettings.rOByte = rOByte;
            }
        } 
        //#if KEYPAD
        //|JG|        else if (UISettings.RENAMETEXTBOX == itemFocused) {
        //|JG|            if (iKeyHandler.getRenameTempText().length() > 0) {
        //|JG|                UISettings.rOByte = 42; // OPT - index for Clear
        //|JG|                UISettings.lOByte = 18; // OPT - index for Delete
        //|JG|            } else {
        //|JG|                UISettings.rOByte = rOByte;
        //|JG|                UISettings.lOByte = -1;
        //|JG|            }
        //|JG|        }
        //#endif
        else if (UISettings.BANNER == itemFocused) {
            iBannerHandler.reLoadFooterMenu();
        } else if (itemFocused == UISettings.POPUPSCREN) {
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
        //#if KEYPAD
        //|JG|        if (null != iKeyHandler) {
        //|JG|            iKeyHandler.setKeyMode();
        //|JG|        }
        //#endif
    }

    /**
     * Method to set the validation parameters of the text box
     * 
     * @param minChar  Minimum character that needs to be entered
     * @param maxChar  Maximum character that is allowed
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @param mask     Mask string if the text needs to be masked
     */
    public void setTextBoxConstraints(int minChar, int maxChar,
            float minValue, float maxValue, String mask, boolean isNotquery) {
        this.minChar = minChar;
        this.maxChar = maxChar;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.mask = mask;
        this.isNotQuery = isNotquery;
    }

    /**
     * Method to remove a menu item from the view
     * 
     * @param iId  Item Id of the menu item
     * @param iName Item Name of the menu item
     */
    public void removeMenuItem(int iId, String iName) {
        iMenu.removeMenuItem(iId, iName);
    }

    /**
     * Method to change a menu item name
     * 
     * @param itemId  Item id of the menu item whose name needs to be changed
     * @param itemName New item name
     */
    public void changeMenuItemName(String itemId, String itemName) {
        iMenu.changeMenuItemName(0, itemName);
    }

    //CR 12118
    //bug 14155
    //bug 14156

    public void changeMenuItemName(String oldValue, byte type, String newValue){
        //CR 13059
        int index = -1;
        if(null != oldValue && (index = oldValue.indexOf("(0)"))>-1){
            oldValue = oldValue.substring(0,index);
        }
        iMenu.updateManuItem(oldValue, null, type, newValue);
    }

    //CR 14672, 14675, 14698
    public void refreshList(String[] contacts, int[] contactId){
        iMenu.updateMenuItems(contacts,contactId);
        ShortHandCanvas.IsNeedPaint();
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
//        if (iCustomPopup.loadMessageBox(type, msg)) {
//            itemFocused = UISettings.POPUPSCREN;
//        }
//    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

//    public void displayMessageSendSprite() {
//        iCustomPopup.setMessageSendSpritTimer();
//    }

    /**
     * Method to select the last accessed menu item
     * 
     * @param iName Item name to be selected
     */
    public void selectLastAccessedItem(String iName) {
        iMenu.selectLastAccessedItem(iName, -3);
    }

    /**
     * Method to rename the menu item. Method adds the item edit box to the view
     * 
     * @param itemId  Item Id of the menu item
     * @param itemName Item Name of the menu item
     */
    //#if KEYPAD
    //|JG|    public void renameMenuItem(int itemId, String itemName) {
    //|JG|        iKeyHandler.setRenameTempText(itemName);
    //|JG|        iKeyHandler.setEntryText(itemName);
    //|JG|        lastItemFocused = UISettings.RENAMETEXTBOX;
    //|JG|        itemFocused = UISettings.RENAMETEXTBOX;
    //|JG|        iKeyHandler.SetItemFocused(itemFocused);
    //|JG|        reLoadFooterMenu();
    //|JG|    }
    //#endif

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
     * Method to deinitialze all the variables
     */
    private void deInitialize() {
        iKeyHandler.deinitialize();
        iMenu.deInitialize();
        iImageMenu.deInitialize();
        imageDisplay.deInitialize();
        iCaptureImage.deInitialize(false);
        iBannerHandler.deInitialize();
        iCustomPopup.deinitialize();
        isImageView = -1;
        
        if (null != dateForm) {
            dateForm.deleteAll();
            dateForm = null;
        }

        if (null != nTextbox) {
            nTextbox = null;
        }

//        userPhoneNumber = null;

        //Int
        lCount = -1;
        tvar = entryType = 0;
        nLine = 1;

        //Boolean
        isEntryBoxEnabled = false;
        isNativeTextbox = false;
        isNative = false;

        //Short
        textbPos = 0;

        itemFocused = 0;
        lastItemFocused = rOByte = 0;
        textboxSize = 0;

        //DateType
        dt = null;

        CustomCanvas.deinitialize();

//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);
    }

    /**
     * Method to copy the text to the text box
     * 
     * @param txt Text to be copied
     */
    public void copyTextToTextBox(String txt, boolean isMaxSet) {
        iKeyHandler.setTextboxValue(txt, isMaxSet);

    }

    /**
     * Method to load the entry presenter screen
     * 
     * @param resDTO  An instance of GetEntryResponseDTO which contains
     *                variables that determine the loading/handling of
     *                entry canvas.
     */
    public void load(GetEntryResponseDTO resDTO) {
        deInitialize();
        numOfMenuItems = UISettings.numOfMenuItems;
        short mPos = UISettings.headerHeight;
        try {
            //bug 14783, CR 14789
            imageUploadNumber = resDTO.getUploadId();
            iCaptureImage.setChatId(resDTO.getUploadId());
            sHeader = resDTO.getSecondaryHeaderText();

            if (UISettings.isTocuhScreenNativeTextbox) {
                isNativeTextbox = UISettings.isTocuhScreenNativeTextbox;
            } else {
                //#if KEYPAD
                //|JG| isNativeTextbox = resDTO.isIsNative();
                //#endif
                //#if !KEYPAD
 isNativeTextbox = true;
                //#endif
            }

            if (null != sHeader) {
                numOfMenuItems--;
                //CR 12817
                mPos += UISettings.secondaryHeaderHeight;
                if ((lCount = resDTO.getLetterCount()) > -1) {
                    CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "160/160", 0);
                } else {
                    CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "", 0);
                }
            }

            entryType = resDTO.getEntryType();

            //Assign the Textbox Draw Position
            if (isEntryBoxEnabled = resDTO.isIsEntryBoxEnabled()) {
                //#if KEYPAD
                //|JG|                iKeyHandler.startTextCursorBlinkTimer();
                //#endif
                numOfMenuItems--;
                if(resDTO.isMultiLineEnabled()){
                    nLine = 2;
                    //textboxSize = (short)(6 + (CustomCanvas.font.getHeight())*2);
                    textboxSize = (short) (12 + (CustomCanvas.font.getHeight()) * 2);
                    if(textboxSize>UISettings.itemHeight){
                        numOfMenuItems--;
                    }
                } else {
                    nLine = 1;
                    //textboxSize = (short)(4 + CustomCanvas.font.getHeight());
                    textboxSize = (short) (8 + CustomCanvas.font.getHeight());
                }

                textbPos = (short) (UISettings.formHeight - (UISettings.footerHeight + textboxSize));
                setTextBoxConstraints(resDTO.getMinChar(), resDTO.getMaxChar(),
                        resDTO.getMinValue(), resDTO.getMaxValue(), resDTO.getMask(),
                        resDTO.isIsNotQuery());
            }

            //Again decrement the Textbox Position
            if (null != resDTO.getBannerText()) {
                numOfMenuItems--;
                textbPos -= UISettings.itemHeight;
                iBannerHandler.setBanner(resDTO.getBannerText(), resDTO.getBannerStyle(), resDTO.getLeftOptionText(), false);
            }
            if (isEntryBoxEnabled) {
                itemFocused = UISettings.TEXTBOX;
                lastItemFocused = UISettings.TEXTBOX;
                setTextboxConstraints();
            }
            //Bug 13117
            byte[] displayByte = getDisplayFontStyle(resDTO.getEntryItems(), resDTO.isIsBold());
            iMenu.setMenu(null, getDisplayValue(resDTO.getEntryItems(),resDTO.isIsBold()),
                    getDisplayValue(resDTO.getEntryItems()), resDTO.getItemId(), 
                    null, false, displayByte,resDTO.getTotalItemCount());

            if (iMenu.isMenuPresent()) {
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
                //#if KEYPAD
                //|JG|                setMenuConstraints();
                //#endif

            }

        } catch (Exception e) {
            Logger.loggerError("EntryCanvas load " + e.toString() + e.getMessage());
        }

        iMenu.setMenuPosition(mPos, numOfMenuItems, resDTO.getLeftOptionText(),true);

        UISettings.lOByte = PresenterDTO.setLOptByte();
        rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
        //#if KEYPAD
        //|JG|        iKeyHandler.setKeyMode();
        //#endif

        reLoadFooterMenu();
        if (iMenu.isMenuPresent() || !isEntryBoxEnabled) {
            itemFocused = UISettings.MENU;
            lastItemFocused = UISettings.MENU;
        } else if (isNativeTextbox && resDTO.isIsShowNative()) {
            isNative = true;
        } else if (UISettings.isTocuhScreenNativeTextbox) {
            isNative = true;
            isNativeTextbox = true;
        }

        //CR 14694
        if(resDTO.getProfileImageType() == 0){
            isImage((byte)53);
        } else if(resDTO.getProfileImageType() == 1){
            isImage((byte)56);
        }

        ShortHandCanvas.IsNeedPaint();
    }

    private void setTextboxConstraints() {
        iKeyHandler.SetItemFocused(UISettings.TEXTBOX);
        iKeyHandler.setEntryProperty(minChar, maxChar, minValue, maxValue, mask, lCount, entryType, isNativeTextbox, isNotQuery);
    }

    //#if KEYPAD
    //|JG|    private void setMenuConstraints() {
    //|JG|        iKeyHandler.SetItemFocused(UISettings.SEARCH);
    //|JG|        iKeyHandler.setEntryProperty((short) 0, Short.MAX_VALUE, 0, Float.MAX_VALUE, null, -1, entryType, false, true);
    //|JG|    }
    //#endif

    /**
     * Method to apply the format and to set the value in the text box
     **/
    private String[] getDisplayValue(String[] itemName) {
        int len;
        String[] tiName = null;
        if (null != itemName && (len = itemName.length) > 0) {
            if (entryType != IKeyHandler.DOLLARCENTS && entryType != IKeyHandler.DECIMAL) {
                tiName = new String[len];
                System.arraycopy(itemName, 0, tiName, 0, len);
                for (int i = 0; i < len; i++) {
                    tiName[i] = tiName[i].trim();
                    iKeyHandler.setEntryText(tiName[i]);
                    iKeyHandler.setEntryTempText(tiName[i]);
                    iKeyHandler.ApplyFormat(false);
                    tiName[i] = iKeyHandler.getEntryTempText();
                }
                iKeyHandler.EntryTextBoxReset();
            }
        }
        return tiName;
    }

    //CR 13059
    private byte[] getDisplayFontStyle(String[] itemName, boolean isBold){
        int len;
        byte[] displayStyle = null;
        if(isBold){
            if (null != itemName && (len = itemName.length) > 0) {
                displayStyle = new byte[len];
                for (int i = 0; i < len; i++) {
                    if(itemName[i].indexOf("(0)") == -1){
                        displayStyle[i] = 1;
                    }
                }
            }
        }
        return displayStyle;
    }

    //CR 13059
    private String[] getDisplayValue(String[] value, boolean  isBold){
        if(isBold && null != value){
            int len = value.length;
            int index = -1;
            for(int i=0;i<len;i++){
                if((index = value[i].indexOf("(0)"))>-1)
                    value[i] = value[i].substring(0,index);
            }
        }
        return value;
    }

    /**
     * Method to deinitialze the canvas
     */
    public void unLoad() {
        try {
            deInitialize();
        } catch (Exception e) {
            Logger.loggerError("Entry Canvas Unload Error " + e.toString());
        }
    }

    /**
     * 
     * @param priority
     * @return
     */
    public byte commandAction(byte priority) {
        //No send any key, just active the JG canvas
        byte rByte = 3;
        try {
            if (isNativeTextbox) {
                if (UISettings.isCenterOkOption) {
                    if (priority == 0) {
                        priority = 1;
                    } else if (priority == 1) {
                        priority = 0;
                    }
                }
                if (priority == 1) {
                    //bug 14464
                    if(null != nTextbox){
                        iKeyHandler.copyTextToTextBox(nTextbox.getString().trim());
                        //Send Fire key
                        rByte = 1;
                    }
                } else if (priority == 2) {
                    //bug 14464
                    if(null != nTextbox){
                        nTextbox.setString("");
                        //Dont active the JG canvas, still Form Alive
                        rByte = 0;
                    }
                } else { // This is for only touch Screen without keypad mobiles
//                    if(isNativeTextbox && !iMenu.isMenuPresent() && rOByte == -1){
//                        return (rByte = 0);
//                    }
                    rByte = 2; //Send Right Key Option
                }
            } else {
                if (priority == 0) {
                    String value = Utilities.getMonthDateYear(dt.getDate().getTime() + "");
                    iKeyHandler.copyTextToTextBox(value.trim());
                }
                if (null != dateForm) {
                    dateForm.deleteAll();
                    dateForm = null;
                }
            }
        } catch (Exception e) {
            Logger.loggerError("Command Action Exception " + e.toString());
        }
        if(rByte != 0){
            reLoadFooterMenu();
        }
        return rByte;
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if (isReLoad) {
            itemFocused = lastItemFocused;
            reLoadFooterMenu();
        }
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    public void handleOptionSelected(byte oIndex) {
        if (oIndex == 42) { //Clear option
            if (lastItemFocused == UISettings.TEXTBOX || lastItemFocused == UISettings.RENAMETEXTBOX) {
                iKeyHandler.clearCharcters(lastItemFocused);
                enablePreviousSelection();
            }
        } else {
            if (lastItemFocused == UISettings.TEXTBOX) {
                enablePreviousSelection();
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, iKeyHandler.getEntryText(), oIndex);
            } else if (lastItemFocused == UISettings.MENU) {
                enablePreviousSelection();
                if (iMenu.isMenuPresent()) {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(iMenu.getSelectedItemId(),
                            iMenu.getSelectedMenuValue(), oIndex);
                } else {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, oIndex);
                }
            } else if (lastItemFocused == UISettings.BANNER) {
                enablePreviousSelection();
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, oIndex);
            } else if (itemFocused == UISettings.IMAGE_MENU) {
                UiGlobalVariables.imagefile = null;
                if(iMenu.isMenuPresent()){
                    itemFocused = UISettings.MENU;
                    lastItemFocused = UISettings.MENU;
                } else if(isEntryBoxEnabled){
                    itemFocused = UISettings.TEXTBOX;
                    lastItemFocused = UISettings.TEXTBOX;
                } 
                reLoadFooterMenu();
            }
        }
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType, boolean isReload) {
        if (isReload) {
            itemFocused = lastItemFocused;
            reLoadFooterMenu();
        }
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend, msgType);
    }

    public void enablePreviousSelection() {
        itemFocused = lastItemFocused;
        //#if KEYPAD
        //|JG|        if (lastItemFocused == UISettings.RENAMETEXTBOX) {
        //|JG|            renameMenuItem(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
        //|JG|        }
        //#endif
        reLoadFooterMenu();
    }

    public void handleSmartPopup(int type) {
        iCustomPopup.handleSmartPopup(type);
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
    //|JG|
    //#endif

    public void setItemfocuse(byte itemFocuse) {
        if (itemFocuse == UISettings.OPTIONS) {
            itemFocused = UISettings.POPUPSCREN;
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
        }
    }

    public void sendSelectedValue(int id, String value) {
        if (itemFocused == UISettings.MENU) {
            if (iMenu.isMenuPresent()) {
                ObjectBuilderFactory.GetKernel().handleItemSelection(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
            } else {
                ObjectBuilderFactory.GetKernel().handleItemSelection(-2, null);
            }
        } else if(itemFocused == UISettings.IMAGE_MENU){
            //CR 14694
            if(imageDisplay.setHeadetText(iImageMenu.getSelectedDisplayMenuValue(),
                    imageUploadNumber, value, (byte)4)){
                isImageView = 2;
                reLoadFooterMenu();
            }
        }
    }

    public void rotateScreen(boolean isLandScape) {
        numOfMenuItems = UISettings.numOfMenuItems;
        short mPos = UISettings.headerHeight;

        if (lCount > -1) {
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "160/160", 0);
        } else {
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "", 0);
        }

        if (null != CustomCanvas.sHeader) {
            numOfMenuItems--;
            mPos += UISettings.secondaryHeaderHeight;
        }

        if (isEntryBoxEnabled) {
            textbPos = (short) (UISettings.formHeight - (UISettings.footerHeight + textboxSize));
            if(textboxSize>UISettings.itemHeight){
                numOfMenuItems -=2;
            } else numOfMenuItems--;
        }

        if (iBannerHandler.isBannerSelect()) {
            numOfMenuItems--;
            textbPos -= UISettings.itemHeight;
        }

        iMenu.rotateMenu(mPos, numOfMenuItems);
        iImageMenu.rotateMenu(mPos, numOfMenuItems);

        iCustomPopup.rotatePopup();
    }

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }

    //#if KEYPAD
    //|JG|    public void handleSymbolpopup(char selSymbol, boolean isReload, boolean isSet) {
    //|JG|        if (isReload) {
    //|JG|            itemFocused = lastItemFocused;
    //|JG|            reLoadFooterMenu();
    //|JG|        }
    //|JG|        
    //|JG|        if (isSet) {
    //|JG|            iKeyHandler.appendCharacter(itemFocused, selSymbol);
    //|JG|        }
    //|JG|    }
    //#endif

        public void setImage(ByteArrayOutputStream byteArrayOutputStream){
        if (isImageView == 2 || iCaptureImage.isCurrentScreen()) { //CR 14418
            //CR 14696
            isImageView = -1;
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
            } else if(isEntryBoxEnabled){
                itemFocused = UISettings.TEXTBOX;
                lastItemFocused = UISettings.TEXTBOX;
            } else if(iBannerHandler.isBannerSelect()){
                itemFocused = UISettings.BANNER;
                lastItemFocused = UISettings.BANNER;
            } else {
                itemFocused = UISettings.MENU;
                lastItemFocused = UISettings.MENU;
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
}
