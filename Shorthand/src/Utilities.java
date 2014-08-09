/*
 * Utilities Class will use to do some common operations like Split Text , remove Text ,replace Text for the all Backend
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough 
 */



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;

public class Utilities {

        // Variable will contain the number of line to disply in the message box
        public static int noline = 0;

        private static long currentTime = 0;

        private static String logging = "";

        
        private static Vector path = null;
    
        /**
         * Method to Split the Given text based on the specified text
         *
         * @param orgStr - Variable will contain the Original Text to Split
         * @param sText - Variable will contain the text which one based to split
         *
         * @return values - Variable will contain the text after split
         **/
	public static String[] split(String orgStr, String sText) {
                int serPos;
		int stPos=0;
		String[] values = null;
		
		if (null != orgStr) {
			Vector value = new Vector();
			while ((serPos = orgStr.indexOf(sText,stPos)) > -1) {
				value.addElement(orgStr.substring(stPos, serPos));
				stPos = serPos+sText.length();
			}
			value.addElement(orgStr.substring(stPos,orgStr.length()));
			values = new String[value.size()];
			value.copyInto(values);
                        value.removeAllElements();
                        value = null;
		}
		return values;
	}

        /**
         * Method to replace the given text for the alternative text in the given Original Text
         *
         * @param orgStr - Variable will contain the Original Text
         * @param rmStr - Variable will contain the text which is going to remove
         * @param reStr - Variable will contain the Text which is going to replace
         *
         * @return - return the String After replaced the given text.It may be null.
         **/
        
	public static String replace(String orgStr, String rmStr, String reStr) {
		int serPos;
		int stPos = 0;
		if (null != orgStr) {
			StringBuffer strBuf = new StringBuffer();
			while ((serPos = orgStr.indexOf(rmStr,stPos)) > -1) {
				strBuf.append(orgStr.substring(stPos, serPos)).append(reStr);
				stPos = serPos + rmStr.length();
			}
			strBuf.append(orgStr.substring(stPos,orgStr.length()));
			return strBuf.toString();
		}
		return null;
	}

        /**
         * Method to remove the given text from the given Original Text
         *
         * @param orgStr - Variable will contain the Original Text
         * @param rmStr - Variable will contain the text which is going to remove
         *
         * @return orgStr - Variable will contain the text after removed the given text
         **/
        
    public static String remove(String orgStr, String rmStr) {
        int index;
        if(null != orgStr){
            StringBuffer strBuf =null;
            while((index = orgStr.indexOf(rmStr))>-1){
                strBuf = new StringBuffer(orgStr.substring(0,index));
                strBuf.append(orgStr.substring(index+rmStr.length()));
                orgStr = strBuf.toString();
            }
        }
        return orgStr;
    }

        /**
         * Method to get the position of the Find text from the Original text
         *
         * @param orgStr - Variable will contain the Original Text
         * @param fStr - Variable will contain the text which one is going to find
         * 
         * @return lindex - Variable will contain the position
         **/
        
        public static int getLastIndex(String orgStr,String fStr){
            int index=0;
            int lindex=-1;
            if(null != orgStr){
                while((index = orgStr.indexOf(fStr,index))>-1){
                    lindex = index;
                    index += 1;
                }
            }
            return lindex;
        }
        
        /**
         * Method to insert the given text in the Original Text
         *
         * @param orgStr - Variable will contain the Original Text
         * @param stIndex - Variable wil contain the position where we have to insert the sub text
         * @param cStr - Variable will contain the text which is going to insert
         *
         * @return - Strig value will return after inserted the given text
         **/
        public static String insertString(String orgStr,int stIndex,String cStr){
            StringBuffer strbuf = new StringBuffer();
            strbuf.append(orgStr.substring(0,stIndex));
            strbuf.append(cStr);
            strbuf.append(orgStr.substring(stIndex));
            return strbuf.toString();
        }

        public static int getCurrentYear(){
            Calendar cal = Calendar.getInstance();
            return cal.get(Calendar.YEAR);
        }
        
        /**
         * Method to split the Date from the given text
         *
         * @param orgStr - Variable will conatin the Text
         *
         * @return - return the date in String format
         *
         * @throws viewDateException
         **/
        public static String getInboxViewDate(String orgStr){
		Calendar calendar = Calendar.getInstance();
		StringBuffer strBuf = new StringBuffer();
		try {
                    long time = Long.parseLong(orgStr);
                    Date date = new Date();
                    date.setTime(time);
                    calendar.setTime(date);
                    date = null;
		} catch (Exception viewDateException) {}
                
                int fM =0;
                int sd =0;
                int ty =0;
                char sep ='/';
                String am_Pm = null;
                Calendar cal = Calendar.getInstance();
                if(cal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        cal.get(Calendar.DATE)== calendar.get(Calendar.DATE) && 
                        cal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
                    fM = calendar.get(Calendar.HOUR);
                    // 3742 Bug ID
                    if(fM == 0)
                        fM = 12;
                    sd = calendar.get(Calendar.MINUTE);
                    ty = calendar.get(Calendar.SECOND);
                    sep =':';
                    if(calendar.get(Calendar.AM_PM) == 0) am_Pm = "AM";
                    else am_Pm = "PM";
                } else {
                    fM = calendar.get(Calendar.MONTH)+1;
                    sd =calendar.get(Calendar.DATE);
                    ty = calendar.get(Calendar.YEAR)-2000;
                }
                //bug id 3859
                if(fM<10 && null == am_Pm)
                    strBuf.append("0");
                strBuf.append(fM).append(sep);
                if(sd<10)
                    strBuf.append("0");
                strBuf.append(sd);
                if(null == am_Pm){
                    strBuf.append(sep);
                    if(ty<10)
                        strBuf.append("0");
                    strBuf.append(ty);
                } else strBuf.append(am_Pm);
                cal = null;
                calendar  = null;
        	return strBuf.toString();
        }
        
//        public static String getViewDate(String orgStr){
//            Calendar calendar = Calendar.getInstance();
//            try {
//                    long time = Long.parseLong(orgStr);
//                    Date date = new Date();
//                    date.setTime(time);
//                    calendar.setTime(date);
//                    date = null;
//            } catch (Exception viewDateException) {}
//
//            String am_Pm = "pm ";
//            if(calendar.get(Calendar.AM_PM) == 0) am_Pm = "am ";
//            short yr = (short)calendar.get(Calendar.HOUR);
//            if(yr == 0)
//                yr =12;
//            StringBuffer strBuf = new StringBuffer()
//                    .append(yr).append(':');
//            yr = (short)calendar.get(Calendar.MINUTE);
//            if(yr<10)
//                strBuf.append("0");
//            strBuf.append(yr)
//                    .append(am_Pm)
//                    .append(calendar.get(Calendar.DATE)).append('-')
//                    .append(getMonth(calendar.get(Calendar.MONTH))).append('-');
//            yr = (short)(calendar.get(Calendar.YEAR)%100);
//            if(yr<10)
//                strBuf.append('0');
//            strBuf.append(yr);
//                    
//            calendar = null;
//            return strBuf.toString();
//        }
        
        public static String getMonth(int nMonth){
            if(Calendar.JANUARY == 0){
                return Constants.months[nMonth];
            } else return Constants.months[nMonth-1];
    }

        
        /**
         * Method to split the Date , Month and Year from other format.
         *
         * @param orgStr - Variable will contain the text with the date month and year
         *
         * @return - date , month and year will return
         **/
        public static String getMonthDateYear(String orgStr){
            char sep='/';
            Calendar calendar = Calendar.getInstance();
            StringBuffer strBuf = new StringBuffer();
            try{
                long time = Long.parseLong(orgStr);
                Date date = new Date();
                date.setTime(time);
                calendar.setTime(date);
            }catch (Exception e){}
            strBuf.append(calendar.get(Calendar.MONTH)+1).append(sep)
			.append(calendar.get(Calendar.DATE)).append(sep)
			.append(calendar.get(Calendar.YEAR));
            calendar = null;
            return strBuf.toString();
        }
        
