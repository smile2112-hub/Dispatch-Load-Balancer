package com.load.balance.service;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Vehicle;
import com.load.balance.repository.DispatchRepository;
import com.load.balance.web.DispatchResponses;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {
    private final DispatchRepository repository;

    public DispatchService(DispatchRepository repository) {
        this.repository = repository;
    }

    public void saveOrders(List<DeliveryOrder> orders) {
        requireUnique(orders.stream().map(DeliveryOrder::orderId).toList(), "orderId");
        repository.replaceOrders(orders);
    }

    public void saveVehicles(List<Vehicle> vehicles) {
        requireUnique(vehicles.stream().map(Vehicle::vehicleId).toList(), "vehicleId");
        repository.replaceVehicles(vehicles);
    }

    public DispatchResponses.PlanResponse createPlan() {
        List<Vehicle> vehicles = repository.vehicles().stream()
                .sorted(Comparator.comparing(Vehicle::vehicleId))
                .toList();
        if (vehicles.isEmpty()) {
            throw new DispatchException("No vehicles have been submitted.");
        }

        List<DeliveryOrder> orders = repository.orders().stream()
                .sorted(Comparator.comparingInt((DeliveryOrder order) -> order.priority().rank())
                        .thenComparing(DeliveryOrder::orderId))
                .toList();
        Map<String, VehicleState> states = new LinkedHashMap<>();
        vehicles.forEach(vehicle -> states.put(vehicle.vehicleId(), new VehicleState(vehicle)));

        for (DeliveryOrder order : orders) {
            VehicleState nearest = states.values().stream()
                    .filter(state -> state.remainingCapacity() >= order.packageWeight())
                    .min(Comparator.<VehicleState>comparingDouble(state -> state.distanceTo(order))
                            .thenComparing(state -> state.vehicle.vehicleId()))
                    .orElseThrow(() -> new DispatchException(
                            "Order '" + order.orderId() + "' cannot be assigned within fleet capacity."));
            nearest.assign(order);
        }

        return new DispatchResponses.PlanResponse(states.values().stream()
                .map(VehicleState::toPlan)
                .toList());
    }

    private static void requireUnique(List<String> ids, String field) {
        if (ids.stream().anyMatch(Objects::isNull) || ids.size() != ids.stream().distinct().count()) {
            throw new DispatchException("Each " + field + " must be unique.");
        }
    }

    private static final class VehicleState {
        private final Vehicle vehicle;
        private final List<DeliveryOrder> assignedOrders = new ArrayList<>();
        private double totalLoad;
        private double lastLatitude;
        private double lastLongitude;
        private double totalDistance;

        private VehicleState(Vehicle vehicle) {
            this.vehicle = vehicle;
            this.lastLatitude = vehicle.currentLatitude();
            this.lastLongitude = vehicle.currentLongitude();
        }

        private double remainingCapacity() {
            return vehicle.capacity() - totalLoad;
        }

        private double distanceTo(DeliveryOrder order) {
            return DistanceCalculator.haversine(lastLatitude, lastLongitude,
                    order.latitude(), order.longitude());
        }

        private void assign(DeliveryOrder order) {
            totalDistance += distanceTo(order);
            totalLoad += order.packageWeight();
            lastLatitude = order.latitude();
            lastLongitude = order.longitude();
            assignedOrders.add(order);
        }

        private DispatchResponses.VehiclePlan toPlan() {
            return new DispatchResponses.VehiclePlan(vehicle.vehicleId(), totalLoad,
                    "%.2f km".formatted(totalDistance), List.copyOf(assignedOrders));
        }
    }
}