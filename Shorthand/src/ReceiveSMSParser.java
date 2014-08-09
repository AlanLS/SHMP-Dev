/*
 * ReceiveSMSParser.java
 *
 * Created on October 4, 2007, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author Hakuna Matata
 * @version 1.0
 * @copyright (c) John Mcdonnough
 */

/**
 *
 */
public class ReceiveSMSParser {

        private static Hashtable msgSend = new Hashtable();

        //CR 14531
        //CR 14563
        private byte refreshTime = 1;//cr 14570

        private ResponseMessageCount rMsgCount = null;

        private int aCount = 0;

//        /** Message Concadenation count */
//        private int cCount = 0;

	/**
	 * Creates a new instance of ReceiveSMSParser
	 */
	public ReceiveSMSParser() {
            rMsgCount = new ResponseMessageCount();
	}

        //CR 14531
        //CR 14563
        public boolean isWaitingForResponse(){
            if(msgSend.size()>0){
                Enumeration enumeration  = msgSend.keys();
                Calendar curtime = Calendar.getInstance();
                long time = 0;
                String fName = null;
                String[] ids = null;
                while(enumeration.hasMoreElements()){
                    fName = (String)enumeration.nextElement();
                    if(fName.startsWith(aCount+"-")){
                        ids = getBucketId(fName);
                        time = curtime.getTime().getTime()-Long.parseLong(ids[0]);
                        if(time>(refreshTime*60*1000) || (time<0 && time<(-1*refreshTime*60*1000))){
                            return false;
                        } else return true;
                    }
                }
            }
            return false;
        }

	/**
	 * Method to create bucket for every message being sent. The method creates
	 * or stores the information on the message sent in the temporary xml for
	 * associating the message when it is returned back.
	 *
	 * @param sender
	 *            Short code or sender name
	 * @param message
	 *            SMS Message being sent.
	 * @param queryType
	 *            Query Type of the message that is sent
	 * @param matchWords
	 *            Group of words separated by comma that can be present in the
	 *            response message that is retrieved
	 * @param misMatchWords
	 *            Group of words separated by comma that should not present in
	 *            the response message for this particular sent message.
	 */
	public byte[] getBucketData(Message sentMessage) {

            byte[] rByte = null;
            ByteArrayWriter dout = new ByteArrayWriter();
            if(null != dout){

                /** Shortcode */
                dout.writeUTF(sentMessage.getShortcode());

                /** Don't Save Message */
                dout.writeBoolean(sentMessage.getDontSaveInbox());

                /** Don't Display Notification */
                dout.writeBoolean(sentMessage.isIsNotNewMsg());

                /** Multi part messaeg concadenation number */
                if (null != sentMessage.getMscf()) {
                    dout.writeUTF(sentMessage.getMscf());
                } else {
                    dout.writeUTF("");
                }

                 /** Write the Sender full Name */
                 dout.writeUTF(sentMessage.getSFullName());

                /** Query Type */
                if(null != sentMessage.getQueryType())
                {
                    dout.writeBoolean(true);
                    dout.writeUTF(sentMessage.getQueryType());
                }
                else dout.writeBoolean(false);

                 dout.writeBoolean(true);

                /** Match Words */
//                if(null != sentMessage.getMatchWords())
//                {
//                    dout.writeBoolean(true);
//                    dout.writeUTF(sentMessage.getMatchWords());
//                }else
                    //dout.writeBoolean(false);

                /** MisMatchWords */
//                if(null != sentMessage.getMisMatchWords())
//                {
//                    dout.writeBoolean(true);
//                    dout.writeUTF(sentMessage.getMisMatchWords());
//                }else
                   // dout.writeBoolean(false);

                /** MaxCount Default */
                dout.writeByte(0);

                rByte = dout.toByteArray();

                dout.close();
                dout = null;
            }
            return rByte;
	}

        /**
         *
         * @param abyte
         * @param sName
         */
        public String addBucket(byte[] abyte,String sName, String fName){
            if(null != abyte){
                if(null == fName){
                    aCount++;
                    sName = aCount + "-" + (new Date().getTime()) + "-" + sName;
                    if(aCount>10000)
                        aCount = 0;
                } else sName = fName;
                msgSend.put(sName, abyte);
                return sName;
            }
            return null;
        }

        public void removeBucket(String id){
            if(msgSend.size()>0 && msgSend.containsKey(id)){
                msgSend.remove(id);
            }
        }

