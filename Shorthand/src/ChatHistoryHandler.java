

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sasi
 */
public class ChatHistoryHandler {

    public String chatId = null;
    public byte DIRECTION_IN = 1;
    public byte DIRECTION_OUT = 2;

    //CR 14325
    public byte STATUS_DELIVERIED = 2;
    public byte STATUS_DISPLAYED = 3;
    
    private int EACH_RECORD_ROW_COUNT = 4;

    public boolean isMessagePlus = false;
    private int MAX_SCRIPT_COUNT = 19;
    private String widgetName = null;
    
    private int DATE_STRING_LENGTH = 8;

    private static String friendRecordSeparator = "^^";
    private static String elementSeparator = "^";
    private String buddyName = null;

    public void initialize(String widgetName, String chatId, String friendName,
        String abberVation, String plusUser){
        this.chatId = chatId;
        this.widgetName = widgetName;
        this.buddyName = friendName;
        if(plusUser.compareTo("+") == 0){
            isMessagePlus = true;
        }
        resetUnreadChatCount(false, widgetName, chatId, friendName, plusUser, false);
    }

    /*
     *  1st Index ChatId
     *  2nd Index FriendName
     *  3th Index MessagePlue(User is message Plus user or not Indication)
     *  4rd Index ChatCount(Over all UnreadCount)
     *
     * When ever Message Comes IN/OUT, user name should be append into the 1st postion of the index, and remove from the old list.
     * CR 13059 - While receive the message, client need to increase the unread count for each user.
     *          - If User view the Message, client must to reset the unread count to Zero.
     * CR 12523, 12790
     * Bug 14368
     */
    private void resetUnreadChatCount(boolean isCountIncrease,
            String widgetName, String chatId, String friendName,
            String messagePlusUser, boolean isRecordCreate){
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        chatAppRecord.openRecordStore(widgetName, true, false, false);
        String chatHolders = null;
        String currentUser = null;
        String prefix = null;
        //CR 13059
        int count = 0;
        if(isCountIncrease)
            count = 1;

        if(chatAppRecord.getNumRecords()>0){
            if(chatAppRecord.getRecord(1) != null){
                chatHolders = new String(chatAppRecord.getRecord(1));
                int index = (friendRecordSeparator+chatHolders).
                        indexOf(friendRecordSeparator+chatId+elementSeparator);
                if(index>-1){
                    chatHolders = friendRecordSeparator+chatHolders;
                    int sIndex = chatHolders.indexOf(friendRecordSeparator,index+2);
                    if(sIndex>-1){
                        currentUser = chatHolders.substring(index+2,sIndex);
                        if(isRecordCreate){
                            chatHolders = chatHolders.substring(2,index+2)+chatHolders.substring(sIndex+2);
                        } else {
                            if(index>2){
                                prefix = chatHolders.substring(2,index);
                            }
                            chatHolders = chatHolders.substring(sIndex+2);
                        }
                    } else {
                        if(index == 0){
                            currentUser = chatHolders.substring(2);
                            chatHolders = null;
                        } else {
                            currentUser = chatHolders.substring(index+2);
                            if(isRecordCreate){
                                chatHolders = chatHolders.substring(2,index);
                            } else {
                                prefix = chatHolders.substring(2,index);
                                chatHolders = null;
                            }
                        }
                    }
                    isRecordCreate = true;
                    //Chat Id
                    index = currentUser.indexOf(elementSeparator);
                    if(isCountIncrease){
                        //Friend Name
                        index = currentUser.indexOf(elementSeparator,index+1);
                        //Message Plus
                        index = currentUser.indexOf(elementSeparator,index+1);
                        //CR 13059
                        //Total UnreadCount
                        count = Integer.parseInt(currentUser.substring(index+1))+1;
                    } else if(null == friendName || null == messagePlusUser){
                        //Chat Id
                        currentUser = currentUser.substring(index+1);
                        //Friend Name
                        index = currentUser.indexOf(elementSeparator);
                        friendName = currentUser.substring(0,index);
                        currentUser = currentUser.substring(index+1);

                        //Message Plus
                        index = currentUser.indexOf(elementSeparator);
                        messagePlusUser = currentUser.substring(0,index);
                        currentUser = currentUser.substring(index+1);
                        count = 0;
                    }
                }
            }
        } 

        //bug 14368
        if(isRecordCreate){
            //bug 14192
            if(null == friendName || friendName.length() == 0)
                friendName = " ";

            //CR 14441
            currentUser = chatId+elementSeparator+friendName+elementSeparator+
                    messagePlusUser+elementSeparator+count;

            //Bug 14819
            if(null != prefix){
                currentUser = prefix + friendRecordSeparator+currentUser;
            }

            if(null != chatHolders && chatHolders.length()>0){
                currentUser += friendRecordSeparator+chatHolders;
            }
            if(chatAppRecord.getNumRecords() == 0){
                chatAppRecord.addRecord(currentUser.getBytes());
            } else {
                chatAppRecord.setRecord(1, currentUser.getBytes());
            }
        }
        chatAppRecord.closeRecordStore();
        chatAppRecord = null;
    }

