import java.util.ArrayList;


public class ThreeDimensionalKlein extends Recipe{
	DFSOperator ifs;
	public ThreeDimensionalKlein(Quaternion z0, double thetaA, double thetaB, int maxLevel, double epsilon) {
		double z0Re = z0.re();
		double R = Math.sqrt( -z0Re * (z0Re + 4.0) * ( Math.sin(thetaA + thetaB) * Math.sin(thetaA + thetaB) ) );
		Quaternion z;
		if(Math.sin(thetaA + thetaB) == 0){
			z = Quaternion.valueOf(-4, 0, 0, 0);
		}else{
			double phi = Math.PI/2;//ƒÓ
			z = Quaternion.valueOf(z0Re, R * Math.cos(phi), R * Math.sin(phi), 0);
		}

		Quaternion qa = Quaternion.valueOf(Math.cos(thetaA), 0, 0, Math.sin(thetaA));
		Matrix a = Matrix.valueOf(
				qa, Quaternion.ZERO,
				z.mult(qa), qa);

		Quaternion qb = Quaternion.valueOf(Math.cos(thetaB), 0, 0, Math.sin(thetaB));

		Matrix b = Matrix.valueOf(qb, qb, Quaternion.ZERO, qb);
		System.out.println(a);
		System.out.println(b);
		ifs = new DFSOperator(a, b, maxLevel, epsilon);
	}

	public ThreeDimensionalKlein(double x, double y0, double v, int maxLevel){
		double y = 1.0;
		double u = Math.sqrt(1.0 + v * v);
		double p = 1.0/v + Math.sqrt(u*u/(v*v) - (x*x + y0 * y0));

		if(x >= 1 | x * x + y0 * y0 > u*u/(v*v)){
			System.out.println("error");
		}

		Quaternion qa1 = Quaternion.valueOf(u, 0);
		Quaternion qa2 = Quaternion.valueOf(0, v * p);
		Quaternion qa3 = Quaternion.valueOf(0, -v/p);
		Matrix a = Matrix.valueOf(qa1, qa2, qa3, qa1);

		Quaternion qb1 = Quaternion.valueOf(x, 0, y0 * y,0);
		Quaternion qb2 = Quaternion.valueOf((x*x -1.0)/y + y0*y0, 0);
		Quaternion qb3 = Quaternion.valueOf(y, 0);
		Quaternion qb4 = Quaternion.valueOf(x, 0, -y0*y,0);

		Matrix b = Matrix.valueOf(qb1, qb2, qb3, qb4);	


		ifs = new DFSOperator(a, b, maxLevel);
	}

