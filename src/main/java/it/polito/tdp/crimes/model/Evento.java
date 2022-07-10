package it.polito.tdp.crimes.model;

import java.time.LocalDateTime;

public class Evento implements Comparable <Evento>{
	
	public enum TipoEvento{
		CRIMINE,
		ARRIVA_AGENTE, 
		GESTITO
	}
	private TipoEvento tipo;
	private LocalDateTime date;
	private Event crimine;
	public TipoEvento getTipo() {
		return tipo;
	}
	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public Event getCrimine() {
		return crimine;
	}
	public void setCrimine(Event crimine) {
		this.crimine = crimine;
	}
	public Evento(TipoEvento tipo, LocalDateTime date, Event crimine) {
		super();
		this.tipo = tipo;
		this.date = date;
		this.crimine = crimine;
	}
	@Override
	public int compareTo(Evento o) {
		// TODO Auto-generated method stub
		return this.date.compareTo(o.getDate());
	}
	
	
}
