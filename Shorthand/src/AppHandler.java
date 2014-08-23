
import generated.Build;

/**
 * AppHandler Class
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough
 **/


public class AppHandler {

    // Bug ID - 4205
    //d Variable to hold the Conform Pin Text for the Secondary Header
    //private final String CONFIRM_PIN = "Confirm Enter PIN";

    // Bug ID - 4205
    // Variable to hold the Pin Number Enrty Text for the Secondary Header
    //private final String ENTER_NEW_PIN = "4-Digit Enter PIN";

    // Variable to hold the Sercurity Pin Number
    //private String securityPin = null;

    // Object to Interact with the Files
    private AppParser parser = null;

    private boolean isAccept = false;

    /**
     * Variable to maintain the Current State of the Basic Application Process
     *
     *  0 - Profile Screen
     *  1 - Advance Menu Screen
     *  2 - Settings Screen
     *  3 - Security Screen
     *  4 and 5 - GetEntry Screen(4-Pin Number Entry Screen, 5-Conform Pin Number Screen)
     *  6 - Get Information
     *  7 - SmartSearch
     *  8 - Favourite
     *  9 - Delete Display Screen
     * 10 - Debug Log Enable Mode
     **/
    private byte currst = 0;

    /**
     * Variable to hold the Message Box State to handle the operations based on the options
     *
     *  2 - Delete All Profiles
     *  1 - Error (Exception Raised)
     *  3 - Welcome Screen
     **/
    private byte msgst;

    //private int fQueryId = -1;

    //private String fQueryName = null;

    private boolean isStartApp = false;

    private boolean isMasterAppLaunch = false;

    /**
     * Method to Load the Welcome Message or Aggrement Message while Application start
     **/

    public void loadInitialMenu(boolean isFirstLaunch) {
       // String domainValue = checkCountryDomain();
        isStartApp = true;
        isMasterAppLaunch = isFirstLaunch;
        if(RecordManager.isAggrementSigned(ObjectBuilderFactory.GetKernel().getVersionNumber(true))){
            //<--CR 13617
            if(Settings.isInitialStart) {
                loadCurrentState(isStartApp);
            } //CR 13617 -->
            else {
                initialize(0,false);
                if(Settings.isIsWelcome()) {
                    showWelcomeMessage();
                } else if(Settings.isIsShowMode()){
                    showModeMessage();
                } else loadNoWidgetMessage();
            }
        } else {
            setPresenterDto();
            //CR number 5999
            if(ChannelData.getTosEnable()){
                ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_PROFILE);
                if(Constants.popupMessage[6].length()>0) {
                    loadMessageBox(8, Constants.popupMessage[5]+Build.LINK+Constants.popupMessage[6], 4,Constants.headerText[11]);
                } else {
                    loadMessageBox(8, Constants.popupMessage[5], 4,Constants.headerText[11]);
                }
            } else {
                msgst = 4;
                handleMessageBox(true);
            }
        }
    }

    private void showModeMessage(){
        //CR 12191
        String mode = Constants.appendText[36];
        if(Settings.isIsGPRS())
            mode = Constants.appendText[37];
        loadMessageBox(9, Constants.popupMessage[65] + " "+mode+" "+Constants.popupMessage[66], 11, null);
        //CR 12191
    }

    private void showWelcomeMessage(){
        String welComeMessage = ChannelData.getWelcomeMessage();
        if(null != welComeMessage){
            welComeMessage = Utilities.replace(welComeMessage, "<CR>", "\n");
            welComeMessage = Utilities.replace(welComeMessage, "-CR", "\n");
            loadMessageBox(9, welComeMessage, 3, Constants.headerText[10]);
        } else {
            if(Constants.popupMessage[4].length() == 0){
                loadMessageBox(9,Constants.popupMessage[3],3,Constants.headerText[10]);
            } else {
                loadMessageBox(9,Constants.popupMessage[3]+Build.LINK+" "+Constants.popupMessage[4],3,Constants.headerText[10]);

            }
        }
    }

