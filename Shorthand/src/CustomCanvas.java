
import generated.Build;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import jg.Gob;

/**
 * Custom Canvas class which handles the custom menu
 * 
 * @author Hakuna Matata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */

public class CustomCanvas {
    public static String sHeader = null;
    private static int bannerX = 0;
    private static int fontSizeValue = Font.SIZE_SMALL;
    private static int fontFaceValue = Font.FACE_PROPORTIONAL;
    public static Font font = Font.getFont(getFontFace(), Font.STYLE_PLAIN, getFontSize()); //Font.getFont(0, 0, 8);//
    /**
     * Options Menu Popup
     */
    public static byte[] bopts; // options byte array
    private static byte optai = 0; // option array index
    
    //MessageBox Starting position
    private static short msgboxStartingPoint = 0;
    // MessageBox Height with Header and footer
    private static short msgboxHeight = 0;
    
    private static short symRowcount = 0;
    
    private static short symColCount = 0;
    private static short selectedRow = 0;
    private static short selecteColumn = 0;
    
    // Message Page Index
    private static String[] msgText = null;
    private static String msgHeaderText = null;
    // Message Page Array Index
    //private static byte mpindex = 0;
    private static byte mplineIndex = 0;
    public static Gob[] images = null;
    public static Gob[] smartOne = null;
    
    //public static Gob[] eMotic = null;
    public static Gob[] splash = null;
    
    public static String notText = null;

    public static byte isNotificationGoto = -1;
    
    public static String[] popText = null;
    
    private static String msgBoxText = null;
    
    private static char[] symbols = Build.SYMBOLS.toCharArray();
    
    private static short symBoxSize = 0;
    
    private static short totalRowCount = 0;
    
    private static short optionsItem = 0;
    
    //private static short optionsStartIndex = 0;
    
    public static int[] gobRGB = new int[1];

    public static int[] splashRGB = new int[]{0x21519c};

    private static int previousY = -1;

    private static Vector chatNotification = null;

    //private static int chatTextPosition = -1;

    private static Timer chatNotificationTimer = null;

    private static String chatNotificationText = null;

    private static Image chatNotificationImage = null;

    private static float yStratPosition = 0;

    public static boolean isShowScroll = false;

    private static Timer scrollTimer = null;

    public static int SCROLL_WIDTH = 0;

    private static boolean iSScrollEnabled = false;

    private static float scrollLength = -1;

    public static byte msgType = -1;

    public static boolean isShowMessageSendSprit = false;

    public static boolean isChatNotification = false;

    private static Image mIcon = null;

    //private static boolean isSelected = false;



    /**
     * Creates a new instance of CustomCanvas
     */
    public static void Initialize() {
        Constants.setApplicationFilePath();
        bannerX = UISettings.formWidth;
        UISettings.POPUP_HEADER_HEIGHT += (4 + font.getHeight());
        UISettings.POPUP_SCROLL_WIDTH += 5;
        try {
            rotateNotificationPopup();
            msgboxStartingPoint = (short) (UISettings.headerHeight + (UISettings.itemHeight / 2));
            msgboxHeight = (short) ((UISettings.numOfMenuItems - 1) * UISettings.itemHeight);
        } catch (Exception ex) { }
    }

    public static void resetBannerPosition(){
        bannerX = UISettings.formWidth;
    }
    
    /**
     * Method to draw primary header
     * 
     * @param g   An instance of Graphics class.
     */
   
    public static void drawHeader(Graphics g) {
        g.setFont(font);
        int y = 2;
        if(null != images){
            images[UISettings.HD].paint(g,0 , 0, Gob.TRANS_NONE);
        } else  {
            g.setColor(0xB8B8B8);
            g.fillRect(0,0,UISettings.formWidth,UISettings.headerHeight);
        }

        if(null != chatNotificationText){
            if(null != chatNotificationImage){
                g.drawImage(chatNotificationImage, 0, (UISettings.headerHeight-chatNotificationImage.getHeight())/2, Graphics.TOP | Graphics.LEFT);
                y = chatNotificationImage.getWidth();
            }
            g.setColor(0x554344);
            //Hema
            //chatNotificationText = Utilities.splitText(chatNotificationText, y + 2, UISettings.formWidth - 2 - images[UISettings.DATA].width, UISettings.headerHeight, font)[0];
            g.drawString(chatNotificationText, y + 2,
                    (UISettings.headerHeight-font.getHeight())/2, Graphics.TOP | Graphics.LEFT);
        } else {
            //CR 12063
            //CR 12635
            if(null != smartOne){
                if(Settings.isIsGPRS())//12545
                    smartOne[UISettings.DATA].paint(g, UISettings.formWidth - smartOne[UISettings.DATA].width - 3, ( UISettings.headerHeight -smartOne[UISettings.DATA].height)/2, Gob.TRANS_NONE);
                 else
                     smartOne[UISettings.SMS].paint(g, UISettings.formWidth - smartOne[UISettings.SMS].width - 3, ( UISettings.headerHeight -smartOne[UISettings.SMS].height)/2, Gob.TRANS_NONE);
            }
            if (PresenterDTO.getHdrLogo() != null) {
                g.drawImage(PresenterDTO.getHdrLogo(), 0, (UISettings.headerHeight-PresenterDTO.getHdrLogo().getHeight())/2, Graphics.TOP | Graphics.LEFT);
                y = PresenterDTO.getHdrLogo().getWidth();
            } else if(PresenterDTO.isHdGob){
                if(null != images){
                    if (ChannelData.isLeftIconShow()) {
                        int partSize = (UISettings.headerHeight-(images[UISettings.SHD].height+(images[UISettings.SHD].getOffsetY(Gob.TRANS_NONE)*2)));
                        if(partSize<0){
                            partSize += images[UISettings.SHD].getOffsetY(Gob.TRANS_NONE);
                        }
                        if(partSize != 0)
                            partSize = partSize/2;
                        images[UISettings.SHD].paint(g,0,partSize,Gob.TRANS_NONE);
                        y = images[UISettings.SHD].width;
                    }
                } else {
                    g.setColor(0xB8B8B8);
                    g.fillRect(0,0,5,UISettings.headerHeight);
                    y = 5;
                }
            }
            g.setColor(0x554344);
            if (PresenterDTO.getHdrtxt() != null) {
                g.drawString(PresenterDTO.getHdrtxt(), y + 2, (UISettings.headerHeight-font.getHeight())/2, Graphics.TOP | Graphics.LEFT);
            }
        }
    }


    public static void startChatNotificationTimer(){
        if(null == chatNotificationTimer){
            chatNotificationTimer = new Timer();
            chatNotificationTimer.schedule(new TimerTask() {
                public void run() {
                    if(chatNotification.size()>0){
                        if(null == chatNotificationText){
                            String[] chatMsg = null;
                            synchronized(chatNotification){
                                chatMsg =  (String[])chatNotification.elementAt(0);
                                chatNotification.removeElementAt(0);
                            }
                            chatNotificationImage = RecordManager.getImage(chatMsg[1]);
                            chatNotificationText = chatMsg[0];
                            //Hema CR 12318

                            //chatNotificationText = getSecondaryHeader(chatNotificationText,"",UISettings.formWidth - chatNotificationImage.getWidth() - 2 - images[UISettings.DATA].width);
                            //chatNotificationText = chatNotificationText.substring(0, chatNotificationText.length()-2);
                            //chatNotificationText = Utilities.splitText(chatNotificationText, y + 2, UISettings.formWidth - 2 - images[UISettings.DATA].width, UISettings.headerHeight, font)[0];
                        } else chatNotificationText = null;
                    } else {
                        chatNotificationText = null;
                        this.cancel();
                        chatNotificationTimer = null;
                    }
                    ShortHandCanvas.IsNeedPaint();
                }
            },0,3000);
        }
    }

    public static void updateChatNotification(String[] msg){
        if(null == chatNotification){
            chatNotification = new Vector();
        }
        synchronized(chatNotification){
            chatNotification.addElement(msg);
        }
    }

    public static void setSmallFont(){
        UISettings.FOTTER_TEXT_DRAW_POSITION = 0;
        UISettings.MESSAGE_BOX_WIDTH =  0;
        UISettings.MESSAGE_BOX_HEIGHT = 0;
        UISettings.POPUP_HEADER_HEIGHT = (4 + font.getHeight());
        UISettings.POPUP_SCROLL_WIDTH = 5;
        UISettings.FONTSIZE = 8;
        font = Font.getFont(getFontFace(), Font.STYLE_PLAIN, getFontSize()); //Font.getFont(0, 0, 8);//
    }
    
    public static int getFontSize(){
        if (UISettings.FONTSIZE == 0){ // Font Medium
            fontSizeValue =  Font.SIZE_MEDIUM;
        } else if (UISettings.FONTSIZE == 16){ // Font large
            fontSizeValue =  Font.SIZE_LARGE;
        }
        return fontSizeValue;
    }
    
    public static int getFontFace(){
        if(Build.FONTFACE == 0){
            fontFaceValue = Font.FACE_SYSTEM;
        }else if(Build.FONTFACE == 32){
            fontFaceValue = Font.FACE_MONOSPACE;
        }
        return fontFaceValue;
     }
    
