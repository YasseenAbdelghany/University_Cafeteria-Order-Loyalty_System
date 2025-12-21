package ServiceManagers;

public class PaymentService_Manager  extends ServicesManager{


    public PaymentService_Manager() {
        super.setName("Payment Service");
        super.setPhoneNumber("0123456789");
        super.setUsername("Mps00001");
        super.setPassword("Mps00001");
    }

    public PaymentService_Manager(String name, String phoneNumber, String username, String password) {
        super.setName(name);
        super.setPhoneNumber(phoneNumber);
        super.setUsername(username);
        super.setPassword(password);
    }

}

