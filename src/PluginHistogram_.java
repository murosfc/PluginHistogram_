import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class PluginHistogram_ implements PlugIn {	
	private GenericDialog gui;
	private ImagePlus image;
	private ImageProcessor processor;
	private int alow, ahigh;
	private final String[] methods = {"expansion","equalization"};

	
	public void run(String arg) {
		image = IJ.getImage();
		processor = image.getProcessor();		
		if (image.getType() != ImagePlus.GRAY8) {
			IJ.error("In order to run this plugin, the image must be Type GRAY8");
		}
		else {					
			this.histogramAdjust();			
		}
	}
	
	public String showGUIReturnMethod(){		
		this.gui = new GenericDialog("Histogram adjust");		
		gui.addRadioButtonGroup("Available methods", methods, 3, 1, methods[0]);		
		gui.showDialog();
		if (gui.wasOKed()) {			
			return gui.getNextRadioButton();
		}
		else return null;
	}

	public void histogramAdjust() {
		String selectedMethod = showGUIReturnMethod();		
		switch (selectedMethod){
			case "expansion":				
				expandHistrogram();
				break;
			case "equalization":				
				equalizeHistrogram();
				break;
			default:
				IJ.log("Cancelled by user");
				break;
		}
	}
	
	private void equalizeHistrogram() {		
		int width = image.getWidth(), height = image.getHeight(), pixelValue, newPixelValue;		
		setAHighALow();
		ImgToExpand histData = new ImgToExpand(width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);
				histData.addPixel(pixelValue);				
			}
		}		
		histData.finishCalculation(ahigh);		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);
				if (histData.getSkRound(pixelValue) > 0) {
					newPixelValue = histData.getSkRound(pixelValue);					
				}
				else newPixelValue = pixelValue;	
				processor.putPixel(x, y, newPixelValue);
			}
		}
		image.updateAndDraw();
		IJ.run(image, "Histogram", "");			
	}

	private void expandHistrogram() {
		int width = image.getWidth(), height = image.getHeight(), pixelValue;
		int amin = 0, amax = 255;
		setAHighALow();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);
				pixelValue = pixelValidation((int) (amin + (pixelValue - alow)*(amax-amin)/(ahigh-alow)));		
				processor.putPixel(x, y, pixelValue);
			}
		}
		image.updateAndDraw();
		IJ.run(image, "Histogram", "");	
	}	
	
	private void setAHighALow() {
		int width = image.getWidth(), height = image.getHeight(),  pixelValue;
		alow = 255;
		ahigh = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);				
				if (ahigh < pixelValue && pixelValue <= 255) {
					ahigh = pixelValue;
				}
				if (alow > pixelValue && pixelValue >= 0 ) {
					alow = pixelValue;
				}
			}								
		}		
	}
	
	private int pixelValidation(int pixel) {
		if (pixel > 255) {
			return 255;
		}
		else if (pixel < 0) {
			return 0;
		}
		else return pixel;
	}
}
	