package com.littlestore.service;

//import java.time.LocalDateTime;
import java.util.List;

//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.littlestore.entity.Cart;
//import com.littlestore.entity.CartDetail;
import com.littlestore.entity.Customer;
import com.littlestore.entity.Order;
//import com.littlestore.entity.OrderDetail;
//import com.littlestore.entity.Product;
import com.littlestore.repository.OrderRepository;

@Service
//@Transactional
public class OrderService {
//	@Autowired
//	private SessionFactory sessionFactory;
    @Autowired
	private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
//    @Autowired
//    private ProductService productService;

    public OrderService(OrderRepository orderRepository) {
    	this.orderRepository = orderRepository;
    }
	
    
/*	@Transactional(rollbackFor=Exception.class)
	public void saveOrder(Cart cart) {
		Session session = this.sessionFactory.getCurrentSession();
		
		Order order = new Order();
		order.setCustomer(customerService.findById(cart.getCustomer().getId()));
		order.setOrderDateTime(LocalDateTime.now());
		order.setReqDeliveryDateTime(null);
		
		session.persist(order);
		
		List<CartDetail> cartItems = cart.getCartItems();
		
		for (CartDetail item : cartItems) {
			OrderDetail lineItem = new OrderDetail();
			lineItem.setOrder(order);
			String upc = item.getProduct().getUpc();
			Product product = productService.get(upc);
			lineItem.setProduct(product);//(item.getProduct());
			lineItem.setQty(item.getQty());
			lineItem.setPrice(item.getPrice());
			lineItem.setLineNumber(item.getLineNumber()); // Might have to generate
			
			session.persist(lineItem);
		}
	}*/

	@Transactional(rollbackFor=Exception.class)
    public void save(Order order) {	// Create new/Update cart in DB for current user
        orderRepository.save(order);
    }
     
    public List<Order> listAll() {
        return (List<Order>) orderRepository.findAll();
    }
     
    public Order get(Integer id) {
        return orderRepository.findById(id).get();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        orderRepository.deleteById(id);
    }
    
	@Transactional(rollbackFor=Exception.class)
	public void delete(Order order) {
		orderRepository.delete(order);
	}

    public List<Order> findByCustomer(Customer customer) {
    	return orderRepository.findByCustomer(customer);
    }
    public List<Order> findByCustomerId(int customerId) {
    	Customer customer = customerService.findById(customerId);
        return orderRepository.findByCustomer(customer);
    }
    
	public List<Order> findByCustomerEmail(String email) {
		Customer customer = customerService.findByEmail(email);
		return orderRepository.findByCustomer(customer);
	}
}