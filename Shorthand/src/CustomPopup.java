
import javax.microedition.lcdui.Graphics;
import java.util.Timer;
import java.util.TimerTask;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class CustomPopup implements ICustomPopup {
    
    private byte itemFocused = -1;
    private boolean msgSel;
    private byte msgKey;
    private short sPopupTimer = -1;
    private byte popupKey;
    private IPopupHandler iPopupHandler = null;
    private byte symKey;
    private Timer popup_timer = null;
    private byte mSendImageRTimer = -1;
    private static Timer msgSendSpriteTimer = null;
    //private boolean isSelected = false;

    public boolean isCustomPopupState(){
        boolean isPopupState = false;

        if(CustomCanvas.isNotificationGoto>-1){
            isPopupState = true;
            showNotification();
        } else if(CustomCanvas.msgType>-1){
            isPopupState = loadMessageBox();
        }

        if(CustomCanvas.isShowMessageSendSprit){
            CustomCanvas.isShowMessageSendSprit = false;
            setMessageSendSpritTimer();
        }

        if(CustomCanvas.isChatNotification){
            CustomCanvas.isChatNotification = false;
            CustomCanvas.startChatNotificationTimer();
        }

        return isPopupState;
    }

    private void startMsgSendSpriteTimer(){
        msgSendSpriteTimer = new Timer();
        msgSendSpriteTimer.schedule(new msgSendSpritTimer(), 0,100);
    }

    private void stopMsgSendSpriteTimer(){
        mSendImageRTimer =-1;
        if(null != msgSendSpriteTimer){
            msgSendSpriteTimer.cancel();
            msgSendSpriteTimer = null;
        }
    }

    private boolean isSendMessageSprite(){
        if(mSendImageRTimer>=0){
            if((mSendImageRTimer%15) == 0){
                if(mSendImageRTimer/15 >= 3){
                    mSendImageRTimer =-1;
                    stopMsgSendSpriteTimer();
                    return true;
                }
            }
            mSendImageRTimer++;
            return true;
        }
        return false;
    }

    public void setMessageSendSpritTimer(){
        mSendImageRTimer = 0;
        stopMsgSendSpriteTimer();
        startMsgSendSpriteTimer();
        //ShortHandCanvas.IsNeedPaint();
    }

    public CustomPopup(IPopupHandler iPopupHandler){
        this.iPopupHandler = iPopupHandler;
    }
    
    public void setItemFocused(byte itemFocused){
        handleSmartPopup(0);
        this.itemFocused = itemFocused;
        reLoadFooterMenu();
    }
    
    public boolean isMessageFocused(){
        if(itemFocused == UISettings.MESSAGEBOX)
            return true;
        return false;
    }
    
    public void showNotification(){
        itemFocused = UISettings.NOTIFICATION;
        byte isGoto = CustomCanvas.isNotificationGoto;
        CustomCanvas.isNotificationGoto = -1;
        if(isGoto == 1)
            handleSmartPopup(13); //With goto and dismiss option
        else if(isGoto == 0)handleSmartPopup(12); //with dismiss option
        else if(isGoto == 2) handleSmartPopup(17); //Chat with Dismiss option
        ShortHandCanvas.IsNeedPaint();
    }
    
    public void drawScreen(Graphics g){
        if (sPopupTimer>-1) {
            CustomCanvas.showPopup(iPopupHandler.getSmartPopupyPos(popupKey), g);
        }
        
        if (itemFocused == UISettings.OPTIONS) {
            CustomCanvas.showOptionsPopup(g);
        }
        
        if(itemFocused == UISettings.SYMBOLS || symKey>0){
            CustomCanvas.drawSymbolpopup(g);
        }
        
        if (itemFocused == UISettings.MESSAGEBOX || msgKey > 1) {
            if(itemFocused != UISettings.NOTIFICATION)
                CustomCanvas.drawMessageBox(g, UISettings.lOByte, UISettings.rOByte, msgSel);
            else CustomCanvas.drawMessageBox(g, (byte)-1, (byte)-1, msgSel);
        }
        if (itemFocused == UISettings.NOTIFICATION) {
            if(popupKey == 13 || popupKey == 17)
                CustomCanvas.drawNotification(g,true);
            else CustomCanvas.drawNotification(g,false);
        }

        CustomCanvas.drawMessageSendSprit(g, mSendImageRTimer);
    }
    
    /**
     * 0. Smart Popup timer With Text and Timer
     * 1. SmartPopup timer without Text and With timer
     * 2. Smartpopup with Text and without Timer
     * 
     * 3. Message box With Ok Option
     * 4. Message Box With Ok and Cancel Option
     * 5. Message Box With Yes option
     * 6. Message Box with Yes and No Option
     * 7. Message Box with Accept and Cancel Option
     * 8. Message Box Remind Me and Don't Remind Me Option
     * 9. Message Box with Now and Latter Option
     * 
     * 10. Notification With goto option
     * 11. Notification with Dismiss option
     * 12. Notification With Goto and Dismiss option
     * 
     * @param type
     * @param popupText
     */
    public boolean loadMessageBox(){
        boolean isMessagebox = true;
        byte tempType = CustomCanvas.msgType;
        CustomCanvas.msgType = -1;
        
        if(tempType == 0){
            tempType = 1;
        } else if(tempType == 20) {
            tempType = 16;
        }
        if (tempType == 1 || tempType == 2 || tempType == 3 || tempType == 16) {
            isMessagebox = false;
        }
        handleSmartPopup(tempType);
        return isMessagebox;
    }
    
    public void keyPressed(int keyCode){
        if(itemFocused == UISettings.OPTIONS)
            handleOption(keyCode);
        else if(itemFocused == UISettings.MESSAGEBOX)
            handleMessagebox(keyCode);
        else if(itemFocused == UISettings.NOTIFICATION)
            handlenotification(keyCode);
        //#if KEYPAD
        //|JG|        else if(itemFocused == UISettings.SYMBOLS)
        //|JG|            handleSymbol(keyCode);
        //#endif
    }

    public boolean pointerPressed(int xPosition, int yPosition, 
            boolean isNotDrag, boolean isDragEnd, boolean isPressed){
        boolean isNoNeedSelect = false;
        //#if KEYPAD
        //|JG|        if(itemFocused == UISettings.SYMBOLS){
        //|JG|            if(yPosition > (UISettings.formHeight-UISettings.footerHeight)){
        //|JG|                if(isNotDrag){
        //|JG|                    if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
        //|JG|                        symKey = 0;
        //|JG|                        itemFocused = 0;
        //|JG|                        iPopupHandler.handleSymbolpopup(' ', true,false);
        //|JG|                    } else if(UISettings.lOByte>-1 && xPosition<=(UISettings.formWidth/2)){
        //|JG|                        symKey = 0;
        //|JG|                        itemFocused = 0;
        //|JG|                        iPopupHandler.handleSymbolpopup(CustomCanvas.getSelectedSymbol(), true,true);
        //|JG|                    }
        //|JG|                }
        //|JG|            } else if(CustomCanvas.isSymbolPopupSelected(xPosition, yPosition, isNotDrag) && isNotDrag){
        //|JG|//                symKey = 0;
        //|JG|//                itemFocused = 0;
        //|JG|//                iPopupHandler.handleSymbolpopup(CustomCanvas.getSelectedSymbol(), true,true);
        //|JG|                isNoNeedSelect = true;
        //|JG|            }
        //|JG|        } else
            //#endif
            if(itemFocused == UISettings.MESSAGEBOX){
            byte selected = CustomCanvas.isMessageSelected(xPosition, yPosition, UISettings.lOByte,
                    UISettings.rOByte,isNotDrag,isDragEnd, isPressed);
            if(isNotDrag || isPressed){

                //CR 13040
                if(isNotDrag && ((selected == 0 && msgSel) || (selected == 1 && !msgSel)))
                    isNoNeedSelect = true;
//                if(isPressed){
//                    if((selected == 0 && msgSel) || (selected == 1 && !msgSel))
//                        isSelected = true;
//                    else isSelected = false;
//                } else if(isSelected) //CR 13040
//                    isNoNeedSelect = true;
                if(selected == 0){
                    msgSel = true;
                } else if(selected == 1){
                    msgSel = false;
                } else {
                    isNoNeedSelect = false;
                }
//                if(selected == 0){
//                    disableMessageBox(true);
//                } else if(selected == 1){
//                    disableMessageBox(false);
//                }
            }
        } else if(itemFocused == UISettings.OPTIONS){
            if(yPosition >= (UISettings.formHeight-UISettings.footerHeight)){
                if(isNotDrag){
                    if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
                        keyPressed(UISettings.RIGHTOPTION);
                    } else if(UISettings.lOByte>-1 && xPosition<=(UISettings.formWidth/2)){
                        keyPressed(UISettings.LEFTOPTION);
                    }
                }
            }else if(CustomCanvas.isOptionSelected(xPosition, yPosition, 
                    isNotDrag, isPressed) && isNotDrag){ //CR 13030
                    isNoNeedSelect = true;
//                if(47 == CustomCanvas.getSelectedOption()){ //Option Index of symbol
//                    itemFocused = UISettings.SYMBOLS;
//                    handleSmartPopup(15); //Show Symbol pop-up
//                } else {
//                    itemFocused = 0;
//                    iPopupHandler.handleOptionSelected(CustomCanvas.getSelectedOption());
//                }
            }
        } else if(itemFocused == UISettings.NOTIFICATION){
            if(isNotDrag){
                if(yPosition >= (UISettings.formHeight-UISettings.footerHeight)){
                    if(UISettings.rOByte>-1 && xPosition>=(UISettings.formWidth/2)){
                        handlenotification(UISettings.RIGHTOPTION);
                    } else if(UISettings.lOByte>-1 && xPosition<=(UISettings.formWidth/2)){
                        handlenotification(UISettings.LEFTOPTION);
                    }
                }
            }
        }
        return isNoNeedSelect;
    }
    //#if KEYPAD
    //|JG|    private void handleSymbol(int keyCode){
    //|JG|        if(keyCode == UISettings.SYMBOLKEYMODE || keyCode == UISettings.RIGHTOPTION){
    //|JG|            symKey = 0;
    //|JG|            itemFocused = 0;
    //|JG|            iPopupHandler.handleSymbolpopup(' ', true,false);
    //|JG|        } else if(keyCode == UISettings.RIGHTARROW || keyCode == UISettings.LEFTARROW ||
    //|JG|                keyCode == UISettings.UPKEY || keyCode == UISettings.DOWNKEY){
    //|JG|            CustomCanvas.moveSymbolPosition(keyCode);
    //|JG|        } else if(keyCode == UISettings.FIREKEY || keyCode == UISettings.LEFTOPTION){
    //|JG|            symKey = 0;
    //|JG|            itemFocused = 0;
    //|JG|            iPopupHandler.handleSymbolpopup(CustomCanvas.getSelectedSymbol(), true,true);
    //|JG|        }
    //|JG|    }
    //#endif
    
    private void handleOption(int keyCode){
        if (keyCode == UISettings.RIGHTOPTION || keyCode == UISettings.LEFTOPTION) { // Load or UnLoad option PopUp
            itemFocused = 0;
            iPopupHandler.enablePreviousSelection();
        } else if (keyCode == UISettings.DOWNKEY) { //Select Next option Item
            CustomCanvas.traverseOptionsMenu(keyCode);
        } else if (keyCode == UISettings.UPKEY) { //Select Previous Option Item
            CustomCanvas.traverseOptionsMenu(keyCode);
        } else if (keyCode == UISettings.FIREKEY) { // UnLoad the Option Popup
            if(47 == CustomCanvas.getSelectedOption()){ //Option Index of symbol
                itemFocused = UISettings.SYMBOLS;
                handleSmartPopup(15); //Show Symbol pop-up
            } else { 
                itemFocused = 0;
                iPopupHandler.handleOptionSelected(CustomCanvas.getSelectedOption());
            }
        } else if(keyCode == UISettings.BACKKEY){
            if(UISettings.lOByte == 22){
                itemFocused = 0;
                iPopupHandler.handleOptionSelected(UISettings.lOByte);
            }
        } 
    }
    
    private void displaySmartPopup(int keyCode) {
        popupKey = (byte) keyCode;
        if (keyCode > 0) {
            if (keyCode == 1 || keyCode == 2 || keyCode == 3 || keyCode == 16) {
                if(keyCode == 2){
                    CustomCanvas.setPopupMessage(UISettings.IMPROPERENTRY);
                }
                if(keyCode == 3){
                  sPopupTimer = 5; //bug id 5401
                } else if(keyCode == 16){
                  sPopupTimer = 5;  
                } else sPopupTimer = 2;
                startTimer(sPopupTimer);
            } else if (keyCode == 4 || keyCode == 5 || keyCode == 6 || keyCode == 7 || 
                    keyCode == 8 || keyCode == 9 || keyCode == 10 || keyCode == 14 || 
                    keyCode == 19 || keyCode == 18 || keyCode == 21 || keyCode == 22
                    || keyCode == 23 || keyCode == 24 || keyCode == 25 || keyCode == 26) {
                msgKey = popupKey;
                msgSel = true;
                if(itemFocused != UISettings.NOTIFICATION)
                    itemFocused = UISettings.MESSAGEBOX;
                reLoadFooterMenu();
            } else if (keyCode == 11 || keyCode == 12 || keyCode== 13 || keyCode == 17) {
                itemFocused = UISettings.NOTIFICATION;
                reLoadFooterMenu();
            } else if(keyCode == 15){
                itemFocused = UISettings.SYMBOLS;
                symKey = 15;
                CustomCanvas.setSymbolPopupScreen();
                reLoadFooterMenu();
            }
        }
    }
    
    private void hideSmartPopup(int keyCode) {
        if (popupKey > 0) {
            if (popupKey == 1 || popupKey == 2 || popupKey == 3  || popupKey == 16) {
                sPopupTimer = -1;
                stopTimer();
//                if (popupKey != keyCode) {
//                    //CustomCanvas.popText = null;
//                }
                popupKey = 0;
            }
        }
    }
    
    private void handleMessagebox(int keyCode){
        if (UISettings.DOWNKEY == keyCode) {
            CustomCanvas.travelMessageBox(keyCode);
        } else if (UISettings.UPKEY == keyCode) {
            CustomCanvas.travelMessageBox(keyCode);
        } else if (UISettings.RIGHTOPTION == keyCode) {
            if (UISettings.rOByte > -1) {
                disableMessageBox(false);
            }
        } else if (UISettings.LEFTOPTION == keyCode) {
            if (UISettings.lOByte > -1) {
                disableMessageBox(true);
            }
        } else if (UISettings.LEFTARROW == keyCode) {
            if (UISettings.lOByte > -1) {
                msgSel = true;
            }
        } else if (UISettings.RIGHTARROW == keyCode) {
            if (UISettings.rOByte > -1) {
                msgSel = false;
            }
        } else if (UISettings.FIREKEY == keyCode) {
            disableMessageBox(msgSel);
        }
    }
    
    private void handlenotification(int keyCode){
        if (keyCode == UISettings.RIGHTOPTION) {
            if (UISettings.rOByte > -1) {
                disableNotification(false);
            }
        } else if (keyCode == UISettings.LEFTOPTION) {
            if (UISettings.lOByte > -1) {
                disableNotification(true);
            }
        }
    }
    
    public void handleSmartPopup(int poptype){
        hideSmartPopup(poptype);
        displaySmartPopup(poptype);
        ShortHandCanvas.IsNeedPaint();
    }
    
    private void disableNotification(boolean isSend){
        CustomCanvas.notText = null;
        popupKey = 1;
        boolean isReload = false;
        itemFocused = 0;
        if (msgKey > 1) {
            itemFocused = UISettings.MESSAGEBOX;
            reLoadFooterMenu();
        } else if(symKey>0){
            itemFocused = UISettings.SYMBOLS;
            reLoadFooterMenu();
        } else { isReload = true;}
        iPopupHandler.handleNotificationSelected(isReload, isSend);
    }
    
    private void disableMessageBox(boolean isSend){
        byte msgType = msgKey;
        msgKey = -1;
        popupKey = 1;
        itemFocused = 0;
        boolean isReload = false;
        if(symKey>0){
            itemFocused = UISettings.SYMBOLS;
            reLoadFooterMenu();
        } else {
            isReload = true;
        }
        CustomCanvas.resetMessageText();
        iPopupHandler.handleMessageBoxSelected(isSend,msgType,isReload);
    }
    
    private void startTimer(short time){
        stopTimer();
        sPopupTimer = time;
        if(null == popup_timer){
            popup_timer = new Timer();
            popup_timer.schedule(new popupClass(), sPopupTimer*1000);
        }
    }
    
    /*
     * 4. Message box With Ok Option
     * 5. Message Box With Ok and Cancel Option
     * 6. Message Box With Yes option
     * 7. Message Box with Yes and No Option
     * 8. Message Box with Accept and Cancel Option
     * 9. Message Box with Remind Me and Don't Remind Me Option
     * 10. Message Box with Now and Later Option
     * 14. Message box with Dismiss option
     * 16. Reserverd
     * 18. Message box with OK and Dismiss option
     * 19. Message box with Retry and Cancel option
     * 20. Reserverd
     * 21. Message box with Ok and Back option
     *  
     *
     * 11. Notification With goto option
     * 12. Notification with Dismiss option
     * 13. Notification With Goto and Dismiss option
     * 17. Notification With Chat and Dismiss option
     * 15. Symbols
     **/
    public void reLoadFooterMenu(){
        if(itemFocused == UISettings.MESSAGEBOX){
            if(msgKey == 4 || msgKey == 23){
                UISettings.lOByte = 7; //Okay Constants Index
                UISettings.rOByte = -1;
            } else if(msgKey == 5){
                UISettings.lOByte = 7; //Okay Constants Index
                UISettings.rOByte = 8; //Cancel Constants Index
            } else if(msgKey == 6){
                UISettings.lOByte = 43; //Yes Constants Index
                UISettings.rOByte = -1;
            } else if(msgKey == 7){
                UISettings.lOByte = 43; //Yes Constants Index
                UISettings.rOByte = 44; //No Constants Index
            } else if(msgKey == 8){
                UISettings.lOByte = 15; //Accept Constants Index
                UISettings.rOByte = 8; //Cancel Constants Index
            } else if(msgKey == 9){
                UISettings.lOByte = 11; //Remined me constants Index
                UISettings.rOByte = 12; //Don't remined Constatns Index
            } else if(msgKey == 10){
                UISettings.lOByte = 45; //Now constants Index
                UISettings.rOByte = 46; //Later Constants Index
                //Now Reserved for some otherOption
            } else if(msgKey == 14){
                UISettings.lOByte = 40; //Dismiss Constants Index
                UISettings.rOByte = -1;
            } else if(msgKey == 19 || msgKey == 24){
                UISettings.lOByte = 49; //Retry Constants Index
                UISettings.rOByte = 8; //Cancel constants Index
            } else if(msgKey == 18){
                UISettings.lOByte = 7; //Okay Constants Index
                UISettings.rOByte = 40; //Dismiss constants Index
            } else if(msgKey == 21) { //CR 12165
                UISettings.lOByte = 7; //Okay Constants Index
                UISettings.rOByte = 22; //Back constants Index
            } else if(msgKey == 22) { //
                UISettings.lOByte  = 51; //Reconnect Constants Index
                UISettings.rOByte = -1;
            } else if(msgKey == 25){
                UISettings.lOByte = 49; //Retry Constants Index
                UISettings.rOByte = -1; 
            }
   //<--CR 13332
            else if(msgKey == 26){
                UISettings.lOByte = 52;
                UISettings.rOByte = 8; //Cancel constants Index
            }
            //CR 13332-->

        } else if(itemFocused == UISettings.NOTIFICATION){
            if(popupKey == 11){
                UISettings.lOByte = 39; //Goto constants Index
                UISettings.rOByte = -1;
            } else if(popupKey == 12){
                UISettings.lOByte = -1; 
                UISettings.rOByte = 40; //Dismiss Constants Index
            } else if(popupKey == 13){
                UISettings.lOByte = 39; //Goto constants Index
                UISettings.rOByte = 40; //Dismiss Constants Index
            } else if(popupKey == 17){
                UISettings.lOByte = 50; //Chat Contanstas Index
                UISettings.rOByte = 40; //Dismiss Constants Index
            }
        } else if(itemFocused == UISettings.SYMBOLS){
            if(popupKey == 15){ //Symbol Pop-up not have any option key
                UISettings.lOByte = 7;
                UISettings.rOByte = 8;
            }
        }
    }
    
    public void deinitialize(){
        sPopupTimer = -1;
        stopTimer();
        stopMsgSendSpriteTimer();
        //CustomCanvas.popText = null; // Bug 6818 and 6621
        //CustomCanvas.notText = null;
        //CustomCanvas.resetMessageText();
        mSendImageRTimer = -1;
        itemFocused = 0;
        msgSel = false;
        msgKey = 0;
        popupKey = 0;
        symKey = 0;
    }
    
    public void rotatePopup(){
        if(itemFocused == UISettings.MESSAGEBOX || msgKey>1){
            CustomCanvas.rotateMessagebox();
        } 
        CustomCanvas.rotateNotificationPopup();
        CustomCanvas.rotateSymbolPopup();
        if (itemFocused == UISettings.OPTIONS) {
            CustomCanvas.rotateOptionMenu();
        }
    }
    
    public void unLoad(){
        iPopupHandler = null;
    }
    
    private void stopTimer(){
        sPopupTimer = -1;
        if(null != popup_timer){
            popup_timer.cancel();
            popup_timer = null;
        }

    }
    
    class popupClass extends TimerTask{
        public void run(){
            stopTimer();
            ShortHandCanvas.IsNeedPaint();
        }
    }

    class msgSendSpritTimer extends TimerTask{
        public void run(){
            if(isSendMessageSprite())
                ShortHandCanvas.IsNeedPaint();

        }
    }

}
