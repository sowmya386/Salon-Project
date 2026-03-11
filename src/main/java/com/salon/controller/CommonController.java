package com.salon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @GetMapping("/info")
    public String info() {
        return "ADMIN OR CUSTOMER ACCESS OK";
    }
}
