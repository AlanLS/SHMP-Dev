/**
 * FileManager Class to Create and Delete the files
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough 
 *
 **/

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

public class RecordManager {

        public static String featureAppName = "SHFApp";
        
        private static String oldAppName = "SHOApp";
        
        public static String startupAppName = "SHStartup";
        
        public static String newAppName = "SHONApp";
        
        public static String jadLessconfig = "SHConf.txt";
        
        public static String languageConfig = "SHCodeText.txt";

        public static String msContact = "SHContact";

        private static String soundFile ="/s.amr";

        public static String chatName ="SHChat";

        public static String globalEATName = "SHGlobalEAT";

        public static String globalMemName ="SHGlobalMem";

        public static String controlChannelName = "SHCName";

        public static String downloadURLRMS = "SHAppUrl";

        public static String startAppName = "Startup";

        public static String dappDownloadURLRMA = "SHAppLinkUrl";

        public static String setttingsRMS = "ConfStatus";

        public static String allMessageCountRMS = "SHMCount";

        public static String allGprsRequestCountRMS = "GRCount";

        public static String shorthandContacts = "ScContacts";

        //CR 14788
        public static String shorthandGroupContacts = "SHGroupContacts";

        //0-User Profile Avatar
        //1-Group Profile Avatar
        //2-Shout Profile Avatar.
        public static String avatorImage = "/avatar";

        public static String gridThumbnailImage = "SHContactsGrid";

        public static String groupGridThumbnailImage = "SHGroupContactsGrid";


        //ObjectBuilderFactory.GetKernel().getVersionNumber(true)
        //CR 13294
        public static boolean deleteOldRMS(String versionNumber){
            if(!isAggrementSigned(versionNumber)){
                RecordStoreParser.clearAppRMS();
            }
            return true;
        }

        
        public static String getClientAppName(){
            if(RecordStoreParser.isRecordStoreExits("SHCApp"))
                return "SHCApp";
            return null;
        }

        public static InputStream getSoundFile(){
            try{
                return Object.class.getClass().getResourceAsStream(soundFile);
            }catch(Exception e){}
            return null;
        }

        
        public static boolean isFeatureAppDownloaded(){
            boolean isDownloaded = true;
            if(null == getAppNames(featureAppName, false))
                isDownloaded = false;
            return isDownloaded;
        }
        
        public static boolean isAppCatalogDownloaded(){
            boolean isDownloaded = true;
            String fNames = getAppNames(featureAppName, false);
            if(null == fNames)
                isDownloaded = false;
            else if(fNames.indexOf("^^"+Settings.getAppCatalogName()+"-j^^") == -1 || fNames.indexOf("^^"+Settings.getAppCatalogName()+"-d^^") == -1) //CR 7230, 10586
                isDownloaded = false;
            return isDownloaded;
        }
        
        /**
         * 
         * @param rName
         * @return
         */
        public static String getRecordStoreName(String rName){
            if(rName.indexOf("-j") == -1){
                //Bug 10645
//                if(rName.charAt(0) == '#')
//                    rName = rName.substring(1);
                int index = rName.indexOf("(");
                
                if(index>-1){
                    int sIndex =rName.indexOf(")",index);
                    if(sIndex>-1){
                        if(rName.length()>(sIndex+1)){
                            rName = rName.substring(0,index)+rName.substring(sIndex+1);
                        } else rName = rName.substring(0,index);
                    }
                }
                if(rName.length()>25)
                    rName = rName.substring(0,25);
            }
            
            return rName;
        }

        public static String getActualWidgetName(String wName){
            int index1 = wName.indexOf("(");
            int index = wName.indexOf(")");
            if(index>-1 && index1>-1)
                wName = wName.substring(0,index1) + wName.substring(index+1);
            return wName.trim();
        }
        
        /**
         * 
         * @param fName
         * @param isOpen
         * @return
         */
        private static RecordStoreParser getRecrodStore(String fName,boolean isOpen){
            RecordStoreParser rStoreParser = new RecordStoreParser();
            try{
                rStoreParser.openRecordStore(fName, isOpen,false,false);
            }catch(Exception e){rStoreParser = null;}
            return rStoreParser;
        }
        
