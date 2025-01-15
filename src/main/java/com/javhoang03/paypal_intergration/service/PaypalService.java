package com.javhoang03.paypal_intergration.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PaypalService {

    // Khai báo đối tượng APIContext, dùng để lưu trữ các thông tin cấu hình kết nối với PayPal API
    private final APIContext apiContext;

    // Constructor của lớp PaypalService, nhận đối tượng APIContext làm tham số
    public PaypalService(APIContext apiContext) {
        // Gán giá trị của đối tượng APIContext vào trường apiContext
        this.apiContext = apiContext;
    }

    //Phương thức tạo giao dịch
    public Payment createPayment(Double total,
                                 String currency,
                                 String method,
                                 String intent,
                                 String description,
                                 String cancelUrl,
                                 String successUrl) throws PayPalRESTException {

        // Tạo đối tượng lưu trữ số tiền cho giao dịch
        Amount amount = new Amount(); // Đối tượng số lượng tiền
        amount.setCurrency(currency); // Đặt loại tiền tệ (ví dụ: USD, EUR)
        amount.setTotal(String.format(Locale.US, "%.2f", total)); // Định dạng số tiền với 2 chữ số thập phân

        // Tạo đối tượng giao dịch (Transaction)
        Transaction transaction = new Transaction();
        transaction.setDescription(description); // Đặt thông tin mô tả giao dịch
        transaction.setAmount(amount); // Gắn số tiền giao dịch vào đối tượng Transaction

        // Danh sách giao dịch (có thể chứa nhiều giao dịch trong một Payment)
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction); // Thêm giao dịch vào danh sách

        // Tạo đối tượng thanh toán (Payer)
        Payer payer = new Payer();
        payer.setPaymentMethod(method); // Đặt phương thức thanh toán (ví dụ: paypal, credit_card)

        // Tạo đối tượng thanh toán chính (Payment)
        Payment payment = new Payment();
        payment.setIntent(intent); // Đặt mục đích giao dịch: "sale" (bán hàng), "authorize" (ủy quyền) hoặc "order" (đặt hàng)
        payment.setPayer(payer); // Gắn thông tin người thanh toán vào Payment
        payment.setTransactions(transactions); // Gắn danh sách giao dịch vào Payment

        // Tạo đối tượng URL chuyển hướng (RedirectUrls)
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl); // URL chuyển hướng khi người dùng hủy giao dịch
        redirectUrls.setReturnUrl(successUrl); // URL chuyển hướng khi giao dịch hoàn tất (thành công)

        payment.setRedirectUrls(redirectUrls); // Gắn URL chuyển hướng vào Payment

        // Gửi yêu cầu tạo Payment tới PayPal API
        return payment.create(apiContext); // Phương thức này sẽ gửi Payment tới PayPal API để xử lý
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecute);
    }
}
