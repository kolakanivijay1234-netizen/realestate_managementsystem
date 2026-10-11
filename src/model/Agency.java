package model;

/**
 * Superclass representing a real estate agency.
 */
public class Agency {
    private int agencyId;
    private String agencyName;
    private String agencyType;
    private String address;
    private String phone;

    public Agency() {
    }

    public Agency(int agencyId, String agencyName, String agencyType, String address, String phone) {
        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.agencyType = agencyType;
        this.address = address;
        this.phone = phone;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getAgencyType() {
        return agencyType;
    }

    public void setAgencyType(String agencyType) {
        this.agencyType = agencyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns a description of the agency. Can be overridden by subclasses for polymorphism.
     *
     * @return agency description
     */
    public String getAgencyDescription() {
        return "Agency: " + agencyName;
    }

    @Override
    public String toString() {
        return "Agency{" +
                "agencyId=" + agencyId +
                ", agencyName='" + agencyName + '\'' +
                ", agencyType='" + agencyType + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