        private String[] getBucketId(String value){
            String[] ids = new String[2];
            int index = value.indexOf("-");
            value = value.substring(index+1);
            index = value.indexOf("-");
            ids[0] = value.substring(0,index);
            ids[1] = value.substring(index+1);
            return ids;
        }

        /**
	 *
	 * @param sender
	 * @param message
	 * @return
	 *
	 */
	public Message receiveMessage(String sCode,String sName, String message,String mCf,String sFullName) {
            Utilities.setCurrentTime();
            Message messageDto = getChatMessage(sCode, sName, message, sFullName);
            Utilities.updateMessage("Chat Message Association End ");
            try{
                if (null == messageDto) {
                    messageDto = associateMessageWithTempXml(sCode, sName, message, mCf, sFullName);
                    if (null != messageDto) {
                        messageDto.setCurRMsg(messageDto.getRMsg()[0]);
                        Utilities.setCurrentTime();
                        if (messageDto.getCurRMsg().length() > 20) {
                            //#if VERBOSELOGGING
                            //|JG|                        Logger.debugOnError("Object array convertion for message" + messageDto.getCurRMsg().substring(0, 20));
                            //#endif //11801
                        } else {
                            //#if VERBOSELOGGING
                            //|JG|                        Logger.debugOnError("Object array convertion for message" + messageDto.getCurRMsg());
                            //#endif //11801
                        }
                        //messageDto = convertObjectArray(msg);
                        Utilities.updateMessage("Received Message Formed into Display format");
                    }
                }
            } catch (Exception ex) {
                Logger.loggerError("ReceiveSMSParser Error" + ex.toString());
            }
            return messageDto;
    }

    private Message getChatMessage(String sCode, String sName, String message, String sFullName){
        Message messageDto = null;
        String chat = sName +">\n";
        boolean isChatMessage = false;
        if(message.startsWith(sName+">\r\n")){
            message = Utilities.remove(message, "\r");
            isChatMessage = true;
        } else if(message.startsWith(chat))
            isChatMessage = true;
        if(isChatMessage){
            messageDto = new Message();
            messageDto.setShortcode(sCode);
            messageDto.setSFullName(sFullName);
            messageDto.setDontSaveInbox(true);
            chat = message.substring(chat.length());
            int index = chat.indexOf("\n");
            if(index>-1){
                messageDto.setCurRMsg(chat.substring(index+1));
//                messageDto.setRMsg(new String[]{chat.substring(index+1)});
                //Remove close paranthisis
                chat = chat.substring(1,index-1);
                String[] splitValues = Utilities.split(chat, ",");
                messageDto.setChatName(splitValues[0]);
                //CR 14197
                messageDto.setChatId(splitValues[1].trim());
                //CR 14134
                if(splitValues.length>2){
                    messageDto.setMessagePlus(splitValues[2]);
                    //CR 14326
                    if(splitValues.length>3){
                        messageDto.setChatSequence(splitValues[3]);
                        if(splitValues[4].length()>8){
                            messageDto.setChatDate(splitValues[4].substring(0,8));
                            messageDto.setCurRMsg(splitValues[4].substring(8)+messageDto.getCurRMsg());
                        } else {
                            messageDto.setChatDate(splitValues[4]);
                        }
                    }
                }
//
//                index = chat.indexOf(",");
//                messageDto.setChatName(chat.substring(1,index).trim());
//                chat = chat.substring(index+1);
//                index = chat.indexOf(",");
//                if(index>-1){
//                    messageDto.setChatId(chat.substring(0,index).trim());
//
//                    messageDto.setMessagePlus(chat.substring(index+1,chat.length()-1).trim());
//                } else {
//
//                    messageDto.setChatId(chat.substring(index+1,chat.length()-1).trim());
//                }
            } else messageDto = null;
        }
        return messageDto;
    }

//    /**
//     *
//     * @param msg
//     * @return
//     *
//     */
//    public String[] convertObjectArray(Message msg) {
//
//        String[] message = new String[12];
//        message[0] = msg.getShortcode();
//        message[1] = msg.getSFullName();
//        message[2] = msg.getRMsg()[0];
//        if (msg.getRMsg()[0].length() > 20) {
//            //#if VERBOSELOGGING
//            Logger.debugOnError("Object array convertion for message" + msg.getRMsg()[0].substring(0, 20));
//            //#endif //11801
//        } else {
//            //#if VERBOSELOGGING
//            Logger.debugOnError("Object array convertion for message" + msg.getRMsg()[0]);
//            //#endif //11801
//
//        }
//        message[3] = msg.getQueryType();
//        if (msg.getRtDelay() > 0) {
//            message[4] = "" + msg.getRtDelay();
//        }
//        if (msg.isIsNotNewMsg()) {
//            message[5] = "";
//        }
//        if (msg.getDontSaveInbox()) {
//            message[6] = "1";
//        }
//        if (msg.getMaxCount() > 1) {
//            message[7] = msg.getMaxCount() + "";
//        }
//
//        //message[8] is used for inbox message want to display the reply option
//        //messaeg[9] is used for chat message identification
//        message[10] = msg.getMscf();
//        if( msg.getPopulatedTime()>0 && msg.isIsRTDASend())
//            message[11] = msg.getPopulatedTime()+"";
//        return message;
//    }

