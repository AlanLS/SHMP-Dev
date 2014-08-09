/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Sasikumar
 */
public class RecordParser {

    /** Profile Records */
    private String records = null;

    /** Select Record Name */
    private String sRName = "";

    /** Currently Stored Record Value */
    private String rValue = "";

    /** currently Displaying Record Values */
    private String[] rValues = null;

    /** Record Selected Values */
    private Object[] selValues = null;

    private String[] tValues = null;

    /** Element Value AddedCount */
    private int adcount = 0;

    private int rPos = 0;

    private int rCount = 1;

    private RecordStoreParser rStoreParser = null;

    private Hashtable cHashTable = null;

    private String oFiledIndex = null;

    private String pRName = null;

    private boolean isCommon = false;

    private String rOptions = null;
    
    private String orgRecName = null;

    //CR 14672
//    private String AppName = null;

//    private String scContacts = "ScContacts";

    /**
     *  Constructor to be store the Profile Location
     * @param loc Profile Location
     * @throws java.io.IOException
     */
    public RecordParser(){

    }

    /**
     * Method to set the profile level records
     * @param recors Records
     */
    public void setRecords(String recors,String rOption,String pName){
        try{
            if(null != recors){
                recors = recors.replace(',', '^');
                rOptions = rOption;
                String tRecords = recors;
                int index = 0;
                recors = "";
                while((index= tRecords.indexOf(":"))>-1){
                    recors = tRecords.substring(0,index);
                    index = tRecords.indexOf("|",index);
                    if(index>-1)
                        recors += tRecords.substring(index);
                    tRecords = recors;
                }
                records = tRecords;
//                AppName = pName;
                pName = RecordManager.getRecordEntryRecordStoreName(pName);
                openRecordStore(pName, true);
            }
        }catch(Exception e){}
    }
    
    public void clearInitialObjects(){
        
        /** Select Record Name */
        sRName = "";

        /** Currently Stored Record Value */
        rValue = "";

        /** currently Displaying Record Values */
        rValues = null;

        /** Record Selected Values */
        selValues = null;
    
        tValues = null;

        /** Element Value AddedCount */
        adcount = 0;

        rPos = 0;

        oFiledIndex = null;

        pRName = null;

        isCommon = false;

        orgRecName = null;
        
    }
    
    private void openRecordStore(String fName,boolean isOpen){
        try{
            rStoreParser = new RecordStoreParser();
            rStoreParser.openRecordStore(fName, isOpen,false,false);
            rCount = rStoreParser.getNumRecords();
        }catch(Exception e){
            rStoreParser = null;
        }
    }

    /**
     *
     * @param rName
     * @return
     */
    private byte[] getRecordBytes(String rName){
        byte[] rBytes = null;
        rPos = 0;
        if(null != rStoreParser){
            ByteArrayReader dStream = null;
            try{
                for(int i=1;i<=rCount;i++){
                    rBytes = rStoreParser.getRecord(i);
                    if(null != rBytes){
                        dStream = new ByteArrayReader(rBytes);
                        dStream.readInt();
                        if(dStream.readUTF().compareTo(rName) == 0){
                            rPos = i;
                            break;
                        } else rBytes = null;
                        dStream.close();
                        dStream = null;
                    }
                }
            }catch(Exception e){}
            
        }
        return rBytes;
    }



    /**
     * Methos to retrieve the record for the given Display format if display format is not given
     *  use thhe default format
     * @param rName Record Name
     * @param dFormat Display Format
     * @return record array
     */
    public String[] getRecordValues(String orName, String rName,String dFormat){
        addLastRecord();
        String[] values = null;
        removeSelectedRecordValue(orName);
        setRecordValues(rName);
        if(null != dFormat)
            values = getFormatedRecordValue(rName, dFormat);
        else values = getDefaultFormatRecordValue(rName);
        return values;
    }

