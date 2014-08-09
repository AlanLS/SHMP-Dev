//#if KEYPAD
//|JG|import generated.Build;
//|JG|import javax.microedition.lcdui.Canvas;
//#endif

import java.util.Timer;
import java.util.TimerTask;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hakuna
 */

public class DTMFHandler implements IKeyHandler{
    
    private boolean isNative = false;

    private int entryType = 0;
    private int tvar = 0;

    //#if KEYPAD
    //|JG|    private final String[] aKeys = {"abc","def","ghi","jkl","mno","pqrs","tuv","wxyz"};
    //|JG|
    //|JG|    private final String[] aNKeys = {"abc2","def3","ghi4","jkl5","mno6","pqrs7","tuv8","wxyz9"};
    //|JG|
    //|JG|    private String[] keys = null;
    //|JG|
    //|JG|    private byte alphaStIndex = 50;
    //|JG|
    //|JG|    private String retText ="";
    //|JG|
    //|JG|    private int keyMajor = -1;
    //|JG|
    //|JG|    private int keyMinor = 0;
    //|JG|
    //|JG|    private boolean isNotFinish = false;
    //|JG|
    //|JG|    private Timer keyTimer = null;
    //|JG|
    //|JG|    private Timer searchStringTimer = null;
    //|JG|
    //|JG|    private boolean isNotquery = true;
    //|JG|
    //|JG|    private String charvalue="";
    //|JG|
    //|JG|    private byte kmode = IKeyHandler.MODE_abc;
    //|JG|
    //|JG|    private short keyTime = -1;
    //|JG|
    //|JG|    private short searchTime = -1;
    //|JG|
    //|JG|    private String stext="",stText="", rnText = "", rntText = "";
    //|JG|
    //#endif

    private Timer textBoxBlinkTimer = null;

    private short currsorTime = -1;

    private String kModeStr = "abc";

    private String text = "", tText = "";
    private int minChar, maxChar;
    private float minValue, maxValue;
    public char cursorChar = '|';
    private byte itemFocused;
    private String  mask = null;
    private int  lCount = -1;
    
    
    private int tbcurpos = 0,  rtbcurPos = 0;//  ptbcurpos = 0;
    
    private boolean isBlink = true;
    
    
    
    
    /**
     * 0 - 123
     * 1 - abc
     * 2 - ABC
     **/
    
    
    private ICanvasHandler iCanvasHandler = null;
    
    
    private String value = "";

    //#if KEYPAD
    //|JG|    public boolean isQWERTY(){
    //|JG|        return false;
    //|JG|    }
    //#endif
    
    public void setCanvasHandler(ICanvasHandler entry) {
        iCanvasHandler = entry;
    }

    //CR number 5430
    public void updateKeyTimer(){
        //#if KEYPAD
        //|JG| if(keyTime == 0){
        //|JG|    keyConformed();
        //|JG| }
        //#endif
        if(currsorTime == 0){
            toggleCursor();
            currsorTime = 1;
        }
    }


    //#if KEYPAD
    //|JG|    //Cr number 5430
    //|JG|    public void updateSearchTimer(){
    //|JG|        if(searchTime == 0)
    //|JG|            resetsearchString();
    //|JG|    }
    //|JG|
    //|JG|    private void keyTimerStop(){
    //|JG|        if(null != keyTimer){
    //|JG|            keyTimer.cancel();
    //|JG|            keyTimer = null;
    //|JG|        }
    //|JG|        keyTime = -1;
    //|JG|    }
    //|JG|
    //|JG|    private void keyTimerStart(){
    //|JG|        //keyTime = Build.KEY_CONFORM_TIMER;
    //|JG|        keyTimer = new Timer();
    //|JG|        keyTimer.schedule(new KeyConfirmer(), 1000);
    //|JG|        keyTime = 1;
    //|JG|    }
    //|JG|
    //|JG|    private void searchTimerStop(){
    //|JG|        if(null != searchStringTimer){
    //|JG|            searchStringTimer.cancel();
    //|JG|            searchStringTimer = null;
    //|JG|        }
    //|JG|        searchTime = -1;
    //|JG|    }
    //|JG|
    //|JG|    private void searchTimerStart(){
    //|JG|        searchTimerStop();
    //|JG|        searchStringTimer = new Timer();
    //|JG|        searchStringTimer.schedule(new SearchMenuCompletionTimer(), 2000);
    //|JG|        searchTime = 1;
    //|JG|//        searchTime = (short)Build.SEARCH_TIME;
    //|JG|    }
    //#endif
    
    private void toogleTimerStop(){
        currsorTime = -1;
        if (null != textBoxBlinkTimer) {
            textBoxBlinkTimer.cancel();
            textBoxBlinkTimer = null;
         }
    }

    private void toogleTimerStart(){
        //currsorTime = (short)Build.CURSOR_TIME;
        toogleTimerStop();
        textBoxBlinkTimer = new Timer();
        textBoxBlinkTimer.schedule(new TextBoxCursorBlink(), 300, 300);
        currsorTime = 1;
    }
    
    
    public void setEntryProperty(int minChar, int maxChar,
            float minValue, float maxValue, String mask,
            int lount,int entryType,boolean isNative,boolean isQuery){
        //#if KEYPAD
        //|JG|        isNotquery = isQuery;
        //#endif
        this.minChar = minChar;
        this.maxChar = maxChar;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.mask = mask;
        this.lCount = lount;
        this.entryType = entryType;
        this.isNative = isNative;
        
        if(entryType == IKeyHandler.ALPHA){
                    //#if KEYPAD
                    //|JG|            kmode = IKeyHandler.MODE_abc;
                    //|JG|            keys = aKeys;
                    //#endif
            kModeStr ="abc";

        } else if(entryType == IKeyHandler.ALPHANUMERIC){
            //#if KEYPAD
            //|JG|            kmode = IKeyHandler.MODE_abc;
            //|JG|            keys = aNKeys;
            //#endif
            kModeStr ="abc";
        } else {
            //#if KEYPAD
            //|JG|            kmode = IKeyHandler.MODE_123;
            //#endif
            kModeStr ="123";
        }
        if (entryType == 6) {
            this.minChar = 1;
            this.maxChar = 11;
            this.minValue = 1;
            this.maxValue = 9999999;
        }
    }
    
