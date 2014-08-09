
import generated.Build;

public class ChannelData {

    public static int iConWidth = Build.ICONWIDTH;
    /**  Variable to hold the Server Name */
    private static String sName = null;
    /** Variable to hold the Server Number */
    public static String longCode = null;
    /** Variable to hold the Prevenence Name */
    private static String pName = null;
    /** Variable to hold the port number depends on the server */
    private static String portNumber = null;
    /** Variable to hold the OS type  */
    public static String osName = "J2ME";
    /** Varibale to hold the initial app compress url */
    private static String url = null;
    /** High Periority queue Name */
    public static final byte HIGH = 1;
    /** Medium Periority queue Name */
    public static final byte MEDIUM = 2;
    /** Low Periority Queue Name */
    public static final byte LOW = 3;
    /** PROV Periority Queue Name */
    public static final byte PROV = 4;
    /** UIDA Periority Queue Name */
    public static final byte UIDA = 5;
    /**  Variable is having the Start Deleimiter Character (To differenciate the Server Message) */
    public static final String SDChar = "!*";
    /** Vsriable is having the End Delimiter Character (To differenciate the Server Message) */
    public static final String EDChar = "*!";
    /** varaible to hold the chatSequence Status separtor*/
    public static final String CHAT_SEQUENCE_SEPARATOR = ":";
    /** Variable to hold the log file uploading url */
    private static String upLoadURL = null;
    /** Variable to hold the region */
    private static String region = null;
    /** indirect App relegion level shortcode Name */
    public static String indireShortCodeName = "[shorthandsc]";
    public static String globalShortCode = "memGLOBALShortCode";
    public static String globalUserPhone = "memGLOBALUserPhone";//CR 0012069
    public static String globalBearerProtocol = "memGLOBALBearerProtocol";
    public static String globalDataMode = "memGLOBALDataMode";
    public static String globalUserId ="memGLOBALUID"; //CR 12988
    public static String globalClientState ="memGLOBALClientState";
    //Cr 14327
    public static String globalChatSequence = "memGLOBALChatSequence";

    //CR 14694
    public static String globalProfilePictureTag = "memGLOBALProfilePic";

    //CR 14733
    public static String globalContctRefresh = "memGLOBALContactsRefresh";

    //CR 14823
    public static String globalContactType = "memGLOBALContactType";

    //CR 14824
    public static String globalContactAdd = "memGLOBALAddContact";
    
    public static String allContacts = "ALLCONTACTS";
    /** Varibale to hoid the Htto download Url */
    private static String download_url = null;
    /** Varibale to hold the JAD Less Detils */
    private static String[] jadLessDetails = null;
    private static boolean isNotSet = true;
    private static boolean isQWERTY = false;
    private static boolean isNotQWERTYSet = true;
    private static String deleteApps = null;
    private static String deleteApp = null;
    private static String tosEnable = null;
    private static String parnterName = null;
    private static String clientName = null;
    private static String bundleName = null;
    private static String aboutTxt = null;
    private static String appCatalogInclude = null;
    private static int monthlyMessageSentPopupCount = -1;
    private static String countDisplay = null;
    private static String ShowTxtSumm = null;
    private static String provQue = null;
    private static String showLeftIcon = null;
    private static String symoblConver = null;
    private static String waterMark = null;
    private static boolean isDubugEnabled = false;
    private static int inboxLimit = -1;
    private static String rmsAlwaysClose = null;
    private static String RTD = null;
    //CR 13900
    private static String imageUrl = null;

    //CR 14111
    private static String uploadImageUrl = null;

    //CR 14492
    private static String downloadAudioUrl = null;

    // 14465
    private static String uploadAudioUrl = null;

    private static String downloadvideoUrl = null;

    private static String uploadVideoUrl = null;

    //CR 14694
    private static String uploadProfilePictureUrl = null;

    private static String downloadProfilePictureUrl = null;

    //CR 14742, 14741
    private static String contactThumbNailDownloadUrl = null;

    private static String socketUrl = null;
   static String globalUserCity = "memGLOBALUserCity";
   static String globalUserCountry ="memGLOBALUserCountry";
 //   <--cr13617
    static String globalMaxDataMode = "memGLOBALMaxDataMode";

//++cr13617
    private static byte isSMSMode = -1;

