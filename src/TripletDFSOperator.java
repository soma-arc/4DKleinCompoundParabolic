import java.awt.Graphics2D;
import java.util.ArrayList;


public class TripletDFSOperator {
	Matrix a, b;
	double epsilon = 0.001;
	int maxLevel = 6;
	double expansion;
	int[] tags = new int[100000];
	Matrix[] gens = new Matrix[6 + 1];
	Matrix[] word = new Matrix[100000];
	//Quaternion[]fixPoint = new Quaternion[4 + 1];
	Quaternion[]fixPoint = new Quaternion[6 + 1];
	int level = 1;
	ArrayList<Point3D> p3DList = new ArrayList<Point3D>();
	
	public TripletDFSOperator(Matrix a, Matrix b, Matrix c) {
		//tags[0] = 1;
		word[0] = Matrix.UNIT;
//		fixPoint[1] = fixPoint[3] = Quaternion.ZERO;
//		fixPoint[2] = fixPoint[4] = Quaternion.INFINITY;
		this.a = a;  
		this.b = b;
		gens[1] = a;
		gens[2] = b;
		gens[3] = c;
		gens[4] = a.inverse();
		gens[5] = b.inverse();
		gens[6] = c.inverse();

		for(int i = 1 ; i <= 6 ; i++){
			fixPoint[i] = Util.getFixPoint(gens[i]);
			//System.out.println("fixPoint[i] "+ fixPoint[i]);
		}
		
		level = 1;
		tags[1] = 1;
		word[1] = gens[1];
		//System.out.println("set");
	}
	
	//Indra's pearls p140 Box16
		protected void calcLimitingSetWithDFS (){
			do{
				while(branchTermination() == false){
					goForward();
				}

				do{
					goBackward();
				} while((level != 0 ) && !isAvailableTurn());

				turnAndGoForward();
				System.gc();
			}while(level != 1 || tags[1] != 1);
		}

		protected void goForward(){
			level++;
			tags[level] = Math.abs((tags[level - 1] + 1)%6);
			if(tags[level] == 0)
				tags[level] = 6;
			word[level] = word[level -1].mult(gens[tags[level]]);
		}
		
		private void goBackward(){
			level--;
		}
		
		protected boolean isAvailableTurn(){
			int t = Math.abs((tags[level] + 2)%6);
			if(t == 0)
				t = 6;
			int t2 = tags[level + 1] -1;
			if(t2 == 0)
				t2 = 6;
			if(t2  == t)
				return false;
			else
				return true;
		}

		protected void turnAndGoForward(){
			tags[level + 1] = Math.abs(tags[level + 1] - 1 % 6);
			if(tags[level +1] == 0)
				tags[level + 1] = 6;
			if(level == 0)
				word[1] = gens[tags[1]];
			else
				word[level + 1] = word[level].mult(gens[tags[level + 1]]);
			level++;
		}

		//Indra's pearls p207 “ÁŽêŒêƒAƒ‹ƒSƒŠƒYƒ€
		protected boolean branchTermination(){
			if(level == maxLevel){
				Quaternion newPoint = Util.mobiusOnPoint(word[level], fixPoint[tags[level]]);
				p3DList.add(new Point3D(newPoint));
				return true;
			}else{
				return false;
			}
		}
}