	/**
	 * Method to sort the given text
         *
         * @param strArray - Variable will contain the String values
         *
         * @return strArray - Variable will contain the String values after sorted
	 */
	public String[] sort(String[] strArray){
            int len;
            if(null != strArray && (len= strArray.length)>0)
            {
                String temp = null;
                for(int i=0;i<len;i++)
                {
                    for(int j=i+1;j<len;j++)
                    {
                        if(strArray[j].compareTo(strArray[i])<0)
                        {
                            temp = strArray[i];
                            strArray[i] = strArray[j];
                            strArray[j]= temp;
                        }
                    }
                }
            }
            return strArray;
	}
	
        
        public static String getCurrentDateHHMMDDYYFormat(){
            Calendar cale = Calendar.getInstance();
            int index = cale.get(Calendar.HOUR);
            StringBuffer stbuf = new StringBuffer();
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            index = cale.get(Calendar.MONTH)+1;
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            index = cale.get(Calendar.DATE);
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            index = cale.get(Calendar.YEAR)%100;
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            cale = null;
            return stbuf.toString();
        }

        //CR 14324
        public static String getCurrentDateYYYYMMDDHHMMFormat(){
            Calendar cale = Calendar.getInstance();
            StringBuffer stbuf = new StringBuffer();
            int index = cale.get(Calendar.YEAR);
            stbuf.append(index);

            index = cale.get(Calendar.MONTH)+1;
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);

            index = cale.get(Calendar.DATE);
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);

            index = cale.get(Calendar.HOUR_OF_DAY);
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            
            index = cale.get(Calendar.MINUTE);
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);
            cale = null;
            return stbuf.toString();
        }

        //CR 14330
        public static String getCurrentDateYYYYMMDDFormat(){
            Calendar cale = Calendar.getInstance();
            StringBuffer stbuf = new StringBuffer();
            int index = cale.get(Calendar.YEAR);
            stbuf.append(index);

            index = cale.get(Calendar.MONTH)+1;
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);

            index = cale.get(Calendar.DATE);
            if(index<10)
                stbuf.append("0");
            stbuf.append(index);

            cale = null;
            return stbuf.toString();
        }
        /**
     * Method to Mark the Phonenumber for the given string
     *      <li> 1. (xxx) xxx-xxxx    </li>
     *      <li> 2. (xxx)xxx-xxxx     </li>
     *      <li> 3. xxx xxx-xxxx      </li>
     *      <li> 4. xxx xxx xxxx      </li>
     *      <li> 5. xxx-xxx-xxxx      </li>
     *      <li> 6. 1 xxx-xxx-xxxx    </li>
     *      <li> 7. 1-xxx-xxx-xxxx    </li>
     *      <li> 8. 1-(xxx) xxx-xxxx  </li>
     *      <li> 9. 1 (xxx) xxx-xxxx  </li>
     *      <li> 10. 1xxxxxxxxxx      </li>
     *      <li> 11. xxxxxxxxxx       </li>
     *      <li> 12. xxxxxxxxxxxx     </li>
     * @param orgText display Messae String or PhoneNumber Mark String
     * @return phoneNumber Marked String
     */
