package seng302.data;


public class Term {

    private String musicalTermName;
    private String musicalTermCategory;
    private String musicalTermOrigin;
    private String musicalTermDefinition;


    public Term(String name, String cat, String origin, String definition) {
        this.musicalTermName = name;
        this.musicalTermCategory = cat;
        this.musicalTermOrigin = origin;
        this.musicalTermDefinition = definition;
    }


    public String getMusicalTermName() {
        return this.musicalTermName;
    }

    public String getMusicalTermCategory() {
        return this.musicalTermCategory;
    }

    public String getMusicalTermOrigin() {
        return this.musicalTermOrigin;
    }

    public String getMusicalTermDefinition() {
        return this.musicalTermDefinition;
    }

    /**
     * Updates any field of a musical term with a new value
     *
     * @param editedField The field to be updated - name, category, origin, description
     * @param newValue    The new value to put in that field
     */
    public void updateInfo(String editedField, String newValue) {
        switch (editedField) {
            case "name":
                this.musicalTermName = newValue;
                break;
            case "category":
                this.musicalTermCategory = newValue;
                break;
            case "origin":
                this.musicalTermOrigin = newValue;
                break;
            case "definition":
                this.musicalTermDefinition = newValue;
                break;
        }

    }


}