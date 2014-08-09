

import javax.microedition.lcdui.Image;

/**
 * @author Hakuna Matata
 * @version 2.0
 * @copyright (c) John Mcdonnough
 */

/**
 * Constants class
 */
public class PresenterDTO {

	private static byte lOptByte = 41; // Right Option Text

	private static Image hdrImg = null; // Headerlogo Image        

	private static Image bgImg = null; // Background Image

	private static String hdrtxt = null; // Header Text
        
        private static String hdrimgLoc="";
        
        private static String bgImgLoc="";
        
        public static boolean isBgGob = false;
        
        public static boolean isHdGob = false;

	/**
	 * 
	 * @return Right Option Text
	 */
	public static byte setLOptByte() {
		return lOptByte;
	}

	public static void setHdrLogo(String hdLogoloc) {
            if(null == hdLogoloc || null == hdrImg || hdrimgLoc.compareTo(hdLogoloc) !=0 ){
                hdrimgLoc = hdLogoloc;
		hdrImg = null;
                if(hdLogoloc.indexOf(".png") == -1){
                    hdrImg = RecordManager.getImage(hdLogoloc);
                    if(null == hdrImg)
                        isHdGob = true;
                } else isHdGob = true; 
            }
	}

	public static void setBgImage(String bgImageloc) {
            if(null == bgImg || null == bgImageloc || bgImageloc.compareTo(bgImgLoc) != 0){
                bgImgLoc =bgImageloc;
		bgImg = null;  
                if(bgImageloc.indexOf(".png") == -1){
                    bgImg = RecordManager.getImage(bgImageloc);
                    if(null == bgImg)
                        isBgGob = true;
                } else isBgGob = true;
            }
	}
	
	public static Image getHdrLogo() {
		return hdrImg;
	}

	public static Image getBgImage() {
		return bgImg;
	}
        
	public static String getHdrtxt() {
		return hdrtxt;
	}

	public static void setHdrtxt(String hdrtxt) {
                hdrtxt = Utilities.replace(hdrtxt, "\n", "");
                //CR 0012063
		PresenterDTO.hdrtxt = CustomCanvas.getSecondaryHeader(hdrtxt, "",45);
	}

	public static void setLOptByte(byte optByte) {
		lOptByte = optByte;
	}
        
        public static void deinitialize()
        {
            hdrImg = null;
            bgImg = null;            
            hdrtxt = null;
            Runtime.getRuntime().gc();
        }
       
}
