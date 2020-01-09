package com.example.talpe.weddingpusher;


import java.io.Serializable;

public class UserBoundary implements Serializable {

    public enum UserRole{PLAYER,MANAGER,ADMIN}
    private Key key = new Key();
    private String username;
    private String avatar;
    private UserRole role;
    private long points;

    public UserBoundary() {
        key = new Key();
    }



    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }



    public static class Key implements Serializable {
        private String smartspace;
        private String email;

        public String getSmartspace() {
            return smartspace;
        }

        public void setSmartspace(String smartspace) {
            this.smartspace = smartspace;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}