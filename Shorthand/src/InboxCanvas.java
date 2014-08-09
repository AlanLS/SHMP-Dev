
//import generated.GobSmart;
import javax.microedition.lcdui.Graphics;
import jg.Gob;


/**
 * Inbox canvas class for the Inbox Presenter Screen
 * 
 * @author Hakuna Matata
 * @version 2.0
 * @copyright (c) ShartHand Mobile Inc
 */
public class InboxCanvas implements IInboxPresenter,IPopupHandler {

    short selectedIndex, numOfMenuItems;
    //short startIndex, endIndex = -1;
    private String[] sender = null;
    private String[] messageId = null;
    //private String[] date = null;
    private String[] itemName = null;
    private boolean[] flag = null;

    //Temporary Left option text
    private byte rOByte;
    
    private byte itemFocused = 0;
    private short mpos = 0;
    private ICustomPopup iCustomPopup = null;
    
    private float scrollLen =-1;
    private int previousY =-1;
    private float yStratPosition = 0;
    
    private int totalNavigationSize = 0;

    private boolean  isScrollEnabled = false;
    //private boolean isSelected = false;
    /**
     * Constructor method to initialize the InboxCanvas
     */
    public InboxCanvas() {
        selectedIndex = 0;
        numOfMenuItems = UISettings.numOfMenuItems;
        iCustomPopup = new CustomPopup(this);
    }

    /**
     * Method to paint inbox canvas
     * 
     * @param g  An instance of Graphics class
     */
    public void paintGameView(Graphics g) {

        if(iCustomPopup.isCustomPopupState()){
            itemFocused = UISettings.POPUPSCREN;
        }

        clearScreen(g);

        drawMenu(g);

        CustomCanvas.drawSecondaryHeader(null, g,false,false);

        if(scrollLen>-1 && !iCustomPopup.isMessageFocused()){
            //CR 12817
            CustomCanvas.drawScroll(g, scrollLen, UISettings.headerHeight,
                    (-1*yStratPosition*scrollLen), (UISettings.formHeight-(2*UISettings.itemHeight))
                    ,UISettings.formWidth);
        }


        CustomCanvas.drawHeader(g);

        if (iCustomPopup.isMessageFocused()) {
            CustomCanvas.DrawOptionsMenu("", (byte) -1, (byte) -1, g);
        } else {
            if(null != messageId){ // CR number 6755
                CustomCanvas.DrawOptionsMenu(Constants.appendText[25], UISettings.lOByte, UISettings.rOByte, g);
            } if (itemFocused == UISettings.POPUPSCREN) {
                CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
            } else {
                CustomCanvas.DrawOptionsMenu("", UISettings.lOByte, UISettings.rOByte, g);
            }
        }
        iCustomPopup.drawScreen(g);
    }

    /**
     * Method to clear screen
     * 
     * @param g  An instance of Graphics class
     */
    private void clearScreen(Graphics g) {
        g.setColor(0xffffff);
        g.fillRect(0, 0, UISettings.formWidth,UISettings.formHeight);
    }
    
    private void setScrollLen(){
        scrollLen =-1;
        totalNavigationSize = 0;
        if(null != messageId){
            if(messageId.length > numOfMenuItems){
                totalNavigationSize = (messageId.length - numOfMenuItems)*UISettings.itemHeight*-1;
                //CR 12817 
                scrollLen =((UISettings.formHeight-mpos) / (float)(-1*totalNavigationSize));
                int count = CustomCanvas.getScrollHeight(scrollLen);
                if(count>-1){
                    scrollLen =((UISettings.formHeight-(mpos+count)) / (float)(-1*totalNavigationSize));
                }
            }
        }
    }

