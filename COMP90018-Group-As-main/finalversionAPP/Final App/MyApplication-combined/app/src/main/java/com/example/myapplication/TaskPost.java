package com.example.myapplication;

public class TaskPost {
    private String Title;
    private String Email;
    private String Description;
    private String image;
    private String Address;
    private String Date;
    public TaskPost(String Title,String Email,String Description,String image, String Address, String Date){
        this.Title = Title;
        this.Email = Email;
        this.Description = Description;
        this.image = image;
        this.Address = Address;
        this.Date = Date;
    }

    public String getTitle(){
        return Title;

    }

    public String getEmail(){
        return Email;

    }

    public String getDescription(){
        return Description;

    }
    public String getAddress(){
        return Address;

    }
    public String getDate(){
        return Date;

    }

    public String getImage(){
        return  image;
    }
}
