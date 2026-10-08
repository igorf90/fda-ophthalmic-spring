package com.fda.ophthalmic.controller;
import com.fda.ophthalmic.dto.FdaPageCheckDto;
import com.fda.ophthalmic.service.FdaPageService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sources/fda-510k-http")
public class FdaPageController {
 private final FdaPageService service;
 public FdaPageController(FdaPageService service) { this.service=service; }
 @GetMapping
 public FdaPageCheckDto check(@RequestParam(defaultValue="K251848")
  @jakarta.validation.constraints.Pattern(regexp="K[0-9]{6}") String id) {
  if(!id.matches("K[0-9]{6}")) throw new IllegalArgumentException("ID must match K followed by 6 digits");
  return service.check(id);
 }
}
