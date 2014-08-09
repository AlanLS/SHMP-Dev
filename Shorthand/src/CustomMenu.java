
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class CustomMenu implements ICustomMenu {

    private String[] itemName = null;
    private int[] itemId = null;
    private String[] tItemName = null;
    private int[] color = {0xffffff, 0x484848, 0xff3252, 0x0000ff,0xe2c501};
    short selectedIndex, numOfMenuItems;
    private short mPos = 0;
    private int[] style = null;
    private int mRotPos = 0;
    private byte sRotion = 0,  iRotPos = -1;
    private byte rOByte = -1;
    private IMenuHandler menuHandler = null;
    private boolean isSearch = false;
    private float scrollLen = -1;
    private short fHeight = 0;
    private Timer menuItemRotateTimer = null;
    private int previousY = -1;
    private byte[] displayStyle = null;
    private int totalItemCount = 0;
    private int totalItem = 0;
    private float yStratPosition = 0;
    private int totalNavigationSize = 0;
    private boolean isNotEscapeText = false;
    private boolean isScrollEnabled = false;

    public CustomMenu(IMenuHandler iMenuHandler) {
        menuHandler = iMenuHandler;
    }

    public byte getSelectedIndex() {
        return (byte)selectedIndex;
    }

    public int getNumberOfItem() {
        return numOfMenuItems;
    }

    public int getMenuPosition(boolean isChecking) {
        if(isChecking){
            if(isNotEscapeText)
                return mPos-UISettings.secondaryHeaderHeight;
        }
        return mPos;
    }

    public void removeOption() {
        rOByte = -1;
    }

    public void setMenuPosition(short pos, byte nMenuItems, byte robyte, boolean isNotEscapeText) {
        if(pos == UISettings.headerHeight+UISettings.secondaryHeaderHeight){
            this.isNotEscapeText = isNotEscapeText;
        } else this.isNotEscapeText = false;
        mPos = pos;
        totalItem = nMenuItems;
        numOfMenuItems = 0;
        rOByte = robyte;
        setIndexPostion();
        setScrollLen();
        stopRotateTimer();
    }

    public void rotateMenu(short pos, byte nMenuItems) {
        mPos = pos;
        totalNavigationSize = 0;
        if(null != itemName){
            numOfMenuItems = nMenuItems;
            totalItem = nMenuItems;
            if(itemName.length<nMenuItems){
                if(yStratPosition<0)
                    selectedIndex += (-1*yStratPosition)/UISettings.itemHeight;
                yStratPosition = 0;
                numOfMenuItems =(byte)itemName.length;
                if(selectedIndex>=numOfMenuItems){
                    selectedIndex = (short)(numOfMenuItems-1);
                }
            } else{
                totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
                if(yStratPosition <totalNavigationSize){
                    yStratPosition = totalNavigationSize;
                } else if(selectedIndex>=(numOfMenuItems)) {
                    yStratPosition -= ((selectedIndex+1)-numOfMenuItems)*UISettings.itemHeight;
                    selectedIndex = (short)(numOfMenuItems-1);
                }
            }
            setScrollLen();
            mRotPos = 0;
            sRotion = 0;
        }
    }

    private void setScrollLen() {
        scrollLen = -1;
        if (null != itemName) {
            if(totalNavigationSize<0){
                fHeight = (short) (UISettings.formHeight - ((2 + (UISettings.numOfMenuItems - numOfMenuItems)) * UISettings.itemHeight));
                //CR 12817
                if(isNotEscapeText)
                    fHeight += UISettings.secondaryHeaderHeight;
                scrollLen = (fHeight / (float)(-1*totalNavigationSize));
                int count = CustomCanvas.getScrollHeight(scrollLen);
                if(count>-1){
                    scrollLen =(fHeight-count) / (float)(-1*totalNavigationSize);
                }
            }
        }
    }

    public void handleMenu(int keyCode) {
        if (UISettings.DOWNKEY == keyCode) {
            if (null != itemName) {
                if(selectedIndex<(numOfMenuItems-1)){
                    stopRotateTimer();
                    selectedIndex++;
                    if(selectedIndex+1 == numOfMenuItems && yStratPosition%UISettings.itemHeight<0){
                        yStratPosition += (yStratPosition%UISettings.itemHeight);
                    }
                } else if(totalNavigationSize<yStratPosition
                        && totalNavigationSize<=(yStratPosition-UISettings.itemHeight)){
                    stopRotateTimer();
                    yStratPosition -= UISettings.itemHeight;
                } else {
                    //CR 13030
                    byte reSelect = menuHandler.enableDownSelection();
                    stopRotateTimer();
                    if(reSelect == 1 || reSelect == 0){ //CR 13030
                        selectedIndex = 0;
                        yStratPosition = 0;
                    } 
                }
            }
        } else if (UISettings.UPKEY == keyCode) {
            if(selectedIndex>0){
                stopRotateTimer();
                selectedIndex--;
                if(selectedIndex == 0 && yStratPosition%UISettings.itemHeight<0){
                    yStratPosition -= (yStratPosition%UISettings.itemHeight);
                }
            } else if(yStratPosition<0 && (yStratPosition+(UISettings.itemHeight))<=0){
                stopRotateTimer();
                yStratPosition += UISettings.itemHeight;
            } else {
                stopRotateTimer();
                //CR 13030
                byte reSelect = menuHandler.enableUpSelection();
                if (reSelect == 2 || reSelect == 0) { //CR 13030
                    selectedIndex = (byte)(numOfMenuItems-1);
                    yStratPosition = totalNavigationSize;
                } 
            }
        } else if (UISettings.FIREKEY == keyCode) {
            if (null != itemName) {
                int startIndex = 0;
                if(yStratPosition !=0)
                    startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
                menuHandler.sendSelectedValue(itemId[startIndex + selectedIndex],
                        itemName[startIndex + selectedIndex]);
            }
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                menuHandler.handleOptionSelected(UISettings.rOByte);
            }
        } else if (keyCode == UISettings.BACKKEY) {
            if (UISettings.rOByte == 22) {
                menuHandler.handleOptionSelected(UISettings.rOByte);
            }
        } else if (UISettings.LEFTOPTION == keyCode) {
            getOption();
        } else if (keyCode == UISettings.RIGHTARROW) {
            if (!isSearch) {
                stopRotateTimer();// ithaya
                byte reSelect = menuHandler.enableDownSelection();
                if (reSelect == 0) //menuHandler.handleInput(keyCode);
                {
                    selectLastItem(); // bug 3917
                } 
            }
        } else if (keyCode == UISettings.LEFTARROW) {
            if (!isSearch && null != itemName) {
                stopRotateTimer();
                yStratPosition = 0;
                selectedIndex = 0;
            }
        } else if (null != itemName) {
            //#if KEYPAD
            //|JG|            if(isSearch){ //|JG| CR 5594
            //|JG|                if (keyCode > 0 && menuHandler.isSearchText(keyCode)) {
            //|JG|                    search();
            //|JG|                }
            //|JG|
            //|JG|            } else { // CR 5594
            //|JG|                if(menuHandler.enableDownSelection() != 0){
            //|JG|                    stopRotateTimer();
            //|JG|                    menuHandler.handleInput(keyCode);
            //|JG|                }
            //|JG|            }
            //#endif
        }
    }

    public boolean  pointerPressed(int xPosition, int yPosition, boolean isNotDrag, 
            boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;
        if(null != itemName){
            int height = mPos;
            if(isNotEscapeText)
                height = UISettings.headerHeight;
            if(yPosition>height){
//                //bug no 12079
//                iRotPos = -1;
                yPosition -= height;
                if(isNotDrag || isPressed){
                    previousY = yPosition;
                    if(isPressed && CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition
                            && (-1*yStratPosition*scrollLen) <= yPosition
                            && ((-1*yStratPosition*scrollLen)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
                        isScrollEnabled = true;
                    } else {
                        isScrollEnabled = false;
                        //CR 13040
                        if(isNotEscapeText){
                            yPosition -= UISettings.secondaryHeaderHeight;
                        }
                        if(yPosition>0){
                            yPosition += (-1)*(yStratPosition%UISettings.itemHeight);
                            xPosition = yPosition/UISettings.itemHeight;
                            if(xPosition<itemName.length){
                                if(isNotDrag) {
                                    isNeedSelect = true;
                                } else if(!isNotDrag) {
                                    if(xPosition>=numOfMenuItems){
                                        selectedIndex = (short)(numOfMenuItems-1);
                                        yStratPosition -= UISettings.itemHeight;
                                        if(yStratPosition<totalNavigationSize)
                                            yStratPosition = totalNavigationSize;
                                    } else {
                                        selectedIndex = (short)xPosition;
                                    }
                                }
                            }
                        }
                    }
                } else if(scrollLen>-1){
                    if(previousY == -1)
                        previousY = yPosition;
                    //CR 13033
                    CustomCanvas.showScroll(isDragEnd);
                    //CR 13032
                    if(isScrollEnabled){
                        float position = (totalNavigationSize/(totalNavigationSize*scrollLen));
                        if((yPosition-previousY) != 0 && position>0)
                            yStratPosition -= (yPosition-previousY)*position;
                    }  else {
                        if(yPosition>UISettings.secondaryHeaderHeight)
                            yStratPosition += yPosition-previousY;
                        else return isNeedSelect;
                    }

                    if(yStratPosition>0)
                        yStratPosition = 0;
                    else if(yStratPosition<totalNavigationSize){
                        yStratPosition = totalNavigationSize;
                    }
                    previousY = yPosition;
                }
            }
        }
        if(isDragEnd){
            previousY = -1;
            isScrollEnabled = false;
        }
        return isNeedSelect;
    }

    public void drawScreen(Graphics g, byte itemFocused, byte lastItemfocused, 
            boolean isMessagebox, String letterCount) {
        try{
            if (null != itemName) {

                if (itemFocused != UISettings.MENU || itemFocused != UISettings.IMAGE_MENU) {
                    stopRotateTimer();
                }

                boolean isStyle = true;
                if (null == style) {
                    isStyle = false;
                }

                boolean isdraw = true;

                int pages = 0;
                if(yStratPosition != 0){
                    pages = (int)(-1*yStratPosition) / UISettings.itemHeight;
                }
                int nLine = numOfMenuItems + pages;

                if(itemName.length>nLine){
                    nLine++;
                    if(itemName.length>nLine){
                        nLine++;
                    }
                }

                int startIndex = pages;

                //CR 13059
                
                Font cFornt = Font.getFont(CustomCanvas.font.getFace(), Font.STYLE_BOLD, CustomCanvas.font.getSize());

                if(yStratPosition != 0){
                    pages = (int)(yStratPosition%UISettings.itemHeight);
                }

                if (UISettings.MENU == lastItemfocused || lastItemfocused == UISettings.IMAGE_MENU) {
                    CustomCanvas.drawSelection(mPos+pages, selectedIndex, g);
                }

                int y = mPos + pages + ((UISettings.itemHeight - g.getFont().getHeight()) / 2);
                //Draw menu
                for (int i = startIndex; i < nLine; i++, y += UISettings.itemHeight) {
                    isdraw = true;
                    //pos = mPos + y * UISettings.itemHeight + height;
                    if (isStyle) {
                        g.setColor(color[style[i]]);
                    } else {
                        g.setColor(color[0]);
                    }

                    if(displayStyle[i] == 1){
                        g.setFont(cFornt);
                    } else g.setFont(CustomCanvas.font);

                    if (i == startIndex + selectedIndex) {
                        if (UISettings.MENU == itemFocused || UISettings.IMAGE_MENU == itemFocused) {
                            //CR 12955
                            if(style[i] == 4){
                                g.setColor(color[0]);
                            }
                            isdraw = drawSelectItem(tItemName[i], g, y);
                        }
                    }
                    if (isdraw) {
                        g.drawString(tItemName[i], 3, y, Graphics.TOP | Graphics.LEFT);
                    }
                }
                cFornt = null;
            }
            if(null != letterCount){
                CustomCanvas.drawSecondaryHeader(letterCount, g,true,false);
                CustomCanvas.drawHeader(g);
            }
            //Scroll Background colour
            if (scrollLen > -1 && !isMessagebox) {
                //CR 12817
                if(isNotEscapeText){
                    CustomCanvas.drawScroll(g, scrollLen, UISettings.headerHeight, 
                            (-1*yStratPosition*scrollLen), fHeight,UISettings.formWidth);
                } else CustomCanvas.drawScroll(g, scrollLen, mPos, (-1*yStratPosition*scrollLen), 
                        fHeight,UISettings.formWidth);
            }

        }catch(Exception e){
            Logger.loggerError("CustomMenu->drawScreen->"+e.toString());
        }
        //bug 14457
        g.setFont(CustomCanvas.font);
    }

    private boolean drawSelectItem(String dStr, Graphics g, int pos) {
        if (iRotPos == -1) {
            mRotPos = 0;
            sRotion = 0;
            setSelectedString(g, dStr);
            //#if KEYPAD
            //|JG|            if(isSearch){
            //|JG|                if (dStr.toLowerCase().startsWith(menuHandler.getSearchTempText()) && menuHandler.getSearchTempText().length() > 0) {
            //|JG|                    g.drawString(dStr, 3, pos, Graphics.TOP | Graphics.LEFT);
            //|JG|                    g.setColor(0x000000);
            //|JG|                    g.drawString(dStr.substring(0, menuHandler.getSearchTempText().length()), 3, pos, Graphics.TOP | Graphics.LEFT);
            //|JG|                    return false;
            //|JG|                }
            //|JG|            }
            //#endif
        } else {
            if (mRotPos == 0) {
                if (sRotion >= 5) {
                    sRotion = 0;
                    iRotPos++;
                } else {
                    sRotion++;
                }
            } else if (sRotion >= 5) {
                sRotion = 0;
                mRotPos -= 4;
            } else {
                sRotion++;
            }
            //bu no 12079
            if (iRotPos >= dStr.length()) {
                iRotPos = 0;
                mRotPos = UISettings.formWidth - 8;
            } else if (mRotPos < 0) {
                mRotPos = 0;
            }
            if (iRotPos < 0) {
                iRotPos = 0;
            }
            g.drawString(dStr.substring(iRotPos), 3 + mRotPos, pos, Graphics.TOP | Graphics.LEFT);
            return false;
        }
        return true;
    }

    //bug 8377
    private void startRotateTimer(){
        stopRotateTimer();
        iRotPos = 0;
        menuItemRotateTimer = new Timer();
        menuItemRotateTimer.schedule(new MenuItemRotate(), 0,10);
    }

    //bug 8377
    private void stopRotateTimer(){
        iRotPos = -1;
        if(null != menuItemRotateTimer){
            menuItemRotateTimer.cancel();
            menuItemRotateTimer = null;
        }
    }

    private void setSelectedString(Graphics g, String value) {
        stopRotateTimer();
        if (g.getFont().stringWidth(value) > UISettings.formWidth - 3) {
            startRotateTimer();
        }
    }

    public String getSelectedMenuValue() {
        if (null != itemName) {
            int startIndex = 0;
            if(yStratPosition != 0)
                startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
            return itemName[selectedIndex + startIndex];
        }
        return null;
    }

    public String getSelectedDisplayMenuValue() {
        if (null != tItemName) {
            int startIndex = 0;
            if(yStratPosition != 0)
                startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
            return tItemName[selectedIndex + startIndex];
        }
        return null;
    }

    public int getSelectedItemId() {
        if (null != itemId) {
            int startIndex = 0;
            if(yStratPosition != 0)
                startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
            return itemId[selectedIndex + startIndex];
        }
        return -2;
    }

    public boolean isMenuPresent() {
        if (null != itemName) {
            return true;
        }
        return false;
    }

    public void setMenu(String[] seqList, String[] iNames, 
            String[] tName, int[] ids, int[] styles, 
            boolean isSearch, byte[] displayStyl, int totalItemCount) {
        this.isSearch = isSearch;
        this.totalItemCount = totalItemCount;
        if (null != iNames) {
            
            int len = iNames.length;
            int count = 0;
            if (null != seqList) {
                count = seqList.length;
            }
            itemName = new String[len + count];
            itemId = new int[len + count];
            style = new int[len + count];
            displayStyle = new byte[len+count];
            if (null != seqList) {
                System.arraycopy(seqList, 0, itemName, 0, count);
                for (int i = 0; i < count; i++) {
                    itemId[i] = -1;
                    style[i] = 2;
                }
            }
            if (null != iNames) {
                System.arraycopy(iNames, 0, itemName, count, len);
                if (null != ids) {
                    System.arraycopy(ids, 0, itemId, count, len);
                } else {
                    setItemId();
                }
                if (null != styles) {
                    System.arraycopy(styles, 0, style, count, len);
                }
                if(null != displayStyl){
                    System.arraycopy(displayStyl, 0, displayStyle, count, len);
                }
                if (null != tName) {
                    tItemName = tName;
                } else {
                    tItemName = itemName;
                }
            }
        }
    }

    private void setItemId() {
        int len;
        if (null != itemName && (len = itemName.length) > 0) {
            for (int i = 0; i < len; i++) {
                itemId[i] = -2;
            }
        }
    }

    private void getOption() {
        if (UISettings.lOByte > -1) {
            int startIndex = 0;
            if(yStratPosition != 0){
                startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
            }
            if(UISettings.lOByte == Constants.OPTIONS){
                byte[] optbyte = null;
                if (null != itemName) {
                    optbyte = ObjectBuilderFactory.GetKernel().getOptions(itemId[selectedIndex + startIndex],
                            itemName[selectedIndex + startIndex]);
                } else {
                    optbyte = ObjectBuilderFactory.GetKernel().getOptions(-2, null);
                }
                if (null != optbyte) {
                    CustomCanvas.setOptionsMenuArray(optbyte);
                    menuHandler.setItemfocuse(UISettings.OPTIONS);
                    optbyte = null;
                }
            } else {
                menuHandler.sendSelectedValue(UISettings.lOByte, itemName[selectedIndex+startIndex]);
            }
        }

    }

    public void reLoadFooterMenu() {
        UISettings.rOByte = rOByte;
        UISettings.lOByte = PresenterDTO.setLOptByte();
    }

    public void deInitialize() {
        itemName = null;
        tItemName = null;
        itemId = null;
        displayStyle = null;
        selectedIndex = 0;
        isScrollEnabled = false;
        totalNavigationSize = 0;
        yStratPosition = numOfMenuItems = 0;
        mPos = 0;
        style = null;
        mRotPos = 0;
        sRotion = 0;
        previousY = -1;
        stopRotateTimer();
        rOByte = -1;
        scrollLen = -1;
        isSearch = false;
    }


    //#if KEYPAD
    //|JG|    private void search() {
    //|JG|        int len = itemName.length;
    //|JG|        boolean isReset = true;
    //|JG|        int sInd = -1;
    //|JG|        int startIndex = 0;
    //|JG|        if(yStratPosition != 0)
    //|JG|            startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
    //|JG|        for (int i = (startIndex + selectedIndex + 1); i < len; i++) {
    //|JG|            if (itemName[i].toLowerCase().startsWith(menuHandler.getSearchTempText())) {
    //|JG|                sInd = i;
    //|JG|                isReset = false;
    //|JG|                break;
    //|JG|            } else if (itemName[i].toLowerCase().startsWith(menuHandler.getSearchText())) {
    //|JG|                isReset = false;
    //|JG|            }
    //|JG|        }
    //|JG|
    //|JG|        if (sInd == -1) {
    //|JG|            len = startIndex + selectedIndex + 1;
    //|JG|            for (int i = 0; i < len; i++) {
    //|JG|                if (itemName[i].toLowerCase().startsWith(menuHandler.getSearchTempText())) {
    //|JG|                    sInd = i;
    //|JG|                    isReset = false;
    //|JG|                    break;
    //|JG|                } else if (itemName[i].toLowerCase().startsWith(menuHandler.getSearchText())) {
    //|JG|                    isReset = false;
    //|JG|                }
    //|JG|            }
    //|JG|        }
    //|JG|
    //|JG|        if (sInd > -1) {
    //|JG|            if(sInd>numOfMenuItems){
    //|JG|                yStratPosition = (-1*(sInd-numOfMenuItems))*UISettings.itemHeight;
    //|JG|                selectedIndex = (short)(numOfMenuItems-1);
    //|JG|            } else {
    //|JG|                yStratPosition = 0;
    //|JG|                selectedIndex = (short)sInd;
    //|JG|            }
    //|JG|            stopRotateTimer();
    //|JG|        }
    //|JG|        if (isReset) {
    //|JG|            menuHandler.resetSearchValue();
    //|JG|        }
    //|JG|    }
    //|JG|
    //#endif

    //CR 14672, 14675
    public void updateMenuItems(String[] contacts,int[] contactId){
        if(null != itemName){
            stopRotateTimer();
            itemName = contacts;
            tItemName = contacts;
            displayStyle =  new byte[contacts.length];
            style = new int[contacts.length];
            itemId = contactId;
            setIndexPostion();
            setScrollLen();
        }
    }

    //CR 12118
    //Bug 14155, 14156
    public void updateManuItem(String oldValue, String dName, byte type, String newValue){
        int index = -1;
        int len = 0;
        if(null != itemName && (len = itemName.length)>0){
            if(null != oldValue){
                for (int i = 0; i < len; i++) {
                    if (itemName[i].compareTo(oldValue) == 0) {
                        index = i;
                        if(type == 3) //Chat Friend Screen
                            return;
                        break;
                    }
                }
            }
            stopRotateTimer();

        } else {
            if(type == 2 || type == 4) {//Chat history
                itemName = new String[]{newValue};
                tItemName = new String[]{newValue};
                displayStyle = new byte[]{1};
            } else {
                itemName = new String[]{newValue};
                tItemName = new String[]{newValue};
                displayStyle = new byte[1];
            }
            itemId = new int[1];
            style = new int[1];
            totalItemCount++;
            if(totalItem>numOfMenuItems)
                numOfMenuItems++;
            else if(totalItem<itemName.length){
                totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
            }
            return;
        }


        if(index>-1){
            tItemName[index] = newValue;
            itemName[index] = newValue;
            if(type == 2)
                displayStyle[index] = 1;
        } else {
            String[] temp = new String[len+1];
            if(type == 2) //Chat history
                temp[0] = newValue;//iName+" "+messagePlus + " (1)";
            else temp[0] = newValue;//iName+" "+messagePlus;
            System.arraycopy(itemName, 0, temp, 1, len);
            itemName = temp;
            temp = null;
            
            temp = new String[len+1];
            if(type == 2) //Chat history
                temp[0] = newValue;//iName+" "+messagePlus + " (1)";
            else temp[0] = newValue;//iName+" "+messagePlus;
            System.arraycopy(tItemName, 0, temp, 1, len);
            tItemName = temp;
            temp = null;

            int[] tId = new int[len+1];
            tId[0] = totalItemCount;
            totalItemCount++;
            System.arraycopy(itemId, 0, tId, 1, len);
            itemId = tId;
            tId = null;

            tId = new int[len+1];
            System.arraycopy(style, 0, tId, 1, len);
            style = tId;
            tId = null;

            byte[] rDisplayStyle = new byte[len+1];
            if(type == 2) //Chat history
                rDisplayStyle[0] = 1;
            System.arraycopy(displayStyle, 0, rDisplayStyle, 1, len);
            displayStyle = rDisplayStyle;
            rDisplayStyle = null;
            if(totalItem>numOfMenuItems){
                numOfMenuItems++;
            } else if(totalItem<itemName.length){
                totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
            }
            setScrollLen();
        }
    }

    public void changeMenuItemName(int itemId, String itemName) {
        int startIndex = 0;
        if(yStratPosition != 0)
            startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
        this.itemName[startIndex + selectedIndex] = itemName;
        tItemName[selectedIndex + startIndex] = itemName;
    }

    public void changeMenuItemStyle(int itemId, byte style) {
        int startIndex = 0;
        if(yStratPosition != 0)
            startIndex = (int)(-1*yStratPosition) / UISettings.itemHeight;
        this.style[startIndex + selectedIndex] = style;
    }

    public void removeMenuItem(int iId, String iName) {
        int len = itemName.length;
        if (len > 1) {
            for (int i = 0; i < len; i++) {
                if (0 == itemName[i].compareTo(iName) && itemId[i] == iId) {
                    stopRotateTimer();
                    len--;
                    String[] temp = new String[len];
                    System.arraycopy(itemName, 0, temp, 0, i);
                    System.arraycopy(itemName, i + 1, temp, i, len - i);
                    itemName = temp;
                    temp = null;
                    temp = new String[len];
                    System.arraycopy(tItemName, 0, temp, 0, i);
                    System.arraycopy(tItemName, i + 1, temp, i, len - i);
                    tItemName = temp;
                    temp = null;
                    int[] tId = new int[len];
                    System.arraycopy(itemId, 0, tId, 0, i);
                    System.arraycopy(itemId, i + 1, tId, i, len - i);
                    itemId = tId;
                    tId = null;
                    int[] tstyle = new int[len];
                    System.arraycopy(style, 0, tstyle, 0, i);
                    System.arraycopy(style, i + 1, tstyle, i, len - i);
                    style = tstyle;
                    tstyle = null;

                    byte[] rDisplayStyle = new byte[len];
                    System.arraycopy(displayStyle, 0, rDisplayStyle, 0, i);
                    System.arraycopy(displayStyle, i + 1, rDisplayStyle, i, len - i);
                    displayStyle = rDisplayStyle;
                    rDisplayStyle = null;

                    if(itemName.length<numOfMenuItems){ 
                        numOfMenuItems--;
                        if(selectedIndex>=numOfMenuItems) {
                            selectedIndex = (short)(numOfMenuItems-1);
                        }
                        totalNavigationSize = 0;
                        yStratPosition = 0;
                    } else if(itemName.length >= numOfMenuItems){ //bug 14397
                        totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
                        if(totalNavigationSize>yStratPosition)
                            yStratPosition = totalNavigationSize;
                    }

//                    if(yStratPosition < 0){
//                        yStratPosition += UISettings.itemHeight;
//                    } else
//                    if(itemName.length>numOfMenuItems){
//                        totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
//                    }
                    break;
                }
            }
        } else {
            itemName = null;
            byte reSelect = menuHandler.enableDownSelection();
            if (reSelect != 0) {
                stopRotateTimer();
            }
        }
        totalItemCount--;
        setScrollLen();
    }

    

    private void setIndexPostion() {
        if (null != itemName) {
            int len = itemName.length;
            yStratPosition = 0;
            selectedIndex = 0;
            numOfMenuItems = (short)totalItem;
            if (len < numOfMenuItems) {
                numOfMenuItems = (short)len;
            } else {
                totalNavigationSize = ((itemName.length-numOfMenuItems)*UISettings.itemHeight*-1);
            }
        }
    }

    public void selectLastAccessedItem(String iName, int iId) {
        if (null != itemName) {
            int len = itemName.length;
            if (iId > -3) {
                int sIndex = -1;
                if (null != iName) {
                    for (int i = 0; i < len; i++) {
                        if (itemName[i].compareTo(iName) == 0) {
                            sIndex = i;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < len; i++) {
                        if (itemId[i] == iId) {
                            sIndex = i;
                            break;
                        }
                    }
                }
                if(sIndex >-1){
                    if(sIndex>numOfMenuItems){
                       selectedIndex = (short)(numOfMenuItems-1);
                       yStratPosition = (-1*(sIndex-selectedIndex)*UISettings.itemHeight);
                    } else selectedIndex = (short)sIndex;
                }
            }
        }
    }

    public int getSmartPopupyPos(int keyCode) {
        int y = 0;
        if (keyCode == 1 || keyCode == 3) {
            y = (UISettings.formHeight / 2) - (UISettings.popupHeight / 2);
        } else if (keyCode == 2) {
            if (selectedIndex > numOfMenuItems / 2) {
                y = (mPos + selectedIndex * UISettings.itemHeight) - UISettings.popupHeight;
            } else {
                y = mPos + (selectedIndex + 1) * UISettings.itemHeight;
            }
        } else {
            y = (UISettings.formHeight / 2 - UISettings.popupHeight / 2);
        }
        return y;
    }

    /**
     * This method to added by the bug 3917
     * This method to should move the last menu item
     */
    public void selectLastItem() {
        if (null != itemName) {
            int len = itemName.length;
            yStratPosition = 0;
            selectedIndex = (byte) (numOfMenuItems - 1);
            if (numOfMenuItems < len) { //All item should not display in single screen
                yStratPosition = (-1*(len-numOfMenuItems))*UISettings.itemHeight;
            } 
        }
    }

    public void unLoad() {
        menuHandler = null;
        previousY = -1;
        totalNavigationSize = 0;
        yStratPosition = 0;
        selectedIndex = 0;
    }

    //bug 8377
    class MenuItemRotate extends TimerTask {
        /**
         * run method to start the timer
         */
        public void run() {
            ShortHandCanvas.IsNeedPaint();
        }
    }

}
