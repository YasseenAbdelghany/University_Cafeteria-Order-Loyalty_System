package ServiceManagers;

public class MenuManagement extends ServicesManager {

    public  MenuManagement() {
        super.setName("Menu Management");
        super.setPhoneNumber("0123456789");
        super.setUsername("Mm00001");
        super.setPassword("Mm00001");
    }
    public MenuManagement(String name, String phoneNumber, String username, String password) {
        super.setName(name);
        super.setPhoneNumber(phoneNumber);
        super.setUsername(username);
        super.setPassword(password);
    }

}