    /**
     * Method to retrieve the formated Value for the given display format
     *
     * @param rName Record Name
     * @param dFormat Display Format
     * @return Retrieved value
     */
    private String[] getFormatedRecordValue(String rName,String dFormat){
        // Logger.debugOnError("if)rname in getfor record value==" + rName +"dformat=" + dFormat);
        int count;
        String[] values = null;
        if(null != rValues && (count = rValues.length)>0){
            values = new String[count];
            String[] elements = getElements(rName);
            String[] sValue = null;
            int eCount = elements.length;
            for(int i=0;i<count;i++){
                sValue = Utilities.split(rValues[i], "^");
                rName = dFormat;
                for(int j=0;j<eCount;j++){
                    if(rName.indexOf("["+elements[j]+"]")>-1 && sValue[j].compareTo("||") != 0)
                        rName = Utilities.replace(rName, "["+elements[j]+"]", sValue[j]);
                    else rName = Utilities.replace(rName, "["+elements[j]+"]","");
                }
                values[i] = Utilities.replace(rName, "null", "");
            }
            sValue = null;
            elements = null;
        }
        return values;
    }

    public String getchangedFormatedValue(String rName, String value,String dFormat){
        if(null != value && value.length()>0){
            if(null == dFormat){
                value = Utilities.replace(value, "||", "");
                value = value.replace('^', ' ');
                value = Utilities.replace(value, "null", "");
            } else {
                String[] elements = getElements(rName);
                String[] sValue = null;
                int eCount = elements.length;
                sValue = Utilities.split(value, "^");
                rName = dFormat;
                for(int j=0;j<eCount;j++){
                    if(rName.indexOf("["+elements[j]+"]")>-1 && sValue[j].compareTo("||") != 0)
                        rName = Utilities.replace(rName, "["+elements[j]+"]", sValue[j]);
                    else rName = Utilities.replace(rName, "["+elements[j]+"]","");
                }
                value = Utilities.replace(rName, "null", "");
                sValue = null;
                elements = null;
            }
        }
        return value;
    }

    /**
     *
     * @return
     */
    private boolean isOptional(){
        boolean isOptional = false;
        if(null != pRName && rValue.length() > 0 && oFiledIndex.length()>0){
            String[] values = Utilities.split(oFiledIndex, "^");
            if(adcount <= values.length){
                isOptional = true;
                tValues = getElements(pRName);
                values = Utilities.split(rValue, "^");
                int count = tValues.length;
                if(count == values.length){
                    for(int i=0;i<count;i++){
                        if(values[i].compareTo(tValues[i]) == 0){
                            if(oFiledIndex.indexOf(i+"")>-1)
                                rValue = Utilities.replace(rValue, tValues[i], "||");
                            else {
                                isOptional = false;
                                break;
                            }
                        }
                    }
                }
                tValues = null;
                values = null;
            }
        }
        return isOptional;
    }

    /**
     * Method to retrieve the record valued for the given record name
     * @param rName Record Name
     * @return Record Values
     */
    private void setRecordValues(String rName){
        rValues = null;
        try{
//Logger.debugOnError("rname in set record value==" + rName);
            byte[] rBytes = getRecordBytes(rName);
            if(null != rBytes){
                ByteArrayReader dStream = new ByteArrayReader(rBytes);
                int count = dStream.readInt();
                dStream.readUTF(); //Skip the Record Name
                if(count>0){
                    rValues = new String[count];
                    for(int i=0;i<count;i++){
                        rValues[i] = dStream.readUTF();
                     //   Logger.debugOnError("rvalues[ "+ i + " ]=in setrecord=" + rValues[i]);
                    }
                }
                dStream.close();
                dStream = null;
                rBytes = null;
            }
        }catch(Exception e){}
    }

    /**
     * Method to retrieve the default format record values
     * @param rName Record Name
     * @return record values
     */
    private String[] getDefaultFormatRecordValue(String rName){
      //  Logger.debugOnError("else)rname in getdef record value==" + rName);
        int count;
        String[] values = null;
        if(null != rValues && (count = rValues.length)>0){
            values = new String[count];
            for(int i=0;i<count;i++) {
                values[i] = Utilities.replace(rValues[i], "||", "");
                values[i] = values[i].replace('^', ' ');
                values[i] = Utilities.replace(values[i], "null", "");
           //     Logger.debugOnError("rvalues[ "+ i + " ] in getdef==" + rValues[i]);

            }
        }
        return values;
    }

    /**
     * Method to remove the already selected record value form the selected list
     * @param rName Record name
     */
    private void removeSelectedRecordValue(String rName){
        int count;
        if(null != selValues && (count=selValues.length)>0){
            tValues = null;
            for(int i=0;i<count;i++){
                tValues = (String[])selValues[i];
                if(tValues[0].compareTo(rName) == 0){
                    Object[] oArray = selValues;
                    count--;
                    selValues = new Object[count];
                    System.arraycopy(oArray, 0, selValues, 0, i);
                    System.arraycopy(oArray, i+1, selValues, i, (count-i));
                    oArray = null;
                    break;
                }
            }
            tValues = null;
        }
    }

