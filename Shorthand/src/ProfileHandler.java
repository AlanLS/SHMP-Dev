/*
 * SMSHandler.java
 *
 * Created on October 3, 2007, 1:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import generated.Build;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Administrator
 */
public class ProfileHandler {

    private ProfileParser pparser = null;
    private EntryParser eparser = null;
    //private QueryParser qparser = null;
    /** Object to hold the Record Parser */
    private RecordParser rParser = null;
    //private GlobalEATParser globalParser = null;
    //private GlobalMemorizeVariable globalMemorize =null;
    //private String global = "GLOBAL";
    private String globalMem = "memGLOBAL";
    private Timer lBTimer = null;
    //Message Receive Delay Notification Time
    private Timer mRDNTimer = null;
    // Current State
    private byte currst;
    /**
     *  1 - Error state (Exception Raised)
     *  2 - Payfor Service warning
     *
     */
    private byte msgst;
    // Varibale to be created by the instance Object
    // private ManageSchedules manageScheduler = null;
    //
    private Sequence[] seqArray;
    private AdData adData = null;
    /** Constant Variable To hold The Prompt For Input */
    private boolean promptForInput = false;
    /** Constant Varibale To hold The sequenceshortcutSelected Bool */
    //private boolean isSeqSel = false;

    /* Varibale To Hold The Display The saveQuery Option To Be Display */
    //private boolean storeIndex = false;
    //Profile Header Object
    private ProfileHeader header = null;
    //Profile location
    private String pLoc = null;
    //Message SendAction Object
    private SMSSendAction _saction = null;
    //Selected Keyword Table Value
    private String[] kwValue = null;
    //Received Message IdMessage id
    private String msgid = null;
    //Preshable Entry
    private Hashtable perEntry = null;
    private boolean isSelect = true;
    //Is Advertisement retrieve boolean once the category advertisement is get or not get the
    //boolean is set false
    private boolean isAdget = true;
    //Store the Loob Back send Message Arguments
    //private String[] lBMsg = null;
    Message loopBackMessage = null;
    private boolean isFeature = false;
    /** Variable to hold the Url String for the Url Action*/
    private String url = null;
    private String eval = null;
    private int iid = -1;
    private String sC = null;
    private DownloadAction doAction = null;
    private boolean isSent = false;
    private boolean isDownloadDATWait = false;
    private String telNumber = null;
    //private boolean isReplaced = false;
    private String[] receivedMsg = null;
    private String[] sequenceNumber = new String[2];
    private int multiPartCount = 1;
    private String maxCount = null;
    //<-- CR 13617
    private boolean isRegister = false;
    // CR 13617 -->
    private boolean isSmsWaitBeforeLoad = false;
    private String chatName = null;
    private String oldValue = null;
    private String newValue = null;
    //CR 14789
    private String imageUploadId = "[shmsisdn]";

    //
    private String groupAndShoutJoined = "[shjoined]";

    private String addContact = "SHContactAdd";

    private String contactType = "SHContactType";

    /**
     * Method to retrieve the profiles list
     *
     * @return SMSProfileHeader[] Array of SMSProfileHeader object
     */
    public SMSProfileHeader[] getProfileList(boolean isNewApp) {
        if (pparser == null) {
            pparser = new ProfileParser();
        }
        SMSProfileHeader[] headers = pparser.getNewProfileList(isNewApp);
        pparser = null;
        int len;
        if (null != headers && (len = headers.length) > 0) {
            for (int i = 0; i < len; i++) {
                if (headers[i].getPUsg() > 0 || headers[i].getQCount() > 0 || headers[i].getAcount() > 0) {
                    ObjectBuilderFactory.getControlChanel().addProfileUsage(headers[i].getPId(), headers[i].getPUsg(), headers[i].getQCount(), headers[i].getAcount());
                }
            }
        }
        return headers;
    }

    //CR 4924
    public SMSProfileHeader getFeatureApp(String name) {
        SMSProfileHeader headers = null;
        if (null != name) {
            if (pparser == null) {
                pparser = new ProfileParser();
            }
            headers = pparser.getFeatureApp(name);
            pparser = null;
        }
        return headers;
    }


//    /**
//     * Future purpose
//     **/
//    public SMSProfileHeader[] getNewProfileList() {
//        if (pparser == null) {
//            pparser = new ProfileParser();
//        }
//        SMSProfileHeader[] headers = pparser.getNewProfileList(true);
//        pparser = null;
//        return headers;
//    }
    public void loadChat(String floc, String chatName, String chatId, String messagePlus, boolean isNotChatLoad) {
        if (loadParsers(floc, isNotChatLoad)) { //bug 12767
            pLoc = floc;
            setCurrentPresenter(floc);
            if (header.getChatId() > -1) {
                setChatRecordValue(chatName, chatId, messagePlus);
                //storeHistory(chatName, chatId);
                loadAction(pparser.getAction(header.getChatId()));
            }
        }
    }

    private void setChatRecordValue(String chatName, String chatId, String messagePlus) {
        rParser.propagateRecord("chat", "friendname", chatName, "chat");
        rParser.propagateRecord("chat", "friendID", chatId, "chat");
        rParser.propagateRecord("chat", "plususer", messagePlus, "chat");
        StoreSequnece(0, "chat.friendname", chatName, false, false, " ", true);
        StoreSequnece(0, "chat.friendID", chatId, false, false, " ", true);
        StoreSequnece(0, "chat.plususer", messagePlus, false, false, " ", true);
    }

    private String[] getContactsItem(String scPrefix, String displayFormat){
        String[] values = null;
        if(scPrefix.compareTo("contfav") == 0){
            values = Contacts.getContacts(false, displayFormat,0);
        } else if(scPrefix.compareTo("contall") == 0){
            values = Contacts.getContacts(true, displayFormat, 0);
        } else if(scPrefix.compareTo("joined") == 0){
            values = Contacts.getContacts(true, displayFormat,3);
        } else if(scPrefix.compareTo("grpall") == 0){
            values = Contacts.getContacts(true, displayFormat,1);
        } else if(scPrefix.compareTo("grpown") == 0){
            values = Contacts.getContacts(false, displayFormat, 1);
        } else if(scPrefix.compareTo("shtall") == 0){
            values = Contacts.getContacts(true, displayFormat, 2);
        } else if(scPrefix.compareTo("shtown") == 0){
            values = Contacts.getContacts(false, displayFormat, 2);
        }
        return values;
    }

    //CR 14672, 14675, 14787
    public void refreshContactScreen(){
        if(2 == currst){
            if(pparser.getType() == ProfileTypeConstant.ENTRYACTION){
                EntryAction entryAction = (EntryAction)pparser.getCurrentObjject();
                if(entryAction.isIsMSRefresh() || entryAction.isIsMsContacts()){
                    String[] values = getContactsItem(entryAction.getScprefix(), entryAction.getRDFormat());
                    entryAction = null;
//                    values = appendEscapeItems(values);
//                    int[] itemId = appendEscapeItemId();
                    ObjectBuilderFactory.GetKernel().refreshContacts(values, Contacts.getContactIndex());
                    values = null;
//                    itemId = null;
                }
            }
        }
    }

    //CR 9774
//    private void storeHistory(String name,String id){
//        String[] values = getRecordNameAndField("history.friendname");
//        rParser.propagateRecord(values[0], values[1], name, values[0] + values[2]);
//        StoreSequnece(0, "history.friendname", name, false, false, " ", true);
//        values = getRecordNameAndField("history.friendID");
//        rParser.propagateRecord(values[0], values[1], id, values[0] + values[2]);
//        StoreSequnece(0, "history.friendID", id, false, false, " ", true);
//    }
    public boolean isNotLoadedChat(String floc, String chatName, String chatId, String messagePlus) {
        boolean isLoaded = true;
        if (null != pLoc && pLoc.compareTo(floc) == 0) {
            setChatRecordValue(chatName, chatId, messagePlus);
            //storeHistory(chatName, chatId);
            if (currst == 3) {
                DisplayAction displayAction = (DisplayAction) pparser.getCurrentObjject();
                if (displayAction.getDispimage() == ProfileTypeConstant.Display.DISPLAY_CHAT) {
                    loadAction(displayAction);
                } else if (header.getChatId() > -1) {
                    loadAction(pparser.getAction(header.getChatId()));
                }
            } else if (header.getChatId() > -1) {
                loadAction(pparser.getAction(header.getChatId()));
            }
            isLoaded = false;
        }
        return isLoaded;
    }

    /**
     *
     */
//    public void executeSequence(String floc, String seqName) {
//        try {
//            if(loadParsers(floc)){
//                pLoc = floc;
//                setCurrentPresenter(floc);
//                pparser.changeInitialScreenId();
////                loadQuery(seqName);
//            }
//        } catch (Exception e) { }
//    }
    /**
     *
     **/
    public void loadProfile(String pfLoc, boolean isDownLoad, boolean isNotChatLoad) throws Exception {
        if (loadParsers(pfLoc, isNotChatLoad)) { //bug 12767
            isFeature = isDownLoad;

            if (isDownLoad) {
                if (null != header.getScode() && header.getScode().length > 0) {
                    ObjectBuilderFactory.GetKernel().registerNewNumers(header.getScode()[0], header.getName(), header.getMSCF(), header.getAbbreviation());
                } else {
                    ObjectBuilderFactory.GetKernel().registerNewNumers(ChannelData.getShortcode(), header.getName(), header.getMSCF(), header.getAbbreviation());
                }
                if (null != Settings.getAppCatalogName() && Settings.getAppCatalogName().compareTo(header.getName()) == 0) //CR 7426
                {
                    isFeature = false;
                }
            }
            //Set the Profile Folder Location
            if (!isDownLoad) {
                pLoc = pfLoc;
            }
            //Set the Presenter Dto for the Header text and Logo
            setCurrentPresenter(pLoc);
            //Load the Initial Menu the Initial Menu Object is MenuItemList
            loadAction(pparser.getInitialMenu());

            //Check the Profile Having the Pay For Information
            //bug id 4522
            //bug id 13463
            if (null != header && null != header.getPf() && pparser.isPayForService()) {
                loadMessageBox(9, replaceVariableData(header.getPf(), null,
                        true, true, true, true), 2, null);
            }
        }
    }

    private boolean loadParsers(String floc, boolean isNotChatLoad) { //bug 12767
        boolean isNotLoad = false;
        try {
            //Create the Profile Parser Instance
            pparser = new ProfileParser();
            //Get the Profile Header Object and Initialize the Initial Menu
            header = pparser.initialize(floc, isNotChatLoad);

            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Entering App Name : " + header.getName());
            //#endif

            //Create the Query parser Instance and Store the Profile Folder Location
//            qparser = new QueryParser();
            //Create the Entry parser Instance and Store the Profile Folder Location
            eparser = new EntryParser();

            //globalMemorize = new GlobalMemorizeVariable();
            //globalParser = new GlobalEATParser();

            /** Create the Record Parser Instance with the Profile folder location */
            rParser = new RecordParser();

            if (null != header) {
                isNotLoad = true;
                eparser.setCommonEntry(header.getName(), header.getCScprefixName());

                /** Set the Profile Records */
                rParser.setRecords(header.getRecords(), header.getROption(), header.getName());

//                qparser.setquerysName(header.getName());
            }
        } catch (Exception e) {
            Logger.loggerError("ProfileHandler-> Loading " + e.getMessage());
            LoadErrorMessageScreen(e, Constants.errorMessage[10]);
        }
        return isNotLoad;
    }

    /**
     *
     * @param floc
     */
    private void setCurrentPresenter(String floc) {
        if (null == floc) {
            PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
        } else {
            PresenterDTO.setBgImage(RecordManager.getBackImage(RecordManager.getRecordStoreName(floc)));
            PresenterDTO.setHdrLogo(RecordManager.getLogoImageName(RecordManager.getRecordStoreName(floc)));
        }
        PresenterDTO.setHdrtxt(header.getPhtxt());
        //CR 6740
        if (isFeature) {
            PresenterDTO.setLOptByte((byte) -1);
        } else {
            PresenterDTO.setLOptByte((byte) 41);
        }

        Constants.options[34] = (header.getName() + " " + Constants.appendText[1]);//.toCharArray();
    }

    /**
     * Method to Load the Initial menu Of the profile
     *
     * @param list
     * @param isim
     *            isInitialMenu
     */
    private void loadMenu(MenuItemList list) {
        //CR 14733
        isContactRefresh();

        isSent = false;
        currst = 1;
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.LWUOT_FRENDENT_MENU);
        LWEATActionDTO lWEATActionDTO = new LWEATActionDTO();
        lWEATActionDTO.setEntryBoxEnabled(false);

//        String adText = null;
//
//        if (list.isPlacead()) {
//            adText = getAdText();
//            if (null != adText) {
//                response.setBannerStyle(adData.getStyle());
//            }
//        }

        if (pparser.isInitialMenu()) {
            seqArray = null;
            rParser.clearInitialObjects();
            //isSeqSel = false;
//            storeIndex = false;
            msgid = null;
            receivedMsg = null;
//            response.setSeqlist(qparser.getQueryNames());
//            if (null == adText) {
//                adText = header.getBtxt();
//                response.setBannerStyle((byte) 1);
//            }
        }
//        response.setBannerText(adText);

        lWEATActionDTO.setSecHdrText(replaceVariableData(list.getSechdrtxt(), null,
                true, true, true, true));


        if (list.isBack()) {
            lWEATActionDTO.setBackID((byte)22);//Opt - Index of Back Option
        } else if (null != list.getLOString()) {
            Constants.options[2] = list.getLOString();//.toCharArray();
            lWEATActionDTO.setBackID((byte)2);
        }
        //CR 14789
//        response.setProfileImageType(GlobalMemorizeVariable.getProfileMode());
//        if(response.getProfileImageType()>-1){
//            response.setUploadId(replaceVariableData(imageUploadId, null, false, true, true, true));
//        }

        //CR 6740
        if (!isFeature) {
            lWEATActionDTO.setOptID(GetOptions(0, null));
            //lWEATActionDTO.setOptText(Utilities.GetOptions(GetOptions(0, null)));
            lWEATActionDTO.setOptIDEsc(GetOptions(-2, null));
           // lWEATActionDTO.setOptTextEsc(Utilities.GetOptions(GetOptionsString(-2, null)));
        }

        int[] stylelist = list.getStylelist();
        String[] namelist = list.getItemnamelist();

        int len;
        if (list.isIscitemName()) {
            if (null != namelist && (len = namelist.length) > 0) {
                for (int i = 0; i < len; i++) {
                    namelist[i] = replaceVariableData(namelist[i], Constants.appendText[20],
                            true, true, false, true);
                }
            }
        }

