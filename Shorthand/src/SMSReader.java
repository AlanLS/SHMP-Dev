
import generated.Build;



public class SMSReader {

	// Creates a new instance of SMSReaderParser
	private SMSReaderParser parser = null;

	// Variable to hold the response to the other customer
	private String responseMessage = null;

	// Variable to hold the Received Message ShortCode or Send Message ShortCode
	private String sCode = null;

        // Variable to hold the Current state
	private byte currst = -1;

	/**
         * Variable to hold the Message State
	 *   1 - Error State(Exception Raised)
         *   2 -
         *   3 - Invoke the Url Browser
	 */
	private byte msgst;

        // Variable to hold the Previous Stste
        private byte preState = -1;

        //Ad Data object
        private AdData adData = null;

        //Set the Loading Url String
        private String url = null;

        private boolean isInboxLoaded = false;

        private String telNumber = null;

        private String[] receivedMessage = null;

	/**
	 * Constructor to create an instance
         *
         * @throws exception
	 */
	public SMSReader() {
            try {
                parser = new SMSReaderParser();
            } catch (Exception exception) {}
	}

	/**
	 * Method to Load the Error MessageBox with the exception and User message
         *
         * @param exp - Variable will contain the System Exception
         * @param msg - Variable will contain the User error message
	 **/

	public void LoadErrorMessageScreen(Exception exp,String msg) {
		Logger.loggerError("SMSReader-> "+ msg + exp.toString());
        loadMessageBox(4, msg, 1,null);
	}

        /**
         * Method to Load the Message Box
         *
         * @param msgType - variable will contain the Message Type.base don that the options will display
         * @param msg - Variable will conatin the Text to display in the message box.
         * @param msgst - Variable will contain the message state.based on this, message response will handle.
         **/

