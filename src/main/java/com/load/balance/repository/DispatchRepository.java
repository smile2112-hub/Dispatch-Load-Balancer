package com.load.balance.repository;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchRepository {
    private final Map<String, DeliveryOrder> orders = new ConcurrentHashMap<>();
    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();

    public synchronized void replaceOrders(List<DeliveryOrder> newOrders) {
        orders.clear();
        newOrders.forEach(order -> orders.put(order.orderId(), order));
    }

    public synchronized void replaceVehicles(List<Vehicle> newVehicles) {
        vehicles.clear();
        newVehicles.forEach(vehicle -> vehicles.put(vehicle.vehicleId(), vehicle));
    }

    public List<DeliveryOrder> orders() {
        return new ArrayList<>(orders.values());
    }

    public List<Vehicle> vehicles() {
        return new ArrayList<>(vehicles.values());
    }
}