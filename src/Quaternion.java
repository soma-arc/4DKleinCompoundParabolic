import java.math.BigDecimal;


public class Quaternion implements Comparable<Quaternion>{
	private double re, i, j, k;
	public static final Quaternion ZERO     = new Quaternion(0, 0, 0, 0);
	public static final Quaternion ONE      = new Quaternion(1, 0, 0, 0);
	public static final Quaternion I        = new Quaternion(0, 1, 0, 0);
	public static final Quaternion INFINITY = new Quaternion(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	
	private Quaternion(double re, double i, double j, double k){
		this.re = re;
		this.i = i;
		this.j = j;
		this.k = k;
	}
	
	public static Quaternion valueOf(double re, double i, double j, double k){
		return new Quaternion(re, i, j, k);
	}

	public static Quaternion valueOf(double re, double i){
		return new Quaternion(re, i, 0, 0);
	}

	public double re(){ return re;}
	public double i() { return i; }
	public double j() { return j; }
	public double k() { return k; }
	
	void roundRe(){
		BigDecimal bigRe = new BigDecimal(re);
		BigDecimal roundedBigRe = bigRe.setScale(10, BigDecimal.ROUND_DOWN);
		re = roundedBigRe.doubleValue();
	}
	
	public Quaternion imag(){
		return new Quaternion(0, i, j, k);
	}
	
	public Quaternion add(Quaternion q){
		return new Quaternion(re + q.re(),
							  i  + q.i(),
							  j  + q.j(), 
							  k  + q.k());
	}
	
	public Quaternion add(double value){
		return new Quaternion(re + value,
							  i ,
							  j , 
							  k );
	}
	
	public Quaternion sub(Quaternion q){
		return new Quaternion(re - q.re(),
							  i  - q.i(),
							  j  - q.j(), 
							  k  - q.k());
	}
	
	public Quaternion sub(double d){
		if(this.isInfinity()) return INFINITY;
		return new Quaternion(re - d, i, j, k);
	}
	
	//âEë§Ç©ÇÁäÑÇÈ
	//public Quaternion div(Quaternion q){
		//return this.mult(q.inverse());
	//}
	
	public Quaternion div(double m){
		if(Double.isInfinite(m) || (m == 0 && this.isZero())) return Quaternion.ZERO;
		else if(m == 0)          return Quaternion.INFINITY;
		return new Quaternion(re/m, i/m, j/m, k/m);
	}
	
	public Quaternion mult(double m){
		if(this.isInfinity() || m == Double.POSITIVE_INFINITY){return INFINITY;}
		return new Quaternion(re * m, i * m, j * m, k * m);
	}
	
	
	//ÉnÉ~ÉãÉgÉìêœ hamilton
	public Quaternion mult(Quaternion q){
		if(this.isZero() || q.isZero()){return ZERO;}
		if(this.isInfinity() || q.isInfinity()){return INFINITY;}
		return new Quaternion(re * q.re() - i * q.i()  - j * q.j()  - k * q.k() , 
				              re * q.i()  + i * q.re() + j * q.k()  - k * q.j() , 
				              re * q.j()  - i * q.k()  + j * q.re() + k * q.i() , 
				              re * q.k()  + i * q.j()  - j * q.i()  + k * q.re());
	}
	
	//èÉãïélå≥êîp = b1i + c1j + d1k Ç∆ q = b2i + c2j + d2kÇ…ëŒÇµÇƒ
	//p.q = b1b2 + c1c2 + d1d2
	//pxq = (c1d2 - d1c2)i + (d1b2 - b1d2)j + (b1c2 - c1b2)k
	public double vectorDot(Quaternion q){
		return i * q.i + j * q.j + k * q.k;
	}
	
	public Quaternion vectorCross(Quaternion q){
		return new Quaternion(0,
				              j*q.k - k*q.j,
				              k*q.i - i*q.k,
				              i*q.j - j*q.i);
	}
	
	//ê≥ãKâª
	public Quaternion unit(){
		double norm = norm();
		if(norm == 0) return ZERO;
		return this.div(norm);
	}
	
	public Quaternion cliffordTransposition(){
		return new Quaternion(re, i, j, -k);
	}
	
	public String toString(){
		String iSign = " + ";
		String jSign = " + ";
		String kSign = " + ";
		if(i < 0) iSign = " - ";
		if(j < 0) jSign = " - ";
		if(k < 0) kSign = " - ";
		return re + iSign + Math.abs(i) +"i" + jSign + Math.abs(j) + "j"+ kSign + Math.abs(k) +"k";
	}
	
	public Quaternion conjugation(){
		return new Quaternion(re, -i, -j, -k);
	}
	
	//ê‚ëŒíl
	public double abs(){
		return re*re + i*i + j*j + k*k;
	}
	
	//ÉmÉãÉÄ
	public double norm(){
		if(this.isInfinity()) return Double.POSITIVE_INFINITY;
		return Math.sqrt(re*re + i*i + j*j + k*k);
	}
	
	public double sqNorm(){
		if(this.isInfinity()) return Double.POSITIVE_INFINITY;
		return re*re + i*i + j*j + k*k;
	}
	
	public Quaternion inverse(){
		double v = Math.pow(norm(), 2);
		if(Double.isInfinite(v)){
			return Quaternion.ZERO;
		}else if(v == 0){
			return Quaternion.INFINITY;
		}
		return conjugation().div(v);
	}
	
	
	//http://www.aihara.co.jp/~taiji/tp/upload/1292868622-q-fundamental.pdf
	//éÆÇRÇR
	public static Quaternion log(Quaternion q){
		double logRe = Math.log(q.norm());
		Quaternion logImag = q.imag().div(q.imag().norm()).mult(  Math.atan( q.imag().norm()/q.re )  );
		return Quaternion.valueOf(logRe, logImag.i, logImag.j, logImag.k);
	}
	
	//30
	public static Quaternion exp(Quaternion q){
		double realValue = Math.exp(q.re) * Math.cos(q.imag().norm());
		Quaternion im = q.imag().div( q.imag().norm() ).mult(Math.sin(q.imag().norm())).mult(Math.exp(q.re));
		return new Quaternion(realValue, im.i, im.j, im.k);
	}
	
	//44
	public static Quaternion pow(Quaternion q, double exponent){
		return exp( log(q).mult(exponent) );
	}
	
	//46
	public Quaternion sqrt(){
		if(isComplex()){
			return complexSqrt();
		}
		return pow(this, 0.5);
	}
	
	public Quaternion complexSqrt(){

	    if(i > 0){
	      return new Quaternion((double)(Math.sqrt(re + Math.sqrt(re*re + i*i)) / Math.sqrt(2)),
	                         (double)(Math.sqrt(-re + Math.sqrt(re*re + i*i)) / Math.sqrt(2)),0,0);

	    }else if(i < 0){
	      return new Quaternion((double)(Math.sqrt(re + Math.sqrt(re*re + i*i)) / Math.sqrt(2)),
	                         (double)(-Math.sqrt(-re + Math.sqrt(re*re + i*i)) / Math.sqrt(2)),0,0);
	    }

	    if(re < 0){
	      return new Quaternion(0.0, (double)Math.sqrt(Math.abs(re)),0,0);
	    }
	    return new Quaternion((double)Math.sqrt(re), 0,0,0);
	  }
	
	public boolean isZero(){
		return re == 0 && i == 0 && j == 0 && k ==0;
	}
	
	public boolean isInfinity(){
		return Double.isInfinite(re)|| 
			   Double.isInfinite(i) || 
			   Double.isInfinite(j) ||
			   Double.isInfinite(k);
	}
	
	public boolean isReal(){
		if(i == 0 && j == 0 && k == 0){
			return true;
		}
		return false;
	}
	
	public boolean isComplex(){
		if(j == 0 && k ==0){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Quaternion)) 
			return false;
		Quaternion q = (Quaternion) o;
		return re == q.re && i == q.i && j == q.j && k == q.k;
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		long l = Double.doubleToLongBits(re);
		result = 31 * result + (int)Math.pow(l, l >>> 32);
		l = Double.doubleToLongBits(i);
		result = 31 * result + (int)Math.pow(l, l >>> 32);
		l = Double.doubleToLongBits(j);
		result = 31 * result + (int)Math.pow(l, l >>> 32);
		l = Double.doubleToLongBits(k);
		result = 31 * result + (int)Math.pow(l, l >>> 32);
		return result;
	}
	
	public int compareTo(Quaternion q){
		double normDiff = norm() - q.norm();
		if(normDiff != 0)
			return (int) normDiff;
		return 0;
	}
}