        /**
         * Method to Check the Application Enabled file exist or not
         *
         * @return - return true if the application enabled file exist.Otherwise false.
         *
         * @throws aggrementEnableException
         **/
        public static boolean isAggrementSigned(String vNumber){
            boolean isStart = false;
            RecordStoreParser rStore =null;
            try{
                rStore = getRecrodStore("SHAppE", true);
                if(null != rStore){
                    byte[] rByte = null;
                    if(rStore.getNumRecords()>0){
                        rByte = rStore.getRecord(1);
                        if(null != rByte && rByte.length>0){
                            String number = new String(rByte);
                            if(number.compareTo(vNumber) == 0)
                                isStart = true;
                            //else RecordStoreParser.clearRecords();
                        }
                    } else {
                        rByte = new byte[0];
                        rStore.addRecord(rByte, 0, rByte.length, true);
                    }
                    rStore.closeRecordStore();
                    rStore = null;
                }
            }catch(Exception e){
                Logger.loggerError("FileManager Register Connection " + e.toString());
            }
            return isStart;
        }
        
        /**
         * Method to Invok to create the Aggrement Enabled file 
         **/
        public static boolean createAggrementSignedFile(String vNumber){
            try{
                RecordStoreParser rStore = getRecrodStore("SHAppE", true);
                if(null != rStore){
                    byte[] rByte = vNumber.getBytes();
                    if(rStore.getNumRecords()>0)
                        rStore.setRecord(1, rByte, 0, rByte.length, true);
                    else
                        rStore.addRecord(rByte, 0, rByte.length, true);
                    rStore.closeRecordStore();
                    rStore = null;
                    return true;
                }
            }catch(Exception e){}
            return false;
        }

        //CR 14743,
        public static Image getContactGridThumb(int index, boolean isGroup){
            Image image = null;
            if(index>-1){
                String name = gridThumbnailImage;
                if(isGroup)
                    name = groupGridThumbnailImage;
                byte[] bytes = RecordStoreParser.getRecordStore(name);
                if(null != bytes && bytes.length>0){
                    Image gridImage = Image.createImage(bytes, 0, bytes.length);
                    if(gridImage.getWidth() >= ((index+1)*32)){
//                        int[] rgb =  new int[32*32];
                        image = Image.createImage(gridImage, index*32, 0, 32, 32, 0);
//                        gridImage.getRGB(rgb, 0, 1, index*32, index*32, 32, 32);
//                        image = Image.createRGBImage(rgb, 32, 32, true);
//                        rgb = null;
                    }
                    gridImage = null;
                    bytes = null;
                }
            }
            return image;
        }



        /**
	 * Method to get the Image from the requested location
         * 
         * @param floc - Variable will contain the file location
         *
         * @return img - Object will contain the Image from the given location.It may be null.
         *
         * @throws  imageException
	 */
	public static Image getImage(String floc) {
            Image img = null;
            try{
                byte[] rbyte = null;
                if(floc.indexOf("/")>-1){
                    rbyte = DownloadHandler.getInstance().getResourcesBytes(floc,false);
                } else {
                    rbyte = RecordStoreParser.getRecordStore(floc);
                }
                if(null != rbyte){
                    img = Image.createImage(rbyte, 0, rbyte.length);
                }
            }catch(Exception e){
                Logger.loggerError("Record manager -GetImage" + " " +floc+" " + e.toString() );
            }
            return img;
	}

        
	/**
	 * Method to get the ShartHand Logo Location
         *
	 * @return - Logo location will be return 
	 */
	public static String getSTLogoLocation() {
            return "SHheader.png";
	}

	/**
	 * Method to get the ShartHand Back ground Image location
         *
	 * @return - Background image location will be return
	 */
	public static String getSTBgLocation() {
            return "SHbg.png";
	}
        
        /**
         * Method for future purpose
         **/
        public static String getBackImage(String floc){
            if(floc.indexOf("-j")>-1){
                return floc.substring(0,floc.length()-2)+"/bg";
            } else if(floc.indexOf("-d")>-1) floc = floc.substring(0,floc.length()-2);
            return floc + "bg";
        }
        
