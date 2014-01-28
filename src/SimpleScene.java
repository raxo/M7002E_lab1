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
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class SimpleScene implements GLEventListener, MouseListener, KeyListener, MouseMotionListener {
	
	private ObjectsData data;
	private GLU glu = new GLU();
	private Command command = Command.UPDATE;
	static final int UPDATE = 0, SELECT = 1;
	int mouseX = 0, mouseY = 0;
	
	public enum Command {
		UPDATE, SELECT
	}
	
    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(400, 400);
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
        SimpleScene app = new SimpleScene();
        
        canvas.addGLEventListener(app);
        canvas.addMouseListener(app);
        canvas.addKeyListener(app);
        canvas.addMouseMotionListener(app);
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //animator.add(canvas);
        animator.start();
    }
    
    public SimpleScene() {
    	
	}
    
    class ObjectsData implements ClipboardOwner  {
    	
    	private Vector<MyObject> myObjects;
    	private GL2 gl;
        private GLU glu;
    	
    	final public String[] validObjects = {
			"star",
			"pyramid",
			"square",
			"light",
			"sphere"
    	};
    	
    	public ObjectsData(GL2 gl, GLU glu) {
    		myObjects = new Vector<MyObject>();
            this.gl = gl;
            this.glu = glu;
		}

    	public void addObject(MyObject o) {
    		myObjects.add(o);
    	}
    	public boolean addObject(String s) {
    		String[] objectArray;
    		float x,y,z,r,color1,color2,color3;
    		boolean success = true;
			MyObject myObject = null;
			
    		objectArray = s.split(",");
    		
			if(objectArray.length >= 8) {
    			if(Arrays.asList(validObjects).contains(objectArray[0])) {
    				x = Float.valueOf(objectArray[1]).floatValue();
    				y = Float.valueOf(objectArray[2]).floatValue();
    				z = Float.valueOf(objectArray[3]).floatValue();
    				r = Float.valueOf(objectArray[4]).floatValue();
    				color1 = Float.valueOf(objectArray[5]).floatValue();
    				color2 = Float.valueOf(objectArray[6]).floatValue();
    				color3 = Float.valueOf(objectArray[7]).floatValue();
    				
    				switch (objectArray[0]) {
					case "star":
						myObject = new Star(gl, glu);
						break;
						
					case "pyramid":
						myObject = new SquareBasedPyramid(gl, glu);
						break;

					case "square":
						myObject = new Square(gl, glu);
						break;
						
					case "light":
						myObject = new LightSource(gl, glu);
						break;
						
					case "sphere":
						myObject = new Sphere(gl, glu);
						break;

					default:
						System.err.println("parsed not supported object: \""+objectArray[0]+"\"");
						success = false;
						break;
					}
					myObject.update(x, y, z, r, color1, color2, color3);
					addObject(myObject);
    			} else {
    				success = false;
    			}
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
    	
    	public void render(GLAutoDrawable drawable) {
    		for (MyObject o : myObjects){
    			o.render(drawable, myObjects.indexOf(o));
    		}
    	}
    	
    	public MyObject getObject(float x,float y,float z) {
    		MyObject ret = null;
    		return ret;
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
    		String data = this.toString();
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
    	protected float x,y,z,r,color1,color2,color3;
    	private GLU glu;
    	private GL2 gl;
    	
        public MyObject(GL2 gl, GLU glu) {
        	this.gl = gl;
            this.glu = glu;
		}
    	
        public void update(float x, float y, float z, float r, float color1, float color2, float color3) {
        	this.x=x;
        	this.y=y;
        	this.z=z;
        	this.r=r;
        	this.color1=color1;
        	this.color2=color2;
        	this.color3=color3;
        }
        
    	public void render(GLAutoDrawable drawable, int id) {
    		gl.glPushName(id);
    		render(drawable);
    		//gl.glPopName();
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
    		return ret+","+x+","+y+","+z+","+r+","+color1+","+color2+","+color3+"";
    	}
    }
    
    class Square extends MyObject {
    	
    	public Square(GL2 gl, GLU glu) {
    		super(gl, glu);
    		x = generateRandom(-0.5f, 0.5f);
    		y = generateRandom(-0.5f, 0.5f);
    		r = generateRandom(0.2f, 0.5f);
		}
    	
    	public void render(GLAutoDrawable drawable) {
    		GL2 gl = drawable.getGL().getGL2();
            float[] ambiColor = {0.0f, 0.0f, 1.0f, 1f};
            float[] specColor = {0.0f, 0.0f, 1.0f, 1f};
            float[] diffColor = {0.0f, 0.0f, 1.0f, 1f};
    	    float d=r*2;
    	    
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    		gl.glTranslatef(x,y,z);
    		
    		gl.glBegin(GL2.GL_QUADS);

    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffColor, 0);

    		gl.glColor3f(0.0f, 0.0f, 1.0f);
    		
    		gl.glVertex3f(0, 0, 0);
    		gl.glVertex3f(0, -d, 0);
    		gl.glVertex3f(d, -d, 0);
    		gl.glVertex3f(d, 0,0);
    		gl.glEnd();

    		gl.glPopAttrib();
    		gl.glPopMatrix();
    	}
    }
    
    class SquareBasedPyramid extends MyObject {
    	private int levels;
    	private float squareDiameter;
    	
    	public SquareBasedPyramid(GL2 gl, GLU glu) {
    		super(gl, glu);
    		x = generateRandom(0.0f, 0.4f);
    		y = generateRandom(0.0f, 0.4f);
    		levels = 10;
    		squareDiameter = 0.02f;
		}
    	
		public void render(GLAutoDrawable drawable) {
			float d = squareDiameter;
    		GL2 gl = drawable.getGL().getGL2();
            float[] ambiColor = {1.0f, 0.0f, 1.0f, 1f};
            float[] specColor = {1.0f, 0.0f, 1.0f, 1f};
            float[] diffColor = {1.0f, 0.0f, 1.0f, 1f};
            
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    		
    		gl.glTranslatef(x,y,z);
    		
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		

    		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffColor, 0);

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

    	public Star(GL2 gl, GLU glu) {
    		super(gl, glu);
    		this.x = generateRandom(0.3f, 0.5f);
    		this.y = generateRandom(0.3f, 0.5f);
    		this.r = generateRandom(0.1f, 0.5f);
		}
    	
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
            float[] ambiColor = {0.0f, 1.0f, 0.0f, 1f};
            float[] specColor = {0.0f, 1.0f, 0.0f, 1f};
            float[] diffColor = {0.0f, 1.0f, 0.0f, 1f};
    		
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    		gl.glTranslatef(x,y,z);

    		gl.glBegin(GL2.GL_TRIANGLES);

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffColor, 0);
    		
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		
    		gl.glVertex3f(0, 0+r*1.0f, 0);
    		gl.glVertex3f(0-r*0.8f, 0-r*0.5f, 0);
    		gl.glVertex3f(0+r*0.8f, 0-r*0.5f, 0);
    		gl.glEnd();

    		gl.glBegin(GL2.GL_TRIANGLES);

            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffColor, 0);
    		
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

    	public Sphere(GL2 gl, GLU glu) {
    		super(gl, glu);
    		this.x = generateRandom(0.3f, 0.5f);
    		this.y = generateRandom(0.3f, 0.5f);
    		this.r = generateRandom(0.1f, 0.5f);
		}
		@Override
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
			GLUT glut = new GLUT();
            float[] ambiColor = {1f, 0.0f, 0.0f, 1f};
            float[] specColor = {1f, 0.0f, 0.0f, 1f};
            float[] diffColor = {1f, 0.0f, 0.0f, 1f};
            float[] shininess = {0.1f};

		    gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    		gl.glTranslatef(x,y,z);
		    gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
            gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffColor, 0);
            gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		    glut.glutSolidSphere(r,20,20);
    		gl.glPopAttrib();
		    gl.glPopMatrix();
		}
    }
    
    class LightSource extends MyObject {
    	public LightSource(GL2 gl, GLU glu) {
    		super(gl, glu);
			x = 1f;
			y = 1f;
			z=-5f;
		}
		@Override
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
		    float[] noAmbient ={ 0.1f, 0.1f, 0.1f, 1f }; 
		    float[] spec =    { 1f, 0.6f, 0.0f, 1f };
		    float[] diffuse ={ 1f, 1f, 1f, 1f };
		    float[] pos = {x,y,z};
			z=-10f;

		    gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		    gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, noAmbient, 0);
		    gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_SPECULAR, spec, 0);
		    gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_DIFFUSE, diffuse, 0);
		    gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_POSITION, pos, 0);
    		gl.glPopAttrib();
		    gl.glPopMatrix();
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
		
    	data = new ObjectsData(gl, glu);
		
		data.addObject(new Square(gl, glu));
    	data.addObject(new SquareBasedPyramid(gl, glu));
    	data.addObject(new Star(gl, glu));
    	data.addObject(new Sphere(gl, glu));
    	data.addObject(new LightSource(gl, glu));
    	
		gl.glShadeModel(GL2.GL_SMOOTH);
    	gl.glEnable(GL2.GL_LIGHTING);
    	gl.glEnable(GL2.GL_LIGHT0);
    	gl.glEnable(GL2.GL_DEPTH_TEST);
    	gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL2.GL_NORMALIZE);
        //gl.glEnable(GL2.GL_CULL_FACE);
    	
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
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
            gl.glFlush();
            hits = gl.glRenderMode(GL2.GL_RENDER);
            processHits(hits, selectBuffer);
            id = getClickedId(hits, selectBuffer);
            System.out.println("id:"+id);
        	
        	command = Command.UPDATE;
        }
	}
	
	private int getClickedId(int hits, IntBuffer buffer) {
		int offset = 0;
		int names = -1;
      	float z1, z2;
      	for (int i=0;i<hits;i++) {
      		names = buffer.get(offset); offset++;
      		//z1 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
      		//z2 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
      		for (int j=0;j<names;j++) {
      			offset++;
            }
        }
      	if(names == -1) {
      		return -1;
      	}
      	return names-1;
	}
	
	public void processHits(int hits, IntBuffer buffer) {
		System.out.println("---------------------------------");
	      System.out.println(" HITS: " + hits);
	      int offset = 0;
	      int names;
	      float z1, z2;
	      for (int i=0;i<hits;i++)
	        {
	          System.out.println("- - - - - - - - - - - -");
	          System.out.println(" hit: " + (i + 1));
	          names = buffer.get(offset); offset++;
	          z1 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
	          z2 = (float) (buffer.get(offset)& 0xffffffffL) / 0x7fffffff; offset++;
	          System.out.println(" number of names: " + names);
	          System.out.println(" z1: " + z1);
	          System.out.println(" z2: " + z2);
	          System.out.println(" names: ");

	          for (int j=0;j<names;j++)
	            {
	              System.out.print("       " + buffer.get(offset)); 
	              if (j==(names-1))
	                System.out.println("<-");
	              else
	                System.out.println();
	              offset++;
	            }
	          System.out.println("- - - - - - - - - - - -");
	        }
	      System.out.println("---------------------------------");
    }
	
	private void update() {
	    // nothing to update yet
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("mouseClicked");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("mousePressed");
		command = Command.SELECT;
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("mouseReleased");
		command = Command.UPDATE;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            System.out.println("Loading from clipboard.");
            data.loadData();
        } else if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            System.out.println("Saving to clipboard.");
            data.saveData();
        } else if ((e.getKeyCode() == KeyEvent.VK_D) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            System.out.println("Delete.");
            data.deleteAllObjects();
        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyChar()) {
	      case KeyEvent.VK_ESCAPE:
	        System.exit(0);
	        break;

	      default:
	        break;
	    }
	}

	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("keyTyped");
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		System.out.println("mouseDragged");
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
	}
}
