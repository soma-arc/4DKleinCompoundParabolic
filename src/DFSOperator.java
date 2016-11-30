import java.util.ArrayList;

public class DFSOperator {
	Matrix a, b;
	double epsilon = 0.001;
	int maxLevel = 10;
	double expansion;
	int[] tags = new int[10000];
	Matrix[] gens = new Matrix[4 + 1];
	Matrix[] word = new Matrix[10000];
	//Quaternion[]fixPoint = new Quaternion[4 + 1];
	Quaternion[][]fixPoint = new Quaternion[4 + 1][4 + 1];
	int level = 1;
	ArrayList<Point3D> p3DList = new ArrayList<Point3D>();
	ArrayList<Line3D> l3DList = new ArrayList<Line3D>();
	
	Matrix calcMatrix(Matrix m1, Matrix m2, Matrix m3, Matrix m4){
        Matrix m1m2 = m1.mult(m2);
        Matrix m1m2m3  = m1m2.mult(m3);
        Matrix m1m2m3m4 = m1m2m3.mult(m4);
        return m1m2m3m4;
	}
	
	public DFSOperator(Matrix a, Matrix b, int maxLevel) {
		this.a = a;  
		this.b = b;
		this.maxLevel = maxLevel;
		setGens(a, b);
		setFixPoints();
		init();
	}
	
	public DFSOperator(Matrix a, Matrix b, int maxLevel, double epsilon){
		this.a = a;
		this.b = b;
		this.maxLevel = maxLevel;
		this.epsilon = Math.abs(epsilon);
		setGens(a, b);
		setFixPoints();
		init();
	}
	
	private void init(){
		word[0] = Matrix.UNIT;
		level = 1;
		tags[1] = 1;
		word[1] = gens[1];
	}
	
	private void setGens(Matrix a, Matrix b){
		gens[1] = a;
		gens[2] = b;
		gens[3] = a.inverse();
		gens[4] = b.inverse();
	}
	
	private void setFixPoints(){
		Matrix[][] repet = new Matrix[4 + 1][4 + 1];
		repet[1][1] = calcMatrix(gens[2], gens[3], gens[4], gens[1]);
	    repet[1][2] = gens[1];
	    repet[1][3] = gens[2].mult(gens[1]);
	    repet[1][4] = calcMatrix(gens[4], gens[3], gens[2], gens[1]);

	    repet[2][1] = calcMatrix(gens[3], gens[4], gens[1], gens[2]);
	    repet[2][2] = gens[2];
	    repet[2][3] = gens[1].mult(gens[2]);
	    repet[2][4] = calcMatrix(gens[1], gens[4], gens[3], gens[2]);

	    repet[3][1] = calcMatrix(gens[4], gens[1], gens[2], gens[3]);
	    repet[3][2] = gens[3];
	    repet[3][3] = gens[4].mult(gens[3]);
	    repet[3][4] = calcMatrix(gens[2], gens[1], gens[4], gens[3]);

	    repet[4][1] = calcMatrix(gens[1], gens[2], gens[3], gens[4]);
	    repet[4][2] = gens[4];
	    repet[4][3] = gens[3].mult(gens[4]);
	    repet[4][4] = calcMatrix(gens[3], gens[2], gens[1], gens[4]);
//		repet[1][1] = calcMatrix(gens[2], gens[3], gens[4], gens[1]);
//	    repet[1][2] = gens[1];
//	    repet[1][3] = calcMatrix(gens[4], gens[3], gens[2], gens[1]);
//
//	    repet[2][1] = calcMatrix(gens[3], gens[4], gens[1], gens[2]);
//	    repet[2][2] = gens[2];
//	    repet[2][3] = calcMatrix(gens[1], gens[4], gens[3], gens[2]);
//
//	    repet[3][1] = calcMatrix(gens[4], gens[1], gens[2], gens[3]);
//	    repet[3][2] = gens[3];
//	    repet[3][3] = calcMatrix(gens[2], gens[1], gens[4], gens[3]);
//
//	    repet[4][1] = calcMatrix(gens[1], gens[2], gens[3], gens[4]);
//	    repet[4][2] = gens[4];
//	    repet[4][3] = calcMatrix(gens[3], gens[2], gens[1], gens[4]);

	    
		for(int i = 1 ; i <= 4 ; i ++){
			for(int j = 1 ; j <= 4 ; j++){
				fixPoint[i][j] = Util.getFixPoint(repet[i][j]);
			}
		}
	}
	
	Quaternion[][] getFixPoint(){
		return fixPoint;
	}
	
	Quaternion oldPoint = null;
	//Indra's pearls p140 Box16
	public ArrayList<Point3D> calcLimitingSetWithDFS (){
		p3DList = new ArrayList<Point3D>();
		l3DList = new ArrayList<Line3D>();
		do{
			while(branchTermination() == false){
				goForward();
			}
			do{
				goBackward();
			} while((level != 0 ) && !isAvailableTurn());

			turnAndGoForward();
		}while(level != 1 || tags[1] != 1);
		
		return p3DList;
		//return l3DList;
	}
	
