
public class Point3D {
	double x, y, z;
	int level1Gen = -1;
	//k�𖳎����āA�����Ai���Aj���ŕ\��
	public Point3D(Quaternion q){
		x = q.re();
		y = q.i();
		z = q.j();
	}
	
	public Point3D(double x, double y, double z, int level1Gen){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level1Gen = level1Gen;
	}
	
	public String toString(){
		return x +", "+ y +", "+ z;
	}
}
