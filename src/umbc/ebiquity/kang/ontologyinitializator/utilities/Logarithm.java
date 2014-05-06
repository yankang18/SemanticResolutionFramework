package umbc.ebiquity.kang.ontologyinitializator.utilities;

public class Logarithm {
	
	public static double log3(double a) {
		return logb(a, 3);
	}
	
	public static double log2(double a) {
		return logb(a, 2);
	}
	
	private static double logb(double a, double b) {
		return Math.log(a) / Math.log(b);
	}


}
