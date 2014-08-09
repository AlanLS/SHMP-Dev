/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hakuna
 */
public interface IKeyHandler {

        public static final byte NUMERIC = 0;
        public static final byte ALPHA = 1;
        public static final byte ALPHANUMERIC = 2;
        public static final byte DECIMAL = 3;
        public static final byte DOLLARCENTS = 4;
        public static final byte DATE = 5;
        public static final byte PHONENUMBER = 6;
        public static final byte MODE_123 = 0;
        public static final byte MODE_abc = 1;
        public static final byte MODE_ABC = 2;
        public static final byte MODE_Abc = 3;
        public static final byte CAPS_SINGLE = 1;
        public static final byte CAPS_FULL = 2;
        public static final byte CAPS_NO = 0;
        public static final byte FUN_NO = 0;
        public static final byte FUN_SINGLE = 1;
        public static final byte FUN_FULL = 2;
        //#if KEYPAD
        //|JG|        void copyTexttoRenameTextBox(String txt);
        //|JG|
        //|JG|        boolean isQWERTY();
        //|JG|
        //|JG|        void updateSearchTimer();
        //|JG|        
        //|JG|        String getSearchTempText();
        //|JG|        
        //|JG|        String getSearchText();
        //|JG|        
        //|JG|        void SearchValueReset();
        //|JG|        
        //|JG|        void RenametextBoxReset();
        //|JG|        
        //|JG|        boolean handleSearchText(int keyCode);
        //|JG|        
        //|JG|        String getRenameTempText();
        //|JG|        
        //|JG|        void setRenameTempText(String value);
        //|JG|        
        //|JG|        void setRenameText(String value);
        //|JG|        
        //|JG|        void setRenameTextValue(String value);
        //|JG|        
        //|JG|        String getRenameText();
        //|JG|        
        //|JG|        void addRenameTextboxCurPos(int value);
        //|JG|        
        //|JG|        void keyConformed();
        //|JG|        
        //|JG|        void changeMode(int keyMode);
        //|JG|        
        //|JG|        int getRenameTextCursorPos();
        //|JG|
        //|JG|        void handleRenameTextKey(int keyCode);
        //|JG|        void addTextbox();
        //|JG|        void setKeyMode();
        //|JG|        void handleTextBoxLeftArrow();
        //|JG|
        //|JG|        void HandleTextBoxRightArrow();
        //|JG|
        //|JG|        void appendCharacter(byte itemfocused,char value);
        //|JG|
        //#endif

        void startTextCursorBlinkTimer();

        void updateKeyTimer();
        
        void handleInputForTextBox(int keyCode);

        void invokeNativeTextbox();
        
        void setEntryProperty(int minChar, int maxChar,
            float minValue, float maxValue, String mask,int lCount,int entryType,boolean isNative,boolean isNotQuery);
        
        void setCanvasHandler(ICanvasHandler entry);
        
        void SetItemFocused(byte itemFocused);
        
        void EntryTextBoxReset();
        
        boolean isMask();
        
        String getKeyMode();
        
        void deinitialize();
        
        boolean isNumericCheck(int count);
        
        boolean isPhoneCheck();
        
        String getEntryTempText();
        
        void setEntryTempText(String value);
        
        boolean isAlphaMaxCheck(int count);
        
        String getLetterCount();
        
        String getEntryText();
        
        void setEntryText(String value);
        
        void addTextboxCurPos(int value);
        
        int getTextboxCursorPos();
    
        char getKeyChar();
        
        void clearCharcters(byte itemFocus);
        
        void deleteCharacter(byte itemFocus);
        
        void ApplyFormat(boolean isTextbox);
       
        void copyTextToTextBox(String text);
        
        void setTextboxValue(String text,boolean  isMaxSet);
}
