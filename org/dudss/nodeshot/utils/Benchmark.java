package org.dudss.nodeshot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.entities.Node;

import com.badlogic.gdx.Gdx;

public class Benchmark implements Runnable{

	ScheduledExecutorService service;
	List<BenchmarkContainer> values = new ArrayList<BenchmarkContainer>();
	int second;
	int seconds;
	int number;
	
	class BenchmarkContainer {
		int fps, sfps;
		double simFac;
		
		BenchmarkContainer(int fps, int sfps, double simFac)  {
			this.fps = fps;
			this.sfps = sfps;
			this.simFac = simFac;
		}
		
		int getFps() {
			return fps;
		}
		int getSFps() {
			return sfps;
		}
		double getSimFac() {
			return simFac;
		}
	}
	
	@Override
	public void run() {
		System.out.println("BENCHMARK RUN: " + second + " s");
		//Base.GameScreen.fps
		BenchmarkContainer bC = new BenchmarkContainer(Gdx.graphics.getFramesPerSecond(), GameScreen.sfps, GameScreen.simFac);
		values.add(bC);	
		
		second++;
		if (second >= seconds) {
			//Shutting down executor calling this run method
			service.shutdown();
			
			//Stopping node movement
			Base.randomMovement = false;
			
			//Removing generated nodes
			removeGeneratedNodes();
			
			int score = 0;
			int index = 0;
			for(BenchmarkContainer b : values) {
				score += b.getFps();
				score += b.getSFps();
				index++;
				
				System.out.println(index + "s : Fps: " + b.getFps() + " sFps: " + b.getSFps() + " simFac: " + b.getSimFac());
			}
			
			System.out.println("Benchmark finished (" + seconds + "s) Performance score: " + score);
		}
	}
	
	public void start(int numberOfNodes, int ticks, int pollingRate) {
		this.seconds = ticks;
		
		generateNodes(numberOfNodes);
		Base.randomMovement = true;
		
		service = Executors.newSingleThreadScheduledExecutor();
   	    service.scheduleAtFixedRate(this, 0, pollingRate, TimeUnit.MILLISECONDS);
	}
	
	void generateNodes(int number) {
		this.number = number;
		int n1 = number / 100;
		int n2 = number / 10;
		
		for (int x = 0; x < n1; x++) {
			for (int y = 0; y < n2; y++) {
				Node n = new Node((GameScreen.WORLD_SIZE/10)*x, (GameScreen.WORLD_SIZE/100)*y, Base.RADIUS);
				GameScreen.nodelist.add(n);
			}
		}
	}
	
	void removeGeneratedNodes() {
		int size = GameScreen.nodelist.size();
			
		for (int i = (size - number); i < number + (size - number); i++) {
			GameScreen.nodelist.get(GameScreen.nodelist.size() - 1).remove();
		}
	}	
}
