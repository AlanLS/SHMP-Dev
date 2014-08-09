import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

/**
 * Image handling functions set
 * @author Sasi
 *
 */
public class ImageHelper {

//        public static Image scale(InputStream inpuStream, int maxX, int maxY) throws IOException{
//            return scale(Image.createImage(inpuStream), maxX, maxY, false,false);
//        }

        public static Image createThumbnail(InputStream inpuStream, int maxX, int maxY, boolean isSquare) throws IOException{
            if(null != inpuStream){
                Logger.debugOnError("CreateThumnnail With inputStream "+maxX+","+maxY  );
                try{
                    return scale(Image.createImage(inpuStream), maxX, maxY, false, isSquare);
                }catch(OutOfMemoryError outOfMemoryError){
                    Logger.debugOnError("out of mem try to get Thumb");
                    //bug 14771
                    return getThumbnailFromStream(inpuStream, inpuStream.available());
                }
            }
            return  null;
        }

        public static Image createThumbnail(InputStream inpuStream, int maxX, int maxY) throws IOException{
            if(null != inpuStream){
                Logger.debugOnError("CreateThumnnail With inputStream with boolean");
                try{
                    return scale(Image.createImage(inpuStream), maxX, maxY,false,false);
                }catch(OutOfMemoryError outOfMemoryError){
                    //bug 14771
                    return getThumbnailFromStream(inpuStream, inpuStream.available());
                }
            }
            return  null;
        }
//
//        public static Image scale(byte[] images, int maxX, int maxY) throws IOException{
//            return scale(Image.createImage(images,0,images.length), maxX, maxY, false, false);
//        }



private static Image getThumbnailFromStream(InputStream str, long fileSize)
{
    int STOP_AT_BYTE = 8192;   //how far to search for thumbnail
    int THUMB_MAX_SIZE = 16284;
    byte[] tempByteArray = new byte[THUMB_MAX_SIZE]; // how big can a thumb get.
    byte[] bytefileReader = new byte[1]; // lazy byte reader
    byte firstByte,secondByte = 0;
    int currentIndex = 0;

    int currByte = 0;

    try {
        str.read(bytefileReader);
        firstByte = bytefileReader[0];
        str.read(bytefileReader);
        secondByte = bytefileReader[0];

        currByte += 2;

        if ((firstByte & 0xFF) == 0xFF && (secondByte & 0xFF) == 0xD8) {    //if this is JPEG
            byte rByte = 0;
            do {
                while (rByte != -1 && currByte < fileSize) {
                    str.read(bytefileReader);
                    rByte = bytefileReader[0];
                    currByte++;
                }

                str.read(bytefileReader);
                rByte = bytefileReader[0];
                currByte++;

                if (currByte > STOP_AT_BYTE) {
                    return null;
                }
            } while ((rByte & 0xFF) != 0xD8 && currByte < fileSize); // thumb starts

            if (currByte >= fileSize) {
                return null;
            }

            tempByteArray[currentIndex++] = -1;
            tempByteArray[currentIndex++] = rByte;
            rByte = 0;

            do {
                while (rByte != -1){
                    str.read(bytefileReader);
                    rByte = bytefileReader[0];
                    tempByteArray[currentIndex++] = rByte;
                }

                str.read(bytefileReader);
                rByte = bytefileReader[0];
                tempByteArray[currentIndex++] = rByte;
            } while ((rByte & 0xFF) != 0xD9); // thumb ends

            tempByteArray[currentIndex++] = -1;
            Image image = Image.createImage(tempByteArray, 0, currentIndex-1);
            tempByteArray = null;
            return image;
        }
    } catch (Exception exception) {
        //error
        Logger.debugOnError("ImageHelper->getThumnail->"+exception.toString());
    } catch(OutOfMemoryError outOfMemoryError){
        Logger.debugOnError("ImageHelper->getThumnail->"+outOfMemoryError.toString());
    }

    return null;
}

	/**
	 * Image scale function (does not preserve original image aspect ration)
	 * @param imagePath the original image path
	 * @param maxX the max width
	 * @param maxY the max height
	 * @return scaled image
	 */
//	public static Image scale(String imagePath, int maxX, int maxY) throws IOException{
//		return scale(Image.createImage(imagePath), maxX, maxY, false, false);
//	}
	
	/**
	 * Image scale function (does not preserve original image aspect ration)
	 * @param inputImage the original image
	 * @param maxX the max width
	 * @param maxY the max height
	 * @return scaled image
	 */
//	public static Image scale(Image inputImage, int maxX, int maxY) {
//		return scale(Image.createImage(inputImage), maxX, maxY, false, false);
//	}
	
