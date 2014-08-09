
/**
 *
 * @author Sasikumar
 */
public class ResponseMessageCount {

    private ByteArrayReader din = null;

    private ByteArrayWriter dout = null;

    public void increaseMessageCount(String pName,boolean isMatch){
        pName = RecordManager.getMessageCountLoc(pName);
        byte[] rbyte = RecordStoreParser.getRecordStore(pName);
        dout = new ByteArrayWriter();
        if(null != rbyte){
            din =  new ByteArrayReader(rbyte);
            if(isMatch){
                dout.writeShort(din.readShort()+1);
                dout.writeShort(din.readShort());
            } else {
                dout.writeShort(din.readShort());
                dout.writeShort(din.readShort()+1);
            }
            din.close();
            din = null;
        } else {
            if(isMatch){
                dout.writeShort(1);
                dout.writeShort(0);
            } else {
                dout.writeShort(0);
                dout.writeShort(1);
            }
        }
        rbyte = dout.toByteArray();
        dout.close();
        dout = null;
        RecordStoreParser.UpdateRecordStore(pName, rbyte, true);
    }

    /**
     *
     */
    public void deinitialize(){
        if(null != din){
            din.close();
            din = null;
        }

        if(null != dout){
            dout.close();
            dout = null;
        }
    }
}