    /**
     * Method to Set the Selected Record Value for the given Record name and selected value
     * @param rName Record Name
     * @param itemId Selected Record value index
     */
    public void setSelectedRecordValue(String rName,int itemId){
        if(null != rValues && itemId<rValues.length && null != rValues[itemId]) {
            setLastCreatedRecord(rName, rValues[itemId]);
        }
    }
    
    private void setLastCreatedRecord(String rName,String value){
       // Logger.debugOnError("2)setlastrecord rNAMe==" + rName +",value=="+ value);
        String[] tArray = new String[] {rName,value};
        if(null != selValues){
            int sIndex = isSelectedValue(rName);
            if(sIndex>-1){
                selValues[sIndex] = tArray;
            } else {
                Object[] oArray = selValues;
                int itemId = selValues.length;
                selValues = new Object[itemId+1];
                System.arraycopy(oArray, 0, selValues, 0, itemId);
                selValues[itemId] = tArray;
                oArray = null;
            }
        } else selValues = new Object[]{tArray};
    }
    
    private int isSelectedValue(String rName){
        if(null != selValues){
            int count = selValues.length;
            String[] values = null;
            for(int i=0;i<count;i++){
                values = (String[])selValues[i];
                if(values[0].compareTo(rName) == 0)
                    return i;
                values = null;
            }
        }
        return -1;
    }
    
    /**
     * 
     * @param rName
     * @return
     */
    public String getLastSelectedValue(String rName,String dRFormat){
        int count;
        String values = null;
        if(null != selValues && (count=selValues.length)>0){
            tValues = null;
            for(int i=0;i<count;i++){
                tValues = (String[])selValues[i];
                if(tValues[0].compareTo(rName) == 0){
                    values = tValues[1];
                    if(null != dRFormat){
                        if((count= rName.indexOf(":"))>-1)
                            rName = rName.substring(0,count);
                        String[] elements = getElements(rName);
                        String[] sValue = null;
                        int eCount = elements.length;
                        sValue = Utilities.split(values, "^");
                        values = dRFormat;
                        for(int j=0;j<eCount;j++){
                            if(values.indexOf("["+elements[j]+"]")>-1 && sValue[j].compareTo("||") != 0)
                                values = Utilities.replace(values, "["+elements[j]+"]", sValue[j]);
                            else values = Utilities.replace(values, "["+elements[j]+"]","");
                        }
                        values = Utilities.replace(values, "null", "");
                        sValue = null;
                        elements = null;
                    } else {
                        values = Utilities.replace(values, "||", "");
                        values = values.replace('^', ' ');
                        values = Utilities.replace(values, "null", "");
                    }
                    break;
                }
            }
        }
        return values;
    }

    /**
     * Method to retrieve the Selected record Value for the given Record Name *
     * @param rName Record Name
     * @return Selected Record Value
     */
    public String getSelectedValue(String rName,boolean isSend,boolean isSymRemove){
        String value = null;
        int count;
        if(null != selValues && (count = selValues.length)>0){
            tValues = null;
            for(int i=0;i<count;i++){
                tValues = (String[])selValues[i];
                if(tValues[0].compareTo(rName) == 0){
                    if(isSymRemove){
                        value = Utilities.replace(tValues[1], "||", "");
                        value = value.replace('^', ' ');
                    } else value = tValues[1];
                    if(isSend)
                        value = Utilities.replace(value, "null", "");
                    break;
                }
            }
            tValues = null;
        }
        return value;
    }

