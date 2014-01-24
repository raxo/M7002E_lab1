
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import com.jogamp.opengl.util.*;

public class SimpleScene implements GLEventListener {
	
	Vector<MyObject> myObjects;
	
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
        
        canvas.addGLEventListener(new SimpleScene());
        
        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //animator.add(canvas);
        animator.start();
    }
    
    public SimpleScene() {
    	
	}
    
    abstract class MyObject {
    	public abstract void render(GLAutoDrawable drawable, float z);
    	protected float generateRandom(float min, float max) {
    		Random r = new Random();
    		return r.nextFloat() * (max - min) + min;
    	}
    	protected int generateRandom(int min, int max) {
    		Random r = new Random();
    		return r.nextInt(max-min)+min;
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
    	
    	public void render(GLAutoDrawable drawable, float z) {
    		GL2 gl = drawable.getGL().getGL2();
    	    
    		gl.glPushMatrix();

    		gl.glBegin(GL2.GL_QUADS);
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glColor3f(0.0f, 0.0f, 1.0f);
    		gl.glPopAttrib();
    		
    		gl.glVertex3f(x, y, z);
    		gl.glVertex3f(x, y-d, z);
    		gl.glVertex3f(x+d, y-d, z);
    		gl.glVertex3f(x+d, y, z);
    		
    		gl.glPopMatrix();
    		gl.glEnd();
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
    	public SquareBasedPyramid() {
    		x = generateRandom(-0.8f, 0.0f);
    		y = generateRandom(-0.8f, 0.0f);
    		levels = generateRandom(2, 20);
    		squareDiameter = generateRandom(0.01f, 0.06f);
		}
    	
		public void render(GLAutoDrawable drawable, float z) {
			float d = squareDiameter;
    		GL2 gl = drawable.getGL().getGL2();
    		gl.glPushMatrix();
    		gl.glBegin(GL2.GL_LINE_LOOP);
    		

    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glColor3f(1.0f, 0.0f, 1.0f);
    		gl.glPopAttrib();
    		
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
    		gl.glPopMatrix();
    		gl.glEnd();
		}
    }
    
    class Star extends MyObject {

    	private float x;
    	private float y;
    	private float r;
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
    	
		public void render(GLAutoDrawable drawable, float z) {
			GL2 gl = drawable.getGL().getGL2();
    		
    		
    		gl.glPushMatrix();
    		gl.glBegin(GL2.GL_TRIANGLES);
    		
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glColor3f(0.0f, 1.0f, z);
    		gl.glPopAttrib();
    		
    		gl.glVertex3f(x, y+r*1.0f, z);
    		gl.glVertex3f(x-r*0.8f, y-r*0.5f, z);
    		gl.glVertex3f(x+r*0.8f, y-r*0.5f, z);
    		gl.glEnd();
    		
    		
    		gl.glPushMatrix();
    		gl.glBegin(GL2.GL_TRIANGLES);
    		
    		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
    		gl.glColor3f(0.0f, 1.0f, z);
    		gl.glPopAttrib();
    		
    		gl.glVertex3f(x, y-r*1.0f, z);
    		gl.glVertex3f(x+r*0.8f, y+r*0.5f, z);
    		gl.glVertex3f(x-r*0.8f, y+r*0.5f, z);
    		gl.glEnd();
    		
    		gl.glPopMatrix();
		}
		public void renderPolygon(GLAutoDrawable drawable, float z) {
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
    		
    		gl.glVertex3f(x-r, y, z);
    		gl.glVertex3f(x-r*0.3f, y-r*0.2f, z);
    		gl.glVertex3f(x-r*0.6f, y-r*0.8f, z);
    		gl.glVertex3f(x, y-r*0.2f, z);
    		gl.glVertex3f(x+r*0.6f, y-r*0.8f, z);
    		gl.glVertex3f(x+r*0.3f, y-r*0.2f, z);
    		gl.glVertex3f(x+r, y, z);
    		gl.glVertex3f(x+r*0.2f, y+r*0.3f, z);
    		gl.glVertex3f(x, y+r*1.0f, z);
    		gl.glVertex3f(x-r*0.2f, y+r*0.3f, z);
    		gl.glEnd();
    		gl.glPopMatrix();
    		//System.out.println(x+","+y+","+r);
		}
    	
    }

    /**
     * Have more than one viewport where you draw the scene from
     *  a different point of view.
     * Demonstrate some more advanced OpenGL 
     *  implementation (vertex arrays, display lists)
     * Buffer!
     * @author oskkla-9
     *
     */
    class MoreComplexPrimitive extends MyObject {
    	private int indiciesArray[] = {
			0,1,2,3,4,5,6,7
    	}; 
		final int VERTICES=8, VELEMENTS=3, 
				BUFFER_SIZE=VERTICES*VELEMENTS*8;
		ByteBuffer vertices = ByteBuffer.allocateDirect(BUFFER_SIZE);
		ByteBuffer indicies = ByteBuffer.allocateDirect(VERTICES*8);
		ByteBuffer colors = ByteBuffer.allocateDirect(BUFFER_SIZE);
    	
    	public MoreComplexPrimitive() {
    		float vertexArray[][] = {
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f},
    			{0.0f,0.0f,1.0f}
    		};
    		float colorsArray[][] = {
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f},
    			{0.0f,1.0f,1.0f}
    		};
    		int indiciesArray[] = {
				0,1,2,3,4,5,6,7
	    	}; 
    		vertices = getBufferFromArrays(vertexArray);
    		colors = getBufferFromArrays(colorsArray);
    		indicies = getBufferFromArrays(indiciesArray);
		}

    	private ByteBuffer getBufferFromArrays(float vertexArray[][]) {
    		int i,j;
    		ByteBuffer ret = ByteBuffer.allocateDirect(BUFFER_SIZE);
    		for(i=0;i<VERTICES;i++){
    			for(j=0; j<VELEMENTS; j++) {
    				ret.putFloat(vertexArray[i][j]);
	    		}
    		}
    		ret.rewind();
    		return ret;
    	}
    	private ByteBuffer getBufferFromArrays(int indiciesArray[]) {
    		int i;
    		ByteBuffer ret = ByteBuffer.allocateDirect(VERTICES*8);
    		for(i=0;i<VERTICES;i++){
				ret.putFloat(indiciesArray[i]);
    		}
    		ret.rewind();
    		return ret;
    	}
    	
    	
    	public void render(GLAutoDrawable drawable, float z) {
    		GL2 gl = drawable.getGL().getGL2();
    		//gl.glViewport(arg0, arg1, arg2, arg3);
    		//gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    		gl.glMatrixMode(GL2.GL_MODELVIEW);
    		gl.glLoadIdentity();
    		
    		float vertexArray[][] = {
    			{0.0f,0.0f,0.5f},
    			{0.0f,-0.5f,0.5f},
    			{0.5f,-0.5f,0.5f},
    			{0.5f,0.5f,0.5f},
    			{0.5f,0.5f,0.5f},
    			{0.5f,0.1f,0.5f},
    			{0.1f,0.1f,0.5f},
    			{0.1f,0.5f,0.5f}
    		};
    		float colorsArray[][] = {
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f},
    			{0.0f,100.0f,100.0f}
    		};
    		vertices = getBufferFromArrays(vertexArray);
    		colors = getBufferFromArrays(colorsArray);
    		//System.out.println("derp");
    		// glXPointer(size, type, stride, pointer):
    		
    	    gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
    		gl.glColorPointer(3,GL2.GL_FLOAT, 0, colors); 
    		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 8);
    		//gl.glDrawElements(GL2.GL_TRIANGLES,1,GL.GL_UNSIGNED_INT, indicies);
    		
    		gl.glFlush();
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
    	myObjects.add(new MoreComplexPrimitive());
    	
    	
		GL2 gl = drawable.getGL().getGL2();
		if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object")){
			 System.out.println("Error: VBO support is missing");
		}
		gl.glMatrixMode(GL2.GL_PROJECTION); 
		gl.glLoadIdentity();
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glClearColor(0f, 0f, 0f ,0f);
		gl.glColor3f(1f, 1f, 1f);
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
        	o.render(drawable, z);
        	z = z+0.1f;
        }
	}
	
	private void update() {
	    // nothing to update yet
	}
}
