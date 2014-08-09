/**
 * SMSAdvertisementParser class
 *
 * @author Hakunamatata
 * @version 1.00.15
 * @copyright (c) SmartTouch Mobile Inc
 */
public class AdvertisementParser {
    
    // Variable to hold the Advertisement File Location
    private String fLoc = null;    
   
    private ByteArrayWriter bArrayWriter = null;
    
    private ByteArrayReader bArrayReader = null;
    
    public AdvertisementParser(){
    }

    /**
     * Method to Open the Advertisement file and store the values in DataInputStream.
     *
     * @return - True will return if the Informations read succssfully.Otherwise false.
     *
     * @throws initialStreamException.
     */
    private boolean  initializeStream(){
           fLoc = RecordManager.getAdvertisementName();
        if(null != fLoc){
            byte[] rbyte = RecordStoreParser.getRecordStore(fLoc);
            bArrayReader = new ByteArrayReader(rbyte);
            return true;
        }
        return false;
    }
    
    /**
     * Method to retrieve the Advertisement based on the requested category.
     *
     * @param pCat - Variable will contain the Profile category.(Not NUll)
     *
     * @return adData - Object will contain the Advertisement informations.It may return Null.
     *
     * @throws getAdException.
     */
    public AdData getAdvertisement(String pCat){
        AdData adDate = new AdData();
        if(initializeStream()){
            bArrayWriter = new ByteArrayWriter();
            String id = getLastSelectedId(pCat);
            boolean isFind = false;
            short skipbyte;
            String adCat =null;
            byte[] adByte =null;
            byte[] wByte =null;
            String tempId = null;
            String wId =null;
            while(bArrayReader.isNotEnd()){
                adCat = bArrayReader.readUTF();
                tempId = bArrayReader.readUTF();
                skipbyte = bArrayReader.readShort();
                wByte = bArrayReader.read(skipbyte);
                    if(!isFind && isPlaceCat(pCat,adCat)){
                        if(null == id){
                            adByte = wByte;
                            wId = tempId;
                            isFind = true;
                        }else if(null != id && 0 == id.compareTo(tempId)){
                            wByte = isRemoveAd(wByte);
                            id = null;
                        } else if(null == adByte){
                            adByte = wByte;
                            wId = tempId;
                        }
                    }
                if(null != wByte){
                    bArrayWriter.writeUTF(adCat);
                    bArrayWriter.writeUTF(tempId);
                    bArrayWriter.writeShort(skipbyte);
                    bArrayWriter.write(wByte);
                }
            }
            deinitialize();
            byte[] rbyte = bArrayWriter.toByteArray();
            bArrayWriter.close();
            bArrayWriter = null;
            wByte =null;
            RecordStoreParser.UpdateRecordStore(RecordManager.getAdvertisementName(), rbyte, true);
            if(adByte != null){
                assignSelectedId(wId,pCat);
                adDate = getAdData(wId,adByte);
            } else if(!isFind){
                assignSelectedId(null,pCat);
            }
        }
        return adDate;
    }
    
    /**
     * Method to close the DataInputStream Buffer.
     *
     * @throws readBufferException
     */
    private void deinitialize(){
        if(null != bArrayReader){
            bArrayReader.close();
            bArrayReader =null;
        }
    }
    
    /**
     * Method to Keep the Count of the shown Advertisements. (Future Method)
     *      
     * @param adCount - Variable will contain the Advertisement count.
     *
     * @return adCount - Variable will contain the Advertisement count.
     */
    private byte[] increaseAdCount(byte[] adCount){
        byte[] tempRCount = new byte[4];
        System.arraycopy(adCount,4,tempRCount,0,4);
        int count = getByteArrayToInt(tempRCount) + 1;
        tempRCount = getIntToByteArray(count);
        System.arraycopy(tempRCount,0,adCount,4,4);
        return adCount;
    }

