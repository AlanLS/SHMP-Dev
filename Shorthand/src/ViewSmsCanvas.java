import generated.Build;
import java.io.ByteArrayOutputStream;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


/**
 * View SMS canvas class for the View SMS Presenter Screen
 *
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) ShartHand Mobile Inc
 */
public class ViewSmsCanvas implements IViewSmsPresenter,
        IPopupHandler,IMenuHandler,ICanvasHandler {

    private String messageId;

    private byte rOByte;

    private byte itemFocused,  lastitemFocused;

    /*1. nLine display message to be splited into the number of line
      2. dLint diplay message to be splited into pagewise, so the each page how may highlighted text should have to be stored into the dColor array,
         this is number color page array length
      3. fHeight display page size.
     */
    private int totoalNumberOfPage = 0,  linePerPage = 0, 
            fHeight = 0;

    private float yStartPosition = 0;

    private String[] dMessages = null;

    private byte[] dColor = null;
    private int[][] highlightingLine = null;

    private int selItem = 0;

    private String urlStr = null;

    //CR 10686
    //private String[] eImg = new String[]{":-)", ";-)", ":-(", ":-D", ":-P", ">:-)", ":-O", ":-X", ":-((", ":-/", ":-&", ":-))", ":->>", ":->", ":O)", "O:-)"};

    private ICustomPopup iCustomPopup = null;

    //#if KEYPAD
    //|JG|    private IKeyHandler iKeyHandler = null;
    //#endif

    private ICustomMenu iMenu = null;

    private IBannerHandler iBannerHandler = null;

    private float scrollLen = -1;

    private String rMessage = null;

    private byte numberOfItem = 0;

    private String sHeader = null;

    private Font font = null;

    //private int scrollStartPosition = 8;
    private boolean isUrlHeighlight = false;
    private boolean isNumberHeightlight = false;

    private int previousY = -1;

    private boolean iSScrollEnabled = false;

    private byte isImageView = -1;

    private String[] imageUrls = null;

    private String[] imageCaptions = null;

//    private Image image = null;

    private String[] imageHighlightText = null;

    private int selectImagePosition = -1;

    private ImageDisplay imageDisplay = null;

//    private int preselectImagePosition = -1;

    private String imageUploadNumber = null;

    //CR 14694
    private ICaptureImage iCaptureImage = null;
    private ICustomMenu iImageMenu = null;
//    private byte imageType = -1;
//    private String userPhoneNumber = null;
    
    public ViewSmsCanvas(){
        imageDisplay = new ImageDisplay();
        iCaptureImage = new CaptureImageAudio(this);
        iImageMenu = new CustomMenu(this);
        
        iCustomPopup = new CustomPopup(this);
        iBannerHandler = new CustomBanner(this);
        iMenu = new CustomMenu(this);
        //#if KEYPAD
        //|JG|        iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        //|JG|        iKeyHandler.setCanvasHandler(this);
        //#endif
        if(null == font){
            if(UISettings.isTocuhScreenNativeTextbox){
                if(UISettings.GENERIC && UISettings.formHeight<=160 && UISettings.formWidth<=128){
                    font = CustomCanvas.font;
                } else font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
            } else font = CustomCanvas.font;
        }
    }

    /**
     * Method to paint view sms canvas
     *
     * @param g Instance of Graphics class
     */
    public void paintGameView(Graphics g) {
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

            //CR 12903
            //Draw background image
            CustomCanvas.drawBackgroundImage(g);
            //CR 13900
            if (isImageView > 0) {
                if(!imageDisplay.drawDisplayImage(g)){
                    resetImageState(sHeader, true);
                } else CustomCanvas.drawSecondaryHeader(null, g, true, false);
            } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
                iCaptureImage.drawCaptureImage(g);
            }  else if (itemFocused == UISettings.IMAGE_MENU) { //CR 12542
                iImageMenu.drawScreen(g, itemFocused, lastitemFocused, iCustomPopup.isMessageFocused(), "");
            } else {
                showScreen(g);
                CustomCanvas.drawSecondaryHeader(null, g,true,false);
                //CR 12817
                if(scrollLen>-1 && !iCustomPopup.isMessageFocused()){
                    //CR 12817
                    CustomCanvas.drawScroll(g, scrollLen, UISettings.headerHeight,
                            (-1*yStartPosition*scrollLen),
                            fHeight+UISettings.secondaryHeaderHeight,UISettings.formWidth);
                }
            }
            CustomCanvas.drawHeader(g);


            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
            } else {
                if(itemFocused == UISettings.MENU){ // CR number 6755
                    CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
                } else if (itemFocused == UISettings.POPUPSCREN) {
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                } else {
                    CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
                }
            }

            iCustomPopup.drawScreen(g);
        }
    }

    //CR 14694
     private void resetImageState(String option, boolean isReset){
         if(isReset){
            isImageView = -1;
         } else isImageView = 0;;
        if(null != option){
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(option, "", 0);
        } else CustomCanvas.sHeader = option;
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    /**
     * Method to draw the rest of the screen other than the primary
     * header and secondary header
     *
     * @param g  Instance of Graphics class
     **/
   private void showScreen(Graphics g) {
        g.setFont(font);
        drawPage(g);
        g.setFont(CustomCanvas.font);

        iMenu.drawScreen(g, itemFocused, lastitemFocused, iCustomPopup.isMessageFocused(),null);

        iBannerHandler.drawScreen(g, itemFocused);
    }

   /**
     * Method to clear the screen.
     *
     * @param g  An instance of Graphics class.
     */
    private void clearScreen(Graphics g) {
        // clear the screen first
        g.setColor(0xffffff);
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
    }

    /**
     * Method to draws the entire page with the message
     *
     * @param g  Instance of Grapics class
     */
    private void drawPage(Graphics g) {
        if (null != dMessages) {
            int pages = 0;
            if(yStartPosition != 0)
                pages = (int)(-1*(yStartPosition /(font.getHeight()+2)));

            int len = linePerPage + pages;
            if(len<dMessages.length){
                len++;
                if(len<dMessages.length)
                    len++;
            }
            int displayLine =  pages;

            int selectingItem = 0;
            for(int i=0;i<pages;i++){
                if(dColor[i] != -1)
                    selectingItem += dColor[i];
            }

            if(selectingItem>0)
                selectingItem = selectingItem/4;

            selectingItem = selItem-selectingItem;

            if(yStartPosition != 0)
                pages = (int)(yStartPosition%(font.getHeight()+2));

            int y = UISettings.headerHeight + UISettings.secondaryHeaderHeight + 2 + pages;

            pages = (linePerPage*(font.getHeight()+2)) + UISettings.headerHeight + UISettings.secondaryHeaderHeight;
            //g.setColor(0x000000);
            for(int i=displayLine; i<len && y<pages; i++, y += (font.getHeight() + 2)){
                if(dColor[i] != 0){
                    if(dColor[i] > 0){
                        selectingItem -= drawColorText(g, dMessages[i], y, selectingItem);
                    } else {
                        if(selectingItem == 0)
                            drawLineColorText(g, dMessages[i], 3, y, true);
                        else drawLineColorText(g, dMessages[i], 3, y, false);
                    }
                } else {
                    //CR 12903
                    g.setColor(0XFFFFFF);
                    g.drawString(dMessages[i], 3, y, Graphics.TOP | Graphics.LEFT);
                }
            }
        }
    }

    /**
     * Method to display the hyperlinked text
     *
     * @param g      Instance of Graphics class
     * @param text   String that needs to be drawn
     * @param yPosition      Y coordinate
     * @param count
     * @return       Length of the string
     */
    private byte drawColorText(Graphics g, String text, int y, int selectedItem) {
        int startIndex = 0, endIndex = 0;
        int x = 3;
        byte highlightCount = 0;
        while(text.length()>0){
            startIndex = text.indexOf("<|");
            endIndex = text.indexOf("|>");
            if(startIndex == 0){ //Color Text
                text = text.substring(2);
                if(endIndex == -1){
                    if(selectedItem == highlightCount){
                        drawLineColorText(g, text, x, y, true);
                    } else {
                        drawLineColorText(g, text, x, y, false);
                    }
                    x += font.stringWidth(text);
                    endIndex = text.length();
                } else {
                    endIndex-=2;
                    if(selectedItem == highlightCount){
                        drawLineColorText(g, text.substring(0,endIndex), x, y, true);
                    } else {
                        drawLineColorText(g, text.substring(0,endIndex), x, y, false);
                    }
                    x += font.stringWidth(text.substring(0,endIndex));
                    endIndex += 2; //bug 13224
                    highlightCount += 1;
                }
                text = text.substring(endIndex);
            } else if(endIndex>-1 &&endIndex<startIndex){ //Color Text
                if(selectedItem == highlightCount){
                    drawLineColorText(g, text.substring(0,endIndex), x, y, true);
                } else {
                    drawLineColorText(g, text.substring(0,endIndex), x, y, false);
                }
                x += font.stringWidth(text.substring(0,endIndex));
                text = text.substring(endIndex+2);
                highlightCount += 1;
            } else if(startIndex>-1){ //Noraml Text
                g.setColor(0XFFFFFF);
                g.drawString(text.substring(0,startIndex), x, y, Graphics.TOP|Graphics.LEFT);
                x += font.stringWidth(text.substring(0,startIndex));
                text = text.substring(startIndex);
            } else if(endIndex>-1){ //Color Text
                if(selectedItem == highlightCount){
                    drawLineColorText(g, text.substring(0,endIndex), x, y, true);
                } else {
                    drawLineColorText(g, text.substring(0,endIndex), x, y, false);
                }
                highlightCount += 1;
                x += font.stringWidth(text.substring(0,endIndex));
                text = text.substring(endIndex+2);
            } else {
                g.setColor(0XFFFFFF);
                g.drawString(text, x, y, Graphics.TOP|Graphics.LEFT);
                //x += font.stringWidth(text.substring(0,startIndex));
                text = "";
            }
        }
        return highlightCount;
    }

    /**
     * Method to draw the line for the colored text.
     *
     * @param g     Instance of Graphics class
     * @param text  Text that needs to be underlined
     * @param xPosition     X- Coordinate
     * @param yPosition     Y - Coordinate
     * @param count Text length
     */
    private void drawLineColorText(Graphics g, String text, int x, int y, boolean isHighlight) {
        //g.setColor(0x0000ff);
        //CR 12903
        g.setColor(0xe2c501);
        if (isHighlight && lastitemFocused == UISettings.VIEW) {
            g.fillRect(x - 1, y, font.stringWidth(text)+1, font.getHeight());
            //CR 12903
            g.setColor(0xffffff);
        }
        g.drawString(text, x, y, Graphics.TOP | Graphics.LEFT);
        y += font.getHeight();
        g.drawLine(x-1, y, (x-1) + font.stringWidth(text), y);
    }

    /**
     * Method to draw the emoticons for the given emoticon string
     *
     * @param g     Instance of Graphics class
     * @param esym  Emoticons String for which the emoticon image needs to be
     *              displayed
     * @param y     x - coordinate
     * @param x     y - coordinate
     */
//    private int drawEmoticImage(Graphics g, String esym, int y, int x) {
//        int len = eImg.length;
//        for (int i = 0; i < len; i++) {
//            if (eImg[i].compareTo(esym) == 0) {
//                return CustomCanvas.drawEmothic(g, i, x, y-1);
//            }
//        }
//        return 0;
//    }

    /**
     * Method to handle the key pressed event based on the item focused
     *
     * @param keyCode  key code
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
        } else if (UISettings.BANNER == itemFocused) {
            iBannerHandler.handleBanner(keyCode);
        }  else if(UISettings.POPUPSCREN == itemFocused) {
            iCustomPopup.keyPressed(keyCode);
        }
    }

    //CR 14694
     private void handleKey(int keyCode) {
         //CR 13900
         if (isImageView > 0) {
            if (imageDisplay.isBack(keyCode) == 1) {
                //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(
                        iImageMenu.getSelectedItemId(), iImageMenu.getSelectedMenuValue(), UISettings.rOByte);
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
            lastitemFocused = UISettings.IMAGE_MENU;
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
                    lastitemFocused = UISettings.CAPTURE_IMAGE;
                }
                ShortHandCanvas.IsNeedPaint();
            }
        }
        return isNotImage;
    }

    public boolean pointerPressed(int xPosition, int yPosition, boolean isNotDrag,
            boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;
        if(UISettings.POPUPSCREN == itemFocused){
            previousY = yPosition;
            isNeedSelect = iCustomPopup.pointerPressed(xPosition, yPosition,
                    isNotDrag, isDragEnd, isPressed);
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen() &&
                yPosition > UISettings.headerHeight
                && yPosition<=(UISettings.headerHeight+UISettings.secondaryHeaderHeight+fHeight)) {
            if(scrollLen != -1 || (null != highlightingLine && highlightingLine[0][3]>0)){
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            }
            yPosition -= UISettings.headerHeight;
            boolean isDown = false;
            if(isNotDrag || isPressed){
                previousY = yPosition;
                if(isPressed && CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition &&
                        (-1*yStartPosition*scrollLen) <= yPosition
                        && ((-1*yStartPosition*scrollLen)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
                    iSScrollEnabled = true;
                } else if(null != highlightingLine && yPosition>UISettings.secondaryHeaderHeight){
                    yPosition -= UISettings.secondaryHeaderHeight;
                    yPosition = yPosition/(font.getHeight()+2);
                    if(yStartPosition != 0)
                        yPosition += (-1*yStartPosition)/(font.getHeight()+2);
                    if(yPosition<dColor.length && dColor[yPosition] != 0){
                        int len = highlightingLine.length;
                        int position = -1;
                        String tempString = null;
                        String value = null;
                        int stringStartWidth = 0, stringEndWidth = 0;
                        for(int i=0;i<len;i++){
                            if(highlightingLine[i][0] <= yPosition && highlightingLine[i][2] >= yPosition){
                                if(dColor[yPosition] == -1){
                                    position = i;
                                       // bug 13828
                                } else if(highlightingLine[i][0] == highlightingLine[i][2]){
                                    if(highlightingLine[i][1] == 0){
                                        stringStartWidth = 0;
                                        value = dMessages[yPosition].substring(0,highlightingLine[i][3]);
                                        stringEndWidth = 0;
                                    } else {
                                        tempString = Utilities.replace(dMessages[yPosition].substring(0,highlightingLine[i][1]),"<|","");
                                        tempString = Utilities.replace(tempString,"|>","");
                                        stringStartWidth = 3 + font.stringWidth(tempString);
                                        value = Utilities.replace(dMessages[yPosition].substring(0,highlightingLine[i][3]),"<|","");
                                        value = Utilities.replace(value,"|>","");
                                        stringEndWidth = 3;
                                    }
                                    
                                    stringEndWidth +=  font.stringWidth(value);
                                    if(xPosition >= stringStartWidth && xPosition <= stringEndWidth){
                                        position = i;
                                    }
                                } else if(highlightingLine[i][0] == yPosition){
                                    if(highlightingLine[i][1] == 0){
                                        stringStartWidth = 0;
                                    } else {
                                        tempString = Utilities.replace(dMessages[yPosition].substring(0,highlightingLine[i][1]),"<|","");
                                        tempString = Utilities.replace(tempString,"|>","");
                                        stringStartWidth = 3 + font.stringWidth(tempString);
                                    }
                                    if(xPosition >= stringStartWidth){
                                        position = i;
                                    }
                                } else{
                                    value = Utilities.replace(dMessages[yPosition].substring(0,highlightingLine[i][3]),"<|","");
                                    value = Utilities.replace(value,"|>","");
                                    if(xPosition <= (3 + font.stringWidth(value))){
                                        position = i;
                                    }
                                }
                                   // bug 13828
                                if(position>-1){
                                    if(isNotDrag && selItem == position)
                                        isNeedSelect = true;
                                    selItem =  position;
                                    break;
                                }
                            } else if(highlightingLine[i][0]>yPosition){
                                break;
                            }
                        }
                    }
                }
            } else if(scrollLen>-1){
                //CR 13033
                CustomCanvas.showScroll(isDragEnd);
                //CR 13032
                if(iSScrollEnabled){
                    float position = (totoalNumberOfPage/(totoalNumberOfPage*scrollLen));
                    if((yPosition-previousY) != 0 && position>0)
                        yStartPosition -= (yPosition-previousY)*position;
                    if(yPosition>previousY)
                        isDown = true;
                } else {
                    if(yPosition>UISettings.secondaryHeaderHeight){
                        yStartPosition += yPosition - previousY;
                        if(yPosition<previousY)
                            isDown = true;
                    } else return isNeedSelect;
                }

                if(yStartPosition>0){
                    yStartPosition = 0;
                } else {
                    if(yStartPosition<totoalNumberOfPage){
                        yStartPosition = totoalNumberOfPage;
                    } else {
                        xPosition = (int)((-1*yStartPosition)/(font.getHeight()+2));
                        if(null != highlightingLine &&
                                ((highlightingLine[selItem][0] <= xPosition &&
                                highlightingLine[selItem][2] <= xPosition) || (
                                highlightingLine[selItem][0] >= (xPosition+linePerPage) &&
                                highlightingLine[selItem][2] >= (xPosition+linePerPage)))){
                            if(isDown){
                                for(int i=selItem+1;i<highlightingLine.length;i++){
                                    if(highlightingLine[i][0] >= xPosition &&
                                            highlightingLine[i][2] < (xPosition+linePerPage)){
                                        selItem = i;
                                        break;
                                    }
                                }
                            } else {
                                for(int i=selItem-1;i>-1;i--){
                                    if(highlightingLine[i][0] >= xPosition &&
                                            highlightingLine[i][2] < (xPosition+linePerPage)){
                                        selItem = i;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                previousY = yPosition;
            }
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen() && null != iBannerHandler.getBannerText() && yPosition >= (UISettings.formHeight-(UISettings.footerHeight+UISettings.itemHeight))
                        && yPosition<= (UISettings.formHeight - UISettings.footerHeight)){
            previousY = yPosition;
       //13849
            if(iBannerHandler.isBannerSelect()){
                if(isNotDrag && itemFocused == UISettings.BANNER){
                    isNeedSelect = true;
                }
                 //13849
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
                reloadFooterMenu();
            }
        }  else if(yPosition >= UISettings.formHeight-UISettings.footerHeight){
            previousY = yPosition;
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
                if (null != iBannerHandler.getBannerText()) {
                    mPosition -= UISettings.itemHeight;
                }
                if (yPosition < mPosition) {
                    itemFocused = UISettings.IMAGE_MENU;
                    lastitemFocused = UISettings.IMAGE_MENU;
                    isNeedSelect = iImageMenu.pointerPressed(xPosition, yPosition, isNotDrag,
                            isDragEnd, isPressed);
                }
            }
        } else if(isImageView == -1 && !iCaptureImage.isCurrentScreen() &&
                iMenu.isMenuPresent() && yPosition >= iMenu.getMenuPosition(false)){
            previousY = yPosition;
            int mPosition =  (UISettings.formHeight-UISettings.footerHeight);
            if(null != iBannerHandler.getBannerText())
                mPosition -= UISettings.itemHeight;
            if(yPosition < mPosition){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
                isNeedSelect = iMenu.pointerPressed(xPosition, yPosition, isNotDrag,
                        isDragEnd, isPressed);
            }
        }
        if(isDragEnd)
            iSScrollEnabled = false;
        return isNeedSelect;


    }

    /**
     * Method to handle the key pressed event in the menu
     *
     * @param keyCode  key code
     **/
    private void handleView(int keyCode) {
         if (UISettings.DOWNKEY == keyCode) {
            if(totoalNumberOfPage <= (yStartPosition-(font.getHeight()+2))
                    || (null != highlightingLine && (selItem+1)<highlightingLine.length)){
                int displayLine = 0;
                if(yStartPosition !=0){
                    displayLine = (int)((-1*yStartPosition)/(font.getHeight()+2));
                }
                if(null != highlightingLine && (selItem+1)<highlightingLine.length &&
                        highlightingLine[selItem+1][0] >= displayLine &&
                        highlightingLine[selItem+1][0] < (displayLine+linePerPage)){
                    selItem++;
                } else if(totoalNumberOfPage < yStartPosition){
                    yStartPosition -= (font.getHeight()+2);
                    if(totoalNumberOfPage>yStartPosition)
                        yStartPosition = totoalNumberOfPage;
                    if(yStartPosition != 0)
                        displayLine = (int)((-1*yStartPosition)/(font.getHeight()+2));
                    else displayLine = 0;
                    if(null != highlightingLine && (selItem+1)<highlightingLine.length
                            && highlightingLine[selItem+1][0] >= displayLine &&
                            highlightingLine[selItem+1][0] < (displayLine+linePerPage)){
                        selItem++;
                    }
                }  else enableDownSelection();
            } else enableDownSelection();
          } else if (UISettings.UPKEY == keyCode) {
              if(yStartPosition<0 || selItem>0){
                  int displayLine = 0;
                  if(yStartPosition !=0){
                      displayLine = (int)((-1*yStartPosition)/(font.getHeight()+2));
                  }
                  if(null != highlightingLine && selItem>0 &&
                          highlightingLine[selItem-1][2] >= displayLine &&
                          highlightingLine[selItem-1][2] < (displayLine+linePerPage)){
                      selItem--;
                  } else if(yStartPosition<0) {
                      yStartPosition += (font.getHeight()+2);
                      if(yStartPosition>=0 || (yStartPosition+(font.getHeight()+2)>0)){
                          yStartPosition = 0;
                          displayLine = 0;
                      } else if(yStartPosition != 0) {
                        displayLine = (int)((-1*yStartPosition)/(font.getHeight()+2));
                      } 
                      if(null != highlightingLine && selItem>0 &&
                              highlightingLine[selItem-1][2] >= displayLine &&
                              highlightingLine[selItem-1][2] < (displayLine+linePerPage)){
                        selItem--;
                      }
                  } else enableUpSelection();
              } else enableUpSelection();
           // Bug ID 3677
        } else if(UISettings.RIGHTARROW == keyCode){
            ObjectBuilderFactory.GetKernel().handleOptionSelection(0, messageId, (byte) 27);
        }else if(UISettings.LEFTARROW == keyCode){
             ObjectBuilderFactory.GetKernel().handleOptionSelection(0, messageId, (byte) 26);
        } else if (UISettings.FIREKEY == keyCode) {
            labelLink_Click();
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                getOptions();
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, messageId, UISettings.rOByte);
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.rOByte == 22)
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, messageId, UISettings.rOByte);
        }
    }

    /**
     * Method to get the option Menu and set the option to the Custom canvas
     **/
    private void getOptions() {
        byte[] optbyte = null;
        if(itemFocused == UISettings.VIEW)
            optbyte = ObjectBuilderFactory.GetKernel().getOptions(0, messageId);
        else if(itemFocused == UISettings.BANNER)
            optbyte = ObjectBuilderFactory.GetKernel().getOptions(-1, null);
        if (null != optbyte) {
            CustomCanvas.setOptionsMenuArray(optbyte);
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            itemFocused = UISettings.POPUPSCREN;
            optbyte = null;
        }
    }

    public void handleOptionSelected(byte sOption){
        if(lastitemFocused == UISettings.VIEW){
            itemFocused = UISettings.VIEW;
            ObjectBuilderFactory.GetKernel().handleOptionSelection(0,messageId, sOption);
        } else if(lastitemFocused == UISettings.MENU){
            itemFocused = UISettings.MENU;
            ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, iMenu.getSelectedMenuValue(), sOption);
        } else if(lastitemFocused == UISettings.BANNER){
            itemFocused = UISettings.BANNER;
            ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, null, sOption);
        } else if (itemFocused == UISettings.IMAGE_MENU) {
            UiGlobalVariables.imagefile = null;
            if(scrollLen>-1 || selItem > -1){
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            } else if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
            } else if(iBannerHandler.isBannerSelect()){
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            }
        }
        reloadFooterMenu();
    }

    /**
     *
     */
    public byte enableUpSelection(){
        byte isUpSelected = 0;
         if (itemFocused == UISettings.IMAGE_MENU) {
            isUpSelected = 2;
        } else if(itemFocused == UISettings.MENU){
            //CR 12646
            if(scrollLen != -1 || (null != highlightingLine && highlightingLine[0][3]>0)){
                isUpSelected = 3;
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            } else isUpSelected = 2;
        } else if(itemFocused == UISettings.BANNER){
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
                //bug 13176
                iMenu.selectLastItem();
            } else {
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            }
        } else if(itemFocused == UISettings.VIEW) {
            //bug 13176
            if(null != highlightingLine)
                selItem = highlightingLine.length-1;
            else selItem = -1;
            yStartPosition = totoalNumberOfPage;
            
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
                //bug 13176
                iMenu.selectLastItem();
            } else if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            } 
        } else if(itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14694
//             if (iMenu.isMenuPresent()) {
//                CustomCanvas.sHeader = sHeader;
//                itemFocused = UISettings.MENU;
//                lastitemFocused = UISettings.MENU;
//                reLoadFooterMenu();
//            } else if (iBannerHandler.isBannerSelect()) {
//                itemFocused = UISettings.BANNER;
//                lastitemFocused= UISettings.BANNER;
//                reLoadFooterMenu();
//            } else {
//                 itemFocused = UISettings.VIEW;
//                 lastitemFocused = UISettings.VIEW;
//            }
            //CR 14784
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0,messageId, UISettings.rOByte);
        } 
        reloadFooterMenu();
        return isUpSelected;
    }

    public byte enableDownSelection(){
        byte isDownSelected = 0;
        if(itemFocused == UISettings.VIEW){
            if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
            } else if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            } else { //CR 13030
                if(null != highlightingLine && highlightingLine[0][3]>0)
                    selItem = 0;
                else selItem = -1;
                yStartPosition = 0;
            }
        } else if(itemFocused == UISettings.MENU){
            if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
                isDownSelected = 3;
            } else { //CR 13030
                if(null != highlightingLine && highlightingLine[0][3]>0)
                    selItem = 0;
                else selItem = -1;
                yStartPosition=0;
                if(scrollLen>-1 || selItem >-1){
                    itemFocused = UISettings.VIEW;
                    lastitemFocused = UISettings.VIEW;
                }
            }
        } else if(itemFocused == UISettings.BANNER){ //CR 13030
            if(null != highlightingLine && highlightingLine[0][3]>0)
                selItem = 0;
            else selItem = -1;
            yStartPosition=0;
            if(scrollLen>-1 || selItem >-1){
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            }
        }
        reloadFooterMenu();
        return isDownSelected;
    }

    /**
     * Method to reload footer menu based on the item focussed
     */
    private void reloadFooterMenu() {
        if (itemFocused == UISettings.VIEW || 
                itemFocused == UISettings.MENU ||
                itemFocused == UISettings.BANNER)  {
            if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                UISettings.lOByte = PresenterDTO.setLOptByte();
                UISettings.rOByte = rOByte;
            }
        } else if(itemFocused == UISettings.POPUPSCREN) {
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
     *
     * @param isLoad
     */
    private void loadData(boolean isLoad) {
        try {
            if (isLoad) {
                if(urlStr.startsWith("tel")){
                    ObjectBuilderFactory.GetKernel().sendInteractiveActionMsg(messageId,urlStr.substring(3));
                    ObjectBuilderFactory.GetProgram().platformRequest(urlStr);
                } else {
                    urlStr = Utilities.getFormatedURlString(urlStr);
                    //ObjectBuilderFactory.GetKernel().sendInteractiveActionMsg(messageId,urlStr);
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Invoke URL:"+urlStr);
                    //#endif
                    ObjectBuilderFactory.getPCanvas().platformRequest(urlStr);
                }
            }
        } catch (Exception e) {
            Logger.loggerError("Http Load View SMS Canvas " + e.toString());
        }
        urlStr = null;
    }

    /**
     * Method to handle the onclick event of the label.
     */
    private void labelLink_Click() {
        int displayLine = 0;
        if(yStartPosition !=0){
            displayLine = (int)((-1*yStartPosition)/(font.getHeight()+2));
        }

        if(null != highlightingLine && selItem>-1 && highlightingLine.length>selItem
                && ((highlightingLine[selItem][0]>= displayLine && highlightingLine[selItem][0] <= (displayLine+linePerPage)
                || (highlightingLine[selItem][2] >= displayLine && highlightingLine[selItem][2] <= (displayLine+linePerPage))))){
            String selectedString = null;
            if(highlightingLine[selItem][0] == highlightingLine[selItem][2]){
                selectedString = dMessages[highlightingLine[selItem][0]].substring(highlightingLine[selItem][1]+2,highlightingLine[selItem][3]);
            } else {
                selectedString = dMessages[highlightingLine[selItem][0]].substring(highlightingLine[selItem][1]+2) +
                        dMessages[highlightingLine[selItem][2]].substring(0,highlightingLine[selItem][3]);
            }

            Logger.debugOnError(selItem +" "+selectedString+" "+highlightingLine.length);

            if (null != selectedString) {
                displayLine = 0;
                //CR 13900
                //CR 13974
                if(null != imageHighlightText){
                    String temp = null;
                    for(int i=0;i<=selItem && imageHighlightText.length>displayLine;i++){
                        if(highlightingLine[i][0] == highlightingLine[i][2]){
                            temp = dMessages[highlightingLine[i][0]].substring(highlightingLine[i][1]+2,highlightingLine[i][3]);
                        } else {
                            temp = dMessages[highlightingLine[i][0]].substring(highlightingLine[i][1]+2) +
                                    dMessages[highlightingLine[i][2]].substring(0,highlightingLine[i][3]);
                        }
                        if(temp.compareTo(imageHighlightText[displayLine]) == 0){
                            displayLine++;
                        }
                    }
                    displayLine--;
                    if(displayLine>-1 && imageHighlightText.length>displayLine && imageHighlightText[displayLine].compareTo(selectedString) == 0){
                        selectImagePosition = displayLine;
                        if(imageDisplay.setHeadetText(imageCaptions[selectImagePosition],
                                imageUrls[selectImagePosition],null,(byte)1)){
                            isImageView = 1;
                            reloadFooterMenu();
                        }
                        return;
                    }
                }
                String turlStr = selectedString.toLowerCase();
                if(isUrlHeighlight && (turlStr.startsWith("https://") || turlStr.startsWith("http://") || //CR 12726
                turlStr.startsWith("www.") || turlStr.indexOf(".com")>-1 || turlStr.indexOf(".co")>-1 ||
                turlStr.indexOf(".gov")>-1 || turlStr.indexOf(".edu")>-1 || turlStr.indexOf(".biz")>-1 ||
                turlStr.indexOf(".info")>-1 || turlStr.indexOf(".name")>-1 || turlStr.indexOf(".net")>-1
                || turlStr.indexOf(".org")>-1 || turlStr.indexOf(".pro")>-1 || turlStr.indexOf(".us")>-1
                || turlStr.indexOf(".in")>-1 || turlStr.indexOf(".mobi")>-1 )){
                    urlStr = selectedString;
                   // CustomCanvas.setMessageBoxText(Constants.popupMessage[1],Constants.headerText[9]);
                   // loadMessageBox((byte)5, null);
                    loadData(true);//CR 12815
                } else if (isNumberHeightlight && null !=(urlStr = getValidPhoneNumber(selectedString))) {
                    try {
                        urlStr = Utilities.getformatedCallNumber(selectedString);
                        if(ChannelData.isZeroAppendRegion() && urlStr.length()>8 && urlStr.charAt(0) != '0') //CR 8561
                            urlStr = "tel:0"+ urlStr;
                        else  urlStr = "tel:"+ urlStr;
                        if(Build.CALL_INVOKE){
                            CustomCanvas.setMessageBoxText(Constants.popupMessage[0]+" "+selectedString+"?",Constants.headerText[8]);
                            CustomCanvas.msgType = 7;
//                            loadMessageBox((byte)7, null);
                        } else loadData(true);
                    } catch (Exception e) { }
                } else {
                    ObjectBuilderFactory.GetKernel().handleItemSelection(0, selectedString);
                }
            }

        }
    }

    /**
     * Method to Check the Given String is Valid Phone Number or Not
     * @param pNum PhoneNumber
     * @return Valid PhoneNumber or Null
     */
    private String getValidPhoneNumber(String pNum){
        pNum = Utilities.markPhoneNumber(pNum);
        if(pNum.indexOf("<|") == 0 && pNum.indexOf("|>") == (pNum.length()-2)){
            pNum = pNum.substring(2, pNum.length()-2);
            pNum = Utilities.getformatedCallNumber(pNum);
        } else pNum = null;
        return pNum;
    }



    /**
     * Method to set the message string. This method internally calls
     * markEmoticImage to mark the emoticons and calls the createPageArray
     * to create pages
     *
     * @param message
     */
    private void setMessage(String fLineText,String message,String appName) {

        //CR 12816
        message = removeAppName(message, appName);

        message = Utilities.remove(message,"\r");

//        message = markEmoticImage(message);
        //CR 9798
//        if(null != messageId  && !dontSave)
//            message += "\n" + Utilities.getInboxViewDate(messageId);// Utilities.getViewDate(messageId);
        if(null != fLineText && fLineText.length()>0)
            message += "\n" + fLineText;
//Logger.debugOnError("Message before adding image=" + message);
        //13960
        message = setImageCaptionAndId(message);

        rMessage = message;
        createPageArray(rMessage);

        selItem = -1;
        if(null != highlightingLine && highlightingLine[0][3]>0){
            selItem = 0;
        } 
    }

    //CR 12816
    private String removeAppName(String message, String appName){
        if(null != appName)
        {
            int index = message.indexOf(appName);
            if(index == 0){
                message = message.substring(appName.length());
            } else {
                if((index = appName.indexOf(" "))>-1){
                    appName = appName.substring(0,index);
                    if((index = message.indexOf(appName)) == 0){
                        message = message.substring(appName.length());
                    }
                }
            }
            index = message.indexOf("\n");
            if(index == 0)
                message = message.substring("\n".length());
        }
        return message;
    }

    /**
     * Method identifies the emoticon string available in the message and tags
     * them with the start delimiter "<!" and end delimiter "!>".
     *
     * @param message  Input Message Text
     * @return String  Tagged String
     */
//    private String markEmoticImage(String message) {
//        int len = eImg.length;
//        for (int i = 0; i < len; i++) {
//            message = Utilities.replace(message, eImg[i], "<!" + eImg[i] + "!>");
//        }
//        return message;
//    }

    /**
     * Method to create pages based on the messages. This method creates
     * multiple pages based on the message length. It identifies the linked text
     * available on the each page
     *
     * @param message
     */
  private void createPageArray(String message) {

        String[] msg = Utilities.split(message, "\n");
        String[] tempdMessages = new String[10];
        byte[] tDColor = new byte[10];
        int[][] thighlight = new int[10][4];

        int position = 0;

        int len = msg.length;
        int arrayIndex = -1;

        int linePading = (UISettings.formWidth-8);
        
        String lineText = null;
        String tempValue = null;
        int separaterCount = 0;
        int highlightCount = 0;
        int xPosition = 0;
        int index = -1;
        boolean isNewLine = false;
        String temp = null;
        boolean isSpace = false;
        for(int i=0;i<len;i++){
            isNewLine = true;
            lineText = msg[i].trim();
            if(lineText.length() == 0){
                arrayIndex++;
                if(tempdMessages.length<=arrayIndex){
                    tempdMessages = Utilities.incrementStringArraySize(tempdMessages, arrayIndex, arrayIndex+10);
                    tDColor = Utilities.incrementByteArraySize(tDColor, arrayIndex, arrayIndex+10);
                }
                tempdMessages[arrayIndex] = lineText;
                continue;
            } else {
                xPosition = 2;
                while(lineText.length()>0){
                    if((index = lineText.indexOf(" "))>-1){
                        tempValue = lineText.substring(0,index);
                        isSpace = true;
                    } else {
                        tempValue = lineText;
                        isSpace = false;
                    }

                    if((index = tempValue.indexOf("<|"))>0){
                        tempValue = tempValue.substring(0,index);
                        isSpace = false;
                    }

                    if((index = tempValue.indexOf("|>"))>-1 && ((index+2) != tempValue.length())){
                        tempValue = tempValue.substring(0,index+2);
                        isSpace= false;
                    }

                    temp = tempValue;

                    tempValue = Utilities.remove(tempValue, "<|");
                    tempValue = Utilities.remove(tempValue, "|>");

                    if((font.stringWidth(tempValue)+xPosition) >linePading){
                        index = Utilities.getWidthIndex(tempValue, linePading-xPosition, font);
                        if(index<8 || tempValue.length()<(index+5)){
                            isNewLine = true;
                            if(font.stringWidth(tempValue)>linePading){
                                index = Utilities.getWidthIndex(tempValue, linePading, font);
                                isSpace = false;
                            } else index = tempValue.length();
                        } else {
                            isSpace = false;
                        }
                        if(index != tempValue.length()){
                            tempValue = tempValue.substring(0,index);
                            index = temp.indexOf(tempValue) + tempValue.length();
                            temp = temp.substring(0,index);
                        }
                    }

                    if(isNewLine){
                        arrayIndex++;
                        if(tempdMessages.length <= arrayIndex){
                            tempdMessages = Utilities.incrementStringArraySize(tempdMessages, arrayIndex, arrayIndex+10);
                            tDColor = Utilities.incrementByteArraySize(tDColor, arrayIndex, arrayIndex+10);
                        }

                        xPosition = 2;
                        isNewLine = false;
                    }

                    xPosition += font.stringWidth(tempValue);
                    index = lineText.indexOf(temp) + temp.length();
                    if(isSpace){
                        index += 1;
                        xPosition += font.stringWidth(" ");
                    }

                    tempValue = lineText.substring(0,index);
                    lineText = lineText.substring(index);
                    
                    temp = Utilities.remove(tempValue, "<|");
                    separaterCount = tempValue.length() - temp.length();
                    temp = Utilities.remove(temp, "|>");
                    highlightCount += tempValue.length()  - temp.length();

                    if(separaterCount>0){
                        thighlight[position][0] = arrayIndex;
                        if(null != tempdMessages[arrayIndex])
                            thighlight[position][1] = tempdMessages[arrayIndex].length();
                    }

                    if(null != tempdMessages[arrayIndex]){
                        tempdMessages[arrayIndex] += tempValue;
                    } else tempdMessages[arrayIndex] = tempValue;

                    if(highlightCount>0 && highlightCount%4 == 0){
                        thighlight[position][2] = arrayIndex;
                        if(isSpace)
                            thighlight[position][3] = tempdMessages[arrayIndex].length()-3;
                        else thighlight[position][3] = tempdMessages[arrayIndex].length()-2;
                        position++;
                        if(thighlight.length <= position)
                            thighlight = Utilities.incrementIntTwoArraySize(thighlight, position, position+10,4);
                    }

                    if(separaterCount>0){
                        if(tDColor[arrayIndex] == -1)
                            tDColor[arrayIndex] = 0;
                        tDColor[arrayIndex] += highlightCount;
                        highlightCount = highlightCount%4;
                    } else if(highlightCount == 2 && tDColor[arrayIndex] == 0){
                        tDColor[arrayIndex] = -1;
                    } else if(highlightCount>0 && (highlightCount%4) == 0){
                        if(tDColor[arrayIndex] == -1)
                            tDColor[arrayIndex] = 0;
                        tDColor[arrayIndex] += 2;
                        highlightCount = highlightCount%4;
                    }
                }
            }
        }  

        scrollLen =-1;
        if(arrayIndex>-1){
            arrayIndex++;
            dMessages = Utilities.incrementStringArraySize(tempdMessages, arrayIndex, arrayIndex);
            dColor = Utilities.incrementByteArraySize(tDColor, arrayIndex, arrayIndex);
            if(position>0)
                highlightingLine = Utilities.incrementIntTwoArraySize(thighlight, position, position,4);
            else highlightingLine = null;

            linePerPage = fHeight/(font.getHeight()+2);
            if(linePerPage<dMessages.length){
                totoalNumberOfPage = (dMessages.length - linePerPage);
                totoalNumberOfPage *= (-1*(font.getHeight()+2));
                //CR 12817
                scrollLen = (dMessages.length-linePerPage)*(font.getHeight()+2);
                scrollLen = ((fHeight+UISettings.secondaryHeaderHeight)/ scrollLen);
                separaterCount = CustomCanvas.getScrollHeight(scrollLen);
                if(separaterCount>-1){
                    scrollLen = (dMessages.length-linePerPage)*(font.getHeight()+2);
                    scrollLen =(((fHeight+UISettings.secondaryHeaderHeight)-separaterCount) / scrollLen);
                }
            } else {
                linePerPage = arrayIndex;
                scrollLen = -1;
                totoalNumberOfPage = 0;
            }
        } else {
            dColor = null;
            dMessages = null;
            highlightingLine = null;
        }
        tempdMessages = null;
        tDColor = null;
        thighlight = null;
        msg = null;
    }

    /**
     * Method to set the secondary header
     *
     * @param senderName  Sender Name
     * @param queryType   Query Type
     **/
    public void SetsecondaryHeader(String senderName, String queryType) {
        if (queryType != null) {
            sHeader = senderName + ", " + queryType;
        } else sHeader = senderName;
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "",0);
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
     * Method to deinitialize variables
     */
    private void deInitialize() {

        //CR 13900
        isImageView = -1;
        imageDisplay.deInitialize();
        imageCaptions = null;
        imageHighlightText = null;
        UiGlobalVariables.imagefile = null;
        imageUrls = null;
//        userPhoneNumber = null;
        selectImagePosition = -1;
//        preselectImagePosition = -1;
        
        iSScrollEnabled = false;

        iCustomPopup.deinitialize();

        //#if KEYPAD
        //|JG|        iKeyHandler.deinitialize();
        //#endif

        iBannerHandler.deInitialize();

        iMenu.deInitialize();
        iImageMenu.deInitialize();
        iCaptureImage.deInitialize(false);

        //String
        messageId = null;

        urlStr = null;

        //String Array
        dMessages = null;//mItems = null;

        //byte
        rOByte = -1;
        scrollLen = -1;
        previousY = -1;
        itemFocused = lastitemFocused  =0;// nItems= selectedIndex = 0;
        selItem = -1;

        //byte Array
        dColor = null;
        highlightingLine = null;

        //int
        linePerPage = 0;
        totoalNumberOfPage = 0;
        yStartPosition = 0;

        rMessage = null;

//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);
        
        //bug 13169
        CustomCanvas.deinitialize();
        
    }

    //Cr 13900
    //CR 13974
    private String setImageCaptionAndId(String message){
        int index = 0, sIndex, tIndex,fIndex;
        int poistion = -1;
        imageCaptions = new String[10];
        imageHighlightText =  new String[10];
//        image = new Image[10];
        imageUrls = new String[10];

        while((index=message.indexOf("<img=",index))>-1){
            sIndex = message.indexOf(";",index);
            if(sIndex>-1){
                tIndex = message.indexOf(";",sIndex+1);
                if(tIndex>-1){
                    fIndex = message.indexOf(">",tIndex);
                    if(fIndex>-1){
                        while(message.charAt(fIndex) == '|'){
                            fIndex = message.indexOf(">",fIndex+1);
                        }
                        poistion++;
                        if(poistion>=imageCaptions.length){
                            imageCaptions = Utilities.incrementStringArraySize(imageCaptions,poistion,poistion+10);
                            imageHighlightText = Utilities.incrementStringArraySize(imageHighlightText,poistion,poistion+10);
                            imageUrls = Utilities.incrementStringArraySize(imageUrls,poistion,poistion+10);
//                            image = Utilities.incrementImageArraySize(image,poistion,poistion+10);
                        }
                        imageUrls[poistion] = message.substring(index+5,sIndex);
                        imageHighlightText[poistion] = message.substring(sIndex+1,tIndex);
                        imageCaptions[poistion] = message.substring(tIndex+1,fIndex);
                        if(imageCaptions[poistion].length() == 0)                          
                            imageCaptions[poistion] = "Picture";

                        message = message.substring(0,index)+"<|"+imageHighlightText[poistion]+"|>"+
                                message.substring(fIndex+1);
                        index += ("<|"+imageHighlightText[poistion]+"|>").length();
                    } else break;
                } else break;
            } else break;
        }
        if(poistion>-1){
            poistion += 1;
            imageCaptions = Utilities.incrementStringArraySize(imageCaptions,poistion,poistion);
            imageHighlightText = Utilities.incrementStringArraySize(imageHighlightText,poistion,poistion);
            imageUrls = Utilities.incrementStringArraySize(imageUrls,poistion,poistion);
//            image = Utilities.incrementImageArraySize(image,poistion,poistion);
        } else {
            imageCaptions = null;
            imageHighlightText = null;
            imageUrls = null;
            UiGlobalVariables.imagefile = null;
        }
        index = 0;
        sIndex = 0;
        return message;

    }

    /**
     * Method to load the ViewSms Canvas
     *
     * @param resDTO
     *          Instance of ViewSmsResponseDTO which contains attributes
     *          to load canvas.
     *
     */
    public void load(ViewSmsResponseDTO resDTO)
    {
        deInitialize();
        try {
            //bug 14783, CR 14789
            imageUploadNumber = resDTO.getUploadId();
            iCaptureImage.setChatId(resDTO.getUploadId());
            //cr 12646
            itemFocused = UISettings.VIEW;
            lastitemFocused = UISettings.VIEW;
            short mPos = (short)(UISettings.formHeight - UISettings.footerHeight);
            fHeight = UISettings.formHeight -(UISettings.headerHeight+UISettings.secondaryHeaderHeight + UISettings.footerHeight);
            messageId = resDTO.getMesssageId();
            numberOfItem = 0;

//            imageCaption = resDTO.getImageCaption();
//            imageId = resDTO.getImageId();
            
            isUrlHeighlight = resDTO.isIsUrlHeighlight();
            isNumberHeightlight = resDTO.isIsPhoneNumberEnabled();
            //CR 12903
            iMenu.setMenu(null, resDTO.getMItems(), null, null,
                    getStyle(resDTO.getMItems()), true, null, 0);
            if(iMenu.isMenuPresent()){

               // numberOfItem = (byte)resDTO.getMItems().length;
                if(resDTO.getMItems().length<4)
                    numberOfItem = (byte)resDTO.getMItems().length;
                else numberOfItem = 4;
                mPos -= numberOfItem *UISettings.itemHeight;
                fHeight -= numberOfItem *UISettings.itemHeight;
            }
            SetsecondaryHeader(resDTO.getSenderName(), resDTO.getQueryType());
            rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
            UISettings.lOByte = PresenterDTO.setLOptByte();
            iBannerHandler.setBanner(resDTO.getBannerText(), resDTO.getBannerStyle(),rOByte,true);
            if(null != resDTO.getBannerText()){
                mPos -= UISettings.itemHeight;
                fHeight -= UISettings.itemHeight;
            }
            iMenu.setMenuPosition(mPos, numberOfItem, rOByte,true);
//            scrollStartPosition = 8;
            //CR 12816
            setMessage(resDTO.getFLineText(), resDTO.getMessage(),resDTO.getAppName());
            //();
            //CR 12646
            //bug 12883
            if(iMenu.isMenuPresent() && scrollLen == -1 && selItem == -1)
            {
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
            }

            //CR 14694
            if(resDTO.getProfileImageType() == 0){
                isImage((byte)53);
            } else if(resDTO.getProfileImageType() == 1){
                isImage((byte)56);
            }

        } catch (Exception e) {
            Logger.loggerError("ViewCanvas load " + e.toString() + e.getMessage());
        }
        
        reloadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }


    private int[] getStyle(String[] iName){
        int[] style = null;
        if(null != iName){
            int len = iName.length;
            style = new int[len];
            for(int i=0;i<len;i++)
                style[i] = 4;
        }
        return style;
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
     * Method to unload the view
     */
    public void unLoad() {
        deInitialize();
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if(isReLoad)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType,boolean isReload) {
        if(isReload)
            enablePreviousSelection();
        if (null == urlStr) {
            ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
        } else {
            loadData(isSend);
        }
    }

    public void enablePreviousSelection() {
        this.itemFocused = lastitemFocused;
        reloadFooterMenu();
    }

    public int getSmartPopupyPos(int keyCode) {
        return ((UISettings.formHeight / 2) - (UISettings.popupHeight / 2));
    }

    public void handleInput(int keyCode) {

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
    //|JG|
    //|JG|    }
    //|JG|
    //#endif

    public void setItemfocuse(byte itemFocuse) {
        if(itemFocuse == UISettings.OPTIONS){
            itemFocused = UISettings.POPUPSCREN;
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
        }
    }



    public void sendSelectedValue(int id, String value) {
        if(itemFocused == UISettings.IMAGE_MENU){
            //CR 14694
            if(imageDisplay.setHeadetText(iImageMenu.getSelectedMenuValue(),
                    imageUploadNumber, value, (byte)4)){
                isImageView = 2;
                reLoadFooterMenu();
            }
        } else {
        //if(itemFocused == UISettings.MENU){
            if(iMenu.isMenuPresent())
                ObjectBuilderFactory.GetKernel().handleItemSelection(iMenu.getSelectedItemId(), iMenu.getSelectedMenuValue());
            else
                ObjectBuilderFactory.GetKernel().handleItemSelection(-2, null);
        }
        //}
    }


    public void handleSmartPopup(int type) {

    }

    public void reLoadFooterMenu() {

    }

    public void showDateForm() {

    }

    public void showNativeTextbox(int maxChar, byte type, boolean isMask) {

    }

    public void rotateScreen(boolean isLandScape) {
        short mPos = (short)(UISettings.formHeight - UISettings.footerHeight);
        fHeight = UISettings.formHeight -(UISettings.headerHeight+UISettings.secondaryHeaderHeight + UISettings.footerHeight);
        if(iMenu.isMenuPresent()){
            mPos -= numberOfItem *UISettings.itemHeight;
            fHeight -= numberOfItem *UISettings.itemHeight;
        }
        if(null != iBannerHandler.getBannerText()){
            mPos -= UISettings.itemHeight;
            fHeight -= UISettings.itemHeight;
        }
        iMenu.rotateMenu(mPos, numberOfItem);
        createPageArray(rMessage);
        
        if(selItem>-1){
            yStartPosition = (-1*highlightingLine[selItem][2]) * (font.getHeight()+2);
        } 
        if(yStartPosition<totoalNumberOfPage){
            yStartPosition = totoalNumberOfPage;
        }


        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "",0);
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

    public void setImage(ByteArrayOutputStream byteArrayOutputStream) {
        if(isImageView == 1 && null != byteArrayOutputStream && selectImagePosition>-1){
            //CR 14423
            String fileLocation = imageDisplay.setImage(byteArrayOutputStream, true);
            if(null != fileLocation){
                reloadFooterMenu();
//                if(fileLocation.length() == 0){
//                    CustomCanvas.setMessageBoxText(Constants.popupMessage[69], "Alert");
//                    CustomCanvas.msgType = (byte)4;
//                }
                ShortHandCanvas.IsNeedPaint();
            } 
        } else if(isImageView == 2 || iCaptureImage.isCurrentScreen()){ //CR 14696
            isImageView = -1;
            if(selItem>-1 || scrollLen>-1){
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            } else if(iMenu.isMenuPresent()){
                itemFocused = UISettings.MENU;
                lastitemFocused = UISettings.MENU;
            } else if(iBannerHandler.isBannerSelect()){
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            } else {
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
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

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }
}
