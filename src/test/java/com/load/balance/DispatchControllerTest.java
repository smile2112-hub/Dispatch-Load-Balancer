package com.load.balance;

import com.load.balance.model.DeliveryOrder;
import com.load.balance.model.Priority;
import com.load.balance.service.DispatchService;
import com.load.balance.web.ApiExceptionHandler;
import com.load.balance.web.DispatchController;
import com.load.balance.web.DispatchResponses;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DispatchControllerTest {
    private final DispatchService service = mock(DispatchService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new DispatchController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void acceptsValidOrders() throws Exception {
        String body = "{\"orders\":[{\"orderId\":\"ORD001\",\"latitude\":12.9,"
                + "\"longitude\":77.5,\"address\":\"MG Road\",\"packageWeight\":10,"
                + "\"priority\":\"HIGH\"}]}";

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Delivery orders accepted."))
                .andExpect(jsonPath("$.status").value("success"));

        verify(service).saveOrders(anyList());
    }

    @Test
    void acceptsValidVehicles() throws Exception {
        String body = "{\"vehicles\":[{\"vehicleId\":\"VEH001\",\"capacity\":100,"
                + "\"currentLatitude\":12.9,\"currentLongitude\":77.5,"
                + "\"currentAddress\":\"Indiranagar\"}]}";

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Vehicle details accepted."))
                .andExpect(jsonPath("$.status").value("success"));

        verify(service).saveVehicles(anyList());
    }

    @Test
    void returnsTheDispatchPlan() throws Exception {
        DeliveryOrder order = new DeliveryOrder("ORD001", 12.9, 77.5, "MG Road", 10.0, Priority.HIGH);
        when(service.createPlan()).thenReturn(new DispatchResponses.PlanResponse(List.of(
                new DispatchResponses.VehiclePlan("VEH001", 10.0, "5.00 km", List.of(order)))));

        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("VEH001"))
                .andExpect(jsonPath("$.dispatchPlan[0].totalLoad").value(10.0))
                .andExpect(jsonPath("$.dispatchPlan[0].totalDistance").value("5.00 km"))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders[0].address").value("MG Road"));
    }

    @Test
    void rejectsAnEmptyOrdersCollection() throws Exception {
        String body = "{\"orders\":[]}";

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsInvalidOrderFields() throws Exception {
        String body = "{\"orders\":[{\"orderId\":\"ORD001\",\"latitude\":100,"
                + "\"longitude\":77.5,\"address\":\"MG Road\",\"packageWeight\":0,"
                + "\"priority\":\"HIGH\"}]}";

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.orders[0].latitude").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid request"));
    }

    @Test
    void rejectsUnknownPriorityValues() throws Exception {
        String body = "{\"orders\":[{\"orderId\":\"ORD001\",\"latitude\":12.9,"
                + "\"longitude\":77.5,\"address\":\"MG Road\",\"packageWeight\":10,"
                + "\"priority\":\"URGENT\"}]}";

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Request body is malformed or contains an invalid value."));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicles\":["))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }
}