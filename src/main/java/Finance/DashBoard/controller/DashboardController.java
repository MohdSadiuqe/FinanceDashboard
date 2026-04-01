package Finance.DashBoard.controller;

import Finance.DashBoard.service.DashboardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@Valid
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService service;

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return service.getSummary();
    }
}