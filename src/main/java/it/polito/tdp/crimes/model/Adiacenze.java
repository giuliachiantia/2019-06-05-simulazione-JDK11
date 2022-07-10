package it.polito.tdp.crimes.model;

public class Adiacenze implements Comparable <Adiacenze> {
	
	private Integer id1;
	private Integer id2;
	private Double distance;
	public Adiacenze(Integer id1, Integer id2, Double distance) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.distance = distance;
	}
	public Integer getId1() {
		return id1;
	}
	public void setId1(Integer id1) {
		this.id1 = id1;
	}
	public Integer getId2() {
		return id2;
	}
	public void setId2(Integer id2) {
		this.id2 = id2;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	@Override
	public int compareTo(Adiacenze o) {
		// TODO Auto-generated method stub
		return this.distance.compareTo(o.getDistance());
	}
	
	

}