//    private String checkCountryDomain(){
//        String linkName = null;
//       if(ChannelData.getRegion().equals("India"))
//            linkName = "in";
//        else if(ChannelData.getRegion().equals("US"))
//            linkName = "com";
//        else if(ChannelData.getRegion().equals("Brasil"))
//            linkName = "br";
//        return linkName;
//    }

    private void loadAppErrorMessage(boolean isAppError){
        boolean isLoad = false;
        if(isAppError){
            if(ObjectBuilderFactory.GetKernel().isDownloadAppPending()){
                if(isStartApp || null != ObjectBuilderFactory.GetKernel().getSMSProfilesLoaded() || RecordManager.isFeatureAppDownloaded())
                    loadMessageBox(10, Constants.popupMessage[7], 8, Constants.headerText[12]);
                else
                    loadMessageBox(14, Constants.popupMessage[8], 0, Constants.headerText[8]);
            }else isLoad = true;
            isStartApp = false;
        }else isLoad = true;
         if(isLoad && null != ObjectBuilderFactory.getControlChanel().versionNumber){ //CR 13721
            String  value = ObjectBuilderFactory.getControlChanel().versionNumber;
            ObjectBuilderFactory.getControlChanel().versionNumber = null;
            ObjectBuilderFactory.GetKernel().displayMessageBox(26, value+" - "+Constants.popupMessage[68],
                            Constants.headerText[32]);
        }

    }

    /**
     * Method to Invoke to Load the Menu items
     *
     * @param chSt - Variable to hold the Current State
     **/
    public void initialize(int chSt,boolean isNotLocal) {
        if(null  == parser)
            parser = new AppParser();
        setPresenterDto();
        load(chSt,isNotLocal);
    }

    /**
     * Method to Load the header and Background Images
     *
     * @throws loadImagesException - Logging the Error Message in a seperate file
     **/
    private void setPresenterDto() {
        try{
            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
            PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
            PresenterDTO.setHdrtxt(Constants.aName);
        } catch(Exception loadImagesException) {
            Logger.loggerError("Back Image:"+loadImagesException.getMessage()+loadImagesException.toString());
        }
    }

    /**
     * Method to Invok to load Profile Menu items or Application Menu items.
     *
     * @param chSt - Variable to hold the Current State.
     *
     **/
    private void load(int chSt,boolean isNotLocal) {
        currst =(byte)chSt;
        Object items = getMenuItems();
        if (items instanceof String[]) {               // Load the Application Default Menu items.
            PresenterDTO.setLOptByte((byte)-1);
            loadMenuItems((String[]) items);
        } else if(null == items){
            if(currst != 8){
                ObjectBuilderFactory.GetKernel().setNewProifleHeaders();
                PresenterDTO.setLOptByte((byte)41);
                loadProfileItems();                    // Load the Profile Menu items.
                loadAppErrorMessage(isNotLocal);
            }
            else
            {
                PresenterDTO.setLOptByte((byte)-1);
                loadMenuItems(null);
            }
        }
    }

    /**
     *
     * @param status
     */
    public void loadCurrentState(boolean status){
        if(currst == 10){
            load(currst,true);
        } else {
            //cr 13617
            if(Settings.isInitialStart){
                isStartApp = false;
              Settings.isInitialStart = false;
                ObjectBuilderFactory.GetKernel().setProfileHeaders();
                //CR 12360
                if(!Settings.isIsPCNF())//V4.0.1
                    ObjectBuilderFactory.getControlChanel().sendProvenanceAction(true);
                String name = null;
                if(RecordStoreParser.isRecordStoreExits(RecordManager.startupAppName)){
                    ObjectBuilderFactory.GetKernel().loadProfile(null, RecordManager.startupAppName+"-d", true,true);
                } else if(null != (name = DownloadHandler.getInstance().getStartupApp())){
                    ObjectBuilderFactory.GetKernel().loadProfile(null, name+"-j", true,true);
                } else{
                   loadInitialMenu(true);
                }
            } else load(0,false);
        }
    }
    /**
     * Method will invok to get different type of Application Menu Items
     *
     * @return - Application values will return.It may return null.
     **/
    private Object getMenuItems() {
        if (currst == 0){
            return null;
        } else if (currst == 1) { //Advance Menu items
            //Cr number 5996
            return getAdvanceOptionMenu(); // advanceEditOptionsForSMS;
        } else if (currst == 2) { // settings Menu items
            return getSettingsMenu();
        }
//        else if (currst == 3) {// Security Menu items
//            return getSecurityMenu();
//        }
//        else if(currst == 8){ //Favourite Menu items
//            return getFavouriteList();
//        }
        else if(currst == 10)
            return getLogMenu();

        return null;
    }

    private String[] getAdvanceOptionMenu(){
        if(!ChannelData.getDeleteApp() && !ChannelData.getDeleteApps())
            return new String[] {Constants.advanceEditOptionsForSMS[3],Constants.advanceEditOptionsForSMS[0]};
        else if(!ChannelData.getDeleteApp()){
            return new String[] {Constants.advanceEditOptionsForSMS[3],Constants.advanceEditOptionsForSMS[0],Constants.advanceEditOptionsForSMS[2]};
        } else if(!ChannelData.getDeleteApps()){
            return new String[] {Constants.advanceEditOptionsForSMS[3],Constants.advanceEditOptionsForSMS[0],Constants.advanceEditOptionsForSMS[1]};
        }
        return new String[]{Constants.advanceEditOptionsForSMS[3],Constants.advanceEditOptionsForSMS[0],Constants.advanceEditOptionsForSMS[1],Constants.advanceEditOptionsForSMS[2]};
       // return Constants.advanceEditOptionsForSMS;
    }

    private String[] getLogMenu(){
        return new String[] {Constants.debugMenu[0],Constants.debugMenu[1],getLogMenuName(), getScreenRefreshMenuName()};
    }

    private String getLogMenuName(){
        boolean isEnabled = Settings.getIsDebug();
        return getChangeDbugMenuName(!isEnabled, Constants.debugMenu[2]);
    }

    //CR 6986
    private String getScreenRefreshMenuName(){
        boolean isEnabled = Settings.isIsScreenRefresh();
        return getChangeDbugMenuName(!isEnabled, Constants.debugMenu[3]);
    }

    private String getChangeDbugMenuName(boolean isEnable, String menuStatus) {
        if (isEnable) {
            menuStatus = menuStatus + " "+Constants.appendText[4]; //Off
        } else {
            menuStatus = menuStatus + " "+Constants.appendText[5]; //On
        }
        return menuStatus;
    }

    /**
     * Method to Get All the Profile Sequence Shortcuts of the Application.
     *
     * @return - return all the profiles shortcuts.It may return null.
     **/
//    private String[] getFavouriteList(){
//        SMSProfileHeader[] pHeaders = ObjectBuilderFactory.GetKernel().getSMSProfilesLoaded();
//        int count;
//        if(null != pHeaders && (count=pHeaders.length)>0){
//            String[] pName = new String[count];
//            for(int i=0;i<count;i++){
//                pName[i] = pHeaders[i].getName();
//            }
//            pHeaders = null;
//            return parser.getFavouriteProfileList(pName);
//        }
//        return null;
//    }

    /**
     * Method to Load the different type of Application Menu items based on the request
     *
     * @param menuItems - Variable will contain the Application Menu items to display
     *
     **/
    private void loadMenuItems(String[] menuItems) {
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_MENU);
        LWEATActionDTO lWEATActionDTO =  new LWEATActionDTO();
        lWEATActionDTO.setEntryBoxEnabled(false);
