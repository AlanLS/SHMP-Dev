/*
 * MemorizedVariable.java
 *
 * Created on October 18, 2007, 7:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author karthik
 */
public class MemorizedVariable  {
    
	private String[] memvarname = null;

	private String[] memvarvalue = null;

        private String[] disvalue = null;

	private int index;

        private String loc = null;

	/** Creates a new instance of MemorizedVariable */
	public MemorizedVariable() {
	}

        /**
         *
         */
        public void setMemorizeValue(String memloc){
            loc = memloc;
            byte[] rbyte = RecordStoreParser.getRecordStore(loc);
            if(null != rbyte){
                ByteArrayReader dIn = new ByteArrayReader(rbyte);
                index = dIn.readByte();
                memvarname = new String[index];
                memvarvalue = new String[index];
                disvalue = new String[index];                        
                for(int i=0;i<index;i++){
                    if(dIn.readBoolean())
                        disvalue[i] = dIn.readUTF();
                    else disvalue[i] = null;
                    memvarname[i] = dIn.readUTF();
                    memvarvalue[i] = dIn.readUTF();
                }
                dIn.close();
                dIn = null;
            } else {
                index = 0;
                memvarname = new String[4];
                memvarvalue = new String[4];
                disvalue = new String[4];
            }
        }
        
	public String[] getMemvarname() {
		return memvarname;
	}

	public void setMemvarname(String[] memvarname) {
		this.memvarname = memvarname;
	}

	public String[] getMemvarvalue() {
		return memvarvalue;
	}

	public void setMemvarvalue(String[] memvarvalue) {
		this.memvarvalue = memvarvalue;
	}

        public String[] getMemdisvalue(){
            return disvalue;
        }

        public int getIndex(){
            return index;
        }

	public String getValue(String _varname,boolean isdis) {
            String value = null;
            if (null != memvarname && index>0) {
                int indexer = _varname.indexOf(".");
                if(indexer>-1){
                    if(_varname.substring(indexer+1).compareTo("itemText") == 0){
                        _varname = _varname.substring(0,indexer);
                    }
                }
                for (int i = 0; i < index; i++) {
                    if ((0 == _varname.toLowerCase().compareTo(memvarname[i].toLowerCase()))) {
                        if(isdis && null != disvalue[i])
                            value = disvalue[i];
                        else
                            value = memvarvalue[i];
                        break;
                    }
                }
            }
            return value;
	}

        /**
         *
         * @param _disval
         * @param _varname
         * @param _value
         */
	public void add(String _disval,String _varname, String _value) {
            if (null != memvarname) {
                //bug id 6014
                for (int i = 0; i < index; i++) {
                    if (0 == _varname.toLowerCase().compareTo(memvarname[i].toLowerCase())) {
                        if(memvarvalue[i].compareTo(_value) != 0 || (null == _disval && null != disvalue[i]) || (null != _disval && null == disvalue[i])
                                ||(null != _disval && null != disvalue[i] && disvalue[i].compareTo(_disval) != 0)){
                            memvarvalue[i] = _value;
                            disvalue[i] = _disval;
                            updateMemorize();
                        }
                        return;
                    }
                }
            }

            if (memvarname.length <= index) {
                String[] _tmp = memvarname;
                memvarname = new String[index + 4];
                System.arraycopy(_tmp, 0, memvarname, 0, index);
                _tmp = memvarvalue;
                memvarvalue = new String[index + 4];
                System.arraycopy(_tmp, 0, memvarvalue, 0, index);
                _tmp = disvalue;
                disvalue = new String[index + 4];
                System.arraycopy(_tmp,0,disvalue,0,index);
                _tmp =null;
            }
            memvarname[index] = _varname;
            memvarvalue[index] = _value;
            disvalue[index] = _disval;
            index++;
            updateNewMemorize();
	}
        
        /**
         * 
         * @param memName
         * @return
         */
        public boolean removeValue(String memName){
            if(null != memvarname){
                for(int i=0;i<index;i++){
                    if(memvarname[i].toLowerCase().compareTo(memName.toLowerCase()) == 0){
                        String[] temp = memvarname;
                        memvarname = new String[temp.length];
                        System.arraycopy(temp, 0, memvarname, 0, i);
                        System.arraycopy(temp, i+1, memvarname, i, (index-(i+1)));
                        temp = memvarvalue;
                        memvarvalue = new String[temp.length];
                        System.arraycopy(temp, 0, memvarvalue, 0, i);
                        System.arraycopy(temp, i+1, memvarvalue, i, (index-(i+1)));
                        temp = disvalue;
                        disvalue = new String[temp.length];
                        System.arraycopy(temp, 0, disvalue, 0, i);
                        System.arraycopy(temp, i+1, disvalue, i, (index-(i+1)));
                        temp = null;
                        index--;
                        updateMemorize();
                        return true;
                    }
                }
            }
            return false;
        }
        
        private void updateMemorize(){
            if(index>0){
                ByteArrayWriter dOut = new ByteArrayWriter();
                dOut.writeByte(index);
                for(int i=0;i<index;i++){
                    if(null != disvalue[i]){
                       dOut.writeBoolean(true);
                       dOut.writeUTF(disvalue[i]);
                    } else dOut.writeBoolean(false);
                    dOut.writeUTF(memvarname[i]);
                    dOut.writeUTF(memvarvalue[i]);
                }
                byte[] rbyte = dOut.toByteArray();
                dOut.close();
                dOut = null;
                RecordStoreParser.UpdateRecordStore(loc, rbyte, true);
            } else RecordStoreParser.deleteRecordStore(loc, true);
        }

        /**
         *
         * @param _disval
         * @param _varName
         * @param _value
         */
        private void updateNewMemorize(){
            byte[] rbyte = RecordStoreParser.getRecordStore(loc);
            ByteArrayWriter dOut = new ByteArrayWriter();
            if(null != rbyte){
                rbyte[0] += 1;
                dOut.write(rbyte);
            } else {
                dOut.writeByte(1);
            }
            if(null != disvalue[index-1]){
               dOut.writeBoolean(true);
               dOut.writeUTF(disvalue[index-1]);
            } else dOut.writeBoolean(false);
            dOut.writeUTF(memvarname[index-1]);
            dOut.writeUTF(memvarvalue[index-1]);
            rbyte = dOut.toByteArray();
            dOut.close();
            dOut = null;
            RecordStoreParser.UpdateRecordStore(loc, rbyte, true);
        }


        /**
         *
         */
        public void deinitialize(){
            disvalue = null;
            memvarname = null;
            memvarvalue = null;
            index = 0;
        }

}
