
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.*;

public class SimpleScene implements GLEventListener {
	
	Vector<MyObject> myObjects;
	
    public static void main(String[] args) {
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        Frame frame = new Frame("AWT Window Test");
        frame.setSize(300, 300);
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
        
        canvas.addGLEventListener(new SimpleScene());
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //animator.add(canvas);
        animator.start();
    }
    
    public SimpleScene() {
    	myObjects = new Vector<MyObject>();
    	//myObjects.add(new Square());
    	//myObjects.add(new SquareBasedPyramid(-0.5f,-0.5f,10));
    	myObjects.add(new Star(-0.5f, -0.5f, 1.5f));
	}
    
    abstract class MyObject {
    	public abstract void render(GLAutoDrawable drawable);
    	protected float generateRandom(float min, float max) {
    		Random r = new Random();
    		return r.nextFloat() * (max - min) + min;
    	}
    }
    
    class Square extends MyObject {
    	private float d = 0.1f;
    	private float x = 0.0f;
    	private float y = 0.0f;
    	
    	public Square(float x, float y, float d) {
    		this.x = x;
    		this.y = y;
    		this.d = d;
    		System.out.println("Square: "+x+", "+y+", "+d);
		}
    	public Square() {
    		x = generateRandom(-0.5f, 0.5f);
    		y = generateRandom(-0.5f, 0.5f);
    		d = generateRandom(0.1f, 0.5f);
    		System.out.println("Square: "+x+", "+y+", "+d);
		}
    	
    	public void render(GLAutoDrawable drawable) {
    		GL2 gl = drawable.getGL().getGL2();
    	    
    		//gl.glBegin(GL2.GL_QUADS);
    		//gl.glVertex3f(x, y, 0.0f);
    		//gl.glVertex3f(x, y+d, 0.0f);
    		//gl.glVertex3f(x+d, y, 0.0f);
    		//gl.glVertex3f(x+d, y+d, 0.0f);
    		gl.glColor3f(0.0f, .0f, 1.0f); 
    		gl.glRectf(x, y, x+d, y+d);
    		//gl.glEnd();
    	}
    }
    
    class SquareBasedPyramid extends MyObject {
    	private float x;
    	private float y;
    	private int levels;
    	private float squareDiameter;
    	public SquareBasedPyramid(float x, float y, int levels) {
    		this.x = x;
    		this.y = y;
    		this.levels = levels;
    		squareDiameter = 0.05f;
		}
    	
		public void render(GLAutoDrawable drawable) {
			float d = squareDiameter;
    		GL2 gl = drawable.getGL().getGL2();
    		gl.glPushMatrix();
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		gl.glVertex3f(x, y, 0.0f); // start
    		for(int i=1; i<=levels; i++) {
        		gl.glVertex3f(x+d*(i-1), y+d*i, 0.0f); // up
        		gl.glVertex3f(x+d*i, y+d*i, 0.0f); // right
    		}
    		int j=levels;
    		for(int i=levels; i>0; i--) {
    			gl.glVertex3f(x+d*(j+1), y+d*i, 0.0f); // right
        		gl.glVertex3f(x+d*(j+1), y+d*(i-1), 0.0f); // down
        		j++;
    		}
    		gl.glVertex3f(x+d*(levels*2-1), y, 0.0f); // end
    		gl.glPopMatrix();
    		gl.glEnd();
		}
    }
    
    class Star extends MyObject {

    	private float x;
    	private float y;
    	private float r;
    	public Star(float x, float y, float r) {
    		this.x = x;
    		this.y = y;
    		this.r = r;
		}
    	
		public void render(GLAutoDrawable drawable) {
    		GL2 gl = drawable.getGL().getGL2();
    		
    		gl.glPushMatrix();
    		gl.glBegin(GL2.GL_POLYGON);
    		/*
    		    .9
    		   .10 .8
    		1.        7.
    		  2. 4. 6.  
    		 3.     5.
    		 
    		 */

    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glColor3f(0.0f, 1.0f, 0.0f);
    		gl.glPopAttrib();
    		r=0.8f;
    		x=0.0f;
    		y=0.0f;
    		
    		gl.glVertex3f(x-r, y, 0.0f);
    		gl.glVertex3f(x-r*0.3f, y-r*0.2f, 0.0f);
    		gl.glVertex3f(x-r*0.6f, y-r*0.8f, 0.0f);
    		gl.glVertex3f(x, y-r*0.2f, 0.0f);
    		gl.glVertex3f(x+r*0.6f, y-r*0.8f, 0.0f);
    		gl.glVertex3f(x+r*0.3f, y-r*0.2f, 0.0f);
    		gl.glVertex3f(x+r, y, 0.0f);
    		gl.glVertex3f(x+r*0.2f, y+r*0.3f, 0.0f);
    		gl.glVertex3f(x, y+r*1.0f, 0.0f);
    		gl.glVertex3f(x-r*0.2f, y+r*0.3f, 0.0f);
    		gl.glEnd();
    		gl.glPopMatrix();
    		//System.out.println("rendering star...");
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
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
	private void render(GLAutoDrawable drawable) {
	    GL2 gl = drawable.getGL().getGL2();
	    
	    // black
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	    
        for (MyObject o : myObjects){
        	o.render(drawable);
        }
	}
	
	private void update() {
	    // nothing to update yet
	}
}
