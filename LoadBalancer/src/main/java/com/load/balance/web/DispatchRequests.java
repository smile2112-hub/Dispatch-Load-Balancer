package com.load.balance.web;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public final class DispatchRequests {
    private DispatchRequests() {
    }

    public record OrdersRequest(@NotEmpty List<@Valid DeliveryOrder> orders) {
    }

    public record VehiclesRequest(@NotEmpty List<@Valid Vehicle> vehicles) {
    }
}