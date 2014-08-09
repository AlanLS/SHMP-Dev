
/**
 *
 *
 * @author - Administrator
 * @Version - v1.00.15
 */




public class ObjectBuilderFactory {
        
        // Object 
	private static IKernel iKernel = null;

	private static Shorthand program = null;
        
        private static ControlChannel cChanel =null;

        private static ShortHandCanvas pCanvas = null;
        
        private static IKeyHandler keyHandler = null;

        public static IKeyHandler getKeyHandler(){
            if(null == keyHandler){
                //#if KEYPAD
                //|JG|if(ChannelData.isQwertyKeypad())
                //|JG|    keyHandler = new QWERTYHandler();
                //|JG|else
                //#endif
                    keyHandler = new DTMFHandler();
            }
            return keyHandler;
        }
        
        public static ShortHandCanvas getPCanvas(){
            synchronized(pCanvas){
                return pCanvas;
            }
        }

        public static void setProgramCanvas(Shorthand sHand, ShortHandCanvas canv){
            program = sHand;
            pCanvas = canv;
            //CR 13294
            RecordManager.deleteOldRMS(GetKernel().getVersionNumber(true));
            Settings.setSetting();
            Logger.initializeLogger();
        }

	/**
	 * 
	 * @return
	 */
	public static IKernel GetKernel() {
            if (null == iKernel) {
                iKernel = new Kernel();
            }
            synchronized(iKernel){
                return iKernel;
            }
	}
        
	/**
	 * 
	 * @return
	 */
	public static Shorthand GetProgram() {
            return program;
	}

                /**
         *
         **/
        public static ControlChannel getControlChanel(){
            if(null == cChanel)
                cChanel = new ControlChannel();
            synchronized(cChanel){
                return cChanel;
            }
        }
}
