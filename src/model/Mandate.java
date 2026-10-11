package model;

import java.time.LocalDate;

/**
 * Represents a mandate held by an agent/agency in a geographical area.
 */
public class Mandate {
    private int mandateId;
    private int agentId;
    private int agencyId;
    private int areaId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String mandateType; // SALE, RENT, PURCHASE, LEASE

    public Mandate() {
    }

    public Mandate(int mandateId, int agentId, int agencyId, int areaId, LocalDate startDate, LocalDate endDate, String mandateType) {
        this.mandateId = mandateId;
        this.agentId = agentId;
        this.agencyId = agencyId;
        this.areaId = areaId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mandateType = mandateType;
    }

    public int getMandateId() {
        return mandateId;
    }

    public void setMandateId(int mandateId) {
        this.mandateId = mandateId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getMandateType() {
        return mandateType;
    }

    public void setMandateType(String mandateType) {
        this.mandateType = mandateType;
    }

    /**
     * Checks if the mandate is active on a given date.
     *
     * @param date the date to check
     * @return true if startDate <= date and endDate >= date
     */
    public boolean isActive(LocalDate date) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        return (!date.isBefore(startDate)) && (!date.isAfter(endDate));
    }

    /**
     * Validates that the end date is strictly after the start date.
     *
     * @throws IllegalArgumentException if endDate <= startDate or either date is null
     */
    public void validateDates() throws IllegalArgumentException {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Invalid Mandate: End date must be greater than start date.");
        }
    }

    @Override
    public String toString() {
        return "Mandate{" +
                "mandateId=" + mandateId +
                ", agentId=" + agentId +
                ", agencyId=" + agencyId +
                ", areaId=" + areaId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", mandateType='" + mandateType + '\'' +
                '}';
    }
}