    public static int getAppLevelUnreadChatCount(String widgetName){
        int appLevelUnreadCount = 0;
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        chatAppRecord.openRecordStore(widgetName, true, false, false);
        byte[] usersByte = null;
        if(chatAppRecord.getNumRecords()>0){
            usersByte = chatAppRecord.getRecord(1);
        }
        chatAppRecord.closeRecordStore();
        chatAppRecord = null;
        
        if(null != usersByte && usersByte.length>0){
            String users = new String(usersByte);
            String[] userList = Utilities.split(users, friendRecordSeparator);
            int index = -1;
            int sIndex = -1;
            for(int i=0;i<userList.length;i++){
                //Chat Id
                index = userList[i].indexOf(elementSeparator);
                //Friend Name
                index = userList[i].indexOf(elementSeparator,index+1);
                //Message Plus
                index = userList[i].indexOf(elementSeparator,index+1);
                
                //Chat Unread Count
                //CR 14441
//                sIndex = userList[i].indexOf(elementSeparator,index+1);
//                appLevelUnreadCount += Integer.parseInt(userList[i].substring(index+1,sIndex));
                appLevelUnreadCount += Integer.parseInt(userList[i].substring(index+1));
            }
        }
        return appLevelUnreadCount;
    }

    //CR 13118
    //bug 14075
    public static String getUserRecord(String widgetName, String chatId){
        String userLevelRecord = null;
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        chatAppRecord.openRecordStore(widgetName, true, false, false);
        byte[] usersByte = null;
        if(chatAppRecord.getNumRecords()>0){
            usersByte = chatAppRecord.getRecord(1);
        }
        chatAppRecord.closeRecordStore();
        chatAppRecord = null;

        if(null != usersByte && usersByte.length>0){
            String users = new String(usersByte);
            //Find the Friend Id
            int index = (friendRecordSeparator+users).
                    indexOf(friendRecordSeparator+chatId+elementSeparator);
            if(index>-1){
                users = friendRecordSeparator+users;
                int sIndex = users.indexOf(friendRecordSeparator,index+2);
                if(sIndex>-1){
                    users = users.substring(index+2,sIndex);
                } else {
                    if(index != 0){
                        users = users.substring(index+2);
                    } else {
                        users = users.substring(2);
                    }
                }
                index = users.indexOf(elementSeparator);
                //Friend Name
                index = users.indexOf(elementSeparator,index+1);
                //Message Plus
                index = users.indexOf(elementSeparator,index+1);

                //Chat count
                userLevelRecord =  users.substring(0,index+1) + (Integer.parseInt(users.substring(index+1))+1);

//                sIndex = users.indexOf(elementSeparator,index+1);
//
//                //Chat Unread Count
//                userLevelRecord =  users.substring(0,index+1) + (Integer.parseInt(users.substring(index+1, sIndex))+1);
            }
        }
        return userLevelRecord;
    }

    public static String[] getRecentHistroyUserList(String widgetName){
        String[] recentUserList = null;
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        chatAppRecord.openRecordStore(widgetName, true, false, false);
        byte[] usersByte = null;
        if(chatAppRecord.getNumRecords()>0){
            usersByte = chatAppRecord.getRecord(1);
        }
        chatAppRecord.closeRecordStore();
        chatAppRecord = null;
        
        if(null != usersByte && usersByte.length>0){
            //Chat Id, //Friend Name, //Message Plus, //Chat Unread Count
            recentUserList = Utilities.split(new String(usersByte), friendRecordSeparator);
        }
        return recentUserList;
    }

