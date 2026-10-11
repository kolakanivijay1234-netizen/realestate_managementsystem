package model;

/**
 * Represents a real estate agent.
 */
public class Agent {
    private int agentId;
    private String agentName;
    private String phone;
    private String email;
    private int agencyId;

    public Agent() {
    }

    public Agent(int agentId, String agentName, String phone, String email, int agencyId) {
        this.agentId = agentId;
        this.agentName = agentName;
        this.phone = phone;
        this.email = email;
        this.agencyId = agencyId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "agentId=" + agentId +
                ", agentName='" + agentName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", agencyId=" + agencyId +
                '}';
    }
}
