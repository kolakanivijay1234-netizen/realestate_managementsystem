package model;

/**
 * Represents an independent real estate agency owned by an individual.
 */
public class IndependentAgency extends Agency {
    private String ownerName;

    public IndependentAgency() {
        super();
    }

    public IndependentAgency(int agencyId, String agencyName, String agencyType, String address, String phone, String ownerName) {
        super(agencyId, agencyName, agencyType, address, phone);
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public String getAgencyDescription() {
        return "Independent Agency: " + getAgencyName() + " owned by " + ownerName;
    }

    @Override
    public String toString() {
        return "IndependentAgency{" +
                "agencyId=" + getAgencyId() +
                ", agencyName='" + getAgencyName() + '\'' +
                ", agencyType='" + getAgencyType() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
