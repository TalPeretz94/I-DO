package com.example.talpe.weddingpusher;


import java.io.Serializable;
import java.util.Date;
import java.util.Map;



public class ActionBoundary {

    public Key getActionKey() {
        return actionKey;
    }

    public void setActionKey(Key actionKey) {
        this.actionKey = actionKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ElementKey getElement() {
        return element;
    }

    public void setElement(ElementKey element) {
        this.element = element;
    }

    public UserBoundary.Key getPlayer() {
        return player;
    }

    public void setPlayer(UserBoundary.Key player) {
        this.player = player;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    private Key actionKey;
    private String type;
    private Date created;
    private ElementKey element = new ElementKey();
    private UserBoundary.Key player = new UserBoundary.Key();
    private Map<String,Object> properties;


    public static class Key {
        public String getSmartspace() {
            return smartspace;
        }

        public void setSmartspace(String smartspace) {
            this.smartspace = smartspace;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String smartspace;
        private String id;
    }
    public static class ElementKey implements Serializable {
        public ElementKey() {
        }

        public ElementKey(String smartspace, String id) {
            this.smartspace = smartspace;
            this.id = id;
        }

        public String getSmartspace() {
            return smartspace;
        }

        public void setSmartspace(String smartspace) {
            this.smartspace = smartspace;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        private String smartspace;
        private String id;
    }

}
