
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class FileRead implements IFileReadHandler {

    
    private DataInputStream dIn = null;

    public void setByteArrayStream(ByteArrayInputStream bArray){
        dIn = new DataInputStream(bArray);
    }
    
    public void setDataInputStream(DataInputStream dInputStream){
        dIn = dInputStream;
    }
    
    public void skip(int sbyte) throws IOException{       
        if(sbyte>0)
            dIn.skipBytes(sbyte);
    }
    
    public void skipBytes(int sbyte)throws IOException{        
        if(sbyte>0)
        dIn.skipBytes(sbyte);
    }
    
    public byte[] read(byte[] rbyte) throws IOException{
        dIn.read(rbyte);
        return rbyte;
    }
    
    public boolean readBoolean() throws IOException{
        return dIn.readBoolean();
    }
    
    public float readFloat()throws IOException{
        return dIn.readFloat();
    }
    
    public int readInt()throws IOException{
        return dIn.readInt();
    }
    
    public short readShort()throws IOException{
        return dIn.readShort();
    }
    
    public byte readByte()throws IOException {
        return dIn.readByte();
    }
    
    public long readLong()throws IOException{
        return  dIn.readLong();
    }
    
    public String readUTF() throws IOException {
        return dIn.readUTF();
    }
    
    public int available() throws IOException{
        return dIn.available();
    }
    
    public void close() throws IOException{
        dIn.close();
        dIn = null;
    }

    public char readChar() throws IOException{
        return dIn.readChar();
    }

    public double readDouble() throws IOException{
        return dIn.readDouble();
    }
    
}