//        MenuResponseDTO _responseDto = new MenuResponseDTO();
        if(null != menuItems){
            int len = menuItems.length;
            if (len > 0) {
                int[] itemids = new int[len];

                for (int i = 0; i < len; i++) {
                    itemids[i] = i;
                }
                lWEATActionDTO.setListItemIds(itemids);
                lWEATActionDTO.setListItems(menuItems);
            }
        }

        lWEATActionDTO.setBackID((byte)22);

        if(1 == currst) {
            lWEATActionDTO.setSecHdrText(Constants.headerText[2]);
        } else if(2 == currst){
            lWEATActionDTO.setSecHdrText(Constants.headerText[3]);
        } else if(3 == currst){
            lWEATActionDTO.setSecHdrText(Constants.headerText[4]);
        } else if(8 == currst){
            lWEATActionDTO.setSecHdrText(Constants.headerText[5]);
            PresenterDTO.setLOptByte((byte)41);
        }

        ObjectBuilderFactory.GetKernel().displayScreen(lWEATActionDTO,true);
        lWEATActionDTO = null;
    }

    /**
     * Method will handle the Menu items Based on the Current state.
     *
     * @param itemId - Variable will hold the Selected item id.It may be Null in some cases.
     * @param itemName - variable will hold the Selected item Name.It may be Null in some cases.
     *
     * @throws handleItemException.
     *
     **/
    public void handleItemSelection(int itemId, String itemName) {
        //try {
            if (2 == currst) {
                handleSettingMenuSelection(itemId);
            } else if (1 == currst) {
                handleManageAppMenuSelected(itemId,itemName);
            }
//            else if (3 == currst) {
//                handleSecurityMenuSelection(itemId);
//            }
//            else if (4 == currst || 5 == currst) {
//                handleSecurityItemSelection(itemName);
//            }
            else if(7 == currst){
                load(0,true);
            }
//            else if(8 == currst){
//                handleFavourite(itemName);
//            }
            else if(9 == currst){
                load(0,true);
            }  else if(10 == currst){
                handleDebugMenu(itemId);
            } else {
                handleAppSelected(itemName);
            }
        //} catch (Exception handleItemException) {}
    }

    private void handleAppSelected(String appName){
        if (null != appName) {
            if (appName.startsWith(new String(Constants.options[38]))) {
                ObjectBuilderFactory.GetKernel().loadInbox();
            } else {
                boolean isNormalApp = false;
                String pLoc = ObjectBuilderFactory.GetKernel().getProfileLocation(appName);
                //CR 4924
                if(null  == pLoc){
                    pLoc = RecordManager.getFeatureAppName(appName);
                    isNormalApp = true;
                    appName = "";
                }
                ObjectBuilderFactory.GetKernel().loadProfile(appName,pLoc,isNormalApp,false);
            }
        }
    }


    /**
     *
     */
    private void handleDebugMenu(int itemID){
        if(itemID == 0){
            if(Logger.isLogNotEmpty()){
                ObjectBuilderFactory.GetKernel().startHttpProcess(5, null, null, null);
                //if(null != Settings.getPhoneNumber()){ //CR 6724 //CR 8999
          //CR 12815         // loadMessageBox(5, Constants.popupMessage[1], 7,Constants.headerText[9]);
                //} else {
                  //  ObjectBuilderFactory.getControlChanel().sendProvenanceAction(true);
                    //bug is 5502
                    //loadMessageBox(4, Constants.popupMessage[9], 0, Constants.headerText[8]);
                //}
            } else loadMessageBox(0, Constants.popupMessage[10], 0, Constants.headerText[8]);
        } else if(itemID == 1){
            loadMessageBox(5,  Constants.popupMessage[63], 10, Constants.headerText[13]);
        } else if(itemID == 2){
            boolean isEnable = Settings.getIsDebug();
            Settings.setIsDebug(!isEnable);
            ObjectBuilderFactory.GetKernel().changeMenuItemName(itemID,
                    getChangeDbugMenuName(isEnable,Constants.debugMenu[itemID]));
        } else if(itemID == 3){ //Cr 6986
            boolean isEnable = Settings.isIsScreenRefresh();
            Settings.setIsScreenRefresh(!isEnable);
            ObjectBuilderFactory.GetKernel().changeMenuItemName(itemID, getChangeDbugMenuName(isEnable, Constants.debugMenu[itemID]));
        }
    }

    /**
     * Method to handle the differnt type of Settings Menus.
     *
     * @param itemId - Variable will contain the Selected item id.(Not Null)
     *
     **/
    private void handleSettingMenuSelection(int itemId) {
        boolean isEnable = false;
        if(0 == itemId){
            handleAboutShartHand();

        } else if(1 == itemId || itemId == 2){ //<--CR 0012014 -->
            loadMessageCounterScreen(itemId);
        }
        //CR 10682
//        else if(2 == itemId){
//            load(3,true);
//        }
        else if(3 == itemId){
            isEnable = Settings.getIsNotification();
            Settings.setIsNotification(!isEnable); //10735
            ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId,
                    getChangeMenuName(isEnable, Constants.settingsMenu[1]));
        }
        else if(4 == itemId){
            Settings.setIsGrid(!Settings.isIsGrid()); //Then when the user clicks the item they are immediately taken to the Shorthand Home.
            //ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId, getViewDisplayMenuName());
            loadPreviousMenu();
        }
        //CR 11974
        else if(5 == itemId){
            //--cr 13222
            if(ChannelData.isSMSMode()){
                  //++cr 13222
            if(null != ChannelData.getDataRequestUrl()){
                Settings.setIsUCNF(false);
                //CR 0012061 and 12147
                Settings.setIsGPRS(!Settings.isIsGPRS()); //Then when the user clicks the item they are immediately taken to the Shorthand Home.

                //ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId, getViewDisplayMenuName());
                loadPreviousMenu();
                if(!Settings.isIsGPRS())
                    ObjectBuilderFactory.GetKernel().ChangeModeOrTimer(true,true);
                //CR 13397
//                else if(null == Settings.getPhoneNumber(true)){ //CR 12988 //CR 13219
//                    loadMessageBox(4, Constants.popupMessage[61], 0, null);
//                }
                //CR 0012061 and 12147
            } else {
                //CR 0012062
                loadMessageBox(4, Constants.popupMessage[62], 0, null);
                } }
              //--cr 13222
            else{
                 load(10,true);
                }
  //++cr 13222
        } else if(6 == itemId){
            load(10,true);
        }
    }

    /**
     * Method to Load the Pin Number Entry and handle the Security Menu items.
     *
     * @param itemId - Variable will contain the Item id.(Not Null)
     **/
//    private void handleSecurityMenuSelection(int itemId) {
//        boolean isEnable = false;
//        if(0 == itemId){
//            loadGetEntry(Constants.headerText[1]); //4-Digit Enter PIN
//        } else if(1 == itemId){
//            isEnable = Settings
//            .getIsPinEnabled();
//            Settings.setIsPinEnabled(
//                    !isEnable);
//            ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId,
//                    getChangeMenuName(isEnable,Constants.securityMenu[itemId]));
//        }
//    }

    /**
     * Method to Load the conformation Enrty and handle the Pin Number Conformation.
     *
     * @param pinNum - Variable will contain the User Entered Pin Number.(Not Null)
     *
     **/
