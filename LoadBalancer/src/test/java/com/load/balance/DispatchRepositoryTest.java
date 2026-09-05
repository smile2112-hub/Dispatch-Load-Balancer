package com.load.balance;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Priority;
import com.load.balance.model.Vehicle;
import com.load.balance.repository.DispatchRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DispatchRepositoryTest {
    private final DispatchRepository repository = new DispatchRepository();

    @Test
    void replacesOrdersAsAnUpsertCollection() {
        DeliveryOrder first = new DeliveryOrder("OLD", 0.0, 0.0, "Old", 1.0, Priority.LOW);
        DeliveryOrder replacement = new DeliveryOrder("NEW", 1.0, 1.0, "New", 2.0, Priority.HIGH);

        repository.replaceOrders(List.of(first));
        repository.replaceOrders(List.of(replacement));

        assertEquals(List.of(replacement), repository.orders());
    }

    @Test
    void replacesVehiclesAsAnUpsertCollection() {
        Vehicle first = new Vehicle("OLD", 10.0, 0.0, 0.0, "Old");
        Vehicle replacement = new Vehicle("NEW", 20.0, 1.0, 1.0, "New");

        repository.replaceVehicles(List.of(first));
        repository.replaceVehicles(List.of(replacement));

        assertEquals(List.of(replacement), repository.vehicles());
    }
}