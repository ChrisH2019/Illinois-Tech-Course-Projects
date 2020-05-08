package com.weiconghong.knowyourgovernment;

import java.io.Serializable;
import java.util.HashMap;

public class Official implements Serializable {

    private String office;
    private String name;
    private String party;

    private String address;
    private String phone;
    private String email;
    private String url;
    private String photoUrl;

    private String googlePlus;
    private String facebook;
    private String twitter;
    private String youTube;



    public Official(String office, String name, String party, String address,
                    String phone, String email, String url, String photoUrl,
                    String googlePlus, String facebook, String twitter, String youTube) {

        this.office = office;
        this.name = name;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.url = url;
        this.photoUrl = photoUrl;
        this.googlePlus = googlePlus;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youTube = youTube;
    }



    public String getOffice() { return office; }

    public String getName() { return name; }

    public String getParty() { return party; }

    public String getAddress() { return address; }

    public String getPhone() { return phone; }

    public String getEmail() { return email; }

    public String getUrl() { return url; }

    public String getPhotoUrl() { return photoUrl; }

    public String getGooglePlus() { return googlePlus; }

    public String getFacebook() { return facebook; }

    public String getTwitter() { return twitter; }

    public String getYouTube() { return youTube; }
}