    public static int drawSHIcon(Graphics g,int yPos){
        if(null != images){
            images[UISettings.SHD].paint(g, 0,yPos+((UISettings.itemHeight - images[UISettings.SHD].width) / 2), Gob.TRANS_NONE);
            return images[UISettings.SHD].width;
        } else {
            g.setColor(0xFFFFFF);
            g.fillRect(0, yPos+((UISettings.itemHeight - images[UISettings.SHD].width) / 2), 5,UISettings.headerHeight);
        }
        return 5;
    }

    public static void drawSHIcon(Graphics g,int xPos, int yPos){
        if(null != images){
            short imageHeight=0,imageWidth = 0;
            if(images[UISettings.SHD].width<UISettings.GRID_IMAGE_HEIGHT){
                imageWidth = (short)((UISettings.GRID_IMAGE_HEIGHT-images[UISettings.SHD].width)/2);
            }
            if(images[UISettings.SHD].height<UISettings.GRID_IMAGE_HEIGHT){
                imageHeight =(short)((UISettings.GRID_IMAGE_HEIGHT-images[UISettings.SHD].height)/2);
            }
            images[UISettings.SHD].paint(g, xPos+imageWidth,yPos+imageHeight, Gob.TRANS_NONE);
        } else {
            g.setColor(0xFFFFFF);
            g.fillRect(xPos, yPos, UISettings.GRID_IMAGE_HEIGHT,UISettings.GRID_IMAGE_HEIGHT);
        }
    }

    public static void drawChatUnreadNotificationImage(Graphics g,int xPos,int yPos,int count,int width, int height){
        if(null != images && count>0){
            int circle=0;
            if(height == 0){
                if(null != images){
                    height = images[UISettings.SHD].height;
                    width = images[UISettings.SHD].width;
                } else {
                    height = UISettings.GRID_IMAGE_HEIGHT;
                    width = UISettings.GRID_IMAGE_HEIGHT;
                }
            }

            yPos += ((UISettings.GRID_IMAGE_HEIGHT-height)/2)-3;
            xPos += (UISettings.GRID_IMAGE_HEIGHT-width)/2+width;
            if(null != smartOne){
                circle = (smartOne[UISettings.NOTIFICATIONIMAGE].width-3);
                smartOne[UISettings.NOTIFICATIONIMAGE].paint(g,xPos-circle,yPos,Gob.TRANS_NONE);
                if(count>9){
                    count = 10;
                }

                count = UISettings.PENDINGIMAGE+(count-1);
                xPos -= circle;
                circle = (circle-smartOne[count].width)/2;
//                pending = (smartOne[UISettings.NOTIFICATIONIMAGE].height - smartOne[count].height)/2;
                smartOne[count].paint(g,xPos,yPos,Gob.TRANS_NONE);
            }
        }
    }

    /**
     * Method to get secondary header text
     * 
     * @param text Seconday Header Text
     * @return String  Secondary Header Text
     */
    public static String getSecondaryHeader(String headerStr, String headerSStr,int eWidth) {
        Font fonts = Font.getFont(getFontFace(), Font.STYLE_PLAIN, Font.SIZE_SMALL);
        int width = fonts.stringWidth(headerStr + headerSStr) + eWidth;
        if ((UISettings.formWidth - 8) < width) {
            width = fonts.stringWidth(headerSStr)+eWidth;
            width = UISettings.formWidth - (width + 8);
            int len = headerStr.length();
            while (fonts.stringWidth(headerStr.substring(0, len)) > width) {
                len--;
            }
            headerStr = headerStr.substring(0, len - 3) + "...";
        }

        return headerStr;
    }

    /**
     * Method to draw the secondary header
     * 
     * @param headerText Secondary Header Text
     * @param lcount     Secondary header count
     * @param g
     */
    
    public static void drawSecondaryHeader(String lcount, Graphics g, boolean isWhite, boolean isPlusIcon) {
        if (null != sHeader && sHeader.length()>0) {

            if(isWhite){
                if (null != PresenterDTO.getBgImage()) {
                    redrawBackground(g);
                } else if(PresenterDTO.isBgGob){
                    g.setColor(gobRGB[0]);
                    g.fillRect(0, UISettings.headerHeight, UISettings.formWidth, UISettings.secondaryHeaderHeight);
    //                if(null != images){
    //                    images[UISettings.BG].p
    //                   images[UISettings.BG].paint(g, (UISettings.formWidth >> 1) - ((images[UISettings.BG].getWidth(Gob.TRANS_NONE) >> 1) - images[UISettings.BG].getOffsetX(Gob.TRANS_NONE)),
    //                  (UISettings.formHeight >> 1) - ((images[UISettings.BG].getHeight(Gob.TRANS_NONE) >> 1) - images[UISettings.BG].getOffsetY(Gob.TRANS_NONE)), Gob.TRANS_NONE);
    //                }
                }
            } else {
                g.setColor(0xFFFFFF);
                g.fillRect(0, UISettings.headerHeight, UISettings.formWidth, UISettings.secondaryHeaderHeight);
            }

//            if(null != images){
//                images[UISettings.HD].paint(g, 0, UISettings.headerHeight, Gob.TRANS_NONE);
//            } else {
//                g.setColor(0xB8B8B8);
//                g.fillRect(0,UISettings.headerHeight,UISettings.formWidth,UISettings.secondaryHeaderHeight);
//            }
            //CR 12854
            if(isWhite)
                g.setColor(0xFFFFFF);
            else g.setColor(0x000000);
            g.setFont(Font.getFont(getFontFace(), Font.STYLE_PLAIN, Font.SIZE_SMALL));

            //CR 12854
            int xPosition = g.getFont().stringWidth(sHeader);
            if (null != lcount && lcount.length()>0) {
                g.drawString(lcount, UISettings.formWidth - (UISettings.POPUP_SCROLL_WIDTH+2),
                        (UISettings.headerHeight) + ((UISettings.secondaryHeaderHeight -
                        g.getFont().getHeight()) / 2), Graphics.TOP | Graphics.RIGHT);
                xPosition += g.getFont().stringWidth(lcount)+2;
            }
            
            xPosition = (UISettings.formWidth - (UISettings.POPUP_SCROLL_WIDTH + xPosition))/2;
            
            g.drawString(sHeader, xPosition, (UISettings.headerHeight) +
                    ((UISettings.secondaryHeaderHeight - g.getFont().getHeight()) / 2),
                    Graphics.TOP | Graphics.LEFT);

            //Cr 12882
            g.setColor(0x828282);
            g.drawLine(5, UISettings.headerHeight+UISettings.secondaryHeaderHeight-1,
                    UISettings.formWidth-10, UISettings.headerHeight+UISettings.secondaryHeaderHeight-1);

            g.setFont(font);

            //CR 14172
            if(isPlusIcon){
                if(null == mIcon){
                    try{
                        byte[] imagebytes = DownloadHandler.getInstance().getResourcesBytes("micon",false);
                        mIcon = Image.createImage(imagebytes, 0, imagebytes.length);
                    }catch(Exception exception){

                    }
                }
                if(null != mIcon){
                    int partSize = UISettings.secondaryHeaderHeight+ ((UISettings.headerHeight-mIcon.getHeight())/2);
                    g.drawImage(mIcon, UISettings.formWidth-(mIcon.getWidth()+UISettings.POPUP_SCROLL_WIDTH+5), partSize, Graphics.TOP|Graphics.LEFT);
                    //g.drawImage(mIcon,0, partSize, Graphics.TOP|Graphics.LEFT);

                }
            }
        }
    }

    public static void redrawBackground(Graphics g){
        int[] color = new int[1];
        PresenterDTO.getBgImage().getRGB(color, 0, 1, 1, 1, 1, 1);
        g.setColor(color[0]);
        g.fillRect(0, UISettings.headerHeight, UISettings.formWidth, UISettings.secondaryHeaderHeight);
        color[0] = ((UISettings.formHeight-PresenterDTO.getBgImage().getHeight())/2)-
                (UISettings.headerHeight+UISettings.secondaryHeaderHeight);
        if(color[0]<0){
            color[0] *= -1;
            g.drawRegion(PresenterDTO.getBgImage(), 0, 0, PresenterDTO.getBgImage().getWidth(),
                    color[0], Gob.TRANS_NONE, (UISettings.formWidth-PresenterDTO.getBgImage().getWidth())/2,
                    (UISettings.headerHeight+UISettings.secondaryHeaderHeight)-color[0], Graphics.TOP|Graphics.LEFT);
        }
    }

//    public static void drawAppNameHeader(String name, Graphics g){
////        if(null != images){
////            images[UISettings.HD].paint(g, 0, UISettings.headerHeight, Gob.TRANS_NONE);
////        } else {
////            g.setColor(0xB8B8B8);
////            g.fillRect(0,UISettings.headerHeight,UISettings.formWidth,UISettings.secondaryHeaderHeight);
////        }
//        g.setColor(0x554344);
//        g.setFont(font);
//        g.drawString(name, (UISettings.formWidth-(font.stringWidth(name)))/2, (UISettings.headerHeight) +
//                ((UISettings.secondaryHeaderHeight - font.getHeight()) / 2),
//                Graphics.TOP | Graphics.LEFT);
//    }
    
//    /*
//     **/
//    public static int drawEmothic(Graphics g, int id,int x, int y){
//        if(null != eMotic){
//            eMotic[id].paint(g,x,y,Gob.TRANS_NONE);
//            return eMotic[id].width;
//        }
//        return 0;
//    }