        /**
         * Method to get the Header image location
         *
         * @return - header image location will be return
         **/
        
        public static String getLogoImageName(String floc){
            if(floc.indexOf("-j")>-1){
                return floc.substring(0,floc.length()-2)+"/header";
            } else if(floc.indexOf("-d")>-1) floc = floc.substring(0,floc.length()-2);
            return floc + "header";
        }

        public static String getTileImageName(String floc){
            if(floc.indexOf("-j")>-1){
                return floc.substring(0,floc.length()-2)+"/tile";
            } else if(floc.indexOf("-d")>-1) floc = floc.substring(0,floc.length()-2);
            return floc + "tile";
        }
        
        /**
         * Method to invok to get the download profiles list
         *
         * @return - return the profiles list
         **/
        public static String[] getfeatureAppsList(boolean isAppCatalog)
        {
            String[] fAppNames = null;
            String name = getAppNames(featureAppName, false);
            if(null != name){
                int index = -1;
                if(!isAppCatalog){ //10739
                    if(null != Settings.getAppCatalogName() && ((index = name.indexOf(Settings.getAppCatalogName()+"-d"))>-1
                            || (index = name.indexOf(Settings.getAppCatalogName()+"-j"))>-1)){ //CR 7230, 10586
                        int sIndex = -1;
                        if((sIndex=name.indexOf("^^",index)) > -1){
                            name = name.substring(0,index) + name.substring(sIndex+2);
                        } else {
                            if(index>2){
                                name = name.substring(0,index-2);
                            } else name = "";
                        }
                    }
                }
                if(name.length()>0){
                    fAppNames = Utilities.split(name, "^^");
                    if(null != fAppNames && (index= fAppNames.length)>0){
                        String temp = DownloadHandler.getInstance().getResourcesAppsName()+","; //10614
                        if(null != temp && temp.length()>1){
                            int count = 0;
                            for(int i=0;i<index;i++){
                                if(fAppNames[i].indexOf("-j")>-1){
                                    if(temp.indexOf("^"+fAppNames[i].substring(0,fAppNames[i].length()-2)+",") == -1){
                                      fAppNames[i] = null;
                                      continue;
                                    }
                                } 
                                name = fAppNames[i];
                                fAppNames[i] = null;
                                fAppNames[count++] = name;
                            }
                            if(count < index){
                                String[] tempApp = new String[count];
                                System.arraycopy(fAppNames, 0, tempApp, 0, count);
                                fAppNames = tempApp;
                            }
                        }
                    }
                    //fAppNames = sort(fAppNames); //bug 10612
                }
            }
            return fAppNames;
        }
        
        /**
         * 
         * @param appName
         * @return
         */
        public static String getMasterAppName(String appName){
            String name = getAppNames(oldAppName, false);
            if(null != name){
                name = "^^"+name+"^^";
                String dowAppName = appName+"-d";
                appName = appName+"-j";
                if(name.indexOf("^^"+appName+"^^")>-1)
                    name = appName;
                else if(name.indexOf("^^"+dowAppName+"^^")>-1)
                    name = dowAppName;
                else name = null;
            }
            return name;
        }
        
        /**
         * 
         * @param rName
         * @param isOpen
         * @return
         */
        public static String getAppNames(String rName,boolean isOpen){
            String name = null;
            RecordStoreParser rStoreParser = new RecordStoreParser();
            try{
                rStoreParser.openRecordStore(rName, isOpen,false,false);
                byte[] rbyte = rStoreParser.getRecord(1);
                if(null != rbyte){
                    name = new String(rbyte);
                }
                rStoreParser.closeRecordStore();
                rStoreParser = null;
            }catch(Exception e){
                rStoreParser = null;
            }
            return name;
        }
        
        /**
         * Method to get the download profiles location
         *
         * @return - location will be return
         **/
        public static String getFeatureAppName(String name){
            String aName = getAppNames(featureAppName, false);
            if (null != aName){
                if(("^^" + aName + "^^").indexOf("^^" + name + "-j^^") > -1)
                    return name+"-j";
                else if(("^^" + aName + "^^").indexOf("^^" + name + "-d^^") > -1){
                    return name+"-d";
                }
            }
            return null;
        }
        
