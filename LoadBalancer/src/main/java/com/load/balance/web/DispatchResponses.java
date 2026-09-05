package com.load.balance.web;

import com.load.balance.model.DeliveryOrder;
import java.util.List;

public final class DispatchResponses {
    private DispatchResponses() {
    }

    public record AcceptedResponse(String message, String status) {
    }

    public record PlanResponse(List<VehiclePlan> dispatchPlan) {
    }

    public record VehiclePlan(String vehicleId, double totalLoad, String totalDistance,
                              List<DeliveryOrder> assignedOrders) {
    }
}