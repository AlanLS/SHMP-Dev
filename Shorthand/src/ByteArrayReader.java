
import java.io.ByteArrayInputStream;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author sasi
 */
public class ByteArrayReader {
    
    IFileReadHandler dat = null;
    ByteArrayInputStream dArrayStream = null;
    int avaiable = 0;
    
    public ByteArrayReader(byte[] rByte){
        dArrayStream = new ByteArrayInputStream(rByte);
        dat = new FileRead();
        dat.setByteArrayStream(dArrayStream);
        avaiable = rByte.length;
    }

    public ByteArrayReader() {
    }
    
    public boolean isNotEnd(){
        if(avaiable == 0)
            return false;
        return true;
    }
    
    public boolean setReader(String fLoc){
        boolean isNotSet = true;
        try {
            byte[] rbyte = null;
            if(fLoc.indexOf("-j")>-1){
                rbyte = DownloadHandler.getInstance().getResourcesBytes(fLoc.substring(0,fLoc.length()-2),true);
            } else {
                rbyte = RecordStoreParser.getRecordStore(fLoc.substring(0,fLoc.length()-2));
            }
            if(null != rbyte){
                dArrayStream = new ByteArrayInputStream(rbyte);
                dat = new FileRead();
                dat.setByteArrayStream(dArrayStream);
                avaiable = dat.available();
                isNotSet = false;
            }
        } catch (Exception ex) { dArrayStream = null; dat = null; }
        return isNotSet;
    }
    
//    public ByteArrayReader(String fLoc){
//        try {
//            byte[] rbyte = RecordStoreParser.getRecordStore(fLoc);
//            if(null != rbyte){
//                dArrayStream = new ByteArrayInputStream(rbyte);
//                if(isNotSamsung)
//                    dat = new FileRead();
//                else dat = new SamsungFileRead();
//                dat.setByteArrayStream(dArrayStream);
//                avaiable = dat.available();
//            }
//        } catch (Exception ex) { }
//    }
    
    public byte[] read(byte[] rByte){
        try{
            avaiable -= rByte.length;
            dat.read(rByte);
        }catch(Exception e){}
        return rByte;
    }
    
    public void skip(int value){
        try{
            avaiable -= value;
            dat.skip(value);
        }catch(Exception e){}
    }
    
    public void skipBytes(int value){
        try{
            avaiable -= value;
            dat.skipBytes(value);
        }catch(Exception e){}
    }
    
    public int readInt(){
        int value = 0;
        try{
            avaiable -= 4;
            value = dat.readInt();
        }catch(Exception e){}
        return value;
    }
    
    public short readShort(){
        short value = 0;
        try{
            avaiable -= 2;
            value = dat.readShort();
        }catch(Exception e){}
        return value;
    }
    
    public boolean readBoolean(){
        boolean value = false;
        try{
            avaiable -= 1;
            value = dat.readBoolean();
        }catch(Exception e){}
        return value;
    }

    public byte readByte(){
        byte value =0;
        try{
            avaiable -= 1;
            value = dat.readByte();
        }catch(Exception e){}
        return value;
    }
    
    public long readLong(){
        long value = 0;
        try{
            avaiable -= 8;
            value = dat.readLong();
        }catch(Exception e){}
        return value;
    }
    
    public byte[] read(int len){
        byte[] value = null;
        try{
            avaiable -= len;
            value = new byte[len];
            dat.read(value);
        }catch(Exception e){ value = null;}
        return value;
    }
    
    public String readUTF(){
        String value = "";
        try{
            value = dat.readUTF();
            avaiable -= (2*value.length());
        }catch(Exception e){}
        return value;
    }
    
    
    public double readDouble(){
        double value = 0;
        try{
            avaiable -= 4;
            value = dat.readDouble();
        }catch(Exception e){}
        return value;
    }
    
    public char readChar(){
        char value=' ';
        try{
            avaiable -= 1;
            value = dat.readChar();
        }catch(Exception e){}
        return value;
    }

    public float readFloat(){
        float value = 0;
        try{
            avaiable -= 4;
            value = dat.readFloat();
        }catch(Exception e){}
        return value;
    }
    
    public void close(){
        try{
            avaiable = 0;
            dArrayStream.close();
            dat.close();
            dArrayStream = null;
            dat = null;
        }catch(Exception e){}
    }
}