	/**
	 * Method to associate and concatenate the recieved message with the
	 * appropriate message Sent. TempMessage.xml has n nodes for the n message
	 * sent through the application. This method process all those n nodes and
	 * identify the best match for the recieved message. The method checks each
	 * node in the tempMessage.xml for the following conditions
	 *
	 * 1. SenderName (Shortcode)- SenderName should match with the received
	 * sender 2. TimeStamp - If the message is received within 5 seconds of sent
	 * message, it is not matched with the message sent. 3. partIndex - If the
	 * part of message recieved should match with partIndex attribute in the
	 * xml. 4. MaxCount - If the maxCount of the message to be recieved should
	 * match with the maxcount attribute in the xml. 5. MismatchWords - The
	 * array of mismatch words in the xml should not be present in the received
	 * message 6. MatchWords - Any of the matchWords in the xml should be
	 * present in the received message. If the message doesn't match any of the
	 * above conditions, a new bucket is created with appropriate property set.
	 */
	private Message associateMessageWithTempXml(String sCode,String sName, String message,String mCf,String sFullName){
            Message receivedMessage = null;
//            //#if VERBOSELOGGING
//            //|JG|Logger.debugOnError("Sender name of the message (sName) = "+ sName);
//            //#endif //11801
            Message tempMsgDTO = findTheBestFit(sName, message);
            Utilities.setCurrentTime();
            if (null != tempMsgDTO) {
//                //#if VERBOSELOGGING
//                //|JG|Logger.debugOnError("tempMsgDTO is not null. File name = "+ tempMsgDTO.getFName());
//                //#endif //11801
                if(tempMsgDTO.getSFullName().length() == 0)
                    tempMsgDTO.setSFullName(sFullName);
//                if(null != tempMsgDTO.getQueryType()){
//                    rMsgCount.increaseMessageCount(tempMsgDTO.getSFullName(),true);
//                } else rMsgCount.increaseMessageCount(tempMsgDTO.getSFullName(),false);
//                Utilities.updateMessage("Filtered  in memmory association buckets");
                msgSend.remove(tempMsgDTO.getFName());
            } else {
                if(null == sName){
                    sName = Constants.aName;
//                //#if VERBOSELOGGING
//                //|JG|Logger.debugOnError("Assign aName to sName: sName");
//                //#endif //11801
                }
                //else rMsgCount.increaseMessageCount(sFullName,false);
                receivedMessage = new Message();
                receivedMessage.setSenderName(sName);
                receivedMessage.setSFullName(sFullName);
                receivedMessage.setShortcode(sCode);
                receivedMessage = setReceiveingMessage(message,receivedMessage);
                if(null != receivedMessage.getTempConFormat()){
                    receivedMessage.setMscf(receivedMessage.getTempConFormat());
                }
                tempMsgDTO = receivedMessage;
            }
            if (tempMsgDTO.getPartIndex()>0 && tempMsgDTO.getTempmaxCount() != 1) {
//                //#if VERBOSELOGGING
//                //|JG|Logger.debugOnError("Tempary max count = " + tempMsgDTO.getTempmaxCount());
//                //#endif //11801
                String[] temp = tempMsgDTO.getRMsg();
                if (null == temp && tempMsgDTO.getMaxCount() == 0) {
                    tempMsgDTO.setMaxCount(tempMsgDTO.getTempmaxCount());
                    temp = new String[tempMsgDTO.getTempmaxCount()];
                }
                tempMsgDTO.setRmsgCount((byte)(tempMsgDTO.getRmsgCount()+1));
                temp[tempMsgDTO.getPartIndex()-1] = tempMsgDTO.getCurRMsg();
                tempMsgDTO.setRMsg(temp);
                temp = null;
                if (tempMsgDTO.getRmsgCount() == tempMsgDTO.getMaxCount()) {
                    receivedMessage = concatenateMessage(tempMsgDTO);
                }else{
                    Logger.debugOnError("Total received part = "+ tempMsgDTO.getRmsgCount()+" is not equal to MAX COUNT " +tempMsgDTO.getMaxCount());
                    Utilities.setCurrentTime();
                    recreateFile(tempMsgDTO);
                    Utilities.updateMessage("Receive Message is MultiPart Message, Recreate the in memory bucket ");
                    receivedMessage = null;
                }
            } else {
                String[] temp = new String[]{ tempMsgDTO.getCurRMsg()};
                tempMsgDTO.setRMsg(temp);
                receivedMessage = tempMsgDTO;
            }
            return receivedMessage;
	}

//        public String getgeneratedNumber(){
//            String value = ""+((char)(cCount+97));
//            cCount++;
//            if(cCount>=26)
//                cCount = 0;
//            return value;
//        }

