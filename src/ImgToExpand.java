

public class ImgToExpand {
	private int[] nk, skRound;
	private int  width, height;
	private double[] pr_rk, sk;
	
	
	public ImgToExpand(int width, int height) {		
		this.nk = new int[256];
		this.skRound = new int[256];
		this.pr_rk = new double[256];
		this.sk = new double[256];
		this.width = width;
		this.height = height;
	}
	
	public void finishCalculation(double amax) {
		double acumSk = 0d;
		for (int i=0; i<256; i++) {	
			if (this.nk[i] > 0) {
				this.pr_rk[i] = (double) this.nk[i] / ((double) this.width * (double) this.height);			
				acumSk += this.pr_rk[i] * amax;
				this.sk[i] = acumSk;
				this.skRound[i] = (int) this.sk[i];	
			}
		}		
	}
	
	public int getSkRound(int index) {
		return this.skRound[index];
	}
	
	public int getNk(int index) {
		return this.nk[index];
	}
	
	public void addPixel(int index) {
		this.nk[index] += 1;
	}
}
