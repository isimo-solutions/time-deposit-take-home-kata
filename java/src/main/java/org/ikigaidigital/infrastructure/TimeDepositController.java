package org.ikigaidigital.infrastructure;

import java.util.List;

import org.ikigaidigital.TimeDepositDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/time-deposits")
class TimeDepositController {
    private final TimeDepositService service;

    TimeDepositController(TimeDepositService service) {
        this.service = service;
    }

    @GetMapping
    public List<TimeDepositDetails> findAll() {
        return service.findAll();
    }

    @PostMapping("/balance-updates")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBalances() {
        service.updateBalances();
    }
}
