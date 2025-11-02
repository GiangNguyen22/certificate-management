package com.example.demo.service.interfaces;

import org.springframework.core.io.ByteArrayResource;

public interface p12Service {
    ByteArrayResource generateP12(String staffCode);
}
