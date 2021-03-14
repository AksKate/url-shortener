package com.example.url.shortener.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    "com.example.url.shortener.service"
)
class ServiceConfiguration