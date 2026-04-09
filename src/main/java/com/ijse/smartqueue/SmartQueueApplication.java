package com.ijse.smartqueue;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ijse.smartqueue.entity.Branch;
import com.ijse.smartqueue.entity.ServiceEntity;

import com.ijse.smartqueue.repository.BranchRepository;
import com.ijse.smartqueue.repository.ServiceRepository;
import com.ijse.smartqueue.repository.UserRepository;



@SpringBootApplication
public class SmartQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartQueueApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepo, BranchRepository branchRepo, ServiceRepository serviceRepo) {
        return args -> {
            // DO NOT CREATE TEST USERS - causes cross-user email issue
            // Users must be created through proper registration/signup
            
            if (branchRepo.count() == 0) {
                Branch branch1 = new Branch();
                branch1.setName("Downtown Branch");
                branch1.setAddress("123 Main St");
                branch1.setIsActive(true);
                branchRepo.save(branch1);

                Branch branch2 = new Branch();
                branch2.setName("Airport Plaza");
                branch2.setAddress("Terminal 1");
                branch2.setIsActive(true);
                branchRepo.save(branch2);
            }
            if (serviceRepo.count() == 0) {
                ServiceEntity svc1 = new ServiceEntity();
                svc1.setName("Customer Service");
                svc1.setDescription("General customer service");
                svc1.setAvgWaitTime(5);
                svc1.setIsActive(true);
                serviceRepo.save(svc1);

                ServiceEntity svc2 = new ServiceEntity();
                svc2.setName("Banking");
                svc2.setDescription("Banking solutions");
                svc2.setAvgWaitTime(4);
                svc2.setIsActive(true);
                serviceRepo.save(svc2);

                ServiceEntity svc3 = new ServiceEntity();
                svc3.setName("Mobile Recharge");
                svc3.setDescription("Mobile phone recharge services");
                svc3.setAvgWaitTime(3);
                svc3.setIsActive(true);
                serviceRepo.save(svc3);

                ServiceEntity svc4 = new ServiceEntity();
                svc4.setName("Bill Payment");
                svc4.setDescription("Utility and bill payment services");
                svc4.setAvgWaitTime(5);
                svc4.setIsActive(true);
                serviceRepo.save(svc4);

                ServiceEntity svc5 = new ServiceEntity();
                svc5.setName("General Inquiry");
                svc5.setDescription("General inquiries and information");
                svc5.setAvgWaitTime(6);
                svc5.setIsActive(true);
                serviceRepo.save(svc5);

                ServiceEntity svc6 = new ServiceEntity();
                svc6.setName("Document Verification");
                svc6.setDescription("Document verification and processing");
                svc6.setAvgWaitTime(8);
                svc6.setIsActive(true);
                serviceRepo.save(svc6);
            }
            System.out.println("Default testing data initialized!");
        };
    }
}
