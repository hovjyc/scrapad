package org.hovjyc.scrapad;

import java.util.Date;

/**
 * Classe représentant une annonce extraite du site.
 */
public class Ad {
    /** Titre de l'annonce */
    private String title;
    
    /** Description de l'annonce */
    private String description;
    
    /** Lieu */
    private String location;
    
    /** Date de publication de l'annonce. */
    private Date date;
    
    /** Pseudo de l'annonceur. */
    private String pseudo;
    
    /** URL de l'annonce. */
    private String url;

    /** Constructeur */
    public Ad(String pTitle, String pPseudo, String pDescription, String pLocation, Date pDate, String pURL) {
        super();
        this.title = pTitle;
        this.pseudo = pPseudo;
        this.description = pDescription;
        this.location = pLocation;
        this.date = pDate;
        this.url = pURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptionFull() {
        return description;
    }

    public void setDescriptionFull(String descriptionFull) {
        this.description = descriptionFull;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
