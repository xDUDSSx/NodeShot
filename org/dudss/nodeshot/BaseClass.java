package org.dudss.nodeshot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.misc.NodeConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.screens.MenuScreen;

import java.util.concurrent.CopyOnWriteArrayList;

public class BaseClass extends Game {
	public static String debugMessage = "Debug message";

	static long currentSimTimeTick;
	static long nextSimTimeTick;

	public static int sfps;
	static int simFrameCount;
	public static double simFac;

	public static Vector2 mousePos = new Vector2();

	public static CopyOnWriteArrayList<Node> nodelist = new CopyOnWriteArrayList<Node>();
	public static CopyOnWriteArrayList<Package> packagelist = new CopyOnWriteArrayList<Package>();

	public static PackageHandler packageHandler;
	public static NodeConnectorHandler nodeConnectorHandler;

	static Boolean NodeInfoHidden = false;
	public static Boolean NodeSelectiveInfo = true;
	static Boolean NodeConnectRadiusHidden = true;

	public static Boolean toggleConnectMode = false;
	public static Boolean buildMode = false;
	public static Boolean activeNewConnection = false;
	public static int newConnectionFromIndex;

	public static Boolean draggingConnection = false;

	public static int mouseX;
	public static int mouseY;

	//edited
	public static Vector3 lastMousePress = new Vector3(0,0,0);

	public static enum MouseType {
		MOUSE_1, MOUSE_2, MOUSE_3
	}
	public static MouseType lastMousePressType;

	public static int selectedIndex = -1;
	public static int highlightedIndex = -1;
	public static enum EntityType {
		NODE, CONNECTOR, PACKAGE, NONE
	}
	public static EntityType selectedType = EntityType.NONE;

	public static int indexOfHighlightedNode = -1;

	static Boolean drawString = false;
	static String stringToWrite = "";

	public static Texture spriteSheet;
	public static Texture nodeTex;

	public static Sprite highlightSprite;

	public static Sprite mapSprite;
	public static Sprite[] mapTiles;

	public static int WORLD_SIZE;

	public static float viewportWidth = 300f;

	public static Screen mainGameScreen;
	public static Screen menuScreen;

	@Override
	public void create() {
		mainGameScreen = new GameScreen(this);
		menuScreen = new MenuScreen(this);
		setScreen(menuScreen);
	}
}