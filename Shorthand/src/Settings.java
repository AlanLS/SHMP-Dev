
import generated.Build;
import java.util.Calendar;

public class Settings {

    /** Variable to hold the shorthand security pinnumber */
	private static String pinNumber = null;

        /** Variable to hold the security pin is enabled or not */
	private static boolean isPinEnabled = false;

        /** Variable to hold the notification sound is enabled or not */
	private static boolean isNotification = true;

        /** Variable to hold the shorthand is run in  the debug mode or not */
	private static boolean isDebug = false;

        /** Variable to hold the inbox sort type */
	private static byte sortType = 0;
        
        /** Variable to hold the shorthand welcome message is present or not */
        private static boolean isWelcome = true;
        
        /** Variable to hold the shorthand enable message */
        private static String appeMsg = null;
        
        /** Variable to hold the shorthand is enabled or not */
        private static boolean isAppEnable = true;

        /** Variable to hold the increase Logging cabablity */
        private static boolean isScreenRefresh = false; // CR 6986
        
        /** Varibale to hold the receive Message count */
        private static short rMsgCount = 0;

        /** Varibale to hold the receive Request count */
        private static double rResponseCount = 0;

        /** Variable to hold the inbox alpha short type is A-Z or Z-A type */
        public static boolean isAlpha = false;
        
        /** Variable to hold the send message count */
        private static short sMsgcount = 0;

        /** Variable to hold the sent Request count */
        private static double sRequestCount = 0;
        
        /** Variable to hold the current year */
        private static short cyear = 0;
        
        /** Variable to hold the current month */
        private static byte cmonth = 0;
        
        /** variable to hold the message count index */
        private static int rMessageIndex = 0;

        /** variable to hold the Request count index */
        private static int rRequestIndex = 0;
        
        /** Variable to hold the client cell number */
        private static String phoneNumber = null;

        /** Variable to hold the client unique User Id */
        private static String UID = null;
        
        public static boolean isStarted = false;
        
        private static RecordStoreParser recordParser = null;
        
        private static short monthlySentCount = 0;

        private static double monthlyUploadedDataCount = 0;
        
        private static String appCatalogName = null; //CR  7230

        private static boolean isAppDownloading = false;

        private static int cCount = 0;

        private static String lgManufature = null;

        private static boolean isGrid = true;

        //CR 11974
        private static boolean isGPRS = false;

        //CR 12191
        private static boolean isShowMode = false;

        //CR 12360
        private static boolean isPCNF = false;

//        //CR 12988
//        private static boolean isFirstSMS = false;

        private static boolean isUCNF = false;

        private static boolean isContactGridUpdate = false;

        //CR 14803
        private static boolean isGroupGridUpdate = false;

        private static boolean  isModeNotChanged = false;

        public static boolean isAliveState = false;

        public static int poolTime = 60000;

        //Cr 14531
        public static int serverPoolTime = 60;

        public static boolean isSocketThreadStart = false;

        //cr 13617
        public static boolean isInitialStart = true;

//        public static boolean isCameraRefresh = false;

        public static boolean isIsModeNotChanged() {
            return isModeNotChanged;
        }

        public static void setIsModeNotChanged(boolean isModeNotChanged) {
            Settings.isModeNotChanged = isModeNotChanged;
        }


//        public static boolean isIsFirstSMS() {
//            return isFirstSMS;
//        }
//
//        public static void setIsFirstSMS() {
//            if(!isFirstSMS){
//                isFirstSMS = true;
//                setSettingValue(7, 4, 1, null);
//            }
//        }

        public static boolean isIsPCNF() {
            return isPCNF;
        }

        public static void setIsPCNF(boolean isPCNF) {
            Settings.isPCNF = isPCNF;
            if(isPCNF) 
                setSettingValue(7, 3, 1, null);
            else setSettingValue(7, 3, 0, null);
        }

        //CR 12988
        public static boolean isIsUCNF() {
            return isUCNF;
        }

        public static void setIsUCNF(boolean isUCNF) {
            Settings.isUCNF = isUCNF;
            if(isUCNF)
                 setSettingValue(7, 4, 1, null);
            else setSettingValue(7, 4, 0, null);
        }

