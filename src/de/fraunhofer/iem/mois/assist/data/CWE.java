package de.fraunhofer.iem.mois.assist.data;

public class CWE {

        String cweId;
        String name;
        String description;


        public CWE(String cweId, String cweName, String cweDescriptin) {
            this.cweId = cweId;
            this.name = cweName;
            this.description = cweDescriptin;

        }

    public String getCweId() {
        return cweId;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}