	/**
	 * Method to get the Profiles Location
	 * 
         * @return - all profiles location will be return
         *
         * @throws profileLocationException
	 */
//	public static String[] getOrdinaryAppsList() {
//            getNewAppNames();
//            String[] oANames = null;
//            String aName = getAppNames(oldAppName, false);
//            if(null != aName){
//                String appendName = "";
//                int index = -1; //CR 9051
//                if(null != ChannelData.getMasterAppName()){
//                    if((index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-j^^"))>-1){
//                        appendName = "-j";
//                    } else if((index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-d^^"))>-1){
//                        appendName = "-d";
//                    }
//                    if(index >0){
//                        aName = aName.substring(0,index)+"a"+aName.substring(index+ChannelData.getMasterAppName().length()+2);
//                    } else aName ="a"+aName.substring(ChannelData.getMasterAppName().length());
//                }
//
//                if(null != ChannelData.getMasterAppName() && ((index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-j^^"))>-1
//                        || (index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-d^^"))>-1)){
//                    if(index >0){
//                        aName = aName.substring(0,index)+"a"+aName.substring(index+ChannelData.getMasterAppName().length());
//                    } else aName ="a"+aName.substring(ChannelData.getMasterAppName().length());
//                }
//                oANames = Utilities.split(aName,"^^");
//                oANames =  sort(oANames);
//                if(index>-1){
//                    oANames[0] = ChannelData.getMasterAppName()+ appendName;
//                }
//            }
//            return oANames;
//	}