    /**
     * Method to draw selected item image
     * 
     * @param mpos  x-coordinate
     * @param yposition     y-coordinate
     * @param g     Instance of Graphics class
     */
    public static void drawSelection(int mpos, int y, Graphics g) {
        if (null != images && null != images[UISettings.IS]) {
            images[UISettings.IS].paint(g, 0, mpos + UISettings.itemHeight * y, Gob.TRANS_NONE);
        } else {
            g.setColor(0x00FF00);
            g.fillRect(0,mpos+ UISettings.itemHeight* y , UISettings.formWidth, UISettings.headerHeight);
        }
    }

    /**
     * Method to draw popup
     * 
     * @param msg  Message that needs to be displayed
     * @param yposition    y-coordinate
     * @param g    Instance of graphics class
     */
//    private void drawPopup(String msg, int y, Graphics g) {
//        g.setColor(0x554344);
//        images[GobSmart.NY].paint(g, UISettings.formWidth / 2 - UISettings.smPWidth / 2, y, Gob.TRANS_NONE);
//    }
    
    public static int drawProcessImage(Graphics g, int rType){
        int size = 0;
        if(null != images && null != images[UISettings.HG]){
            images[UISettings.HG+rType].paint(g, (UISettings.formWidth - images[UISettings.HG].width)/ 2,(UISettings.formHeight -images[UISettings.HG].height)/ 2,0);       
            size = images[UISettings.HG+rType].height;
        }
       return size;
    }
    
    public static void drawMessageSendImage(Graphics g, int rType){
        if(null != images && null != images[UISettings.MSG]){
            images[UISettings.MSG+rType].paint(g, (UISettings.formWidth - images[UISettings.HG].width)/ 2,(UISettings.formHeight -images[UISettings.HG].height)/ 2,0);       }
       
    }
    
//    public static void drawL700IssuePopup(Graphics g){
//        int height = g.getFont().getHeight();
//        int width = g.getFont().stringWidth("Please press center key") + 4;
//        g.setColor(0xffffff);
//        g.fillRect((UISettings.formWidth-width)/2, (UISettings.formHeight-height)/2, width, height-1);//(3*20)+30
//        g.setColor(00000000);
//        g.drawRect((UISettings.formWidth-width)/2, (UISettings.formHeight-height)/2, width, height-1);
//        g.drawString(" Sending message...", ((UISettings.formWidth-width)/2)+(1+images[GobSmart.MSG].width) , ((UISettings.formHeight-(height-g.getFont().getHeight()))/2) +1, Graphics.TOP|Graphics.LEFT);
//    }
    
    /**
     * Method to display the message sending sprite animation for all the screen
     * @param g Standard Graphics
     */
    //BUG ID : 4033 CR: 3518
    public static void drawMessageSendSprit(Graphics g, byte mSendImageRTimer){
        if(mSendImageRTimer>=0){
            if(null != images && null != images[UISettings.MSG]){
                int height = g.getFont().getHeight();
                if(height<images[UISettings.MSG].height)
                    height = images[UISettings.MSG].height + 2;
                int width = images[UISettings.MSG].width + g.getFont().stringWidth(Constants.headerText[15]) + 4;
                g.setColor(0xffffff);
                g.fillRect((UISettings.formWidth-width)/2, (UISettings.formHeight-height)/2, width, height-1);//(3*20)+30
                g.setColor(00000000);
                g.drawRect((UISettings.formWidth-width)/2, (UISettings.formHeight-height)/2, width, height-1);
                images[UISettings.MSG+(mSendImageRTimer/15)].paint(g, ((UISettings.formWidth-width)/2)+1,((UISettings.formHeight-height)/2)+1,0);
                g.drawString(" "+Constants.headerText[15], ((UISettings.formWidth-width)/2)+(1+images[UISettings.MSG].width) , ((UISettings.formHeight-(height-g.getFont().getHeight()))/2) +1, Graphics.TOP|Graphics.LEFT);
            }
        }
    }

    /**
     * Method to get the up/down position
     * 
     * @param text
     * @param isup
     * @param pos
     * @return
     */
    public static int getUpOrDownPos(String text, boolean isup, int pos) {
        int len = -1;
        if(null != text){
            int cWidth = UISettings.formWidth - (2 + font.charWidth('|'));
            String temp = null;
            if (font.stringWidth(text) > cWidth) {
                if (isup) {
                    temp = text.substring(0, pos);
                    if (font.stringWidth(temp) > cWidth) {
                        len = Utilities.getWidthIndex(new StringBuffer(temp).reverse().toString(),
                                cWidth, font);
                    }
                } else {
                    temp = text.substring(pos);
                    if (font.stringWidth(temp) > cWidth) {
                        len = Utilities.getWidthIndex(temp, cWidth, font);
                    } else if (font.stringWidth(temp) > (font.stringWidth(text) % cWidth)) {
                        len = temp.length();
                    }
                }
            }
        }
        return len;
    }

    /**
     * 
     * @param text
     * @param width
     * @return
     */
    private static String[] getSplitTextBoxText(String text, int width) {
        String[] value = null;
        if (font.stringWidth(text) <= width) {
            value = new String[]{text};
        } else {
            value = new String[5];
            int len = 0, index = 0;
            while (text.length() > 0) {
                if (len >= value.length) {
                    value = Utilities.incrementStringArraySize(value, len, len + 5);
                }
                index = Utilities.getWidthIndex(text, width, font);
                value[len++] = text.substring(0, index);
                text = text.substring(index);
            }
            if (len > 0 && len < value.length) {
                value = Utilities.incrementStringArraySize(value, len, len);
            }
        }
        return value;
    }

    /**
     * 
     * @param text
     * @param cursor
     * @param permanentString
     * @param yposition
     * @param g
     */
    public static void drawTextBox(int borderColor, String text, char cursor,
            String permanentString, int y, int curPos, short textboxSize,
            Graphics g, int nLine, int textColor)throws Exception{
        int borderStart = 1;
        //CR 12541
        if(textColor == -1){
            g.setFont(font); //bug id 6147
        } else {
            g.setFont(Font.getFont(getFontFace(), Font.STYLE_PLAIN, Font.SIZE_SMALL));
            if(borderColor != textColor)
                borderStart = 3;
        }
        g.setColor(borderColor);
        g.fillRect(0, y, UISettings.formWidth, textboxSize);
        
        g.setColor(0xffffff);
        g.fillRect(borderStart, y+borderStart, UISettings.formWidth - (borderStart*2), textboxSize - (borderStart*2));
        if(textColor>-1)
            g.setColor(textColor);
        else g.setColor(1, 3, 2);
        if (nLine>1) {
            MultiLineTextbox(text, cursor, y, curPos, g);
        } else {
            singleTextbox(text, cursor, permanentString, y,
                    curPos, g,textboxSize, borderStart);
        }
        g.setFont(font);
    }

    /**
     * 
     * @param text
     * @param cursor
     * @param permanentString
     * @param yposition
     * @param curPos
     * @param g
     */
    private static void singleTextbox(String text, char cursor, String permanentString, 
            int y, int curPos, Graphics g, int textBoxHeight, int padding) {
        int i = g.getFont().charWidth('|');
        String[] value = getSplitTextBoxText(text, UISettings.formWidth - (2 + i));
        i = 0;
        int pval = curPos;
        while (curPos > 0) {
            if (value.length > i) {
                pval = curPos;
                curPos -= value[i++].length();
            }
        }
        if (i > 0) {
            i--;
        }

        y += ((textBoxHeight - g.getFont().getHeight()) / 2);
        //y += 4;

        if (null != permanentString) {
            value[i] += "-" + permanentString;
        }
        curPos = padding+2;
        g.drawString(value[i].substring(0, pval), curPos, y, Graphics.TOP | Graphics.LEFT);
        curPos += g.getFont().stringWidth(value[i].substring(0, pval));
        g.drawChar(cursor, curPos, y, Graphics.TOP | Graphics.LEFT);
        curPos += g.getFont().charWidth('|');
        g.drawString(value[i].substring(pval), curPos, y, Graphics.TOP | Graphics.LEFT);
        value = null;
    }

    /**
     * 
     * @param text
     * @param cursor
     * @param yposition
     * @param curPos
     * @param g
     */
    private static void MultiLineTextbox(String text, char cursor, int y, int curPos, Graphics g) {
        int calWidth = (UISettings.formWidth - (2 + font.charWidth('|')));
        y += 4;
        String temp = null;
        if (font.stringWidth(text) > calWidth) {
            int len = 0;
            temp = text.substring(0, curPos);
            if (font.stringWidth(temp) <= calWidth) {
                len = Utilities.getWidthIndex(text, calWidth, font);
                g.drawString(text.substring(len), 2, y + (2 + font.getHeight()), Graphics.TOP | Graphics.LEFT);
                text = text.substring(0, len);
            } else if (font.stringWidth(temp) % calWidth != 0) {
                len = Utilities.getWidthIndex(new StringBuffer(temp).reverse().toString(), font.stringWidth(temp) % calWidth, font);
                len++;
                curPos -= len;
                g.drawString(text.substring(0, curPos), calWidth + 1, y, Graphics.TOP | Graphics.RIGHT);
                text = text.substring(curPos);
                curPos = len;
                y += (4 + font.getHeight());
            }
        }
        temp = text.substring(0, curPos);
        calWidth = 2;
        g.drawString(temp, calWidth, y, Graphics.TOP | Graphics.LEFT);
        calWidth += font.stringWidth(temp);
        g.drawChar(cursor, calWidth, y, Graphics.TOP | Graphics.LEFT);
        calWidth += g.getFont().charWidth('|');
        g.drawString(text.substring(curPos), calWidth, y, Graphics.TOP | Graphics.LEFT);
    }