    /**
     * Method to retrieve the element value for the Record and element Name
     * @param rName Record Name
     * @param eName Element Name
     * @return retrieved element value
     */
    public String getSelectedElementValue(String rName,String eName,boolean isSend){
        String value = null;
        int count;
        if(null != selValues && (count=selValues.length)>0){
            tValues = null;
            String tValue = null;
            String orName = rName;
            if(orName.indexOf(":")>-1)
                orName = orName.substring(0,orName.indexOf(":"));
            for(int i=0;i<count;i++){
                tValues = (String[]) selValues[i];
                if(tValues[0].compareTo(rName) == 0){
                    tValue = tValues[1];
                    tValues = getElements(orName);
                    count = tValues.length;
                    for(int j=0;j<count;j++){
                        if(tValues[j].compareTo(eName) == 0){
                            tValues = Utilities.split(tValue, "^");
                            if(tValues[j].compareTo("||") != 0){
                                if(isSend && tValues[j].compareTo("null") == 0)
                                    value = "";
                                else value = tValues[j];
                            } else value = "";
                            break;
                        }
                    }
                    break;
                }
            }
            tValues = null;
        }
        return value;
    }

    /**
     * Method to add the record value for the given record element name
     * @param rName Record Name
     * @param eName Element Name
     * @param value Element Value
     */
    public void addRecord(String rName,String eName,String value,String orgName){
        if(null == sRName || sRName.compareTo(rName) != 0){
            addLastRecord();
            sRName = rName;
            // Logger.debugOnError("addrecord -->sRName==" + sRName);
            pRName = rName;
            //  Logger.debugOnError("addrecord -->pRName==" + pRName);
            orgRecName = orgName;
            rValue = "";
            tValues = getElements(rName);
         
            oFiledIndex = getOptionFieldIndex(rName);
            adcount = tValues.length;
          // Logger.debugOnError("length== " + adcount);
            if(rValue.length() == 0){
                rValue += tValues[0];
                for(int i=1;i<adcount;i++){
                    rValue += "^" + tValues[i];
                  //   Logger.debugOnError("rValue=="+ rValue);
                  //   Logger.debugOnError("tvalues=="+ tValues[i]);
                }
              //  Logger.debugOnError("rValue=="+ rValue);
            }
            tValues = null;
        }
        removeStoredOptionalElement(eName, value);
        adcount--;
        if(adcount == 0)
            updateNewRecordValue(sRName, rValue,orgRecName);
          //  Logger.debugOnError("addrecord if loop-->sRName==" + sRName +",rValue==" + rValue + ",orgRecName=" + orgRecName);
    }

    /**
     *
     * @return
     */
    private boolean addLastRecord(){
        boolean isSet = false;
        if(adcount > 0 && isOptional()){
            updateNewRecordValue(pRName, rValue,orgRecName);
            isSet = true;
        }

        adcount = 0;
        sRName = "";
        rValue = "";
      //  Logger.debugOnError("addlastrecord");
        return isSet;

    }

    /**
     *
     * @param eName
     * @param value
     */
    private void removeStoredOptionalElement(String eName,String value){
       int index = ("^"+rValue+"^").indexOf("^"+eName+"^");

        if(index >-1){
           int mPos = rValue.indexOf("^");
           byte count = 0;
           while(mPos>-1 && mPos<index){
               mPos = rValue.indexOf("^",mPos+1);
               count++;
           }
           rValue = rValue.substring(0,index) + value + rValue.substring(index+eName.length());
           if((index = oFiledIndex.indexOf(count+""))>-1){
               if((index+1)<oFiledIndex.length())
                    oFiledIndex = oFiledIndex.substring(0,index) + oFiledIndex.substring(index+2);
               else oFiledIndex = oFiledIndex.substring(0,index);
           }
        }
    }

    /**
     *
     * @param rName
     * @return
     */
    private String getOptionFieldIndex(String rName){
        String rField = "";
        if(null != rOptions){
            int index = rOptions.indexOf(rName+"|^");
            if(index>-1){
                 rField = rOptions.substring(index+rName.length()+2);
                 if(rField.length()>0){
                     if((index = rField.indexOf("^|"))>-1){
                         if(rField.charAt(0) != '|')
                            rField = rField.substring(0,index);
                         else rField = "";
                     } else rField = rField.substring(0,rField.length()-1);
                 }
            }
        }
        return rField;
    }


    /**
     * Method to retrieve the record element for the given record Name
     * @param rName Record Name
     * @return Record Element Names
     */
    private String[] getElements(String rName){
        String[] elements = null;
        if(null != records){
            int index = records.indexOf(rName+"|^");
            if(index>-1){

                if(records.charAt(index-1) == '1')
                    isCommon = true;
                else isCommon = false;

                rName = records.substring(index+rName.length()+2);
                index = rName.indexOf("^|");
                if(index>-1)
                    rName = rName.substring(0,index);
                else
                    rName = rName.substring(0,rName.length()-1);
                elements = Utilities.split(rName, "^");
            }
        }
        return elements;
    }

