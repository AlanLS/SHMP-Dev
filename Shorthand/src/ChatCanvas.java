
import java.io.ByteArrayOutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * View SMS canvas class for the View SMS Presenter Screen
 *
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) ShartHand Mobile Inc
 */
public class ChatCanvas implements IChatPresenter, IPopupHandler, IMenuHandler, ICanvasHandler {

    private byte rOByte;
    private byte itemFocused, lastitemFocused;

    /*1. nLine display message to be splited into the number of line
    2. dLint diplay message to be splited into pagewise, so the each page how
    may highlighted text should have to be stored into the dColor array,
    this is number color page array length
    3. fHeight display page size.
     */
    private int numberOfLinePerPage = 0,
            numberOfTextboxLine = 1,
            textAreaHeight = 0, totalNumberOfPage = 0;
    private Vector warpedMessages = new Vector();
    private Vector viewImage = new Vector();
    private int[][] highlightingLine = null;
    private ImageDisplay imageDisplay = null;
    private int preselectImagePosition = -1;
    private String urlStr = null;
    private ICustomPopup iCustomPopup = null;
    private IKeyHandler iKeyHandler = null;
    private ICustomMenu iMenu = null;
    private IBannerHandler iBannerHandler = null;
    private float scrollLen = -1;
    private byte numberOfItem = 0;
    private String sHeader = null;
    private Font font = null, dateFont = null;
//    Display display = null;
    TextBox nTextbox = null;
    private boolean isEntryBoxEnabled;
    byte nTextBoxLine = 1;
    //Text box Position
    private short textbPos = 0;
    //Native Textbox
    private boolean isNativeTextbox = false;
    private boolean isNative = false;
    private int lCount = -1;
    private int minChar = 0;
    private int maxChar = UISettings.MAX_COUNT;
    private float minValue = 0;
    private float maxValue = UISettings.MAX_COUNT;
    private ChatHistoryHandler chatHistory = null;
    private short textboxSize = 0;
    private int previousY = -1;
    private float yStartPosition = 0;
    private boolean isScrollEnabled = false;
    private byte isImageView = -1;
    private int selItem = 0;
    private long previousDate = -1;
    private ICaptureImage iCaptureImage = null;

    public ChatCanvas() {
        imageDisplay = new ImageDisplay();
        iCaptureImage = new CaptureImageAudio(this);
        chatHistory = new ChatHistoryHandler();
        iCustomPopup = new CustomPopup(this);
        iBannerHandler = new CustomBanner(this);
        iMenu = new CustomMenu(this);
        iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        iKeyHandler.setCanvasHandler(this);
        if (null == font) {
            if (UISettings.isTocuhScreenNativeTextbox) {
                if (UISettings.GENERIC && UISettings.formHeight <= 160 && UISettings.formWidth <= 128) {
                    font = CustomCanvas.font;
                } else {
                    font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE);
                }
            } else {
                font = CustomCanvas.font;
            }
        }
        if (null == dateFont) {
            dateFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        }
    }

    /**
     * Method to paint view sms canvas
     *
     * @param g Instance of Graphics class
     */
    public void paintGameView(Graphics g) {

        //To Avoid Flickering Issue
        if (iCaptureImage.isCameraScreen()) {
            CustomCanvas.drawHeader(g);
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
        } else if(iCaptureImage.isAudioScreen()){
            clearScreen(g);
            CustomCanvas.drawBackgroundImage(g); //CR 12672
            CustomCanvas.drawHeader(g);
            CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
            iCaptureImage.drawCaptureImage(g);
            //bug 14637
            CustomCanvas.drawSecondaryHeader("", g, true, false);
        } else {

            if (iCustomPopup.isCustomPopupState()) {
                itemFocused = UISettings.POPUPSCREN;
            }

            iKeyHandler.updateKeyTimer();
            //#if KEYPAD
            //|JG|            iKeyHandler.updateSearchTimer();
            //#endif

            clearScreen(g);

            CustomCanvas.drawBackgroundImage(g); //CR 12672

            //CR 13900
            if (isImageView > 0) {
                if(!imageDisplay.drawDisplayImage(g)){
                    resetImageState(sHeader,true);
                } else CustomCanvas.drawSecondaryHeader(null, g, true, false);
            } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
                iCaptureImage.drawCaptureImage(g);
            } else if (itemFocused == UISettings.MENU) { //CR 12542
                iMenu.drawScreen(g, itemFocused, lastitemFocused, iCustomPopup.isMessageFocused(), "");
            } else { //CR 14111
                showScreen(g);
                //CR 12542
                CustomCanvas.drawSecondaryHeader(null, g, true, chatHistory.isMessagePlus);

                //Bug no 12703
                if (scrollLen > -1 && !iCustomPopup.isMessageFocused()) {
                    CustomCanvas.drawScroll(g, scrollLen, UISettings.headerHeight,
                            (-1 * yStartPosition * scrollLen),
                            (textAreaHeight + UISettings.secondaryHeaderHeight), UISettings.formWidth);
                }
            }
            CustomCanvas.drawHeader(g);

            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
            } else if (UISettings.TEXTBOX == itemFocused) { // CR number 6755
                CustomCanvas.DrawOptionsMenu(iKeyHandler.getKeyMode(), UISettings.lOByte, UISettings.rOByte, g);
            } else {
                CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
            }

            iCustomPopup.drawScreen(g);
            //CR 9530
            if (isNative && itemFocused == UISettings.TEXTBOX) {
                isNative = false;
                iKeyHandler.invokeNativeTextbox();
            }
        }