//    private void handleSecurityItemSelection(String pinNum) {
//        if (4 == currst) {
//            securityPin = pinNum;
//            loadGetEntry(Constants.headerText[0]); //Confirm Enter PIN
//        } else if (5 == currst) {
//            if (securityPin.equals(pinNum)) {
//                Settings.setPinNumber(securityPin);
//                load(3,true);
//            } else {
//                loadMessageBox(0,Constants.popupMessage[12],0,Constants.headerText[12]);
//            }
//        }
//    }

    /**
     * Method to retrieve the Settings Menu Items.
     *
     * @return settingMenus - Variable will contain the Setting Menus.
     *
     **/
    private String[] getSettingsMenu() { // CR 10548
        currst = 2;
        String[] settingMenus = null;
        //CR 10682
//        if(Settings.getIsDebug())
//            settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[0], getNotificationMenuName(), getViewDisplayMenuName(),Constants.settingsMenu[3]};
//        else
//            settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[0], getNotificationMenuName(), getViewDisplayMenuName()};//,Constants.settingsMenu[2]};
        //CR 11974

        // CR 13222 -->



        if(ChannelData.isSMSMode()){
            if(Settings.getIsDebug()) {
                settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[10], getNotificationMenuName(), getViewDisplayMenuName(),getRequestDataMenuName(),Constants.settingsMenu[3]};
            } else {
                settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[10], getNotificationMenuName(), getViewDisplayMenuName(),getRequestDataMenuName()};//,Constants.settingsMenu[2]};
            }
        } else {
            if(Settings.getIsDebug()) {
                settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[10], getNotificationMenuName(), getViewDisplayMenuName(),Constants.settingsMenu[3]};
            } else {
                settingMenus = new String[] {Constants.settingsMenu[6], Constants.settingsMenu[7], Constants.settingsMenu[10], getNotificationMenuName(), getViewDisplayMenuName()};//,Constants.settingsMenu[2]};
            }
        }

        //cr 13222++
        //getDebugMenuName(),//,getTexting() ;// , getDefaultSMSClient(), getTexting()
        // };
        return settingMenus;
    }

    /**
     *  Method to get the Settings Debug Menu Status.
     *
     * @return - return the Ststus.
     *
     **/
//    private String getDebugMenuName() {
//        boolean isEnabled = Settings.getIsDebug();
//        return getChangeMenuName(!isEnabled, Constants.settingsMenu[2]);
//    }

    /**
     * Method to get the Settings Notification Menu Ststus.
     *
     * @return - return the Ststus.
     *
     **/
    private String getNotificationMenuName() {
        boolean isEnabled = Settings.getIsNotification();
        return getChangeMenuName(!isEnabled, Constants.settingsMenu[1]);
    }

    private String getViewDisplayMenuName(){
        if(Settings.isIsGrid())
            return Constants.settingsMenu[4];
        return Constants.settingsMenu[5];
    }

    //CR 11974
    private String getRequestDataMenuName(){
        if(Settings.isIsGPRS()){
            return Constants.settingsMenu[8];
        }
        return Constants.settingsMenu[9];
    }

    /**
     * Method to get the Security Menus.
     *
     * @return - return the Security Menu items.
     *
     **/
//    private String[] getSecurityMenu() {
//        currst = 3;
//        String[] securityMenus = { Constants.securityMenu[0], getPinNumberMenuName() };
//        return securityMenus;
//    }

    /**
     * Method to get the Security Pin Number Menu Status
     *
     * @return - return the Status.
     *
     **/
//    private String getPinNumberMenuName() {
//        boolean isEnabled = Settings
//        .getIsPinEnabled();
//        return getChangeMenuName(!isEnabled, Constants.securityMenu[1]);
//    }



    /**
     * Method to change the status weather disable or enable based on the request.
     *
     *
     * @param isEnable - Status will convert based on this variable.if enable, status will change to disable.
     * @param menuStatus - variable will contain the current Status.
     *
     * @return menuStatus - Variable will contain the Actual Status.
     **/
    private String getChangeMenuName(boolean isEnable, String menuStatus) {
        if (isEnable) {
            menuStatus = menuStatus + " " + Constants.appendText[6]; //Disabled
        } else {
            menuStatus = menuStatus + " " + Constants.appendText[7]; //Enabled
        }
        return menuStatus;
    }

    /**
     * Method to Get the different type of options based on the state.
     * It may contain the profile Name to display in options.
     *
     * @param profileName - Variable will contain the profile Name.it may null for some cases.
     *
     * @return opts - Variable will conain the options for the current state.(Not Null).
     *
     **/
    public byte[] getOptionsMenu(String profileName) {
        byte[] opts = null;
        if(currst != 8){
            if (null == profileName || 
                    profileName.startsWith(String.valueOf(Constants.options[38])) ||
                    //CR 4924
                    (null != Settings.getAppCatalogName() &&
                    profileName.startsWith(String.valueOf(Settings.getAppCatalogName())))){
//Removed My Shortcuts from Options Menu - CR 7217
//                opts = new byte[] {0,48,4,13,5};
                //opts = new byte[] {0,48,4,5};
                opts = new byte[] {0,5}; //CR 10548
            } else {
                Constants.advanceEditOptionsForSMS[3] = (Constants.appendText[0]+" "+ profileName);//.toCharArray();
//                if(ChannelData.getDeleteApps()) { // CR number 6757
//                    opts = new byte[] {0,48,1,3,4,5};
//                } else {
//                    opts = new byte[] {0,48,3,4,5};
//                }
                if(ChannelData.getDeleteApps()) { // CR number 6757, 10548
                    opts = new byte[] {1,0,5};
                } else {
                    opts = new byte[] {0,5};
                }
            }

            //CR 4924
            boolean isInclueded = ChannelData.getAppCatalogInclude();
            if(null != Settings.getAppCatalogName()
                        && profileName.compareTo(Settings.getAppCatalogName()) == 0){
                isInclueded = false;
            }

            byte[] temp = Constants.setDOpt(RecordManager.getfeatureAppsList(isInclueded), true,true); //CR 7230
            if(null != temp) {
                byte appCatalog = 0;
                if(isInclueded){ //CR 7230
                    appCatalog = 1;
                }
                byte[] opt = new byte[opts.length+temp.length];

                System.arraycopy(temp, 0, opt, 0, temp.length);
                System.arraycopy(opts, 0, opt, temp.length, opts.length);
                opts = sort(opt, appCatalog); //bug 10612
            }
        } else{
            opts = new byte[] { 36 };
        }
        return opts;
    }
