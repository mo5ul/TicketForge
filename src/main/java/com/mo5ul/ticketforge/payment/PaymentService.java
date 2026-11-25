package com.mo5ul.ticketforge.payment;
import java.time.Instant;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mo5ul.ticketforge.event.EventSeat;
import com.mo5ul.ticketforge.event.EventSeatRepository;
import com.mo5ul.ticketforge.user.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
@Service
public class PaymentService{
    private final RazorpayClient razorpay;
    private final EventSeatRepository seats;
    private final UserRepository users;
    private final PaymentRepository payments;
    private final String keyId;
    public PaymentService(RazorpayClient razorpay, EventSeatRepository seats, UserRepository users, PaymentRepository payments, @Value("${app.razorpay.key-id}") String keyId){
        this.razorpay = razorpay;
        this.seats = seats;
        this.users = users;
        this.payments = payments;
        this.keyId = keyId;
    }
    @Transactional 
    public OrderResponse createOrder(Long eventId, Long seatId, String email){
        Long userId = users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user")).getId();
        EventSeat seat = seats.findForUpdate(eventId, seatId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        if (!seat.isHeldBy(userId, Instant.now())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No valid hold for this user");
        }
        try{
           JSONObject req = new JSONObject();
            req.put("amount", seat.getPricePaise());
            req.put("currency", "INR");
            req.put("receipt", "seat-" + seat.getId());
            Order order = razorpay.orders.create(req);
            String orderId = order.get("id");
            payments.save(new Payment(seat.getId(), userId, seat.getPricePaise(), orderId));
            return new OrderResponse(orderId, seat.getPricePaise(), "INR", keyId);
        }catch (RazorpayException e){
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Payment provider error");
        }
    }
}