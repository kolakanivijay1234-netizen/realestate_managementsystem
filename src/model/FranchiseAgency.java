package model;

/**
 * Represents a franchise real estate agency operating under a brand.
 */
public class FranchiseAgency extends Agency {
    private String franchiseBrand;

    public FranchiseAgency() {
        super();
    }

    public FranchiseAgency(int agencyId, String agencyName, String agencyType, String address, String phone, String franchiseBrand) {
        super(agencyId, agencyName, agencyType, address, phone);
        this.franchiseBrand = franchiseBrand;
    }

    public String getFranchiseBrand() {
        return franchiseBrand;
    }

    public void setFranchiseBrand(String franchiseBrand) {
        this.franchiseBrand = franchiseBrand;
    }

    @Override
    public String getAgencyDescription() {
        return "Franchise Agency: " + getAgencyName() + " under brand " + franchiseBrand;
    }

    @Override
    public String toString() {
        return "FranchiseAgency{" +
                "agencyId=" + getAgencyId() +
                ", agencyName='" + getAgencyName() + '\'' +
                ", agencyType='" + getAgencyType() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", franchiseBrand='" + franchiseBrand + '\'' +
                '}';
    }
}