    /**
     * Method to check given profile category match with any Advertisement category.
     *
     * @param pCat - Variable will contain the Profile Category.
     * @param adcat - Variable will contain the Advertisement categorys.
     *
     * @return - return true if the category match.Otherwise return false.
     **/
    private boolean isPlaceCat(String pCat,String adCat){
        String[] text = Utilities.split(adCat,",");
        int len =0;
        if(null != text && (len=text.length)>0){
            for(int i=0;i<len;i++){
                if(0 == pCat.compareTo(text[i]) || 0 == Constants.appendText[3].compareTo(text[i]))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Method to get the Last selected Advertisement
     *
     * @param pCat - Variable will contain the Profile category.(Not NUll)
     *
     * @return id - Variable will have the Advertisement Id.
     *
     * @throws lastAdException 
     **/
    private String getLastSelectedId(String pCat){
            String id =null;
            String selectedAdName = RecordManager.getAppNames(RecordManager.getLastSelectedAdName(),false);
            if(null != selectedAdName){
                selectedAdName = "^-"+selectedAdName;
                int index = selectedAdName.indexOf("^-"+pCat);
                if(index>-1){
                    index += 2+ pCat.length()+1;
                    selectedAdName = selectedAdName.substring(index);
                    index = selectedAdName.indexOf("^-");
                    if(index>-1)
                        id = selectedAdName.substring(0,index);
                    else id = selectedAdName;
                }
            }
            return id;
    }
    
    /**
     *
     **/
    private void assignSelectedId(String id,String pCat){

        String selectedAdName = RecordManager.getAppNames(RecordManager.getLastSelectedAdName(),true);
        String name = null;
        if(null != selectedAdName){
            name = selectedAdName;
            selectedAdName = "^-"+selectedAdName;
            int index = selectedAdName.indexOf("^-"+pCat);
            if(index>-1){
                index += 2+ pCat.length()+1;
                name = selectedAdName.substring(0,index);
                selectedAdName = selectedAdName.substring(index);
                name += id;
                index = selectedAdName.indexOf("^-");
                if(index>-1)
                    name = selectedAdName.substring(index);
            } 
        } else name = pCat +"-"+id;
        RecordStoreParser.UpdateRecordStore(RecordManager.getLastSelectedAdName(), name.getBytes(), true);
    }
    
    /**
     *
     **/
    private byte[] isRemoveAd(byte[] rbyte){
        if(null != rbyte && rbyte.length>7){
            byte[] iByte = new byte[4];
            System.arraycopy(rbyte,0,iByte,0,4);
            int maxCount = getByteArrayToInt(iByte);
            System.arraycopy(rbyte,4,iByte,0,4);
            int count = getByteArrayToInt(iByte) + 1;
            if(maxCount == count)
                return null;
            else {
                iByte = getIntToByteArray(count);
                System.arraycopy(iByte,0,rbyte,4,4);
            }
        }
        return rbyte;
    }
    
    /**
     * Method to retrieve the Advertisement Text
     *
     * @param id - Variable will contain the Advertisement Id.
     * @param rByte
     **/
    private AdData getAdData(String id, byte[] rByte){
        AdData adData = new AdData();
        ByteArrayReader bArray = new ByteArrayReader(rByte);
        bArray.readInt();
        bArray.readInt();
        adData.setAdText(bArray.readUTF());
        if(bArray.readBoolean())
            adData.setStyle((byte)3);
        //Header Byte
        byte[] header = bArray.read(adData.getHEADER_SIZE());
        boolean[] bit = getBitArray(header);

        //Landing Page
        if(bit[0]){
            header = bArray.read(adData.getLANDPAGE_SIZE());
            adData.setLPag(getBitArray(header));
        }

        //Phone Number
        if(bit[1])
            adData.setPNo(bArray.readUTF());

        //Url
        if(bit[2])
            adData.setUrl(bArray.readUTF());

        adData.setAdId(id);
        bArray.close();
        bArray =null;
        return adData;
    }

    /**
     * Method 
     **/
    private boolean[] getBitArray(byte[] eByte)
    {
        int len = eByte.length;
        boolean[] bitArray = new boolean[len*8];
        int k=0;
        for(int i=0;i<len;i++)
        {
            for(int j=0;j<8;j++)
                bitArray[k++] = ((eByte[i] & (1 << j)) != 0);
        }
        return bitArray;
     }
    
    /**
     *
     **/
    private int getByteArrayToInt(byte[] rByte){
        int count= rByte[0];
          for(int i = 1; i < 4; i++) {
            count = count << 8;
            count += (rByte[i]  & 0x000000FF);
          }
        return count;
    }
    
    /**
     * Method to 
     **/
    private byte[] getIntToByteArray(int value){
        byte[] rByte =new byte[4];
        for(int i=0,j=3;i<4;i++,j--){
             rByte[j] = (byte)(value >> (i*8));
          }
        return rByte;
    }
}
