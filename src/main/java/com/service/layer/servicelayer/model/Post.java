package com.service.layer.servicelayer.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.util.Date;

public class Post {

    String title;
    String author;
    String text;

    Date published;

    @XmlElement(name = "title")
    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "author")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlElement(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlElement(name = "published")
    @XmlSchemaType(name="date")
    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }
}
