import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class WorldGenerator {
	
	private double seed;
	
	// Variable used for ridged multi fractal
	private boolean first = true;
	private double exponent_array[];
	/* “H” determines the fractal dimension of the roughest areas
	 * “lacunarity” is the gap between successive frequencies
	 	A multiplier that determines how quickly the frequency increases for each successive octave in a Perlin-noise function.
		The frequency of each successive octave is equal to the product of the previous octave's frequency and the lacunarity value.
		A similar property to lacunarity is persistence, which modifies the octaves' amplitudes in a similar way.
	 * “octaves” is the number of frequencies in the fBm
	 * “offset” raises the terrain from “sea level”
	 */
	private double H = 1;
	private double lacunarity = 2; 
	private double octaves = 16;
	private double offset = 1;
	private double gain = 3;
	private double scaleX = 4;
	private double scaleY = 4;

	
	public WorldGenerator(double seedParam)
	{
		this.seed = seedParam;
	}
	
	
	public double[][] generateWorld1(double size)
	{
		double result[][] = new double[(int)size][(int)size];
		first = true;

		int radius = getRadiusForSize(size);
		
		for(int x = 0; x < size; x++)
		{
			for(int y = 0; y < size; y++)
			{
				result[x][y] = ridgedMultifractal(normalizeForRidged(x, radius), normalizeForRidged(y, radius), H, lacunarity, octaves, offset, gain, scaleX, scaleY);
			}
		}
		return result;
	}
		
		
	
	/*
	 * DEBUG
	 */
	public static void main(String[] args) {

		WorldGenerator test = new WorldGenerator(1000);
		double size = 1024;
		double[][] worldResult = test.generateWorld1(size);
		
		BufferedImage img = new BufferedImage((int)size, (int)size, BufferedImage.TYPE_INT_ARGB);
		
		for(int x = 0; x < worldResult.length; x++){
		
			for(int y = 0; y < worldResult[0].length; y++){
				//int rgb =  Color.HSBtoRGB(0.0f,0.0f,(float) worldResult[x][y]);
				//img.setRGB(x, y, rgb);

				double height = worldResult[x][y];
				img.setRGB(x, y, greyscale(height));
				//System.out.println(String.valueOf(x)+"-"+String.valueOf(y)+"="+String.valueOf(height));
			}
		}


		File outputFile = new File("ridgedMultifractal.png");
		try {
			ImageIO.write(img, "PNG", outputFile);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
		
		
/*
 * Private
 */
  public static int greyscale(double height) {
	    // Convert height to ARGB as a simple black to white palette
	    int scaled = heightToByte(height);
	    return 0xFF000000 | scaled << 16 | scaled << 8 | scaled;
  }
  
  static int heightToByte(double height) {
	    int scaled = (int) (height * 255);
	    return scaled;
  }

	
	/* Ridged multifractal terrain model.
	*
	* Some good parameter values to start with:
	* H: 1.0
	* offset: 1.0
	* gain: 2.0
	*/
	private double ridgedMultifractal(double x, double y, double H, double lacunarity, double octaves, double offset, double gain, double xScale, double yScale)
	{
		x *= xScale;
		y *= yScale;
		
		double result, frequency, signal, weight;
		int i;
		/* precompute and store spectral weights */
		if ( first ) {
			/* seize required memory for exponent_array */
			exponent_array = new double[(int)octaves+1];
			frequency = 1.0;
			for (i=0; i<=octaves; i++) {
				/* compute weight for each frequency */
				exponent_array[i] = Math.pow( frequency, -H );
				frequency *= lacunarity;
			}
		first = false;
		}
		/* get first octave */
		signal = ImprovedNoise.noise(x,y,0);
		/* get absolute value of signal (this creates the ridges) */
		if ( signal < 0.0 ) signal = -signal;
		/* invert and translate (note that “offset” should be  = 1.0) */
		signal = offset - signal;
		/* square the signal, to increase “sharpness” of ridges */
		signal *= signal;
		/* assign initial values */
		result = signal;
		weight = 1.0;
		for( i=1; i<octaves; i++ ) {
			/* increase the frequency */
			x *= lacunarity;
			y *= lacunarity;
	
			/* weight successive contributions by previous signal */
			weight = signal * gain;
			if ( weight > 1.0 ) weight = 1.0;
			if ( weight < 0.0 ) weight = 0.0;
			signal =  ImprovedNoise.noise(x,y,0);
			if ( signal < 0.0 ) signal = -signal;
			signal = offset - signal;
			signal *= signal;
			/* weight the contribution */
			signal *= weight;
			result += signal *exponent_array[i];
		}
		return( result );
	} /* RidgedMultifractal() */
	
	public double[] normalize2(double x, double y)
	{
		double result[] = new double[2];
		double s;
	
		s = 1.0/Math.sqrt(x * x + y * y);
		result[0] = x * s;
		result[1] = y * s;
		return result;
	}
	
	public int getRadiusForSize(double size)
	{
		return (int) (size/2);
	}
	public double normalizeForRidged(double valToNorm, int radius)
	{
//		System.out.println(String.valueOf( (valToNorm - radius)/radius));
		return (valToNorm - radius)/radius;
	}
}