	/**
	 * Image scale function
	 * @param imagePath the original image path
	 * @param maxX the max width
	 * @param maxY the max height
	 * @param preserveAspectRatio true to preserve the original picture aspect ratio
	 * @return scaled image
	 */
//	public static Image scale(String imagePath, int maxX, int maxY, boolean preserveAspectRatio) throws IOException{
//		return scale(Image.createImage(imagePath), maxX, maxY, preserveAspectRatio, false);
//	}

//        private static Image createThumbnail(Image image,int thumbWidth, int thumbHeight) {
//            int sourceWidth = image.getWidth();
//            int sourceHeight = image.getHeight();
//            Logger.debugOnError("CreateThumnnail With Image "+sourceWidth + "X"
//                    + sourceHeight + " " + thumbWidth + "X" + thumbHeight);
//            if(sourceHeight<=thumbHeight && sourceWidth<=thumbWidth)
//                return image;
//
//            if(sourceHeight<thumbHeight)
//                thumbHeight = sourceHeight;
//            if(sourceWidth<thumbWidth)
//                thumbWidth = sourceWidth;
//
//        Image tmp = Image.createImage(thumbWidth, thumbHeight);
//        Graphics g = tmp.getGraphics();
//        int ratio = (sourceWidth << 16) / thumbWidth;
//        int pos = ratio / 2;
//        //Horizontal Resize
//
//        for (int index = 0; index < thumbWidth; index++) {
//            g.setClip(index, 0, 1, sourceWidth);
//            g.drawImage(image, index - (pos >> 16), 0, Graphics.LEFT|Graphics.TOP);
//            pos += ratio;
//        }
//
//        Image resizedImage = Image.createImage(thumbHeight, sourceHeight);
//        g = resizedImage.getGraphics();
//        ratio = (sourceHeight << 16) / thumbHeight;
//        pos = ratio / 2;
//
//        //Vertical resize
//
//        for (int index = 0; index < thumbHeight; index++) {
//            g.setClip(0, index, sourceHeight, 1);
//            g.drawImage(tmp, 0, index - (pos >> 16), Graphics.LEFT|Graphics.TOP);
//            pos += ratio;
//        }
//        return resizedImage;
//
//
////            Image thumb = Image.createImage(thumbWidth, thumbHeight);
////            Graphics g = thumb.getGraphics();
////            for (int y = 0; y < thumbHeight; y++) {
////                for (int x = 0; x < thumbWidth; x++) {
////                    g.setClip(x, y, 1, 1);
////                    int dx = x * sourceWidth / thumbWidth;
////                    int dy = y * sourceHeight / thumbHeight;
////                    g.drawImage(image, x - dx, y - dy,
////                            Graphics.LEFT | Graphics.TOP);
////                }
////            }
////            image = Image.createImage(thumb);
////            thumb = null;
////            Runtime.getRuntime().gc();
////            return image;
//        }
//
	/**
	 * Image scale function
	 * @param inputImage the original image
	 * @param maxX the max width
	 * @param maxY the max height
	 * @param preserveAspectRatio true to preserve the original picture aspect ratio
	 * @return scaled image
	 */
	public static Image scale(Image inputImage, int maxX, int maxY, boolean preserveAspectRatio, boolean isSquare) {

		if (preserveAspectRatio){
			//Resize properly the splash screen image
			float aspectRatio = (float)inputImage.getWidth()/inputImage.getHeight();
			if (inputImage.getHeight()>maxY){
				maxX=(int)(maxY*aspectRatio);
			}
			if (inputImage.getWidth()>maxX){
				maxY=(int)(maxX/aspectRatio);
			}
		}

                Image temp2 = null;

                if(inputImage.getHeight()<=maxY && inputImage.getWidth()<=maxX){
                    if(isSquare){
                        maxX = inputImage.getWidth();
                        maxY = inputImage.getHeight();
                        temp2 =  inputImage;
                    } else {
                        return inputImage;
                    }
                } else {
                    if(inputImage.getHeight()<maxY)
                        maxY = inputImage.getHeight();
                    if(inputImage.getWidth()<maxX)
                        maxX = inputImage.getWidth();

                    int rgb[] = new int[inputImage.getWidth() * inputImage.getHeight()];
                    // Get the RGB array of image into "rgb"
                    inputImage.getRGB(
                                    rgb,
                                    0,
                                    inputImage.getWidth(),
                                    0,
                                    0,
                                    inputImage.getWidth(),
                                    inputImage.getHeight());

                    int rgb2[] =
                            rescalaArray(rgb, inputImage.getWidth(), inputImage.getHeight(), maxX, maxY);
    //		// Create an image with that RGB array
                     temp2 = Image.createRGBImage(rgb2, maxX, maxY, true);
                }
                if(isSquare){
                    if(maxX>maxY){
                        maxX = maxY;
                    } else {
                        maxY = maxX;
                    }
                    temp2 = Image.createImage(temp2, (temp2.getWidth()-maxX)/2, (temp2.getHeight()-maxY)/2 , maxX, maxY, 0);
                }
		return temp2;
	}
	
