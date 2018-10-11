Author: Dan Raku�an (DUDSS) Project began: Beginning of 2018 (January), old Java2D BitBucket repository created on: 23.6.2018


A prototype engine for a possible WIP towerdefense/RTS game.
Heavily inspired by Creeper World by KnuckleCracker and Factorio

OpenGL renderer Java adaptation and some basic game functionality stands on the LibGdx framework (Low-level opengl calls based on LWJGL)

* * *
* Current version includes:
	* Buildings (mines, storages and connectors (that have nodes of either side) and turrets that shoot projectiles)
	
	* Corruption, an abstract enemy creeping through the world

	* Randomly generated terrain, ore spawning and terrain height. (Simplex noise gen)

	* Working package system (packages follow connectors)
		
	* Items (in form of packages)
	
	* Few different types of nodes that handle items (Input node, Output node) (Buildings are made up from nodes)
	
	* 2 package pathfinding schemes (conveyor belt and shortest path)

	* A semi-working UI

* * *

![intro](img/intro.PNG)

![multilevel corruption](img/firstrealmultilevelcreepimplementation.PNG)

![terrain](img/artokey.PNG)

![screen2](img/scr2.PNG)

![screen1](img/scr1.PNG)

![different production rates](img/differentproductionrates.PNG)

![buildings](img/nodeshotscreen101.PNG)

![connections](img/idk.PNG)