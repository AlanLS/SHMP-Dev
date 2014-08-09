/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hakuna
 */
public class DownloadAction {
    
    private int gotiId;
    
    private String type;
    
    private String wId;
    
     private int id; 
    
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }
    
    public void setGotoId(int gtID){
        this.gotiId = gtID;
    }
    public int getGotoId(){
        return gotiId;
    }
    
    public void setwId(String wid){
        this.wId = wid;
    }
    public String getwId(){
        return wId;
    }
    
     public void setActionId(int id){
        this.id = id;
    }
    public int getActionId(){
        return id;
    }

}
