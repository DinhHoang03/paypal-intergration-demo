package com.javhoang03.paypal_intergration.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Lớp cấu hình PayPalConfig, dùng để cấu hình và cung cấp đối tượng APIContext cho Spring Context
@Configuration
public class PayPalConfig {

    // Lấy giá trị client-id từ file cấu hình (application.properties hoặc application.yml)
    @Value("${paypal.client-id}")
    private String clientId;

    // Lấy giá trị client-secret từ file cấu hình
    @Value("${paypal.client-secret}")
    private String clientSecret;

    // Lấy giá trị mode từ file cấu hình (sandbox hoặc live)
    @Value("${paypal.mode}")
    private String mode;

    // Định nghĩa một Bean APIContext để sử dụng trong toàn bộ ứng dụng
    @Bean
    public APIContext apiContext(){
        // Tạo và trả về một đối tượng APIContext với các thông tin clientId, clientSecret và mode
        return new APIContext(clientId, clientSecret, mode);
    }
}