    /**
     * Method to draw menu
     * 
     * @param g  An instance of Graphics class.
     */
    private void drawMenu(Graphics g) {

        try {
            if (null != messageId) {
                int iWidth = 0;
                int iheight = 0;
                if(null != CustomCanvas.images){
                    iWidth = CustomCanvas.images[UISettings.RD].width;
                    iheight = CustomCanvas.images[UISettings.RD].height;
                }
                iheight = (UISettings.itemHeight - iheight)/2;
                boolean isfit = true;
                
                int pages = 0;
                if(yStratPosition != 0)
                    pages = (int)(-1*yStratPosition) / UISettings.itemHeight;
                int nLine = numOfMenuItems + pages;

                if(messageId.length>nLine){
                    nLine++;
                    if(messageId.length>nLine)
                        nLine++;
                }
                
                int startIndex = pages;

                int fheight = (UISettings.itemHeight - g.getFont().getHeight()) / 2;

                if(yStratPosition != 0)
                    pages = (int)(yStratPosition%UISettings.itemHeight);

                CustomCanvas.drawSelection(mpos+pages, selectedIndex, g);

                int y = mpos + pages;
                //Draw Inbox items
                for (int i = startIndex; i < nLine; i++, y+= UISettings.itemHeight) {
                    g.setColor(0xffffff);
                    //pos = mpos + UISettings.itemHeight * y;
                    if(null != CustomCanvas.images){
                        if (flag[i]) {
                            if (isfit) {
                                CustomCanvas.images[UISettings.RD].paint(g, 2, y + iheight, Gob.TRANS_NONE);
                            } else {
                                CustomCanvas.images[UISettings.RD].paintClipped(g, 2, y , Gob.TRANS_NONE,0,0,iWidth,UISettings.itemHeight);
                            }
                        } else {
                            if (isfit) {
                                CustomCanvas.images[UISettings.UR].paint(g, 2, y + iheight , Gob.TRANS_NONE);
                            } else {
                                CustomCanvas.images[UISettings.UR].paintClipped(g, 2, y , Gob.TRANS_NONE,0,0,iWidth,UISettings.itemHeight);
                            }
                        }
                    }

                    g.setColor(0x672792);
                    g.drawString(sender[i], iWidth + 4, y + fheight, Graphics.TOP | Graphics.LEFT);
                    //g.drawString(date[i], UISettings.formWidth - 5, pos + fheight, Graphics.TOP | Graphics.RIGHT);
                    g.drawLine(0, y + UISettings.itemHeight, UISettings.formWidth, y + UISettings.itemHeight);
                }
                
            } else {
                g.setColor(0x000000);
                g.drawString(Constants.headerText[22], UISettings.formWidth / 2 - g.getFont().stringWidth(Constants.headerText[22]) / 2,
                        UISettings.formHeight / 2, Graphics.TOP | Graphics.LEFT);
            }
        } catch (Exception e) {
            Logger.loggerError("InboxCanvas->DrawMenu "+e.toString());
        }
    }

    /**
     * Method to handle the key pressed event based on the item focussed.
     * 
     * @param type Key Code
     **/
    public void keyPressed(int keyCode) {
        if (itemFocused == UISettings.MENU) {
            handleMenu(keyCode);
        }else if(itemFocused == UISettings.POPUPSCREN){
            iCustomPopup.keyPressed(keyCode);
        }
    }    