    public boolean isMask(){
        if(null != mask)
            return true;
        return false;
    }
    
    public void EntryTextBoxReset() {
        text = "";
        tText = "";
        value = "";
        tbcurpos = 0;
    }

    //#if KEYPAD
    //|JG|    public void RenametextBoxReset(){
    //|JG|        rnText = "";
    //|JG|        rntText = "";
    //|JG|        rtbcurPos = 0;
    //|JG|    }
    //|JG|    
    //|JG|    public void SearchValueReset(){
    //|JG|        resetsearchString();
    //|JG|     }
    //|JG|
    //|JG|    public int getRenameTextCursorPos(){
    //|JG|        return rtbcurPos;
    //|JG|    }
//#endif
    public int getTextboxCursorPos(){
        return tbcurpos;
    }
    
    
    
    public void SetItemFocused(byte itemFocus){
        itemFocused = itemFocus;
    }
    
    
    
    public void addTextboxCurPos(int value){
        tbcurpos += value;
    }
    
    //#if KEYPAD
    //|JG|          public void addRenameTextboxCurPos(int value){
    //|JG|               rtbcurPos += value; 
    //|JG|            }
    //|JG|
    //|JG|    public String getRenameText(){
    //|JG|        return rnText;
    //|JG|    }
    //|JG|
    //|JG|    public void setRenameText(String value){
    //|JG|        rnText = value;
    //|JG|    }
    //|JG|
    //|JG|    public String getRenameTempText(){
    //|JG|        return rntText;
    //|JG|    }
    //|JG|
    //|JG|    public void setRenameTempText(String value){
    //|JG|        rntText = value;
    //|JG|    }
    //#endif

    public String getEntryText(){
        if(null != mask)
            return value;
        return text;
    }
    
    public void setEntryText(String value){
        text = value;
    }
    
    
    public String getEntryTempText(){
        return tText;
    }
    
    public void setEntryTempText(String value){
        tText = value;
    }