    public static boolean deleteHistory(String widgetName, String chatId){
        boolean isDeleted = false;
        RecordStoreParser.deleteRecordStore(chatId, true);
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        chatAppRecord.openRecordStore(widgetName, true, false, false);
        if(chatAppRecord.getNumRecords()>0){
            byte[] usersByte = chatAppRecord.getRecord(1);
            if(null != usersByte && usersByte.length>0){
                String usersList = new String(usersByte);
                int index = usersList.indexOf(chatId);
                if(index>-1){
                    int secondIndex = usersList.indexOf(friendRecordSeparator,index);
                    if(secondIndex>-1){
                        usersList = usersList.substring(0,index)+usersList.substring(secondIndex+2);
                    } else if(index>0){
                        usersList = usersList.substring(0,index-2);
                    } else {
                        usersList = "";
                    }
                    usersByte = usersList.getBytes();
                    chatAppRecord.setRecord(1, usersByte);
                    isDeleted = true;
                }
            }
            usersByte = null;
        }
        chatAppRecord.closeRecordStore();
        chatAppRecord = null;
        return isDeleted;
    }

    public ChatScriptDto[] getHistoryScriptsDto(){
        ChatScriptDto[] chatScriptDto = null;
        RecordStoreParser recordStoreParser = new RecordStoreParser();
        recordStoreParser.openRecordStore(chatId, true, false, false);
        int count = recordStoreParser.getNumRecords();
        if(count>0){
            chatScriptDto = new ChatScriptDto[count/EACH_RECORD_ROW_COUNT];
            String rowString = null;
            for(int i=1,j=0;i<=count;i+=EACH_RECORD_ROW_COUNT,j++){
                chatScriptDto[j] = new ChatScriptDto();
                rowString = new String(recordStoreParser.getRecord(i));
                chatScriptDto[j].setDirection(Byte.parseByte(rowString.substring(0,1)));
                chatScriptDto[j].setDate(Long.parseLong(rowString.substring(1,DATE_STRING_LENGTH+1)));
                chatScriptDto[j].setChatSequn(rowString.substring(DATE_STRING_LENGTH+1));
                //bug 14557
                if(null != chatScriptDto[j].getChatSequn() && chatScriptDto[j].getChatSequn().length()>0){
                    chatScriptDto[j].setChatSequence(Long.parseLong(chatScriptDto[j].getChatSequn()));
                }
                chatScriptDto[j].setStatus(recordStoreParser.getRecord(i+1)[0]);
                try {
                    chatScriptDto[j].setScript(new String(recordStoreParser.getRecord(i+2),"utf-8"));
                } catch(Exception exception){
                    chatScriptDto[j].setScript(new String(recordStoreParser.getRecord(i+2)));
                }
                if(null != recordStoreParser.getRecord(i+3)){
                    chatScriptDto[j].setFileLocation(new String(recordStoreParser.getRecord(i+3)));
                }
                //Ready to Display the Message
                if(chatScriptDto[j].getDirection() == DIRECTION_IN){
                    recordStoreParser.setRecord(i+1, new byte[]{3});
                }
            }
        }
        recordStoreParser.closeRecordStore();
        recordStoreParser = null;
        return chatScriptDto;
    }