    /**
     * 
     * @param bannerText
     * @param style
     * @param isSelected
     * @param g
     */
    public static void drawBanner(String bannerText, byte style, boolean isSelected, Graphics g,boolean isBannerMove) {
        if (isSelected) {
            g.setColor(0x00ca00);
        } else {
            g.setColor(0xffff80);
        }

        int y = UISettings.formHeight - (UISettings.footerHeight + UISettings.itemHeight);
        g.fillRect(0, y, UISettings.formWidth, UISettings.itemHeight);
        g.setColor(1, 3, 2);
        int x = 0;
        if (style == 0 || style == 2) {
            x = font.stringWidth(bannerText);
            x = UISettings.formWidth / 2 - x / 2;
        }
        if (style == 1 || style == 3) {
            x = font.stringWidth(bannerText);
            if(isBannerMove){
                if (bannerX + x > 0) {
                    bannerX -= 2;
                } else {
                    bannerX = UISettings.formWidth;
                }
            }
            x = bannerX;
        }
        g.drawString(bannerText, x, y + ((UISettings.itemHeight - font.getHeight()) / 2), Graphics.TOP | Graphics.LEFT);
    }

    /**
     * 
     * @param text
     * @param g
     */
    public static void drawNotification(Graphics g, boolean isNotification) {
        try{
            if(null != notText){
                int nHeight = UISettings.smpHeight;
                int x = (UISettings.formWidth / 2) - (UISettings.smPWidth / 2);
                String[] text = Utilities.splitText(notText, 0, UISettings.smPWidth, UISettings.smpHeight, font);
                int count = text.length;
                int y = ((count+1)*font.getHeight()) + (count*2);
                if(y>UISettings.smpHeight)
                    nHeight = y;
                y = UISettings.formHeight - (UISettings.footerHeight + nHeight)-2;
                int size = 15;
                g.setColor(0xe8e8e8);
                g.fillRoundRect(x, y, UISettings.smPWidth, nHeight, size, size);
                g.setColor(0x8cc63f);
                g.drawRoundRect(x-1, y, UISettings.smPWidth + 2, nHeight, size, size);
                g.drawRoundRect(x, y+1, UISettings.smPWidth, nHeight-2, size-1, size-1);

//                g.setColor(0x8cc63f);
//                g.fillRoundRect(x-1, y-1, UISettings.smPWidth+2, nHeight+2, size, size);
//                g.setColor(0xe8e8e8);
//                g.fillRoundRect(x+1, y+1, UISettings.smPWidth-2, nHeight-2, size, size);
//                g.setColor(0x8cc63f);
                g.drawLine(x, y+(g.getFont().getHeight()), x+UISettings.smPWidth, y+(g.getFont().getHeight()));
                g.drawLine(x, y+(g.getFont().getHeight()+1), x+UISettings.smPWidth, y+(g.getFont().getHeight()+1));
                g.setColor(0x554344);
                if(isNotification){
                    g.drawString(Constants.headerText[30], x+(UISettings.smPWidth-font.stringWidth(Constants.headerText[30]))/2, y+1, Graphics.TOP|Graphics.LEFT);
                }
                y+=(font.getHeight()+2);
                for(int i=0;i<count;i++,y+=(font.getHeight()+2)){
                    g.drawString(text[i],x , y, Graphics.TOP|Graphics.LEFT);
                }
            }
        }catch(Exception e){ }
    }

    /**
     * 
     * @param keycode
     */
    public static void travelMessageBox(int keycode) {
        float totalSize = (-1*(msgText.length-mplineIndex)*(font.getHeight()+2));
        if (keycode == UISettings.DOWNKEY) {
            if ((yStratPosition-(font.getHeight()+2)) > totalSize) {
                yStratPosition -= (font.getHeight()+2);
                if(yStratPosition<totalSize)
                    yStratPosition = totalSize;
            } else yStratPosition = 0;
        } else if (keycode == UISettings.UPKEY) {
            if(yStratPosition<0){
                yStratPosition += (font.getHeight()+2);
                if(yStratPosition>0)
                    yStratPosition = 0;
            } else {
                yStratPosition = totalSize;
            }
        }
    }

    /**
     * 
     */
    public static void resetMessageText() {
        yStratPosition = 0;
        scrollLength = -1;
        mplineIndex = 0;
        msgText = null;
        msgHeaderText  = null;
        msgBoxText = null;
        msgboxHeight = (short) (UISettings.formHeight - (UISettings.headerHeight+UISettings.footerHeight+((UISettings.secondaryHeaderHeight)*UISettings.MESSAGE_BOX_HEIGHT)));
        msgboxStartingPoint = (short) UISettings.headerHeight;
        if(UISettings.MESSAGE_BOX_HEIGHT > 0)
             msgboxStartingPoint += (short)(UISettings.itemHeight / 2);
    }

    /**
     * 
     * @param text
     */
    public static void setMessageBoxText(String text, String hText) {
        try{
            resetMessageText();
            msgBoxText = text;
            msgText = Utilities.splitText(msgBoxText, 2, UISettings.formWidth - (UISettings.POPUP_SCROLL_WIDTH+UISettings.MESSAGE_BOX_WIDTH), msgboxHeight - (UISettings.POPUP_HEADER_HEIGHT * 2), font);
            mplineIndex = (byte) Utilities.noline;
            if(msgBoxText.length()>2){
                msgboxHeight = (short) ((font.getHeight() * mplineIndex) + (UISettings.POPUP_HEADER_HEIGHT * 2) + (mplineIndex*2));
            } else msgboxHeight = (short) ((font.getHeight() * 3) + (UISettings.POPUP_HEADER_HEIGHT * 2) + (3*2));
            msgboxStartingPoint = (short) ((UISettings.formHeight - msgboxHeight) / 2);
            if (null != hText) {
                msgHeaderText = hText;
            } else {
                msgHeaderText = Constants.headerText[16];
            }

            if(msgText.length>mplineIndex){
                scrollLength = (msgText.length-mplineIndex)*(font.getHeight()+2);
                scrollLength = (msgboxHeight - (UISettings.POPUP_HEADER_HEIGHT * 2)) / scrollLength;
                int separaterCount = getScrollHeight(scrollLength);
                if(separaterCount>-1){
                    scrollLength = (msgText.length-mplineIndex)*(font.getHeight()+2);
                    scrollLength = ((msgboxHeight - (UISettings.POPUP_HEADER_HEIGHT * 2))-separaterCount) / scrollLength;
                }
            }
        }catch(Exception e){
            
        }
    }
    
    public static void rotateMessagebox(){
        if(null != msgBoxText)
            setMessageBoxText(msgBoxText, msgHeaderText);
    }
    
