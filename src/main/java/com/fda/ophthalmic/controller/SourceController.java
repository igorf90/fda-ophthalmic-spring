package com.fda.ophthalmic.controller;
import com.fda.ophthalmic.service.SourceService;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/sources") public class SourceController {
 private final SourceService service;
 public SourceController(SourceService service){this.service=service;}
 @GetMapping("/{key:federal-register|ecfr|guidance|fda-510k-page}") public Object check(@PathVariable String key){return service.check(key);}
}
