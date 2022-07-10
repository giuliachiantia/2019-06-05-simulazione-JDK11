package it.polito.tdp.crimes.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.crimes.model.Adiacenze;
import it.polito.tdp.crimes.model.Event;



public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	public List<Integer> getAnni(){
		String sql="select distinct year(reported_date) as anni "
				+ "from events";
		List <Integer> anni= new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					anni.add(res.getInt("anni"));
				} catch (Throwable t) {
					t.printStackTrace();
					
				}
			}
			
			conn.close();
			return anni ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<Integer> getVertici(){
		String sql="select distinct district_id as id "
				+ "from events "
				+ "order by district_id";
		List<Integer> list= new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getInt("id"));
			}
			conn.close();
			return list ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
		
	}
	public List<Adiacenze> getArchi(Integer anno){
		String sql="select e1.district_id as id1, e2.district_id as id2, AVG(e1.geo_lat) AS lat1 , AVG(e2.geo_lat) AS lat2,  AVG(e1.geo_lon) AS longi1, AVG(e2.geo_lon) AS longi2 "
				+ "from events e1, events e2 "
				+ "where e1.district_id>e2.district_id and e1.reported_date=e2.reported_date and year(e2.reported_date)=? "
				+ "group by e1.district_id, e2.district_id";
		List<Adiacenze> list=new ArrayList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				LatLng d1= new LatLng(res.getDouble("lat1"), res.getDouble("longi1"));
				LatLng d2= new LatLng(res.getDouble("lat2"), res.getDouble("longi2"));
				
				double distance=LatLngTool.distance(d1, d2, LengthUnit.KILOMETER);
				
				
				list.add(new Adiacenze(res.getInt("id1"), res.getInt("id2"), distance));
			}
			
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}

		return list;
			
		
	}
	public Double getLatMedia(Integer anno, Integer v1) {
		// TODO Auto-generated method stub
		String sql="select avg(geo_lat) as lat "
				+ "from events "
				+ "where year(reported_date)=? and district_id=?";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1,anno);
			st.setInt(2, v1);
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				conn.close();
				return res.getDouble("lat");
			} else {
				conn.close();
				return null;
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			return null ;
		}
		
	}
	public Double getLonMedia(Integer anno, Integer v1) {
		String sql="select avg(geo_lon) as lon "
				+ "from events "
				+ "where year(reported_date)=? and district_id=?";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1,anno);
			st.setInt(2, v1);
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				conn.close();
				return res.getDouble("lon");
			} else {
				conn.close();
				return null;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
		
	}
	public List<Integer> getMesi(){
		String sql = "SELECT DISTINCT MONTH(reported_date) as anno FROM events";
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getInt("anno"));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	
	public List<Integer> getGiorni(){
		String sql = "SELECT DISTINCT DAY(reported_date) as anno FROM events";
		List<Integer> result = new ArrayList<Integer>();
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				result.add(res.getInt("anno"));
			}
			conn.close();
			Collections.sort(result);
			return result;
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	public Integer getDistrettoMin(Integer anno) {
		String sql = "SELECT district_id " + 
				"FROM events " + 
				"WHERE Year(reported_date) = ? " + 
				"GROUP BY district_id " + 
				"ORDER BY COUNT(*) ASC " + 
				"LIMIT 1"; //fa si che query restituisca solo prima occorrenza
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				conn.close();
				return res.getInt("district_id");
			} else {
				conn.close();
				return null;
			}
			
			
		} catch(Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	public List<Event> listAllEventsByDate(Integer anno, Integer mese, Integer giorno){
		String sql = "SELECT * FROM events WHERE Year(reported_date) = ? "
				+ "AND Month(reported_date) = ? AND Day(reported_date) = ?" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			st.setInt(2, mese);
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

}
