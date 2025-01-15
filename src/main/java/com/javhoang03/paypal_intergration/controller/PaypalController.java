package com.javhoang03.paypal_intergration.controller;

import com.javhoang03.paypal_intergration.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller // Đánh dấu lớp là một Controller trong Spring MVC, nơi xử lý các yêu cầu HTTP từ người dùng
public class PaypalController {

    private final PaypalService paypalService; // Khai báo dịch vụ PaypalService để xử lý logic thanh toán
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaypalController.class); // Tạo đối tượng Logger cho class này

    // Constructor để inject PaypalService vào controller
    public PaypalController(PaypalService paypalService) {
        this.paypalService = paypalService;
    }

    // Phương thức xử lý yêu cầu GET đến trang chủ, trả về tên view "index"
    @GetMapping("/")
    public String home(){
        return "index"; // Trả về view index.html
    }

    // Phương thức xử lý yêu cầu POST khi người dùng gửi thông tin thanh toán từ form
    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method, // Tham số phương thức thanh toán
            @RequestParam("amount") String amount, // Tham số số tiền thanh toán
            @RequestParam("currency") String currency, // Tham số loại tiền tệ
            @RequestParam("description") String description){ // Tham số mô tả giao dịch

        try {
            // Địa chỉ URL để quay lại trang sau khi thanh toán thành công hoặc bị hủy
            String cancelUrl = "http://localhost:8080/payment/cancel";
            String successUrl = "http://localhost:8080/payment/success";

            // Gọi phương thức createPayment của PaypalService để tạo giao dịch
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount), // Chuyển đổi số tiền từ String sang Double
                    currency, // Loại tiền tệ
                    method, // Phương thức thanh toán
                    "sale", // Loại giao dịch (ở đây là sale)
                    description, // Mô tả giao dịch
                    cancelUrl, // URL hủy giao dịch
                    successUrl // URL thành công giao dịch
            );

            // Duyệt qua các liên kết trả về từ Paypal, tìm và chuyển hướng đến liên kết approval_url (URL thanh toán)
            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref()); // Chuyển hướng người dùng đến URL thanh toán
                }
            }

        } catch (PayPalRESTException e) {
            log.error("Error occurred: ", e); // Ghi lại lỗi nếu có
        }
        return new RedirectView("/payment/error"); // Trả về trang lỗi nếu có vấn đề xảy ra
    }

    // Phương thức xử lý yêu cầu GET khi thanh toán thành công
    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId, // ID giao dịch
            @RequestParam("PayerID") String payerId // ID người thanh toán
    ){
        try {
            // Gọi phương thức executePayment để xác nhận thanh toán
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equals("approved")){ // Kiểm tra trạng thái thanh toán
                return "paymentSuccess"; // Trả về view thành công nếu giao dịch được phê duyệt
            }
        }catch (PayPalRESTException e){
            log.error("Error occurred: ", e); // Ghi lại lỗi nếu có
        }
        return "paymentSuccess"; // Nếu có lỗi, vẫn trả về view thành công
    }

    // Phương thức xử lý yêu cầu GET khi người dùng hủy giao dịch
    @GetMapping("/payment/cancel")
    public String paymentCancel(){
        return "paymentCancel"; // Trả về view thông báo giao dịch bị hủy
    }

    // Phương thức xử lý yêu cầu GET khi có lỗi trong quá trình thanh toán
    @GetMapping("/payment/error")
    public String paymentError(){
        return "paymentError"; // Trả về view thông báo lỗi
    }
}
