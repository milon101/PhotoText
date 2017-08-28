package com.tag.phototext;

public class PDFDoc {
    String name, path, type;
    Boolean checked;
    private String num;

    public PDFDoc(String name, String path, String type) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.checked = false;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {

        return type;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getChecked() {

        return checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNum() {
        return num;
    }
}
