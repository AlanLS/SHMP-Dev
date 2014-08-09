
import java.io.ByteArrayOutputStream;
import java.util.Date;

public class Kernel implements IKernel {

    // Variable will maintain the current loaded Profile Name
    private String loadProfileName = null;
    // Variable to hold the current Backend State
    private byte bkState = 0;
    // Varable to hold the current UI State
    private byte uiStState = -1;
    // Variable will be set if the application is Authorized
    private boolean isAuthenticated = false;
    //
    private AppHandler appHandler = null;
    // Object to Read messages
    private SMSReader smsReader = null;
    // Object to hold the Application settings
    //private Settings settings = null;
    // Object to Access the Application Settings
    private Security security = null;
    //Object to Access the Advertisments
    private Advertisement adHandler = null;
    // Object to Access the Profiles
    private ProfileHandler smsHandler = null;
    // Interface Object to Access the ProfilePresenter Screen
    private IProfilePresenter iProfilePresenter = null;
    // Interface Object to Access the EntryPresenter Screen
    private IGetEntryPresenter iGetEntryPresenter = null;
    // Interface Object to Access the InboxPresenter Screen
    private IInboxPresenter iInboxPresenter = null;
    // Interface Object to Access the ViewPresenter Screen
    private IViewSmsPresenter iViewPresenter = null;
    // Interface Object to Access the DisplayPresenter Screen
    private IDisplayPresenter iDisplayPresenter = null;
    // Interface Object to Access the MenuPresenter Screen
    private IMenuPresenter iMenuPresenter = null;
    // Object to hold the Information about the Profile.
    private SMSProfileHeader[] headers = null;
    // Object to Send and Receive the SMSRequests
    private SMSSendReceive smsSR = null;
    //Interface Object to Access the DownloadPresenter Screen
    private IDownloadCanvas iDownloadPresenter = null;
    //Interface Object to Access the DownloadHandler screen
//    private DownloadHandler downloadHandler = null;
    private boolean isStartup = false;
    private IFlashPresenter iFlashPresenter = null;
    private IChatPresenter iChatPresenter = null;
    //CR 14727
    private IUserProfilePresenter iUserProfilePresenter = null;
    private String[] nString = null;
    //private byte nIsGoTo = 0;
    private String gotoWidget = null;
    private boolean isNewLoaded = false;
    //private boolean isExit = true;
    private boolean isLast = false;
    private byte exitPressed = 0;
    //private String nText = null;
    //private boolean isResend = false;
    private SMSProfileHeader moreAppHeader = null;

    private int count = 0;

    private LWForm lWForm = null;

    /**
     *  Method to set the Initial Operations.
     *
     * @throws LaunchException - Show the Error Message
     */
    public Kernel() {
    }

    public void startApplication() {
        try {
            //Settings.setSetting();
            //settings = getSettings();
            smsReader = new SMSReader();
            smsSR = new SMSSendReceive();
            
            if (RecordManager.isAggrementSigned(getVersionNumber(true))) {
                setProfileHeaders();
                setSendReceive();
            }
        } catch (Exception LaunchException) {
            loadErrorScreen(LaunchException, Constants.errorMessage[9]);
        }
    }

    public void startSocketReaderThread(){
        smsSR.startSocketReader();
    }

    /**
     * Method to start the Download Process
     * @param processType Currently Http calling process
     *          <li> 1. Download Process </li>
     *          <li> 2. Upload Process   </li>
     * @param url apps download server url
     * @param wId download app id
     */
    public void startHttpProcess(int requestType, String url, String wId, String headerDisplayName) {
        byte pState = -1;
        if (bkState != KernelConstants.BACKEND_DOWNLOADHANDLER) {
            pState = bkState;
        }
        if (requestType == 1) { //Initial download Process
            DownloadHandler.getInstance().startInitilAppdownload(pState);
            bkState = KernelConstants.BACKEND_DOWNLOADHANDLER;
        } else if (requestType == 2) { //Dapp Download Process
            PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
            DownloadHandler.getInstance().startDappPushDownload(pState);
            bkState = KernelConstants.BACKEND_DOWNLOADHANDLER;
        } else if (requestType == 3) { //single Widget Download Process
            DownloadHandler.getInstance().startSingleAppdownload(pState, url, wId, headerDisplayName);
            bkState = KernelConstants.BACKEND_DOWNLOADHANDLER;
        } else if (requestType == 4) { //Multi App download
            DownloadHandler.getInstance().startMultiappDownload(pState);
            bkState = KernelConstants.BACKEND_DOWNLOADHANDLER;
        } else if (requestType == 5) { //file upload Process
            DownloadHandler.getInstance().upload(pState); //Upload Process Type
            bkState = KernelConstants.BACKEND_DOWNLOADHANDLER;
        } else if (requestType == 6) { //push Dapp download url, url should not empty
            DownloadHandler.getInstance().pushDappData(url);
        }
    }

    public void loadCurrentScreen(byte pState, boolean status) {
        if (pState == KernelConstants.BACKEND_PROFILEHANDLER) {
            bkState = pState;
            smsHandler.loadNextState(status);
        } else if (pState == KernelConstants.BACKEND_SMSREADER) {
            bkState = pState;
            smsReader.loadCurrentScreen();
        } else if (pState == KernelConstants.BACKEND_ADDHANDLER) {
            bkState = pState;
            adHandler.loadCurrentState();
        } else {
            if (null != appHandler) {
                bkState = pState;
                appHandler.loadCurrentState(status);
            } else {
                initializeHandlers(pState);
                appHandler.initialize(0, false);
            }
        }
    }

    public boolean isDownloadAppPending() {
        return DownloadHandler.getInstance().isPendingAppToDownload();
    }

