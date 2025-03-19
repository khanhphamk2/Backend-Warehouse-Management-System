package org.khanhpham.wms.database;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.khanhpham.wms.common.OrderStatus;
import org.khanhpham.wms.domain.model.*;
import org.khanhpham.wms.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SalesOrderRepository salesOrderRepository;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedSuppliers();
        seedCategories();
        seedCustomers();
        seedProducts();
        seedPurchaseOrders();
        seedSalesOrders();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin role");

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("User role");

            Role managerRole = new Role();
            managerRole.setName("MANAGER");
            managerRole.setDescription("Manager role");

            List<Role> roles = List.of(adminRole, userRole, managerRole);
            roleRepository.saveAll(roles);
        } else
            log.info("Roles already exist");
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<Role> roles = roleRepository.findAll();

            IntStream.range(0, 500).forEach(i -> {
                User user = new User();
                user.setUsername(faker.internet().username());
                user.setPassword(password);
                user.setName(faker.name().fullName());
                user.setEmail(faker.internet().emailAddress());
                user.setPhone(faker.phoneNumber().phoneNumber());

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roles.get(faker.random().nextInt(roles.size())));
                user.setRoles(userRoles);

                userRepository.save(user);
            });

            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(password);
            admin.setName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPhone("0123456789");
            Set<Role> adminRoles = new HashSet<>(roleRepository.findAll());
            admin.setRoles(adminRoles);

            userRepository.save(admin);
        } else
            log.info("Users already exist");
    }

    private void seedSuppliers() {
        if (supplierRepository.count() == 0) {
            IntStream.range(0, 300).forEach(i -> {
                Supplier supplier = new Supplier();
                supplier.setName(faker.company().name());
                supplier.setContactInfo(faker.internet().emailAddress());
                supplier.setAddress(faker.address().fullAddress());
                supplier.setPhone(faker.phoneNumber().cellPhone());
                supplier.setEmail(faker.internet().emailAddress());
                supplier.setDescription(faker.lorem().sentence());

                supplierRepository.save(supplier);
            });
        } else
            log.info("Suppliers already exist");
    }

    private void seedCustomers() {
        if (customerRepository.count() == 0) {
            IntStream.range(0, 300).forEach(i -> {
                Customer customer = new Customer();
                customer.setName(faker.name().fullName());
                customer.setPhone(faker.phoneNumber().cellPhone());
                customer.setEmail(faker.internet().emailAddress());
                customer.setAddress(faker.address().fullAddress());

                customerRepository.save(customer);
            });
        } else
            log.info("Customers already exist");
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            IntStream.range(0, 50).forEach(i -> {
                String name = faker.commerce().department();
                if (!categoryRepository.existsByName(name)) {
                    Category category = new Category();
                    category.setName(name);
                    category.setDescription(faker.lorem().sentence());

                    categoryRepository.save(category);
                }
            });
        } else
            log.info("Categories already exist");
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<Category> categories = categoryRepository.findAll();

            IntStream.range(0, 1000).forEach(i -> {
                Product product = new Product();
                product.setName(faker.commerce().productName());
                product.setDescription(faker.lorem().paragraph());
                product.setPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
                product.setSku(faker.code().isbn10());
                product.setExpiryDate(LocalDateTime.now().plusDays(faker.number().numberBetween(30, 365)));
                product.setUnit("pcs");
                product.setImageUrl(faker.internet().image());
                product.setQuantity(faker.number().numberBetween(1, 100));
                product.setSupplier(suppliers.get(faker.random().nextInt(suppliers.size())));
                Set<Category> productCategories = new HashSet<>();
                productCategories.add(categories.get(faker.random().nextInt(categories.size())));
                product.setCategories(productCategories);

                productRepository.save(product);
            });
        } else
            log.info("Products already exist");
    }

    private void seedPurchaseOrders() {
        if (purchaseOrderRepository.count() == 0) {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<Product> products = productRepository.findAll();

            IntStream.range(0, 500).forEach(i -> {
                PurchaseOrder po = new PurchaseOrder();
                po.setPoNumber(faker.code().isbn13());
                po.setSupplier(suppliers.get(faker.random().nextInt(suppliers.size())));
                po.setOrderDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 30)));
                po.setReceiveDate(po.getOrderDate().plusDays(faker.number().numberBetween(1, 10)));
                po.setStatus(OrderStatus.PROCESSING);
                po.setNotes(faker.lorem().sentence());

                Set<PurchaseOrderItem> items = new HashSet<>();
                final BigDecimal[] totalAmount = {BigDecimal.ZERO};
                Set<Product> selectedProducts = new HashSet<>();

                IntStream.range(0, faker.number().numberBetween(1, 10)).forEach(j -> {
                    Product product;
                    do {
                        product = products.get(random.nextInt(products.size()));
                    } while (selectedProducts.contains(product));
                    selectedProducts.add(product);

                    int quantity = faker.number().numberBetween(1, 1000);
                    BigDecimal unitPrice = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 500));
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setPurchaseOrder(po);
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    item.setTotalPrice(totalPrice);
                    item.setNotes(faker.lorem().sentence());

                    items.add(item);
                    totalAmount[0] = totalAmount[0].add(totalPrice);
                });

                po.setTotalAmount(totalAmount[0]);
                po.setPurchaseOrderItems(items);
                purchaseOrderRepository.save(po);
            });
        } else
           log.info("Purchase orders already exist");
    }

    private void seedSalesOrders() {
        if (salesOrderRepository.count() == 0) {
            List<Customer> customers = customerRepository.findAll();
            List<Product> products = productRepository.findAll();

            IntStream.range(0, 500).forEach(i -> {
               SalesOrder so = new SalesOrder();
                so.setSoNumber(faker.code().isbn13());
                so.setCustomer(customers.get(faker.random().nextInt(customers.size())));
                so.setOrderDate(LocalDate.now().minusDays(faker.number().numberBetween(1, 30)));
                so.setExpectedShipmentDate(so.getOrderDate().plusDays(faker.number().numberBetween(1, 10)));
                so.setStatus(OrderStatus.PROCESSING);
                so.setNotes(faker.lorem().sentence());
                so.setDiscount(BigDecimal.valueOf(faker.number().numberBetween(1, 100)));
                so.setShippingCost(BigDecimal.valueOf(faker.number().numberBetween(1, 100)));
                so.setTaxAmount(BigDecimal.valueOf(faker.number().numberBetween(1, 100)));

                Set<SalesOrderItem> items = new HashSet<>();
                final BigDecimal[] subTotal = {BigDecimal.ZERO};
                Set<Product> selectedProducts = new HashSet<>();

                IntStream.range(0, faker.number().numberBetween(1, 10)).forEach(j -> {
                    Product product;
                    do {
                        product = products.get(random.nextInt(products.size()));
                    } while (selectedProducts.contains(product));
                    selectedProducts.add(product);

                    int quantity = faker.number().numberBetween(1, 1000);
                    BigDecimal unitPrice = BigDecimal.valueOf(faker.number().randomDouble(2, 10, 500));
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

                    SalesOrderItem item = new SalesOrderItem();
                    item.setSalesOrder(so);
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    item.setUnitPrice(unitPrice);
                    item.setTotalPrice(totalPrice);
                    item.setNotes(faker.lorem().sentence());
                    items.add(item);

                    subTotal[0] = subTotal[0].add(totalPrice);
                });
                so.setSubtotal(subTotal[0]);
                so.setSalesOrderItems(items);
                so.setTotalAmount(subTotal[0].add(so.getShippingCost()).add(so.getTaxAmount()).subtract(so.getDiscount()));

                salesOrderRepository.save(so);
            });
        } else
            log.info("Sales orders already exist");
    }
}
