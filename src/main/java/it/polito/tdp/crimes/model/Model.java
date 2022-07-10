package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private EventsDao dao;
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private List<Integer> vertici;
	
	
	public Model() {
		dao=new EventsDao();
	}
	
	public List<Integer> getAnni(){
		
		List<Integer> lista=dao.getAnni();
		Collections.sort(lista);
		return lista;
	}
	
	public void creaGrafo(Integer anno) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		this.vertici=dao.getVertici();
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		
		//valido solo perche ho pochi vertici
		/*for(Integer v1: this.grafo.vertexSet()) {
			for(Integer v2: this.grafo.vertexSet()) {
				if(!v1.equals(v2)) {
					if(this.grafo.getEdge(v1, v2)==null) {
						Double latMediaV1=dao.getLatMedia(anno, v1);
						Double latMediaV2=dao.getLatMedia(anno, v2);
						Double lonMediaV1=dao.getLonMedia(anno, v1);
						Double lonMediaV2=dao.getLonMedia(anno, v2);
						
						Double distanzaMedia=LatLngTool.distance(new LatLng(latMediaV1, lonMediaV1), new LatLng(latMediaV2, lonMediaV2), LengthUnit.KILOMETER);
						Graphs.addEdgeWithVertices(this.grafo, v1, v2, distanzaMedia);
						
						
					}
				}
			}
		}*/
		for(Adiacenze a : dao.getArchi(anno)) {
			Graphs.addEdge(this.grafo, a.getId1(), a.getId2(), a.getDistance());
		}
		System.out.println("Grafo creato");
		System.out.println("#Vertici: "+this.grafo.vertexSet().size());
		System.out.println("#Archi: "+this.grafo.edgeSet().size());
		
	}
	
	public List<Vicini> getVicini(Integer distretto){
		List<Vicini> vicini= new ArrayList<>();
		List<Integer> vicini_id=Graphs.neighborListOf(this.grafo, distretto);
		
		for(Integer v:vicini_id) {
			vicini.add(new Vicini(v, this.grafo.getEdgeWeight(this.grafo.getEdge(distretto, v))));
		}
		Collections.sort(vicini);
		return vicini;
		
	}

	public Set<Integer> getVertici() {
		// TODO Auto-generated method stub
		return this.grafo.vertexSet();
	}
	public List<Integer> getMesi() {
		return this.dao.getMesi();
	}

	public List<Integer> getGiorni() {
		// TODO Auto-generated method stub
		return this.dao.getGiorni();
	}
	
	public int simula(Integer anno, Integer mese, Integer giorno, Integer N) {
		Simulatore sim = new Simulatore();
		sim.init(N, anno, mese, giorno, grafo);
		return sim.run();
	}
	
	
}
