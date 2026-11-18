package com.load.balance.web;

import com.load.balance.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {
    private final DispatchService service;

    public DispatchController(DispatchService service) {
        this.service = service;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DispatchResponses.AcceptedResponse acceptOrders(
            @Valid @RequestBody DispatchRequests.OrdersRequest request) {
        service.saveOrders(request.orders());
        return new DispatchResponses.AcceptedResponse("Delivery orders accepted.", "success");
    }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DispatchResponses.AcceptedResponse acceptVehicles(
            @Valid @RequestBody DispatchRequests.VehiclesRequest request) {
        service.saveVehicles(request.vehicles());
        return new DispatchResponses.AcceptedResponse("Vehicle details accepted.", "success");
    }

    @GetMapping("/plan")
    public DispatchResponses.PlanResponse getPlan() {
        return service.createPlan();
    }
}