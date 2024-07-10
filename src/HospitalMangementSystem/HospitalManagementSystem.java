package HospitalMangementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    
//    here private elsiye use kiya hai taaki koi isse class ke bhar use na kr paye 
//    here static esliye use kiya hai taaki inko use krne ke liye main class me object na banana pde
//    here final esliye use kiya hai taaki koi inki value baadme change na kar paye
    
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "";
    
    public static void main(String[] args){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        
        Scanner scanner = new Scanner(System.in);
        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);
            Patient patient = new Patient(connection,scanner);
            Doctor doctor = new Doctor(connection);
            while(true)
            {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1.Add Patient");
                System.out.println("2.View Patients");
                System.out.println("3.View Doctors");
                System.out.println("4.Book Appointments");
                System.out.println("5.Exit");
                System.out.print("Enter your choice : ");
                int choice = scanner.nextInt();
                
                switch(choice)
                {
                    case 1:
                        //Add Patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        //view Patients
                        patient.viewPatients();
                        System.out.println();
                        break;                                
                    case 3:
                        //View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;                        
                    case 4:
                        //Book Appointments
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("Thank You For Using Hospital Managament System");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!!");
                        break;
                }
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner)
    {
        System.out.print("Enter  Patient Id : ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id : ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointments date (YYYY-MM-DD) : ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId))
        {
            if(checkDoctorAvailability(doctorId,appointmentDate,connection))
            {
                String appointmentQuery = "Insert into appointments(patient_id,doctor_id,appointment_date)values(?,?,?)";
                try
                {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0)
                    {
                        System.out.println("Appointment Booked!");
                    }
                    else
                    {
                        System.out.println("Failed to Book Appoinment!");
                    }
                }catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                System.out.println("Doctor not available on this date!!");
            }
        }else
        {
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }
    public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection)
    {
        String query = "Select Count(*) from appointments where doctor_id = ? AND appointment_date = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next())
            {
                int count = rs.getInt(1);
                if(count == 0)
                {
                    return true;
                }else
                {
                    return false;
                }
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}