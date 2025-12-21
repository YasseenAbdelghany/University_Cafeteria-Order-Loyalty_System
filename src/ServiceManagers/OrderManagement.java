package ServiceManagers;

public class OrderManagement  extends ServicesManager{


    public OrderManagement() {
        super.setName("Order Management");
        super.setPhoneNumber("0123456789");
        super.setUsername("Mord00001");
        super.setPassword("Mord00001");

    }

    public OrderManagement(String name, String phoneNumber, String username, String password) {
        super.setName(name);
        super.setPhoneNumber(phoneNumber);
        super.setUsername(username);
        super.setPassword(password);
    }

}
