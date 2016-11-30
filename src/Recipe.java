import java.util.ArrayList;


public class Recipe {
	DFSOperator ifs;
	public ArrayList<Point3D> run(){
		return ifs.calcLimitingSetWithDFS();
	}
	
	public ArrayList<Line3D> runLine()throws InterruptedException{
		return ifs.calcLimitingSetLineWithDFS();
	}
}
