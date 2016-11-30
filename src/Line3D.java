
public class Line3D {
	double x1, y1, z1, x2, y2, z2;
	int level1GenIndex = -1;
	
	Line3D(Quaternion p1, Quaternion q2){
		x1 = p1.re();
		y1 = p1.i();
		z1 = p1.j();
		x2 = q2.re();
		y2 = q2.i();
		z2 = q2.j();
	}
	
	Line3D(Quaternion p1, Quaternion q2, int level1GenIndex){
		x1 = p1.re();
		y1 = p1.i();
		z1 = p1.j();
		x2 = q2.re();
		y2 = q2.i();
		z2 = q2.j();
		this.level1GenIndex = level1GenIndex;
	}
	
	int[] tags;
	Line3D(Quaternion p1, Quaternion q2, int[] tags, int level1GenIndex){
		x1 = p1.re();
		y1 = p1.i();
		z1 = p1.j();
		x2 = q2.re();
		y2 = q2.i();
		z2 = q2.j();
		this.tags = tags;
		this.level1GenIndex = level1GenIndex;
	}
	
	public int[] getTags(){
		return tags;
	}
	
	Line3D(double x1, double y1, double z1, double x2, double y2, double z2){
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	Line3D(double x1, double y1, double z1, double x2, double y2, double z2, int level1GenIndex){
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.level1GenIndex = level1GenIndex;
	}
	
	public String toPointsFormat(){
		StringBuilder builder = new StringBuilder();
		builder.append(x1).append(",").append(y1).append(",").append(z1).append(",").append(level1GenIndex).append("\n");
		builder.append(x2).append(",").append(y2).append(",").append(z2).append(",").append(level1GenIndex).append("\n");
		return new String(builder);
	}
	
	double epsilon = 0.001;
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Line3D other = (Line3D) obj;
		if(dist(x1, other.x1) < epsilon && dist(y1, other.y1) < epsilon && dist(z1, other.z1) < epsilon &&
				dist(x2, other.x2) < epsilon && dist(y2, other.y2) < epsilon && dist(z2, other.z2) < epsilon){
			return true;
		}
		return false;
	}
	
	private double dist(double d1, double d2){
		return Math.abs(d1 - d2);
	}
	
	public String toString(){
		return "("+ x1 +", "+ y1 +", "+ z1 +") to ("+ x2 +", "+ y2 +", "+ z2 +")";
	}
}