    public boolean pointerPressed(int xPosition, int yPosition, boolean isNotDrag, 
            boolean isDragEnd, boolean isPressed){
        boolean isNeedSelect = false;
        if(UISettings.POPUPSCREN == itemFocused){
            previousY = yPosition;
            isNeedSelect = iCustomPopup.pointerPressed(xPosition, yPosition, isNotDrag,isDragEnd, isPressed);
        } else if(yPosition > UISettings.headerHeight){
            if(yPosition>=(UISettings.formHeight-UISettings.footerHeight)){
                previousY = yPosition;
                if(isNotDrag){
                    if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
                        keyPressed((UISettings.RIGHTOPTION));
                    } else if(UISettings.lOByte>-1 && xPosition>=(UISettings.FOTTER_TEXT_DRAW_POSITION) &&
                            xPosition<=(UISettings.formWidth/2)){
                        keyPressed(UISettings.LEFTOPTION);
                    }
                }
            }  else if(null != messageId){
                yPosition -= UISettings.headerHeight;
                if(isNotDrag || isPressed){
                    previousY = yPosition;
                    if(isPressed && CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition
                            && ((-1*yStratPosition*scrollLen)) <= yPosition
                            && ((-1*yStratPosition*scrollLen)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
                        isScrollEnabled = true;
                    } else if(yPosition>UISettings.secondaryHeaderHeight){
                        yPosition -= UISettings.secondaryHeaderHeight;
                        yPosition += (-1)*(yStratPosition%UISettings.itemHeight);
                        xPosition = yPosition/UISettings.itemHeight;
                        if(xPosition<itemName.length){
                            //CR 13040
                            if(isNotDrag) {
                                isNeedSelect = true;
                            } else if(!isNotDrag) {
                                if(xPosition>=numOfMenuItems){
                                    selectedIndex = (short)(numOfMenuItems-1);
                                    yStratPosition -= UISettings.itemHeight;
                                    if(yStratPosition<totalNavigationSize)
                                        yStratPosition = totalNavigationSize;
                                } else{
                                    selectedIndex = (short)xPosition;
                                }
                            }
                        }
                    }
                } else if(scrollLen>-1){
                    //CR 13033
                    CustomCanvas.showScroll(isDragEnd);

                    //CR 13032
                    if(isScrollEnabled){
                        float position = (totalNavigationSize/(totalNavigationSize*scrollLen));
                        if((yPosition-previousY) != 0 && position>0)
                            yStratPosition -= (yPosition-previousY)*position;
                    } else {
                        if(yPosition>UISettings.secondaryHeaderHeight){
                            yStratPosition += yPosition-previousY;
                        } else return isNeedSelect;
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
        if(isDragEnd)
            isScrollEnabled = false;
        return isNeedSelect;
    }

    /**
     * Method to handle key pressed event in the options menu
     * 
     * @param type Key Code
     **/
    private void handleMenu(int keyCode) {
        if (keyCode == UISettings.LEFTOPTION) {  //Right options key
            if (UISettings.lOByte > -1) {
                getOptions();
            }
        } else if (keyCode == UISettings.RIGHTOPTION) {  //Left options key
            if (UISettings.rOByte > -1) {
                if (null != messageId) {
                    int startIndex = 0;
                    if(yStratPosition !=0)
                        startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
                            messageId[selectedIndex + startIndex], UISettings.rOByte);
                } else {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
                }
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.rOByte == 22){
                if (null != messageId) {
                    int startIndex = 0;
                    if(yStratPosition !=0)
                        startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
                            messageId[selectedIndex + startIndex], UISettings.rOByte);
                } else {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, null, UISettings.rOByte);
                }
            }
        } else if (keyCode == UISettings.FIREKEY ) { //Fire key
            if (null != messageId) {
                int startIndex = 0;
                if(yStratPosition !=0)
                    startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
                ObjectBuilderFactory.GetKernel().handleItemSelection(0, messageId[selectedIndex + startIndex]);
            }
        } else if (keyCode == UISettings.UPKEY) { //Up Arrow key
            if(null != messageId){
                if(selectedIndex>0){
                    selectedIndex--;
                    if(selectedIndex == 0 && yStratPosition%UISettings.itemHeight<0){
                        yStratPosition -= (yStratPosition%UISettings.itemHeight);
                    }
                } else if(yStratPosition<0 && (yStratPosition+(UISettings.itemHeight)) <= 0){
                    yStratPosition += UISettings.itemHeight;
                } else {
                    //CR 13030
                    selectedIndex = (short)(numOfMenuItems-1);
                    yStratPosition = totalNavigationSize;
                }
            }
        } else if (keyCode == UISettings.DOWNKEY) { //Down Arrow Key
            if (null != messageId) {
                if(selectedIndex<(numOfMenuItems-1)){
                    selectedIndex++;
                    if(selectedIndex+1 == numOfMenuItems && yStratPosition%UISettings.itemHeight<0){
                        yStratPosition -= (yStratPosition%UISettings.itemHeight);
                    }
                } else if(totalNavigationSize<yStratPosition
                        && totalNavigationSize<=(yStratPosition-UISettings.itemHeight)){
                    yStratPosition -= UISettings.itemHeight;
                } else {
                    //CR 13030
                    selectedIndex = 0;
                    yStratPosition = 0;
                }
            }
        }
    }

    /**
     * Method get the options menu from the backend and draws them using 
     * Custom Canvas.
     */
    private void getOptions() {
        int startIndex = 0;
        if(yStratPosition !=0)
            startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
        byte[] opts = ObjectBuilderFactory.GetKernel().getOptions(0, messageId[selectedIndex + startIndex]);
        CustomCanvas.setOptionsMenuArray(opts);
        if (null != opts) {
            iCustomPopup.setItemFocused(UISettings.OPTIONS);
            itemFocused = UISettings.POPUPSCREN;
        }
    }

    /**
     * Method to reload footer menu based on the item focussed.
     */
    private void reLoadFooterMenu() {
        if (itemFocused == UISettings.MENU) {
            UISettings.rOByte = rOByte;
            if (null != messageId) {
                UISettings.lOByte = PresenterDTO.setLOptByte();
            } else {
                UISettings.lOByte = -1;
            }
        }else if(itemFocused == UISettings.POPUPSCREN)
            iCustomPopup.reLoadFooterMenu();
    }

    /**
     * Method to populate the sender array, messageId array, date array
     * and read/unread flag array based on the InboxItems dto array.
     * 
     * @param inboxitem An instance of InboxItems
     */
    public void setMenu(InboxItems[] inboxItemList) {
        int len = 0;
        if (inboxItemList != null && (len = inboxItemList.length) > 0) {
            itemFocused = UISettings.MENU;
            sender = new String[len];
            itemName = new String[len];
            messageId = new String[len];
            //date = new String[len];
            flag = new boolean[len];
            int width = 0;
            if(null != CustomCanvas.images)
                width = CustomCanvas.images[UISettings.RD].width;
            for (int i = 0; i < len; i++) {
                sender[i] = CustomCanvas.getSecondaryHeader(inboxItemList[i].getSender(), "",width);
                
                messageId[i] = inboxItemList[i].getMessageId();
                itemName[i] = inboxItemList[i].getSender();
                //date[i] = Utilities.getViewDate(inboxItemList[i].getDate());
                flag[i] = inboxItemList[i].isReadFlag();
            }
        } else {
            sender = null;
            itemName = null;
            messageId = null;
            //date = null;
            flag = null;
            selectedIndex = 0;
            numOfMenuItems = 0;
        }
    }

    /**
     * Method to remove a selected item. 
     * 
     * @param msgId  Message Id
     */
    public void removeSelectedItem(String msgId) {
        if (null != messageId && null != msgId) {
            int len = messageId.length;
            if (len > 1) {
                for (int i = 0; i < len; i++) {
                    if (0 == messageId[i].compareTo(msgId)) {
                        len -= 1;
                        String[] temp = messageId;
                        messageId = new String[len];
                        System.arraycopy(temp, 0, messageId, 0, i);
                        System.arraycopy(temp, i + 1, messageId, i, len - i);
                        temp = sender;
                        sender = new String[len];
                        System.arraycopy(temp, 0, sender, 0, i);
                        System.arraycopy(temp, i + 1, sender, i, len - i);
                        
//                        temp = date;
//                        date = new String[len];
//                        System.arraycopy(temp, 0, date, 0, i);
//                        System.arraycopy(temp, i + 1, date, i, len - i);
                        temp = null;
                        temp = itemName;
                        itemName = new String[len];
                        System.arraycopy(temp, 0, itemName, 0, i);
                        System.arraycopy(temp, i + 1, itemName, i, len - i);
                        temp = null;
                        
                        boolean[] tfalge = new boolean[len];
                        System.arraycopy(flag, 0, tfalge, 0, i);
                        System.arraycopy(flag, i + 1, tfalge, i, len - i);
                        flag = tfalge;
                        tfalge = null;

                        if(yStratPosition<0){
                            yStratPosition += UISettings.itemHeight;
                        } else if(messageId.length<numOfMenuItems){
                            numOfMenuItems = (short)messageId.length;
                            if(selectedIndex >= numOfMenuItems){
                                selectedIndex = (short)(numOfMenuItems-1);
                            }
                        }
                        break;
                    }
                }
            } else {
                selectedIndex = 0;
                numOfMenuItems = 0;
                yStratPosition = 0;
                messageId = null;
                sender = null;
                itemName = null;
                flag = null;
                reLoadFooterMenu();
            }
            setScrollLen();
        }
    }

    /**
     * Method to load message box based on the type
     * 
     * @param type  Type of the message box.
     *              <li> 0 - smartPopup </li>
     *              <li> 1 - MessageBox (ok)  </li>
     *              <li> 2 - MessageBox (OK) and (Cancel) </li>
     *              <li> 3 - MessageBox (RemindMe) and (Don't Remind Me) </li>
     * 
     * @param msg  Message box message
     */
//    public void loadMessageBox(byte type, String msg) {
//        if(iCustomPopup.loadMessageBox(type, msg))
//            itemFocused = UISettings.POPUPSCREN;
//    }
    
//    public void displayMessageSendSprite(){
//        iCustomPopup.setMessageSendSpritTimer();
//    }

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
     * 
     */
    

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
     * Method to deinitialze the variables.
     */
    private void deInitialize() {
        
        iCustomPopup.deinitialize();
        
        //String Array
        sender = messageId = itemName = null;//date = null;

        //Bolean Array
        flag = null;
        isScrollEnabled = false;

        //Byte
        selectedIndex = numOfMenuItems = 0;
        yStratPosition = 0;
        totalNavigationSize = 0;
        itemFocused = 0;
        rOByte = -1;
        scrollLen =-1;

        //Short
        mpos = 0;

//        ObjectBuilderFactory.getPCanvas().setNotificationParam(false);
        
        //bug 13169
        CustomCanvas.deinitialize();
       

        //int
        previousY = -1;
    }

    /**
     * Method to load the InboxCanvas based on the InboxResponse DTO
     * 
     * @param resDTO  An instance of InboxResponseDTO which contains attributes
     *                to load the canvas.
     */
    public void load(InboxResponseDTO resDTO) {
        deInitialize();
        try {
            mpos = UISettings.headerHeight;
            numOfMenuItems = UISettings.numOfMenuItems;

            if (null != (CustomCanvas.sHeader=resDTO.getSecondaryHeaderText())) {
                numOfMenuItems--;
                //CR 12817
                //bug 12924
                mpos += UISettings.secondaryHeaderHeight;
            }
            
            setMenu(resDTO.getMessages());
            
            rOByte = UISettings.rOByte = resDTO.getLeftOptionText();
            UISettings.lOByte = PresenterDTO.setLOptByte();
            reLoadFooterMenu();
            
            setScrollLen();
        } catch (Exception e) {
            Logger.loggerError("InboxCanvas -> Load" + e.toString() + e.getMessage());
        }
        ShortHandCanvas.IsNeedPaint();
    }

    /**
     * Method to reorder the inbox menu item
     * 
     * @param lasId Last selected id.
     */
    public void reorder(String lasId) {
        if (null != sender) {
            int len = sender.length;
            int count = len / 2;
            String temp;
            boolean flg;
            for (int i = 0,  j = len - 1; i < count; i++, j--) {
                temp = messageId[i];
                messageId[i] = messageId[j];
                messageId[j] = temp;
//                temp = date[i];
//                date[i] = date[j];
//                date[j] = temp;
                temp = sender[i];
                sender[i] = sender[j];
                sender[j] = temp;
                
                temp = itemName[i];
                itemName[i] = itemName[j];
                itemName[j] = temp;
                
                flg = flag[i];
                flag[i] = flag[j];
                flag[j] = flg;
            }
            selectLastAccessedItem(lasId);
        }
    }

    /**
     * Method to select the last accessed item.
     * 
     * @param msgId Previously selected message id.
     **/
    public void selectLastAccessedItem(String msgId) {
        itemFocused = UISettings.MENU;
        if (null != messageId) {
            int len = messageId.length;
            selectedIndex = 0;
            if (len < numOfMenuItems) {
                numOfMenuItems = (byte)len;
            }
            if (null != msgId) {
                for (byte i = 0; i < len; i++) {
                    if (0 == messageId[i].compareTo(msgId)) {
                        if(i>=numOfMenuItems){
                            yStratPosition = ((-1)*((i+1)-numOfMenuItems))*UISettings.itemHeight;
                            selectedIndex = (short) (numOfMenuItems-1);
                        } else selectedIndex = i;
                        break;
                    }
                }
            }
        } else {
            yStratPosition = 0;
            selectedIndex = 0;
            numOfMenuItems = 0;
        }
        reLoadFooterMenu();
    }

    /**
     * Method to unload the canvas
     */
    public void unLoad() {
        deInitialize();
    }

    public void handleNotificationSelected(boolean isReLoad, boolean isSend) {
        if(isReLoad)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleNotificationSelection(isSend);
        
    }

    public void handleOptionSelected(byte oIndex) {
        enablePreviousSelection();
        if(null != messageId){
            int startIndex = 0;
            if(yStratPosition !=0)
                startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
            ObjectBuilderFactory.GetKernel().handleOptionSelection(
                    0, messageId[selectedIndex + startIndex],
                    oIndex);
        } else {
            ObjectBuilderFactory.GetKernel().handleOptionSelection(
                    0, null,oIndex);
        }
    }

    public void handleMessageBoxSelected(boolean isSend, byte msgType,boolean isReload) {
        if(isReload)
            enablePreviousSelection();
        ObjectBuilderFactory.GetKernel().handleMessageBox(isSend,msgType);
    }

    public void enablePreviousSelection() {
        if (null != messageId) {
            itemFocused = UISettings.MENU;
        } else {
            itemFocused = 0;
        }
        reLoadFooterMenu();

    }

    public int getSmartPopupyPos(int keyCode) {
        int y = 0;
        if (keyCode == 1 || keyCode == 3) {
            y = UISettings.formHeight / 2 - UISettings.popupHeight / 2;
        }
        return y;
    }

    public void rotateScreen(boolean isLandScape) {
        rotateMenuScreen();
        reformatMenu();
        iCustomPopup.rotatePopup();
    }
    
    private void reformatMenu(){
        if(null != itemName){
            int width = 0;
            if(null != CustomCanvas.images)
                width = CustomCanvas.images[UISettings.RD].width;
            int len = itemName.length;
            sender = new String[len];
            for (int i = 0; i < len; i++) {
                sender[i] = CustomCanvas.getSecondaryHeader(itemName[i], "",width);
            }
        }
    }
            
    private void rotateMenuScreen(){
        numOfMenuItems = UISettings.numOfMenuItems;
        if(null != messageId){
            if(messageId.length<numOfMenuItems)
                numOfMenuItems =(short)itemName.length;

            if(yStratPosition != 0){
                int startIndex = 0;
                if(yStratPosition !=0)
                    startIndex = (int)(-1*yStratPosition)/UISettings.itemHeight;
                if((startIndex+numOfMenuItems)>messageId.length){
                    yStratPosition += ((startIndex+numOfMenuItems)-messageId.length) *UISettings.itemHeight;
                }
            }

            if(selectedIndex>=numOfMenuItems){
                selectedIndex = (short)(numOfMenuItems-1);
            }
            setScrollLen();
        }
    }

    public void handleSymbolpopup(char selSymbol, boolean isReload,boolean isSet) {

    }

    public void loadSympolPopup() {
        itemFocused = UISettings.POPUPSCREN;
        iCustomPopup.handleSmartPopup(15);
    }

//    //CR 12318
//    public void updateChatNotification(String[] msg){
//        CustomCanvas.updateChatNotification(msg);
//    }
}