    //CR 14423
    public synchronized ChatScriptDto appenedChatScript(Message message, boolean isAppend,
            String fileLocation){

        ChatScriptDto chatScriptDto = null;
        try{
            String rowValues = null;
            byte status = 0;
            String chatScript = null;
            boolean isNotCurrent = true;
            if(isAppend){
                rowValues = DIRECTION_OUT + "";
                status = 1;
                message.setChatDate(Utilities.getCurrentDateYYYYMMDDFormat());
                if(null == message.getChatId())
                    message.setChatId(this.chatId);
                //CR 14330
                chatScript = Constants.appendText[28]+": " + message.getCurRMsg() + " ("+Utilities.getHHMM24HrsChatFormat()+")";
            } else {
                rowValues = DIRECTION_IN + "";
                if(null != chatId && chatId.compareTo(message.getChatId()) == 0){
                    status = 3;
                    if(message.getMessagePlus().compareTo("+") == 0){
                        isMessagePlus = true;
                    }
                    isAppend = true;
                } else {
                    isNotCurrent = false;
                    status = 2;
                }
                chatScript = message.getCurRMsg();
            }

            if(isNotCurrent){
                chatScriptDto = new ChatScriptDto();
                chatScriptDto.setScript(chatScript);
                if(status<3){
                    chatScriptDto.setStatus(status);
                } else chatScriptDto.setStatus((byte)2);
                chatScriptDto.setDirection(Byte.parseByte(rowValues));
                chatScriptDto.setDate(Long.parseLong(message.getChatDate()));
                chatScriptDto.setChatSequn(message.getChatSequence());
                //bug 14557
                if(null != message.getChatSequence() && message.getChatSequence().length()>0){
                    chatScriptDto.setChatSequence(Long.parseLong(message.getChatSequence()));
                }
                chatScriptDto.setFileLocation(fileLocation);
            }

            //CR 14330
            rowValues += message.getChatDate() + message.getChatSequence();

            RecordStoreParser recordStoreParser = new RecordStoreParser();
            recordStoreParser.openRecordStore(message.getChatId(), true, false, false);
            int count = recordStoreParser.getNumRecords();

            if(count>(MAX_SCRIPT_COUNT*EACH_RECORD_ROW_COUNT)){
                count -= EACH_RECORD_ROW_COUNT;
                for(int i=1;i<=count;i+=EACH_RECORD_ROW_COUNT){
                    recordStoreParser.setRecord(i,
                            recordStoreParser.getRecord(i+EACH_RECORD_ROW_COUNT));
                    recordStoreParser.setRecord(i+1,
                            recordStoreParser.getRecord(i+EACH_RECORD_ROW_COUNT+1));
                    recordStoreParser.setRecord(i+2,
                            recordStoreParser.getRecord(i+EACH_RECORD_ROW_COUNT+2));
                    //bug 14627, 14651
                    if(null != recordStoreParser.getRecord(i+EACH_RECORD_ROW_COUNT+3)){
                        recordStoreParser.setRecord(i+3,
                                recordStoreParser.getRecord(i+EACH_RECORD_ROW_COUNT+3));
                    } else recordStoreParser.setRecord(i+3,new byte[0]);
                }
            } else {
                //Direction+Date+chatSequence
                recordStoreParser.addRecord(new byte[0]);
                //Status
                recordStoreParser.addRecord(new byte[0]);
                //Script
                recordStoreParser.addRecord(new byte[0]);
                //FileLocation
                recordStoreParser.addRecord(new byte[0]);
            }

            recordStoreParser.setRecord(count+1, rowValues.getBytes());
            recordStoreParser.setRecord(count+2, new byte[]{status});
            recordStoreParser.setRecord(count+3, chatScript.getBytes("utf-8"));
            //CR 14423
            if(null == fileLocation){
                fileLocation = "";
            }
            //Bug 14651
            recordStoreParser.setRecord(count+4, fileLocation.getBytes());
            
            recordStoreParser.closeRecordStore();
            recordStoreParser = null;

            //bug 14591
            if(null == message.getSFullName()){
                message.setSFullName(widgetName);
            }
            if(null == message.getChatName()){
                message.setChatName(buddyName);
            }
            //View Picture Not have any Reference for which app is it
            if(null != message.getSFullName()){
                if(message.getMessagePlus().length()>1){
                    message.setMessagePlus(" ");
                }
                resetUnreadChatCount(!isAppend, message.getSFullName(), message.getChatId(),
                        message.getChatName(), message.getMessagePlus(), true);
            }

            chatScript = null;
            rowValues = null;
        } catch(Exception exception){
            Logger.loggerError(exception.toString());
        }

        return chatScriptDto;
    }

    //CR 14423
    public void updateFileLocation(int currentPostion, int lastPosition,
            String fileLocation){
        RecordStoreParser recordStoreParser = new RecordStoreParser();
        if(!recordStoreParser.openRecordStore(chatId, false, false, false)){
            int count = recordStoreParser.getNumRecords()/EACH_RECORD_ROW_COUNT;

            if(count>(lastPosition-currentPostion)){
                //bug 14423
                count = (count-lastPosition)+currentPostion;
                recordStoreParser.setRecord(count*EACH_RECORD_ROW_COUNT, fileLocation.getBytes());
            }
            recordStoreParser.closeRecordStore();
        }
        recordStoreParser = null;
    }

