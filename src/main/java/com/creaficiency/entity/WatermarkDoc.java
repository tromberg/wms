package com.creaficiency.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

@Entity
public class WatermarkDoc implements Serializable {
    public enum DocType { BOOK, JOURNAL };

	private static final long serialVersionUID = 4585112990890474278L;
	
	private Long id;
    private String title;
    private String author;
    
    private DocType docType;
    private String topic;
	private String watermark;
 
    public WatermarkDoc() {}
 
    
   

	public WatermarkDoc(String title, String author, DocType docType, String topic) {
		super();
		this.title = title;
		this.author = author;
		this.docType = docType;
		this.topic = topic;
	}




	@Id @GeneratedValue
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }
 
    
    @NotNull
    public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	

	@NotNull
	public String getAuthor() {
		return author;
	}



	public void setAuthor(String author) {
		this.author = author;
	}


	@NotNull
	@Enumerated(EnumType.STRING)
	public DocType getDocType() {
		return docType;
	}



	public void setDocType(DocType docType) {
		this.docType = docType;
	}



	public String getTopic() {
		return topic;
	}



	public void setTopic(String topic) {
		this.topic = topic;
	}



	public String getWatermark() {
        return watermark;
    }
 
    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    /**
     * perform entity-level validation
     * could be done with class-level constraint 
     * https://docs.jboss.org/hibernate/validator/5.0/reference/en-US/html/validator-customconstraints.html#section-class-level-constraints
     * but it feels a little like overkill here
     */
    public void validate() {
    	switch (docType) {
    	case JOURNAL:
    		if (topic != null) throw new ValidationException("Doc " + id + " is a Journal and must not have a topic.");
    		break;
    	case BOOK:
    		if (topic == null) throw new ValidationException("Doc " + id + " is a Book and must have a topic");
    		break;
    	default: // null not allowed by field validation
    	}
    	
    }
    	


	@Override
	public String toString() {
		return "WatermarkDoc [id=" + id + ", title=" + title + ", author=" + author + ", docType=" + docType
				+ ", topic=" + topic + ", watermark=" + watermark + "]";
	}




	public int calcWatermarkCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
		return result;
	}


	
   
}
