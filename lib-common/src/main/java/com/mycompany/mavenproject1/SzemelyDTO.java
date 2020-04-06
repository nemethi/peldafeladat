/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Ruzsinak
 */
public class SzemelyDTO implements Serializable{

    /**
     * @return the visNev
     */
    public String getVisNev() {
        return visNev;
    }

    /**
     * @param visNev the visNev to set
     */
    public void setVisNev(String visNev) {
        this.visNev = visNev;
    }

    /**
     * @return the szulNev
     */
    public String getSzulNev() {
        return szulNev;
    }

    /**
     * @param szulNev the szulNev to set
     */
    public void setSzulNev(String szulNev) {
        this.szulNev = szulNev;
    }

    /**
     * @return the aNev
     */
    public String getaNev() {
        return aNev;
    }

    /**
     * @param aNev the aNev to set
     */
    public void setaNev(String aNev) {
        this.aNev = aNev;
    }

    /**
     * @return the szulDat
     */
    public Date getSzulDat() {
        return szulDat;
    }

    /**
     * @param szulDat the szulDat to set
     */
    public void setSzulDat(Date szulDat) {
        this.szulDat = szulDat;
    }

    /**
     * @return the neme
     */
    public String getNeme() {
        return neme;
    }

    /**
     * @param neme the neme to set
     */
    public void setNeme(String neme) {
        this.neme = neme;
    }

    /**
     * @return the allampKod
     */
    public String getAllampKod() {
        return allampKod;
    }

    /**
     * @param allampKod the allampKod to set
     */
    public void setAllampKod(String allampKod) {
        this.allampKod = allampKod;
    }

    /**
     * @return the allampDekod
     */
    public String getAllampDekod() {
        return allampDekod;
    }

    /**
     * @param allampDekod the allampDekod to set
     */
    public void setAllampDekod(String allampDekod) {
        this.allampDekod = allampDekod;
    }

    /**
     * @return the okmLista
     */
    public ArrayList<OkmanyDTO> getOkmLista() {
        return okmLista;
    }

    /**
     * @param okmLista the okmLista to set
     */
    public void setOkmLista(ArrayList<OkmanyDTO> okmLista) {
        this.okmLista = okmLista;
    }
    private static final long serialVersionUID = 4L;
    
    private String visNev;
    
    private String szulNev;
    
    private String aNev;
    
    private Date szulDat;
    
    private String neme;
    
    private String allampKod;
    
    private String allampDekod;
    
    private ArrayList<OkmanyDTO> okmLista;
    
}