    public String getKeyMode(){
        return kModeStr;
    }
    //#if KEYPAD
    //|JG|    public String getSearchTempText(){
    //|JG|         return stText.toLowerCase();
    //|JG|     }
    //|JG|
    //|JG|     public String getSearchText(){
    //|JG|         return stext.toLowerCase();
    //|JG|     }
    //|JG|     
    //|JG|
    //|JG|    // Key Conformed
    //|JG|     
    //|JG|    public void keyConformed() {
    //|JG|        boolean isNotKey = false;
    //|JG|        try{
    //|JG|            keyTimerStop();
    //|JG|            if (retText.length()>0 && isNotFinish && retText.length()>keyMinor) {
    //|JG|                searchTimerStop();
    //|JG|                char keyChar = retText.charAt(keyMinor);
    //|JG|                if (kmode == IKeyHandler.MODE_ABC) {
    //|JG|                    keyChar = Character.toUpperCase(keyChar);
    //|JG|                }
    //|JG|                int ipos = 0;
    //|JG|                if (UISettings.TEXTBOX == itemFocused) {
    //|JG|                    if(tbcurpos>0)
    //|JG|                        ipos = tbcurpos-1;
    //|JG|                    if (isNotquery || '$' != keyChar) {
    //|JG|                        text = appendValue(text, keyChar + "", ipos);
    //|JG|                    } else {
    //|JG|                        isNotKey = true;
    //|JG|                        tbcurpos = ipos;
    //|JG|                    }
    //|JG|                    tText = text;
    //|JG|                    ApplyFormat(true);
    //|JG|                } else if (UISettings.RENAMETEXTBOX == itemFocused) {
    //|JG|                    if(rtbcurPos>0)
    //|JG|                        ipos = rtbcurPos - 1;
    //|JG|                    if(isNotquery || '$' != keyChar){
    //|JG|                        rnText = appendValue(rnText, keyChar + "", ipos);
    //|JG|                    } else {
    //|JG|                        isNotKey = true;
    //|JG|                        rtbcurPos = ipos;
    //|JG|                    }
    //|JG|                    rntText = rnText;
    //|JG|                } else if(UISettings.SEARCH == itemFocused){
    //|JG|                    stext += keyChar;
    //|JG|                    stText = stext;
    //|JG|                    searchTimerStop();
    //|JG|                    searchTimerStart();
    //|JG|                }
    //|JG|            }
    //|JG|        }catch(Exception e) { }
    //|JG|        isNotFinish = false;
    //|JG|        keyMajor = -1;
    //|JG|        keyMinor = 0;
    //|JG|        retText = "";
    //|JG|        isBlink = true;
    //|JG|        if (isNotKey) {
    //|JG|            iCanvasHandler.handleSmartPopup(2);
    //|JG|        }
    //|JG|    }
    //|JG|
    //|JG|    
    //|JG|    private void resetsearchString() {
    //|JG|        keyTimerStop();
    //|JG|        searchTimerStop();
    //|JG|        retText = "";
    //|JG|        keyMinor = 0;
    //|JG|        keyMajor = -1;
    //|JG|        isBlink = true;
    //|JG|        isNotFinish = false;
    //|JG|        stext = stText = "";
    //|JG|    }
    //|JG|
    //|JG|
    //|JG|    private String getKeyCodeValue(int keyCode){
    //|JG|        searchTimerStop();
    //|JG|        if(keyCode != keyMajor){
    //|JG|            keyConformed();
    //|JG|            keyMajor = keyCode;
    //|JG|            keyMinor = 0;
    //|JG|            if(keyCode == UISettings.SPACEKEYCODE)
    //|JG|                retText = UISettings.SPACETEXT;
    //|JG|            else if(keyCode == Canvas.KEY_NUM1){
    //|JG|                retText = UISettings.SYMBOLTEXT;
    //|JG|            } else if(keyCode == UISettings.SPECIALSYMBOLSCODE)
    //|JG|                retText = UISettings.SPECIALSYMBOLS;
    //|JG|            else if(keys.length>keyCode)
    //|JG|                retText = keys[keyCode];
    //|JG|            else {
    //|JG|                isBlink = true;
    //|JG|                return "";
    //|JG|            }
    //|JG|            isNotFinish = true;
    //|JG|            if (UISettings.RENAMETEXTBOX == itemFocused) {
    //|JG|                rtbcurPos++;
    //|JG|            } else if (UISettings.TEXTBOX == itemFocused) {
    //|JG|                tbcurpos++;
    //|JG|            }
    //|JG|        } else {
    //|JG|            keyTimerStop();
    //|JG|            isBlink = true;
    //|JG|            keyMinor++;
    //|JG|            if (retText.length() <= keyMinor) {
    //|JG|                keyMinor = 0;
    //|JG|            }
    //|JG|        }
    //|JG|        if(retText.length() == 1){
    //|JG|            keyMajor = -1;
    //|JG|            isBlink = true;
    //|JG|        } else isBlink = false;
    //|JG|        keyTimerStart();
    //|JG|        if(retText.length()>keyMinor)
    //|JG|            return retText.charAt(keyMinor) + "";
    //|JG|        return "";
    //|JG|    }
    //|JG|
    //|JG|    /**
    //|JG|     * Method to get he numeric value for the give key code
    //|JG|     *
    //|JG|     * @param keyCode Key Code
    //|JG|     * @return String Numeric value of the key code.
    //|JG|     */
    //|JG|    private String getNumericValue(int keyCode) {
    //|JG|        if (keyCode > 47 && keyCode < 58) {
    //|JG|            searchTimerStop();
    //|JG|            keyCode = keyCode - 48;
    //|JG|            return keyCode + "";
    //|JG|        } else {
    //|JG|            if(keyCode != Canvas.KEY_STAR && keyCode != Canvas.KEY_POUND)
    //|JG|                iCanvasHandler.handleSmartPopup(2); //Improper entry
    //|JG|        }
    //|JG|        return "";
    //|JG|    }
    //|JG|
    //|JG|    private String getKeyValue(int keyCode) {
    //|JG|        charvalue = "";
    //|JG|        try{
    //|JG|            if (UISettings.SUBMODE != 0 && keyCode == UISettings.SUBMODE && kmode != IKeyHandler.MODE_123) {
    //|JG|                keyConformed();
    //|JG|                changeMode(keyCode);
    //|JG|            } else if (keyCode == UISettings.MAINMODE) {
    //|JG|                if (kmode != IKeyHandler.MODE_123) {
    //|JG|                    keyConformed();
    //|JG|                } else if(UISettings.SEARCH == itemFocused){
    //|JG|                    searchTimerStop();
    //|JG|                    searchTimerStart();
    //|JG|                }
    //|JG|                changeMode(keyCode);
    //|JG|            } else if (kmode == IKeyHandler.MODE_123) {
    //|JG|                charvalue = getNumericValue(keyCode);
    //|JG|            } else {
    //|JG|                if(keyCode == Canvas.KEY_NUM1){
    //|JG|                    if(entryType == IKeyHandler.ALPHA){
    //|JG|                        keyConformed();
    //|JG|                        //bug id 4874
    //|JG|                        //iCanvasHandler.handleSmartPopup(2);
    //|JG|                    } else charvalue = getKeyCodeValue(keyCode);
    //|JG|                } else if(keyCode == UISettings.SPACEKEYCODE){
    //|JG|                    if(entryType == IKeyHandler.ALPHA){
    //|JG|                        UISettings.SPACETEXT = " ";
    //|JG|                    } else UISettings.SPACETEXT = Build.SPACE_TEXT;
    //|JG|                        charvalue = getKeyCodeValue(keyCode);
    //|JG|                } else if(UISettings.SPECIALSYMBOLSCODE > 0 && keyCode == UISettings.SPECIALSYMBOLSCODE){
    //|JG|                  if(entryType == IKeyHandler.ALPHA)  {
    //|JG|                      keyConformed();
    //|JG|                      iCanvasHandler.handleSmartPopup(2);
    //|JG|                  } else charvalue = getKeyCodeValue(keyCode);
    //|JG|                } else {
    //|JG|                    keyCode = keyCode - alphaStIndex;
    //|JG|                    if(keyCode>-1){
    //|JG|                        charvalue = getKeyCodeValue(keyCode);
    //|JG|                        if (kmode == IKeyHandler.MODE_ABC) {
    //|JG|                            charvalue = charvalue.toUpperCase();
    //|JG|                        }
    //|JG|                    }else keyConformed();
    //|JG|                }
    //|JG|            }
    //|JG|        }catch(Exception e){Logger.loggerError("Error Occured getKeyValue "+ e.toString() +" temptext "+tText+" text "+text+" currsorpos "+tbcurpos); }
    //|JG|        return charvalue;
    //|JG|    }
    //|JG|
    //|JG|    public void changeMode(int keyCode) {
    //|JG|        kmode++;
    //|JG|        if(UISettings.SUBMODE == 0){
    //|JG|            if (itemFocused == UISettings.RENAMETEXTBOX || entryType == IKeyHandler.ALPHANUMERIC ) {
    //|JG|                if(kmode > IKeyHandler.MODE_ABC)
    //|JG|                    kmode = IKeyHandler.MODE_123;
    //|JG|            } else if(entryType == IKeyHandler.ALPHA) {
    //|JG|                if(kmode > IKeyHandler.MODE_ABC)
    //|JG|                    kmode = IKeyHandler.MODE_abc;
    //|JG|                else if(kmode == IKeyHandler.MODE_123)
    //|JG|                    kmode = IKeyHandler.MODE_abc;
    //|JG|            }
    //|JG|        } else {
    //|JG|            if (itemFocused == UISettings.RENAMETEXTBOX || entryType == IKeyHandler.ALPHANUMERIC) {
    //|JG|                if (keyCode == UISettings.SUBMODE) {
    //|JG|                    if(kmode > IKeyHandler.MODE_ABC)
    //|JG|                        kmode = IKeyHandler.MODE_abc;
    //|JG|                    else if(kmode == IKeyHandler.MODE_123)
    //|JG|                        kmode = IKeyHandler.MODE_abc;
    //|JG|                } else if (kmode > IKeyHandler.MODE_abc) {
    //|JG|                    kmode = IKeyHandler.MODE_123;
    //|JG|                }
    //|JG|            } else if (entryType == IKeyHandler.ALPHA) {
    //|JG|                if(keyCode == UISettings.SUBMODE){
    //|JG|               if(kmode > IKeyHandler.MODE_ABC)
    //|JG|                    kmode = IKeyHandler.MODE_abc;
    //|JG|                else if(kmode == IKeyHandler.MODE_123)
    //|JG|                    kmode = IKeyHandler.MODE_abc;
    //|JG|                }
    //|JG|                else kmode--;
    //|JG|            }
    //|JG|        }
    //|JG|        setKeyMode();
    //|JG|    }
    //|JG|
    //|JG|    /**
    //|JG|     * Method to set the key mode in the view.
    //|JG|     */
    //|JG|    public void setKeyMode() {
    //|JG|        if (itemFocused == UISettings.RENAMETEXTBOX || itemFocused == UISettings.TEXTBOX || itemFocused == UISettings.SEARCH) {
    //|JG|            if (entryType == IKeyHandler.ALPHA || entryType == IKeyHandler.ALPHANUMERIC ||
    //|JG|                    itemFocused == UISettings.RENAMETEXTBOX) {
    //|JG|                if (kmode == IKeyHandler.MODE_123) {
    //|JG|                    kModeStr = "123";
    //|JG|                } else if (kmode == IKeyHandler.MODE_abc) {
    //|JG|                    kModeStr = "abc";
    //|JG|                } else if (kmode == IKeyHandler.MODE_ABC) {
    //|JG|                    kModeStr = "ABC";
    //|JG|                }
    //|JG|            } else {
    //|JG|                kModeStr = "123";
    //|JG|                kmode = IKeyHandler.MODE_123;
    //|JG|            }
    //|JG|        } else {
    //|JG|            kModeStr = "";
    //|JG|        }
    //|JG|    }
    //|JG|
    //|JG|    public boolean handleSearchText(int keyCode){
    //|JG|        boolean isSearch = false;
    //|JG|        try{
    //|JG|            if(keyCode>-1){
    //|JG|               stText = getKeyValue(keyCode);
    //|JG|               if(stText.length() > 0){
    //|JG|                    isSearch = true;
    //|JG|                    if (kmode == IKeyHandler.MODE_123) {
    //|JG|                        stext += stText;
    //|JG|                        stText = stext;
    //|JG|                        searchTimerStop();
    //|JG|                        searchTimerStart();
    //|JG|                    } else stText = stext + stText;
    //|JG|               } else {
    //|JG|                   stText = stext;
    //|JG|               }
    //|JG|            }
    //|JG|        }catch(Exception e){}
    //|JG|        return isSearch;
    //|JG|    }
    //|JG|
    //#endif

