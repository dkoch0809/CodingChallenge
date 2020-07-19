package darstellung;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import rundweg.*;

public class Map2DRenderer extends JPanel {


	ArrayList<Places> place;
	ArrayList<Edge> path;

	
	public Map2DRenderer(ArrayList<Places> place, ArrayList<Edge> path) {
		this.place = place;
		this.path = path;
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(Color.darkGray);
		g2.translate(-2000, 4500);
		for (int i = 0; i < place.size(); i++) {
			int kkx=(int)(-(place.get(i).getLaenge()*100)); //Koordinaten des Knotens
			int kky=(int)((place.get(i).getBreite()*100));
			
			g2.setColor(Color.RED);
			g2.drawOval(getWidth()-kkx, getHeight()-kky, 25,25);
			g2.drawString(place.get(i).getPlace(),getWidth()-kkx, getHeight()-kky);
			
		}
		
		for (int i = 0; i < path.size(); i++) {
			int kkx=(int)(-(path.get(i).getSource().getLaenge()*100)); //Koordinaten des Knotens
			int kky=(int)((path.get(i).getSource().getBreite()*100));
			int dkx=(int)(-(path.get(i).getDestination().getLaenge()*100)); //Koordinaten des Knotens
			int dky=(int)((path.get(i).getDestination().getBreite()*100));
			
			g2.setColor(Color.BLUE);
			g2.drawLine(getWidth()-kkx ,getHeight()-kky,getWidth()- dkx,getHeight()-dky);
			g2.drawOval(getWidth()-kkx, getHeight()-kky, 25,25);
			
		}
	
	}
}