//        else if (itemFocused == UISettings.CAPTURE_IMAGE && !iCaptureImage.isCameraScreen()
//                && !iCaptureImage.isCurrentScreen()) {
//            iCaptureImage.loadCamera();
//        }
        
    }

    /**
     * Method to draw the rest of the screen other than the primary
     * header and secondary header
     *
     * @param g  Instance of Graphics class
     **/
    private void showScreen(Graphics g) {
        g.setFont(font);//bug 11925
        drawPage(g);
        g.setFont(CustomCanvas.font);//bug 11925

        drawTextBox(g);
        iBannerHandler.drawScreen(g, itemFocused);
    }

    private void drawTextBox(Graphics g) {
        if (isEntryBoxEnabled) {
            try {
                //CR 12541
                if (UISettings.TEXTBOX == itemFocused) {
                    CustomCanvas.drawTextBox(0x8cc734, "Click to type message...", iKeyHandler.getKeyChar(),
                            null, textbPos, 0, textboxSize, g, numberOfTextboxLine, 0x3b3b3b);
                } else {
                    CustomCanvas.drawTextBox(0x3b3b3b, "Click to type message...", ' ',
                            null, textbPos, 0, textboxSize, g, numberOfTextboxLine, 0x3b3b3b);
                }
            } catch (Exception e) {
                iKeyHandler.setTextboxValue(iKeyHandler.getEntryTempText(), false);
            }
        }
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
        if (warpedMessages.size() > 0) {
            //Cr 13032
            //CR 13033
            int pages = 0;

            if (yStartPosition != 0) {
                pages = (int) (-1 * yStartPosition) / (font.getHeight() + 3);
            }

            int len = numberOfLinePerPage + pages;
            if (len < warpedMessages.size()) {
                len++;
                if (len < warpedMessages.size()) {
                    len++;
                }
            }

            int selectingItem = 0;
            ChatScriptDto chatScriptDto = null;
            for (int i = 0; i < pages; i++) {
                chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(i);
                selectingItem += chatScriptDto.getMarkedLine();
            }

            selectingItem = selItem - selectingItem;

            int startLine = pages;
            if (yStartPosition != 0) {
                pages = (int) (yStartPosition % (font.getHeight() + 3));
            }

            int y = (UISettings.headerHeight + UISettings.secondaryHeaderHeight + 3) + pages;
            pages = UISettings.headerHeight + UISettings.secondaryHeaderHeight + (numberOfLinePerPage * (font.getHeight() + 3));
            for (int i = startLine; i < len && y < pages; i++, y += (font.getHeight() + 3)) {
                chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(i);
                //View Picture
                if (chatScriptDto.getMarkedLine() == 1) {
                    if (chatScriptDto.getDirection() == chatHistory.DIRECTION_IN) {
                        drawInViewPicture(chatScriptDto, selectingItem, g, y);
                    } else {
                        drawOutViewPicture(chatScriptDto, selectingItem, g, y);
                    }
                    selectingItem--;
                } else if (chatScriptDto.getStatus() == 10) { // dateStatus
                    //CR 14330
                    if (i != startLine) {
                        drawSeparaterLine(g, y - 2);
                    }
                    g.setFont(dateFont);
                    drawDate(chatScriptDto, g, y);
                    g.setFont(font);
                } else if (chatScriptDto.getDirection() == chatHistory.DIRECTION_IN) { //Received Message
                    drawInChatMessage(chatScriptDto, g, y);
                } else { //Sent Message
                    g.setColor(0xFFFFFF); //CR 12671
                    g.drawString(chatScriptDto.getScript(), 5, y, Graphics.TOP | Graphics.LEFT);
                }
            }
        }
    }

    //14330
    private void drawDate(ChatScriptDto chatScriptDto, Graphics graphics, int yPosition) {
        graphics.setColor(0xdcc04e); //Yellowish CR 12671
        int width = UISettings.formWidth;
        if (scrollLen > -1) {
            width -= UISettings.POPUP_SCROLL_WIDTH;
        }
        graphics.drawString(chatScriptDto.getScript(), (width - font.stringWidth(chatScriptDto.getScript())) / 2,
                yPosition, Graphics.TOP | Graphics.LEFT);
    }

    private void drawOutViewPicture(ChatScriptDto chatScriptDto, int selectingItem,
            Graphics graphics, int yPosition) {
        String text = chatScriptDto.getScript();
        graphics.setColor(0xFFFFFF); //CR 12671
        graphics.drawString(Constants.appendText[28] + ": ", 5, yPosition, Graphics.TOP | Graphics.LEFT);
        text = text.substring((Constants.appendText[28] + ": ").length());
        int appendTextWidth = 5 + font.stringWidth(Constants.appendText[28] + ": ");
        graphics.setColor(0xe2c501);
        String markText = "View Picture";
        if(text.indexOf(markText) == -1){
            markText = "Play Audio";
            if(text.indexOf(markText) == -1){
                markText = "Play Movie"; //CR 14629
            }
        }
        if (selectingItem == 0 && lastitemFocused == UISettings.VIEW) {
            graphics.fillRect(appendTextWidth, yPosition, font.stringWidth(markText) + 1, font.getHeight());
            //CR 12903
            graphics.setColor(0xffffff);
        }
        graphics.drawString(markText, appendTextWidth, yPosition, Graphics.TOP | Graphics.LEFT);
        graphics.drawLine(appendTextWidth, yPosition + font.getHeight(),
                appendTextWidth + font.stringWidth(markText), yPosition + font.getHeight());

        //CR 14330
        graphics.setColor(0xFFFFFF); //CR 12671
        graphics.drawString(text.substring(markText.length()), appendTextWidth + font.stringWidth(markText),
                yPosition, Graphics.TOP | Graphics.LEFT);
    }

    private void drawInViewPicture(ChatScriptDto chatScriptDto, int selectingItem,
            Graphics graphics, int yPosition) {
        int width = UISettings.formWidth;
        if (scrollLen > -1) {
            width -= UISettings.POPUP_SCROLL_WIDTH;
        }

        graphics.setColor(0xe2c501);

        String text = chatScriptDto.getScript();

        //CR 14629
        String markText = "View Picture";
        if(text.indexOf(markText) == -1){
            markText = "Play Audio";
            if(text.indexOf(markText) == -1){
                markText = "Play Movie";
            }
        }

        if (text.length() > markText.length()) {
            text = text.substring(markText.length());
            graphics.drawString(text, width - 5, yPosition, Graphics.TOP | Graphics.RIGHT);
        } else {
            text = "";
        }

        if (selectingItem == 0 && lastitemFocused == UISettings.VIEW) {
            graphics.fillRect(width - (5 + font.stringWidth(markText+text)+1),
                    yPosition, font.stringWidth(markText) + 1, font.getHeight());
            //CR 12903
            graphics.setColor(0xffffff);
        }

        graphics.drawString(markText, width - (5 + font.stringWidth(text)),
                yPosition, Graphics.TOP | Graphics.RIGHT);
        //bug 14662
        graphics.drawLine(width - (5 + font.stringWidth(text)+1), yPosition + font.getHeight(),
               width - (5 + font.stringWidth(text+markText)+1) , yPosition + font.getHeight());
    }

    private void drawSeparaterLine(Graphics graphics, int yPosition) {
        graphics.setColor(0x6E6E6E);
        int width = UISettings.formWidth;
        int margin = 10;
        if (scrollLen > -1) {
            width -= UISettings.POPUP_SCROLL_WIDTH;
            margin = 0;
        }
        graphics.drawLine(5, yPosition, width - margin, yPosition);
    }

    private void drawInChatMessage(ChatScriptDto chatScriptDto, Graphics graphics, int yPosition) {
        int width = UISettings.formWidth;
        if (scrollLen > -1) {
            width -= UISettings.POPUP_SCROLL_WIDTH;
        }
        graphics.setColor(0xdcc04e); //Yellowish CR 12671
        graphics.drawString(chatScriptDto.getScript(), width - 5, yPosition, Graphics.TOP | Graphics.RIGHT); //CR 12538
    }

    /**
     * Method to handle the key pressed event based on the item focused
     *
     * @param keyCode  key code
     */
    public void keyPressed(int keyCode) {
        if (itemFocused == UISettings.CAPTURE_IMAGE) { //14418
            iCaptureImage.keyPressed(keyCode);
        } else if (itemFocused == UISettings.VIEW) {
            handleView(keyCode);
        } else if (UISettings.BANNER == itemFocused) {
            iBannerHandler.handleBanner(keyCode);
        } else if (UISettings.MENU == itemFocused) {
            handleMenu(keyCode);
        } else if (UISettings.POPUPSCREN == itemFocused) {
            iCustomPopup.keyPressed(keyCode);
        } else if (itemFocused == UISettings.TEXTBOX) {
            handleTextbox(keyCode);
        }
    }

    private void resetImageState(String option, boolean isReset){
        if(isReset){
            isImageView = -1;
        } else isImageView = 0;
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(option, "", 0);
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    private void handleMenu(int keyCode) {
        if (isImageView == 2) {
            byte back = imageDisplay.isBack(keyCode);
            if (back == 1) {
                resetImageState(Constants.options[53],false);
            } else if (back == 2) {
                reLoadFooterMenu();
            }
        } else {
            iMenu.handleMenu(keyCode);
        }
    }

    private void handleTextbox(int keyCode) {
        iKeyHandler.SetItemFocused(itemFocused);
        if (UISettings.DOWNKEY == keyCode) {  //Down Arrow Key
            //#if KEYPAD
            //|JG|            iKeyHandler.keyConformed();
            //#endif
            if (numberOfLinePerPage > 1) {
                int tvar = CustomCanvas.getUpOrDownPos(iKeyHandler.getEntryText(), false, iKeyHandler.getTextboxCursorPos());
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
            if (numberOfLinePerPage > 1) {
                int tvar = CustomCanvas.getUpOrDownPos(iKeyHandler.getEntryText(), true, iKeyHandler.getTextboxCursorPos());
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
                    iKeyHandler.clearCharcters(lastitemFocused);
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
            if (isNativeTextbox) {
                iKeyHandler.invokeNativeTextbox();
            } else {
                handleSmartPopup(2);
            }
        } else if (UISettings.BACKKEY == keyCode) {
            if (UISettings.rOByte == 22) {
                sendTextboxValue(keyCode);
            } else if (UISettings.rOByte == 18 || UISettings.lOByte == 18) {
                handleSmartPopup(0);
                iKeyHandler.deleteCharacter(itemFocused);
            }
        } else {
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

    private void sendTextboxValue(int keyCode) {
        handleSmartPopup(0);
        //#if KEYPAD
        //|JG|        iKeyHandler.keyConformed();
        //#endif
        if (UISettings.TEXTBOX == itemFocused) {
            //bug 4396
            String temp = Utilities.replace(iKeyHandler.getEntryText(), "[", "(");
            temp = Utilities.replace(temp, "]", ")");
            ObjectBuilderFactory.GetKernel().handleOptionSelection(0, temp, UISettings.rOByte);
            iKeyHandler.EntryTextBoxReset();
        }
        reLoadFooterMenu();
    }

    public boolean pointerPressed(int xPosition, int yPosition, boolean isNotDrag,
            boolean isDragEnd, boolean isPressed) {
        boolean isNeedSelect = false;
//        if(iCaptureImage.isCurrentScreen()){
//            Logger.debugOnError("Camera is On");
//        }
        if (UISettings.POPUPSCREN == itemFocused) {
            previousY = yPosition;
            isNeedSelect = iCustomPopup.pointerPressed(xPosition, yPosition,
                    isNotDrag, isDragEnd, isPressed);
        } else if (isImageView == -1 && !iCaptureImage.isCurrentScreen() //bug 14472
                && isEntryBoxEnabled && yPosition >= textbPos && yPosition <= (textbPos + textboxSize)) {
            previousY = yPosition;
            if (isNotDrag) {
                if (itemFocused != UISettings.TEXTBOX) {
                    //#if KEYPAD
                    //|JG|                    iKeyHandler.SearchValueReset();
                    //#endif
                    lastitemFocused = UISettings.TEXTBOX;
                    itemFocused = UISettings.TEXTBOX;
                    iKeyHandler.SetItemFocused(itemFocused);
                    setTextboxConstraints();
                    reLoadFooterMenu();
                } else {
                    isNative = isNativeTextbox;
                }
            }
        } else if (isImageView == -1 && !iCaptureImage.isCurrentScreen()
                &&  null != iBannerHandler.getBannerText() && yPosition
                >= (UISettings.formHeight - (UISettings.footerHeight + UISettings.itemHeight))
                && yPosition <= (UISettings.formHeight - UISettings.footerHeight)) {
            previousY = yPosition;
            //13849
            if (iBannerHandler.isBannerSelect()) {
                if (isNotDrag && itemFocused == UISettings.BANNER) {
                    isNeedSelect = true;
                }
                //13849
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
                reLoadFooterMenu();
            }
        } else if (yPosition >= UISettings.formHeight - UISettings.footerHeight) {
            previousY = yPosition;
            if (isNotDrag) {
                if (UISettings.rOByte > -1 && xPosition >= (UISettings.formWidth / 2)) {
                    keyPressed(UISettings.RIGHTOPTION);
                } else if (UISettings.lOByte > -1 && xPosition <= (UISettings.formWidth / 2)) {
                    keyPressed(UISettings.LEFTOPTION);
                }
            }
        } else if (itemFocused == UISettings.MENU && !iCaptureImage.isCurrentScreen()) {
            if (iMenu.isMenuPresent() && yPosition >= iMenu.getMenuPosition(false)) {
                previousY = yPosition;
                int mPosition = (UISettings.formHeight - UISettings.footerHeight);
                if (null != iBannerHandler.getBannerText()) {
                    mPosition -= UISettings.itemHeight;
                }
                if (yPosition < mPosition) {
                    itemFocused = UISettings.MENU;
                    lastitemFocused = UISettings.MENU;
                    isNeedSelect = iMenu.pointerPressed(xPosition, yPosition, isNotDrag,
                            isDragEnd, isPressed);
                }
            }
        } else if (isImageView == -1 && !iCaptureImage.isCurrentScreen()
                && yPosition > UISettings.headerHeight
                && yPosition <= (UISettings.headerHeight +
                UISettings.secondaryHeaderHeight + textAreaHeight)) {

            if (scrollLen != -1 || (null != highlightingLine && highlightingLine[0][1] > 0)) {
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            }
            yPosition -= UISettings.headerHeight;
            boolean isDown = false;
            if (isNotDrag || isPressed) {
                previousY = yPosition;
                if (isPressed && CustomCanvas.isShowScroll && UISettings.formWidth - CustomCanvas.SCROLL_WIDTH <= xPosition
                        && ((-1 * yStartPosition * scrollLen) <= yPosition
                        && ((-1 * yStartPosition * scrollLen) + CustomCanvas.SCROLL_WIDTH) >= yPosition)) {
                    isScrollEnabled = true;
                } else if (null != highlightingLine && yPosition > UISettings.secondaryHeaderHeight) {
                    yPosition -= UISettings.secondaryHeaderHeight;
                    yPosition = yPosition / (font.getHeight() + 3);
                    if (yStartPosition != 0) {
                        yPosition += (-1 * yStartPosition) / (font.getHeight() + 3);
                    }
                    if (yPosition < warpedMessages.size() && ((ChatScriptDto) warpedMessages.elementAt(yPosition)).getMarkedLine() > 0) {
                        int position = 0;
                        ChatScriptDto chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(yPosition);
                        if (chatScriptDto.getDirection() == chatHistory.DIRECTION_IN) {
                            position = (UISettings.formWidth - (5 + font.stringWidth(chatScriptDto.getScript())));
                        } else {
                            position = 5;
                        }
                        if (position <= xPosition && (position + font.stringWidth(chatScriptDto.getScript())) >= xPosition) {
                            position = 0;
                            for (int i = 0; i <= yPosition; i++) {
                                chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(i);
                                position += chatScriptDto.getMarkedLine();
                            }
                            position = (position - 1);
                        } else {
                            position = -1;
                        }
                        if (position > -1) {
                            if (isNotDrag && selItem == position) {
                                isNeedSelect = true;
                            }
                            selItem = position;
                        }
                    }
                }
            } else if (scrollLen > -1) {
                //CR 13033
                CustomCanvas.showScroll(isDragEnd);

                //CR 13032
                if (isScrollEnabled) {
                    float position = (totalNumberOfPage / (totalNumberOfPage * scrollLen));
                    if ((yPosition - previousY) != 0 && position > 0) {
                        yStartPosition -= (yPosition - previousY) * position;
                    }
                    if (yPosition > previousY) {
                        isDown = true;
                    }
                } else {
                    if (yPosition > UISettings.secondaryHeaderHeight) {
                        yStartPosition += (yPosition - previousY);
                        if (yPosition < previousY) {
                            isDown = true;
                        }
                    } else {
                        return isNeedSelect;
                    }
                }

                if (yStartPosition > 0) {
                    yStartPosition = 0;
                } else {
                    if (yStartPosition < totalNumberOfPage) {
                        yStartPosition = totalNumberOfPage;
                    } else {
                        xPosition = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
                        if (null != highlightingLine && (highlightingLine[selItem][0] <= xPosition
                                || highlightingLine[selItem][0] >= (xPosition + numberOfLinePerPage))) {
                            if (isDown) {
                                for (int i = selItem + 1; i < highlightingLine.length; i++) {
                                    if (highlightingLine[i][0] >= xPosition
                                            && highlightingLine[i][0] < (xPosition + numberOfLinePerPage)) {
                                        selItem = i;
                                        break;
                                    }
                                }
                            } else {
                                for (int i = selItem - 1; i > -1; i--) {
                                    if (highlightingLine[i][0] >= xPosition
                                            && highlightingLine[i][0] < (xPosition + numberOfLinePerPage)) {
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
        }  //else previousY = yPosition; //Bug 14153

        if (isDragEnd) {
            isScrollEnabled = false;
        }
        return isNeedSelect;
    }

    /**
     * Method to handle the key pressed event in the menu
     *
     * @param keyCode  key code
     **/
    private void handleView(int keyCode) {
        if (isImageView > 0) {
            if (imageDisplay.isBack(keyCode) == 1) {
                resetImageState(sHeader,true);
            }
        } else if (UISettings.DOWNKEY == keyCode) {
            if (totalNumberOfPage <= (yStartPosition - (font.getHeight() + 3))
                    || (null != highlightingLine && (selItem + 1) < highlightingLine.length)) {
                int displayLine = 0;
                if (yStartPosition != 0) {
                    displayLine = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
                }
                if (null != highlightingLine && (selItem + 1) < highlightingLine.length
                        && highlightingLine[selItem + 1][0] >= displayLine
                        && highlightingLine[selItem + 1][0] < (displayLine + numberOfLinePerPage)) {
                    selItem++;
                } else if (totalNumberOfPage < yStartPosition) {
                    yStartPosition -= (font.getHeight() + 3);
                    if (totalNumberOfPage > yStartPosition) {
                        yStartPosition = totalNumberOfPage;
                    }
                    if (yStartPosition != 0) {
                        displayLine = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
                    } else {
                        displayLine = 0;
                    }
                    if (null != highlightingLine && (selItem + 1) < highlightingLine.length
                            && highlightingLine[selItem + 1][0] >= displayLine
                            && highlightingLine[selItem + 1][0] < (displayLine + numberOfLinePerPage)) {
                        selItem++;
                    }
                } else {
                    enableDownSelection();
                }
            } else {
                enableDownSelection();
            }
        } else if (UISettings.UPKEY == keyCode) {
            if (yStartPosition < 0 || selItem > 0) {
                int displayLine = 0;
                if (yStartPosition != 0) {
                    displayLine = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
                }
                if (null != highlightingLine && selItem > 0
                        && highlightingLine[selItem - 1][0] >= displayLine
                        && highlightingLine[selItem - 1][0] < (displayLine + numberOfLinePerPage)) {
                    selItem--;
                } else if (yStartPosition < 0) {
                    yStartPosition += (font.getHeight() + 3);
                    if (yStartPosition >= 0 || (yStartPosition + (font.getHeight() + 3) > 0)) {
                        yStartPosition = 0;
                        displayLine = 0;
                    } else if (yStartPosition != 0) {
                        displayLine = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
                    }
                    if (null != highlightingLine && selItem > 0
                            && highlightingLine[selItem - 1][0] >= displayLine
                            && highlightingLine[selItem - 1][0] < (displayLine + numberOfLinePerPage)) {
                        selItem--;
                    }
                } else {
                    enableUpSelection();
                }
            } else {
                enableUpSelection();
            }
            // Bug ID 3677
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                getOptions();
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
            }
        } else if (keyCode == UISettings.BACKKEY) {
            if (UISettings.rOByte == 22) {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
            }
        } else if (UISettings.FIREKEY == keyCode) {
            labelLink_Click();
        }
    }

    /**
     * Method to get the option Menu and set the option to the Custom canvas
     **/
    private void getOptions() {
        byte[] optbyte = null;
        if (itemFocused == UISettings.VIEW) {
            optbyte = ObjectBuilderFactory.GetKernel().getOptions(0, null);
        } else if (itemFocused == UISettings.BANNER) {
            optbyte = ObjectBuilderFactory.GetKernel().getOptions(-1, null);
        } else if (itemFocused == UISettings.TEXTBOX) {
            optbyte = ObjectBuilderFactory.GetKernel().getOptions(-1, null);
        }
        if (null != optbyte) {
            CustomCanvas.setOptionsMenuArray(optbyte);
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            itemFocused = UISettings.POPUPSCREN;
            optbyte = null;
        }
    }

    private boolean isImage(byte sOption) {
        boolean isNotImage = true;
        if (sOption == 53) { //List Image
            isNotImage = loadMenu();
        } else if (sOption == 56 || sOption == 58 || sOption == 60) { //Capture Image //CR 14418, 14630
            isNotImage = !iCaptureImage.isCapture(((sOption-56)==0)? -1: (sOption-58)/2);
            if (!isNotImage) {
                if(iCaptureImage.loadCamera()){
                    //bug 14637
                    iCaptureImage.rotateScreen();
                    isImageView = 0;
                    itemFocused = UISettings.CAPTURE_IMAGE;
                    lastitemFocused = UISettings.CAPTURE_IMAGE;
                }
                ShortHandCanvas.IsNeedPaint();
            }
        }
        return isNotImage;
    }

    public void handleOptionSelected(byte sOption) {
        boolean isSetMenu = true;
        if (lastitemFocused == UISettings.VIEW) {
            if (isImage(sOption)) {
                itemFocused = UISettings.VIEW;
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, sOption);
            }
        } else if (itemFocused == UISettings.MENU) {
            UiGlobalVariables.imagefile = null;
            itemFocused = UISettings.VIEW;
            lastitemFocused = UISettings.VIEW;
            reLoadFooterMenu();
        } else if (lastitemFocused == UISettings.BANNER) {
            itemFocused = UISettings.BANNER;
            ObjectBuilderFactory.GetKernel().handleOptionSelection(-2, null, sOption);
        } else if (lastitemFocused == UISettings.TEXTBOX) {
            if (sOption == 42) { //Clear option
                iKeyHandler.clearCharcters(lastitemFocused);
                enablePreviousSelection();
                isSetMenu = false;
            } else {
                isSetMenu = isImage(sOption);
            }
            if (isSetMenu) {
                enablePreviousSelection();
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0, iKeyHandler.getEntryText(), sOption);
            }
        }
        reLoadFooterMenu();
    }

    /**
     *
     */
    public byte enableUpSelection() {
        byte isUpSelected = 0;
        if (itemFocused == UISettings.MENU) {
            isUpSelected = 2;
        } else if (itemFocused == UISettings.BANNER) {
            if (isEntryBoxEnabled) {
                lastitemFocused = UISettings.TEXTBOX;
                itemFocused = UISettings.TEXTBOX;
                iKeyHandler.SetItemFocused(itemFocused);
                reLoadFooterMenu();
            } else {
                if (scrollLen != -1 || (null != highlightingLine && highlightingLine[0][1] > 0)) {
                    isUpSelected = 3;
                    itemFocused = UISettings.VIEW;
                    lastitemFocused = UISettings.VIEW;
                } else {
                    isUpSelected = 2;
                }
            }
        } else if (itemFocused == UISettings.TEXTBOX) {
            if (scrollLen != -1 || (null != highlightingLine && highlightingLine[0][1] > 0)) {
                isUpSelected = 3;
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
                if (null != highlightingLine) {
                    selItem = highlightingLine.length - 1;
                } else {
                    selItem = -1;
                }
                yStartPosition = totalNumberOfPage;
            } else {
                isUpSelected = 2;
            }
        } else if (itemFocused == UISettings.VIEW) { //CR 13030, 13156
            //bug 13176
            if (null != highlightingLine) {
                selItem = highlightingLine.length - 1;
            } else {
                selItem = -1;
            }
            yStartPosition = totalNumberOfPage;
            if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            }
        } else if (itemFocused == UISettings.CAPTURE_IMAGE) {
            CustomCanvas.sHeader = sHeader;
            itemFocused = UISettings.VIEW;
            lastitemFocused = UISettings.VIEW;
            isImageView = -1;
        }
        reLoadFooterMenu();
        return isUpSelected;
    }

    public byte enableDownSelection() {
        byte isDownSelected = 0;
        if (itemFocused == UISettings.VIEW) {
            if (isEntryBoxEnabled) {
                isDownSelected = 3;
                //#if KEYPAD
                //|JG|                iKeyHandler.SearchValueReset();
                //#endif
                lastitemFocused = UISettings.TEXTBOX;
                itemFocused = UISettings.TEXTBOX;
                iKeyHandler.SetItemFocused(itemFocused);
                setTextboxConstraints();
                reLoadFooterMenu();
                //CR 12541
                //isNative = isNativeTextbox;
            } else if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
            } else { //CR 13030
                if (null != highlightingLine && highlightingLine[0][1] > 0) {
                    selItem = 0;
                } else {
                    selItem = -1;
                }
                yStartPosition = 0;
            }
        } else if (itemFocused == UISettings.MENU) {
//            if(isEntryBoxEnabled){
//                isDownSelected = 3;
//                //#if KEYPAD
//iKeyHandler.SearchValueReset();
//                //#endif
//                lastitemFocused = UISettings.TEXTBOX;
//                itemFocused = UISettings.TEXTBOX;
//                iKeyHandler.SetItemFocused(itemFocused);
//                setTextboxConstraints();
//                reLoadFooterMenu();
//                //CR 12541
//                    //isNative = isNativeTextbox;
//            } else if (iBannerHandler.isBannerSelect()) {
//                itemFocused = UISettings.BANNER;
//                lastitemFocused = UISettings.BANNER;
//                isDownSelected = 3;
//            } else { //CR 13030
//                if(null != highlightingLine && highlightingLine[0][1]>0)
//                    selItem = 0;
//                else selItem = -1;
//                yStartPosition=0;
//                if(scrollLen>-1 || selItem >-1){
//                    itemFocused = UISettings.VIEW;
//                    lastitemFocused = UISettings.VIEW;
//                }
//            }
        } else if (itemFocused == UISettings.TEXTBOX) {
            if (iBannerHandler.isBannerSelect()) {
                itemFocused = UISettings.BANNER;
                lastitemFocused = UISettings.BANNER;
                isDownSelected = 3;
            } else {
                if (scrollLen > -1) { //CR 13030
                    yStartPosition = 0;
                    itemFocused = UISettings.VIEW;
                    lastitemFocused = UISettings.VIEW;
                }
                if (null != highlightingLine && highlightingLine[0][1] > 0) {
                    selItem = 0;
                } else {
                    selItem = -1;
                }
            }
        } else if (itemFocused == UISettings.BANNER) {
            if (null != highlightingLine && highlightingLine[0][1] > 0) {
                selItem = 0;
            } else {
                selItem = -1;
            }
            yStartPosition = 0;
            if (scrollLen > -1 || selItem > -1) {
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
            }
        }
        reLoadFooterMenu();
        return isDownSelected;
    }

    /**
     * Method to reload footer menu based on the item focussed
     */
    public void reLoadFooterMenu() {
        if (itemFocused == UISettings.VIEW
                || itemFocused == UISettings.BANNER) {
            if (isImageView > 0) {
                UISettings.rOByte = 22; //Back Option Index
                UISettings.lOByte = -1;
            } else {
                isImageView = -1;
                UISettings.lOByte = PresenterDTO.setLOptByte();
                UISettings.rOByte = rOByte;
            }
        } else if (itemFocused == UISettings.MENU) {
            if (isImageView > 0 && imageDisplay.isWait()) {
                UISettings.rOByte = -1;
                UISettings.lOByte = -1;
            } else {
                UISettings.rOByte = 22; //Back Option Index
                if (isImageView == 2) {
                    UISettings.lOByte = 54; //Upload Image option Index
                } else {
                    UISettings.lOByte = 55; //OK Option
                }
            }
        } else if (itemFocused == UISettings.TEXTBOX) {
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
        } else if (itemFocused == UISettings.POPUPSCREN) {
            iCustomPopup.reLoadFooterMenu();
        } else if (itemFocused == UISettings.CAPTURE_IMAGE) { //CR 14418
            iCaptureImage.reLoadFooterMenu();
        }
        ShortHandCanvas.IsNeedPaint();
        
    }

    /**
     *
     * @param isLoad
     */
    private void loadData(boolean isLoad) {
        try {
            if (isLoad) {
                if (urlStr.startsWith("tel")) {
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
        if (yStartPosition != 0) {
            displayLine = (int) ((-1 * yStartPosition) / (font.getHeight() + 3));
        }

        if (null != highlightingLine && selItem > -1 && highlightingLine.length > selItem
                && ((highlightingLine[selItem][0] >= displayLine && highlightingLine[selItem][0] <= (displayLine + numberOfLinePerPage)))) {
            ChatScriptDto chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(highlightingLine[selItem][0]);
            //CR 13900
            //CR 13974
            ///CR 14112
            if (chatScriptDto.getMarkedLine() > 0) {
                if (preselectImagePosition != selItem) {
                    UiGlobalVariables.imagefile = null;
                }
                preselectImagePosition = selItem;
                //Image Caption Index 0
                //Image Id Index 1
                String[] imagesText = (String[]) viewImage.elementAt(selItem);
                //CR 14423, 14491
                
                if(imageDisplay.setHeadetText(imagesText[0], imagesText[1],
                        chatScriptDto.getFileLocation(),  imagesText[3].compareTo("-1") == 0?
                            (byte)0 : (byte)(Integer.parseInt(imagesText[3])+2))){
                    isImageView = 1;
                    reLoadFooterMenu();
                }
            }
        }
    }

    public byte commandAction(byte priority) {
        //No send any key, just active the JG canvas
        byte rByte = 3;
        try {
            if (iCaptureImage.isCurrentScreen()) {
                rByte = iCaptureImage.commandAction(priority);
            } else if (isNativeTextbox) {
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
                        if (textbox_EnterPressed()) {
                            reLoadFooterMenu();
                            rByte = 3; //CR 12541
                        }
                    } 
//                    else {
//                        Display.getDisplay(Obj).setCurrent(nTextbox);
//                    }
                    //Send Fire key
                } else if (priority == 2) {
                    //bug 14464
                    if(null != nTextbox){
                        nTextbox.setString("");
    //                    display.setCurrent(nTextbox);
                        //Dont active the JG canvas, still Form Alive
                        rByte = 0;
                        return rByte;
                    } 
                    
                } else { // This is for only touch Screen without keypad mobiles
                    //if(null != dMessages && dMessages.length>0){
                    //rByte = 0;
                    //} else rByte = 2;
//                    if(isNativeTextbox && !iMenu.isMenuPresent() && rOByte == -1){
//                        return (rByte = 0);
//                    }
                    rByte = 2; //Send Right Key Option
                }
            }
        } catch (Exception e) {
            Logger.loggerError("Command Action Exception " + e.toString());
        }
//        display = null;
        reLoadFooterMenu();
        return rByte;
    }

    //Cr 13900
    //CR 13974
    //CR 14112
    //Cr 14629
    private String setImageCaptionAndId(String message) {
        int index = 0, sIndex, tIndex, fIndex, audioIndex=0, videoIndex=0;
        byte isAudio = -1;
        try {
            String[] imageText = null;

            while ((index = message.indexOf("<img=", index)) > -1 || 
                    (audioIndex = message.indexOf("<aud=", audioIndex)) > -1
                    || (videoIndex = message.indexOf("<mov=", videoIndex)) > -1) {
                isAudio = -1;
                if(index == -1){
                    index = audioIndex;
                    if(index == -1){
                        index = videoIndex;
                        isAudio = 1;
                    } else isAudio = 0;
                }
                sIndex = message.indexOf(";", index);
                if (sIndex > -1) {
                    tIndex = message.indexOf(";", sIndex + 1);
                    if (tIndex > -1) {
                        fIndex = message.indexOf(">", tIndex);
                        if (fIndex > -1) {
                            while (message.charAt(fIndex) == '|') {
                                fIndex = message.indexOf(">", fIndex + 1);
                            }
                            imageText = new String[5];
                            imageText[0] = message.substring(tIndex + 1, fIndex);
                            imageText[1] = message.substring(index + 5, sIndex);
                            imageText[2] = message.substring(sIndex + 1, tIndex);
                            if (imageText[0].length() == 0) {
                                if(isAudio==0){
                                    imageText[0] = "Play Audio";
                                } else if(isAudio == 1){
                                    imageText[0] = "Play Movie";
                                }else {
                                    imageText[0] = "Picture";
                                }
                            }
                            //CR 14491
                            imageText[3] = isAudio+"";
                            viewImage.addElement(imageText);

                            message = message.substring(0, index) + "<!" + imageText[2] + "!>"
                                    + message.substring(fIndex + 1);
                            index += ("<!" + imageText[2] + "!>").length();
                            audioIndex = index;
                            videoIndex = index;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (viewImage.size() == 0) {
                UiGlobalVariables.imagefile = null;
                UiGlobalVariables.extension = null;
            }
        } catch (Exception exception) {
            UiGlobalVariables.imagefile = null;
            Logger.debugOnError("setImageCaptionAndId -> " + exception.toString());
        }
        index = 0;
        sIndex = 0;
        return message;
    }

    /**
     * Method to set the message string. This method internally calls
     * markEmoticImage to mark the emoticons and calls the createPageArray
     * to create pages
     *
     * @param message
     */
    public void setMessage(ChatScriptDto[] chatScriptDtos) {
        if (null != chatScriptDtos && chatScriptDtos.length > 0) {
            createPageArray(chatScriptDtos);
            yStartPosition = totalNumberOfPage;
            selItem = -1;
            if (null != highlightingLine && highlightingLine[0][1] > 0) {
                selItem = highlightingLine.length;
            }
        }
    }

    //bug 13837
    public synchronized boolean updateReceiveMessage(Message messageDto, boolean isNotReceiveMessage) {
        if (null != chatHistory) {
            synchronized (chatHistory) {
                //CR 14423
                ChatScriptDto chatScriptDto = chatHistory.appenedChatScript(messageDto, isNotReceiveMessage, null);
                if (!isNotReceiveMessage) {
                    if (null != chatScriptDto) {
                        appenedNewMessage(null, chatScriptDto);
                    }
                    return true;
                } else {
                    updateStatus(chatScriptDto.getChatSequn(), 1);
                }
            }
        }
        return false;
    }

    //CR 14333
    //cR 14441
    public void changeChatStatus(String chatSequence, String chatId, 
            int status, int type) {
        if(type == 0){
            if(null != chatId && chatId.compareTo(chatHistory.chatId) == 0){
                sHeader = CustomCanvas.getSecondaryHeader(chatSequence, "", 0);
                if(isImageView == -1){ //bug 14655
                    CustomCanvas.sHeader = sHeader;
                    ShortHandCanvas.IsNeedPaint(); //bug 14638
                }
            }
        } else if (chatHistory.updateStatus(chatSequence, chatId, status)) {
            updateStatus(chatSequence, status);
            ShortHandCanvas.IsNeedPaint();
        }
    }

    private void updateStatus(String searchValue, int status) {
        int count = -1;
        if ((count = warpedMessages.size()) > 0) {
            ChatScriptDto chatScriptDto = null;
            int i = 0;
            int index = 0;
            while (i < count && index < searchValue.length()) {
                chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(i);
                byte eleCount = chatScriptDto.getTotoalElements();
                if (chatScriptDto.getDirection() == chatHistory.DIRECTION_OUT
                        && searchValue.indexOf(chatScriptDto.getChatSequn()) > -1) {
                    for (int j = 0; j < eleCount; j++) {
                        chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(i + j);
                        if (chatScriptDto.getStatus() != 10) {
                            chatScriptDto.setStatus((byte) status);
                            //CR 14328
                            if ((j + 1) == eleCount) {
                                chatScriptDto.setScript(chatScriptDto.getScript().substring(0, chatScriptDto.getScript().length() - 1) + status);
                            }
                            warpedMessages.setElementAt(chatScriptDto, i + j);
                        }
                    }
                    index = searchValue.indexOf(ChannelData.CHAT_SEQUENCE_SEPARATOR, index);
                    if (index == -1) {
                        index = searchValue.length();
                    } else {
                        index++;
                    }
                }
                i += eleCount;
            }
            ShortHandCanvas.IsNeedPaint();
        }
    }

    private void appenedNewMessage(String message, ChatScriptDto chatScriptDto) {
        ChatScriptDto[] chatScriptDtos = new ChatScriptDto[1];
        if (null == chatScriptDto) {
            chatScriptDtos[0] = new ChatScriptDto();
            //CR 14330
            chatScriptDtos[0].setScript(message + " (" + Utilities.getHHMM24HrsChatFormat() + ")");
            chatScriptDtos[0].setDirection(chatHistory.DIRECTION_OUT);
            chatScriptDtos[0].setStatus((byte) 0);
            chatScriptDtos[0].setChatSequn(GlobalMemorizeVariable.getChatSequenceNumber());
            chatScriptDtos[0].setDate(Long.parseLong(Utilities.getCurrentDateYYYYMMDDFormat()));
            chatScriptDtos[0].setChatSequence(Long.parseLong(chatScriptDtos[0].getChatSequn()));
        } else {
            chatScriptDtos[0] = chatScriptDto;
        }
        createPageArray(chatScriptDtos);
        yStartPosition = totalNumberOfPage;
        if (null != highlightingLine) {
            selItem = highlightingLine.length - 1;
        }
    }

    private ChatScriptDto cloneScript(String script, ChatScriptDto chatScriptDto, 
            int status) {
        ChatScriptDto chatScriptDto1 = new ChatScriptDto();
        chatScriptDto1.setScript(script);
        chatScriptDto1.setDate(chatScriptDto.getDate());
        chatScriptDto1.setDirection(chatScriptDto.getDirection());
        if (status != -1) {
            chatScriptDto1.setStatus((byte) status);
        } else {
            chatScriptDto1.setStatus(chatScriptDto.getStatus());
        }
        //bug 14557
        if(chatScriptDto.getChatSequence()>-1){
            chatScriptDto1.setChatSequence(chatScriptDto.getChatSequence());
            chatScriptDto1.setChatSequn(chatScriptDto.getChatSequn());
        }

        //CR 14423
        chatScriptDto1.setCurrentRecordPosition(chatScriptDto.getCurrentRecordPosition());
        chatScriptDto1.setFileLocation(chatScriptDto.getFileLocation());
        return chatScriptDto1;
    }

    /* Method to create pages based on the messages. This method creates
     * multiple pages based on the message length. It identifies the linked text
     * available on the each page
     *
     * @param message
     */
    private void createPageArray(ChatScriptDto[] chatScriptDtos) {
        if (null != chatScriptDtos) {
            String controlChannel = "";
            int linePading = (UISettings.formWidth - (15 + 8));
            String lineText = null, tempValue = null;

            int dtoCount = chatScriptDtos.length, index = -1, xPosition = 2;
            String scriptText = null;
            int newLineIndex = -1, padingWidth, stringWidth;

            int oldImageIndex = viewImage.size();
            int startingPosition = warpedMessages.size();
            int currentPosition = 0;
            if(startingPosition>0){
                currentPosition = ((ChatScriptDto)warpedMessages.elementAt(startingPosition-1)).getCurrentRecordPosition();
            }
            String[] imageView = null;
            try {
                //CR 12721
                ChatScriptDto chatScriptDto = null;
                for (int i = 0; i < dtoCount; i++) {
                    //CR 14423
                    chatScriptDtos[i].setCurrentRecordPosition(i+currentPosition);
                    if (startingPosition < warpedMessages.size()) {
                        chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(startingPosition);
                        chatScriptDto.setTotoalElements((byte) (warpedMessages.size() - startingPosition));
                        warpedMessages.setElementAt(chatScriptDto, startingPosition);
                        startingPosition = warpedMessages.size();
                        chatScriptDto = null;
                    }

                    if (chatScriptDtos[i].getDirection() == chatHistory.DIRECTION_IN) {
                        if (chatScriptDtos[i].getStatus() == chatHistory.STATUS_DELIVERIED) {
                            //bug 14557
                            if(chatScriptDtos[i].getChatSequence()>-1){
                                controlChannel += ChannelData.CHAT_SEQUENCE_SEPARATOR + chatScriptDtos[i].getChatSequn();
                            }
                            chatScriptDtos[i].setStatus(chatHistory.STATUS_DISPLAYED);
                        }
                        //CR 14328
                        scriptText = chatScriptDtos[i].getScript().trim();
                    } else {
                        scriptText = chatScriptDtos[i].getScript().trim() + "-" + chatScriptDtos[i].getStatus();
                    }

                    scriptText = Utilities.remove(scriptText, "\r");

                    //CR 14112
                    scriptText = setImageCaptionAndId(scriptText);

                    if (previousDate == -1 || previousDate != chatScriptDtos[i].getDate()) {
                        warpedMessages.addElement(cloneScript(Utilities.convertLongToChatDate(chatScriptDtos[i].getDate()),
                                chatScriptDtos[i], 10));
                        previousDate = chatScriptDtos[i].getDate();
                    }

                    if (oldImageIndex<viewImage.size()) {
                        imageView = (String[]) viewImage.elementAt(oldImageIndex);
                        imageView[4] = warpedMessages.size() + "";
                        viewImage.setElementAt(imageView, oldImageIndex++);
                        index = scriptText.indexOf("<!");
                        newLineIndex = scriptText.indexOf("!>");
                        byte markedLine = 1;
                        lineText = scriptText.substring(newLineIndex + 2);
                        scriptText = scriptText.substring(0, index) + scriptText.substring(index + 2, newLineIndex);
                        if ((font.stringWidth(scriptText + lineText) + xPosition) > linePading) {
                            chatScriptDto = cloneScript(scriptText, chatScriptDtos[i], -1);
                            chatScriptDto.setMarkedLine(markedLine);
                            warpedMessages.addElement(chatScriptDto);
                            markedLine = 0;
                            scriptText = lineText;
                        } else {
                            scriptText += lineText;
                        }

                        chatScriptDtos[i].setScript(scriptText);
                        chatScriptDtos[i].setMarkedLine(markedLine);
                        warpedMessages.addElement(chatScriptDtos[i]);
                    } else {
                        // scriptText= Utilities.replace(scriptText, "\n", " ");
                        scriptText = scriptText.trim();
                        scriptText += "\n";

                        while ((newLineIndex = scriptText.indexOf("\n")) > -1) {
                            lineText = scriptText.substring(0, newLineIndex);
                            scriptText = scriptText.substring(newLineIndex + 1);
                            //.trim();
                            index = -1;
                            while (lineText.length() > 0) {
                                if ((newLineIndex = lineText.indexOf(" ", index + 1)) > -1) {
                                    tempValue = lineText.substring(0, newLineIndex);
                                    if ((font.stringWidth(tempValue) + xPosition) < linePading) {
                                        index = newLineIndex;
                                        continue;
                                    }
                                } else {
                                    newLineIndex = lineText.trim().length();
                                }
                                if (index == -1) {
                                    stringWidth = 0;
                                    index = 0;
                                } else {
                                    tempValue = lineText.substring(0, index + 1);
                                    stringWidth = font.stringWidth(tempValue);
                                }
                                tempValue = lineText.substring(index + 1, newLineIndex);

                                padingWidth = (font.stringWidth(tempValue) + xPosition + stringWidth) - linePading;
                                stringWidth = font.stringWidth(tempValue);
                                if (padingWidth > 0) {
                                    padingWidth = (tempValue.length() * (stringWidth - padingWidth)) / stringWidth;
                                    if (padingWidth >= 8) {
                                        if (tempValue.length() <= 11) {
                                            newLineIndex = (index + tempValue.length() - 6);
                                        } else {
                                            newLineIndex = index + padingWidth;
                                        }
                                    } else {
                                        newLineIndex = index;// + tempValue.length();
                                    }
                                }
                                warpedMessages.addElement(cloneScript(lineText.substring(0, newLineIndex),
                                        chatScriptDtos[i], -1));
                                lineText = lineText.substring(newLineIndex).trim();
                                index = -1;
                            }
                        }
                    }
                }

                if (startingPosition < warpedMessages.size()) {
                    chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(startingPosition);
                    chatScriptDto.setTotoalElements((byte) (warpedMessages.size() - startingPosition));
                    warpedMessages.setElementAt(chatScriptDto, startingPosition);
                    chatScriptDto = null;
                }

                if ((index = viewImage.size()) > 0) {
                    if (null == highlightingLine || highlightingLine.length < index) {
                        highlightingLine = new int[index][2];
                        String[] values = null;
                        for (int i = 0; i < index; i++) {
                            values = (String[]) viewImage.elementAt(i);
                            highlightingLine[i][0] = Integer.parseInt(values[4]);
                            highlightingLine[i][1] = values[2].length() + 2;
                        }
                    }
                } else {
                    highlightingLine = null;
                }

                if (warpedMessages.size() > 0) {
                    numberOfLinePerPage = textAreaHeight / (font.getHeight() + 3);
                    if (numberOfLinePerPage < warpedMessages.size()) {
                        totalNumberOfPage = (warpedMessages.size() - numberOfLinePerPage);
                        totalNumberOfPage *= (-1 * (font.getHeight() + 3));
                        //CR 12817
                        scrollLen = (warpedMessages.size() - numberOfLinePerPage) * (font.getHeight() + 3);
                        scrollLen = ((textAreaHeight + UISettings.secondaryHeaderHeight) / scrollLen);
                        index = CustomCanvas.getScrollHeight(scrollLen);
                        if (index > -1) {
                            scrollLen = (warpedMessages.size() - numberOfLinePerPage) * (font.getHeight() + 3);
                            scrollLen = (((textAreaHeight + UISettings.secondaryHeaderHeight) - index) / scrollLen);
                        }
                    } else {
                        numberOfLinePerPage = warpedMessages.size();
                        scrollLen = -1;
                        totalNumberOfPage = 0;
                    }
                }

                //CR 14333
                if (controlChannel.length() > 0) {
                    //Bug 14329
                    ObjectBuilderFactory.getControlChanel().addChatMessageDisplayed(chatHistory.chatId, controlChannel.substring(1));
                }
            } catch (Exception exception) {
                Logger.debugOnError("CreatePageArray->" + exception.toString());
            }
        }
    }

    /**
     * Method to set the secondary header
     *
     * @param senderName  Sender Name
     * @param queryType   Query Type
     **/
    public void SetsecondaryHeader(String senderName, int count) {
        if (count > -1) {
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(senderName, "160/160", 0);
        } else {
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(senderName, "", 0);
        }
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

        iCaptureImage.deInitialize(false); //CR 14418

        UiGlobalVariables.imagefile = null;
//        selectImagePosition = -1;
        preselectImagePosition = -1;
        previousDate = -1;

        isScrollEnabled = false;

        iCustomPopup.deinitialize();

        iKeyHandler.deinitialize();

        iBannerHandler.deInitialize();

        iMenu.deInitialize();

        chatHistory.deinitialize();

        urlStr = null;

        yStartPosition = 0;

        viewImage.removeAllElements();
        warpedMessages.removeAllElements();
//        dMessages = null;

        //Boolean Array
//        displayColor = null;
//        dColor = null;
        highlightingLine = null;
        selItem = -1;

        textbPos = 0;
        lCount = -1;
        previousY = -1;

        nTextbox = null;

        //byte
        rOByte = -1;
        scrollLen = -1;
        nTextBoxLine = 2;
        isNative = false;
        isNativeTextbox = false;
        isEntryBoxEnabled = false;
        itemFocused = lastitemFocused = 0;

        //int
        yStartPosition = numberOfLinePerPage = totalNumberOfPage = 0;
        textboxSize = 0;
        //bug 13169
        CustomCanvas.deinitialize();
    }

    /**
     * Method to load the ViewSms Canvas
     *
     * @param resDTO
     *          Instance of ViewSmsResponseDTO which contains attributes
     *          to load canvas.
     *
     */
    public void load(ChatResponseDTO resDTO) {
        deInitialize();
        try {
            chatHistory.initialize(resDTO.getAppName(), resDTO.getChatId(),
                    resDTO.getChatName(), resDTO.getAbbervation(), resDTO.getPlusUser());
            iCaptureImage.setChatId(chatHistory.chatId); //CR 14418
//            queryFormat = resDTO.getQueryFormat();
            itemFocused = UISettings.TEXTBOX; //CR 12656
            lastitemFocused = UISettings.TEXTBOX; //CR 12656
            textAreaHeight = UISettings.formHeight - (UISettings.headerHeight + UISettings.secondaryHeaderHeight + UISettings.footerHeight);
            numberOfItem = 0;

            isNativeTextbox = true; //CR 12656

            lCount = resDTO.getLetterCount();
            isEntryBoxEnabled = true;
            textboxSize = UISettings.headerHeight; //CR 12541
            textbPos = (short) (UISettings.formHeight - (UISettings.footerHeight + textboxSize));
            itemFocused = UISettings.TEXTBOX;
            lastitemFocused = UISettings.TEXTBOX;
            setTextboxConstraints();
            textAreaHeight -= textboxSize;
            iKeyHandler.startTextCursorBlinkTimer();

//            lCount = resDTO.getLetterCount();
            //Bug no 12707
            sHeader = resDTO.getBuddyName();
            //sHeader = resDTO.getScreenName();
            SetsecondaryHeader(sHeader, -1);
            rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
            UISettings.lOByte = PresenterDTO.setLOptByte();
            iBannerHandler.setBanner(resDTO.getBannerText(), resDTO.getBannerStyle(), rOByte, true);
            if (null != resDTO.getBannerText()) {
                textbPos -= UISettings.itemHeight;
                textAreaHeight -= UISettings.itemHeight;
            }
            setMessage(chatHistory.getHistoryScriptsDto());
        } catch (Exception e) {
            Logger.loggerError("ChatCanvas load " + e.toString() + e.getMessage());
        }
        reLoadFooterMenu();
        ShortHandCanvas.IsNeedPaint();
    }

    //CR 14111
    private boolean loadMenu() {
        String[] names = Utilities.getImageGalleryNames();
        if(null != names){
            byte numOfMenuItems = (byte) (UISettings.numOfMenuItems - 1);
            if (null != iBannerHandler.getBannerText()) {
                numOfMenuItems--;
            }
            iMenu.setMenu(null, Utilities.getImageGalleryPath(), names, null, null, true, null, 0);
            iMenu.setMenuPosition((short) (UISettings.headerHeight + UISettings.secondaryHeaderHeight),
                    numOfMenuItems, (byte) -1, true);
            isImageView = 0;
            itemFocused = UISettings.MENU;
            lastitemFocused = UISettings.MENU;
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.options[53], "", 0);
            reLoadFooterMenu();
            return false;
        }
        return true;
     
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
        //#if KEYPAD
        //|JG|        iKeyHandler.keyConformed();
        //#endif

        if (isSend = iKeyHandler.isAlphaMaxCheck(lco) && iKeyHandler.getEntryTempText().length() > 0) {
            String temp = Utilities.replace(iKeyHandler.getEntryTempText(), "[", "(");
            temp = Utilities.replace(temp, "]", ")");

            // bug 13837      chatHistory.updateHistory(null, iKeyHandler.getEntryTempText());
            appenedNewMessage(Constants.appendText[28] + ": " + iKeyHandler.getEntryTempText(), null);

            ObjectBuilderFactory.GetKernel().handleItemSelection(0, temp);////bug 4396, CR 14327
        }


        iKeyHandler.copyTextToTextBox("");
        return isSend;
    }

    private void setTextboxConstraints() {
        iKeyHandler.SetItemFocused(UISettings.TEXTBOX);
        iKeyHandler.setEntryProperty(minChar, maxChar, minValue, maxValue, null, lCount, IKeyHandler.ALPHANUMERIC, isNativeTextbox, true);
    }

//    private void setMenuConstraints() {
//        iKeyHandler.SetItemFocused(UISettings.SEARCH);
//        iKeyHandler.setEntryProperty((short) 0, Short.MAX_VALUE, 0, Float.MAX_VALUE, null, -1, IKeyHandler.ALPHANUMERIC, false, true);
//    }
//    private int[] getStyle(String[] iName){
//        int[] style = null;
//        if(null != iName){
//            int len = iName.length;
//            style = new int[len];
//            for(int i=0;i<len;i++)
//                style[i] = 0;
//        }
//        return style;
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
//        itemFocused = UISettings.POPUPSCREN;
//        iCustomPopup.showNotification(isGoTo);
//    }
    /**
     * Method to unload the view
     */
    public void unLoad() {
        deInitialize();
        chatHistory = null;
        iKeyHandler = null;
        iCaptureImage = null;
        iCustomPopup = null;
        imageDisplay = null;
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if (isReLoad) {
            enablePreviousSelection();
        }
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType, boolean isReload) {
        if (isReload) {
            enablePreviousSelection();
        }
        if (null == urlStr) {
            ObjectBuilderFactory.GetKernel().handleMessageBox(isSend, msgType);
        } else {
            loadData(isSend);
        }
    }

    public void enablePreviousSelection() {
        this.itemFocused = lastitemFocused;
        reLoadFooterMenu();
    }

    public int getSmartPopupyPos(int keyCode) {
        return ((UISettings.formHeight / 2) - (UISettings.popupHeight / 2));
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
        if (itemFocuse == UISettings.OPTIONS) {
            itemFocused = UISettings.POPUPSCREN;
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
        }
    }

    public void sendSelectedValue(int id, String value) {
        //CR 14423
        if(imageDisplay.setHeadetText(iMenu.getSelectedDisplayMenuValue(), chatHistory.chatId, value,
                (byte)1)){
            isImageView = 2;
            reLoadFooterMenu();
        }
    }

    public void handleSmartPopup(int type) {
    }

    public void showDateForm() {
    }

    /**
     *
     * @param maxChar
     * @param type
     * @param isMask
     */
    public void showNativeTextbox(int maxChar, byte type, boolean isMask) {
//        display = null;

        if (type == IKeyHandler.ALPHA || type == IKeyHandler.ALPHANUMERIC) {
            nTextbox = new TextBox(CustomCanvas.sHeader, iKeyHandler.getEntryText(), maxChar, TextField.ANY);
            //nTextbox.setConstraints(TextField.ANY);
        }
        if (UISettings.isCenterOkOption) { //bug id 3619
            nTextbox.addCommand(new Command(Constants.options[7], Command.OK, 0));
            nTextbox.addCommand(new Command(Constants.options[22], Command.EXIT, 1));
        } else {
            nTextbox.addCommand(new Command(Constants.options[22], Command.EXIT, 0));
            nTextbox.addCommand(new Command(Constants.options[7], Command.OK, 1));
        }

        if (UISettings.HASTHIRDSOFTKEY) { //bug id 3619
            nTextbox.addCommand(new Command(Constants.options[42], Command.ITEM, 2));
        }
        nTextbox.setCommandListener(ObjectBuilderFactory.getPCanvas());
        Display.getDisplay(ObjectBuilderFactory.GetProgram()).setCurrent(nTextbox);
        reLoadFooterMenu();
    }

    public void rotateScreen(boolean isLandScape) {
        short mPos = (short) (UISettings.formHeight - UISettings.footerHeight);
        textAreaHeight = UISettings.formHeight - (UISettings.headerHeight + UISettings.secondaryHeaderHeight + UISettings.footerHeight);

        if (isEntryBoxEnabled) {
            textbPos = (short) (UISettings.formHeight - (UISettings.footerHeight + textboxSize));
            mPos -= textboxSize;
            textAreaHeight -= textboxSize;
        }

        if (null != iBannerHandler.getBannerText()) {
            textbPos -= UISettings.itemHeight;
            mPos -= UISettings.itemHeight;
            textAreaHeight -= UISettings.itemHeight;
        }

        SetsecondaryHeader(sHeader, -1);
        iMenu.rotateMenu(mPos, numberOfItem);

        warpedMessages.removeAllElements();
        viewImage.removeAllElements();
        previousDate = -1;

        createPageArray(chatHistory.getHistoryScriptsDto());

        if (selItem > -1) {
            yStartPosition = (-1 * highlightingLine[selItem][0]) * (font.getHeight() + 3);
        }
        if (yStartPosition < totalNumberOfPage) {
            yStartPosition = totalNumberOfPage;
        }


        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "", 0);

        iCustomPopup.rotatePopup();
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload, boolean isSet) {
        if (isReload) {
            enablePreviousSelection();
        }
        //#if KEYPAD
        //|JG|        if (isSet) {
        //|JG|            iKeyHandler.appendCharacter(itemFocused, selSymbol);
        //|JG|        }
        //#endif
    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

    //CR 14423
    private void updateImageLocation(String fileLocation){
        ChatScriptDto chatScriptDto = (ChatScriptDto) warpedMessages.elementAt(highlightingLine[selItem][0]);
        chatScriptDto.setFileLocation(fileLocation);
        warpedMessages.setElementAt(chatScriptDto, highlightingLine[selItem][0]);
        int currentPosition = chatScriptDto.getCurrentRecordPosition();

        chatScriptDto = (ChatScriptDto)warpedMessages.elementAt(warpedMessages.size()-1);
        int lastPosition = chatScriptDto.getCurrentRecordPosition();
        
        chatHistory.updateFileLocation(currentPosition+1,lastPosition+1,fileLocation);
    }

    public void setImage(ByteArrayOutputStream byteArrayOutputStream) {
        if (isImageView == 1) {
            if (null != byteArrayOutputStream) {
                if (selItem > -1) {
                    //CR 14423
                    String fileLocation = imageDisplay.setImage(byteArrayOutputStream, true);
                    if (null != fileLocation) {
                        updateImageLocation(fileLocation);
                        reLoadFooterMenu();
                        if(fileLocation.length() == 0){
                            CustomCanvas.setMessageBoxText(Constants.popupMessage[69], "Alert");
                            CustomCanvas.msgType = (byte)4;
                        }
                    }
                    ShortHandCanvas.IsNeedPaint();
                }
            } else {
                itemFocused = UISettings.VIEW;
                lastitemFocused = UISettings.VIEW;
                isImageView = -1;
                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
            }
        } else if (isImageView == 2 || iCaptureImage.isCurrentScreen()) { //CR 14418
            //CR 14111
            isImageView = -1;
            
            itemFocused = UISettings.VIEW;
            lastitemFocused = UISettings.VIEW;
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "", 0);

            if (null != byteArrayOutputStream && byteArrayOutputStream.size() > 0) {
                String receiveTag = new String(byteArrayOutputStream.toByteArray()).trim();
                String fileLocation = null;

                //bug 14732
                Message message = new Message();
                message.setChatSequence(GlobalMemorizeVariable.getChatSequenceNumber());
                GlobalMemorizeVariable.updateChatCharacterSequence(1);
                message.setChatId(chatHistory.chatId);
                message.setChatDate(Utilities.getCurrentDateYYYYMMDDFormat());
                //CR 14330
                message.setCurRMsg(new String(byteArrayOutputStream.toByteArray()).trim());
                ChatScriptDto chatScriptDto = chatHistory.appenedChatScript(message, true, fileLocation);
                appenedNewMessage(null, chatScriptDto);

                //CR 14423
                if(iCaptureImage.isCurrentScreen()){
                    int index = receiveTag.indexOf("<img=");
                    //CR 14491
                    if(index == -1){
                        index = receiveTag.indexOf("<aud=");
                        if(index == -1){
                            index = receiveTag.indexOf("<mov=");
                        }
                    }
                    fileLocation = iCaptureImage.SaveImage_Audio(receiveTag.substring(index+5,receiveTag.indexOf(";")));
                    iCaptureImage.deInitialize(false);
                    if(null == fileLocation){
                        CustomCanvas.setMessageBoxText(Constants.popupMessage[69], "Alert");
                        CustomCanvas.msgType = (byte)4;
                    }
                } else {
                    fileLocation = imageDisplay.getFileLocation();
                    iMenu.deInitialize();
                }
                //bug 14732
                Logger.loggerError("Saved Image File Location "+fileLocation);
                if(null != fileLocation){
                    updateImageLocation(fileLocation);
                }
                
            }
            
            reLoadFooterMenu();
            ShortHandCanvas.IsNeedPaint();
        }
    }
//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }
}
