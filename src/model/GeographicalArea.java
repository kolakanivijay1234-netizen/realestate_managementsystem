package model;

/**
 * Represents a geographical area served by agencies and mandates.
 */
public class GeographicalArea {
    private int areaId;
    private String areaName;
    private int population;

    public GeographicalArea() {
    }

    public GeographicalArea(int areaId, String areaName, int population) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.population = population;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    @Override
    public String toString() {
        return "GeographicalArea{" +
                "areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", population=" + population +
                '}';
    }
}
