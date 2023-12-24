import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.*;
import java.sql.Statement;
import java.sql.ResultSet;



public class HotelManagementSystem {
 private static final String url;

 private static final String username;

 private static final String password;

    static {
        url = "jdbc:mysql://localhost:3306/hotel_db";
        username = "root";
        password = "123456";
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
    try {
        Class.forName("com.msql.cj.jdbc.Driver");
    }catch (ClassNotFoundException e){
        System.out.println(e.getMessage());
    }
    try{
        Connection connection = DriverManager.getConnection(url,username,password);
        while (true){
            System.out.println();
            System.out.println("Hotel Management System");
            Scanner sc = new Scanner(System.in);
            System.out.println("1. Reserve a Room: ");
            System.out.println("2. View Reservations: ");
            System.out.println("3. Get Reservation: ");
            System.out.println("4. Update Reservations: ");
            System.out.println("5. Delete Reservations: ");
            System.out.println("0. Exit: ");
            System.out.println("Choose an Option: ");
            int choice  = sc.nextInt();
            switch (choice){
                case 1:
                    reserveRoom(connection,sc);
                    break;
                case 2:
                    viewReservations(connection);
                    break;
                case 3:
                    getRoomNumber(connection,sc);
                    break;
                case 4:
                    updateReservation(connection,sc);
                    break;
                case 5:
                    deleteReservation(connection,sc);
                    break;
                case 0:
                    exit();
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice : Try Again");
            }

        }
    }catch (SQLException e){
        System.out.println(e.getMessage());
    }catch (InterruptedException e){
        throw new RuntimeException(e);
    }

}
private static void reserveRoom(Connection connection,Scanner sc){
    try{
        System.out.println("Enter guest name: ");
        String guestName  = sc.next();
        sc.nextLine();
        System.out.println("Enter room number: ");
        int roomNumber = sc.nextInt();
        System.out.println("Enter contact number: ");
        String contactNumber = sc.next();


        String sql  = "INSERT INTO reservations (guest_name, room_number, contact_number)" +
                "VALUES('" + guestName + "'," + roomNumber + ", '" + contactNumber + "')";


        try(Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if(affectedRows > 0){
                System.out.println("Reservation successful !");
            }else {
                System.out.println("Reservation Failed OopS!");
            }
        }
    }catch (SQLException e){
        e.printStackTrace();
    }
}
private  static void viewReservations(Connection connection) throws SQLException{
    String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)){

        
        System.out.println("Current Reservations:");
        System.out.println("+----------------+-------+----------+--------------+---------------------+------------------+");
        System.out.println("| Reservation ID | Guest |          | Room Number  | Contact Number      | Reservation Date |");
        System.out.println("+----------------+-------+----------+--------------+---------------------+------------------+");
        while (resultSet.next()){
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();


            System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                    reservationId, guestName , roomNumber, contactNumber, reservationDate);
        }
        System.out.println("+----------------+-------+----------+--------------+---------------------+------------------+");
    }

}
private  static void getRoomNumber(Connection connection, Scanner sc){
    try{
        System.out.println("Enter Reservation ID");
        int reservationID =sc.nextInt();
        System.out.println("Enter Guest Name");
        String guestName = sc.next();


        String sql = "Select room_number FROM reservations " +
                "WHERE reservation_ID = " + reservationID +
                "AND guest_name = '" + guestName + "'";
        try(Statement statement =connection.createStatement();
            ResultSet resultSet =statement.executeQuery(sql);){

            if(resultSet.next()){
                int roomNumber = resultSet.getInt("room_number");
                System.out.println("Room number for Reservation ID " + reservationID +
                        "and Guest " + guestName + "is: " + roomNumber);
            }
            else {
                System.out.println("Reservation not found  for the given ID and guest name.");
            }

        }
    }catch (SQLException e){
        e.printStackTrace();
    }
}
private static  void updateReservation(Connection connection,Scanner sc){
    try{
        System.out.print("Enter Reservation ID to Update: ");
        int reservationId =sc.nextInt();
        sc.nextLine();

        if(!reservationExists(connection,reservationId)){
            System.out.println("Reservation not found for the given ID: ");
            return;
        }

        System.out.println("Enter new Guest Name: ");
        String newGuestName = sc.nextLine();
        System.out.println("Enter new Room Number: ");
        int newRoomNumber = sc.nextInt();
        System.out.println("Enter new Contact Number: ");
        String newContactNumber = sc.next();


        String sql = "UPDATE reservations SET guest_name = '" +newGuestName + "', " +
                "room_number = " + newRoomNumber +", " +
                "conatct_number = '" +newContactNumber + "' " +
                "WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()){
            int affectedRow =statement.executeUpdate(sql);


            if(affectedRow>0){
                System.out.println("Reservation Updated Successfully!");
            }else {
                System.out.println("Reservation Update Failed.");
            }

        }
    }catch (SQLException e){
        e.printStackTrace();
    }
}
private  static  void deleteReservation(Connection connection,Scanner sc){
    try{
        System.out.println("Enter Reservation ID to delete: ");
        int reservationId = sc.nextInt();


        if(!reservationExists(connection,reservationId)){
            System.out.println("Reservation not found for the given ID: ");
            return;
        }


        String sql = "DELETE FROM reservations WHERE reservation_id = " +reservationId;


        try (Statement statement = connection.createStatement()){
            int affectedRow =statement.executeUpdate(sql);


            if(affectedRow>0){
                System.out.println("Reservation Deleted Successfully!");
            }else {
                System.out.println("Reservation Delete Failed.");
            }

        }


    }catch (SQLException e){
        e.printStackTrace();
    }
}
private static boolean reservationExists(Connection connection, int reservationId){
    try {
        String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " +reservationId;


        try (Statement statement = connection.createStatement();
        ResultSet resultSet =statement.executeQuery(sql) ){

            return resultSet.next();
        }
        }catch (SQLException e){
        e.printStackTrace();
        return false;
    }
}
public  static  void  exit() throws  InterruptedException{
    System.out.print("Exiting System");
    int i=5;
    while (i!=0){
        System.out.print(".");
        Thread.sleep(450);
        i--;
    }
    System.out.println();
    System.out.println("Thank You for using Hotel Reservation System!!!");

    }


}


