        //CR 12191
        public static void setIsShowMode(boolean isShowMode) {
            Settings.isShowMode = isShowMode;
            if(isShowMode) //GPRS Position is 2 and the Index is 7
                setSettingValue(7, 2, 1, null);
            else setSettingValue(7, 2, 0, null);
        }

        //CR 14741
        public static void setContactGridUpdate(boolean isUpdate){
            Settings.isContactGridUpdate =  isUpdate;
            if(isUpdate){ 
                setSettingValue(7, 5, 1, null);
            } else {
                setSettingValue(7, 5, 0, null);
            }
        }

        public static boolean isIsContactGridUpdate() {
            return isContactGridUpdate;
        }

        //CR 14803
        public static void setGroupContactGridUpdate(boolean isUpdate){
            Settings.isGroupGridUpdate =  isUpdate;
            if(isUpdate){ 
                setSettingValue(7, 6, 1, null);
            } else {
                setSettingValue(7, 6, 0, null);
            }
        }

        public static boolean isIsGroupGridUpdate() {
            return isGroupGridUpdate;
        }

        //CR 12191
        public static boolean isIsShowMode() {
            return isShowMode;
        }

        //CR 11974
        public static boolean isIsGPRS() {
            return isGPRS;
        }

        //CR 11974
        public static void setIsGPRS(boolean isGPRS) {
            Settings.isGPRS = isGPRS;
            //CR 13074
            if(isGPRS) {//GPRS Position is 2 and the Index is 7
                UISettings.MAX_COUNT = 1000;
                setSettingValue(7, 1, 1, null);
            } else {
                UISettings.MAX_COUNT = 160;
                setSettingValue(7, 1, 0, null);
            }
        }


        public static void setLongCode(String longCode) {
            if(null == ChannelData.longCode || ChannelData.longCode.compareTo(longCode) != 0){
                setSettingValue(8, 0, 0, longCode);
            }
            ChannelData.longCode = longCode;
        }

        public static boolean isIsGrid() {
            return isGrid;
        }

        public static void setIsGrid(boolean isGrid) {
            Settings.isGrid = isGrid;
            if(isGrid) //Grid Display Position is 1 and the Index is 7
                setSettingValue(7, 0, 1, null);
            else setSettingValue(7, 0, 0, null);
        }

        public static boolean isLg(){
            lgManufature = Utilities.getLGManufature();
            if(lgManufature.length()>0)
                return true;
            return Build.SERVERCONNECTION;
        }


        public static boolean isIsDownload() {
            return isAppDownloading;
        }

        public static void setIsDownload(boolean download) {
            isAppDownloading = download;
        }

        public static String getAppCatalogName() {
            if(null != appCatalogName && appCatalogName.length()>0)
                return appCatalogName;
            return null;
        }

        public static void setAppCatalogName(String catalogName) {
            appCatalogName = catalogName;
            setSettingValue(5, 0, 0, appCatalogName);
        }

        /** Get the Current month message sent count */
        public static short getMonthlySentCount() {
            return monthlySentCount;
        }

        public static double getMonthlyUploadedDataCount() {
            return monthlyUploadedDataCount;
        }

        public static boolean isIsScreenRefresh() {
            return isScreenRefresh;
        }

        public static void setIsScreenRefresh(boolean isReferesh) {
            isScreenRefresh = isReferesh;
            if(isReferesh)
                setSettingValue(1, 7, 1, null);
            else setSettingValue(1, 7, 0, null);
        }

       public static String getPhoneNumber() {
           if( null != phoneNumber && phoneNumber.compareTo("0000000000")==0)
                return null;
            return phoneNumber;
            //return "919600084216";
         }

        public static void setPhoneNumber(String number) {
            phoneNumber = number;
            setSettingValue(4, 0, 0, number);
        }

        //CR 12990
        public static String getUID(boolean isNullZero) {
            if(null == UID && isNullZero)
                return "0000000000";
            return UID;
        }

        public static void setUID(String UID) {
            Settings.UID = UID;
            setSettingValue(9, 0, 0, UID);
        }

        //CR 8352
        public static String getSequenceNumber(){
            String value = ""+((char)(cCount+97));
            cCount++;
            if(cCount>=26)
                cCount = 0;
            setSettingValue(6, 0, 0, cCount+"");
            return value;
        }

