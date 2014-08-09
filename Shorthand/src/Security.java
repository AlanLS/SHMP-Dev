/**
 * Security Class to hold , read and write the Basic Application Settings.
 *
 * @author  - HakunaMatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough
 *
 **/

public class Security {

	/**
         * Variable will maintain the State of the Message
	 *  1 - Error state(Exception Raised)
	 */
	private byte msgst;
	
        // Variable to maintain the Previous Screen State
        private byte preState = -1;
        
	/**
	 * Method to Load the Screen to get the input from the User	 
	 * 
	 */ //CR 10682
//	public void loadPinNumber() {
//                ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_ENTRY);
//		PresenterDTO.setLOptByte((byte)-1);
//                PresenterDTO.setHdrtxt(Constants.aName);
//                PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
//                PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
//		GetEntryResponseDTO _responseDto = new GetEntryResponseDTO();
//		_responseDto.setSecondaryHeaderText(Constants.headerText[4]);
//		_responseDto.setMask("****");
//		_responseDto.setEntryType((byte) 0);
//		_responseDto.setMaxChar((byte)4);     // Fixed ShartHand Pin Number Maximum Length
//		_responseDto.setMinChar((byte)4);     // Fixed ShartHand Pin Number Minimum Length
//		_responseDto.setMaxValue(9999);
//                ObjectBuilderFactory.GetKernel().displayScreen(_responseDto,true); // Control transfer to Entry Screen DTO
//		_responseDto = null;
//	}

	/**
	 * Method to check the Pin Number qweather match or mismatch
         *	 
	 */
//	public void handleItemSelection(String pinNumber) {
//		if (Settings.getPinNumber()
//				.equals(pinNumber)) {
//			ObjectBuilderFactory.GetKernel().loadAppication();
//		} else {
//			loadMessageBox(0, Constants.popupMessage[12],0);
//		}
//	}

	/**
	 *  Method to Load the Error Message with Exception and User error information
         *
	 * @param exp - Variable will contain the Syatem Exception
         * @param msg - Variable will contain the User Error Information
	 */
//	public void loadErrorMessageScreen(Exception exp,String msg) {
//            Logger.loggerError("Security Error "+exp.toString());
//            loadMessageBox(4, msg,1);
//	}

	/**
	 * Method to Load the MessageBox
         *
	 * @param msgType - Variable will contain the Message Type(Not Null)
	 * @param msg - Variable will contain the Message Text(Not Null)
	 * @param msgst - Variable will contain the Message State.(Not Null)
	 * 
	 **/
        
//	private void loadMessageBox(int msgType,String msg,int msgst){
//            this.msgst = (byte)msgst;   // Based on the State message response will handle
//            ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg,null);
//	}

        /**
         * Method to Load the Server Message if the User not authenticated.
         *
         * @param pState - Variable will contain the Previous State
         **/
        
        public void loadApplicationMessage(byte pState){
            if(pState>-1 || pState == -2)
                preState = pState;
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_VIEW);
            ViewSmsResponseDTO _responseDto = new ViewSmsResponseDTO();
            if(!Settings.isIsAppEnable())
                _responseDto.setLeftOptionText((byte)5); //Option  - Index for Exit ShartHand
            else
                _responseDto.setLeftOptionText((byte)40); //Option - Index for Back Option
            _responseDto.setMessage(Settings.getAppE_DMsg());
            _responseDto.setMesssageId(Constants.appendText[6]);
            _responseDto.setSenderName(Constants.aName);
            PresenterDTO.setLOptByte((byte)-1);
            PresenterDTO.setHdrtxt(Constants.aName);
            ObjectBuilderFactory.GetKernel().displayScreen(_responseDto, true);
            _responseDto = null;
        }
        
	/**
	 * Method to handle MessageBox response
         *
         * @param status 
	 **/
        
//	public void handleMessageBoxSelection(boolean status){
//            if(status){
//                if(1 == msgst){         // Error or Exception State
//                    ObjectBuilderFactory.GetKernel().reLaunchApplication();
//                }
//            }
//	}
        
        /**
         * Method to take the different type operations based on the selected options
         *
         * @param optByte - Variable will contain the Selected Option.
         **/
        public void handleOptionSelected(byte optByte){
            if(optByte == 5) {                                       //Exit ShartHand
                ObjectBuilderFactory.GetKernel().removeAllProfiles();
                ObjectBuilderFactory.GetKernel().unLoad();
            }
//            else if(optByte == 40) {                               //Back Option
//                if(preState == -2 && Settings.getIsPinEnabled() && null != Settings.getPinNumber()){
//                    loadPinNumber();
//                } else {
//                    byte pState = preState;
//                    if(pState < 0)
//                        pState = 0;
//                    preState =-1;
//                    ObjectBuilderFactory.GetKernel().setPreviousBkState(pState);
//                    ObjectBuilderFactory.GetKernel().deInitializeHandlers(KernelConstants.BACKEND_SECURITY);
//                }
//            }
        }

	/**
	 * Method to get the Application Basic Settings
         *
	 * @return settings - Object will contain the Basic Application Settings
         *
         * @throws getSettingsException
	 * 
	 */
        
        /**
     * Method to Store the Currently Send and Receive Message Count
     * in this file Maintaining the Send Message Count and Receive Messaeg count
     *  Each Month to be Maintian the Seperately in the same file
     * 
     * @param rMsgcount Receive Message Count
     * 
     * @param sMsgCount Send Message Count
     */
    
}