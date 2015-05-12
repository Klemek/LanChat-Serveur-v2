package base;


public class Util {

	public static double random(double min, double max){
		return Math.random()*(max+1-min)+min;
	}
	
	public static boolean find(String[] l, String i){
		if(l != null){
			for(int i2 = 0; i2 <l.length;i2++){
				if(l[i2].equals(i)){
					return true;
				}
			}
			
		}
		return false;
	}
	
}