    /**
     *
     * @param rName
     * @return
     */
    private String getElement(String rName){
        if(null != records){
            int index = records.indexOf(rName+"|^");
            if(index>-1){

                if(records.charAt(index-1) == '1')
                    isCommon = true;
                else isCommon = false;

                rName = records.substring(index+rName.length()+2);
                index = rName.indexOf("^|");
                if(index>-1)
                    rName = rName.substring(0,index);
                else
                    rName = rName.substring(0,rName.length()-1);
            }
        } else rName = "";
        return rName;
    }


    //Cr 12880
    public String getRecordId(String rName, int itemId){
        if(null != rValues && itemId<rValues.length){
            return rValues[itemId];
        }
        return null;
    }

    /**
     * Method to delete the record for the given record Name and itemid
     * @param rName Record Name
     * @param itemId String Array index
     */
    public void deleteRecord(String rName,int itemId){
        if(null != rValues && itemId<rValues.length){
            sRName = rName;
            if(null != rValues[itemId]){
                rValues[itemId] = null;
                deleteRecordValue();
                rValue = "";
                sRName = "";
                adcount = 0;
            }
        }
    }

    /**
     *
     * @param rName
     * @param value
     */
    public void deleteSingleRecord(String orName,String rName){
        setRecordValues(rName);
        String value = getSelectedValue(orName,false,false);
        int count =0;
        if(null != rValues && null != value){
            //bug 13351
          //  removeSelectedRecordValue(rName);
            count = rValues.length;
            int mCount = 0;
            byte[] rBytes = null;
            ByteArrayWriter dOutStream = new ByteArrayWriter();
            try {
                if(count>1){
                    for(int i=0;i<count;i++){
                        if(rValues[i].compareTo(value) != 0){
                            dOutStream.writeUTF(rValues[i]);
                            mCount++;
                        }
                    }
                    if(rPos>0){
                        rBytes = dOutStream.toByteArray();
                        dOutStream = new ByteArrayWriter();
                        dOutStream.writeInt(mCount);
                        dOutStream.writeUTF(rName);
                        dOutStream.write(rBytes);
                    }
                } else{
                    dOutStream.writeInt(mCount);
                    dOutStream.writeUTF(rName);
                }
                rValues = null;
                rBytes = dOutStream.toByteArray();
                dOutStream.close();
                dOutStream = null;
                rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
            }catch(Exception exc){}
            rBytes = null;
        }
    }


    //CR 13118
    public String updateSingleRecordElement(String chatId, String updateValue,
            String rName){
        int count = 0;
        String oldValue = null;
        String[] elements = getElements(rName);
        if(null != elements){
            String findValue = null;
            if(elements[0].compareTo("friendID") == 0){
                findValue = chatId+"^";
            } else if(elements[1].compareTo("friendID") == 0 ||
                    elements[2].compareTo("friendID") == 0){
                findValue = "^"+chatId+"^";
            } else {
                findValue = "^"+chatId;
            }

            if(null != rValues && (count=rValues.length)>0){
                byte[] rBytes = null;
                ByteArrayWriter dOutStream = new ByteArrayWriter();
                dOutStream.writeInt(count);
                for(int i=0;i<count;i++){
                    if(rValues[i].indexOf(findValue)>-1){
                        oldValue = rValues[i];
                        rValues[i] = updateValue;
                    }
                    dOutStream.writeUTF(rValues[i]);
                }
                rBytes = dOutStream.toByteArray();
                dOutStream.close();
                dOutStream = null;
                rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
            }
        }
        return oldValue;
    }


    /**
     *
     * @param rName
     */
    public void deleteAllRecord(String rName){
        byte[] rBytes = getRecordBytes(rName);
        if(null != rBytes){
            ByteArrayWriter dout = new ByteArrayWriter();
            try{
                dout.writeInt(0);
                dout.writeUTF(rName);
                rBytes = dout.toByteArray();
                dout.close();
                dout = null;
                rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
            }catch(Exception e){}
            rBytes = null;
        }
    }

