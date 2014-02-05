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
 - Move/add lightsource with clicking+5. 
 - Select next object with arrows left and right. 
 - Move selected object in z-axis with + and -. Speed up by pressing ctrl. 
 - Move object with clicking+draging+space. 
 - Reshape object with clicking+draging+shift. 
 - Rotate object with clicking+draging+r. 
 - Load in objects with ctrl+v with valid string in clipboard. 
 - Save all objects to clipboard by ctrl+c. 
 - Cut selected object with ctrl+x. 
 - Delete all objects with ctrl+d. 
 
Examples
===========
star,0.30012453,0.29249617,0.0,0.06973848,0.0,0.0,0.0,684.40344;light,0.20921546,0.2618683,0.09999993,0.0,0.0,0.0,0.0,0.0;sphere,0.18555418,0.50995404,0.0,0.11225948,0.0,0.0,0.0,0.0;sphere,0.10585305,0.17764166,0.0,0.09962639,0.0,0.0,0.0,0.0;sphere,0.4607721,0.13782543,0.0,0.10534169,0.0,0.0,0.0,0.0;sphere,0.38854295,0.4793262,0.0,0.10534169,0.0,0.0,0.0,0.0;sphere,0.0958904,0.35222054,0.0,0.10534169,0.0,0.0,0.0,0.0;sphere,0.5006227,0.32312405,0.0,0.10585308,0.0,0.0,0.0,0.0;sphere,0.27521792,0.10107198,0.0,0.10534169,0.0,0.0,0.0,0.0;sphere,0.6513076,0.46401227,-0.9000001,0.05977583,0.0,0.0,0.0,0.0;sphere,0.6537983,0.63246554,-0.9000001,0.037359893,0.0,0.0,0.0,0.0;light,0.20921546,0.2618683,0.09999993,0.0,0.0,0.0,0.0,0.0;star,0.6363636,0.6385911,-0.6,0.012453288,0.0,0.0,0.0,0.0;star,0.65877956,0.6385911,-0.6,0.012453288,0.0,0.0,0.0,0.0;square,0.6475716,0.6171516,-0.3,0.006226659,0.0,0.0,0.0,0.0;sphere,0.6525529,0.55436444,-0.9000001,0.05105853,0.0,0.0,0.0,0.0;sphere,0.5678705,0.5957121,-0.9000001,0.019925296,0.0,0.0,0.0,0.0;sphere,0.5977584,0.57733536,-0.9000001,0.028642595,0.0,0.0,0.0,0.0;sphere,0.7085928,0.5727412,-0.9000001,0.028642595,0.0,0.0,0.0,0.0;sphere,0.739726,0.5957121,-0.9000001,0.017434657,0.0,0.0,0.0,0.0;pyramid,0.10460773,0.7871363,0.0,0.124533,0.0,0.0,0.0,0.0;square,0.23038605,0.79479325,0.0,0.09962639,0.0,0.0,0.0,223.84564;
