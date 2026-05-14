package es.codeurjc.daw.library.dto;

import java.util.Map;

public class ChartDto {
    private String name;
    private Map<String, Long> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Long> getData() {
        return data;
    }

    public void setData(Map<String, Long> data) {
        this.data = data;
    }
}

