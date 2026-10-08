package com.fda.ophthalmic.service;
import com.fda.ophthalmic.dto.FdaPageCheckDto;
import com.fda.ophthalmic.external.FdaPageRepository;
import org.springframework.stereotype.Service;
@Service
public class FdaPageService {
 private final FdaPageRepository repository;
 public FdaPageService(FdaPageRepository repository) { this.repository=repository; }
 public FdaPageCheckDto check(String kNumber) { return repository.check(kNumber); }
}
