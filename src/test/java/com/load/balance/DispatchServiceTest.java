package com.load.balance;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Priority;
import com.load.balance.model.Vehicle;
import com.load.balance.repository.DispatchRepository;
import com.load.balance.service.DispatchException;
import com.load.balance.service.DispatchService;
import com.load.balance.web.DispatchResponses;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DispatchServiceTest {
    private DispatchService service;

    @BeforeEach
    void setUp() {
        service = new DispatchService(new DispatchRepository());
    }

    @Test
    void assignsHighPriorityOrdersBeforeLowerPriorityOrders() {
        service.saveVehicles(List.of(vehicle("V1", 30, 0, 0)));
        service.saveOrders(List.of(
                order("LOW", 10, 0, 1, Priority.LOW),
                order("HIGH", 10, 0, 2, Priority.HIGH)));

        DispatchResponses.VehiclePlan plan = service.createPlan().dispatchPlan().getFirst();

        assertEquals(List.of("HIGH", "LOW"), plan.assignedOrders().stream()
                .map(DeliveryOrder::orderId).toList());
        assertEquals(20, plan.totalLoad());
    }

    @Test
    void selectsTheNearestVehicleWithEnoughCapacity() {
        service.saveVehicles(List.of(vehicle("FAR", 100, 0, 10), vehicle("NEAR", 20, 0, 1)));
        service.saveOrders(List.of(order("ORD-1", 10, 0, 1.1, Priority.HIGH)));

        DispatchResponses.PlanResponse plan = service.createPlan();

        assertEquals("NEAR", plan.dispatchPlan().stream()
                .filter(vehicle -> !vehicle.assignedOrders().isEmpty())
                .findFirst().orElseThrow().vehicleId());
    }

    @Test
    void rejectsAnOrderThatCannotFitInAnyVehicle() {
        service.saveVehicles(List.of(vehicle("V1", 5, 0, 0)));
        service.saveOrders(List.of(order("TOO-LARGE", 10, 0, 0, Priority.HIGH)));

        DispatchException exception = assertThrows(DispatchException.class, service::createPlan);

        assertEquals("Order 'TOO-LARGE' cannot be assigned within fleet capacity.", exception.getMessage());
    }

    @Test
    void acceptsAnOrderExactlyEqualToVehicleCapacity() {
        service.saveVehicles(List.of(vehicle("V1", 10, 0, 0)));
        service.saveOrders(List.of(order("EXACT", 10, 0, 0, Priority.MEDIUM)));

        DispatchResponses.VehiclePlan plan = service.createPlan().dispatchPlan().getFirst();

        assertEquals(10, plan.totalLoad());
        assertEquals(List.of("EXACT"), plan.assignedOrders().stream()
                .map(DeliveryOrder::orderId).toList());
    }

    @Test
    void includesVehiclesWithNoAssignedOrders() {
        service.saveVehicles(List.of(vehicle("V1", 10, 0, 0)));

        DispatchResponses.VehiclePlan plan = service.createPlan().dispatchPlan().getFirst();

        assertEquals(0, plan.totalLoad());
        assertEquals("0.00 km", plan.totalDistance());
        assertEquals(List.of(), plan.assignedOrders());
    }

    @Test
    void rejectsPlanningWithoutVehicles() {
        service.saveOrders(List.of(order("ORD-1", 1, 0, 0, Priority.LOW)));

        DispatchException exception = assertThrows(DispatchException.class, service::createPlan);

        assertEquals("No vehicles have been submitted.", exception.getMessage());
    }

    @Test
    void calculatesTheRouteAsTheSumOfAssignedStops() {
        service.saveVehicles(List.of(vehicle("V1", 30, 0, 0)));
        service.saveOrders(List.of(
                order("FIRST", 10, 0, 1, Priority.HIGH),
                order("SECOND", 10, 0, 2, Priority.LOW)));

        DispatchResponses.VehiclePlan plan = service.createPlan().dispatchPlan().getFirst();

        assertEquals("222.39 km", plan.totalDistance());
    }

    @Test
    void rejectsDuplicateOrderIdsBeforeReplacingExistingOrders() {
        service.saveOrders(List.of(order("EXISTING", 1, 0, 0, Priority.LOW)));

        DispatchException exception = assertThrows(DispatchException.class,
                () -> service.saveOrders(List.of(
                        order("DUPLICATE", 1, 0, 0, Priority.LOW),
                        order("DUPLICATE", 2, 0, 0, Priority.HIGH))));

        assertEquals("Each orderId must be unique.", exception.getMessage());
    }

    @Test
    void rejectsDuplicateVehicleIds() {
        DispatchException exception = assertThrows(DispatchException.class,
                () -> service.saveVehicles(List.of(
                        vehicle("DUPLICATE", 10, 0, 0),
                        vehicle("DUPLICATE", 20, 1, 1))));

        assertEquals("Each vehicleId must be unique.", exception.getMessage());
    }

    private static DeliveryOrder order(String id, double weight, double latitude,
                                       double longitude, Priority priority) {
        return new DeliveryOrder(id, latitude, longitude, "Test address", weight, priority);
    }

    private static Vehicle vehicle(String id, double capacity, double latitude, double longitude) {
        return new Vehicle(id, capacity, latitude, longitude, "Vehicle address");
    }
}