	private void loadMessageBox(int msgType,String msg,int msgst,String hText){
            this.msgst = (byte)msgst;
            ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg,hText);
	}

	/**
	 *  method to handle message box response
         *
         * @param status - Variable will contain the message response whcih option user selects.
	 */
	public void handleMessageBoxSelection(boolean status) {
            if(status){
                if (1 == msgst){        //Error State
                    loadPreviousScreen();
                } else if(2 == msgst){ // Delete All Message
                    synchronized(parser){
                        if (parser.deleteAllMessages())
                            loadInbox();
                    }
                } else if(3 == msgst){ //Invoke Browser or URL
                    invokeBrowser(url);
                } else if(4 == msgst){ // Delete Selected single Message Id
                //#if VERBOSELOGGING
                //|JG|Logger.loggerError("User confirmed deletion of message");
                    //#endif
                    DeleteSelectedMessage();
                } else if(5 == msgst){
                    try{
                        ObjectBuilderFactory.GetProgram().platformRequest(telNumber);
//                        ObjectBuilderFactory.getPCanvas().platformRequest(telNumber);
                    }catch(Exception e){}
                }
            }
            url = null;
            telNumber = null;
	}

        private void setPresenterDto(boolean isBg){
            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
            PresenterDTO.setHdrtxt(Constants.aName);
            PresenterDTO.setLOptByte((byte)41);
            if(isBg)
                PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
        }

	/**
	 * Method to retrieve the All Received Message
	 *
         * @throws inboxException - Show the error message
	 */
	public void loadInbox() {
		try {
                    isInboxLoaded = true;
                       // PresenterDTO.setBgImage(null,null);// No BackGround Image
                    ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_INBOX);
                    setPresenterDto(false);

                    currst = 1;
                    InboxResponseDTO inboxResponseDto = new InboxResponseDTO();
                    inboxResponseDto.setSecondaryHeaderText(String.valueOf(Constants.options[38]));
                    inboxResponseDto.setLeftOptionText((byte)22); //OPT - index for Back Options

                    synchronized(parser){
                        inboxResponseDto.setMessages(parser.getAllMessages(Settings.getSortType(),Settings.isIsAlpha()));
                        if (null == inboxResponseDto.getMessages()) {
                                PresenterDTO.setLOptByte((byte)-1);
                        } else PresenterDTO.setLOptByte((byte)41);
                        ObjectBuilderFactory.GetKernel().displayScreen(inboxResponseDto,false);
                        ObjectBuilderFactory.GetKernel().lastSelectedItem(parser.getCurrentMsgId(),0);
                    }
                    if(inboxResponseDto.getMessages()!= null && inboxResponseDto.getMessages().length>50)
                        loadMessageBox(3,Constants.popupMessage[44], 0,null); //bug id 4327

                    inboxResponseDto = null;
		} catch (Exception inboxException) {
                    LoadErrorMessageScreen(inboxException,Constants.errorMessage[17]);
		}
	}

    /**
     * Method to Handle the SelectdMessage bassed on the Messageid
     *
     * @param msgId - Selected Messsage Id
     *
     */
	public void HandleItemSelection(String msgId) {
            if (3 == currst || 4 == currst) {       // MessageBox response will handle
                HandleMessageResponse(msgId);
            } else if(5 == currst){ //Hnadle Display Selection
                handledisplySelection();
            } else if (null != parser) {            // Message Information will show
                LoadViewMessage(msgId);
            }
	}

        private void handledisplySelection(){
            if (preState >-1 && !isInboxLoaded)
                    loadPreviousScreen();
            else loadInbox();
        }

        public AdData getSelectedAd(){
            if(null != adData.getLPag()){
                if(adData.isIsImdAct()){
                    if(null != adData.getPNo()){
                        ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(),adData.getAdId());
                        ObjectBuilderFactory.getControlChanel().sendAdCallItemSelectAction(adData.getPrId(),adData.getAdId(),1);
                        InvokeCall(adData.getPNo());
                    } else if(null != adData.getUrl()){
                       // ObjectBuilderFactory.getControlChanel().sendAdMenuItemSelectAction(adData.getPrId(),adData.getAdId(),3);
                        url = adData.getUrl();
                         invokeBrowser(url); //CR 12815
                        //loadMessageBox(5, Constants.popupMessage[1], 3, Constants.headerText[9]);
                    } else{
                        ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(),adData.getAdId());
                        boolean isSet = true;
                        int n = (byte)(adData.getLANDPAGE_SIZE() * 8);
                        for(int i=0;i<n && isSet;i++){
                            if(adData.getLPag()[i]){
                                isSet = false;
                                ObjectBuilderFactory.getControlChanel().sendAdMenuItemSelectAction(adData.getPrId(),adData.getAdId(),i);
                                loadMessageBox(0,Constants.popupMessage[2], 0,null);
                                break;
                            }
                        }
                        if(isSet)
                            loadMessageBox(0, Constants.popupMessage[42], 0,null);
                    }
                    return null;
                }
                ObjectBuilderFactory.getControlChanel().addSelected(adData.getPrId(),adData.getAdId());
                return adData;
            }
            return null;
    }

    private void InvokeCall(String callNum){
        if(null != callNum)
            try {
                if(Build.CALL_INVOKE){
                    telNumber = "tel:"+callNum;
                    loadMessageBox(7, Constants.popupMessage[0]+" "+callNum+"?", 5, Constants.headerText[8]);
                } else {
                    ObjectBuilderFactory.GetProgram().platformRequest("tel:" + callNum);
//                    ObjectBuilderFactory.getPCanvas().platformRequest("tel:" + callNum);
                }
            } catch (Exception ex) { }
        else loadMessageBox(0, Constants.popupMessage[45], 0,null);
    }

    private void invokeBrowser(String url){
        if(null != url){
            try{ //  1.www.google.com   , 2. mobile.google.com   ,  3. http://www.google.com
                if(!url.startsWith(Constants.appendText[13])){
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Invoke URL: http://"+url);
                    //#endif
                    ObjectBuilderFactory.getPCanvas().platformRequest(Constants.appendText[13]+url);
                }else{
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Invoke URL:"+url);
                    //#endif
                    ObjectBuilderFactory.getPCanvas().platformRequest(url);
                }

            }catch (Exception ex) { }
        } else loadMessageBox(0, Constants.popupMessage[34], 0,null);
        url = null;
    }


	/**
	 * Method to View the Requested Message
	 *
	 * @param msgId - Variable will contain the selected Message Id
         *
         * @throws viewMsgexception - Show the Error Message
	 */
	private void LoadViewMessage(String msgId) {
            try {
                boolean isView = false;
                if (null != msgId) {
                    InboxItems inboxItem = null;
                    synchronized(parser){
                        inboxItem = parser.getInboxMessage(msgId, -1, true,true);
                    }
                    if (null != inboxItem) {
                        //vmsgId = msgId;
                        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_VIEW);
                        PresenterDTO.setLOptByte((byte)41);
                        currst = 2;

                        msgId = inboxItem.getMessageId();
//                                    pName = inboxItem.getSender();
                        String floc = ObjectBuilderFactory.GetKernel().getProfileLocation(inboxItem.getSender());
                                //getLaunchIconLocation(inboxItem.getSender());
                        if(null != floc){
                            PresenterDTO.setHdrLogo(RecordManager.getLogoImageName(floc));
                            //CR 12903
                            PresenterDTO.setBgImage(RecordManager.getBackImage(floc));
                        } else {
                            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
                        }

                        ViewSmsResponseDTO viewResponseDto = new ViewSmsResponseDTO();
                        PresenterDTO.setHdrtxt(String.valueOf(Constants.options[38]));
                        viewResponseDto.setLeftOptionText((byte)22); //OPT - index for Back Options
                        if(null != inboxItem.getChennalData()){
                            adData = ObjectBuilderFactory.getControlChanel().getChannelData(inboxItem.getChennalData(),false);
                            adData.setPrId(ObjectBuilderFactory.GetKernel().getProfileNameID(inboxItem.getSender()));
                            if(null != adData.getUrl())
                                adData.setUrl(ChannelData.geturl(adData.getUrl()));
                            if(inboxItem.isReadFlag() && null != adData.getLPag() && adData.isIsDCDSend())
                                ObjectBuilderFactory.getControlChanel().addDisplayed(adData.getPrId(),adData.getAdId());
                            viewResponseDto.setBannerStyle(adData.getStyle());
                            viewResponseDto.setBannerText(adData.getAdText());
                        }

                        viewResponseDto.setFLineText(inboxItem.getFLineText());

                        viewResponseDto.setSenderName(inboxItem.getSender());
                        viewResponseDto.setAppName(inboxItem.getSender());
                        viewResponseDto.setQueryType(inboxItem.getQueryType());
                        viewResponseDto.setMesssageId(msgId);

                        msgId = Utilities.markUrl(inboxItem.getMessage());
                        viewResponseDto.setMessage(Utilities.markPhoneNumber(msgId));

                        ObjectBuilderFactory.GetKernel().displayScreen(viewResponseDto,false);
                        viewResponseDto = null;
                        isView = true;
                        inboxItem = null;
                    }
                }
                if (!isView) {
                        loadInbox();
                }
            } catch (Exception viewMsgexception) {
                    LoadErrorMessageScreen(viewMsgexception,Constants.errorMessage[14]);
            }
	}

	/**
	 * Method to get the Options for different type of Screen Based on the stste
	 *
	 * @return string[] Options
	 */
	public byte[] getOptions(String itemId) {
		byte[] opts = new byte[0];
		int i=0;
                byte[] temp = new byte[9];
                InboxItems inboxItems = null;
                if (1 == currst) {          // State for Inbox Screen
/*                    synchronized(parser){  CR 0014381
                        parser.setSelectedMessage(itemId);
                        if(null != itemId){
                            inboxItems = parser.getInboxMessage(itemId, -1, false,false);
                            //oMessage = parser.isQueryMessage(itemId);
                            //cr 0014381
//                            if(null == inboxItems.getQueryType() && null != inboxItems.getSender()){
//                                 if(inboxItems.getSender().compareTo(inboxItems.getShortCode()) != 0){
//                                     if(inboxItems.getSender().compareTo(Constants.appendText[2]) != 0 && inboxItems.getSender().compareTo(Constants.aName) != 0){
//                                          //pName ="Widget Catalog";
//
//                                          temp[i++] = 16;                   //Opt - Index for the Reply stop
//                                     }
//                                 }
////                                if(null != pName && pName.compareTo(parser.getShortCode(itemId)) != 0 && pName.compareTo(Constants.appendText[2]) != 0){
////                                    if(pName.compareTo(Constants.aName) != 0){
////                                        //pName ="Widget Catalog";
////
////                                        temp[i++] = 16;                   //Opt - Index for the Reply stop
////                                    }
////                                }
//                            }
                        }
                    }*/

                  //  if(null != inboxItems && null != inboxItems.getQueryType())
                   //     temp[i++] = 20;                     //Opt - index for Reply CR 0014381
                    temp[i++] = 21;                     //Opt - index for Forward
                    temp[i++] = 23;                     //Opt - index for  Sort By Date Option
                    temp[i++] = 17;                     //Opt - index for Sort by Profile Name Option
//                            if (Settings.getIsTexting())
//                                    temp[i++] = 24; //OPT - index for New Option
                    temp[i++] = 18;                     //Opt - index for Delete
                    temp[i++] = 19;                     //Opt - index for Delete All
                    temp[i++] = 36;                     //Opt - Index for ShartHand Home

                } else if ( 2 == currst) {   // State for Message View Screen
                    synchronized(parser){
                        if(null != itemId){
                            inboxItems = parser.getInboxMessage(itemId, -1, false,false);
                            if(null != inboxItems){
                               if(inboxItems.getSender().compareTo(inboxItems.getShortCode()) != 0 && inboxItems.getSender().compareTo(Constants.appendText[2]) != 0){
                                    if(inboxItems.getSender().compareTo(Constants.aName) != 0){
                                        //pName ="Widget Catalog";
                                        Constants.options[14] = (Constants.options[39]+ " " + inboxItems.getSender() + " "+Constants.appendText[1]);//.toCharArray();
                                        temp[i++] = 14;                   //Opt - Index for the GOTO Profile Home
//                                        if(null == inboxItems.getQueryType())
//                                            temp[i++] = 16;
//                                    //Reply stop CR 0014381
                                    }
                                }
                            }
                        }
                        if (!parser.isLastMessage()) 
                                temp[i++]= 27;              //Opt - index for view next sms option
                        if (!parser.isFirstMessage())
                                temp[i++] = 26;             //Opt - index for View Previous SMS Option
                    }
                    if(null != itemId){
                        temp[i++] = 18;                     //Opt - Index for Delete option
                      //  if(inboxItems.isIsReply())
                       //     temp[i++] = 20;                     //Opt - Index of Reply  CR 0014381
                        temp[i++] = 21;                     //Opt - Index of Forward
                    }
                    temp[i++] = 36;                     //Opt - Index for ShartHand Home
                } else if (3 == currst || 4 == currst) {     // State for Reply and Forward Screen
			opts = new byte[1];
			//opts[i++] = 25;                       //Opt - index for Send Option
                        opts[i++] = 36;                         //Opt - Index for ShartHand Home
                        return opts;
                }
                opts = new byte[i];
                System.arraycopy(temp, 0, opts, 0, i);
		return opts;
	}

        /**
	 * Method to retrieve the Message for the Given MessageId
	 *
	 * @param msgId - Variable will contain the selected Mesage id.(Not Null)
	 *
	 * @return msg - Variable will contain the Message for the requested Id.It may return null.
	 */
	public String[] GetViewMessage(String msgId) {
            String[] msg = null;
            if (null != parser && null != msgId) {
                synchronized(parser){
                    InboxItems inboxItems = parser.getInboxMessage(RemoveQueryType(msgId), -1, true,false);
                    if(null != inboxItems){
                        msgId = ObjectBuilderFactory.getControlChanel().removePacket(inboxItems.getMessage());
                        if(null != msgId)
                            inboxItems.setMessage(Utilities.remove(inboxItems.getMessage(), msgId));
                        msg =  new String[]{inboxItems.getMessage(),inboxItems.getFLineText()};
                    }
                }
            }
            return msg;
	}

        public String getSenderName(String msgId){
            synchronized(parser){
                InboxItems inboxItems = parser.getInboxMessage(msgId, -1, false, false);
                if(null != inboxItems)
                return inboxItems.getSender();
            }
            return null;
        }

	/**
	 * Method to Delete the Message for the Given MessageId
	 *
	 * @param msgId - Variable will contain the selected MessageId
	 *
	 * @return isDelete - if the message deleted , it will return true.Otherwise false.
	 */
	public boolean DeleteViewMessage(String msgId) {
		//#if VERBOSELOGGING
  //|JG|Logger.loggerError("User has selected to delete message from DAT-SMS");
            //#endif
		boolean isDelete = false;
		if (null != parser) {
                    synchronized(parser){
			isDelete = parser.deleteMessage(RemoveQueryType(msgId));
                    }
		}
		return isDelete;
	}

	/**
     * Method to Handle the different type of operations based on the selected Options
     *
     *
     * @param msgId - Variable will contain the selected MessageId
     * @param optText - Variable will conatin the Selected Option
     */
	public void handleOptionsSelected(String msgId, byte optText) {
            if (18 == optText) {                        // OPT - index for Delete Option
            //#if VERBOSELOGGING
            //|JG|Logger.loggerError("User selected to delete message in Inbox");
                //#endif
                handleDeleteOption(msgId);
            } else if (22 == optText) {                 //OPT - index for Back Option
                HandleBackOption();
            } else if(36 == optText){                   //Opt - Index for ShartHand Home
                handleShartHandHomeOption();
            } else if (20 == optText) {                 //OPT - index for Reply Option
                setPresenterDto(true);
                HandleReplyOption(msgId);
            } else if (21 == optText) {                 //OPT - index for Forward Option
                setPresenterDto(true);
                ForwardMessage(msgId);
            }  else if(14 == optText){                  //Opt - Index for GOTO profile Home
                HandleGotoProfile(msgId);
            } else if(16==optText){                     //Opt - Index for the Reply Stop
                synchronized(parser){
                    InboxItems inboxItems = parser.getInboxMessage(msgId, -1, false, false);
                    if(null != inboxItems){
                        ObjectBuilderFactory.getControlChanel().sendReplyStop(inboxItems.getSender());
                    } else ObjectBuilderFactory.getControlChanel().sendReplyStop(null);
                }
                ObjectBuilderFactory.GetKernel().displayMessageSendSprit();
            } else if (19 ==  optText){              //OPT - index for Delete All Option
                    DeletAllMessages();
            } else if(1 == currst){             // State for Inbox Screen
                if(23 == optText){                      //OPT - index for Sort by Date Option
                    Settings.setIsAlpha(!Settings.isIsAlpha());
                    if(0 != Settings.getSortType())
                    {
                        Settings.setSortType((byte)0);
                        loadInbox();
                    } else {
                        synchronized(parser){
                            parser.reorderMessages();
                            ObjectBuilderFactory.GetKernel().reorder(parser.getCurrentMsgId());
                        }
                    }
                } else if(17 == optText){               //OPT - index Sort by Profile Name option
                     Settings.setIsAlpha(!Settings.isIsAlpha());
                    if(1 != Settings.getSortType())
                    {
                            Settings.setSortType((byte)1);
                            loadInbox();
                    } else {
                         synchronized(parser){
                            parser.reorderMessages();
                            ObjectBuilderFactory.GetKernel().reorder(parser.getCurrentMsgId());
                         }
                    }
                }else if (24 == optText) {              //OPT - index for New Option
                    HandleNewOptionSelect();
                }
            } else if(2 == currst){         //State for Message View Screen
                if (27 == optText){                     //OPT - index for View Next SMS Option
                    ViewNextOrPreviousMessage(msgId,true);
                } else if (26 == optText) {             //OPT - index for View Previous SMS Option
                    ViewNextOrPreviousMessage(msgId,false);
                }
            } else if(3 == currst || 4 == currst){ // State for Reply and Forward Screen
                if (25 == optText) {                    //OPT - index for Send Option
                    HandleMessageResponse(msgId);
                }
            }
	}

        private void handleDeleteOption(String msgId){
            //vmsgId = msgId;
            loadMessageBox(7,Constants.popupMessage[37], 4,Constants.headerText[8]);
        }

	/**
	 * Method to retrieve the Next Message
	 */
	private void ViewNextOrPreviousMessage(String msgId, boolean isNext) {
            synchronized(parser){
                msgId = parser.getNextOrPreviousMessageId(msgId, isNext);
                if (null != msgId)
                    LoadViewMessage(msgId);
            }
	}

