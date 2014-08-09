import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sasi
 */
public class Contacts {

    private static RecordStoreParser recordStoreParser = null;
    private static String[] contactIndex = null;
    private static String indexedContact = null;
    private static int imageGridIndex = -1;


    private static String[] getContactDetails(boolean isSend, String separator){
        String[] values = null;
            PIM pim = PIM.getInstance();
             try{
                String[] list = pim.listPIMLists(PIM.CONTACT_LIST);
                int count = list.length;
                Logger.debugOnError("Number of contact List:" + count);
                Contact contact = null;
                ContactList addressbook = null;
                Enumeration items = null;
                int nameCount = 0;
                int telCount = 0;
                String personName = null;
                String[] nameField;
                Vector value = new Vector();
                int supportedFormats = -1;
                for(int i=0;i<count;i++){
                    addressbook = (ContactList)pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY, list[i]);
                    if(supportedFormats == -1){
                        items = addressbook.items();
                        Logger.loggerError("FORMATTED_NAME "+addressbook.isSupportedField(Contact.FORMATTED_NAME)+"\n"+
                                "NAME "+addressbook.isSupportedField(Contact.NAME)+"\n"+
                                "NAME_FAMILY "+addressbook.isSupportedField(Contact.NAME_FAMILY) + "\n"+
                                "NAME_GIVEN "+addressbook.isSupportedField(Contact.NAME_GIVEN)+"\n" +
                                "NAME_OTHER "+addressbook.isSupportedField(Contact.NAME_OTHER)+"\n"+
                                "NAME_SUFFIX "+addressbook.isSupportedField(Contact.NAME_SUFFIX)+"\n"+
                                "NAME_PREFIX "+addressbook.isSupportedField(Contact.NAME_PREFIX)+ "\n"+                                
                                "NICKNAME "+addressbook.isSupportedField(Contact.NICKNAME));
                        if(addressbook.isSupportedField(Contact.FORMATTED_NAME)) {
                            supportedFormats = Contact.FORMATTED_NAME;
                        } else if(addressbook.isSupportedField(Contact.NAME)) {
                            supportedFormats = Contact.NAME;
                        } else if(addressbook.isSupportedField(Contact.NAME_GIVEN)) {
                            supportedFormats = Contact.NAME_GIVEN;
                        } else if(addressbook.isSupportedField(Contact.NAME_OTHER)) {
                            supportedFormats = Contact.NAME_OTHER;
                        } else if(addressbook.isSupportedField(Contact.NAME_PREFIX)) {
                            supportedFormats = Contact.NAME_PREFIX;
                        } else if(addressbook.isSupportedField(Contact.NAME_SUFFIX)) {
                            supportedFormats = Contact.NAME_SUFFIX;
                        }
                    }

                    while (items.hasMoreElements()) {
                        contact = (Contact)items.nextElement();
                        telCount = contact.countValues(Contact.TEL);
                        nameCount = contact.countValues(supportedFormats);
           

                        if (telCount > 0 && nameCount > 0)
                        {
                            if (supportedFormats == 105) {//Contact.FORMATTED_NAME
                                personName = contact.getString(supportedFormats, 0);
                                for (int k = 0; k < telCount; k++) {
                                    if(isSend){ //CR 14669
                                        value.addElement(personName+separator+contact.getString(Contact.TEL, k));
                                    } else value.addElement(contact.getString(Contact.TEL, k)+"^"+personName);
                                  //  Logger.debugOnError("NAME = : "+ personName + " and  NUM =  : " + contact.getString(Contact.TEL, k));
                                }
                            } else if (supportedFormats == 106) {//Contact.NAME
                                nameField = contact.getStringArray(supportedFormats, 0);
                                //13751
                                for (int k = 0; k < telCount; k++) {
                                    if (nameField[Contact.NAME_GIVEN] != null && nameField[Contact.NAME_FAMILY] != null){
                                        if(isSend){ //CR 14669
                                            value.addElement(nameField[Contact.NAME_GIVEN]+" "+nameField[Contact.NAME_FAMILY]+separator+contact.getString(Contact.TEL, k));
                                        } else value.addElement(contact.getString(Contact.TEL, k)+"^"+nameField[Contact.NAME_GIVEN]+" "+nameField[Contact.NAME_FAMILY]);
                                    }
                                    //13751
                                    else if(nameField[Contact.NAME_FAMILY] != null) {
                                        if(isSend){ //CR 14669
                                            value.addElement(nameField[Contact.NAME_FAMILY]+separator+contact.getString(Contact.TEL, k));
                                        } else value.addElement(contact.getString(Contact.TEL, k)+"^"+nameField[Contact.NAME_FAMILY]);
                                     //   Logger.debugOnError("Contact.NAME_FAMILY:"+nameField[Contact.NAME_FAMILY] + ",  NUMBER = " + contact.getString(Contact.TEL, k) );
                                    }
                                    else if(nameField[Contact.NAME_GIVEN] != null) {
                                        if(isSend){ //CR 14669
                                            value.addElement(nameField[Contact.NAME_GIVEN]+separator+contact.getString(Contact.TEL, k));
                                        } else value.addElement(contact.getString(Contact.TEL, k)+"^"+nameField[Contact.NAME_GIVEN]);
                                    //    Logger.debugOnError("Contact.NAME_GIVEN:"+nameField[Contact.NAME_GIVEN] + "AND NUMBER = " + contact.getString(Contact.TEL, k) );
                                    }
                                                                    
                                }
                            }
                        }
                    }
                }
                if(value.size()>0){
                    Logger.debugOnError("Total Contacts= " + value.size() + "\n");
                    values = new String[value.size()];
                    value.copyInto(values);
                    value.removeAllElements();
                    value = null;
                }
            } catch (SecurityException e) {
                Logger.loggerError("Set Contact Details Security exception:" + e.toString());
            } catch (Exception e){
                Logger.loggerError("Set Contact Details exception:" + e.toString());

            }
           // values = new String[]{"fer:18587037686","sasi:918754415792","nata:18583531337","hema:9849343424","muthu:1234567891"};
            return values;
    }

    //CR 14824
    public static void addContacts(String name, String msisdn){
        try {

            PIM pim = PIM.getInstance();
            ContactList contactList = (ContactList) pim.openPIMList(PIM.CONTACT_LIST, PIM.WRITE_ONLY);
            Contact new_contact = contactList.createContact();

            if (contactList.isSupportedField(Contact.NAME)) {
                String[] names = new String[contactList.stringArraySize(Contact.NAME)];
                if (contactList.isSupportedArrayElement(Contact.NAME, Contact.NAME_FAMILY)) {
                    names[Contact.NAME_FAMILY] = name;
                }
                if (new_contact.countValues(Contact.NAME) > 0) {
                    new_contact.setStringArray(
                            Contact.NAME, PIMItem.ATTR_NONE, 0, names);
                } else {
                    new_contact.addStringArray(
                            Contact.NAME, PIMItem.ATTR_NONE, names);
                }
            } else if(contactList.isSupportedField(Contact.FORMATTED_NAME)) {
                new_contact.addString(Contact.FORMATTED_NAME, PIMItem.ATTR_NONE, name);
            } else if(contactList.isSupportedField(Contact.NAME_GIVEN)) {
                new_contact.addString(Contact.NAME_GIVEN, PIMItem.ATTR_NONE, name);
            } else if(contactList.isSupportedField(Contact.NAME_OTHER)) {
                new_contact.addString(Contact.NAME_OTHER, PIMItem.ATTR_NONE, name);
            } else if(contactList.isSupportedField(Contact.NAME_PREFIX)) {
                new_contact.addString(Contact.NAME_PREFIX, PIMItem.ATTR_NONE, name);
            } else if(contactList.isSupportedField(Contact.NAME_SUFFIX)) {
                new_contact.addString(Contact.NAME_SUFFIX, PIMItem.ATTR_NONE, name);
            }

            if (contactList.isSupportedField(Contact.TEL)) {
                if (new_contact.countValues(Contact.TEL) > 0) {
                    new_contact.setString(Contact.TEL, Contact.ATTR_NONE, 0,
                            msisdn);
                } else {
                    new_contact.addString(Contact.TEL, Contact.ATTR_NONE,
                            msisdn);
                }
            }

//            if (contactList.isSupportedField(Contact.TEL)) {
//                new_contact.addString(Contact.TEL, PIMItem.ATTR_NONE, msisdn);
//            }
//
////            contactList.getSupportedFields();
//
//            if(contactList.isSupportedField(Contact.FORMATTED_NAME)) {
//                new_contact.addString(Contact.FORMATTED_NAME, PIMItem.ATTR_NONE, name);
//            } else if(contactList.isSupportedField(Contact.NAME_GIVEN)) {
//                new_contact.addString(Contact.NAME_GIVEN, PIMItem.ATTR_NONE, name);
//            } else if(contactList.isSupportedField(Contact.NAME_OTHER)) {
//                new_contact.addString(Contact.NAME_OTHER, PIMItem.ATTR_NONE, name);
//            } else if(contactList.isSupportedField(Contact.NAME_PREFIX)) {
//                new_contact.addString(Contact.NAME_PREFIX, PIMItem.ATTR_NONE, name);
//            } else if(contactList.isSupportedField(Contact.NAME_SUFFIX)) {
//                new_contact.addString(Contact.NAME_SUFFIX, PIMItem.ATTR_NONE, name);
//            } else if(contactList.isSupportedField(Contact.NAME)) {
//                new_contact.addString(Contact.NAME, PIMItem.ATTR_NONE, name);
//            }
            new_contact.commit();
            contactList.close();
        } catch(SecurityException securityException){
            Logger.loggerError("Add Contact Details Security exception:" + securityException.toString());
        } catch(Exception exception){
            Logger.loggerError("Add Contact Details exception:" + exception.toString());
        }
    }

    //CR 14469
    public static String getUploadContacts(String separator){
        
        String[] values = getContactDetails(true,separator);
        int count = 0;
        StringBuffer resultString = new StringBuffer();
        if(null != values && (count=values.length)>0){
            for(int i=0;i<count;i++){
                resultString.append(values[i]).append("\n");
            }
        }
        return resultString.toString().trim();
    }

    private static boolean opendContactRecord(boolean isCreate, String name){
        boolean isOpen = false;
        if(null == recordStoreParser){
            recordStoreParser = new RecordStoreParser();
            if(!recordStoreParser.openRecordStore(name, isCreate, false, false))
                isOpen = true;
            else closeContactRecord();
        }
        return isOpen;
    }

    private static void closeContactRecord(){
        if(null != recordStoreParser){
            recordStoreParser.closeRecordStore();
            recordStoreParser = null;
        }
    }

    //CR 14672, 14675, 14698
    public static void updateMessagePlusAppContacts(String response, byte replaceType){
        if(replaceType == 0 || replaceType == 3){
            replaceContacts(response, replaceType);
        } else if(replaceType == 1){
            updateContactStatus(response);
        } else if(replaceType == 2){
            updateSingleContact(response);
        } 
    }

    //CR 14698
    private static String updateSingleContact(String response){
        if(response.length()>0){
            String msisdn = response.substring(0,response.indexOf("("));
            response = response.substring(msisdn.length()+1, response.length()-1);
            response = response.replace(':', '^');
            if(opendContactRecord(false,RecordManager.shorthandContacts)){
                    synchronized(recordStoreParser){
                            if(recordStoreParser.getNumRecords()>0){
                                    ByteArrayReader byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                                    ByteArrayWriter dOutStream = new ByteArrayWriter();
                                    String msdn = null;
                                    String orginalMsdn = null;
                                    int currentCount = byteArrayReader.readInt();
                                    dOutStream.writeInt(currentCount);
                                    int index = -1;
                                    for(int i=0;i<currentCount;i++){
                                            msdn = byteArrayReader.readUTF();
                                            orginalMsdn = msdn;
                                            index = msdn.indexOf("^");
                                            msdn = msdn.substring(index+1,msdn.indexOf("^",index+1));
                                            if(msisdn.compareTo(msdn) == 0){
                                                //CR 14742
                                                String temp = orginalMsdn.substring(0,index)+'^'+msisdn+'^'+response;
                                                
                                                index = orginalMsdn.indexOf("^",index+1);
                                                int version = orginalMsdn.indexOf("^",index+1);
                                                index = orginalMsdn.indexOf("^",version+1);

                                                msdn = orginalMsdn.substring(version+1,index);

                                                version = response.indexOf("^");
                                                index = response.indexOf("^",version+1);

                                                response = response.substring(version+1,index);

                                                if(response.length() != msdn.length() || response.compareTo(msdn) != 0){
                                                    Settings.setContactGridUpdate(true);
                                                }

                                                orginalMsdn = temp;
                                                temp = null;
                                            }
                                            dOutStream.writeUTF(orginalMsdn);
                                    }
                                    byteArrayReader.close();
                                    byteArrayReader = null;
                                    recordStoreParser.setRecord(1, dOutStream.toByteArray());
                                    dOutStream.close();
                                    dOutStream = null;
                            }
                    }
                    closeContactRecord();
            }
        }
        return response;
    }

    //CR 14672, 14803
    private static void replaceContacts(String controlChannel, byte responseType){
        boolean isNotUpdateThumb = true;
        boolean isGroup = responseType==3?true:false;
        if(controlChannel.trim().length()>0){
            controlChannel = Utilities.replace(controlChannel, "(", "");
            controlChannel = Utilities.replace(controlChannel, ")", "");
            controlChannel = controlChannel.replace(':', '^');
            String[] contacts = Utilities.split(controlChannel, "\n");
            int count = 0;
            if(null != contacts && (count = contacts.length)>0){
                ByteArrayWriter dOutStream = new ByteArrayWriter();
                dOutStream.writeInt(count);
                for(int i=0;i<count;i++){
                    dOutStream.writeUTF(contacts[i]);
                    if(isNotUpdateThumb){
                        if(isGroup && contacts[i].trim().indexOf("^ ^ ^") != 0 &&
                            contacts[i].trim().indexOf("^ ^*^") != 0){
                            isNotUpdateThumb = false;
                        } else if(contacts[i].trim().indexOf("+^ ^") != 0 &&
                            contacts[i].trim().indexOf("^ ^ ^") != 0){
                            isNotUpdateThumb = false;
                        }
                    }
                }
                controlChannel = RecordManager.shorthandContacts;
                if(isGroup){
                    controlChannel = RecordManager.shorthandGroupContacts;
                }
                if(opendContactRecord(true, controlChannel)){
                    synchronized(recordStoreParser){
                        if(recordStoreParser.getNumRecords() == 0){
                            recordStoreParser.addRecord(new byte[0]);
                        }
                        recordStoreParser.setRecord(1, dOutStream.toByteArray());
                    }
                    closeContactRecord();
                }
                dOutStream.close();
                dOutStream = null;
            }
        } else {
            controlChannel = RecordManager.shorthandContacts;
            if(isGroup){
                controlChannel = RecordManager.shorthandGroupContacts;
            }
            RecordStoreParser.deleteRecordStore(controlChannel, true);
        }
        if(isGroup){
            //CR 14741
            if(isNotUpdateThumb){
                RecordStoreParser.deleteRecordStore(RecordManager.groupGridThumbnailImage, true);
            } else {
                Settings.setGroupContactGridUpdate(true);
            }
        } else {
            //CR 14741
            if(isNotUpdateThumb){
                RecordStoreParser.deleteRecordStore(RecordManager.gridThumbnailImage, true);
            } else {
                Settings.setContactGridUpdate(true);
            }
        }
    }

    //CR 14675
    private static void updateContactStatus(String status){
            status = "\n"+status+"\n";
            if(opendContactRecord(false, RecordManager.shorthandContacts)){
                    synchronized(recordStoreParser){
                            if(recordStoreParser.getNumRecords()>0){
                                    ByteArrayReader byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                                    ByteArrayWriter dOutStream = new ByteArrayWriter();
                                    String msdn = null;
                                    String orginalMsdn = null;
                                    int currentCount = byteArrayReader.readInt();
                                    dOutStream.writeInt(currentCount);
                                    int index = -1;
                                    for(int i=0;i<currentCount;i++){
                                            msdn = byteArrayReader.readUTF();
                                            orginalMsdn = msdn;
                                            index = msdn.indexOf("^");
                                            msdn = msdn.substring(index+1,msdn.indexOf("^",index+1));
                                            if(status.indexOf("\n"+msdn+"\n")>-1){
                                                    if(orginalMsdn.toCharArray()[orginalMsdn.length()-1] == '^'){
                                                            orginalMsdn += "*";
                                                    }
                                            } else if(orginalMsdn.toCharArray()[orginalMsdn.length()-1] == '*') {
                                                    orginalMsdn = orginalMsdn.substring(0, orginalMsdn.length()-1);
                                            }
                                            dOutStream.writeUTF(orginalMsdn);
                                    }
                                    byteArrayReader.close();
                                    byteArrayReader = null;
                                    recordStoreParser.setRecord(1, dOutStream.toByteArray());
                                    dOutStream.close();
                                    dOutStream = null;
                            }
                    }
                    closeContactRecord();
            }
    }

    //CR 14675, 14672, 14665
    public static String[] getContacts(boolean isAll, String displayFormat, int recordType){
        String recordName = RecordManager.shorthandContacts;
        String secondIndicator = "";
        //CR 14787
        // 0 - Contact(contfav, contall)
        // 1 - Group (grpall, grpown)
        // 2 - Shout (shtall, shtown)
        // 3 - joined
        if(recordType > 0){
            //bug 14787
            if(!isAll && recordType != 3){
                secondIndicator ="^*^";
            }
            recordName = RecordManager.shorthandGroupContacts;
        }
        String[] contacts = null;
        ByteArrayReader byteArrayReader = null;
        if(opendContactRecord(false, recordName)){
            synchronized(recordStoreParser){
                if(recordStoreParser.getNumRecords()>0){
                    byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                }
            }
            closeContactRecord();
        }
        if(null != byteArrayReader){
            String[] elements = null;
            String indicator = null;
            if(recordType == 0){
                indicator = "^+^";
                elements = new String[]{"[friendname]","[friendID]","[plususer]", "[friendIMG]", "[friendstatus]"};
            } else if(recordType == 1){
                isAll = false;
                indicator = "^G";
                elements = new String[]{"[groupname]","[groupID]","[groupIMG]","[groupowner]","[groupownerID]"};
            } else if(recordType == 2){
                isAll = false;
                indicator = "^S";
                elements = new String[]{"[shoutname]","[shoutID]","[shoutIMG]","[shoutowner]","[shoutownerID]"};
            } else if(recordType == 3){
                indicator = "";
                elements = new String[]{"[joinedname]","[joinedID]","[joinedIMG]","[joinedowner]","[joinedownerID]"};
            }
            int count = byteArrayReader.readInt();
            String tempComtact = null;
            String[] sValue = null;
            Vector contactsList = new Vector();
            Vector index = new Vector();
            for(int i=0;i<count;i++){
                tempComtact =  byteArrayReader.readUTF();
                if(isAll || tempComtact.indexOf(indicator)>-1 &&
                        (secondIndicator.length() == 0 || tempComtact.indexOf(secondIndicator)>-1)){
                    System.out.println(tempComtact);
                    sValue = Utilities.split(tempComtact, "^");
                    tempComtact = Utilities.replace(displayFormat, elements[0], sValue[0]);
                    tempComtact = Utilities.replace(tempComtact, elements[1], sValue[1]);
                    tempComtact = Utilities.replace(tempComtact, elements[2], sValue[2]);
                    tempComtact = Utilities.replace(tempComtact, elements[3], sValue[3]);
                    tempComtact = Utilities.replace(tempComtact, elements[4], sValue[4]);
                    contactsList.addElement(tempComtact);
                    index.addElement(Integer.toString(i));
                    tempComtact = null;
                }
            }
            byteArrayReader.close();
            byteArrayReader = null;

            if(contactsList.size()>0){
                contacts = new String[contactsList.size()];
                contactsList.copyInto(contacts);
                contactsList.removeAllElements();

                contactIndex = new String[index.size()];
                index.copyInto(contactIndex);
                index.removeAllElements();
            }
            index = null;
            contactsList = null;
        }
        return contacts;
    }

    //CR 14802
    public static String getGroupOrShoutOwnerName(String msisdn, byte recordType){
        String recordName = RecordManager.shorthandContacts;
        //CR 14787
        // 0 - Contact(contfav, contall)
        // 1 - Group (grpall, grpown)
        // 2 - Shout (shtall, shtown)
        // 3 - joined
//        if(recordType > 0){
//            recordName = RecordManager.shorthandGroupContacts;
//        }
        
        ByteArrayReader byteArrayReader = null;
        if(opendContactRecord(false, recordName)){
            synchronized(recordStoreParser){
                if(recordStoreParser.getNumRecords()>0){
                    byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                }
            }
            closeContactRecord();
        }
        String groupOrShoutOwner = msisdn;
        if(null != byteArrayReader){
            int count = byteArrayReader.readInt();
            String tempComtact = null;
            for(int i=0;i<count;i++){
                tempComtact =  byteArrayReader.readUTF();
                if(tempComtact.indexOf("^"+msisdn+"^") >-1){
                    groupOrShoutOwner = tempComtact.substring(0,tempComtact.indexOf("^"))+" " + msisdn;
                    System.out.println(tempComtact);
                    break;
                }
            }
            byteArrayReader.close();
            byteArrayReader = null;
        }
        return groupOrShoutOwner;
    }

    public static int[] getContactIndex(){
        int[] index = null;
        int length = -1;
        if(null != contactIndex && (length=contactIndex.length)>0){
             index = new int[contactIndex.length];
             for(int i=0;i<length;i++){
                 index[i] = Integer.parseInt(contactIndex[i]);
             }
             contactIndex = null;
        }
        return index;
    }

        //CR 14823
    public static boolean isContactMsisdn(String msisdn){
        boolean isPresent = false;
        ByteArrayReader byteArrayReader = null;
        if(opendContactRecord(false, RecordManager.shorthandContacts)){
            synchronized(recordStoreParser){
                if(recordStoreParser.getNumRecords()>0){
                    byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                }
            }
            closeContactRecord();
        }
        if(null != byteArrayReader){
            int count = byteArrayReader.readInt();
            String selectedValue = null;
            while(count>-1){
                selectedValue =  byteArrayReader.readUTF().trim();
                if(selectedValue.indexOf("^"+msisdn+"^") > -1){
                    isPresent = true;
                    break;
                }
                count--;
            }
            byteArrayReader.close();
            byteArrayReader = null;
        }
        return isPresent;
    }

    //CR 14743
    public static String[] getGridIndex(String msisdn, byte recordIndex){
        String[] imageIndex = null;
        //Bug 14832
        if(recordIndex>-1 && null != msisdn){
            int index = imageGridIndex;
            imageGridIndex = -1;
            if(index == -1 || null == indexedContact || msisdn.compareTo(indexedContact) != 0){
                ByteArrayReader byteArrayReader = null;
                String recordName = RecordManager.shorthandContacts;
                if(recordIndex == 1){
                    recordName = RecordManager.shorthandGroupContacts;
                }
                if(opendContactRecord(false, recordName)){
                    synchronized(recordStoreParser){
                        if(recordStoreParser.getNumRecords()>0){
                            byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                        }
                    }
                    closeContactRecord();
                }
                if(null != byteArrayReader){
                    int count = byteArrayReader.readInt();
                    String selectedValue = null;
                    index = -1;
                    while(count>-1){
                        selectedValue =  byteArrayReader.readUTF().trim();
                        if(selectedValue.indexOf("+^ ^") == -1
                            && selectedValue.indexOf("^ ^ ^") == -1 &&
                            selectedValue.indexOf("^ ^*") == -1){
                            index++;
                            if(selectedValue.indexOf("^"+msisdn+"^") >-1){
                                if(recordIndex == 1){
                                    imageIndex = new String[]{""+index,Utilities.split(selectedValue, "^")[2]};
                                } else {
                                    imageIndex = new String[]{""+index,Utilities.split(selectedValue, "^")[3]};
                                }
                                break;
                            }
                        } else if(selectedValue.indexOf("^"+msisdn+"^") >-1){ //bug 14812
                            break;
                        }
                        count--;
                    }
                    byteArrayReader.close();
                    byteArrayReader = null;
                }
            } else {
                imageIndex = new String[]{index+""};
            }
        }
        return imageIndex;
    }

    public static String getSelectedIndexContacts(int index, byte entryIndex){
        ByteArrayReader byteArrayReader = null;
        String recordName = RecordManager.shorthandContacts;
        if(entryIndex == 1){
            recordName = RecordManager.shorthandGroupContacts;
        }
        if(opendContactRecord(false, recordName)){
            synchronized(recordStoreParser){
                if(recordStoreParser.getNumRecords()>0){
                    byteArrayReader = new ByteArrayReader(recordStoreParser.getRecord(1));
                }
            }
            closeContactRecord();
        }
        
        String selectedValue = null;
        if(null != byteArrayReader){
            byteArrayReader.readInt();
            imageGridIndex = -1;
            while(index>-1){
                selectedValue =  byteArrayReader.readUTF();
                if(selectedValue.trim().indexOf("+^ ^") == -1 &&
                        selectedValue.trim().indexOf("^ ^ ^") == -1 &&
                        selectedValue.trim().indexOf("^ ^*^") == -1){ //CR 14804
                    imageGridIndex++;
                }
                index--;
            }
            index = selectedValue.indexOf("^");
            indexedContact = selectedValue.substring(index+1,selectedValue.indexOf("^",index+1));

            byteArrayReader.close();
            byteArrayReader = null;
        }
        return selectedValue;
    }

    //CR 14806
    public static boolean isSHGroupOrShoutContactsRefresh(byte recordIndex){
        boolean isRefresh = true;
        String recordName = RecordManager.shorthandContacts;
        if(recordIndex == 1){
            recordName = RecordManager.shorthandGroupContacts;
        }
        if(opendContactRecord(false, recordName)){
            synchronized(recordStoreParser){
                if(recordIndex == 1 && recordStoreParser.getNumRecords() > 0){
                    isRefresh = false;
                } else if(recordIndex == 0 && recordStoreParser.getNumRecords() == 0){
                    isRefresh = false;
                }
            }
            closeContactRecord();
        }
        return isRefresh;
    }
}