    /**
     * Method to check weather the Appliation is going to show or not
     * @return boolean -
     */
//    private boolean IsSecurityEnable() {
//        if (!Settings.isIsAppEnable()) {
//            initializeHandlers(KernelConstants.BACKEND_SECURITY);
//            security.loadApplicationMessage((byte) -2);
//            return true;
//        } else if (Settings.getIsPinEnabled() && null != Settings.getPinNumber()) {
//            bkState = KernelConstants.BACKEND_SECURITY;
//            initializeHandlers(KernelConstants.BACKEND_SECURITY);
//            security.loadPinNumber();
//            return true;
//        }
//        return false;
//    }
    private boolean isDynamicAdStore(String pName) {
        int len;
        boolean isAdStore = true;
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if (pName.compareTo(headers[i].getName()) == 0) {
                        isAdStore = headers[i].isIsDynamicAd();
                        break;
                    }
                }
            }
        }
        return isAdStore;
    }

    private boolean isInboxReplyEnable(String pName) {
        int len;
        boolean isInboxReply = true;
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if (pName.compareTo(headers[i].getName()) == 0) {
                        isInboxReply = headers[i].isIsInboxReply();
                        break;
                    }
                }
            }
        }
        return isInboxReply;
    }

    /**
     * Method to Load the Profiles with the Welcome Message
     * @throws Launchexception - Close the Application
     */
    private void LaunchProfiles() {
        try {
            initializeHandlers(KernelConstants.BACKEND_APPHANDLER); //AppHandler
            appHandler.loadInitialMenu(false);
        } catch (Exception Launchexception) {
            Logger.loggerError("Launch profiles" + Launchexception.toString());
            unLoad();
        }
    }

    /**
     * Method to Initialize the Object for the requested backend State
     * @param istate - variable will have the state which is going to access(Not Null)
     */
    private boolean initializeHandlers(byte istate) {
        if (KernelConstants.BACKEND_APPHANDLER == istate) {
            bkState = istate;
            if (null == appHandler) {
                appHandler = new AppHandler();
            } else {
                appHandler.deInitialize();
            }
        } else if (KernelConstants.BACKEND_PROFILEHANDLER == istate) {
            bkState = istate;
            if (null == smsHandler) {
                smsHandler = new ProfileHandler();
            } else {
                if (!smsHandler.isExitNodePresent(loadProfileName)) {
                    smsHandler.DeInitialize();
                } else {
                    return false;
                }
            }
        } else if (KernelConstants.BACKEND_ADDHANDLER == istate) {
            bkState = istate;
            if (null == adHandler) {
                adHandler = new Advertisement();
            } else {
                adHandler.DeInitialize();
            }
        } else if (KernelConstants.BACKEND_SECURITY == istate) {
            bkState = istate;
            if (null == security) {
                security = new Security();
            }
        }
        return true;
    }

    /**
     * Method to null all the Backend objects.
     *
     * @throws backendUnloadException
     */
    private void BackendUnLoad() {
        try {
            ObjectBuilderFactory.getControlChanel().deinitialize();
            deInitializeHandlers(KernelConstants.BACKEND_APPHANDLER);
            deInitializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER);
            deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER);
            deInitializeHandlers(KernelConstants.BACKEND_DOWNLOADHANDLER);
            deInitializeHandlers(KernelConstants.BACKEND_SMSREADER);
            bkState = 10;
            //headers = null;
        } catch (Exception backendUnloadException) {
            Logger.loggerError("BackEnd Unload Error " + backendUnloadException.toString());
        }
    }

    /**
     * Method to null all the UI Objects.
     *
     * @throws uiUnloadException -
     */
    private void UnLoadPresenter() {
        try {
            if (null != iFlashPresenter) {
                synchronized(iFlashPresenter){
                    iFlashPresenter.unLoad();
                    iFlashPresenter = null;
                }
            } else if (null != iProfilePresenter) {
                synchronized(iProfilePresenter){
                    iProfilePresenter.unLoad();
                    iProfilePresenter = null;
                }
            } else if (null != iMenuPresenter) {
                synchronized(iMenuPresenter){
                    iMenuPresenter.unLoad();
                    iMenuPresenter = null;
                }
            } else if (null != iGetEntryPresenter) {
                synchronized(iGetEntryPresenter){
                    iGetEntryPresenter.unLoad();
                    iGetEntryPresenter = null;
                }
            } else if (null != iDisplayPresenter) {
                synchronized(iDisplayPresenter){
                    iDisplayPresenter.unLoad();
                    iDisplayPresenter = null;
                }
            } else if (null != iInboxPresenter) {
                synchronized(iInboxPresenter){
                    iInboxPresenter.unLoad();
                    iInboxPresenter = null;
                }
            } else if (null != iViewPresenter) {
                synchronized(iViewPresenter){
                    iViewPresenter.unLoad();
                    iViewPresenter = null;
                }
            } else if (null != iDownloadPresenter) {
                synchronized(iDownloadPresenter){
                    iDownloadPresenter.unLoad();
                    iDownloadPresenter = null;
                }
            } else if (null != iChatPresenter) {
                synchronized(iChatPresenter){
                    iChatPresenter.unLoad();
                    iChatPresenter = null;
                }
            } else if(null != iUserProfilePresenter){
                synchronized(iUserProfilePresenter){
                    iUserProfilePresenter.unLoad();
                    iUserProfilePresenter = null;
                }
            } else if(null != lWForm){
                synchronized(lWForm){
                    lWForm.deregisterAnimated(null);
                    lWForm = null;
                    ObjectBuilderFactory.getPCanvas().setAsCurrentScreen();
                }
            }
        } catch (Exception uiUnloadException) {
            Logger.loggerError("UnLoadPresenter error " + uiUnloadException.toString());
        }
        Runtime.getRuntime().gc();
    }

    /**
     * @return
     */
    public String[] getUnReadMsgCount() {
        //return smsReader.getUnReadMsgCount(getWidgetName());
        return smsReader.getUnReadMsgCount(null);
    }

    /**
     * Method to Start the Message Listener in the Specified Port
     */
    public void setSendReceive() {
        if (null == smsSR) {
            smsSR = new SMSSendReceive();
        }
        smsSR.setMessageListener();
        ObjectBuilderFactory.getControlChanel().reorderQueue();
    }

    /*
     */
    public void UnRegisterConnection() {
        if (null == smsSR) {
            smsSR = new SMSSendReceive();
        }
        smsSR.deRegisterPushRegistry();
    }

    /**
     * Method to return the Application current version
     * @return VERSION - The variable return only Version Number.(Not Null)
     */
    public String getVersionNumber(boolean check) {
        if (check) {
          return "1.00.15";
            // return "4.08.06";
            //return "3.08.02.04";
        } else {
            //return "3.8.2.4";
           return "1.0.15";
            // return "4.8.6";
        }
    }

    /**
     *  Method to Load the Application
     */
    public void launchApplication() {
        //if (!IsSecurityEnable()) {
        loadAppication();
        //}
    }

    /**
     * Method to hold the basic Application settings
     * @return settings  - This Object will keep the Basic Application settings
     * @throws exception - Close the Application
     */
    public void clearSetting() {
        exitPressed = 2;
    }

    /**
     * Method to load the Profiles or View Message
     * @throws LoadAppexception - Show the Error Message
     */
    public void loadAppication() {
        try {
            security = null;
            LaunchProfiles();
            if (null != loadProfileName) {
                isLast = true;
                LoadViewMessage(loadProfileName);
                loadProfileName = null;
            }
            isAuthenticated = true;
        } catch (Exception LoadAppexception) {
            loadErrorScreen(LoadAppexception, Constants.errorMessage[0]);
        }
    }

    /**
     * Method to transfer the Exception with user Error message to the related Backends
     * @param exp       - Exceptions
     * @param message   - User error message
     */
    public void loadErrorScreen(Exception exp, String message) {
        Logger.loggerError("Error->" + message + exp.toString());
        if (exitPressed >0) {
            return;
        }
        if (KernelConstants.BACKEND_APPHANDLER == bkState) {
            appHandler.loadErrorMessageScreen(exp, message);
        } else if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
            smsHandler.LoadErrorMessageScreen(exp, message);
        } else if (KernelConstants.BACKEND_SMSREADER == bkState) {
            smsReader.LoadErrorMessageScreen(exp, message);
        } //        else if (KernelConstants.BACKEND_SECURITY == bkState) {
        //            security.loadErrorMessageScreen(exp, message);
        //        }
        else if (KernelConstants.BACKEND_ADDHANDLER == bkState) {
            adHandler.LoadErrorMessageScreen(exp, message);
        } else {
            unLoad();
        }
    }

    /**
     * Method to hold the Profiles Basic informations
     */
    public void setNewProifleHeaders() {
        if (isNewLoaded) {
            isNewLoaded = false;
            smsHandler = new ProfileHandler();

            //CR 4924
            if(ChannelData.getAppCatalogInclude())
                moreAppHeader = smsHandler.getFeatureApp(RecordManager.getFeatureAppName(Settings.getAppCatalogName()));

            SMSProfileHeader[] pHeaders = smsHandler.getProfileList(true);
            if (null != pHeaders) {
                if (null != headers) {
                    String[] fList = RecordManager.getOrdinaryAppsList();
                    int count = fList.length;
                    int len = pHeaders.length;
                    int pCount = headers.length;
                    boolean isNotSet = true;
                    SMSProfileHeader[] tPheaders = new SMSProfileHeader[count];
                    int plen = 0;
                    for (int i = 0, j = 0, k = 0; i < count; i++) {
                        isNotSet = true;
                        if (len > j && fList[i].compareTo(pHeaders[j].getProfileLocation()) == 0) {
                            isNotSet = false;
                            tPheaders[plen++] = pHeaders[j];
                            loadProfileName = pHeaders[j].getName();
                            registerNewNumers(pHeaders[j].getSC(), pHeaders[j].getName(), pHeaders[j].getMCFormat(), pHeaders[j].getAbbreviation());
                            j++;
                        }
                        if (pCount > k && (fList[i].compareTo(headers[k].getProfileLocation()) == 0
                                || headers[k].getProfileLocation().compareTo(fList[i].substring(0, fList[i].length() - 2) + "-j") == 0)) {
                            if (isNotSet) {
                                tPheaders[plen++] = headers[k];
                            }
                            k++;
                        }
                    }
                    if (plen > 0) {
                        if (count > plen) {
                            headers = new SMSProfileHeader[plen];
                            System.arraycopy(tPheaders, 0, headers, 0, plen);
                        } else {
                            headers = tPheaders;
                        }
                    }
                    tPheaders = null;
                    fList = null;
                } else {
                    headers = pHeaders;
                    registerProfilenumbers();
                }
                pHeaders = null;
            }
            smsHandler = null;
        }
    }

    public void setProfileHeaders() {
        try {
            smsHandler = new ProfileHandler();
            headers = smsHandler.getProfileList(false);
            //CR 4924
            if(ChannelData.getAppCatalogInclude())
                moreAppHeader = smsHandler.getFeatureApp(RecordManager.getFeatureAppName(Settings.getAppCatalogName()));

            smsHandler = null;
            isNewLoaded = false;
            registerProfilenumbers();
        } catch (Exception e) {
            Logger.loggerError("Kernel Set profile Header" + e.toString());
        }
    }

    public void isNewLoaded() {
        isNewLoaded = true;
    }

    /**
     * Method will return the Profile basic informations ( Not Null)
     * @return headers - Object will keep the Profile basic information
     */
    public SMSProfileHeader[] getSMSProfilesLoaded() {
        return headers;
    }

    //CR 4924
    public SMSProfileHeader getFeatureApp(){
        return moreAppHeader;
    }

    /**
     * Method will return the requested profile Informations
     * @param profileName - Variable will hold the Profile Name
     * @return headers    - varibale will return the profile informations.it may return Null.
     */
    public SMSProfileHeader getProfileHeader(String pName) {
        int len;
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if (pName.compareTo(headers[i].getName()) == 0) {
                        loadProfileName = pName;
                        return headers[i];
                    }
                }
            }
        }
        return null;
    }

    public void setLastSelectedWidgetName(String pName) {
        loadProfileName = pName;
    }

    /**
     * Method will return the profile location for the requested profile name
     * @param pName - varibale will have the requested Profile Name
     * @return pLoc - variable will have the Profile Location.it may be Null.
     */
    public String getProfileLocation(String pName) {
        int len;
        String pLoc = null;
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if ((pName.compareTo(headers[i].getName())) == 0) {
                        pLoc = headers[i].getProfileLocation();
                        break;
                    }
                }
            }
        }
        return pLoc;
    }

    public String getAppAbbervation(String pName) {
        int len;
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if ((pName.compareTo(headers[i].getName())) == 0) {
                        pName = headers[i].getAbbreviation();
                        break;
                    }
                }
            }
        }
        return pName;
    }

    private String getAppName(String abbervation) {
        int len;
        if (null != abbervation) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if (headers[i].getAbbreviation().indexOf(abbervation)>-1) {
                        abbervation = headers[i].getName();
                        break;
                    }
                }
            }
        }
        return abbervation;
    }

