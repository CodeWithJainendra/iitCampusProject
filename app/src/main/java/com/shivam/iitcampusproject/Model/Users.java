package com.shivam.iitcampusproject.Model;

public class Users {

   String username;
   String email;
   String mobile;
   String userType;
   String photoUrl;
   String city;
   String address_main_latitude;
   String address_main_longitude;
   String address_main;
   String address_second;
   String deafault_address;

   public Users() {
   }

   public Users(String username, String email, String mobile, String userType, String photoUrl, String city, String address_main_latitude, String address_main_longitude, String address_main, String address_second, String deafault_address) {
      this.username = username;
      this.email = email;
      this.mobile = mobile;
      this.userType = userType;
      this.photoUrl = photoUrl;
      this.city = city;
      this.address_main_latitude = address_main_latitude;
      this.address_main_longitude = address_main_longitude;
      this.address_main = address_main;
      this.address_second = address_second;
      this.deafault_address = deafault_address;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getMobile() {
      return mobile;
   }

   public void setMobile(String mobile) {
      this.mobile = mobile;
   }

   public String getUserType() {
      return userType;
   }

   public void setUserType(String userType) {
      this.userType = userType;
   }

   public String getPhotoUrl() {
      return photoUrl;
   }

   public void setPhotoUrl(String photoUrl) {
      this.photoUrl = photoUrl;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getAddress_main_latitude() {
      return address_main_latitude;
   }

   public void setAddress_main_latitude(String address_main_latitude) {
      this.address_main_latitude = address_main_latitude;
   }

   public String getAddress_main_longitude() {
      return address_main_longitude;
   }

   public void setAddress_main_longitude(String address_main_longitude) {
      this.address_main_longitude = address_main_longitude;
   }

   public String getAddress_main() {
      return address_main;
   }

   public void setAddress_main(String address_main) {
      this.address_main = address_main;
   }

   public String getAddress_second() {
      return address_second;
   }

   public void setAddress_second(String address_second) {
      this.address_second = address_second;
   }

   public String getDeafault_address() {
      return deafault_address;
   }

   public void setDeafault_address(String deafault_address) {
      this.deafault_address = deafault_address;
   }
}