	public ThreeDimensionalKlein(int maxLevel){
		//Quaternion qa2 = Quaternion.valueOf(Math.cos(thetaA), Math.cos(thetaA), -Math.sin(thetaA), Math.sin(thetaA) );
				//Quaternion qa3 = Quaternion.valueOf(Math.cos(thetaA), -Math.cos(thetaA), Math.sin(thetaA), Math.sin(thetaA));	

		Quaternion aq = Quaternion.valueOf(Math.sqrt(2), 0);
		Quaternion bq = Quaternion.valueOf(0, 1 + Math.sqrt(3)/2.0);
		Quaternion cq = Quaternion.valueOf(0, -4.0 -2.0 * Math.sqrt(3));
		//Quaternion cq = Quaternion.valueOf(0, 4.0 -2.0 * Math.sqrt(3));
		//Quaternion cq = Quaternion.valueOf(0, -4.0 + 2.0 * Math.sqrt(3));
		Quaternion dq = Quaternion.valueOf(Math.sqrt(2),  0);

//		Quaternion aq = Quaternion.valueOf(Math.sqrt(2), 0);
//		Quaternion bq = Quaternion.valueOf(0, 1.0/2.0);
//		Quaternion cq = Quaternion.valueOf(0, -2);
//		Quaternion dq = Quaternion.valueOf(Math.sqrt(2),  0);
		
		//Quaternion bq = Quaternion.valueOf(0, -1 + Math.sqrt(3)/2.0);		
		//Quaternion cq = Quaternion.valueOf(0, -4.0 -2.0 * Math.sqrt(3));
		
		Quaternion aq2;
		Quaternion bq2;
		Quaternion cq2;
		Quaternion dq2;
		//pack-x
		aq = Quaternion.valueOf(1, 0, Math.sqrt(2 * Math.sqrt(2)), 0);
		bq = Quaternion.valueOf(2 * Math.sqrt(2), 0);
		cq = Quaternion.valueOf(1, 0);
		dq = Quaternion.valueOf(1, 0, -Math.sqrt(2 * Math.sqrt(2)), 0);
		aq2 = Quaternion.valueOf(1.0, 0, 0,0);
		bq2 = Quaternion.valueOf(0, 0);
		cq2 = Quaternion.valueOf(0, -1.0 / Math.sqrt(2.0));
		dq2 = Quaternion.valueOf(1.0, 0, 0,0);
		//

		//pack a=2
		aq = Quaternion.valueOf(1, 0, Math.sqrt(3)/2.0, 0);
		bq = Quaternion.valueOf(1.5, 0);
		cq = Quaternion.valueOf(0.5, 0);
		dq = Quaternion.valueOf(1, 0, -Math.sqrt(3)/2.0, 0);
		aq2 = Quaternion.valueOf(Math.sqrt(2), 0, 0,0);
		bq2 = Quaternion.valueOf(0, 1);
		cq2 = Quaternion.valueOf(0, -1);
		dq2 = Quaternion.valueOf(Math.sqrt(2), 0, 0,0);
		//
		
		//pack quasi
		aq = Quaternion.valueOf(5.0/4.0, 0, Math.sqrt(3)/4.0, 0);
		bq = Quaternion.valueOf(1.5, 0);
		cq = Quaternion.valueOf(0.5, 0);
		dq = Quaternion.valueOf(5.0/4.0, 0, -Math.sqrt(3)/4.0,0);
		aq2 = Quaternion.valueOf(Math.sqrt(2), 0, 0,0);
		bq2 = Quaternion.valueOf(0, 1);
		cq2 = Quaternion.valueOf(0, -1);
		dq2 = Quaternion.valueOf(Math.sqrt(2), 0);
		//
		Matrix a = Matrix.valueOf(
		aq, bq,
		cq, dq);

		

//		Quaternion aq2 = Quaternion.valueOf(0.5, 0, 1.0/4.0,0);
//		Quaternion bq2 = Quaternion.valueOf(-Math.sqrt(7)/8.0, 0, (2 + Math.sqrt(7))/4.0, 0);
//		Quaternion cq2 = Quaternion.valueOf(1, 0);
//		Quaternion dq2 = Quaternion.valueOf(2.0, 0, Math.sqrt(7)/2,0);

//		aq2 = Quaternion.valueOf(3, 0, 2, 0);
//		bq2 = Quaternion.valueOf(6, 0);
//		cq2 = Quaternion.valueOf(2, 0);
//		dq2 = Quaternion.valueOf(3, 0, -2,0);
		Matrix b = Matrix.valueOf(
		aq2,             bq2,
		cq2, dq2);
		ifs = new DFSOperator(a, b, maxLevel);
	}

	public Matrix[] getGens(){
		return ifs.gens;
	}

	public ArrayList<Point3D> run(){
		return ifs.calcLimitingSetWithDFS();
	}

	public ArrayList<Line3D> runLine()throws InterruptedException{
		return ifs.calcLimitingSetLineWithDFS();
	}
	public ArrayList<Line3D> runPartOf(int[] beginTags, int[] endTags)throws InterruptedException{
		return ifs.calcPartOfLimitingSetLine(beginTags, endTags);
	}
}
