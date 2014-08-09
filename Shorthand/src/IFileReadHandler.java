
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public interface IFileReadHandler {

    public char readChar() throws IOException;

    public double readDouble() throws IOException;

    void setByteArrayStream(ByteArrayInputStream bArray);
    
    void setDataInputStream(DataInputStream din);
    
    void skip(int sbyte) throws IOException;
    
    void skipBytes(int sbyte)throws IOException;
    
    byte[] read(byte[] rbyte) throws IOException;
    
    boolean readBoolean() throws IOException;
    
    int readInt() throws IOException;
    
    long readLong() throws IOException;
    
    byte readByte() throws IOException;
    
    String readUTF() throws IOException;
    
    float readFloat() throws IOException;
    
    short readShort() throws IOException;
    
    int available() throws IOException;
    
    void close() throws IOException;
    
}
