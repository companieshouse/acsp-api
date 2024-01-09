package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.service.CostService;
import uk.gov.companieshouse.api.model.payment.Cost;


import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/transaction/{transaction_id}/acsp/{acsp_id}/costs")
public class CostsController {

    private final CostService costService;

    @Autowired
    public CostsController(CostService costService) {
        this.costService = costService;
    }

    @GetMapping
    public ResponseEntity<List<Cost>> getCosts() {
    System.out.println("Entered Costs Controller");
        var cost = costService.getCosts();

        return ResponseEntity.ok(Collections.singletonList(cost));
    }
}
