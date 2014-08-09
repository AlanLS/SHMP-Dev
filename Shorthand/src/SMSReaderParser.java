import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class SMSReaderParser {

	// Varibale to hold the All messages Id
	//private static String[] allMsgIds = null;

        private static Vector allMsgIds = new Vector();

        // Variable to hold the Index to get the message Id
        //private static int selIndex = -1;
        private static String currentMsgId = null;

        //RMS Empty Index Store Variable
        private static String emptyRecordIndex ="";

        private RecordStoreParser rStoreParser = null;

        private byte messageStart = 4;

        private static String oldMessageIndex = "";

        public SMSReaderParser(){
            openRecordStore(true);
            try{
                if(null != rStoreParser) {
                    byte[] rbyte = rStoreParser.getRecord(3);
                    if(null != rbyte && rbyte.length>0)
                        emptyRecordIndex = new String(rbyte);

                    //Old Message Index
                    rbyte = rStoreParser.getRecord(2);
                    if(null != rbyte && rbyte.length>0)
                        oldMessageIndex = new String(rbyte);
                    rbyte = null;
                }
            }catch(Exception e){}
        }

        /**
         *
         * @param isSet
         */
        private void updateEmptyRecords(){
            try{
//				//#if VERBOSELOGGING
//Logger.loggerError("Storing empty record index into RMS");
//               //#endif //11801
                byte[] rbyte = emptyRecordIndex.getBytes();
                rStoreParser.setRecord(3, rbyte, 0, rbyte.length, true);
//                //#if VERBOSELOGGING
//Logger.loggerError("Storing old record index into RMS");
//               //#endif //11801
                rbyte = oldMessageIndex.getBytes();
                rStoreParser.setRecord(2, rbyte, 0, rbyte.length, true);
                rbyte = null;
            }catch(Exception e){}
        }

        /**
         *
         * @param isCreate
         */
        private void openRecordStore(boolean isCreate){
            try{
                rStoreParser = new RecordStoreParser();
                rStoreParser.openRecordStore(RecordManager.getRecMsgRmsName(), isCreate,false,false);
                if(rStoreParser.getNumRecords() == 0){
                    byte[] rbyte = "0".getBytes();
                    rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                    rbyte = "".getBytes();
                    rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                    rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                }
            }catch(Exception e){}
        }

        /**
         *
         */
        private void closeRecordStore(){
            try{
                if(null != rStoreParser){
                    rStoreParser.closeRecordStore();
                    rStoreParser = null;
                }
            }catch(Exception e){
               Logger.loggerError("SMSReaderParser->CloserRecordStore->"+e.toString()+e.getMessage());
            }
        }

        /**
         * Method to retrieve the unread message count
         * @return
         */
        public String[] getUnReadMsgCount(String[] wName){
            String[] mCount = new String[]{""};
            if(null != rStoreParser){
                try {
                    byte[] rByte = rStoreParser.getRecord(1);
                    if (null != rByte && rByte.length > 0) {
                        String value = new String(rByte);
                        if(value.compareTo("0") != 0)
                            mCount[0] += " (" + new String(rByte)+")";
                        value = null;
                    }
                    rByte = null;
                } catch (Exception ex) {}
            }
            return mCount;
        }

	/**
	 * Method to Retrieve all the messages from the specified path and store in its corresponding Dto
         *
	 * @param sType - variable will contain the Message sorting type.
         *
         * @return  - Return the Dto Object with the messages
	 */
	public InboxItems[] getAllMessages(byte sType,boolean isAlpha) {
            InboxItems[] inboxItems = null;
            if(null != rStoreParser){
                int count = rStoreParser.getNumRecords();
                Hashtable msgTable = new Hashtable();
                allMsgIds.removeAllElements();
                InboxItems inboxItem = null;
                if((count-(messageStart-1))>0){
                    for (int i = messageStart; i <= count; i++) {
                        inboxItem = getInboxMessage(null, i, false,false);
                        if(null != inboxItem){
                            if (sType == 1) {
                                allMsgIds.addElement(inboxItem.getSender() + inboxItem.getMessageId());
                            } else {
                                allMsgIds.addElement(inboxItem.getMessageId());
                            }

                            if(null != inboxItem.getQueryType())
                                inboxItem.setSender(inboxItem.getSender()+", "+inboxItem.getQueryType());

                            msgTable.put((String)allMsgIds.lastElement(), inboxItem);
                        }
                    }
                    if (allMsgIds.size() > 0) {
                        sort();
                        inboxItems =  arrangeMessage(msgTable, sType, isAlpha);
                        msgTable.clear();
                        msgTable = null;
                    }
                }
            }
            return inboxItems;
	}
        /**
         * Method to Sort the message Ids
         *
         * @param strArray - Variable will contain the message Ids.
         * @param len - Variable will contain the number of messages
         *
         * @return strArray - Variable will contain the sorted message Ids.
         **/
	private void sort()
	{
            int len = 0;
            if((len=allMsgIds.size())>0)
            {
                String temp = null;
                String[] messages = new String[allMsgIds.size()];
                allMsgIds.copyInto(messages);
                allMsgIds.removeAllElements();
                for(int i=0;i<len;i++)
                {
                    for(int j=i+1;j<len;j++)
                    {
                        if(messages[j].compareTo(messages[i])>=0)
                        {
                            temp = messages[i];
                            messages[i] = messages[j];
                            messages[j] = temp;
                        }
                    }
                    allMsgIds.addElement(messages[i]);
                }
            }
	}


	/**
	 * Method to  rearrange the messages for the required Sort type
         *
	 * @param msgTable  - variable will contain the messages
	 * @param strArray  - Variable will contain the Sorted message Ids
         * @param sType - Variable will contain the sorting type
         * @param len - variable will contain the Number of messages
         *
	 * @return inboxItems - Dto Object will contain the ordered message with its informations
	 */
	private InboxItems[] arrangeMessage(Hashtable msgTable,byte sType,boolean isAlpha){
            InboxItems[] inboxItems =null;
            if(msgTable.size()>0){
                int len = allMsgIds.size();
                inboxItems = new InboxItems[len];
                String[] strArray = new String[len];
                allMsgIds.copyInto(strArray);
                allMsgIds.removeAllElements();
                int j=0;
                Object _obj =null;
                if((isAlpha && sType == 0) || (!isAlpha && sType != 0)){
                    for(int i=len-1;i>-1;i--)
                    {
                        _obj = msgTable.get(strArray[i]);
                        if(null != _obj)
                        {
                                inboxItems[j] = (InboxItems)_obj;
                                allMsgIds.addElement(inboxItems[j].getMessageId());
                                j++;
                        }
                    }
                } else {
                    for(int i=0;i<len;i++)
                    {
                        _obj = msgTable.get(strArray[i]);
                        if(null != _obj)
                        {
                                inboxItems[j] = (InboxItems)_obj;
                                allMsgIds.addElement(inboxItems[j].getMessageId());
                                j++;
                        }
                    }
                }
                if(j>0){
                    if(j<len){
                        InboxItems[] tInb = inboxItems;
                        inboxItems = new InboxItems[j];
                        System.arraycopy(tInb, 0, inboxItems, 0, j);
                        tInb = null;
                    }
                } else inboxItems = null;
                msgTable.clear();
                msgTable =null;
            }
            return inboxItems;
	}

        /**
         *
         */
        public void reorderMessages(){
            int count = 0;
            if((count=allMsgIds.size())>0){
                String temp = null;
                for(int i=0;i<count/2;i++){
                    temp = (String)allMsgIds.elementAt(count-(i+1));
                    allMsgIds.setElementAt(allMsgIds.elementAt(i),count-(i+1));
                    allMsgIds.setElementAt(temp,i);
                }
            }
        }

        /**
         *
         * @param msgId
         */
        public void setSelectedMessage(String msgId){
            currentMsgId = msgId;
        }


        public InboxItems getInboxMessage(String msgId, int index, boolean isSetRead, boolean isView){
            InboxItems inboxItems = null;

            if(null != msgId){
                index = getMessageIndex(msgId);
            }
            if(index>3 && null != rStoreParser){
                byte[] rbyte = rStoreParser.getRecord(index);
                if (null != rbyte && rbyte.length > 0) {
                    inboxItems = new InboxItems();
                    ByteArrayReader din = new ByteArrayReader(rbyte);

                    inboxItems.setRmsIndex(index);

                    if(rbyte[0] == 0 && isSetRead){
                        rbyte[0] = 1;
                    } else isSetRead = false;

                    //Leave the message read flag
                    inboxItems.setReadFlag(din.readBoolean());

                    //leave the message id
                    inboxItems.setMessageId(din.readUTF());

                    if(isView)
                        setSelectedMessage(inboxItems.getMessageId());

                    //read the Shortcode
                    inboxItems.setShortCode(din.readUTF());

                    //leave the Send Name
                    inboxItems.setSender(din.readUTF());

                    //Read the Receive message
                    inboxItems.setMessage(din.readUTF());

                    //read the Query Type
                    if (din.readBoolean()) {
                        inboxItems.setQueryType(din.readUTF());
                    }

                    //Read Log Flag
                    if (din.readBoolean()) {
                        if(isView){
                            inboxItems.setMessage(din.readUTF()+ " " +inboxItems.getMessage());
                        } else din.readUTF();
                    }

                    //Read the Ad Channel Data
                    if (din.readBoolean()) {
                        inboxItems.setChennalData(din.readUTF());
                    }

                    //Read the Number of part index message present
                    if (din.readBoolean()) {
                        inboxItems.setFLineText(din.readUTF());
                    }

                    //Read the reply enable option present
                    inboxItems.setIsReply(din.readBoolean());

                    din.close();
                    din = null;

                    if(isSetRead){
                        rStoreParser.setRecord(index, rbyte, 0, rbyte.length, true);
                        changeUnreadCount(-1);
                    }
                }
                rbyte = null;
            }
            return inboxItems;
        }

//        private void flushRMS(){
//            if(ChannelData.isRMSAlwaysClose()){
//                closeRecordStore();
//                openRecordStore(true);
//            }
//        }

        /**
         *
         * @param msgId
         * @return
         */
        private int getMessageIndex(String msgId){
            int index = -1;
            if((index = oldMessageIndex.indexOf(msgId+"^"))>-1){
                index = Integer.parseInt(oldMessageIndex.substring(index+msgId.length()+1,oldMessageIndex.indexOf(",",index)));
            }
            return index;
        }

	/**
	 * Metrhod to Delete the Selected Message based on the Given Id
	 *
	 * @param msgId - Variable will contain the Selected Message Id.
	 *
	 * @return - return true, if the message deleted.otherwise false
	 */
	public boolean deleteMessage(String msgId) {
            boolean isDeleted = false;
            //#if VERBOSELOGGING
            //|JG|Logger.loggerError("Getting old message to be deleted.");
           //#endif
            InboxItems inboxItems = getInboxMessage(msgId, -1, true, false);
            if(null != inboxItems){
				//#if VERBOSELOGGING
    //|JG|Logger.loggerError("Going to delete message - "+ inboxItems.getMessage());
               //#endif
                byte[] rbyte = new byte[0];
                rStoreParser.setRecord(inboxItems.getRmsIndex(), rbyte, 0, rbyte.length, true);
                isDeleted = true;
                //#if VERBOSELOGGING
                //|JG|Logger.loggerError("Deleted message - "+ inboxItems.getMessage());
               //#endif
                emptyRecordIndex = (emptyRecordIndex + inboxItems.getRmsIndex() + ",");
                //#if VERBOSELOGGING
                //|JG|Logger.loggerError("Deleted message is added to empty record index - "+ emptyRecordIndex);
               //#endif
                updateOldMessageIndex(true, inboxItems.getRmsIndex()+"",msgId);
                updateEmptyRecords();
//                flushRMS();
                addOrRemoveMessageId(msgId, false, false);
            }
            return isDeleted;
	}

        private void updateOldMessageIndex(boolean isRemove, String postion, String msgId){
//			//#if VERBOSELOGGING
//   //|JG|Logger.loggerError("Old message index, Before updating  - "+ oldMessageIndex);
//           //#endif //11801
            if(isRemove){
                int index = 0;
                if((index = (oldMessageIndex).indexOf(msgId+"^"+postion+","))>-1){
                    oldMessageIndex = oldMessageIndex.substring(0,index) + oldMessageIndex.substring(index+msgId.length()+1+postion.length()+1);
                }
            } else {
                oldMessageIndex += msgId + "^"+postion+",";
            }
//            //#if VERBOSELOGGING
//            //|JG|Logger.loggerError("Old message index, After updating  - "+ oldMessageIndex);
//           //#endif //11801
        }

	/**
	 * Method to Delete the all Messages
         *
	 * @return - return true, if all messages deleted successfully.
	 */
	public boolean deleteAllMessages() {
            try{
                closeRecordStore();
                RecordStoreParser.deleteRecordStore(RecordManager.getRecMsgRmsName(), true);
                openRecordStore(true);
            }catch(Exception e){
			    Logger.loggerError("Delete All Message Error "+e.toString());
            }
            allMsgIds.removeAllElements();
            currentMsgId = null;
            emptyRecordIndex = "";
            oldMessageIndex = "";
            return true;
	}

	/**
	 * Method to Write the Newly Received Message for the Given Object Array
	 * Object Array Sequentially Having the MessageId,SendName,Message,QueryType
	 * SomeTime QueryType comes Null
	 *
	 * @param msg - Variable will contain the message details
	 *
	 * @return recMsgId - Variable will contain the recreated message Id
	 */
	public Message writeNewMessage(Message messageDto,boolean isRestSelectedId) throws Exception {
            int errorMessage = 0;
            try{
            String msgId = new Date().getTime() + "";
            String recMsgId = msgId;
            ByteArrayWriter dout = new ByteArrayWriter();
            //try{
                //Write the Read Flag
                dout.writeBoolean(false);

                //Write the message ID
                dout.writeUTF(recMsgId);

                errorMessage = 1;
                //Write the sender Shortcode
                //bug 14628
                if(null == messageDto.getShortcode()){
                    dout.writeUTF(ChannelData.getShortcode());
                } else dout.writeUTF(messageDto.getShortcode());

                errorMessage = 2;
                //Write the SenderName
                if(null == messageDto.getSFullName())
                    dout.writeUTF(ChannelData.getClientName());
                else dout.writeUTF(messageDto.getSFullName());

                errorMessage = 3;
                //Write the Receive Message
                if(null != messageDto.getRMsg()){
                    dout.writeUTF(messageDto.getRMsg()[0]);
                } else {
                    dout.writeUTF(messageDto.getCurRMsg());
                }

                errorMessage = 4;
                //Write the Query Type
                if(null != messageDto.getQueryType()){
                    //Set the Message Query Present byte
                    dout.writeBoolean(true);
                    //Write the receive Message Query
                    dout.writeUTF(messageDto.getQueryType());
                    recMsgId += '|' + messageDto.getQueryType();
                } else dout.writeBoolean(false);

                errorMessage = 5;
                //Write the Log Message
                if(null != messageDto.getLogMessage()){
                    //Set the Log Message Present byte
                    dout.writeBoolean(true);
                    //Write the receive message log message
                    dout.writeUTF(messageDto.getLogMessage());
                } else dout.writeBoolean(false);

                errorMessage = 6;
                //Write the AD Packet
                if(null != messageDto.getChannelData()){
                    //Set the Ad Packer Present byte
                    dout.writeBoolean(true);
                    //Write the Ad Channel Data Pocket Message
                    dout.writeUTF(messageDto.getChannelData());
                } else dout.writeBoolean(false);

                errorMessage = 7;
                //Write the Message part index
                if(messageDto.getMaxCount()>1){
                    //write the message part is present byte
                    dout.writeBoolean(true);
                    //Write the Number of Message part Index
                    dout.writeUTF("("+messageDto.getMaxCount()+" "+Constants.appendText[22]);
                } else dout.writeBoolean(false);

                errorMessage = 8;
                /** Write the reply option enable bit */
                dout.writeBoolean(messageDto.isIsInboxReply());

                errorMessage = 9;
                byte[] rbyte = dout.toByteArray();
                //#if VERBOSELOGGING
                //|JG|Logger.loggerError("Byte array of new message prepared");
               //#endif
                dout.close();
                dout = null;
                
                int index = 0;
                int pos = 0;
                errorMessage = 10;
                isCrossedLimit(); //CR 10674
                errorMessage = 11;
                if((index=emptyRecordIndex.indexOf(","))>-1){
                    pos = Integer.parseInt(emptyRecordIndex.substring(0,index));
                    emptyRecordIndex = emptyRecordIndex.substring(index+1);
                    rStoreParser.setRecord(pos, rbyte, 0, rbyte.length, true);
                } else{
                    rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                    pos = rStoreParser.getNumRecords();
                }
                errorMessage = 12;
                updateOldMessageIndex(false, pos+"",msgId);
                errorMessage = 13;
                updateEmptyRecords();
                errorMessage = 14;
                changeUnreadCount(1);
                errorMessage = 15;
//                flushRMS();
                addOrRemoveMessageId(msgId, true,isRestSelectedId);
                errorMessage = 16;
                messageDto.setFName(recMsgId);
            }catch(Exception exception){
                Logger.loggerError("SMSReaderParser->WriteMessage->Error id "+errorMessage+" "+exception.toString());
                throw exception;
            }
                return messageDto;
	}



        private void isCrossedLimit(){
            String[] temp = Utilities.split(oldMessageIndex, ",");
            if(null != temp && ChannelData.getInboxLimit() > 0 && ChannelData.getInboxLimit()<temp.length){
				//#if VERBOSELOGGING
    //|JG|Logger.loggerError("Crossed Inbox size limit. Must delete. Inbox limit= "+ ChannelData.getInboxLimit());
               //#endif
               //#if VERBOSELOGGING
               //|JG|Logger.loggerError("Number of messages in Inbox = "+ temp.length);
               //#endif
                deleteMessage(temp[0].substring(0,temp[0].indexOf("^")));
            }
        }


        /**
         *
         * @param iCount
         */
        private void changeUnreadCount(int iCount){
            try{
                String rWName = null;
                byte[] rbyte = rStoreParser.getRecord(1);
                int value = 0;
                //bug 1428
                if(null != rbyte && rbyte.length>0){
                    rWName = new String(rbyte);
                    value = Integer.parseInt(rWName);
                }
                value += iCount;
                if(value<0)
                    value = 0;
                rbyte = (""+value).getBytes();
                rStoreParser.setRecord(1, rbyte, 0, rbyte.length, true);
            }catch(Exception e){}
        }

	/**
	 * Method to Add Or Remove the MessageId from the allMessageIds Object
	 *
	 * @param msgId - Variable will contain the message Id to Add or Remove
	 * @param isAddMsg - boolean variable will differenciate weather the message Id want to Add or Remove
	 */
	private void addOrRemoveMessageId(String msgId, boolean isAddMsg,boolean isResetSelectedId) {
            if (allMsgIds.size()>0) {
                if (isAddMsg) {
                    allMsgIds.insertElementAt(msgId, 0);
                    if(isResetSelectedId)
                        currentMsgId = msgId;
                } else {
                   int index = allMsgIds.indexOf(msgId);
                   allMsgIds.removeElement(msgId);
                   //bug 14628
                   if(null != currentMsgId && currentMsgId.compareTo(msgId) == 0){
                       if(allMsgIds.size()>0){
                           if(index>0){
                               currentMsgId = (String)allMsgIds.elementAt(index-1);
                           } else currentMsgId = (String)allMsgIds.elementAt(index);
                       }
                   }

                }
            } else if(isAddMsg){
                allMsgIds.addElement(msgId);
                currentMsgId = msgId;
            }

	}

	/**
	 * Method to retrieve the NextMessage Id basd on the selected Id
	 *
	 * @return - return the message Id.it may return null.
	 */
	public String getNextOrPreviousMessageId(String msgId, boolean isNext) {
            int index = -1;
            if((index = allMsgIds.indexOf(msgId))>-1){
                if(isNext){
                    if(allMsgIds.size()> (index+1)) {
                        currentMsgId =(String)allMsgIds.elementAt(index+1);
                    }
                } else {
                    if((index-1)>-1) {
                        currentMsgId = (String)allMsgIds.elementAt(index-1);
                    }
                }
            }
            return currentMsgId;
	}

        /**
         * Method to get the Current message Id
         *
         * @return - return the message Id.it may return null.
         **/
        public String getCurrentMsgId(){
            return currentMsgId;
        }

	/**
	 * Method to Check the Selected Message id First Message or Not First
	 * Message Means return true otherwise false
	 *
	 * @return - return true if its the FIrest message.Based on the selIndex we can find the message position.
	 */
	public boolean isFirstMessage() {
            if(allMsgIds.size() > 0 && null != currentMsgId && allMsgIds.indexOf(currentMsgId) == 0)
                return true;
            return false;
	}

	/**
	 * Method to Check the Selected Message is Last Message or Not Last Message
	 * Means return true otherwise return false
	 *
	 * @return - true, if the selected message is last.otherwise false.
	 */
	public boolean isLastMessage() {
            if(allMsgIds.size() == 0 || null == currentMsgId || allMsgIds.indexOf(currentMsgId) == allMsgIds.size()-1)
                return true;
            return false;
	}


	/**
	 * Method to DeInitialize the SMSReaderParser and Save the Message for
	 * Selected Profile Document and Release the All Resources
	 */
	public void deInitialize() {
            closeRecordStore();
            allMsgIds.removeAllElements();
            currentMsgId = null;
	}

}
