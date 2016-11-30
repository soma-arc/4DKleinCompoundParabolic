import java.util.ArrayList;


public class SakugawaParabolicRecipe {
	int id, maxLevel;
	Quaternion z0;
	double thetaA, thetaB;
	private ArrayList<Line3D> l3DList = new ArrayList<Line3D>();
	private DFSOperator ifs;
	Matrix a, b;
	public SakugawaParabolicRecipe(int id, Quaternion z0, double thetaA, double thetaB, int maxLevel, String pointsStr) {
		this.id = id;
		this.z0 = z0;
		this.thetaA = thetaA;
		this.thetaB = thetaB;
		double z0Re = z0.re();
		this.maxLevel = maxLevel;
		double R = Math.sqrt( -z0Re * (z0Re + 4.0) * ( Math.sin(thetaA + thetaB) * Math.sin(thetaA + thetaB) ) );
		Quaternion z;
		if(Math.sin(thetaA + thetaB) == 0){
			z = Quaternion.valueOf(-4, 0, 0, 0);
		}else{
			double phi = Math.PI/2;//É”
			z = Quaternion.valueOf(z0Re, R * Math.cos(phi), R * Math.sin(phi), 0);
		}

		Quaternion qa = Quaternion.valueOf(Math.cos(thetaA), 0, 0, Math.sin(thetaA));
		a = Matrix.valueOf(qa, Quaternion.ZERO, z.mult(qa), qa);

		Quaternion qb = Quaternion.valueOf(Math.cos(thetaB), 0, 0, Math.sin(thetaB));
		
		b = Matrix.valueOf(qb, qb, Quaternion.ZERO, qb);
		
		loadLines(pointsStr);
		ifs = new DFSOperator(a, b, maxLevel);
	}
	
	public SakugawaParabolicRecipe(int id, Quaternion z0, double thetaA, double thetaB, int maxLevel) {
		this.id = id;
		this.z0 = z0;
		this.thetaA = thetaA;
		this.thetaB = thetaB;
		double z0Re = z0.re();
		this.maxLevel = maxLevel;
		double R = Math.sqrt( -z0Re * (z0Re + 4.0) * ( Math.sin(thetaA + thetaB) * Math.sin(thetaA + thetaB) ) );
		Quaternion z;
		if(Math.sin(thetaA + thetaB) == 0){
			z = Quaternion.valueOf(-4, 0, 0, 0);
		}else{
			double phi = Math.PI/2;//É”
			z = Quaternion.valueOf(z0Re, R * Math.cos(phi), R * Math.sin(phi), 0);
		}

		Quaternion qa = Quaternion.valueOf(Math.cos(thetaA), 0, 0, Math.sin(thetaA));
		a = Matrix.valueOf(qa, Quaternion.ZERO, z.mult(qa), qa);

		Quaternion qb = Quaternion.valueOf(Math.cos(thetaB), 0, 0, Math.sin(thetaB));
		
		b = Matrix.valueOf(qb, qb, Quaternion.ZERO, qb);
		

		ifs = new DFSOperator(a, b, maxLevel);
	}
	
	ArrayList<Line3D> getLinesList(){
		return l3DList;
	}
	
	public void loadLines(String pointsStr){
		String[] lines = pointsStr.split("\n");
		for(int i = 0 ; i < lines.length ; i++){
			String point1 = lines[i];
			String point2 = lines[++i];
			String point3 = lines[++i];
			String point4 = lines[++i];
			String[] coordinates1 = point1.split(",");
			String[] coordinates2 = point2.split(",");
			String[] coordinates3 = point3.split(",");
			String[] coordinates4 = point4.split(",");
			l3DList.add(new Line3D(Double.valueOf(coordinates1[0]),
								   Double.valueOf(coordinates1[1]),
								   Double.valueOf(coordinates1[2]),
								   Double.valueOf(coordinates2[0]),
								   Double.valueOf(coordinates2[1]),
								   Double.valueOf(coordinates2[2]), Integer.valueOf(coordinates2[3])));
			
			l3DList.add(new Line3D(Double.valueOf(coordinates2[0]),
								   Double.valueOf(coordinates2[1]),
								   Double.valueOf(coordinates2[2]),
								   Double.valueOf(coordinates3[0]),
								   Double.valueOf(coordinates3[1]),
								   Double.valueOf(coordinates3[2]), Integer.valueOf(coordinates3[3])));
			
			l3DList.add(new Line3D(Double.valueOf(coordinates3[0]),
								   Double.valueOf(coordinates3[1]),
								   Double.valueOf(coordinates3[2]),
								   Double.valueOf(coordinates4[0]),
								   Double.valueOf(coordinates4[1]),
								   Double.valueOf(coordinates4[2]), Integer.valueOf(coordinates4[3])));
		}
	}
}
