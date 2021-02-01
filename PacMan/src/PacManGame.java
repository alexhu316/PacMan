
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;


public class PacManGame extends JPanel implements KeyListener, ActionListener {

	//The walls of the stage
	Rectangle [] wallsQ1 = new Rectangle[15];
	Rectangle [] wallsQ2 = new Rectangle[15];
	Rectangle [] wallsQ3 = new Rectangle[18];
	Rectangle [] wallsQ4 = new Rectangle[18];
	Rectangle ghostDoor;
	
	int xPos = 190;
	int yPos = 345;
	static int width = 20;
	static int height = 20;
	char direction=' ';
	static Rectangle pacManRect = new Rectangle (190, 343, width, height);
	Rectangle[] ghosts = {new Rectangle (190, 190, 20, 20), new Rectangle (190, 228, 20, 20), new Rectangle (163, 228, 20, 20), new Rectangle (217, 228, 20, 20)}; 
	boolean []ghostsCanMove = {true, true, true, true,};
	char[] ghostDir = {'u','u','u','u'};
	int [] randomGhostDir = {1, 3, 1, 3};
	int [] [] lastGhostPos = new int [4][4];
	boolean ghostsScared=false;
	Image pacManImage = Toolkit.getDefaultToolkit ().getImage ("pac man r.png");
	Image ghostGreen = Toolkit.getDefaultToolkit ().getImage ("ghostGreen.gif");
	Image ghostRed = Toolkit.getDefaultToolkit ().getImage ("ghostRed.gif");
	Image ghostPink = Toolkit.getDefaultToolkit ().getImage ("ghostPink.gif");
	Image ghostOrange = Toolkit.getDefaultToolkit ().getImage ("ghostOrange.gif");
	Image superPacDot = Toolkit.getDefaultToolkit ().getImage ("superPacDot.gif");
	Rectangle[] dots = new Rectangle [240];
	boolean [] dotsExist = new boolean [240];
	Rectangle[] superDots = new Rectangle [4];
	boolean [] superDotsExist = new boolean [4];
	int numDots = 244;
	int eventTime=-601;
	int gameTime=0;
	boolean gameOver=false;
	boolean loser;
	int points = 0;
	Font textFont = new Font("Monospaced", Font.BOLD, 20);  
	int ghostPointMultiplier=1;
	
	AudioClip pacManEating=Applet.newAudioClip (getCompleteURL ("pacman_eating.mp3"));
	AudioClip pacManGhost=Applet.newAudioClip (getCompleteURL ("pacman_eatghost.mp3"));
	AudioClip pacManDeath=Applet.newAudioClip (getCompleteURL ("pacman_death.mp3"));
	AudioClip pacManIntro=Applet.newAudioClip (getCompleteURL ("pacman_intro.mp3"));