//    public String getLaunchIconLocation(String pName) {
//        int len;
//        String pLoc = null;
//        if (null != pName) {
//            if (null != headers && (len = headers.length) > 0) {
//                for (int i = 0; i < len; i++) {
//                    if ((pName.compareTo(headers[i].getName())) == 0) {
//                        pLoc = headers[i].getLaunchIcon();
//                        break;
//                    }
//                }
//            }
//        }
//        return pLoc;
//    }
    /**
     *
     * @param pName
     * @return
     */
    public String getProfileNameID(String pName) {
        int len;
        String pId = "000";
        if (null != pName) {
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    if (pName.compareTo(headers[i].getName()) == 0) {
                        pId = headers[i].getPId();
                        break;
                    }
                }
            }
        }
        return pId;
    }

    /**
     * Method to delete the requested profile.
     * @param profileName - Variable wil have the profile Name
     */
    public void removeProfileHeader(String profileName) {
        if (null != profileName) {
            int len;
            if (null != headers && (len = headers.length) > 0) {
                for (int i = 0; i < len; i++) {
                    String name = headers[i].getName();
                    if (0 == name.compareTo(profileName)) {
                        smsSR.removeWidgetName(name);
                        //CR 12319
                        GlobalMemorizeVariable.clearChatUnReadCount(headers[i].getAbbreviation());
                        SMSProfileHeader[] finHdrs = new SMSProfileHeader[len - 1];
                        System.arraycopy(headers, 0, finHdrs, 0, i);
                        System.arraycopy(headers, i + 1, finHdrs, i, (len - (i + 1)));
                        headers = finHdrs;

                        break;
                    }
                }
            }
        }
    }

    /**
     * Method to delete all the profiles
     */
    public void removeAllProfiles() {
        if (null != headers) {
            SMSProfileHeader[] tHeader = headers;
            headers = null;
            //As per the CR
            ObjectBuilderFactory.getControlChanel().addProfileDelete("ALL");
            int len = tHeader.length;
            for (int i = 0; i < len; i++) {
                //Delete Widget record Store (Entry shortcut,Query Shortcut,Memorize value,Widget property,Record values)
                RecordManager.deleteRecords(tHeader[i].getName());
                //CR 12319
                GlobalMemorizeVariable.clearChatUnReadCount(tHeader[i].getAbbreviation());
                //ObjectBuilderFactory.getControlChanel().addProfileDelete(headers[i].getPId());
            }
            RecordManager.deleteAllProfiles();
            headers = null;
        }
    }

    //CR 14333
    //CR 14441
    public void updateChatStatus(int status, String chatSequences, String chatId){
        int index = -1;
        if(null != chatSequences){
            if(null != chatId){
                //CR 14441
                if(null != iChatPresenter){
                    iChatPresenter.changeChatStatus(chatSequences, chatId, status, 0);
                } else if(null != iUserProfilePresenter){ //CR 14727
                    iUserProfilePresenter.changeChatStatus(chatSequences, chatId, status, 0);
                }
            } else if((index = chatSequences.indexOf("("))>-1){
                chatId = chatSequences.substring(0,index);
                chatSequences = chatSequences.substring(index+1,chatSequences.length()-1);
                if(null != iChatPresenter){
                    iChatPresenter.changeChatStatus(chatSequences, chatId, status, 1);
                } else {
                    ChatHistoryHandler chatHistoryHandler = new ChatHistoryHandler();
                    chatHistoryHandler.updateStatus(chatSequences, chatId, status);
                    chatHistoryHandler.deinitialize();
                    chatHistoryHandler = null;
                }
            }
        }
    }

    public int reSyncChatCount(String apperVation,String appName){
        int unreadCount = ChatHistoryHandler.getAppLevelUnreadChatCount(appName);
//        if(unreadCount>-1){
            //Bug 13203
        GlobalMemorizeVariable.updateChatUnreadCount(apperVation, unreadCount,true);
        return unreadCount;
//        }
    }

    public void reLaunchProfiles() {
        try {
            deInitializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER); // SMSHandler
            deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); // AdHander
            initializeHandlers(KernelConstants.BACKEND_APPHANDLER); // AppHamdler
            if (isStartup) {
                appHandler.loadInitialMenu(true);
                isStartup = false;
            } else {
                appHandler.initialize(0, true);
            }
        } catch (Exception reLaunchexception) {
            Logger.loggerError("ReLaunche profiles" + reLaunchexception.toString());
            unLoad();
        }
    }

    /**
     * Method to Load the Profiles without Welcome Message
     * @throws reLaunchexception - Close the Application
     */
    public void reLaunchApplication() {
        try {
            if (null == gotoWidget) {
                boolean isExitRun = false;
                if (null != smsHandler) {
                    isExitRun = smsHandler.isExitNodePresent(null);
                }
                if (!isExitRun) {
                    deInitializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER); // SMSHandler
                    deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); // AdHander
                    initializeHandlers(KernelConstants.BACKEND_APPHANDLER); // AppHamdler
                    appHandler.initialize(0, true);
                } else {
                    smsHandler.handleExitProfile();
                }
            } else {
                String goWidget = gotoWidget;
                gotoWidget = null;
                deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); // AdHander
                if (null == smsHandler) {
                    initializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER); // SMSHandler
                } else {
                    smsHandler.DeInitialize();
                }
                smsHandler.loadProfile(goWidget, false, true); //bug 12767
            }
        } catch (Exception reLaunchexception) {
            Logger.loggerError("reLaunche application" + reLaunchexception.toString());
            unLoad();
        }
    }

    /**
     * Method to Load the requested profile or launch all the profiles
     * @param pName - Variable will have the ProfileName.It May be Null.
     * @param pLoc  - Variable will have the Profile Location.
     * @param isFeature - variable to
     * @return loadexception - Show the Error Message
     */
    public void loadProfile(String pName, String pLoc, boolean isFeature, boolean isStartup) {
        try {
            if (null != pLoc) {
                this.isStartup = isStartup;
                if (null != pName) {
                    loadProfileName = pName;
                }
                deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER);
                deInitializeHandlers(KernelConstants.BACKEND_APPHANDLER);
                if (initializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER)) {
                    smsHandler.loadProfile(pLoc, isFeature,true); //bug 12767
                } else {
                    gotoWidget = pLoc;
                    smsHandler.handleExitProfile();
                }
            } else {
                reLaunchApplication();
            }
        } catch (Exception loadexception) {
            loadErrorScreen(loadexception, Constants.errorMessage[1]);
        }
    }

    /**
     * Method to run one full Sequence of profile opertion
     * @param pname - Variable will have the requested Profile Name (Not Null).
     * @param seqName - Variable will have the profile Sequence Name(Not Null).
     * @throws sequenceException - Show the Error Message
     */
