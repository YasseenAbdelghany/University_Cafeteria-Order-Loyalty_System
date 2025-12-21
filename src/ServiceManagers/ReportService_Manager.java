package ServiceManagers;

public class ReportService_Manager extends ServicesManager {

    public ReportService_Manager() {
        super.setName("Report Service");
        super.setPhoneNumber( "0123456789");
        super.setUsername("Mrs00001");
        super.setPassword("Mrs00001");
    }
    public ReportService_Manager(String name, String phoneNumber, String username, String password) {
        super.setName(name);
        super.setPhoneNumber(phoneNumber);
        super.setUsername(username);
        super.setPassword(password);
    }


}
