package com.klef.jfsd.exam;

import java.util.List;
import java.util.Scanner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class HCQLOperations {

    public static void main(String[] args) {
        HCQLOperations operations = new HCQLOperations();

        operations.addProject();

        operations.aggregateFunctions();
    }

    public void addProject() {
        Scanner sc = new Scanner(System.in);

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sf = configuration.buildSessionFactory();
        Session session = sf.openSession();

        Transaction t = session.beginTransaction();

        Project project = new Project();

        System.out.println("Would you like to enter a new project? (y/n)");

        // Check if the user wants to enter new data or use default values
        String userInput = sc.nextLine();
        
        if (userInput.equalsIgnoreCase("y")) {
            // Enter details manually
            System.out.print("Enter Project Name: ");
            project.setProjectName(sc.nextLine());

            System.out.print("Enter Project Duration (in months): ");
            project.setDuration(sc.nextInt());

            System.out.print("Enter Project Budget: ");
            project.setBudget(sc.nextDouble());

            sc.nextLine();  // Consume newline

            System.out.print("Enter Team Lead Name: ");
            project.setTeamLead(sc.nextLine());
        } else {
            // Default project values
            project.setProjectName("Default Project");
            project.setDuration(12); // Default duration (1 year)
            project.setBudget(100000.0); // Default budget
            project.setTeamLead("John Doe"); // Default team lead
        }

        // Persist the project object
        session.persist(project);
        t.commit();
        System.out.println("Project Added Successfully: " + project);

        session.close();
        sf.close();
        sc.close();
    }

    // Method to perform aggregate functions on the Budget
    public void aggregateFunctions() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sf = configuration.buildSessionFactory();
        Session session = sf.openSession();

        // Get CriteriaBuilder
        CriteriaBuilder cb = session.getCriteriaBuilder();
        
        // CriteriaQuery for aggregate operations
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Project> root = cq.from(Project.class);
        
        // Aggregate functions on the Budget
        cq.multiselect(
            cb.count(root.get("budget")),        // COUNT
            cb.max(root.get("budget")),          // MAX
            cb.min(root.get("budget")),          // MIN
            cb.sum(root.get("budget")),          // SUM
            cb.avg(root.get("budget"))           // AVG
        );

        // Execute the query
        Query<Object[]> query = session.createQuery(cq);
        List<Object[]> result = query.getResultList();

        // Print the aggregate values
        for (Object[] values : result) {
            System.out.println("Count: " + values[0]);
            System.out.println("Max Budget: " + values[1]);
            System.out.println("Min Budget: " + values[2]);
            System.out.println("Sum of Budgets: " + values[3]);
            System.out.println("Average Budget: " + values[4]);
        }

        session.close();
        sf.close();
    }
}
