
/**
 * EntryParser Class to read and write the Entry Shortcuts values
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough
 **/
public class GlobalEATParser {

    // Variable related to EntryShortcut
    private String _name = null;

    //Variable to hold the recodset position
    private int rPos = 0;

    //Varibale to hold the recordset count
    private int rCount = 1;

    //Variable to hold the RecordStore instance
    private RecordStoreParser rStoreParser = null;

    public GlobalEATParser() {
        openRecordStore(RecordManager.globalEATName);
    }

    private void openRecordStore(String pName) {
        rPos = 0;
        rCount = 1;
        pName = RecordManager.getEntryRecordStoreName(pName);
        try {
            rStoreParser = new RecordStoreParser();
            rStoreParser.openRecordStore(RecordManager.getRecordStoreName(pName), true, false, false);
            rCount = rStoreParser.getNumRecords();
            if (rCount == 0) {
                rStoreParser.addRecord(new byte[0], 0, 0, true);
                rCount++;
//                flushRMS();
            }
        } catch (Exception e) {
            rStoreParser = null;
        }
    }

    public boolean getIsPasswordSaved(String scprifix) {
        boolean isStore = false;
        try {
            if (rStoreParser != null && rStoreParser.getNumRecords() > 0) {
                if (null != rStoreParser.getRecord(1)) {
                    String passStatus = new String(rStoreParser.getRecord(1));
                    if (passStatus.indexOf(scprifix + "|") > -1) {
                        isStore = true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return isStore;
    }

    public boolean getPasswordsaveStatus(String scprifix) {
        boolean isSave = false;
        try {
            if (rStoreParser != null && null != rStoreParser.getRecord(1)) {
                String passStatus = new String(rStoreParser.getRecord(1));
                if (passStatus.indexOf(scprifix + "|1") > -1) {
                    isSave = true;
                }
            }
        } catch (Exception e) {
        }
        return isSave;
    }

    public void setPasswordSave(String scprifix, boolean status) {
        try {
            if (rStoreParser != null) {
                byte[] rByte = rStoreParser.getRecord(1);
                byte iscon = 0;
                if (status) {
                    iscon = 1;
                }
                String value = null;
                if (null != rByte) {
                    value = new String(rByte);
                    value += "," + scprifix + "|" + iscon;
                } else {
                    value = scprifix + "|" + iscon;
                }
                rByte = value.getBytes();
                rStoreParser.setRecord(1, rByte, 0, rByte.length, true);
//                flushRMS();
            }
        } catch (Exception e) {
        }
    }

//    private void flushRMS(){
//        if(ChannelData.isRMSAlwaysClose()){
//            closeRecordStore();
//            openRecordStore(RecordManager.globalEATName);
//        }
//    }

    private byte[] getEntryRecordBytes(String sName) {
        byte[] rBytes = null;
        rPos = 1;
        if (null != rStoreParser) {
            String rValue = null;
            try {
                for (int i = 2; i <= rCount; i++) {
                    rBytes = rStoreParser.getRecord(i);
                    rValue = new String(rBytes);
                    if (rValue.length() > 0) {
                        if (rValue.startsWith(sName + "^") || rValue.compareTo(sName) == 0) {
                            rPos = i;
                            break;
                        }
                    }
                    rBytes = null;
                }
            } catch (Exception e) {
                rBytes = null;
                rPos = 1;
            }
            rValue = null;
        }
        return rBytes;
    }

    /**
     *  Method to get All the
     * @param scname
     * @return
     */
    public String[] getEntryValue(String scname) {
        _name = scname;
        String[] value = null;
        byte[] rBytes = getEntryRecordBytes(scname);
        if (null != rBytes) {
            String values = new String(rBytes);
            if (values.compareTo(scname) != 0) {
                values = values.substring(scname.length() + 1);
                value = Utilities.split(values, "^");
            }
            values = null;
            rBytes = null;
        }
        return value;
    }

    /**
     * This method return value (boolean) is used to identify whether we need to
     * delete the entry in sequence shortcut file
     */
    public void DeleteEntryShortcut(String scvalue) {
        if (rPos > 0) {
            try {
                byte[] rbyte = rStoreParser.getRecord(rPos);
                String value = new String(rbyte);
                if (value.length() > 0) {
                    int index = ("^" + value + "^").indexOf("^" + scvalue + "^");
                    if (index > -1) {
                        if ((index + 1 + scvalue.length()) < value.length()) {
                            value = value.substring(0, index) + value.substring(index + 1 + scvalue.length());
                        } else {
                            value = value.substring(0, index - 1);
                        }
                        rbyte = value.getBytes();

                        rStoreParser.setRecord(rPos, rbyte, 0, rbyte.length, true);
//                        flushRMS();
                    }
                }
            } catch (Exception rsex) {
            }
        }
    }

    public boolean isEdited(String oValue, String nValue) {
        boolean isEdited = false;
        try {
            byte[] ebyte = rStoreParser.getRecord(rPos);
            String value = new String(ebyte);
            if (value.length() > 0) {
                int index;
                if ((index = ("^" + value + "^").indexOf("^" + oValue + "^")) > -1) {
                    if ((index + 1 + nValue.length()) < value.length()) {
                        value = value.substring(0, index) + nValue + value.substring(index + 1 + oValue.length());
                    } else {
                        value = value.substring(0, index) + nValue;
                    }
                    ebyte = value.getBytes();
                    rStoreParser.setRecord(rPos, ebyte, 0, ebyte.length, true);
//                    flushRMS();
                    isEdited = true;
                }
            }
        } catch (Exception e) {
        }
        return isEdited;
    }

    public boolean isValueExitsts(String nValue) {
        boolean isExits = false;
        try {
            if (rPos > -1) {
                byte[] ebyte = rStoreParser.getRecord(rPos);
                String value = new String(ebyte);
                if (value.length() > 0) {
                    value = "^" + value + "^";
                    if (value.indexOf("^" + nValue + "^") > -1) {
                        isExits = true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return isExits;
    }

    /**
     *
     *
     * @param eName
     * @param eValue
     */
    public void deleteSingleValue(String eName, String eValue) {
        byte[] ebyte = getEntryRecordBytes(eName);
        if (null != ebyte) {
            String value = new String(ebyte);
            int index;
            try {
                if ((index = ("^" + value + "^").indexOf("^" + eValue + "^")) > -1) {
                    if ((index + 1 + eValue.length()) < value.length()) {
                        value = value.substring(0, index) + value.substring(index + 1 + eValue.length());
                    } else {
                        value = value.substring(0, index - 1);
                    }
                    ebyte = value.getBytes();
                    rStoreParser.setRecord(rPos, ebyte, 0, ebyte.length, true);
//                    flushRMS();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @param eName
     */
    public void deleteAllValues(String eName) {
        _name = eName; // bug id 5263
        byte[] ebyte = getEntryRecordBytes(eName);
        if (null != ebyte) {
            try {
                ebyte = eName.getBytes();
                rStoreParser.setRecord(rPos, ebyte, 0, ebyte.length, true);
//                flushRMS();
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @param scvalue
     */
    public void storeEntrySC(String scvalue) {
        if (scvalue != null && scvalue.toLowerCase().compareTo("null") != 0) {// ithaya
            try {
                String sValue = null;
                byte[] rBytes = null;
                if (rPos > 1) {
                    rBytes = rStoreParser.getRecord(rPos);
                    sValue = new String(rBytes);
                    if ((sValue + "^").indexOf("^" + scvalue + "^") == -1) {
                        sValue += "^" + scvalue;
                        rBytes = sValue.getBytes();
                        rStoreParser.setRecord(rPos, rBytes, 0, rBytes.length, true);
                    }
                } else {
                    rCount++;
                    sValue = _name + "^" + scvalue;
                    rBytes = sValue.getBytes();
                    rStoreParser.addRecord(rBytes, 0, rBytes.length, true);
                    rPos = rCount;
                }
//                flushRMS();
                sValue = null;
                rBytes = null;
            } catch (Exception e) {
            }
        }
    }

    private void closeRecordStore() {
        try {
            if (null != rStoreParser) {
                rStoreParser.closeRecordStore();
                rStoreParser = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    public void Deinitialize() {
        closeRecordStore();
        rPos = 0;
        rCount = 1;
        _name = null;
    }

    /**
     *
     **/
    public void WritePropagateValue(String scname, String value) {
        if (null == _name || _name.compareTo(scname) != 0) {
            _name = scname;
            getEntryRecordBytes(scname);
        }
        storeEntrySC(value);
    }
}