        lWEATActionDTO.setListItems(namelist);
        lWEATActionDTO.setListItemIds(list.getItemidlist());
        if(null != pparser.getEscapeMenu()){
            lWEATActionDTO.setEscapeText(pparser.getEscapeMenu());
            lWEATActionDTO.setEscapeIDs(getEscapeIdInt(pparser.getEscapeMenu().length));
        }
//        String[] temp = pparser.getEscapeMenu();
//        if (null != temp) {
//            if (null != namelist) {
//                String[] tempname = namelist;
//                len = tempname.length + temp.length;
//                namelist = new String[len];
//                System.arraycopy(tempname, 0, namelist, 0, tempname.length);
//                System.arraycopy(temp, 0, namelist, tempname.length, temp.length);
//                int[] tempid = idlist;
//                idlist = new int[len];
//                System.arraycopy(tempid, 0, idlist, 0, tempid.length);
//                int[] tempstyle = new int[len];
//                System.arraycopy(stylelist, 0, tempstyle, 0, stylelist.length);
//                stylelist = tempstyle;
//                tempname = null;
//                tempstyle = null;
//                tempid = null;
//            } else {
//                namelist = temp;
//                idlist = new int[temp.length];
//                stylelist = new int[temp.length];
//            }
//            temp = null;
//        }

//        response.setItemnamelist(namelist);
//        response.setItemidlist(idlist);
//        response.setStylelist(stylelist);
        ObjectBuilderFactory.GetKernel().displayScreen(lWEATActionDTO, true);
        namelist = null;
//        idlist = null;
//        stylelist = null;
//        response = null;
//        adText = null;
    }

    /**
     * Method to handle the selection of menu item or selection of Entry action
     * or display action. This method handles the input based on the current
     * state.
     *
     * @param id
     *            This parameter represents the menu id if the current state is
     *            menu state. It is that action id in case of the entry state
     *            and display state.
     *
     * @param itemId
     *            Menu Item Id in case of menu state, entry value in case of get
     *            entry state.
     * @param itemName
     *            Menu Item name or Sequence name in case of menu state or entry
     *            shortcut name in case of Get Entry state.
     */
    public void handleItemSelection(int itemId, String itemName) {
        if (1 == currst) { // Menu State
            handleMenuItemSelection(itemId, itemName);
        } else if (2 == currst) { // entry state
            handleEntrySelection(itemName, itemId);
        } else if (3 == currst) { // Display state
            handleDisplaySelection(itemId, itemName);
        }
//        else if (4 == currst) { // Save Query State
//            saveQuerySequence(itemName);
//        }
    }

    /**
     * Method To Handle The Selected Menu Item.
     *
     * Menu Item Is Sequence Menu Item To Be Handle TheSequenceShortcutMenu
     * Method
     *
     * @param menuId
     *            Menu Id
     * @param itemId
     *            Item Id
     * @param itemName
     *            Item Name
     */
    private void handleMenuItemSelection(int itemid, String itemName) {

        try {
//            if (itemid < 0) {
//                if (null != itemName && itemName.length() > 0) {
//                    loadQuery(itemName);
//                }
//            } else

            if (itemid == 0) {
                handleEscapeText(itemName);
            } else {
                itemName = IsMenuStore(itemid, itemName);
                if (null != itemName) {
                    if (!isGotoWidgetName(itemName)) {
                        loadNextAction(pparser.getItemNextActionId(itemid));
                        loadMessageBox(4, Constants.appendText[14] + " " + itemName + " " + Constants.popupMessage[16] + " " + itemName + " "
                                + Constants.popupMessage[17], 7, null);
                    }
                } else {
                    loadNextAction(pparser.getItemNextActionId(itemid));
                }
            }
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[3]);
            Logger.loggerError("HandleMenuSelection" + itemName);
        }
    }

    private String IsMenuStore(int itemid, String itemName) {
        MenuItemList list = (MenuItemList) pparser.getCurrentObjject();
        String widgetName = null;
        String smsValue = null;
        int[] items = list.getItemidlist();
        int len = items.length;
        for (int i = 0; i < len; i++) {
            if (items[i] == itemid) {
                smsValue = list.getSmsvaluelist()[i];
                if (null != smsValue) {
                    smsValue = replaceVariableData(smsValue, null, false, true, true, true);
                } else {
                    smsValue = itemName;
                }
                widgetName = list.getGotoWidgetName()[i];
                break;
            }
        }
        // Setting memorized value
        String memvar = list.getMemvarname();
        if (null != memvar) {
            if (memvar.startsWith(globalMem)) {
                GlobalMemorizeVariable.add(memvar, itemName);
            } else {
                pparser.setMenuMemorizedValue(memvar, itemName, smsValue);
            }
            StoreSequnece(itemid, list.getName(), smsValue, false, false, null, false);
        } else // Storing smsvalue associated with the menu name
        {
            StoreSequnece(itemid, list.getName(), smsValue, false, false, null, true);
        }
        memvar = null;
        list = null;
        items = null;
        smsValue = null;

        return widgetName;
    }

    /**
     * Method to load the next action from the current state
     *
     * @param itemid
     *            Item Id to load the action association with that particular
     *            item selection, if null it is considered as an action
     */
    private void loadNextAction(int itemid) {
//		//#if VERBOSELOGGING
//  //|JG|Logger.loggerError("Loading Action File Byte Location "+itemid);
//        //#endif //11801
        if (itemid > 0) {
            Object _nextaction = pparser.getNextAction(itemid);
            loadAction(_nextaction);
            _nextaction = null;
        } else if (itemid == -2) {
            handleExitProfile();
        } else {
            loadMessageBox(16, Constants.popupMessage[29], 0, null);
        }
    }

    /**
     *
     */

    private void loadAction(Object _nextaction) {
        if (null != _nextaction) {
            int type = pparser.getType();
//            //#if VERBOSELOGGING
//            //|JG|Logger.debugOnError("Currently Loading screen Type= "+ type );
//            //#endif //11801
            if (type == ProfileTypeConstant.MENU) {
                MenuItemList _list = (MenuItemList) _nextaction;
                loadMenu(_list);
            } else if (type == ProfileTypeConstant.ENTRYACTION) {
                EntryAction entry = (EntryAction) _nextaction;
                loadEntry(entry);
            } else if (type == ProfileTypeConstant.SMSSENDACTION) {
                SMSSendAction send = (SMSSendAction) _nextaction;
                loadSmsSend(send, false);
            } else if (type == ProfileTypeConstant.DISPLAYACTION) {
                DisplayAction display = (DisplayAction) _nextaction;
                loadDisplay(display);
            } else if (type == ProfileTypeConstant.CALLACTION) {
                CallAction call = (CallAction) _nextaction;
                loadCallAction(call);
            } else if (type == ProfileTypeConstant.URLACTION) {
                UrlAction urlA = (UrlAction) _nextaction;
                loadUrlAction(urlA);
            } else if (type == ProfileTypeConstant.GENERALACTION) {
                GeneralAction general = (GeneralAction) _nextaction;
                loadGeneralAction(general);
            } else if (type == ProfileTypeConstant.DOWNLOADACTION) {
                DownloadAction down = (DownloadAction) _nextaction;
                loadDownlaodAction(down);
            }
//             //#if VERBOSELOGGING
//             //|JG|Logger.debugOnError("Load Action END" );
//             //#endif //11801
        }
    }

    /**
     *  Method to load the Download Action
     *  Parameter DownloadAction Object
     */
    private void loadDownlaodAction(DownloadAction down) {
        // if(down.getType().compareTo(Constants.appendText[8])== 0){
        loadNextAction(down.getGotoId());
        if (null == doAction) {
            doAction = down;
            isDownloadDATWait = true;
            //CR12815
            startDownload();
            //loadMessageBox(5, Constants.popupMessage[1], 6, Constants.headerText[9]);
        } else {
            loadMessageBox(4, Constants.popupMessage[30], 0, Constants.headerText[24]); //bu no 4897
        }
        //}
    }

    /**
     * Method to start the Downloading process, When the user accept the "Data Charges Appaly" Messagebox
     */
    private void startDownload() {
        if (null != doAction) {
//                if(null != Settings.getPhoneNumber()){ //CR 8353
            if (null != header.getUrl() && null != doAction.getwId()) {
                isDownloadDATWait = false;
                ObjectBuilderFactory.GetKernel().startHttpProcess(3, header.getUrl(), doAction.getwId(), header.getName());
            }
//                } else {
//                    isDownloadDATWait = true;
//                    doAction = null;
//                    ObjectBuilderFactory.getControlChanel().sendProvenanceAction(true);
//                    loadMenu((MenuItemList)pparser.getInitialMenu());
//                    //bug id 5502
//                    loadMessageBox(4, Constants.popupMessage[9], 0, Constants.headerText[8]);
//                }
        }
    }

    private boolean restoreHistoryRecord(String scprefix) {
        boolean isBold = false;
        if (scprefix.compareTo("history") == 0) {
            rParser.deleteAllRecord("history");
            isBold = true;
            String[] record = ChatHistoryHandler.getRecentHistroyUserList(header.getName());
            if (null != record) {
                //CR 13059
                int count = record.length;
                for (int i = 0; i < count; i++) {
                    System.out.println(record[i]);
                    rParser.updateNewRecordValue("history", record[i], "history");

                }
            }
        }
        return isBold;
    }

    private boolean isContactRefresh(){
       //CR 14733
        boolean isRefresh = GlobalMemorizeVariable.isRefreshContacts();
        
        if (!isRefresh) {
            isRefresh = !Contacts.isSHGroupOrShoutContactsRefresh((byte)0);
        }
        if (isRefresh) {
            currst = 3;
            //<-- Cr 13681
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_DISPLAY);
            DisplayResponseDTO displayResponseDto = new DisplayResponseDTO();
            displayResponseDto.setIsDATWait(true);
            displayResponseDto.setSecondaryHeaderText("Loading Contacts, please wait...");
            ObjectBuilderFactory.GetKernel().displayScreen(displayResponseDto, false);
            ObjectBuilderFactory.getControlChanel().sendContacts();
            // CR 13681 -->
        }
        return isRefresh;
    }

    /**
     * Method to Load the Get Entry Item Values
     *
     * @param actionId
     *            Selected Load Action
     */
    private void loadEntry(EntryAction action) {
        String[] value = null;
        boolean isRefresh = isContactRefresh();
        if (isRefresh || action.isIsMsContacts()) { //CR 13695
            value = getContactsItem(action.getScprefix(), action.getRDFormat());
        }

        currst = 2;
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("Loading entry action");
//        //#endif //11801
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.LWUOT_FRENDENT_ENTRY);

//        GetEntryResponseDTO _response = new GetEntryResponseDTO();
        LWEATActionDTO lWEATActionDTO = new LWEATActionDTO();
        try {

            //CR 6740
            lWEATActionDTO.setSecHdrText(replaceVariableData(action.getEName(),
                    null, true, true, true, true));

            //    String[] value = null;
            if (action.isIsfixed()) { //bug id 4790
                String scPrefix = action.getScprefix();
                if (action.isIsRecord()) {
                    value = getRecordNameAndField(scPrefix, true);
                    if (null != value) {
                        if (value.length > 1) {
                            value = eparser.getEntryValue(value[1]);
                            // value = eparser.getEntryValue(value[0]+"."+value[1]); // bug id 4677
                        } else {
                            value = rParser.getRecordValues(scPrefix, value[0], action.getRDFormat());
                        }
                    }
                } else {
                    int index = scPrefix.indexOf(".");
                    if (index > -1) {
                        scPrefix = scPrefix.substring(0, index);
                    }
                    if (action.isPerentry()) {
                        value = getPershableEntry(scPrefix);
                    } else {
                        value = eparser.getEntryValue(scPrefix);
                    }
                }
            }

            int[] itemId = null;
            if(null != value){
                if(action.isIsMsContacts()){
                    itemId = Contacts.getContactIndex();
                } else {
                    itemId = getItemId(value.length);
                }
            }

            if(null != value){
                lWEATActionDTO.setListItems(value);
                lWEATActionDTO.setListItemIds(itemId);
                lWEATActionDTO.setOptID(GetOptions(0, null));
                //lWEATActionDTO.setOptText(Utilities.GetOptions(GetOptionsString(0, null)));
            }

            if(null != pparser.getEscapeMenu()){
                lWEATActionDTO.setEscapeText(pparser.getEscapeMenu());
                lWEATActionDTO.setEscapeIDs(appendEscapeItemIdInt());
                lWEATActionDTO.setOptIDEsc(GetOptions(-2, null));
                //lWEATActionDTO.setOptTextEsc(Utilities.GetOptions(GetOptionsString(-2, null)));
            }
//            lWEATActionDTO.setOptTextEsc(GetOptions(-1));

            if(!isFeature){
                if (action.isBack()) {
                    lWEATActionDTO.setBackID((byte)22);//Opt - Index of Back Option
                } else if (null != action.getLOString()) {
                    Constants.options[2] = action.getLOString();//.toCharArray();
                    lWEATActionDTO.setBackID((byte)2);
                }
            }

            lWEATActionDTO.setMaxChar(action.getMaxchar());
            lWEATActionDTO.setMinChar(action.getMinchar());
            lWEATActionDTO.setMinValue(action.getMinValue());
            lWEATActionDTO.setEntryBoxEnabled(action.isIsebRemove());
            //lWEATActionDTO.setEntryBoxConstraint(getEntryType(action.getEtype()));
            lWEATActionDTO.setEntryBoxConstraint(action.getEtype());
            //
            lWEATActionDTO.setMaxValue(action.getMaxvalue());
            ObjectBuilderFactory.GetKernel().displayScreen(lWEATActionDTO, isSelect);
            value = null;
            promptForInput = false;
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[11]);
        }
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("Entry action Loaded");
//        //#endif //11801
    }
/*
    private String getEntryType(byte entryType){
        String entryString = null;
        if(entryType == 0){
            entryString = "NUMERIC";
        } else if(entryType == 1){
            entryString = "ALPHA";
        } else if(entryType == 2){
            entryString = "ALPHANUMERIC";
        } else if(entryType == 3){
            entryString = "DECIMAL";
        } else if(entryType == 4){
            entryString = "DOLLARCENTS";
        } else if(entryType == 5){
            entryString = "DATE";
        } else if(entryType == 6){
            entryString = "PHONENUMBER";
        }
        return  entryString;
    }
*/
    private byte[] appendEscapeItemId(){
        String[] temp = pparser.getEscapeMenu();
        if(null != temp){
            return getEscapeId(temp.length);
        }
        return null;
    }

    private int[] appendEscapeItemIdInt(){
        String[] temp = pparser.getEscapeMenu();
        if(null != temp){
            return getEscapeIdInt(temp.length);
        }
        return null;
    }
//    private int[] setScContacts(String[] item){
//        int count = item.length;
//        int totalCount = 0;
//        int[] itemId = new int[count];
//        for(int i=0;i<count;i++){
//            if(item[i].indexOf("^+^")>-1){
//                item[totalCount] = item[i];
//                itemId[totalCount++] = i;
//            }
//        }
//        if(totalCount<count){
//            String[] newItem = new String[totalCount];
//            int[] newId = new int[totalCount];
//            System.arraycopy(item, 0, newItem, 0, totalCount);
//            item = newItem;
//            newItem = null;
//
//            System.arraycopy(itemId, 0, newId, 0, totalCount);
//            itemId = newId;
//            newId = null;
//        }
//        return itemId;
//    }

