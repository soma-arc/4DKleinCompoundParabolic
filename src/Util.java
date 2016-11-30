import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;


public class Util {
	private Util(){}
	
	 public static void insertSakugawaPoints(SQLiteHandler db, double z0, double theta_a, double theta_b, int maxLevel, ArrayList<Line3D> lines) throws SQLException{
		 StringBuilder builder = new StringBuilder();
		 for(int i = 0 ; i < lines.size() ; i++){
			 Line3D line = lines.get(i);
			 builder.append(line.toPointsFormat());
			 i += 2; //àÍÇ¬ÇÃé}ÇÃññí[Ç…Ç¬Ç´3ñ{ÇÃê¸Ç™à¯Ç©ÇÍÇÈ
			 line = lines.get(i);
			 builder.append(line.toPointsFormat());
		 }
		 String points = new String(builder);
		 db.execute("insert into sakugawa_parabolic_table "
		 		+ "(z0, theta_a, theta_b, max_level, points)"
		 		+ "values("
				 + z0 +","
				 + theta_a +","
				 + theta_b +","
				 + maxLevel +","
				 +"'"+ points +"');");
		 db.commit();
	 }
	 
	 public static void insertSakugawaParam(SQLiteHandler db, double z0, double theta_a, double theta_b, int maxLevel) throws SQLException{
		 db.execute("insert into sakugawa_parabolic_table "
		 		+ "(z0, theta_a, theta_b, max_level)"
		 		+ "values("
				 + z0 +","
				 + theta_a +","
				 + theta_b +","
				 + maxLevel +");");
		 db.commit();
	 }
	 
	 public static ArrayList<Point3D> readTripletPoints(String fileName){
		 ArrayList<Point3D> list = new ArrayList<Point3D>();
		 try{
			 FileReader r = new FileReader(new File(fileName));
			 BufferedReader reader = new BufferedReader(r);
			 String line;
			 while( (line = reader.readLine()) != null ){
				 String[] tmp = line.split(",");
				 Point3D p = new Point3D(Double.valueOf(tmp[0]), Double.valueOf(tmp[1]), Double.valueOf(tmp[2]), Integer.valueOf(tmp[3]));
				 list.add(p);
			 }
		 }catch (Exception ex){
			 ex.printStackTrace();
		 }
		 return list;
	 }
	
	public static Quaternion mobiusOnPoint(Matrix m, Quaternion q){
		
		//System.out.println(m);
		//System.out.println("q"+q);
		Quaternion a = m.a; Quaternion b = m.b; Quaternion c = m.c; Quaternion d = m.d;
		//if(c.inverse().isInfinity()) System.out.println("inf"+c);
		
		
		if(q.isInfinity()){
			if(c.isZero()){
				return Quaternion.INFINITY;
				//return Quaternion.ZERO;
			}
			return a.mult(c.inverse());
		}
		
		Quaternion left = a.mult(q).add(b);
		Quaternion right = c.mult(q).add(d).inverse();
		return left.mult(right);
//		Quaternion left = a.mult(q).mult( c.mult(q).add(d).inverse() );
//		Quaternion right = b.mult(c.mult(q).add(d).inverse());
//		return left.add(right);
		
	}
	
	public static double calcDelta(Matrix m){
		return m.a.add( m.d.cliffordTransposition() ).imag().sqNorm() + 4 * m.b.k() * m.c.k();
	}
	