//    public void loadSequenceProfile(String pname, String seqName) {
//        try {
//            loadProfileName = null;
//            deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); //AdHandler
//            deInitializeHandlers(KernelConstants.BACKEND_APPHANDLER); //AppHandler
//            initializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER); //ProfileHandler
//            smsHandler.executeSequence(pname, seqName);
//        } catch (Exception sequenceException) {
//            loadErrorScreen(sequenceException, Constants.errorMessage[2]);
//        }
//    }
    /**
     * Method to Load the Messages
     */
    public void loadInbox() {
        //#if VERBOSELOGGING
        //|JG|        Logger.debugOnError("Entering Inbox");
        //#endif
        byte preState = bkState;
        if (bkState == KernelConstants.BACKEND_SMSREADER) {
            preState = -1;
        }
        bkState = KernelConstants.BACKEND_SMSREADER;
        smsReader.setPreviousScreenState(preState);
        smsReader.loadInbox();
    }

    /**
     * Method to handle the Reply or Forward operation
     * @param messageId - Variable will have the Message Id (Not Null)
     * @param isreply - Variable will differenciate the operation weather Reply or Forward
     */
    public void loadReplyOrForwardOptionSelect(String messageId, boolean isReply, String[] rMessage) {
        byte pState = -1;
        if (bkState != KernelConstants.BACKEND_SMSREADER) {
            pState = bkState;
        }
        bkState = KernelConstants.BACKEND_SMSREADER;
        smsReader.setPreviousScreenState(pState);
        smsReader.loadReplyOrForwardOptionSelect(messageId, isReply, rMessage);
    }

    /**
     * Method to show the message View
     * @param messageId - Variable will have the message Id (Not Null).
     */
    private void LoadViewMessage(String messageId) {
        byte pState = -1;
        if (bkState != KernelConstants.BACKEND_SMSREADER) {
            pState = bkState;
        }
        bkState = KernelConstants.BACKEND_SMSREADER;
        smsReader.setPreviousScreenState(pState);
        smsReader.loadViewMessage(messageId);
    }

    /**
     * Method to retrieve the message for the requested id
     * @param messageId - Variable will have the message Id(Not Null)
     * @return - method will reurn the message for the requested Id.It may be Null.
     */
    public String[] getViewMessage(String messageId) {
        return smsReader.GetViewMessage(messageId);
    }

    /**
     * Method to delete the message
     * @param messageId - Variable will have the message Id (Not Null)
     * @return isDeleted -  Variable will contain true or false for the conformation weather message deleted or not
     */
    public boolean deleteViewMessage(String messagId) {
        boolean isDelete = false;
        isDelete = smsReader.DeleteViewMessage(messagId);
        return isDelete;
    }

    //CR 13900
    //CR 14112
    public void setImage(ByteArrayOutputStream byteArrayOutputStream, boolean isDownload, boolean isGroupAndShout){
        //Bug 14832
        if(isGroupAndShout){
            if(uiStState == KernelConstants.FRENDENT_USER_PROFILE && null != iUserProfilePresenter){
                iUserProfilePresenter.updateUserImage(Contacts.getGridIndex(iUserProfilePresenter.getUserNumber(),
                        smsHandler.getRecordIndexToUpdateProfileScreenImage(iUserProfilePresenter.getUserNumber())));
            }
        } else if(uiStState == KernelConstants.FRENDEND_VIEW && null != iViewPresenter){
            synchronized(iViewPresenter){
                iViewPresenter.setImage(byteArrayOutputStream);
            }
        } else if(uiStState == KernelConstants.FRENDENT_CHAT && null != iChatPresenter){
            synchronized(iChatPresenter){
                iChatPresenter.setImage(byteArrayOutputStream);
            }
        } else if(uiStState == KernelConstants.FRENDEND_MENU && null != iMenuPresenter){
            synchronized(iMenuPresenter){
                iMenuPresenter.setImage(byteArrayOutputStream);
            }
        } else if(uiStState == KernelConstants.FRENDEND_ENTRY && null != iGetEntryPresenter){
            synchronized(iGetEntryPresenter){
                iGetEntryPresenter.setImage(byteArrayOutputStream);
            }
        } else if(uiStState == KernelConstants.FRENDEND_DISPLAY && null != iDisplayPresenter){
            synchronized(iDisplayPresenter){
                iDisplayPresenter.setImage(byteArrayOutputStream);
            }
        } else if(uiStState == KernelConstants.FRENDENT_USER_PROFILE && null != iUserProfilePresenter){ //CR 14727
            synchronized(iUserProfilePresenter){
                iUserProfilePresenter.setImage(byteArrayOutputStream);
            }
        }

        //Bug 14832
        if(null == byteArrayOutputStream && !isGroupAndShout){
            if(isDownload){
                displayMessageBox(4, "Image Download Error, Please try again",  Constants.headerText[8]);
            } else {
                displayMessageBox(4, "Image Upload Error, Please try again",  Constants.headerText[8]);
            }
        }
    }

    /**
     * Method to Load the Previous Screen
     * @param pState - Variable will hold the Previous screen Backend State(Not Null)
     */
    public void setPreviousBkState(byte pState) {
        if (pState == KernelConstants.BACKEND_ADDHANDLER) {
            if (null != adHandler) {
                adHandler.loadCurrentState();
                bkState = pState;
            } else if (null != smsHandler) {
                smsHandler.loadCurrentState();
                bkState = KernelConstants.BACKEND_PROFILEHANDLER;
            } else {
                loadProfileName = null;
                initializeHandlers(KernelConstants.BACKEND_APPHANDLER);
                appHandler.initialize(bkState, true);
            }
        } else if (pState == KernelConstants.BACKEND_PROFILEHANDLER) {
            if (null != smsHandler) {
                smsHandler.loadCurrentState();
                bkState = pState;
            } else {
                loadProfileName = null;
                initializeHandlers(KernelConstants.BACKEND_APPHANDLER);
                appHandler.initialize(bkState, true);
            }
        } else if (pState == KernelConstants.BACKEND_SMSREADER) {
            if (null != smsReader) {
                smsReader.loadCurrentScreen();
                bkState = pState;
            } else if (null != adHandler) {
                adHandler.loadCurrentState();
                bkState = pState;
            } else if (null != smsHandler) {
                smsHandler.loadCurrentState();
                bkState = KernelConstants.BACKEND_PROFILEHANDLER;
            } else {
                loadProfileName = null;
                initializeHandlers(KernelConstants.BACKEND_APPHANDLER);
                appHandler.initialize(bkState, true);
            }
        } else if (pState == KernelConstants.BACKEND_APPHANDLER) {
            loadProfileName = null;
            initializeHandlers(pState);
            if (isLast) {
                isLast = false;
                appHandler.loadInitialMenu(false);
            } else {
                appHandler.initialize(pState, true);
            }
        }
    }

    /**
     * Method to Null the requested Backend Object
     * @param rstate - Variable will have the state which one is going to remove(Not Null)
     */
    public void deInitializeHandlers(byte rstate) {
        try {
            if (KernelConstants.BACKEND_APPHANDLER == rstate) {
                if (null != appHandler) {
                    appHandler.deInitialize();
                    appHandler = null;
                }
            } else if (KernelConstants.BACKEND_PROFILEHANDLER == rstate) {
                if (null != smsHandler) {
                    smsHandler.DeInitialize();
                    smsHandler = null;
                }
            } else if (KernelConstants.BACKEND_SMSREADER == rstate) {
                if (null != smsReader) {
                    smsReader.DeInitialize();
                    smsReader = null;
                }
            } else if (KernelConstants.BACKEND_ADDHANDLER == rstate) {
                if (null != adHandler) {
                    adHandler.DeInitialize();
                    adHandler = null;
                }
            } 
        } catch (Exception e) {
            Logger.loggerError("Handler Turn off Error " + e.toString() + "State " + rstate);
        }
    }

    /**
     * Method to
     *
     * @param itemId - Variable will have the item id.
     * @param itemName - Variable will have selected item Name
     * @throws itemException - Show the Error Message
     */
    public void handleItemSelection(int itemId, String itemName) {
        try {
            //#if VERBOSELOGGING
            //|JG|            Logger.loggerError("Bakstate=" + bkState + " UIstate=" + uiStState + " itemid=" + itemName + " itemname=" + itemName);
            //#endif
            if (exitPressed>0) {
                if(exitPressed>1)
                    ObjectBuilderFactory.getPCanvas().exitShortHand();
            } else if (KernelConstants.BACKEND_APPHANDLER == bkState) {
                appHandler.handleItemSelection(itemId, itemName);
            } else if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
                smsHandler.handleItemSelection(itemId, itemName);
            } else if (KernelConstants.BACKEND_SMSREADER == bkState) {
                smsReader.HandleItemSelection(itemName);
            } //            else if (KernelConstants.BACKEND_SECURITY == bkState) {
            //                security.handleItemSelection(itemName);
            //            }
            else if (KernelConstants.BACKEND_ADDHANDLER == bkState) {
                adHandler.handleItemSelection(itemId);
            }
        } catch (Exception itemException) {
            loadErrorScreen(itemException, Constants.errorMessage[3]);
        }
    }

    /**
     * Method to send the highlited string in the message to the server
     *
     * @param interactiveMsg - Variable will contain the highlited String in the message
     */
    public void sendInteractiveActionMsg(String msgid, String interactiveMsg) {
        //Deactivated for the bug number 3835
        String pId = "000";
        if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
            pId = smsHandler.getProfileId();
        } else {
            pId = getProfileNameID(smsReader.getSenderName(msgid));
        }
        if (isEventReportSend(pId)) //Bug 8311  and CR 8619
        {
            ObjectBuilderFactory.getControlChanel().sendInteractiveAction(pId, interactiveMsg);
        }
    }

    private boolean isEventReportSend(String pId) {
        if (null != pId && null != headers) {
            int len = headers.length;
            for (int i = 0; i < len; i++) {
                if (headers[i].getPId().compareTo(pId) == 0) {
                    return headers[i].isIsReportIEVT();
                }
            }
        }
        return false;
    }

    /**
     * Method will check weather the application is authorized or not.
     * if its not authorized, server message will display
     */
    public void handleApplicationMessage() {
        if (isAuthenticated) {
            byte pState = -1;
            if (bkState != KernelConstants.BACKEND_SECURITY) {
                pState = bkState;
            }
            initializeHandlers(KernelConstants.BACKEND_SECURITY);
            security.loadApplicationMessage(pState);
        }
    }

    /**
     * Method to transfer the control to the current backend.
     *
     * @param itemId - Variable will have the menu id.
     * @param itemname - Variable will have the selected menu Name
     * @param optSel - Variable will have the selected option Id
     *
     * @throws optionException - Show the Error Scrren
     */
    public void handleOptionSelection(int itemId, String itemName, byte optSel) {
        try {
            if (KernelConstants.BACKEND_APPHANDLER == bkState) {
                appHandler.handleOptionsSelected(itemId, itemName, optSel);
            } else if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
                smsHandler.handleOptionsSelected(itemId, itemName, optSel);
            } else if (KernelConstants.BACKEND_SMSREADER == bkState) {
                smsReader.handleOptionsSelected(itemName, optSel);
            } else if (KernelConstants.BACKEND_ADDHANDLER == bkState) {
                adHandler.handleOptionSelected(itemId, optSel);
            } else if (KernelConstants.BACKEND_SECURITY == bkState) {
                security.handleOptionSelected(optSel);
            }
        } catch (Exception optionException) {
            Logger.loggerError("Kernel Option " + optionException.toString() + "\n" + optionException.getMessage());
            loadErrorScreen(optionException, Constants.errorMessage[4]);
        }
    }

    /**
     * Method to remove the option in the right and left side
     */
    public void removeOption() {
        if (KernelConstants.FRENDEND_DISPLAY == uiStState) {
            iDisplayPresenter.removeOptions();
        }
    }

    /**
     * Method to handle the selected Advertisement
     */
    public void handleAdSelection() {
        if (null == adHandler) {
            AdData adData = null;
            if (bkState == 2) {
                adData = smsReader.getSelectedAd();
            } else {
                adData = smsHandler.getSelectedAd();
            }
            if (null != adData) {
                byte pState = -1;
                if (bkState != KernelConstants.BACKEND_ADDHANDLER) {
                    pState = bkState;
                }
                initializeHandlers(KernelConstants.BACKEND_ADDHANDLER); //AdHandler
                adHandler.LoadInitialMenu(adData, pState);
            }
            adData = null;
        }
    }

    /**
     * Method to start the DAPP download, if the client doesn't have the phoneNumber it should not start the download
     *  process, and it will send the PROV message to server.
     */
    public void startDAPPDownload() {
        if (null != Settings.getPhoneNumber()) {
            startHttpProcess(2, null, null, null); //DAPP download to be start
        } else {
            ObjectBuilderFactory.getControlChanel().sendProvenanceAction(true); //CR 8351
        }
    }

    /**
     * Method to retrieve the Advertisement based on the requested category
     *
     *
     * @param profileCat - Variable will contain the Advertisement category.it may be null.
     * @return adData - Variable will have the Advertisement data for the requested category.it may be Null.
     */
    public AdData getAdvertisement(String profileCat) {
        AdData adData = null;
        if (null != profileCat && "".compareTo(profileCat) != 0) {
            initializeHandlers(KernelConstants.BACKEND_ADDHANDLER); //AdHandler
            bkState = KernelConstants.BACKEND_PROFILEHANDLER;
            adData = adHandler.GetAdvertisement(profileCat);
            deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); // AdHandler
        }
        return adData;
    }

    /**
     * Method to rename the selected item.
     *
     * @param itemId - Variable will contain the selected item Id
     * @param itemName - Varibale will contain the selected item Name.
     * @param new Name - Variable will contain the rename text.
     *
     * @throws handleRenameException - Show the Error Message
     */
    public void handleRename(int itemId, String itemName, String newName) {
        try {
            smsHandler.handleRename(itemId, itemName, newName);
        } catch (Exception handleRenameException) {
            loadErrorScreen(handleRenameException, Constants.errorMessage[5]);
        }
    }

    /**
     * Method to transfer the control to the current Backend
     *
     * @param status  -
     *
     * @throws msgException - Show the Error message.
     */
    public void handleMessageBox(boolean status, byte msgType) {
        try {
//            if (isResend) {
//                isResend = false;
//                smsSR.reSendMessage();
//            }

            if (exitPressed>0) {
                if(exitPressed == 1 && status){
                    sendReconnectOrPollMessage("!*EXIT*!",true);
                } else unLoad();
            } else {
                if(msgType == 23 || msgType == 24){
                    if(status)
                        smsSR.reSendMessage();
                } else if(msgType == 22){
                    sendReconnectOrPollMessage("!*KEEP*!",true);
                     }
                else if(msgType == 26){ //Cr 13332
                    if(status){
                    ObjectBuilderFactory.getPCanvas().platformRequest(ObjectBuilderFactory.getControlChanel().updateUrl);

                    }
                } else if (KernelConstants.BACKEND_APPHANDLER == bkState) {
                    appHandler.handleMessageBox(status);
                } else if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
                    smsHandler.handleMessageBox(status);
                } else if (KernelConstants.BACKEND_SMSREADER == bkState) {
                    smsReader.handleMessageBoxSelection(status);
                } //            else if (KernelConstants.BACKEND_SECURITY == bkState) {
                //                security.handleMessageBoxSelection(status);
                //            }
                else if (KernelConstants.BACKEND_ADDHANDLER == bkState) {
                    adHandler.handleMessageBox(status);
                } else if (KernelConstants.BACKEND_DOWNLOADHANDLER == bkState) {
                    DownloadHandler.getInstance().handleMessageBox(status);
                }
            }
        } catch (Exception msgException) {
            loadErrorScreen(msgException, Constants.errorMessage[6]);
        }
    }

    public Message sendReconnectOrPollMessage(String sendMessage,boolean isSend){
        Message message = new Message();
        message.setShortcode(ChannelData.getShortcode());
        message.setSFullName(Constants.aName);
        message.setIsSendQueueEmpty(false);
        message.setInboxFunc(true);
        message.setIsNotChatMessage(false);
        message.setAbberVation(Constants.appName);
        if(isSend){
            message.setRMsg(new String[]{ChannelData.getServerName()+" "+sendMessage});
            sendMessage(message);
        } else {
            message.setRMsg(new String[]{smsSR.ConvertASCIIValue(ChannelData.getServerName()+" "+sendMessage)});
        }
        return message;
    }

    /**
     * Method will handle the SMS weather its going to display or not.
     *
     * @param notificationArray - Array Variable will contain the SMS response.it may be null.
     * @param isGoto - Variable will contain the value true or false.if its true, the message will be display.
     *
     * @throws notificationException -
     */
    public void handleNotificationSelection(boolean isGoto) {
        try {
            if (null != nString && nString.length > 0) {
                if (isGoto) {
                    if (Constants.appendText[19].compareTo(nString[0]) == 0) {
                        LoadViewMessage(nString[1]);
                    } else if (Constants.appendText[27].compareTo(nString[0]) == 0) {
                        loadChatApp(nString);
                    }
                }
            } else {
                if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
                    smsHandler.handleNotification();
                }
            }
            nString = null;
        } catch (Exception notificationException) {
        }
    }

    private void loadChatApp(String[] property) {
        String location = getProfileLocation(property[1]);
        if (null == smsHandler || smsHandler.isNotLoadedChat(location, property[2], property[3], property[4])) {
            loadProfileName = null;
            deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER); //AdHandler
            deInitializeHandlers(KernelConstants.BACKEND_APPHANDLER); //AppHandler
            initializeHandlers(KernelConstants.BACKEND_PROFILEHANDLER); //ProfileHandler
            smsHandler.loadChat(location, property[2], property[3],property[4],false);
        }
    }

    //CR 14675, 14672, 14698, 14803
    public void updateMessagePlusContacts(String response, byte replaceType){
        Contacts.updateMessagePlusAppContacts(response, replaceType);
        //CR 14741, 14742
        if(replaceType == 0 || replaceType == 2 || replaceType == 3){
            DownloadHandler.getInstance().contactThumubUpdate();
        }
        if(null != smsHandler){
            smsHandler.refreshContactScreen();
        } else if(replaceType == 0){ //Need to show the small popup contact Refreshed

        }
    }

    //CR 14672, 14675
    public void refreshContacts(String[] contacts, int[] contactId){
        if(null != iGetEntryPresenter){
            synchronized(iGetEntryPresenter){
                iGetEntryPresenter.refreshList(contacts, contactId);
            }
        }
    }

    /**
     * Method to transfer the control to get the options
     *
     *
     * @param itemId - Variable will contain the selected item Id.
     * @param itemName - Variable will contain the item Name.it may be null.
     * @return options - Variable will contain the options based the requested itemId and itemName.
     *
     * @throws optionsException -
     */
    public byte[] getOptions(int itemId, String itemName) {
        byte[] options = new byte[0];
        try {
            if (exitPressed>0) {
                unLoad();
            } else if (KernelConstants.BACKEND_APPHANDLER == bkState) {
//                smsSR.receiveMessage("VMP>\n(Hemalatha Paunrajan,918754415795,+,"+
//                        GlobalMemorizeVariable.getValue(ChannelData.globalChatSequence)+","+Utilities.getCurrentDateYYYYMMDDFormat()+")\n" +
//                    "<mov=52;View Video;> (15:37)", "Message+", Settings.getPhoneNumber());
//                smsSR.receiveMessage("FBC>\n(K Madhan Kumar,100002558333993)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Facebook Chat", Settings.getPhoneNumber());
//                smsSR.receiveMessage("FBC>\nIthayakumar Durairaj,577511355)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Facebook Chat", Settings.getPhoneNumber());
                options = appHandler.getOptionsMenu(itemName);
            } else if (KernelConstants.BACKEND_PROFILEHANDLER == bkState) {
//                smsSR.receiveMessage("VMP>\n(Ithayakumar Durairaj,577511355,+,"+
//                        GlobalMemorizeVariable.getValue(ChannelData.globalChatSequence)+","+Utilities.getCurrentDateYYYYMMDDFormat()+")\niam Sasikumar " +
//                            "Calling from chat, Wher is the document applies to the network!*PINT005*!", "Message+", Settings.getPhoneNumber());
//
//                smsSR.receiveMessage("!*USST918754415793:OnlineFriends*!", "Message+", Settings.getPhoneNumber());
//                if(count>2){
//                    count =0;
//                }
//                smsSR.receiveMessage("VMP>\n("+count+"Hemalatha Paunrajan,68342257"+count+",+)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Message+", Settings.getPhoneNumber());
//                count++;
//                smsSR.receiveMessage("VMP>\n(Hemalatha Paunrajan,683422571,+)\niam Sasikumar!-!VMP>\n(K Madhan Kumar,100002558333993,+)\niam Sasikumar!-!VMP>\n(Ithayakumar Durairaj,577511355,+)\niam Sasikumar"
//                        , "Message+", Settings.getPhoneNumber());
////                smsSR.receiveMessage("FBC>\n(Hemalatha Paunrajan,683422571)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Facebook Chat", Settings.getPhoneNumber());
//                smsSR.receiveMessage("FBC>\n(K Madhan Kumar,100002558333993)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Facebook Chat", Settings.getPhoneNumber());
//                smsSR.receiveMessage("FBC>\n(Ithayakumar Durairaj,577511355)\niam Sasikumar " +
//                    "Calling from chat, Wher is the document applies to the network", "Facebook Chat", Settings.getPhoneNumber());

                options = smsHandler.GetOptions(itemId, itemName);
            } else if (KernelConstants.BACKEND_SMSREADER == bkState) {
                options = smsReader.getOptions(itemName);
            }
        } catch (Exception optionsException) {
        }
        return options;
    }

    /**
     * Method to create the Object for the requested UI screen Id
     *
     * @param screenId - variable will contain the requested screen Id.(Not Null)
     *
     * @throws uiScreenException -
     */
    public void initializeScreen(byte screenId) {
        try {
            if (screenId == KernelConstants.FRENDEND_FLASH && null == iFlashPresenter) {
                iFlashPresenter = (IFlashPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(0);
            } else if (screenId == KernelConstants.FRENDEND_PROFILE && null == iProfilePresenter) {
                UnLoadPresenter();
                iProfilePresenter = (IProfilePresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(1);
            } else if (screenId == KernelConstants.FRENDEND_MENU && null == iMenuPresenter) {
                UnLoadPresenter();
                iMenuPresenter = (IMenuPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(3);
            } else if (screenId == KernelConstants.FRENDEND_ENTRY && null == iGetEntryPresenter) {
                UnLoadPresenter();
                iGetEntryPresenter = (IGetEntryPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(4);
            } else if (screenId == KernelConstants.FRENDEND_DISPLAY && null == iDisplayPresenter) {
                UnLoadPresenter();
                iDisplayPresenter = (IDisplayPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(5);
            } else if (screenId == KernelConstants.FRENDEND_INBOX && null == iInboxPresenter) {
                UnLoadPresenter();
                iInboxPresenter = (IInboxPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(2);
            } else if (screenId == KernelConstants.FRENDEND_VIEW && null == iViewPresenter) {
                UnLoadPresenter();
                iViewPresenter = (IViewSmsPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(6);
            } else if (screenId == KernelConstants.FRENDEND_DOWNLOAD && null == iDownloadPresenter) {
                UnLoadPresenter();
                iDownloadPresenter = (IDownloadCanvas) ObjectBuilderFactory.getPCanvas().getScreenObject(7);
            } else if (screenId == KernelConstants.FRENDENT_CHAT && null == iChatPresenter) {
                UnLoadPresenter();
                iChatPresenter = (IChatPresenter) ObjectBuilderFactory.getPCanvas().getScreenObject(8);
            } else if(screenId == KernelConstants.FRENDENT_USER_PROFILE && null == iUserProfilePresenter){
                UnLoadPresenter();
                iUserProfilePresenter = (IUserProfilePresenter)ObjectBuilderFactory.getPCanvas().getScreenObject(9);
            } else if((screenId == KernelConstants.LWUOT_FRENDENT_ENTRY ||
                    screenId == KernelConstants.LWUOT_FRENDENT_MENU ||
                    screenId == KernelConstants.LWUOT_FRENDENT_PROFILE) && null == lWForm){
                UnLoadPresenter();
                lWForm = new LWFormEAT();
            }
            uiStState = screenId;
        } catch (Exception uiScreenException) {
        }
    }

    /**
     * Method to dispaly the requested UI screen.
     *
     * @param obj - Object will contain the information which screen is going to display.(Not Null)
     * @param isSelect -
     *
     * @throws dispalyScreenException - show the Error Message.
     */
    public void displayScreen(Object obj, boolean isSelect) {
        try {
            if (uiStState == KernelConstants.FRENDEND_PROFILE) {
                isLast = false;
                iProfilePresenter.load((ProfileResponseDTO) obj);
                iProfilePresenter.selectLastAccessedItem(loadProfileName);
            } else if (uiStState == KernelConstants.FRENDEND_MENU) {
                iMenuPresenter.load((MenuResponseDTO) obj);
                if (isSelect) {
                    iMenuPresenter.selectLastAccessedItem(null, -3);
                }
            } else if (uiStState == KernelConstants.FRENDEND_ENTRY) {
                iGetEntryPresenter.load((GetEntryResponseDTO) obj);
                if (isSelect) {
                    iGetEntryPresenter.selectLastAccessedItem(null);
                }
            } else if (uiStState == KernelConstants.FRENDEND_DISPLAY) {
                iDisplayPresenter.load((DisplayResponseDTO) obj);
            } else if (uiStState == KernelConstants.FRENDEND_INBOX) {
                iInboxPresenter.load((InboxResponseDTO) obj);
                if (isSelect) {
                    iInboxPresenter.selectLastAccessedItem(null);
                }
            } else if (uiStState == KernelConstants.FRENDEND_VIEW) {
                iViewPresenter.load((ViewSmsResponseDTO) obj);
            } else if (uiStState == KernelConstants.FRENDEND_DOWNLOAD) {
                iDownloadPresenter.load((String[]) obj);
            } else if (uiStState == KernelConstants.FRENDENT_CHAT) {
                iChatPresenter.load((ChatResponseDTO) obj);
            } else if(uiStState == KernelConstants.FRENDENT_USER_PROFILE){ //CR 14727
                iUserProfilePresenter.load((UserProfileDTO)obj);
            } else if(uiStState == KernelConstants.LWUOT_FRENDENT_ENTRY 
                    || uiStState == KernelConstants.LWUOT_FRENDENT_MENU
                    || uiStState == KernelConstants.LWUOT_FRENDENT_PROFILE){
                lWForm.initialize((LWDTO)obj);
                lWForm.show();
            }
            obj = null;
        } catch (Exception dispalyScreenException) {
            displayMessageBox(0, dispalyScreenException.getMessage() + dispalyScreenException.toString(), null);
        }
        Runtime.getRuntime().gc();
    }

    public void pendingMsgSnd() {
        if (null != smsSR) {
            smsSR.pendingMsgSnd();
        }
    }

    public void ChangeModeOrTimer(boolean isRest,boolean isNotTimer){
        if(null != smsSR){
            if(isNotTimer)
                smsSR.changeMode(isRest);
            else smsSR.reSchedulePollTimer(false, 1); //CR 13346
        }
    }

    /**
     * Method to show the textbox to rename for selected item.
     *
     * @param itemId - Variable will contain the selected item Id.(Not Null)
     * @param itemName - Variable will contain the selected item Name.(Not Null)
     *
     * @throws renameException - Show the Error Screen.
     */
    public void renameMenuItem(int itemId, String itemName) {
        try {
            //#if KEYPAD
            //|JG|            if (uiStState == KernelConstants.FRENDEND_MENU) {
            //|JG|                iMenuPresenter.renameMenuItem(itemId, itemName);
            //|JG|            } else if (uiStState == KernelConstants.FRENDEND_ENTRY) {
            //|JG|                iGetEntryPresenter.renameMenuItem(itemId, itemName);
            //|JG|            }
            //#endif
        } catch (Exception renameException) {
            loadErrorScreen(renameException, Constants.errorMessage[7]);
        }
    }

    /**
     * Method to show the Ordered Messages
     *
     * @param msgId - Variable will have the message Id.(Not Null)
     */
    public void reorder(String msgId) {
        if (KernelConstants.FRENDEND_INBOX == uiStState) {
            iInboxPresenter.reorder(msgId);
        }
    }

    /**
     * Method to show the message box in the current UI screen
     *
     * @throws displayMessgaeException -
     */
    public void displayMessageBox(int msgType, String msg, String hText) {
        if (exitPressed>1) { //CR 13278
                unLoad();
                return;
        } else if (msgType > 3 && msgType != 16) {
            CustomCanvas.setMessageBoxText(msg, hText);
        } else CustomCanvas.setPopupMessage(msg);
        CustomCanvas.msgType = (byte)msgType;
    }

    /**
     * Method to show the notification if SMSResponse delayed.
     *
     * @param delayMsg - Variable will conatin the delay response text
     */
    public void raiseNotification(String delayMsg) {
        if (KernelConstants.BACKEND_PROFILEHANDLER == bkState && KernelConstants.FRENDEND_DISPLAY == uiStState) {
            setNotification(0, null, delayMsg);
        }
    }

    /**
     * Method to show the Notification Text
     *
     * @param isGoto - Variable is going to decide weather the notification option going to disdplay or not.
     * @param msgId - Variable will contain the message Id.
     * @param displayMsg - Variable will contain the display text.
     *
     * @throws displayNotificationException -
     */
    private void setNotification(int isGoto, String[] msgId, String displayMsg) {
        if (exitPressed>0) {
            isGoto = 0;
        }
        if (KernelConstants.FRENDEND_PROFILE == uiStState && null != iProfilePresenter) {
            synchronized (iProfilePresenter)
            {
                iProfilePresenter.renameIndexedName(getUnReadMsgCount());
            }
        } else if (isGoto == 0 && KernelConstants.FRENDEND_INBOX == uiStState && null != iInboxPresenter){
            loadInbox();
            return;
        }

        nString = msgId;
        CustomCanvas.notText = displayMsg;
        CustomCanvas.isNotificationGoto = (byte)isGoto;
    }

    /**
     *
     */
    public void displayMessageSendSprit() {
        if (!Settings.isIsGPRS() || null == Settings.getPhoneNumber()) {
            CustomCanvas.isShowMessageSendSprit = true;
        }
    }

    /**
     * CR 12318
     */
    public void displayChatNotification(String[] notificationText) {
        //CR 12318
        if (KernelConstants.FRENDEND_PROFILE == uiStState) {
            if(null != iProfilePresenter){
                synchronized(iProfilePresenter){
                    iProfilePresenter.updateChatNotification(notificationText);
                }
            }
        }
        CustomCanvas.updateChatNotification(notificationText);
        CustomCanvas.isChatNotification = true;
    }

    /**
     * Method to transfer the Last Selected item.
     *
     * @param lastSelItem - variable will contain the last selected item name.
     * @param itemId - Variable will contain the last selected item Id.
     */
    public void lastSelectedItem(String lastSelItem, int itemId) {
        if (KernelConstants.FRENDEND_MENU == uiStState) {
            iMenuPresenter.selectLastAccessedItem(lastSelItem, itemId);
        } else if (KernelConstants.FRENDEND_ENTRY == uiStState) {
            iGetEntryPresenter.selectLastAccessedItem(lastSelItem);
        } else if (KernelConstants.FRENDEND_INBOX == uiStState) {
            iInboxPresenter.selectLastAccessedItem(lastSelItem);
        }
    }

    /**get
     * Method to show the given message in the Textbox
     *
     * @param message - Variable will contain the message text to show.it may be null
     */
    public void showGivenValue(String message, boolean isMaxSet) {
        if (uiStState == KernelConstants.FRENDEND_ENTRY) {
            iGetEntryPresenter.copyTextToTextBox(message, isMaxSet);
        }
    }

    /**
     * Method will show the new Name after it renamed.
     *
     * @param itemId - Variable will contain the selected item id.(Not Null)
     * @param new Name - Variable will contain the renamed Text.(Not Null)
     *
     * @throws menuItemChangeException
     */
    public void changeMenuItemName(int itemId, String newName) {
        try {
            if (KernelConstants.FRENDEND_MENU == uiStState) {
                iMenuPresenter.changeMenuItemName(itemId, newName);
            }
        } catch (Exception menuItemChangeException) {
        }
    }

    public void changeHearder(String[] hText) {
        if (KernelConstants.FRENDEND_DOWNLOAD == uiStState) {
            iDownloadPresenter.changeHeaderText(hText);
        }
    }

    /**
     * Method to display the items after deleted the selected one.
     *
     * @param itemId - Variable will contain the deleted item Id.(Not Null)
     * @param itemName - Variable will conatin the deleted item Name.(Not Null)
     *
     * @throws removeMenuException
     */
    public void removeMenuItem(int itemId, String itemName) {
        try {
            if (KernelConstants.FRENDEND_PROFILE == uiStState) {
                iProfilePresenter.removeMenuItem(itemId, itemName);
            } else if (KernelConstants.FRENDEND_MENU == uiStState) {
                iMenuPresenter.removeMenuItem(itemId, itemName);
            } else if (KernelConstants.FRENDEND_ENTRY == uiStState) {
                iGetEntryPresenter.removeMenuItem(itemId, itemName);
            } else if (KernelConstants.FRENDEND_INBOX == uiStState) {
                iInboxPresenter.removeSelectedItem(itemName);
            }
        } catch (Exception removeMenuException) {
        }
    }

    /**
     * Method to Null all the UI Objects.
     *
     * @throws frontendUnloadException
     */
    private void FrontendUnLoad() {
        UnLoadPresenter();
        uiStState = 10;
    }

    /**
     * Method to load the Message Send Waitint Screen, When the user press the ExitShorthand option in the priofile screen.
     * While the Application is try to send user message/receiveing the user message, it will load the Message send waiting screen.
     * That time user cant able to exit the shorthand.
     */
    private void LoadMessageSendWaitinScreen(String msg, int timer) {
        initializeScreen(KernelConstants.FRENDEND_DISPLAY);
        PresenterDTO.setLOptByte((byte) -1);
        DisplayResponseDTO displayDTO = new DisplayResponseDTO();
        displayDTO.setDisplayImage((byte) 1);
        displayDTO.setSecondaryHeaderText(msg);
        displayDTO.setDisplayTime((short) timer);
        displayScreen(displayDTO, true);
        displayDTO = null;
    }

    /**
     * Method to Null UI and Backend Objects.
     *
     * @throws unLoadException
     */
    public void unLoad() {
        //#if VERBOSELOGGING
        //|JG|        Logger.loggerError("Kernel->Unload isExitPressed " + exitPressed);
        //#endif
        //isExitPressed = true;

        //boolean isSent = false;

        //CR 13278
//        if(exitPressed == 0){
//            if(smsSR.isNotSocketMode()){
//                LoadMessageSendWaitinScreen(Constants.headerText[8], 0);
//                //String msg ="Do you want to log out of your chat services?";
//                displayMessageBox(4, Constants.popupMessage[68], Constants.headerText[8]); //Bug 4897
//            } else sendReconnectOrPollMessage("!*STOP*!");
//            exitPressed = 1;
//        } else
            if (exitPressed == 0) {
            String temp1 = "0", temp2 = "0";
            if (ChannelData.getMessageTextSummery()) { //CR number 7220
                if (Settings.getsRequestCount() > 0) {
                    temp1 = (Settings.getsRequestCount() / 1024 + "").substring(0, (Settings.getsRequestCount() / 1024 + "").indexOf(".") + 3);
                }

                if (Settings.getrResponseCount() > 0) {
                    temp2 = (Settings.getrResponseCount() / 1024 + "").substring(0, (Settings.getrResponseCount() / 1024 + "").indexOf(".") + 3);
                }

                String msg = Settings.getSMsgcount() + " " + Constants.popupMessage[26] + " " + Settings.getRMsgCount() + " "
                        + Constants.popupMessage[64]
                        //CR<-0012014->
                        + "\n" + temp1 + " "
                        + Constants.popupMessage[59] + " " + temp2 + " "
                        + Constants.popupMessage[60];
                //CR<-0012014->
//                String msg = Settings.getSMsgcount() + " " + Constants.popupMessage[26] + " " + Settings.getRMsgCount() + " " +
//                        //CR 11975
//                        Constants.popupMessage[59]sendMessage
//                        + " " + Settings.getsRequestCount() + Constants.popupMessage[60] + " " + Settings.getrResponseCount() +" " +
//                                Constants.popupMessage[61];
//                        //<!-- CR 11975 -->
                LoadMessageSendWaitinScreen(Constants.headerText[23], 0);
                displayMessageBox(4, msg, Constants.headerText[7]); //Bug 4897
            } else {
                ObjectBuilderFactory.getPCanvas().exitShortHand();
            }
            exitPressed = 2;
        } else {
            ObjectBuilderFactory.getPCanvas().exitShortHand();
        }
    }

    public byte exitShorthand() {
        //if(exitPressed == 2) {
           try {
                //#if VERBOSELOGGING
                //|JG|            Logger.loggerError("Kernel->exitShorthand()->Called Exit Shorthand");
                //#endif
                if (null != smsSR) {
                    synchronized (smsSR) {
                        smsSR.deinitialize();
                        smsSR = null;
                    }
                }
                BackendUnLoad();
                FrontendUnLoad();
                Logger.clossLogger();
            } catch (Exception e) {
                Logger.loggerError("ExitShorthand " + e.toString());
            }
//        } else {
//            unLoad();
//        }
        return exitPressed;
    }

    public void setInterpopup(boolean isPopup) {
        if (isPopup) { // CR number 6414
            if (ChannelData.getMonthlyMessageSentPopupCount() > 0 && (Settings.getMonthlySentCount() % ChannelData.getMonthlyMessageSentPopupCount()) == 0) {
                displayMessageBox(4, Constants.popupMessage[50] + " " + Settings.getMonthlySentCount() + " " + Constants.popupMessage[51], Constants.headerText[8]);
            }
        } else {
            ObjectBuilderFactory.getPCanvas().setWarningpopup();
        }
    }

//    public void exitApplication(){
//        ObjectBuilderFactory.getControlChanel().deinitialize();
//        ObjectBuilderFactory.Deinitialize();
//        isExit = false;
//        if(null != smsSR){
//            smsSR.deinitialize();
//            smsSR = null;
//        }
//        Logger.loggerError("SMS Lisiner Close");
//        BackendUnLoad();
//        Logger.loggerError("BackEnd Cleaned");
//        FrontendUnLoad();
//        Logger.loggerError("UI Cleaned");
//        Logger.clossLogger();
//    }
//
    /**
     *  Method to create a file to send and received messages.
     *
     * @param msg - variable will contain the message to write.
     * @param isRec - Variable is going to differenciate the send and received message.
     * @param cPacket - Variable will contain the server message.it may be null.
     *
     * @return msgId - Variable will contain the message Id.
     */
    private Message writeMessage(Message messageDto, boolean isRec, String cPacket, boolean isAd){
        int errorCode = 0;
        try{
        if (Settings.getIsDebug()) {
            errorCode = 1;
            StringBuffer temp = new StringBuffer();
            if (isRec) {
//				//#if VERBOSELOGGING
//    //|JG|Logger.loggerError("Building Rx Log message");
//                //#endif //11801
                temp.append(Constants.appendText[16] + ":").append(messageDto.getShortcode());
                if (messageDto.getRtDelay() > 0) {
                    temp.append(",").append(messageDto.getRtDelay()).append("s");
                }
                if (null != cPacket) {
                    messageDto.setCurRMsg(messageDto.getCurRMsg()+ " " + cPacket);
                }

            } else {
                errorCode = 3;
                //#if VERBOSELOGGING
                //|JG|                Logger.loggerError("Building Tx Log message");
                //#endif
                temp.append(Constants.appendText[17] + ":").append(messageDto.getSFullName());
                if (null != messageDto.getQueryType()) {
                    temp.append(",").append(messageDto.getQueryType());
                }
                temp.append(",").append(messageDto.getShortcode());
//                messageDto.setCurRMsg(messageDto.getRMsg()[0]);
            }
            errorCode= 4;
            temp.append(":\n");
            messageDto.setLogMessage(temp.toString());
            //#if VERBOSELOGGING
            //|JG|            Logger.loggerError("Writing string to Inbox - " + temp.toString());
            //#endif
            temp = null;
            errorCode = 5;
        }

        errorCode = 6;
        if (isInboxReplyEnable(messageDto.getSFullName()) && Constants.aName.compareTo(messageDto.getSFullName()) != 0) { // bug Id 7172 and 8310
            messageDto.setIsInboxReply(true);
        }
        errorCode = 7;


        if (isAd && isDynamicAdStore(messageDto.getSFullName())) {
            messageDto.setChannelData(cPacket);
        }
        errorCode = 8;
        if (null == smsReader) {
            smsReader = new SMSReader();
        }
        String msgId = null;
        errorCode = 9;
        synchronized (smsReader) {
            messageDto = smsReader.WriteReceivedMessage(messageDto);
        }
        errorCode = 10;
        if (!isAuthenticated && isRec) {
            loadProfileName = msgId;
        }
        }catch(Exception e){
            System.out.println("Exception->"+e.toString());
            Logger.loggerError("Kernel->WriteMessage->"+e.toString()+" "+errorCode);
        }
        return messageDto;
    }

    /**
     * Method to
     */
    private void registerProfilenumbers() {
        int len = 0;
        if (null != smsSR && null != headers && (len = headers.length) > 0) {
            for (int i = 0; i < len; i++) {
                if (null != headers[i] && null != headers[i].getSC() && headers[i].getSC().length() > 0) {
                    smsSR.addNumber(headers[i].getSC(), headers[i].getName(), headers[i].getMCFormat(), headers[i].getAbbreviation());
                }
            }
        }
    }

    /**
     *
     *
     * @param num -
     * @param name -
     * @param mCf -
     */
    public void registerNewNumers(String num, String name, String mCf, String abbreviat) {
        if (null != smsSR) {
            smsSR.addNumber(num, name, mCf, abbreviat);
        }
    }

    //CR 10044
    public void multiPartMessage(String[] msg, String dString) {
        try {
            if (null != iDisplayPresenter) {
                if (null != dString) {
                    synchronized (iDisplayPresenter) {
                        iDisplayPresenter.displayMultiPartMessage(dString);
                    }
                } else if (KernelConstants.FRENDEND_DISPLAY == uiStState && null != smsHandler) {
                    synchronized (smsHandler) {
                        if (null != (dString = smsHandler.isMultiPartWait(msg))) {
                            synchronized (iDisplayPresenter) {
                                iDisplayPresenter.displayMultiPartMessage(dString);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.loggerError("Kernel->MultipartMessage->" + e.toString());
        }
    }

    public void messageReceived(int msgSendType, Message messageDto, boolean isPlaysound) {
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("Kernel> Error type or Message sendtype= "+msgSendType );
//        //#endif//11801
        try {
            if (null != messageDto) {
                if (msgSendType == 0) {
                    handleResponseMessage(messageDto);
                } else if (msgSendType == 3) { //CR 10203
                    if(exitPressed>0){ //CR 13278
                        unLoad();
                    } else {
                        if(null != messageDto.getChatId()){
                            String receiveMessage = messageDto.getCurRMsg();
                            String cPacket = ObjectBuilderFactory.getControlChanel().removePacket(receiveMessage);
                     //        13837
                            if (null != cPacket) {
                                receiveMessage = Utilities.remove(receiveMessage, cPacket);
                                messageDto.setCurRMsg(receiveMessage);
                            }
                            if (null != iChatPresenter) {
                                iChatPresenter.updateReceiveMessage(messageDto,true);
                            } else {
            //                            //#if VERBOSELOGGING
            //                            //|JG|Logger.debugOnError("Kernel> Chat message history ");
            //                            //#endif //11801
                                ChatHistoryHandler chatHistory = new ChatHistoryHandler();
                                chatHistory.appenedChatScript(messageDto,true,null);
                                chatHistory = null;
                            }
                        } else if (null != smsHandler) {
                            synchronized (smsHandler) {
                                Logger.debugOnError("Sync with SMS handler");
                                if (!smsHandler.isWaitingForMessage(messageDto, msgSendType)) {
                                    if (null != iDisplayPresenter) {
                                        synchronized (iDisplayPresenter) {
                                            iDisplayPresenter.invokeTimer();
                                        }
                                    }
                                }
                            }
                        }
                        setInterpopup(true);
                        if (UISettings.isActivate) {
                            setInterpopup(false);
                        }
                    }
                } else { // Ithaya  - if SMS Sending failed , load initial menu
                    if(exitPressed>0){ //CR 13278
                        unLoad();
                    } else handleErrorMessage(messageDto, msgSendType);
                }
            } else if(msgSendType == 9){
                //Socket reader Exception <--CR 13615
                smsSR.setPOLLMessage(sendReconnectOrPollMessage("!*POLL*!",true));
                //<++13615               
                displayMessageBox(24, Constants.popupMessage[52], Constants.headerText[31]);
            }
        } catch (Exception receiveMsgException) {
            Logger.loggerError("Message Receive Error-> " + receiveMsgException.toString());
            loadErrorScreen(receiveMsgException, Constants.errorMessage[8]);
        }
        //if(UISettings.GENERIC)
        //  ObjectBuilderFactory.getPCanvas().setWarningpopup();
    }

    private void handleErrorMessage(Message messageDto, int msgSendType) {
        if (KernelConstants.BACKEND_PROFILEHANDLER != bkState
                || smsHandler.isWaitingForMessage(messageDto, msgSendType)) {
            Logger.debugOnError("Removing Packet data, error Type = "+msgSendType);
//        13837
            //if(null != messageDto.getChatId()){
//                String receiveMessage = messageDto.getRMsg()[0];
//                String cPacket = ObjectBuilderFactory.getControlChanel().removePacket(receiveMessage);
//                if (null != cPacket) {
//                    receiveMessage = Utilities.remove(receiveMessage, cPacket);
//                    messageDto.setRMsg(new String[]{receiveMessage});
//                }
//                if (null != iChatPresenter) {
//                    iChatPresenter.updateReceiveMessage(messageDto, false);
//                } else {
////                            //#if VERBOSELOGGING
////                            //|JG|Logger.debugOnError("Kernel> Chat message history ");
////                            //#endif //11801
//                    ChatHistoryHandler chatHistory = new ChatHistoryHandler();
//                    chatHistory.removeChatHistory(messageDto);
//                    chatHistory = null;
//                }
//            }
            
            if (msgSendType == 2) {
                displayMessageBox(23, Constants.popupMessage[53], null);
            } else if (msgSendType == 4) {
                displayMessageBox(4, Constants.popupMessage[58], null);
            } else if(msgSendType == 7 || msgSendType == 5  || msgSendType == 11){ //bu 14096
                displayMessageBox(24, Constants.popupMessage[52], Constants.headerText[26]);
            } else if(msgSendType == 8){ //Http/Socket poll error popup CR 13431
                displayMessageBox(22, Constants.popupMessage[67], Constants.headerText[31]);
            } else {
                if (null != messageDto.getErrorMessage()) {
                    displayMessageBox(16, messageDto.getErrorMessage(), null);
                } else {
                    displayMessageBox(16, Constants.popupMessage[28], null);
                }
            }
        }
    }

    private void handleResponseMessage(Message messageDto) throws Exception{
        int errorCode = 0;
        try{
        
        String receiveMessage = messageDto.getCurRMsg();
        String packetData = "";
        String cPacket = null;
        //CR 14398
        while(true){
            cPacket = ObjectBuilderFactory.getControlChanel().removePacket(receiveMessage);
            if (null != cPacket) {
                if(packetData.length()>0){
                    packetData = packetData.substring(0,packetData.length()-2)+","+ cPacket.substring(2);
                } else packetData = cPacket;
                receiveMessage = Utilities.remove(receiveMessage, cPacket);
            } else break;
        }

        AdData adData = null;
        if (packetData.length()>0) {
            adData = ObjectBuilderFactory.getControlChanel().getChannelData(packetData,messageDto.isIsSendQueueEmpty());
        }
        //Cr 14333
        if(null != messageDto.getChatId()){
            ObjectBuilderFactory.getControlChanel().addChatMessageReceived(messageDto.getChatId(), messageDto.getChatSequence());
        }

        errorCode = 1;
        boolean isNo = false;
        if (receiveMessage.length() > 0) {
            errorCode = 2;
            messageDto.setCurRMsg(receiveMessage);
            if (Settings.isIsAppEnable()) {
                //String msgId = cPacket;
                //cPacket = msg[2];
                if (null != adData) {
                    isNo = true;
                }
                boolean saveinbox = false;
                errorCode = 3;
                if (messageDto.getDontSaveInbox()) {
                    Logger.loggerError("Incoming message not saved in Inbox " + messageDto.getSFullName());
                    messageDto.setIsNotNewMsg(true);
                    saveinbox = true;
                    messageDto.setFName((new Date().getTime()) + "|" + messageDto.getQueryType());
                } else {
                    errorCode = 4;
                    Utilities.setCurrentTime();
                    messageDto.setFName(null);
                    messageDto = writeMessage(messageDto, true, cPacket, isNo);
                    Utilities.updateMessage("Received Message stored into inbox " + messageDto.getSFullName());
                    errorCode = 5;
//                            //#if VERBOSELOGGING
//                            //|JG|Logger.debugOnError("Kernel> Processing.. After saving message in Inbox");
//                            //#endif//11801
                }
                if (null != messageDto.getFName()) {
                    errorCode = 6;
                    byte waiting = 0;
                    //boolean isNotDisplayed = true;
                    if (KernelConstants.BACKEND_PROFILEHANDLER == bkState && null != smsHandler) {
                        synchronized (smsHandler) {
                           errorCode = 7;
                           waiting = smsHandler.isWaitingForMessage(messageDto);
                           errorCode = 8;
                           if (waiting == 1) {
                                //Receive Message - cPacket
                                //Dynamic Received ad - adData
                                //received number - msg[0]
                                //Message has been saved inbox or not - saveinbox
                                //Message part - msg[7]
                                //isNotDisplayed = false;
                                //Logger.debugOnError("Kernel->" + msg[0]);
                                if (null != messageDto.getChatId()) {
                                    //bug 13837
                                    errorCode = 9;
                                    if(null == iChatPresenter || !iChatPresenter.updateReceiveMessage(messageDto, false)){
                                        waiting = 0;
                                    } 
                                } else {
                                    errorCode = 10;
                                    if(null != smsHandler){
                                        errorCode = 11;
                                        Utilities.setCurrentTime();
                                        smsHandler.handleReceivedMessage(receiveMessage, adData, messageDto.getShortcode(),
                                                saveinbox, messageDto.getMaxCount());
                                        errorCode = 12;
                                        Utilities.updateMessage("Loaded DAT/Info screen after the Propagete, ignore and keyword");
                                    } else {
                                        waiting = 0;
                                    }
                                }
                            }
                        }
                    }
                    if (waiting != 1) {
                        errorCode = 13;
                        if (null != messageDto.getChatId()) {
                            errorCode = 14;
                            if (null == iChatPresenter) {
                                errorCode = 15;
                                Utilities.setCurrentTime();
                                ChatHistoryHandler chatHistory = new ChatHistoryHandler();
                    //          bug 13837
                                chatHistory.appenedChatScript(messageDto,false,null);
                                chatHistory = null;
                                errorCode = 16;
                                Utilities.updateMessage("Received chat message stored into Chat history");
                            }
                            //CR 12319, 13121 13645, 14035
                            errorCode = 17;
                            String abbervation = getAppAbbervation(messageDto.getSFullName());
                            errorCode = 18;
                            if(abbervation.indexOf("FBC") >-1 || abbervation.indexOf("GGC") >-1
                                    || abbervation.indexOf("FUN") >-1 || abbervation.indexOf("CHT") >-1
                                     || abbervation.indexOf("VMP") >-1)  {
                                //Bug 13203
                                errorCode = 19;
                                if(null == smsHandler || smsHandler.isNotCurrentApp(messageDto.getSFullName())){
                                    errorCode = 20;
                                    if (abbervation.indexOf(")>") == -1) {
                                        abbervation = "(" + abbervation + ")>";
                                    }
                                    GlobalMemorizeVariable.updateChatUnreadCount(abbervation,1,false);
                                    errorCode = 21;
                                }
                            }
                            
                            //CR 12118
                            if(null != messageDto.getChatId()){
                                errorCode = 22;
                                if(waiting == 2 && null != iGetEntryPresenter){
                                    //Bug 14155,14156, 0014368
                                    if(null != smsHandler){
                                        errorCode = 23;
                                        iGetEntryPresenter.changeMenuItemName(smsHandler.getOldValue(), waiting, smsHandler.getNewValue());
                                        errorCode = 24;
                                    }
                                } else if(waiting == 3 && null != iMenuPresenter){
                                    errorCode = 25;
                                    iMenuPresenter.changeMenuItemName(-1, "Recent Chats");
                                    errorCode = 26;
                                }
                            }
                            
                        }
                         // Logger.debugOnError(" before is authenticated" );
                        if (isAuthenticated && (!messageDto.isIsNotNewMsg() || null != messageDto.getChatId())) { //msg[9] is for chat
                            String dmsg = null;
                            String[] notifMsg = null;
                            if (null == messageDto.getChatId()) {
//                                        //#if VERBOSELOGGING
//                                        //|JG|Logger.debugOnError("Kernel> chat message ");
//                                        //#endif //11801
                                dmsg = Constants.appendText[18] + " ";
                                if (null != messageDto.getQueryType()) {
                                    dmsg += messageDto.getSFullName() + ": " + messageDto.getQueryType();
                                } else if (null != messageDto.getSFullName()) {
                                    dmsg += messageDto.getSFullName();
                                }
                                notifMsg = new String[]{Constants.appendText[19], messageDto.getFName()};
                                setNotification(1, notifMsg, dmsg);
                            } else {
                                //CR 12318, 13121 13645, 14035
                                String abbervation = getAppAbbervation(messageDto.getSFullName());
                                if(abbervation.indexOf("FBC") >-1 || abbervation.indexOf("GGC") >-1
                                        || abbervation.indexOf("FUN") >-1  || abbervation.indexOf("CHT") >-1
                                        || abbervation.indexOf("VMP") >-1){
                                    dmsg = Constants.appendText[38] + " " + messageDto.getChatName();
                                    String iconLocation = getProfileLocation(messageDto.getSFullName());
                                    if (null != iconLocation) {
                                        iconLocation = RecordManager.getLogoImageName(getProfileLocation(messageDto.getSFullName()));
                                    } else {
                                        iconLocation = RecordManager.getSTLogoLocation();
                                    }
                                    displayChatNotification(new String[]{dmsg, iconLocation, messageDto.getSFullName()});
                                } else {
                                    dmsg = Constants.appendText[26] + " " + messageDto.getChatName() + " " + Constants.appendText[29];
                                    notifMsg = new String[]{Constants.appendText[27], messageDto.getSFullName(), messageDto.getChatName(), messageDto.getChatId(),messageDto.getMessagePlus()};
                                    setNotification(2, notifMsg, dmsg);
                                }
                            }
                        }
                    }
                    //bug id 4934 (isPlaySound)
                    if (Settings.getIsNotification() && (!messageDto.isIsNotNewMsg() || null != messageDto.getChatId())) { //msg[9] is for chat
                        //#if VERBOSELOGGING
                        //|JG|                        Logger.debugOnError("Kernel> Play sound ");
                        //#endif
                        SoundManager.getInstance().playSound();
                    }
                }
            }
            //CR 11730
            if (messageDto.getPopulatedTime() > 0 && messageDto.isIsRTDASend()) {
                ObjectBuilderFactory.getControlChanel().addRoundTripTime(messageDto.getPopulatedTime(), messageDto.getMscf());
            }
        }
        }catch(Exception e){
            Logger.loggerError("Kernel->Handlereponse->"+e.toString()+" "+errorCode);
            throw  e;
        }
    }

//    public void resendMessage() {
//        smsSR.reSendMessage();
//    }

    public String[] sendMessage(Message message){
        //CR 14324
        message.getRMsg()[0] = ObjectBuilderFactory.getControlChanel().addPacket(message.getShortcode(), 
                message.getRMsg()[0],message.isIsDateTimeSend());
        if (!message.isIsDSend() && message.isIsNotChatMessage()) {
            writeLogQuery(message);
        }

        if (null != smsSR) {
            return smsSR.populateSendMessageDto(message);
        }
        return null;
    }

    public void receiveMessage(String msg, String pName, String num, String sequenceNumber) {
//        msg = "(1/4h)Google Local Listings: Residence Inn San Diego La Jolla 8901 Gilman Drive La Jolla 858-587-1770 Hotel La Jolla At the Shores";
//        smsSR.receiveMessage(msg,pName, num);
//        msg = "(2/4h) 7955 La Jolla Shores Drive La Jolla, 92037-3301 858-459-0541Lodge at Torrey Pines Spa The Ste A, 11480 N Torrey Pines Rd";
//        smsSR.receiveMessage(msg,pName, num);
//        msg ="(3/4h) La Jolla, 92037-1095 858-777-6687 Reply with NEXT for more results. Tip: yourdefault location has been set to La Jolla";
//        smsSR.receiveMessage(msg,pName, num);
//        msg = "(4/4h), San Diego, CA 92037. For help on default location commands, reply with HELP LOCATION.";
        //msg = "Twitter 1. cricketwealth 2. criclivescore 3. CricNews 4. crictwits 5. crixlee 6. CSFinance 7. daily_finance 8. DashcodeWidgets 9. debt_guy 10. DINKSFinance 11. DurhamCricket 12. ebidwidgets 13. ECB_cricket 14. EssexCricket 15. EverydayFinance 16. FamilyFT 17. FCFCU 18. finance_news 19. finance_yard 20. finance1978 21. FinanceGirl22. financejobs 23. FinanceJobUK 24. FinanceLinks25. FinanceStartups 26. ForbesTech 27. free_wp_widgets 28. freewebsite4u 29. FTfinancenews 30. Full2Cricket *EOL*";
        //  msg ="mGive Donations VH1 Save The Music Foundation! 50555, MUSIC VFW Foundation! 90999, VFW USA Cares, Inc.! 90999, ICARE University of Kentucky Research Foundation! 50555, DANCE University ofFlorida Foundation, Inc.! 90999, UF United Way Worldwide! 864833, FIT United Way of the National Capital Area! 864833, NCA United Way of the Columbia-Willamette! 864833, PDXCARES United Way of the Bluegrass! 864833, BLUE United Way of the Big Bend! 864833, UWBB United Way of SoutheasternPennsylvania! 864833, PHILLY United Way of San Diego County! 864833, SAND";
        //msg = "!*PCNF16505767294:*!";
        // msg = "Boletim NOTICIAS_0,31_60";
        //msg = "Netflix 1. Nine 2. The Kite Runner 3. Borat 4. District 9 5. Starman 6. Happy-Go-Lucky 7. Star Trek 8. Babel 9. Flyboys 10. Geisha Assassin 11. The Line 12. Little Miss Sunshine 13. Van He lsing 14. Once Upon a Time in China 2 15. Spanglish 16. The Sentinel 17. Warriors of Heaven and Earth 18. Taken 19. The Time Traveler's Wife .";
//        msg ="sasikumar!*ANEW1I91081Play MOBileWars on ur PilgrimTilt:C163068*!";
        //msg = "Twitter 1. cricketwealth 2. criclivescore 3. CricNews 4. crictwits 5. crixlee 6. CSFinance 7. daily_finance 8. DashcodeWidgets 9. debt_guy 10. DINKSFinance 11. DurhamCricket 12. ebidwidgets 13. ECB_cricket 14. EssexCricket 15. EverydayFinance 16. FamilyFT 17. FCFCU 18. finance_news 19. finance_yard 20. finance1978 21. FinanceGirl22. financejobs 23. FinanceJobUK 24. FinanceLinks25. FinanceStartups 26. ForbesTech 27. free_wp_widgets 28. freewebsite4u 29. FTfinancenews 30. Full2Cricket *EOL*";
        //msg ="mGive Donations Zoological Society of San Diego! 90999, PANDA YMCA of Metropolitan Atlanta, Inc.! 90999, YMCA Wounded Warrior Project! 90999, WWP World Relief Corp. of National Association of Evangelicals! 50555, DONATE World Emergency Relief! 50555, RESCUE Women's Media Center! 50555, WOMEN WHYY, Inc.! 90999, WHYY WhyHunger (formerly World Hunger Year or WHY)! 90999, WHY Washington Sports & Entertainment Charities, Inc.! 50555,WIZARDS W.I. Cook Foundation! 50555, COOK Volunteers of America Southeast! 50555, START Volunteers of America! 50555, VOA .";
        smsSR.receiveMessage(msg, pName, num);
    }

    /**
     * Method to send the SMSRequest.
     *
     * @param pId - Variable will contain the Profile Id.
     * @param sCode - Variable will contain the Server Code.
     * @param msg - Variable will contain the SMSRequest.
     * @param qType - Variable will contain the Request Type.
     * @param mWords - Variable will contain the Match Words.it may be null.
     * @param mMWords - Variable will contain the MisMatch Words.it may be null.
     * @param mscf - Variable will contain the receive message concadination type
     */
    /**
     * Method to write the Send message
     *
     * @param qType - Variable will contain the SMSRequest type.
     * @param pName - Variable will contain the Profile Name
     * @param msg - Variable will contain the Send message.
     * @param sCode - Variable will contain the Server Code.
     */
    public void writeLogQuery(Message message){
        if (Settings.getIsDebug()) {
            writeMessage(message, false, null, false);
        }
    }
}