//    private void storeContacts(){
//        Contacts.setContactDetails();
//        String[] name = Contacts.getName();
//        String[] number = Contacts.getNumber();
//        int count=0;
//        if(null != name && (count=name.length)>0){
//            for(int i=0;i<count;i++){
//                rParser.propagateRecord("contact", "name", name[i], "contact");
//                rParser.propagateRecord("contact", "cell", number[i], "contact");
//              //  Logger.debugOnError("NAME  :   " + name[i]);
//              //  Logger.debugOnError("NUM   :   "+ number[i]);
//            }
//        }
//    }
    /**"
     *
     * @param count
     * @return
     */
    private int[] getItemId(int count) {
        int[] itemId = new int[count];
        for (int i = 0; i < count; i++) {
            itemId[i] = i;
        }
        return itemId;
    }

    /**
     *
     * @param count
     * @return
     */
    private byte[] getEscapeId(int count) {
        byte[] itemId = new byte[count];
        for (int i = 0; i < count; i++) {
            itemId[i] = (byte)-1;
        }
        return itemId;
    }

     private int[] getEscapeIdInt(int count) {
        int[] itemId = new int[count];
        for (int i = 0; i < count; i++) {
            itemId[i] = (byte)-1;
        }
        return itemId;
    }
    
    
    /**
     *
     * @param format
     * @return
     */
    private short getLetterCount(String format, boolean isChat) {
        //CR 13074
        if (null != format && !Settings.isIsGPRS()) {
            format = replaceVariableData(format, null, false, true, true, true);
            String serText = null;
            while (null != (serText = getSearchString(format, "[", "]"))) {
                format = Utilities.replace(format, "[" + serText + "]", "");
            }
            //9545
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Query format count:"+ format.length());
            //#endif
            if (isChat) {
                return (short) (format.length());//dont add count for SQ number
            } else {
                return (short) (format.length() + 3); //3 For sequence Number / bug id 6577
            }
        }
        return -1;
    }

    /**
     *
     **/
    private String[] getPershableEntry(String scname) {
        String[] value = null;
        if (null != perEntry && perEntry.containsKey(scname)) {
            value = (String[]) perEntry.get(scname);
        }
        return value;
    }

    private void deletePershableEntry(String scName, String value) {
        if (null != perEntry && perEntry.containsKey(scName)) {
            if (null == value) {
                perEntry.remove(scName);
            } else {
                String[] values = (String[]) perEntry.get(scName);
                int count = 0;
                if (null != values && (count = values.length) > 0) {
                    for (int i = 0; i < count; i++) {
                        if (value.compareTo(values[i]) == 0) {
                            String[] temp = new String[count - 1];
                            System.arraycopy(values, 0, temp, 0, i);
                            System.arraycopy(values, i + 1, temp, i, (count - (i + 1)));
                            perEntry.put(scName, temp);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     *
     **/
    private void addPershableEntry(String scname, String value) {
        String[] evalue = new String[]{value};
        if (null == perEntry) {
            perEntry = new Hashtable();
        } else if (perEntry.containsKey(scname)) {
            String[] temp = (String[]) perEntry.get(scname);
            int len = temp.length;
            for (int i = 0; i < len; i++) {
                if (0 == value.compareTo(temp[i])) {
                    return;
                }
            }
            evalue = new String[len + 1];
            System.arraycopy(temp, 0, evalue, 0, len);
            evalue[len] = value;
            perEntry.remove(scname);
        }
        perEntry.put(scname, evalue);
        evalue = null;
    }

    /**
     * Method To Handle The Get Entry Item Selected
     *
     * @param actionId
     *            Action Id
     * @param entryValue
     *            Entry Value
     * @param entryName
     *            Entry Name
     */
    private void handleEntrySelection(String evalue, int itemId) {
        try {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Entry select handle Stated\nCurrent Screen Type ="+ pparser.getType() +" \nSelected Item Id "+ itemId);
            //#endif
            EntryAction action = (EntryAction) pparser.getCurrentObjject();
            if (itemId == -1) {
                String memvar = action.getMvarname();
                if (null != memvar && memvar.startsWith(globalMem)) {
                    GlobalMemorizeVariable.add(memvar, evalue);
                }
                StoreSequnece(itemId, action.getScprefix(), evalue, !action.isIsfixed(), false, action.getSep(), true);
                Object _obj = getEscapeAction(evalue);
                if (null != _obj) {
                    loadAction(_obj);
                }
                _obj = null;
            } else {
                iid = itemId;
                eval = evalue;
                sC = action.getScprefix();
                if (action.getMask() != null && action.getMask().length() > 0) {
                    if (action.isIsfixed()) {
//                        if(sC.startsWith(global)){
//                            if(!globalParser.getIsPasswordSaved(sC)){
//                                loadMessageBox(7, Constants.popupMessage[31], 4, null);
//                            }else {
//                                checkIsPasswordSave(globalParser.getPasswordsaveStatus(sC));
//                                action = null;
//                            }
//                        } else {
                        if (!eparser.getIsPasswordSaved(sC)) {
                            loadMessageBox(7, Constants.popupMessage[31], 4, null);
                        } else {
                            checkIsPasswordSave(eparser.getPasswordsaveStatus(sC));
                            action = null;
                        }
//                        }
                    } else {
                        checkIsPasswordSave(false);
                    }
                } else {
                    checkIsPasswordSave(true);
                }
            }
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[12]);
        }
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("Entry select handle end");
//        //#endif //11801
    }

    private void updatePasswordStatus(boolean status) {
        eparser.setPasswordSave(sC, status);
        sC = null;
        checkIsPasswordSave(status);
    }

    /** Check the Status to save the password **/
    private void checkIsPasswordSave(boolean status) {
        EntryAction action = (EntryAction) pparser.getCurrentObjject();
        isEntryStore(eval, iid, status);
        eval = null;
        if (null != action.getGotoWidgetName()) {
            if (!isGotoWidgetName(action.getGotoWidgetName())) {
                loadNextAction(action.getGotoid());
                loadMessageBox(4, Constants.appendText[14] + " " + action.getGotoWidgetName() + " " + Constants.popupMessage[16] + " " + action.getGotoWidgetName() + " "
                        + Constants.popupMessage[17], 7, null);
            }
        } else {
            loadNextAction(action.getGotoid());
        }
        action = null;
    }

    private String[] getElementName(String scPrefix){
        String[] elements = null;
        if(scPrefix.compareTo("contfav") == 0 || scPrefix.compareTo("contall") == 0){
            elements = new String[]{"friendname","friendID","plususer", "friendIMG", "friendstatus"};
        } else if(scPrefix.compareTo("joined") == 0){
            elements = new String[]{"joinedname","joinedID","joinedIMG","joinedowner","joinedownerID"};
        } else if(scPrefix.compareTo("grpall") == 0 || scPrefix.compareTo("grpown") == 0){
            elements = new String[]{"groupname","groupID","groupIMG","groupowner","groupownerID"};
        } else if(scPrefix.compareTo("shtall") == 0 || scPrefix.compareTo("shtown") == 0){
            elements = new String[]{"shoutname","shoutID","shoutIMG","shoutowner","shoutownerID"};
        }
        return elements;
    }

    /**
     * Method to store the Entry value for the give entry Name
     * @param evalue
     * @param itemId
     * @param isPassEntrySave
     * @return
     */
    private String isEntryStore(String evalue, int itemId, boolean isPassEntrySave) {
        EntryAction action = (EntryAction) pparser.getCurrentObjject();
        String memvar = action.getMvarname();
        String scPrefix = action.getScprefix();

        if (null != memvar) {
            if (memvar.startsWith(globalMem)) {
                GlobalMemorizeVariable.add(memvar, evalue);
            } else {
                pparser.setEntryMemorizeValue(memvar, evalue, action.getMask());
            }
        }

        if (action.isIsRecord()) {
            String[] values = null;
            if (action.isIsMSRefresh() || action.isIsMsContacts()) {
                byte entryIndex = 1;
                if(action.getScprefix().indexOf("con") == 0){
                    entryIndex = 0;
                }
                evalue = Contacts.getSelectedIndexContacts(itemId,entryIndex);
                values = Utilities.split(evalue, "^");
                String[] elements = getElementName(action.getScprefix());
                StoreSequnece(action.getId(), action.getScprefix()+"."+elements[0], values[0], !action.isIsfixed(), false, action.getSep(), true);
                StoreSequnece(action.getId(), action.getScprefix()+"."+elements[1], values[1], !action.isIsfixed(), false, action.getSep(), true);
                StoreSequnece(action.getId(), action.getScprefix()+"."+elements[2], values[2], !action.isIsfixed(), false, action.getSep(), true);
                StoreSequnece(action.getId(), action.getScprefix()+"."+elements[3], values[3], !action.isIsfixed(), false, action.getSep(), true);
                StoreSequnece(action.getId(), action.getScprefix()+"."+elements[4], values[4], !action.isIsfixed(), false, action.getSep(), true);
            } else {
                values = getRecordNameAndField(scPrefix, true);
                if (null != values) {
                    if (values.length < 2) {
                        rParser.setSelectedRecordValue(scPrefix, itemId);
                    } else {
                        if (action.isIsfixed() && !promptForInput || !action.isIsfixed() && promptForInput) {
                            if (!action.isPerentry() || isPassEntrySave) {
                                eparser.storeEntrySC(evalue);
                            }
                        }
                        rParser.addRecord(values[0], values[1], evalue, values[0] + values[2]);
                        if (null != action.getMulValue()) {
                            StoreSequnece(action.getId(), action.getMulValue(), evalue, !action.isIsfixed(), true, action.getSep(), true);
                        } else {
                            StoreSequnece(action.getId(), action.getScprefix(), evalue, !action.isIsfixed(), false, action.getSep(), true);
                        }
                    }
                }
            }
        } else {
            int index = scPrefix.indexOf(".");
            if (index > -1) {
                scPrefix = scPrefix.substring(0, index);
            }
            if ((action.isIsfixed() && !promptForInput || !action.isIsfixed() && promptForInput)) {
                if (action.isPerentry() || !isPassEntrySave) {
                    addPershableEntry(scPrefix, evalue);
                } else {
                    // if(scPrefix.startsWith(global)){
                    //   globalMemorize.add(scPrefix, evalue); //globalParser.storeEntrySC(evalue);
                    //} else {
                    eparser.storeEntrySC(evalue);
                    //}
                }
            }

            if (null != action.getMulValue()) {
                StoreSequnece(action.getId(), action.getMulValue(), evalue, !action.isIsfixed(), true, action.getSep(), true);
            }
            StoreSequnece(action.getId(), action.getScprefix(), evalue, !action.isIsfixed(), false, action.getSep(), true);

        }

        evalue = action.getGotoWidgetName();
        promptForInput = false;
        action = null;

        return evalue;
    }

    /**
     *
     * @param scPrefix
     * @return
     */
    private String[] getRecordNameAndField(String scPrefix, boolean isRecordCheck) {
        String[] value = null;

        if (null != header.getROption() && null != scPrefix) {
            int index = scPrefix.indexOf(":");
            int sIndex = scPrefix.indexOf(".");
            if (index > -1) {
                if (sIndex < 0) {
                    value = new String[]{scPrefix.substring(0, index)};
                } else if (index > sIndex) {
                    value = new String[]{scPrefix.substring(0, sIndex), scPrefix.substring(sIndex + 1, index), scPrefix.substring(index)};
                } else {
                    value = new String[]{scPrefix.substring(0, index), scPrefix.substring(sIndex + 1), scPrefix.substring(index, sIndex)};
                }
            } else if (sIndex > -1) {
                value = new String[]{scPrefix.substring(0, sIndex), scPrefix.substring(sIndex + 1), ""};
            } else {
                value = new String[]{scPrefix};
            }

            if (isRecordCheck && null != value && header.getROption().indexOf(value[0] + "|") < 0) {
                value = null;
            }
        }
        return value;
    }

    private void loadChatScreen(DisplayAction action) {
        try {
            currst = 3;
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDENT_CHAT);
            //CR 6740
            if (isFeature) {
                PresenterDTO.setLOptByte((byte) -1);
            } else {
                PresenterDTO.setLOptByte((byte) 41);
            }

            ChatResponseDTO _responseDto = new ChatResponseDTO();
            _responseDto.setAbbervation(header.getAbbreviation());

            if (action.isPlacead() && null != getAdText()) {
                _responseDto.setBannerText(getAdText());
                _responseDto.setBannerStyle(adData.getStyle());
            }
            _responseDto.setChatId(replaceVariableData("[chat.friendID]",
                    null, true, false, true, true));
            //CR 12316
            _responseDto.setChatName(replaceVariableData("[chat.friendname]",
                    null, true, false, true, true));

            //CR 14112
            _responseDto.setPlusUser(replaceVariableData("[chat.plususer]",
                    null, true, false, true, true));

            _responseDto.setAppName(header.getName());

            if (null != action.getBuddyName()) {
                _responseDto.setBuddyName(replaceVariableData(action.getBuddyName(),
                        null, true, false, true, true));
            }

            //if (!isSeqSel) {
            if (null != action.getLOString()) {
                Constants.options[2] = action.getLOString();//.toCharArray();
                _responseDto.setLeftOptionText((byte) 2);
            } else if (action.isBack()) {
                _responseDto.setLeftOptionText((byte) 22);//Opt - Index of Back Option
            }
            //}

            //CR 14326
//            _responseDto.setQueryFormat(action.getMqtype());

            _responseDto.setMItems(pparser.getEscapeMenu());

            _responseDto.setLetterCount(getLetterCount(replaceVariableData(action.getMqtype(),
                    null, true, false, true, true), true));

            ObjectBuilderFactory.GetKernel().displayScreen(_responseDto, false);
            _responseDto = null;
        } catch (Exception e) {
        }
    }

    private void loadUserProfileScreen(DisplayAction displayAction){
        currst = 3;
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDENT_USER_PROFILE);
        UserProfileDTO userProfileDTO = new UserProfileDTO();

        if (displayAction.isPlacead() && null != getAdText()) {
            userProfileDTO.setBannerText(getAdText());
            userProfileDTO.setBannerstyle(adData.getStyle());
        }

        userProfileDTO.setmItems(pparser.getEscapeMenu());

        userProfileDTO.setSecondaryHeader(replaceVariableData(displayAction.getOtxt(),
                    null, true, false, true, true));

        //CR 14789
        userProfileDTO.setProfileImageType(GlobalMemorizeVariable.getProfileMode());
        if(userProfileDTO.getProfileImageType()>-1){
            userProfileDTO.setUploadId(replaceVariableData(imageUploadId, null, false, true, true, true));
        }

        String userId =  replaceVariableData(displayAction.getInfo(),
                    null, true, false, true, true);
        byte recordIndex = 0;
        userProfileDTO.setDisplayType(displayAction.getDispimage());
        if(displayAction.getDispimage() != ProfileTypeConstant.Display.DISPLAY_PROFILE){
            recordIndex = 1;
            if(userId.charAt(0) == 'S'){
                userProfileDTO.setDisplayType(ProfileTypeConstant.Display.DISPLAY_MYPROFILE);
            } 
        }
        //CR 14743
        String[] temp = Contacts.getGridIndex(userId, recordIndex);
        if(null != temp){
            userProfileDTO.setGridThumbNailIndex(Integer.parseInt(temp[0]));
        }

        userProfileDTO.setUserPhoneNumber(userId);

        userId = displayAction.getInfo().substring(1,displayAction.getInfo().length()-1);
        String[] recordName = getRecordNameAndField(userId, false);
        String[] elements = getElementName(recordName[0]);

        userProfileDTO.setName(replaceVariableData("["+recordName[0]+"."+elements[0]+"]",
                    null, true, false, true, true));
        byte index = 2;
        userProfileDTO.setInformation("");
        if(recordName[0].startsWith("con")){
            index = 3;
            userId= replaceVariableData("["+recordName[0]+".plususer]", null, true, false, true, true);
            if(null != userId && userId.trim().compareTo("+") == 0){
                userProfileDTO.setPlusUser(Constants.appendText[39]);
            } else {
                userProfileDTO.setPlusUser(Constants.appendText[40]);
            }
        } else {
            //CR 14802
            userId= replaceVariableData("["+recordName[0]+"."+elements[3]+"]", null, true, false, true, true);
            if(null != userId && userId.trim().compareTo("*") == 0){
                if(userProfileDTO.getUserPhoneNumber().charAt(0) == 'S'){
                    userProfileDTO.setPlusUser(Constants.appendText[43]);
                } else {
                    userProfileDTO.setPlusUser(Constants.appendText[41]);
                }
            } else {
                userId= replaceVariableData("["+recordName[0]+"."+elements[elements.length-1]+"]", null, true, false, true, true);
                if(userProfileDTO.getUserPhoneNumber().charAt(0) == 'S'){
                    userProfileDTO.setPlusUser(Constants.appendText[44]);
                    //CR 14802
                    userId = Constants.appendText[46]+" "+Contacts.getGroupOrShoutOwnerName(userId, index);
                } else {
                    userProfileDTO.setPlusUser(Constants.appendText[42]);
                    //CR 14802
                    userId = Constants.appendText[45]+" "+Contacts.getGroupOrShoutOwnerName(userId, index);
                }
                userProfileDTO.setInformation(Utilities.markPhoneNumber(userId));
            }
        }
        userId = replaceVariableData("["+recordName[0]+"."+elements[index]+"]",
                    null, true, false, true, true);
        if(null != userId && userId.trim().length()>0){
            userProfileDTO.setImageVersion(userId);
        }

        if (null != displayAction.getLOString()) {
            Constants.options[2] = displayAction.getLOString();//.toCharArray();
            userProfileDTO.setLopttxt((byte) 2);
        } else if (displayAction.isBack()) {
            userProfileDTO.setLopttxt((byte) 22);//Opt - Index of Back Option
        }
        
        ObjectBuilderFactory.GetKernel().displayScreen(userProfileDTO, false);
        userProfileDTO = null;
    }

    //BUG 14832
    public byte getRecordIndexToUpdateProfileScreenImage(String msisdn){
        byte recordIdex = -1;
        if(currst == 3 && null != msisdn){
            DisplayAction action = (DisplayAction)pparser.getCurrentObjject();
            if(ProfileTypeConstant.Display.DISPLAY_PROFILE == action.getDispimage()){
                recordIdex = 0;
            } else if(ProfileTypeConstant.Display.DISPLAY_GSPROFILE == action.getDispimage()){
                recordIdex = 1;
            }
            if(recordIdex>-1){
                String currentMsisdn = replaceVariableData(action.getInfo(),
                    null, true, false, true, true);
                if(currentMsisdn.compareTo(msisdn) != 0){
                    recordIdex = -1;
                }
            }
        }
        return recordIdex;
    }

    /**
     *
     */
    private void loadDisplay(DisplayAction action) {
        //CR 14733
        isContactRefresh();

        msgid = null;
        receivedMsg = null;
        currst = 3;
        if (ProfileTypeConstant.Display.DISPLAY_CHAT == action.getDispimage()) {
            loadChatScreen(action);
        } else if (ProfileTypeConstant.Display.DISPLAY_INFO == action.getDispimage()) {
            msgid = "Info";
            //bug id 4854
            loadViewScreen(action, new String[]{replaceVariableData(action.getInfo(),
                        null, true, true, false, true), null}, null);
        } else if(ProfileTypeConstant.Display.DISPLAY_PROFILE == action.getDispimage() ||
                ProfileTypeConstant.Display.DISPLAY_GSPROFILE == action.getDispimage() ||
                ProfileTypeConstant.Display.DISPLAY_MYPROFILE == action.getDispimage()){
            //CR 14727
            loadUserProfileScreen(action);
        } else {
            isSmsWaitBeforeLoad = true;
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_DISPLAY);
            DisplayResponseDTO _response = new DisplayResponseDTO();
            //CR 14789
            _response.setProfileImageType(GlobalMemorizeVariable.getProfileMode());
            if(_response.getProfileImageType()>-1){
                _response.setUploadId(replaceVariableData(imageUploadId, null, false, true, true, true));
            }
            //CR 6740
            if (isFeature) {
                PresenterDTO.setLOptByte((byte) -1);
            } else {
                PresenterDTO.setLOptByte((byte) 41);
            }

            try {

                if (action.isPlacead() && null != getAdText()) {
                    _response.setBannerText(getAdText());
                    _response.setBannerStyle(adData.getStyle());
                }

                // if (!isSeqSel) {
                if (action.isBack()) {
                    _response.setLeftOptionText((byte) 22);//Opt - Index of Back Option
                } else if (null != action.getLOString()) {
                    Constants.options[2] = action.getLOString();//.toCharArray();
                    _response.setLeftOptionText((byte) 2);
                }
                // }
                if (isSent) {// CR 11976
                    _response.setIsDATWait(true);
                    _response.setIsAppWait(true);
                }

                _response.setDisplayImage(action.getDispimage());

                _response.setDisplayTime(action.getDisptime());

                _response.setMItems(pparser.getEscapeMenu());

                String outputText = replaceVariableData(action.getOtxt(), Constants.appendText[20],
                        true, true, false, true);
                _response.setSecondaryHeaderText(outputText);
                if (!isSmsWaitBeforeLoad) {
                    _response.setIsSmsWaitBeforeLoad(true);
                }
                ObjectBuilderFactory.GetKernel().displayScreen(_response, false);
                _response = null;
                isSmsWaitBeforeLoad = false;
            } catch (Exception exception) {
                LoadErrorMessageScreen(exception, Constants.errorMessage[13]);
            }
        }
        isSent = false;
    }

    /**
     *
     **/
    private void loadViewScreen(DisplayAction daction, String[] msg, String msgId) {
        try {
            currst = 3;
            if (null != msg) {
                ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_VIEW);
                ViewSmsResponseDTO _responseDto = new ViewSmsResponseDTO();
                //CR 14789
                _responseDto.setProfileImageType(GlobalMemorizeVariable.getProfileMode());
                if(_responseDto.getProfileImageType()>-1){
                    _responseDto.setUploadId(replaceVariableData(imageUploadId, null,
                            false, true, true, true));
                }
                _responseDto.setAppName(header.getName());
                //CR 6740
                if (isFeature) {
                    PresenterDTO.setLOptByte((byte) -1);
                } else {
                    PresenterDTO.setLOptByte((byte) 41);
                }

                if (daction.isPlacead() && null != getAdText()) {
                    _responseDto.setBannerText(getAdText());
                    _responseDto.setBannerStyle(adData.getStyle());
                }

                if (null != daction.getInfo()) {
                    _responseDto.setSenderName(replaceVariableData(daction.getOtxt(), null,
                            true, false, true, true));
                    if (daction.isIsUrl()) {
                        msg[0] = Utilities.markUrl(msg[0]);
                    } else {
                        //Bug id 11659
                        _responseDto.setIsUrlHeighlight(daction.isIsUrl());
                    }

                    //Bug id 9292
                    if (daction.isIsNumber()) {
                        msg[0] = Utilities.markPhoneNumber(msg[0]);
                    } else {
                        //Bug id 9272
                        _responseDto.setIsPhoneNumberEnabled(daction.isIsNumber());
                    }
                    //Bug id 9272
                    _responseDto.setMessage(msg[0]);
                } else if (null != _saction) {
                    _responseDto.setSenderName(replaceVariableData(daction.getOtxt(), null,
                            true, true, true, true));
                    if (_saction.isChkforurl()) {
                        msg[0] = Utilities.markUrl(msg[0]);

                    }
                    if (_saction.isChkforno()) {
                        msg[0] = Utilities.markPhoneNumber(msg[0]);
                        msg[0] = Utilities.replace(msg[0], "<|", "$*<|");
                        msg[0] = Utilities.replace(msg[0], "|>", "|>*$");
                    }

                    //Bug no 11341
                    _responseDto.setIsUrlHeighlight(_saction.isChkforurl());
                    _responseDto.setIsPhoneNumberEnabled(_saction.isChkforno());
                    Utilities.setCurrentTime();
                    _responseDto.setMessage(getMatchTextMsg(msg[0]));
                    Utilities.updateMessage("Time for Match text= ");

                }

                _responseDto.setFLineText(msg[1]);
                // _responseDto.setInternalLB(isStoredMsg);//cr121hema
                _responseDto.setMItems(pparser.getEscapeMenu());

                // if (isStoredMsg) {//cr121hema
                if (null != daction.getLOString()) {
                    Constants.options[2] = daction.getLOString();//.toCharArray();
                    _responseDto.setLeftOptionText((byte) 2);
                } else if (daction.isBack()) {
                    _responseDto.setLeftOptionText((byte) 22);//Opt - Index of Back Option
                }
                // }

                _responseDto.setMesssageId(msgId);
                ObjectBuilderFactory.GetKernel().displayScreen(_responseDto, false);
                _responseDto = null;
            }
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[14]);
        }

    }

    /**
     *
     **/
    private void loadCallAction(CallAction caction) {

        try {
            loadNextAction(caction.getGoId());
            invokeCall(replaceVariableData(caction.getCallNum(), null,
                    false, true, true, true));
        } catch (Exception e) {
        }
    }

    private void invokeCall(String callNum) {
        try {
            if (null != callNum && callNum.indexOf("[") == -1) {
                int index = callNum.indexOf(",");
                if (index > -1) {
                    callNum = callNum.replace(',', 'p');
                    callNum = "tel:" + callNum.substring(0, index) + "/" + callNum.substring(index);
                } else {
                    callNum = "tel:" + callNum;
                }
                //bug id 5363
                callNum = Utilities.getformatedCallNumber(callNum);

                if (Build.CALL_INVOKE) {
                    telNumber = callNum;
                    loadMessageBox(7, Constants.popupMessage[0] + " " + callNum + "?", 8, Constants.headerText[8]);
                } else {
                    if (Build.PLATFORM_REQUEST_SUPPORTED) {
                        ObjectBuilderFactory.GetProgram().platformRequest(callNum);
                    } //                            ObjectBuilderFactory.getPCanvas().platformRequest(callNum);
                    else {
                        loadMessageBox(16, Constants.popupMessage[32], 0, Constants.headerText[8]);
                    }
                }
            } else {
                loadMessageBox(16, Constants.popupMessage[33], 0, Constants.headerText[8]);
            }
        } catch (Exception e) {
        }
    }

    private void loadUrlAction(UrlAction uaction) {
        try {
            url = replaceVariableData(uaction.getUrl(), null, false, true, true, true);
            loadNextAction(uaction.getGoId());
            if (null != url) {
                invokeUrl(); //CR12815
                //loadMessageBox(5, Constants.popupMessage[1], 3, Constants.headerText[9]);
            } else {
                loadMessageBox(16, Constants.popupMessage[34], 0, null);
            }
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param action
     */
    private void loadGeneralAction(GeneralAction action) {
        try {
            String scValue = null;
            String name = action.getScPrefix();
            int index = -1;
            if (null != name) {
                if (null != getRecordNameAndField(name, true)) {
                    index = name.indexOf(".");
                    if (index > -1) {
                        name = name.substring(index + 1);
                    } else {
                        index = name.indexOf(":");
                        if (index > -1) {
                            scValue = name.substring(0, index);
                        } else {
                            scValue = name;
                        }
                        if (action.isCSingleValue()) {
                            rParser.deleteSingleRecord(name, scValue);
                        } else {
                            rParser.deleteAllRecord(scValue);
                        }
                        scValue = null;
                        name = null;
                    }
                } else if ((null != header.getMemVarName() && header.getMemVarName().indexOf("," + name + ",") > 0) || null != header.getATableMemVarName() && header.getATableMemVarName().indexOf("," + name + ",") > 0) {
                    if (name.startsWith(globalMem)) {
                        GlobalMemorizeVariable.removeValue(name);
                    } else {
                        pparser.clearMemorizeValue(name);
                    }
                    //bug id 5295
                    remvoeStoreSequence(name);
                    name = null;
                } else {
                    if ((index = name.indexOf(".")) > -1) {
                        name = name.substring(0, index);
                    }
                    if (action.isCSingleValue()) {
                        scValue = getLastedStoreNameValue(action.getScPrefix());
                        if (null != scValue) {
                            //if(name.startsWith(global)){
                            //  globalMemorize.removeValue(name);
                            //globalParser.deleteSingleValue(name, scValue);
                            // } else {
                            eparser.deleteSingleValue(name, scValue);
                            deletePershableEntry(name, scValue); // bug 11581
                            //}
                        }
                    } else if (action.isClearAll()) {
                        //if(name.startsWith(global)){
                        //  globalMemorize.removeValue(name);
                        //globalParser.deleteAllValues(name);
                        //} else {
                        deletePershableEntry(name, null); // bug 11581
                        eparser.deleteAllValues(name);
                        //}
                    }
                }
            }

            /**
             * Handle General Action Assign the value to the proper parser
             */
            GenericAssignTable[] gTable = action.getGTable();
            if (null != gTable) {
                index = gTable.length;
                for (int i = 0; i < index; i++) {
                    //CR 14824
                    if(gTable[i].getVarName().compareTo(addContact) == 0){
                        scValue = replaceVariableData("[addnumber]", null, false, false, true, true);
                        name = replaceVariableData("[addname]", null, false, false, true, true);
                        if(scValue.indexOf("[")==-1 && name.indexOf("[")==-1){
                            Contacts.addContacts(name, scValue);
                        }
                    } else if(gTable[i].getVarName().compareTo(contactType) == 0){
                        //CR 14823
                        GlobalMemorizeVariable.setContactType(replaceVariableData(gTable[i].getVarValue(),
                                            null, true, false, true, true));
                    } else {
                        scValue = gTable[i].getVarValue();
                        if (scValue.indexOf("[") > -1) {
                            scValue = replaceVariableData(scValue, null, false, false, true, true);
                        }
                        if (scValue.indexOf("]") == -1) {
                            if (gTable[i].getIsMem()) {
                                if (scValue.compareTo("null") == 0) {
                                    if (gTable[i].getVarName().startsWith(globalMem)) {
                                        GlobalMemorizeVariable.removeValue(gTable[i].getVarName());
                                    } else {
                                        pparser.clearMemorizeValue(gTable[i].getVarName());
                                    }
                                } else {
                                    if (gTable[i].getVarName().startsWith(globalMem)) {
                                        GlobalMemorizeVariable.add(gTable[i].getVarName(), scValue);
                                    } else {
                                        pparser.setEntryMemorizeValue(gTable[i].getVarName(), scValue, null);
                                    }
                                }
                                StoreSequnece(-2, gTable[i].getVarName(), scValue, false, false, null, false);
                            } else {
                                storeAssignValue(scValue, gTable[i].getVarName());
                            }
                        }
                    }
                }
                gTable = null;
            }

            /**
             * Handle the General action
             */
            LogicalBranchTable[] lTable = action.getLBTable();
            if (null != lTable) {
                    byte count = (byte) lTable.length;
                boolean isSet = false;
                for (int i = 0; i < count; i++) {
                    scValue = replaceVariableData(lTable[i].getScPrefix(), null, false, false, true, true);
                    name = replaceVariableData(lTable[i].getValue(), null, false, false, true, true);
                    //CR 14806
                    if(scValue.compareTo(groupAndShoutJoined) == 0 && name.compareTo("null") == 0){
                        isSet = Contacts.isSHGroupOrShoutContactsRefresh((byte)1);
                    } else if (scValue.indexOf("[") > -1 && name.compareTo("null") == 0) { //Natacha CR
                        isSet = true;
                    } else if (name.indexOf("[") == -1) {
                        isSet = scValue.toLowerCase().equals(name);
                    }
                    if ((lTable[i].isIsEqual() && isSet) || (!lTable[i].isIsEqual() && !isSet)) {
                        loadAction(pparser.getAction(lTable[i].getGotoid()));
                        return;
                    }
                }
                lTable = null;
            }
            loadNextAction(action.getGotoid());
        } catch (Exception e) {
            Logger.debugOnError("ProfileHandler->LoadGeneralAction-> "+e.toString());
        }
    }

    /**
     *
     * @param evalue
     * @param scprefix
     */
    private void storeAssignValue(String evalue, String scprefix) {
        String[] values = getRecordNameAndField(scprefix, true);
        if (null != values) {
            if (values.length > 1) {
                if (evalue.compareTo("null") != 0) {
                    eparser.WritePropagateValue(values[1], evalue);
                }
                //if(evalue.compareTo("null") != 0 || scprefix.compareTo("friendemail.alias") !=0)
                rParser.addRecord(values[0], values[1], evalue, values[0] + values[2]);
                StoreSequnece(-2, scprefix, evalue, false, false, null, true);
            }
            values = null;
        } else if (null != header.getMemVarName() && header.getMemVarName().indexOf("," + scprefix + ",") > -1) {
            if (header.getMemVarName().startsWith(globalMem)) {
                GlobalMemorizeVariable.add(scprefix, evalue);
            } else {
                pparser.setEntryMemorizeValue(scprefix, evalue, null);
            }
        } else {
            String temp = scprefix;
            int index = temp.indexOf(".");
            if (index > -1) {
                temp = temp.substring(0, index);
            }
            if (evalue.compareTo("null") != 0) {
                //if(temp.startsWith(global)){
                //  globalMemorize.add(temp, evalue);
//                    globalParser.WritePropagateValue(temp, evalue);
                //} else {
                eparser.WritePropagateValue(temp, evalue);
                //}
            }
            StoreSequnece(-2, scprefix, evalue, false, false, null, true);
        }
    }

    /**
     *
     * @param name
     * @return
     */
    private String getLastedStoreNameValue(String name) {
        int len = 0;
        if (null != name && null != seqArray) {
            len = seqArray.length;
            for (int i = 0; i < len; i++) {
                if (seqArray[i].getSelectedName().compareTo(name) == 0) {
                    return seqArray[i].getSelectedValue();
                }
            }
        }
        return null;
    }

    /**
     * Method to Invoke the Browser for the stored url String
     */
    private void invokeUrl() {
        try {
            url = Utilities.getFormatedURlString(url);
            if (Build.PLATFORM_REQUEST_SUPPORTED) {
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Invoke URL:" + url);
                //#endif
                ObjectBuilderFactory.getPCanvas().platformRequest(url);
            } else {
                loadMessageBox(16, Constants.popupMessage[35], 0, Constants.headerText[8]);
            }
        } catch (Exception e) {
        }
        url = null;
    }

    private String getMatchTextMsg(String msg) {
        if (null != _saction) {
            try {
                StringBuffer stbuf = null;
                int serIndex = 0;
                int actualStartTextIndex = 0;
                int startTextIndex;
                int startTextLength = 0;
                KeywordDef[] kd = _saction.getKdarr();
                int len = 0;

                msg = AppendIgnoreString(msg, _saction.getITable());
                if (null != kd && (len = kd.length) > 0) {
                    int endTextIndex;
                    String startText;
                    String endText;
                    byte endadd = 0;
                    int msgLength = msg.length();

                    //bug id 12108 and 12052
                    int keywordCount = 0;
                    //CR 11799
                    for (int i = 0; i < len && msgLength > 0; i++) {
                        startTextIndex = 0;
                        startText = kd[i].getStText();
                        endText = kd[i].getEndText();
                        startTextLength = startText.length();
                        serIndex = 0;
                        //bug id 12108 and 12052
                        keywordCount = 0;
                        while ((startTextIndex = msg.toLowerCase().indexOf(startText, serIndex)) > -1
                                //bug id 12108 and 12052
                                && keywordCount < kd[i].getCount()) {
                            endTextIndex = msgLength;
                            if (endText.length() > 0) {
                                endTextIndex = msg.toLowerCase().indexOf(endText, startTextIndex + startTextLength);
                            }
                            if (endTextIndex == -1) {
                                break;
                            }
                            actualStartTextIndex = startTextIndex + startTextLength;
                            if (!isHighlighedValue(msg, startTextIndex, endTextIndex) && !isIgnoreText(msg, startTextIndex, endTextIndex)) {
                                if (actualStartTextIndex < endTextIndex) {
                                    if (kd[i].getSelText() == null) {
                                        kd[i].setSelText(msg.substring(actualStartTextIndex, endTextIndex));
                                    } else {
                                        kd[i].setSelText(kd[i].getSelText() + "^" + msg.substring(actualStartTextIndex, endTextIndex));
                                    }
                                    stbuf = new StringBuffer(msg);
                                    stbuf.insert(endTextIndex + endText.length() + endadd, "*$");
                                    stbuf.insert(endTextIndex, "|>");
                                    stbuf.insert(actualStartTextIndex, "<|");
                                    stbuf.insert(startTextIndex, "$*");
                                    msg = stbuf.toString();
                                    msgLength += 8;
                                    stbuf = null;
                                    //bug id 12108 and 12052
                                    keywordCount++;
                                    // break;
                                }
                            }
                            actualStartTextIndex = serIndex;
                            serIndex = msg.indexOf("*$", startTextIndex);
                            if (serIndex > endTextIndex || serIndex == -1) {
                                serIndex = endTextIndex + endText.length();
                            } else {
                                serIndex += 2;
                            }
                            if (actualStartTextIndex >= serIndex) {
                                serIndex = actualStartTextIndex + 1;
                            }
                            if (msgLength <= serIndex) {
                                break;
                            }
                        }
                    }
                    _saction.setKdarr(kd);
                }
                kd = null;

                IntText[] iText = _saction.getItarr();
                if (null != iText && (len = iText.length) > 0) {
                    String[] text = null;
                    for (int i = 0; i < len; i++) {
                        text = Utilities.split(iText[i].getSrctxt(), ",");
                        startTextLength = text.length;
                        for (int j = 0; !iText[i].isIsImediateGoto() && j < startTextLength; j++) {
                            serIndex = 0;
                            String temp = text[j];
                            while ((startTextIndex = msg.toLowerCase().indexOf(text[j], serIndex)) > -1) {
                                actualStartTextIndex = startTextIndex + text[j].length();
                                if (!isHighlighedValue(msg, startTextIndex, actualStartTextIndex) && !isIgnoreText(msg, startTextIndex, actualStartTextIndex)) {
                                    if (msg.startsWith("\n", startTextIndex)) {
                                        startTextIndex += 1;
                                    }
//                                        if(msg.startsWith("\n",actStIndex))
//                                            actStIndex -= 1;
                                    stbuf = new StringBuffer(msg);
                                    stbuf.insert(actualStartTextIndex, "|>");
                                    stbuf.insert(startTextIndex, "<|");
                                    msg = stbuf.toString();
                                    stbuf = null;
                                    break;
                                } else {
                                    serIndex = actualStartTextIndex;
                                    if (msg.length() <= serIndex) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                iText = null;

//                    if(null != _saction.getITable()){
//
//                    }
            } catch (Exception e) {
            }
            msg = Utilities.remove(msg, "$*");
            msg = Utilities.remove(msg, "*$");
        }
        return msg;
    }

    /**
     * @param msg
     * @param iTable
     * @return
     */
    private String AppendIgnoreString(String msg, IgnoreTable[] iTable) {
        int len;
        if (null != iTable && (len = iTable.length) > 0) {
            int stIndex, edIndex;
            StringBuffer stbuf = new StringBuffer(msg);
            int serIndex = 0;
            for (int i = 0; i < len; i++) {
                stIndex = msg.toLowerCase().indexOf(iTable[i].getStText(), serIndex);
                if (stIndex > -1) {
                    edIndex = msg.length();
                    if (iTable[i].getEdText().length() > 0) {
                        edIndex = msg.toLowerCase().indexOf(iTable[i].getEdText(), stIndex + iTable[i].getStText().length());
                    }
                    if (edIndex > -1) {
                        serIndex = edIndex + iTable[i].getEdText().length();
                        stbuf.insert(serIndex, "*$");
                        stbuf.insert(stIndex, "$*");
                        serIndex += 4;
                        msg = stbuf.toString();
                    }
                }
            }
        }
        return msg;
    }

    /**
     * @param msg
     * @param stIndex
     * @param edIndex
     * @return
     */
    private boolean isHighlighedValue(String msg, int stIndex, int edIndex) {
        int index = msg.indexOf("<|", stIndex);
        if (index > -1 && index <= edIndex) {
            return true;
        }
        index = msg.indexOf("|>", stIndex);
        if (index > -1) {
            if (edIndex >= index) {
                return true;
            }
            if ((msg.substring(edIndex, index)).indexOf("<|") < 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param msg
     * @param stIndex
     * @param edIndex
     * @return
     */
    private boolean isIgnoreText(String msg, int stIndex, int edIndex) {
        int index = msg.indexOf("$*", stIndex);
        if (index > -1 && index <= edIndex) {
            return true;
        }
        index = msg.indexOf("*$", stIndex);
        if (index > -1) {
            if (edIndex >= index) {
                return true;
            }
            if ((msg.substring(edIndex, index)).indexOf("$*") < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public byte isWaitingForMessage(Message message) {
//        String msgId, String chatId,
//            String sNumber, String chatName, String appName, String messagePlus) {
        byte isWaiting = 0;
        try {

            if (null != pparser && null != header) {
                synchronized (pparser) {
                    if (currst == 3 && pparser.getType() == ProfileTypeConstant.DISPLAYACTION) {
                        DisplayAction dAction = (DisplayAction) pparser.getCurrentObjject();
                        if (null != message.getChatId() &&
                                dAction.getDispimage() == ProfileTypeConstant.Display.DISPLAY_CHAT) {
                            isWaiting = 1;
                        } else if (null != sequenceNumber[0] && null != message.getMscf()
                                && sequenceNumber[0].compareTo(message.getMscf()) == 0) {
                            String qtype = "";
                            int index = message.getFName().indexOf("|");
                            if (index > 0) {
                                qtype = message.getFName().substring(index + 1);
                            }
                            if (dAction.getDisptime() == -1 && null != dAction.getMqtype() && qtype.compareTo(dAction.getMqtype()) == 0) {
                                msgid = message.getFName().substring(0, index);
                                isWaiting = 1;
                            }
                        }
                        dAction = null;
                        //CR 13118
                    } else if (pparser.getType() == ProfileTypeConstant.ENTRYACTION &&
                            null != message.getChatId() && message.getSFullName().compareTo(header.getName()) == 0) {
                        EntryAction entryAction = (EntryAction) pparser.getCurrentObjject();
//            if(entryAction.getScprefix().startsWith("chat")){
//                rParser.propagateRecord("chat", "friendname", chatName, "chat");
//                rParser.propagateRecord("chat", "friendID", chatId, "chat");
//                rParser.getRecordValues("chat", "chat", null);
//                isWaiting = 3;
//            } else

                        if (entryAction.getScprefix().compareTo("history") == 0) {
                            //bug 14075
                            String values = ChatHistoryHandler.getUserRecord(header.getName(), message.getChatId());
                            if (null != rParser) {
                                this.chatName = message.getChatName();
                                //bug 0014368
                                if (null != values) {
                                    oldValue = rParser.updateSingleRecordElement(message.getChatId(), values, "history");
                                    oldValue = rParser.getchangedFormatedValue("history", oldValue, entryAction.getRDFormat());
                                    newValue = rParser.getchangedFormatedValue("history", values, entryAction.getRDFormat());
                                } else {
                                    oldValue = null;
                                    rParser.updateNewRecordValue("history", message.getChatId() + "^" + message.getChatName()
                                            + "^" + message.getMessagePlus() + "^1", "history");
                                    newValue = rParser.getchangedFormatedValue("history",
                                            message.getChatId() + "^" + message.getChatName()
                                            + "^" + message.getMessagePlus() + "^1", entryAction.getRDFormat());
                                    rParser.getRecordValues("history", "history", null);
                                }
                            }
                            isWaiting = 2;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }


        return isWaiting;
    }

    //bug 14075
    public String getChatName() {
        return chatName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    //bug 13203
    public boolean isNotCurrentApp(String appName) {
        if (null != header && header.getName().compareTo(appName) != 0) {
            return true;
        }
        return false;
    }

    public String isMultiPartWait(String[] sNumber) {

        String displayString = null;
        if (pparser.getType() == ProfileTypeConstant.DISPLAYACTION) {
            if (currst == 3) {
                if (null != sequenceNumber[0] && null != sNumber && sequenceNumber[0].compareTo(sNumber[0]) == 0) {
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("SEQUENCE NUMBER = " + sNumber[0]);
                    //#endif
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("TOTAL PARTS = " + sNumber[1]);
                    //#endif
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Comparing current message sequence number " + sNumber[0] + "with SQ number " + sequenceNumber[0]);
                    //#endif

                    multiPartCount++;
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Increment the multipart count and count = "+ multiPartCount);
                    //#endif
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("ProfileHandler> set maxcount ="+sNumber[1]);
                    //#endif
                    maxCount = sNumber[1];
                    displayString = Constants.appendText[30] + " " + multiPartCount + " " + Constants.appendText[31] + " " + maxCount;
                }
            }
        }
        return displayString;
    }

    public boolean isWaitingForMessage(Message messageDto, int failType) {
        boolean isWaiting = true;
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("isWaitingForMessage:Start point ");
//        //#endif //11801
        if (messageDto.getSFullName().compareTo(header.getName()) == 0) {
            if (currst == 3 && pparser.getType() == ProfileTypeConstant.DISPLAYACTION) {
                //DisplayAction dAction = (DisplayAction) pparser.getCurrentObjject();
                if (null != sequenceNumber[1] && sequenceNumber[1].compareTo(messageDto.getPopulatedTime() + "") == 0) {
                    isWaiting = false;
                    if (failType == 3) { //CR 10203
                        setMsgWaitingTime();
                        if (isSmsWaitBeforeLoad) {
                            isSmsWaitBeforeLoad = false;
                        }
                    } else {//if(null != dAction.getMqtype() && msg[2].compareTo(dAction.getMqtype()) == 0){
                        raiseMsgDelayNotification(false);
                        //<-- CR 13618
                        if (isRegister) {
                            loadMessageBox(25, Constants.popupMessage[52], 9, Constants.headerText[26]);
                        } //<-- CR 13618
                        else if (failType == 2) { //Cr 9710
                            loadMessageBox(19, Constants.popupMessage[55], 9, Constants.headerText[26]);
                        } else if (failType == 4) { //CR 11975
                            loadMessageBox(19, Constants.popupMessage[58], 9, Constants.headerText[26]);
                        } //<!-- CR 11975 -->
                        else {

                            //CR 11975
                            if (null != messageDto.getErrorMessage()) {
                                loadMessageBox(19, messageDto.getErrorMessage(), 9, Constants.headerText[26]);
                            } //<!-- CR 11975 -->
                            else {
                                loadMessageBox(19, Constants.popupMessage[52], 9, Constants.headerText[26]);
                            }
                        }
                    }
                }
                //dAction = null;
            }
        }
//        else if(messageDto.isIsSendQueueEmpty()){
//            isWaiting = false;
//            loadMessageBox(19, Constants.popupMessage[52], 9, Constants.headerText[26]);
//        }
//        //#if VERBOSELOGGING
//        //|JG|Logger.debugOnError("isWaitingForMessage= "+isWaiting+" "+ "Error type= "+failType);
//        //#endif //11801
        return isWaiting;

    }

    private String getRemovedIgnoredString(String msg, IgnoreTable[] iTable) {
        int len;
        if (null != iTable && (len = iTable.length) > 0) {
            int stIndex, edIndex;
            for (int i = 0; i < len; i++) {
                stIndex = msg.toLowerCase().indexOf(iTable[i].getStText());
                if (stIndex > -1) {
                    edIndex = msg.length();
                    if (iTable[i].getEdText().length() > 0) {
                        edIndex = msg.toLowerCase().indexOf(iTable[i].getEdText(), stIndex + iTable[i].getStText().length());
                        if (edIndex > -1) {
                            msg = Utilities.remove(msg, msg.substring(stIndex, edIndex + iTable[i].getEdText().length()));
                        }
                    }
                }
            }
        }
        return msg;
    }

    /**
     *
     */
    public void handleReceivedMessage(String msg, AdData dyAdd, String num,
            boolean isNotSaved, int nPartMessage) {
        try {
            //boolean dontSaveinInbox = isNotSaved;//cr121hema
            String value = null;
            raiseMsgDelayNotification(false);
//                if(saveInbox){
            if (null != dyAdd && header.isIsDynamicAd()) {
                isAdget = false;
                adData = dyAdd;
                if (null != adData.getLPag() && adData.isIsDCDSend()) {
                    ObjectBuilderFactory.getControlChanel().addDisplayed(header.getPId(), adData.getAdId());
                }
                dyAdd = null;
            }

            Object _obj = null;
            IntText[] intText = null;
            boolean isGoto = true;
            if (null != _saction && (null != (intText = _saction.getItarr()))) {
                int count = intText.length;
                for (int i = 0; i < count; i++) {
                    if (intText[i].isIsImediateGoto() && msg.toLowerCase().indexOf(intText[i].getSrctxt()) > -1) {
                        isGoto = false;
                        _obj = pparser.getAction(intText[i].getPgotoid());
                        break;
                    }
                }
            }

            if (isGoto) {
                if (isNotSaved) {
                    value = msg;
                }
                //Remvoe the Ignore Text
                msg = getRemovedIgnoredString(msg, _saction.getITable());

                //Store Entry Propagated Value
                storePropagateValues(_saction.getPtarr(), msg);

                //Propagate ShortCode and Message Arrival Time
                propagateValue(num);
            }

            if (null == _obj) {
                _obj = pparser.getNextAction(-1);
            }

            if (null != _obj) {
                //++CR 6713 - Moved this block from the else block of if-else below to outside.
                String[] nMessage = null;
                if (isNotSaved) {
                    //bug id 5265
                    //isNotSaved = false;
                    nMessage = new String[2];
                    nMessage[0] = value;
                    value = null;
                } else {
                    isNotSaved = true;
                    nMessage = ObjectBuilderFactory.GetKernel().getViewMessage(msgid);
                }
                //++CR 6713
                if (ProfileTypeConstant.DISPLAYACTION == pparser.getType() && ((DisplayAction) _obj).getDispimage() == ProfileTypeConstant.Display.DISPLAY_SMSMESSAGE) {
                    receivedMsg = new String[]{nMessage[0], nMessage[1]}; //bug id 8504
                    loadViewScreen((DisplayAction) _obj, nMessage, msgid);//cr121hema
                } else {
                    msgid = null;
                    receivedMsg = null;
                    loadAction(_obj);
                }
                _obj = null;
            }
        } catch (Exception e) {
            Logger.loggerError("Profile Handler-> Handle Receive Message " + e.toString());
        }
    }

//    private void propagateContacts(String storeContacts){
//        if(null != _saction && null != _saction.getQtype() &&
//                 _saction.getQtype().toLowerCase().compareTo("message+") == 0){
//                 rParser.propagateContacts("Message+", null);
//        }
//    }
    public void handleAfterDwonloaded() {
        DisplayAction disAction = (DisplayAction) pparser.getCurrentObjject();
        loadNextAction(disAction.getGotoid());
    }

    /**
     *
     * @param num
     */
    private void propagateValue(String num) {
        //Propagate Shortcode
        if (null != _saction.getSCProName()) {
            //if(_saction.getSCProName().startsWith(global)){
            //  globalMemorize.add(_saction.getSCProName(), num);
            //globalParser.WritePropagateValue(_saction.getSCProName(), num);
            //} else {
            eparser.WritePropagateValue(_saction.getSCProName(), num);
            //}
        }

        //Propagate Message Arrival Time
        if (null != _saction.getProArrivalTime() && null != msgid) {
            //if(_saction.getProArrivalTime().startsWith(global)){
            //  globalMemorize.add(_saction.getProArrivalTime(), Utilities.getHHMMFormat(msgid));
//                globalParser.WritePropagateValue(_saction.getProArrivalTime(), Utilities.getHHMMFormat(msgid));
            //} else {
            eparser.WritePropagateValue(_saction.getProArrivalTime(), Utilities.getHHMMFormat(msgid));
            //}
        }
    }

    /**
     *
     **/
    private void storePropagateValues(PropagateType[] ppgt, String msg) {
        int len = 0;
        int stIndex;
        int endIndex;
        int length;
        if (null != ppgt && (len = ppgt.length) > 0) {
            String temp = null;
            // Record Name
            String rName = null;
            String[] value = null;
            for (int i = 0; i < len && msg.length() > 0; i++) {
                stIndex = 0;
                length = ppgt[i].getStText().length();
                if (length > 0) {
                    stIndex = msg.toLowerCase().indexOf(ppgt[i].getStText());
                }

                if (stIndex == -1) {
                    continue;
                }

                temp = msg.substring(stIndex + length);

                endIndex = temp.length();
                if (ppgt[i].getEndText().length() > 0) {
                    endIndex = temp.toLowerCase().indexOf(ppgt[i].getEndText());
                }

                if (endIndex == -1) {
                    continue;
                }

                //bug id 4944
                temp = temp.substring(0, endIndex);//.trim();
                //bug id 4990
                endIndex += ppgt[i].getEndText().length() + length;
                //stIndex += endIndex + ppgt[i].getEndText().length() + length;
                msg = msg.substring(0, stIndex) + msg.substring(stIndex + endIndex);
                rName = ppgt[i].getSCPrefix();
                stIndex = rName.indexOf(".");
                value = getRecordNameAndField(rName, true);
                if (temp.length() > 0) {
                    if (null != value) {
                        if (value.length > 1) {
                            rParser.propagateRecord(value[0], value[1], temp, value[0] + value[2]);
                        }
                        value = null;
                    } else {
                        stIndex = rName.indexOf(".");
                        if (stIndex > -1) {
                            rName = rName.substring(0, stIndex);
                        }
//                        if(rName.startsWith(global)){
//                            globalParser.WritePropagateValue(rName, temp);
//                        } else {
                        eparser.WritePropagateValue(rName, temp);
//                        }
                    }
                    StoreSequnece(-2, ppgt[i].getSCPrefix(), temp, false, false, ",", true);
                }
            }
            _saction.setPtarr(null);

            temp = null;
            rName = null;
            value = null;
        }
        ppgt = null;
    }

    /**
     *
     */
    private void loadSmsSend(SMSSendAction action, boolean isShouldLoad) {
        try {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Going to send SMS");
            //#endif

            //CR 12165
            //CR 12988 cr 13219
            //CR 13397
//            if(!isShouldLoad && Settings.isIsGPRS() &&
//                    !action.getInternalLoopBack() &&
//                    !action.isDSendMsg()&& null == Settings.getPhoneNumber(true)){
//                loadMessageBox(21, Constants.popupMessage[61], 11, null);
//            } else
            {
                boolean isSend = false;
                _saction = action;
                String msgDisplay = null;
                String qFormat = action.getQfmt();
                String sc = action.getSc();

                //Suppose the Selected profile is DownLoad Profile Means We cant create
                // Sequece Shortcut and so check this condition
                //            if (isSeqSel) {
                //                storeIndex = false;
                ////                sc = qparser.getShortCode();
                //                qFormat = qparser.getQuery();
                //                isSeqSel = false;
                //                qparser.erace();
                //            } else {
                //                storeIndex = true;
                //            }
                //CR 14035
//                boolean isUpload =false;
//                if(qFormat.toLowerCase().indexOf(ChannelData.allContacts)>-1){
//                    isUpload = true;
//                }

                //Cr 14139
                boolean isLogin = false;
                if (qFormat.indexOf("(STARTUP) LOGIN") > -1) {
                    isLogin = true;
                }

                String msg = replaceVariableData(qFormat, null, false, true, true, false);
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Message: "+msg);
                //#endif

                if (null == sc && null != header.getScode() && header.getScode().length > 0) {
                    sc = header.getScode()[0];
                }
                if (null != sc) {
                    if (sc.indexOf("[") > -1) {
                        sc = replaceVariableData(sc, null, false, true, true, true);// getShortCode(sc);// replaceVariableData(sc, null);
                        ObjectBuilderFactory.GetKernel().registerNewNumers(sc, header.getName(), header.getMSCF(), header.getAbbreviation());
                    }


                    if (ChannelData.indireShortCodeName.compareTo(sc.toLowerCase()) == 0) {
                        sc = ChannelData.getShortcode();
                    }

                    if (null == (msgDisplay = getSearchString(sc, "[", "]"))) {
                        if (null == (msgDisplay = getSearchString(msg, "[", "]"))) {
                            isSend = true;
                            //                    if (storeIndex) {
                            //                        qparser.storeQuery(sc, getVarIds(action.getId()),
                            //                                saveQueryMsg(qFormat), getFixedValue());
                            //                    }
                            //#if VERBOSELOGGING
                            //|JG|Logger.debugOnError("Before ProfileHandler>sendmessage()");
                            //#endif
                            sendMessage(msg, action.getQtype(), sc, action.getNoNewMSG(),
                                    action.getDontSaveInbox(), action.getInternalLoopBack(), isLogin);
                            //                        //#if VERBOSELOGGING
                            //                        //|JG|Logger.debugOnError("After ProfileHandler>sendmessage()");
                            //                        //#endif //11801
                        }
                    }
                } else {
                    msgDisplay = "Please set sender number";
                }

                if (!isSend) {
                    loadMenu((MenuItemList) pparser.getInitialMenu());
                    //bug id 5421
                    loadMessageBox(16, new StringBuffer(Constants.appendText[20] + " ").append(
                            getWarningMsg(msgDisplay)).toString(), 0, null);
                }
            }
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[15]);
        }
    }

    /**
     * Method to retrieve the Wraning message for the given Not set query String
     * @param cStr Checking String/ Not set query String
     * @return Warning String or Same Sring
     */
    private String getWarningMsg(String cStr) {
        if (null != header.getMWMsg()) {
            int len = header.getMWMsg().indexOf(cStr + "|");
            if (len > -1) {
                cStr = header.getMWMsg().substring(len + cStr.length() + 1);
                len = cStr.indexOf("||");
                if (len > -1) {
                    cStr = cStr.substring(0, len);
                }
            }
        }
        return cStr;
    }

    /**
     * Method To eene The Display Selecetion
     *
     * @param actionId
     *            Action Id
     * @param itemId
     *            Item Id
     * @param itemName
     *            Item Name
     */
//	private void handleDisplaySelection(String itemName) {
//            try {
//                DisplayAction action =(DisplayAction)pparser.getCurrentObjject();
//                if (null == itemName) {
//                    raiseMsgDelayNotification(false);
//                    loadNextAction(action.getGotoid());
//                } else if(null != _saction){
//                    int len;
//                    int nextid =-1;
//                    String value = null;
//                    IntText[] inttxt = _saction.getItarr();
//                    if(null != inttxt && (len=inttxt.length)>0)
//                    {
//                        for(int i=0;i<len;i++)
//                        {
//                            value = Utilities.replace(inttxt[i].getSrctxt(),"\n","");
//                            if(0 == value.compareTo(itemName))
//                            {
//                                nextid = inttxt[i].getPgotoid();
//                                break;
//                            }
//                        }
//                    }
//                    inttxt = null;
//                    if(nextid<0)
//                    {
//                        KeywordDef[] kd = _saction.getKdarr();
//                        if(null != kd && (len=kd.length)>0)
//                        {
//                            for(int i=0;i<len;i++)
//                            {
//                                if(null != kd[i].getSelText()){
//                                    value = Utilities.replace(kd[i].getSelText(),"\n","");
//                                    if(value.compareTo(itemName) == 0)
//                                    {
//                                        kwValue = new String[2];
//                                        kwValue[0] = kd[i].getSCPrefix();
//                                        kwValue[1] = itemName;
//                                        nextid = kd[i].getPgotoid();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        kd = null;
//                    }
//                    String wName = action.getGotoWidgetName();
//                    if(null != wName && !isGotoWidgetName(wName)){
//                        if(nextid>-1)
//                            loadAction(pparser.getAction(nextid));
//                        loadMessageBox(0, "Goto Widget file Missing", 0,null);
//                    } else if(nextid>-1) loadAction(pparser.getAction(nextid));
//                }
//                action = null;
//            } catch (Exception exception) {
//                    LoadErrorMessageScreen(exception,"Display Handle Error");
//            }
//	}
    private void handleDisplaySelection(int itemId, String itemName) {
        try {
            DisplayAction action = (DisplayAction) pparser.getCurrentObjject();

            if (action.getDispimage() == ProfileTypeConstant.Display.DISPLAY_CHAT) {
                if (itemName.length() > 0) {
                    //CR 9774
                    String chatname = replaceVariableData("[chat.friendname]", null,
                            true, false, true, true);
                    String chatId = replaceVariableData("[chat.friendID]", null,
                            true, false, true, true);
                    String plusUser = replaceVariableData("[chat.plususer]", null,
                            true, false, true, true);
                    sendChatMessage(itemName, chatId, chatname, plusUser, action.getMqtype());
                }
            } else if (null == itemName) {
                raiseMsgDelayNotification(false);
                String wName = action.getGotoWidgetName();
                if (null != wName) {
                    if (!isGotoWidgetName(wName)) {
                        loadNextAction(action.getGotoid());
                        loadMessageBox(4, Constants.appendText[14] + " " + wName + " " + Constants.popupMessage[16] + " " + wName + " "
                                + Constants.popupMessage[17], 7, null);
                    }
                } else {
                    loadNextAction(action.getGotoid());
                }
            } else {
                if (itemId == -2) {
                    handleEscapeText(itemName);
                } else if (null != _saction) {
                    int len;
                    int nextid = -1;
                    String value = null;
                    IntText[] inttxt = _saction.getItarr();
                    if (null != inttxt && (len = inttxt.length) > 0) {
                        for (int i = 0; i < len; i++) {
                            value = Utilities.replace(inttxt[i].getSrctxt(), "\n", "");
                            if (0 == value.compareTo(itemName.toLowerCase())) {
                                nextid = inttxt[i].getPgotoid();
                                break;
                            }
                        }
                    }
                    inttxt = null;
                    if (nextid < 0) {
                        KeywordDef[] kd = _saction.getKdarr();
                        if (null != kd && (len = kd.length) > 0) {
                            for (int i = 0; i < len; i++) {
                                if (null != kd[i].getSelText()) {
                                    //CR 11799
                                    value = "^" + Utilities.replace(kd[i].getSelText(), "\n", "") + "^";
                                    if (value.indexOf("^" + itemName + "^") > -1) {
                                        StoreSequnece(-2, kd[i].getSCPrefix(), itemName, false, false, null, true);
                                        nextid = kd[i].getPgotoid();
                                    }
                                }
                            }
                        }
                        kd = null;
                    }
                    String wName = action.getGotoWidgetName();
                    if (null != wName) {
                        if (!isGotoWidgetName(wName)) {
                            if (nextid > -1) {
                                loadAction(pparser.getAction(nextid));
                            }
                            loadMessageBox(4, Constants.appendText[14] + " " + wName + " " + Constants.popupMessage[16] + " " + wName + " "
                                    + Constants.popupMessage[17], 7, null);
                        }
                    } else if (nextid > -1) {
                        loadAction(pparser.getAction(nextid));
                    }
                }
            }
            action = null;
        } catch (Exception exception) {
            LoadErrorMessageScreen(exception, Constants.errorMessage[16]);
        }
    }

    /**
     * Param MenuId To Be Represnet The current MenuId and MenuId Is Not Null
     * itemId To be Represnet The itemId or Selected Entryshortcut value
     * presnenterName to Be represnet The Presnenter Name Presneter Name Is null
     * Menu State And Menu Item Selected or Sequence Shortcut Seleced
     */
    public byte[] GetOptions(int itemId, String itemName) {
        byte[] options = null;
        if (1 == currst) {
            options = getMenuOptionItems(itemId, itemName); // Menu Options
        } else if (2 == currst) {
            options = getEntryOptionItems(itemName, itemId); // Get Entry Options
        } else if (3 == currst) {
            options = getDisplayOptionItems(itemId); // Display Options
        }
        return appendDynamicOptioins(options);
    }
/*
    public int[] GetOptionsString(int itemId, String itemName) {
        byte[] options = null;
        if (1 == currst) {
            options = getMenuOptionItems(itemId, itemName); // Menu Options
        } else if (2 == currst) {
            options = getEntryOptionItems(itemName, itemId); // Get Entry Options
        } else if (3 == currst) {
            options = getDisplayOptionItems(itemId); // Display Options
        }
        return appendDynamicIntOptioins(options);
    }
*/
    /**
     *
     **/
    private byte[] appendDynamicOptioins(byte[] opts) {
        String[] values = pparser.getEscapeOption();
        int count = 0;
        //CR 6984
        if (null != values && (count = values.length) > 0) {
            for (int i = 0; i < count; i++) {
                values[i] = replaceVariableData(values[i], null,
                        false, true, true, true);
            }
            byte[] dtemp = Constants.setDOpt(values, false, false);
            byte[] temp = new byte[dtemp.length + opts.length];
            // Changes made for BUG iD 3746
            System.arraycopy(opts, 0, temp, 0, opts.length);
            System.arraycopy(dtemp, 0, temp, opts.length, dtemp.length);

            opts = temp;
            temp = null;
        }

        return opts;
    }
/*
    private int[] appendDynamicIntOptioins(byte[] opts) {
        String[] values = pparser.getEscapeOption();
        int count = 0;
        //CR 6984
        int[] response = null;
        if (null != values && (count = values.length) > 0) {
            for (int i = 0; i < count; i++) {
                values[i] = replaceVariableData(values[i], null,
                        false, true, true, true);
            }
            int[] dtemp = Constants.setDtOptInt(values, false, false);
            int[] temp = new int[dtemp.length + opts.length];
            // Changes made for BUG iD 3746
            for(int i=0;i<opts.length;i++){
                temp[i] = opts[i];
            }
            System.arraycopy(dtemp, 0, temp, opts.length, dtemp.length);

            response = temp;
            temp = null;
        } else {
            response = new int[opts.length];
            for(int i=0;i<opts.length;i++){
                response[i] = opts[i];
            }
        }

        return response;
    }
 */
    
    
    /**
     *
     * @param itemId
     * @param itemName
     * @return
     *
     */
    private byte[] getEntryOptionItems(String itemName, int itemId) {

        byte[] opts = new byte[4];
        int i = 0;
        opts[i++] = 38; //Opt - Index of Inbox Option
        opts[i++] = 34; //Opt - Index of Main Menu Option
        opts[i++] = 36; //Opt - Index of Exit Profile Option
        if (itemId != -1 && itemId != -2) {
            EntryAction action = (EntryAction) pparser.getCurrentObjject();
            // <--CR 13679
            if (!action.isIsRDel() && !action.isIsMSRefresh() && !action.isIsMsContacts()) {
//CR13679-->
                opts[i++] = 18; //Opt - Index of Delete option
            }                    //Just enable the Edit option CR 2932
//                    if(!action.isIsRecord())
//                        opts[i++] = 30; //Opt - Index of Edit Option
            action = null;
        }
//		if (null != itemName) {
//                    if(!pparser.isEscapeMenu(itemName)){
//			opts[i++] = 18; //Opt - Index of Delete Option
//                    }
//		} else {
////			EntryAction action = (EntryAction) pparser.getCurrentObjject();
////			if (action.isIsfixed())
////				opts[i++] = 29; //Opt - Index of PromptFor Input Option
////			else
////				opts[i++] = 28; //Opt - Index of Make Entry Shortcut Option
//		}

        byte[] finopts = new byte[i];
        System.arraycopy(opts, 0, finopts, 0, i);
        opts = finopts;

        return opts;
    }

    /**
     * Param menuId is Specified The Current MenuId and is not Come Null itemId
     * is Specified The Selected ItemId ItemId is null Sequenceshortcut TO be
     * Selected otherwise Profile MenuItem Selected
     */
    private byte[] getMenuOptionItems(int itemId, String itemName) {
        byte[] opts = new byte[4];
        boolean isinitial = pparser.isInitialMenu();
        int i = 0;

        opts[i++] = 38; //Opt - Index of Inbox Option
        if (!isinitial) {
            opts[i++] = 34; //Opt - Index of Main Menu
        }
        opts[i++] = 36; //Opt - Index of Exit Profile

        // ItemId Is null Return The SequenceShortcut Options MenuItem
        if (itemId == -1) {
            opts[i++] = 31; //Opt - Index of Rename Option
            opts[i++] = 18; //Opt - Index of  Delete Option
        }

        byte[] finopts = new byte[i];
        System.arraycopy(opts, 0, finopts, 0, i);
        opts = finopts;

        return opts;
    }

    public boolean isExitNodePresent(String name) {
        if (null != name) {
            if (header.getName().compareTo(name) != 0) {
                return pparser.isExitNode();
            } else {
                loadMenu((MenuItemList) pparser.getInitialMenu());
                return true;
            }
        } else {
            return pparser.isExitNode();
        }
    }

    /**
     * Method to send the Display Option Item
     */
    private byte[] getDisplayOptionItems(int itemId) {
        byte[] opts = new byte[10];
        int i = 0;

        DisplayAction action = (DisplayAction) pparser.getCurrentObjject();
//        if(!isSent) //CR 10337
//            opts[i++] = 38; //Opt - Index of Inbox Option
        opts[i++] = 34; //Opt - Index of MainMenu Option
        opts[i++] = 36; //Opt - Index of Exit Profile Option
        if (itemId != -2) {
            //Display Image is SMSView
            if (itemId != -1 && 2 == action.getDispimage()) {
                // opts[i++] = 18; //Opt - Index of Delete Option //CR 10337
                //CR 14381
//                if (action.isIsDReply()) {
//                    opts[i++] = 20;
//                } //Opt - Index of Reply Option
                // opts[i++] = 21; //Opt - Index of Forward Option //CR 10337
            } else if (4 == action.getDispimage()) {
                opts[i++] = 53;
                String capture = System.getProperty("supports.video.capture"); //Cr 14418
                if(null != capture && capture.toLowerCase().compareTo("true") == 0){
                  opts[i++] = 56; //Capture Inage Index
                  if(Utilities.isVideoCapture()){
                    opts[i++] = 60; //Capture Video index
                  }
                }

                //CR 14491
                capture = System.getProperty("supports.audio.capture");
                if(null != capture && capture.toLowerCase().compareTo("true") == 0){
                  opts[i++] = 58; //Capture Inage Index
                }
            }
            //else if(4 == action.getDispimage()){ //CR 6984
            //  opts[i++] = 20; //Opt - Index of Reply Option.
            //   }
        }

//        if (!isSeqSel && storeIndex && !isSent) { //CR 10337
//            opts[i++] = 37; //Opt - Index of Save Query Option
//        }


        byte[] finopts = new byte[i];
        System.arraycopy(opts, 0, finopts, 0, i);
        opts = finopts;

        return opts;
    }

    /**
     *
     * @param menuId
     * @param itemId
     * @param itemName
     * @param OptionText
     *
     */
    public void handleOptionsSelected(int itemId, String itemName, byte opttxt) {

        if (34 == opttxt) { //Opt - Index of Main Menu Option
            raiseMsgDelayNotification(false);
//            qparser.erace();
            loadMenu((MenuItemList) pparser.getInitialMenu());
            //loadAction(pparser.getInitialMenu());
        } else if (22 == opttxt) {// || 2==opttxt) { //Opt - Index of Back Option
            raiseMsgDelayNotification(false);
            handleBackOption(true);
        } else if (2 == opttxt) {  //Opt - Index of the Left Option
            raiseMsgDelayNotification(false);
            handleLeftOption();
        } else if (38 == opttxt) { //Opt - Index of Inbox Option
            handleInboxOption();
        } else if (36 == opttxt) { //Opt - Index of Exit Profile Option
            raiseMsgDelayNotification(false);
            handleExitProfile();
        } else if (18 == opttxt) { //Opt - Index of Delete Option
            handleDeleteOptions(itemId, itemName);
        } else if (31 == opttxt || 30 == opttxt) { //Opt - Index of Rename Option
            ObjectBuilderFactory.GetKernel().renameMenuItem(itemId, itemName);
        } else if (29 == opttxt || 28 == opttxt) { //Opt - 29 Index of Prompt for Input option
            //Opt - 28 Index of Make EntryShortcut Option
            promptForInput = true;
        } //        else if (37 == opttxt) { //Opt - Index of Save Query Option
        //            loadEntrySaveQuery();
        //        }
        else if (20 == opttxt) {  //Opt - Index of Reply Option
            handleReplyOrForwardOption(itemName, true);
        } else if (21 == opttxt) { //Opt - Index of Forward Option
            handleReplyOrForwardOption(itemName, false);
        } else if (opttxt >= Constants.SIZE) {
            raiseMsgDelayNotification(false);
            String eText = String.valueOf(Constants.options[opttxt]);
            if (null != eText) {
                if (null != itemName) {
                    if (currst == 1 && itemId != 0) {
                        IsMenuStore(itemId, itemName);
                    } else if (currst == 2 && itemId != -1 && itemName.length() > 0) {
                        isEntryStore(itemName, itemId, true);
                    }
                }
                handleEscapeText(eText);
            }
        }
    }

    /**
     *  Method to handle the different type of delete operations
     *
     * @param IID
     * @param value
     */
    private void handleDeleteOptions(int IID, String value) {
        iid = IID;
        eval = value;
        if (currst == 1) {
            loadMessageBox(7, Constants.popupMessage[13], 5, null);
        } else if (currst == 2) {
            loadMessageBox(7, Constants.popupMessage[36], 5, null);
        } else if (currst == 3 && null != msgid) {
            loadMessageBox(7, Constants.popupMessage[37], 5, null);
        }
    }

    public void handleNotification() {
//        qparser.erace();
        if (isFeature) {
            PresenterDTO.setLOptByte((byte) -1);
        } else {
            PresenterDTO.setLOptByte((byte) 41);
        }
        loadMenu((MenuItemList) pparser.getInitialMenu());
    }

    /**
     * Method to hadle the left option string select
     */
    private void handleLeftOption() {
        if (currst == 1) {
            MenuItemList list = (MenuItemList) pparser.getCurrentObjject();
            if (null != list.getLOString()) {
                Object obj = null;
                if (list.isSmartBack()) {
                    obj = pparser.getPreviousAction(list.getLOGotoId());
                } else {
                    obj = pparser.getAction(list.getLOGotoId());
                }
                if (null != obj) {
                    loadAction(obj);
                }
                obj = null;
            }
            list = null;
        } else if (currst == 2) {
            EntryAction action = (EntryAction) pparser.getCurrentObjject();
            if (null != action.getLOString()) {
                Object obj = null;
                if (action.isSmartBack()) {
                    obj = pparser.getPreviousAction(action.getLOGotoId());
                } else {
                    obj = pparser.getAction(action.getLOGotoId());
                }
                if (null != obj) {
                    loadAction(obj);
                }
                obj = null;
            }
            action = null;
        } else if (currst == 3) {
            DisplayAction action = (DisplayAction) pparser.getCurrentObjject();
            if (null != action.getLOString()) {
                Object obj = null;
                if (action.isSmartBack()) {
                    obj = pparser.getPreviousAction(action.getLOGotoId());
                } else {
                    obj = pparser.getAction(action.getLOGotoId());
                }
                if (null != obj) {
                    loadAction(obj);
                }
            }
            action = null;
        }
    }

    /**
     * Method to Handle Back Option Selected
     **/
    private void handleBackOption(boolean isBackLoad) {
        if (4 == currst) {
            currst = 3;
            loadCurrentState();
        } else {
            Object prev = pparser.getPreviousAction(isBackLoad);
            if (null != prev) {
                msgid = null;
                receivedMsg = null;
                isSelect = false;
                String[] values = null;
                byte state = 0;
                if (ProfileTypeConstant.ENTRYACTION == pparser.getType()) {
                    state = 2;
                    EntryAction action = (EntryAction) prev;
                    if (action.isIsRecord()) {
                        values = getRecordNameAndField(action.getScprefix(), true);
                        if (null != values && values.length > 1) {
                            rParser.removeLastStoreValue(values[0], values[1]);
                            values = getLastDisplyedItemValue(action.getScprefix(), action.getSep());
                        } else {
                            values = new String[]{rParser.getLastSelectedValue(action.getScprefix(), action.getRDFormat()), "0"};
                        }
                    } else {
                        values = getLastDisplyedItemValue(action.getScprefix(), action.getSep());
                    }
                    action = null;
                } else if (ProfileTypeConstant.MENU == pparser.getType()) {
                    state = 1;
                    if (null != ((MenuItemList) prev).getName()) {
                        values = getLastDisplyedItemValue(((MenuItemList) prev).getName(), null);
                    }
                } else {
                    values = getLastDisplyedItemValue(null, null);
                }
                if (isBackLoad) {
                    loadAction(prev);
                    ObjectBuilderFactory.GetKernel().lastSelectedItem(values[0], Integer.parseInt(values[1]));
                } else {
                    currst = state;
                }
                values = null;
                isSelect = true;
            } else {
                handleExitProfile();
            }
        }
    }

    /**
     * Method to handle the rename option selection
     *
     * @param itemId
     * @param itemName
     *
     */
    public void handleRename(int itemId, String oldname, String newname) {
        if (currst == 1) {
            if (itemId == -1) {
                if (0 != oldname.compareTo(newname)) {
                    if (0 == "".compareTo(newname)) {
                        newname = oldname;
                    } //                    else if (!qparser.isQueryNameExits(newname)) {
                    //                        qparser.renameQuery(oldname, newname);
                    //                    }
                    else {
                        newname = oldname;
                        // bug Id 4294
                        loadMessageBox(16, Constants.popupMessage[38], 0, null);
                    }
                }
                ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId, newname);
            }
        } else if (currst == 2) {
            if (itemId != -1 && itemId != -2) {
                if (0 != oldname.compareTo(newname)) {
                    if (0 == "".compareTo(newname)) {
                        newname = oldname;
                    } else if (!eparser.isValueExitsts(newname)) {
                        eparser.isEdited(oldname, newname);
                    } else {
                        newname = oldname;
                        loadMessageBox(16, Constants.popupMessage[39], 0, null);
                    }
                }
                ObjectBuilderFactory.GetKernel().changeMenuItemName(itemId, newname);
            }
        }
    }

    /**
     * Method to handle the delete option selection
     *
     * @param menuId
     *            Menu Id
     * @param itemId
     *            ItemId
     * @param itemName
     *            ItemName
     */
    private void processDiffDeletion() {
//        if (currst == 1) {
//            if (null != eval && qparser.deleteQuery(eval)) {
//                ObjectBuilderFactory.GetKernel().removeMenuItem(iid, eval);
//            }
//        } else
        if (currst == 2) {
            if (null != eval) {
                String historyNameId = null;
                EntryAction action = (EntryAction) pparser.getCurrentObjject();
                if (!action.isPerentry()) {
                    /** * TBC - DeleteEntryShortcut needs scprefix or entry shortcut name * */
                    if (action.isIsRecord()) {
                        String[] values = getRecordNameAndField(action.getScprefix(), true);
                        if (null != values) {
                            if (values.length > 1) {
                                eparser.DeleteEntryShortcut(eval);
                            } else {
                                //bug 12880
                                if (action.getScprefix().compareTo("history") == 0) {
                                    rParser.setSelectedRecordValue(action.getScprefix(), iid);
                                    historyNameId = rParser.getSelectedElementValue("history", "friendID", false);
                                }
                                rParser.deleteRecord(values[0], iid);
                            }
                            values = null;
                        }
                    } else {
                        //if(action.getScprefix().startsWith(global)){
                        //  globalMemorize.removeValue(action.getScprefix());
                        //globalParser.DeleteEntryShortcut(eval);
                        //} else {
                        eparser.DeleteEntryShortcut(eval);
                        //}
                    }
                } else {
                    deletePershableEntry(action.getScprefix(), eval);
                }
                //CR 12880
                if (null != historyNameId) {
                    ChatHistoryHandler.deleteHistory(header.getName(), historyNameId);
                }

                ObjectBuilderFactory.GetKernel().removeMenuItem(iid, eval);
                action = null;
            }
        } else if (currst == 3) {
            if (null != msgid) {
                ObjectBuilderFactory.GetKernel().deleteViewMessage(msgid);
                msgid = null;
                receivedMsg = null;
            }
            loadMenu((MenuItemList) pparser.getInitialMenu());
        }
        iid = -1;
        eval = null;
    }

    /**
     * Method to HandleEscapeText Option Clicked for the given actionId
     *
     * @param actionId
     *            SelectedActionId
     * @param itemId
     *            selected ItemId
     * @param escapeText
     *            Selected OptionText
     */
    private void handleEscapeText(String escapeText) {
        Object obj = null;
        if (null != escapeText) {
            obj = getEscapeAction(escapeText);
        }
        if (null != obj) {
            loadAction(obj);
        } else {
            loadMenu((MenuItemList) pparser.getInitialMenu());
        }
        obj = null;
    }

    /**
     *
     **/
    private Object getEscapeAction(String sText) {
        EscapeText[] esTxt = pparser.getEscapeTxt();
        if (null != esTxt) {
            int len = esTxt.length;
            String temp = null;
            for (int i = 0; i < len; i++) {
                temp = replaceVariableData(esTxt[i].getEsText(), null, false,
                        true, true, true);
                if (temp.compareTo(sText) == 0) {
                    return pparser.getAction(esTxt[i].getGotoId());
                }
            }
        }
        return null;
    }

    /**
     * Method to Handle the Exit Profile.. When the User Select the Exit Profile
     * this Method to be Invoked and Check the Scheduler is Running or not
     * Suppose the Scheduler is Running Load the Security otherwise exit the
     * Profile
     */
//	private void handleExitProfile() {
//            ObjectBuilderFactory.GetKernel().reLaunchApplication();
//	}
    /**
     *
     */
    private String getSearchString(String value, String stStr, String endStr) {
        int sInd = value.indexOf(stStr);
        int eInd = value.indexOf(endStr);
        if (sInd > -1 && eInd > -1) {
            sInd++;
            return value.substring(sInd, eInd);
        }
        return null;
    }

    /**
     * Method to store the valeu to the Sequence Object
     *
     *
     * @param menuId
     * @param itemId
     * @param itemName
     * @param value
     * @param isVariable
     * @param Seperator
     */
    private void StoreSequnece(int id, String itemName, String value, boolean isVariable, boolean isMulVal, String sep, boolean isNotMemorize) {
        int len = 0;
        if (null != itemName) {
            if (null != seqArray) {
                len = seqArray.length;
                for (int i = 0; i < len; i++) {
                    if (itemName.compareTo(seqArray[i].getSelectedName()) == 0) {
                        if (isMulVal) {
                            value = seqArray[i].getSelectedValue() + sep + value;
                        }
                        seqArray[i].setSelectedValue(value);
                        //if (id > 0 || seqArray[i]) {
                        seqArray[i].setId(id);
                        //}
                        return;
                    }
                }
            }

            Sequence[] temp = new Sequence[len + 1];
            if (len > 0) {
                System.arraycopy(seqArray, 0, temp, 0, len);
            }

            temp[len] = new Sequence();
            temp[len].setIsVariable(isVariable);
            temp[len].setId(id);
            temp[len].setSelectedValue(value);
            temp[len].setSelectedName(itemName);
            temp[len].setIsNotMemorize(isNotMemorize);

            seqArray = temp;
            temp = null;
        }
    }

    private boolean remvoeStoreSequence(String name) {
        boolean isRemoved = false;
        if (null != seqArray) {
            int count = seqArray.length;
            for (int i = 0; i < count; i++) {
                if (seqArray[i].getSelectedName().compareTo(name) == 0) {
                    Sequence[] temp = seqArray;
                    seqArray = new Sequence[count - 1];
                    System.arraycopy(temp, 0, seqArray, 0, i);
                    System.arraycopy(temp, i + 1, seqArray, i, (count - (i + 1)));
                    temp = null;
                    isRemoved = true;
                    break;
                }
            }
        }
        return isRemoved;
    }

    /**
     * Method to retrieve the Last Store Sequence, Suppose the Last Sequence is value Having the
     * Multiple Value the it will not remove the SequenceArray and split the Last value only
     **/
    private String[] getLastDisplyedItemValue(String name, String sep) {
        String[] lsName = new String[]{null, "-3"};
        if (null != seqArray) {
            int len = seqArray.length - 1;
            int pos = -1;
            for (int i = len; i > -1; i--) {
                if (seqArray[i].getId() != -1 && (null == name || name.compareTo(seqArray[i].getSelectedName()) == 0)) {
                    pos = i;
                    break;
                }
            }
            if (pos != -1) {
                lsName[0] = seqArray[pos].getSelectedValue();
                lsName[1] = seqArray[pos].getId() + "";
                int index = -1;
                if (null != sep) {
                    index = Utilities.getLastIndex(lsName[0], sep);
                }
                if (index > -1) {
                    seqArray[pos].setSelectedValue(lsName[0].substring(0, index));
                    lsName[0] = lsName[0].substring(index + 1);
                } else {
                    if (len > 0) {
                        Sequence[] temp = seqArray;
                        seqArray = new Sequence[len];
                        if (len == pos) {
                            System.arraycopy(temp, 0, seqArray, 0, len);
                        } else {
                            System.arraycopy(temp, 0, seqArray, 0, pos);
                            System.arraycopy(temp, pos + 1, seqArray, pos, len - pos);
                        }
                        temp = null;
                    } else {
                        seqArray = null;
                    }
                }
            }
        }
        return lsName;
    }

    /**
     * Method to Load the Error Screen for the Given Error Messge When the
     * Exception is Raised this method to be invoked and show the Error Screen
     *
     * @param msg
     *            Message Represent the display Error Message
     */
    public void LoadErrorMessageScreen(Exception exp, String msg) {
        Logger.loggerError("Profile Handler-> " + msg + exp.toString());
        loadMessageBox(4, msg, 1, null);
    }

    /**
     *
     */
//    private int[] getVarIds(int id) {
//        int len = 0;
//        int varLen = 0;
//        int[] varIds = new int[1];
//        if (null != seqArray && (len = seqArray.length) > 0) {
//            for (int i = 0; i < len; i++) {
//                if (seqArray[i].isIsVariable()) {
//                    varLen++;
//                }
//            }
//            varIds = new int[varLen + 1];
//            for (int i = 0, j = 0; i < len; i++) {
//                if (seqArray[i].isIsVariable()) {
//                    varIds[j] = seqArray[i].getId();
//                    j++;
//                }
//            }
//        }
//        varIds[varLen] = id;
//        return varIds;
//    }
//    private String[] getFixedValue() {
//        int len;
//        if (null != seqArray && (len = seqArray.length) > 0) {
//            String[] fValue = null;
//            int varLen = 0;
//            for (int i = 0; i < len; i++) {
//                if (!seqArray[i].isIsVariable() && null != seqArray[i].getSelectedValue()) {
//                    varLen++;
//                }
//            }
//            if (varLen > 0) {
//                fValue = new String[varLen + varLen];
//                for (int i = 0, j = 0; i < len; i++) {
//                    if (!seqArray[i].isIsVariable() && null != seqArray[i].getSelectedValue()) {
//                        fValue[j++] = seqArray[i].getSelectedName();
//                        fValue[j++] = seqArray[i].getSelectedValue();
//                    }
//                }
//                return fValue;
//            }
//        }
//        return null;
//    }
//    private String getShortCode(String value) {
//        value = replaceVariableData(value, null, false, true, true);
//        if (value.indexOf("[") > -1) {
//            return value;
//        }
////            int len;
////            if(null != getSearchString(value,"[","]")){
////                if (null != seqArray && (len = seqArray.length) > 0) {
////                    String serText = null;
////                    for (int i = 0; i < len; i++) {
////                        serText = "[" + seqArray[i].getSelectedName() + "]";
////                        value = Utilities.replace(value, serText, seqArray[i]
////                                        .getSelectedValue());
////                    }
////                }
////
////                //Propagate Value(keyword, Shortcode and Msg Arrival Time
////                value = getPropagetValue(value);
////
////                value = getMemValue(value,false);
////            }
//        ObjectBuilderFactory.GetKernel().registerNewNumers(value, header.getName(), header.getMSCF(), header.getAbbreviation());
//        return value;
//    }
    //CR 5106
    private String replaceJadAttributevalue(String value) {
        String temp = value;
        String serText = null;
        String jadValue = null;
        while (null != (serText = getSearchString(temp, "[", "]"))) {
            if (serText.startsWith("JAD-")) {
                jadValue = ChannelData.getJADValue(serText);
                if (null != jadValue) {
                    value = Utilities.replace(value, "[" + serText + "]", jadValue);
                    jadValue = null;
                }
            }
            temp = Utilities.replace(temp, "[" + serText + "]", "");
        }
        return value;
    }

    private String replaceMemoGlobalValue(String value, String compareValue, String checkingValue, String setValue, boolean isDis) {
        int len;

        if ((len = value.toLowerCase().indexOf(compareValue.toLowerCase())) > -1) {
            String serText = getMemValue(checkingValue, isDis);
            serText = GlobalMemorizeVariable.getValue(serText);
            if (null == serText) {
                if (null != setValue) {
                    serText = setValue;
                    GlobalMemorizeVariable.add(checkingValue, setValue);
                }
            }
            //value = Utilities.replace(value,"["+checkingValue+"]", serText);
            if (null != serText) {
                value = value.substring(0, len) + serText + value.substring(len + compareValue.length());
            }
        }
        return value;
    }

    /**
     *
     */
    private String replaceVariableData(String value, String setValue, boolean isdis,
            boolean isSend, boolean isNotEmpty, boolean isAvoidEscapeText) {
        int len;
//        isReplaced = false;
        if (null != value) {
            //int count = 0;
            String serText = null;

            //CR 14035
            if (value.indexOf(ChannelData.allContacts) > -1) {
                serText = Contacts.getUploadContacts(",");
                value = Utilities.replace(value, ChannelData.allContacts, serText);
            }

            value = replaceJadAttributevalue(value);

            //bug id 4522
            value = replaceMemoGlobalValue(value, ChannelData.indireShortCodeName, ChannelData.globalShortCode, ChannelData.getShortcode(), isdis);

            //CR 12069
            //bug 13351
            value = replaceMemoGlobalValue(value, "[" + ChannelData.globalUserPhone + "]",
                    ChannelData.globalUserPhone, Settings.getPhoneNumber(), isdis);

            //CR 12988
            value = replaceMemoGlobalValue(value, "[" + ChannelData.globalUserId + "]",
                    ChannelData.globalUserId, Settings.getUID(true), isdis);


            //<|CR 12069>
            if ((len = value.toLowerCase().indexOf(ChannelData.globalBearerProtocol.toLowerCase())) > -1) {
                //count--;
                serText = getMemValue(ChannelData.globalBearerProtocol, isdis);
                serText = GlobalMemorizeVariable.getValue(serText);
                String temp = "SMS";
                if (Settings.isIsGPRS()) {
                    temp = "Data";
                }
                if (null == serText || serText.compareTo(temp) != 0) {
                    serText = temp;
                    GlobalMemorizeVariable.add(ChannelData.globalBearerProtocol, serText);
                }
                //bug 12854
                //value = Utilities.replace(value,"["+ChannelData.globalBearerProtocol+"]", serText);
                value = value.substring(0, len - 1) + serText + value.substring(len + 1 + ChannelData.globalBearerProtocol.length());
            }

            len = 0;

            value = getRecordValues(value, isSend);

            //Propagate Value(keyword, Shortcode and Msg Arrival Time
            value = getPropagetValue(value);

            value = getMemValue(value, isdis);

            serText = null;
            if (null != seqArray && (len = seqArray.length) > 0) {
                for (int i = 0; i < len; i++) {
                    serText = "[" + seqArray[i].getSelectedName() + "]";
                    if (value.indexOf(serText) > -1 && null != seqArray[i].getSelectedValue()) { //bug id 4047
                        //count--;
                        //Bug 12265
                        if (isAvoidEscapeText || seqArray[i].getId() != -1) {
                            if (isSend && seqArray[i].getSelectedValue().compareTo("null") == 0) {
                                if (!isNotEmpty || seqArray[i].isIsNotMemorize()) {
                                    value = Utilities.replace(value, serText, "");
                                }
                            } else {
                                value = Utilities.replace(value, serText, seqArray[i].getSelectedValue());
                            }
                        }
                    }
                }
            }

            if (null != setValue) {
                while ((serText = getSearchString(value, "[", "]")) != null) {
                    value = Utilities.replace(value, "[" + serText + "]", setValue);
                }
            }
            serText = null;
        }
        return value;
    }

//    private String saveQueryMsg(String value) {
//
//        value = getRecordValues(value, true);
//
//        int len;
//        if (null != seqArray && (len = seqArray.length) > 0) {
//            String serText = null;
//            for (int i = 0; i < len; i++) {
//                serText = "[" + seqArray[i].getSelectedName() + "]";
//                if (!seqArray[i].isIsVariable()) {
//                    value = Utilities.replace(value, serText, seqArray[i].getSelectedValue());
//                }
//            }
//            serText = null;
//        }
//
//        //Propagate Value(keyword, Shortcode and Msg Arrival Time
//        value = getPropagetValue(value);
//
//        return getMemValue(value, false);
//    }
    /**
     *
     * @param value
     * @return
     */
    private String getPropagetValue(String value) {
        if (null != kwValue) {
            if (value.indexOf("[" + kwValue[0] + "]") > -1) {
                value = Utilities.replace(value, "[" + kwValue[0] + "]", kwValue[1]);
            }
        }
        return value;
    }

    /**
     * Method to replace the Record Values for the given Query type
     * @param value replacing Query Type
     * @return Replaced value
     */
    private String getRecordValues(String value, boolean isSend) {
        if (null != header.getRecords()) {
            String text = null;
            String tempStr = value;
            String sValue = null;
            String[] rField = null;
            int index = -1;
            while (null != (text = getSearchString(tempStr, "[", "]"))) {
                if (null != (rField = getRecordNameAndField(text, true))) {
                    if (rField.length > 1) {
                        if ((index = text.indexOf(":")) > -1) {
                            rField[0] += text.substring(index);
                        }
                        sValue = rParser.getSelectedElementValue(rField[0], rField[1], isSend);
                    } else {
                        sValue = rParser.getSelectedValue(text, isSend, true);
                    }
                }
                if (null != sValue) {
                    if (value.indexOf("[" + text + "]") > -1) {
                        value = Utilities.replace(value, "[" + text + "]", sValue);
                    }
                }
                tempStr = Utilities.replace(tempStr, "[" + text + "]", "");
                sValue = null;
            }
            rField = null;
            tempStr = null;
            text = null;
        }
        return value;
    }

    //
    private String getMemValue(String value, boolean isdis) {
        String text = null;
        String tempStr = value;
        String memValue = null;
        while (null != (text = getSearchString(tempStr, "[", "]"))) {
            if (text.startsWith(globalMem)) {
                memValue = GlobalMemorizeVariable.getValue(text);
                if (null == memValue) {
                    memValue = ChannelData.getJADValue(text);
                    if (null != memValue) {
                        GlobalMemorizeVariable.add(text, memValue);
                    } else if (text.indexOf("Pending") > -1) {
                        memValue = "0";
                    }
                }
            } else {
                memValue = pparser.getMemorizedValue(text, isdis);
            }
            if (null != memValue) {
                if (value.indexOf("[" + text + "]") > -1) {
                    value = Utilities.replace(value, "[" + text + "]", memValue);
                }
            }
            tempStr = Utilities.replace(tempStr, "[" + text + "]", "");
        }
        memValue = null;
        tempStr = null;
        text = null;
        return value;
    }

    /**
     * Method to Handle to store the Sequence for the Given SequenceName
     *
     * @param qName
     *            SequenceShortcutName
     */
//    private void saveQuerySequence(String qName) {
//        if (null != qName && qName.compareTo("") != 0) {
//            if (!qparser.isQueryNameExits(qName)) {
//                String msg = Constants.popupMessage[41];
//                if (!qparser.saveQuery(qName)) {
//                    msg = Constants.popupMessage[40];
//                } else {
//                    storeIndex = false;
//                }
//                currst = 3;
//                loadCurrentState();
//                loadMessageBox(16, msg, 0, null);
//            } else {
//                loadMessageBox(16, Constants.popupMessage[38], 0, null);
//            }
//        }
//    }
    /**
     *
     */
    private void handleInboxOption() {
        ObjectBuilderFactory.GetKernel().loadInbox();
    }

    /**
     *
     */
    private void handleReplyOrForwardOption(String msgId, boolean isReply) {
        ObjectBuilderFactory.GetKernel().loadReplyOrForwardOptionSelect(msgId, isReply, receivedMsg);
    }

    /**
     * Method To Load The MessageBox For The Given msgType
     *
     * @param menuId
     *            MenuId
     * @param message
     *            Message
     * @param msgType
     *            MessageType
     */
    private void loadMessageBox(int msgType, String msg, int msgst, String hText) {
        this.msgst = (byte) msgst;
        ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg, hText);
    }

    /**
     * Method To Handle The Message box
     *
     * @param menuId
     *            Menu Id
     * @param itemId
     *            Item Id
     * @param optionText
     *            Option Text
     * @param status
     *            OK/Cancel
     *
     *  1 - Profile Exit
     *  2 - Payfor serveice Status Update
     *  3 - Invoke Browser
     *  4 - Update User permission for Password
     *  5 - Delete Query Shortcut
     *  6 - App Download
     *  7 - Goto App Name
     *  8 - Call Invoke Popup
     */
    public void handleMessageBox(boolean status) {
        byte tMsgSt = msgst;
        msgst = 0;
        if (status) {
            if (1 == tMsgSt || 7 == tMsgSt) {
                handleExitProfile();
            } else if (3 == tMsgSt) {
                invokeUrl();
            } else if (5 == tMsgSt) {
                processDiffDeletion();
            } else if (tMsgSt == 4) {
                updatePasswordStatus(status);
            } else if (6 == tMsgSt) {
                if (currst == 3) {
                    ObjectBuilderFactory.GetKernel().removeOption();
                }
                startDownload();
            } else if (8 == tMsgSt) {
                try {
                    ObjectBuilderFactory.GetProgram().platformRequest(telNumber);
//                         ObjectBuilderFactory.getPCanvas().platformRequest(telNumber);
                } catch (Exception e) {
                }
            } else if (9 == tMsgSt) {
//                if(pparser.getLastSMSSendAction()>-1){
//                    ObjectBuilderFactory.GetKernel().resendMessage();
//                    setMsgWaitingTime(); //CR 9710
//                }
                if (pparser.getLastSMSSendAction() > -1) { //CR 9710
                    Object obj = pparser.getAction(pparser.getLastSMSSendAction());
                    loadAction(obj);
                }
            } else if (10 == tMsgSt) {
                loadMenu((MenuItemList) pparser.getInitialMenu());
            } else if (11 == tMsgSt) { //CR 12165
                loadSmsSend((SMSSendAction) pparser.getCurrentObjject(), true);
            }
        } else {
            if (2 == tMsgSt) {
                pparser.setPayForService();
            } else if (4 == tMsgSt) {
                updatePasswordStatus(status);
            } else if (6 == tMsgSt) {
                doAction = null;
                isDownloadDATWait = false;
                loadAction(pparser.getInitialMenu());
            } else if (9 == tMsgSt) {
                loadMenu((MenuItemList) pparser.getInitialMenu());
            } else if (11 == tMsgSt) { //CR 12165
                handleBackOption(false);
            }
        }

        telNumber = null;
    }

    /**
     * Method to handle the Exit the loaded Profile,
     *  Suppose the user is in the StartUp widget means he can click the exit Profile Means we can send the
     *  Provenence message also
     */
    public void handleExitProfile() {
        Object obj = pparser.GetExitNodeObject();
        if (null != obj) {
            loadAction(obj);
            obj = null;
        } else {
            if (isFeature) {
                ObjectBuilderFactory.GetKernel().reLaunchProfiles();
            } else {
                ObjectBuilderFactory.GetKernel().reLaunchApplication();
            }
        }
    }

    /**
     * Method to get the selected ad object (suppose the selected having the imediate ad action to process the imediate action)
     *
     *  <li> 1. Call Action </li>
     *  <li> 3. Browser Invoke Action </li>
     *
     * @return Selected AdData objectt(this object having profile id also)
     */
    public AdData getSelectedAd() {
        adData.setPrId(header.getPId());
        if (null != adData.getLPag()) {
            if (adData.isIsImdAct()) {
                if (null != adData.getPNo()) {
                    ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(), adData.getAdId());
                    ObjectBuilderFactory.getControlChanel().sendAdCallItemSelectAction(adData.getPrId(), adData.getAdId(), 1);
                    invokeCall(adData.getPNo());
                } else if (null != adData.getUrl()) {
                    if (null != (url = ChannelData.geturl(adData.getUrl()))) {
                        //   ObjectBuilderFactory.getControlChanel().sendAdMenuItemSelectAction(adData.getPrId(),adData.getAdId(),3);
                        loadMessageBox(5, Constants.popupMessage[1], 3, Constants.headerText[9]);
                    }
                } else {
                    ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(), adData.getAdId());
                    boolean isSet = true;
                    int len = adData.getLANDPAGE_SIZE() * 8;
                    for (int i = 0; i < len && isSet; i++) {
                        if (adData.getLPag()[i] && i != 8) {
                            isSet = false;
                            ObjectBuilderFactory.getControlChanel().sendAdMenuItemSelectAction(adData.getPrId(), adData.getAdId(), i);
                            loadMessageBox(16, Constants.popupMessage[2], 0, null);
                            break;
                        }
                    }
                    if (isSet) {
                        loadMessageBox(16, Constants.popupMessage[42], 0, null);
                    }
                }
                return null;
            }
            ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(), adData.getAdId());
            return adData;
        }
        return null;
    }

    public String getProfileId() {
        return header.getPId();
    }

    /**
     * Method to Load the Selected Presenter
     */
    public void loadCurrentState() {
        setCurrentPresenter(pLoc);
//        if (currst == 4) {
//            loadEntrySaveQuery();
//        } else
        {
            Object _obj = pparser.getCurrentObjject();
            if (currst == 3) {
                DisplayAction dAct = (DisplayAction) _obj;
                if (dAct.getDispimage() == ProfileTypeConstant.Display.DISPLAY_CHAT) {
                    loadChatScreen(dAct);
                } else if (null != msgid) {
                    String[] msg = null;
                    if (dAct.getDispimage() == ProfileTypeConstant.Display.DISPLAY_INFO) {
                        msg = new String[2];
                        msg[0] = replaceVariableData(dAct.getInfo(), null, true, true, false, true);
                        loadViewScreen(dAct, msg, null);
                    } else {
                        // bug id 4854
                        if (null != receivedMsg) {
                            msg = new String[]{receivedMsg[0], receivedMsg[1]}; //bug id 8504
                            loadViewScreen(dAct, msg, msgid);
                        } else {
                            loadMenu((MenuItemList) pparser.getInitialMenu());
                        }
                    }
                    dAct = null;
                } else if (null != doAction && isDownloadDATWait) {
                    loadMessageBox(5, Constants.popupMessage[1], 6, Constants.headerText[9]);
                } else {
                    loadAction(_obj);
                    if (multiPartCount > 1 && null != maxCount) {
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Create display string with multiPartCount = "+multiPartCount+" and maxcount = "+maxCount );
                        //#endif
                        String displayString = Constants.appendText[30] + " " + multiPartCount + " " + Constants.appendText[31] + " " + maxCount;
                        ObjectBuilderFactory.GetKernel().multiPartMessage(null, displayString);
                    }
                }
            } else {
                loadAction(_obj);
            }
            _obj = null;
        }
    }

    /**
     * Method to handle the download process is done or not, if the process is done it will move to next action otherwise
     *  it will move to initial menu
     *
     * @param status
     */
    public void loadNextState(boolean status) {
        isDownloadDATWait = false;
        setCurrentPresenter(pLoc);
        if (currst == 3 && null != doAction) {
            doAction = null;
            if (status) {
                Object obj = pparser.getNextAction(-1);
                if (null != obj) {
                    loadAction(obj);
                } else {
                    DisplayAction dAction = (DisplayAction) pparser.getCurrentObjject();
                    if (dAction.getGotoid() == -2) {
                        loadNextAction(-2);
                    } else {
                        loadAction(pparser.getInitialMenu());
                    }
                    dAction = null;
                }
            } else {
                loadAction(pparser.getInitialMenu());
            }
        } else {
            doAction = null;
            loadAction(pparser.getInitialMenu());
        }
    }

    /**
     * Method to handle the sequence shortcut selection
     *
     * @param menuId
     *            Menu Id
     */
//    private boolean handleQuerySelection() {
//        int id = qparser.getNextAction();
//        if (id > -1) {
//            Object obj = pparser.getAction(id);
//            loadAction(obj);
//            obj = null;
//        }
//        return true;
//    }
    /**
     * Method to Get the Sending message string from the SequencePrase Xml file
     * more than one message is stored in the Same SequenceshortcutName retrieve
     * the one by one and send the Message. Suppose the Send Message is want any
     * value this SMS is consider as the Variable SMS so this SMS is Handle the
     * HandleSequenceShortcutSelection function
     */
//    private void loadQuery(String qName) {
//        if (null != qparser) {
//            isSeqSel = true;
//            qparser.loadQuery(qName);
//            setFixedValue();
//            handleQuerySelection();
//        }
//    }
//    private void setFixedValue() {
//        String[] fvalue = qparser.getFixValue();
//        int count;
//        if (null != fvalue && (count = fvalue.length) > 0) {
//            for (int i = 0; i < count; i += 2) {
//                StoreSequnece(0, fvalue[i], fvalue[i + 1], false, false, " ", true);
//            }
//        }
//    }
    private void sendChatMessage(String sendSMS, String chatId, String chatName,
            String plusUser, String chatQueryType) {
        pparser.increaseProfileUsage();
        String sCode = header.getScode()[0];
        Message message = new Message();
        message.setIsSMSSEND(true);
        message.setMessagePlus(plusUser);
        if (sCode.indexOf("[") > -1) {
            sCode = replaceVariableData(sCode, null, false, true, true, false);
            ObjectBuilderFactory.GetKernel().registerNewNumers(sCode, header.getName(), header.getMSCF(), header.getAbbreviation());
        }

        //CR 14326
        chatQueryType = replaceVariableData(chatQueryType, null, true, false, true, true) + sendSMS;
        message.setSFullName(header.getName());
        message.setShortcode(sCode);
        message.setChatId(chatId);
        message.setChatName(chatName);
        message.setCurRMsg(sendSMS);
        message.setRMsg(new String[]{chatQueryType});
        message.setMscf(header.getMSCF());
        message.setIsNotNewMsg(true);
        message.setAbberVation(header.getAbbreviation());
        message.setInboxFunc(true);
        message.setChatSequence(GlobalMemorizeVariable.getChatSequenceNumber());

        //CR 14327
        if (header.getAbbreviation().indexOf("VMP") > -1) {
            GlobalMemorizeVariable.updateChatCharacterSequence((byte) 1);
        }

        ObjectBuilderFactory.GetKernel().sendMessage(message);////CR 11975
    }

    /**
     * Method to Send the Message from the Given number
     *
     * @param msg
     * @param qType
     * @param pName
     * @param sCode
     * @param mWords
     * @param mMWords
     * @return
     *
     */
    private boolean sendMessage(String msg, String qType,
            String sCode, boolean isNoNewMSG,
            boolean dontSaveInbox, boolean isIntLoop, boolean isLogin) {

        Message message = new Message();
        //Cr 14139
        message.setIsSMSSEND(isLogin);
        message.setRMsg(new String[]{msg});
        message.setShortcode(sCode);
        message.setMscf(header.getMSCF());
        message.setSFullName(header.getName());
        message.setQueryType(qType);
        message.setIsNotNewMsg(isNoNewMSG);
        message.setDontSaveInbox(dontSaveInbox);
        message.setAbberVation(header.getAbbreviation());
        message.setInboxFunc(true); // CR 11975
        message.setIsNotChatMessage(true);
//        message.setIsPost(isUpload);
        pparser.increaseProfileUsage();
        isSent = true;

        //<-- CR 13617
        if (msg.toLowerCase().indexOf(RecordManager.startAppName.toLowerCase()) > -1
                && (msg.indexOf("FONEREG") > -1
                || msg.indexOf("LOGIN") > -1)) {
            isRegister = true;
        } else {
            isRegister = false;
        }
        // CR 13617 -->

        //CR 14324
        message.setIsDateTimeSend(isRegister);

        if (null != sCode) {
            sCode = Utilities.remove(sCode, "\n").trim();
        }
        if ((header.isLBack() && (null == sCode || sCode.length() == 0)) || isIntLoop) { //&& null == sCode)
            isSent = false;
            if (!isGotoWidgetName(_saction.getGotoWidgetName())) {
                if (null == sCode && sCode.length() == 0) {
                    sCode = "00000";
                }
                loadNextAction(_saction.getGotoid());
                loopBackMessage = new Message();
                loopBackMessage.setShortcode(sCode);
                loopBackMessage.setSFullName(header.getName());
                loopBackMessage.setRMsg(new String[]{msg});
                loopBackMessage.setCurRMsg(msg);
                loopBackMessage.setQueryType(qType);
                loopBackMessage.setMscf("st");
                loopBackMessage.setIsNotNewMsg(_saction.getNoNewMSG());
                loopBackMessage.setDontSaveInbox(dontSaveInbox);
                loopBackMessage.setIsInboxReply(header.isIsInboxReply());
                loopBackMessage.setIsDWRes(_saction.isDWResponse());
                loopBackMessage.setIsDSend(_saction.isDSendMsg());
                loopBackMessage.setAbberVation(header.getAbbreviation());
                if (!_saction.isDSendMsg()) {
                    ObjectBuilderFactory.GetKernel().writeLogQuery(message);
                    sequenceNumber[0] = "st";
                    sequenceNumber[1] = (new Date().getTime()) + "";
                    multiPartCount = 1;
                    maxCount = null;
                    handleLoopBack();
                }
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("sCode "+sCode);
                //#endif
                if (null != _saction.getGotoWidgetName()) {
                    loadMessageBox(4, Constants.appendText[14] + " " + _saction.getGotoWidgetName() + " " + Constants.popupMessage[16] + " " + _saction.getGotoWidgetName() + " "
                            + Constants.popupMessage[17], 7, null);
                }
            }
        } else {
            if (_saction.isDSendMsg()) {
                isSent = false;
            }
            message.setIsDSend(_saction.isDSendMsg());
            message.setIsDWRes(_saction.isDWResponse());
            multiPartCount = 1;
            maxCount = null;
//            //#if VERBOSELOGGING
//            //|JG|Logger.debugOnError("Before Go to kernel>sendMessage()");
//            //#endif //11801
            sequenceNumber = ObjectBuilderFactory.GetKernel().sendMessage(message);////CR 11975
//            //#if VERBOSELOGGING
//            //|JG|Logger.debugOnError("After kernel>sendMessage()");
//            //#endif //11801
            if (!isGotoWidgetName(_saction.getGotoWidgetName())) {
                loadNextAction(_saction.getGotoid());
                //ObjectBuilderFactory.GetKernel().receiveMessage(null, null, ChannelData.getShortcode());
                if (null != _saction) {
                    //setMsgWaitingTime();
                    if (null != _saction.getGotoWidgetName()) {
                        loadMessageBox(4, Constants.appendText[14] + " " + _saction.getGotoWidgetName() + " " + Constants.popupMessage[16] + " " + _saction.getGotoWidgetName() + " "
                                + Constants.popupMessage[17], 7, null);
                    }
                }
            }
            //   ObjectBuilderFactory.GetKernel().receiveMessage(msg, header.getName(), sCode);
        }
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Send message:true");
        //#endif
        return true;
    }

    /**
     *Method to Hadle the profile Loop Back option
     **/
    private void handleLoopBack() {
        if (header.getLBdelay() > 0) {
            loopBackMessage.setRtDelay(header.getLBdelay() * 1000);
            lBTimer = new Timer();
            lBTimer.schedule(new loopBackDelayTimer(), header.getLBdelay() * 1000);
            //setMsgWaitingTime(); //CR 10203
        } else {
            sendLoopBackMsg();
        }
    }

    public void sendLoopBackMsg() {
        if (null != lBTimer) {
            lBTimer.cancel();
            lBTimer = null;
        }
        if (null != loopBackMessage) {
            setMsgWaitingTime(); //CR 10203
            ObjectBuilderFactory.GetKernel().messageReceived(0, loopBackMessage, false);
        }
    }

    private void setMsgWaitingTime() {
        if (null != mRDNTimer) {
            mRDNTimer.cancel();
            mRDNTimer = null;
        }
        mRDNTimer = new Timer();
        mRDNTimer.schedule(new MsgRecDelayNotificationTimer(), 1000 * (60 * 3));
    }

    public void raiseMsgDelayNotification(boolean isRaise) {
        if (null != mRDNTimer) {
            mRDNTimer.cancel();
            mRDNTimer = null;
        }
        if (isRaise && currst == 3) {
            ObjectBuilderFactory.GetKernel().raiseNotification(Constants.popupMessage[43]);
        }
    }

    /**
     * Method to find and load the Specified widget for the Given Widget Name
     * @param wName goto Widget Name
     * @return Boolean (widget is Present return true otherwise false)
     */
    private boolean isGotoWidgetName(String wName) {
        if (null != wName) {
            boolean isFeatureApp = false;
            String loc = ObjectBuilderFactory.GetKernel().getProfileLocation(wName);
            if (null == loc) { // bug 10707
                loc = RecordManager.getFeatureAppName(wName);
                isFeatureApp = true;
            }
            if (null != loc) {
                ObjectBuilderFactory.GetKernel().loadProfile(wName, loc, isFeatureApp, false);
                return true;
            }
        }
        return false;
    }

    /**
     * Method to retrive the Advertisement
     *
     * @return String
     */
    private String getAdText() {
        String adText = null;

        if (isAdget) {
            isAdget = false;
            if (header.isIsStaticAd()) {
                adData = ObjectBuilderFactory.GetKernel().getAdvertisement(header.getCategory());
                if (null != adData && null != adData.getLPag() && adData.isIsDCDSend()) {
                    ObjectBuilderFactory.getControlChanel().addDisplayed(header.getPId(), adData.getAdId());
                }
            }
        }
        if (null != adData) {
            adText = adData.getAdText();
        }

        return adText;
    }

    /**
     * Method to save the Parsers Profile
     */
    private void SaveParsers() {
        if (null != pparser) {
            try {
                pparser.shutDownParser();
            } catch (Exception exception) {
            }
            pparser = null;
        }

        SMSProfileHeader[] pHeaders = ObjectBuilderFactory.GetKernel().getSMSProfilesLoaded();
        int count = 0;
        if (null != pHeaders) {
            count = pHeaders.length;
        }
        if (null != eparser) {
            if (eparser.isExternalPropagate()) {
                if (count > 1) {
                    for (int i = 0; i < count; i++) {
                        if (header.getName().compareTo(pHeaders[i].getName()) != 0 && null != pHeaders[i].getCEntryScprefix()) {
                            eparser.UpdateExternalPropagation(pHeaders[i].getName(), pHeaders[i].getCEntryScprefix());
                        }
                    }
                }
            }
            eparser.Deinitialize();
            eparser = null;
        }

//        if(null != globalMemorize){
//            globalMemorize.deinitialize();
//            globalMemorize = null;
//        }

//        if(null != globalParser){
//            globalParser.Deinitialize();
//            globalParser = null;
//        }

//        if (null != qparser) {
//            qparser.shutdownParser();
//            qparser = null;
//        }

        if (null != rParser) {
            if (rParser.isExternalRecord()) {
                if (count > 1) {
                    for (int i = 0; i < count; i++) {
                        if (header.getName().compareTo(pHeaders[i].getName()) != 0) {
                            rParser.propagateExternalValue(pHeaders[i].getName(), pHeaders[i].getRecords(), pHeaders[i].getROption());
                        }
                    }
                }
            }
            rParser.deinitialize();
            rParser = null;
        }

        pHeaders = null;

    }

    /**
     * Method To DeInitialize The All Object And Variables
     *
     * is Call Mode End The Call endCall = 2(Exit The ShartHand Application)
     * endCall = 1(reInitialize The ShartHand Aplication)
     */
    public void DeInitialize() {
        try {
            isDownloadDATWait = false;
            if (null != lBTimer) {
                lBTimer.cancel();
                lBTimer = null;
            }
            isSent = false;
            msgid = null;
            receivedMsg = null;
            raiseMsgDelayNotification(false);
            SaveParsers();
            seqArray = null;
            adData = null;
            _saction = null;
            isFeature = false;
//            isSeqSel = false;
//            storeIndex = false;
            pLoc = null;
            header = null;
            Runtime.getRuntime().gc();
            sequenceNumber = new String[2];
            multiPartCount = 1;
            maxCount = null;
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("App Exited");
            //#endif
        } catch (Exception exception) {
        }
    }

    class loopBackDelayTimer extends TimerTask {

        public void run() {
            sendLoopBackMsg();
        }
    }

    class MsgRecDelayNotificationTimer extends TimerTask {

        public void run() {
            raiseMsgDelayNotification(true);
        }
    }
}


