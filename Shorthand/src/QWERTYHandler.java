//#if KEYPAD
//|JG|import generated.Build;
//|JG|import java.util.Hashtable;
//|JG|import java.util.Timer;
//|JG|import java.util.TimerTask;
//|JG|
//|JG|/*
//|JG| * To change this template, choose Tools | Templates
//|JG| * and open the template in the editor.
//|JG| */
//|JG|/**
//|JG| *
//|JG| * @author Hakuna
//|JG| */
//|JG|public class QWERTYHandler implements IKeyHandler {
//|JG|
//|JG|    private int entryType = 0;
//|JG|    String text = "", tText = "", rnText = "", rntText = "";
//|JG|    int minChar, maxChar;
//|JG|    float minValue, maxValue;
//|JG|    public char cursorChar = '|';
//|JG|    private String mask = null;
//|JG|    int lCount = -1;
//|JG|    private int tbcurpos = 0,  rtbcurPos = 0;
//|JG|    private String mValue = "";
//|JG|    private boolean isNative = false;
//|JG|    private boolean isNotSeqQuery = true;
//|JG|    /**
//|JG|     * 1 - Single Character Upper case Mode
//|JG|     * 2 - All Caharacter Upper case Mode
//|JG|     **/
//|JG|    //private byte kmode = 0;
//|JG|    private ICanvasHandler iCanvasHandler = null;
//|JG|    private String stext = "",  stText = "";
//|JG|    private byte itemFocus = 0;
//|JG|    //private int key_Mode = Build.MAIN_MODE;
//|JG|    //private int Symbols_Key = Build.SPECIALSYMBOLS_MODE;
//|JG|    //private int Space_Key = Build.SPACKKEY_MODE;
//|JG|    private String kModeStr = "abc";
//|JG|    private byte kmode = IKeyHandler.MODE_abc;
//|JG|    //private boolean isNotquery = true;
//|JG|    private short searchTime = 0;
//|JG|    private short currsorTime = 0;
//|JG|    private byte mode_Change = 0;
//|JG|    private boolean isFull = false;
//|JG|    private byte fun_Key = IKeyHandler.FUN_NO;
//|JG|    private Hashtable functionKey_Map = new Hashtable();
//|JG|    private Hashtable numericKey_Map = new Hashtable();
//|JG|    private Hashtable alpha_Map = new Hashtable();
//|JG|    private Hashtable extra_Map = new Hashtable();
//|JG|    private Timer textBoxBlinkTimer = null;
//|JG|    private Timer searchStringTimer = null;
//|JG|
//|JG|
//|JG|    public QWERTYHandler() {
//|JG|        String[] value = null;
//|JG|        int count = 0;
//|JG|        if (null != Build.SPECIALSYMBOLS && Build.SPECIALSYMBOLS.length() > 0) {
//|JG|            value = Utilities.split(Build.SPECIALSYMBOLS, "^");
//|JG|            count = value.length;
//|JG|            for (int i = 0; i < count; i += 2) {
//|JG|                functionKey_Map.put(value[i], value[i + 1]);
//|JG|            }
//|JG|        }
//|JG|        if (null != Build.NUMERIC_TEXT && Build.NUMERIC_TEXT.length() > 0) {
//|JG|            value = Utilities.split(Build.NUMERIC_TEXT, "^");
//|JG|            count = value.length;
//|JG|            String keCode = "0";
//|JG|            for (int i = 0; i < count; i++) {
//|JG|                keCode = "" + i;
//|JG|                numericKey_Map.put(value[i], keCode);
//|JG|            }
//|JG|        }
//|JG|        if (Build.NEED_ALPHA_CHARACTER && Build.ALPHA_CHARACTERS.length() > 0) {
//|JG|            value = Utilities.split(Build.ALPHA_CHARACTERS, "^");
//|JG|            count = value.length;
//|JG|            for (int i = 0; i < count; i += 2) {
//|JG|                alpha_Map.put(value[i], value[i + 1]);
//|JG|            }
//|JG|        }
//|JG|        if (Build.NEED_EXTRA_CHARACTER && Build.EXTRA_CHARACTERS.length() > 0) {
//|JG|            value = Utilities.split(Build.EXTRA_CHARACTERS, "^");
//|JG|            count = value.length;
//|JG|            for (int i = 0; i < count; i += 2) {
//|JG|                extra_Map.put(value[i], value[i + 1]);
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public boolean isQWERTY() {
//|JG|        return true;
//|JG|    }
//|JG|
//|JG|    public void setCanvasHandler(ICanvasHandler entry) {
//|JG|        iCanvasHandler = entry;
//|JG|    }
//|JG|
//|JG|    public void updateSearchTimer() {
//|JG|        if(searchTime == 0){
//|JG|            SearchValueReset();
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void updateKeyTimer() {
//|JG|        if(currsorTime == 0){
//|JG|            toggleCursor();
//|JG|            currsorTime = 1;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void EntryTextBoxReset() {
//|JG|        text = "";
//|JG|        tText = "";
//|JG|        mValue = "";
//|JG|        tbcurpos = 0;
//|JG|    }
//|JG|
//|JG|    public void RenametextBoxReset() {
//|JG|        rnText = "";
//|JG|        rntText = "";
//|JG|        rtbcurPos = 0;
//|JG|    }
//|JG|
//|JG|    public void SearchValueReset() {
//|JG|        stopSearchTimer();
//|JG|        stext = stText = "";
//|JG|    }
//|JG|
//|JG|    public String getSearchTempText() {
//|JG|        return stText.toLowerCase();
//|JG|    }
//|JG|
//|JG|    public String getSearchText() {
//|JG|        return stext.toLowerCase();
//|JG|    }
//|JG|
//|JG|    public void setRenameTextValue(String value) {
//|JG|        rntText = rnText = value;
//|JG|        rtbcurPos = value.length();
//|JG|        toogleTimerStop();
//|JG|        toogleTimerStart();
//|JG|        kmode = IKeyHandler.MODE_abc;
//|JG|        fun_Key = IKeyHandler.FUN_NO;
//|JG|    }
//|JG|
//|JG|    public void setEntryProperty(int minChar, int maxChar,
//|JG|            float minValue, float maxValue, String mask,
//|JG|            int lCount, int entryType, boolean isNative, boolean isquery) {
//|JG|        this.minChar = minChar;
//|JG|        this.maxChar = maxChar;
//|JG|        this.minValue = minValue;
//|JG|        this.maxValue = maxValue;
//|JG|        this.mask = mask;
//|JG|        this.lCount = lCount;
//|JG|        this.entryType = entryType;
//|JG|        this.isNative = isNative;
//|JG|        this.fun_Key = IKeyHandler.FUN_NO;
//|JG|        isNotSeqQuery = isquery;
//|JG|        if (entryType == IKeyHandler.ALPHA) {
//|JG|            kmode = IKeyHandler.MODE_abc;
//|JG|            kModeStr = "abc";
//|JG|        } else if (entryType == IKeyHandler.ALPHANUMERIC) {
//|JG|            kmode = IKeyHandler.MODE_abc;
//|JG|            kModeStr = "abc";
//|JG|        } else {
//|JG|            kmode = IKeyHandler.MODE_123;
//|JG|            kModeStr = "123";
//|JG|        }
//|JG|        if (entryType == 6) {
//|JG|            this.minChar = 1;
//|JG|            this.maxChar = 11;
//|JG|            this.minValue = 1;
//|JG|            this.maxValue = 9999999;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void SetItemFocused(byte itemFocus) {
//|JG|        this.itemFocus = itemFocus;
//|JG|    }
//|JG|
//|JG|    private void stopSearchTimer() {
//|JG|        if(null != searchStringTimer){
//|JG|            searchStringTimer.cancel();
//|JG|            searchStringTimer = null;
//|JG|        }
//|JG|        searchTime = -1;
//|JG|    }
//|JG|
//|JG|    private void startSearchTimer() {
//|JG|        //searchTime = (short) Build.SEARCH_TIME;
//|JG|        stopSearchTimer();
//|JG|        searchStringTimer = new Timer();
//|JG|        searchStringTimer.schedule(new SearchMenuCompletionTimer(), 2000);
//|JG|        searchTime = 1;
//|JG|    }
//|JG|
//|JG|    public boolean handleSearchText(int keyCode) {
//|JG|        String temp = getKeyValue(keyCode);
//|JG|        if (temp.length() > 0) {
//|JG|            stopSearchTimer();
//|JG|            stText = stext += temp;
//|JG|            startSearchTimer();
//|JG|            return true;
//|JG|        } else {
//|JG|            stText = stext;
//|JG|        }
//|JG|        return false;
//|JG|    }
//|JG|
//|JG|    private void toogleTimerStop() {
//|JG|        currsorTime = -1;
//|JG|        if (null != textBoxBlinkTimer) {
//|JG|            textBoxBlinkTimer.cancel();
//|JG|            textBoxBlinkTimer = null;
//|JG|         }
//|JG|    }
//|JG|
//|JG|    private void toogleTimerStart() {
//|JG|        toogleTimerStop();
//|JG|        textBoxBlinkTimer = new Timer();
//|JG|        textBoxBlinkTimer.schedule(new TextBoxCursorBlink(), 0, 300);
//|JG|        currsorTime = 1;
//|JG|    }
//|JG|
//|JG|    public boolean isMask() {
//|JG|        if (null != mask) {
//|JG|            return true;
//|JG|        }
//|JG|        return false;
//|JG|    }
//|JG|
//|JG|    // Key Conformed
//|JG|    public void keyConformed() {
//|JG|    }
//|JG|
//|JG|    public boolean isAlphaMaxCheck(int count) {
//|JG|        if (text.length() >= minChar && (count + text.length()) <= UISettings.MAX_COUNT) {
//|JG|            if (mask != null && mask.length() > 0) {
//|JG|                mValue = Utilities.encryptEntryValue(mask, text);
//|JG|            }
//|JG|            return true;
//|JG|        }
//|JG|        return false;
//|JG|    }
//|JG|
//|JG|    public boolean isNumericCheck(int count) {
//|JG|        double textValue = 0;
//|JG|        if (text.length() > 0 && (count + text.length()) <= UISettings.MAX_COUNT) {
//|JG|            textValue = Double.parseDouble(text);
//|JG|        }
//|JG|        if (text.length() >= minChar && textValue >= minValue && (count + text.length()) <= UISettings.MAX_COUNT) {
//|JG|            ApplyFormat(true);
//|JG|            if (mask != null && mask.length() > 0) {
//|JG|                mValue = Utilities.encryptEntryValue(mask, text);
//|JG|            }
//|JG|            return true;
//|JG|        }
//|JG|        return false;
//|JG|    }
//|JG|
//|JG|    public char getKeyChar() {
//|JG|        return cursorChar;
//|JG|    }
//|JG|
//|JG|    public int getTextboxCursorPos() {
//|JG|        return tbcurpos;
//|JG|    }
//|JG|
//|JG|    public int getRenameTextCursorPos() {
//|JG|        return rtbcurPos;
//|JG|    }
//|JG|
//|JG|    public void addRenameTextboxCurPos(int value) {
//|JG|        rtbcurPos += value;
//|JG|    }
//|JG|
//|JG|    public void addTextboxCurPos(int value) {
//|JG|        tbcurpos += value;
//|JG|    }
//|JG|
//|JG|    public String getRenameText() {
//|JG|        return rnText;
//|JG|    }
//|JG|
//|JG|    public String getEntryText() {
//|JG|        if (null != mask) {
//|JG|            return mValue;
//|JG|        }
//|JG|        return text;
//|JG|    }
//|JG|
//|JG|    public void setRenameText(String value) {
//|JG|        rnText = value;
//|JG|    }
//|JG|
//|JG|    public void setEntryText(String value) {
//|JG|        text = value;
//|JG|    }
//|JG|
//|JG|    public String getRenameTempText() {
//|JG|        return rntText;
//|JG|    }
//|JG|
//|JG|    public void setRenameTempText(String value) {
//|JG|        rntText = value;
//|JG|    }
//|JG|
//|JG|    public String getEntryTempText() {
//|JG|        return tText;
//|JG|    }
//|JG|
//|JG|    public void setEntryTempText(String value) {
//|JG|        tText = value;
//|JG|    }
//|JG|
//|JG|    public String getKeyMode() {
//|JG|        if (UISettings.FUNKEY != 0) {
//|JG|            return kModeStr;
//|JG|        }
//|JG|        return "";
//|JG|    // return kModeStr;
//|JG|    }
//|JG|
//|JG|    public String getLetterCount() {
//|JG|        final String maxCount = "/"+UISettings.MAX_COUNT;
//|JG|        if (lCount > -1) {
//|JG|            return lCount + tText.length() + maxCount;
//|JG|        }
//|JG|        return null;
//|JG|    }
//|JG|
//|JG|    public void changeMode(int keyCode) {
//|JG|        if (entryType == IKeyHandler.ALPHA || entryType == IKeyHandler.ALPHANUMERIC) {
//|JG|            if (mode_Change == IKeyHandler.CAPS_FULL || mode_Change == IKeyHandler.CAPS_SINGLE) {
//|JG|                kmode = IKeyHandler.MODE_ABC;
//|JG|            } else {
//|JG|                kmode = IKeyHandler.MODE_abc;
//|JG|            }
//|JG|        } else {
//|JG|            kmode = IKeyHandler.MODE_123;
//|JG|        }
//|JG|        setKeyMode();
//|JG|    }
//|JG|
//|JG|    /**
//|JG|     * Method to set the key mode in the view.
//|JG|     */
//|JG|    public void setKeyMode() {
//|JG|        if (kmode == IKeyHandler.MODE_123) {
//|JG|            kModeStr = "123";
//|JG|        } else if (kmode == IKeyHandler.MODE_abc) {
//|JG|            kModeStr = "abc";
//|JG|        } else if (kmode == IKeyHandler.MODE_ABC) {
//|JG|            kModeStr = "ABC";
//|JG|        }
//|JG|        if (kmode == IKeyHandler.MODE_Abc) {
//|JG|            kModeStr = "Abc";
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void handleInputForTextBox(int keyCode) {
//|JG|        if (isNative) {
//|JG|            invokeNativeTextbox();
//|JG|        } else if (entryType == IKeyHandler.DATE) {
//|JG|            iCanvasHandler.showDateForm();
//|JG|        } else {
//|JG|            int lco = lCount;
//|JG|            if (lCount == -1) {
//|JG|                lco = 0;
//|JG|            }
//|JG|            if (text.length() < maxChar && (lco + text.length()) < UISettings.MAX_COUNT) {
//|JG|                if (entryType == IKeyHandler.NUMERIC || entryType == IKeyHandler.PHONENUMBER || entryType == IKeyHandler.DOLLARCENTS || entryType == IKeyHandler.DECIMAL) {
//|JG|                    handleInputForNumericMode(keyCode);
//|JG|                } else {
//|JG|                    tText = getKeyValue(keyCode);
//|JG|                    if (tText.length() > 0) {
//|JG|                        text = appendValue(text, tText, tbcurpos);
//|JG|                        tbcurpos++;
//|JG|                    }
//|JG|                    tText = text;
//|JG|                    ApplyFormat(true);
//|JG|                }
//|JG|            } else {
//|JG|                if (keyCode != UISettings.MAINMODE) {
//|JG|                    iCanvasHandler.handleSmartPopup(2);
//|JG|                }
//|JG|            }
//|JG|            if (text.length() < 2) {
//|JG|                iCanvasHandler.reLoadFooterMenu();
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    private String getKeyValue(int keyCode) {
//|JG|        String kText = "";
//|JG|        if (UISettings.SYMBOLKEYMODE != 0 && keyCode == UISettings.SYMBOLKEYMODE) {
//|JG|            if (IKeyHandler.ALPHANUMERIC == entryType) {
//|JG|                iCanvasHandler.loadSympolPopup(); //Show symbol menu
//|JG|            }
//|JG|            return kText;
//|JG|        } else if (keyCode == UISettings.FUNKEY) {
//|JG|            setFunctionMode();
//|JG|            return kText;
//|JG|        } else if (keyCode == UISettings.MAINMODE) {
//|JG|            setModeKey();
//|JG|            if (UISettings.SEARCH == itemFocus) {
//|JG|                stopSearchTimer();
//|JG|                startSearchTimer();
//|JG|            }
//|JG|            changeMode(keyCode);
//|JG|            return kText;
//|JG|        } else if (keyCode == UISettings.SPACEKEYCODE) {
//|JG|            kText = " ";
//|JG|        } else {
//|JG|            if (fun_Key != IKeyHandler.FUN_NO) {
//|JG|                if(Build.NO_KEYNAME){
//|JG|                    kText = getFunctionkeyValue(keyCode,(char)(keyCode)+"");
//|JG|                } else {
//|JG|                    if (Build.NEED_ALPHA_CHARACTER && IKeyHandler.ALPHA == entryType) { // Alpha Should not have the symbols character bug 5183
//|JG|                        kText = ObjectBuilderFactory.getPCanvas().getKeyName(keyCode);
//|JG|                    } else {
//|JG|                        kText = getFunctionkeyValue(keyCode,kText);
//|JG|                    }
//|JG|                }
//|JG|            }
//|JG|            else {
//|JG|                if(Build.NO_KEYNAME){
//|JG|                    kText = (char)(keyCode)+"";
//|JG|                } else {
//|JG|                    kText = ObjectBuilderFactory.getPCanvas().getKeyName(keyCode);
//|JG|                }
//|JG|            }
//|JG|            if (kText.length() > 0) {
//|JG|                boolean isNotAplha = true; // Motorola QA1
//|JG|                if (Build.NEED_ALPHA_CHARACTER) { // Motorola QA1
//|JG|                    if (alpha_Map.containsKey(kText.toLowerCase())) {
//|JG|                        kText = (String) alpha_Map.get(kText.toLowerCase());
//|JG|                        isNotAplha = false;
//|JG|                    }
//|JG|                }
//|JG|                if (Build.NEED_EXTRA_CHARACTER) { //Motorola QA
//|JG|                    if (extra_Map.containsKey(kText.toLowerCase())) {
//|JG|                        kText = (String) extra_Map.get(kText.toLowerCase());
//|JG|                    }
//|JG|                }
//|JG|                if (IKeyHandler.ALPHA == entryType) {
//|JG|                    if (isNotAplha) {
//|JG|                        if ((keyCode < 65 || keyCode > 122) || (keyCode > 90 && keyCode < 97)) { //bug 5183,9555,9563,9630
//|JG|                            iCanvasHandler.handleSmartPopup(2);
//|JG|                            kText = "";
//|JG|                        }
//|JG|                    }
//|JG|                }
//|JG|                if (kmode == IKeyHandler.MODE_ABC) {
//|JG|                    kText = kText.toUpperCase();
//|JG|                } else if (Build.NEED_TO_CHANGE_SMALL) //bug 5181 Motorola QA1 need to change lower case
//|JG|                {
//|JG|                    kText = kText.toLowerCase();
//|JG|                }
//|JG|            }
//|JG|        }
//|JG|        resetKeyMode(keyCode);
//|JG|        return kText;
//|JG|    }
//|JG|
//|JG|    private void setFunctionMode() {
//|JG|        if (Build.SYM_REPEATE) {
//|JG|            if (fun_Key == IKeyHandler.FUN_NO) {
//|JG|                if (isFull) {
//|JG|                    fun_Key = IKeyHandler.FUN_NO;
//|JG|                    isFull = false;
//|JG|                } else {
//|JG|                    fun_Key = IKeyHandler.FUN_SINGLE;
//|JG|                }
//|JG|            } else if (fun_Key == IKeyHandler.FUN_SINGLE) {
//|JG|                fun_Key = IKeyHandler.FUN_FULL;
//|JG|            } else if (fun_Key == IKeyHandler.FUN_FULL) {
//|JG|                fun_Key = IKeyHandler.FUN_NO;
//|JG|                isFull = true;
//|JG|            }
//|JG|        } else {
//|JG|            if (fun_Key == IKeyHandler.FUN_NO) {
//|JG|                fun_Key = IKeyHandler.FUN_SINGLE;
//|JG|            } else if (fun_Key == IKeyHandler.FUN_SINGLE) {
//|JG|                fun_Key = IKeyHandler.FUN_FULL;
//|JG|            } else if (fun_Key == IKeyHandler.FUN_FULL) {
//|JG|                fun_Key = IKeyHandler.FUN_NO;
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    private String getFunctionkeyValue(int keycode,String value) {
//|JG|        if(Build.NO_KEYNAME){
//|JG|            value = value.toLowerCase();
//|JG|        } else { value = ObjectBuilderFactory.getPCanvas().getKeyName(keycode).toLowerCase(); }
//|JG|        if (functionKey_Map.containsKey(value)) {
//|JG|            value = (String) functionKey_Map.get(value);
//|JG|        } else {
//|JG|            value = "";
//|JG|        }
//|JG|        if (fun_Key == IKeyHandler.FUN_SINGLE) {
//|JG|            fun_Key = IKeyHandler.FUN_NO;
//|JG|        }
//|JG|        return value;
//|JG|    }
//|JG|
//|JG|    private void setModeKey() {
//|JG|        if (Build.REPEAT_MODE) {
//|JG|            if (mode_Change == IKeyHandler.CAPS_NO) {
//|JG|                if (isFull) {
//|JG|                    mode_Change = IKeyHandler.CAPS_NO;
//|JG|                    isFull = false;
//|JG|                } else {
//|JG|                    mode_Change = IKeyHandler.CAPS_SINGLE;
//|JG|                }
//|JG|            } else if (mode_Change == IKeyHandler.CAPS_SINGLE) {
//|JG|                mode_Change = IKeyHandler.CAPS_FULL;
//|JG|            } else if (mode_Change == IKeyHandler.CAPS_FULL) {
//|JG|                mode_Change = IKeyHandler.CAPS_NO;
//|JG|                isFull = true;
//|JG|            }
//|JG|        } else {
//|JG|            if (Build.SINGLE_PRESS_CAPS) {
//|JG|                if (fun_Key == IKeyHandler.FUN_NO) {
//|JG|                    mode_Change = IKeyHandler.CAPS_FULL;
//|JG|                } else if (fun_Key == IKeyHandler.CAPS_FULL) {
//|JG|                    mode_Change = IKeyHandler.CAPS_NO;
//|JG|                }
//|JG|            } else {
//|JG|                if (mode_Change == IKeyHandler.CAPS_NO) {
//|JG|                    mode_Change = IKeyHandler.CAPS_SINGLE;
//|JG|                } else if (mode_Change == IKeyHandler.CAPS_SINGLE) {
//|JG|                    mode_Change = IKeyHandler.CAPS_FULL;
//|JG|                } else if (mode_Change == IKeyHandler.CAPS_FULL) {
//|JG|                    mode_Change = IKeyHandler.CAPS_NO;
//|JG|                }
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    private void resetKeyMode(int keyCode) {
//|JG|        if (Build.REPEAT_MODE) {
//|JG|            if (isFull && IKeyHandler.CAPS_NO == mode_Change) {
//|JG|                mode_Change = IKeyHandler.CAPS_FULL;
//|JG|                changeMode(keyCode);
//|JG|                isFull = false;
//|JG|            } else if (mode_Change == IKeyHandler.CAPS_SINGLE) {
//|JG|                mode_Change = IKeyHandler.CAPS_NO;
//|JG|                changeMode(keyCode);
//|JG|            }
//|JG|        } else {
//|JG|            if (!Build.SINGLE_PRESS_CAPS) {
//|JG|                if (mode_Change == IKeyHandler.CAPS_SINGLE) {
//|JG|                    mode_Change = IKeyHandler.CAPS_NO;
//|JG|                    changeMode(keyCode);
//|JG|                }
//|JG|            }
//|JG|        }
//|JG|        if (Build.SYM_REPEATE) {
//|JG|            if (isFull && IKeyHandler.FUN_NO == fun_Key) {
//|JG|                fun_Key = IKeyHandler.FUN_FULL;
//|JG|                isFull = false;
//|JG|            } else if (fun_Key == IKeyHandler.FUN_SINGLE) {
//|JG|                fun_Key = IKeyHandler.FUN_NO;
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void invokeNativeTextbox() {
//|JG|        int mchar = maxChar;
//|JG|        if (lCount > -1) {
//|JG|            if((lCount+mchar)>UISettings.MAX_COUNT){
//|JG|                mchar = (short) (UISettings.MAX_COUNT-lCount);
//|JG|            }
//|JG|            if (mchar < 0) {
//|JG|                mchar = 1;
//|JG|            }
//|JG|        }
//|JG|        iCanvasHandler.showNativeTextbox(mchar, (byte) entryType, isMask());
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
//|JG|    private void handleInputForNumericMode(int keyCode) {
//|JG|        int t = 0;
//|JG|        if (keyCode < 46 || keyCode > 57) {
//|JG|            String kName = "";
//|JG|            if(Build.NO_KEYNAME){
//|JG|                kName = ((char)keyCode)+"";
//|JG|            } else {
//|JG|                kName = ObjectBuilderFactory.getPCanvas().getKeyName(keyCode);
//|JG|            }
//|JG|            if (numericKey_Map.containsKey(kName.toLowerCase())) {
//|JG|                keyCode = 48 + Integer.parseInt((String) numericKey_Map.get(kName.toLowerCase()));
//|JG|            }
//|JG|        }
//|JG|        if (keyCode > 47 && keyCode < 58) {
//|JG|            t = tbcurpos - getAppendOrDeletePos();
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
//|JG|            } else {
//|JG|                iCanvasHandler.handleSmartPopup(2); //ImproperEntry SmartPopup
//|JG|            }
//|JG|        } else {
//|JG|            if (UISettings.MAINMODE != keyCode) {
//|JG|                iCanvasHandler.handleSmartPopup(2); //handleSmartPopup(2);
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    /**
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
//|JG|
//|JG|    private int getAppendOrDeletePos() {
//|JG|        String tem = null;
//|JG|        if (tText.length() > tbcurpos) {
//|JG|            tem = tText.substring(0, tbcurpos);
//|JG|        } else {
//|JG|            tem = tText;
//|JG|        }
//|JG|        int t = 0;
//|JG|        int ipos = 0;
//|JG|        if (IKeyHandler.PHONENUMBER == entryType) {
//|JG|            while ((t = tem.indexOf("-", t)) > -1) {
//|JG|                ipos++;
//|JG|                t++;
//|JG|            }
//|JG|        } else if (IKeyHandler.DOLLARCENTS == entryType || entryType == IKeyHandler.DECIMAL) {
//|JG|            while ((t = tem.indexOf(",", t)) > -1) {
//|JG|                ipos++;
//|JG|                t++;
//|JG|            }
//|JG|            if (tem.indexOf(".") > -1) {
//|JG|                ipos++;
//|JG|            }
//|JG|            if (tem.indexOf("$") > -1) {
//|JG|                ipos++;
//|JG|            }
//|JG|            if (text.length() == 2) {
//|JG|                ipos++;
//|JG|            } else if (text.length() == 1) {
//|JG|                ipos += 2;
//|JG|            }
//|JG|        }
//|JG|        return ipos;
//|JG|    }
//|JG|
//|JG|    public void ApplyFormat(boolean isTextbox) {
//|JG|        String oValue = null;
//|JG|        String tValue = null;
//|JG|        int cPosition = 0;
//|JG|        if (itemFocus == UISettings.RENAMETEXTBOX) {
//|JG|            oValue = rnText;
//|JG|            tValue = rntText;
//|JG|            cPosition = rtbcurPos;
//|JG|        } else {
//|JG|            oValue = text;
//|JG|            tValue = tText;
//|JG|            if (isTextbox) {
//|JG|                cPosition = tbcurpos;
//|JG|            } else {
//|JG|                cPosition = -1;
//|JG|            }
//|JG|        }
//|JG|        if ((entryType == IKeyHandler.DOLLARCENTS || entryType == IKeyHandler.DECIMAL)) {
//|JG|            if (oValue.length() > 0) {
//|JG|                tValue = MoneyFormat(oValue);
//|JG|            }
//|JG|        } else if (entryType == IKeyHandler.PHONENUMBER) {
//|JG|//            StringBuffer sb = new StringBuffer(oValue); // CR id 6366
//|JG|            //if (oValue.startsWith("1")) { // bug id 6516
//|JG|                maxChar = 11;
//|JG|//                if (oValue.length() > 1) {
//|JG|//                    sb = sb.insert(1, '-');
//|JG|//                }
//|JG|//                if (oValue.length() > 4) {
//|JG|//                    sb = sb.insert(5, '-');
//|JG|//                }
//|JG|//                if (oValue.length() > 7) {
//|JG|//                    sb = sb.insert(9, '-');
//|JG|//                }
//|JG|            //} else {
//|JG|              //  maxChar = 10;
//|JG|//                if (oValue.length() > 3) {
//|JG|//                    sb = sb.insert(3, '-');
//|JG|//                }
//|JG|//                if (oValue.length() > 6) {
//|JG|//                    sb = sb.insert(7, '-');
//|JG|//                }
//|JG|            //}
//|JG|//            tValue = sb.toString();
//|JG|//            sb = null;
//|JG|        } else if (null != mask && tValue.length() > 0) {
//|JG|            int len = tValue.length();
//|JG|            StringBuffer sb = new StringBuffer();
//|JG|            for (int i = 0; i < len; i++) {
//|JG|                if (mask.compareTo("*") == 0 || mask.charAt(i) == '*') {
//|JG|                    if ((cPosition == 0 && i == 0) || i == cPosition - 1) {
//|JG|                        sb.append(tValue.charAt(i));
//|JG|                    } else {
//|JG|                        sb.append("*");
//|JG|                    }
//|JG|                } else if (Character.isDigit(mask.charAt(i))) {
//|JG|                    sb.append(tValue.charAt(i));
//|JG|                }
//|JG|            }
//|JG|            tValue = sb.toString();
//|JG|        }
//|JG|        if (itemFocus == UISettings.RENAMETEXTBOX) {
//|JG|            rntText = tValue;
//|JG|        } else {
//|JG|            tText = tValue;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    /**
//|JG|     * Method to apply the money format on the input value
//|JG|     *
//|JG|     * @param value  Input value
//|JG|     * @param String Modified value based on the money format
//|JG|     */
//|JG|    private String MoneyFormat(String value) {
//|JG|        int length = value.length();
//|JG|        StringBuffer val = new StringBuffer(value);
//|JG|        if (length > 5) {
//|JG|            int pos = 3;
//|JG|            length -= 2;
//|JG|            while (length > pos) {
//|JG|                val.insert(length - pos, ",");
//|JG|                pos += 3;
//|JG|            }
//|JG|        } else {
//|JG|            if (length == 1) {
//|JG|                val.insert(0, "00");
//|JG|            } else if (length == 2) {
//|JG|                val.insert(0, "0");
//|JG|            }
//|JG|        }
//|JG|        val.insert(val.length() - 2, ".");
//|JG|        if (entryType == IKeyHandler.DOLLARCENTS) {
//|JG|            val.insert(0, "$");
//|JG|        }
//|JG|        return val.toString();
//|JG|    }
//|JG|
//|JG|    public void setTextBoxConstraints(short minChar, short maxChar,
//|JG|            float minValue, float maxValue, String mask) {
//|JG|        this.minChar = minChar;
//|JG|        this.maxChar = maxChar;
//|JG|        this.minValue = minValue;
//|JG|        this.maxValue = maxValue;
//|JG|        this.mask = mask;
//|JG|
//|JG|        if (entryType == 6) {
//|JG|            this.minChar = 1;
//|JG|            this.maxChar = 11;
//|JG|            this.minValue = 1;
//|JG|            this.maxValue = 9999999;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public boolean isPhoneCheck() {
//|JG|        //if ((text.length() == 10 && !text.startsWith("1")) || (text.startsWith("1") && text.length() == 11)) {
//|JG|        if(text.length()>6 && text.length()<12) { //bug id 6516
//|JG|            if (mask != null && mask.length() > 0) {
//|JG|                mValue = Utilities.encryptEntryValue(mask, text);
//|JG|            }
//|JG|            return true;
//|JG|        }
//|JG|        return false;
//|JG|    }
//|JG|
//|JG|    public void setTextboxValue(String txt, boolean isMaxSet) {
//|JG|        text = tText = "";
//|JG|        tbcurpos = 0;
//|JG|        if (txt != null) {
//|JG|            text = tText = txt;
//|JG|            tbcurpos = text.length();
//|JG|            if (isMaxSet && (tbcurpos > UISettings.MAX_COUNT)) {
//|JG|                maxChar = (short) tbcurpos;
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void startTextCursorBlinkTimer() {
//|JG|        toogleTimerStop();
//|JG|        toogleTimerStart();
//|JG|    }
//|JG|
//|JG|    public void handleRenameTextKey(int keyCode) {
//|JG|        String temp = getKeyValue(keyCode);
//|JG|        if (temp.length() > 0) {
//|JG|            if (rnText.length() < 30) {
//|JG|                rntText = rnText = appendValue(rnText, temp, rtbcurPos);
//|JG|                rtbcurPos++;
//|JG|                if (rnText.length() == 1) {
//|JG|                    iCanvasHandler.reLoadFooterMenu();
//|JG|                }
//|JG|            } else if (keyCode != UISettings.MAINMODE) {
//|JG|                iCanvasHandler.handleSmartPopup(2);
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void clearCharcters(byte itemFocus) {
//|JG|        if (UISettings.RENAMETEXTBOX == itemFocus) {
//|JG|            rnText = rntText = "";
//|JG|            rtbcurPos = 0;
//|JG|        } else if (UISettings.TEXTBOX == itemFocus) {
//|JG|            text = tText = "";
//|JG|            tbcurpos = 0;
//|JG|        }
//|JG|        iCanvasHandler.reLoadFooterMenu();
//|JG|    }
//|JG|
//|JG|    public void deleteCharacter(byte itemFocus) {
//|JG|        if (UISettings.RENAMETEXTBOX == itemFocus) {
//|JG|            if (rnText.length() > 0 && rtbcurPos > 0) {
//|JG|                rnText = rnText.substring(0, rtbcurPos - 1) + rnText.substring(rtbcurPos);
//|JG|                rtbcurPos--;
//|JG|            }
//|JG|            rntText = rnText;
//|JG|        } else if (UISettings.TEXTBOX == itemFocus) {
//|JG|            handleTextBoxDelete();
//|JG|        }
//|JG|        iCanvasHandler.reLoadFooterMenu();
//|JG|    }
//|JG|
//|JG|    private void handleTextBoxDelete() {
//|JG|        if (entryType == IKeyHandler.DATE) {  //Entry type not equal to Date and Native Textbox
//|JG|            text = tText = "";
//|JG|            tbcurpos = 0;
//|JG|        } else if (isNative) {
//|JG|            if (tbcurpos > 0) {
//|JG|                tbcurpos--;
//|JG|                tText = text = text.substring(0, tbcurpos);
//|JG|            }
//|JG|        } else {
//|JG|            if (text.length() > 0 && tbcurpos > 0) {
//|JG|                tbcurpos--;
//|JG|                int tvar = (tbcurpos + 1) - getAppendOrDeletePos();
//|JG|                text = text.substring(0, tvar - 1) + text.substring(tvar);
//|JG|                tText = text;
//|JG|                ApplyFormat(true);
//|JG|                setNumericCursorPos(-1, 0);
//|JG|                if (entryType != IKeyHandler.ALPHA && entryType != IKeyHandler.ALPHANUMERIC && entryType != IKeyHandler.NUMERIC) {
//|JG|                    if (text.length() < 3) {
//|JG|                        tbcurpos = tText.length();
//|JG|                    }
//|JG|                }
//|JG|            }
//|JG|        }
//|JG|    }
//|JG|
//|JG|    private void setNumericCursorPos(int changePos, int moving) {
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
//|JG|    public void handleTextBoxLeftArrow() {
//|JG|        if (itemFocus == UISettings.TEXTBOX) {
//|JG|            if (isNative) {
//|JG|                invokeNativeTextbox();
//|JG|            } else if (IKeyHandler.DATE != entryType) {
//|JG|                if (tbcurpos > 0) {
//|JG|                    if (entryType == IKeyHandler.ALPHA || entryType == IKeyHandler.ALPHANUMERIC || entryType == IKeyHandler.NUMERIC) {
//|JG|                        tbcurpos--;
//|JG|                    } else {
//|JG|                        if (text.length() > 1) {
//|JG|                            tbcurpos--;
//|JG|                            setNumericCursorPos(-1, 1);
//|JG|                        } else {
//|JG|                            if (tbcurpos == 5) {
//|JG|                                tbcurpos--;
//|JG|                            }
//|JG|                        }
//|JG|                    }
//|JG|                }
//|JG|            }
//|JG|        } else if (rtbcurPos > 0) {
//|JG|            rtbcurPos--;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void HandleTextBoxRightArrow() {
//|JG|        if (itemFocus == UISettings.TEXTBOX) {
//|JG|            if (isNative) {
//|JG|                invokeNativeTextbox();
//|JG|            } else if (IKeyHandler.DATE != entryType) {
//|JG|                if (tText.length() > tbcurpos) {
//|JG|                    if (IKeyHandler.ALPHA == entryType || entryType == IKeyHandler.ALPHANUMERIC || entryType == IKeyHandler.NUMERIC) {
//|JG|                        tbcurpos++;
//|JG|                    } else {
//|JG|                        setNumericCursorPos(1, 1);
//|JG|                        tbcurpos++;
//|JG|                    }
//|JG|                }
//|JG|            }
//|JG|        } else if (rtbcurPos < rntText.length()) {
//|JG|            tbcurpos++;
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void toggleCursor() {
//|JG|        if (cursorChar == ' ') {
//|JG|            cursorChar = '|';
//|JG|        } else {
//|JG|            cursorChar = ' ';
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void copyTextToTextBox(String txt) {
//|JG|        text = tText = "";
//|JG|        tbcurpos = 0;
//|JG|        if (txt != null) {
//|JG|            text = tText = txt;
//|JG|            tbcurpos = text.length();
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void copyTexttoRenameTextBox(String txt) {
//|JG|        rnText = rntText = "";
//|JG|        rtbcurPos = 0;
//|JG|        if (null != txt) {
//|JG|            rnText = rntText = txt;
//|JG|            rtbcurPos = rnText.length();
//|JG|        }
//|JG|    }
//|JG|
//|JG|    public void addTextbox() {
//|JG|        toogleTimerStop();
//|JG|        toogleTimerStart();
//|JG|    }
//|JG|
//|JG|    public void deinitialize() {
//|JG|
//|JG|        stopSearchTimer();
//|JG|
//|JG|        toogleTimerStart();
//|JG|
//|JG|        tbcurpos = rtbcurPos = 0;
//|JG|
//|JG|        text = tText = rnText = rntText = mValue = "";
//|JG|
//|JG|        minChar = maxChar = 0;
//|JG|
//|JG|        minValue = maxValue = 0;
//|JG|
//|JG|        mask = null;
//|JG|
//|JG|        stText = stext = "";
//|JG|
//|JG|//        isNotquery = true;
//|JG|
//|JG|        isNative = false;
//|JG|
//|JG|        isNotSeqQuery = true;
//|JG|
//|JG|        kmode = IKeyHandler.MODE_abc;
//|JG|
//|JG|        kModeStr = "abc";
//|JG|
//|JG|        isFull = false;
//|JG|
//|JG|        fun_Key = IKeyHandler.FUN_NO;
//|JG|
//|JG|        mode_Change = IKeyHandler.CAPS_NO;
//|JG|
//|JG|        cursorChar = '|';
//|JG|
//|JG|    }
//|JG|
//|JG|    public void appendCharacter(byte itemfocused, char value) {
//|JG|        if (itemfocused == UISettings.TEXTBOX) {
//|JG|            if (isNotSeqQuery || '$' != value) {
//|JG|                text = appendValue(text, value + "", tbcurpos);
//|JG|                tbcurpos++;
//|JG|                tText = text;
//|JG|                ApplyFormat(true);
//|JG|                if (text.length() < 2) {
//|JG|                    iCanvasHandler.reLoadFooterMenu();
//|JG|                }
//|JG|            }
//|JG|        } else if (itemfocused == UISettings.RENAMETEXTBOX) {
//|JG|            if (isNotSeqQuery || '$' != value) {
//|JG|                rnText = appendValue(rnText, value + "", rtbcurPos);
//|JG|                rtbcurPos++;
//|JG|                rntText = rnText;
//|JG|                ApplyFormat(true);
//|JG|                if (rnText.length() < 2) {
//|JG|                    iCanvasHandler.reLoadFooterMenu();
//|JG|                }
//|JG|            }
//|JG|        } else {
//|JG|            stopSearchTimer();
//|JG|            stext += value;
//|JG|            startSearchTimer();
//|JG|        }
//|JG|    }
//|JG|
//|JG|    class TextBoxCursorBlink extends TimerTask {
//|JG|        /**
//|JG|         * run method to start the timer
//|JG|         */
//|JG|        public void run() {
//|JG|            currsorTime = 0;
//|JG|            //bug 8377
//|JG|            if(itemFocus == UISettings.TEXTBOX || itemFocus == UISettings.RENAMETEXTBOX)
//|JG|                ShortHandCanvas.IsNeedPaint();
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
//|JG|
//|JG|}
//#endif
