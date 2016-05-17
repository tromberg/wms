package com.creaficiency.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class WatermarkDoc implements Serializable {
   
	private static final long serialVersionUID = 4585112990890474278L;
	
	private Long id;
    private String title;
	private String watermark;
 
    public WatermarkDoc() {}
 
    
   
    public WatermarkDoc(String title) {
		super();
		this.title = title;
	}



	@Id @GeneratedValue
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    
    
    public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getWatermark() {
        return watermark;
    }
 
    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }
 
    @Override
    public String toString() {
        return "WatermarkDoc@" + hashCode() + "[id = " + id + "; watermark = " + watermark + "]";
    }
}
