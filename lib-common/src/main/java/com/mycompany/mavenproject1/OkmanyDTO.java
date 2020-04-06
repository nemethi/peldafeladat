/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Ruzsinak
 */
public class OkmanyDTO implements Serializable{

    /**
     * @return the okmTipus
     */
    public String getOkmTipus() {
        return okmTipus;
    }

    /**
     * @param okmTipus the okmTipus to set
     */
    public void setOkmTipus(String okmTipus) {
        this.okmTipus = okmTipus;
    }

    /**
     * @return the okmanySzam
     */
    public String getOkmanySzam() {
        return okmanySzam;
    }

    /**
     * @param okmanySzam the okmanySzam to set
     */
    public void setOkmanySzam(String okmanySzam) {
        this.okmanySzam = okmanySzam;
    }

    /**
     * @return the okmanyKep
     */
    public byte[] getOkmanyKep() {
        return okmanyKep;
    }

    /**
     * @param okmanyKep the okmanyKep to set
     */
    public void setOkmanyKep(byte[] okmanyKep) {
        this.okmanyKep = okmanyKep;
    }

    /**
     * @return the lejarDat
     */
    public Date getLejarDat() {
        return lejarDat;
    }

    /**
     * @param lejarDat the lejarDat to set
     */
    public void setLejarDat(Date lejarDat) {
        this.lejarDat = lejarDat;
    }

    /**
     * @return the ervenyes
     */
    public boolean isErvenyes() {
        return ervenyes;
    }

    /**
     * @param ervenyes the ervenyes to set
     */
    public void setErvenyes(boolean ervenyes) {
        this.ervenyes = ervenyes;
    }
    private static final long serialVersionUID = 1L;
    
    private String okmTipus;
    
    private String okmanySzam;
    
    private byte[] okmanyKep;
    
    private Date lejarDat;
    
    private boolean ervenyes;
    
}