    public static void rotateNotificationPopup(){
        UISettings.smPWidth = (short)(UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH);
        UISettings.smpHeight = (byte)(((font.getHeight()+2)*4)+2);
    }

    
    public static void setMessageHeight(){
        if(msgboxStartingPoint == 0) {
             msgboxStartingPoint = (short)30;
             msgboxHeight = (short) (UISettings.formHeight-50);
        }
    }
    /**
     * 
     * @param g
     * @param loptindex
     * @param roptindex
     * @param msgSel
     */
    public static void drawMessageBox(Graphics g, byte loptindex, byte roptindex, boolean msgSel) {
        try {
            byte msgStartXPosition = (byte)(UISettings.MESSAGE_BOX_WIDTH/2);
            if(null != msgText){
                g.setFont(font);

                //Fill the white color 2 clear the screen
                g.setColor(0xffffff);
                g.fillRect((UISettings.MESSAGE_BOX_WIDTH/2), msgboxStartingPoint,
                        UISettings.formWidth - UISettings.MESSAGE_BOX_WIDTH, msgboxHeight);//(3*20)+30
                
                int pages = 0;
                if(yStratPosition !=0){
                    pages = (int)((-1)*yStratPosition)/(font.getHeight()+2);
                }
                int mpindex = pages;
                int len = mplineIndex + pages;
                if(msgText.length>len){
                    len++;
                }

                int y = (msgboxStartingPoint + UISettings.POPUP_HEADER_HEIGHT);
                g.setColor(00000000);
                if(yStratPosition !=0)
                    pages = (int)(yStratPosition %(font.getHeight()+2));
                y += (1 + pages);
                msgStartXPosition += 2;
                for (int i = mpindex; i < len; i++, y += font.getHeight() + 2) {
                   if(null != msgText[i])
                    g.drawString(msgText[i], msgStartXPosition, y, Graphics.LEFT | Graphics.TOP);
                }

                //Message Box Header color
                g.setColor(0x1E90FF);
                g.fillRect((UISettings.MESSAGE_BOX_WIDTH/2), msgboxStartingPoint, UISettings.formWidth - UISettings.MESSAGE_BOX_WIDTH, UISettings.POPUP_HEADER_HEIGHT);

                g.setColor(0xFFFAFA);
                g.drawString(msgHeaderText, msgStartXPosition+2, msgboxStartingPoint+ ((UISettings.POPUP_HEADER_HEIGHT-g.getFont().getHeight())/2), Graphics.TOP | Graphics.LEFT);

                //Message Text writing
                 //Footer Color
                g.setColor(0xA9A9A9);
                g.fillRect((UISettings.MESSAGE_BOX_WIDTH/2), msgboxStartingPoint + msgboxHeight - UISettings.POPUP_HEADER_HEIGHT, UISettings.formWidth - UISettings.MESSAGE_BOX_WIDTH, UISettings.POPUP_HEADER_HEIGHT);

                if(loptindex>-1){
                    String temp = String.valueOf(Constants.options[loptindex]);
                    int tempX = getMessageBoxLeftOptionDrawPosition(msgSel, loptindex, roptindex);
                    int x1 = 0;
                    byte increasePosition = 0;
                    if(roptindex == -1 && temp.length()<4)
                            increasePosition = 8;
                    if (msgSel) {
                        x1 = tempX - 2;
                        
                    } else {
                        temp = String.valueOf(Constants.options[roptindex]);
                        x1 = (UISettings.formWidth / 2) + 2;
                    }
                    //Option Selection color
                    g.setColor(0xc8c8c8);
                    g.fillRect(x1-increasePosition, msgboxStartingPoint + msgboxHeight - UISettings.POPUP_HEADER_HEIGHT + 1, font.stringWidth(temp) + 4 + (increasePosition*2), UISettings.POPUP_HEADER_HEIGHT-1);

                    //Draw Rectangle
                    g.setColor(0x000000);
                    g.drawRect((UISettings.MESSAGE_BOX_WIDTH/2), msgboxStartingPoint, UISettings.formWidth - (UISettings.MESSAGE_BOX_WIDTH+1), msgboxHeight);
                    g.drawRect((UISettings.MESSAGE_BOX_WIDTH/2), msgboxStartingPoint + UISettings.POPUP_HEADER_HEIGHT, UISettings.formWidth - (UISettings.MESSAGE_BOX_WIDTH+1), (msgboxStartingPoint + msgboxHeight - UISettings.POPUP_HEADER_HEIGHT) - (msgboxStartingPoint + UISettings.POPUP_HEADER_HEIGHT));
                    g.drawString(Constants.options[loptindex], tempX, msgboxStartingPoint + msgboxHeight - ((UISettings.POPUP_HEADER_HEIGHT+g.getFont().getHeight())/2), Graphics.TOP | Graphics.LEFT);
                    
                }

                if (roptindex > -1) {
                    g.drawString(Constants.options[roptindex],  UISettings.formWidth / 2 + 4,
                            msgboxStartingPoint + msgboxHeight - ((UISettings.POPUP_HEADER_HEIGHT+g.getFont().getHeight())/2), Graphics.TOP | Graphics.LEFT);
                }
                
            } else {
                g.setColor(00000000);
            }

            if(scrollLength>-1){
                drawScroll(g, scrollLength, msgboxStartingPoint + UISettings.POPUP_HEADER_HEIGHT, (-1*yStratPosition*scrollLength),
                        (msgboxHeight - (UISettings.POPUP_HEADER_HEIGHT * 2)),
                        UISettings.formWidth-msgStartXPosition);
            }

        }catch(Exception e){
            Logger.loggerError("draw Message Box" + e.toString());
        }
    }
    
    private static int getMessageBoxLeftOptionDrawPosition(boolean msgSel,byte loptindex,byte roptindex){
        String temp = String.valueOf(Constants.options[loptindex]);
        int tempX = font.stringWidth(temp);
        if (msgSel) {
            if (roptindex == -1) {
                tempX = UISettings.formWidth / 2 - tempX / 2;
            } else {
                tempX = UISettings.formWidth / 2 - (2 + tempX);
            }
        } else {
            tempX = UISettings.formWidth / 2 - (2 + tempX);
        }
        return tempX;
    }


    public static byte isMessageSelected(int xPosition, int yposition, 
            byte loptindex, byte roptindex, boolean isNotDrag, boolean isDragEnd, boolean isPressed){
        byte isSelected = -1;
        yposition -= msgboxStartingPoint;
        xPosition -= (UISettings.MESSAGE_BOX_WIDTH/2);
        if(yposition>UISettings.POPUP_HEADER_HEIGHT && yposition<msgboxHeight
                && xPosition>0 &&
                xPosition<(UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH)){
            //CR 13040
            if(isNotDrag || isPressed){
                previousY = yposition;
                yposition -= UISettings.POPUP_HEADER_HEIGHT;
                if(isPressed && CustomCanvas.isShowScroll && 
                        (UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH)-CustomCanvas.SCROLL_WIDTH<=xPosition &&
                        ((-1*yStratPosition*scrollLength)) <= yposition
                        && ((-1*yStratPosition*scrollLength)+CustomCanvas.SCROLL_WIDTH) >= yposition){
                    iSScrollEnabled = true;
                } else if(yposition>=(msgboxHeight-(UISettings.POPUP_HEADER_HEIGHT*2))){
                    byte increaseHeight = 2;
                    if(loptindex>-1){
                        int leftOptionStringWidth = font.stringWidth(String.valueOf(Constants.options[loptindex]));
                        int leftOptionXPosition = 0;
                        int rightOptionXPosition = 0;
                        int rightOptionStringWidth = 0;
                        if(roptindex == -1){
                            leftOptionXPosition = ((UISettings.formWidth-leftOptionStringWidth)/2)-8;
                            increaseHeight = 8;
                        } else {
                            leftOptionXPosition = ((UISettings.formWidth/2)-(2+leftOptionStringWidth));
                            rightOptionStringWidth = font.stringWidth(String.valueOf(Constants.options[roptindex]));
                            rightOptionXPosition = (UISettings.formWidth/2)+2;
                        }

                        xPosition += (UISettings.MESSAGE_BOX_WIDTH/2);
                        if(xPosition>= leftOptionXPosition && xPosition<=(leftOptionXPosition + leftOptionStringWidth + (increaseHeight*2))){
                            isSelected = 0;
                        } else if(xPosition>=rightOptionXPosition && xPosition<=(rightOptionStringWidth+rightOptionXPosition+ (increaseHeight*2))){
                            isSelected = 1;
                        }
                    }
                }
            } else if(scrollLength>-1 &&
                    yposition<(msgboxHeight-UISettings.POPUP_HEADER_HEIGHT)){
                showScroll(isDragEnd);
                int totalNumberOfPage = (msgText.length-mplineIndex);
                totalNumberOfPage *= (-1*(font.getHeight()+2));
                if(iSScrollEnabled){
                    float position = (totalNumberOfPage/(totalNumberOfPage*scrollLength));
                    if((yposition-previousY) != 0 && position>0)
                        yStratPosition -= (yposition-previousY)*position;
                }  else yStratPosition += yposition - previousY;

                if(yStratPosition>0)
                    yStratPosition = 0;
                else if(yStratPosition<totalNumberOfPage){
                    yStratPosition = totalNumberOfPage;
                }
                previousY = yposition;
            }
        }
        if(isDragEnd)
            iSScrollEnabled = false;
        return isSelected;
    }
    
    /**
     * Method to draw the scroll bar for all the screen.
     * @param g Graphics for the screen
     * @param scrollLen Lenght of the scroll bar(blue)
     * @param scrollSPos Scroll bar start position
     * @param startIndex Number item selected
     * @param fHeight total form height
     */
    public static void drawScroll(Graphics g,float scrollLen,int scrollSPos,
            float startIndex,int fHeight, int xPosition){
        g.setColor(0xc8c8c8);
        g.fillRect(xPosition - UISettings.POPUP_SCROLL_WIDTH, scrollSPos, UISettings.POPUP_SCROLL_WIDTH, fHeight);

        // Scroll Colour - blue
        //g.setColor(0x483d8b);
        //Green Color
        g.setColor(0x8dc63f);
        g.fillRect(xPosition - UISettings.POPUP_SCROLL_WIDTH,(int)(scrollSPos+startIndex),
                UISettings.POPUP_SCROLL_WIDTH, smartOne[UISettings.SCROLL].height);

        // Border Colour
        g.setColor(00000000);
        g.drawRect(xPosition-UISettings.POPUP_SCROLL_WIDTH, scrollSPos, UISettings.POPUP_SCROLL_WIDTH, fHeight);

        if(isShowScroll) {
            if(scrollLen<smartOne[UISettings.SCROLL].height){
                smartOne[UISettings.SCROLL].paint(g, xPosition-smartOne[UISettings.SCROLL].width,
                        (int)(scrollSPos+startIndex), Gob.TRANS_NONE);
            } 
        }
    } 
    
    
    public static void setPopupMessage(String msg){
        popText = Utilities.splitText(msg, 2, UISettings.formWidth - (UISettings.MESSAGE_BOX_WIDTH/2 + UISettings.POPUP_SCROLL_WIDTH), msgboxHeight - UISettings.POPUP_HEADER_HEIGHT, font);
        if(popText.length>2){
            UISettings.popupHeight = (short) ((font.getHeight() * popText.length) + (2*popText.length)+ UISettings.POPUP_HEADER_HEIGHT);
        } else UISettings.popupHeight = (short) ((font.getHeight() * 3) + (2*3)+ UISettings.POPUP_HEADER_HEIGHT);
    }

