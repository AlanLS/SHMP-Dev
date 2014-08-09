import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


/**
 * Profile Canvas class for the profile presenter screen
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) ShartHand Mobile Inc
 */
public class ProfileCanvas implements IProfilePresenter,ICanvasHandler ,IPopupHandler
{
    //Item Name array
    private String[] itemName = null;
    
    //Logo Image array
    private Image[] logoimg = null;
    
    private String[] msgUnReadCount = null;

    //Item focused
    private byte itemFocused = 0;
    
    //Left option text
    private byte rOByte, rightArrow = 0, leftArrow = 0;
    
    float yStartPosition = 0;
    
    private boolean isPaint = false;

    //#if KEYPAD
    //|JG|    private IKeyHandler iKeyHandler = null;
    //|JG|//    private int[] debugOn = new int[]{51,50,56,52,51,54};
    //#endif
    
    private ICustomPopup iCustomPopup = null;
    
    private float scrollLineLength = -1;
    
    private String msgCount = null;

    private short numberofRow, columnCount, rowCount = 0, selectedIndex;

    private byte gridHighlightGap = 15;

    private int previousY = -1;

    private int[] unreadChatCount = null;

    private int totalNavigationSize = 0;

    private boolean isScrollSelected = false;

    //private boolean isSelectedItem = false;

    /**
     * Constructor method to initialize the profile canvas
     */
    public ProfileCanvas() {
        setGridDisplayCount(false);
        //#if KEYPAD
        //|JG|        iKeyHandler = ObjectBuilderFactory.getKeyHandler();
        //|JG|        iKeyHandler.setCanvasHandler(this);
        //#endif
        iCustomPopup = new CustomPopup(this);
    }