        public static short getRMsgCount() {
            return rMsgCount;
        }

        public static short getSMsgcount() {
            return sMsgcount;
        }

        public static double getrResponseCount() {
            return rResponseCount;
        }

        public static double getsRequestCount() {
            return sRequestCount;
        }

        public static boolean isIsAlpha() {
            return isAlpha;
        }

        public static void setIsAlpha(boolean isalpha) {
            isAlpha = isalpha;
            if(isalpha) //Alpha Position is 1 and the Index is 5
                setSettingValue(1, 5, 1, null);
            else setSettingValue(1, 5, 0, null);
        }

        private static void openRecordStore(String recordName){
            recordParser = new RecordStoreParser();
            if(recordParser.openRecordStore(recordName, true,false,false))
                recordParser = null;
        }

        private static void closeRecordStore(){
            if(null != recordParser){
                recordParser.closeRecordStore();
                recordParser = null;
            }
        }
        
        public static void setSetting() {
            Calendar cal = Calendar.getInstance();
            cyear = (short)cal.get(Calendar.YEAR);
            cmonth = (byte)cal.get(Calendar.MONTH);
            cal = null;
            openRecordStore(RecordManager.setttingsRMS);
            if(null != recordParser){
                //CR 13294
                //Cr 13416
                int count = 0;
                byte[] rByte = null;
                if((count=recordParser.getNumRecords()) > 0){

                    //CR 13416
                    updateSettings(count);

                    rByte = recordParser.getRecord(1);

                    if(rByte[0] == 1)
                        isWelcome = true;
                    else isWelcome = false;
                    if(rByte[1] == 1)
                        isPinEnabled = true;
                    if(rByte[2] == 1)
                        isNotification = true;
                    else isNotification = false;
                    //CR 10118
                    if(rByte[3] == 1 || ChannelData.isIsDubugEnabled()){
                        isDebug = true;
                    }
                    if(rByte[4] == 1)
                        isAppEnable = true;
                    else isAppEnable = false;
                    if(rByte[5] == 1)
                        isAlpha = true;

                    sortType = rByte[6];

                    if(rByte[7] == 1)
                        isScreenRefresh = true;
                    else isScreenRefresh = false;

                    rByte = recordParser.getRecord(2);
                    if(null != rByte && rByte.length>0){
                        pinNumber = new String(rByte);
                    }

                    rByte = recordParser.getRecord(3);
                    if(null != rByte && rByte.length>0){
                        appeMsg = new String(rByte);
                    }

                    rByte = recordParser.getRecord(4);
                    if(null != rByte && rByte.length>0){
                        phoneNumber = new String(rByte);
                    } else {
                        //CR 0012061
                        phoneNumber = ChannelData.getUserPhoneNumber();
                        //<-CR 0012061->
                    }                    
                
                    rByte = recordParser.getRecord(5); //CR 7230
                    if(null != rByte && rByte.length>0){
                        appCatalogName = new String(rByte);
                    }

                    rByte = recordParser.getRecord(6); //CR 8352
                    if(null != rByte && rByte.length>0){
                        cCount = Integer.parseInt(new String(rByte));
                    }

                    rByte = recordParser.getRecord(7);
                    if(null != rByte){
                        //Grid
                        if(rByte[0] == 0)
                            isGrid = false;
                        
                        //GPRS
                        if(rByte[1] == 1){
                            isGPRS = true;
                            UISettings.MAX_COUNT = 1000;
                        }


                        if(rByte[2] == 1)
                            isShowMode = true;

                        //PCNF Received
                        if(rByte[3] == 1)
                            isPCNF = true;

                        //UCNF Received
                        if(rByte[4] == 1)
                            isUCNF = true;

                        //CR 14741
                        if(rByte[5] == 1){
                            isContactGridUpdate = true;
                        }
                    }

                    rByte = recordParser.getRecord(8); //l0669
                    if(null != rByte && rByte.length>0)
                        ChannelData.longCode = new String(rByte);

                    rByte = recordParser.getRecord(9); //Cr 12990
                    if(null != rByte && rByte.length>0)
                        UID = new String(rByte);
                    else UID = ChannelData.getUserUID(); //CR 12988

                } else {
                   updateSettings(count);
                }
                closeRecordStore();
            } 
        }