    public void handleInputForTextBox(int keyCode) {
          try{
              if(keyCode>-1){
    //#if KEYPAD
    //|JG|            if(isNative){
    //#endif
                        invokeNativeTextbox();
    //#if KEYPAD
    //|JG|            }
    //|JG|            else  if(entryType == IKeyHandler.DATE){
    //|JG|                iCanvasHandler.showDateForm();
    //|JG|            } else {
    //|JG|                int lco = 0;
    //|JG|                if (lCount > -1) {
    //|JG|                    lco = lCount;
    //|JG|                }
    //|JG|                if (text.length() < maxChar && (lco + text.length()) < UISettings.MAX_COUNT) {
    //|JG|                    if (entryType == IKeyHandler.NUMERIC || entryType == IKeyHandler.PHONENUMBER || entryType == IKeyHandler.DOLLARCENTS || entryType == IKeyHandler.DECIMAL) {
    //|JG|                        handleInputForNumericMode(keyCode);
    //|JG|                    } else {
    //|JG|                        handleAlphaValue(keyCode);
    //|JG|                    }
    //|JG|                } else {
    //|JG|                   keyTimerStop();
    //|JG|                   isBlink = true;
    //|JG|                    if ((entryType == IKeyHandler.NUMERIC || entryType == IKeyHandler.PHONENUMBER || entryType == IKeyHandler.DOLLARCENTS || entryType == IKeyHandler.DECIMAL) ){//&& keyCode != UISettings.MAINMODE) {
    //|JG|                        if(keyCode != Canvas.KEY_STAR && keyCode != Canvas.KEY_POUND)
    //|JG|                        iCanvasHandler.handleSmartPopup(2);
    //|JG|                    } //ImproperEntry SmartPopup;
    //|JG|                    else {
    //|JG|                        if (keyCode == UISettings.SUBMODE && kmode != IKeyHandler.MODE_123) {
    //|JG|                            keyConformed();
    //|JG|                            changeMode(keyCode);
    //|JG|                        } else if (keyCode == UISettings.MAINMODE) {
    //|JG|                            if (kmode != IKeyHandler.MODE_123) {
    //|JG|                                keyConformed();
    //|JG|                            }
    //|JG|                            changeMode(keyCode);
    //|JG|                        } else {
    //|JG|                            iCanvasHandler.handleSmartPopup(2);
    //|JG|                        } //ImproperEntry SmartPopup;
    //|JG|                    }
    //|JG|                }
    //|JG|                if (text.length() < 2) {
    //|JG|                    iCanvasHandler.reLoadFooterMenu();
    //|JG|                }
    //|JG|            }
    //#endif
              }
            }catch(Exception e){}
    }
    //#if KEYPAD
    //|JG|    private void handleAlphaValue(int keyCode) {
    //|JG|        int ipos = lCount;
    //|JG|        if (ipos == -1) {
    //|JG|            ipos = 0;
    //|JG|        }
    //|JG|        int npos = tbcurpos;
    //|JG|        charvalue = getKeyValue(keyCode);
    //|JG|        if (text.length() < maxChar && (ipos + text.length()) < UISettings.MAX_COUNT) {
    //|JG|            ipos = tbcurpos;
    //|JG|            if (charvalue.length() > 0) {
    //|JG|                if (kmode == IKeyHandler.MODE_123) {
    //|JG|                    text = appendValue(text, charvalue, ipos);
    //|JG|                    tText = text;
    //|JG|                    tbcurpos++;
    //|JG|                } else {
    //|JG|                    if (ipos == tbcurpos) {
    //|JG|                        ipos--;
    //|JG|                    }
    //|JG|                    tText = appendValue(text, charvalue, ipos);// text + tText;
    //|JG|                }
    //|JG|            } else {
    //|JG|                tText = text;
    //|JG|            }
    //|JG|            ApplyFormat(true);
    //|JG|        } else {
    //|JG|            String tep = text;
    //|JG|            keyConformed();
    //|JG|            text = tText = tep;
    //|JG|            tbcurpos = npos;
    //|JG|            ApplyFormat(true);
    //|JG|            iCanvasHandler.handleSmartPopup(2); //ImproperEntry SmartPopup;
    //|JG|        }
    //|JG|    }
    //|JG|
    //|JG|    private String appendValue(String orgvalue, String aValue, int iPos) {
    //|JG|        if (iPos >= orgvalue.length()) {
    //|JG|            orgvalue += aValue;
    //|JG|        } else {
    //|JG|            orgvalue = orgvalue.substring(0, iPos) + aValue + orgvalue.substring(iPos);
    //|JG|        }
    //|JG|        return orgvalue;
    //|JG|    }
    //|JG|
    //|JG|
    //|JG|    private void handleInputForNumericMode(int keyCode) {
    //|JG|        if (keyCode > 47 && keyCode < 58) {
    //|JG|            int t = tbcurpos - getAppendOrDeletePos();
    //|JG|            if (t < 0) {
    //|JG|                t = 0;
    //|JG|            }
    //|JG|            String tem = appendValue(text, (keyCode - 48) + "", t);
    //|JG|            if (CheckForValidMaxValue(tem)) {
    //|JG|                text = tText = tem;
    //|JG|                ApplyFormat(true);
    //|JG|                if (text.length() == 1) {
    //|JG|                    tbcurpos = tText.length();
    //|JG|                } else {
    //|JG|                    if (tbcurpos != tText.length()) {
    //|JG|                        setNumericCursorPos(1, 0);
    //|JG|                        tbcurpos++;
    //|JG|                    }
    //|JG|                }
    //|JG|                tText = text;
    //|JG|                ApplyFormat(true);
    //|JG|            } else if(keyCode!= Canvas.KEY_STAR && keyCode!=Canvas.KEY_POUND) {
    //|JG|                tText = text;
    //|JG|                ApplyFormat(true);
    //|JG|                iCanvasHandler.handleSmartPopup(2); //ImproperEntry SmartPopup
    //|JG|            }
    //|JG|        }
    //|JG|    }
    //|JG|
    //|JG|         /**
    //|JG|     * Checks For MinValue and returns true if Value falls within range else
    //|JG|     * false. This method checks for both the maximum character and maximum
    //|JG|     * value
    //|JG|     * 
    //|JG|     * @param text Input text
    //|JG|     * @param boolean  <li> true - if it satisfies the condtion </li>
    //|JG|     *                 <li> false - if it doesn't </li>
    //|JG|     */
    //|JG|    private boolean CheckForValidMaxValue(String text) {
    //|JG|        try {
    //|JG|            boolean isValid = true;
    //|JG|            double textValue = 0;
    //|JG|            text = text.trim();
    //|JG|
    //|JG|            if (entryType == IKeyHandler.NUMERIC || entryType == IKeyHandler.DECIMAL || entryType == IKeyHandler.DOLLARCENTS) {
    //|JG|                if (text.length() > 0) {
    //|JG|                    textValue = Double.parseDouble(text);
    //|JG|                }
    //|JG|                Logger.debugOnError(textValue+" "+maxValue+" "+text+" "+maxChar);
    //|JG|                if (textValue > maxValue || text.length() > maxChar) {
    //|JG|                    isValid = false;
    //|JG|                }
    //|JG|            } else if (entryType == IKeyHandler.ALPHANUMERIC || entryType == IKeyHandler.ALPHA) {
    //|JG|                if (text.length() > maxChar) {
    //|JG|                    isValid = false;
    //|JG|                } else if (entryType == IKeyHandler.PHONENUMBER) {
    //|JG|                    if (text.startsWith("1")) {
    //|JG|                        if (text.length() > 10) {
    //|JG|                            isValid = false;
    //|JG|                        }
    //|JG|                    } else {
    //|JG|                        if (text.length() > 9) {
    //|JG|                            isValid = false;
    //|JG|                        }
    //|JG|                    }
    //|JG|                }
    //|JG|            }
    //|JG|            return isValid;
    //|JG|        } catch (Exception e) {
    //|JG|            return false;
    //|JG|        }
    //|JG|    }
//#endif
    public boolean isAlphaMaxCheck(int count){
        if(text.length() >= minChar && text.length() <= maxChar){
            if (mask != null && mask.length() > 0) {
                value = Utilities.encryptEntryValue(mask, text);
            }
            return true;
        } 
        return false;
    }
    