    /**
     * Method to remove the last store value for the given rName and the element Value
     * @param rName Record Name
     * @param eName element Name
     */
    public void removeLastStoreValue(String rName,String eName){
        if(adcount>0 && sRName != null && sRName.compareTo(rName) == 0 && eName.length()>0){
            tValues = getElements(rName);
            int count = tValues.length;
            rName = rValue;
            rValue ="";
            boolean isSet = false;
            for(int i=0;i<count;i++){
                if(tValues[i].compareTo(eName) == 0){
                    tValues = Utilities.split(rName, "^");
                    tValues[i] = eName;
                    rValue = tValues[0];
                    isSet = true;
                    break;
                }
            }
            if(isSet){
                for(int i=1;i<count;i++)
                    rValue += "^" + tValues[i];
                adcount++;
            } else rValue = rName;
            tValues = null;
        }
    }

    /**
     * Method to Store the record value for the given recordName and specified element Name
     * @param rName Record Name
     * @param eName Element Name
     * @param value element Value
     */
    public void propagateRecord(String rName,String eName,String value,String orgName){
        addRecord(rName,eName,value,orgName);
       // Logger.debugOnError("1 "+ rName + "2 "+ eName+ "3 "+ value+ " 4 "+ orgName);
    }

    /**     *
     * @param nValue
     */
    public boolean updateNewRecordValue(String rName ,String nValue,String orgName){
        //  Logger.debugOnError("1)In updatenew record-->RName==" + rName +",nValue==" + nValue + ",orgName=" + orgName);
        if(null != rStoreParser){
            //bug id 4969
            setLastCreatedRecord(orgName, nValue);
            byte[] rBytes = getRecordBytes(rName);
           
            boolean isStore = true;
            int count = 0;
            if(null != rBytes){
                ByteArrayReader din = new ByteArrayReader(rBytes);
                try{
                    count = din.readInt();
                    din.readUTF(); //skip the Record Name
                    for(int i=0;i<count;i++){
                        if(din.readUTF().compareTo(nValue) == 0)
                        {
                            isStore = false;
                            break;
                        }
                    }
                }catch(Exception e){}
                din.close();
                din = null;
            }

            if(isStore){
                ByteArrayWriter dOutStream = new ByteArrayWriter();
                try{
                    if(null != rBytes){
                        byte[] newBytes = new byte[rBytes.length-4];
                        System.arraycopy(rBytes, 4, newBytes,0, newBytes.length);
                        rBytes = null;
                        dOutStream.writeInt(count+1);
                        dOutStream.write(newBytes);
                        newBytes = null;
                        dOutStream.writeUTF(nValue);
                        rBytes = dOutStream.toByteArray();
                        rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
                    } else{
                        dOutStream.writeInt(1);
                        dOutStream.writeUTF(rName);
                        dOutStream.writeUTF(nValue);
                        rBytes = dOutStream.toByteArray();
                        rStoreParser.addRecord(rBytes, 0, rBytes.length, true);
                        rPos = rStoreParser.getNumRecords();
                        rCount =  rPos;
                    }
                }catch(Exception e){}
                dOutStream.close();
                dOutStream = null;
            }
         }
        if(isCommonRecord(pRName))
            storeCommonEntry(pRName, nValue, oFiledIndex);
        rValue = "";
        sRName = "";
        pRName = "";
        adcount = 0;
        return true;
    }
    
    /**
     * 
     * @param rName
     * @return
     */
    private boolean isCommonRecord(String rName){
        if(null != records){
            int index = records.indexOf(rName+"|");
            if(index>-1 && records.charAt(index-1) == '1')
                return true;
        }
        return false;
    }

    /**
     *
     * @param id
     */
    private void deleteRecordValue(){
        if(null != rStoreParser){
            try{
                int count = rValues.length;
                ByteArrayWriter dOutStream = new ByteArrayWriter();
                int mCount = 0;
                for(int i=0;i<count;i++){
                    if(null != rValues[i]){
                        dOutStream.writeUTF(rValues[i]);
                        mCount++;
                    }
                }
                if(rPos>0){
                    byte[] rBytes = dOutStream.toByteArray();
                    dOutStream = new ByteArrayWriter();
                    dOutStream.writeInt(mCount);
                    dOutStream.writeUTF(sRName);
                    dOutStream.write(rBytes);
                    rBytes = dOutStream.toByteArray();
                    rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
                    rBytes = null;
                }
                dOutStream.close();
                dOutStream = null;
            }catch(Exception exc){}
        }
    }