        private static void updateSettings(int count){
            byte[] rByte = null;
            if(count == 0)
                count++;
            for(int i=count;i<=9;i++){
                if(i ==1){
                    if(ChannelData.isIsDubugEnabled()){
                        rByte = new byte[]{1,0,1,1,1,0,0,0};
                        isDebug = true;
                    } else rByte = new byte[]{1,0,1,0,1,0,0,0};
                    
                    recordParser.addRecord(rByte,0, rByte.length, true);
                } else if(i == 2){
                    //CR 0012061
                    //CR 12360
                    phoneNumber = ChannelData.getUserPhoneNumber();
                    if(null != phoneNumber)
                        rByte = phoneNumber.getBytes();
                    else rByte = new byte[0];
                    //<-CR 0012061->
                    rByte = new byte[0];
                    recordParser.addRecord(rByte,0, rByte.length, true);
                } else if(i == 7){
                    //CR 12191
                    isShowMode = ChannelData.getUserModeIsShow();
                    //CR 12191

                    //CR 9001
                    if(isShowMode)
                        rByte = new byte[]{1,0,1,0,0,0,0,0};
                    else rByte = new byte[]{1,0,0,0,0,0,0,0};
                    recordParser.addRecord(rByte, 0, rByte.length, true);
                    isShowMode = false;
                } else if(i == 9){
                    //CR 12990
                    UID = ChannelData.getUserUID();
                    if(null != UID)
                        rByte = UID.getBytes();
                    else rByte = new byte[0];
                    recordParser.addRecord(rByte, 0, rByte.length, true);
                } else {
                    //CR 7230 - 5
                    //CR 8352 - 6
                    //CR 10669 - 8
                    rByte = new byte[0];
                    recordParser.addRecord(rByte,0, rByte.length, true);
                }
            }
        }

        public static void setSortAndAlphaMode(){
            byte[] rbyte = null;
            sortType = 0; //Sorttype postion is 1 and the index is 6
            isAlpha = false;
            openRecordStore(RecordManager.setttingsRMS);
            rbyte = recordParser.getRecord(1);
            rbyte[6] = 0;
            rbyte[5] = 0;
            recordParser.setRecord(1, rbyte ,0, rbyte.length, true);
            closeRecordStore();
        }
        
        private static void setSettingValue(int pos,int index,int sType, String value){
            byte[] rbyte = null;
            openRecordStore(RecordManager.setttingsRMS);
            if(pos == 1 || pos == 7){ //All Alpha Value Index Change Part
                rbyte = recordParser.getRecord(pos);
                rbyte[index] = (byte)sType;
            } else {//if((pos > 1 && pos < 7) || pos == 8){
                //2. Change the Pin Number
                //3. Change the Application Enable Message
                //4. Change the User Phone Number
                //5. appCatalog Name
                //6. SMS SequenceNumber
                //8. User Uqnique ID(UID)
                //Bug no 13351
                if(null != value)
                    rbyte = value.getBytes();
                else rbyte = new byte[0];
            } 
            recordParser.setRecord(pos, rbyte ,0, rbyte.length, true);
            closeRecordStore();
        }
        
	/**
	 * Method to Write the Application Settings.
         *
	 * @param settings - Object will contain the Application basic settings  Infromations
	 * 
         * @throws setSettingsException - Exception wil store in seperate file.
	 */
	public static void storeSettings() {
               openRecordStore(RecordManager.setttingsRMS);
                byte[] rByte = new byte[]{0,0,0,0,0,0,0,0};
                if(null != recordParser){
                    if(isWelcome)
                        rByte[0] = 1;

                    if(isPinEnabled)
                        rByte[1] = 1;

                    if(isNotification)
                        rByte[2] = 1;

                    if(isDebug)
                        rByte[3] = 1;

                    if(isAppEnable)
                        rByte[4] = 1;

                    if(isAlpha)
                        rByte[5] = 1;

                    rByte[6] = sortType;

                    if(isScreenRefresh)
                        rByte[7] = 1;

                    recordParser.setRecord(1, rByte,0,rByte.length, true);

                    if(null != pinNumber){
                        rByte = pinNumber.getBytes();
                        recordParser.setRecord(2, rByte,0,rByte.length, true);
                    }

                    if(null != appeMsg){
                        rByte = appeMsg.getBytes();
                        recordParser.setRecord(3, rByte,0,rByte.length, true);
                    }

                    if(null != phoneNumber){
                        rByte = phoneNumber.getBytes();
                        recordParser.setRecord(4, rByte,0,rByte.length, true);
                    }
                    
                    if(null != appCatalogName){ //CR 7230
                        rByte = appCatalogName.getBytes();
                        recordParser.setRecord(5, rByte, 0, rByte.length, true);
                    }

//                    if(null != UID){ //CR 12990
//                        rByte = UID.getBytes();
//                        recordParser.setRecord(9, rByte, 0, rByte.length, true);
//                    }

                    closeRecordStore();
                }
	}
        