    public boolean isNumericCheck(int count){
        double textValue = 0;
        if (text.length() > 0 && (count + text.length()) <= UISettings.MAX_COUNT) {
            textValue = Double.parseDouble(text);
        }
        if (text.length() >= minChar && textValue >= minValue && (count + text.length()) <= UISettings.MAX_COUNT) {
            ApplyFormat(true);
            if (mask != null && mask.length() > 0) {
                value = Utilities.encryptEntryValue(mask, text);
            }
            return true;
        } 
        return false;
    }
    
    public boolean isPhoneCheck(){ //bug id 6516
        //if((text.length() == 10 && !text.startsWith("1"))||  (text.startsWith("1") && text.length() == 11)){
        if(text.length()>6 && text.length()<12){
            if (mask != null && mask.length() > 0) {
                value = Utilities.encryptEntryValue(mask, text);
            }
            return true;
        }
        //}
        return false;
    }
        
    private int getAppendOrDeletePos() {
        String tem = null;
        if (tText.length() > tbcurpos) {
            tem = tText.substring(0, tbcurpos);
        } else {
            tem = tText;
        }
        int t = 0;
        int ipos = 0;
        if (IKeyHandler.PHONENUMBER == entryType) {
            while ((t = tem.indexOf("-", t)) > -1) {
                ipos++;
                t++;
            }
        } else if (IKeyHandler.DOLLARCENTS == entryType || entryType == IKeyHandler.DECIMAL) {
            while ((t = tem.indexOf(",", t)) > -1) {
                ipos++;
                t++;
            }
            if (tem.indexOf(".") > -1) {
                ipos++;
            }
            if (tem.indexOf("$") > -1) {
                ipos++;
            }
            if (text.length() == 2) {
                ipos++;
            } else if (text.length() == 1) {
                ipos += 2;
            }
        }
        return ipos;
    }
    
