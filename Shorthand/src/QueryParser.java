// Cr 10684 and 10683
///**
// * QueryParser.java
// *
// * Created on October 3, 2007, 10:21 AM
// */
//
///**
// * Parser class for the query.obj
// */
//public class QueryParser {
//
//	private int[] varids = null;
//
//	private char[] query = null;
//
//	private char[] sc = null;
//
//        private String[] fValue = null;
//
//	private int action;
//
//	private String[] querynames = null;
//
//        private String[] tquerynames = null;
//
//        private RecordStoreParser rStoreParser = null;
//
//        private String bRIndex = "";
//
//        private int rCount = 0;
//
//        private String pName = null;
//
//        private byte qStCount = 1;
//
//        public QueryParser(){
//
//        }
//
//        /**
//         *
//         * @param pName
//         */
//        public void setquerysName(String pName){
//            this.pName = RecordManager.getQueryAndMemRecordLoc(pName);
//            openRecordStore();
//            byte[] rbytes = null;
//            if(null != rStoreParser){
//                int len = 0;
//                querynames = new String[10];
//                for(int i=qStCount;i<=rCount;i+=2){
//                    rbytes = rStoreParser.getRecord(i);
//                    if(null == rbytes || rbytes.length == 0)
//                        bRIndex = (bRIndex + i + ',');
//                    else {
//                        querynames[len++] = new String(rbytes);
//                        if(querynames.length==len){
//                            String[] temp = querynames;
//                            querynames = new String[len+10];
//                            System.arraycopy(temp, 0, querynames, 0,len);
//                            temp = null;
//                        }
//                    }
//                }
//                if(len>0){
//                    if(querynames.length>len){
//                        String[] temp = querynames;
//                        querynames = new String[len];
//                        System.arraycopy(temp, 0, querynames, 0,len);
//                        temp = null;
//                    }
//                } else querynames = null;
//                closeRecordStore();
//                setTempQueryNames();
//            }
//        }
//
//        private void setTempQueryNames(){
//            if(null != querynames){
//                int len = querynames.length;
//                tquerynames = new String[len];
//                for(int i=0;i<len;i++){
//                    if(querynames[i].startsWith("$"))
//                        tquerynames[i] = querynames[i].substring(1);
//                    else
//                        tquerynames[i] = querynames[i];
//                }
//            }
//        }
//
//	/**
//	 * Method to get the list of query names
//	 */
//	public String[] getQueryNames() {
//            return tquerynames;
//	}
//
//	/**
//	 * Method to get the next action in the sequence
//	 *
//	 * @return
//	 * <li>returns offset value if stack is not empty</li>
//	 * <li>return -1 if the stack is empty </li>
//	 */
//	public int getNextAction() {
//            int id = -1;
//            if (null != varids) {
//                if (action < varids.length) {
//                    id = varids[action++];
//                    if (action == varids.length)
//                        varids = null;
//                }
//            }
//
//            return id;
//	}
//
//        /**
//         *
//         **/
//        public void erace(){
//            varids = null;
//            query = null;
//            sc = null;
//            fValue =null;
//            action = 0;
//        }
//
//        /**
//         *
//         * @param name
//         * @return
//         */
//        public boolean deleteQuery(String name){
//            int len = 0;
//            boolean isdelete = false;
//            openRecordStore();
//            if(null != rStoreParser){
//                if(null != querynames && (len=querynames.length)>0) {
//
//                    for(int i=0;i<len;i++){
//                        if(name.compareTo(tquerynames[i]) == 0){
//                            len--;
//                            name = querynames[i];
//                            if(querynames.length>1){
//                                String[] temp = querynames;
//                                querynames = new String[len];
//                                System.arraycopy(temp,0,querynames,0,i);
//                                System.arraycopy(temp,i+1,querynames,i,len-i);
//                                temp = tquerynames;
//                                tquerynames = new String[len];
//                                System.arraycopy(temp,0,tquerynames,0,i);
//                                System.arraycopy(temp,i+1,tquerynames,i,len-i);
//                                temp = null;
//                            } else{
//                                querynames = null;
//                                tquerynames = null;
//                            }
//                            isdelete = true;
//                            break;
//                        }
//                    }
//
//                    String qName = null;
//                    byte[] rBytes = null;
//                    for(int i=qStCount;i<=rCount;i+=2){
//                        rBytes = rStoreParser.getRecord(i);
//                        if(null != rBytes && rBytes.length>0){
//                            qName = new String(rBytes);
//                            if(name.compareTo(qName) == 0){
//                                bRIndex = (bRIndex + i + ",");
//                                rBytes = new byte[0];
//                                rStoreParser.setRecord(i, rBytes, 0, 0, true);
//                                rStoreParser.setRecord(i+1, rBytes, 0, 0, true);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//            closeRecordStore();
//            return isdelete;
//        }
//
//
//        private void openRecordStore(){
//            rStoreParser = new RecordStoreParser();
//            if(rStoreParser.openRecordStore(RecordManager.getRecordStoreName(pName), true,false,false))
//                rStoreParser = null;
//            else
//                rCount = rStoreParser.getNumRecords();
//        }
//
//	/**
//	 *
//	 * @param name
//	 * @return boolean
//	 */
//	private boolean addQuery(String name) {
//            int len;
//            if(null == querynames) {
//                querynames = new String[1];
//                tquerynames = new String[1];
//                querynames[0] = name;
//                tquerynames[0] = name;
//            }else {
//                len = querynames.length;
//                String[] temp = querynames;
//                querynames = new String[len+1];
//                System.arraycopy(temp, 0, querynames, 0, len);
//                temp = tquerynames;
//                tquerynames = new String[len+1];
//                System.arraycopy(temp,0,tquerynames,0,len);
//                querynames[len]= name;
//                tquerynames[len]= name;
//                temp =null;
//            }
//            return true;
//	}
//
//
//
//        public void renameQuery(String oldName,String newName){
//            int len = 0;
//            openRecordStore();
//            if (null != rStoreParser) {
//                if(null != querynames && (len = querynames.length) > 0){
//                    for (int i = 0; i < len; i++) {
//                        if (oldName.compareTo(tquerynames[i]) == 0) {
//                            tquerynames[i] = newName;
//                            if(querynames[i].startsWith("$")){
//                                newName ="$" + newName;
//                                oldName ="$" + oldName;
//                            }
//                            querynames[i] = newName;
//                            break;
//                        }
//                    }
//                    byte[] rBytes = null;
//                    String qName =null;
//                    for(int i=qStCount;i<=rCount;i+=2){
//                        rBytes = rStoreParser.getRecord(i);
//                        if(null != rBytes && rBytes.length>0){
//                            qName = new String(rBytes);
//                            if(oldName.compareTo(qName) == 0){
//                                rBytes = newName.getBytes();
//                                rStoreParser.setRecord(i, rBytes, 0, rBytes.length, true);
//                                break;
//                            }
//                        }
//                    }
//                }
//                closeRecordStore();
//            }
//        }
//
//
//	/**
//	 *
//	 * @param name
//	 * @return
//	 */
//	public boolean isQueryNameExits(String name) {
//            int len;
//            if (null != tquerynames && (len = tquerynames.length) > 0) {
//                for (int i = 0; i < len; i++) {
//                    if (tquerynames[i].compareTo(name) == 0)
//                        return true;
//                    if(querynames[i].compareTo(name) == 0)
//                        return true;
//                }
//            }
//            return false;
//	}
//
//        /**
//         *
//         * @param name
//         * @return
//         */
//        private String getQueryName(String name){
//            int len;
//            if(null != tquerynames && (len =tquerynames.length)>0){
//                for(int i=0;i<len;i++){
//                    if(tquerynames[i].compareTo(name) == 0)
//                        return querynames[i];
//                }
//            }
//            return null;
//        }
//
//        /**
//         *
//         * @param name
//         * @return
//         */
//        public int loadQuery(String name){
//            int i=-1;
//            erace();
//            name = getQueryName(name);
//            if(null != name){
//                openRecordStore();
//                if(null != rStoreParser){
//                    byte[] rbytes = null;
//                    String qName = null;
//                    for(int j=qStCount;j<=rCount;j+=2){
//                        rbytes = rStoreParser.getRecord(j);
//                        if(null != rbytes && rbytes.length>0){
//                            qName = new String(rbytes);
//                            if(name.compareTo(qName) == 0){
//                                rbytes = rStoreParser.getRecord(j+1);
//                                if(null != rbytes){
//                                    ByteArrayReader dInstream = new ByteArrayReader(rbytes);
//                                    query = dInstream.readUTF().toCharArray(); // read query
//                                    if(dInstream.readBoolean())
//                                        sc = dInstream.readUTF().toCharArray(); // read shortcode
//                                    byte icount = dInstream.readByte(); // read number of variable id's
//                                    varids = new int[icount];
//                                    for (byte k = 0; k < icount; k++) {
//                                        varids[k] = dInstream.readInt();
//                                    }
//                                    icount = dInstream.readByte();
//                                    if(icount>0){
//                                        fValue = new String[icount];
//                                        for(int k=0;k<icount;k++){
//                                            fValue[k] = dInstream.readUTF();
//                                        }
//                                    }
//                                    dInstream.close();
//                                    dInstream = null;
//                                }
//                            }
//                        }
//                    }
//                    closeRecordStore();
//                }
//            if (null != varids && varids.length > 0)
//                        return varids[0];
//            }
//            return i;
//        }
//
//	/**
//	 * Method to get the query stored.
//	 *
//	 * @return
//	 */
//	public String getQuery() {
//            return String.valueOf(query);
//	}
//
//        /**
//         * Method to get the Fixed value
//         *
//         * @return String Array
//         **/
//        public String[] getFixValue(){
//            return fValue;
//        }
//
//	/**
//	 * Method to get the shortcode;
//	 *
//	 * @return
//	 */
//	public String getShortCode() {
//            return String.valueOf(sc);
//	}
//
//	/**
//	 *
//	 * @param name
//	 * @param sc
//	 * @param ids
//	 * @param query
//	 *
//	 */
//	public void storeQuery(String sc, int[] ids, String query,String[] fval) {
//            if(null != sc)
//                this.sc = sc.toCharArray();
//            this.varids = ids;
//            this.query = query.toCharArray();
//            this.fValue = fval;
//	}
//
//	/**
//	 *
//	 * @param qName
//	 */
//	public boolean saveQuery(String qName) {
//            boolean isStore = true;
//            if(null != query){
//                ByteArrayWriter dout = new ByteArrayWriter();
//                if(null != dout){
//                    dout.writeUTF(String.valueOf(query));
//                    if(null != sc){
//                        dout.writeBoolean(true);
//                        dout.writeUTF(String.valueOf(sc));
//                    } else dout.writeBoolean(false);
//                    byte n = (byte)varids.length; //variable ids length
//                    dout.writeByte(n);
//                    for(byte i = 0; i < n; i++)
//                        dout.writeInt(varids[i]);
//                    if(null != fValue){
//                    n = (byte)fValue.length;
//                    dout.writeByte(n);
//                    for(byte i = 0; i < n; i++)
//                        dout.writeUTF(fValue[i]);
//                    } else dout.writeByte(0);
//                    byte[] rBytes = dout.toByteArray();
//                    openRecordStore();
//                    if(null != rStoreParser){
//                        if(bRIndex.indexOf(",")>-1){
//                            int len  = Integer.parseInt(bRIndex.charAt(0)+"");
//                            bRIndex = bRIndex.substring(2);
//                            rStoreParser.setRecord(len, qName.getBytes(), 0, qName.length(), true);
//                            rStoreParser.setRecord(len+1, rBytes, 0, rBytes.length, true);
//                        } else {
//                            rStoreParser.addRecord(qName.getBytes(), 0, qName.length(), true);
//                            rStoreParser.addRecord(rBytes, 0, rBytes.length, true);
//                        }
//                        closeRecordStore();
//                    } else isStore = false;
//                    erace();
//                    addQuery(qName);
//                } else isStore = false;
//                dout.close();
//                dout = null;
//            }
//            return isStore;
//	}
//
//
//        /**
//         *
//         */
//        private void closeRecordStore(){
//            if(null != rStoreParser){
//                rStoreParser.closeRecordStore();
//                rStoreParser = null;
//            }
//        }
//
//	/**
//	 *
//	 */
//	public void shutdownParser() {
//            closeRecordStore();
//            varids = null;
//            query = null;
//            sc = null;
//            fValue = null;
//            querynames = null;
//            tquerynames = null;
//            bRIndex = "";
//            pName = null;
//	}
//}