    public static void showPopup(int y, Graphics g) {
        g.setColor(0xffffff);
        g.fillRect(UISettings.MESSAGE_BOX_WIDTH/2, y, UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH, UISettings.popupHeight);

        g.setColor(0x5858FA);
        g.fillRect(UISettings.MESSAGE_BOX_WIDTH/2, y, UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH, UISettings.POPUP_HEADER_HEIGHT);

        g.setColor(0xffffff);
        g.drawString(Constants.headerText[8], (UISettings.MESSAGE_BOX_WIDTH/2)+2, y + (UISettings.POPUP_HEADER_HEIGHT-g.getFont().getHeight())/2, Graphics.TOP | Graphics.LEFT);

        g.setColor(00000000);
        g.drawRect(UISettings.MESSAGE_BOX_WIDTH/2, y, UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH , UISettings.popupHeight - 1);
        g.drawRect(UISettings.MESSAGE_BOX_WIDTH/2, y, UISettings.formWidth-UISettings.MESSAGE_BOX_WIDTH , UISettings.POPUP_HEADER_HEIGHT);

        y += UISettings.POPUP_HEADER_HEIGHT ;//+ UISettings.POPUP_SCROLL_WIDTH;
        drawSmartpoupText((UISettings.MESSAGE_BOX_WIDTH/2)+2, y, g);
    }

    private static void drawSmartpoupText(int x,int y,Graphics g){
        if(null != popText){
            int len = popText.length;
            for(int i=0;i<len;i++,y += (font.getHeight() + 2)){
                g.drawString(popText[i], x, y, Graphics.TOP | Graphics.LEFT);
            }
        }
    }
    
    /**
     * @param keyMode Type String
     * @param loptindex
     * @param roptindex
     * @param g
     */
    public static void DrawOptionsMenu(String kmT, byte loptindex, byte roptindex, Graphics g) {
        if(null != images){
            images[UISettings.HD].paint(g, 0, UISettings.formHeight - UISettings.footerHeight, Gob.TRANS_NONE);
        } else {
            g.setColor(0xB8B8B8);
            g.fillRect(0,UISettings.formHeight - UISettings.footerHeight,UISettings.formWidth,UISettings.headerHeight);
        }
        g.setColor(0x232323);

        //Left option String
        //bug id 5211
        if (loptindex > -1) {
            g.drawString(Constants.options[loptindex], UISettings.FOTTER_TEXT_DRAW_POSITION, UISettings.formHeight - ((UISettings.footerHeight + font.getHeight()) / 2), Graphics.TOP | Graphics.LEFT);
        }
        g.drawString(kmT, UISettings.formWidth / 2 - font.stringWidth(kmT) / 2,
                UISettings.formHeight - ((UISettings.footerHeight + font.getHeight()) / 2), Graphics.TOP | Graphics.LEFT);

        //Right Option String
        //bug id 5211
        if (roptindex > -1) {
            g.drawString(Constants.options[roptindex], UISettings.formWidth - UISettings.FOTTER_TEXT_DRAW_POSITION,
                    UISettings.formHeight - ((UISettings.footerHeight + font.getHeight()) / 2), Graphics.TOP | Graphics.RIGHT);
        }
    }
    
    public static boolean isOptionSelected(int xPosition, int yPosition, 
            boolean isNotDrag, boolean isPressed){
        float scrollLenght = -1;
        int availableSpace = UISettings.formHeight - (UISettings.headerHeight+UISettings.footerHeight);
        if(optionsItem<bopts.length){
            scrollLenght = (bopts.length-(optionsItem+1));
            scrollLenght = (availableSpace/ scrollLenght);
        }
        
        availableSpace = (UISettings.formHeight - UISettings.footerHeight) -
                (optionsItem*UISettings.POPUP_HEADER_HEIGHT);
        
        if(yPosition<(UISettings.formHeight-UISettings.footerHeight) &&
                yPosition>availableSpace && xPosition<=(UISettings.optWidth+2)){
            yPosition -= availableSpace;
            if(isNotDrag || isPressed){ //CR 13030
                previousY = yPosition;
                xPosition = yPosition/UISettings.POPUP_HEADER_HEIGHT;
                if(xPosition<optionsItem){
                    //CR 13040
                    if(isNotDrag && optai == xPosition)
                        isNotDrag = true;
                    else isNotDrag = false;
                    optai = (byte)xPosition;
                }  
            } else if(scrollLenght>-1){
                yStratPosition += yPosition-previousY;
                if(yStratPosition>0)
                    yStratPosition = 0;
                else if((yStratPosition+1)<(-1*(bopts.length-optionsItem)*UISettings.POPUP_HEADER_HEIGHT)){
                    yStratPosition = (-1*(bopts.length-optionsItem)*UISettings.POPUP_HEADER_HEIGHT)-1;
                }
                previousY = yPosition;
            }
        } else isNotDrag = false;
        return isNotDrag;
    }

    /**
     * 
     * @param array
     */
    public static void setOptionsMenuArray(byte[] array) {
        optai = 0;
        optionsItem = 0;
        bopts = array;
        UISettings.optWidth = 0;
        yStratPosition = 0;
        if (null != bopts) {
            int cwidth = 0;
            int len = bopts.length;
            String opStr = null;
            for (int i = 0; i < len; i++) {
                opStr = String.valueOf(Constants.options[bopts[i]]);
                if (font.stringWidth(opStr) > cwidth) {
                    cwidth = font.stringWidth(opStr);
                }
            }
            if (UISettings.formWidth <= cwidth) {
                UISettings.optWidth = (short) (UISettings.formWidth - 3);
            } else {
                UISettings.optWidth = (short) (cwidth + 3);
            }
            cwidth = UISettings.formHeight - (UISettings.headerHeight+UISettings.footerHeight);
            
            optionsItem = (short)(cwidth/UISettings.POPUP_HEADER_HEIGHT);
            if(optionsItem>len)
                optionsItem = (short)len;
        }
    }
    
    public static void rotateOptionMenu(){
        if(null != bopts)
            setOptionsMenuArray(bopts);
//        if(null != bopts){
//            if(bopts.length>0){
//                for(int i=0;i<bopts.length;i++){
//                    Constants.options[bopts[i]] = optionValues[i];
//                }
//            }
//            
//        }
    }

    /**
     * 
     * @param g
     */
    public static void showOptionsPopup(Graphics g) {
        if (null != bopts) {
            g.setColor(0xc8c8c8);
            int y = UISettings.formHeight - (UISettings.headerHeight+UISettings.footerHeight);
            
            y -= ((optionsItem*UISettings.POPUP_HEADER_HEIGHT)-UISettings.footerHeight);
            
            g.fillRect(0, y, UISettings.optWidth+2, optionsItem * UISettings.POPUP_HEADER_HEIGHT);

            g.setColor(0xDA8505);
            g.fillRect(0, y + (optai * UISettings.POPUP_HEADER_HEIGHT), UISettings.optWidth+2, UISettings.POPUP_HEADER_HEIGHT);

            g.setColor(0x232323);
            int len = (UISettings.POPUP_HEADER_HEIGHT-g.getFont().getHeight())/2;
            int optionsStartIndex = 0;
            if(yStratPosition != 0){
                optionsStartIndex = (int)(-1*yStratPosition)/UISettings.POPUP_HEADER_HEIGHT;
            }
            for (int i = 0; i < optionsItem; i++) {
                g.drawString(Constants.options[bopts[i+optionsStartIndex]], 3, y + len,Graphics.LEFT | Graphics.TOP);
                y += UISettings.POPUP_HEADER_HEIGHT;
            }
        }
    }

    /**
     * 
     * @return
     */
    public static byte getSelectedOption() {
        int optionsStartIndex = 0;
        if(yStratPosition != 0){
            optionsStartIndex = (int)(-1*yStratPosition)/UISettings.POPUP_HEADER_HEIGHT;
        }
        return bopts[optai+optionsStartIndex];
    }

    /**
     * 
     * @return
     */
    public static byte getSelectedOptionIndex() {
        int optionsStartIndex = 0;
        if(yStratPosition != 0){
            optionsStartIndex = (int)(-1*yStratPosition)/UISettings.POPUP_HEADER_HEIGHT;
        }
        return (byte)(optai+optionsStartIndex);
    }

