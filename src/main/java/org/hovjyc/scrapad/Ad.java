package org.hovjyc.scrapad;

import java.util.Date;

/**
 * Classe représentant une annonce extraite du site.
 */
public class Ad {
    /** Titre de l'annonce */
    private String title;
    
    /** Aperçu de la description de l'annonce (celle qui est lisible avant même de cliquer sur l'annonce)*/
    private String descriptionPreview;
    
    /** Description complète de l'annonce (celle qui est lisible une fois que l'on a cliqué sur l'annonce) */
    private String descriptionFull;
    
    /** Lieu */
    private String location;
    
    /** Date de publication de l'annonce. */
    private Date date;
    
    /** Pseudo de l'annonceur. */
    private String pseudo;
    
    /** URL de l'annonce. */
    private String url;

    /** Constructeur */
    public Ad(String pTitle, String pDescriptionPreview, String pLocation, Date pDate, String pURL) {
        super();
        this.title = pTitle;
        this.descriptionPreview = pDescriptionPreview;
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

    public String getDescriptionPreview() {
        return descriptionPreview;
    }

    public void setDescriptionPreview(String descriptionPreview) {
        this.descriptionPreview = descriptionPreview;
    }

    public String getDescriptionFull() {
        return descriptionFull;
    }

    public void setDescriptionFull(String descriptionFull) {
        this.descriptionFull = descriptionFull;
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
