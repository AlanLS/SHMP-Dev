//
//
//import javax.microedition.lcdui.Graphics;
//import javax.microedition.lcdui.Image;
//
//
//
//
///**
// * Profile Canvas class for the profile presenter screen
// *
// * @author Hakuna Matata
// * @version 1.00.15
// * @copyright (c) ShartHand Mobile Inc
// */
//public class Nail
//{
//    //Item Name array
//    private String[] imagePath = null;
//
//    //Logo Image array
//    private Image[] logoimg = null;
//
//    private float yStartPosition = 0;
//
//    private float scrollLineLength = -1;
//
//    private short numberofRow, columnCount, rowCount = 0, selectedIndex;
//
//    private byte gridHighlightGap = 15;
//
//    private int previousY = -1;
//
//    private int totalNavigationSize = 0;
//
//    private boolean isScrollSelected = false;
//
//    /**
//     * Constructor method to initialize the profile canvas
//     */
//    public Nail() {
//        setGridDisplayCount(false);
//    }
//
//    private void setGridDisplayCount(boolean isScroll){
//        int width = UISettings.formWidth;
//        if(isScroll)
//            width -= UISettings.POPUP_SCROLL_WIDTH;
//        if(UISettings.isTocuhScreenNativeTextbox && !UISettings.GENERIC){ //CR 10336
//            gridHighlightGap = 15;
//            setGridWidth();
//            columnCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
//           if((columnCount*((UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap))+UISettings.GRID_IMAGE_HEIGHT)<width)
//               columnCount++;
//            width = (short)(UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight));
//           rowCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
//           if(width>=((UISettings.GRID_IMAGE_HEIGHT*(rowCount+1)+(gridHighlightGap*rowCount)))){
//               rowCount+=1;
//           }
//        } else {
//            gridHighlightGap = 10;
//            columnCount = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+6));
//
//            if((columnCount *(UISettings.GRID_IMAGE_HEIGHT+6))>width){
//                columnCount--;
//            }
//            int gap = (short)(width / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
//            rowCount = (short)((UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight)) / (UISettings.GRID_IMAGE_HEIGHT+6));
//            int gaptow = (short)((UISettings.formHeight-(UISettings.headerHeight+UISettings.secondaryHeaderHeight+UISettings.footerHeight)) / (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap));
//
//            if(gaptow<rowCount){
//                gridHighlightGap = 6;
//            } else if(gap<columnCount){
//                gridHighlightGap = 6;
//            }
//        }
//    }
//
//    private void setGridWidth(){
//        if(UISettings.formWidth<=240) {
//            gridHighlightGap = 10;
//        } else if(UISettings.formWidth>240 && UISettings.formWidth<=300) {
//           gridHighlightGap = 20;
//        } else if(UISettings.formWidth>300 && UISettings.formWidth<=360) {
//             gridHighlightGap = 30;
//        } else if(UISettings.formWidth>360) {
//            gridHighlightGap = 40;
//        }
//    }
//
//    private void setScrollLen(){
//        scrollLineLength =-1;
//        if(null != imagePath){
//            //CR 12817
//            if(totalNavigationSize<0){
//                scrollLineLength = (UISettings.formHeight - (2*UISettings.itemHeight))/(float)(-1*totalNavigationSize);
//                int len = CustomCanvas.getScrollHeight(scrollLineLength);
//                if(len>-1){
//                    scrollLineLength =((UISettings.formHeight - ((2*UISettings.itemHeight)+len)) /(float) (-1*totalNavigationSize));
//                }
//            }
//        }
//    }
//
//    /**
//     * Method to draw profile menu
//     *
//     * @param g  Instance of graphics class
//     */
//    public void paintGameView(Graphics g) {
//        if (null != imagePath) {
//            displyGridView(g);
//            if(scrollLineLength>0){
//                //CR 12817
//                CustomCanvas.drawScroll(g, scrollLineLength, UISettings.headerHeight,
//                        (-1*yStartPosition*scrollLineLength),
//                        (UISettings.formHeight - (2*UISettings.itemHeight)),
//                        UISettings.formWidth);
//            }
//        }
//    }
//
//    private void displyGridView(Graphics g){
//
//        int pages = 0;
//        if(yStartPosition != 0)
//            pages = (int)(-1*yStartPosition)/getGridGapWithHeight();
//
//        //CR 12817
//        short nLine = (short)((numberofRow+pages)*columnCount);
//
//        if(imagePath.length>(nLine+columnCount)){
//            nLine += columnCount;
//            if(imagePath.length>(nLine+columnCount)){
//                nLine += columnCount;
//            } else {
//                nLine = (short)imagePath.length;
//            }
//        } else {
//            nLine = (short)imagePath.length;
//        }
//
//        int startX = ((UISettings.formWidth-(((columnCount-1)*getGridGapWithHeight())+ UISettings.GRID_IMAGE_HEIGHT))/2);
//
//        int imageHeight,imageWidth = 0;
//
//        int y = UISettings.headerHeight+UISettings.secondaryHeaderHeight+
//                (int)(yStartPosition%getGridGapWithHeight())+5;
//
//        for (int i = (pages*columnCount), k=0, x = startX; i < nLine; i++, x+=getGridGapWithHeight(),k++) {
//            if(k !=0 && (k%columnCount) == 0){
//                y+= getGridGapWithHeight();
//                x = startX;
//            }
//
//            if(i == selectedIndex){
//                drawGridSelection(g,x,y, false);
//            }
//
//            imageHeight = 0;
//            imageWidth = 0;
//            if(null != logoimg[i]){
//                if(logoimg[i].getWidth()<UISettings.GRID_IMAGE_HEIGHT){
//                    imageWidth = (UISettings.GRID_IMAGE_HEIGHT - logoimg[i].getWidth())/2;
//                }
//                if(logoimg[i].getHeight()<UISettings.GRID_IMAGE_HEIGHT){
//                    imageHeight = (UISettings.GRID_IMAGE_HEIGHT - logoimg[i].getHeight())/2;
//                }
//                g.drawImage(logoimg[i], x+imageWidth, y+imageHeight, Graphics.TOP | Graphics.LEFT);
//            }
//
//            if(i == selectedIndex){
//                drawGridSelection(g,x,y, true);
//            }
//        }
//    }
//
//    private void drawGridSelection(Graphics g, int x, int y,boolean isAfterImage){
//        byte arc = 17;
//        g.setColor(0x8cc63f);
//        g.setStrokeStyle(Graphics.SOLID);
//        if(isAfterImage){
//            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc,arc);
//            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-1,arc-1);
//            g.drawRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-2,arc-2);
//
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc,arc);
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-1,arc-1);
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+2, UISettings.GRID_IMAGE_HEIGHT+2,arc-2,arc-2);
//
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc,arc);
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc-1,arc-1);
//            g.drawRoundRect(x-1, y-1, UISettings.GRID_IMAGE_HEIGHT+1, UISettings.GRID_IMAGE_HEIGHT+1,arc-2,arc-2);
//
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-1,arc-1);
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-2,arc-2);
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT, UISettings.GRID_IMAGE_HEIGHT,arc-3,arc-3);
//
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc,arc);
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-1,arc-1);
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-2,arc-2);
//            g.drawRoundRect(x, y, UISettings.GRID_IMAGE_HEIGHT-1, UISettings.GRID_IMAGE_HEIGHT-1,arc-3,arc-3);
//
//        } else g.fillRoundRect(x-2, y-2, UISettings.GRID_IMAGE_HEIGHT+3, UISettings.GRID_IMAGE_HEIGHT+3,arc,arc);
//    }
//
//    /**
//     *
//     * @param xPosition
//     * @param yPosition
//     */
//    public boolean pointerPressed(int xPosition, int yPosition,
//            boolean isNotDrag, boolean isDragEnd, boolean isPressed){
//        boolean isNeedSelect = false;
//
//        if(yPosition>UISettings.headerHeight){
//            if(null != imagePath){
//                isNeedSelect = pointerGridView(xPosition, yPosition,
//                        isNotDrag, isDragEnd, isPressed);
//            }
//        }
//
//        if(isDragEnd)
//            isScrollSelected = false;
//
//        return isNeedSelect;
//    }
//
//    private boolean pointerGridView(int xPosition, int yPosition,
//        boolean isNotDrag, boolean isDragEnd, boolean isPressed){
//        yPosition -= UISettings.headerHeight;//Hema
//        if(isNotDrag || isPressed){
//            previousY = yPosition;
//
//            if(isPressed &&
//                CustomCanvas.isShowScroll && UISettings.formWidth-CustomCanvas.SCROLL_WIDTH<=xPosition
//                    && ((-1*yStartPosition*scrollLineLength)) <= yPosition
//                    && ((-1*yStartPosition*scrollLineLength)+CustomCanvas.SCROLL_WIDTH) >= yPosition){
//                    isScrollSelected = true;
//            } else if( yPosition>UISettings.secondaryHeaderHeight){
//                yPosition -= UISettings.secondaryHeaderHeight;
//                yPosition += (-1*yStartPosition) + 5;
//                yPosition = (int)(yPosition/getGridGapWithHeight());
//                yPosition *= columnCount;
//
//                int startX = ((UISettings.formWidth-(((columnCount-1)*getGridGapWithHeight())+ UISettings.GRID_IMAGE_HEIGHT))/2);
//                if(xPosition<startX || xPosition>(UISettings.formWidth-startX))
//                    return false;
//                xPosition -= startX;
//                if(xPosition<0)
//                    xPosition = 0;
//                else xPosition = (int)(xPosition/getGridGapWithHeight());
//
//                yPosition += xPosition;
//
//                //13040
//                if(isNotDrag && yPosition != selectedIndex){
//                    isNotDrag = false;
//                } else if(!isNotDrag && yPosition<imagePath.length)
//                    selectedIndex = (short)yPosition;
//            } else isNotDrag = false;
//        } else if(scrollLineLength>-1){
//            //CR 13033
//            CustomCanvas.showScroll(isDragEnd);
//
//            if(isScrollSelected){
//                float position = (totalNavigationSize/(totalNavigationSize*scrollLineLength));
//                if((yPosition-previousY) != 0 && position>0)
//                    yStartPosition -= (yPosition-previousY)*position;
//            } else {
//                if(yPosition>UISettings.secondaryHeaderHeight){
//                    //CR 13032
//                    yStartPosition += (yPosition-previousY);
//                } else return isNotDrag;
//            }
//
//            if(yStartPosition>0)
//                yStartPosition = 0;
//            else{
//                if(yStartPosition<totalNavigationSize){
//                    yStartPosition = totalNavigationSize;
//                } else{
//                    previousY = selectedIndex/columnCount;
//                    if(selectedIndex%columnCount>0)
//                        previousY++;
//                    previousY *= (-1)*getGridGapWithHeight();
//                    if((yStartPosition-getGridGapWithHeight())<previousY){
//                        selectedIndex += columnCount;
//                        if(selectedIndex>=imagePath.length)
//                            selectedIndex = (short)(imagePath.length-1);
//                    } else if((yStartPosition-(numberofRow*getGridGapWithHeight()))>previousY){
//                        selectedIndex -= columnCount;
//                    }
//                }
//            }
//            previousY = yPosition;
//        }
//        return isNotDrag;
//    }
//
//    private int getGridGapWithHeight(){
//        //#if ROLTECH
//        //|JG|  return (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap/2);
//            //#else
//            return (UISettings.GRID_IMAGE_HEIGHT+gridHighlightGap);
//            //#endif
//    }
//    /**
//     * Method to handle the keypressed event in the menu
//     *
//     * @param option key code
//     **/
//    public String keyPressed(int keyCode) {
//        if (UISettings.DOWNKEY == keyCode) {
//            if(null != imagePath){
//                int pages = (int)((-1*yStartPosition)/getGridGapWithHeight())*columnCount;
//                if(pages<=selectedIndex && (selectedIndex+columnCount)<pages+(numberofRow*columnCount)){
//                    selectedIndex += columnCount;
//                    if(imagePath.length<=selectedIndex)
//                        selectedIndex = (short)(imagePath.length-1);
//                } else if(yStartPosition>totalNavigationSize){
//                    if(yStartPosition%getGridGapWithHeight()<0)
//                        yStartPosition -= (getGridGapWithHeight() + yStartPosition%getGridGapWithHeight());
//                    else yStartPosition -= getGridGapWithHeight();
//                    selectedIndex += columnCount;
//                    if(imagePath.length<=selectedIndex)
//                        selectedIndex = (short)(imagePath.length-1);
//                } else {
//                    //CR 13030
//                    selectedIndex = (short)(selectedIndex%columnCount);
//                    yStartPosition = 0;
//                }
//            }
//        } else if (UISettings.UPKEY == keyCode) {
//            if(null != imagePath){
//                int pages = -1*(selectedIndex/columnCount)*getGridGapWithHeight();
//                if(yStartPosition>pages && (selectedIndex-columnCount)>=0){
//                    selectedIndex -= columnCount;
//                    if((pages+getGridGapWithHeight())>yStartPosition && yStartPosition%getGridGapWithHeight()<0){
//                        yStartPosition -= yStartPosition%getGridGapWithHeight();
//                    }
//                } else if(yStartPosition<0){
//                    yStartPosition += getGridGapWithHeight();
//                    selectedIndex -= columnCount;
//                } else {
//                    //CR 13030
//                    selectedIndex++;
//                    if(imagePath.length%columnCount<=selectedIndex)
//                        selectedIndex = (short)(imagePath.length-1);
//                    else selectedIndex = (short)((imagePath.length-1)-selectedIndex);
//                    yStartPosition = totalNavigationSize;
//                }
//            }
//        } else if (UISettings.FIREKEY == keyCode) {
//            if(null != imagePath){
//                return imagePath[selectedIndex];
//            }
//        } else if(keyCode == UISettings.BACKKEY){
//            if(UISettings.rOByte == 22){
//                if(null != imagePath){
//                    ObjectBuilderFactory.GetKernel().handleOptionSelection(0, imagePath[selectedIndex], UISettings.rOByte);
//                }
//            }
//        } else if(keyCode == UISettings.RIGHTARROW) { //CR 8435
//            if((selectedIndex+1)<imagePath.length && (selectedIndex+1)%columnCount != 0){
//                selectedIndex++;
//            }
//        } else if(keyCode == UISettings.LEFTARROW){ //CR 8435
//            if(selectedIndex%columnCount>0){
//                selectedIndex--;
//            }
//        }
//        return null;
//    }
//
//
//    public void handleOptionSelected(byte oIndex){
//        if(null != imagePath){
//            ObjectBuilderFactory.GetKernel().handleOptionSelection(0,
//                    imagePath[selectedIndex], oIndex);
//        }
//    }
//
//    /**
//     * Method to set the itemName array, logoimg array
//     *
//     * @param logos  String array which represent logo location
//     * @param names  String array which represent the profile names
//     */
//    private void setMenu(String[] logos, String[] names) {
//        imagePath = names;
//        totalNavigationSize = 0;
//        if (null != imagePath) {
//            int len = imagePath.length;
//            numberofRow = (short)(len/columnCount);
//            if((len%columnCount) != 0)
//                numberofRow++;
//            if(rowCount<numberofRow){
//                totalNavigationSize = (-1*(numberofRow-rowCount)*getGridGapWithHeight());
//                numberofRow = rowCount;
//            }
//
//            logoimg = new Image[len];
//            for (int i = 0; i < len; i++) {
//                if(logos[i] != null){
//                    logoimg[i] = CustomCanvas.getImage(logos[i]);
//                }
//            }
//        }
//    }
//
//
//    /**
//     * Method to de-initialize variables.
//     */
//    private void deInitialize() {
//
//        scrollLineLength = -1;
//        previousY = -1;
//
//        isScrollSelected = false;
//
//        //byte
//        yStartPosition = totalNavigationSize = selectedIndex =0;
//
//        numberofRow = 0;
//
//        //String Array
//        imagePath = null;
//
//        //Image Array
//        logoimg = null;
//
//        //bug 13169
//        CustomCanvas.deinitialize();
//
//    }
//
//    /**
//     * Method to load the profile presenter canvas
//     *
//     * @param resDTO Instance of ProfileResponseDTO. Please refer
//     *               ProfileResponseDTO for the description of its attributes
//     */
//    public void load(ProfileResponseDTO resDTO) {
//        deInitialize();
//        UISettings.lOByte = PresenterDTO.setLOptByte();
//        try {
//            setMenu(resDTO.getLogoLink(), resDTO.getProfileName());
//            setScrollLen();
//        } catch (Exception e) {
//            Logger.loggerError("ProfileCanvas -> " + e.toString() + e.getMessage());
//        }
//        ShortHandCanvas.IsNeedPaint();
//    }
//
//    /**
//     * Method to unload the view
//     */
//    public void unLoad() {
//        deInitialize();
//    }
//
//    public int getSmartPopupyPos(int keyCode) {
//        return (UISettings.formHeight / 2 - UISettings.popupHeight / 2);
//    }
//
//    public void rotateScreen(boolean isLandScape) {
//        if(null != imagePath){
//            totalNavigationSize = 0;
//            setGridDisplayCount(false);
//            rotateGridView();
//            setScrollLen();
//        }
//    }
//
//    private void rotateGridView(){
//        int count = imagePath.length;
//        numberofRow = (short)(count/columnCount);
//        if(count%columnCount>0)
//            numberofRow++;
//        if(rowCount<numberofRow){
//            totalNavigationSize = (-1*(numberofRow-rowCount)*getGridGapWithHeight());
//            if(yStartPosition<totalNavigationSize){
//                yStartPosition = totalNavigationSize;
//            }
//            numberofRow =  rowCount;
//        } else {
//            yStartPosition = 0;
//        }
//    }
//}
