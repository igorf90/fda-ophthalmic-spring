package com.fda.ophthalmic.controller;
import com.fda.ophthalmic.service.FdaService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
@RestController @Validated @RequestMapping("/api/fda")
public class FdaController {
 private final FdaService service;
 public FdaController(FdaService service){this.service=service;}
 @GetMapping("/510k/{number}") public Object k510(@PathVariable @Pattern(regexp="K[0-9]{6}") String number){return service.k510(number);}
 @GetMapping("/pma/{number}") public Object pma(@PathVariable @Pattern(regexp="P[0-9]{6}") String number){return service.pma(number);}
 @GetMapping("/classification/{code}") public Object classification(@PathVariable @Pattern(regexp="[A-Za-z0-9]{3}") String code){return service.classification(code.toUpperCase());}
 @GetMapping("/udi/product-codes/{number}") public Object codes(@PathVariable @Pattern(regexp="(?:K[0-9]{6}|DEN[0-9]{6}|P[0-9]{6})") String number){return service.udiCodes(number);}
 @GetMapping("/bulk/manifest") public Object manifest(){return service.manifest();}
}
