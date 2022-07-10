package it.polito.tdp.crimes.model;

public class Vicini implements Comparable <Vicini>{
	
	private Integer vicino;
	private Double distanza;
	public Vicini(Integer vicino, Double distanza) {
		super();
		this.vicino = vicino;
		this.distanza = distanza;
	}
	public Integer getVicino() {
		return vicino;
	}
	public void setVicino(Integer vicino) {
		this.vicino = vicino;
	}
	public Double getDistanza() {
		return distanza;
	}
	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}
	@Override
	public int compareTo(Vicini o) {
		// TODO Auto-generated method stub
		return this.distanza.compareTo(o.getDistanza());
	}
	

}