	public ArrayList<Line3D> calcLimitingSetLineWithDFS ()throws InterruptedException{
		p3DList = new ArrayList<Point3D>();
		l3DList = new ArrayList<Line3D>();
		do{
			while(branchTermination() == false){
				goForward();
			}
			do{
				goBackward();
			} while((level != 0 ) && !isAvailableTurn());

			turnAndGoForward();
			if(GLInterface.calcThread.interrupted()){
				throw new InterruptedException();
			}
		}while(level != 1 || tags[1] != 1);
		
		//return p3DList;
		return l3DList;
	}
	
	public ArrayList<Line3D> calcPartOfLimitingSetLine (int[] beginTags, int[] endTags) throws InterruptedException{
		
		for(int i = 1 ; i < beginTags.length ; i++){
			tags[i] = beginTags[i];
			word[i] = word[i - 1].mult(gens[beginTags[i]]);
		}
		level = beginTags.length -1;
		
		p3DList = new ArrayList<Point3D>();
		l3DList = new ArrayList<Line3D>();
		do{
			while(branchTermination() == false){
				goForward();
			}
			
			if(isDestination(endTags)){break;}
			
			do{
				goBackward();
			} while((level != 0 ) && !isAvailableTurn());

			turnAndGoForward();
			if(GLInterface.calcThread.interrupted()){
				throw new InterruptedException();
			}
		}while(level != 1 || tags[1] != 1);
		
		//return p3DList;
		return l3DList;
	}
	
	private boolean isDestination(int[] endTags){
		//if(level != 1 || tags[1] != 1) return false;
		//else{
			for(int i = 1 ; i < endTags.length ; i++){
				if(tags[i] != endTags[i]) return false;
			}
		//}
		for(int i = endTags.length -1 + 1 ; i <= level ; i++){
			int t = tags[i - 1] -1;
			if(t == 0) t = 4;
			if(tags[i] != t){
				return false;
			}
		}
		return true;
	}
	
	private void goForward(){
		level++;
		tags[level] = Math.abs((tags[level - 1] + 1)%4);
		if(tags[level] == 0)
			tags[level] = 4;
		//dumpWord();
		//System.out.println("go forward current level "+ level +"tag["+ level +"]"+ tags[level]);
		word[level] = word[level -1].mult(gens[tags[level]]);
	}

	private void goBackward(){
		level--;
		//System.out.println("go backward current level"+ level);
	}

	private boolean isAvailableTurn(){
		int t = Math.abs((tags[level] + 2)%4);
		if(t == 0)
			t = 4;
		int t2 = tags[level + 1] -1;
		if(t2 == 0)
			t2 = 4;
		if(t2  == t)
			return false;
		else
			return true;
	}

	private void turnAndGoForward(){
		tags[level + 1] = Math.abs((tags[level + 1]) - 1 % 4);
		if(tags[level +1] == 0)
			tags[level + 1] = 4;
		//dumpWord();
		if(level == 0)
			word[1] = gens[tags[1]];
		else
			word[level + 1] = word[level].mult(gens[tags[level + 1]]);
		level++;
		//System.out.println("turn and go forward current level"+ level);
	}
	
	//Indra's pearls p207 “ÁŽêŒêƒAƒ‹ƒSƒŠƒYƒ€
	private boolean branchTermination(){
		Quaternion[] z = new Quaternion[4 + 1];
		//System.out.println(word[level]);
		for(int j = 1 ; j <= 4 ; j++){
			//System.out.println(fixPoint[tags[level]][j]);
			z[j] = Util.mobiusOnPoint(word[level], fixPoint[tags[level]][j]);
			//System.out.println(z[j]);
		}
		
		
		if(level == maxLevel || (Util.distQuaternion3D(z[1], z[2]) <= epsilon && Util.distQuaternion3D(z[2], z[3]) <= epsilon && Util.distQuaternion3D(z[3], z[4]) <= epsilon)){
			//if(level == maxLevel) System.out.println("max level!");
			double delta = Util.calcDelta(word[level]);
			//System.out.println("delta "+ delta);
			//System.out.println("trace "+ Math.abs(word[level].trace().re()));
			if(GLInterface.ellipticCheck.isSelected()){
				if(delta < -0.0000001 || 
						(Math.abs(delta) < 0.00001 && 
								Math.abs(word[level].b.k()) < 0.00001 && 
								Math.abs(word[level].c.k()) < 0.00001 && 
								Math.abs(word[level].trace().re()) < 2.0 - 0.000001 )){
					System.out.println("elliptic");
					System.out.println("delta "+ delta);
					System.out.println("trace "+ Math.abs(word[level].trace().re()));
				}
			}
			int[] t = new int[maxLevel + 1];
			for(int i = 1 ; i <= level ; i++){
				//System.out.print(tags[i]);
				t[i] = tags[i];
			}
			//System.out.println();
			l3DList.add(new Line3D(z[1], z[2], t, tags[1]));
			l3DList.add(new Line3D(z[2], z[3], t, tags[1]));
			l3DList.add(new Line3D(z[3], z[4], t, tags[1]));
			return true;
		}else{
			return false;
		}
	}
}