//    public static String markPhoneNumber(String orgText){
//        try{
//            if(null != orgText){
//                int len = orgText.length();
//                int mLen = len;
//                char ch;
//                int count = 0;
//                boolean isfind = false;
//                boolean isExit = true;
//                int iCount = 0;
//                int lexc = 0;
//                byte nCount = 0;
//                int i=0;
//                StringBuffer text = new StringBuffer(orgText);
//                while(i<mLen){
//                    if((len=orgText.indexOf("<|", i))>-1){
//                        if((i-len) == 0) { 
//                            len = orgText.indexOf("|>",i);
//                            i = len + 2;
//                            if((len = orgText.indexOf("<|",i)) == -1)
//                                len = orgText.length();
//                        } 
//                    } else len = orgText.length();
//                    while(i<len){
//                        isfind = false;
//                        isExit = true;
//                        iCount = 0;
//                        ch = text.charAt(i);
//                        if('(' ==  ch){
//                            lexc = i + 2;
//                            count = i + 1;
//                            nCount = 0;
//                            iCount = i + 3;
//                            while(count<lexc && iCount<len && isExit){
//                                try{
//                                    Integer.parseInt(orgText.substring(lexc,iCount));
//                                    count = iCount;
//                                    if(isfind){
//                                        ch = text.charAt(iCount+1);
//                                        if(ch == ')'){
//                                            lexc +=1;
//                                            if(text.charAt(lexc+1) == ' ' || text.charAt(lexc+1) == '-'){
//                                                lexc+=1;
//                                                iCount+= 2;
//                                            }
//                                            iCount += 4;
//                                            isfind = true;
//                                        } else isExit = false;;
//                                    } else lexc = 0;
//                                }catch(Exception e){ isExit = false;}
//                                if(isfind && count != iCount)
//                                    isfind = false;
//                            }
////                            while(count<lexc && count<len && isExit){
////                                ch = text.charAt(count);
////                                if(isExit && iCount<nCount){
////                                    if(ch > 47 && ch < 58){
////                                        count++;
////                                        iCount++;
////                                    } else isExit = false;
////                                } else if(iCount == nCount && ')' == ch){
////                                    count++;
////                                    ch = text.charAt(count);
////                                    if(' ' == ch)
////                                        count++;
////                                    else lexc = i + 14;
////                                    iCount = 0;
////                                } else if(iCount == nCount && '-' == ch){
////                                    iCount = 0;
////                                    nCount = 4;
////                                    count++;
////                                } else lexc = 0;
////                            }
////                            if(isExit && iCount == 4){
////                                isfind = true;
////                                nCount = 5;
////                            }else isExit = true;
//                        } 
//                        if(!isfind && '1' == ch){
////                            iCount = i+1;
////                            lexc = i+1;
////                            count = i;
////                            if(iCount<len){
////                                ch = text.charAt(iCount);
////                                if(ch == ' '|| ch == '-'){
////                                    lexc = iCount + 1;
////                                    count = iCount;
////                                    iCount += 1;
////                                    if(iCount<len){
////                                        if(text.charAt(i) == '('){
////                                            lexc += 1;
////                                            count = iCount;
////                                            iCount += 4;
////                                        } else iCount += 2;
////                                    }
////                                    while(count<lexc && iCount<len && isExit){
////                                        try{
////                                            Integer.parseInt(text.substring(lexc,iCount));
////                                            count = iCount;
////                                            
////                                            
////                                        }catch(Exception e){}
////                                    }
////                                }
////                            }
//                            
//                            
//                            count =i+1;
//                            if(count<len)
//                                ch = text.charAt(count);
//                            if(ch == ' ' || ch == '-' || (ch > 47 && ch < 58)){
//                                count++;
//                                if(count<len){
//                                    if('(' == text.charAt(count)){
//                                        count++;
//                                        lexc = i + 16;
//                                    } else if(ch > 47 && ch < 58)
//                                        lexc = i + 11;
//                                    else lexc = i + 14;
//                                    nCount = 3;
//                                    while(count<lexc && count<len && isExit){
//                                        ch = text.charAt(count);
//                                        if(isExit && iCount<nCount){
//                                            if(ch > 47 && ch < 58){
//                                                count++;
//                                                iCount++;
//                                            } else isExit = false;
//                                        } else if(iCount == nCount && ('-' == ch || ')' == ch)){
//                                            iCount = 0;
//                                            count++;
//                                            if(count<len){
//                                                ch = text.charAt(count);
//                                                if(count>(i+9))
//                                                    nCount = 4;
//                                                else if(ch == ' ')
//                                                    count++;
//                                            }
//                                        } else if(iCount == nCount && ch > 47 && ch < 58){
//                                            iCount = 0;
//                                            if(count>(i+6))
//                                                nCount = 4;
//                                            else nCount = 2;
//                                        } else lexc = 0;
//                                    }
//                                    if(isExit && iCount == 4){
//                                        isfind = true;
//                                        nCount = 5;
//                                    } else isExit = true;
//                                }
//                            }
//                        }
//                        if(!isfind && ch > 47 && ch < 58){
//                            count = i;
//                            lexc = i+1;
//                            iCount = i + 3;
//                            nCount = 0;
//                            try{
//                                while(count<lexc && iCount<len && isExit){
//                                    Integer.parseInt(orgText.substring(lexc,iCount));
//                                    count = iCount;
//                                    if(!isfind){
//                                        ch = text.charAt(iCount+1);
//                                        if(nCount < 6 && ' ' == ch || '-' == ch){
//                                            lexc = iCount + 1;
//                                            if(nCount == 3){
//                                                iCount += 2;
//                                                isfind = true;
//                                            }
//                                            iCount += 4;
//                                            nCount = 3;
//                                        } else if(ch > 47 && ch < 58){
//                                            lexc = iCount + 1;
//                                            if(nCount == 6){
//                                                iCount += 2;
//                                                nCount = 7;
//                                                isfind = true;
//                                            } else { 
//                                                iCount += 7;
//                                            }
//                                            nCount = 6;
//                                        } else isExit = false;
//                                    } else lexc = 0;
//                                }
//                            }catch(Exception e){ isExit = false;}
//                            if(!isExit && nCount == 7){
//                                isfind = true;
//                            } 
//                            if(isfind)
//                                nCount = 0;
////                            
////                            lexc = i + 12;
////                            count = i + 1;
////                            nCount = 2;
////                            while(count < lexc && count<len && isExit){
////                                ch = text.charAt(count);
////                                if(isExit && iCount<nCount){
////                                    if(ch > 47 && ch < 58){
////                                        iCount++;
////                                        count++;
////                                    } else isExit = false;
////                                } else if(iCount == nCount && (' ' == ch || '-' == ch)){
////                                    iCount = 0;
////                                    if(count>(i+6))
////                                        nCount = 4;
////                                    else nCount = 3;
////                                    count++;
////                                } else if(iCount == nCount && (ch > 47 && ch < 58)){
////                                    lexc = i + 10;
////                                    iCount = 0;
////                                    if(count>(i+5))
////                                        nCount = 4;
////                                    else nCount = 3;
////                                } else lexc = 0;
////                            }
////                            if(isExit && iCount == 4)
////                                isfind = true;
//                        }
//                        if(isfind){
//                            text.insert(count, "|>");
//                            text.insert(i, "<|");
//                            i = count + nCount;
//                            len += 4;
//                            mLen += 4;
//                        } else i++;
//                    }
//                    orgText = text.toString();
//                }
//                text = null;
//            }
//        }catch(Exception markException){ }
//        return orgText;
//    }
     /* Method to Mark the Phonenumber for the given string
     *      <li> 1. (xxx) xxx-xxxx    </li>
     *      <li> 2. (xxx)xxx-xxxx     </li>
     *      <li> 3. xxx xxx-xxxx      </li>
     *      <li> 4. xxx xxx xxxx      </li>
     *      <li> 5. xxx-xxx-xxxx      </li>
     *      <li> 6. 1 xxx-xxx-xxxx    </li>
     *      <li> 7. 1-xxx-xxx-xxxx    </li>
     *      <li> 8. 1-(xxx) xxx-xxxx  </li>
     *      <li> 9. 1 (xxx) xxx-xxxx  </li>
     *      <li> 10. 1xxxxxxxxxx      </li>
     *      <li> 11. xxxxxxxxxx       </li>
     *      <li> 12. xxxxxxxxxxxx     </li>
     * @param orgText display Messae String or PhoneNumber Mark String
     * @return phoneNumber Marked String
     */
        public static String markNumbers(String markText){
            if(null != markText){
                Hashtable buildTable = new Hashtable();
                int index = 0;
                int sIndex =0;
                while((index=markText.indexOf("1 (",sIndex))>-1 || (index=markText.indexOf("1-(",sIndex))>-1){
                    sIndex = index+3;
                    
                }
            }
            return markText;
            
        }
        
        private static String markSerailNumbers(String markText){
            return markText;
        }
        
        private static boolean isMarked(String msrkedText,int index, int endIndex){
            return false;
        }
        
        public static String markNumber(String markText){
            String[] phumber = new String[]{"(",") ","-"};
            int[] number = new int[]{3,3,4};
            int charLen = 13;
            boolean[] priority = new boolean[]{true,false,true,false,true,false};
            int index =-1;
            int endIndex =-1;
            int len = markText.length();
            int serLen = priority.length;
            int nCount =0;
            int sCount =0;
            boolean isNotMatch = true;
            int sIndex = -1;
            if((index = markText.indexOf(phumber[0]))>-1){
                while(len>(index+charLen)){
                    isNotMatch = true;
                    nCount = 0;
                    sCount = 0;
                    sIndex = -1;
                    for(int i=0;i<serLen && isNotMatch;i++){
                        if(sIndex == -1)
                            sIndex = index;
                        if(priority[i]){
                            endIndex = markText.indexOf(phumber[sCount],index);
                            if(endIndex == index){
                                index += phumber[sCount].length();
                                sCount++;
                            } else isNotMatch = false;
                        } else {
                            try{
                                Integer.parseInt(markText.substring(index,index+number[nCount]));
                                nCount++;
                            }catch(Exception e){
                                isNotMatch = false;
                                index = index+number[nCount];
                            }
                        }
                    }
                    if(isNotMatch){
                        
                    }
                }
            }
            return markText;
        }