        public static String[] getOrdinaryAppsList() {
            getNewAppNames();
            String[] oANames = null;
            String aName = getAppNames(oldAppName, false);
            if(null != aName){
                int index = -1; //CR 9051
                if(null != ChannelData.getMasterAppName() && ((index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-j^^"))>-1
                        || (index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-d^^"))>-1)){
                    if(index >0){
                        aName = aName.substring(0,index)+"a"+aName.substring(index+ChannelData.getMasterAppName().length());
                    } else aName ="a"+aName.substring(ChannelData.getMasterAppName().length());
                }
                oANames = Utilities.split(aName,"^^");
                oANames =  sort(oANames);
                if(index>-1){
                    oANames[0] = ChannelData.getMasterAppName()+ oANames[0].substring(1);//Hema bug :
                }
            }
            return oANames;
 }
        
        /**
         * Method to sort the file Locations
         *
         * @param fLoc - Variable will contain the file locations
         **/
        private static String[] sort(String[] fLoc){
            int len;
            if(null != fLoc && (len=fLoc.length)>0){
                String temp = null;
                for(int i=0;i<len;i++)
                {
                    for(int j=i+1;j<len;j++)
                    {
                        if(fLoc[i].toLowerCase().compareTo(fLoc[j].toLowerCase())>0)
                        {
                            temp = fLoc[i];
                            fLoc[i] = fLoc[j];
                            fLoc[j] = temp;
                        }
                    }
                }
            }
            return fLoc;
        }
        
        
        
        /**
         * Future Purpose
         **/
        public static String[] getNewAppNames()
        {
            String[] wNames = null;
            String aName = getAppNames(newAppName, false);
            if(null != aName){
                RecordStoreParser.deleteRecordStore(newAppName, true);
                wNames = Utilities.split(aName, "^^");
                updateNewApps(wNames);
                int index = -1; //CR 9051
                if(null != ChannelData.getMasterAppName() && ((index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-d^^"))>-1 ||
                        (index=("^^"+aName+"^^").indexOf("^^"+ChannelData.getMasterAppName()+"-j^^"))>-1)){
                    if(index >0){
                        aName = aName.substring(0,index)+"a"+aName.substring(index+ChannelData.getMasterAppName().length());
                    } else aName ="a"+aName.substring(ChannelData.getMasterAppName().length());
                }
                wNames = Utilities.split(aName, "^^");
                wNames = sort(wNames);
                if(index>-1){
                    wNames[0] = ChannelData.getMasterAppName();
                }
            }
            return wNames;
        }
        
        private static void updateNewApps(String[] wNames){
            if(null != wNames){
                String oName = getAppNames(oldAppName, false);
                int count = wNames.length;
                if(null != oName){
                    for(int i=0;i<count;i++){
                        if(("^^"+oName+"^^").indexOf("^^"+wNames[i]+"^^") == -1){
                            if(("^^"+oName+"^^").indexOf("^^"+wNames[i].substring(0,wNames[i].length()-2)+"-j^^") == -1){
                                oName += "^^" + wNames[i];
                            } else {
                                oName = Utilities.replace(oName, wNames[i].substring(0,wNames[i].length()-2)+"-j", wNames[i]);
                            }
                        }
                    }
                } else {
                    oName = "";
                    for(int i=0;i<count;i++){
                        oName += wNames[i];
                        if((i+1)!= count)
                            oName += "^^";
                    }
                } 
                try{
                    RecordStoreParser rStoreParser =  new RecordStoreParser();
                    rStoreParser.openRecordStore(oldAppName, true,false,false);
                    if(null != rStoreParser){
                        byte[] rbyte = oName.getBytes();
                        if(rStoreParser.getNumRecords()>0){
                            rStoreParser.setRecord(1, rbyte, 0, rbyte.length, true);
                        } else rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                        rStoreParser.closeRecordStore();
                        rStoreParser = null;
                    }
                }catch(Exception e){}
            }
        }
        
        
	/**
	 * Method to get the Advertisement file Location
         *
	 * @return name - Advertisement file location will be return
         *
         * @throws AdException
	 * 
	 */
	public static String getAdvertisementName() {
            String name = null;
            if(RecordStoreParser.isRecordStoreExits("SHSAd"))
                name = "SHSAd";
            return name;
	}
        
	/**
	 * Method to get the profile from the given location
         *
	 * @param floc - Variable will contain the location
         *
	 * @return pName - Variable will contain the profile location
	 * 
	 */
	public static String getOrdinaryAppName(String floc)throws IOException {
            return getRecordStoreName(floc);
	}
        

        /**
         *
         * @param pName
         * @return
         */
        public static String getEntryRecordStoreName(String pName){
            pName = getRecordStoreName(pName);
            return pName + "Es";
        }

        public static String getMemorizeName(String pName){
            pName = getRecordStoreName(pName);
            return pName +"MEM";
        }
        
        public static String getProfileProperty(String pName){
            pName = getRecordStoreName(pName);
            return pName +"APro";
        }
        
        public static String getMessageCountLoc(String pName){
            pName = getRecordStoreName(pName);
            return pName +"AMsgC";
        }
        
        /**
         *
         * @param pName
         * @return
         */
        public static String getRecordEntryRecordStoreName(String pName){
            pName = getRecordStoreName(pName);
            return pName +"Rs";
        }

//        public static String getQueryAndMemRecordLoc(String pName){
//            pName = getRecordStoreName(pName);
//            return pName + "MS";
//        }
        
        
        public static void deleteRecords(String pname){
            RecordStoreParser.deleteRecordStore(getMemorizeName(pname), true);
            RecordStoreParser.deleteRecordStore(getProfileProperty(pname), true);
            RecordStoreParser.deleteRecordStore(getMessageCountLoc(pname), true);
            //Delete Query Shortcut and Memorize and Widget Property information
            //RecordStoreParser.deleteRecordStore(getQueryAndMemRecordLoc(pname), true);
            //Delete the Record values
            RecordStoreParser.deleteRecordStore(getRecordEntryRecordStoreName(pname), true);
            //Delete the Entryshortcut values
            RecordStoreParser.deleteRecordStore(getEntryRecordStoreName(pname), true);
            //CR 6984
            ChatHistoryHandler.removeChatRecords(pname);
        }
        
        public static String getRecMsgRmsName(){
            return "rmsRM";
        }
        
	/**
	 * Method to Delete all profile 
         *
	 * @return - true will be return if deleted
	 * 
	 */
	public static boolean deleteAllProfiles() {
            String name = getAppNames(oldAppName, false);
            RecordStoreParser.deleteRecordStore(oldAppName, true);
            if(null != name){
                String[] appNames = Utilities.split(name, "^^");
                int count = appNames.length;
                for(int i=0;i<count;i++){
                    if(appNames[i].indexOf("-j") == -1){
                        appNames[i] = appNames[i].substring(0,appNames[i].length()-2);
                        appNames[i] = getRecordStoreName(appNames[i]);
                        RecordStoreParser.deleteRecordStore(appNames[i], true);
                        RecordStoreParser.deleteRecordStore(getBackImage(appNames[i]), true);
                        RecordStoreParser.deleteRecordStore(getLogoImageName(appNames[i]), true);
                        RecordStoreParser.deleteRecordStore(getTileImageName(appNames[i]), true);
                    } else appNames[i] = appNames[i].substring(0,appNames[i].length()-2);
                    RecordStoreParser.deleteRecordStore(getMemorizeName(appNames[i]), true);
//                    RecordStoreParser.deleteRecordStore(getQueryAndMemRecordLoc(appNames[i]), true);
                    RecordStoreParser.deleteRecordStore(getMessageCountLoc(appNames[i]), true);
                    RecordStoreParser.deleteRecordStore(getProfileProperty(appNames[i]), true);
                    RecordStoreParser.deleteRecordStore(getEntryRecordStoreName(appNames[i]), true);
                    RecordStoreParser.deleteRecordStore(getRecordEntryRecordStoreName(appNames[i]), true);
                    //CR 6984
                    ChatHistoryHandler.removeChatRecords(appNames[i]);
                }
            }
            return true;
	}
        
        public static void deleteSingleApp(String wName){
            String name = getAppNames(oldAppName, false);
            if(null != name){
                String[] appNames = Utilities.split(name, "^^");
                int len = appNames.length;
                name = "";
                int position = -1;
                if(len>0){
                    for(int i=0;i<len;i++){
                        if(appNames[i].compareTo(wName) == 0 || appNames[i].compareTo(wName+"-d") ==0 || appNames[i].compareTo(wName+"-j")==0) {
                            position = i;
                        } else name += "^^"+ appNames[i];
                    }
                    if(name.length()>0)
                        name = name.substring(2);
                }
                if(name.length()>0)
                    RecordStoreParser.UpdateRecordStore(oldAppName, name.getBytes(), true);
                else RecordStoreParser.deleteRecordStore(oldAppName, true);
                if(position>-1){
                    if(appNames[position].indexOf("-j") == -1){
                        RecordStoreParser.deleteRecordStore(wName, true);
                        RecordStoreParser.deleteRecordStore(getBackImage(wName), true);
                        RecordStoreParser.deleteRecordStore(getLogoImageName(wName), true);
                        RecordStoreParser.deleteRecordStore(getTileImageName(wName), true);
                    } 
                    RecordStoreParser.deleteRecordStore(getMemorizeName(wName), true);
//                    RecordStoreParser.deleteRecordStore(getQueryAndMemRecordLoc(wName), true);
                    RecordStoreParser.deleteRecordStore(getMessageCountLoc(wName), true);
                    RecordStoreParser.deleteRecordStore(getProfileProperty(wName), true);
                    RecordStoreParser.deleteRecordStore(getEntryRecordStoreName(wName), true);
                    RecordStoreParser.deleteRecordStore(getRecordEntryRecordStoreName(wName), true);
                    //CR 6984
                    ChatHistoryHandler.removeChatRecords(wName);
                }
            }
        }
        
	/**
	 * Method to get the Logger file location
         *
	 * @return - file location will be return
	 * 
	 */
        public static String getLoggerName(){
            return "SHLogger";
        }
        
//        public static String getTouchLogger(){
//            return "SHTouchLogger";
//        }

        /**
         * Method to get the Last Access Advertisement file location
         *
         * @return aName - Variable will contain the file location
         *
         * @throws lastAdException
         **/
        public static String getLastSelectedAdName(){
            return "SHLastSelectedAdName";
        }
}