        /** Get the Application Enable/Disable String */
        public static String getAppE_DMsg() {
            return appeMsg;
        }

        /** Set the Application Enable/Disable String */
        public static void setAppE_DMsg(String appMsg) {
            appeMsg = appMsg;
            setSettingValue(3, 0, 0, appeMsg);
        }

        /** Get the Application enable/diable Bit if its true Applicatiion is Enable otherwise Disable */
        public static boolean isIsAppEnable() {
            return isAppEnable;
        }

        /** Set the applciation enable/Disable bit if its true application is enable otherwise disable */
        public static void setIsAppEnable(boolean isApEnable) {
            isAppEnable = isApEnable;
            if(isAppEnable) //Application Enable bit position is 1 and the index is 4
                setSettingValue(1, 4, 1, null);
            else setSettingValue(1, 4, 0, null);
        }

        /** Get the Application Welcome Message bit if its true Application Message is Display
         * at the of time Application Start otherwise dont display the welcome Message 
         */
        public static boolean isIsWelcome() {
            return isWelcome;
        }

        /** Set the Application Welcome Message bit if its true Application Message is Display
         * at the time of Application Start otherwise dont display the welcome Message 
         */
        public static void setIsWelcome(boolean isWelcme) {
            isWelcome = isWelcme;
            if(isWelcome) // Wlcome Screen Postion is 1 and the index is 0
                setSettingValue(1, 0, 1, null);
            else setSettingValue(1, 0, 0, null);
        }

        /** Get the Application Security PinNumber */
	public static String getPinNumber() {
		return pinNumber;
	}

        /** Set the Application Security PinNumber */
	public static boolean getIsDebug() {
		return isDebug;
	}

        /** Get the Notification Sound Bit if its true When the Message Receive to raise the Sound otherwise 
         * dont raise the sound
         */
	public static boolean getIsNotification() {
		return isNotification;
	}

        /** Get the Security Pin enable Bit if its true Security pin is enabled otherwise disabled */
	public static boolean getIsPinEnabled() {
		return isPinEnabled;
	}

        /** Set the Security Pin enable Bit if its true Security pin is enabled otherwise disabled */
	public static void setPinNumber(String pinNum) {
		pinNumber = pinNum;
                setSettingValue(2, 0, 0, pinNumber);
	}

        /** Set the Application Debug Mode Bit if its true send message to be stored in the inbox
         * otherwise dont store the message in inbox 
         */
	public static void setIsDebug(boolean isDbug) {            
		isDebug = isDbug;            
                if(isDebug) // Debug enable bit position is 1 and the inbdex is 3
                    setSettingValue(1, 3, 1, null);
                else setSettingValue(1, 3, 0, null);
	}

        /** Set the Notification Sound Bit if its true When the Message Receive to raise the Sound otherwise 
         * dont raise the sound
         */
        public static void setIsNotification(boolean notification) {
		isNotification = notification;
                if(isNotification) //notification enable bit position is 1 and the index is 2
                    setSettingValue(1, 2, 1, null);
                else setSettingValue(1, 2, 0, null);
	}

        /** Set the Security Pin enable Bit if its true Security pin is enabled otherwise disabled */
	public static void setIsPinEnabled(boolean isPnEnabled) {
		isPinEnabled = isPnEnabled;
                if(isPinEnabled) //pinEnabled bit position is 1 and the index is 1
                    setSettingValue(1, 1, 1, null);
                else setSettingValue(1, 1, 0, null);
	}
	
