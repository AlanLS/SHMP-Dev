
import java.io.ByteArrayOutputStream;

public interface IKernel {

        void multiPartMessage(String[] values, String displayString);

        void ChangeModeOrTimer(boolean isRest,boolean isNotTimer);

        void startSocketReaderThread();

        //CR 14675, 14672, 14698
        void updateMessagePlusContacts(String response, byte replaceType);

        //CR 14675, 14672
        void refreshContacts(String[] contacts, int[] contactId);

        //CR 13900, Bug 14832
        void setImage(ByteArrayOutputStream byteArrayOutputStream, boolean isDownload, boolean isGroupAndShout);

        Message sendReconnectOrPollMessage(String reconnectOrPollMessage, boolean isSend);

        void changeHearder(String[] hText);

        byte exitShorthand();

        void setInterpopup(boolean isPopup);

        void startHttpProcess(int processType,String url,String wId, String headerDisplayName);

        void loadCurrentScreen(byte pState,boolean status);

        boolean isDownloadAppPending();

        void startDAPPDownload();

        void displayMessageSendSprit();

        void removeOption();

        String[] getUnReadMsgCount();

        void setLastSelectedWidgetName(String pName);

        void UnRegisterConnection();

        void isNewLoaded();

        void startApplication();

        //Message Listener
        void setSendReceive();

	// Method to Initialize the Which Handler to Load
	void launchApplication();

        void pendingMsgSnd();

        void writeLogQuery(Message message);
        //
        void handleApplicationMessage();

	// Method to Initialize the AppHandler and Load the Initial Profiles
	void loadProfile(String pName,String pLoc,boolean isFeature,boolean isStartup);

        // Method to Load the Sequence Profile (pLoc Profile Location SeqName SequenceName)
//        void loadSequenceProfile(String pLoc,String seqNane);

	// Method to Handle the Item to be Selected for any Presender
	void handleItemSelection(int itemId, String itemName);

	// Method to Handle the option to be selected for any AppHandler
	void handleOptionSelection(int itemId, String itemName, byte optionSelected);

	// Method to Get the Options and this Method to be used SMSHandler and
	// SMSReader classes
	byte[] getOptions(int itemId, String itemName);

	// Method to Handle SMSHandler to Be Rename Any item
	void handleRename(int itemId, String itemName, String newName);

	// Method to Handle the SMSHandler Message box based on the given Status for
	// the SMSHandler
	void handleMessageBox(boolean status, byte msgType);

	// Method to Hdndle the Adverisement is Sellected
	void handleAdSelection();

	// Mathod to Load the Reply Screen
	void loadReplyOrForwardOptionSelect(String messageId, boolean isReply, String[] rMessage);

	// Method to Unload the SmartTouch
	void unLoad();

//        void exitApplication();

	// Method to reInitialize the SmartTouch
	void reLaunchApplication();

        void reLaunchProfiles();

	// Method to retrive the given ProfileName Header
	SMSProfileHeader getProfileHeader(String profileName);

        //CR 4924
        //Method to retrieve the MoreApp Header Details;
        SMSProfileHeader getFeatureApp();

	// Method to Remove the Profile Header for Given ProfileName
	void removeProfileHeader(String profileName);

	// Method to retrieve the Message for the Given MessageId
	String[] getViewMessage(String messageId);

	// Method to Handle the Notification selection..
	// when Message is Received automatically Notification Notification is
	// Raised
	// Parameter messageId is Represent the Receive Message Id
	void handleNotificationSelection(boolean isGoto);

	// Method to load the Application After the Security Checked
	void loadAppication();

        //
        void clearSetting();

	// Method to return the Profiles ArrayList
	SMSProfileHeader[] getSMSProfilesLoaded();

        //Interactive Action Send sMsg Contains Url or Phonenumber
        void sendInteractiveActionMsg(String msgid,String sMsg);

        //Method to register the New Number
        void registerNewNumers(String nNumber,String name,String mCf, String abbreviat);

	// Method to Delete the Message for the Given MessageId
	boolean deleteViewMessage(String messageId);

	// Method to retrieve the Version Number of the SmartTouch
	String getVersionNumber(boolean check);

	// Method to Load the Error Screen
	void loadErrorScreen(Exception exp,String message);

	// Method to Remove the All Profile
	void removeAllProfiles();

//        void displayNotification();

	// Method to Send the Message for the Given Shortcode
        // pID Profile Id
        // sCode Shortcode
        // msg Message
        // qType QueryType
        // mWords MatchWords
        // mMwords MisMatchWords
        // mscf Message Concation Format
        //Dont Send Message
        //No wait Response Message
	String[] sendMessage(Message message);

        //Method to Raise the Message Delay notification
        void raiseNotification(String delayMsg);

	// Method to retrieve the Advertisement Object for the given Profile Category
	AdData getAdvertisement(String profileCatagory);

        void setPreviousBkState(byte pState);

        void deInitializeHandlers(byte state);

        // Method to Retrieve the Profile Location for the given Profile Name
        String getProfileLocation(String pName);

//        String getLaunchIconLocation(String pName);

        // Method to Retrieve the Profile ID for the given Profile Name
        String getProfileNameID(String pName);
        /** */
        void messageReceived(int msrType,Message messageDto,boolean isExit);

	void renameMenuItem(int itemId, String itemName);

	void removeMenuItem(int itemId, String itemName);

	void changeMenuItemName(int itemId,String newname);

	void displayMessageBox(int type, String msg, String hText);

        void displayScreen(Object obj,boolean isSelect);

        //Method to Initialize the Ui Presenter
        void initializeScreen(byte uiState);

	void lastSelectedItem(String lastSelectedItem,int itemId);

	void showGivenValue(String value,boolean  isMaxSet);

	void loadInbox();

        //Method to reorder the Inbox Present values (Ascending to descendign order)
        //Param lasId is Laset Selected Message Id
        void reorder(String lasId);

        void receiveMessage(String msg,String pId,String num, String sequenceNumber);

        void setProfileHeaders();

        void setNewProifleHeaders();

        String getAppAbbervation(String appName);

        int reSyncChatCount(String apperVation,String appName);

        //cr 14333
        void updateChatStatus(int status, String chatSequences, String chatId);
}
