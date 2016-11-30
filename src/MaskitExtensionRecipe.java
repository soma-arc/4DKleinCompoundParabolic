import java.util.ArrayList;


public class MaskitExtensionRecipe extends Recipe{
	DFSOperator dfs;
	public MaskitExtensionRecipe(double p, double q, double r, int maxLevel, double epsilon){
		Quaternion P = Quaternion.valueOf(p, q, r, 0);
		
		Matrix a = Matrix.valueOf(Quaternion.ONE, P, Quaternion.ZERO, Quaternion.ONE).mult(
				   Matrix.valueOf(Quaternion.ZERO, Quaternion.valueOf(0, 0, 1, 0),
						   		  Quaternion.valueOf(0, 0, 1, 0), Quaternion.ZERO));
		
		Matrix b = Matrix.valueOf(Quaternion.ONE, Quaternion.valueOf(2, 0), Quaternion.ZERO, Quaternion.ONE);
		ifs = new DFSOperator(a, b, maxLevel, epsilon);
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