        /**
         *
         * @param sName
         * @return
         */
        private String[] getFileName(String sName){
            String[] fileName = null;
//            //#if VERBOSELOGGING
//            //|JG|Logger.debugOnError("msgSend.size()=" + msgSend.size());
//            //#endif //11801
            if(msgSend.size()>0){
                Enumeration enumeration  = msgSend.keys();
//                Date d = new Date();
//                Calendar ftime = Calendar.getInstance();
//                Calendar curtime = Calendar.getInstance();
//                long time = 0;
                int j = 0;
                String fName = null;
                int count = msgSend.size();
                fileName = new String[count];
                String[] ids = null;
                while(enumeration.hasMoreElements()){
                    fName = (String)enumeration.nextElement();
                    ids = getBucketId(fName);
                    //CR 8759
//                    d.setTime(Long.parseLong(ids[0]));
//                    ftime.setTime(d);
//                    time = curtime.get(Calendar.MINUTE)-ftime.get(Calendar.MINUTE);
//                    if(time>refreshTime){
//                        //Older than 3 minutes. If the message is not recieved within 3 Minutes
//                        msgSend.remove(fName);
//                    } else
                      if(null == sName || ids[1].compareTo(sName) == 0)
                      {
//                          //#if VERBOSELOGGING
//                          //|JG|Logger.debugOnError("Mathcing file name =" + fName );
//                          //#endif //11801
                          fileName[j++] = fName;
                    }
                }
                if(j>0){
                    if(j<count){
                        String[] tFiles = fileName;
                        fileName = new String[j];
//                        //#if VERBOSELOGGING
//                        //|JG|Logger.debugOnError("File name to return = "+fileName );
//                        //#endif //11801
                        System.arraycopy(tFiles, 0, fileName, 0, j);
                        tFiles = null;
                    }
                } else
                {
                    fileName = null;
//                    //#if VERBOSELOGGING
//                    //|JG|Logger.debugOnError("File name to return = "+fileName);
//                    //#endif //11801
                }
            }
            return fileName;
        }

