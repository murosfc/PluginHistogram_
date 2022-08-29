import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class PluginHistogram_ implements PlugIn {	
	private GenericDialog gui;
	private ImagePlus image;
	private ImageProcessor processor;
	private double amin, amax;
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
		setAMinAMax();
		ImgToExpand histData = new ImgToExpand(width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);
				histData.addPixel(pixelValue);				
			}
		}		
		histData.finishCalculation(amax);		
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
		double alow = 0d, ahigh = 255d, newPixelValue;
		setAMinAMax();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);
				newPixelValue = amin + ((double) pixelValue - alow)*(amax-amin)/(ahigh-alow);		
				processor.putPixel(x, y, (int) newPixelValue);
			}
		}
		image.updateAndDraw();
		IJ.run(image, "Histogram", "");	
	}	
	
	private void setAMinAMax() {
		int width = image.getWidth(), height = image.getHeight(),  pixelValue;
		amin = 255d;
		amax = 0d;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				pixelValue = processor.getPixel(x, y);				
				if (amax < pixelValue && pixelValue <= 255) {
					amax = (double) pixelValue;
				}
				if (amin > pixelValue && pixelValue >= 0 ) {
					amin = (double) pixelValue;
				}
			}								
		}		
	}
}
	