	public static Quaternion getFixPoint(Matrix m){
		
		Quaternion sigma = m.c.mult(m.c.cliffordTransposition().inverse());//É–
		sigma.roundRe();
		Quaternion tau = sigma.mult(m.a.cliffordTransposition()).add(m.d);//É—
		//double delta = tau.imag().norm() * tau.imag().norm() - sigma.sub(1).norm() * sigma.sub(1).norm();
		double delta = m.a.add(m.d.cliffordTransposition()).imag().sqNorm() + 4 * m.b.k() * m.c.k();
		BigDecimal bigDelta = new BigDecimal(delta);
		BigDecimal roundedBigDelta = bigDelta.setScale(10, BigDecimal.ROUND_DOWN);
		delta = roundedBigDelta.doubleValue();
		Quaternion t = Quaternion.ONE;
//		System.out.println("sigma "+ sigma);
//		System.out.println("tau "+ tau);
//		System.out.println("delta "+ delta);
//		System.out.println("t "+ t);
		
		if(sigma.equals(Quaternion.ONE)){
			if(tau.isReal()){
				//(1)
				//System.out.println("(1)");
				if(Math.abs(m.trace().re()) >= 2){
					t = tau.div(2).add(  tau.div(2).mult(tau.div(2)).sub(1).sqrt()  );
					//t = tau.div(2).sub(  tau.div(2).mult(tau.div(2)).sub(1).sqrt()  );
				}else{
					
					System.out.println("(1) infinity"+ Math.abs(m.trace().re()));
					//ñ≥å¿å¬
				}
			}else{
				//(2)               Å´-Ç‡Ç†ÇÈ
				//System.out.println("(2)");
				double TValue = tau.re() + Math.sqrt((tau.re() * tau.re() - delta - 4)/2 + Math.sqrt(Math.pow(delta + 4 - tau.re() * tau.re(), 2) + 4 * tau.re() * tau.re() * delta ));
				//double TValue = tau.re() - Math.sqrt((tau.re() * tau.re() - delta - 4)/2 + Math.sqrt(Math.pow(delta + 4 - tau.re() * tau.re(), 2) + 4 * tau.re() * tau.re() * delta ));
				Quaternion T = Quaternion.valueOf(TValue, 0, 0, 0);
				double NValue = TValue/(TValue - 2 * tau.re());
				Quaternion N = Quaternion.valueOf(NValue, 0, 0, 0);
				t = (T.sub(tau)).inverse().mult(N.sub(sigma));
			}
		}else{
			if(Double.compare(delta, 0.0) == 0 || Double.compare(delta, 0.0) == -1){
				//(3)
				//System.out.println("(3)");
				Quaternion N = Quaternion.ONE;
				//double TValue = tau.re() + Math.sqrt(-delta);
				double TValue = tau.re() - Math.sqrt(-delta);
				//System.out.println("-delta"+ -delta);
				Quaternion T = Quaternion.valueOf(TValue, 0, 0, 0);
				t = (T.sub(tau)).inverse().mult(N.sub(sigma));
				//System.out.println(T);
			}else{
				//(4)    (2)Ç∆Ç®Ç»Ç∂           Å´-Ç‡Ç†ÇÈ
				//System.out.println("(4)");
				double TValue = tau.re() + Math.sqrt((tau.re() * tau.re() - delta - 4)/2 + Math.sqrt(Math.pow(delta + 4 - tau.re() * tau.re(), 2) + 4 * tau.re() * tau.re() * delta ));
				//double TValue = tau.re() - Math.sqrt((tau.re() * tau.re() - delta - 4)/2 + Math.sqrt(Math.pow(delta + 4 - tau.re() * tau.re(), 2) + 4 * tau.re() * tau.re() * delta ));
				Quaternion T = Quaternion.valueOf(TValue, 0, 0, 0);
				double NValue = TValue/(TValue - 2 * tau.re());
				Quaternion N = Quaternion.valueOf(NValue, 0, 0, 0);
				t = (T.sub(tau)).inverse().mult(N.sub(sigma));
			}
		}
		
		Quaternion V = m.c.inverse().mult(t.sub(m.d));
		return V;
	}
	
	
	public static double distQuaternion3D(Quaternion q1, Quaternion q2){
		Quaternion diff = q2.sub(q1);
		double total = diff.re() * diff.re() + diff.i() * diff.i() + diff.j() * diff.j();
		return Math.sqrt(total);
	}
	
}
