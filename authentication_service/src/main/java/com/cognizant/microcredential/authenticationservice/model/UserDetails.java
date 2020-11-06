package com.cognizant.microcredential.authenticationservice.model;

import java.io.Serializable;
import java.util.Date;

public class UserDetails implements Serializable {

    private String username;

    private String userstatus;

    private Date usercreatedate;

    private Date userlastmodifieddate;

    private String email;

    private String firstname;

    private String lastname;

    private String dateofbirth;

    private String userid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserstatus() {
        return userstatus;
    }

    public void setUserstatus(String userstatus) {
        this.userstatus = userstatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getUsercreatedate() {
        return usercreatedate;
    }

    public void setUsercreatedate(Date usercreatedate) {
        this.usercreatedate = usercreatedate;
    }

    public Date getUserlastmodifieddate() {
        return userlastmodifieddate;
    }

    public void setUserlastmodifieddate(Date userlastmodifieddate) {
        this.userlastmodifieddate = userlastmodifieddate;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }
}