    /**
     * 
     * @param keyCode
     */
    public static void traverseOptionsMenu(int keyCode) {
        if (keyCode == UISettings.UPKEY) {
            if(optai == 0){
                if(yStratPosition<0){
                    yStratPosition += UISettings.POPUP_HEADER_HEIGHT;
                } else {
                    optai = (byte)(optionsItem-1);
                    if(optionsItem<bopts.length){
                        yStratPosition = ((-1)*(bopts.length-optionsItem)*UISettings.POPUP_HEADER_HEIGHT);
                    } 
                }
            } else if(optai>0){
                optai--;
            }
        } else if (keyCode == UISettings.DOWNKEY) {
            if((optai+1)<optionsItem){
                optai++;
            } else if( (-1*(bopts.length-optionsItem)*UISettings.POPUP_HEADER_HEIGHT)<yStratPosition &&
                    (yStratPosition-UISettings.POPUP_HEADER_HEIGHT) >=
            (-1*(bopts.length-optionsItem)*UISettings.POPUP_HEADER_HEIGHT)){
                yStratPosition -= UISettings.POPUP_HEADER_HEIGHT;
            } else {
                yStratPosition = 0;
                optai = 0;
            }
        }
    }

    public static void drawSplash(Graphics g){
        if(null != splash){
            g.setColor(splashRGB[0]);
            g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
            splash[0].paint(g, (UISettings.formWidth >> 1) - ((splash[0].getWidth(Gob.TRANS_NONE) >> 1) - splash[0].getOffsetX(Gob.TRANS_NONE)),
           (UISettings.formHeight >> 1) - ((splash[0].getHeight(Gob.TRANS_NONE) >> 1) - splash[0].getOffsetY(Gob.TRANS_NONE)), Gob.TRANS_NONE);
        }
    }
    
    /**
     * 
     * @param g
     * @param sHeader
     * @param bText
     */
    public static void drawBackgroundImage(Graphics g) {
        try{
            if (null != PresenterDTO.getBgImage()) {
                int[] color = new int[1];
                PresenterDTO.getBgImage().getRGB(color, 0, 1, 0, 0, 1, 1);
                g.setColor(color[0]);
                g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
                g.drawImage(PresenterDTO.getBgImage(),  UISettings.formWidth/2, UISettings.formHeight/2, Graphics.HCENTER | Graphics.VCENTER);
            } else if(PresenterDTO.isBgGob){
                g.setColor(gobRGB[0]);
                g.fillRect(0, 0, UISettings.formWidth, UISettings.formHeight);
                if(null != images){
                   images[UISettings.BG].paint(g, (UISettings.formWidth >> 1) - ((images[UISettings.BG].getWidth(Gob.TRANS_NONE) >> 1) - images[UISettings.BG].getOffsetX(Gob.TRANS_NONE)),
                  (UISettings.formHeight >> 1) - ((images[UISettings.BG].getHeight(Gob.TRANS_NONE) >> 1) - images[UISettings.BG].getOffsetY(Gob.TRANS_NONE)), Gob.TRANS_NONE);
                }
            }
        }catch(Exception e){
            Logger.loggerError("CustomCanvas->DrawBackgroundImage-> "+e.toString()+" "+e.getMessage());
        }
    }

    public static void setSymbolPopupScreen(){
        selecteColumn = 0;
        selectedRow = 0;
        if(symColCount == 0){
            int height = (UISettings.formHeight-(UISettings.headerHeight+UISettings.footerHeight+UISettings.secondaryHeaderHeight + UISettings.POPUP_HEADER_HEIGHT));
            symRowcount = (short)(font.getHeight()*UISettings.SYMBOL_BOX_WIDTH);
            symColCount = (short)(font.charWidth('<')*UISettings.SYMBOL_BOX_WIDTH);
            if(symRowcount>symColCount)
                symRowcount = symColCount;
            else symColCount = symRowcount;
            symBoxSize = symColCount;
            symRowcount = (short) (height/symColCount);
            symColCount = (short) ((UISettings.formWidth-(UISettings.MESSAGE_BOX_WIDTH+UISettings.POPUP_SCROLL_WIDTH))/symColCount);
            totalRowCount = (short) (symbols.length/symColCount);  //bug# 5179
            if (symbols.length%symColCount != 0)                   //bug# 5179
            	totalRowCount += 1;

        }
    }
    
    public static void rotateSymbolPopup(){
        if(symColCount != 0){
            symColCount = 0;
            setSymbolPopupScreen();
        }
    }
    
    public static void drawSymbolpopup(Graphics g){
        boolean isMultiPageSymbol = false;
        if(symbols.length>(symColCount*symRowcount)){
            isMultiPageSymbol = true;
        }
        
        byte scrollwidth = UISettings.POPUP_SCROLL_WIDTH;
        int fulwidth = symColCount * symBoxSize;
        int symScreenHeight = (symBoxSize * symRowcount) + UISettings.POPUP_HEADER_HEIGHT+1;
        int yPosition = ((UISettings.formHeight-symScreenHeight)/2);
        int xPosition = ((UISettings.formWidth-(UISettings.MESSAGE_BOX_WIDTH/2))-fulwidth)/2;
        if(!isMultiPageSymbol){
            xPosition +=(UISettings.POPUP_SCROLL_WIDTH/2);
            scrollwidth = 0;
        }
        
        
        //Fill the white color 2 clear the screen
        //g.setColor(0xffffff);
        g.setColor(00000000); //black color
        g.fillRect(xPosition, yPosition, fulwidth+scrollwidth, symScreenHeight);//(3*20)+30
        
        //Message Box Header color
        g.setColor(0x1E90FF);
        g.fillRect(xPosition, yPosition, fulwidth+scrollwidth, UISettings.POPUP_HEADER_HEIGHT);
        
        //symbolHeader Text
        g.setColor(0xFFFAFA);
        g.drawString(Constants.headerText[17]+"..", xPosition+2, yPosition + ((UISettings.POPUP_HEADER_HEIGHT-g.getFont().getHeight())/2), Graphics.TOP | Graphics.LEFT);
        
        g.setColor(0xffffff);
        g.drawRect(xPosition, yPosition, fulwidth+scrollwidth, symScreenHeight);
        
        if(isMultiPageSymbol){
            int slen = (symbols.length / (symColCount*symRowcount));
            if ((symbols.length / (symColCount*symRowcount)) != 0) {
                slen++;
            }
            slen = (symScreenHeight-UISettings.POPUP_HEADER_HEIGHT) / slen;

            //Scroll Background colour
            g.setColor(0xc8c8c8);
            g.fillRect((xPosition+fulwidth), yPosition+UISettings.POPUP_HEADER_HEIGHT, UISettings.POPUP_SCROLL_WIDTH, symScreenHeight-UISettings.POPUP_HEADER_HEIGHT);
            // Scroll Colour - blue
            g.setColor(0x483d8b);
            g.fillRect((xPosition+fulwidth), yPosition+UISettings.POPUP_HEADER_HEIGHT + ((selectedRow/symRowcount) * slen), UISettings.POPUP_SCROLL_WIDTH, slen);
            // Border Colour
            //g.setColor(00000000);
            g.setColor(0xffffff);
            g.drawRect((xPosition+fulwidth), yPosition+UISettings.POPUP_HEADER_HEIGHT, UISettings.POPUP_SCROLL_WIDTH,symScreenHeight-UISettings.POPUP_HEADER_HEIGHT);
        }
        
        for(int i = 0,count=((selectedRow/symRowcount)*(symColCount*symRowcount))-1,y=yPosition+UISettings.POPUP_HEADER_HEIGHT;i<symRowcount;i++,y+=symBoxSize){
            for(int j=0,k=(symBoxSize/2),l=xPosition;j<symColCount;j++,l+=symBoxSize){
                count++;
                if(symbols.length>count){
                    scrollwidth = (byte)((font.charWidth(symbols[count])/2));
                    if(((selectedRow*symColCount)+selecteColumn) == count){
                        g.setColor(0xFFCC66);
                        g.fillRect(l, y, symBoxSize, symBoxSize);
                        g.setColor(00000000);
                        g.drawChar(symbols[count],l+k-scrollwidth, y+(symBoxSize/2)-((UISettings.POPUP_HEADER_HEIGHT-1)/2), Graphics.LEFT|Graphics.TOP);
                        g.setColor(0xffffff);
                    } else g.drawChar(symbols[count],l+k-scrollwidth, y+(symBoxSize/2)-((UISettings.POPUP_HEADER_HEIGHT-1)/2), Graphics.LEFT|Graphics.TOP);
                    
                    g.drawRect(l, y, symBoxSize, symBoxSize);
                    
                } else { 
                    j = symColCount;
                    i = symRowcount;
                }
            }
        }
    }
    
