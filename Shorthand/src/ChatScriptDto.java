/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public class ChatScriptDto {

        private String script = null;

        private byte status = 0;

        private long date = 0;

        private byte direction = 0;

        //bug 14557
        private long chatSequence = -1;

        private byte markedLine = 0;

        private byte totoalElements = 0;

        private String chatSequn = null;

        private int currentRecordPosition = 0;

        //CR 14423
        private String fileLocation = null;

        public int getCurrentRecordPosition() {
            return currentRecordPosition;
        }

        public void setCurrentRecordPosition(int currentRecordPosition) {
            this.currentRecordPosition = currentRecordPosition;
        }

        public String getFileLocation() {
            return fileLocation;
        }

        public void setFileLocation(String fileLocation) {
            this.fileLocation = fileLocation;
        }


        public String getChatSequn() {
            return chatSequn;
        }

        public void setChatSequn(String chatSequn) {
            this.chatSequn = chatSequn;
        }

        public byte getTotoalElements() {
            return totoalElements;
        }

        public void setTotoalElements(byte totoalElements) {
            this.totoalElements = totoalElements;
        }

    public byte getMarkedLine() {
        return markedLine;
    }

    public void setMarkedLine(byte markedLine) {
        this.markedLine = markedLine;
    }

        

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public long getDate() {
        return date;
    }


    public void setDate(long date) {
        this.date = date;
    }

    public long getChatSequence() {
        return chatSequence;
    }

    public void setChatSequence(long chatSequence) {
        this.chatSequence = chatSequence;
    }

    

}
