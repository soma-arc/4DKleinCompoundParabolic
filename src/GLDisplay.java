import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import static java.lang.Math.*;

public class GLDisplay implements GLEventListener, MouseListener, MouseMotionListener, KeyListener{
	public static void main(String[] args) {
		new GLDisplay();
	}
	
	public static final int INTERFACE_WIDTH = 250;
	private static final int INITIAL_WIDTH = 1024;
	private static final int INITIAL_HEIGHT = 640;
	private GL gl;
	private GL2 gl2;
	private GLUT glut;
	private GLCanvas canvas;
	JFrame frame;
	GLInterface glInterface;
	SQLiteHandler db;
	
	ArrayList<Point3D> p3DList = new ArrayList<Point3D>();
	ArrayList<Line3D> l3DList = new ArrayList<Line3D>();

	int prevMouseX, prevMouseY;
	double angleX, angleY;
	int translateX = 0;
	int translateY = 0;
	boolean isAnimation = false;
	Quaternion[] fixPoint = new Quaternion[6 + 1];
	int maxLevel = 10;
	Matrix[] gens = new Matrix[4 +1];
	Quaternion fix_ab;
	public GLDisplay() {
		// 3Dを描画するコンポーネント
		canvas = new GLCanvas();
		addListeners(canvas);
		frame = new JFrame("Klein");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		
		try{
			db = new SQLiteHandler("klein.db");
		}catch(Exception ex){
			JOptionPane.showMessageDialog(frame, "データベースに接続できませんでした");
		}
		
		canvas.setMaximumSize(new Dimension(INITIAL_WIDTH - INTERFACE_WIDTH, INITIAL_HEIGHT));
		frame.getContentPane().add(canvas);
		glInterface = new GLInterface(frame, this, db);
		frame.getContentPane().add(glInterface);
		
		final Animator animator = new Animator(canvas);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				animator.stop();
				System.exit(0);
			}
		});
		
		
		
		frame.setVisible(true);
		animator.start();	
	}

	public void init(GLAutoDrawable drawable) {
		// 初期化処理
		gl = drawable.getGL();
		gl2 = gl.getGL2();
		glut = new GLUT();  
		//gl2.glLightf( gl2.GL_LIGHT0, gl2.GL_LINEAR_ATTENUATION, 0.01f);
		gl2.glEnable(gl2.GL_CULL_FACE);
		//gl2.glEnable(gl2.GL_DEPTH_TEST);
	}

	public void reshape(GLAutoDrawable drawable,
			int x, int y,
			int width, int height) {
		canvas.setMaximumSize(new Dimension(frame.getWidth() - INTERFACE_WIDTH, frame.getHeight()));
		glInterface.setMaximumSize(new Dimension(INTERFACE_WIDTH, frame.getHeight()));
		canvas.setMinimumSize(new Dimension(frame.getWidth() - INTERFACE_WIDTH, frame.getHeight()));
		glInterface.setMinimumSize(new Dimension(INTERFACE_WIDTH, frame.getHeight()));
		// 描画領域変更処理
		float ratio = (float)height / (float)width;

		gl2.glViewport(0, 0, width, height);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-1.0f, 1.0f, -ratio, ratio,5.0f, 40.0f);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		gl2.glTranslatef(0.0f, 0.0f, -20.0f);
		//    	  gl2.glViewport( 0, 0, width, height );
		//    	  gl2.glMatrixMode( gl2.GL_PROJECTION );
		//    	  gl2.glLoadIdentity();
		//    	  gl2.glOrtho( -width/2, width/2, -height/2, height/2 ,-0.001,0.001);
	}
	float expansion = 1.0f;
	public int[] followTags;
	public void display(GLAutoDrawable drawable){ 
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl2.glPushMatrix();
		gl2.glScalef(expansion, expansion, expansion);
		gl2.glRotatef((float)angleX, 1.0f, 0.0f, 0.0f);
		gl2.glRotatef((float)angleY, 0.0f, 1.0f, 0.0f);
		gl2.glTranslatef(-translateX, translateY, 0);

		
		gl2.glCullFace(gl2.GL_FRONT);

		gl2.glColor3d(255, 255, 255);
		gl2.glBegin(gl2.GL_LINES);
		//System.out.println(l3DList.size());
		synchronized(l3DList){
			for(Line3D l : l3DList){
				int level1Gen = l.level1GenIndex;
				switch (level1Gen) {
				case 1:
					gl2.glColor3d(255, 0, 0);
					break;
				case 2:
					gl2.glColor3d(0, 255, 0);
					break;
				case 3:
					gl2.glColor3d(255, 0, 255);//violet
					break;
				case 4:
					gl2.glColor3d(255, 255, 0);//yellow
					break;
				default:
					break;
				}
				gl2.glVertex3d(l.x1  * expansion , l.y1 * expansion, l.z1 * expansion);
				gl2.glVertex3d(l.x2  * expansion , l.y2 * expansion, l.z2 * expansion);
				int count = 0;
				if(followTags != null){
					for(int i = 1 ; i < l.getTags().length && i < followTags.length ; i++){
						int tag = l.getTags()[i];
						if(tag == followTags[i]){
							count++;
						}
					}
					if(count == l.getTags().length - 1){
						gl2.glColor3d(255, 255, 255);
						gl2.glVertex3d(l.x1  * expansion , l.y1 * expansion, l.z1 * expansion);
						gl2.glVertex3d(100, 100, 100);
					}
				}
			}
		}
		gl2.glEnd();
		gl2.glPopMatrix();
	}

	private void addListeners(GLCanvas canvas){
		canvas.addGLEventListener(this);

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				prevMouseX = e.getX();
				prevMouseY = e.getY();
			}
		});

		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();

				Dimension size = e.getComponent().getSize();

				// 回転量の算出
				// 端から端までで，360度回転するようにする
				float thetaY = 360.0f * ((float)(x-prevMouseX)/size.width);
				float thetaX = 360.0f * ((float)(prevMouseY-y)/size.height);

				// 角度の更新
				angleX -= thetaX;
				angleY += thetaY;

				// 現在のマウスの位置を保存
				prevMouseX = x;
				prevMouseY = y;
			}
		});

		canvas.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e){
				int keyCode = e.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_UP:
					translateY++;
					break;
				case KeyEvent.VK_RIGHT:
					translateX++;
					break;
				case KeyEvent.VK_DOWN:
					translateY--;
					break;
				case KeyEvent.VK_LEFT:
					translateX--;
					break;
				case KeyEvent.VK_MINUS:
					if(expansion <= 0.01){
						expansion -= 0.001;
					}else if(expansion <= 0.1){
						expansion -=0.01;
					}else{
						expansion -= 0.1;
					}
					break;
				default:
					break;
				}

				int mod = e.getModifiersEx();
				int key = e.getKeyChar();
				if (key == '+' /*&& (mod & java.awt.event.InputEvent.SHIFT_DOWN_MASK) != 0*/){
					expansion += 0.1;
				}	
			}
		});
	}

	public void displayChanged(GLAutoDrawable drawable,
			boolean modeChanged,
			boolean deviceChanged) {
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseExited(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mousePressed(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