    private static String socketPort = null;

    public static String getSocketPort() {
        if (null == socketPort) {
            socketPort = getJadLessDetails("SocketPort");
            if (null == socketPort) {
                try {
                    socketPort = ObjectBuilderFactory.GetProgram().getAppProperty("SocketPort");
                } catch (Exception e) {
                }
            }
        }
        if(null == socketPort || socketPort.length() == 0)
            socketPort = "5000";
        return socketPort;
    }



    // <-- CR 13222
    public static boolean isSMSMode() {
        if (isSMSMode == -1) {
            String smsMode = getJadLessDetails("SMSAllowed");
            if (null == smsMode) {
                try {
                    smsMode = ObjectBuilderFactory.GetProgram().getAppProperty("SMSAllowed");
                } catch (Exception e) { }
            }
            if(null != smsMode && smsMode.toLowerCase().compareTo("true") == 0)
                isSMSMode = 1;
            else isSMSMode = 0;
        }
        if(isSMSMode == 1)
            return true;
        return false;
    }
// CR 13222 -->
    //CR 11974
    public static String getSocketUrl() {
        if (null == socketUrl) {
            socketUrl = getJadLessDetails("SocketUrl");
            if (null == socketUrl) {
                try {
                    socketUrl = ObjectBuilderFactory.GetProgram().getAppProperty("SocketUrl");
                } catch (Exception e) {
                }
            } 
        }
//++ CR 13597
// Commented out following lines
//        if(null == socketUrl)
//            socketUrl ="doodad.shorthandmobile.com";
        // "http://doodad.shorthandmobile.com/trials/j2me/test/sasi.jad";
//-- CR 13597
        return socketUrl;
    }

    //CR 13229
    public static String getJadHeight(){
        String height = getJadLessDetails("Height");
        if (null == height) {
            try {
                height = ObjectBuilderFactory.GetProgram().getAppProperty("Height");
            } catch (Exception e) {
            }
        }
        if(null == height || height.length() ==0)
            height = UISettings.formHeight+"";
        return height;
    }

    public static String getJadWidth(){
        String width = getJadLessDetails("Width");
        if (null == width) {
            try {
                width = ObjectBuilderFactory.GetProgram().getAppProperty("Width");
            } catch (Exception e) {
            }
        }
        if(null == width || width.length() ==0)
            width = UISettings.formWidth+"";
        return width;
    }

    //CR 11974
    private static String dataRequestUrl = null;

    //CR 11974
    public static String getDataRequestUrl() {
        if (null == dataRequestUrl) {
            dataRequestUrl = getJadLessDetails("DataRequestUrl");
            if (null == dataRequestUrl) {
                try {
                    dataRequestUrl = ObjectBuilderFactory.GetProgram().getAppProperty("DataRequestUrl");
                } catch (Exception e) {
                }
            } 
        }
        // "http://doodad.shorthandmobile.com/trials/j2me/test/sasi.jad";
        return dataRequestUrl;
    }



    //CR 0012061
    public static String getUserPhoneNumber() {
        String userNumber = getJadLessDetails(globalUserPhone);
        if (null == userNumber) {
            try {
                userNumber = ObjectBuilderFactory.GetProgram().getAppProperty(globalUserPhone);
            } catch (Exception e) {
            }
        }
        return userNumber;
    }
    //<-CR 0012061->

    //CR 0012988
    public static String getUserUID() {
        String uniqueId = getJadLessDetails(globalUserId);
        if (null == uniqueId) {
            try {
                uniqueId = ObjectBuilderFactory.GetProgram().getAppProperty(globalUserId);
            } catch (Exception e) {
            }
        }
        return uniqueId;
    }

    //CR 12191
    public static boolean getUserModeIsShow() {
        String showMode = getJadLessDetails("memGLOBALShowMode");
        if (null == showMode) {
            try {
                showMode = ObjectBuilderFactory.GetProgram().getAppProperty("memGLOBALShowMode");
            } catch (Exception e) {
            }
        }
        if(null != showMode && showMode.toLowerCase().compareTo("true") == 0)
            return true;
        return false;
    }
    //<-CR 12191->


