
import generated.Build;
import generated.GobSmart;
import generated.GobSmart1;
import jg.JgCanvas;



/**
 * Class to define all the constans required by UI
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc 
 */
public class UISettings {

    //Screen Header Height its Static*/
    public static byte headerHeight = 25;
    //Option Item height */
    //public static byte optItemHeight = 20;
    //display Screen having the this Number of Menu Item 
    public static byte numOfMenuItems = 6;

    // Each menu item Height (its dynamic)
    public static byte itemHeight = 26;

    // Secondary Header Height it's dynamic 
    public static byte secondaryHeaderHeight = 25;
    // Fotter Item Height */
    public static byte footerHeight = 25;

    //Display Screen Height or mobile screen Height */
    public static short formHeight = 0;

    //display Screen Width or Mobile Screen Width */
    public static short formWidth = 0;

    //Draw Option Item Width */
    public static short optWidth = 0;
    /** SmartPopUp Image Width */
    public static short smPWidth = 0;
    //SmartPopup Image Heigth */
    public static byte smpHeight = 0;
    //Message BOx Height
    public static int msgHeight = 0;
    public static int msgPos = 0;
    
    public static int popupHeight = 0;
    
    
    public static int SUBMODE = Build.SUB_MODE;
    public static int MAINMODE = Build.MAIN_MODE;
    public static int SPACEKEYCODE = Build.SPACKKEY_MODE;
    public static String SPACETEXT = Build.SPACE_TEXT;
    //bug id 5370
    public static String SYMBOLTEXT=".?!1@,()#&/-:;'_%+=*[]~<>^{}|\\$"; 
    public static String SPECIALSYMBOLS = Build.SPECIALSYMBOLS;
    public static int SPECIALSYMBOLSCODE = Build.SPECIALSYMBOLS_MODE;
    public static int FUNKEY = Build.FUN_KEY;
    public static int SYMBOLKEYMODE = Build.SYMBOL_KEY_MODE;
    
    public static int BACKSPACE = JgCanvas.JG_KEY_CLEAR;
    public static int LEFTOPTION = JgCanvas.JG_KEY_SOFTKEY_LEFT; 
    public static int RIGHTOPTION = JgCanvas.JG_KEY_SOFTKEY_RIGHT;
    public static int UPKEY = JgCanvas.JG_KEY_UP;
    public static int DOWNKEY = JgCanvas.JG_KEY_DOWN;
    public static int LEFTARROW = JgCanvas.JG_KEY_LEFT;
    public static int RIGHTARROW =JgCanvas.JG_KEY_RIGHT;
    public static int FIREKEY = JgCanvas.JG_KEY_SELECT;
    public static int SELECTKEY = Build.ENTER_KEY_CODE;
    public static int BACKKEY = JgCanvas.JG_KEY_BACK;
    public static int BACK = Build.BACK;
    public static boolean GENERIC = Build.GENERIC;
    public static boolean LG = Build.SERVERCONNECTION;
    public static boolean HASTHIRDSOFTKEY = false;
    
    public static final byte BANNER = 91;
    public static final byte NOTIFICATION = 92;
    public static final byte OPTIONS = 93;
    public static String IMPROPERENTRY = "";
    public static final byte MENU = 95;
    public static final byte RENAMETEXTBOX = 96;
    public static final byte TEXTBOX = 97;
    public static final byte MESSAGEBOX = 98;
    public static final byte VIEW = 99;
    public static final byte SEARCH = 100;
    public static final byte POPUPSCREN = 101;
    public static final byte SYMBOLS = 102;
    public static final byte CAPTURE_IMAGE =  103;
    public static final byte IMAGE_MENU = 104;

    public static byte rOByte = -1;
    
    public static byte lOByte = -1;
    
    public static byte itemFocused = -1;
    
    public static byte itemLastFocused = -1;

    public static boolean isTocuhScreenNativeTextbox = Build.NATIVE_TEXTBOX;

    public static boolean isCenterOkOption = Build.NATIVEBOX_OPTION;
    
    public static int MESSAGE_BOX_WIDTH = Build.MESSAGEBOX_SIZE;
    
    public static int MESSAGE_BOX_HEIGHT = Build.MESSAGEBOX_HEIGHT;
    
    public static byte FOTTER_TEXT_DRAW_POSITION = (byte)Build.FOTTER_TEXT_POSITION;
    
    public static int SYMBOL_BOX_WIDTH = Build.SYMBOL_BOX_WIDTH;
    
    public static byte POPUP_SCROLL_WIDTH = (byte)Build.POPUP_SCROLL_WIDTH;
    
    public static int POPUP_HEADER_HEIGHT = (byte)Build.POPUP_HEADER_HEIGHT;

    public static int SELECT_TIME = Build.TOUCH_SELECT_TIME;
    
    public static String SHORTHAND_WATER_MARK = Build.SHORTHAND_WATER_MARK;

    public static int GRID_IMAGE_HEIGHT = Build.GRIDIMAGEHEIGHT;

    public static int FONTSIZE = Build.FONTSIZE;

    public static boolean isActivate = Build.ACTIVATE;

    public static boolean isRegistryNotRemove = Build.REGISTRY_NOT_REMOVED;

    public static boolean isConverSymbol = Build.SMS_SYMBOL;

    public static boolean isUTF8Convert = Build.FORMATCONVERT;

    public static boolean isRmsLogNotDisable = Build.LOGNOTDISABLE;
    
    public static int HD = GobSmart.HD;

    public static int SHD = GobSmart.SHD;

    public static int IS = GobSmart.IS;

    public static int HG = GobSmart.HG;

    public static int MSG = GobSmart.MSG;

   public static int BG = GobSmart.BG;

    public static int RD = GobSmart.RD;

    public static int UR = GobSmart.UR;
    //CR 0012063
    public static int SMS = GobSmart1.SMS;
    public static int DATA = GobSmart1.DATA;
    //<|CR 0012063>

    //CR 13033
    public static int SCROLL = GobSmart1.SCROLL;

    //CR 12319
    public static int NOTIFICATIONIMAGE = GobSmart1.CIRCLE;
    
    public static int PENDINGIMAGE = GobSmart1._1;

    //CR 13074
    public static int MAX_COUNT = 160;

//    public static int BG = 0;
//
//    public static int IS = 1;
//
//    public static int HD = 2;
//
//    public static int SHD = 3;
//
//    public static int HG = 6;
//
//    public static int MSG = 10;
//
//    public static int RD = 4;
//
//    public static int UR = 5;

}
