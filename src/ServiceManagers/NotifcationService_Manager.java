package ServiceManagers;

public class NotifcationService_Manager extends ServicesManager {


    public NotifcationService_Manager() {
        super.setName("Notification Service");
        super.setPhoneNumber("0123456789");
        super.setUsername("Mns00001");
        super.setPassword("Mns00001");
    }

    public NotifcationService_Manager(String name, String phoneNumber, String username, String password) {
        super.setName(name);
        super.setPhoneNumber(phoneNumber);
        super.setUsername(username);
        super.setPassword(password);
    }

}