     public static boolean isSymbolPopupSelected(int x, int y, boolean isPointed){
        boolean isSelected = false;
        boolean isMultiPageSymbol = false;
        if(symbols.length>(symColCount*symRowcount)){
            isMultiPageSymbol = true;
        }
        byte scrollWidth = UISettings.POPUP_SCROLL_WIDTH;
        int fulwidth = symColCount * symBoxSize;
        int symScreenHeight = (symBoxSize * symRowcount) + UISettings.POPUP_HEADER_HEIGHT;
        int yPosition = ((UISettings.formHeight-symScreenHeight)/2);
        int xPosition = ((UISettings.formWidth-(UISettings.MESSAGE_BOX_WIDTH/2))-fulwidth)/2;
        if(!isMultiPageSymbol){
            xPosition +=(UISettings.POPUP_SCROLL_WIDTH/2);
            scrollWidth = 0;
        }
        float y1 = 0;
        if(x>=xPosition && y>=(yPosition+UISettings.POPUP_HEADER_HEIGHT) && x<=(xPosition+fulwidth+scrollWidth) && y<=(yPosition+symScreenHeight)){
            if(isMultiPageSymbol && (x>=(xPosition+fulwidth) || (!isPointed && x>=xPosition)) && x<=(xPosition+fulwidth+scrollWidth)){
                int slen = (symbols.length / (symColCount*symRowcount));
                if ((symbols.length / (symColCount*symRowcount)) != 0) {
                    slen++;
                }
                slen = (symScreenHeight-UISettings.POPUP_HEADER_HEIGHT) / slen;
                y1= yPosition+UISettings.POPUP_HEADER_HEIGHT + ((selectedRow/symRowcount) * slen);
                if(y != y1 && y != (y1+slen)){
                    y = (int)(y-y1);
                    y1 = y / slen;
                    short oldRow = selectedRow;
                    selectedRow += (int)((y1-1)*symRowcount);
                    if((y%slen)>0){
                        selectedRow += symRowcount;
                    } 
                    if(((selectedRow*symColCount)+selecteColumn)>=symbols.length){
                        selectedRow = oldRow;
                    } 
                }
            } else {
                short oldRow = (short)(selectedRow/symRowcount);
                oldRow = (short)(selectedRow - ((symRowcount * oldRow)) + 1);
                y1 = y -(yPosition+UISettings.POPUP_HEADER_HEIGHT+(oldRow*symBoxSize));
                y = (int)(y1 / symBoxSize);
                oldRow = selectedRow;
                selectedRow += (int)y;
                if((y1%symBoxSize)>0){
                    selectedRow++;
                }
                if(selectedRow>=totalRowCount){
                    selectedRow = oldRow;
                    return isSelected;
                } 
                y1 = x - (xPosition+((selecteColumn+1)*symBoxSize));
                x = (int)(y1 /symBoxSize);
                short oldColumn = selecteColumn;
                selecteColumn += (int)x;
                if((y1%symBoxSize)>0){
                    selecteColumn++;
                }
                if(((selectedRow*symColCount)+selecteColumn)>=symbols.length){
                    selecteColumn = oldColumn;
                    selectedRow = oldRow;
                } else isSelected = true;
            }
        }
        return isSelected;
    }
    
    public static void moveSymbolPosition(int keycode){
        int count = symbols.length;
        if(keycode == UISettings.DOWNKEY){
            selectedRow++;
            if(selectedRow>=totalRowCount){
                selectedRow = 0;
            } else if(((selectedRow*symColCount)+selecteColumn)>=count){
                selecteColumn = (short)((count -(selectedRow*symColCount))-1);
            }
        } else if(keycode == UISettings.UPKEY){
            selectedRow--;
            if(selectedRow<0){
                selectedRow = (short) (totalRowCount-1);
                if(((count -(selectedRow*symColCount))-1)<selecteColumn)
                    selecteColumn =(short) ((count -(selectedRow*symColCount))-1);
            }
        } else if(keycode == UISettings.LEFTARROW){
            selecteColumn--;
            if(selecteColumn<0){
              if(selectedRow == 0){
                  selectedRow = (short) (totalRowCount-1);
                  selecteColumn = (short)((count -(selectedRow*symColCount))-1);
              } else {
                  selectedRow--;
                  selecteColumn = (short)(symColCount-1);
              }
            } 
        } else if(keycode == UISettings.RIGHTARROW){
            selecteColumn++;
            if(((selecteColumn+(selectedRow*symColCount)))>(count-1)){
                selecteColumn =0;
                selectedRow = 0;
            } else if(selecteColumn>(symColCount-1)){
                selecteColumn = 0;
                selectedRow++;
            }
        }
    }
    
    public static char getSelectedSymbol(){
        return symbols[selecteColumn+(selectedRow*symColCount)];
    }
    
    /**
     * 
     * @param floc
     * @return
     */
    public static Image getImage(String floc) {
        return RecordManager.getImage(floc);
    }


    //CR 13033
    public static void showScroll(boolean isShow){
        if(null != scrollTimer){
            scrollTimer.cancel();
            scrollTimer = null;
        }
        scrollTimer = new Timer();
        scrollTimer.schedule(new ScrollShow(), 2000);
        if(!isShow)
            isShowScroll = !isShow;
    }


    static class ScrollShow extends TimerTask {
        /**
         * run method to start the timer
         */
        public void run() {
            isShowScroll = false;
            ShortHandCanvas.IsNeedPaint();
        }
    }

    public static int getScrollHeight(float scrollLength){
        int scrollHeight = -1;
        SCROLL_WIDTH = 0;
        if(scrollLength>-1 && null != smartOne){
            if(smartOne[UISettings.SCROLL].height>scrollLength){
                SCROLL_WIDTH = smartOne[UISettings.SCROLL].width;
                scrollHeight = smartOne[UISettings.SCROLL].height;
            }
        }
        return scrollHeight;
    }

    public static void deinitialize(){
        sHeader = null;
        //resetMessageText();
        yStratPosition = 0;
        if(null != scrollTimer){
            scrollTimer.cancel();
            scrollTimer = null;
        }
        isShowScroll = false;
    }

    public static boolean setAudioFile(String location){
        boolean isNotSecurity = true;
        FileConnection fileConnection = null;
        InputStream inputStream = null;
        UiGlobalVariables.imagefile = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        UiGlobalVariables.byteArrayInputStream = null;
        UiGlobalVariables.extension = null;
        try {
            //CR 14291
            Runtime.getRuntime().gc();
            fileConnection = (FileConnection)Connector.open(location,Connector.READ);
            inputStream = fileConnection.openInputStream();
            String name = fileConnection.getName();
            Logger.debugOnError("File Location "+location+"\nFileName "+name);
            int index = name.indexOf(".");
            if(index>-1){
                UiGlobalVariables.extension = name.substring(index+1);//5.wav
            }

          int bytesRead = (int)Runtime.getRuntime().freeMemory()/2;
//          UiGlobalVariables.inputStream=inputStream;
          byteArrayOutputStream  = new ByteArrayOutputStream();
          byte[] buff = new byte[bytesRead];
          while((bytesRead = inputStream.read(buff)) != -1) {
             byteArrayOutputStream.write(buff, 0, bytesRead);
          }
          buff = null;
          UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
          Logger.debugOnError("byte stream audio size:"+ byteArrayOutputStream.size());
        } catch(SecurityException securityException){
            isNotSecurity = false;
            Logger.loggerError("Image File Security Exception "+securityException.toString());
        } catch(Exception e){
            Logger.loggerError("Image File Create Error "+e.toString()+"\n"+e.getMessage());
        } catch(OutOfMemoryError outOfMemoryError){
            Logger.loggerError("Image File Create Error OutOfMemory"+outOfMemoryError.toString());
        }

        try{
            if(null != byteArrayOutputStream){
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
            }
        }catch(Exception exception){

        }

        try{
            if(null != inputStream){
                inputStream.close();
                inputStream = null;
            }
        }catch(Exception exception){}
        try{
            if(null != fileConnection){
                fileConnection.close();
                fileConnection = null;
            }
        }catch(Exception exception){}

        return isNotSecurity;
    }

    public static boolean getFileImage(String imageLocation, boolean isSquare){
        boolean isNotSecurity = true;
        FileConnection fileConnection = null;
        InputStream inputStream = null;
        UiGlobalVariables.imagefile = null;
        try {
            //CR 14291
            Runtime.getRuntime().gc();
            Logger.debugOnError("File location "+imageLocation);
            fileConnection = (FileConnection)Connector.open(imageLocation,Connector.READ);
            int height = (UISettings.headerHeight+UISettings.secondaryHeaderHeight);
            //CR 14774
            if(isSquare){
                height = (UISettings.formHeight - height+UISettings.footerHeight);
            } else {
                height = (UISettings.formHeight - (height+UISettings.footerHeight+font.getHeight()+4));
            }
            int width = UISettings.formWidth;
            inputStream = fileConnection.openInputStream();
            UiGlobalVariables.imagefile = ImageHelper.createThumbnail(inputStream, width, height, isSquare);
        } catch(SecurityException securityException){
            isNotSecurity = false;
            Logger.loggerError("Image File Security Exception "+securityException.toString());
        } catch(Exception e){
            Logger.loggerError("Image File Create Error "+e.toString()+"\n"+e.getMessage());
        } catch(OutOfMemoryError outOfMemoryError){
            Logger.loggerError("Image File Create Error OutOfMemory"+outOfMemoryError.toString());
        }
        try{
            if(null != inputStream){
                inputStream.close();
                inputStream = null;
            }
        }catch(Exception exception){}
        try{
            if(null != fileConnection){
                fileConnection.close();
                fileConnection = null;
            }
        }catch(Exception exception){}

        return isNotSecurity;
    }

    //CR 14727
    public static Image getThumbNail(String phoneNumber){
        Image thumNailImage = null;
        byte[] thumnailImage = RecordStoreParser.getRecordStore(RecordManager.getTileImageName(phoneNumber));
        if(null == thumnailImage){
            thumnailImage = DownloadHandler.getInstance().getResourcesBytes(RecordManager.avatorImage, isShowScroll);
        }
        if(null != thumnailImage){
            thumNailImage = Image.createImage(thumnailImage, 0, thumnailImage.length);
        }
        return thumNailImage;
    }

}
