import java.util.ArrayList;


public class GrandmaSpecialGroup extends Recipe{
	DFSOperator ifs;
	public GrandmaSpecialGroup(Quaternion t_a, Quaternion t_b, boolean isT_abPlus, int maxLevel) {
		Quaternion t_ab;
		if(isT_abPlus){
			t_ab = t_a.mult(t_b).add(   (t_a.mult(t_a).mult(t_b.mult(t_b)).sub(t_a.mult(t_a).add(t_b.mult(t_b)).mult(4.0))).complexSqrt()  ).mult(0.5);
		}else{
			t_ab = t_a.mult(t_b).sub(   (t_a.mult(t_a).mult(t_b.mult(t_b)).sub(t_a.mult(t_a).add(t_b.mult(t_b)).mult(4.0))).complexSqrt()  ).mult(0.5);
		}
		System.out.println("t_ab"+ t_ab);
		Quaternion z0 = t_ab.sub(2.0).mult(t_b).mult(  t_b.mult(t_ab).sub(t_a.mult(2.0)).add(t_ab.mult(Quaternion.valueOf(0, 2.0))).inverse()   );
		System.out.println("z0" +  z0);
	    Matrix gen_a = Matrix.valueOf(t_a.mult(0.5),
	                              t_a.mult(t_ab).sub(t_b.mult(2.0)).add( Quaternion.valueOf(0, 4.0) ).mult(    z0.mult(t_ab.mult(2.0).add(4.0)).inverse()   ),
	                              z0.mult(t_a.mult(t_ab).sub(t_b.mult(2.0)).sub( Quaternion.valueOf(0, 4.0)).mult(  t_ab.mult(2.0).sub(4.0).inverse()  )),
	                              t_a.mult(0.5));
	    Matrix gen_b = Matrix.valueOf(t_b.sub(Quaternion.valueOf(0, 2.0)).mult(0.5),
	                              t_b.mult(0.5),
	                              t_b.mult(0.5),
	                              t_b.add(Quaternion.valueOf(0, 2.0)).mult(0.5));
	    System.out.println("gen_a "+ gen_a);
	    System.out.println("gen_b "+ gen_b);	    
		ifs = new DFSOperator(gen_a, gen_b, maxLevel);
	}
	
	public ArrayList<Point3D> run(){
		return ifs.calcLimitingSetWithDFS();
	}
	
	public ArrayList<Line3D> runLine()throws InterruptedException{
		return ifs.calcLimitingSetLineWithDFS();
	}
}