//    
        private String[] getNumberMatchFormat(int count){
            String[] value = null;
            if(count == 0){
                
            }
            return value;
        }
        
        
        
        public static String markPhoneNumber(String orgText){
        try{
            if(null != orgText){
                int len = orgText.length();
                int mLen = len;
                char ch;
                int count = 0;
                boolean isfind = false;
                boolean isExit = true;
                byte iCount = 0;
                int lexc = 0;
                byte nCount = 0;
                int i=0;
                StringBuffer text = new StringBuffer(orgText);
                while(i<mLen){
                    if((len=orgText.indexOf("<|", i))>-1){
                        if((i-len) == 0) { 
                            len = orgText.indexOf("|>",i);
                            i = len + 2;
                            if((len = orgText.indexOf("<|",i)) == -1)
                                len = orgText.length();
                        } 
                    } else len = orgText.length();
                    while(i<len){
                        isfind = false;
                        isExit = true;
                        iCount = 0;
                        ch = text.charAt(i);
                        if((len-i)>7 && ch == '+'){ //CR 2846
                            boolean isNot = true;
                            count = i+7;
                            lexc = i+1;
                            try{
                                Integer.parseInt(text.toString().substring(lexc,(count+1)));
                                count++;
                            }catch(Exception e){
                                isNot = false;
                            }
                            while((len-count)>0 && (count-lexc)<12 && isNot ){
                                ch = text.charAt(count);
                                if(ch>47 && ch<58){
                                    count++;
                                } else isNot = false;
                            }
                            if((count-i)>7){
                                isfind = true;
                            }
                        }
                        ch = text.charAt(i);
                        if(!isfind && '(' ==  ch){
                            lexc = i+15;
                            count = i+1;
                            nCount = 3;
                            while(count<lexc && count<len && isExit){
                                ch = text.charAt(count);
                                if(isExit && iCount<nCount){
                                    if(ch > 47 && ch < 58){
                                        count++;
                                        iCount++;
                                    } else isExit = false;
                                } else if(iCount == nCount && ')' == ch){
                                    count++;
                                    ch = text.charAt(count);
                                    if(' ' == ch)
                                        count++;
                                    else lexc = i + 14;
                                    iCount = 0;
                                } else if(iCount == nCount && '-' == ch){
                                    iCount = 0;
                                    nCount = 4;
                                    count++;
                                } else lexc = 0;
                            }
                            if(isExit && iCount == 4)
                                isfind = true;
                            else isExit = true;
                        }
                        ch = text.charAt(i);
                        if( !isfind && ('1' == ch || '0' == ch)){
                            count =i+1;
                            if(count<len)
                                ch = text.charAt(count);
                            if(ch == ' ' || ch == '-' || (ch > 47 && ch < 58)){
                                count++;
                                if(count<len){
                                    if('(' == text.charAt(count)){
                                        count++;
                                        lexc = i + 16;
                                    } else if(ch > 47 && ch < 58)
                                        lexc = i + 11;
                                    else lexc = i + 14;
                                    nCount = 3;
                                    while(count<lexc && count<len && isExit){
                                        ch = text.charAt(count);
                                        if(isExit && iCount<nCount){
                                            if(ch > 47 && ch < 58){
                                                count++;
                                                iCount++;
                                            } else isExit = false;
                                        } else if(iCount == nCount && ('-' == ch || ')' == ch)){
                                            iCount = 0;
                                            count++;
                                            if(count<len){
                                                ch = text.charAt(count);
                                                if(count>(i+9))
                                                    nCount = 4;
                                                else if(ch == ' ')
                                                    count++;
                                            }
                                        } else if(iCount == nCount && ch > 47 && ch < 58){
                                            iCount = 0;
                                            if(count>(i+6))
                                                nCount = 4;
                                            else nCount = 2;
                                        } else lexc = 0;
                                    }
                                    if(isExit && iCount == 4)
                                        isfind = true;
                                    else isExit = true;
                                }
                            }
                        }
                        ch = text.charAt(i);
                        if(!isfind && ch > 47 && ch < 58){
                            lexc = i + 12;
                            count = i + 1;
                            nCount = 2;
                            iCount = 0;
                            while(count <= lexc && count<len && isExit){
                                ch = text.charAt(count);
                                if(isExit && iCount<nCount){
                                    if(ch > 47 && ch < 58){
                                        iCount++;
                                        count++;
                                    } else isExit = false;
                                } else if(iCount == nCount && (count <= (i+9)) && (' ' == ch || '-' == ch)){
                                    iCount = 0;
                                    if(count>(i+6))
                                        nCount = 4;
                                    else nCount = 3;
                                    count++;
                                } else if(iCount == nCount && (ch > 47 && ch < 58)){
                                    lexc = i + 11;
                                    iCount = 0;
                                    if(count>(i+9)){
                                        isfind = true;
                                        nCount = 1;
                                    } else if(count>(i+5)){
                                        nCount = 4;
                                    } else nCount = 3;
                                } else lexc = 0;
                            }
                            if(isExit && iCount == 4)
                                isfind = true;
                        }
                        ch = text.charAt(i);
                        if(!isfind && ch>47 && ch<58){
                            if((len-i)>7){
                                ch = text.charAt(i+2);
                                if(ch == '-' || ch == ' ' && (len-i)>11){
                                    try{
                                        Integer.parseInt(text.toString().substring(i,i+2));
                                        Integer.parseInt(text.toString().substring(i+3,i+11));
                                        count = i+11;
                                        isfind = true;
                                    }catch(Exception e){ }
                                } else {
                                    try{
                                        Integer.parseInt(text.toString().substring(i,i+8));
                                        count = i+8;
                                        isfind = true;
                                    }catch(Exception e){}
                                }
                            }
                        }
                        if(isfind){
                            text.insert(count, "|>");
                            text.insert(i, "<|");
                            i = count + 4; //i = count + 5;
                            len += 4;
                            mLen += 4;
                        } else i++;
                    }
                    orgText = text.toString();
                }
                text = null;
            }
        }catch(Exception markException){ }
        return orgText;
    }

        
        /**
         * Method to get the Current Date , Month and Year 
         *
         * @return - date ,month and year will return in the String format
         **/
        public static String getCurrentMMDDYY(){
            StringBuffer stbuf = new StringBuffer();
            Calendar curCal = Calendar.getInstance();
            if((curCal.get(Calendar.MONTH)+1)<10)
                stbuf.append(0);
            stbuf.append(curCal.get(Calendar.MONTH)+1);
            if(curCal.get(Calendar.DATE)<10)
                stbuf.append(0);
            stbuf.append(curCal.get(Calendar.DATE));
            int ye =(curCal.get(Calendar.YEAR)-2000)%100;
            if(ye<10)
                stbuf.append(0);
            stbuf.append(ye);
            curCal = null;
            return stbuf.toString();
        }


        
        /**
         * Method to get the Hour and Minutes
         * 
         * @return - hour and minutes will return in the String format
         */
        public static String getHHMMFormat(String forTime){
            Calendar cal = Calendar.getInstance();
            Date date = new Date();
            long time = Long.parseLong(forTime);
            date.setTime(time);
            cal.setTime(date);
            int temp = cal.get(Calendar.HOUR);
            StringBuffer stbuf = new StringBuffer();
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp);
            temp = cal.get(Calendar.MINUTE);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp);
            temp = cal.get(Calendar.AM_PM);
            if(temp == 0) //AM
                stbuf.append("AM");
            else
                stbuf.append("PM");
            cal = null;
            return stbuf.toString();
        }
        
        public static String getcurrentMMDDYYHHMMSSFormat(){
            Calendar cal = Calendar.getInstance();
            StringBuffer stbuf = new StringBuffer();
            
            if((cal.get(Calendar.MONTH)+1)<10)
                stbuf.append(0);
            stbuf.append(cal.get(Calendar.MONTH)+1).append("/");
            if(cal.get(Calendar.DATE)<10)
                stbuf.append(0);
            stbuf.append(cal.get(Calendar.DATE)).append("/");
            int ye =(cal.get(Calendar.YEAR)-2000)%100;
            if(ye<10)
                stbuf.append(0);
            stbuf.append(ye).append(" ");
            int temp = cal.get(Calendar.HOUR);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp).append(":");
            temp = cal.get(Calendar.MINUTE);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp).append(":");
            temp = cal.get(Calendar.SECOND);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp).append(" ");
            temp = cal.get(Calendar.AM_PM);
            if(temp == 0) //AM
                stbuf.append("AM:");
            else
                stbuf.append("PM:");
            cal = null;
            return stbuf.toString();
        }
        
        /**
         * Method to get Hour , Minutes in the 24 hours format
         *
         * @return - hour and minute will return in the String format
         */
        public static String getHHMM24HrsFormat(){
            Calendar cal = Calendar.getInstance();
            StringBuffer stbuf = new StringBuffer();
            int temp = cal.get(Calendar.HOUR_OF_DAY);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp);
            temp = cal.get(Calendar.MINUTE);
            if(temp<10)
                stbuf.append("0");
            stbuf.append(temp);
            cal = null;
            return stbuf.toString();
        }


        //CR 14330
        public static String getHHMM24HrsChatFormat(){
            Calendar cal = Calendar.getInstance();
            StringBuffer stbuf = new StringBuffer();
            int temp = cal.get(Calendar.HOUR_OF_DAY);
            if(temp<10)
                stbuf.append(0);
            stbuf.append(temp).append(":");
            temp = cal.get(Calendar.MINUTE);
            if(temp<10)
                stbuf.append("0");
            stbuf.append(temp);
            cal = null;
            return stbuf.toString();
        }

        public static String getHourMinuteSecond(){
            Calendar cal = Calendar.getInstance();
            Date d = new Date();
            cal.setTime(d);
            return cal.get(Calendar.SECOND)+":"+cal.get(Calendar.MILLISECOND);
        }
        
        /**
         * Method to Split the Text based on the given height and width
         *
         * @param text - Vaiable will conatin the text which is going to split
         * @param x - variable will contain the x position
         * @param width - variable will contain the Width to split
         * @param height - Variable will contain the height to split
         * @param font - Variable will contain the font size.
         *
         * @return - String Array will return after Splited the given text
         **/
        public static String[] splitText(String text,int x,int width,int height,Font font){

            byte pindex = -1;
            String[] value = new String[10];
            int xt = x;
            boolean isSpace = true;
            boolean isNewLine = false;
            noline = 0;
            text = remove(text, "\r");
            String[] messages = split(text, "\n");
            int count = messages.length;

            try{

            for(int i=0;i<count;i++){
                isNewLine = true;
                x =  xt;
                text = messages[i];
                if (font.stringWidth(text) > width){
                    int index = 0;
                    String temp;
                    while (text.length() > 0) {
                        index = text.indexOf(" ");
                        if(index>-1){
                            temp = text.substring(0,index);
                            isSpace = true;
                        } else {
                            temp = text.substring(0);
                            isSpace = false;
                        }
                        if((font.stringWidth(temp) + x) > width){
                            isNewLine = true;
                            if((font.stringWidth(temp)+xt)>width)
                            {
                                index = getWidthIndex(temp, width-x,font);
                                if(index < 8){
                                    index = getWidthIndex(temp, width-xt,font);
                                } else isNewLine = false;
                                if(temp.length() != index){
                                    isSpace = false;
                                    temp = temp.substring(0,index);
                                }
                            }
                        }
                        if(isNewLine){
                            x = xt;
                            pindex++;
                            height -= (font.getHeight() + 2);
                            if((height+2) <= 0 && noline==0)
                                noline = pindex;
                            isNewLine = false;
                        }

                        index = text.indexOf(temp);
                        index += temp.length();
                        if(isSpace)
                            text = text.substring(index+1);
                        else
                            text = text.substring(index);
                        x+= font.stringWidth(temp);
                        if(value.length<= pindex)
                            value = incrementStringArraySize(value, pindex, pindex+10);
                        if(null == value[pindex])
                            value[pindex] = temp;
                        else value[pindex] += temp;
                        if(isSpace){
                            value[pindex] +=' ';
                            x += font.charWidth(' ');
                        }
                    }
                } else {
                    if(isNewLine){
                        pindex++;
                        height -= font.getHeight() + 2;
                        if((height+2) <= 0 && noline==0)
                            noline = pindex;
                    }
                    if(pindex>=value.length){
                        value = incrementStringArraySize(value, pindex, pindex+10);
                    }
                    value[pindex] = text;
                }
            }
            }catch(Exception e){
                Logger.loggerError("Split text" + e.toString());
            }

            if(pindex>-1 || null != value[0]){
                pindex++;
                if(pindex<value.length){
                    value = incrementStringArraySize(value, pindex, pindex);
                }
            } else value = null;
            if(noline == 0)
                noline = pindex;
            pindex = 0;


            return value;
        }
         /**
          * Method to Increase the given Integer Array size
          *
          * @param temp - Variable will contain the array values
          * @param count - Variable will contain the number of values in the given array
          * @param length - Variable will contain the number of size want to increase
          *
          * @return temp - return the array values after increased to given size
          **/
         
        public static int[] incrementIntArraySize(int[] temp,int count,int length){
            int[] ttemp = temp;
            temp = new int[length];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }
        
         /**
          * Method to Increase the given String Array size
          *
          * @param temp - Variable will contain the array values
          * @param count - Variable will contain the number of values in the given array
          * @param length - Variable will contain the number of size want to increase
          *
          * @return temp - return the array values after increased to given size
          **/
        public static String[] incrementStringArraySize(String[] temp,int count,int length){
            String[] ttemp = temp;
            temp = new String[length];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }

        public static Image[] incrementImageArraySize(Image[] temp,int count,int length){
            Image[] ttemp = temp;
            temp = new Image[length];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }
        /**
          * Method to Increase the given Boolean Array size
          *
          * @param temp - Variable will contain the array values
          * @param count - Variable will contain the number of values in the given array
          * @param length - Variable will contain the number of size want to increase
          *
          * @return temp - return the array values after increased to given size
          **/
        public static boolean[] incrementBooleanArraySize(boolean[] temp,int count,int length){
            boolean[] ttemp = temp;
            temp = new boolean[length];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }
         /**
          * Method to Increase the given Byte Array size
          *
          * @param temp - Variable will contain the array values
          * @param count - Variable will contain the number of values in the given array
          * @param length - Variable will contain the number of size want to increase
          *
          * @return temp - return the array values after increased to given size
          **/
        public static byte[] incrementByteArraySize(byte[] temp, int count, int lenght){
            byte[] ttemp = temp;
            temp = new byte[lenght];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }

        public static int[][] incrementIntTwoArraySize(int[][] temp, int count, int lenght,int arraySize){
            int[][] ttemp = temp;
            temp = new int[lenght][arraySize];
            System.arraycopy(ttemp, 0, temp, 0, count);
            ttemp = null;
            return temp;
        }
        
        /**
         *  Method to get the number of characters to display in the given text in the given width
         *
         * @param text - Variable will contain the text
         * @param width - Variable will contain the width 
         * @param font - Variable willl contain the font size
         *
         * @return k - Variable will contain the count of characters
         */
        public static int getWidthIndex(String text,int width,Font font){
            int k = 0;
            if(width>0){
                if(font.stringWidth(text)<= width)
                    k = text.length();
                else {
                    while(text.length()> k && font.stringWidth(text.substring(0,k))<width)
                            k+=5;
                    if(text.length()<k)
                        k = text.length();
                    while(k>0 && font.stringWidth(text.substring(0,k)) > width)
                        k--;
                }
            }
            return k;
        }
        
        /**
         * Method to Insert the some special characters at Start and end positions of the Phone numbers and URL in the given text
         *
         * @param text - Variable will contain the text
         * @param isUrl - Variable to identify weather Url is available or not in the text
         * @paam isPhone - variable to identify weather Phone number is available or not in the text
         **/
        
        public static String markUrl(String text){
            
            if(null != text){
                //9272
                  text = replace(text, "\r\n", "\n");      //9272
                StringBuffer stbuf = new StringBuffer(text);
                try{
                    int len = 0;
                    String[] nMsg = split(text, "\n");
                    int nLen = nMsg.length;
                    int index =-1;
                    String[] sMsg = null;
                    int mLen = text.length();
                    int sIndex = -1;
                    boolean isEnd = false;
                    for(int i=nLen-1;i>-1;i--){
                        sMsg = split(nMsg[i], " ");
                        len = sMsg.length;
                        for (int j = len - 1; j > -1; j--) {
                            isEnd = false;
                            sMsg[j] = sMsg[j].toLowerCase();
                            if ((index = sMsg[j].indexOf("http://")) > -1) {
                                
                            } else if ((index = sMsg[j].indexOf("https://")) > -1) {
                                
                            } else if ((index = sMsg[j].indexOf("www.")) > -1) {
                                
                            } else if(sMsg[j].indexOf(".com")>-1 || sMsg[j].indexOf(".gov")>-1 || sMsg[j].indexOf(".edu")>-1 ||
                                    sMsg[j].indexOf(".biz")>-1 || sMsg[j].indexOf(".info")>-1 || sMsg[j].indexOf(".name")>-1 ||
                                    sMsg[j].indexOf(".org")>-1 || sMsg[j].indexOf(".pro")>-1 || sMsg[j].indexOf(".net")>-1 ||
                                    sMsg[j].indexOf(".us")>-1 || sMsg[j].indexOf(".in")>-1 || sMsg[j].indexOf(".mobi")>-1 || 
                                    sMsg[j].indexOf(".ca")>-1 || sMsg[j].indexOf(".co")>-1){ //CR 12726
                                index = 0;
                                isEnd = true;
                            } 
                            if(index>-1){
                                if(index == 0 && sMsg[j].indexOf("@",index)>-1){  //Bug 8611
                                    mLen -= (sMsg[j].length() + 1);
                                    continue;
                                } else if((sIndex = sMsg[j].indexOf("<",index))>-1 || (sIndex = sMsg[j].indexOf(">",index))>-1 ||
                                        (sIndex = sMsg[j].indexOf(",",index))>-1){
                                    if(index == 0){
                                        if(sIndex > 0) {
                                            if(isEnd){
                                                index = sIndex;
                                            } else {
                                                if(sIndex ==sMsg[j].length())
                                                    mLen--;
                                                else {
                                                    mLen -= sMsg[j].substring(sIndex).length();
                                                    sMsg[j] = sMsg[j].substring(0,sIndex);
                                                }
                                            }
                                        } else index = 1;
                                    } else if(sIndex>index){
                                        if(sIndex == sMsg[j].length())
                                            mLen--;
                                        else {
                                            mLen -= sMsg[j].substring(sIndex).length();
                                            sMsg[j] = sMsg[j].substring(0,sIndex);
                                        }
                                    }
                                } 
                                        
                                if(stbuf.charAt(mLen-1) == '\n' || stbuf.charAt(mLen-1) == '\r'){
                                    mLen--;
                                }
                                if(stbuf.charAt(mLen-1) == '.' || stbuf.charAt(mLen-1) == ',' || stbuf.charAt(mLen-1) == '?' ||
                                        stbuf.charAt(mLen-1) == '!' || stbuf.charAt(mLen-1) == '(' || stbuf.charAt(mLen-1) == ')' ||
                                        stbuf.charAt(mLen-1) == ';' || stbuf.charAt(mLen-1) == ':' || stbuf.charAt(mLen-1) == '\'' ||
                                        stbuf.charAt(mLen-1) == '”' || stbuf.charAt(mLen-1) == '-')
                                    stbuf.insert(mLen-1, "|>");
                                else stbuf.insert(mLen, "|>");
                                stbuf.insert(mLen - (sMsg[j].length() - index), "<|");
                            }
                            mLen -= (sMsg[j].length() + 1);
                        }
                        sMsg = null;
                    }
                    nMsg = null;
                }catch(Exception e){}
                return stbuf.toString();
            }
            return null;
        }
        
        private static int getNumdate(int nMonth){
            if(nMonth == Calendar.FEBRUARY){
                return 28;
            }
            else if(nMonth == Calendar.APRIL || nMonth == Calendar.NOVEMBER || nMonth == Calendar.SEPTEMBER || nMonth == Calendar.JUNE || nMonth == Calendar.DECEMBER)
                return 30;
            return 31;
        }
        
        /**
         * 
         * @param cD current Date
         * @param cM current Month
         * @param cY current Year
         * @param sD 
         * @param sM
         * @param sY
         * @return
         */
        public static int dateDiff(int cD,int cM,int cY,int sD,int sM,int sY){
            int diff = 0;
            if(sY == cY){
                if(sM == cM){
                    if(cD >sD)
                        return cD-sD;
                }else if(cM>sM){
                    diff = getNumdate(sM)-sD;
                    for(int i=0;i<(cM-sM);i++){
                       diff += getNumdate(sM+i);                       
                    }
                    return diff += cD;                    
                }
            }else if(cY > sY){
                diff = getNumdate(sM)- sD;
                for(int j=0;j<(cY-sY);j++){
                    for(int i=sM;i<12;i++)
                        diff += getNumdate(i);
                    sM = 0;
                }
                
                for(int j=0;j<cM;j++)
                    diff +=getNumdate(j);
                return diff+cD;
            }
            return diff;
        }      
        
        // Ithaya - v 2.01.11.02(DOODAD)
        public static String encryptEntryValue(String mask,String evalue){
            String temp =evalue;
            int len = evalue.length();
            int val=0;
            evalue = "";
            if(mask.compareTo("*") == 0){
                    for(int i=0;i<len;i++){
                        val = (int)temp.charAt(i);
                        if((val>96 && val<121) || (val>64 && val<89) || (val>47 &&val<56))
                            evalue += (char)(val+2);
                        else if(val == 121)
                            evalue += 'a';
                        else if(val == 122)
                            evalue += 'b';
                        else if(val == 89)
                            evalue += 'A';
                        else if(val == 90)
                            evalue += 'B';
                        else if(val == 56)
                            evalue += '0';
                        else if(val == 57)
                            evalue += '1';
                        else evalue += temp.charAt(i);
                    }
                } else {
                    for(int i=0;i<len;i++){
                        val = (int)temp.charAt(i);
                        if(mask.charAt(i) == '*'){
                            if((val>96 && val<121) || (val>64 && val<89) || (val>47 &&val<56))
                                evalue += (char)(val+2);
                            else if(val == 121)
                                evalue += 'a';
                            else if(val == 122)
                                evalue += 'b';
                            else if(val == 89)
                                evalue += 'A';
                            else if(val == 90)
                                evalue += 'B';
                            else if(val == 56)
                                evalue += '0';
                            else if(val == 57)
                                evalue += '1';
                            else evalue += temp.charAt(i);
                        } else evalue += (char)val;
                    }
                }
            return evalue;
        }
        
        public static String getformatedCallNumber(String call){
            if(null != call){
                call = call.replace('-', ' ');
                call = call.replace(')', ' ');
                call = call.replace('(', ' ');
                call = replace(call, " ","");
            }
            return call;
        }
        
        public static String getFormatedURlString(String url){
            if(null != url){
                if(!url.startsWith("http://"))
                    url ="http://"+url;
            }
            return url;
        }

        public static String getLGManufature(){
            if(UISettings.GENERIC){
                String orginalValue = System.getProperty("microedition.platform");
                if(null != orginalValue && orginalValue.toLowerCase().indexOf("lg")>-1)
                    return "lg";
            }
            return "";
        }

        public static String getManufacture(){ //CR 8440
            String orginalValue = null;
            String value = null;
            try{
                orginalValue = System.getProperty("device.model");
                value =  orginalValue;
                if(null == orginalValue || (orginalValue.indexOf("MIDP")<0 && orginalValue.toLowerCase().indexOf("nokia")<0
                         && orginalValue.toLowerCase().indexOf("motorola")<0 && orginalValue.toLowerCase().indexOf("samsung")<0
                         && orginalValue.toLowerCase().indexOf("sony")<0 && orginalValue.toLowerCase().indexOf("lg")<0)){
                    orginalValue = System.getProperty("microedition.platform");
                    if(null == orginalValue || (orginalValue.indexOf("MIDP")<0 && orginalValue.toLowerCase().indexOf("nokia")<0
                         && orginalValue.toLowerCase().indexOf("motorola")<0 && orginalValue.toLowerCase().indexOf("samsung")<0
                         && orginalValue.toLowerCase().indexOf("sony")<0 && orginalValue.toLowerCase().indexOf("lg")<0)){
                        orginalValue = System.getProperty("USER_AGENT");
                        if(null == orginalValue || (orginalValue.indexOf("MIDP")<0 && orginalValue.toLowerCase().indexOf("nokia")<0
                         && orginalValue.toLowerCase().indexOf("motorola")<0 && orginalValue.toLowerCase().indexOf("samsung")<0
                         && orginalValue.toLowerCase().indexOf("sony")<0 && orginalValue.toLowerCase().indexOf("lg")<0)){
                            orginalValue = value;
                        }
                    }
                } 
            }catch(Exception e){}


//            try{
//               orginalValue = System.getProperty("microedition.platform");
//               if(orginalValue.toLowerCase().compareTo("j2me") == 0){
//                   orginalValue = "SAMSUNG";
//                    if(null != (value = System.getProperty("USER_AGENT"))){
//                        orginalValue = value;
//                    }
//               }
//            }catch(Exception e){
//                if(null == orginalValue){
//                    try{
//                        orginalValue = System.getProperty("device.model");
//                    }catch(Exception ex){}
//                }
//            }
            if(null != orginalValue){
                int index;
                if((index=orginalValue.indexOf("/"))>-1){
                    orginalValue = orginalValue.substring(0,index);
                }
            } else orginalValue = Constants.appendText[24];
            return orginalValue;
        }


        public static boolean isSendTimeout(long sendStartTime){
            if(sendStartTime>0){
                Calendar calendar = Calendar.getInstance();
                long currentTime = calendar.getTime().getTime();
                currentTime = currentTime - sendStartTime;
                if(currentTime>60000)
                    return true;
            }
        return false;
    }

    public static String ConvertASCIIValue(String value) {
//        if(UISettings.TEXT_DIRECTION_SYMBOL == 1){
            char[] convertvalues = value.toCharArray();
            int count = convertvalues.length;
            char characte;
            for (int i = 0; i < count; i++) {
                characte = convertvalues[i];
                if (characte > 191 && characte < 198) {
                    characte = 'A';
                } else if (characte == 199) {
                    characte = 'C';
                } else if (characte > 199 && characte < 204) {
                    characte = 'E';
                } else if (characte > 203 && characte < 208) {
                    characte = 'I';
                } else if (characte == 209) {
                    characte = 'N';
                } else if (characte > 209 && characte < 215 || characte == 216) {
                    characte = 'O';
                } else if (characte > 216 && characte < 221) {
                    characte = 'U';
                } else if (characte == 221) {
                    characte = 'Y';
                } else if (characte > 223 && characte < 231) {
                    characte = 'a';
                } else if (characte == 231) {
                    characte = 'c';
                } else if (characte > 231 && characte < 236) {
                    characte = 'e';
                } else if (characte > 235 && characte < 240) {
                    characte = 'i';
                } else if (characte == 241) {
                    characte = 'n';
                } else if (characte > 241 && characte < 247 || characte == 248) {
                    characte = 'o';
                } else if (characte > 248 && characte < 252) {
                    characte = 'u';
                } else if (characte == 252 || characte == 255) {
                    characte = 'y';
                } else if (characte < 32 && characte != 9 && characte != 10 && characte != 13) {
                    characte = '?';
                } else if (characte > 126 && characte != 130 && characte != 132 && characte != 136
                        && characte != 145 && characte != 146 && characte != 147
                        && characte != 148 && characte != 152 && characte != 166 && characte != 168) {
                    characte = '?';
                }
                convertvalues[i] = characte;
            }
            value = new String(convertvalues);
//        }
        return value;
    }



    public static void setCurrentTime(){
        Calendar aCalendar = Calendar.getInstance();
        currentTime = aCalendar.getTime().getTime();
        aCalendar = null;
    }

    public static void updateMessage(String msg){// CR 11703
        Calendar aCalendar = Calendar.getInstance();
        long timeDuration = aCalendar.getTime().getTime() - currentTime;
        aCalendar = null;
        long temp;
        if (timeDuration / 1000 < 1) {
            temp = timeDuration;
            logging += msg +" "+ temp +" Milisec"+"\n";
        } else {
            float temp1 = (float)timeDuration / 1000;
            logging += msg +" "+ temp1 +" sec"+"\n";
        }

    }

    public static String getLogginMessage(){
        return logging;
    }

    public static void resetMessage(){
        logging = "";
    }

