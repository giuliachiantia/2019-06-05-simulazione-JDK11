package it.polito.tdp.crimes.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.crimes.db.EventsDao;
import it.polito.tdp.crimes.model.Evento.TipoEvento;

public class Simulatore {
	//Tipi di evento 
	/*1. Evento Criminoso
	 *  1.1 La centrale seleziona l'agente libero più vicino
	 *  1.2 Se non ci sono disponibilita --> crimine mal gestito
	 *  1.3 Se c'è un agente libero --> setto agente a occupato
	 *2. Agente selezionato arriva sul posto
	 *  2.1 definisco quanto durera intervento
	 *  2.2 controllo se crimine è mal gestito (ritardo dell'agente)
	 *3.Crimine Terminato
	 *  3.1 libero agente che torna a essere dispo  
	 *
	 */
	
	//input
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;
	
	//output
	private Integer malGestiti;
	
	//stato del mondo 
	private Graph<Integer, DefaultWeightedEdge> grafo;
	private Map<Integer, Integer> agenti; //mappa(distretto-n' agenti liberi)
	
	//coda
	PriorityQueue<Evento> queue;

	
	public void init(Integer N, Integer anno, Integer mese, Integer giorno, Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N=N;
		this.anno=anno;
		this.giorno=giorno;
		this.grafo=grafo;
		
		this.malGestiti=0;
		
		this.agenti= new HashMap<Integer, Integer>();
		for(Integer d: this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		//devo scegliere dove è la centrale e mettere N agenti
		//in quel distretto
		
		EventsDao dao= new EventsDao();
		Integer minD=dao.getDistrettoMin(anno);
		this.agenti.put(minD, N);
		
		//creo e inizializzo la coda
		this.queue= new PriorityQueue<Evento>();
		for(Event e :dao.listAllEventsByDate(anno, mese, giorno)) {
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));
		}
	}
	public int run() {
		Evento e;
		while((e=queue.poll()) !=null) {
			switch (e.getTipo()) {
			case CRIMINE:
				System.out.println("Nuovo Crimine: " +e.getCrimine().getIncident_id());
				//cerco agente libero piu vicino
				Integer partenza=null;
				partenza=cercaAgente(e.getCrimine().getDistrict_id());
				if(partenza!=null) {
					//ce agente libero in partenza
					//setto l'agente come occupato
					this.agenti.put(partenza, this.agenti.get(partenza)-1);
					//cerco di capire quanto ci mettera agente libero a
					//arrivare sul posto
					Double distanza;
					if(partenza.equals(e.getCrimine().getDistrict_id())) {
						distanza=0.0;
					} else {
						distanza=this.grafo.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
					}
					Long seconds=(long) ((distanza*1000)/(60*3.6));
					this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getDate().plusSeconds(seconds), e.getCrimine()));
				} else {
					//non ce nessun agente libero al momento -->crimine
					//mal Gestito
					System.out.println("Crimine: " +e.getCrimine().getIncident_id()+" è mal gestito");
					this.malGestiti++;
					
				}
				break;
			case ARRIVA_AGENTE:
				System.out.println("ARRIVA AGENTE PER CRIMINE! " + e.getCrimine().getIncident_id());
				Long duration = getDurata(e.getCrimine().getOffense_category_id());
				this.queue.add(new Evento(TipoEvento.GESTITO,e.getDate().plusSeconds(duration), e.getCrimine()));
				//controllare se il crimine è mal gestito
				if(e.getDate().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
					System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " MAL GESTITO!");
					this.malGestiti ++;
				}
				break;
			case GESTITO:
				System.out.println("CRIMINE " + e.getCrimine().getIncident_id() + " GESTITO");
				this.agenti.put(e.getCrimine().getDistrict_id(), this.agenti.get(e.getCrimine().getDistrict_id())+1);
				break;
		}
	}
		return this.malGestiti;
	}
	
	private Long getDurata(String offense_category_id) {
		if(offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if(r.nextDouble() > 0.5)
				return Long.valueOf(2*60*60);
			else
				return Long.valueOf(1*60*60);
		} else {
			return Long.valueOf(2*60*60);
		}
	}

	private Integer cercaAgente(Integer district_id) {
		Double distanza=Double.MAX_VALUE;
		Integer distretto = null;
		
		for(Integer d : this.agenti.keySet()) {
			if(this.agenti.get(d) > 0) {
				if(district_id.equals(d)) {
					distanza = 0.0;
					distretto = d; 
				} else if(this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(district_id, d));
					distretto = d;
				}
			}
		}
		return distretto;
	}
	
}
