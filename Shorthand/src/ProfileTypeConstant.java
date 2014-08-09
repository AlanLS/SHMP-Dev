/*
 * ProfileTypeConstant.java
 *
 * Created on October 12, 2007, 8:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * 
 * @author karthik
 */
public class ProfileTypeConstant {

    public static final byte PROFILEHEADER = 0;
    public static final byte MENU = 1;
    public static final byte ITEM = 2;
    public static final byte ENTRYACTION = 3;
    public static final byte SMSSENDACTION = 4;
    public static final byte DISPLAYACTION = 5;
    public static final byte CALLACTION = 6;
    public static final byte URLACTION = 7;
    public static final byte GENERALACTION = 8;
    public static final byte DOWNLOADACTION = 9;


    public static final byte MENUHEADER_SIZE = 1;
    public static final byte ITEMHEADER_SIZE = 1;
    public static final byte DISPLAYACTIONHEADER_SIZE = 1;
    public static final byte PROFILEHEADER_SIZE = 2;
    public static final byte ENTRYACTIONHEADER_SIZE = 3;
    public static final byte SMSSENDACTIONHEADER_SIZE = 2;
    public static final byte CALLACTIONHEADER_SIZE = 0;
    public static final byte URLACTIONHEADER_SIZE = 0;
    public static final byte GENERALACTIONHEADER_SIZE = 1;
    public static final byte ENTRYACTIONHEADER_SECOND =1;

    public class BackEndState {
        public static final byte APPHANDLER_STATE = 0;
        public static final byte PROFILEHANDLER_STATE = 1;
        public static final byte SMSREADER_STATE = 2;
        public static final byte SECURITY_STATE = 3;
        public static final byte ADHANDLER_STATE = 4;
    }

    public class Display{
        public static final byte DISPLAY_CHAT = 4;
        public static final byte DISPLAY_INFO = 3;
        public static final byte DISPLAY_SMSMESSAGE = 2;
        public static final byte DISPLAY_SMSWAIT = 1;
        public static final byte DISPLAY_PROFILE = 5;
        public static final byte DISPLAY_GSPROFILE = 6;
        public static final byte DISPLAY_MYPROFILE = 7;
    }
}