//    public static long getRoundTripTime(long startedTime){
//        Calendar ftime = Calendar.getInstance();
//        Calendar curtime = Calendar.getInstance();
//        Date d = new Date();
//        d.setTime(startedTime);
//        ftime.setTime(d);
//        startedTime = ((curtime.get(Calendar.MINUTE)*60) + curtime.get(Calendar.SECOND))-
//                ((ftime.get(Calendar.MINUTE)*60) + ftime.get(Calendar.SECOND));
//        if(startedTime>999)
//                startedTime=999;
//        return startedTime;
//    }
//

    public static long getRoundTripTime(long startedTime){
        Date date = new Date();
        date.setTime(startedTime);
        Calendar curtime = Calendar.getInstance();
        startedTime = curtime.getTime().getTime()- startedTime;
        startedTime = (startedTime/1000) +(startedTime/(60*1000)) +(startedTime/(60*60*1000));//+(startedTime/(24*60*60*1000));
//        startedTime = ((curtime.get(Calendar.MINUTE)*60)+curtime.get(Calendar.SECOND));
//        curtime.setTime(date);
//        startedTime -= ((curtime.get(Calendar.MINUTE)*60)+curtime.get(Calendar.SECOND));
        if(startedTime>999||startedTime<0)
            startedTime = 999;


        curtime = null;
        date = null;
        return startedTime;
    }


    public static String encode(String s) throws Exception {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(bOut);
        StringBuffer ret = new StringBuffer(); //return value
        dOut.writeUTF(s);
        ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
        bIn.read();
        bIn.read();
        int c = bIn.read();
        while (c >= 0) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') 
                    || c == '.' || c == '-' || c == '*' || c == '_' || c == '+') {
                ret.append((char) c);
            } else if (c == ' ') {
                ret.append("%20");
            } else {
                if (c < 128) {
                    appendHex(c, ret);
                } else if (c < 224) {
                    appendHex(c, ret);
                    appendHex(bIn.read(), ret);
                } else if (c < 240) {
                    appendHex(c, ret);
                    appendHex(bIn.read(), ret);
                    appendHex(bIn.read(), ret);
                }
            }
            c = bIn.read();
        }
        return ret.toString();
    }

    private static void appendHex(int arg0, StringBuffer buff) {
        buff.append('%');
        if (arg0 < 16) {
            buff.append('0');
        }
        buff.append(Integer.toHexString(arg0));
    }


    public static String convertLongToChatDate(long value){
        String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sep","Oct","Nov","Dec"};
        String returnValue = value+"";
        value = Integer.parseInt(returnValue.substring(4,6));
        returnValue = returnValue.substring(6)+" "+months[(int)value-1]+" "+returnValue.substring(0,4);
        return returnValue;
    }

    public static boolean isCameraVideoSupport() {
        String encodings = System.getProperty("video.snapshot.encodings");
        return (encodings != null) && ((encodings.indexOf("png") != -1) || (encodings.indexOf("image/bmp") != -1));
    }

    public static void saveProfilePicture(ByteArrayInputStream byteArrayInputStream, Image imageFile,
            String fileName){
            String location = System.getProperty("fileconn.dir.photos");
            if(null == location){
                location = "file:///c://sh/";
            }
            FileConnection fileConnection = null;
            if(null != location){
                try{
                    JPGEncoder encoder = new JPGEncoder();
                    if(null != byteArrayInputStream){
                        imageFile =  ImageHelper.createThumbnail(byteArrayInputStream, 32, 32, false);
                        byteArrayInputStream =  new ByteArrayInputStream(encoder.encode(imageFile, 100));
                    } else {
                        imageFile = ImageHelper.scale(imageFile, 32, 32, false, false);
                        byteArrayInputStream =  new ByteArrayInputStream(encoder.encode(imageFile, 100));
                    }
                    encoder.freeEncoder();
                    encoder = null;

                    location += fileName +".jpeg";
                    fileConnection = (FileConnection)Connector.open(location);
                    if(fileConnection.exists()){
                        fileConnection.delete();
                    }
                    fileConnection.create();
                    DataOutputStream dataOutputStream = fileConnection.openDataOutputStream();
                    int index = (int)Runtime.getRuntime().freeMemory()/2;
                    byte[] readByte = new byte[index];
                    while((index= byteArrayInputStream.read(readByte)) != -1){
                        dataOutputStream.write(readByte, 0,index);
                    }
                    readByte = null;
                    dataOutputStream.close();
                    dataOutputStream = null;
                } catch(SecurityException securityException){
                    location = null;
                    Logger.loggerError("Utilities->saveProfilePicture->Security"+securityException.toString());
                } catch(Exception exception){
                    location = null;
                    Logger.loggerError("Utilities->saveProfilePicture->"+exception.toString());
                }
                try{
                    if(null != fileConnection){
                        fileConnection.close();
                        fileConnection = null;
                    }
                }catch(Exception exception){}
            }
    }


    //CR 14423
    public static String saveImage_Audio(ByteArrayInputStream byteArrayInputStream, String imageId,
            int fileFormat, String extension){
        String location = null;
        if(fileFormat == -1){ //Image
            location = System.getProperty("fileconn.dir.photos");
        } else if(fileFormat == 0){ //audio
            location = System.getProperty("fileconn.dir.recordings");
        } else { //Video
            location = System.getProperty("fileconn.dir.videos");
        }

        if(null == location){
            location = "file:///c://sh/";
        }
        
        FileConnection fileConnection = null;  
        if(null != location){
            try{
                location += imageId +"."+extension;
                Logger.debugOnError("storage location:"+location );
                fileConnection = (FileConnection)Connector.open(location);
                if(fileConnection.exists()){
                    fileConnection.delete();
                }
                fileConnection.create();
                DataOutputStream dataOutputStream = fileConnection.openDataOutputStream();
                int index = (int)Runtime.getRuntime().freeMemory()/2;
                byte[] readByte = new byte[index];
                while((index= byteArrayInputStream.read(readByte)) != -1){
                    dataOutputStream.write(readByte, 0,index);
                }
                readByte = null;
                Logger.debugOnError("saved in gallery file size="+fileConnection.fileSize());
                dataOutputStream.close();
                dataOutputStream = null;
            } catch(SecurityException securityException){
                location = null;
                Logger.loggerError("Utilities->SaveImage->Security"+securityException.toString());
            } catch(Exception exception){
                location = null;
                Logger.loggerError("Utilities->SaveImage->"+exception.toString());
            }
            try{
                if(null != fileConnection){
                    fileConnection.close();
                    fileConnection = null;
                }
            }catch(Exception exception){}
        }
        return location;
    }

    //Cr 14465
    public static String getElapsedTime(long time){
        String value = "";
        if(time>0){
            time = (Calendar.getInstance().getTime().getTime()-time);
            time = (20-(time/1000));
            if(time>1){
                value = time+"secs";
            } else if(time<0)
                value = 0+"sec";
            else
                value = time+"sec";
        }
        return value;
    }

    public static boolean isVideoCapture(){
        boolean isSupport = false;
        try{
            String[] value = Manager.getSupportedContentTypes("capture://video");
            if(null != value){
                isSupport = true;
            } 
        }catch(Exception exception){

        }
        return isSupport;
    }

    public static String[] getImageGalleryNames(){
         
        String[] nameList = null;
        try {
           Vector name = new Vector();
           path = new Vector();
            String photoLocation = System.getProperty("fileconn.dir.photos");
            if (null != photoLocation && photoLocation.length() > 0) {
                loadImages(photoLocation, name, path);
            }

            String external = System.getProperty("fileconn.dir.roots.external");
            String location = photoLocation;
            int index = -1;
            if (null != external) {
                String[] privates = Utilities.split(external, "\n");
                int count = privates.length;
                String externalLocation = null;
                for (int i = 0; i < count; i++) {
                    if (location.indexOf(privates[i]) == 8) {
                        externalLocation = privates[i];
                        break;
                    }
                }

                if (null == externalLocation) {
                    externalLocation = location.substring(8);
                    index = externalLocation.indexOf(":/");
                    if (index > -1) {
                        externalLocation.substring(index + 2);
                    }
                    index = externalLocation.indexOf("/");
                    if (index > -1) {
                        externalLocation = externalLocation.substring(0, index + 1);
                    }
                }
                if (null != externalLocation) {
                    for (int i = 0; i < count; i++) {
                        if (externalLocation.compareTo(privates[i]) != 0) {
                            photoLocation = Utilities.replace(location, externalLocation, privates[i]);
                            loadImages(photoLocation, name, path);
                        }
                    }
                }
            }

            photoLocation = System.getProperty("fileconn.dir.memorycard");
            if (null != photoLocation) {
                if (location.indexOf(photoLocation) != 0) {
                    location = location.substring(7);
                    index = location.indexOf(":/");
                    if (index > -1) {
                        location = location.substring(index + 2);
                    }
                    location = photoLocation + location;
                    loadImages(location, name, path);
                }
                photoLocation += "Images/";
                loadImages(photoLocation, name, path);
            }



            //For Laptop testing location(Only for Emulator)
            if (name.size() == 0) {
                photoLocation = "file:///c:/photos/";
                loadImages(photoLocation, name, path);
            }

            if (name.size() > 0) {
                nameList = new String[name.size()];
                name.copyInto(nameList);
                name.removeAllElements();
                name = null;
            }
        } catch (Exception exception) {
            Logger.loggerError("Utilities->getImageGalleryNames->" + exception.toString() + "\n" + exception.getMessage());
        }
        return nameList;
    }

    public static String[] getImageGalleryPath(){
        String[] paths = null;
        if(null != path && path.size()>0){
             paths = new String[path.size()];
             path.copyInto(paths);
             path.removeAllElements();
             path = null;
        }
         return paths;
    }

    private static void loadImages(String floc, Vector name, Vector imagePath) {
        try {
            Logger.debugOnError("Folder Locations->" + floc);
            FileConnection fcon = (FileConnection) Connector.open(floc, Connector.READ);
            Enumeration enumeration = fcon.list();
            FileConnection newcon = null;
            StringBuffer stBuffer = null;
            String imageName = null;
            while (enumeration.hasMoreElements()) {
                imageName = (String) enumeration.nextElement();
                stBuffer = new StringBuffer(floc).append(imageName);
                newcon = (FileConnection) Connector.open(stBuffer.toString(), Connector.READ);
                if (newcon.isDirectory()) {
                    loadImages(stBuffer.toString(), name, imagePath);
                } else {
                    if (imageName.toLowerCase().indexOf(".png") == imageName.length() - 4
                            || imageName.toLowerCase().indexOf(".jpg") == imageName.length() - 4
                            || imageName.toLowerCase().indexOf(".jpeg") == imageName.length() - 5) {
                        name.addElement(imageName);
                        imagePath.addElement(stBuffer.toString());
                    }
                }
                newcon.close();
                newcon = null;
            }
            enumeration = null;
            fcon.close();
            fcon = null;
        } catch (Exception exception) {
            Logger.loggerError("Utilities->loadImages->"+floc+" "+exception.toString());
        }
    }

    public static void saveProfileImage(){
        
    }

    public static boolean isImageCaptureSupport(){
        String capture = System.getProperty("supports.video.capture"); //Cr 14418
        if(null != capture && capture.toLowerCase().compareTo("true") == 0){
            return true;
        }
        return false;
    }

//    public static String replaceSquerBrackets(String value){
//        int index = -1, sIndex = -1;
//        while((index = value.indexOf("["))>-1){
//                if((sIndex = value.indexOf("]",index))>-1){
//                    orgValue = value.substring(index+1,sIndex);
//                    value = value.substring(0,index)+value.substring(sIndex+1);
//                    if(!isRecord(orgValue) && orgValue.indexOf(":")>-1){
//                        tempvalue = orgValue;
//                        orgValue = orgValue.replace(':', '.');
//                        qFormat = qFormat.replaceAll(tempvalue, orgValue);
//                    }
//                } else {
//                    return value
//                }
//            }
//    }
}
