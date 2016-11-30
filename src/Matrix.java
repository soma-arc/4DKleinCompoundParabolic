
public class Matrix {
	Quaternion a, b, c, d;
	public static final Matrix UNIT = new Matrix(Quaternion.ONE,  Quaternion.ZERO,
												 Quaternion.ZERO, Quaternion.ONE);
	
	private Matrix(Quaternion a, Quaternion b, Quaternion c, Quaternion d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public static Matrix valueOf(Quaternion a, Quaternion b, Quaternion c, Quaternion d){
		return new Matrix(a, b, c, d);
	}
	
	public Matrix mult(Matrix m){
		return new Matrix(a.mult(m.a).add(b.mult(m.c)),
						  a.mult(m.b).add(b.mult(m.d)),
						  c.mult(m.a).add(d.mult(m.c)),
	                      c.mult(m.b).add(d.mult(m.d)));
	}
	
	/*public Matrix mut(Quaternion q){
		return Matrix.valueOf(a.mult(q),
							  b.mult(q),
							  c.mult(q),
							  d.mult(q));
	}*/
	
	public Matrix inverse(){
		return new Matrix(d.cliffordTransposition()         , b.cliffordTransposition().mult(-1),
				          c.cliffordTransposition().mult(-1), a.cliffordTransposition());
	}
	
	public Quaternion trace(){
		return a.add(d);
	}
	
	public String toString(){
		return "{"+ a.toString() +","+ b.toString() +"\n"+ c.toString() +","+ d.toString() +"}";
	}
}
