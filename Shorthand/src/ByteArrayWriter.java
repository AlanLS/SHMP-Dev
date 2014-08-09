
import java.io.ByteArrayOutputStream;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.DataOutputStream;

/**
 *
 * @author sasi
 */
public class ByteArrayWriter {
    
    DataOutputStream dout = null;
    
    ByteArrayOutputStream dArrayStream = null;
    
    public ByteArrayWriter(){
        dArrayStream = new ByteArrayOutputStream();
        dout = new DataOutputStream(dArrayStream);
    }
    
    public void writeInt(int value){
        try{
            dout.writeInt(value);
        }catch(Exception e){}
    }
    
    public void writeShort(int value){
        try{
            dout.writeShort(value);
        }catch(Exception e){}
    }
    
    public void writeShort(short value){
        try{
            dout.writeShort(value);
        }catch(Exception e){}
    }

    public void writeUTF(String value){
        try{
            dout.writeUTF(value);
        }catch(Exception e){}
    }
    
    public void writeByte(byte value){
        try{
            dout.writeByte(value);
        }catch(Exception e){}
    }
    
    public void writeByte(int value){
        try{
            dout.writeByte(value);
        }catch(Exception e){}
    }
    
    
    public void write(byte[] value){
        try{
            dout.write(value);
        }catch(Exception e){}
    }
    
    public void writeFloat(float value){
        try{
            dout.writeFloat(value);
        }catch(Exception e){}
    }
    
    public void writeDouble(double value){
        try{
            dout.writeDouble(value);
        }catch(Exception e){}
    }
    
    public void writeChar(char value){
        try{
            dout.writeChar(value);
        }catch(Exception e){}
    }
    
    public void writeChars(String value){
        try{
            dout.writeChars(value);
        }catch(Exception e){}
    }
    
    public void writeBoolean(boolean value){
        try{
            dout.writeBoolean(value);
        }catch(Exception e){}
    }
    
    public void flush(){
        try{
            dout.flush();
        }catch(Exception e){}
    }
    
    public void writeLong(long value){
        try{
            dout.writeLong(value);
        }catch(Exception e){}
    }
    
    public byte[] toByteArray(){
        byte[] rByte = dArrayStream.toByteArray();
        return rByte;
    }
    
    public void close(){
        try{
            dArrayStream.close();
            dArrayStream = null;
            dout.close();
            dout = null;
        }catch(Exception e){}
    }
}