	/**
	 * Method to identify the node in temp message that best fits with the
	 * message received
	 *
	 * @param node
	 *            Instance of XmlNode of the temp message xml
	 * @param sender
	 *            Short code
	 * @param message
	 *            Message Recieved
	 * @param partIndex
	 *            Part Index of the multi-part message
	 * @param maxCount
	 *            Count of the multi-part message
	 */
	private Message findTheBestFit(String sName, String message) {
		Message tempMsgDTO = null;
            Message sendDto = null;
            Message mSendDto = null;
            byte partIndex = 0;
            int count;
            Utilities.setCurrentTime();
            String[] fileName = getFileName(sName);
            Utilities.updateMessage("Filtered in memmory association files");
            //String[] fileName = null;
            if(null != fileName && (count = fileName.length)>0)
            {
//                Calendar ftime = Calendar.getInstance();
//                Calendar curtime = Calendar.getInstance();
//                Date d = new Date();
                long time = 0;
//                //#if VERBOSELOGGING
//                //|JG|Logger.debugOnError("Mathcing file count="+ count);
//                //#endif //11801
                for(int i=count-1;i>-1;i--){
                    Utilities.setCurrentTime();
                    tempMsgDTO = populateMessageDTO(fileName[i]);
                    Utilities.updateMessage("Populateed the Association DTO");
                    if(null != tempMsgDTO) {

                        Utilities.setCurrentTime();
                        /** set the Part Index and MaxCount */
                        tempMsgDTO = setReceiveingMessage(message,tempMsgDTO);
                        Utilities.updateMessage("Find the Attributes of Sequence Numebr and PartInde");

                        partIndex = tempMsgDTO.getPartIndex();

                        if(tempMsgDTO.getTempConFormat().compareTo(tempMsgDTO.getMscf().toLowerCase()) != 0)
                            continue;

                        // Check for MaxCount
                        if ((tempMsgDTO.getMaxCount() > 0 && tempMsgDTO.getTempmaxCount() != tempMsgDTO.getMaxCount()))
                                continue;

                        if(tempMsgDTO.getRmsgCount()>0 && partIndex == 0)
                            continue;

                        if(partIndex>0 && tempMsgDTO.getRmsgCount()>0 && null != tempMsgDTO.getRMsg()[partIndex-1])
                                continue;

                        String temp = fileName[i].substring(fileName[i].indexOf('-')+1);//9701 Roundtrip delay does not work
                        time = Long.parseLong(temp.substring(0,temp.indexOf('-')));
                        tempMsgDTO.setPopulatedTime(time);
                        tempMsgDTO.setRtDelay((int)Utilities.getRoundTripTime(time));

//                        d.setTime(time);
//                        ftime.setTime(d);
//                        time = ((curtime.get(Calendar.MINUTE)*60) + curtime.get(Calendar.SECOND))-
//                                ((ftime.get(Calendar.MINUTE)*60) + ftime.get(Calendar.SECOND));
//
//                        tempMsgDTO.setRtDelay((int)time);

                        if (null == sendDto){
                                sendDto = tempMsgDTO;
                        } else if(partIndex > 0 && tempMsgDTO.getRmsgCount()> 0)
                        sendDto = tempMsgDTO;
                    }
                }
            }
            if(null != mSendDto)
                return mSendDto;
		return sendDto;
	}

        /**
         *
         **/
        private Message setReceiveingMessage(String msg,Message tempMsgDto)
        {
            String tempMsg = msg;
            if(tempMsg.length()>18)
                tempMsg = tempMsg.substring(0,18);
            msg = msg.trim();
            int index, sep;
            if(tempMsg.charAt(0) == '('){
                if((index = tempMsg.indexOf(')',1))>-1){
//                    //#if VERBOSELOGGING
//                    //|JG|Logger.debugOnError("Sequnce of the message= "+tempMsg.substring(0, index));
//                    //#endif //11801
                    if((sep = tempMsg.substring(0,index).indexOf('/'))>-1){
                        tempMsgDto.setPartIndex(Byte.parseByte(tempMsg.substring(1,sep)));
                        tempMsg = msg.substring(sep+1,index-1).trim();//Bug 11097
                        if(tempMsg.length() == 0){
                            tempMsg = msg.substring(sep+1,index);
                        } else tempMsgDto.setTempConFormat(msg.substring(index-1,index));
                        tempMsgDto.setTempmaxCount(Byte.parseByte(tempMsg));
                        msg = msg.substring(index+1);
                    }
                }
            }
            tempMsgDto.setCurRMsg(msg);
            return tempMsgDto;
//            String tempMsg = msg;
//            if(tempMsg.length()>18)
//                tempMsg = tempMsg.substring(0,10);
//            String[] text = Utilities.split(tempMsgDto.getMscf(),"|");
//            int stIndex =0,sep =0,endIndex =0;
//            stIndex = tempMsg.indexOf(text[0]);
//            sep = tempMsg.indexOf(text[1]);
//            endIndex = tempMsg.indexOf(text[2]);
//            if(stIndex>-1 && sep>0 && endIndex>0){
//                tempMsgDto.setPartIndex(Byte.parseByte(tempMsg.substring(stIndex+text[0].length(),sep)));
//                tempMsgDto.setTempmaxCount(Byte.parseByte(tempMsg.substring(sep+text[1].length(),endIndex)));
//                msg = msg.substring(endIndex+text[2].length());
//            }
//            tempMsgDto.setCurRMsg(msg);
//            return tempMsgDto;
        }
	/**
	 *
	 */
//	private boolean checkForMisMatchWords(String misMatchWords, String message) {
//        if(null != misMatchWords){
//            message = message.toLowerCase();
//            misMatchWords = misMatchWords.toLowerCase();
//            String temp[] = Utilities.split(misMatchWords, ",");
//            if (null != temp) {
//                        for (int i = 0; i < temp.length; i++)
//                            if (message.indexOf(temp[i]) > 0) return false;
//            }
//        }
//		return true;
//	}