    public static int getInboxLimit() {
        if (inboxLimit < 0) {
            String limit = getJadLessDetails("InbxMsgLimit");
            if (null == limit) {
                try {
                    limit = ObjectBuilderFactory.GetProgram().getAppProperty("InbxMsgLimit");
                } catch (Exception e) {
                }
                if (null == limit) {
                    limit = "";
                }
            }
            if (null != limit && limit.length() > 0) {
                inboxLimit = Integer.parseInt(limit);
            } else {
                inboxLimit = 0;
            }
        }
        //return 10;
        return inboxLimit;
    }

    //CR No 11444
    public static boolean isRMSAlwaysClose() {
        if (null == rmsAlwaysClose) {
            rmsAlwaysClose = getJadLessDetails("AlwaysCloseRMS");
            if (null == rmsAlwaysClose) {
                try {
                    rmsAlwaysClose = ObjectBuilderFactory.GetProgram().getAppProperty("AlwaysCloseRMS");
                } catch (Exception e) {
                }
                if (null == rmsAlwaysClose) {
                    rmsAlwaysClose = "";
                }
            }
        }
        if (null != rmsAlwaysClose && rmsAlwaysClose.length() > 0) {
            if (rmsAlwaysClose.toLowerCase().compareTo("true") == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundTripTime() {
        //String RTD = null;
        if (RTD == null) {
            RTD = getJadLessDetails("RTD");
            if (null == RTD) {
                try {
                    RTD = ObjectBuilderFactory.GetProgram().getAppProperty("RTD");
                } catch (Exception e) {
                }
                if (null == RTD) {
                    RTD = "true";
                }
            }
        }
        if (null != RTD && RTD.length() > 0) {
            if (RTD.toLowerCase().compareTo("false") == 0) {
                return false;
            } else {
                return true;
            }
        }

        return true;

    }

    //private static String debugOn = null;
    public static boolean isIsDubugEnabled() {
        String debugOn = null;
        debugOn = getJadLessDetails("DbugOn");
        if (null == debugOn) {
            try {
                debugOn = ObjectBuilderFactory.GetProgram().getAppProperty("DbugOn");
            } catch (Exception e) {
            }
            if (null == debugOn) {
                debugOn = "";
            }
        }
        if (null != debugOn && debugOn.length() > 0) {
            if (debugOn.toLowerCase().compareTo("true") == 0) {
                isDubugEnabled = true;
            }
        }
        return isDubugEnabled;

    }

    public static boolean isZeroAppendRegion() {
        getRegion();
        if (null != region && region.toLowerCase().compareTo("india") == 0) {
            return true;
        }
        return false;
    }

    public static boolean isWaterMark() {
        boolean isConvert = false;
        if (null == waterMark) {
            waterMark = getJadLessDetails("WaterMark");
            if (null == waterMark) {
                try {
                    waterMark = ObjectBuilderFactory.GetProgram().getAppProperty("WaterMark");
                } catch (Exception e) {
                }
                if (null == waterMark) {
                    waterMark = "";
                }
            }
        }
        if (null != waterMark && waterMark.length() > 0) {
            if (waterMark.toLowerCase().compareTo("true") == 0) {
                isConvert = true;
            }
        }
        return isConvert;
    }

    public static boolean isSymbolConvert() {
        boolean isConvert = false;
        if (null == symoblConver) {
            symoblConver = getJadLessDetails("SymoblConvert");
            if (null == symoblConver) {
                try {
                    symoblConver = ObjectBuilderFactory.GetProgram().getAppProperty("SymoblConvert");
                } catch (Exception e) {
                }
                if (null == symoblConver) {
                    symoblConver = "";
                }
            }
        }
        if (null != symoblConver && symoblConver.length() > 0) {
            if (symoblConver.toLowerCase().compareTo("true") == 0) {
                isConvert = true;
            }
        }
        return isConvert;
    }

    public static boolean isLeftIconShow() {
        boolean isShow = true;
        if (null == showLeftIcon) {
            showLeftIcon = getJadLessDetails("ShowTopLeftIcon");
            if (null == showLeftIcon) {
                try {
                    showLeftIcon = ObjectBuilderFactory.GetProgram().getAppProperty("ShowTopLeftIcon");
                } catch (Exception e) {
                }
                if (null == showLeftIcon) {
                    showLeftIcon = "";
                }
            }
        }
        if (null != showLeftIcon && showLeftIcon.length() > 0) {
            if (showLeftIcon.toLowerCase().compareTo("false") == 0) {
                isShow = false;
            }
        }
        return isShow;
    }

    public static boolean getProvQue() {
        boolean isShowTxtSumm = false;
        if (null == provQue) {
            provQue = getJadLessDetails("QueuePROV");
            if (null == provQue) {
                try {
                    provQue = ObjectBuilderFactory.GetProgram().getAppProperty("QueuePROV");
                } catch (Exception e) {
                }
                if (null == provQue) {
                    provQue = "";
                }
            }
        }
        if (null != provQue && provQue.length() > 0) {
            if (provQue.toLowerCase().compareTo("true") == 0) {
                isShowTxtSumm = true;
            }
        }
        return isShowTxtSumm;
    }

    public static boolean isPushEnabled() {
        boolean isEnabled = false;
        try {
            if (null != ObjectBuilderFactory.GetProgram().getAppProperty("MIDlet-Push-1")) {
                isEnabled = true;
            }
        } catch (Exception e) {
        }
        return isEnabled;
    }

    public static boolean getMessageTextSummery() {
        boolean isShowTxtSumm = true;
        if (null == ShowTxtSumm) {
            ShowTxtSumm = getJadLessDetails("ShowTxtSumm");
            if (null == ShowTxtSumm) {
                try {
                    ShowTxtSumm = ObjectBuilderFactory.GetProgram().getAppProperty("ShowTxtSumm");
                } catch (Exception e) {
                }
                if (null == ShowTxtSumm) {
                    ShowTxtSumm = "";
                }
            }
        }
        if (null != ShowTxtSumm && ShowTxtSumm.length() > 0) {
            if (ShowTxtSumm.toLowerCase().compareTo("false") == 0) {
                isShowTxtSumm = false;
            }
        }
        return isShowTxtSumm;
    }

    public static int getMonthlyMessageSentPopupCount() {
        if (getCountDisplay() && monthlyMessageSentPopupCount == -1) {
            String value = getJadLessDetails("TextUsageWarnLimit");
            if (null == value) {

                try {
                    value = ObjectBuilderFactory.GetProgram().getAppProperty("TextUsageWarnLimit");
                } catch (Exception e) {
                }
                if (null == value) {
                    value = "";
                }

            }
            if (null != value && value.length() > 0) {
                monthlyMessageSentPopupCount = Integer.parseInt(value);
            } else {
                monthlyMessageSentPopupCount = 0;
            }
        }
        return monthlyMessageSentPopupCount;
    }

    private static boolean getCountDisplay() {
        boolean isAppCatalogDisplay = false;
        if (null == countDisplay) {
            countDisplay = getJadLessDetails("ShowTextUsageWarning");
            if (null == countDisplay) {

                try {
                    countDisplay = ObjectBuilderFactory.GetProgram().getAppProperty("ShowTextUsageWarning");
                } catch (Exception e) {
                }
                if (null == countDisplay) {
                    countDisplay = "";
                }

            }
        }
        if (null != countDisplay && countDisplay.length() > 0) {
            if (countDisplay.toLowerCase().compareTo("true") == 0) {
                isAppCatalogDisplay = true;
            }
        }
        return isAppCatalogDisplay;
    }

    public static boolean getAppCatalogInclude() {
        boolean isAppCatalogDisplay = true;
        if (null == appCatalogInclude) {
            appCatalogInclude = getJadLessDetails("AppCatalog");
            if (null == appCatalogInclude) {
                try {
                    appCatalogInclude = ObjectBuilderFactory.GetProgram().getAppProperty("AppCatalog");
                } catch (Exception e) {
                }
                if (null == appCatalogInclude) {
                    appCatalogInclude = "";
                }

            }
        }
        if (null != appCatalogInclude && appCatalogInclude.length() > 0) {
            if (appCatalogInclude.toLowerCase().compareTo("false") == 0) {
                isAppCatalogDisplay = false;
            }
        }

        return isAppCatalogDisplay;
    }

    public static String getAboutTxt() {
        if (null == aboutTxt) {
            aboutTxt = getJadLessDetails("AboutTxt");
            if (null == aboutTxt) {
                try {
                    aboutTxt = ObjectBuilderFactory.GetProgram().getAppProperty("AboutTxt");
                } catch (Exception e) {
                }
                if (null != aboutTxt) {
                    aboutTxt += "\nV" + ObjectBuilderFactory.GetKernel().getVersionNumber(false);
                }
            } else {
                if (aboutTxt.length() > 0) {
                    aboutTxt += "\nV" + ObjectBuilderFactory.GetKernel().getVersionNumber(false);
                }
            }
        }
        return aboutTxt;
    }

    public static String getBundleName() {
        if (null == bundleName) {
            bundleName = getJadLessDetails("BundleName");
            if (null == bundleName) {
                try {
                    bundleName = ObjectBuilderFactory.GetProgram().getAppProperty("BundleName");
                } catch (Exception e) {
                }
                if (null == bundleName) {
                    bundleName = "";
                }
            }
        }
        //return "RollTe";
        return bundleName;
    }

    public static String getParnterName() {
        if (null == parnterName) {
            parnterName = getJadLessDetails("ParnterName");
            if (null == parnterName) {
                try {
                    parnterName = ObjectBuilderFactory.GetProgram().getAppProperty("ParnterName");
                } catch (Exception e) {
                }
            }
        }
        if (null == parnterName || parnterName.length() == 0) {
            parnterName = Constants.appName;
        }
        return parnterName;
    }

    public static String getClientName() {
        if (null == clientName) {
            clientName = getJadLessDetails("ClientName");
            if (null == clientName) {
                try {
                    clientName = ObjectBuilderFactory.GetProgram().getAppProperty("ClientName");
                } catch (Exception e) {
                }
            }
        }
        if (null == clientName || clientName.length() == 0) {
            clientName = Constants.appName;
        }
        return clientName;
    }

    public static boolean getTosEnable() {
        boolean isTosEnable = false;
        if (null == tosEnable) {
            tosEnable = getJadLessDetails("TOSEnable");
            if (null == tosEnable) {
                try {
                    tosEnable = ObjectBuilderFactory.GetProgram().getAppProperty("TOSEnable");
                } catch (Exception e) {
                }
                if (null == tosEnable) {
                    tosEnable = "";
                }
            }
        }
        if (tosEnable.length() > 0) {
            if (tosEnable.compareTo("true") == 0) {
                isTosEnable = true;
            }
        }
        return isTosEnable;
    }

    public static boolean getDeleteApp() {
        boolean isDeleteApp = false;
        if (null == deleteApp) {
            deleteApp = getJadLessDetails("DeleteApp");
            if (null == deleteApp) {

                try {
                    deleteApp = ObjectBuilderFactory.GetProgram().getAppProperty("DeleteApp");
                } catch (Exception e) {
                }
                if (null == deleteApp) {
                    deleteApp = "";
                }

            }
        }
        if (deleteApp.length() > 0) {
            if (deleteApp.compareTo("true") == 0) {
                isDeleteApp = true;
            }
        }
        return isDeleteApp;
    }

    public static boolean getDeleteApps() {
        boolean isDeleteApps = false;
        if (null == deleteApps) {
            deleteApps = getJadLessDetails("DeleteApps");
            if (null == deleteApps) {

                try {
                    deleteApps = ObjectBuilderFactory.GetProgram().getAppProperty("DeleteApps");
                } catch (Exception e) {
                }
                if (null == deleteApps) {
                    deleteApps = "";
                }

            }
        }
        if (deleteApps.length() > 0) {
            if (deleteApps.compareTo("true") == 0) {
                isDeleteApps = true;
            }
        }
        return isDeleteApps;
    }

    public static boolean isQwertyKeypad() {
        if (isNotQWERTYSet) {
            isNotQWERTYSet = false;
            String qwerty = getJadLessDetails("Keypad");
            if (null == qwerty) {

                try {
                    qwerty = ObjectBuilderFactory.GetProgram().getAppProperty("Keypad");
                } catch (Exception e) {
                }
                if (null != qwerty && qwerty.compareTo("QWERTY") == 0) {
                    isQWERTY = true;
                }

            } else {
                if (qwerty.compareTo("QWERTY") == 0) {
                    isQWERTY = true;
                }
            }
        }
        return isQWERTY;
    }

    public static int getIconHeight() {
        String value = getJadLessDetails("ImageHeight");
        if (null == value) {
            try {
                value = ObjectBuilderFactory.GetProgram().getAppProperty("ImageHeight");
            } catch (Exception e) {
            }

        }
        if (null != value && value.length() > 0) {
            return Integer.parseInt(value);
        }
        return 24;
    }

    public static boolean isInitialAppsDownload() {
        boolean isInitialdownload = false;
        String appdownload = getJadLessDetails("Download-Url");
        if (null == appdownload) {
            try {
                appdownload = ObjectBuilderFactory.GetProgram().getAppProperty("Download-Url");
            } catch (Exception e) {
            }
            if (null == appdownload) {
                appdownload = "";
            }

        } else {
            if (appdownload.length() == 0) {
                appdownload = null;
            }
        }
        if (null != appdownload && appdownload.trim().length() > 0) {
            isInitialdownload = true;
        }
        //return false;
        return isInitialdownload;
    }

    public static String getResourcesApps() {
        String resourcesApps = null;
        String appdownload = getJadLessDetails("AppIncluded");
        if (null == appdownload) {

            try {
                appdownload = ObjectBuilderFactory.GetProgram().getAppProperty("AppIncluded");
            } catch (Exception e) {
            }

        } else {
            if (appdownload.length() == 0) {
                appdownload = null;
            }
        }
        if (null != appdownload && appdownload.trim().length() > 0) {
            resourcesApps = appdownload.trim();
        }

        //return "RollTech2";
        return resourcesApps;
    }

    public static String getInitialDownloadUrl() {
        if (null == download_url) {
            download_url = getJadLessDetails("Download-Url");
            if (null == download_url) {

                try {
                    download_url = ObjectBuilderFactory.GetProgram().getAppProperty("Download-Url");
                } catch (Exception e) {
                }
                if (null != download_url) {
                    download_url = download_url.trim();
                }

            }
        }
        //return null;
        return download_url;
    }

    //CR 13900
    public static String getImageUrl() {
        if (null == imageUrl) {
            imageUrl = getJadLessDetails("Image-Url");
            if (null == imageUrl) {

                try {
                    imageUrl = ObjectBuilderFactory.GetProgram().getAppProperty("Image-Url");
                } catch (Exception e) {
                }
                if (null != imageUrl) {
                    imageUrl = imageUrl.trim();
                }

            }
        }
        //return null;
        return imageUrl;
    }

    //CR 14111
    public static String getChatImageUrl() {
        if (null == uploadImageUrl) {
            uploadImageUrl = getJadLessDetails("ChatImageUrl");
            if (null == uploadImageUrl) {

                try {
                    uploadImageUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ChatImageUrl");
                } catch (Exception e) {
                }
                if (null != uploadImageUrl) {
                    uploadImageUrl = uploadImageUrl.trim();
                }
            }
        }
        return uploadImageUrl;
    }

    //CR 14485
    public static String getAudioUploadUrl() {
        if (null == uploadAudioUrl) {
            uploadAudioUrl = getJadLessDetails("ChatAudioUrl");
            if (null == uploadAudioUrl) {

                try {
                    uploadAudioUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ChatAudioUrl");
                } catch (Exception e) {
                }
                if (null != uploadAudioUrl) {
                    uploadAudioUrl = uploadAudioUrl.trim();
                }
            }
        }
        return uploadAudioUrl;
    }

    //CR 14492
    public static String getAudioDownloadUrl() {
        if (null == downloadAudioUrl) {
            downloadAudioUrl = getJadLessDetails("Audio-Url");
            if (null == downloadAudioUrl) {

                try {
                    downloadAudioUrl = ObjectBuilderFactory.GetProgram().getAppProperty("Audio-Url");
                } catch (Exception e) {
                }
                if (null != downloadAudioUrl) {
                    downloadAudioUrl = downloadAudioUrl.trim();
                }
            }
        }
        return downloadAudioUrl;
    }


    //CR 14485
    public static String getVideoUploadUrl() {
        if (null == uploadVideoUrl) {
            uploadVideoUrl = getJadLessDetails("ChatVideoUrl");
            if (null == uploadVideoUrl) {

                try {
                    uploadVideoUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ChatVideoUrl");
                } catch (Exception e) {
                }
                if (null != uploadVideoUrl) {
                    uploadVideoUrl = uploadVideoUrl.trim();
                }
            }
        }
        return uploadVideoUrl;
    }

    //CR 14492
    public static String getVideoDownloadUrl() {
        if (null == downloadvideoUrl) {
            downloadvideoUrl = getJadLessDetails("Video-Url");
            if (null == downloadvideoUrl) {

                try {
                    downloadvideoUrl = ObjectBuilderFactory.GetProgram().getAppProperty("Video-Url");
                } catch (Exception e) {
                }
                if (null != downloadvideoUrl) {
                    downloadvideoUrl = downloadvideoUrl.trim();
                }
            }
        }
        return downloadvideoUrl;
    }

// CR 14694
    public static String getProfilePictureUploadUrl() {
        if (null == uploadProfilePictureUrl) {
            uploadProfilePictureUrl = getJadLessDetails("ProfileUploadUrl");
            if (null == uploadProfilePictureUrl) {

                try {
                    uploadProfilePictureUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ProfileUploadUrl");
                } catch (Exception e) {
                }
                if (null != uploadProfilePictureUrl) {
                    uploadProfilePictureUrl = uploadProfilePictureUrl.trim();
                }
            }
        }
        return uploadProfilePictureUrl;
    }

// CR 14694
    public static String getProfilePictureDownloadUrl() {
        if (null == downloadProfilePictureUrl) {
            downloadProfilePictureUrl = getJadLessDetails("ProfileDownloadUrl");
            if (null == downloadProfilePictureUrl) {

                try {
                    downloadProfilePictureUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ProfileDownloadUrl");
                } catch (Exception e) {
                }
                if (null != downloadProfilePictureUrl) {
                    downloadProfilePictureUrl = downloadProfilePictureUrl.trim();
                }
            }
        }
        return downloadProfilePictureUrl;
    }

    //CR 14741, 14742
    public static String getContactGridThumbNailDownloadUrl() {
        if (null == contactThumbNailDownloadUrl) {
            contactThumbNailDownloadUrl = getJadLessDetails("ThumbDownloadUrl");
            if (null == contactThumbNailDownloadUrl) {

                try {
                    contactThumbNailDownloadUrl = ObjectBuilderFactory.GetProgram().getAppProperty("ThumbDownloadUrl");
                } catch (Exception e) {
                }
                if (null != contactThumbNailDownloadUrl) {
                    contactThumbNailDownloadUrl = contactThumbNailDownloadUrl.trim();
                }
            }
        }
        return contactThumbNailDownloadUrl;
    }

    public static String getRegion() {
        if (null == region) {
            region = getJadLessDetails("Region");
            if (null == region) {

                try {
                    region = ObjectBuilderFactory.GetProgram().getAppProperty("Region");
                } catch (Exception e) {
                }
                if (null != region) {
                    region = region.trim();
                } else {
                    region = "US";
                }

            } else {
                if (region.length() == 0) {
                    region = "US";
                }
            }
        }
        return region;
    }

    public static String getServerName() {
        if (null == sName) {
            sName = getJadLessDetails("ServerName");
            if (null == sName) {
                try {
                    sName = ObjectBuilderFactory.GetProgram().getAppProperty("ServerName");
                } catch (Exception e) {
                }
                if (null != sName) {
                    sName = sName.trim();
                } else {
                    sName = "";
                }

            } else {
                sName = sName.trim();
                if (sName.length() > 0) {
                    sName = sName;
                }
            }
        }
        return sName;
    }

    public static String getShortcode() {
        if (null == longCode) {
            //CR 10472 Changed ShortCode to memGLOBALShortCode
            longCode = getJadLessDetails(globalShortCode);
            if (null == longCode) {

                try {
                    longCode = ObjectBuilderFactory.GetProgram().getAppProperty(globalShortCode);
                } catch (Exception e) {
                }
                if (null != longCode) {
                    longCode = longCode.trim();
                } else {
                    longCode = "767767";
                }

            } else {
                if (longCode.length() == 0) {
                    longCode = "767767";
                }
            }
        }
        return longCode;
    }

    public static String getProvenenceName() {
        if (null == pName) {
            pName = getJadLessDetails("Provenance");
            if (null == pName) {

                try {
                    pName = ObjectBuilderFactory.GetProgram().getAppProperty("Provenance");
                } catch (Exception e) {
                }
                if (null != pName) {
                    pName = pName.trim();
                } else {
                    pName = "mdsr1";
                }

            } else {
                if (pName.length() == 0) {
                    pName = "mdsr1";
                }
            }
        }
        return pName;
    }

    public static String getPortNumber() {
        if (null == portNumber) {
            portNumber = getJadLessDetails("PortNumber");
            if (null == portNumber) {

                try {
                    portNumber = ObjectBuilderFactory.GetProgram().getAppProperty("PortNumber");
                } catch (Exception e) {
                }
                if (null != portNumber) {
                    portNumber = portNumber.trim();
                } else {
                    portNumber = "3333";
                }

            } else {
                if (portNumber.length() == 0) {
                    portNumber = "3333";
                }
            }
        }
        return portNumber;
    }

    public static String getUploadUrl() {
        if (null == upLoadURL) {
            upLoadURL = getJadLessDetails("Upload-Url");
            if (null == upLoadURL) {

                try {
                    upLoadURL = ObjectBuilderFactory.GetProgram().getAppProperty("Upload-Url");
                } catch (Exception e) {
                }
                if (null != upLoadURL) {
                    upLoadURL = upLoadURL.trim();
                }

            }
        }
        return upLoadURL;
    }

    public static String getMasterAppName() {
        String masterAppName = null;
        String mApp = getJadLessDetails("MasterApp");
        if (null == mApp) {
            try {
                mApp = ObjectBuilderFactory.GetProgram().getAppProperty("MasterApp");
            } catch (Exception e) {
            }
        }
        if (null != mApp && mApp.trim().length() > 0) {
            masterAppName = RecordManager.getRecordStoreName(RecordManager.getActualWidgetName(mApp.trim()));
        }
        return masterAppName;
    }

    public static String getWelcomeMessage() {
        String welcomeMessage = null;
        String welMsg = getJadLessDetails("WelcomeMsg");
        if (null == welMsg) {
            try {
                welMsg = ObjectBuilderFactory.GetProgram().getAppProperty("WelcomeMsg");
            } catch (Exception e) {
            }
        }
        if (null != welMsg && welMsg.trim().length() > 0) {
            welcomeMessage = welMsg.trim();
        }
        return welcomeMessage;
    }

    /**
     * Method to get the URL links
     *
     * @param id - Variable will contain the Advertisement Id.it may be null.
     *
     * @return id - Variable will contain the URL for the requested Id.it may return null.
     */
    public static String geturl(String id) {
        if (null != id) {
            if (null == url) {
                url = getJadLessDetails("Compress-Url");
                if (null == url) {
                    try {
                        url = ObjectBuilderFactory.GetProgram().getAppProperty("Compress-Url");
                    } catch (Exception e) {
                    }
                    if (null != url) {
                        url = url.trim();
                    } else {
                        url = "";
                    }
                }
            }
            try {
                if (null != url && url.length() > 0) {
                    Integer.parseInt(id.substring(1));
                    id = url + id.substring(1);
                }
            } catch (Exception e) {
            }
        }
        return id;
    }

    public static String getJADValue(String jadAttributeName) {
        String jadAttributeValue = getJadLessDetails(jadAttributeName);
        if (null == jadAttributeValue) {
            try {
                jadAttributeName = ObjectBuilderFactory.GetProgram().getAppProperty(jadAttributeName);
            } catch (Exception e) {
            }
            if (null != jadAttributeName && jadAttributeName.trim().length() > 0) {
                jadAttributeValue = jadAttributeName.trim();
            }
        } else {
            if (jadAttributeValue.length() == 0) {
                jadAttributeValue = null;
            }
        }
        return jadAttributeValue;
    }

    private static String getJadLessDetails(String value) {
        if (null == jadLessDetails && isNotSet) {
            isNotSet = false;
            String jaddetails = DownloadHandler.getInstance().getConfigDetails(RecordManager.jadLessconfig);
            if (null != jaddetails) {
                jadLessDetails = Utilities.split(jaddetails, "^");
            }
        }
        if (null != jadLessDetails) {
            int count = jadLessDetails.length;
            for (int i = 0; i < count; i += 2) {
                if (value.compareTo(jadLessDetails[i]) == 0) {
                    return jadLessDetails[i + 1];
                }
            }
            return "";
        }
        return null;
    }
}