/*

    public int[] getOptionsMenuString(String profileName) {
        int[] opts = null;
        if(currst != 8){
            if (null == profileName ||
                    profileName.startsWith(String.valueOf(Constants.options[38])) ||
                    //CR 4924
                    (null != Settings.getAppCatalogName() &&
                    profileName.startsWith(String.valueOf(Settings.getAppCatalogName())))){
//Removed My Shortcuts from Options Menu - CR 7217
//                opts = new byte[] {0,48,4,13,5};
                //opts = new byte[] {0,48,4,5};
                opts = new int[] {0,5}; //CR 10548
            } else {
                Constants.advanceEditOptionsForSMS[3] = (Constants.appendText[0]+" "+ profileName);//.toCharArray();
//                if(ChannelData.getDeleteApps()) { // CR number 6757
//                    opts = new byte[] {0,48,1,3,4,5};
//                } else {
//                    opts = new byte[] {0,48,3,4,5};
//                }
                if(ChannelData.getDeleteApps()) { // CR number 6757, 10548
                    opts = new int[] {1,0,5};
                } else {
                    opts = new int[] {0,5};
                }
            }

            //CR 4924
            boolean isInclueded = ChannelData.getAppCatalogInclude();
            if(null != Settings.getAppCatalogName()
                        && profileName.compareTo(Settings.getAppCatalogName()) == 0){
                isInclueded = false;
            }

            int[] temp = Constants.setDtOptInt(RecordManager.getfeatureAppsList(isInclueded), true,true); //CR 7230
            if(null != temp) {
                byte appCatalog = 0;
                if(isInclueded){ //CR 7230
                    appCatalog = 1;
                }
                int[] opt = new int[opts.length+temp.length];

                System.arraycopy(temp, 0, opt, 0, temp.length);
                System.arraycopy(opts, 0, opt, temp.length, opts.length);
                opts = sorting(opt, appCatalog); //bug 10612
            }
        } else{
            opts = new int[] { 36 };
        }
        return opts;
    }


    private int[] sorting(int[] fLoc, byte appcatalog){
        int len;
        if(null != fLoc && (len=fLoc.length)>0){
            int temp = -1;
            for(int i=appcatalog;i<(len-1);i++)
            {
                for(int j=i+1;j<(len-1);j++)
                {
                    if(Constants.options[fLoc[i]].toLowerCase().
                            compareTo(Constants.options[fLoc[j]].toLowerCase())>0)
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
*/
    private byte[] sort(byte[] fLoc, byte appcatalog){
        int len;
        if(null != fLoc && (len=fLoc.length)>0){
            byte temp = -1;
            for(int i=appcatalog;i<(len-1);i++)
            {
                for(int j=i+1;j<(len-1);j++)
                {
                    if(Constants.options[fLoc[i]].toLowerCase().
                            compareTo(Constants.options[fLoc[j]].toLowerCase())>0)
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
     * Method to handle the different type of operations based the requested option.
     *
     *
     * @param itemId - Variable will conatin the option item Id.It may be Null.
     * @param itemName - Variable will contain the option item name.It may be Null.
     * @param optId - Variable will contain the requested Option Id.(Not Null)
     **/

    public void handleOptionsSelected(int itemId, String itemName,byte optId) {
        if(9 == optId){                                     // Opt - Index of Inbox option.
            ObjectBuilderFactory.GetKernel().loadInbox();
        } else if (0 == optId) {                            //Opt - Index of Settings Option.
            load(2,true);
        } else if (1 == optId) {                            //Opt - Index of Advance Option.
            loadManageAppMenu(itemName);
        } else if (4 == optId) {                            //Opt - Index of About ShartHand Option.
            handleAboutShartHand();
        } else if (5 == optId) {                            //Opt - Index of Exit ShartHand Option.
            handleExitShartHand();
        } else if (22 == optId || 36 == optId) {            //Opt 22 - Index of Back Option ,Opt 36 - Index of ShartHand Home Option.
            loadPreviousMenu();
        } else if(3 == optId){                              //Opt - Index of GetInfo Option.
            loadGetInformation(itemName);
        }
//        else if(10 == optId){                             //Opt - Index of SmartSearch (Future Purpose).
//            handleSmartSearchOption();
//        }
        else if(13 == optId){                             // Opt - Index of Favorites Option.
            load(8,true);
        }
//        else if(18 == optId){                             // Opt - Index of Delete Option.
//            fQueryId = itemId;
//            fQueryName = itemName;
//            loadMessageBox(7,Constants.popupMessage[13],6,Constants.headerText[12]);
//        }
        else if(48 == optId){ // Opt - Index of Message Counter
            loadMessageCounterScreen(1);
        }
        else if(Constants.SIZE <= optId){                 // Opt - Index of Widget Catalog Option.
            handleProfileCatalog(optId);
        }
    }

    /**
     * Method to handle the favorites Menu item.
     *
     * @param itemName - Variable will contain the Selected favorites Menu items name.
     **/

//    private void handleFavourite(String itemName){
//        if(null != itemName){
//            int index = itemName.indexOf(":");
//            if(index>-1){
//                String pName = itemName.substring(0,index);
//                itemName = itemName.substring(index+1);
//                pName = ObjectBuilderFactory.GetKernel().getProfileLocation(pName);
//                ObjectBuilderFactory.GetKernel().loadSequenceProfile(pName,itemName);
//            }
//        }
//    }

    /**
     * Method to handle the Favorites Menu deletion.
     *
     * @param itemId - Variable will contain the Menu item Id.
     * @param itemName - Variable will contain the Menu item Name.
     **/
//    private void handleFavoriteDeleteOption(){
//        if(null != fQueryName){
//            int index = fQueryName.indexOf(":");
//            if(index>-1){
//                String pName = fQueryName.substring(0,index);
//                parser.deleteFavouriteProfile(pName,fQueryName,fQueryName.substring(index+1));
//                ObjectBuilderFactory.GetKernel().removeMenuItem(fQueryId,fQueryName);
//            }
//        }
//    }

    /**
     *  Method to Make a Searching (Future purpose)
     **/
//    private void handleSmartSearchOption(){
//        currst=7;
//        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_ENTRY);
//        GetEntryResponseDTO _responceDto = new GetEntryResponseDTO();
//        _responceDto.setLeftOptionText((byte)22);
//        _responceDto.setSecondaryHeaderText(Constants.headerText[6]); // Entry SmartSearch Value
//        _responceDto.setEntryType((byte)2);
//        _responceDto.setIsEntryBoxEnabled(true);
//        _responceDto.setMinChar((short)1);
//        PresenterDTO.setLOptByte((byte)-1);
//        ObjectBuilderFactory.GetKernel().displayScreen(_responceDto,true);
//        _responceDto = null;
//    }

    /**
     * Method to Save the Selected Profile informations and Load the Advance option Menus.
     *
     * @param profileName - Variable will contain the Focused Profile Name.(Not Null).
     **/
    private void loadManageAppMenu(String profileName) {
        SMSProfileHeader header = ObjectBuilderFactory.GetKernel().getProfileHeader(profileName);
        if (null != header)
            parser.setProfileLocation(header.getProfileLocation(),header.getName(),header.getPId());
        header = null;
        load(1,true);
    }

    /**
     * Method to Handle the Advanced Menu items.
     *
     * @param itemId - Variable will contain the selected item Id.(Not Null)
     *
     **/
    private void handleManageAppMenuSelected(int itemId, String itemName) {
        if(itemId == 0){
            //Bug 13724
            String appName = Constants.advanceEditOptionsForSMS[3];
            int index = -1;
            if((index = (appName.indexOf(Constants.appendText[0]+" ")))>-1){
                appName = appName.substring((Constants.appendText[0]+" ").length());
            }
            loadGetInformation(appName);

          //  loadGetInformation(Utilities.replace(Constants.advanceEditOptionsForSMS[3] , Constants.appendText[0]+" " , ""));
        } else if(Constants.advanceEditOptionsForSMS[0].compareTo(itemName) == 0){
            String appName = parser.deleteWidgetRecords();
            appName = ObjectBuilderFactory.GetKernel().getAppAbbervation(appName);
            GlobalMemorizeVariable.clearChatUnReadCount(appName);
            initialize(0,true);
        } else if(Constants.advanceEditOptionsForSMS[1].compareTo(itemName) == 0){                             // Delete the selected Profile
             loadMessageBox(7,Constants.popupMessage[14], 5,Constants.headerText[12]);
        } else if(Constants.advanceEditOptionsForSMS[2].compareTo(itemName) == 0){                             // Delete All the profiles
            loadMessageBox(7,Constants.popupMessage[15], 2,Constants.headerText[12]);
        }
    }

    /**
     * Method to Delete the Selected Profile.
     *
     **/
    private void handleDeleteProfile() {
        //loadDisplayScreen(Constants.headerText[14]);
        String pName = parser.deleteProfile();
        if(null != pName) {
            ObjectBuilderFactory.GetKernel().removeProfileHeader(pName);
            //#if VERBOSELOGGING
            //|JG|Logger.loggerError("Deleting APP Name "+pName); //bug 11535
            //#endif
            loadDisplayScreen(Constants.headerText[14]);//bug 0011535
            ObjectBuilderFactory.getControlChanel().addProfileDelete(parser.getProfileId());
        }
    }

    /**
     * Method to Load the Previous Menu Based on the State.
     *  <li> 0. Profile </li>
     *  <li> 1. Advance Menu </li>
     *  <li> 2. Settings Menu </li>
     *  <li> 3. Security Menu </li>
     *  <li> 4. New Pin entry Screen </li>
     *  <li> 5. Conformatiion Pin Screen </li>
     *  <li> 6. Proifle Information Screen </li>
     *  <li> 7. SmartSearch Screen </li>
     *  <li> 8. Favorite Screen </li>
     *  <li> 9. Message Counter Screen </li>
     *  <li> 10. Log Menu </li>
     **/
    private void loadPreviousMenu() {
        if (0 == currst || 2 == currst || 7 == currst || 8 == currst) {
            load(0,true);
        } else if (1 == currst) {
            if(null != parser)
                parser.deInitialize();
            load(0,true);
        } else if (3 == currst || 10 == currst || 9 == currst) {
            load(2,true);
        } else if (4 == currst || 5== currst) {
            load(3,true);
        } else if(6 == currst){
            setPresenterDto();
            load(1,true);
        }
    }

    /**
     * Method to Load the Widgets to Download
     *
     * @param optText - Variable will contain the Index of the selected Option.
     **/
    private void handleProfileCatalog(byte optText) {
        String name = String.valueOf(Constants.options[optText]);
        String fLoc = ObjectBuilderFactory.GetKernel().getProfileLocation(name);
        if(null == fLoc){
            fLoc = RecordManager.getFeatureAppName(name);
            if(null != fLoc)
                ObjectBuilderFactory.GetKernel().loadProfile(null, fLoc, true,false);
            else
                loadMessageBox(4, Constants.appendText[14]+" "+ name +" "+Constants.popupMessage[16]+" "+ name +" " +
                                      Constants.popupMessage[17], 0,null);
        }
    }

    /**
     * Method to Load the View Screen to read the information about the profile.
     *
     * @param profileName - Variable will contain the Profile Name.(Not Null)
     *
     **/
    private void loadGetInformation(String profileName) {
        SMSProfileHeader pH = ObjectBuilderFactory.GetKernel().getProfileHeader(profileName);
        //CR 4924
        if(null == pH)
        {
            pH = ObjectBuilderFactory.GetKernel().getFeatureApp();
            if(null != pH){
                if(pH.getName().compareTo(profileName) != 0)
                    pH = null;
            }
        }
        //bug id 4053(If widget not have the discription, client should display atlease version number)
        if (null != pH && (pH.getVersion()!= null || null != pH.getPDes())){//&& null != pH.getPDes()) { // ithaya

            currst = 6;
            //Enable the Last selected Widget Name CR 2929
            //ObjectBuilderFactory.GetKernel().setLastSelectedWidgetName(pH.getName());
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_VIEW);
            PresenterDTO.setHdrLogo(RecordManager.getLogoImageName(pH.getProfileLocation()));
            PresenterDTO.setHdrtxt(pH.getName());
            //PresenterDTO.setBgImage(RecordManager.getBackImage(pH.getProfileLocation()));
            PresenterDTO.setLOptByte((byte)-1);
            ViewSmsResponseDTO _responceDto = new ViewSmsResponseDTO();
            _responceDto.setSenderName(Constants.appendText[0]+ " " + profileName + " "+Constants.appendText[8]);
            String ver = pH.getVersion();
            if(!ver.startsWith("v"))
                ver ="v"+ver;
            if(pH.getPDes() != null)
               _responceDto.setMessage(pH.getPDes()+"\n"+ver);
            else
                _responceDto.setMessage(ver);
            _responceDto.setLeftOptionText((byte)22);
            ObjectBuilderFactory.GetKernel().displayScreen(_responceDto,false);
            _responceDto =null;
            pH = null;
        }
    }

    private void loadMessageCounterScreen(int type){
        String mCounter = null;
         ViewSmsResponseDTO _responceDto = new ViewSmsResponseDTO();
        if(type == 1){
            mCounter = parser.getMsgCount();
            if(mCounter.length() == 0)
                mCounter = Constants.appendText[15];
            _responceDto.setSenderName(Constants.headerText[7]);
        } else {
            mCounter = parser.getDataCount();
            if(mCounter.length() == 0){
                mCounter = Constants.appendText[32];
            }
            _responceDto.setSenderName(Constants.settingsMenu[10]);
        }
        currst = 9;
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_VIEW);
        PresenterDTO.setLOptByte((byte)-1);
        PresenterDTO.setHdrtxt(Constants.aName);
        PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
        _responceDto.setMessage(mCounter);
        _responceDto.setLeftOptionText((byte)22);
        ObjectBuilderFactory.GetKernel().displayScreen(_responceDto, false);
        _responceDto = null;
    }

    /**
     * Method will Load the Message Box to read about the ShartHand.
     **/
    private void handleAboutShartHand() {
        String versionNumber = ChannelData.getAboutTxt();
        if(null == versionNumber || versionNumber.length() == 0){
            versionNumber = ObjectBuilderFactory.GetKernel().getVersionNumber(false);
            //CR 5984
            if(Constants.popupMessage[19].length() > 0){
                versionNumber = Constants.popupMessage[18] + versionNumber + ". "+Constants.popupMessage[19];
            } else {
                versionNumber = Constants.popupMessage[18] + "\nV"+ versionNumber+".";
            }
        }
        loadMessageBox(4, versionNumber, 0,Constants.appendText[0] +" "+ChannelData.getClientName()); //CR 8071
    }

    /**
     * Method to Load the MessageBox with the requested Information ,Status and type.
     *
     * @param menuType - Variable will contain the Message type.
     * @param message - Variable will contain the Information to show in the MessageBox.
     * @param msgType - Variable will contain the Message State.Based on this, Message response will handle.
     *
     **/
    private void loadMessageBox(int msgType,String msg,int msgst,String hText) {
        this.msgst =(byte)msgst;
        ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg,hText);
    }

    private void loadDisplayScreen(String hText){
        currst = 9;
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_DISPLAY);
        PresenterDTO.setLOptByte((byte)-1);
        DisplayResponseDTO displayDTO = new DisplayResponseDTO();
        displayDTO.setDisplayImage((byte)1);
        displayDTO.setSecondaryHeaderText(hText);
        displayDTO.setDisplayTime((short)1);
        displayDTO.setIsDATWait(true);
        ObjectBuilderFactory.GetKernel().displayScreen(displayDTO, true);
        displayDTO = null;
    }

    /**
     * Method will handle the different type of operations based on the response.
     * The operations will find based on the State.
     *
     * @param status - Variable will contain the true or false.
     *
     **/
    public void handleMessageBox(boolean status) {
        byte tMsgst = msgst;
        msgst = 0;
        if (status) {                   // If the Status is true, Left Option will handle.
            if (1 == tMsgst) {          // Error Messsage Exception Raised.
                if (0 != tMsgst) {
                    ObjectBuilderFactory.GetKernel().reLaunchApplication();
                } else {
                    handleExitShartHand();
                }
            } else if (2 == tMsgst) {    //Delete All Profiles.
                loadDisplayScreen(Constants.headerText[14]);
                parser.deInitialize();
//                parser.clearMyShortcuts();
                ObjectBuilderFactory.GetKernel().removeAllProfiles();
                //load(0);
            } else if(4 == tMsgst){     // Aggrement message option " Accept " will handle.
                isAccept = true;
                LoadRegistrationForm();
            } else if(3 == tMsgst){ // Welcome Message Left option will handle.
                if(Settings.isIsShowMode()){
                    showModeMessage();
                } else {
                    if(!isMasterAppLaunch || !isMasterAppToLoad()){ //CR 4941
                        loadNoWidgetMessage();
                    }
                    isMasterAppLaunch = false;
                }
            } else if(5 == tMsgst){
                handleDeleteProfile();
            }
//            else if(6 == tMsgst){
//                 handleFavoriteDeleteOption();
//                 fQueryId = -1;
//                 fQueryName = null;
//            }
            else if(7 == tMsgst){ //Upload the Log file
                ObjectBuilderFactory.GetKernel().startHttpProcess(5, null, null, null);
            } else if(8 == tMsgst){ //Apps Error "Now" handle
                ObjectBuilderFactory.GetKernel().startHttpProcess(1, null, null, null);
            } else if(9 == tMsgst){
                if(isAccept){
                    isStartApp = false;
                }
                loadAppErrorMessage(!isAccept);
                isAccept = false;
            } else if(10 == tMsgst){
                Logger.clearLog();
                loadMessageBox(0, Constants.popupMessage[11], 0, Constants.headerText[13]);
            } else if(11 == tMsgst) { //CR 12191
                if(!isMasterAppLaunch || !isMasterAppToLoad()){ //CR 4941
                    loadNoWidgetMessage();
                }
                isMasterAppLaunch = false;
            }//CR 12191
        } else {
            if(3 == tMsgst){ // Welcome Message Right option will handle.
                Settings.setIsWelcome(status);
                if(Settings.isIsShowMode()){
                    showModeMessage();
                } else {
                    if(!isMasterAppLaunch || !isMasterAppToLoad()){ //CR 4941
                        loadNoWidgetMessage();
                    }
                    isMasterAppLaunch = false;
                }
            } else if(4 == tMsgst){      // Aggrement message option " Cancel " will handle.
                ObjectBuilderFactory.GetKernel().clearSetting();
                ObjectBuilderFactory.GetKernel().UnRegisterConnection();
                ObjectBuilderFactory.GetKernel().unLoad();
            } else if(11 == tMsgst){ //CR 12191
                Settings.setIsShowMode(false);
                if(!isMasterAppLaunch || !isMasterAppToLoad()){ //CR 4941
                    loadNoWidgetMessage();
                }
                isMasterAppLaunch = false;
            } //CR 12191
//            else if(6 == tMsgst){
//                 fQueryId = -1;
//                 fQueryName = null;
//            }
        }
    }

    private boolean isMasterAppToLoad(){
        boolean isMasterApp = false;
        String masterAppName = ChannelData.getMasterAppName();
        if(null != masterAppName && null != (masterAppName = RecordManager.getMasterAppName(masterAppName))){
            isMasterApp = true;
            ObjectBuilderFactory.GetKernel().loadProfile(ChannelData.getMasterAppName(), masterAppName, false,false);
        }
        return isMasterApp;
    }

    private void loadNoWidgetMessage(){
        if(null == ObjectBuilderFactory.GetKernel().getSMSProfilesLoaded()){
            if(RecordManager.isAppCatalogDownloaded())
                loadMessageBox(4, Constants.popupMessage[20], 9,Constants.headerText[8]);
            else {
                if(isAccept){
                    loadMessageBox(4, Constants.popupMessage[21], 9,Constants.headerText[8]);
                } else loadMessageBox(4, Constants.popupMessage[22], 9,Constants.headerText[8]);
            }
        } else {
            loadAppErrorMessage(!isAccept);
            isAccept = false;
        }
    }

   private void LoadRegistrationForm(){
        if(RecordManager.createAggrementSignedFile(ObjectBuilderFactory.GetKernel().getVersionNumber(true))){
            ObjectBuilderFactory.GetKernel().setSendReceive();
            ObjectBuilderFactory.GetKernel().startHttpProcess(1, null,  null, null);
        } else {
            ObjectBuilderFactory.GetKernel().UnRegisterConnection();
            ObjectBuilderFactory.GetKernel().unLoad();
        }
    }

    /**
     * Method to Display the Profiles.
     *
     * @throws  loadProfileException -  Close the Application
     **/
    private void loadProfileItems() {
        try {
            currst = 0;
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.LWUOT_FRENDENT_PROFILE);
            SMSProfileHeader[] pHeader = ObjectBuilderFactory.GetKernel().getSMSProfilesLoaded();
            LWEATActionDTO lWEATActionDTO = new LWEATActionDTO();
//            ProfileResponseDTO _responseDto = new ProfileResponseDTO();
            SMSProfileHeader moreApp = ObjectBuilderFactory.GetKernel().getFeatureApp();
            if(null != moreApp){
                //CR 4924
                lWEATActionDTO.setOptIDEsc(getOptionsMenu(Settings.getAppCatalogName()));
                //lWEATActionDTO.setOptTextEsc(Utilities.GetOptions(getOptionsMenuString(Settings.getAppCatalogName())));
                lWEATActionDTO.setEscapeText(new String[]{moreApp.getName()});
                lWEATActionDTO.setEntryBoxEnabled(false);
                //CR 4924
//                if(isGrid)
//                    pLogo[pLogo.length-1] = moreApp.getTildIcon();
//                else pLogo[pLogo.length-1] = moreApp.getLaunchIcon();
//                lWEATActionDTO.setE(new String[]{moreApp.getTildIcon()});
                lWEATActionDTO.setEscapeIDs(new int[1]);
            }

            if (null != pHeader && pHeader.length > 0) {
                lWEATActionDTO = setNameAndLogoLink(lWEATActionDTO, pHeader);
                lWEATActionDTO.setOptID(getOptionsMenu("Name"));
                //lWEATActionDTO.setOptText(Utilities.GetOptions(getOptionsMenuString("Name")));
            } else if(!Settings.isIsGrid()){
                //CR 4924
                lWEATActionDTO.setListItems(new String[]{String.valueOf(Constants.options[38])});
                lWEATActionDTO.setListItemIds(new int[1]);
                //lWEATActionDTO.setOptText(Utilities.GetOptions(getOptionsMenuString(Settings.getAppCatalogName())));
                lWEATActionDTO.setOptID(getOptionsMenu(Settings.getAppCatalogName()));
            } 
            
//            _responseDto.setMsgUnReadCount(ObjectBuilderFactory.GetKernel().getUnReadMsgCount());
//            _responseDto.setLeftOptionText((byte)9);
            lWEATActionDTO.setBackID((byte)9);
            ObjectBuilderFactory.GetKernel().displayScreen(lWEATActionDTO,false); // Transfer the control to display the Profile Presenter Screen DTO
            lWEATActionDTO = null;
        } catch (Exception loadProfileException) {
             Logger.loggerError("load profiles "+ loadProfileException.toString());
             ObjectBuilderFactory.GetKernel().unLoad();
        }
    }

    /**
     * Method
     **/
    private LWEATActionDTO setNameAndLogoLink(LWEATActionDTO lWEATActionDTO,
            SMSProfileHeader[] pHeader){
        byte subCount = 1;
        boolean isGrid = Settings.isIsGrid();
        if(isGrid){ //CR 9408
            subCount = 0;
        }

        int count = pHeader.length;
        String[] pName = new String[count+subCount];
        String[] pLogo = new String[count+subCount];
        int[] unreadChatCount = new int[count+subCount];

        if(!isGrid){ //CR 9408
            pName[0] = String.valueOf(Constants.options[38]);
        }

        for (int i = 0,j=subCount; i < count; i++,j++) {
            try{
                pName[j] = pHeader[i].getName();
                if(isGrid){
                    pLogo[j] = pHeader[i].getTildIcon();
//                    int count = ObjectBuilderFactory.GetKernel().reSyncChatCount(pHeader[i].getAbbreviation(), pHeader[i].getName());
                    unreadChatCount[j] = ObjectBuilderFactory.GetKernel().reSyncChatCount(pHeader[i].getAbbreviation(), pHeader[i].getName());//GlobalMemorizeVariable.getChatUnreadCount(pHeader[i].getAbbreviation());
                } else {
                    pLogo[j] = pHeader[i].getLaunchIcon();
                }
            }catch(Exception e){

            }
        }
        
        lWEATActionDTO.setListItems(pName);
        lWEATActionDTO.setListItemIds(new int[pName.length]);
        lWEATActionDTO.setListImages(pLogo);
        lWEATActionDTO.setGridLayout(isGrid);
//        _responseDto.setChatUnReadCount(unreadChatCount);
        return lWEATActionDTO;
    }

    /**
     * Method to Load the Screen to Get the input from the User.
     *
     * @param headerText - Variable will contain the Text to display in the header.
     * //CR 10682
     **/