    public void ApplyFormat(boolean isTextbox) {
        String oValue = null;
        String tValue = null;
        int cPosition = 0;

        //#if KEYPAD
        //|JG|        if(itemFocused == UISettings.RENAMETEXTBOX){
        //|JG|            oValue = rnText;
        //|JG|            tValue = rntText;
        //|JG|            cPosition = rtbcurPos;
        //|JG|        } else
        //#endif
        {
            oValue = text;
            tValue = tText;
            if(isTextbox)
                cPosition = tbcurpos;
            else cPosition = -1;
        }
        if ((entryType == IKeyHandler.DOLLARCENTS || entryType == IKeyHandler.DECIMAL)) {
            if (oValue.length() > 0) {
                tValue = MoneyFormat(oValue);
            }
        }
        else if (entryType == IKeyHandler.PHONENUMBER) {
            //StringBuffer sb = new StringBuffer(oValue); // CR id 6366
            //if (oValue.startsWith("1")) { //bug id 6516
                maxChar = 11;
//                if (oValue.length() > 1) {
//                    sb = sb.insert(1, '-');
//                }
//                if (oValue.length() > 4) {
//                    sb = sb.insert(5, '-');
//                }
//                if (oValue.length() > 7) {
//                    sb = sb.insert(9, '-');
//                }
            //} else {
              //  maxChar = 10;
//                if (oValue.length() > 3) {
//                    sb = sb.insert(3, '-');
//                }
//                if (oValue.length() > 6) {
//                    sb = sb.insert(7, '-');
//                }
            //}
//            tValue = sb.toString();
//            sb = null;
        } 
        else if (null != mask && tValue.length()>0) {
            int len = tValue.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                if (mask.compareTo("*") == 0 || mask.charAt(i) == '*') {
                    if ((cPosition == 0 && i == 0) || i == cPosition - 1) {
                        sb.append(tValue.charAt(i));
                    } else {
                        sb.append("*");
                    }
                } else if (Character.isDigit(mask.charAt(i))) {
                    sb.append(tValue.charAt(i));
                }
            }
            tValue = sb.toString();
        }
        //#if KEYPAD
        //|JG|        if(itemFocused == UISettings.RENAMETEXTBOX){
        //|JG|            rntText = tValue;
        //|JG|        } else
            //#endif
        {
            tText = tValue;
        }
    }

    /**
     * Method to apply the money format on the input value
     * 
     * @param value  Input value
     * @param String Modified value based on the money format
     */
    private String MoneyFormat(String value) {
        int length = value.length();
        StringBuffer val = new StringBuffer(value);
        if (length > 5) {
            int pos = 3;
            length -= 2;
            while (length > pos) {
                val.insert(length - pos, ",");
                pos += 3;
            }
        } else {
            if (length == 1) {
                val.insert(0, "00");
            } else if (length == 2) {
                val.insert(0, "0");
            }
        }
        val.insert(val.length() - 2, ".");
        if (entryType == IKeyHandler.DOLLARCENTS) {
            val.insert(0, "$");
        }
        return val.toString();
    }

    public void copyTextToTextBox(String txt) {
        text = tText = "";
        tbcurpos = 0;
        if (txt != null) {
            text = tText = txt;
            tbcurpos = text.length();
        }
        isBlink = true;
    }

    //#if KEYPAD
    //|JG|    public void copyTexttoRenameTextBox(String txt){
    //|JG|        rnText = rntText = "";
    //|JG|        rtbcurPos = 0;
    //|JG|        if(null != txt){
    //|JG|            rnText = rntText = txt;
    //|JG|            rtbcurPos = rnText.length();
    //|JG|        }
    //|JG|        isBlink = true;
    //|JG|    }
    //|JG|    
    //|JG|    public void addTextbox() {
    //|JG|        toogleTimerStop();
    //|JG|        toogleTimerStart();
    //|JG|    }
    //#endif
    
    public char getKeyChar(){
        return cursorChar;
    }
    
    public void toggleCursor() {
            if(isBlink){
                if (cursorChar == ' ') {
                    cursorChar = '|';
                } else {
                    cursorChar = ' ';
                }
            } else {
                cursorChar = ' ';
            }
        }
    
    //#if KEYPAD
    //|JG|     public void handleRenameTextKey(int keyCode) {
    //|JG|         try{
    //|JG|             if(keyCode>-1){
    //|JG|                int ipos = rtbcurPos;
    //|JG|                rntText = getKeyValue(keyCode);
    //|JG|                if(rnText.length()<30){
    //|JG|                    if (rnText.length() == 0) {
    //|JG|                        iCanvasHandler.reLoadFooterMenu();
    //|JG|                    }
    //|JG|                    if (kmode == IKeyHandler.MODE_123) {
    //|JG|                        if(rntText.length()>0){
    //|JG|                            rnText = appendValue(rnText, rntText, ipos);
    //|JG|                            rtbcurPos++;
    //|JG|                        }
    //|JG|                        rntText = rnText;
    //|JG|                    } else {
    //|JG|                        if (rntText.length() > 0 && ipos == rtbcurPos) {
    //|JG|                            ipos--;
    //|JG|                        }
    //|JG|                        rntText = appendValue(rnText, rntText, ipos);
    //|JG|                    }
    //|JG|                } else {
    //|JG|                    String temp = rnText;
    //|JG|                    keyConformed();
    //|JG|                    rntText = rnText = temp;
    //|JG|                    if(temp.length()<ipos)
    //|JG|                        rtbcurPos = temp.length();
    //|JG|                    else rtbcurPos = ipos;
    //|JG|                    iCanvasHandler.handleSmartPopup(2);
    //|JG|                }
    //|JG|             }
    //|JG|         }catch(Exception e){}
    //|JG|    }
    //|JG|
    //|JG|     public void setRenameTextValue(String value){
    //|JG|        toogleTimerStop();
    //|JG|        toogleTimerStart();
    //|JG|        rnText = rntText = value;
    //|JG|        rtbcurPos = rnText.length();
    //|JG|        kmode = IKeyHandler.MODE_abc;
    //|JG|     }
    //|JG|
    //#endif

     public void clearCharcters(byte itemFocus) {
         //#if KEYPAD
         //|JG|        keyConformed();
         //|JG|        
         //|JG|        if (UISettings.RENAMETEXTBOX == itemFocus) {
         //|JG|            rnText = rntText = "";
         //|JG|            rtbcurPos = 0;
         //|JG|        } else
        //#endif
            if (UISettings.TEXTBOX == itemFocus) {
            itemFocused = itemFocus;
            text = tText = "";
            tbcurpos = 0;
        }
        iCanvasHandler.reLoadFooterMenu();
    }

     public void deleteCharacter(byte itemFocus) {
         //#if KEYPAD
         //|JG|        keyConformed();
         //|JG|        
         //|JG|        if (UISettings.RENAMETEXTBOX == itemFocus) {
         //|JG|            if (rnText.length() > 0 && rtbcurPos > 0) {
         //|JG|                rnText = rnText.substring(0, rtbcurPos - 1) + rnText.substring(rtbcurPos);
         //|JG|                rtbcurPos--;
         //|JG|            }
         //|JG|            rntText = rnText;
         //|JG|        } else
            //#endif
            if (UISettings.TEXTBOX == itemFocus) {
                handleTextBoxDelete();
        }
        iCanvasHandler.reLoadFooterMenu();
    }

     
     
    public void setTextboxValue(String txt,boolean isMaxSet) {
        //#if KEYPAD
        //|JG|        keyTimerStop();
        //#endif
        text = tText = "";
        tbcurpos = 0;
        if (txt != null) {
            text = tText = txt;
            ApplyFormat(true);
            tbcurpos = text.length();
            if(isMaxSet && (tbcurpos>UISettings.MAX_COUNT)){
                maxChar = (short) tbcurpos;
            }
        }
    }

     public void startTextCursorBlinkTimer() {
         toogleTimerStop();
         toogleTimerStart();
    }
     
     private void handleTextBoxDelete() {
        if (entryType == IKeyHandler.DATE) { //Entry type not equal to Date
            text = tText = "";
            tbcurpos = 0;
        } else if(isNative){
            if(tbcurpos>0){
                tbcurpos--;
                tText = text = text.substring(0,tbcurpos);
            }
        } else {
            if (text.length() > 0 && tbcurpos > 0) {
                tbcurpos--;
                tvar = (tbcurpos + 1) - getAppendOrDeletePos();
                if(tvar >0)
                    text = text.substring(0, tvar - 1) + text.substring(tvar);
                tText = text;
                ApplyFormat(true);
                //#if KEYPAD
                //|JG|                setNumericCursorPos(-1, 0);
                //#endif
                if (entryType != IKeyHandler.ALPHA && entryType != IKeyHandler.ALPHANUMERIC && entryType != IKeyHandler.NUMERIC) {
                    if (text.length() < 3) {
                        tbcurpos = tText.length();
                    }
                }
            }
        } 
    }

     //#if KEYPAD
     //|JG|
     //|JG|     private void setNumericCursorPos(int changePos, int moving) {
     //|JG|        int change = tbcurpos;
     //|JG|        if (change > 0 && changePos < 0) {
     //|JG|            change += changePos;
     //|JG|        }
     //|JG|        if (tText.length() > change) {
     //|JG|            if (IKeyHandler.PHONENUMBER == entryType) {
     //|JG|                if (tText.charAt(change) == '-') {
     //|JG|                    tbcurpos += changePos;
     //|JG|                }
     //|JG|            } else if (entryType == IKeyHandler.DECIMAL || entryType == IKeyHandler.DOLLARCENTS) {
     //|JG|                if (moving == 0) {
     //|JG|                    change = 0;
     //|JG|                    if (entryType == IKeyHandler.DOLLARCENTS) {
     //|JG|                        change = 1;
     //|JG|                    }
     //|JG|                    if (changePos > 0 && tbcurpos > (1 + change)) {
     //|JG|                        if (tText.charAt((1 + change)) == ',') {
     //|JG|                            tbcurpos += changePos;
     //|JG|                        }
     //|JG|                    } else if (changePos < 0 && tbcurpos > (3 + change)) {
     //|JG|                        if (tText.charAt((3 + change)) == ',') {
     //|JG|                            tbcurpos += changePos;
     //|JG|                        }
     //|JG|                    }
     //|JG|                } else {
     //|JG|                    if (tText.charAt(change) == '.' || tText.charAt(change) == ',') {
     //|JG|                        tbcurpos += changePos;
     //|JG|                    }
     //|JG|                }
     //|JG|            }
     //|JG|        } else {
     //|JG|            tbcurpos = tText.length();
     //|JG|        }
     //|JG|    }
     //|JG|
     //|JG|     
     //|JG|     public void handleTextBoxLeftArrow() {
     //|JG|         if(itemFocused == UISettings.TEXTBOX){
     //|JG|             if(isNative){
     //|JG|                invokeNativeTextbox();
     //|JG|             } else if (IKeyHandler.DATE != entryType) {
     //|JG|                    if (tbcurpos > 0) {
     //|JG|                        if (entryType == IKeyHandler.ALPHA || entryType == IKeyHandler.ALPHANUMERIC || entryType == IKeyHandler.NUMERIC) {
     //|JG|                            keyConformed();
     //|JG|                            tbcurpos--;
     //|JG|                        } else {
     //|JG|                            if (text.length() > 1) {
     //|JG|                                tbcurpos--;
     //|JG|                                setNumericCursorPos(-1, 1);
     //|JG|                            } else {
     //|JG|                                if (tbcurpos == 5) {
     //|JG|                                    tbcurpos--;
     //|JG|                                }
     //|JG|                            }
     //|JG|                        }
     //|JG|                    } else {
     //|JG|                        isBlink = true;
     //|JG|                    }
     //|JG|                }
     //|JG|         } else {
     //|JG|             if(rtbcurPos>0){
     //|JG|                 if(rtbcurPos > 0){
     //|JG|                       keyConformed();
     //|JG|                       rtbcurPos--;
     //|JG|                 }
     //|JG|             }
     //|JG|         }
     //|JG|    }
     //|JG|
     //|JG|     public void HandleTextBoxRightArrow() {
     //|JG|         if(itemFocused == UISettings.TEXTBOX){
     //|JG|             if(isNative){
     //|JG|                invokeNativeTextbox();
     //|JG|             } else
     //|JG|                if (IKeyHandler.DATE != entryType) {
     //|JG|                    if (tText.length() > tbcurpos) {
     //|JG|                        if (IKeyHandler.ALPHA == entryType || entryType == IKeyHandler.ALPHANUMERIC || entryType == IKeyHandler.NUMERIC) {
     //|JG|                            keyConformed();
     //|JG|                            tbcurpos++;
     //|JG|                        } else {
     //|JG|                            setNumericCursorPos(1, 1);
     //|JG|                            tbcurpos++;
     //|JG|                        }
     //|JG|                    } else if(tText.length()>text.length()){
     //|JG|                        keyConformed();
     //|JG|                    }
     //|JG|                }
     //|JG|         } else {
     //|JG|             if(rtbcurPos < rntText.length()){
     //|JG|                 keyConformed();
     //|JG|                 rtbcurPos++;
     //|JG|             }
     //|JG|         }
     //|JG|    }
     //#endif
     
     public void invokeNativeTextbox(){
        int mchar = maxChar;
        if(lCount>-1){           
            if((mchar+lCount) >UISettings.MAX_COUNT)
                mchar = (short) (UISettings.MAX_COUNT - lCount);
            
            if(mchar<0)
                mchar = 1;
        }
        iCanvasHandler.showNativeTextbox(mchar, (byte)entryType, isMask());
     }
     
     public String getLetterCount(){
        final String maxCount = "/"+UISettings.MAX_COUNT;
        if (lCount > -1) {
            return lCount + tText.length() + maxCount;
        }
        return null;
     }
     
     public void deinitialize(){
         //#if KEYPAD
         //|JG|         searchTimerStop();
         //|JG|
         //|JG|         keyTimerStop();
         //|JG|
         //|JG|         keyMajor = -1;
         //|JG|
         //|JG|         keyMinor = 0;
         //|JG|
         //|JG|         isNotquery = true;
         //|JG|
         //|JG|         isNotFinish = false;
         //|JG|
         //|JG|         kmode = IKeyHandler.MODE_abc;
         //|JG|         
         //|JG|         kModeStr = "abc";
         //|JG|         
         //|JG|         rnText = rntText = stText = stext = "";
         //#endif

         isBlink = true;

         toogleTimerStop();

         cursorChar ='|';
         
         tbcurpos = rtbcurPos = 0;
         
         text = tText = value = "";
         
         minChar = maxChar = 0;
         
         minValue = maxValue = 0;
          
         mask = null;
          
         lCount = -1;
     }

     //#if KEYPAD
     //|JG|
     //|JG|    public void appendCharacter(byte itemfocused, char value) {
     //|JG|        if(itemfocused == UISettings.TEXTBOX){
     //|JG|           keyConformed();
     //|JG|           if (isNotquery || '$' != value) {
     //|JG|               text = appendValue(text, value+"", tbcurpos);
     //|JG|               tbcurpos++;
     //|JG|               tText = text;
     //|JG|               ApplyFormat(true);
     //|JG|               if(text.length()<2)
     //|JG|                   iCanvasHandler.reLoadFooterMenu();
     //|JG|           }
     //|JG|        } else if(itemfocused == UISettings.RENAMETEXTBOX){
     //|JG|           keyConformed();
     //|JG|           if (isNotquery || '$' != value) {
     //|JG|               rnText = appendValue(rnText, value+"", rtbcurPos);
     //|JG|               rtbcurPos++;
     //|JG|               rntText = rnText;
     //|JG|               ApplyFormat(true);
     //|JG|               if(rnText.length()<2)
     //|JG|                   iCanvasHandler.reLoadFooterMenu();
     //|JG|           }
     //|JG|        } else {
     //|JG|            searchTimerStop();
     //|JG|            stext += value;
     //|JG|            searchTimerStart();
     //|JG|        }
     //|JG|    }
     //|JG|
     //|JG|
     //|JG|    class KeyConfirmer extends TimerTask {
     //|JG|        /**
     //|JG|         * run method to start the timer
     //|JG|         */
     //|JG|        public void run() {
     //|JG|            keyTime = 0;
     //|JG|            ShortHandCanvas.IsNeedPaint();
     //|JG|        }
     //|JG|    }
     //|JG|
     //|JG|    class SearchMenuCompletionTimer extends TimerTask {
     //|JG|        /**
     //|JG|         * Run method to start the timer
     //|JG|         */
     //|JG|        public void run() {
     //|JG|            searchTime = 0;
     //|JG|            ShortHandCanvas.IsNeedPaint();
     //|JG|        }
     //|JG|    }
    //#endif

    class TextBoxCursorBlink extends TimerTask {
        /**
         * run method to start the timer
         */
        public void run() {
            currsorTime = 0; //bug 8377
            if(itemFocused == UISettings.TEXTBOX || itemFocused == UISettings.RENAMETEXTBOX)
                ShortHandCanvas.IsNeedPaint();
        }
    }

    

}