	/**
	 *
	 */
//	private boolean CheckForMatchWords(String matchWords, String message) {
//            message = message.toLowerCase();
//            matchWords = matchWords.toLowerCase();
//            String[] temp = Utilities.split(matchWords, ",");
//            if (null != temp) {
//                for (int i = 0; i < temp.length; i++)
//                    if (message.indexOf(temp[i]) < 0) return false;
//            }
//            return true;
//	}

	/**
	 *
	 */
	private Message concatenateMessage(Message tempMessage) {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Concatenating the message");
            //#endif
            String[] msg = tempMessage.getRMsg();
            int maxCount = tempMessage.getMaxCount();
            String[] message = new String[] {""};
            for(int i=0;i<maxCount;i++){
                if (null != msg[i])
                    message[0] += msg[i];
            }
            tempMessage.setRMsg(message);
            return tempMessage;
	}

        /**
         *
         **/
        private void recreateFile(Message temprecDto)
        {
            ByteArrayWriter dout = new ByteArrayWriter();
            if(null != dout){

                /** Write Shortcode */
                dout.writeUTF(temprecDto.getShortcode());

                // Dont Save inbox boolean set
                dout.writeBoolean(temprecDto.getDontSaveInbox());

                //Dont display Notification
                dout.writeBoolean(temprecDto.isIsNotNewMsg());

                /** Write Multi Message QueryFormat */
                dout.writeUTF(temprecDto.getMscf());

                dout.writeUTF(temprecDto.getSFullName());

                /** Query Type */
                if(null != temprecDto.getQueryType()){
                    dout.writeBoolean(true);
                    dout.writeUTF(temprecDto.getQueryType());
                }else dout.writeBoolean(false);

                //CR 11730
                dout.writeBoolean(temprecDto.isIsRTDASend());

                /** Match Words */
//                if(null != temprecDto.getMatchWords()){
//                    dout.writeBoolean(true);
//                    dout.writeUTF(temprecDto.getMatchWords());
//                }else
//                    dout.writeBoolean(false);

                /** MisMatch Words */
//                if(null != temprecDto.getMisMatchWords()){
//                    dout.writeBoolean(true);
//                    dout.writeUTF(temprecDto.getMisMatchWords());
//                }else
//                    dout.writeBoolean(false);

                /* MaxCount Default Zero*/
                byte maxcount = temprecDto.getMaxCount();
                dout.writeByte(maxcount);
                String[] temp = temprecDto.getRMsg();
                for(int i=0;i<maxcount;i++)
                {
                    if(null != temp[i]){
                        /** Part Index */
                        dout.writeByte((byte)i+1);
                        /** Part Index Message */
                        dout.writeUTF(temp[i]);
                    }
                }

                byte[] rbyte = dout.toByteArray();
                dout.close();
                dout = null;
                addBucket(rbyte, temprecDto.getSenderName(), temprecDto.getFName());
            }
        }

