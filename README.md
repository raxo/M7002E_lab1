M7002E_lab1
===========
The program should be compileable. 
If glugen-rt-natives-windows something is missing it can be found at http://jogamp.org/, place it in lib. 

I've used the following as aid: 
 - https://sites.google.com/site/justinscsstuff/jogl-tutorials
 - and picking.java from jogl official website.
 - http://www.java-tips.org/other-api-tips/jogl/how-to-use-gluunproject-in-jogl.html

I've also used the slides from lecture 2,4. 

Everything is in the same class SimpleScene, including nestled objects. 

All extra features is implemented. The fileformat for save/load is up to the user since the clipboard is used. Representation of each object is defined in the abstract class. See examples down below for text format. 

Controlls
===========
 - Spawn objects by clicking+1,2,3,4... 
 - Add lightsource with clicking+5. 
 - Select next object with arrows left and right. 
 - Move selected object in z-axis with + and -. Speed up by pressing ctrl. 
 - Move object with clicking+draging+space. 
 - Reshape object with clicking+draging+shift. 
 - Rotate object with clicking+draging+r. 
 - Load in objects with ctrl+v with valid string in clipboard. 
 - Save all objects to clipboard by ctrl+c. 
 - Cut selected object with ctrl+x. 
 - Delete all objects with ctrl+delete. 
 - Delete selected object with delete. 
 - Escape exits the program. 
 - Change ambient color by clicking+a and drag around.
 - Change specular color by clicking+s and drag around.
 - Change diffuse color by clicking+d and drag around.
 - Change shininess color by clicking+f and drag horizontaly.
 - Do any clicking operation on the currently slected object by using another mouse button then mouse1. 
 
Examples
===========
sphere,0.3690476,0.66107786,0.0,0.18504064,0.0,1.0,0.1,0.1,0.0,1.0,1.0,0.1,0.0,1.0,1.0,0.1,0.0,1.0;star,0.8174603,0.25868264,0.0,0.09126985,154.89276,0.0,0.83928585,0.021556914,0.82440466,0.9305389,1.1011906,0.021556914,0.0059523582,0.15089813,0.19047636,0.34491026,0.1220237,0.025149643;light,0.4047619,0.3988024,0.3,0.0,0.0,0.0,0.1,0.1,0.1,1.0,1.0,0.6,0.0,1.0,1.0,1.0,1.0,1.0;sphere,0.50099206,0.18203592,0.0,0.10396644,0.0,1.0,0.1,0.1,0.0,1.0,1.0,0.1,0.0,1.0,1.0,0.1,0.0,1.0;star,0.3045635,0.7413174,0.0,0.19940478,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0;square,0.5089286,0.19401199,0.0,0.12698412,0.0,0.0,0.0,0.0,1.0,1.0,0.0,0.0,1.0,1.0,0.0,0.0,1.0,1.0;square,0.46924603,0.74011976,0.0,0.1428572,490.39288,0.80357146,0.98809516,0.08263475,0.02678579,0.03233546,0.2410714,0.0,0.023809612,0.014371276,0.08333337,0.06467074,3.0,0.0035927296;pyramid,0.35119048,0.3233533,0.0,0.16567463,0.0,0.0,1.0,0.0,1.0,1.0,1.0,0.0,1.0,1.0,1.0,0.0,1.0,1.0;