    private void setGridDisplayCount(boolean isScroll){
        int width = UISettings.formWidth;
        if(isScroll)
            width -= UISettings.POPUP_SCROLL_WIDTH;
        if(UISettings.isTocuhScreenNativeTextbox && !UISettings.GENERIC){ //CR 10336            
            gridHighlightGap = 15;
            setGridWidth();
            columnCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
           if((columnCount*((UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap))+UISettings.GRID_IMAGE_HEIGHT)<width)
               columnCount++;
            width = (short)(UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight));
           rowCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
           if(width>=((UISettings.GRID_IMAGE_HEIGHT*(rowCount+1)+(gridHighlightGap*rowCount)))){
               rowCount+=1;
           } 
        } else {
            gridHighlightGap = 10;
            columnCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+6));
            
            if((columnCount *(UISettings.GRID_IMAGE_HEIGHT+6))>width){
                columnCount--;               
            }
            int gap = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
            rowCount = (short)((UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight)) / (UISettings.GRID_IMAGE_HEIGHT+6));
            int gaptow = (short)((UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight)) / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));

            if(gaptow<rowCount){
                //if(gap<columnCount){
                    gridHighlightGap = 6;
                //}
            }
            if(gap<columnCount){
                gridHighlightGap = 6;
            }
        }
    }

    private void setGridWidth(){
        if(UISettings.formWidth<=240)
            gridHighlightGap = 10;
//        else if(UISettings.formWidth>200 && UISettings.formWidth<=240)
//            gridHighlightGap = 20;
        else if(UISettings.formWidth>240 && UISettings.formWidth<=300)
           gridHighlightGap = 20;
        else if(UISettings.formWidth>300 && UISettings.formWidth<=360)
             gridHighlightGap = 30;
        else if(UISettings.formWidth>360)
            gridHighlightGap = 40;
    }

    /**
     * Method to paint profile canvas
     * 
     * @param g  Instance of Graphics class
     */
    public void paintGameView(Graphics g) {

        if(iCustomPopup.isCustomPopupState()){
            isPaint = true;
            itemFocused = UISettings.POPUPSCREN;
        }

        if(isPaint){

            //#if KEYPAD
            //|JG|            iKeyHandler.updateKeyTimer();
            //|JG|
            //|JG|            iKeyHandler.updateSearchTimer();
            //#endif
            
            clearScreen(g);

            if(!Settings.isIsGrid()){ //CR 9359
                CustomCanvas.drawBackgroundImage(g);
            }

            drawMenu(g);
            
            CustomCanvas.drawHeader(g);
        
            if (iCustomPopup.isMessageFocused()) {
                CustomCanvas.DrawOptionsMenu("", (byte)-1, (byte) -1, g);
            } else if(itemFocused == UISettings.POPUPSCREN) { // CR number 6755
                CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
            }else{
                CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
            }
            iCustomPopup.drawScreen(g);
        }
        
    }

    /**
     * Method to clear the view
     * 
     * @param g Instance of Graphics class
     */
    private void clearScreen(Graphics g) {
        if(Settings.isIsGrid()){ //CR 9359
            g.setColor(0);
        } else {
            g.setColor(0x21519c);
        }
        g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
    }
    
    public void showNativeTextbox(int maxChar,byte type,boolean isMask){
        
    }
    
    public void handleSmartPopup(int poptype){
        iCustomPopup.handleSmartPopup(poptype);
    }
    
    private void setScrollLen(){
        scrollLineLength =-1;
        if(null != itemName){
            //CR 12817
            if(totalNavigationSize<0){
                scrollLineLength = (UISettings.formHeight - (2*UISettings.itemHeight))/(float)(-1*totalNavigationSize);
                int len = CustomCanvas.getScrollHeight(scrollLineLength);
                if(len>-1){
                    scrollLineLength =((UISettings.formHeight - ((2*UISettings.itemHeight)+len)) /(float) (-1*totalNavigationSize));
                }
            }
        }
    }

    /**
     * Method to draw profile menu
     * 
     * @param g  Instance of graphics class
     */
    private void drawMenu(Graphics g) {
        if (null != itemName) {
            if(Settings.isIsGrid()){
                displyGridView(g);
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(itemName[selectedIndex], "",0);
                CustomCanvas.drawSecondaryHeader("", g, true,false);

                if(scrollLineLength>0 && !iCustomPopup.isMessageFocused()){
                    //CR 12817
                    CustomCanvas.drawScroll(g, scrollLineLength, UISettings.headerHeight,
                            (-1*yStartPosition*scrollLineLength), 
                            (UISettings.formHeight - (2*UISettings.itemHeight)),
                            UISettings.formWidth);
                }
            } else {
                displayListView(g);
                if(scrollLineLength>0 && !iCustomPopup.isMessageFocused()){
                    CustomCanvas.drawScroll(g, scrollLineLength, UISettings.headerHeight,
                            (-1*yStartPosition*scrollLineLength), 
                            (UISettings.formHeight - (2*UISettings.itemHeight))
                            ,UISettings.formWidth);
                }
            }
        } 
    }

    private void displayListView(Graphics g){
            int pages = 0;
            if(yStartPosition != 0){
                pages = (int)(-1*yStartPosition)/UISettings.itemHeight;
            }
            int startIndex = pages;

            int nLine = numberofRow + pages;
            if(itemName.length>nLine){
                nLine++;
                if(itemName.length>nLine)
                    nLine++;
            }
            //if(yStartPosition != 0)
            pages = (int)(yStartPosition%UISettings.itemHeight);

            int fWidth = (UISettings.itemHeight - g.getFont().getHeight()) / 2;
            int y = UISettings.headerHeight + pages;
            CustomCanvas.drawSelection(y, selectedIndex, g);
            for (int i = startIndex, x=3; i < nLine; i++, y+=UISettings.itemHeight,x=3) {
                //pos = UISettings.headerHeight + (y * UISettings.itemHeight);
                if (null != logoimg[i]) {
                    x = logoimg[i].getWidth();
                    if (UISettings.itemHeight >= logoimg[i].getHeight()) {
                        g.drawImage(logoimg[i], 0, y + ((UISettings.itemHeight - logoimg[i].getHeight()) / 2), Graphics.TOP | Graphics.LEFT);
                    } else {
                        g.drawRegion(logoimg[i], 0, 0, x, UISettings.itemHeight, Sprite.TRANS_NONE,
                                0, y, Graphics.TOP | Graphics.LEFT);
                    }
                    x += 2;
                } else if(i != 0){
                    x = CustomCanvas.drawSHIcon(g, y) +  2;
                }
                g.setColor(0xffffff);
                g.drawString(itemName[i], x, y + fWidth, Graphics.TOP | Graphics.LEFT);
                //#if KEYPAD
                //|JG|                if (i == startIndex + selectedIndex && itemName[i].toLowerCase().startsWith(iKeyHandler.getSearchTempText())) {
                //|JG|                    g.setColor(0x000000);
                //|JG|                    g.drawString(itemName[i].substring(0, iKeyHandler.getSearchTempText().length()), x,
                //|JG|                            y + fWidth, Graphics.TOP | Graphics.LEFT);
                //|JG|                }
                //#endif
            }
    }

    private void displyGridView(Graphics g){

        int pages = 0;
        if(yStartPosition != 0)
            pages = (int)(-1*yStartPosition)/getGridGapWithHeight();

        //CR 12817
        short nLine = (short)((numberofRow+pages)*columnCount);

        if(itemName.length>(nLine+columnCount)){
            nLine += columnCount;
            if(itemName.length>(nLine+columnCount)){
                nLine += columnCount;
            } else nLine = (short)itemName.length;
        } else nLine = (short)itemName.length;

        int startX = ((UISettings.formWidth-(((columnCount-1)*getGridGapWithHeight())+ UISettings.GRID_IMAGE_HEIGHT))/2);

        int imageHeight,imageWidth = 0 ,height = 0, width = 0;

        int y = UISettings.headerHeight+UISettings.secondaryHeaderHeight+
                (int)(yStartPosition%getGridGapWithHeight())+5;


        for (int i = (pages*columnCount), k=0, x = startX; i < nLine; i++, x+=getGridGapWithHeight(),k++) {
            if(k !=0 && (k%columnCount) == 0){
                y+= getGridGapWithHeight();
                x = startX;
            }
            
            if(i == selectedIndex){
                drawGridSelection(g,x,y, false);
            }
            imageHeight = 0;
            imageWidth = 0;
            height = 0;
            width = 0;
            if(null != logoimg[i]){
                width = logoimg[i].getWidth();
                if(logoimg[i].getWidth()<UISettings.GRID_IMAGE_HEIGHT){
                    imageWidth = (UISettings.GRID_IMAGE_HEIGHT - logoimg[i].getWidth())/2;
                }
                height = logoimg[i].getHeight();
                if(logoimg[i].getHeight()<UISettings.GRID_IMAGE_HEIGHT){
                    imageHeight = (UISettings.GRID_IMAGE_HEIGHT - logoimg[i].getHeight())/2;
                }
                g.drawImage(logoimg[i], x+imageWidth, y+imageHeight, Graphics.TOP | Graphics.LEFT);
            } else {
                CustomCanvas.drawSHIcon(g, x, y);
            }
            
            if(i == selectedIndex){
                drawGridSelection(g,x,y, true);
                //g.setColor(0x8cc63f);
                //g.fillRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+4, UISettings.GRID_IMAGE_HEIGHT+4);
            }

            //CR 12319
            if(null != unreadChatCount)
                CustomCanvas.drawChatUnreadNotificationImage(g, x+imageWidth, y,unreadChatCount[i],width,height);

        }
    }

    private void drawGridSelection(Graphics g, int x, int y,boolean isAfterImage){
        byte arc = 17;
        g.setColor(0x8cc63f);
        g.setStrokeStyle(Graphics.SOLID);
        if(isAfterImage){
            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc,arc);
            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-1,arc-1);
            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-2,arc-2);

            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc,arc);
            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-1,arc-1);
            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-2,arc-2);

            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc,arc);
            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc-1,arc-1);
            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc-2,arc-2);

            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-1,arc-1);
            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-2,arc-2);
            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-3,arc-3);

            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc,arc);
            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-1,arc-1);
            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-2,arc-2);
            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-3,arc-3);
            
        } else g.fillRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+3, UISettings.GRID_IMAGE_HEIGHT+3,arc,arc);
    }

    /**
     * Method to handle key pressed event based on the item focused.
     */
    public void keyPressed(int keyCode) {
        if(isPaint){
            if (itemFocused == UISettings.MENU) {
                handleMenu(keyCode);
            } else if(itemFocused == UISettings.POPUPSCREN){
                iCustomPopup.keyPressed(keyCode);
            }
        }
    }
    
    /**
     * 
     * @param xPosition
     * @param yPosition
     */
    public boolean pointerPressed(int xPosition, int yPosition, 
            boolean isNotDrag, boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;

        if(itemFocused == UISettings.MENU){
                if(yPosition<=UISettings.headerHeight){
                    //Bug 14366
					if(previousY>=UISettings.headerHeight || previousY == -1){
						leftArrow = 0;
					}
					if(isNotDrag){
						previousY = yPosition;
						leftArrow++;
						if(leftArrow == 6){
							if(Settings.getIsDebug()){
								Settings.setIsDebug(false);
							} else Settings.setIsDebug(true);
							leftArrow = 0;
						}
					}
                }else if(yPosition>UISettings.headerHeight){
                    if(yPosition > (UISettings.formHeight-UISettings.footerHeight)){
                        previousY = yPosition;
                        if(isNotDrag){
                            if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
                                keyPressed(UISettings.RIGHTOPTION);
                            } else if(UISettings.lOByte>-1 && xPosition<=(UISettings.formWidth/2)){
                                keyPressed(UISettings.LEFTOPTION);
                            }
                        }
                    } else if(null != itemName){
                        if(Settings.isIsGrid()){
                            isNeedSelect = pointerGridView(xPosition, yPosition, 
                                    isNotDrag, isDragEnd, isPressed);
                        } else {
                            isNeedSelect = pointerListView(xPosition, yPosition, 
                                    isNotDrag, isDragEnd, isPressed);
                        }
                    }
                }
        } else if(itemFocused == UISettings.POPUPSCREN) {
            isNeedSelect = iCustomPopup.pointerPressed(xPosition, yPosition, 
                    isNotDrag,isDragEnd, isPressed);
        }

        if(isDragEnd)
            isScrollSelected = false;

        return isNeedSelect;
    }

    private boolean pointerGridView(int xPosition, int yPosition, 
        boolean isNotDrag, boolean isDragEnd, boolean isPressed){
        yPosition -= UISettings.headerHeight;//Hema
        if(isNotDrag || isPressed){
            previousY = yPosition;

            if(isPressed &&
                CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition
                    && ((-1*yStartPosition*scrollLineLength)) <= yPosition
                    && ((-1*yStartPosition*scrollLineLength)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
                    isScrollSelected = true;
            } else if( yPosition>UISettings.secondaryHeaderHeight){
                yPosition -= UISettings.secondaryHeaderHeight;
                yPosition += (-1*yStartPosition) + 5;
                yPosition = (int)(yPosition/getGridGapWithHeight());
                yPosition *= columnCount;

                int startX = ((UISettings.formWidth-(((columnCount-1)*getGridGapWithHeight())+ UISettings.GRID_IMAGE_HEIGHT))/2);
                if(xPosition<startX || xPosition>(UISettings.formWidth-startX))
                    return false;
                xPosition -= startX;
                if(xPosition<0)
                    xPosition = 0;
                else xPosition = (int)(xPosition/getGridGapWithHeight());
              
                yPosition += xPosition;

                //13040
                if(isNotDrag && yPosition != selectedIndex){
                    isNotDrag = false;
                } else if(!isNotDrag && yPosition<itemName.length)
                    selectedIndex = (short)yPosition;
            } else isNotDrag = false;
        } else if(scrollLineLength>-1){
            //CR 13033
            CustomCanvas.showScroll(isDragEnd);

            if(isScrollSelected){
                float position = (totalNavigationSize/(totalNavigationSize*scrollLineLength));
                if((yPosition-previousY) != 0 && position>0)
                    yStartPosition -= (yPosition-previousY)*position;
            } else {
                if(yPosition>UISettings.secondaryHeaderHeight){
                    //CR 13032
                    yStartPosition += (yPosition-previousY);
                } else return isNotDrag;
            }

            if(yStartPosition>0)
                yStartPosition = 0;
            else{
                if(yStartPosition<totalNavigationSize){
                    yStartPosition = totalNavigationSize;
                } else{
                    previousY = selectedIndex/columnCount;
                    if(selectedIndex%columnCount>0)
                        previousY++;
                    previousY *= (-1)*getGridGapWithHeight();
                    if((yStartPosition-getGridGapWithHeight())<previousY){
                        selectedIndex += columnCount;
                        if(selectedIndex>=itemName.length)
                            selectedIndex = (short)(itemName.length-1);
                    } else if((yStartPosition-(numberofRow*getGridGapWithHeight()))>previousY){
                        selectedIndex -= columnCount;
                    }
                }
            }
            previousY = yPosition;
        }
        return isNotDrag;
    }

    private boolean pointerListView(int xPosition, int yPosition, 
            boolean isNotDrag, boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;
        yPosition -= UISettings.headerHeight;
        if(isNotDrag || isPressed){
            previousY = yPosition;
            if(isPressed && CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition &&
                    ((-1*yStartPosition*scrollLineLength)) <= yPosition
                    && ((-1*yStartPosition*scrollLineLength)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
                isScrollSelected = true;
            } else if(isScrollSelected){
                isNotDrag = false;
            } else if(yPosition>0){
                yPosition += (-1)*(yStartPosition%UISettings.itemHeight);
                xPosition = yPosition/UISettings.itemHeight;
                if(xPosition<itemName.length){
                    //CR 13040
                    if(isNotDrag) {
                        isNeedSelect = true;
                    } else if(!isNotDrag) {
                        if(xPosition>=numberofRow){
                            selectedIndex = (short)(numberofRow-1);
                            yStartPosition -= UISettings.itemHeight;
                            if(yStartPosition<totalNavigationSize)
                                yStartPosition = totalNavigationSize;
                        } else {
                            selectedIndex = (short)xPosition;
                        }
                    }
                }
            }
        } else if(scrollLineLength>0) {
            //CR 13033
            CustomCanvas.showScroll(isDragEnd);
            if(isScrollSelected){
                float position = (totalNavigationSize/(totalNavigationSize*scrollLineLength));
                if((yPosition-previousY) != 0 && position>0)
                    yStartPosition -= (yPosition-previousY)*position;
            }  else {
                //CR 13032
                if(yPosition>UISettings.secondaryHeaderHeight)
                    yStartPosition += (yPosition-previousY);
                else return isNeedSelect;
            }

            if(yStartPosition>0)
                yStartPosition = 0;
            else if(yStartPosition<totalNavigationSize){
                yStartPosition = totalNavigationSize;
            }
            previousY = yPosition;
        }
        return isNeedSelect;
    }

    /**
     * 
     * @return
     */
  //#if KEYPAD
  //|JG|        private void search(){
  //|JG|            if(null != itemName){
  //|JG|                int len = itemName.length;
  //|JG|                byte sInd = -1;
  //|JG|                boolean isReset = true;
  //|JG|
  //|JG|                int index = 0;
  //|JG|                if(Settings.isIsGrid()){
  //|JG|                    index = selectedIndex+1;
  //|JG|                } else {
  //|JG|                    if(yStartPosition != 0)
  //|JG|                        index = (int)(-1*yStartPosition)/UISettings.itemHeight;
  //|JG|                    index += selectedIndex+1;
  //|JG|                }
  //|JG|
  //|JG|                for(int i=index;i<len;i++){
  //|JG|                    if(itemName[i].toLowerCase().startsWith(iKeyHandler.getSearchTempText())){
  //|JG|                        sInd =(byte)i;
  //|JG|                        isReset = false;
  //|JG|                        break;
  //|JG|                    } else if(itemName[i].toLowerCase().startsWith(iKeyHandler.getSearchText()))
  //|JG|                        isReset = false;
  //|JG|                }
  //|JG|
  //|JG|                if(sInd == -1){
  //|JG|                    len = index;
  //|JG|                    for(int i=0;i<len;i++){
  //|JG|                         if(itemName[i].toLowerCase().startsWith(iKeyHandler.getSearchTempText())){
  //|JG|                            sInd =(byte)i;
  //|JG|                            isReset = false;
  //|JG|                            break;
  //|JG|                         } else if(itemName[i].toLowerCase().startsWith(iKeyHandler.getSearchText()))
  //|JG|                            isReset = false;
  //|JG|                    }
  //|JG|                }
  //|JG|
  //|JG|                if(sInd>-1){
  //|JG|                    if(Settings.isIsGrid()){
  //|JG|                        //Bug No 12413
  //|JG|                        //len = (sInd/columnCount);
  //|JG|                        len = numberofRow * columnCount;
  //|JG|
  //|JG|                        if(sInd>len){
  //|JG|                            yStartPosition = (int)(((sInd/columnCount)-numberofRow)* getGridGapWithHeight());
  //|JG|                        }
  //|JG|                        selectedIndex = sInd;
  //|JG|                    } else {
  //|JG|                        if(sInd>numberofRow){
  //|JG|                            yStartPosition = (sInd-numberofRow)*UISettings.itemHeight*-1;
  //|JG|                        }
  //|JG|                        selectedIndex = (short)(numberofRow-1);
  //|JG|                    }
  //|JG|                } else if("debugon".compareTo(iKeyHandler.getSearchTempText()) == 0 ||
  //|JG|                        "dbugen".compareTo(iKeyHandler.getSearchTempText()) == 0){
  //|JG|                    Settings.setIsDebug(true);
  //|JG|                    SoundManager.getInstance().playSound();
  //|JG|                    iKeyHandler.SearchValueReset();
  //|JG|                } else if("debugoff".compareTo(iKeyHandler.getSearchTempText()) == 0 ||
  //|JG|                        "dbugof".compareTo(iKeyHandler.getSearchTempText()) == 0) {
  //|JG|                    Settings.setIsDebug(false);
  //|JG|                    SoundManager.getInstance().playSound();
  //|JG|                    iKeyHandler.SearchValueReset();
  //|JG|                }  else if("debugon".startsWith(iKeyHandler.getSearchText()) || "debugoff".startsWith(iKeyHandler.getSearchText()) ||
  //|JG|                  "dbugen".startsWith(iKeyHandler.getSearchText()) || "dbugof".startsWith(iKeyHandler.getSearchText())){
  //|JG|                      isReset = false;
  //|JG|                }
  //|JG|                if(isReset)
  //|JG|                    iKeyHandler.SearchValueReset();
  //|JG|            }
  //|JG|        }
//#endif

        private int getGridGapWithHeight(){
            //#if ROLTECH
            //|JG|  return (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap/2);
                //#else
                return (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap);
                //#endif
        }
    /**
     * Method to handle the keypressed event in the menu
     * 
     * @param option key code
     **/
    private void handleMenu(int keyCode) {
        if (UISettings.DOWNKEY == keyCode) {
            if(null != itemName){
                if(Settings.isIsGrid()){
                    int pages = (int)((-1*yStartPosition)/getGridGapWithHeight())*columnCount;
                    if(pages<=selectedIndex && (selectedIndex+columnCount)<pages+(numberofRow*columnCount)){
                        selectedIndex += columnCount;
                        if(itemName.length<=selectedIndex)
                            selectedIndex = (short)(itemName.length-1);
                    } else if(yStartPosition>totalNavigationSize){
                        if(yStartPosition%getGridGapWithHeight()<0)
                            yStartPosition -= (getGridGapWithHeight() + yStartPosition%getGridGapWithHeight());
                        else yStartPosition -= getGridGapWithHeight();
                        selectedIndex += columnCount;
                        if(itemName.length<=selectedIndex)
                            selectedIndex = (short)(itemName.length-1);
                    } else {
                        //CR 13030
                        selectedIndex = (short)(selectedIndex%columnCount);
                        yStartPosition = 0;
                    }
                } else {
                    if((selectedIndex+1)<numberofRow){
                        selectedIndex++;
                        if((selectedIndex+1) == numberofRow && yStartPosition%UISettings.itemHeight<0){
                            yStartPosition += (yStartPosition%UISettings.itemHeight);
                        }
                    } else if((yStartPosition-UISettings.itemHeight)>=totalNavigationSize){
                        yStartPosition -= UISettings.itemHeight;
                    } else{
                        yStartPosition = 0;
                        selectedIndex = 0;
                    }
                }
            }
        } else if (UISettings.UPKEY == keyCode) {
            if(null != itemName){
                if(Settings.isIsGrid()){
                    int pages = -1*(selectedIndex/columnCount)*getGridGapWithHeight();
                    if(yStartPosition>pages && (selectedIndex-columnCount)>=0){
                        selectedIndex -= columnCount;
                        if((pages+getGridGapWithHeight())>yStartPosition && yStartPosition%getGridGapWithHeight()<0){
                            yStartPosition -= yStartPosition%getGridGapWithHeight();
                        }
                    } else if(yStartPosition<0){
                        yStartPosition += getGridGapWithHeight();
                        selectedIndex -= columnCount;
                    } else {
                        //CR 13030
                        selectedIndex++;
                        if(itemName.length%columnCount<=selectedIndex)
                            selectedIndex = (short)(itemName.length-1);
                        else selectedIndex = (short)((itemName.length-1)-selectedIndex);
                        yStartPosition = totalNavigationSize;
                    }
                } else {
                    if(selectedIndex>0){
                        selectedIndex--;
                        if(selectedIndex == 0 && yStartPosition%UISettings.itemHeight<0){
                            yStartPosition -= (yStartPosition%UISettings.itemHeight);
                        }
                    } else if(yStartPosition<0){
                        yStartPosition += UISettings.itemHeight;
                    } else{
                        //CR 13030
                        yStartPosition = totalNavigationSize;
                        selectedIndex = (short)(numberofRow-1);
                    }
                }
            }
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                getOption();
            }
        } else if (UISettings.FIREKEY == keyCode) {
            if(null != itemName){
                if(Settings.isIsGrid()){
                    ObjectBuilderFactory.GetKernel().handleItemSelection(selectedIndex, itemName[selectedIndex]);
                } else {
                    int startIndex = 0;
                    if(yStartPosition != 0){
                        startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                    }
                    ObjectBuilderFactory.GetKernel().handleItemSelection(startIndex+selectedIndex, itemName[startIndex + selectedIndex]);
                }
            } else {
                int startIndex = 0;
                if(yStartPosition != 0){
                    startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                }
                ObjectBuilderFactory.GetKernel().handleItemSelection(startIndex+selectedIndex, null);
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                if(null != itemName){
                    if(Settings.isIsGrid()){
                        ObjectBuilderFactory.GetKernel().handleOptionSelection(0, itemName[selectedIndex], UISettings.rOByte);
                    } else {
                        int startIndex = 0;
                        if(yStartPosition != 0){
                            startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                        }
                        ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
                                itemName[startIndex + selectedIndex], UISettings.rOByte);
                    }
                } else {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
                }
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.rOByte == 22){
                if(null != itemName){
                    if(Settings.isIsGrid()){
                        ObjectBuilderFactory.GetKernel().handleOptionSelection(0, itemName[selectedIndex], UISettings.rOByte);
                    } else {
                        int startIndex = 0;
                        if(yStartPosition != 0){
                            startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                        }
                        ObjectBuilderFactory.GetKernel().handleOptionSelection(0, itemName[startIndex + selectedIndex], UISettings.rOByte);
                    }
                }else {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
                }
            }
        } else if(keyCode == UISettings.RIGHTARROW) { //CR 8435
            if(Settings.isIsGrid()){
                if((selectedIndex+1)<itemName.length && (selectedIndex+1)%columnCount != 0){
                    selectedIndex++;
                } 
            } else {
                leftArrow = 0;
                if(rightArrow == 3)
                    rightArrow = 0;
                rightArrow++;
            }
        } else if(keyCode == UISettings.LEFTARROW){ //CR 8435
            if(Settings.isIsGrid()){
                if(selectedIndex%columnCount>0){
                    selectedIndex--;
                }
            } else {
                if(rightArrow == 3){
                    if(leftArrow == 3){
                        leftArrow = 0;
                        if(Settings.getIsDebug()){
                            Settings.setIsDebug(false);
                        } else Settings.setIsDebug(true);
                        SoundManager.getInstance().playSound();
                        rightArrow = 0;
                    }
                    leftArrow++;
                } else rightArrow = 0;
            }
        } 
        //#if KEYPAD
        //|JG|        else {
        //|JG|            if(iKeyHandler.handleSearchText(keyCode))
        //|JG|                search();
        //|JG|        }
        //#endif
    }

    /**
     * Method to get the option Menu and set the option to the Custom canvas 
     **/
    private void getOption() {
        byte[] opts = null;
        if(null != itemName){
            if(Settings.isIsGrid()){
                opts = ObjectBuilderFactory.GetKernel().getOptions(0, itemName[selectedIndex]); // OPT - index for options
            } else {
                int startIndex = 0;
                if(yStartPosition != 0){
                    startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                }
                opts = ObjectBuilderFactory.GetKernel().getOptions(0, itemName[startIndex + selectedIndex]);
            } // OPT - index for options\
        } else {
            opts = ObjectBuilderFactory.GetKernel().getOptions(0, null); //Cr 9408
        }
        if (null != opts) {
            CustomCanvas.setOptionsMenuArray(opts);
            itemFocused = UISettings.POPUPSCREN;
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            opts = null;
        }
    }

    public void handleOptionSelected(byte oIndex){
        enablePreviousSelection();
        if(null != itemName){
            if(Settings.isIsGrid()){
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
                        itemName[selectedIndex], oIndex);
            } else {
                int startIndex = 0;
                if(yStartPosition != 0){
                    startIndex = (int)(-1*yStartPosition)/UISettings.itemHeight;
                }
                ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
                        itemName[selectedIndex+startIndex], oIndex);
            }
        }else ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, oIndex);
    }
    
    public void handleNotificationSelected(boolean isReLoad,boolean isSend){
        if(isReLoad){
            enablePreviousSelection();
        }
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
    }
    
    public void enablePreviousSelection(){
        itemFocused = UISettings.MENU;
        reLoadFooterMenu();
    }
    
    public void handleMessageBoxSelected(boolean isSend, byte msgType,boolean isReload){
        if(isReload)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
    }

    /**
     * Method to reload footer menu based on the item focussed
     */
    public void reLoadFooterMenu() {
        if (itemFocused == UISettings.MENU) {
            UISettings.lOByte = PresenterDTO.setLOptByte();
            UISettings.rOByte = rOByte;
        } else if(itemFocused == UISettings.POPUPSCREN){
            iCustomPopup.reLoadFooterMenu();
        }
    }

    public void showDateForm(){
        
    }

    /**
     * Method to set the itemName array, logoimg array
     * 
     * @param logos  String array which represent logo location
     * @param names  String array which represent the profile names
     */
    private void setMenu(String[] logos, String[] names) {
        itemName = names;
        totalNavigationSize = 0;
        if (null != itemName) {
            int len = itemName.length;
            if(Settings.isIsGrid()){
                numberofRow = (short)(len/columnCount);
                if((len%columnCount) != 0)
                    numberofRow++;
                if(rowCount<numberofRow){
                    totalNavigationSize = (-1*(numberofRow-rowCount)*getGridGapWithHeight());
//                    totalNavigationSize += UISettings.formHeight-(
//                            (rowCount*getGridGapWithHeight())+(UISettings.headerHeight+UISettings.footerHeight));
                    numberofRow = rowCount;
                }
            } else {
                numberofRow = UISettings.numOfMenuItems;
                if(len<numberofRow){
                    numberofRow = (short)len;
                } else {
                    totalNavigationSize = ((len-numberofRow)*(UISettings.itemHeight*-1));
//                    totalNavigationSize += UISettings.formHeight-(
//                            (numberofRow*UISettings.itemHeight)+(UISettings.headerHeight+UISettings.footerHeight));
                }
            }
            logoimg = new Image[len];
            for (int i = 0; i < len; i++) {
                if(logos[i] != null){
                    logoimg[i] = CustomCanvas.getImage(logos[i]);
                }
            }
        }
    }

    /**
     * Method to rename phone number
     * 
     * @param itemId  Item Id
     * @param iName   Item Name
     */
    public void renameIndexedName(String[] msgUnReadCount) {
        this.msgUnReadCount = msgUnReadCount;
        if(null != itemName && !Settings.isIsGrid())
            itemName[0] = String.valueOf(Constants.options[38]) + msgUnReadCount[0] ;
        else msgCount = String.valueOf(Constants.options[38]) + msgUnReadCount[0];
    }

    /**
     * Method to remove menu item
     * 
     * @param itemId item Id
     * @param iName item Name
     */
    public void removeMenuItem(int itemId, String iName) {
//        int len = itemName.length;
//        for (int i = 0; i < len; i++) {
//            if (0 == itemName[i].compareTo(iName)) {
//                len -= 1;
//                String[] temp = itemName;
//                itemName = new String[len];
//                System.arraycopy(temp, 0, itemName, 0, i);
//                System.arraycopy(temp, i + 1, itemName, i, len - i);
//                temp = null;
//                if (null != logoimg) {
//                    Image[] tempI = new Image[len];
//                    System.arraycopy(logoimg, 0, temp, 0, i);
//                    System.arraycopy(logoimg, i + 1, temp, i, len - i);
//                    logoimg = tempI;
//                    tempI = null;
//                }
//                if(yStartPosition<0){
//                    yStartPosition += UISettings.itemHeight;
//                } else {
//                    if(itemName.length<numberofRow)
//                        numberofRow--;
//                    if(selectedIndex>=numberofRow){
//                        selectedIndex = (short)(numberofRow-1);
//                    }
//                }
//                break;
//            }
//        }
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
//        isPaint = true;
//        if(iCustomPopup.loadMessageBox(type, msg)){
//            itemFocused = UISettings.POPUPSCREN;
//        }
//    }
    
//    public void displayMessageSendSprite(){
//        iCustomPopup.setMessageSendSpritTimer();
//    }

    /**
     * Method to select the last accessed menu item
     * 
     * @param iName Item name to be selected
     */
    public void selectLastAccessedItem(String iName) {
        itemFocused = UISettings.MENU;
        yStartPosition = 0;
        selectedIndex = 0;
        if (null != itemName) {
            int len = itemName.length;
            int index = -1;
            if(null != iName){
                for (byte i = 0; i < len; i++) {
                    if (itemName[i].compareTo(iName) == 0) {
                        index = i;
                        break;
                    }
                }
            }
            if(Settings.isIsGrid()){
                if(index>-1){
                    selectedIndex = (short)index;
                    if(totalNavigationSize<0) {
                        index = ((index+1)-(numberofRow*columnCount));
                        if(index>0){
                            len = index/columnCount;
                            if(index%columnCount>0){
                                len++;
                            }
                            yStartPosition = (-1*len)*getGridGapWithHeight();
                        }
                    }
                }
            } else {
                if(index>-1) {
                    if(index>(numberofRow-1)){
                        yStartPosition = (index-(numberofRow-1))*(-1*UISettings.itemHeight);
                        selectedIndex = (short)(numberofRow-1);
                    } else selectedIndex = (short)index;
                }
            }
        }
        ShortHandCanvas.IsNeedPaint();
    }

    /**
     * Method to show notification. This function internally calls the 
     * showsmartpopup with the type defined for notification window
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
//        isPaint = true;
//        itemFocused = UISettings.POPUPSCREN;
//        iCustomPopup.showNotification(isGoTo);
//    }

    /**
     * Method to de-initialize variables.
     */
    private void deInitialize() {
        
        scrollLineLength = -1;
        previousY = -1;
        
        isPaint = false;
        isScrollSelected = false;
        //#if KEYPAD
        //|JG|        iKeyHandler.deinitialize();
        //#endif
        
        iCustomPopup.deinitialize();

        //byte
        itemFocused = 0;
        yStartPosition = totalNavigationSize = selectedIndex = rightArrow = leftArrow = 0;

        numberofRow = 0;

        //String Array
        itemName = null;
        msgUnReadCount = null;

        //int Array
        unreadChatCount = null;

        //Image Array
        logoimg = null;
        
//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);
        
        //bug 13169
        CustomCanvas.deinitialize();
        
    }

    /**
     * Method to load the profile presenter canvas
     * 
     * @param resDTO Instance of ProfileResponseDTO. Please refer 
     *               ProfileResponseDTO for the description of its attributes
     */
    public void load(ProfileResponseDTO resDTO) {
        deInitialize();
        itemFocused = UISettings.MENU;
        rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
        UISettings.lOByte = PresenterDTO.setLOptByte();
        reLoadFooterMenu();
        try {
            unreadChatCount = resDTO.getChatUnReadCount();
            setMenu(resDTO.getLogoLink(), resDTO.getProfileName());
            msgUnReadCount = resDTO.getMsgUnReadCount();
            if(!Settings.isIsGrid()){
                itemName[0] += msgUnReadCount[0];
                if(null != msgCount)
                    itemName[0]  = msgCount;
            }
            msgCount = null;
            //#if KEYPAD
            //|JG|            iKeyHandler.SetItemFocused(UISettings.SEARCH);
            //|JG|            iKeyHandler.setEntryProperty((short)0, Short.MAX_VALUE, 0, Float.MAX_VALUE, null, -1, iKeyHandler.ALPHANUMERIC,false,true);
            //#endif
            setScrollLen();
        } catch (Exception e) {
            Logger.loggerError("ProfileCanvas -> " + e.toString() + e.getMessage());
        }
        ShortHandCanvas.IsNeedPaint();
        isPaint = true;
    }

    /**
     * Method to unload the view
     */
    public void unLoad() {
        deInitialize();
    }

    public int getSmartPopupyPos(int keyCode) {
        return (UISettings.formHeight / 2 - UISettings.popupHeight / 2);
    }

    public void rotateScreen(boolean isLandScape) {
        if(null != itemName){
            totalNavigationSize = 0;
            setGridDisplayCount(false);
            if(Settings.isIsGrid()){
                rotateGridView();
            } else {
                rotateMenuScreen();
            }
            setScrollLen();
        }
        iCustomPopup.rotatePopup();
    }

    private void rotateGridView(){
        int count = itemName.length;
        numberofRow = (short)(count/columnCount);
        if(count%columnCount>0)
            numberofRow++;
        if(rowCount<numberofRow){
            totalNavigationSize = (-1*(numberofRow-rowCount)*getGridGapWithHeight());
            if(yStartPosition<totalNavigationSize){
                yStartPosition = totalNavigationSize;
            }
            numberofRow =  rowCount;
        } else {
            yStartPosition = 0;
        }
    }


    private void rotateMenuScreen(){
        numberofRow = UISettings.numOfMenuItems;
        if(itemName.length<numberofRow){
            numberofRow = (short)itemName.length;
            yStartPosition = 0;
        } else {
            totalNavigationSize = ((itemName.length-numberofRow)*(UISettings.itemHeight*-1));
            if(yStartPosition<totalNavigationSize){
                yStartPosition = totalNavigationSize;
            }
        }

        if(selectedIndex>=numberofRow){
            selectedIndex = (short)(numberofRow-1);
        }
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

    //CR 12318
    public void updateChatNotification(String[] msg){
        if(null != unreadChatCount){
            int count = itemName.length;
            for(int i=0;i<count;i++){
                if(itemName[i].compareTo(msg[2]) == 0){
                    unreadChatCount[i]++;
                    break;
                }
            }
        }
//        CustomCanvas.updateChatNotification(msg);
    }
}
