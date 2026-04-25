package com.freelix.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationDto {

    @NotBlank(message = "Proposal is required")
    private String proposal;

    @NotNull(message = "Bid amount is required")
    @Min(value = 1, message = "Bid must be positive")
    private Double bidAmount;

    private Integer estimatedDays;

    public ApplicationDto() {}

    public String getProposal() { return proposal; }
    public void setProposal(String proposal) { this.proposal = proposal; }

    public Double getBidAmount() { return bidAmount; }
    public void setBidAmount(Double bidAmount) { this.bidAmount = bidAmount; }

    public Integer getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(Integer estimatedDays) { this.estimatedDays = estimatedDays; }
}
