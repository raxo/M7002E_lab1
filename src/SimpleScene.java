import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLPointerFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class SimpleScene implements GLEventListener, MouseListener, KeyListener, MouseMotionListener {
	
	private ObjectsData data;
	private MyObject selectedObject = null;
	private GLU glu = new GLU();
	private Command command = Command.UPDATE;
	static final int UPDATE = 0, SELECT = 1;
	int mouseX = 0, mouseY = 0;
	int mouseXPressed = 0, mouseYPressed = 0;
	float mouseZ = 0;
	double[] joglPoint = {0,0,0};
	
	KeyboardBuffer KeysCurrentlyPressed = new KeyboardBuffer();
	
	class KeyboardBuffer {
		public boolean square = false;
		public boolean pyramid = false;
		public boolean sphare = false;
		public boolean star = false;
		public boolean light = false;
		public boolean rotate = false;
		public boolean move = false;
		public boolean resize = false;
		public boolean colorAmbient = false;
		public boolean colorSpecular = false;
		public boolean colorDiffuse = false;
		public boolean shininess = false;
		public boolean focusSelected = false;
	}
	
	public enum Command {
		UPDATE, SELECT, MOUSE_PRESSED, TRANSLATE_MOUSE
	}
	
    public static void main(String[] args) {
        new SimpleScene();
    }
    
    public SimpleScene() {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(400,400);
        frame.add(canvas);
        frame.setVisible(true);
        
        // by default, an AWT Frame doesn't do anything when you click
        // the close button; this bit of code will terminate the program when
        // the window is asked to close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        canvas.addGLEventListener(this);
        canvas.addMouseListener(this);
        canvas.addKeyListener(this);
        canvas.addMouseMotionListener(this);
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //animator.add(canvas);
        animator.start();
	}
    
    class ObjectsData implements ClipboardOwner  {
    	
    	private Vector<MyObject> myObjects;
    	
    	final public String[] validObjects = {
			"star",
			"pyramid",
			"square",
			"light",
			"sphere"
    	};
    	
    	public ObjectsData() {
    		myObjects = new Vector<MyObject>();
		}

    	public void addObject(MyObject o) {
    		myObjects.add(o);
    	}
    	public boolean addObject(String s) {
    		String[] objectArray;
    		float x,y,z,r,rotation, shininess;
    		float[] colorAmbient = new float[4], colorSpecular = new float[4], colorDiffuse = new float[4];
    		boolean success = true;
			MyObject myObject = null;
			
    		objectArray = s.split(",");
    		
			if(objectArray.length >= 19) {
    			if(Arrays.asList(validObjects).contains(objectArray[0])) {
    				x = Float.valueOf(objectArray[1]).floatValue();
    				y = Float.valueOf(objectArray[2]).floatValue();
    				z = Float.valueOf(objectArray[3]).floatValue();
    				r = Float.valueOf(objectArray[4]).floatValue();
    				rotation = Float.valueOf(objectArray[5]).floatValue();
    				shininess = Float.valueOf(objectArray[6]).floatValue();
    				colorAmbient[0] = Float.valueOf(objectArray[7]).floatValue();
    				colorAmbient[1] = Float.valueOf(objectArray[8]).floatValue();
    				colorAmbient[2] = Float.valueOf(objectArray[9]).floatValue();
    				colorAmbient[3] = Float.valueOf(objectArray[10]).floatValue();
    				colorSpecular[0] = Float.valueOf(objectArray[11]).floatValue();
    				colorSpecular[1] = Float.valueOf(objectArray[12]).floatValue();
    				colorSpecular[2] = Float.valueOf(objectArray[13]).floatValue();
    				colorSpecular[3] = Float.valueOf(objectArray[14]).floatValue();
    				colorDiffuse[0] = Float.valueOf(objectArray[15]).floatValue();
    				colorDiffuse[1] = Float.valueOf(objectArray[16]).floatValue();
    				colorDiffuse[2] = Float.valueOf(objectArray[17]).floatValue();
    				colorDiffuse[3] = Float.valueOf(objectArray[18]).floatValue();
    				
    				switch (objectArray[0]) {
					case "star":
						myObject = new Star();
						break;
						
					case "pyramid":
						myObject = new SquareBasedPyramid();
						break;

					case "square":
						myObject = new Square();
						break;
						
					case "light":
						myObject = new LightSource();
						break;
						
					case "sphere":
						myObject = new Sphere();
						break;

					default:
						System.out.println("parsed not supported object: \""+objectArray[0]+"\"");
						success = false;
						break;
					}
					myObject.update(x, y, z, r, colorAmbient, colorSpecular, colorDiffuse, rotation);
					addObject(myObject);
    			} else {
    				success = false;
    			}
			} else {
				success = false;
			}
    		return success;
    	}
    	/**
    	 * 
    	 * @param s  objectType,x,y,z,radie,color1,color2,color3;
    	 * @return
    	 */
    	public boolean addObjects(String s) {
    		boolean success = true;
    		String[] strings = s.split(";");
    		for (String objectString : strings){
    			success = success | addObject(objectString);
    		}
    		return success;
    	}
    	public void deleteAllObjects() {
    		myObjects.clear();
    	}
    	public void deleteObject(MyObject o) {
    		int i = myObjects.indexOf(o);
    		if(i == -1) {
    			return;
    		}
    		myObjects.remove(i);
    	}
    	
    	public void render(GLAutoDrawable drawable) {
    		for (MyObject o : myObjects){
    			o.render(drawable, myObjects.indexOf(o));
    		}
    	}
    	
    	public MyObject getObject(int id) {
    		MyObject ret = null;
    		if(id < 0) {
    			return ret;
    		}
    		if(id >= myObjects.size()) {
    			return ret;
    		}
    		ret = myObjects.get(id);
    		return ret;
    	}

    	public MyObject getNextObject(MyObject o) {
    		int id = myObjects.indexOf(o);
    		if(0 <= id && id+1 <= myObjects.size()-1) {
    			return myObjects.get(id+1);
    		}
    		if(myObjects.size() == 0) {
    			return null;
    		}
			return myObjects.get(0);
    	}
    	public MyObject getPrevObject(MyObject o) {
    		int id = myObjects.indexOf(o);
    		if(0 <= id-1 && id <= myObjects.size()-1) {
    			return myObjects.get(id-1);
    		}
    		if(myObjects.size() == 0) {
    			return null;
    		}
			return myObjects.get(0);
    	}
    	
    	@Override
    	public String toString() {
    		String ret = "";
    		for (MyObject o : myObjects){
    			ret = ret+o.toString()+";";
    		}
    		return ret;
    	}
    	
    	public void saveData() {
    		saveData(this.toString());
    	}
    	public void saveData(String data) {
    		StringSelection stringSelection = new StringSelection(data);
    	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	    clipboard.setContents(stringSelection, this);
    		System.out.println(data);
    	}
    	public void loadData() {
    		String result = "";
    	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	    //odd: the Object param of getContents is not currently used
    	    Transferable contents = clipboard.getContents(null);
    	    if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
    	      try {
    	        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
    	      }
    	      catch (UnsupportedFlavorException | IOException e){
    	        System.out.println(e);
    	        e.printStackTrace();
    	      }
    	    }
    	    System.out.println(result);
    	    addObjects(result);
    	}

		@Override
		public void lostOwnership(Clipboard arg0, Transferable arg1) {
			
		}
    	
    }
    
    //jogamp.org/jogl-demos/src/demos/misc/Picking.java 
    abstract class MyObject {
    	public float x,y,z,r, rotation, shininess;
    	public float[] colorAmbient = new float[4],colorSpecular = new float[4],colorDiffuse = new float[4];
    	
        public MyObject() {
		}
    	
        public void update(float x, float y, float z, float r, float[] colorAmbient, float[] colorSpecular, float[] colorDiffuse, float rotation) {
        	this.x=x;
        	this.y=y;
        	this.z=z;
        	this.r=r;
        	if(colorAmbient.length == 4) {
        		this.colorAmbient=colorAmbient;
        	}
        	if(colorSpecular.length == 4) {
        		this.colorSpecular=colorSpecular;
        	}
        	if(colorDiffuse.length == 4) {
        		this.colorDiffuse=colorDiffuse;
        	}
        	this.rotation = rotation;
        }
        
    	public void render(GLAutoDrawable drawable, int id) {
    		GL2 gl = drawable.getGL().getGL2();
    		id++;
    		gl.glPushName(id);
    		render(drawable);
    		gl.glPopName();
    	}
        
    	protected abstract void render(GLAutoDrawable drawable);
    	
    	protected float generateRandom(float min, float max) {
    		Random r = new Random();
    		return r.nextFloat() * (max - min) + min;
    	}
    	protected int generateRandom(int min, int max) {
    		Random r = new Random();
    		return r.nextInt(max-min)+min;
    	}
    	@Override
    	public String toString() {
    		String ret = "";
    		if(this instanceof Star) {
    			ret = "star";
    		} else if(this instanceof Square) {
    			ret = "square";
    		} else if(this instanceof SquareBasedPyramid) {
    			ret = "pyramid";
    		} else if(this instanceof LightSource) {
    			ret = "light";
    		} else if(this instanceof Sphere) {
    			ret = "sphere";
    		} else {
    			return "";
    		}
    		ret += ","+x+","+y+","+z+","+r+","+rotation+""+","+shininess+"";
    		for (int i = 0; i < colorAmbient.length; i++) {
    			ret += ","+colorAmbient[i];
			}
    		for (int i = 0; i < colorSpecular.length; i++) {
    			ret += ","+colorSpecular[i];
			}
    		for (int i = 0; i < colorDiffuse.length; i++) {
    			ret += ","+colorDiffuse[i];
			}
    		return ret;
    	}
    }
    
    class Square extends MyObject {
    	
    	public Square() {
    		x = generateRandom(0.2f, 0.5f);
    		y = generateRandom(0.2f, 0.8f);
    		r = generateRandom(0.2f, 0.3f);
		    colorAmbient[0] = 0f;
		    colorAmbient[1] = 0f;
		    colorAmbient[2] = 1f;
		    colorAmbient[3] = 1f;
		    colorSpecular[0] = 0f;
		    colorSpecular[1] = 0f;
		    colorSpecular[2] = 1f;
		    colorSpecular[3] = 1f;
		    colorDiffuse[0] = 0f;
		    colorDiffuse[1] = 0f;
		    colorDiffuse[2] = 1f;
		    colorDiffuse[3] = 1f;
		}
    	
    	public void render(GLAutoDrawable drawable) {
    		GL2 gl = drawable.getGL().getGL2();
            float[] shininess = {this.shininess};
    	    
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    		gl.glTranslatef(x,y,z);
    		gl.glRotatef(rotation,0.0f,0.0f,1.0f);
    		gl.glTranslatef(0f,0f,0f);
    		
    		gl.glBegin(GL2.GL_QUADS);

    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shininess, 0);

    		gl.glColor3f(0.0f, 0.0f, 1.0f);
    		
    		gl.glVertex3f(-r, r, z);
    		gl.glVertex3f(-r, -r, z);
    		gl.glVertex3f(r, -r, z);
    		gl.glVertex3f(r, r, z);
    		gl.glEnd();

    		gl.glPopAttrib();
    		gl.glPopMatrix();
    	}
    }
    
    class SquareBasedPyramid extends MyObject {
    	private int levels;
    	private float squareDiameter;
    	
    	public SquareBasedPyramid() {
    		x = generateRandom(0.0f, 0.4f);
    		y = generateRandom(0.0f, 0.4f);
    		r = generateRandom(0.3f, 0.6f);
    		levels = 10;
    		squareDiameter = 0.1f;
		    colorAmbient[0] = 1f;
		    colorAmbient[1] = 0f;
		    colorAmbient[2] = 1f;
		    colorAmbient[3] = 1f;
		    colorSpecular[0] = 1f;
		    colorSpecular[1] = 0f;
		    colorSpecular[2] = 1f;
		    colorSpecular[3] = 1f;
		    colorDiffuse[0] = 1f;
		    colorDiffuse[1] = 0f;
		    colorDiffuse[2] = 1f;
		    colorDiffuse[3] = 1f;
		}
    	
		public void render(GLAutoDrawable drawable) {
			float d = squareDiameter*r;
    		GL2 gl = drawable.getGL().getGL2();
            float[] shininess = {this.shininess};
            
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    		
    		gl.glTranslatef(x,y,z);
    		gl.glRotatef(rotation,0.0f,0.0f,1.0f);
    		gl.glTranslatef(0f,0f,0f);
    		
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		
    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shininess, 0);

    		gl.glColor3f(1.0f, 0.0f, 1.0f);
    		
    		gl.glVertex3f(0, 0, 0); // start
    		for(int i=1; i<=levels; i++) {
        		gl.glVertex3f(d*(i-1), d*i, 0); // up
        		gl.glVertex3f(d*i, d*i, 0); // right
    		}
    		int j=levels;
    		for(int i=levels; i>0; i--) {
    			gl.glVertex3f(d*(j+1), d*i, 0); // right
        		gl.glVertex3f(d*(j+1), d*(i-1),0); // down
        		j++;
    		}
    		gl.glVertex3f(d*(levels*2-1), 0, 0); // end
    		gl.glEnd();
    		gl.glPopAttrib();
    		gl.glPopMatrix();
		}
    }
    
    class Star extends MyObject {

    	public Star() {
    		this.x = generateRandom(0.3f, 0.5f);
    		this.y = generateRandom(0.3f, 0.5f);
    		this.r = generateRandom(0.1f, 0.5f);
		    colorAmbient[0] = 0f;
		    colorAmbient[1] = 1f;
		    colorAmbient[2] = 0f;
		    colorAmbient[3] = 1f;
		    colorSpecular[0] = 0f;
		    colorSpecular[1] = 1f;
		    colorSpecular[2] = 0f;
		    colorSpecular[3] = 1f;
		    colorDiffuse[0] = 0f;
		    colorDiffuse[1] = 1f;
		    colorDiffuse[2] = 0f;
		    colorDiffuse[3] = 1f;
		}
    	
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
            float[] shininess = {this.shininess*128f};
    		
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    		gl.glTranslatef(x,y,z);
    		gl.glRotatef(rotation,0.0f,0.0f,1.0f);
    		gl.glTranslatef(0f,0f,0f);

    		gl.glBegin(GL2.GL_TRIANGLES);
    		
    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
    		
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		
    		gl.glVertex3f(0, 0+r*1.0f, 0);
    		gl.glVertex3f(0-r*0.8f, 0-r*0.5f, 0);
    		gl.glVertex3f(0+r*0.8f, 0-r*0.5f, 0);
    		gl.glEnd();

    		gl.glBegin(GL2.GL_TRIANGLES);
    		
    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
    		
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		
    		gl.glVertex3f(0, 0-r*1.0f, 0);
    		gl.glVertex3f(0+r*0.8f, 0+r*0.5f, 0);
    		gl.glVertex3f(0-r*0.8f, 0+r*0.5f,0);
    		

            
    		gl.glEnd();

    		gl.glPopAttrib();
    		gl.glPopMatrix();
		}
    }

    class Sphere extends MyObject {

    	public Sphere() {
    		this.x = generateRandom(0.3f, 0.5f);
    		this.y = generateRandom(0.3f, 0.5f);
    		this.r = generateRandom(0.1f, 0.3f);
		    colorAmbient[0] = 0.1f;
		    colorAmbient[1] = 0.1f;
		    colorAmbient[2] = 0f;
		    colorAmbient[3] = 1f;
		    colorSpecular[0] = 1f;
		    colorSpecular[1] = 0.1f;
		    colorSpecular[2] = 0.0f;
		    colorSpecular[3] = 1f;
		    colorDiffuse[0] = 1f;
		    colorDiffuse[1] = 0.1f;
		    colorDiffuse[2] = 0.0f;
		    colorDiffuse[3] = 1f;
		    shininess = 1f;
		}
		@Override
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			GLUT glut = new GLUT();
            float[] shininess = {this.shininess*128f};

		    gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    		gl.glTranslatef(x,y,z);
    		gl.glRotatef(rotation,0.0f,0.0f,1.0f);
    		gl.glTranslatef(0f,0f,0f);
    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		    glut.glutSolidSphere(r,20,20);
    		gl.glPopAttrib();
		    gl.glPopMatrix();
		}
    }
    
    class LightSource extends MyObject {
    	public LightSource() {
			x = 0f;
			y = 0f;
			z=10f;
		    colorAmbient[0] = 0.1f;
		    colorAmbient[1] = 0.1f;
		    colorAmbient[2] = 0.1f;
		    colorAmbient[3] = 1f;
		    colorSpecular[0] = 1f;
		    colorSpecular[1] = 0.6f;
		    colorSpecular[2] = 0.0f;
		    colorSpecular[3] = 1f;
		    colorDiffuse[0] = 1f;
		    colorDiffuse[1] = 1f;
		    colorDiffuse[2] = 1f;
		    colorDiffuse[3] = 1f;
		}
    	
    	@Override
    	public void update(float x, float y, float z, float r, float[] colorAmbient, float[] colorSpecular, float[] colorDiffuse, float rotation) {
    		super.update(x,y,z,r,colorAmbient, colorSpecular, colorDiffuse,rotation);
    		z = z+1;
    	}
    	
		@Override
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
		    float[] pos = {x,y,z, 1};

		    gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, colorAmbient, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, colorSpecular, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, colorDiffuse, 0);
		    gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, pos, 0);
    		gl.glPopAttrib();
		    gl.glPopMatrix();
		}
		@Override
		protected void finalize() throws Throwable {
			super.finalize();

			//gl.glDisable( GL2.GL_LIGHT0 );
		}
    }	
    
	@Override
	public void display(GLAutoDrawable drawable) {
		update();
	    render(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// https://docs.google.com/file/d/0B9hhZie2D-fEZGI2NTZhZTMtYWYwNS00NTljLWFiNGQtM2UyNTYyNjAzNDYy/edit?hl=en

		GL2 gl = drawable.getGL().getGL2();
		
    	data = new ObjectsData();
		
    	
		gl.glShadeModel(GL2.GL_SMOOTH);
    	gl.glEnable(GL2.GL_LIGHTING);
    	gl.glEnable(GL2.GL_LIGHT0);
    	gl.glEnable(GL2.GL_DEPTH_TEST);
    	gl.glDepthFunc(GL2.GL_LEQUAL);
    	gl.glDepthFunc(GL.GL_LESS);
        //gl.glEnable(GL2.GL_NORMALIZE);
        //gl.glEnable(GL2.GL_CULL_FACE);
    	
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0,0,0.0f,1);
		//gl.glColor3f(1f, 1f, 1f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0f,1.0f,0.0f,1.0f);
	}
	
	// http://www.java-tips.org/other-api-tips/jogl/how-to-use-gluunproject-in-jogl.html
	public double[] convertToGL(GL gl, int x, int y, float z) {
		int[] viewport = new int[4];
	    double mvmatrix[] = new double[16];
	    double projmatrix[] = new double[16];
	    int realy = 0;
	    double wcoord[] = new double[4];
		
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        ((GL2GL3) gl).glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
        ((GL2GL3) gl).glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
        realy = viewport[3] - (int) y - 1;
        glu.gluUnProject((double) x, (double) realy, z, 
            mvmatrix, 0,
            projmatrix, 0, 
            viewport, 0, 
            wcoord, 0);
		
		double[] ret = { wcoord[0],wcoord[1],wcoord[2]};
		return ret;
	}
	
	private void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW); 
		gl.glLoadIdentity();
	    
	    // black
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    
        int id;
        
        if(command == Command.UPDATE) {
        	data.render(drawable);
        } else if(command == Command.SELECT) {
        	
        	int buffsize = 512;
            double x = (double) mouseX, y = (double) mouseY;
            int[] viewPort = new int[4];
            IntBuffer selectBuffer = Buffers.newDirectIntBuffer(buffsize);
            int hits = 0;
            gl.glGetIntegerv(GL2.GL_VIEWPORT, viewPort, 0);
            gl.glSelectBuffer(buffsize, selectBuffer);
            gl.glRenderMode(GL2.GL_SELECT);
            gl.glInitNames();
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            glu.gluPickMatrix(x, (double) viewPort[3] - y, 5.0d, 5.0d, viewPort, 0);
            glu.gluOrtho2D(0.0d, 1.0d, 0.0d, 1.0d);
            data.render(drawable);
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glPopMatrix();
            //gl.glFlush();
            hits = gl.glRenderMode(GL2.GL_RENDER);
            id = getClickedId(hits, selectBuffer);
            System.out.println("Clickaed id:"+id+".");
    		if(KeysCurrentlyPressed.focusSelected == false) { // dont select new if focus is on!
    			selectedObject = data.getObject(id);
    		}
            if(selectedObject != null) {
            	System.out.println(id+":"+selectedObject.toString());
            	mouseZ = selectedObject.z;
            } else {
            	mouseZ = 0;
            }
        	joglPoint = convertToGL(gl, mouseX, mouseY, mouseZ);
        	command = Command.UPDATE;
        } else if(command == Command.TRANSLATE_MOUSE) {
    		joglPoint = convertToGL(gl, mouseX, mouseY, mouseZ);
        	data.render(drawable);
        	command = Command.UPDATE;
        } else {
        	command = Command.UPDATE;
        }
	}
	
	
	
	private int getClickedId(int hits, IntBuffer buffer) {
		int offset = 0;
		int names = -1;
		int ret = -1;
      	float z1, z2;
      	for (int i=0;i<hits;i++) {
      		names = buffer.get(offset); offset++;
      		z1 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
            z2 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
      		for (int j=0;j<names;j++) {
      			ret = buffer.get(offset);
      			offset++;
            }
        }
      	if(ret == -1) {
      		return -1;
      	}
      	return ret-1;
	}
	
	private void update() {
	    // nothing to update yet
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		mouseXPressed = e.getX();
		mouseYPressed = e.getY();
		if (e.getButton() != MouseEvent.BUTTON1) {
			KeysCurrentlyPressed.focusSelected = true;
		}
		command = Command.SELECT;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(selectedObject == null && (
				KeysCurrentlyPressed.square || 
				KeysCurrentlyPressed.star || 
				KeysCurrentlyPressed.sphare || 
				KeysCurrentlyPressed.pyramid || 
				KeysCurrentlyPressed.light)) {
			MyObject o = null;
			if(KeysCurrentlyPressed.square) {
				o = new Square();
			} else if(KeysCurrentlyPressed.star) {
				o = new Star();
			} else if(KeysCurrentlyPressed.sphare) {
				o = new Sphere();
			} else if(KeysCurrentlyPressed.pyramid) {
				o = new SquareBasedPyramid();
			} else if(KeysCurrentlyPressed.light) {
				o = new LightSource();
			}
			o.x = (float) joglPoint[0];
			o.y = (float) joglPoint[1];
			//o.z = (float) joglPoint[2];
			System.out.println(o);
			data.addObject(o);
			selectedObject = o;
		}

		if (e.getButton() != MouseEvent.BUTTON1) {
			KeysCurrentlyPressed.focusSelected = false;
		}
		
		//selectedObject = null;
		//mouseZ = 0;
		command = Command.UPDATE;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            data.loadData();
        } else if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            data.saveData();
        } else if ((e.getKeyCode() == KeyEvent.VK_DELETE) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            data.deleteAllObjects();
        } else if ((e.getKeyCode() == KeyEvent.VK_DELETE) && selectedObject != null) {
            data.deleteObject(selectedObject);
        } else if ((e.getKeyCode() == KeyEvent.VK_X) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            if(selectedObject != null) {
	            data.saveData(selectedObject.toString());
	            data.deleteObject(selectedObject);
            }
        } else if(e.getKeyCode() == KeyEvent.VK_1) {
        	KeysCurrentlyPressed.sphare = true;
        } else if(e.getKeyCode() == KeyEvent.VK_2) {
        	KeysCurrentlyPressed.star = true;
        } else if(e.getKeyCode() == KeyEvent.VK_3) {
        	KeysCurrentlyPressed.square = true;
        } else if(e.getKeyCode() == KeyEvent.VK_4) {
        	KeysCurrentlyPressed.pyramid = true;
        } else if(e.getKeyCode() == KeyEvent.VK_5) {
        	KeysCurrentlyPressed.light = true;
        } else if(e.getKeyCode() == KeyEvent.VK_R) {
        	KeysCurrentlyPressed.rotate = true;
        } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	KeysCurrentlyPressed.resize = true;
        } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
        	KeysCurrentlyPressed.move = true;
        } else if(e.getKeyCode() == KeyEvent.VK_A) {
        	KeysCurrentlyPressed.colorAmbient = true;
        } else if(e.getKeyCode() == KeyEvent.VK_S) {
        	KeysCurrentlyPressed.colorSpecular = true;
        } else if(e.getKeyCode() == KeyEvent.VK_D) {
        	KeysCurrentlyPressed.colorDiffuse = true;
        } else if(e.getKeyCode() == KeyEvent.VK_F) {
        	KeysCurrentlyPressed.shininess = true;
        } else if(selectedObject != null && e.getKeyCode() == KeyEvent.VK_PLUS) {
        	if((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
        		selectedObject.z = selectedObject.z+1f;
        	} else {
        		selectedObject.z = selectedObject.z+0.1f;
        	}
        } else if(selectedObject != null && e.getKeyCode() == KeyEvent.VK_MINUS) {
        	if((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
        		selectedObject.z = selectedObject.z-1f;
        	} else {
        		selectedObject.z = selectedObject.z-0.1f;
        	}
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if(e.getKeyCode() == KeyEvent.VK_1) {
        	KeysCurrentlyPressed.sphare = false;
        } else if(e.getKeyCode() == KeyEvent.VK_2) {
        	KeysCurrentlyPressed.star = false;
        } else if(e.getKeyCode() == KeyEvent.VK_3) {
        	KeysCurrentlyPressed.square = false;
        } else if(e.getKeyCode() == KeyEvent.VK_4) {
        	KeysCurrentlyPressed.pyramid = false;
        } else if(e.getKeyCode() == KeyEvent.VK_5) {
        	KeysCurrentlyPressed.light = false;
        } else if(e.getKeyCode() == KeyEvent.VK_R) {
        	KeysCurrentlyPressed.rotate = false;
        } else if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	KeysCurrentlyPressed.resize = false;
        } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
        	KeysCurrentlyPressed.move = false;
        } else if(e.getKeyCode() == KeyEvent.VK_A) {
        	KeysCurrentlyPressed.colorAmbient = false;
        } else if(e.getKeyCode() == KeyEvent.VK_S) {
        	KeysCurrentlyPressed.colorSpecular = false;
        } else if(e.getKeyCode() == KeyEvent.VK_D) {
        	KeysCurrentlyPressed.colorDiffuse = false;
        } else if(e.getKeyCode() == KeyEvent.VK_F) {
        	KeysCurrentlyPressed.shininess = false;
        } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
        	selectedObject = data.getNextObject(selectedObject);
        } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
        	selectedObject = data.getPrevObject(selectedObject);
        }
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		command = Command.TRANSLATE_MOUSE;
		if(selectedObject != null && KeysCurrentlyPressed.move) {
			selectedObject.x = (float) joglPoint[0];
			selectedObject.y = (float) joglPoint[1];
			//selectedObject.z = (float) joglPoint[2];
		} else if(selectedObject != null){ 
			float xDiff = (float) joglPoint[0] - selectedObject.x;
			float yDiff = (float) joglPoint[1] - selectedObject.y;
			int colorMultiplyer=3;
			boolean drawVertical; // 0 right, 2 left, 3 down, 4 up. 
			
			if (Math.abs(mouseXPressed-mouseX) > Math.abs(mouseYPressed-mouseY)) { // horizontal
				drawVertical = false;
	        } else { // vertical
	        	drawVertical = true;
	        }
			
			
			if(KeysCurrentlyPressed.resize) {
				selectedObject.r = Math.abs(xDiff);
			} else if(KeysCurrentlyPressed.shininess) {
				if(Math.abs(xDiff) > 1f) {
					xDiff = 1f;
				}
				selectedObject.shininess = Math.abs(xDiff);
			} else if(KeysCurrentlyPressed.rotate) {
				selectedObject.rotation += (Math.abs(xDiff))*360*0.1f;
				if(selectedObject.rotation > 360f) {
					selectedObject.rotation -= 360f;
				} else if(selectedObject.rotation < 360f) {
					selectedObject.rotation += 360f;
				}
			} else if(KeysCurrentlyPressed.colorAmbient) {
				if(Math.abs(xDiff*colorMultiplyer) > 1f) {
					xDiff = 1f/colorMultiplyer;
				}
				if(Math.abs(yDiff*colorMultiplyer) > 1f) {
					yDiff = 1f/colorMultiplyer;
				}
				if(xDiff < 0 && !drawVertical) {
					selectedObject.colorAmbient[0] = -xDiff*colorMultiplyer;
				} else if(!drawVertical) {
					selectedObject.colorAmbient[2] = xDiff*colorMultiplyer;
				}
				if(yDiff < 0 && drawVertical) {
					selectedObject.colorAmbient[3] = -yDiff*colorMultiplyer;
				} else if(drawVertical) {
					selectedObject.colorAmbient[1] = yDiff*colorMultiplyer;
				}
			} else if(KeysCurrentlyPressed.colorSpecular) {
				if(Math.abs(xDiff) > 1f) {
					xDiff = 1f;
				}
				if(Math.abs(yDiff) > 1f) {
					yDiff = 1f;
				}
				if(xDiff < 0 && !drawVertical) {
					selectedObject.colorSpecular[0] = -xDiff*colorMultiplyer;
				} else if(!drawVertical) {
					selectedObject.colorSpecular[2] = xDiff*colorMultiplyer;
				}
				if(yDiff < 0 && drawVertical) {
					selectedObject.colorSpecular[3] = -yDiff*colorMultiplyer;
				} else if(drawVertical) {
					selectedObject.colorSpecular[1] = yDiff*colorMultiplyer;
				}
			} else if( KeysCurrentlyPressed.colorDiffuse) {
				if(Math.abs(xDiff) > 1f) {
					xDiff = 1f;
				}
				if(Math.abs(yDiff) > 1f) {
					yDiff = 1f;
				}
				if(xDiff < 0 && !drawVertical) {
					selectedObject.colorDiffuse[0] = -xDiff*colorMultiplyer;
				} else if(!drawVertical) {
					selectedObject.colorDiffuse[2] = xDiff*colorMultiplyer;
				}
				if(yDiff < 0 && drawVertical) {
					selectedObject.colorDiffuse[3] = -yDiff*colorMultiplyer;
				} else if(drawVertical) {
					selectedObject.colorDiffuse[1] = yDiff*colorMultiplyer;
				}
			}
			System.out.println(selectedObject);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
}
