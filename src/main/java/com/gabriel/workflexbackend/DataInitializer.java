package com.gabriel.workflexbackend;

import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.model.RiskEnum;
import com.gabriel.workflexbackend.model.Workation;
import com.gabriel.workflexbackend.repository.EmployeeRepository;
import com.gabriel.workflexbackend.repository.WorkationRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Profile("!test") // Prevents loading in the 'test' profile
public class DataInitializer implements CommandLineRunner {

    private final WorkationRepository workationRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing with sample workation data...");

        // Clear existing data
        workationRepository.deleteAll();
        employeeRepository.deleteAll();

        // Map to store employees and avoid duplicates
        Map<String, Employee> employeeMap = new HashMap<>();

        // Import workations from resources/static/workations.csv
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/workations.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] fields = line.split(",");
                if (fields.length >= 8) {
                    try {
                        // CSV columns: workationId,employee,origin,destination,start,end,workingDays,risk
                        String employeeName = fields[1].trim();
                        String origin = fields[2].trim();
                        String destination = fields[3].trim();
                        LocalDateTime start = LocalDateTime.parse(fields[4].trim() + "T00:00:00");
                        LocalDateTime end = LocalDateTime.parse(fields[5].trim() + "T23:59:59");
                        Integer workingDays = Integer.parseInt(fields[6].trim());
                        RiskEnum risk = RiskEnum.valueOf(fields[7].trim());

                        // Create or get existing employee
                        Employee employee = employeeMap.get(employeeName);
                        if (employee == null) {
                            employee = new Employee(null, employeeName);
                            employee = employeeRepository.save(employee);
                            employeeMap.put(employeeName, employee);
                            System.out.println("Created employee: " + employeeName);
                        }

                        // Create workation with employee relationship
                        Workation workation = new Workation(null, employee, origin, destination, start, end, workingDays, risk);
                        workationRepository.save(workation);
                        
                        System.out.println("Saved workation: " + employeeName + " from " + origin + " to " + destination);
                    } catch (Exception e) {
                        System.err.println("Failed to parse line: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to import workations: " + e.getMessage());
            e.printStackTrace();
        }

        long employeeCount = employeeRepository.count();
        long workationCount = workationRepository.count();
        System.out.println("Sample data initialized. Employees: " + employeeCount + ", Workations: " + workationCount);
    }
}