    //CR 14441
//    public synchronized void updateLastUpdateStatus(String widgetName, String chatId, String statusText){
//        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
//        RecordStoreParser chatAppRecord = new RecordStoreParser();
//        if(!chatAppRecord.openRecordStore(widgetName, false, false, false)){
//            if(chatAppRecord.getNumRecords()>0){
//                if(chatAppRecord.getRecord(1) != null){
//                    String chatHolders = new String(chatAppRecord.getRecord(1));
//                    int index = (friendRecordSeparator+chatHolders).
//                            indexOf(friendRecordSeparator+chatId+elementSeparator);
//                    if(index>-1){
//                        String currentUser = null;
//                        chatHolders = friendRecordSeparator+chatHolders;
//                        int sIndex = chatHolders.indexOf(friendRecordSeparator,index+2);
//                        if(sIndex>-1){
//                            currentUser = chatHolders.substring(index+2,sIndex);
//                            chatHolders = chatHolders.substring(2,index+2)+chatHolders.substring(sIndex+2);
//                        } else {
//                            if(index != 0){
//                                currentUser = chatHolders.substring(index+2);
//                                chatHolders = chatHolders.substring(2,index);
//                            } else {
//                                currentUser = chatHolders.substring(2);
//                                chatHolders = "";
//                            }
//                        }
//
//
//                        //Chat Id
//                        index = currentUser.indexOf(elementSeparator);
//                        //Friend Name
//                        index = currentUser.indexOf(elementSeparator,index+1);
//                        //Message Plus
//                        index = currentUser.indexOf(elementSeparator,index+1);
//                        //CR 13059
//                        //Total UnreadCount
//                        index = Integer.parseInt(elementSeparator,index+1);
//
//                        //Status Text
//                        currentUser = currentUser.substring(index+1)+statusText;
//                        if(chatHolders.length()>0){
//                            currentUser += friendRecordSeparator+chatHolders;
//                        }
//                        chatAppRecord.setRecord(1, currentUser.getBytes());
//                    }
//                }
//            }
//            chatAppRecord.closeRecordStore();
//        }
//        chatAppRecord = null;
//    }

    public synchronized boolean updateStatus(String chatsequence, 
            String chatId, int status){
        RecordStoreParser recordStoreParser = new RecordStoreParser();
        boolean isCurrentUser = false;
        int chatCount = 0;
        if(null != this.chatId && chatId.compareTo(this.chatId) == 0){
            isCurrentUser = true;
        }
        if(!recordStoreParser.openRecordStore(chatId, false, false, false)){
            int count = recordStoreParser.getNumRecords();
            if(count>0){
                String findValue = null;
                for(int i=1;i<=count && chatCount<chatsequence.length();i+=EACH_RECORD_ROW_COUNT){
                    findValue = new String(recordStoreParser.getRecord(i));
                    if(findValue.charAt(0) == '2' &&
                            chatsequence.indexOf(findValue.substring(1+DATE_STRING_LENGTH)) >-1){
                        recordStoreParser.setRecord(i+1, new byte[]{(byte)status});
                        chatCount = chatsequence.indexOf(ChannelData.CHAT_SEQUENCE_SEPARATOR,chatCount);
                        if(chatCount == -1){
                            chatCount = chatsequence.length();
                        } else chatCount++;
                    }
                }
            }
            recordStoreParser.closeRecordStore();
        }
        recordStoreParser = null;
        return isCurrentUser;
    }

    public static void removeChatRecords(String widgetName){
        widgetName = RecordManager.getRecordStoreName(widgetName)+"Chat";
        RecordStoreParser chatAppRecord = new RecordStoreParser();
        if(!chatAppRecord.openRecordStore(widgetName, false, false, false)){
            byte[] userbytes = chatAppRecord.getRecord(1);
            if(null != userbytes && userbytes.length>0){
                String usersList = new String(userbytes);
                int index = -1;
                while((index=usersList.indexOf(elementSeparator))>-1){
                    RecordStoreParser.deleteRecordStore(usersList.substring(0,index), true);
                    if((index = usersList.indexOf(friendRecordSeparator))>-1){
                        //bug 14437
                        usersList = usersList.substring(index+2);
                    } else {
                        usersList = "";
                    }
                }
            }
            chatAppRecord.closeRecordStore();
            chatAppRecord = null;
            RecordStoreParser.deleteRecordStore(widgetName, true);
        }
    }

    public void deinitialize(){
        chatId = null;
        isMessagePlus = false;
    }

}