//    private void loadGetEntry(String headerText) {
//        PresenterDTO.setLOptByte((byte)-1);
//        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_ENTRY);
//        GetEntryResponseDTO _responseDto = new GetEntryResponseDTO();
//        _responseDto.setSecondaryHeaderText(headerText);
//        _responseDto.setMask("****");
//        _responseDto.setEntryType((byte) 0);
//        _responseDto.setMaxValue(9999);
//        _responseDto.setMaxChar((byte) 4);
//        _responseDto.setMinChar((byte) 4);
//        if (!Constants.headerText[0].equals(headerText)) { //Confirm Enter PIN
//            currst =4;
//            _responseDto.setLeftOptionText((byte)22);     //Opt - Index of Back Option
//        } else currst = 5;
//        ObjectBuilderFactory.GetKernel().displayScreen(_responseDto,true);
//        _responseDto = null;
//    }

    /**
     * Method to Load the Error MessagBox with the Exception and User Message.
     *
     *
     * @param exception - Variable will contain the Excepton Informations.
     * @param message - Variable will contain the User Error Message.
     **/
    public void loadErrorMessageScreen(Exception exception, String message) {
		Logger.loggerError("APPHandler -> "+exception.toString());
        loadMessageBox(4, message, 1,"Error");
    }

    /**
     * Method to Close the Application
     *
     **/
    private void handleExitShartHand() {
        if(null != parser)
            parser.deInitialize();
        parser = null;
        ObjectBuilderFactory.GetKernel().unLoad();
    }

    /**
     * Method to remove the objects.
     **/
    public void deInitialize(){
        if(null != parser)
            parser.deInitialize();
        parser = null;
//        securityPin = null;
        currst = 0;
        Runtime.getRuntime().gc();
    }
}