//	/**
//	 * Method to retrieve the Previous Message
//	 */
//	private void ViewPreviousMessage() {
//           synchronized(parser){
//                String msgId = parser.getPreviousMessageId();
//                if (null != msgId)
//                        LoadViewMessage(msgId);
//           }
//	}

	/**
	 * Method to Handle the Back Option Based on the State
	 *
	 */
	private void HandleBackOption() {
            if(5 == currst){
                loadInbox();
            } else if (4 == currst) {            // Forward State
                responseMessage = responseMessage.substring(5, responseMessage.length());
                ForwardMessage(responseMessage);
            } else if(1 != currst && isInboxLoaded){
                responseMessage = null;
                loadInbox();
            } else if (1 == currst || preState>0) // Inbox State
                loadPreviousScreen();
            else {
                responseMessage = null;
                loadInbox();
            }
	}

	/**
	 * Method to Delete the Selected Message for Given MessageId
	 *
	 * @param msgId - VAriable will contain the Selected MessageId
	 */
	private void DeleteSelectedMessage() {
            boolean isDeleted = false;
            synchronized(parser){
                if (null != parser.getCurrentMsgId() && null != parser) {
                    String msgId = parser.getCurrentMsgId();
                    isDeleted = parser.deleteMessage(msgId);
                if (2 == currst){
                    LoadViewMessage(parser.getCurrentMsgId());
                }else if (isDeleted)
                    ObjectBuilderFactory.GetKernel().removeMenuItem(0, msgId);
                }
            }
            //vmsgId= null;
	}

	/**
	 * Method to Delete All the Indox Messages
	 */
	private void DeletAllMessages() {
            loadMessageBox(7,Constants.popupMessage[46], 2,null);
	}

	/**
	 * Method to Load the Reply and Forward Screen
         *
         * @param msgId - Variable will contain the Selected message Id
         * @param isReply - Variable will differenciate the Reply and Forward Option
         * @param pState - Variable will contain the Present state
         *
	 */
	public void loadReplyOrForwardOptionSelect(String msgId,boolean isReply, String[] rMessage) {
            receivedMessage = rMessage;
            if (isReply)
                HandleReplyOption(msgId);
            else
                ForwardMessage(msgId);
	}

        /**
         * Method to set the previous state of the Which Handle Should be Called to load the Inbox,view,Reply or forward Screen
         * @param pState
         */
        public void setPreviousScreenState(byte pState){
            if(pState>-1)
                preState = pState;
        }

	/**
	 * Method to Handle the Reply Option
         *
         * @param msgId - Variable will contain the Selected Message id
	 */
	private void HandleReplyOption(String msgId) {
//            synchronized(parser){
//		sCode = parser.getShortCode(msgId);
//            }
            LoadGetEntry(RemoveQueryType(msgId), true, false);
	}

        /**
         * Method to Load the ShartHand Home Screen
         **/
        private void handleShartHandHomeOption(){
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Inbox Exited");
            //#endif
            ObjectBuilderFactory.GetKernel().reLaunchApplication();

        }

	/**
	 * Method to handle the Forward Option
         *
         * @param msgId - Variable will contain the Selected Message Id
	 */
	private void ForwardMessage(String msgId) {
            msgId = RemoveQueryType(msgId);
            LoadGetEntry(msgId, false, false);
	}

	/**
	 * Method to Load the screen to get the Input from the User
         *
         * @param msgId - Variable will contain the selected Message id
         * @param isMultiLine - Variable will differenciate weather the Textbox need single line or morethan one line
         * @param isMsgDisplay - Variable will differenciate weather the message want to display in the textbox or not.
	 */
	private void LoadGetEntry(String msgId, boolean isMultiLine, boolean isMsgDisplay) {
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_ENTRY);
            GetEntryResponseDTO _responseDto = new GetEntryResponseDTO();

            String secondaryHeaderText = null;
            responseMessage = msgId;
            currst = 3;
            if (isMultiLine) {
                    _responseDto.setMaxChar(UISettings.MAX_COUNT);
                    _responseDto.setMinChar((short) 0);
                    _responseDto.setEntryType((byte) 2);
                    if (isMsgDisplay)
                            currst = 4;
                    _responseDto.setLetterCount((short)0);
                    secondaryHeaderText = Constants.headerText[27];
                    responseMessage = "Send/" + responseMessage;
            } else {
                    secondaryHeaderText = Constants.headerText[28];
                    _responseDto.setEntryType((byte) 6);
            }
            _responseDto.setMultiLineEnabled(isMultiLine);

            _responseDto.setLeftOptionText((byte)22); //Opt Index of Back Option

            //PresenterDTO.setBgImage(FileManager.getSTBgLocation());
            PresenterDTO.setLOptByte((byte)-1);
            //PresenterDTO.setLOptByte((byte)41);

            _responseDto.setSecondaryHeaderText(secondaryHeaderText);
            ObjectBuilderFactory.GetKernel().displayScreen(_responseDto,true);
            if (isMsgDisplay) {
                String message = null;
                synchronized(parser){
                    InboxItems inboxItems = parser.getInboxMessage(msgId, -1, true,false);// getReceiveMessage(msgId,true);
                    if(null != inboxItems){
                        msgId = ObjectBuilderFactory.getControlChanel().removePacket(inboxItems.getMessage());
                        if(null != msgId){
                            message = Utilities.remove(inboxItems.getMessage(), msgId);
                        } else {
                            message = inboxItems.getMessage();
                        }
                    } else  message = receivedMessage[0];
                }
                ObjectBuilderFactory.GetKernel().showGivenValue(message,true);
            }
            _responseDto = null;
	}

	/**
	 * Method to send the Reply or Forward Message
         *
         * @param msg - Variable will contain the message to send
	 */
	private void HandleMessageResponse(String msg) {
            boolean isSend = true;
            if (null != responseMessage) {
                if (responseMessage.indexOf("Send/") > -1) {
                    String msgId = responseMessage.substring(5, responseMessage.length());
                    synchronized(parser){
                        InboxItems inboxItems = parser.getInboxMessage(msgId, -1, false,false);
                        if(null != inboxItems){
                            msgId = inboxItems.getSender();
                            if(null == sCode)
                                sCode = inboxItems.getShortCode();
                        }
                    }
                    if(null == msgId)
                        msgId = Constants.aName;

                    Message message = new Message();
                    message.setSFullName(msgId);
                    message.setShortcode(sCode);
                    message.setRMsg(new String[]{msg});
                    message.setIsNotChatMessage(true);
                    message.setAbberVation(Constants.appName);
                    ObjectBuilderFactory.GetKernel().sendMessage(message);//CR 11975
                } else {
                    isSend = false;
                    sCode = msg;
                    LoadGetEntry(responseMessage, true, true);
                }
            }
            if (isSend) { loadDisplay(); }
	}

        /**
         *
         */
        private void loadDisplay(){
            String msg = Constants.popupMessage[47];
            if(4 == currst)
                msg = Constants.popupMessage[48];
            currst = 5;
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_DISPLAY);
            PresenterDTO.setLOptByte((byte)-1);
            DisplayResponseDTO displayDTO = new DisplayResponseDTO();
            displayDTO.setDisplayImage((byte)1);
            displayDTO.setSecondaryHeaderText(msg);
            displayDTO.setDisplayTime((short)1);
            displayDTO.setIsDATWait(true);
            ObjectBuilderFactory.GetKernel().displayScreen(displayDTO, true);
            displayDTO = null;
        }

	/**
	 * future purpose
	 */
	private void HandleNewOptionSelect() {
            String floc = RecordManager.getClientAppName();
            if (null == floc)
                loadMessageBox(0, Constants.popupMessage[49], 0,null);
            else
                ObjectBuilderFactory.GetKernel().loadProfile(null,floc,true,false);
	}
        /**
         * Method to Load the Pofile Home Screen based on the message Id
         *
         * @param msgId - Variable will contain the Selected Message Id
         **/
        private void HandleGotoProfile(String msgId){
            boolean isFeature = false;
            InboxItems inboxItems = null;
            synchronized(parser){
                inboxItems = parser.getInboxMessage(msgId, -1, false,false);
            }
            if(null != inboxItems && inboxItems.getSender().compareTo(inboxItems.getShortCode()) != 0){
                if(inboxItems.getSender().compareTo(Constants.aName) == 0 && null != Settings.getAppCatalogName())
                    inboxItems.setSender(Settings.getAppCatalogName()); //CR 7230
                String fLoc = ObjectBuilderFactory.GetKernel().getProfileLocation(inboxItems.getSender());
                if(null ==  fLoc){
                    fLoc = RecordManager.getFeatureAppName(inboxItems.getSender());
                    isFeature = true;
                }
                if(null != fLoc){
                    if(isFeature)
                        inboxItems.setSender(null);
                    ObjectBuilderFactory.GetKernel().loadProfile(inboxItems.getSender(),fLoc,isFeature,false);
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Inbox Exited");
                    //#endif
                } else loadMessageBox(4, Constants.appendText[14]+" "+ inboxItems.getSender() +" "+Constants.popupMessage[16]+" "+ inboxItems.getSender() +" " +
                                        Constants.popupMessage[17], 0,null);
            }
        }

	/**
     * Method to Load the View Message for the Given Message Id This Method to
     * be invoke the AppHandler.. Notification to be raise the view Message
     * screen
     *
     *
     * @param msgId - Variable will conatin the Selected msgId(Not Null)
     * @param pState - Variable will contain the present State
     */
	public void loadViewMessage(String msgId) {
            LoadViewMessage(RemoveQueryType(msgId));
	}

        /**
         * @return
         */
        public String[] getUnReadMsgCount(String[] wName){
            synchronized(parser){
                return parser.getUnReadMsgCount(wName);
            }
        }

	/**
	 * Method to remove the queryType the given MessageId This MessageId is
	 * Having the MessageId and QueryType
	 *
	 * @param msgId - Variable will contain the Selected Message Id
	 *
	 * @return string messageId.It may be null.
	 */
	private String RemoveQueryType(String msgId) {
            if (null != msgId) {
                int index = msgId.indexOf("|");
                if (index > 0)
                        return msgId.substring(0,index);
            }
            return msgId;
	}

	/**
	 * Method to DeInitialize the smsReader and Release the All Resources
	 */
	public void DeInitialize() {
            if (null != parser) {
                // DeInitialize the smsReaderParser
                synchronized(parser){
                    parser.deInitialize();
                    parser = null;
                }
            }
            preState = -1;
	}

	/**
	 * Method to Write the Newly Receive Message for the Specified Profile
	 *
	 * @param msg - this Represent the newly received Message
	 *
	 * @return - return the messageId
	 */
	public Message WriteReceivedMessage(Message msg) throws Exception {
		//#if VERBOSELOGGING
  //|JG|            Logger.loggerError("Beginning write into inbox");
            //#endif
//            Settings.setSortType((byte)0);
//            Settings.setIsAlpha(false);
            Settings.setSortAndAlphaMode();//11800
            boolean isInbox = true;
            if(preState > -1 && currst == 2)
                isInbox = false;
            return parser.writeNewMessage(msg,isInbox);
	}

        /**
         *
         */
        public void loadCurrentScreen(){
            if(2 == currst){
                LoadViewMessage(parser.getCurrentMsgId());
            } else loadInbox();
        }

	/**
	 *  Method to Load the Previous Screen
	 */
	private void loadPreviousScreen() {
            isInboxLoaded = false;
            byte pState = preState;
            preState = -1;
            if(pState == -1)
                pState = KernelConstants.BACKEND_APPHANDLER;
            ObjectBuilderFactory.GetKernel().setPreviousBkState(pState);
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Inbox Exited");
            //#endif
	}
}
