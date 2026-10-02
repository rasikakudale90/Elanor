package com.elanor.returns.dto;

import com.elanor.returns.enums.ReturnStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateReturnStatusRequest {

    @NotNull(message = "Return status is required")
    private ReturnStatus status;

    private String note;

    private boolean restockInventory = true;

    public UpdateReturnStatusRequest() {}

    public UpdateReturnStatusRequest(ReturnStatus status, String note, boolean restockInventory) {
        this.status = status;
        this.note = note;
        this.restockInventory = restockInventory;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isRestockInventory() {
        return restockInventory;
    }

    public void setRestockInventory(boolean restockInventory) {
        this.restockInventory = restockInventory;
    }
}
