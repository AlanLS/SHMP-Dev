public class CallAction {  
    
    // Variable to hold the Call Action Id
    private int id;
    
    // Variable to hold the phone Number
    private String callNum;
    
    // Variable to hold the Next Action Id
    private int goId;

    // Method to Set and Get the Call Action Id
   
    public void setId(int id) {
        this.id = id;
    }
     
    public int getId() {
        return id;
    }

   // Method to Set and Get the Phone Number

    public void setCallNum(String callNum) {
        this.callNum = callNum;
    }
     
    public String getCallNum() {
        return callNum;
    }

    // method to Set and Get the Next Action Id
    
    public void setGoId(int goId) {
        this.goId = goId;
    }
    
    public int getGoId() {
        return goId;
    }   
}
