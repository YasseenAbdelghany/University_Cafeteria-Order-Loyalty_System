package ServiceManagers;
// Pojo class for Student Management
public class StudentManagement extends ServicesManager{

        //Default constructor
        public  StudentManagement(){
            super.setName("Student Management");
            super.setPhoneNumber("0123456789");
            super.setUsername("Mst00001");
            super.setPassword("Mst00001");

        }
        public StudentManagement(String name, String phoneNumber, String username, String password) {
            super.setName(name);
            super.setPhoneNumber(phoneNumber);
            super.setUsername(username);
            super.setPassword(password);
        }

}