	/**
	 * Method to create bucket for every message being sent. The method creates
	 * or stores the information on the message sent in the temporary xml for
	 * associating the message when it is returned back.
	 *
	 * @param sender
	 *            Short code or sender name
	 * @param message
	 *            SMS Message being sent.
	 * @param queryType
	 *            Query Type of the message that is sent
	 * @param matchWords
	 *            Group of words separated by comma that can be present in the
	 *            response message that is retrieved
	 * @param misMatchWords
	 *            Group of words separated by comma that should not present in
	 *            the response message for this particular sent message.
	 */
//	public void createBucket(String message, Message receiveMsgDto) {
//
//            ByteArrayWriter dout = new ByteArrayWriter();
//            if(null != dout){
//
//                /** Sender Code */
//                dout.writeUTF(receiveMsgDto.getShortcode());
//
//               //Dont save message in inbox
//                dout.writeBoolean(receiveMsgDto.getDontSaveInbox());
//
//                //Dont Display Notification
//                dout.writeBoolean(receiveMsgDto.isIsNotNewMsg());
//
//                /** Multi Message Format */
//                dout.writeUTF(receiveMsgDto.getMscf());
//
//                /** write the sender Full Name */
//                 dout.writeUTF(receiveMsgDto.getSFullName());
//
//                 /** Query Type Not Present so set the Bit to false */
//                dout.writeBoolean(false);
//
//                /** Match Words Not Present so set the Bit to False */
//                dout.writeBoolean(false);
//
//                /** MisMatch Words Not Present so set the Bit to false */
//                dout.writeBoolean(false);
//
//                /** MaxCount */
//                dout.writeByte(receiveMsgDto.getTempmaxCount());
//
//                /** PartIndex */
//                dout.writeByte(receiveMsgDto.getPartIndex());
//                /** PaerIndex Message */
//                dout.writeUTF(message);
//
//                byte[] rbyte = dout.toByteArray();
//
//                dout.close();
//                dout = null;
//                addBucket(rbyte, receiveMsgDto.getSenderName(), receiveMsgDto.getFName());
//            }
//	}

	/**
	 *
	 */
	private Message populateMessageDTO(String fName) {
            if (null != fName) {
                byte[] rbyte = (byte[])msgSend.get(fName);
                ByteArrayReader dat = new ByteArrayReader(rbyte);
                if(null != dat)
                {

                    Message message = new Message();

                    String[] ids = getBucketId(fName);

                    message.setSenderName(ids[1]);

                    /** ShortCode */
                    message.setShortcode(dat.readUTF());

                    // Dont save receive message in inbox
                    message.setDontSaveInbox(dat.readBoolean());

                    //Dont Display Notification
                    message.setIsNotNewMsg(dat.readBoolean());

                    /** Multi Message Format */
                    message.setMscf(dat.readUTF());

                    /** sender Full Name */
                    message.setSFullName(dat.readUTF());

                    /** QueryType */
                    if(dat.readBoolean())
                        message.setQueryType(dat.readUTF());

                    //CR 11730
                    message.setIsRTDASend(dat.readBoolean());

//                    /** Match Words */
//                    if(dat.readBoolean())
//                        message.setMatchWords(dat.readUTF());
//
//                    /** MisMatch Words */
//                    if(dat.readBoolean())
//                        message.setMisMatchWords(dat.readUTF());

                    /** MaxCount */
                    byte  maxCount = dat.readByte();
//                    while (dat.isNotEnd()) {
//                        dat.readByte();

//                        Logger.debugOnError("data in byte array reader" + dat.readByte());
//                    }
                    message.setMaxCount(maxCount);
                    if(maxCount>0)
                    {
                        String[] msg = new String[maxCount];
                        byte partIndex =0,j=0;
                        try
                        {
                            for(int i=0;i<maxCount;i++)
                            {
                               partIndex = dat.readByte();
//                               //#if VERBOSELOGGING
//                               //|JG|Logger.debugOnError("part index from byte array is "+partIndex );
//                               //#endif //11801
                               if(partIndex-1<0)
                                   break;
                               msg[partIndex-1] = dat.readUTF();
//                               //#if VERBOSELOGGING
//                               //|JG|Logger.debugOnError("msg["+(partIndex-1 )+ "]= "+msg[partIndex-1]);
//                               //#endif //11801
                               j++;
//                               //#if VERBOSELOGGING
//                               //|JG|Logger.debugOnError("RmsgCount incremented "+ j);
//                               //#endif //11801
                            }
                        }catch (Exception e){
							//#if VERBOSELOGGING
       //|JG|Logger.debugOnError("exception"+ e.getMessage());
                            //#endif
                        }
                        message.setRMsg(msg);
                        message.setRmsgCount(j);
                    }

                    message.setFName(fName);

                    dat.close();
                    dat =null;

                    return message;
                }
            }
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Populated Message DTO is null");
            //#endif
            return null;
	}

	/**
	 *
	 */
	public void deInitialize() {
            if(null != rMsgCount){
                rMsgCount.deinitialize();
                rMsgCount = null;
            }
            msgSend.clear();
            aCount = 0;
	}
}
