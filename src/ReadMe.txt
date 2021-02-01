Features of the game:

	This game is the Pac-Man game as we inteded to make. The player
	controls the Pac-Man with WASD keys. The objective of the game
	is to eat all of the pac-dots located around the board. Once all
	of the pac-dots are eaten, the user wins the game. However, the 
	player must avoid the ghosts. If the Pac-Man is touched by a ghost,
	the player loses the game.



Some known bugs:
	The template is divided into four quadrants. When the Pac-Man is on
	the exact pixel between quadrants, it may move through the wall.



Improvements if time had permitted:
	Right now, all ghosts move randomly. We would have liked to allow the ghosts 
	to chance statuses, chasing or fleeing from Pac-Man, in addition to the random 
	movement.
	In addition, an overall improvement in ghost AI, allowing them to chase 
	Pac-Man off the left and right sides of the screen
	
	Also, even and proper spacing of the dots allowing for fluid sound effects



Split up of the work:
	Alex:
	-	Put up pac dots onto the board
	-	Created mechanics for running the game (Timer, TimerEventHandler class)
	–	Created methods movePacRect, drawSprite, gameOver
	- 	Added sounds into the game
	-	Co-wrote moveGhost with Adrian
	
	Adrian:
	- 	Created the intro screen and most of the graphics
	- 	Put up the walls for collision detection
	- 	Created collision detection methods 
	- 	Co-wrote moveGhost with Alex
	-	Conceptualized a significant amount of the mechanics
	 