import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.media.opengl.glu.GLU;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.*;

public class SimpleScene implements GLEventListener, MouseListener, KeyListener, MouseMotionListener {
	
	private Vector<MyObject> myObjects;
	private GLU glu = new GLU();
	
    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(1024, 768);
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
    
    class ObjectsData {
    	
    	Vector<MyObject> myObjects;
    	
    	final public String[] validObjects = {
			"star",
			"pyramid",
			"square"
    	};
    	
    	public ObjectsData() {
    		myObjects = new Vector<MyObject>();
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
						myObject = new Star();
						break;
						
					case "pyramid":
						myObject = new SquareBasedPyramid();
						break;
						
					case "square":
						myObject = new Square();
						break;

					default:
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
    		String[] objectArray;
    		for (String objectString : strings){
    			success = success | addObject(objectString);
    		}
    		return success;
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
    	
    }
    
    abstract class MyObject {
    	protected float x,y,z,r,color1,color2,color3;
    	
        public MyObject() {
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
        
    	public abstract void render(GLAutoDrawable drawable);
    	
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
    		} else {
    			return "";
    		}
    		return ret+","+x+","+y+","+z+","+r+","+color1+","+color2+","+color3+";";
    	}
    }
    
    class Square extends MyObject {
    	
    	public Square(float x, float y, float r) {
    		this.x = x;
    		this.y = y;
    		this.r = r;
    		System.out.println("Square: "+x+", "+y+", "+r);
		}
    	public Square() {
    		x = generateRandom(-0.5f, 0.5f);
    		y = generateRandom(-0.5f, 0.5f);
    		r = generateRandom(0.2f, 0.5f);
    		System.out.println("Square: "+x+", "+y+", "+r);
		}
    	
    	public void render(GLAutoDrawable drawable) {
    		GL2 gl = drawable.getGL().getGL2();
    	    float d=r*2;
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);

    		gl.glBegin(GL2.GL_QUADS);
    		gl.glColor3f(0.0f, 0.0f, 1.0f);
    		
    		gl.glVertex3f(x, y, z);
    		gl.glVertex3f(x, y-d, z);
    		gl.glVertex3f(x+d, y-d, z);
    		gl.glVertex3f(x+d, y, z);
    		gl.glEnd();

    		gl.glPopAttrib();
    		gl.glPopMatrix();
    	}
    }
    
    class SquareBasedPyramid extends MyObject {
    	private int levels;
    	private float squareDiameter;
    	public SquareBasedPyramid(float x, float y, int levels) {
    		this.x = x;
    		this.y = y;
    		this.levels = levels;
    		squareDiameter = 0.05f;
		}
    	public SquareBasedPyramid() {
    		x = generateRandom(-0.8f, 0.0f);
    		y = generateRandom(-0.8f, 0.0f);
    		levels = generateRandom(2, 20);
    		squareDiameter = generateRandom(0.01f, 0.06f);
		}
    	
		public void render(GLAutoDrawable drawable) {
			float d = squareDiameter;
    		GL2 gl = drawable.getGL().getGL2();
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		

    		gl.glColor3f(1.0f, 0.0f, 1.0f);
    		
    		gl.glVertex3f(x, y, z); // start
    		for(int i=1; i<=levels; i++) {
        		gl.glVertex3f(x+d*(i-1), y+d*i, z); // up
        		gl.glVertex3f(x+d*i, y+d*i, z); // right
    		}
    		int j=levels;
    		for(int i=levels; i>0; i--) {
    			gl.glVertex3f(x+d*(j+1), y+d*i, z); // right
        		gl.glVertex3f(x+d*(j+1), y+d*(i-1), z); // down
        		j++;
    		}
    		gl.glVertex3f(x+d*(levels*2-1), y, z); // end
    		gl.glEnd();
    		gl.glPopAttrib();
    		gl.glPopMatrix();
		}
    }
    
    class Star extends MyObject {

    	public Star(float x, float y, float r) {
    		if(r == 0.0f){
        		x = generateRandom(-0.5f, 0.5f);
        		y = generateRandom(-0.5f, 0.5f);
        		r = generateRandom(0.05f, 0.4f);
    		}
    		this.x = x;
    		this.y = y;
    		this.r = r;
		}
    	public Star() {
    		this.x = generateRandom(-0.5f, 0.5f);
    		this.y = generateRandom(-0.5f, 0.5f);
    		this.r = generateRandom(0.1f, 0.5f);
		}
    	
		public void render(GLAutoDrawable drawable) {
			GL2 gl = drawable.getGL().getGL2();
    		
    		
    		gl.glPushMatrix();
    		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
    		gl.glBegin(GL2.GL_TRIANGLES);
    		
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		
    		gl.glVertex3f(x, y+r*1.0f, z);
    		gl.glVertex3f(x-r*0.8f, y-r*0.5f, z);
    		gl.glVertex3f(x+r*0.8f, y-r*0.5f, z);
    		gl.glEnd();

    		gl.glBegin(GL2.GL_TRIANGLES);
			
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		
    		gl.glVertex3f(x, y-r*1.0f, z);
    		gl.glVertex3f(x+r*0.8f, y+r*0.5f, z);
    		gl.glVertex3f(x-r*0.8f, y+r*0.5f, z);
    		gl.glEnd();

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

    	myObjects = new Vector<MyObject>();
    	myObjects.add(new Square());
    	myObjects.add(new SquareBasedPyramid());
    	myObjects.add(new Star());
    	
    	
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glClearColor(0,0,0.25f,1);
		//gl.glColor3f(1f, 1f, 1f);
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	private void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW); 
		gl.glLoadIdentity();
	    
	    // black
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    
        float z = 0.0f;
        for (MyObject o : myObjects){
        	o.render(drawable);
        	//z = z+0.1f;
        }
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("mouseReleased");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
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