	ImageObserver observer = new ImageObserver() {
		public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
			return false;
		}	
	};

	Timer gameTimer;
	boolean cornerReached = false;
	
	static JFrame frame;
	static JPanel panel;
	JFrame insFrame;
	JFrame gameOverFrame;
	JPanel insPanel;
	JPanel gameOverPanel;

	//constructor
	public PacManGame () {
		displayInstructions();
		
		setPreferredSize(new Dimension (400, 480));
		setBackground(Color.BLACK);
		setLayout (new BoxLayout (this, BoxLayout.PAGE_AXIS));

		initializeDots();
		setFocusable(true);
		addKeyListener (this);

		gameTimer = new Timer(30, new TimerEventHandler());
	}

	//This method is called on each frame of action, performing
	//everything that may happen in a given frame
	private class TimerEventHandler implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			if (!gameOver) {
				movePacRect(direction);
				checkForCollision(pacManRect);
				for (int i = 0;  i<4; i++) {
					if (ghostsCanMove[i])
						moveGhost(i);
				}
				repaint((int)pacManRect.getX()-14,(int)pacManRect.getY()-14,47,47);
				if (pacManRect.x>376) {
					repaint(0,228,20,20);
				}
				else if (pacManRect.x<4) {
					repaint(380,228,20,20);
				}
				for (int i = 0; i<4; i++) {
					repaint((int)ghosts[i].getX()-1,(int)ghosts[i].getY()-1,22,22);
				}
				if (ghostsScared&&gameTime-600==eventTime) {
					ghostsScared=false;
					for (int i = 0; i<4; i++) {
						ghostsCanMove[i]=true;
					}
					ghostGreen = Toolkit.getDefaultToolkit ().getImage ("ghostGreen.gif");
					ghostRed = Toolkit.getDefaultToolkit ().getImage ("ghostRed.gif");
					ghostPink = Toolkit.getDefaultToolkit ().getImage ("ghostPink.gif");
					ghostOrange = Toolkit.getDefaultToolkit ().getImage ("ghostOrange.gif");
					ghostPointMultiplier=1;
				}
				for (int i = 0; i<4; i++) {
					if (pacManRect.intersects(ghosts[i])&&(!ghostsScared)) {
						pacManDeath.play();
						loser=true;
						gameOver=true;
					}
					else if (pacManRect.intersects(ghosts[i])&&ghostsScared) {
						pacManGhost.play();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ghosts[i].setLocation(163+27*(int)(Math.random()*3), 228);
						ghostsCanMove[i]=false;
						points+=200*ghostPointMultiplier;
						ghostPointMultiplier*=2;
					}
					
				}
				
				repaint(0,0,60,30);
				if (superDotsExist[0])
					repaint(17, 91, 13, 13);
				if (superDotsExist[1])
					repaint(369, 91, 13, 13);
				if (superDotsExist[2])
					repaint(17, 347, 13, 13);
				if (superDotsExist[3])
					repaint(369, 347, 13, 13);
				if (numDots==0) {
					gameOver=true;
					loser=false;
				}
				gameTime++;
				for (int i = 0; i<4; i++) {
					lastGhostPos[i][0]=lastGhostPos[i][2];
					lastGhostPos[i][1]=lastGhostPos[i][3];
					lastGhostPos[i][2]=(int)ghosts[i].getX();
					lastGhostPos[i][3]=(int)ghosts[i].getY();
				}

			}
			else {
				gameOver();
				gameTimer.stop();
			}
		}
	}

	
	//initialize the positions of the walls
	public void initializeWalls(int xPos, int yPos, int width, int height) {
		wallsQ1[0] = new Rectangle (0, 49, 200, 8);
		wallsQ1[1] = new Rectangle (-2, 55, 8, 120);
		wallsQ1[2] = new Rectangle (38, 85, 40, 23);
		wallsQ1[3] = new Rectangle (109, 85, 54, 23);
		wallsQ1[4] = new Rectangle (195, 55, 5, 53);
		wallsQ1[5] = new Rectangle (38, 136, 40, 10);
		wallsQ1[6] = new Rectangle (152, 136, 48, 10);
		wallsQ1[7] = new Rectangle (0, 175, 78, 8);
		wallsQ1[8] = new Rectangle (195, 146, 5, 39);
		wallsQ1[9] = new Rectangle (109, 136, 12, 88);
		wallsQ1[10] = new Rectangle (121, 175, 41, 11);
		wallsQ1[11] = new Rectangle (70, 181, 8, 38);
		wallsQ1[12] = new Rectangle (0, 217, 78, 8);
		wallsQ1[13] = new Rectangle (152, 213, 33, 8);
		wallsQ1[14] = new Rectangle (152, 219, 8, 19);

		wallsQ2[0] = new Rectangle (200, 49, 200, 8);
		wallsQ2[1] = new Rectangle (394, 55, 8, 120);
		wallsQ2[2] = new Rectangle (322, 175, 78, 8);
		wallsQ2[3] = new Rectangle (322, 181, 8, 38);
		wallsQ2[4] = new Rectangle (322, 221, 78, 8);
		wallsQ2[5] = new Rectangle (237, 85, 54, 23);
		wallsQ2[6] = new Rectangle (323, 85, 40, 23);
		wallsQ2[7] = new Rectangle (322, 136, 40, 10);
		wallsQ2[8] = new Rectangle (279, 136, 12, 88);
		wallsQ2[9] = new Rectangle (237, 175, 41, 11);
		wallsQ2[10] = new Rectangle (200, 55, 5, 53);
		wallsQ2[11] = new Rectangle (200, 136, 48, 10);
		wallsQ2[12] = new Rectangle (200, 146, 5, 39);
		wallsQ2[13] = new Rectangle (216, 213, 33, 8);
		wallsQ2[14] = new Rectangle (241, 219, 8, 19);

		wallsQ3[0] = new Rectangle (0, 248, 78, 8);
		wallsQ3[1] = new Rectangle (70, 256, 8, 39);
		wallsQ3[2] = new Rectangle (0, 293, 78, 8);
		wallsQ3[3] = new Rectangle (-2, 301, 8, 148);
		wallsQ3[4] = new Rectangle (38, 330, 40, 10);
		wallsQ3[5] = new Rectangle (6, 368, 29, 10);
		wallsQ3[6] = new Rectangle (68, 340, 10, 38);
		wallsQ3[7] = new Rectangle (109, 330, 54, 10);
		wallsQ3[8] = new Rectangle (109, 368, 12, 38);
		wallsQ3[9] = new Rectangle (109, 252, 12, 48);
		wallsQ3[10] = new Rectangle (38, 406, 125, 10);
		wallsQ3[11] = new Rectangle (152, 238, 8, 19);
		wallsQ3[12] = new Rectangle (152, 256, 48, 8);
		wallsQ3[13] = new Rectangle (152, 291, 48, 10);
		wallsQ3[14] = new Rectangle (195, 301, 5, 39);
		wallsQ3[15] = new Rectangle (152, 368, 48, 10);
		wallsQ3[16] = new Rectangle (195, 378, 5, 39);
		wallsQ3[17] = new Rectangle (0, 444, 200, 8);

		wallsQ4[0] = new Rectangle (322, 248, 78, 8);
		wallsQ4[1] = new Rectangle (322, 256, 8, 39);
		wallsQ4[2] = new Rectangle (322, 293, 78, 8);
		wallsQ4[3] = new Rectangle (394, 301, 8, 148);
		wallsQ4[4] = new Rectangle (323, 330, 40, 10);
		wallsQ4[5] = new Rectangle (365, 368, 29, 10);
		wallsQ4[6] = new Rectangle (323, 340, 10, 38);
		wallsQ4[7] = new Rectangle (237, 330, 54, 10);
		wallsQ4[8] = new Rectangle (279, 252, 12, 48);
		wallsQ4[9] = new Rectangle (279, 368, 12, 38);
		wallsQ4[10] = new Rectangle (237, 406, 125, 10);
		wallsQ4[11] = new Rectangle (241, 238, 8, 19);
		wallsQ4[12] = new Rectangle (200, 256, 48, 8);
		wallsQ4[13] = new Rectangle (200, 291, 48, 10);
		wallsQ4[14] = new Rectangle (200, 301, 5, 39);
		wallsQ4[15] = new Rectangle (200, 368, 48, 10);
		wallsQ4[16] = new Rectangle (200, 378, 5, 39);
		wallsQ4[17] = new Rectangle (200, 444, 200, 8);

		ghostDoor = new Rectangle (185, 213, 31, 8);
	}
	

	//initialize the location of the dots
	public void initializeDots() {
		for (int i = 0; i<12; i++){
			dots[i] = new Rectangle (22+14*i, 70, 3, 3);
		}
		for (int i = 0; i<12; i++){
			dots[i+12] = new Rectangle (220+14*i, 70, 3, 3);
		}
		for (int i = 0; i<2; i++) {
			dots[i+24] = new Rectangle (22, 83+i*26, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+26] = new Rectangle (92, 83+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+29] = new Rectangle (176, 83+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+32] = new Rectangle (220, 83+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+35] = new Rectangle (304, 83+i*13, 3, 3);
		}
		for (int i = 0; i<2; i++) {
			dots[i+38] = new Rectangle (374, 83+i*26, 3, 3);
		}
		for (int i = 0; i<12; i++){
			dots[i+40] = new Rectangle (22+14*i, 122, 3, 3);
		}
		for (int i = 0; i<2; i++) {
			dots[i+52] = new Rectangle (191+i*15, 122, 3, 3);
		}		
		for (int i = 0; i<12; i++){
			dots[i+54] = new Rectangle (220+14*i, 122, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+66] = new Rectangle (22, 135+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+69] = new Rectangle (92, 135+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+72] = new Rectangle (134, 135+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+75] = new Rectangle (262, 135+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+78] = new Rectangle (304, 135+i*13, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+81] = new Rectangle (374, 135+i*13, 3, 3);
		}
		for (int i = 0; i<4; i++) {
			dots[i+84] = new Rectangle (36+i*14, 161, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+88] = new Rectangle (148+i*15, 161, 3, 3);
		}
		for (int i = 0; i<3; i++) {
			dots[i+91] = new Rectangle (217+i*15, 161, 3, 3);
		}
		for (int i = 0; i<4; i++) {
			dots[i+94] = new Rectangle (318+i*14, 161, 3, 3);
		}
		int realignment=0;
		for (int i = 0; i<11; i++) {
			dots[i+98] = new Rectangle (92, 174+i*13-realignment, 3, 3);
			if (i%3==0) {
				realignment++;
			}	
		}
		realignment=0;
		for (int i = 0; i<11; i++) {
			dots[i+109] = new Rectangle (304, 174+i*13-realignment, 3, 3);
			if (i%3==0) {
				realignment++;
			}	
		}
		for (int i = 0; i<12; i++){
			dots[i+120] = new Rectangle (22+14*i, 313, 3, 3);
		}
		for (int i = 0; i<12; i++){
			dots[i+132] = new Rectangle (220+14*i, 313, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+144] = new Rectangle (22, 326+13*i, 3, 3);
		}
		for (int i = 0; i<6; i++){
			dots[i+146] = new Rectangle (92, 326+13*i, 3, 3);
		}
		for (int i = 0; i<3; i++){
			dots[i+152] = new Rectangle (176, 326+13*i, 3, 3);
		}
		for (int i = 0; i<3; i++){
			dots[i+155] = new Rectangle (220, 326+13*i, 3, 3);
		}
		for (int i = 0; i<6; i++){
			dots[i+158] = new Rectangle (304, 326+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+164] = new Rectangle (374, 326+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+166] = new Rectangle (36+14*i, 352, 3, 3);
		}
		for (int i = 0; i<5; i++){
			dots[i+168] = new Rectangle (106+14*i, 352, 3, 3);
		}
		for (int i = 0; i<5; i++){
			dots[i+173] = new Rectangle (234+14*i, 352, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+178] = new Rectangle (346+14*i, 352, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+180] = new Rectangle (50, 365+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+182] = new Rectangle (134, 365+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+184] = new Rectangle (262, 365+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+186] = new Rectangle (346, 365+13*i, 3, 3);
		}
		for (int i = 0; i<5; i++){
			dots[i+188] = new Rectangle (22+14*i, 391, 3, 3);
		}
		for (int i = 0; i<4; i++){
			dots[i+193] = new Rectangle (134+14*i, 391, 3, 3);
		}
		for (int i = 0; i<4; i++){
			dots[i+197] = new Rectangle (220+14*i, 391, 3, 3);
		}
		for (int i = 0; i<5; i++){
			dots[i+201] = new Rectangle (318+14*i, 391, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+206] = new Rectangle (22, 404+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+208] = new Rectangle (176, 404+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+210] = new Rectangle (220, 404+13*i, 3, 3);
		}
		for (int i = 0; i<2; i++){
			dots[i+212] = new Rectangle (374, 404+13*i, 3, 3);
		}
		for (int i = 0; i<12; i++){
			dots[i+214] = new Rectangle (22+14*i, 430, 3, 3);
		}
		for (int i = 0; i<2; i++) {
			dots[i+226] = new Rectangle (191+i*15, 430, 3, 3);
		}		
		for (int i = 0; i<12; i++){
			dots[i+228] = new Rectangle (220+14*i, 430, 3, 3);
		}
		superDots[0] = new Rectangle (17, 91, 13, 13);
		superDots[1] = new Rectangle (369, 91, 13, 13);
		superDots[2] = new Rectangle (17, 347, 13, 13);
		superDots[3] = new Rectangle (369, 347, 13, 13);

		for (int i = 0; i<240; i++) {
			dotsExist[i]=true;
		}
		for (int i = 0; i<4; i++) {
			superDotsExist[i]=true;
		}

	}	

	
	//Draws all the images on the screen
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		int xPos = 0, yPos = 50, width = 400, height = 400;

		initializeWalls(xPos, yPos, width, height);

		Image backgroundImg = Toolkit.getDefaultToolkit ().getImage ("template.gif");
		g.drawImage(backgroundImg, xPos, yPos, width, height, this);

		g.setColor(Color.WHITE);
		
		g.setFont(textFont);
		g.drawString("Points: " + points, 10, 30);

		
		if (direction == 'l') {
			pacManImage = Toolkit.getDefaultToolkit ().getImage ("pac man l.png");
		}
		else if (direction == 'd') {
			pacManImage = Toolkit.getDefaultToolkit ().getImage ("pac man d.png");
		}
		else if (direction == 'u') {
			pacManImage = Toolkit.getDefaultToolkit ().getImage ("pac man u.png");
		}
		else if (direction == 'r') {
			pacManImage = Toolkit.getDefaultToolkit ().getImage ("pac man r.png");
		}
		drawSprite(g, pacManRect, pacManImage,observer);
		drawSprite(g, ghosts[0], ghostGreen,observer);
		drawSprite(g, ghosts[1], ghostRed,observer);		
		drawSprite(g, ghosts[2], ghostPink,observer);
		drawSprite(g, ghosts[3], ghostOrange,observer);
		g2.setColor(Color.white);
		for (int i = 0; i<240; i++){
			if (dotsExist[i]) {
				g2.fill(dots[i]);
			}
		}

		if(gameTime%60<30) {
			g.setColor(Color.WHITE);
		}
		else
			g.setColor(Color.GRAY);

		if (superDotsExist[0])
			g.fillOval(17, 91, 13, 13);
		if (superDotsExist[1])
			g.fillOval(369, 91, 13, 13);
		if (superDotsExist[2])
			g.fillOval(17, 347, 13, 13);
		if (superDotsExist[3])
			g.fillOval(369, 347, 13, 13);
	}


	//determines which direction to move a ghost, and moves the ghost
	//in that direction. Directions are chosen at random and ensured to
	//not be continuously into a wall
	public void moveGhost (int ghostNum) {

		int xPos = (int)ghosts[ghostNum].getX(), yPos=(int)ghosts[ghostNum].getY();
		int pixels = 1;
		
		if (gameTime > 0) {
			if (xPos>=157&&xPos<=240&&yPos>=218&&yPos<=255) {
				if (xPos<189)
					ghostDir[ghostNum] = 'r';
				else if (xPos>191)
					ghostDir[ghostNum] = 'l';
				else if (xPos>=189&&xPos<=191&&yPos<=255)
					ghostDir[ghostNum] = 'u';
			}
	
	
			else if (gameTime%120==0||(lastGhostPos[ghostNum][0]==xPos&&lastGhostPos[ghostNum][1]==yPos)) {
				int random = (int)(Math.random()*4) + 1;	
				if (random == 1) {
					ghostDir[ghostNum] = 'l';
				}
				else if(random == 2) {
					ghostDir[ghostNum] = 'r';
				}
				else if (random == 3) {
					ghostDir[ghostNum] = 'u';
				}
				else if (random == 4) {
					ghostDir[ghostNum] = 'd';
				}
			}
			
			if (ghostDir[ghostNum]=='l') {
				ghosts[ghostNum].setLocation((int)ghosts[ghostNum].getX()-pixels, (int)ghosts[ghostNum].getY());
				checkForCollision(ghosts[ghostNum]);
			}
			else if (ghostDir[ghostNum]=='r') {
				ghosts[ghostNum].setLocation((int)ghosts[ghostNum].getX()+pixels, (int)ghosts[ghostNum].getY());
				checkForCollision(ghosts[ghostNum]);
			}
			else if (ghostDir[ghostNum]=='u') {
				ghosts[ghostNum].setLocation((int)ghosts[ghostNum].getX(), (int)ghosts[ghostNum].getY()-pixels);
				checkForCollision(ghosts[ghostNum]);
			} 
			else if (ghostDir[ghostNum]=='d') {
				ghosts[ghostNum].setLocation((int)ghosts[ghostNum].getX(), (int)ghosts[ghostNum].getY()+pixels);
				checkForCollision(ghosts[ghostNum]);
			} 
		}
			
	}

	//Assists in checking to see if pacman or a ghost is intersecting with 
	//a wall after moving by checking which quadrant the player/ghost
	//is in, and calling the collision method
	public boolean checkForCollision(Rectangle animatedRect) {
		double x = animatedRect.getX();
		double y = animatedRect.getY();
		double x2 = animatedRect.getX()+20;
		boolean collision = false;

		//checks to see if pacMan/ghost is in quadrant 1
		if ((x <= 190) && (y <= 238)) { 
			for (int i = 0; i < 15; i++) {
				if (collision) {
					return collision;
				}
				collision = collision(animatedRect, wallsQ1[i]);
			}
		}

		//checks to see if pacMan/ghost is in quadrant 2
		if ((x >= 190) && (y <= 238)) {
			for (int i = 0; i < 15; i++) {
				if (collision) {
					return collision;
				}
				collision = collision(animatedRect, wallsQ2[i]);
			}
		}


		//checks to see if pacMan/ghost is in quadrant 3
		if ((x <= 190 && x2 <= 209) && (y >= 228)) {
			for (int i = 0; i < 18; i++) {
				if (collision) {
					return collision;
				}
				collision = collision(animatedRect, wallsQ3[i]);
			}
		}

		//checks to see if pacMan/ghost is in quadrant 4
		if ((x >= 190 && x2 >= 211) && (y>= 228)) {
			for (int i = 0; i < 18; i++) {
				if (collision) {
					return collision;
				}
				collision = collision(animatedRect, wallsQ4[i]);
			}
		}


		if (animatedRect==pacManRect) {
			collision(pacManRect, new Rectangle (186, 214, 30, 4));
		}

		for (int i = 0; i<4; i++) {
			if (animatedRect==ghosts[i]&&ghostDir[i]=='d') {
				collision(ghosts[i], new Rectangle (186, 214, 30, 4));
			}
		}
		
		return collision;
	}
	
	//the collision method checks to see if the pacman or ghost is
	//intersecting with a wall, and if so, then taking it out of
	//the wall and placing it back onto the playable area
	public boolean collision(Rectangle animatedRect, Rectangle wall) {
		boolean collision = false;

		if (animatedRect.intersects(wall)) {

			collision = true;

			//stop the rect from moving
			double left1 = animatedRect.getX();
			double right1 = animatedRect.getX() + animatedRect.getWidth();
			double top1 = animatedRect.getY();
			double bottom1 = animatedRect.getY() + animatedRect.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();

			if(right1 > left2 && 
					left1 < left2 && 
					right1 - left2 < bottom1 - top2 && 
					right1 - left2 < bottom2 - top1)
			{
				//animatedRect collides from left side of the wall
				animatedRect.x = wall.x - animatedRect.width;
			}
			else if(left1 < right2 &&
					right1 > right2 && 
					right2 - left1 < bottom1 - top2 && 
					right2 - left1 < bottom2 - top1)
			{
				//animatedRect collides from right side of the wall
				animatedRect.x = wall.x + wall.width;
			}
			else if(bottom1 > top2 && top1 < top2)
			{
				//animatedRect collides from top side of the wall
				animatedRect.y = wall.y - animatedRect.height;
			}
			else if(top1 < bottom2 && bottom1 > bottom2)
			{
				//animatedRect collides from bottom side of the wall
				animatedRect.y = wall.y + wall.height;
			}
		}

		return collision;
	}

	
	//Moves the Pac Man in the direction of the user's choosing
	public void movePacRect (char direction) {
		int xPos = (int) pacManRect.getX();
		int yPos = (int) pacManRect.getY();

		if (direction=='l'&&xPos<=0) { //going off screen on the left side
			pacManRect.setLocation(380, 228);
		}
		else if (direction=='r'&&xPos>=380) { //going off screen on the right side
			pacManRect.setLocation(0, 228);
		}
		else if (direction=='l') {
			pacManRect.setLocation(xPos-1, yPos);
		}
		else if (direction=='r') {
			pacManRect.setLocation(xPos+1, yPos);
		}
		else if (direction=='u') {
			pacManRect.setLocation(xPos, yPos-1);	
		} 
		else if (direction=='d') {
			pacManRect.setLocation(xPos, yPos+1);
		} 

		for (int i = 0; i<240; i++) {
			if (pacManRect.intersects(dots[i])&&dotsExist[i]) {
				dotsExist[i]=false;
				numDots--;
				points +=10;
				pacManEating.play();
				break;
			}
		}
		for (int i = 0; i<4; i++) {
			if (pacManRect.intersects(superDots[i])&&superDotsExist[i]) {
				superDotsExist[i]=false;
				numDots--;
				eventTime = gameTime;
				ghostsScared=true;
				ghostGreen = Toolkit.getDefaultToolkit ().getImage ("ghostFrightened.gif");
				ghostRed = Toolkit.getDefaultToolkit ().getImage ("ghostFrightened.gif");
				ghostPink = Toolkit.getDefaultToolkit ().getImage ("ghostFrightened.gif");
				ghostOrange = Toolkit.getDefaultToolkit ().getImage ("ghostFrightened.gif");
				points +=50;
				pacManEating.play();
				break;
			}
		}
	}

	//displays the image of the pacman and the ghosts
	public void drawSprite (Graphics g, Rectangle rect, Image image, ImageObserver observer) {
		int xPos = (int) rect.getX();
		int yPos = (int) rect.getY();
		g.drawImage(image, xPos, yPos, 20, 20, this);
	}
	
	public URL getCompleteURL (String fileName)
	{
		try
		{
			return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println (e.getMessage ());
		}
		return null;
	}

	//determines which direction the user wants to move in
	public void keyPressed (KeyEvent key) {
		char input = Character.toUpperCase (key.getKeyChar ());
		if (input == 'W'||input == 'w')
			direction = 'u';
		else if (input == 'S'||input == 's')
			direction = 'd';
		if (input == 'A'||input == 'a')
			direction = 'l';
		if (input == 'D'||input == 'd')
			direction = 'r';
	}

	public void keyReleased(KeyEvent key) {
	}


	public void keyTyped(KeyEvent e) {
	}
	
	
	//Generates and displays the game over screen 
	public void gameOver() {
		gameOverFrame = new JFrame();
		gameOverFrame.setPreferredSize(new Dimension(400, 375));

		gameOverPanel = new JPanel();
		gameOverPanel.setLayout(new BorderLayout());


		ImageIcon gameOverIcon = new ImageIcon ("pac man r.png"); 
		
		final JLabel pointsMessage = new JLabel ("Final Score: "+points);
		final JLabel gameOverMessage;
		if (loser) {
			gameOverMessage = new JLabel("You Lost!");
			gameOverIcon = new ImageIcon ("ghostRed.gif");
		}	
		else {
			gameOverMessage = new JLabel("You Won!");
		}
		
		final JButton newGameBtn = new JButton("Quit Game");
		newGameBtn.setActionCommand ("exit");
		newGameBtn.addActionListener (this);
		
		Image gameOverImage = gameOverIcon.getImage().getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH);
		JLabel gameOverLabel = new JLabel(new ImageIcon(gameOverImage));
		
		gameOverPanel.add(gameOverMessage, BorderLayout.CENTER);
		gameOverPanel.add(pointsMessage, BorderLayout.NORTH);
		gameOverPanel.add(gameOverLabel, BorderLayout.LINE_END);

		gameOverPanel.add(newGameBtn, BorderLayout.PAGE_END);
		
		gameOverFrame.add(gameOverPanel);
		gameOverFrame.pack();
		gameOverFrame.setVisible(true);
		gameOverFrame.setResizable(false);
		gameOverFrame.setLocationRelativeTo(null);
	}

	//displays instructions of the game and allows the user to
	//start the game
	public void displayInstructions() {
		pacManIntro.play();
		insFrame = new JFrame();
		insFrame.setPreferredSize(new Dimension(400, 375));

		insPanel = new JPanel();
		insPanel.setLayout(new BorderLayout());
		
		final JLabel welcomeText = new JLabel("                         Welcome to the Pac Man Game");
		insPanel.add(welcomeText, BorderLayout.PAGE_START);
		
		final ImageIcon adrianImgIcon = new ImageIcon ("Adrian.gif"); 
		JLabel adrian = new JLabel();
		Image adrianImg = adrianImgIcon.getImage().getScaledInstance(100, 200,  java.awt.Image.SCALE_SMOOTH);
		adrian.setIcon(new ImageIcon (adrianImg));
		insPanel.add(adrian, BorderLayout.LINE_START);

		final JLabel insText = new JLabel("Control using WASD keys!");
		insPanel.add(insText, BorderLayout.CENTER);
		
		final ImageIcon alex = new ImageIcon("alex.gif");
		Image alexImg = alex.getImage().getScaledInstance(100, 200,  java.awt.Image.SCALE_SMOOTH); //Convert icon into an image and scales it
		JLabel alexLabel = new JLabel(new ImageIcon(alexImg));
		insPanel.add(alexLabel, BorderLayout.LINE_END);
		
		final JButton startBtn = new JButton("START GAME");
		startBtn.setActionCommand ("startGame");
		startBtn.addActionListener (this);
		insPanel.add(startBtn, BorderLayout.PAGE_END);
		
		insFrame.add(insPanel);
		insFrame.pack();
		insFrame.setVisible(true);
		insFrame.setResizable(false);
		insFrame.setLocationRelativeTo(null);
	}
	
	public void actionPerformed (ActionEvent event) {
		
		//sets the event name to the action command
		String eventName = event.getActionCommand();
		if (eventName.equals ("startGame")) {
			gameTimer.start();
			frame.setVisible(true);
			insFrame.setVisible(false);
		}
		else if (eventName.equals ("exit")) {
			System.exit(0);
		}
		
	}
	public static void main(String[] args) {		
		frame = new JFrame ("Pac Man");
		panel = new PacManGame();
		
		frame.add(panel);
		frame.pack();
		
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
	}

}



