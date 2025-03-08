package org.khanhpham.wms.config;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.khanhpham.wms.domain.model.*;
import org.khanhpham.wms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private static final String PASSWORD = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

    private final Faker faker = new Faker();

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedUsers();
        seedSuppliers();
        seedCategories();
        seedCustomers();
        seedProducts();
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
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<Role> roles = roleRepository.findAll();

            IntStream.range(0, 100).forEach(i -> {
                User user = new User();
                user.setUsername(faker.internet().username());
                user.setPassword(PASSWORD);
                user.setName(faker.name().fullName());
                user.setEmail(faker.internet().emailAddress());
                user.setPhone(faker.phoneNumber().phoneNumber());

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roles.get(faker.random().nextInt(roles.size())));
                user.setRoles(userRoles);

                userRepository.save(user);
            });
        }
    }

    private void seedSuppliers() {
        if (supplierRepository.count() == 0) {
            IntStream.range(0, 100).forEach(i -> {
                Supplier supplier = new Supplier();
                supplier.setName(faker.company().name());
                supplier.setContactInfo(faker.internet().emailAddress());
                supplier.setAddress(faker.address().fullAddress());
                supplier.setPhone(faker.phoneNumber().cellPhone());
                supplier.setEmail(faker.internet().emailAddress());
                supplier.setDescription(faker.lorem().sentence());

                supplierRepository.save(supplier);
            });
        }
    }

    private void seedCustomers() {
        if (customerRepository.count() == 0) {
            IntStream.range(0, 100).forEach(i -> {
                Customer customer = new Customer();
                customer.setName(faker.name().fullName());
                customer.setPhone(faker.phoneNumber().cellPhone());
                customer.setEmail(faker.internet().emailAddress());
                customer.setAddress(faker.address().fullAddress());

                customerRepository.save(customer);
            });
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            IntStream.range(0, 30).forEach(i -> {
                String name = faker.commerce().department();
                if (!categoryRepository.existsByName(name)) {
                    Category category = new Category();
                    category.setName(name);
                    category.setDescription(faker.lorem().sentence());

                    categoryRepository.save(category);
                }
            });
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            List<Supplier> suppliers = supplierRepository.findAll();
            List<Category> categories = categoryRepository.findAll();

            IntStream.range(0, 300).forEach(i -> {
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
        }
    }
}