	private static int[] rescalaArray(
			int[] ini,
			int x,
			int y,
			int x2,
			int y2) {
		int out[] = new int[x2 * y2];
		for (int yy = 0; yy < y2; yy++) {
			int dy = yy * y / y2;
			for (int xx = 0; xx < x2; xx++) {
				int dx = xx * x / x2;
				out[(x2 * yy) + xx] = ini[(x * dy) + dx];
			}
		}
		return out;
	}



        private static int[] reescalaArray(Image temp, int x, int y, int x2, int y2) {
                int ini[] = new int[temp.getWidth()*temp.getHeight()];
                //Get the RGB array of image into "rgb"

                int i=0,t2;
                int dy,dx;
                temp.getRGB(ini,0,temp.getWidth(),0,0,temp.getWidth(),temp.getHeight());
                int out[] = new int[x2*y2];
                t2=x/x2;
                for (int yy = 0; yy < y2; yy++) {
                    dy = yy *y/y2;
                    for (int xx = 0; xx < x2; xx++) {
                        dx = xx * t2;
                        out[i++]=ini[(x*dy)+dx]; //(x2*yy)+xx
                    }
                }
                return out;
        }
	
	/**
	 * Image gray scale conversion function
	 * @param originalImage the source image
	 * @return the gray scale image
	 */
	public static Image convertToGrayScale(Image originalImage) {
		// Need an array (for RGB, with the size of original image)
		int rgb[] = new int[originalImage.getWidth() * originalImage.getHeight()];
		// Get the RGB array of image into "rgb"
		originalImage.getRGB(
				rgb,
				0,
				originalImage.getWidth(),
				0,
				0,
				originalImage.getWidth(),
				originalImage.getHeight());
		
		for (int i=0;i<rgb.length;i++)
			rgb[i] = getGrayScale(rgb[i]);
		
		// Create an image with that RGB array
		Image temp2 = Image.createRGBImage(rgb, originalImage.getWidth(), originalImage.getHeight(), true);
		return temp2;
	}
	
	/**
	 * In place image gray scale conversion function
	 * @param originalImage the source image
	 */
	public static void convertToGrayScaleInPlace(Image originalImage) {
		// Need an array (for RGB, with the size of original image)
		int rgb[] = new int[originalImage.getWidth() * originalImage.getHeight()];
		// Get the RGB array of image into "rgb"
		originalImage.getRGB(
				rgb,
				0,
				originalImage.getWidth(),
				0,
				0,
				originalImage.getWidth(),
				originalImage.getHeight());
		
		for (int i=0;i<rgb.length;i++)
			rgb[i] = getGrayScale(rgb[i]);
		
//		// Create an image with that RGB array
//		Image temp2 = Image.createRGBImage(rgb, originalImage.getWidth(), originalImage.getHeight(), true);
		originalImage.getGraphics().drawRGB(rgb, 0, originalImage.getWidth(), 0, 0, originalImage.getWidth(), originalImage.getHeight(), false);
	}
	
	/**
	 * Converts and RGB color to a gray scale value
	 * @param color the RGB color
	 * @return the gray scale value
	 */
	public static final int getGrayScale(int color){
		int a = (color >> 24) & 0xff;
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;
		
		int tmp = (r + g + b) / 3;
		return (a << 24) | (tmp << 16) | (tmp << 8) | tmp;
	}
	