    /**
     *
     * @param rName
     * @param rValue
     * @param oElement
     */
    private void storeCommonEntry(String rName,String rValue,String oElement){
        if(null == cHashTable)
            cHashTable = new Hashtable();
        tValues = null;
        if(cHashTable.containsKey(rName)){
            tValues = (String[])cHashTable.get(rName);
            if(tValues[0].indexOf(rValue)>-1)
                return;
            tValues[0] +="|^|"+rValue;
            tValues[1] += "||"+oElement;
            cHashTable.remove(rName);
        } else {
            tValues = new String[] { rValue,oElement,getElement(rName)};
        }
        cHashTable.put(rName, tValues);
        tValues = null;
    }

    /**
     *
     * @return
     */
    public boolean isExternalRecord(){
        addLastRecord();
        if(null != cHashTable){
            closeRecords();
            return true;
        }
        return false;
    }

    /**
     *
     * @param pName
     * @param records
     * @param rOption
     */
    public void propagateExternalValue(String pName,String record,String rOption){
       if(null != records){
            Enumeration enuma = cHashTable.keys();
            String[] cValues = null;
            String[] values = null;
            String[] oIndex = null;
            byte[] rbytes = null;
            byte len = 0;
            byte rvlen = 0;
            byte count = 0;
            setRecords(record,rOption, pName);
            while(enuma.hasMoreElements()){
                rbytes = null;
                sRName = (String)enuma.nextElement();
                oFiledIndex = getElement(sRName);
                cValues = (String[])cHashTable.get(sRName);
                if(null != oFiledIndex && oFiledIndex.compareTo(cValues[2]) == 0){
                    oFiledIndex = getOptionFieldIndex(sRName);
                    setRecordValues(sRName);
                    values = Utilities.split(cValues[0], "|^|");
                    adcount = (byte)values.length;
                    oIndex = Utilities.split(cValues[1], "||");
                    try {
                        if(null != rValues)
                            rvlen = (byte)rValues.length;
                        ByteArrayWriter dout = new ByteArrayWriter();
                        if(rPos>0){
                            rbytes = rStoreParser.getRecord(rPos);
                            dout.write(rbytes);
                        } else {
                            dout.writeInt(0);
                            dout.writeUTF(sRName);
                        }
                        count = 0;
                        for(int i=0;i<adcount;i++){
                            isCommon = true;
                            if(values[i].indexOf("||")>-1){
                                cValues = Utilities.split(oIndex[i], "^");
                                len = (byte)cValues.length;
                                for(int j=0;j<len;j++){
                                    if(oFiledIndex.indexOf(oIndex[i]) == -1){
                                        values[i] = null;
                                        break;
                                    }
                                }
                                cValues = null;
                            }

                            if(null != values[i]){
                                for(int j=0;j<rvlen;j++){
                                    if(rValues[j].compareTo(values[i]) == 0){
                                        isCommon = false;
                                        break;
                                    }
                                }
                                if(isCommon){
                                    count++;
                                    dout.writeUTF(values[i]);
                                }
                            }
                        }
                        if(count>0){
                            if(null != rbytes){
                                rbytes = dout.toByteArray();
                                rbytes[0] += count;
                                rStoreParser.setRecord(rPos, rbytes, 0, rbytes.length, true);
                            } else {
                                rbytes = dout.toByteArray();
                                rbytes[0] += count;
                                rStoreParser.addRecord(rbytes, 0, rbytes.length, true);
                            }
                        }
                        dout.close();
                        dout = null;
                    }catch(Exception e){}
                }
            }
            closeRecords();
            enuma = null;
            sRName = null;
            cValues = null;
            values = null;
            oIndex = null;
            rbytes = null;
       }
    }

    /**
     *
     */
    private void closeRecords(){
        try{
            if(null != rStoreParser){
                rStoreParser.closeRecordStore();
                rStoreParser = null;
            }
        }catch(Exception e){}
    }

    /**
     * Method to deinitialize all resources
     */
    public void deinitialize(){
        try{
            closeRecords();
            if(null != cHashTable){
                cHashTable.clear();
                cHashTable = null;
            }
            rValue = null;
            rValues = null;
            selValues = null;
            records = null;
            adcount = -1;
            sRName = null;
        }catch(Exception e){}
    }
}