        /** Get the Inbox sort type
         *  <li> 0. Date sort </li>
         *  <li> 1. ProfileName Sort </li>
         */
	public static byte getSortType() {
		return sortType;
	}
	
        /** Set the Inbox sort type
         *  <li> 0. Date sort </li>
         *  <li> 1. ProfileName Sort </li>
         */
	public static void setSortType(byte sType) {
		sortType = sType; //Sorttype postion is 1 and the index is 6
                setSettingValue(1, 6, sortType, null);
                isAlpha = !isAlpha;
                if(isAlpha) //Alpha sorttype position is 1 and the index is 5
                    setSettingValue(1, 5, 1, null);
                else setSettingValue(1, 5, 0, null);
	}


        public static void updateSendAndReceiveMessageCount(String recordName, double sCount, double rCount, boolean isSms){
            try{
            Calendar cal = Calendar.getInstance();
                if(cmonth != cal.get(Calendar.MONTH) || cyear != cal.get(Calendar.YEAR)){
                    cmonth = (byte) cal.get(Calendar.MONTH);
                    cyear = (short) cal.get(Calendar.YEAR);
                }
                openRecordStore(recordName);
                boolean isCh = false;
                if(recordParser!=null){
                    byte mon=0;
                    short yr=0;
                    int i=1;
                    int num= recordParser.getNumRecords();
                    if(isSms){
                        if(rMessageIndex>0)
                            num = i= rMessageIndex;
                    } else {
                        if(rRequestIndex>0)
                            num = i= rRequestIndex;
                    }
                    boolean isNotfind = true;
                    byte[] rbyte = null;
                    for(;i<=num && isNotfind;i++){
                        rbyte = recordParser.getRecord(i);
                        if(null != rbyte){
                            ByteArrayReader din = new ByteArrayReader(recordParser.getRecord(i));
                            mon = din.readByte();
                            yr =  din.readShort();
                            if(cmonth == mon && yr==cyear){
                               if(isSms){
                                   sCount += din.readShort();
                                    rCount += din.readShort();
                                    rMessageIndex = i;
                                } else{
                                   sCount += din.readDouble();
                                   rCount += din.readDouble();
                                    rRequestIndex = i;
                                }
                               isCh=true;
                               isNotfind = false;
                            }
                            din.close();
                            din = null;
                        }
                    }
                    ByteArrayWriter dout = new ByteArrayWriter();
                    dout.writeByte((byte)cal.get(Calendar.MONTH));
                    dout.writeShort(cyear);
                    if(isSms){
                        dout.writeShort((short)sCount);
                        monthlySentCount = (short)sCount;
                        dout.writeShort((short)rCount);
                    } else {
                        dout.writeDouble(sCount);
                        monthlyUploadedDataCount = sCount;
                        dout.writeDouble(rCount);
                    }
                    rbyte = dout.toByteArray();
                    if(isCh)
                        if(isSms)
                            recordParser.setRecord(rMessageIndex, rbyte,0,rbyte.length, true);
                        else recordParser.setRecord(rRequestIndex, rbyte,0,rbyte.length, true);
                    else{
                        recordParser.addRecord(rbyte,0,rbyte.length, true);
                        if(isSms)
                            rMessageIndex = recordParser.getNumRecords();
                        else rRequestIndex = recordParser.getNumRecords();
                    }
                    dout.close();
                    dout = null;
                 }
                closeRecordStore();
            }catch(Exception e){}
        }
        
        public static void increaseReceiveMsgcount(){
            rMsgCount++;
            updateSendAndReceiveMessageCount(RecordManager.allMessageCountRMS,0,1,true);
        }
        
        public static void increaseSendMsgCount(){
            sMsgcount++;
            updateSendAndReceiveMessageCount(RecordManager.allMessageCountRMS,1,0,true);
        }

        //<-CR 0012014 ->
        public static void increaseReceiveRequestcount(int size){
            rResponseCount+=size;
            updateSendAndReceiveMessageCount(RecordManager.allGprsRequestCountRMS,0,size,false);
        }

        public static void increaseSentRequestCount(int size){
            sRequestCount+=size;
            updateSendAndReceiveMessageCount(RecordManager.allGprsRequestCountRMS,size,0,false);
        }
        //CR<- 0012014 ->
}