	/**
	 * Sets and alpha value to the image
	 * @param raw the RGB array to be blended
	 * @param alphaValue the alpha value to be set
	 */
	public static void blend(int[] raw, int alphaValue){
		// Start loop through all the pixels in the image.
		for(int i = raw.length - 1; i > 0; i--){
			int a = 0;
			int color = (raw[i] & 0x00FFFFFF); // get the color of the pixel.
			a = alphaValue;     // set the alpha value we want to use 0-255.
			a = (a<<24);    // left shift the alpha value 24 bits.
			color += a;
			raw[i] = color;
		}
	}
	/**
	 * Returns an Image object containing the requested resource or a white 10px square Image
	 * @param path Path of image resource required
	 * @return Image
	 */
	public static Image createSafeImage(String path){
		Image img;
		try {
			img = Image.createImage(path);
		} catch (IOException e) {
			img = Image.createImage(10, 10);
		}
		return img;
	}
	/**
	 * Implementations of BoxBlur effect to be applied on Images
	 * @author Stefano Driussi
	 *
	 */
	public static class GraphicEffects {
		/**
		 * Apply a box blur effect to the passed in Image
		 * @param src Image to be filtered
		 * @param hRadius Horizontal component for radius
		 * @param vRadius Vertical component for radius
		 * @param iterations Number of iterations
		 * @return Blurred Image
		 */
		public static Image blurFilter(Image src, int hRadius, int vRadius, int iterations){
			int width = src.getWidth();
	        int height = src.getHeight();

	        int[] inPixels = new int[width*height];
	        int[] outPixels = new int[width*height];
	        src.getRGB(inPixels, 0, width, 0, 0, width, height);

	        for (int i = 0; i < iterations; i++ ) {
	            blur( inPixels, outPixels, width, height, hRadius );
	            blur( outPixels, inPixels, height, width, vRadius );
	        }
	        return Image.createRGBImage(inPixels, width, height, false);
		}
		/**
		 * Apply a box blur effect to the passed in Image
		 * @param src Image to be filtered
		 * @param hRadius Horizontal component for radius
		 * @param vRadius Vertical component for radius
		 * @param iterations Number of iterations
		 */
		public static void blurFilterInPlace(Image src, int hRadius, int vRadius, int iterations){
			int width = src.getWidth();
	        int height = src.getHeight();

	        int[] inPixels = new int[width*height];
	        int[] outPixels = new int[width*height];
	        src.getRGB(inPixels, 0, width, 0, 0, width, height);

	        for (int i = 0; i < iterations; i++ ) {
	            blur( inPixels, outPixels, width, height, hRadius );
	            blur( outPixels, inPixels, height, width, vRadius );
	        }
	        src.getGraphics().drawRGB(inPixels, 0, width, 0, 0,width, height, false);
		}
		
		/**
		 * Apply a box blur effect to the passed in Image using defaul values of 3 for hRadius,
		 * 3 for vRadius and 1 for iterations parameters
		 * @param src Image to be filtered
		 * @return Blurred Image
		 */
		public static Image blurFilter(Image src) {
	        return blurFilter(src, 3, 3, 1);
		}
		
		private static int clamp(int x, int a, int b) {
			return (x < a) ? a : (x > b) ? b : x;
		}
		
		private static void blur( int[] in, int[] out, int width, int height, int radius ) {
	        int widthMinus1 = width - 1;
	        int tableSize = 2 * radius + 1;
	        int divide[] = new int[256 * tableSize];

	        for ( int i = 0; i < 256 * tableSize; i++ )
	            divide[i] =  i / tableSize;

	        int inIndex = 0;
	        
	        for ( int y = 0; y < height; y++ ) {
	            int outIndex = y;
	            int ta = 0, tr = 0, tg = 0, tb = 0;

	            for ( int i = -radius; i <= radius; i++ ) {
	                int rgb = in[inIndex + clamp(i, 0, width-1)];
	                ta += (rgb >> 24) & 0xff;
	                tr += (rgb >> 16) & 0xff;
	                tg += (rgb >> 8) & 0xff;
	                tb += rgb & 0xff;
	            }

	            for ( int x = 0; x < width; x++ ) {
	                out[ outIndex ] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

	                int i1 = x+radius+1;
	                if ( i1 > widthMinus1 )
	                    i1 = widthMinus1;
	                int i2 = x-radius;
	                if ( i2 < 0 )
	                    i2 = 0;
	                int rgb1 = in[inIndex+i1];
	                int rgb2 = in[inIndex+i2];
	                
	                ta += ((rgb1 >> 24) & 0xff)-((rgb2 >> 24) & 0xff);
	                tr += ((rgb1 & 0xff0000)-(rgb2 & 0xff0000)) >> 16;
	                tg += ((rgb1 & 0xff00)-(rgb2 & 0xff00)) >> 8;
	                tb += (rgb1 & 0xff)-(rgb2 & 0xff);
	                outIndex += height;
	            }
	            inIndex += width;
	        }
	    }
	}
}
