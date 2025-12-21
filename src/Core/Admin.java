package Core;

public  class Admin {
    private int id;
    private String Name;
        private String Username;
        private String Password;

        public Admin() {}
        public Admin(String name,  String username, String password) {
            this.Name = name;
            this.Username = username;
            this.Password = password;
        }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
